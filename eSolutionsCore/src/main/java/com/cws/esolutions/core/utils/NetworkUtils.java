/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.utils
 * File: NetworkUtils.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
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
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.SftpClient;
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

import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.config.xml.HTTPConfig;
import com.cws.esolutions.core.config.xml.ProxyConfig;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.core.exception.CoreServiceException;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public final class NetworkUtils
{
    private static final String CRLF = "\r\n";
    private static final String TERMINATE_TELNET = "^]";
    private static final String PROXY_AUTH_TYPE_NTLM = "NTLM";
    private static final String PROXY_AUTH_TYPE_BASIC = "basic";
    private static final String CNAME = NetworkUtils.class.getName();

    private static final String SALT = "userSalt";
    private static final String KEYFILE = "userKeyFile";
    private static final String ACCOUNT = "userAccount";
    private static final String PASSWORD = "userPassword";
    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER + CNAME);

    public static final void main(final String[] args)
    {
        final String methodName = NetworkUtils.CNAME + "#main(final String[] args)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", args);
        }

        if (args.length == 0)
        {
            System.exit(1);
        }

        try
        {
            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "eSolutionsCore/logging/logging.xml");
        }
        catch (CoreServiceException csx)
        {
            System.err.println("An error occurred while loading configuration data: " + csx.getMessage());

            System.exit(1);
        }

        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml");
        }
        catch (SecurityServiceException ssx)
        {
            System.err.println("An error occurred while loading configuration data: " + ssx.getMessage());

            System.exit(1);
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
     * @param sshProps - The SSH properties file to utilize
     * @param authProps - A list containing the authentication properties
     * @param sourceFile - The full path to the source file to transfer
     * @param targetFile - The full path (including file name) of the desired target file
     * @param targetHost - The target server to perform the transfer to
     * @param isUpload - <code>true</code> is the transfer is an upload, <code>false</code> if it
     *            is a download 
     * @throws UtilityException - If an error occurs processing SSH keys or file transfer operations
     */
    public static final synchronized void executeSCPTransfer(final Properties sshProps, final Properties authProps, final List<File> sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeSCPTransfer(final Properties sshProps, final Properties authProps, final List<String> authList, final String targetFile, final String targetHost, final String passphrase) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", sshProps);
            DEBUGGER.debug("Value: {}", sourceFile);
            DEBUGGER.debug("Value: {}", targetFile);
            DEBUGGER.debug("Value: {}", targetHost);
        }

        final SshClient sshClient = new SshClient();

        if (DEBUG)
        {
            DEBUGGER.debug("SshClient: {}", sshClient);
        }

        try
        {
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

                if (authProps.containsKey(NetworkUtils.KEYFILE))
                {
                    isKeyAuthentication = true;

                    SshPrivateKeyFile sshPrivateKeyFile = SshPrivateKeyFile.parse(FileUtils.getFile(authProps.getProperty(NetworkUtils.KEYFILE)));
                    SshPrivateKey sshPrivateKey = null;

                    switch (authProps.getProperty(NetworkUtils.PASSWORD).length())
                    {
                        case 0:
                            sshPrivateKeyFile.toPrivateKey(null);

                            break;
                        default:
                            sshPrivateKeyFile.toPrivateKey(PasswordUtils.decryptText(authProps.getProperty(NetworkUtils.PASSWORD), authProps.getProperty(NetworkUtils.SALT).length()));

                            break;
                    }

                    keyAuth = new PublicKeyAuthenticationClient();
                    keyAuth.setKey(sshPrivateKey);
                    keyAuth.setUsername(authProps.getProperty(NetworkUtils.ACCOUNT));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("PublicKeyAuthenticationClient: {}", keyAuth);
                    }
                }
                else
                {
                    passAuth = new PasswordAuthenticationClient();
                    passAuth.setUsername(authProps.getProperty(NetworkUtils.ACCOUNT));
                    passAuth.setPassword(PasswordUtils.decryptText(authProps.getProperty(NetworkUtils.PASSWORD), authProps.getProperty(NetworkUtils.SALT).length()));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("PasswordAuthenticationClient: {}", passAuth);
                    }
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
                            throw new AuthenticationProtocolException("Failed to authenticate to remote host. Username: " + authProps.getProperty(NetworkUtils.ACCOUNT));
                        }
                    }
                    else
                    {
                        throw new AuthenticationProtocolException("Failed to authenticate to remote host. Username: " + authProps.getProperty(NetworkUtils.ACCOUNT));
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
            throw new UtilityException(apx.getMessage());
        }
        catch (IOException iox)
        {
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

    public static final synchronized void executeSftpTransfer(final Properties authProps, final List<File> sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeSftpTransfer(final Properties authProps, final List<File> sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", sourceFile);
            DEBUGGER.debug("Value: {}", targetFile);
            DEBUGGER.debug("Value: {}", targetHost);
            DEBUGGER.debug("Value: {}", isUpload);
        }

        final SshClient sshClient = new SshClient();

        if (DEBUG)
        {
            DEBUGGER.debug("SshClient: {}", sshClient);
        }

        try
        {
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

                if (authProps.containsKey(NetworkUtils.KEYFILE))
                {
                    isKeyAuthentication = true;

                    SshPrivateKeyFile sshPrivateKeyFile = SshPrivateKeyFile.parse(FileUtils.getFile(authProps.getProperty(NetworkUtils.KEYFILE)));
                    SshPrivateKey sshPrivateKey = null;

                    switch (authProps.getProperty(NetworkUtils.PASSWORD).length())
                    {
                        case 0:
                            sshPrivateKeyFile.toPrivateKey(null);

                            break;
                        default:
                            sshPrivateKeyFile.toPrivateKey(PasswordUtils.decryptText(authProps.getProperty(NetworkUtils.PASSWORD), authProps.getProperty(NetworkUtils.SALT).length()));

                            break;
                    }

                    keyAuth = new PublicKeyAuthenticationClient();
                    keyAuth.setKey(sshPrivateKey);
                    keyAuth.setUsername(authProps.getProperty(NetworkUtils.ACCOUNT));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("PublicKeyAuthenticationClient: {}", keyAuth);
                    }
                }
                else
                {
                    passAuth = new PasswordAuthenticationClient();
                    passAuth.setUsername(authProps.getProperty(NetworkUtils.ACCOUNT));
                    passAuth.setPassword(PasswordUtils.decryptText(authProps.getProperty(NetworkUtils.PASSWORD), authProps.getProperty(NetworkUtils.SALT).length()));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("PasswordAuthenticationClient: {}", passAuth);
                    }
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
                            throw new AuthenticationProtocolException("Failed to authenticate to remote host. Username: " + authProps.getProperty(NetworkUtils.ACCOUNT));
                        }
                    }
                    else
                    {
                        throw new AuthenticationProtocolException("Failed to authenticate to remote host. Username: " + authProps.getProperty(NetworkUtils.ACCOUNT));
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
            throw new UtilityException(apx.getMessage());
        }
        catch (IOException iox)
        {
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
     * @param targetHost - The target server to perform the transfer to
     * @param commandList - The list of commands to execute on the remote host. 
     * @throws UtilityException - If an error occurs processing SSH keys or file transfer operations
     */
    public static final synchronized StringBuilder executeSshConnection(final Properties sshProps, final Properties authProps, final String targetHost, final List<String> commandList) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeSshConnection(final Properties sshProps, final Properties authProps, final String targetHost, final List<String> commandList) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", sshProps);
            DEBUGGER.debug("Value: {}", authProps);
            DEBUGGER.debug("Value: {}", targetHost);
            DEBUGGER.debug("Value: {}", commandList);
        }

        StringBuilder sBuilder = null;
        boolean isKeyAuthentication = false;
        PasswordAuthenticationClient passAuth = null;
        PublicKeyAuthenticationClient keyAuth = null;

        final SshClient sshClient = new SshClient();

        if (DEBUG)
        {
            DEBUGGER.debug("SshClient: {}", sshClient);
        }

        try
        {
            SshConnectionProperties connProps = new SshConnectionProperties();
            connProps.setHost(targetHost); // set as obtained from db

            if (DEBUG)
            {
                DEBUGGER.debug("SshConnectionProperties: {}", connProps);
            }

            if (authProps.containsKey(NetworkUtils.KEYFILE))
            {
                isKeyAuthentication = true;

                SshPrivateKeyFile sshPrivateKeyFile = SshPrivateKeyFile.parse(FileUtils.getFile(authProps.getProperty(NetworkUtils.KEYFILE)));
                SshPrivateKey sshPrivateKey = null;

                switch (authProps.getProperty(NetworkUtils.PASSWORD).length())
                {
                    case 0:
                        sshPrivateKeyFile.toPrivateKey(null);

                        break;
                    default:
                        sshPrivateKeyFile.toPrivateKey(PasswordUtils.decryptText(authProps.getProperty(NetworkUtils.PASSWORD), authProps.getProperty(NetworkUtils.SALT).length()));

                        break;
                }

                keyAuth = new PublicKeyAuthenticationClient();
                keyAuth.setKey(sshPrivateKey);
                keyAuth.setUsername(authProps.getProperty(NetworkUtils.ACCOUNT));

                if (DEBUG)
                {
                    DEBUGGER.debug("PublicKeyAuthenticationClient: {}", keyAuth);
                }
            }
            else
            {
                passAuth = new PasswordAuthenticationClient();
                passAuth.setUsername(authProps.getProperty(NetworkUtils.ACCOUNT));
                passAuth.setPassword(PasswordUtils.decryptText(authProps.getProperty(NetworkUtils.PASSWORD), authProps.getProperty(NetworkUtils.SALT).length()));

                if (DEBUG)
                {
                    DEBUGGER.debug("PasswordAuthenticationClient: {}", passAuth);
                }
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
                                        sBuilder.append(line + CoreServiceConstants.LINE_BREAK);
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
                        throw new AuthenticationProtocolException("Failed to authenticate to remote host. Username: " + authProps.getProperty(NetworkUtils.ACCOUNT));
                    }
                }
                else
                {
                    throw new AuthenticationProtocolException("Failed to authenticate to remote host. Username: " + authProps.getProperty(NetworkUtils.ACCOUNT));
                }
            }
            else
            {
                throw new ConnectException("Failed to connect to remote host");
            }
        }
        catch (AuthenticationProtocolException apx)
        {
            throw new UtilityException(apx.getMessage());
        }
        catch (IOException iox)
        {
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

    public static final synchronized void executeFtpConnection(final Properties authProps, final List<File> sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeFtpConnection(final Properties authProps, final List<File> sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", authProps);
            DEBUGGER.debug("Value: {}", sourceFile);
            DEBUGGER.debug("Value: {}", targetFile);
            DEBUGGER.debug("Value: {}", targetHost);
            DEBUGGER.debug("Value: {}", isUpload);
        }

        final FTPClient client = new FTPClient();

        if (DEBUG)
        {
            DEBUGGER.debug("FTPClient: {}", client);
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
                boolean isAuthenticated = false;

                if (StringUtils.isNotBlank(authProps.getProperty(NetworkUtils.PASSWORD)))
                {
                    isAuthenticated = client.login(authProps.getProperty(NetworkUtils.ACCOUNT),
                            PasswordUtils.decryptText(authProps.getProperty(NetworkUtils.PASSWORD), authProps.getProperty(NetworkUtils.SALT).length()));
                }
                else
                {
                    isAuthenticated = client.login(authProps.getProperty(NetworkUtils.ACCOUNT), null);
                }

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
     * @param timeout - The timeout for the connection
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
            throw new UtilityException(cx.getMessage(), cx);
        }
        catch (UnknownHostException ux)
        {
            throw new UtilityException(ux.getMessage(), ux);
        }
        catch (SocketException sx)
        {
            throw new UtilityException(sx.getMessage(), sx);
        }
        catch (IOException iox)
        {
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
    public static final synchronized byte[] executeHttpConnection(final HTTPConfig httpConfig, final ProxyConfig proxyConfig, final String hostName, final int timeout, final String method) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeHttpConnection(final HTTPConfig httpConfig, final ProxyConfig proxyConfig, final String hostName, final int timeout, final String method) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", httpConfig);
            DEBUGGER.debug("Value: {}", proxyConfig);
            DEBUGGER.debug("Value: {}", hostName);
            DEBUGGER.debug("Value: {}", timeout);
            DEBUGGER.debug("Value: {}", method);
        }

        HttpMethod httpMethod = null;

        final HttpClient httpClient = new HttpClient();
        final HttpClientParams httpParams = new HttpClientParams();

        try
        {
            if (StringUtils.isNotEmpty(httpConfig.getTrustStoreFile()))
            {
                System.setProperty("javax.net.ssl.trustStoreType",
                        (StringUtils.isNotEmpty(httpConfig.getTrustStoreType()) ? httpConfig.getTrustStoreType() : "jks"));
                System.setProperty("javax.net.ssl.trustStore", httpConfig.getTrustStoreFile());
                System.setProperty("javax.net.ssl.trustStorePassword",
                        PasswordUtils.decryptText(httpConfig.getTrustStorePass(), httpConfig.getTrustStoreSalt().length()));
            }

            if (StringUtils.isNotEmpty(httpConfig.getKeyStoreFile()))
            {
                System.setProperty("javax.net.ssl.keyStoreType",
                        (StringUtils.isNotEmpty(httpConfig.getKeyStoreType()) ? httpConfig.getKeyStoreType() : "jks"));
                System.setProperty("javax.net.ssl.keyStore", httpConfig.getKeyStoreFile());
                System.setProperty("javax.net.ssl.keyStorePassword",
                        PasswordUtils.decryptText(httpConfig.getKeyStorePass(), httpConfig.getKeyStoreSalt().length()));
            }

            httpParams.setVersion(HttpVersion.parse(httpConfig.getHttpVersion()));
            httpParams.setSoTimeout(httpConfig.getSoTimeout());
            httpParams.setParameter("http.socket.linger", httpConfig.getSocketLinger());
            httpParams.setParameter("http.socket.timeout", httpConfig.getSocketTimeout());
            httpParams.setParameter("http.connection.timeout", httpConfig.getConnTimeout());
            httpParams.setParameter("http.connection-manager.timeout", httpConfig.getConnMgrTimeout());
            httpParams.setParameter("http.connection.stalecheck", httpConfig.getStaleCheck());

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
                        List<String> authList = new ArrayList<>();
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

                        String proxyPwd = PasswordUtils.decryptText(proxyConfig.getProxyPassword(), proxyConfig.getProxyPwdSalt().length());

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

            synchronized(new Object())
            {
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

                return httpMethod.getResponseBody();
            }
        }
        catch (ConnectException cx)
        {
            throw new UtilityException(cx.getMessage(), cx);
        }
        catch (UnknownHostException ux)
        {
            throw new UtilityException(ux.getMessage(), ux);
        }
        catch (SocketException sx)
        {
            throw new UtilityException(sx.getMessage(), sx);
        }
        catch (IOException iox)
        {
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
     * @param object - The serializable object to send to the target
     * @return Obj
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
            throw new UtilityException(cx.getMessage(), cx);
        }
        catch (UnknownHostException ux)
        {
            throw new UtilityException(ux.getMessage(), ux);
        }
        catch (SocketException sx)
        {
            throw new UtilityException(sx.getMessage(), sx);
        }
        catch (IOException iox)
        {
            throw new UtilityException(iox.getMessage(), iox);
        }
        catch (ClassNotFoundException cnfx)
        {
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
}
