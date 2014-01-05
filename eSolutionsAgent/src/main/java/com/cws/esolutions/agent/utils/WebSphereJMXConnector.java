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
 * File: WebSphereJMXConnector.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Properties;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.exception.ConnectorException;

/**
 * @see com.cws.esolutions.agent.jmx.interfaces.JMXConnection
 */
public static final class WebSphereJMXConnector
{
    private static final String CNAME = WebSphereJMXConnector.class.getName();

    @Override
    public static final AdminClient getJMXConnector(final String mbeanName) throws ConnectorException
    {
        final String methodName = WebSphereJMXConnector.CNAME + "#getJMXConnector(final String mbeanName) throws ConnectorException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        AdminClient adminClient = null;

        try
        {
            Properties props = new Properties();
            props.setProperty(AdminClient.CACHE_DISABLED, "false");
            props.setProperty(AdminClient.CONNECTOR_HOST, jmxConfig.getServerName());
            props.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
            //props.setProperty(AdminClient.CONNECTOR_SOAP_CONFIG, jmxConfig.getUserConfig());
            props.setProperty(AdminClient.USERNAME, "wasadm");
            props.setProperty(AdminClient.CONNECTOR_PORT, String.valueOf(jmxConfig.getNmPort()));

            if (DEBUG)
            {
                DEBUGGER.debug("Properties: {}", props);
            }

            if (!(jmxConfig.getIsSecure()))
            {
                adminClient = AdminClientFactory.createAdminClient(props);

                if (DEBUG)
                {
                    DEBUGGER.debug("AdminClient: {}", adminClient);
                }
            }
            else
            {
                props.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "true");
                props.setProperty(AdminClient.CONNECTOR_AUTO_ACCEPT_SIGNER, "true");
                props.setProperty("javax.net.ssl.keyStore", jmxConfig.getSslKeystore());
                props.setProperty("javax.net.ssl.keyStorePassword", jmxConfig.getKeystorePass());
                props.setProperty("javax.net.ssl.trustStore", jmxConfig.getSslTruststore());
                props.setProperty("javax.net.ssl.trustStorePassword", jmxConfig.getTruststorePass());

                if (DEBUG)
                {
                    DEBUGGER.debug("Properties: {}", props);
                }

                adminClient = AdminClientFactory.createAdminClient(props);

                if (DEBUG)
                {
                    DEBUGGER.debug("AdminClient: {}", adminClient);
                }
            }
        }
        catch (ConnectorException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new ConnectorException(cx.getMessage(), cx);
        }

        return adminClient;
    }
}
