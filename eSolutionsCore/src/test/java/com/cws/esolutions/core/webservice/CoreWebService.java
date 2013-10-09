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
package com.cws.esolutions.core.webservice;

import org.slf4j.Logger;
import javax.xml.ws.Endpoint;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.exception.CoreServiceException;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.webservice.impl.WebServiceRequestProcessorImpl;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.agent.listener
 * AgentWSListener.java
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
 * kh05451 @ Nov 12, 2012 12:32:24 PM
 *     Created.
 */
public class CoreWebService
{
    private String serviceURL = null;

    private static final String CNAME = CoreWebService.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public CoreWebService()
    {
        try
        {
            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "logging/logging.xml");
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/config/SecurityLogging.xml");

            serviceURL = "http://127.0.0.1:8080/eSolutions/eSolutionsService";

            if (DEBUG)
            {
                DEBUGGER.debug(serviceURL);
            }
        }
        catch (SecurityServiceException ssx)
        {
            ERROR_RECORDER.error(ssx.getMessage(), ssx);
        }
        catch (CoreServiceException csx)
        {
            ERROR_RECORDER.error(csx.getMessage(), csx);
        }
    }

    public void run()
    {
        final String methodName = CoreWebService.CNAME + "#run()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        int exitCode = -1;

        try
        {
            Endpoint endPoint = Endpoint.publish(serviceURL, new WebServiceRequestProcessorImpl());

            if (DEBUG)
            {
                DEBUGGER.debug("Endpoint: ", endPoint);
            }

            System.out.println("Web Service Published using JAX-WS EndPoint Server...");
            System.out.println("WS-URL: " + serviceURL);

            while (endPoint.isPublished())
            {
                Thread.sleep(1000);
            }

            exitCode = 0;
        }
        catch (InterruptedException ix)
        {
            ERROR_RECORDER.error(ix.getMessage(), ix);

            exitCode = 1;
        }

        System.exit(exitCode);
    }

    /**
     * @param args
     */
    public static void main(final String[] args)
    {
        CoreWebService wsListener = new CoreWebService();
        wsListener.run();
    }
}