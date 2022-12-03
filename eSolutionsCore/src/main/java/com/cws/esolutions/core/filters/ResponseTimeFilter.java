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
package com.cws.esolutions.core.filters;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.filters
 * File: ResponseTimeFilter.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.io.IOException;
import javax.naming.Context;
import javax.servlet.Filter;
import org.slf4j.LoggerFactory;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import org.apache.commons.lang.StringUtils;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;

import com.cws.esolutions.core.CoreServicesConstants;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see javax.servlet.Filter
 */
@WebFilter(filterName = "ResponseTimeFilter", urlPatterns = {"/*"}, initParams = @WebInitParam(name = "env", value = "dev"))
public class ResponseTimeFilter implements Filter
{
	private String environment = null;

    private static final String CNAME = ResponseTimeFilter.class.getName();
    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServicesConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServicesConstants.ERROR_LOGGER);

    public void init(final FilterConfig config) throws ServletException
    {
    	final String methodName = ResponseTimeFilter.CNAME + "#init(final FilterConfig config) throws ServletException";

    	if (DEBUG)
    	{
    		DEBUGGER.debug("Value: {}", methodName);
    		DEBUGGER.debug("Value: {}", config);
    	}

        try
        {
        	Context initContext = new InitialContext();
        	Context envContext = (Context) initContext.lookup(SecurityServiceConstants.DS_CONTEXT);
        	environment = (String) envContext.lookup("env");
        }
        catch (NamingException nx)
        {
        	ERROR_RECORDER.error(nx.getMessage(), nx);
        }
    }

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException
    {
    	final String methodName = ResponseTimeFilter.CNAME + "#doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException";

    	if (DEBUG)
    	{
    		DEBUGGER.debug("Value: {}", methodName);
    		DEBUGGER.debug("Value: {}", request);
    		DEBUGGER.debug("Value: {}", response);
    		DEBUGGER.debug("Value: {}", chain);
    	}

    	if ((StringUtils.equalsIgnoreCase(environment, String.valueOf(ServiceRegion.DEV)) || (StringUtils.equalsIgnoreCase(environment, String.valueOf(ServiceRegion.INT)))
    			|| (StringUtils.equalsIgnoreCase(environment, String.valueOf(ServiceRegion.SIT))) || (StringUtils.equalsIgnoreCase(environment, String.valueOf(ServiceRegion.QA)))
    					|| (StringUtils.equalsIgnoreCase(environment, String.valueOf(ServiceRegion.STG)))))
        {
            long time = System.currentTimeMillis();

            if (DEBUG)
            {
            	DEBUGGER.debug("Value: {}", time);
            }

            time = System.currentTimeMillis() - time;
            String url = request instanceof HttpServletRequest ? ((HttpServletRequest) request).getRequestURL().toString() : "N/A";

            if (DEBUG)
            {
            	DEBUGGER.debug("Value: {}", time);
            	DEBUGGER.debug("Value: {}", url);
            }

            System.out.println("Time taken for request to complete:  " + time + "ms");
            System.out.println("Request url : " + url);

            chain.doFilter(request, response);
        }
        else
        {
            chain.doFilter(request, response);
        }
    }

    public void destroy()
    {
    }
}