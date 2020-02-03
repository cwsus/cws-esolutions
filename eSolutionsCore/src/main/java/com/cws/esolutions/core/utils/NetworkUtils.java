/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
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
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.net.URL;
import java.net.URI;
import java.util.List;
import java.net.Socket;
import org.slf4j.Logger;
import java.util.Arrays;
import org.xbill.DNS.Name;
import org.xbill.DNS.Type;
import java.util.Hashtable;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.IOException;
import org.xbill.DNS.Lookup;
import com.jcraft.jsch.JSch;
import java.net.InetAddress;
import org.xbill.DNS.Record;
import java.io.BufferedReader;
import java.security.Security;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;
import java.io.FileInputStream;
import org.slf4j.LoggerFactory;
import org.apache.http.HttpHost;
import java.io.FileOutputStream;
import java.net.SocketException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelExec;
import java.net.URISyntaxException;
import org.xbill.DNS.SimpleResolver;
import java.util.concurrent.TimeUnit;
import java.net.UnknownHostException;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.http.auth.AuthScope;
import org.apache.commons.io.FileUtils;
import org.xbill.DNS.TextParseException;
import org.apache.http.auth.NTCredentials;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;

import com.cws.esolutions.core.CoreServicesBean;
import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.config.xml.FTPConfig;
import com.cws.esolutions.core.config.xml.SSHConfig;
import com.cws.esolutions.core.config.xml.HTTPConfig;
import com.cws.esolutions.core.config.xml.ProxyConfig;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.core.utils.exception.UtilityException;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public final class NetworkUtils
{
    private static final String CRLF = "\r\n";
    private static final String TERMINATE_TELNET = "^]";
    private static final String PROXY_AUTH_TYPE_NTLM = "NTLM";
    private static final String PROXY_AUTH_TYPE_BASIC = "basic";
    private static final String CNAME = NetworkUtils.class.getName();
    private static final CoreServicesBean appBean = CoreServicesBean.getInstance();
    private static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER + CNAME);

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
     *  - provide passphrase
     * Type 'yes'
     * Type 'no'
     * Type 'yes'
     * Hit enter twice without entering a new passphrase
     * Convert the keyfile: ssh-keygen -i -f /path/to/file &gt; /path/to/new-file
     * Re-encrypt the file: ssh-keygen -p -f /path/to/new-file
     *
     * @param sourceFile - The full path to the source file to transfer
     * @param targetFile - The full path (including file name) of the desired target file
     * @param targetHost - The target server to perform the transfer to
     * @param isUpload - <code>true</code> is the transfer is an upload, <code>false</code> if it
     *            is a download 
     * @throws UtilityException - If an error occurs processing SSH keys or file transfer operations
     */
    public static final synchronized void executeSftpTransfer(final String sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeSftpTransfer(final String sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", sourceFile);
            DEBUGGER.debug("Value: {}", targetFile);
            DEBUGGER.debug("Value: {}", targetHost);
            DEBUGGER.debug("Value: {}", isUpload);
        }

        Session session = null;
        Channel channel = null;

        final SSHConfig sshConfig = appBean.getConfigData().getSshConfig();

        if (DEBUG)
        {
            DEBUGGER.debug("SSHConfig sshConfig: {}", sshConfig);
        }

        try
        {
            Hashtable<String, String> sshProperties = new Hashtable<String, String>();
            sshProperties.put("StrictHostKeyChecking", "no");

            if (DEBUG)
            {
                DEBUGGER.debug("Hashtable<String, String> sshProperties: {}", sshProperties);
            }

            JSch jsch = new JSch();
            JSch.setConfig(sshProperties);

            if (DEBUG)
            {
                DEBUGGER.debug("JSch jsch: {}", jsch);
            }

            session = jsch.getSession((StringUtils.isNotEmpty(sshConfig.getSshAccount())) ? sshConfig.getSshAccount() : System.getProperty("user.name"),
                targetHost, 22);

            if (DEBUG)
            {
                DEBUGGER.debug("Session session: {}", session);
            }

            if (StringUtils.isNotEmpty(sshConfig.getSshKey()))
            {
                if (!(FileUtils.getFile(sshConfig.getSshKey()).canRead()))
                {
                    throw new UtilityException("Provided keyfile cannot be accessed.");
                }

                switch (sshConfig.getSshPassword().length())
                {
                    case 0:
                        jsch.addIdentity(FileUtils.getFile(sshConfig.getSshKey()).toString());

                        break;
                    default:
                        jsch.addIdentity(FileUtils.getFile(sshConfig.getSshKey()).toString(),
                            PasswordUtils.decryptText(sshConfig.getSshPassword(), sshConfig.getSshSalt(),
                                secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                                secBean.getConfigData().getSecurityConfig().getIterations(),
                                secBean.getConfigData().getSecurityConfig().getKeyBits(),
                                secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                                secBean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                                appBean.getConfigData().getSystemConfig().getEncoding()));

                        break;
                }
            }
            else
            {
                session.setPassword(PasswordUtils.decryptText(sshConfig.getSshPassword(), sshConfig.getSshSalt(),
                        secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                        secBean.getConfigData().getSecurityConfig().getIterations(),
                        secBean.getConfigData().getSecurityConfig().getKeyBits(),
                        secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                        secBean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                        appBean.getConfigData().getSystemConfig().getEncoding()));
            }

            session.connect((int) TimeUnit.SECONDS.toMillis(appBean.getConfigData().getSshConfig().getTimeout()));

            if (!(session.isConnected()))
            {
                throw new UtilityException("Failed to connect to the target host");
            }

            channel = session.openChannel("sftp");
            channel.connect();

            if (DEBUG)
            {
                DEBUGGER.debug("Channel channel: {}", channel);
            }

            ChannelSftp sftpChannel = (ChannelSftp) channel;

            if (DEBUG)
            {
                DEBUGGER.debug("ChannelSftp sftpChannel: {}", sftpChannel);
            }

            if (isUpload)
            {
                
                sftpChannel.put(sourceFile, targetFile, ChannelSftp.OVERWRITE);

                int returnCode = sftpChannel.getExitStatus();

                if (DEBUG)
                {
                    DEBUGGER.debug("int returnCode: {}", returnCode);
                }
            }
            else
            {
                FileOutputStream outStream = new FileOutputStream(FileUtils.getFile(targetFile));

                if (DEBUG)
                {
                    DEBUGGER.debug("BufferedInputStream outStream: {}", outStream);
                }

                sftpChannel.get(sourceFile, outStream);

                int returnCode = sftpChannel.getExitStatus();

                if (DEBUG)
                {
                    DEBUGGER.debug("int returnCode: {}", returnCode);
                }

                outStream.flush();
                outStream.close();
            }

            channel.disconnect();
        }
        catch (IOException iox)
        {
            throw new UtilityException(iox.getMessage(), iox);
        }
        catch (SftpException sx)
        {
            throw new UtilityException(sx.getMessage(), sx);
        }
        catch (JSchException jx)
        {
            throw new UtilityException(jx.getMessage(), jx);
        }
        finally
        {
            if ((channel != null) && (channel.isConnected()))
            {
                channel.disconnect();
            }

            if ((session != null) && (session.isConnected()))
            {
                session.disconnect();
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
     *  - provide passphrase
     * Type 'yes'
     * Type 'no'
     * Type 'yes'
     * Hit enter twice without entering a new passphrase
     * Convert the keyfile: ssh-keygen -i -f /path/to/file &gt; /path/to/new-file
     * Re-encrypt the file: ssh-keygen -p -f /path/to/new-file
     *
     * @param targetHost - The target server to perform the transfer to
     * @param commandList - The list of commands to execute on the remote host.
     * @return <code>StringBuilder</code> containing the response information from the provided commandList
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an error occurs processing
     */
    public static final synchronized StringBuilder executeSshConnection(final String targetHost, final String commandList) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeSshConnection(final String targetHost, final String commandList) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", targetHost);
            DEBUGGER.debug("Value: {}", commandList);
        }

        Session session = null;
        Channel channel = null;
        StringBuilder sBuilder = null;

        final SSHConfig sshConfig = appBean.getConfigData().getSshConfig();

        if (DEBUG)
        {
            DEBUGGER.debug("SSHConfig: {}", sshConfig);
        }

        try
        {
            Hashtable<String, String> sshProperties = new Hashtable<String, String>();
            sshProperties.put("StrictHostKeyChecking", "yes");

            if (DEBUG)
            {
                DEBUGGER.debug("Hashtable<String, String> sshProperties: {}", sshProperties);
            }

            JSch jsch = new JSch();
            JSch.setConfig(sshProperties);

            if (DEBUG)
            {
                DEBUGGER.debug("JSch: {}", jsch);
            }

            session = jsch.getSession((StringUtils.isNotEmpty(sshConfig.getSshAccount())) ? sshConfig.getSshAccount() : System.getProperty("user.name"),
                    targetHost, 22);

            if (DEBUG)
            {
                DEBUGGER.debug("Session: {}", session);
            }

            if (StringUtils.isNotEmpty(sshConfig.getSshKey()))
            {
                if (!(FileUtils.getFile(sshConfig.getSshKey()).canRead()))
                {
                    throw new UtilityException("Provided keyfile cannot be accessed.");
                }

                switch (sshConfig.getSshPassword().length())
                {
                    case 0:
                        jsch.addIdentity(FileUtils.getFile(sshConfig.getSshKey()).toString());

                        break;
                    default:
                        jsch.addIdentity(FileUtils.getFile(sshConfig.getSshKey()).toString(),
                            PasswordUtils.decryptText(sshConfig.getSshPassword(), sshConfig.getSshSalt(),
                                    secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                                    secBean.getConfigData().getSecurityConfig().getIterations(),
                                    secBean.getConfigData().getSecurityConfig().getKeyBits(),
                                    secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                                    secBean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                                    appBean.getConfigData().getSystemConfig().getEncoding()));

                        break;
                }
            }
            else
            {
                session.setPassword(PasswordUtils.decryptText(sshConfig.getSshPassword(), sshConfig.getSshSalt(),
                        secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                        secBean.getConfigData().getSecurityConfig().getIterations(),
                        secBean.getConfigData().getSecurityConfig().getKeyBits(),
                        secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                        secBean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                        appBean.getConfigData().getSystemConfig().getEncoding()));
            }

            session.connect((int) TimeUnit.SECONDS.toMillis(appBean.getConfigData().getSshConfig().getTimeout()));

            if (!(session.isConnected()))
            {
                throw new UtilityException("Failed to connect to the target host");
            }

            if (StringUtils.isNotEmpty(commandList))
            {
                for (String cmd : StringUtils.split(commandList, ","))
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("cmd: {}", cmd);
                    }

                    channel = session.openChannel("exec");
                    ((ChannelExec) channel).setCommand(cmd.trim());
                    ((ChannelExec) channel).setErrStream(System.err);
                    channel.setInputStream(null);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ChannelExec: {}", channel);
                    }

                    channel.connect((int) TimeUnit.SECONDS.toMillis(appBean.getConfigData().getSshConfig().getTimeout()));

                    if (!(channel.isConnected()))
                    {
                        throw new UtilityException("Failed to open a channel connection to the target host");
                    }

                    String line = null;
                    BufferedReader bReader = new BufferedReader(new InputStreamReader(channel.getInputStream()));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("BufferedReader: {}", bReader);
                    }

                    sBuilder = new StringBuilder();

                    while ((line = bReader.readLine()) != null)
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Data: {}", line);
                        }

                        sBuilder.append(line + CoreServiceConstants.LINE_BREAK);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("StringBuilder: {}", sBuilder.toString());
                    }

                    bReader.close();

                    channel.disconnect();
                }
            }
        }
        catch (IOException iox)
        {
            throw new UtilityException(iox.getMessage(), iox);
        }
        catch (JSchException jx)
        {
            throw new UtilityException(jx.getMessage(), jx);
        }
        finally
        {
            if ((channel != null) && (channel.isConnected()))
            {
                channel.disconnect();
            }

            if ((session != null) && (session.isConnected()))
            {
                session.disconnect();
            }
        }

        return sBuilder;
    }

    /**
     * Creates a connection to a target host and then executes an FTP
     * request to send or receive a file to or from the target. This is fully
     * key-based, as a result, a keyfile is required for the connection to
     * successfully authenticate.
     * 
     * @param sourceFile - The full path to the source file to transfer
     * @param targetFile - The full path (including file name) of the desired target file
     * @param targetHost - The target server to perform the transfer to
     * @param isUpload - <code>true</code> is the transfer is an upload, <code>false</code> if it
     *            is a download 
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an error occurs processing
     */
    public static final synchronized void executeFtpConnection(final String sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeFtpConnection(final String sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", sourceFile);
            DEBUGGER.debug("Value: {}", targetFile);
            DEBUGGER.debug("Value: {}", targetHost);
            DEBUGGER.debug("Value: {}", isUpload);
        }

        final FTPClient client = new FTPClient();
        final FTPConfig ftpConfig = appBean.getConfigData().getFtpConfig();

        if (DEBUG)
        {
            DEBUGGER.debug("FTPClient: {}", client);
            DEBUGGER.debug("FTPConfig: {}", ftpConfig);
        }

        try
        {
            client.connect(targetHost);

            if (DEBUG)
            {
                DEBUGGER.debug("FTPClient: {}", client);
            }

            if (!(client.isConnected()))
            {
                throw new IOException("Failed to authenticate to remote host with the provided information");
            }

            boolean isAuthenticated = false;

            if (StringUtils.isNotBlank(ftpConfig.getFtpAccount()))
            {
                isAuthenticated = client.login((StringUtils.isNotEmpty(ftpConfig.getFtpAccount())) ? ftpConfig.getFtpAccount() : System.getProperty("user.name"),
                    PasswordUtils.decryptText(ftpConfig.getFtpPassword(), ftpConfig.getFtpSalt(),
                            secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                            secBean.getConfigData().getSecurityConfig().getIterations(),
                            secBean.getConfigData().getSecurityConfig().getKeyBits(),
                            secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                            secBean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                            appBean.getConfigData().getSystemConfig().getEncoding()));
            }
            else
            {
                isAuthenticated = client.login(ftpConfig.getFtpAccount(), null);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("isAuthenticated: {}", isAuthenticated);
            }

            if (!(isAuthenticated))
            {
                throw new IOException("Failed to connect to FTP server: " + targetHost);
            }

            client.enterLocalPassiveMode();

            if (!(FileUtils.getFile(sourceFile).exists()))
            {
                throw new IOException("File " + sourceFile + " does not exist. Skipping");
            }

            if (isUpload)
            {
                client.storeFile(targetFile, new FileInputStream(FileUtils.getFile(sourceFile)));
            }
            else
            {
                client.retrieveFile(sourceFile, new FileOutputStream(targetFile));
            }

            if (DEBUG)
            {
                DEBUGGER.debug("Reply: {}", client.getReplyCode());
                DEBUGGER.debug("Reply: {}", client.getReplyString());
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
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an error occurs processing
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
                if (InetAddress.getByName(hostName) == null)
                {
                    throw new UnknownHostException("No host was found in DNS for the given name: " + hostName);
                }

                InetSocketAddress socketAddress = new InetSocketAddress(hostName, portNumber);

                socket = new Socket();
                socket.setSoTimeout((int) TimeUnit.SECONDS.toMillis(timeout));
                socket.setSoLinger(false, 0);
                socket.setKeepAlive(false);
                socket.connect(socketAddress, (int) TimeUnit.SECONDS.toMillis(timeout));

                if (!(socket.isConnected()))
                {
                    throw new ConnectException("Failed to connect to host " + hostName + " on port " + portNumber);
                }

                PrintWriter pWriter = new PrintWriter(socket.getOutputStream(), true);

                pWriter.println(NetworkUtils.TERMINATE_TELNET + NetworkUtils.CRLF);

                pWriter.flush();
                pWriter.close();
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
     * @param methodType - GET or POST, depending on what is necessary
     * @return A object containing the response data
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an error occurs processing
     */
    public static final synchronized Object executeHttpConnection(final URL hostName, final String methodType) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeHttpConnection(final URL hostName, final String methodType) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", hostName);
            DEBUGGER.debug("Value: {}", methodType);
        }

        RequestConfig requestConfig = null;
        CloseableHttpClient httpClient = null;
        CredentialsProvider credsProvider = null;
        CloseableHttpResponse httpResponse = null;

        final HttpClientParams httpParams = new HttpClientParams();
        final HTTPConfig httpConfig = appBean.getConfigData().getHttpConfig();
        final ProxyConfig proxyConfig = appBean.getConfigData().getProxyConfig();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpClient: {}", httpClient);
            DEBUGGER.debug("HttpClientParams: {}", httpParams);
            DEBUGGER.debug("HTTPConfig: {}", httpConfig);
            DEBUGGER.debug("ProxyConfig: {}", proxyConfig);
        }
        try
        {
            final URI requestURI = new URIBuilder()
                .setScheme(hostName.getProtocol())
                .setHost(hostName.getHost())
                .setPort(hostName.getPort())
                .build();

            if (StringUtils.isNotEmpty(httpConfig.getTrustStoreFile()))
            {
                System.setProperty("javax.net.ssl.trustStoreType",
                        (StringUtils.isNotEmpty(httpConfig.getTrustStoreType()) ? httpConfig.getTrustStoreType() : "jks"));
                System.setProperty("javax.net.ssl.trustStore", httpConfig.getTrustStoreFile());
                System.setProperty("javax.net.ssl.trustStorePassword",
                    PasswordUtils.decryptText(httpConfig.getTrustStorePass(), httpConfig.getTrustStoreSalt(),
                            secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                            secBean.getConfigData().getSecurityConfig().getIterations(),
                            secBean.getConfigData().getSecurityConfig().getKeyBits(),
                            secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                            secBean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                            appBean.getConfigData().getSystemConfig().getEncoding()));
            }

            if (StringUtils.isNotEmpty(httpConfig.getKeyStoreFile()))
            {
                System.setProperty("javax.net.ssl.keyStoreType",
                    (StringUtils.isNotEmpty(httpConfig.getKeyStoreType()) ? httpConfig.getKeyStoreType() : "jks"));
                System.setProperty("javax.net.ssl.keyStore", httpConfig.getKeyStoreFile());
                System.setProperty("javax.net.ssl.keyStorePassword",
                    PasswordUtils.decryptText(httpConfig.getKeyStorePass(), httpConfig.getKeyStoreSalt(),
                            secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                            secBean.getConfigData().getSecurityConfig().getIterations(),
                            secBean.getConfigData().getSecurityConfig().getKeyBits(),
                            secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                            secBean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                            appBean.getConfigData().getSystemConfig().getEncoding()));
            }

            if (proxyConfig.isProxyServiceRequired())
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("ProxyConfig: {}", proxyConfig);
                }

                if (StringUtils.isEmpty(proxyConfig.getProxyServerName()))
                {
                    throw new UtilityException("Configuration states proxy usage is required, but no proxy is configured.");
                }

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

                    requestConfig = RequestConfig.custom()
                        .setConnectionRequestTimeout((int) TimeUnit.SECONDS.toMillis(httpConfig.getConnTimeout()))
                        .setConnectTimeout((int) TimeUnit.SECONDS.toMillis(httpConfig.getConnTimeout()))
                        .setContentCompressionEnabled(Boolean.TRUE)
                        .setProxy(new HttpHost(proxyConfig.getProxyServerName(), proxyConfig.getProxyServerPort()))
                        .setProxyPreferredAuthSchemes(authList)
                        .build();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("requestConfig: {}", requestConfig);
                    }

                    String proxyPwd = PasswordUtils.decryptText(proxyConfig.getProxyPassword(), proxyConfig.getProxyPwdSalt(),
                            secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                            secBean.getConfigData().getSecurityConfig().getIterations(),
                            secBean.getConfigData().getSecurityConfig().getKeyBits(),
                            secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                            secBean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                            appBean.getConfigData().getSystemConfig().getEncoding());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("proxyPwd: {}", proxyPwd);
                    }

                    if (StringUtils.equals(NetworkUtils.PROXY_AUTH_TYPE_BASIC, proxyConfig.getProxyAuthType()))
                    {
                        credsProvider = new SystemDefaultCredentialsProvider();
                        credsProvider.setCredentials(new AuthScope(proxyConfig.getProxyServerName(), proxyConfig.getProxyServerPort()),
                            new UsernamePasswordCredentials(proxyConfig.getProxyUserId(), proxyPwd));
                    }
                    else if (StringUtils.equals(NetworkUtils.PROXY_AUTH_TYPE_NTLM, proxyConfig.getProxyAuthType()))
                    {
                        credsProvider = new SystemDefaultCredentialsProvider();
                        credsProvider.setCredentials(new AuthScope(proxyConfig.getProxyServerName(), proxyConfig.getProxyServerPort()),
                            new NTCredentials(proxyConfig.getProxyUserId(), proxyPwd, InetAddress.getLocalHost().getHostName(), proxyConfig.getProxyAuthDomain()));
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("httpClient: {}", httpClient);
                    }
                }
            }

            synchronized(new Object())
            {
                httpClient = HttpClients.custom()
                    .setDefaultCredentialsProvider(credsProvider)
                    .build();

                if (StringUtils.equalsIgnoreCase(methodType, "POST"))
                {
                    HttpPost httpMethod = new HttpPost(requestURI);
                    httpMethod.setConfig(requestConfig);

                    httpResponse = httpClient.execute(httpMethod);
                }
                else
                {
                    HttpGet httpMethod = new HttpGet(requestURI);
                    httpMethod.setConfig(requestConfig);

                    httpResponse = httpClient.execute(httpMethod);
                }

                int responseCode = httpResponse.getStatusLine().getStatusCode();

                if (DEBUG)
                {
                    DEBUGGER.debug("responseCode: {}", responseCode);
                }

                if (responseCode != 200)
                {
                    ERROR_RECORDER.error("HTTP Response Code received NOT 200: " + responseCode);

                    throw new UtilityException("HTTP Response Code received NOT 200: " + responseCode);
                }

                return httpResponse.getEntity().toString();
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
        catch (URISyntaxException usx)
        {
            throw new UtilityException(usx.getMessage(), usx);
        }
        finally
        {
            if (httpResponse != null)
            {
                try
                {
                    httpResponse.close();
                }
                catch (IOException iox) {} // dont do anything with it
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
     * @param timeout - How long to wait for a connection to establish or a response from the target
     * @param object - The serializable object to send to the target
     * @return <code>Object</code> as output from the request
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an error occurs processing
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
                if (StringUtils.isEmpty(InetAddress.getByName(hostName).toString()))
                {
                    throw new UnknownHostException("No host was found in DNS for the given name: " + hostName);
                }

                InetSocketAddress socketAddress = new InetSocketAddress(hostName, portNumber);

                socket = new Socket();
                socket.setSoTimeout((int) TimeUnit.SECONDS.toMillis(timeout));
                socket.setSoLinger(false, 0);
                socket.setKeepAlive(false);
                socket.connect(socketAddress, (int) TimeUnit.SECONDS.toMillis(timeout));

                if (!(socket.isConnected()))
                {
                    throw new ConnectException("Failed to connect to host " + hostName + " on port " + portNumber);
                }

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

    /**
     * Performs a DNS lookup of a given name and type against a provided server
     * (or if no server is provided, the default system resolver).
     *
     * If an error occurs during the lookup, a <code>UtilityException</code> is
     * thrown containing the error response text.
     * 
     * @param resolverHost - The target host to use for resolution. Can be null, if not provided the
     * the default system resolver is used.
     * @param recordName - The DNS hostname/IP address to lookup.
     * @param recordType - The type of record to look up.
     * @param searchList - A search list to utilize if a short name is provided.
     * @return An ArrayList as output from the request
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an error occurs processing
     */
    public static final synchronized List<List<String>> executeDNSLookup(final String resolverHost, final String recordName, final String recordType, final String[] searchList) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeDNSLookup(final String resolverHost, final String recordName, final String recordType, final String[] searchList) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("String: {}", resolverHost);
            DEBUGGER.debug("String: {}", recordName);
            DEBUGGER.debug("String: {}", recordType);
            DEBUGGER.debug("String: {}", (Object) searchList);
        }

        Lookup lookup = null;
        String responseName = null;
        String responseType = null;
        Record[] recordList = null;
        String responseAddress = null;
        SimpleResolver resolver = null;
        List<String> lookupData = null;
        List<List<String>> response = null;

        final String currentTimeout = Security.getProperty("networkaddress.cache.ttl");

        if (DEBUG)
        {
            DEBUGGER.debug("currentTimeout: {}", currentTimeout);
        }

        try
        {
            // no authorization required for service lookup
            Name name = Name.fromString(recordName);
            lookup = new Lookup(name, Type.value(recordType));

            if (DEBUG)
            {
                DEBUGGER.debug("Name: {}", name);
                DEBUGGER.debug("Lookup: {}", lookup);
            }

            if (StringUtils.isNotEmpty(resolverHost))
            {
                resolver = new SimpleResolver(resolverHost);

                if (DEBUG)
                {
                    DEBUGGER.debug("SimpleResolver: {}", resolver);
                }
            }
            else
            {
                resolver = new SimpleResolver();

                if (DEBUG)
                {
                    DEBUGGER.debug("SimpleResolver: {}", resolver);
                }
            }

            lookup.setResolver(resolver);
            lookup.setCache(null);

            if (searchList != null)
            {
                lookup.setSearchPath(searchList);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("Lookup: {}", lookup);
            }

            recordList = lookup.run();

            if (DEBUG)
            {
                if (recordList != null)
                {
                    for (Record dRecord : recordList)
                    {
                        DEBUGGER.debug("Record: {}", dRecord);
                    }
                }
            }

            if (lookup.getResult() != Lookup.SUCCESSFUL)
            {
                throw new UtilityException("An error occurred during the lookup. The response obtained is: " + lookup.getErrorString());
            }

            response = new ArrayList<List<String>>();

            if ((recordList == null) || (recordList.length == 0))
            {
                throw new UtilityException("No results were found for the provided information.");
            }

            switch (recordList.length)
            {
                case 1:
                    Record sRecord = recordList[0];

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Record: {}", sRecord);
                    }

                    responseAddress = sRecord.rdataToString();
                    responseName = sRecord.getName().toString();
                    responseType = Type.string(sRecord.getType());

                    lookupData = new ArrayList<String>(
                            Arrays.asList(
                                    responseAddress,
                                    responseName,
                                    responseType));
                    if (DEBUG)
                    {
                        DEBUGGER.debug("responseAddress: {}", responseAddress);
                        DEBUGGER.debug("responseName: {}", responseName);
                        DEBUGGER.debug("responseType: {}", responseType);
                    }

                    response.add(lookupData);

                    break;
                default:
                    for (Record mRecord : recordList)
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Record: {}", mRecord);
                        }

                        responseAddress = mRecord.rdataToString();
                        responseName = mRecord.getName().toString();
                        responseType = Type.string(mRecord.getType());

                        lookupData = new ArrayList<String>(
                                Arrays.asList(
                                        responseAddress,
                                        responseName,
                                        responseType));
                        if (DEBUG)
                        {
                            DEBUGGER.debug("responseAddress: {}", responseAddress);
                            DEBUGGER.debug("responseName: {}", responseName);
                            DEBUGGER.debug("responseType: {}", responseType);
                        }

                        response.add(lookupData);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("response: {}", response);
                        }
                    }

                    break;
            }

            if (DEBUG)
            {
                DEBUGGER.debug("response: {}", response);
            }
        }
        catch (TextParseException tpx)
        {
            ERROR_RECORDER.error(tpx.getMessage(), tpx);

            throw new UtilityException(tpx.getMessage(), tpx);
        }
        catch (UnknownHostException uhx)
        {
            ERROR_RECORDER.error(uhx.getMessage(), uhx);

            throw new UtilityException(uhx.getMessage(), uhx);
        }
        finally
        {
            // reset java dns timeout
            try
            {
                Security.setProperty("networkaddress.cache.ttl", currentTimeout);
            }
            catch (NullPointerException npx) {}
        }

        return response;
    }
}
