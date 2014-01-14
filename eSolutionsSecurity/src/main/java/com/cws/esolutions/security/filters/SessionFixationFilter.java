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
package com.cws.esolutions.security.filters;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.filters
 * File: SessionFixationFilter.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import org.slf4j.Logger;
import java.util.HashMap;
import java.io.IOException;
import javax.servlet.Filter;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * @see javax.servlet.Filter
 */
public class SessionFixationFilter implements Filter
{
    private static final String CNAME = SessionFixationFilter.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    @Override
    public void init(final FilterConfig filterConfig)
    {
        final String methodName = SessionFixationFilter.CNAME + "#init(final FilterConfig filterConfig) throws ServletException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("FilterConfig: {}", filterConfig);
        }
    }

    @Override
    public void doFilter(final ServletRequest sRequest, final ServletResponse sResponse, final FilterChain filterChain) throws IOException, ServletException
    {
        final String methodName = SessionFixationFilter.CNAME + "#doFilter(final ServletRequest sRequest, final ServletResponse sResponse, final FilterChain filterChain) throws IOException, ServletException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServletRequest: {}", sRequest);
            DEBUGGER.debug("ServletResponse: {}", sResponse);
        }

        Map<String, Object> currentSession = null;

        final HttpServletRequest hRequest = (HttpServletRequest) sRequest;
        final HttpServletResponse hResponse = (HttpServletResponse) sResponse;
        final HttpSession hSession = hRequest.getSession();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpServletResponse: {}", hResponse);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("HttpSession.getId(): {}", hSession.getId());

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        Enumeration<String> sessionAttributes = hSession.getAttributeNames();

        if (DEBUG)
        {
            DEBUGGER.debug("Enumeration<String>: {}", sessionAttributes);
        }

        if (sessionAttributes.hasMoreElements())
        {
            while (sessionAttributes.hasMoreElements())
            {
                String element = sessionAttributes.nextElement();

                if (DEBUG)
                {
                    DEBUGGER.debug("element: {}", element);
                }

                Object value = hSession.getAttribute(element);

                if (DEBUG)
                {
                    DEBUGGER.debug("sessionValue: {}", value);
                }

                currentSession = new HashMap<>();
                currentSession.put(element, value);

                if (DEBUG)
                {
                    DEBUGGER.debug("Map<String, Object>: {}", currentSession);
                }
            }
            
            hSession.invalidate();

            HttpSession nSession = hRequest.getSession(true);

            if (DEBUG)
            {
                DEBUGGER.debug("HttpSession: {}", nSession);
                DEBUGGER.debug("HttpSession.getId(): {}", nSession.getId());
            }

            if (currentSession != null)
            {
                for (String key : currentSession.keySet())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Key: {}", key);
                    }

                    nSession.setAttribute(key, currentSession.get(key));
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("HttpSession: {}", nSession);

                    DEBUGGER.debug("Dumping session content:");
                    Enumeration<String> sessionEnumeration = nSession.getAttributeNames();

                    while (sessionEnumeration.hasMoreElements())
                    {
                        String newSesionElement = sessionEnumeration.nextElement();
                        Object newSessionValue = nSession.getAttribute(newSesionElement);
    
                        DEBUGGER.debug("Attribute: " + newSesionElement + "; Value: " + newSessionValue);
                    }
                }
            }
        }

        filterChain.doFilter(sRequest, sResponse);

        return;
    }

    @Override
    public void destroy()
    {
        final String methodName = SessionFixationFilter.CNAME + "#destroy()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }
    }
}
