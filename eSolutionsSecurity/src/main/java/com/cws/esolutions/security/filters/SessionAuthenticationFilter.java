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
package com.cws.esolutions.security.filters;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.filters
 * File: SessionAuthenticationFilter.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly           11/23/2008 22:39:20             Created.
 */
import java.util.Arrays;
import java.io.IOException;
import java.util.Enumeration;
import jakarta.servlet.Filter;
import java.util.ResourceBundle;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletRequest;
import org.apache.logging.log4j.Logger;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import java.util.MissingResourceException;
import org.apache.logging.log4j.LogManager;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebFilter;
import org.apache.commons.lang3.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpServletResponse;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * @see jakarta.servlet.Filter
 */
@WebFilter(filterName = "SessionAuthenticationFilter", urlPatterns = {"/*"}, initParams = @WebInitParam(name = "filter-config", value = "SecurityService/filters/SessionAuthenticationFilter"))
public class SessionAuthenticationFilter implements Filter
{
    private String loginURI = null;
    private String passwordURI = null;
    private String[] ignoreURIs = null;

    private static final String LOGIN_URI = "login.uri";
    private static final String USER_ACCOUNT = "userAccount";
    private static final String IGNORE_URI_LIST = "ignore.uri.list";
    private static final String PASSWORD_URI = "password.change.uri";
    private static final String FILTER_CONFIG_PARAM_NAME = "filter-config";
    private static final String FILTER_CONFIG_FILE_NAME = "config/FilterConfig";
    private static final String CNAME = SessionAuthenticationFilter.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LogManager.getLogger(SecurityServiceConstants.ERROR_LOGGER + CNAME);

    public void init(final FilterConfig filterConfig) throws ServletException
    {
        final String methodName = SessionAuthenticationFilter.CNAME + "#init(final FilterConfig filterConfig) throws ServletException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("FilterConfig: {}", filterConfig);
        }

        ResourceBundle rBundle = null;

