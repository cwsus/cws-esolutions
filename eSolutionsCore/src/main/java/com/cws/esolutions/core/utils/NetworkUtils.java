/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cws.esolutions.core.utils;

import java.io.File;
import java.util.List;
import java.net.Socket;
import org.slf4j.Logger;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Properties;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.FileInputStream;
import org.slf4j.LoggerFactory;
import java.io.FileOutputStream;
import java.net.SocketException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import com.sshtools.j2ssh.ScpClient;
import com.sshtools.j2ssh.SftpClient;
import com.sshtools.j2ssh.SshClient;
import java.net.UnknownHostException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import com.sshtools.j2ssh.session.SessionChannelClient;
import org.apache.commons.httpclient.methods.PostMethod;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey;
import org.apache.commons.httpclient.params.HttpClientParams;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;
import com.sshtools.j2ssh.configuration.SshConnectionProperties;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolException;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.config.SSHConfig;
import com.cws.esolutions.core.config.ProxyConfig;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.core.utils.exception.UtilityException;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.utils
 * NetworkUtils.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Dec 26, 2012 12:54:17 PM
 *     Created.
 * kh05451 @ Dec 31, 2012 9:45:18 AM
 *     Add in http method
 * kh05451 @ May 30, 2013 11:41:18 AM
 *     Add in ssh method. It currently uses the IgnoreHostKeyVerification for
 *     hostkey verification, sadly, this should not be the way it is. But at
 *     the moment, there isn't a way for jsch to import a host key and save it
 */
public final class NetworkUtils
{
    private static final CoreServiceBean appBean = CoreServiceBean.getInstance();

    private static final String CRLF = "\r\n";
    private static final int HTTP_SOCKET_LINGER = 1;
    private static final String TERMINATE_TELNET = "^]";
    private static final boolean HTTP_STALE_CHECK = true;
    private static final String PROXY_AUTH_TYPE_NTLM = "NTLM";
    private static final String PROXY_AUTH_TYPE_BASIC = "basic";
    private static final String CNAME = NetworkUtils.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    /**
     * Creates an SSH connection to a target host and then executes an SCP
     * request to send or receive a file to or from the target. This is fully
     * key-based, as a result, a keyfile is required for the connection to
     * successfully authenticate.
     * 
     * NOTE: The key file provided MUST be an OpenSSH key. If its not, it must
     * be converted using ssh-keygen:
     * If no passphrase exists on the key: ssh-keygen -i -f /path/to/file
     * If a passphrase exists:
     * Remove the passphrase first: ssh-keygen-g3 -e /path/to/file
     * <enter passphrase>
     * Type 'yes'
     * Type 'no'
     * Type 'yes'
     * Hit enter twice without entering a new passphrase
     * Convert the keyfile: ssh-keygen -i -f /path/to/file > /path/to/new-file
     * Re-encrypt the file: ssh-keygen -p -f /path/to/new-file
     * 
     * @param sourceFile - The full path to the source file to transfer
     * @param targetFile - The full path (including file name) of the desired target file
     * @param targetHost - The target server to perform the transfer to
     * @param username - The SSH username to utilize for this connection
     * @param sshKeyFile - The SSH keyfile to utilize with this connection - MUST BE AN OPENSSH KEY
     * @param passphrase - SSH key file passphrase (optional, if not required specify either null
     *            or "")
     * @param isUpload - <code>true</code> is the transfer is an upload, <code>false</code> if it
     *            is a download 
     * @throws IOException - If an error occurs processing SSH keys or file transfer operations
     * @throws AuthenticationProtocolException - If authentication with the remote host fails
     */
    public static final synchronized void executeSCPTransfer(final List<File> sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException
    {
        final String methodName = CNAME + "#executeSCPTransfer(final List<File> sourceFile, final String targetFile, final String targetHost, final String passphrase) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", sourceFile);
            DEBUGGER.debug("Value: {}", targetFile);
            DEBUGGER.debug("Value: {}", targetHost);
        }

        final SshClient sshClient = new SshClient();
        final SSHConfig sshConfig = appBean.getConfigData().getSshConfig();

        if (DEBUG)
        {
            DEBUGGER.debug("SshClient: {}", sshClient);
            DEBUGGER.debug("SSHConfig: {}", sshConfig);
        }

