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
import java.util.Arrays;
import org.slf4j.Logger;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.IOException;
import com.jcraft.jsch.JSch;
import java.net.InetAddress;
import java.io.BufferedReader;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;
import java.io.FileInputStream;
import org.slf4j.LoggerFactory;
import java.io.FileOutputStream;
import java.net.SocketException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import com.jcraft.jsch.ChannelExec;
import com.sshtools.j2ssh.ScpClient;
import com.sshtools.j2ssh.SshClient;
import java.util.concurrent.TimeUnit;
import com.sshtools.j2ssh.SftpClient;
import com.jcraft.jsch.OpenSSHConfig;
import java.net.UnknownHostException;
import com.jcraft.jsch.JSchException;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import com.jcraft.jsch.ConfigRepository;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.methods.GetMethod;
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

import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.config.xml.FTPConfig;
import com.cws.esolutions.core.config.xml.SSHConfig;
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
@SuppressWarnings("static-access")
public final class NetworkUtils
{
    private static Options options = null;
    private static OptionGroup sshOptions = null;
    private static OptionGroup scpOptions = null;
    private static OptionGroup tcpOptions = null;
    private static OptionGroup httpOptions = null;
    private static OptionGroup telnetOptions = null;

    private static final String CRLF = "\r\n";
    private static final String TERMINATE_TELNET = "^]";
    private static final String PROXY_AUTH_TYPE_NTLM = "NTLM";
    private static final String PROXY_AUTH_TYPE_BASIC = "basic";
    private static final String CNAME = NetworkUtils.class.getName();
    private static final CoreServiceBean appBean = CoreServiceBean.getInstance();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER + CNAME);

    static
    {
        OptionGroup commandOptions = new OptionGroup();
        commandOptions.addOption(OptionBuilder.withLongOpt("ssh")
            .withDescription("Perform an SSH connection to a target host")
            .isRequired(true)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("scp")
            .withDescription("Perform an SCP connection to a target host")
            .isRequired(true)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("telnet")
            .withDescription("Perform an telnet connection to a target host")
            .isRequired(true)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("tcp")
            .withDescription("Perform an TCP connection to a target host and put data on the request")
            .isRequired(true)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("http")
            .withDescription("Perform an HTTP request to a target host")
            .isRequired(true)
            .create());

        options = new Options();
        options.addOptionGroup(commandOptions);

        sshOptions = new OptionGroup();
        sshOptions.addOption(OptionBuilder.withLongOpt("targetHost")
            .withDescription("The hostname of the server to connect to")
            .hasArg(true)
            .withArgName("HOSTNAME")
            .withType(String.class)
            .isRequired(true)
            .create());
        sshOptions.addOption(OptionBuilder.withLongOpt("commandList")
            .withDescription("A comma separated list of commands to execute on the host")
            .hasArg(true)
            .withArgName("COMMANDS")
            .withType(String.class)
            .isRequired(false)
            .create());

        scpOptions = new OptionGroup();
        scpOptions.addOption(OptionBuilder.withLongOpt("sourceFile")
            .withDescription("The source file to copy to/from the host")
            .hasArg(true)
            .withArgName("SOURCE")
            .withType(String.class)
            .isRequired(true)
            .create());
        scpOptions.addOption(OptionBuilder.withLongOpt("targetFile")
            .withDescription("The target file to copy to/from the host")
            .hasArg(true)
            .withArgName("TARGET")
            .withType(String.class)
            .isRequired(true)
            .create());
        scpOptions.addOption(OptionBuilder.withLongOpt("targetHost")
            .withDescription("The hostname of the server to connect to")
            .hasArg(true)
            .withArgName("HOSTNAME")
            .withType(String.class)
            .isRequired(true)
            .create());
        scpOptions.addOption(OptionBuilder.withLongOpt("isUpload")
            .withDescription("Determines if this is a local -> remote transfer. True if yes, false otherwise")
            .hasArg(true)
            .withArgName("UPLOAD")
            .withType(Boolean.class)
            .isRequired(true)
            .create());

        telnetOptions = new OptionGroup();
        telnetOptions.addOption(OptionBuilder.withLongOpt("targetHost")
            .withDescription("The hostname of the server to connect to")
            .hasArg(true)
            .withArgName("HOSTNAME")
            .withType(String.class)
            .isRequired(true)
            .create());
        telnetOptions.addOption(OptionBuilder.withLongOpt("port")
            .withDescription("The port number to connect to the server on")
            .hasArg(true)
            .withArgName("PORT")
            .withType(Integer.class)
            .isRequired(true)
            .create());
        telnetOptions.addOption(OptionBuilder.withLongOpt("timeout")
            .withDescription("The length of time to wait for the connection to complete. Defaults to 10 seconds if not specified.")
            .hasArg(true)
            .withArgName("TIMEOUT")
            .withType(Integer.class)
            .isRequired(false)
            .create());

        tcpOptions = new OptionGroup();
        tcpOptions.addOption(OptionBuilder.withLongOpt("targetHost")
            .withDescription("The hostname of the server to connect to")
            .hasArg(true)
            .withArgName("HOSTNAME")
            .withType(String.class)
            .isRequired(true)
            .create());
        tcpOptions.addOption(OptionBuilder.withLongOpt("port")
            .withDescription("The port number to connect to the server on")
            .hasArg(true)
            .withArgName("PORT")
            .withType(Integer.class)
            .isRequired(true)
            .create());
        tcpOptions.addOption(OptionBuilder.withLongOpt("timeout")
            .withDescription("The length of time to wait for the connection to complete. Defaults to 10 seconds if not specified.")
            .hasArg(true)
            .withArgName("TIMEOUT")
            .withType(Integer.class)
            .isRequired(false)
            .create());
        tcpOptions.addOption(OptionBuilder.withLongOpt("data")
            .withDescription("The data to place on the telnet request for response.")
            .hasArg(true)
            .withArgName("OBJECT")
            .isRequired(false)
            .create());

        httpOptions = new OptionGroup();
        httpOptions.addOption(OptionBuilder.withLongOpt("targetHost")
            .withDescription("The hostname of the server to connect to")
            .hasArg(true)
            .withArgName("HOSTNAME")
            .withType(String.class)
            .isRequired(true)
            .create());
        httpOptions.addOption(OptionBuilder.withLongOpt("method")
            .withDescription("The data to place on the telnet request for response.")
            .hasArg(true)
            .withArgName("HTTP-METHOD")
            .withType(String.class)
            .isRequired(true)
            .create());
    }

    public static final void main(final String[] args)
    {
        final String methodName = NetworkUtils.CNAME + "#main(final String[] args)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", (Object) args);
        }

        if (args.length == 0)
        {
            HelpFormatter usage = new HelpFormatter();
            usage.printHelp(NetworkUtils.CNAME, options, true);

            return;
        }

        try
        {
            CoreServiceInitializer.initializeService("C:/opt/cws/eSolutions/etc/eSolutionsCore/config/ServiceConfig.xml",
                "C:/opt/cws/eSolutions/etc/eSolutionsCore/logging/logging.xml");
        }
        catch (CoreServiceException csx)
        {
            System.err.println("An error occurred while loading configuration data: " + csx.getCause().getMessage());

            System.exit(1);
        }

        try
        {
            SecurityServiceInitializer.initializeService("C:/opt/cws/eSolutions/etc/SecurityService/config/ServiceConfig.xml",
                "C:/opt/cws/eSolutions/etc/SecurityService/logging/logging.xml", false);
        }
        catch (SecurityServiceException ssx)
        {
            System.err.println("An error occurred while loading configuration data: " + ssx.getCause().getMessage());

            System.exit(1);
        }

        Options options = new Options();
        CommandLineParser parser = new PosixParser();

        try
        {
            if (StringUtils.equals(args[0], "ssh"))
            {
                options.addOptionGroup(sshOptions);

                CommandLine commandLine = parser.parse(options, args);

                if (commandLine.getOptions().length >= 1)
                {
                    try
                    {
                        NetworkUtils.executeSshConnection(commandLine.getOptionValue("targetHost"),
                                (commandLine.hasOption("commandList") ? commandLine.getOptionValue("commandList") : null));
                    }
                    catch (UtilityException ux)
                    {
                        System.err.println("An error occurred while executing the request: " + ux.getMessage());
                    }
                }
                else
                {
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp(NetworkUtils.CNAME + " ssh", options, true);
                }
            }

            if (StringUtils.equals(args[0], "scp"))
            {
                options.addOptionGroup(scpOptions);

                CommandLine commandLine = parser.parse(options, args);

                if (commandLine.getOptions().length == 4)
                {
                    List<String> sourceList = new ArrayList<>(
                        Arrays.asList(commandLine.getOptionValue(commandLine.getOptionValue("sourceFile"))));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<String>: {}", sourceList);
                    }

                    try
                    {
                        NetworkUtils.executeSCPTransfer(sourceList,
                                commandLine.getOptionValue("targetFile"),
                                commandLine.getOptionValue("targetHost"),
                                Boolean.valueOf(commandLine.getOptionValue("isUpload")));
                    }
                    catch (UtilityException ux)
                    {
                        System.err.println("An error occurred while executing the request: " + ux.getMessage());
                    }
                }
                else
                {
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp(NetworkUtils.CNAME + " scp", options, true);
                }
            }

            if (StringUtils.equals(args[0], "telnet"))
            {
                options.addOptionGroup(telnetOptions);

                CommandLine commandLine = parser.parse(options, args);

                if (commandLine.getOptions().length >= 2)
                {
                    try
                    {
                        NetworkUtils.executeTelnetRequest(commandLine.getOptionValue("targetHost"),
                                Integer.valueOf(commandLine.getOptionValue("port")),
                                (commandLine.hasOption("timeout") ? Integer.parseInt(commandLine.getOptionValue("timeout"))
                                        : appBean.getConfigData().getSshConfig().getTimeout()));
                    }
                    catch (UtilityException ux)
                    {
                        System.err.println("An error occurred while executing the request: " + ux.getMessage());
                    }
                }
                else
                {
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp(NetworkUtils.CNAME + " telnet", options, true);
                }
            }

            if (StringUtils.equals(args[0], "tcp"))
            {
                options.addOptionGroup(tcpOptions);

                CommandLine commandLine = parser.parse(options, args);

                if (commandLine.getOptions().length == 4)
                {
                    try
                    {
                        NetworkUtils.executeTcpRequest(commandLine.getOptionValue("targetHost"),
                                Integer.valueOf(commandLine.getOptionValue("port")),
                                (commandLine.hasOption("timeout") ? Integer.parseInt(commandLine.getOptionValue("timeout"))
                                        : appBean.getConfigData().getSshConfig().getTimeout()),
                                commandLine.getParsedOptionValue("data"));
                    }
                    catch (UtilityException ux)
                    {
                        System.err.println("An error occurred while executing the request: " + ux.getMessage());
                    }
                }
                else
                {
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp(NetworkUtils.CNAME + " telnet", options, true);
                }
            }

            if (StringUtils.equals(args[0], "http"))
            {
                options.addOptionGroup(httpOptions);

                CommandLine commandLine = parser.parse(options, args);

                if (commandLine.getOptions().length >= 2)
                {
                    try
                    {
                        NetworkUtils.executeHttpConnection(commandLine.getOptionValue("targetHost"),
                                commandLine.getOptionValue("method"));
                    }
                    catch (UtilityException ux)
                    {
                        System.err.println("An error occurred while executing the request: " + ux.getMessage());
                    }
                }
                else
                {
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp(NetworkUtils.CNAME + " http", options, true);
                }
            }
        }
        catch (ParseException px)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(NetworkUtils.CNAME, options, true);
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
     * @param isUpload - <code>true</code> is the transfer is an upload, <code>false</code> if it
     *            is a download 
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an error occurs processing
     */
    public static final synchronized void executeSCPTransfer(final List<String> sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeSCPTransfer(final List<String> authList, final String targetFile, final String targetHost, final String passphrase) throws UtilityException";

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
            SshConnectionProperties connProps = new SshConnectionProperties();
            connProps.setHost(targetHost); // set as obtained from db

            if (DEBUG)
            {
                DEBUGGER.debug("SshConnectionProperties: {}", connProps);
            }

            boolean isKeyAuthentication = false;
            PasswordAuthenticationClient passAuth = null;
            PublicKeyAuthenticationClient keyAuth = null;

            if (StringUtils.isNotEmpty(sshConfig.getSshKey()))
            {
                isKeyAuthentication = true;

                SshPrivateKeyFile sshPrivateKeyFile = SshPrivateKeyFile.parse(FileUtils.getFile(sshConfig.getSshKey()));
                SshPrivateKey sshPrivateKey = null;

                switch (sshConfig.getSshPassword().length())
                {
                    case 0:
                        sshPrivateKeyFile.toPrivateKey(null);

                        break;
                    default:
                        sshPrivateKeyFile.toPrivateKey(PasswordUtils.decryptText(sshConfig.getSshPassword(), sshConfig.getSshSalt().length(),
                                appBean.getConfigData().getAppConfig().getAlgorithm(), appBean.getConfigData().getAppConfig().getInstance(),
                                appBean.getConfigData().getAppConfig().getEncoding()));

                        break;
                }

                keyAuth = new PublicKeyAuthenticationClient();
                keyAuth.setKey(sshPrivateKey);
                keyAuth.setUsername((StringUtils.isNotEmpty(sshConfig.getSshAccount())) ? sshConfig.getSshAccount() : System.getProperty("user.name"));

                if (DEBUG)
                {
                    DEBUGGER.debug("PublicKeyAuthenticationClient: {}", keyAuth);
                }
            }
            else
            {
                passAuth = new PasswordAuthenticationClient();
                passAuth.setUsername((StringUtils.isNotEmpty(sshConfig.getSshAccount())) ? sshConfig.getSshAccount() : System.getProperty("user.name"));
                passAuth.setPassword(PasswordUtils.decryptText(sshConfig.getSshPassword(), sshConfig.getSshSalt().length(),
                        appBean.getConfigData().getAppConfig().getAlgorithm(), appBean.getConfigData().getAppConfig().getInstance(),
                        appBean.getConfigData().getAppConfig().getEncoding()));

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

                        for (String str : sourceFile)
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("File: {}", str);
                            }

                            if (isUpload)
                            {
                                File srcFile = FileUtils.getFile(str);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("File: {}", srcFile);
                                }

                                if (!(srcFile.canRead()))
                                {
                                    ERROR_RECORDER.error("File {} does not exist or cannot be read. Skipping!", srcFile);
                                }

                                if (srcFile.canRead())
                                {
                                    client.put(srcFile.getAbsoluteFile().toString(), targetFile, false);
                                }
                            }
                            else
                            {
                                client.get(targetFile, str, false);
                            }
                        }
                    }
                    else
                    {
                        throw new AuthenticationProtocolException("Failed to authenticate to remote host.");
                    }
                }
                else
                {
                    throw new AuthenticationProtocolException("Failed to authenticate to remote host.");
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
     * @param isUpload - <code>true</code> is the transfer is an upload, <code>false</code> if it
     *            is a download 
     * @throws UtilityException - If an error occurs processing SSH keys or file transfer operations
     */
    public static final synchronized void executeSftpTransfer(final List<File> sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeSftpTransfer(final List<File> sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException";

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

                if (StringUtils.isNotEmpty(sshConfig.getSshKey()))
                {
                    isKeyAuthentication = true;

                    SshPrivateKeyFile sshPrivateKeyFile = SshPrivateKeyFile.parse(FileUtils.getFile(sshConfig.getSshKey()));
                    SshPrivateKey sshPrivateKey = null;

                    switch (sshConfig.getSshPassword().length())
                    {
                        case 0:
                            sshPrivateKeyFile.toPrivateKey(null);

                            break;
                        default:
                            sshPrivateKeyFile.toPrivateKey(PasswordUtils.decryptText(sshConfig.getSshPassword(), sshConfig.getSshSalt().length(),
                                    appBean.getConfigData().getAppConfig().getAlgorithm(), appBean.getConfigData().getAppConfig().getInstance(),
                                    appBean.getConfigData().getAppConfig().getEncoding()));
                            break;
                    }

                    keyAuth = new PublicKeyAuthenticationClient();
                    keyAuth.setKey(sshPrivateKey);
                    keyAuth.setUsername((StringUtils.isNotEmpty(sshConfig.getSshAccount())) ? sshConfig.getSshAccount() : System.getProperty("user.name"));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("PublicKeyAuthenticationClient: {}", keyAuth);
                    }
                }
                else
                {
                    passAuth = new PasswordAuthenticationClient();
                    passAuth.setUsername((StringUtils.isNotEmpty(sshConfig.getSshAccount())) ? sshConfig.getSshAccount() : System.getProperty("user.name"));
                    passAuth.setPassword(PasswordUtils.decryptText(sshConfig.getSshPassword(), sshConfig.getSshSalt().length(),
                            appBean.getConfigData().getAppConfig().getAlgorithm(), appBean.getConfigData().getAppConfig().getInstance(),
                            appBean.getConfigData().getAppConfig().getEncoding()));

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
                            throw new AuthenticationProtocolException("Failed to authenticate to remote host.");
                        }
                    }
                    else
                    {
                        throw new AuthenticationProtocolException("Failed to authenticate to remote host.");
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
            ConfigRepository configRepository = OpenSSHConfig.parseFile(sshConfig.getSshProperties());

            if (DEBUG)
            {
                DEBUGGER.debug("ConfigRepository: {}", configRepository);
            }

            JSch jsch = new JSch();
            jsch.setConfigRepository(configRepository);

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
                            PasswordUtils.decryptText(sshConfig.getSshPassword(), sshConfig.getSshSalt().length(),
                                appBean.getConfigData().getAppConfig().getAlgorithm(), appBean.getConfigData().getAppConfig().getInstance(),
                                appBean.getConfigData().getAppConfig().getEncoding()));

                        break;
                }
            }
            else
            {
                session.setPassword(PasswordUtils.decryptText(sshConfig.getSshPassword(), sshConfig.getSshSalt().length(),
                        appBean.getConfigData().getAppConfig().getAlgorithm(), appBean.getConfigData().getAppConfig().getInstance(),
                        appBean.getConfigData().getAppConfig().getEncoding()));
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
    public static final synchronized void executeFtpConnection(final List<File> sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeFtpConnection(final List<File> sourceFile, final String targetFile, final String targetHost, final boolean isUpload) throws UtilityException";

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

            if (client.isConnected())
            {
                boolean isAuthenticated = false;

                if (StringUtils.isNotBlank(ftpConfig.getFtpAccount()))
                {
                    isAuthenticated = client.login((StringUtils.isNotEmpty(ftpConfig.getFtpAccount())) ? ftpConfig.getFtpAccount() : System.getProperty("user.name"),
                            PasswordUtils.decryptText(ftpConfig.getFtpPassword(), ftpConfig.getFtpSalt().length(),
                            appBean.getConfigData().getAppConfig().getAlgorithm(), appBean.getConfigData().getAppConfig().getInstance(),
                            appBean.getConfigData().getAppConfig().getEncoding()));
                }
                else
                {
                    isAuthenticated = client.login(ftpConfig.getFtpAccount(), null);
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
                if (InetAddress.getByName(hostName) != null)
                {
                    InetSocketAddress socketAddress = new InetSocketAddress(hostName, portNumber);

                    socket = new Socket();
                    socket.setSoTimeout((int) TimeUnit.SECONDS.toMillis(timeout));
                    socket.setSoLinger(false, 0);
                    socket.setKeepAlive(false);
                    socket.connect(socketAddress, (int) TimeUnit.SECONDS.toMillis(timeout));

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
     * @param method - GET or POST, depending on what is necessary
     * @return A byte array containing the response data
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an error occurs processing
     */
    public static final synchronized byte[] executeHttpConnection(final String hostName, final String method) throws UtilityException
    {
        final String methodName = NetworkUtils.CNAME + "#executeHttpConnection(final String hostName, final String method) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", hostName);
            DEBUGGER.debug("Value: {}", method);
        }

        HttpMethod httpMethod = null;

        final HttpClient httpClient = new HttpClient();
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
            if (StringUtils.isNotEmpty(httpConfig.getTrustStoreFile()))
            {
                System.setProperty("javax.net.ssl.trustStoreType",
                        (StringUtils.isNotEmpty(httpConfig.getTrustStoreType()) ? httpConfig.getTrustStoreType() : "jks"));
                System.setProperty("javax.net.ssl.trustStore", httpConfig.getTrustStoreFile());
                System.setProperty("javax.net.ssl.trustStorePassword",
                        PasswordUtils.decryptText(httpConfig.getTrustStorePass(), httpConfig.getTrustStoreSalt().length(),
                                appBean.getConfigData().getAppConfig().getAlgorithm(), appBean.getConfigData().getAppConfig().getInstance(),
                                appBean.getConfigData().getAppConfig().getEncoding()));
            }

            if (StringUtils.isNotEmpty(httpConfig.getKeyStoreFile()))
            {
                System.setProperty("javax.net.ssl.keyStoreType",
                        (StringUtils.isNotEmpty(httpConfig.getKeyStoreType()) ? httpConfig.getKeyStoreType() : "jks"));
                System.setProperty("javax.net.ssl.keyStore", httpConfig.getKeyStoreFile());
                System.setProperty("javax.net.ssl.keyStorePassword",
                        PasswordUtils.decryptText(httpConfig.getKeyStorePass(), httpConfig.getKeyStoreSalt().length(),
                                appBean.getConfigData().getAppConfig().getAlgorithm(), appBean.getConfigData().getAppConfig().getInstance(),
                                appBean.getConfigData().getAppConfig().getEncoding()));
            }

            httpParams.setSoTimeout(httpConfig.getSoTimeout());
            httpParams.setVersion(HttpVersion.parse(httpConfig.getHttpVersion()));
            httpParams.setParameter("http.socket.linger", (int) TimeUnit.SECONDS.toMillis(httpConfig.getSocketLinger()));
            httpParams.setParameter("http.socket.timeout", (int) TimeUnit.SECONDS.toMillis(httpConfig.getSocketTimeout()));
            httpParams.setParameter("http.connection.timeout", (int) TimeUnit.SECONDS.toMillis(httpConfig.getConnTimeout()));
            httpParams.setParameter("http.connection-manager.timeout", TimeUnit.SECONDS.toMillis(httpConfig.getConnMgrTimeout()));
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

                        String proxyPwd = PasswordUtils.decryptText(proxyConfig.getProxyPassword(), proxyConfig.getProxyPwdSalt().length(),
                                appBean.getConfigData().getAppConfig().getAlgorithm(),
                                appBean.getConfigData().getAppConfig().getInstance(),
                                appBean.getConfigData().getAppConfig().getEncoding());

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
                if (InetAddress.getByName(hostName) != null)
                {
                    InetSocketAddress socketAddress = new InetSocketAddress(hostName, portNumber);

                    socket = new Socket();
                    socket.setSoTimeout((int) TimeUnit.SECONDS.toMillis(timeout));
                    socket.setSoLinger(false, 0);
                    socket.setKeepAlive(false);
                    socket.connect(socketAddress, (int) TimeUnit.SECONDS.toMillis(timeout));

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
