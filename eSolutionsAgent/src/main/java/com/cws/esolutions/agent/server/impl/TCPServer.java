/*
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
package com.cws.esolutions.agent.server.impl;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.server.impl
 * File: TCPServer.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.io.FileInputStream;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.KeyManagerFactory;
import java.security.KeyStoreException;
import org.apache.commons.lang.StringUtils;
import javax.net.ssl.SSLServerSocketFactory;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import com.cws.esolutions.agent.AgentBean;
import com.cws.esolutions.agent.dto.AgentRequest;
import com.cws.esolutions.agent.dto.AgentResponse;
import com.cws.esolutions.agent.utils.PasswordUtils;
import com.cws.esolutions.agent.config.xml.ServerConfig;
import com.cws.esolutions.agent.exception.AgentException;
import com.cws.esolutions.agent.server.interfaces.AgentServer;
import com.cws.esolutions.agent.server.processors.impl.AgentRequestProcessorImpl;
import com.cws.esolutions.agent.server.processors.interfaces.IAgentRequestProcessor;
/**
 * @see com.cws.esolutions.agent.server.interfaces.AgentServer
 */
public class TCPServer extends Thread implements AgentServer
{
    private boolean isSSLEnabled = false;
    private boolean isTCPDisabled = false;
    private ServerSocket tcpSocket = null;
    private SSLServerSocket sslSocket = null;

    private static final String TRANSPORT_TYPE_TCP = "TCP";
    private static final AgentBean agentBean = AgentBean.getInstance();