        try
        {
            if (filterConfig.getInitParameter(SessionAuthenticationFilter.FILTER_CONFIG_PARAM_NAME) == null)
            {
                ERROR_RECORDER.error("Filter configuration not found. Using default !");

                rBundle = ResourceBundle.getBundle(SessionAuthenticationFilter.FILTER_CONFIG_FILE_NAME);
            }
            else
            {
                rBundle = ResourceBundle.getBundle(filterConfig.getInitParameter(SessionAuthenticationFilter.FILTER_CONFIG_PARAM_NAME));
            }

            this.loginURI = rBundle.getString(SessionAuthenticationFilter.LOGIN_URI);
            this.passwordURI = rBundle.getString(SessionAuthenticationFilter.PASSWORD_URI);
            this.ignoreURIs = (StringUtils.isNotEmpty(rBundle.getString(SessionAuthenticationFilter.IGNORE_URI_LIST)))
                    ? rBundle.getString(SessionAuthenticationFilter.IGNORE_URI_LIST).trim().split(",") : new String[] { "ALL" };

            if (DEBUG)
            {
                if (this.ignoreURIs != null)
                {
                    for (String str : this.ignoreURIs)
                    {
                        DEBUGGER.debug(str);
                    }
                }
            }
        }
        catch (final MissingResourceException mre)
        {
            ERROR_RECORDER.error(mre.getMessage(), mre);

            throw new UnavailableException(mre.getMessage());
        }
    }

    public void doFilter(final ServletRequest sRequest, final ServletResponse sResponse, final FilterChain filterChain) throws IOException, ServletException
    {
        final String methodName = SessionAuthenticationFilter.CNAME + "#doFilter(final ServletRequest sRequest, final ServletResponse sResponse, final FilterChain filterChain) throws IOException, ServletException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServletRequest: {}", sRequest);
            DEBUGGER.debug("ServletResponse: {}", sResponse);
        }

        final String passwdPage = this.passwordURI;
        final HttpServletRequest hRequest = (HttpServletRequest) sRequest;
        final HttpServletResponse hResponse = (HttpServletResponse) sResponse;
        final HttpSession hSession = hRequest.getSession(false);
        final String requestURI = hRequest.getRequestURI();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpServletResponse: {}", hResponse);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("RequestURI: {}", requestURI);
            DEBUGGER.debug("passwdPage: {}", passwdPage);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<?> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = (String) sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (StringUtils.equals(this.loginURI, requestURI))
        {
            if (DEBUG)
            {
                DEBUGGER.debug("Request is for the login URI. Breaking!");
            }

            filterChain.doFilter(sRequest, sResponse);

            return;
        }

        if ((this.ignoreURIs != null) && (this.ignoreURIs.length != 0))
        {
            if (Arrays.asList(this.ignoreURIs).contains("ALL"))
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("ALL URIs are ignored. Breaking ...");
                }

                filterChain.doFilter(sRequest, sResponse);

                return;
            }

            // hostname isnt in ignore list
            for (String uri : this.ignoreURIs)
            {
                uri = uri.trim();

                if (DEBUG)
                {
                    DEBUGGER.debug(uri);
                    DEBUGGER.debug(requestURI);
                }

                if (StringUtils.contains(requestURI, uri))
                {
                    // ignore
                    if (DEBUG)
                    {
                        DEBUGGER.debug("URI matched to ignore list - breaking out");
                    }

                    filterChain.doFilter(sRequest, sResponse);

                    return;
                }
            }
        }

        if (hRequest.isRequestedSessionIdFromURL())
        {
            ERROR_RECORDER.error("Session found is from URL. Redirecting request to " + this.loginURI);

            // invalidate the session
            hRequest.getSession(false).invalidate();
            hSession.removeAttribute(SessionAuthenticationFilter.USER_ACCOUNT);
            hSession.invalidate();

            hResponse.sendRedirect(this.loginURI);

            return;
        }

        Enumeration<?> sessionAttributes = hSession.getAttributeNames();

        if (DEBUG)
        {
            DEBUGGER.debug("Enumeration<String>: {}", sessionAttributes);
        }

        while (sessionAttributes.hasMoreElements())
        {
            String element = (String) sessionAttributes.nextElement();

            if (DEBUG)
            {
                DEBUGGER.debug("element: {}", element);
            }

            Object value = hSession.getAttribute(element);

            if (DEBUG)
            {
                DEBUGGER.debug("sessionValue: {}", value);
            }

            if (value instanceof UserAccount)
            {
                UserAccount userAccount = (UserAccount) value;

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", userAccount);
                }

                if (userAccount.getStatus() != null)
                {
                    switch (userAccount.getStatus())
                    {
                        case EXPIRED:
                            if ((!(StringUtils.equals(requestURI, passwdPage))))
                            {
                                ERROR_RECORDER.error("Account is expired and this request is not for the password page. Redirecting !");

                                hResponse.sendRedirect(this.passwordURI);

                                return;
                            }

                            filterChain.doFilter(sRequest, sResponse);

                            return;
                        case RESET:
                            if ((!(StringUtils.equals(requestURI, passwdPage))))
                            {
                                ERROR_RECORDER.error("Account has status RESET and this request is not for the password page. Redirecting !");

                                hResponse.sendRedirect(this.passwordURI);

                                return;
                            }

                            filterChain.doFilter(sRequest, sResponse);

                            return;
                        case SUCCESS:
                            filterChain.doFilter(sRequest, sResponse);

                            return;
                        default:
                            break;
                    }
                }
            }
        }

        // no user account in the session
        ERROR_RECORDER.error("Session contains no existing user account. Redirecting request to " + this.loginURI);

        // invalidate the session
        hSession.removeAttribute(SessionAuthenticationFilter.USER_ACCOUNT);
        hSession.invalidate();

        hResponse.sendRedirect(this.loginURI);

        return;
    }

    public void destroy()
    {
        final String methodName = SessionAuthenticationFilter.CNAME + "#destroy()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.ignoreURIs = null;
    }
}