        try
        {
            Properties sshProps = new Properties();
            sshProps.load(NetworkUtils.class.getResourceAsStream(appBean.getConfigData().getSshConfig().getSshProperties()));

            if (DEBUG)
            {
                DEBUGGER.debug("Properties: {}", sshProps);
            }

            if ((sourceFile != null) && (sourceFile.size() != 0))
            {
                SshConnectionProperties connProps = new SshConnectionProperties();
                connProps.setHost(targetHost); // set as obtained from db

                if (DEBUG)
                {
                    DEBUGGER.debug("SshConnectionProperties: {}", connProps);
                }

                boolean isKeyAuthentication = false;
                PasswordAuthenticationClient passAuth = null;
                PublicKeyAuthenticationClient keyAuth = null;

                if ((StringUtils.isNotEmpty(sshConfig.getSshUserPassword()) && (StringUtils.isEmpty(sshConfig.getSshKeyFile()))))
                {
                    passAuth = new PasswordAuthenticationClient();
                    passAuth.setUsername(sshConfig.getSshUserAccount());
                    passAuth.setPassword(
                            (StringUtils.isNotEmpty(sshConfig.getSshUserSalt())) ?
                                    PasswordUtils.decryptText(sshConfig.getSshUserPassword(), sshConfig.getSshUserSalt().length()) :
                                        sshConfig.getSshUserPassword());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("PasswordAuthenticationClient: {}", passAuth);
                    }
                }
                else if ((StringUtils.isEmpty(sshConfig.getSshUserPassword()) && (StringUtils.isNotEmpty(sshConfig.getSshKeyFile()))))
                {
                    isKeyAuthentication = true;

                    SshPrivateKeyFile sshPrivateKeyFile = SshPrivateKeyFile.parse(FileUtils.getFile(sshConfig.getSshKeyFile()));
                    SshPrivateKey sshPrivateKey = (StringUtils.isNotEmpty(sshConfig.getSshKeySalt())) ? sshPrivateKeyFile.toPrivateKey(
                            PasswordUtils.decryptText(sshConfig.getSshKeyPassword(), sshConfig.getSshKeySalt().length())) : 
                            sshPrivateKeyFile.toPrivateKey(null);

                    keyAuth = new PublicKeyAuthenticationClient();
                    keyAuth.setKey(sshPrivateKey);
                    keyAuth.setUsername(sshConfig.getSshUserAccount());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("PublicKeyAuthenticationClient: {}", keyAuth);
                    }
                }
                else
                {
                    throw new UtilityException("No valid authentication method has been configured. Cannot continue.");
                }

                sshClient.connect(connProps, new IgnoreHostKeyVerification());

                if (sshClient.isConnected())
                {
                    int authResult = -1;

                    if (isKeyAuthentication)
                    {
                        authResult = sshClient.authenticate(keyAuth);
                    }
                    else
                    {
                        authResult = sshClient.authenticate(passAuth);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Authentication Result: {}", authResult);
                    }

                    if (authResult == AuthenticationProtocolState.COMPLETE)
                    {
                        // do stuff...
                        if (sshClient.isAuthenticated())
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("SSH client connected and authenticated");
                            }

                            ScpClient client = sshClient.openScpClient();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("ScpClient: {}", client);
                            }

