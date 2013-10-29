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
package com.cws.esolutions.agent.jmx.impl;

import java.io.IOException;
import java.util.Hashtable;
import javax.naming.Context;
import java.net.MalformedURLException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import weblogic.security.UsernameAndPassword;
import weblogic.deploy.api.tools.SessionHelper;
import weblogic.security.UserConfigFileManager;
import javax.management.remote.JMXConnectorFactory;
import weblogic.deploy.api.spi.WebLogicDeploymentManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;

import com.cws.esolutions.agent.jmx.dto.JMXConnectorObject;
import com.cws.esolutions.agent.jmx.interfaces.JMXConnection;
import com.cws.esolutions.agent.jmx.exception.JMXConnectorException;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.jmx.impl
 * WebLogicJMXConnector.java
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
 * kh05451 @ Oct 26, 2012 17:22:16
 *     Created.
 */
public class WebLogicJMXConnector implements JMXConnection
{
    private static final String PROTOCOL_PACKAGES = "weblogic.management.remote";

    public JMXConnectorObject getJMXConnector(final String mbeanName) throws JMXConnectorException
    {
        final String methodName = JMXConnection.CNAME + "#getJMXConnector(final String mbeanName) throws JMXConnectorException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        JMXConnector jmxConnector = null;
        JMXServiceURL jmxServiceURL = null;
        UsernameAndPassword authInfo = null;
        JMXConnectorObject jmxObject = new JMXConnectorObject();
        Hashtable<String, Object> jmxTable = new Hashtable<String, Object>();

        try
        {
            if (!(jmxConfig.getIsSecure()))
            {
                authInfo = new UsernameAndPassword();

                authInfo = UserConfigFileManager.getUsernameAndPassword(jmxConfig.getUserConfig(),
                        jmxConfig.getKeyConfig(), "weblogic.management");

                if (DEBUG)
                {
                    DEBUGGER.debug("authInfo: {}", authInfo);
                }

                jmxTable.put(Context.SECURITY_PRINCIPAL, new String(authInfo.getUsername()));
                jmxTable.put(Context.SECURITY_CREDENTIALS, new String(authInfo.getPassword()));
            }

            jmxTable.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, PROTOCOL_PACKAGES);

            if (DEBUG)
            {
                DEBUGGER.debug("jmxTable: {}", jmxTable);
            }
            
            jmxServiceURL = new JMXServiceURL(jmxConfig.getProtocol(), jmxConfig.getServerName(),
                    jmxConfig.getNmPort(), JNDI_ROOT + mbeanName);

            if (DEBUG)
            {
                DEBUGGER.debug("jmxServiceURL: {}", jmxServiceURL);
            }

            jmxConnector = JMXConnectorFactory.newConnector(jmxServiceURL, jmxTable);

            if (DEBUG)
            {
                DEBUGGER.debug("jmxConnector: {}", jmxConnector);
            }

            if (jmxConnector == null)
            {
                throw new MalformedURLException("Unable to create a JMXConnector with the provided information");
            }

            jmxObject.setConnector(jmxConnector);

            if (DEBUG)
            {
                DEBUGGER.debug("JMXConnectorObject: {}", jmxObject);
            }
        }
        catch (MalformedURLException mx)
        {
            ERROR_RECORDER.error(mx.getMessage(), mx);

            throw new JMXConnectorException(mx.getMessage(), mx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new JMXConnectorException(iox.getMessage(), iox);
        }

        return jmxObject;
    }

    public JMXConnectorObject getDeploymentConnector() throws JMXConnectorException
    {
        final String methodName = JMXConnection.CNAME + "#getJMXConnector() throws JMXConnectorException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        UsernameAndPassword authInfo = null;
        WebLogicDeploymentManager deployManager = null;
        JMXConnectorObject jmxObject = new JMXConnectorObject();

        try
        {
            authInfo = UserConfigFileManager.getUsernameAndPassword(jmxConfig.getUserConfig(),
                    jmxConfig.getKeyConfig(), "weblogic.management");

            if (DEBUG)
            {
                DEBUGGER.debug("authInfo: {}", authInfo);
            }

            deployManager = SessionHelper.getRemoteDeploymentManager(jmxConfig.getProtocol(),
                    jmxConfig.getServerName(),
                    String.valueOf(jmxConfig.getNmPort()),
                    new String(authInfo.getUsername()), new String(authInfo.getPassword()));

            if (DEBUG)
            {
                DEBUGGER.debug("deployManager: {}", deployManager);
            }

            if ((!(deployManager.isConnected())) && (!(deployManager.isAuthenticated())))
            {
                throw new JMXConnectorException("Failed to connect and authenticate to the requested deployment manager");
            }

            jmxObject.setConnector(deployManager);
        }
        catch (DeploymentManagerCreationException dmcx)
        {
            ERROR_RECORDER.error(dmcx.getMessage(), dmcx);

            throw new JMXConnectorException(dmcx.getMessage(), dmcx);
        }

        if (DEBUG)
        {
            DEBUGGER.debug("jmxObject: {}", jmxObject);
        }
        return jmxObject;
    }
}