    public TCPServer() throws AgentException
    {
        ServerConfig serverConfig = agentBean.getConfigData().getServerConfig();

        if (DEBUG)
        {
            DEBUGGER.debug("ServerConfig: {}", serverConfig);
        }

        try
        {
            if (StringUtils.equals(serverConfig.getTransportType().toUpperCase(), TRANSPORT_TYPE_TCP))
            {
                synchronized (new Object())
                {
                    if (this.isSSLEnabled)
                    {
                        String protocol = serverConfig.getSslProtocol();
                        String keyStoreFile = serverConfig.getSslKeyDatabase();
                        String keyStorePass = (StringUtils.isNotEmpty(serverConfig.getSslKeySalt())) ? 
                                PasswordUtils.decryptText(serverConfig.getSslKeyPassword() + serverConfig.getSslKeySalt(),
                                        serverConfig.getSslKeySalt().length()) :
                                            serverConfig.getSslKeyPassword();

                        // TODO: build an ssl listener here
                        SSLContext sslContext = SSLContext.getInstance(protocol);
                        KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                        keyStore.load(new FileInputStream(keyStoreFile), keyStorePass.toCharArray());
                        keyFactory.init(keyStore, keyStorePass.toCharArray());
                        KeyManager[] keyManager = keyFactory.getKeyManagers();
                        sslContext.init(keyManager, null, new SecureRandom());

                        SSLServerSocketFactory sslFactory = sslContext.getServerSocketFactory();
                        this.sslSocket = (SSLServerSocket) sslFactory.createServerSocket();
                        this.sslSocket.bind(new InetSocketAddress(serverConfig.getListenAddress(), serverConfig.getPortNumber()), serverConfig.getBacklogCount());

                        if (!(this.sslSocket.isBound()))
                        {
                            throw new IOException("Unable to bind against configured address/port. Please verify configuration and re-try.");
                        }

                        INFO_RECORDER.info("Server ready.");
                    }

                    if (!(this.isTCPDisabled))
                    {
                        this.tcpSocket = new ServerSocket();
                        this.tcpSocket.bind(new InetSocketAddress(serverConfig.getListenAddress(), serverConfig.getPortNumber()), serverConfig.getBacklogCount());

                        if (!(this.tcpSocket.isBound()))
                        {
                            throw new IOException("Unable to bind against configured address/port. Please verify configuration and re-try.");
                        }

                        INFO_RECORDER.info("Server ready.");
                    }
                }
            }
            else
            {
                throw new IOException("Unsupported transport type " + serverConfig.getTransportType()); 
            }
        }
        catch (IOException iox)
        {
            System.err.println("Failed to start server: " + iox.getMessage());

            throw new AgentException(iox.getMessage(), iox);
        }
        catch (NoSuchAlgorithmException nsx)
        {
            ERROR_RECORDER.error(nsx.getMessage(), nsx);
            
            throw new AgentException(nsx.getMessage(), nsx);
        }
        catch (KeyStoreException ksx)
        {
            ERROR_RECORDER.error(ksx.getMessage(), ksx);
            
            throw new AgentException(ksx.getMessage(), ksx);
        }
        catch (CertificateException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);
            
            throw new AgentException(ax.getMessage(), ax);
        }
        catch (UnrecoverableKeyException ukx)
        {
            ERROR_RECORDER.error(ukx.getMessage(), ukx);
            
            throw new AgentException(ukx.getMessage(), ukx);
        }
        catch (KeyManagementException kmx)
        {
            ERROR_RECORDER.error(kmx.getMessage(), kmx);
            
            throw new AgentException(kmx.getMessage(), kmx);
        }
    }

    @Override
    public void run()
    {
        Socket requestSocket = null;
        AgentRequest agentRequest = null;
        ObjectOutputStream oStream = null;
        AgentResponse agentResponse = null;

        final IAgentRequestProcessor agentProcessor = new AgentRequestProcessorImpl();

        while (true)
        {
            try
            {
                if (agentBean.getStopServer())
                {
                    if (this.isSSLEnabled)
                    {
                        this.sslSocket.close();
                    }

                    if (!(this.isTCPDisabled))
                    {
                        this.tcpSocket.close();
                    }

                    break;
                }

                if (this.isSSLEnabled)
                {
                    requestSocket = this.sslSocket.accept();

                    if (requestSocket.isConnected())
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Host connected: ");
                            DEBUGGER.debug("Remote address: {}", requestSocket.getRemoteSocketAddress());
                        }

                        oStream = new ObjectOutputStream(requestSocket.getOutputStream());
                        agentRequest = (AgentRequest) new ObjectInputStream(requestSocket.getInputStream()).readObject();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("AgentRequest: {}", agentRequest);
                        }

                        if (agentRequest != null)
                        {
                            if (agentRequest.getRequestPayload() == "SHUTDOWN")
                            {
                                break;
                            }

                            agentResponse = agentProcessor.processRequest(agentRequest);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("AgentResponse: {}", agentResponse);
                            }

                            if (agentResponse != null)
                            {
                                oStream.writeObject(agentResponse);
                            }
                            else
                            {
                                throw new AgentException("Failed to process request");
                            }
                        }
                    }
                }

                if (!(this.isTCPDisabled))
                {
                    requestSocket = this.tcpSocket.accept();

                    if (requestSocket.isConnected())
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Host connected: ");
                            DEBUGGER.debug("Remote address: {}", requestSocket.getRemoteSocketAddress());
                        }

                        oStream = new ObjectOutputStream(requestSocket.getOutputStream());
                        agentRequest = (AgentRequest) new ObjectInputStream(requestSocket.getInputStream()).readObject();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("AgentRequest: {}", agentRequest);
                        }

                        if (agentRequest != null)
                        {
                            if (agentRequest.getRequestPayload() == "SHUTDOWN")
                            {
                                break;
                            }

                            agentResponse = agentProcessor.processRequest(agentRequest);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("AgentResponse: {}", agentResponse);
                            }

                            if (agentResponse != null)
                            {
                                oStream.writeObject(agentResponse);
                            }
                            else
                            {
                                throw new AgentException("Failed to process request");
                            }
                        }
                    }
                }
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);

                try
                {
                    oStream.writeObject(iox);
                }
                catch (IOException iox1)
                {
                    // dont do anything
                }
            }
            catch (ClassNotFoundException cnfx)
            {
                ERROR_RECORDER.error(cnfx.getMessage(), cnfx);

                try
                {
                    oStream.writeObject(cnfx);
                }
                catch (IOException iox)
                {
                    // dont do anything
                }
            }
            catch (AgentException ax)
            {
                ERROR_RECORDER.error(ax.getMessage(), ax);

                try
                {
                    oStream.writeObject(ax);
                }
                catch (IOException iox)
                {
                    // dont do anything
                }
            }
            finally
            {
                try
                {
                    if (oStream != null)
                    {
                        oStream.flush();
                        oStream.close();
                    }

                    if (requestSocket.isConnected())
                    {
                        requestSocket.close();
                    }
                }
                catch (IOException iox)
                {
                    ERROR_RECORDER.error(iox.getMessage(), iox);
                }
            }
        }
    }
}