                            for (File file : sourceFile)
                            {
                                if (DEBUG)
                                {
                                    DEBUGGER.debug("File: {}", file);
                                }

                                if (isUpload)
                                {
                                    client.put(file.getAbsoluteFile().toString(), targetFile, false);
                                }
                                else
                                {
                                    client.get(targetFile, file.getAbsoluteFile().toString(), false);
                                }
                            }
                        }
                        else
                        {
                            throw new AuthenticationProtocolException("Failed to authenticate to remote host. Username: " + sshConfig.getSshUserAccount());
                        }
                    }
                    else
                    {
                        throw new AuthenticationProtocolException("Failed to authenticate to remote host. Username: " + sshConfig.getSshUserAccount());
                    }
                }
                else
                {
                    throw new ConnectException("Failed to connect to remote host");
                }
            }
            else
            {
                throw new IOException("Requested source file: " + sourceFile + " does not exist");
            }
        }
        catch (AuthenticationProtocolException apx)
        {
            ERROR_RECORDER.error(apx.getMessage(), apx);

            throw new UtilityException(apx.getMessage());
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new UtilityException(iox.getMessage(), iox);
        }
        finally
        {
            if (sshClient.isConnected())
            {
                sshClient.disconnect();
            }
        }
    }

    public static final synchronized void executeSftpTransfer(final List<File> sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException
    {
        final String methodName = CNAME + "#executeSftpTransfer(final List<File> sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", sourceFile);
            DEBUGGER.debug("Value: {}", targetFile);
            DEBUGGER.debug("Value: {}", targetHost);
            DEBUGGER.debug("Value: {}", isUpload);
        }

        final SshClient sshClient = new SshClient();
        final SSHConfig sshConfig = appBean.getConfigData().getSshConfig();

        if (DEBUG)
        {
            DEBUGGER.debug("SshClient: {}", sshClient);
            DEBUGGER.debug("SSHConfig: {}", sshConfig);
        }

        try
        {
            Properties sshProps = new Properties();
            sshProps.load(NetworkUtils.class.getResourceAsStream(appBean.getConfigData().getSshConfig().getSshProperties()));

            if ((sourceFile != null) && (sourceFile.size() != 0))
            {
                SshConnectionProperties connProps = new SshConnectionProperties();
                connProps.setHost(targetHost); // set as obtained from db
                
                if (DEBUG)
                {
                    DEBUGGER.debug("SshConnectionProperties: {}", connProps);
                }

                boolean isKeyAuthentication = false;
                PasswordAuthenticationClient passAuth = null;
                PublicKeyAuthenticationClient keyAuth = null;

                if ((StringUtils.isNotEmpty(sshConfig.getSshUserPassword()) && (StringUtils.isEmpty(sshConfig.getSshKeyFile()))))
                {
                    passAuth = new PasswordAuthenticationClient();
                    passAuth.setUsername(sshConfig.getSshUserAccount());
                    passAuth.setPassword(
                            (StringUtils.isNotEmpty(sshConfig.getSshUserSalt())) ?
                                    PasswordUtils.decryptText(sshConfig.getSshUserPassword(), sshConfig.getSshUserSalt().length()) :
                                        sshConfig.getSshUserPassword());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("PasswordAuthenticationClient: {}", passAuth);
                    }
                }
                else if ((StringUtils.isEmpty(sshConfig.getSshUserPassword()) && (StringUtils.isNotEmpty(sshConfig.getSshKeyFile()))))
                {
                    isKeyAuthentication = true;

                    SshPrivateKeyFile sshPrivateKeyFile = SshPrivateKeyFile.parse(FileUtils.getFile(sshConfig.getSshKeyFile()));
                    SshPrivateKey sshPrivateKey = (StringUtils.isNotEmpty(sshConfig.getSshKeySalt())) ? sshPrivateKeyFile.toPrivateKey(
                            PasswordUtils.decryptText(sshConfig.getSshKeyPassword(), sshConfig.getSshKeySalt().length())) : 
                            sshPrivateKeyFile.toPrivateKey(null);

                    keyAuth = new PublicKeyAuthenticationClient();
                    keyAuth.setKey(sshPrivateKey);
                    keyAuth.setUsername(sshConfig.getSshUserAccount());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("PublicKeyAuthenticationClient: {}", keyAuth);
                    }
                }
                else
                {
                    throw new UtilityException("No valid authentication method has been configured. Cannot continue.");
                }

                sshClient.connect(connProps, new IgnoreHostKeyVerification());

                if (sshClient.isConnected())
                {
                    int authResult = -1;

                    if (isKeyAuthentication)
                    {
                        authResult = sshClient.authenticate(keyAuth);
                    }
                    else
                    {
                        authResult = sshClient.authenticate(passAuth);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Authentication Result: {}", authResult);
                    }

                    if (authResult == AuthenticationProtocolState.COMPLETE)
                    {
                        // do stuff...
                        if (sshClient.isAuthenticated())
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("SSH client connected and authenticated");
                            }

                            SftpClient client = sshClient.openSftpClient();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("SftpClient: {}", client);
                            }

                            for (File file : sourceFile)
                            {
                                if (DEBUG)
                                {
                                    DEBUGGER.debug("File: {}", file);
                                }

                                if (isUpload)
                                {
                                    client.put(file.getAbsoluteFile().toString(), targetFile);
                                }
                                else
                                {
                                    client.get(targetFile, file.getAbsoluteFile().toString());
                                }
                            }

                            client.quit();
                        }
                        else
                        {
                            throw new AuthenticationProtocolException("Failed to authenticate to remote host. Username: " + sshConfig.getSshUserAccount());
                        }
                    }
                    else
                    {
                        throw new AuthenticationProtocolException("Failed to authenticate to remote host. Username: " + sshConfig.getSshUserAccount());
                    }
                }
                else
                {
                    throw new ConnectException("Failed to connect to remote host");
                }
            }
            else
            {
                throw new IOException("Requested source file: " + sourceFile + " does not exist");
            }
        }
        catch (AuthenticationProtocolException apx)
        {
            ERROR_RECORDER.error(apx.getMessage(), apx);

            throw new UtilityException(apx.getMessage());
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new UtilityException(iox.getMessage(), iox);
        }
        finally
        {
            if (sshClient.isConnected())
            {
                sshClient.disconnect();
            }
        }
    }

    /**
     * Creates an SSH connection to a target host and then executes an SCP
     * request to send or receive a file to or from the target. This is fully
     * key-based, as a result, a keyfile is required for the connection to
     * successfully authenticate.
     * 
     * NOTE: The key file provided MUST be an OpenSSH key. If its not, it must
     * be converted using ssh-keygen:
     * If no passphrase exists on the key: ssh-keygen -i -f /path/to/file
     * If a passphrase exists:
     * Remove the passphrase first: ssh-keygen-g3 -e /path/to/file
     * <enter passphrase>
     * Type 'yes'
     * Type 'no'
     * Type 'yes'
     * Hit enter twice without entering a new passphrase
     * Convert the keyfile: ssh-keygen -i -f /path/to/file > /path/to/new-file
     * Re-encrypt the file: ssh-keygen -p -f /path/to/new-file
     * 
     * @param sourceFile - The full path to the source file to transfer
     * @param targetFile - The full path (including file name) of the desired target file
     * @param targetHost - The target server to perform the transfer to
     * @param username - The SSH username to utilize for this connection
     * @param sshKeyFile - The SSH keyfile to utilize with this connection - MUST BE AN OPENSSH KEY
     * @param passphrase - SSH key file passphrase (optional, if not required specify either null
     *            or "")
     * @param isUpload - <code>true</code> is the transfer is an upload, <code>false</code> if it
     *            is a download 
     * @throws IOException - If an error occurs processing SSH keys or file transfer operations
     * @throws AuthenticationProtocolException - If authentication with the remote host fails
     */
    public static final synchronized StringBuilder executeSshConnection(final String targetHost, List<String> commandList) throws UtilityException
    {
        final String methodName = CNAME + "#executeSshConnection(final String targetHost, List<String> commandList) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("targetHost: {}", targetHost);
            DEBUGGER.debug("command: {}", commandList);
        }

        StringBuilder sBuilder = null;

        final SshClient sshClient = new SshClient();
        final SSHConfig sshConfig = appBean.getConfigData().getSshConfig();

        if (DEBUG)
        {
            DEBUGGER.debug("SshClient: {}", sshClient);
            DEBUGGER.debug("SSHConfig: {}", sshConfig);
        }

        try
        {
            SshConnectionProperties connProps = new SshConnectionProperties();
            connProps.setHost(targetHost); // set as obtained from db

            if (DEBUG)
            {
                DEBUGGER.debug("SshConnectionProperties: {}", connProps);
            }

            boolean isKeyAuthentication = false;
            PasswordAuthenticationClient passAuth = null;
            PublicKeyAuthenticationClient keyAuth = null;

            if ((StringUtils.isNotEmpty(sshConfig.getSshUserPassword()) && (StringUtils.isEmpty(sshConfig.getSshKeyFile()))))
            {
                passAuth = new PasswordAuthenticationClient();
                passAuth.setUsername(sshConfig.getSshUserAccount());
                passAuth.setPassword(
                        (StringUtils.isNotEmpty(sshConfig.getSshUserSalt())) ?
                                PasswordUtils.decryptText(sshConfig.getSshUserPassword(), sshConfig.getSshUserSalt().length()) :
                                    sshConfig.getSshUserPassword());

                if (DEBUG)
                {
                    DEBUGGER.debug("PasswordAuthenticationClient: {}", passAuth);
                }
            }
            else if ((StringUtils.isEmpty(sshConfig.getSshUserPassword()) && (StringUtils.isNotEmpty(sshConfig.getSshKeyFile()))))
            {
                isKeyAuthentication = true;

                SshPrivateKeyFile sshPrivateKeyFile = SshPrivateKeyFile.parse(FileUtils.getFile(sshConfig.getSshKeyFile()));
                SshPrivateKey sshPrivateKey = (StringUtils.isNotEmpty(sshConfig.getSshKeySalt())) ? sshPrivateKeyFile.toPrivateKey(
                        PasswordUtils.decryptText(sshConfig.getSshKeyPassword(), sshConfig.getSshKeySalt().length())) : 
                        sshPrivateKeyFile.toPrivateKey(null);

                keyAuth = new PublicKeyAuthenticationClient();
                keyAuth.setKey(sshPrivateKey);
                keyAuth.setUsername(sshConfig.getSshUserAccount());

                if (DEBUG)
                {
                    DEBUGGER.debug("PublicKeyAuthenticationClient: {}", keyAuth);
                }
            }
            else
            {
                throw new UtilityException("No valid authentication method has been configured. Cannot continue.");
            }

            sshClient.connect(connProps, new IgnoreHostKeyVerification());

            if (sshClient.isConnected())
            {
                int authResult = -1;

                if (isKeyAuthentication)
                {
                    authResult = sshClient.authenticate(keyAuth);
                }
                else
                {
                    authResult = sshClient.authenticate(passAuth);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("Authentication Result: {}", authResult);
                }

                if (authResult == AuthenticationProtocolState.COMPLETE)
                {
                    // do stuff...
                    if (sshClient.isAuthenticated())
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("SSH client connected and authenticated");
                        }

                        for (String command : commandList)
                        {
                            SessionChannelClient client = sshClient.openSessionChannel();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("SessionChannelClient: {}", client);
                            }

                            if (client.isOpen())
                            {
                                if (client.executeCommand(command))
                                {
                                    String line = null;
                                    BufferedReader bReader = new BufferedReader(new InputStreamReader(client.getInputStream()));

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("BufferedReader: {}", bReader);
                                    }

                                    sBuilder = new StringBuilder();

                                    while ((line = bReader.readLine()) != null)
                                    {
                                        sBuilder.append(line + Constants.LINE_BREAK);
                                    }

                                    bReader.close();
                                }
                                else
                                {
                                    throw new UtilityException("Failed to execute command " + command + " on host " + targetHost);
                                }

                                client.close();
                            }
                            else
                            {
                                throw new UtilityException("Failed to open ssh channel to remote host.");
                            }
                        }
                    }
                    else
                    {
                        throw new AuthenticationProtocolException("Failed to authenticate to remote host. Username: " + sshConfig.getSshUserAccount());
                    }
                }
                else
                {
                    throw new AuthenticationProtocolException("Failed to authenticate to remote host. Username: " + sshConfig.getSshUserAccount());
                }
            }
            else
            {
                throw new ConnectException("Failed to connect to remote host");
            }
        }
        catch (AuthenticationProtocolException apx)
        {
            ERROR_RECORDER.error(apx.getMessage(), apx);

            throw new UtilityException(apx.getMessage());
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new UtilityException(iox.getMessage(), iox);
        }
        finally
        {
            if (sshClient.isConnected())
            {
                sshClient.disconnect();
            }
        }

        return sBuilder;
    }

    public static final synchronized void executeFtpConnection(final List<File> sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException
    {
        final String methodName = CNAME + "#executeFtpConnection(final List<File> sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", sourceFile);
            DEBUGGER.debug("Value: {}", targetFile);
            DEBUGGER.debug("Value: {}", targetHost);
            DEBUGGER.debug("Value: {}", isUpload);
        }

        final FTPClient client = new FTPClient();
        final SSHConfig sshConfig = appBean.getConfigData().getSshConfig();

        if (DEBUG)
        {
            DEBUGGER.debug("FTPClient: {}", client);
            DEBUGGER.debug("SSHConfig: {}", sshConfig);
        }

        try
        {
            client.connect(targetHost);

            if (DEBUG)
            {
                DEBUGGER.debug("FTPClient: {}", client);
            }

            if (client.isConnected())
            {
                boolean isAuthenticated = client.login(sshConfig.getSshUserAccount(),
                        PasswordUtils.decryptText(sshConfig.getSshKeyPassword(), sshConfig.getSshKeySalt().length()));

                if (DEBUG)
                {
                    DEBUGGER.debug("isAuthenticated: {}", isAuthenticated);
                }

                if (isAuthenticated)
                {
                    client.enterLocalPassiveMode();

                    for (File file : sourceFile)
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("File: {}", file);
                        }

                        if (isUpload)
                        {
                            client.storeFile(targetFile, new FileInputStream(file));
                        }
                        else
                        {
                            client.retrieveFile(file.getAbsolutePath(), new FileOutputStream(targetFile));
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Reply: {}", client.getReplyCode());
                            DEBUGGER.debug("Reply: {}", client.getReplyString());
                        }
                    }
                }
                else
                {
                    throw new IOException("Failed to authenticate to remote host with the provided information");
                }
            }
            else
            {
                throw new IOException("Failed to connect to FTP server: " + targetHost);
            }
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new UtilityException(iox.getMessage(), iox);
        }
        finally
        {
            try
            {
                if (client.isConnected())
                {
                    client.completePendingCommand();
                    client.disconnect();
                }
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);
            }
        }
    }

    /**
     * Creates an telnet connection to a target host and port number. Silently
     * succeeds if no issues are encountered, if so, exceptions are logged and
     * re-thrown back to the requestor.
     *
     * If an exception is thrown during the <code>socket.close()</code> operation,
     * it is logged but NOT re-thrown. It's not re-thrown because it does not indicate
     * a connection failure (indeed, it means the connection succeeded) but it is
     * logged because continued failures to close the socket could result in target
     * system instability.
     * 
     * @param hostName - The target host to make the connection to
     * @param portNumber - The port number to attempt the connection on 
     * @throws UtilityException - If an error occurs during the connection attempt
     */
    public static final synchronized void executeTelnetRequest(final String hostName, final int portNumber, final int timeout) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeTelnetRequest(final String hostName, final int portNumber, final int timeout) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(hostName);
            DEBUGGER.debug("portNumber: {}", portNumber);
            DEBUGGER.debug("timeout: {}", timeout);
        }

        Socket socket = null;

        try
        {
            synchronized(new Object())
            {
                if (InetAddress.getByName(hostName) != null)
                {
                    InetSocketAddress socketAddress = new InetSocketAddress(hostName, portNumber);

                    socket = new Socket();
                    socket.setSoTimeout(timeout);
                    socket.setSoLinger(false, 0);
                    socket.setKeepAlive(false);
                    socket.connect(socketAddress, timeout);

                    if (socket.isConnected())
                    {
                        PrintWriter pWriter = new PrintWriter(socket.getOutputStream(), true);

                        pWriter.println(NetworkUtils.TERMINATE_TELNET + NetworkUtils.CRLF);

                        pWriter.flush();
                        pWriter.close();
                    }
                    else
                    {
                        throw new ConnectException("Failed to connect to host " + hostName + " on port " + portNumber);
                    }
                }
                else
                {
                    throw new UnknownHostException("No host was found in DNS for the given name: " + hostName);
                }
            }
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new UtilityException(cx.getMessage(), cx);
        }
        catch (UnknownHostException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new UtilityException(ux.getMessage(), ux);
        }
        catch (SocketException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new UtilityException(sx.getMessage(), sx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new UtilityException(iox.getMessage(), iox);
        }
        finally
        {
            try
            {
                if ((socket != null) && (!(socket.isClosed())))
                {
                    socket.close();
                }
            }
            catch (IOException iox)
            {
                // log it - this could cause problems later on
                ERROR_RECORDER.error(iox.getMessage(), iox);
            }
        }
    }

    /**
     * Creates an HTTP connection to a provided website and returns the data back
     * to the requestor.
     *
     * @param hostName - The fully qualified URL for the host desired. MUST be
     *     prefixed with http/https:// as necessary
     * @param timeout - Connection timeout. If not specified, defaults to 10 seconds
     * @param method - GET or POST, depending on what is necessary
     * @return A byte array containing the response data
     * @throws UtilityException if the connection cannot be established or if the
     *     response code != 200
     */
    public static final synchronized byte[] executeHttpConnection(final String hostName, final int timeout, final String method) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeHttpConnection(final String hostName, final int timeout, final String method) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("hostName: {}", hostName);
            DEBUGGER.debug("timeout: {}", timeout);
            DEBUGGER.debug("method: {}", method);
        }

        HttpMethod httpMethod = null;

        final HttpClient httpClient = new HttpClient();
        final HttpClientParams httpParams = new HttpClientParams();
        final ProxyConfig proxyConfig = appBean.getConfigData().getProxyConfig();

        try
        {
            synchronized(new Object())
            {
                httpParams.setVersion(HttpVersion.HTTP_1_0);
                httpParams.setSoTimeout(timeout);
                httpParams.setParameter("http.socket.linger", NetworkUtils.HTTP_SOCKET_LINGER);
                httpParams.setParameter("http.socket.timeout", timeout);
                httpParams.setParameter("http.connection.timeout", timeout);
                httpParams.setParameter("http.connection-manager.timeout", Long.valueOf(timeout));
                httpParams.setParameter("http.connection.stalecheck", NetworkUtils.HTTP_STALE_CHECK);

                if (DEBUG)
                {
                    DEBUGGER.debug("httpParams: {}", httpParams);
                }

                if (proxyConfig.isProxyServiceRequired())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("ProxyConfig: {}", proxyConfig);
                    }

                    if (StringUtils.isNotEmpty(proxyConfig.getProxyServerName()))
                    {
                        httpClient.getHostConfiguration().setProxy(proxyConfig.getProxyServerName(),
                                proxyConfig.getProxyServerPort());

                        if (proxyConfig.isProxyAuthRequired())
                        {
                            List<String> authList = new ArrayList<String>();
                            authList.add(AuthPolicy.BASIC);
                            authList.add(AuthPolicy.DIGEST);
                            authList.add(AuthPolicy.NTLM);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("authList: {}", authList);
                            }

                            httpParams.setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authList);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("httpParams: {}", httpParams);
                            }

                            String proxyPwd = (StringUtils.isNotEmpty(proxyConfig.getProxyPwdSalt())) ?
                                    PasswordUtils.decryptText(proxyConfig.getProxyPassword(), proxyConfig.getProxyPwdSalt().length()) : proxyConfig.getProxyPassword();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("proxyPwd: {}", proxyPwd);
                            }

                            if (StringUtils.equals(NetworkUtils.PROXY_AUTH_TYPE_BASIC, proxyConfig.getProxyAuthType()))
                            {

                                httpClient.getState().setProxyCredentials(new AuthScope(
                                        proxyConfig.getProxyServerName(),
                                        proxyConfig.getProxyServerPort(),
                                        proxyConfig.getProxyServerRealm()),
                                        new UsernamePasswordCredentials(proxyConfig.getProxyUserId(), proxyPwd));
                            }
                            else if (StringUtils.equals(NetworkUtils.PROXY_AUTH_TYPE_NTLM, proxyConfig.getProxyAuthType()))
                            {
                                httpClient.getState().setProxyCredentials(new AuthScope(
                                        proxyConfig.getProxyServerName(),
                                        proxyConfig.getProxyServerPort(),
                                        proxyConfig.getProxyServerRealm()),
                                        new NTCredentials(
                                                proxyConfig.getProxyUserId(),
                                                proxyPwd,
                                                InetAddress.getLocalHost().getHostName(),
                                                proxyConfig.getProxyAuthDomain()));
                            }

                            if (DEBUG)
                            {
                                DEBUGGER.debug("httpClient: {}", httpClient);
                            }
                        }
                    }
                    else
                    {
                        throw new HttpException("Configuration states proxy usage is required, but no proxy is configured.");
                    }
                }

                httpClient.setParams(httpParams);

                httpMethod = (StringUtils.equalsIgnoreCase(method, "POST")) ? new PostMethod(hostName) : new GetMethod(hostName);

                // Set this to the information for accessing the aforementioned target URL
                int responseCode = httpClient.executeMethod(httpMethod);

                if (DEBUG)
                {
                    DEBUGGER.debug("responseCode: {}", responseCode);
                }

                if (responseCode != 200)
                {
                    ERROR_RECORDER.error("HTTP Response Code received NOT 200: " + responseCode);

                    throw new HttpException("HTTP Response Code received NOT 200: " + responseCode);
                }
                else
                {
                    return httpMethod.getResponseBody();
                }
            }
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new UtilityException(cx.getMessage(), cx);
        }
        catch (UnknownHostException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new UtilityException(ux.getMessage(), ux);
        }
        catch (SocketException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new UtilityException(sx.getMessage(), sx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new UtilityException(iox.getMessage(), iox);
        }
        finally
        {
            if (httpMethod != null)
            {
                httpMethod.releaseConnection();
            }
        }
    }

    /**
     * Creates an telnet connection to a target host and port number. Silently
     * succeeds if no issues are encountered, if so, exceptions are logged and
     * re-thrown back to the requestor.
     *
     * If an exception is thrown during the <code>socket.close()</code> operation,
     * it is logged but NOT re-thrown. It's not re-thrown because it does not indicate
     * a connection failure (indeed, it means the connection succeeded) but it is
     * logged because continued failures to close the socket could result in target
     * system instability.
     * 
     * @param hostName - The target host to make the connection to
     * @param portNumber - The port number to attempt the connection on 
     * @throws UtilityException - If an error occurs during the connection attempt
     */
    public static final synchronized Object executeTcpRequest(final String hostName, final int portNumber, final int timeout, final Object object) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeTcpRequest(final String hostName, final int portNumber, final int timeout, final Object object) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(hostName);
            DEBUGGER.debug("portNumber: {}", portNumber);
            DEBUGGER.debug("timeout: {}", timeout);
            DEBUGGER.debug("object: {}", object);
        }

        Socket socket = null;
        Object resObject = null;

        try
        {
            synchronized(new Object())
            {
                if (InetAddress.getByName(hostName) != null)
                {
                    InetSocketAddress socketAddress = new InetSocketAddress(hostName, portNumber);

                    socket = new Socket();
                    socket.setSoTimeout(timeout);
                    socket.setSoLinger(false, 0);
                    socket.setKeepAlive(false);
                    socket.connect(socketAddress, timeout);

                    if (socket.isConnected())
                    {
                        ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ObjectOutputStream: {}", objectOut);
                        }

                        objectOut.writeObject(object);

                        resObject = new ObjectInputStream(socket.getInputStream()).readObject();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("resObject: {}", resObject);
                        }

                        PrintWriter pWriter = new PrintWriter(socket.getOutputStream(), true);

                        pWriter.println(NetworkUtils.TERMINATE_TELNET + NetworkUtils.CRLF);

                        pWriter.flush();
                        pWriter.close();
                    }
                    else
                    {
                        throw new ConnectException("Failed to connect to host " + hostName + " on port " + portNumber);
                    }
                }
                else
                {
                    throw new UnknownHostException("No host was found in DNS for the given name: " + hostName);
                }
            }
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new UtilityException(cx.getMessage(), cx);
        }
        catch (UnknownHostException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new UtilityException(ux.getMessage(), ux);
        }
        catch (SocketException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new UtilityException(sx.getMessage(), sx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new UtilityException(iox.getMessage(), iox);
        }
        catch (ClassNotFoundException cnfx)
        {
            ERROR_RECORDER.error(cnfx.getMessage(), cnfx);

            throw new UtilityException(cnfx.getMessage(), cnfx);
        }
        finally
        {
            try
            {
                if ((socket != null) && (!(socket.isClosed())))
                {
                    socket.close();
                }
            }
            catch (IOException iox)
            {
                // log it - this could cause problems later on
                ERROR_RECORDER.error(iox.getMessage(), iox);
            }
        }

        return resObject;
    }

    public static final synchronized boolean isHostValid(final String hostName)
    {
        final String methodName = NetworkUtils.CNAME + "#isHostValid(final String hostName)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(hostName);
        }

        boolean validHost = false;

        try
        {
            synchronized(new Object())
            {
                if (InetAddress.getByName(hostName) != null)
                {
                    validHost = true;
                }
            }
        }
        catch (UnknownHostException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);
        }

        return validHost;
    }
}
