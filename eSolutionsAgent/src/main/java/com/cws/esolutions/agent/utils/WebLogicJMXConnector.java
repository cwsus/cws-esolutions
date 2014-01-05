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
package com.cws.esolutions.agent.utils;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.utils
 * File: WebLogicJMXConnector.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.io.IOException;
import java.util.Hashtable;
import javax.naming.Context;
import org.slf4j.LoggerFactory;
import java.net.MalformedURLException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import weblogic.security.UsernameAndPassword;
import weblogic.deploy.api.tools.SessionHelper;
import weblogic.security.UserConfigFileManager;
import javax.management.remote.JMXConnectorFactory;

import com.cws.esolutions.agent.AgentConstants;
import com.cws.esolutions.agent.config.xml.JMXConfig;
/**
 * @see com.cws.esolutions.agent.jmx.interfaces.JMXConnection
 */
public final class WebLogicJMXConnector
{
    private static final String JNDI_ROOT= "/jndi/";
    private static final String CNAME = WebLogicJMXConnector.class.getName();
    private static final String PROTOCOL_PACKAGES = "weblogic.management.remote";

    private static final Logger DEBUGGER = LoggerFactory.getLogger(AgentConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(AgentConstants.ERROR_LOGGER + CNAME);

    public static final JMXConnector getJMXConnector(final String mbeanName, final JMXConfig jmxConfig) throws IOException, MalformedURLException
    {
        final String methodName = WebLogicJMXConnector.CNAME + "#getJMXConnector(final String mbeanName, final JMXConfig jmxConfig) throws IOException, MalformedURLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        JMXConnector jmxConnector = null;

        try
        {
			Hashtable<String, Object> jmxTable = new Hashtable<String, Object>();
            jmxTable.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, WebLogicJMXConnector.PROTOCOL_PACKAGES);

            if (jmxConfig.getIsSecure())
            {
                UsernameAndPassword authInfo = new UsernameAndPassword();
                authInfo = UserConfigFileManager.getUsernameAndPassword(
                        jmxConfig.getUserConfig(),
					    jmxConfig.getKeyConfig(),
                        "weblogic.management");

                if (DEBUG)
                {
                    DEBUGGER.debug("authInfo: {}", authInfo);
                }

                jmxTable.put(Context.SECURITY_PRINCIPAL, new String(authInfo.getUsername()));
                jmxTable.put(Context.SECURITY_CREDENTIALS, new String(authInfo.getPassword()));
            }

            if (DEBUG)
            {
                DEBUGGER.debug("jmxTable: {}", jmxTable);
            }

            JMXServiceURL jmxServiceURL = new JMXServiceURL(jmxConfig.getProtocol(),
                jmxConfig.getServerName(), jmxConfig.getNmPort(),
                WebLogicJMXConnector.JNDI_ROOT + mbeanName);

            if (DEBUG)
            {
                DEBUGGER.debug("jmxServiceURL: {}", jmxServiceURL);
            }

            jmxConnector = JMXConnectorFactory.newJMXConnector(jmxServiceURL, jmxTable);

            if (DEBUG)
            {
                DEBUGGER.debug("jmxConnector: {}", jmxConnector);
            }

            if (jmxConnector == null)
            {
                throw new MalformedURLException("Unable to create a JMXConnector with the provided information");
            }
        }
        catch (MalformedURLException mx)
        {
            ERROR_RECORDER.error(mx.getMessage(), mx);

            throw new MalformedURLException(mx.getMessage());
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new IOException(iox.getMessage(), iox);
        }

        return jmxConnector;
    }
}
