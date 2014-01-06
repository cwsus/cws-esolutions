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
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.filters
 * File: RequestAuthorizationFilter.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import org.slf4j.Logger;
import java.io.IOException;
import javax.servlet.Filter;
import java.sql.SQLException;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import java.util.ResourceBundle;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.util.MissingResourceException;
import javax.servlet.UnavailableException;
import org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.services.enums.AdminControlType;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
import com.cws.esolutions.security.dao.reference.impl.SecurityReferenceDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
/**
 * @see javax.servlet.Filter
 */
public class RequestAuthorizationFilter implements Filter
{
    private String[] adminList = null;
    private String[] ignoreURIs = null;
    private String unauthorizedPage = null;
    private Map<String, String> serviceMap = null;

    private static final String ADMIN_URIS = "admin.uris";
    private static final String IGNORE_URI_LIST = "ignore.uri.list";
    private static final String UNAUTHORIZED_PAGE = "unauthorized.uri";
    private static final String FILTER_CONFIG_PARAM_NAME = "filter-config";
    private static final String FILTER_CONFIG_FILE_NAME = "config/FilterConfig";
    private static final String CNAME = RequestAuthorizationFilter.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER + CNAME);

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException
    {
        final String methodName = RequestAuthorizationFilter.CNAME + "#init(final FilterConfig filterConfig) throws ServletException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("FilterConfig: {}", filterConfig);
        }

        ResourceBundle rBundle = null;

        try
        {
            if (filterConfig.getInitParameter(RequestAuthorizationFilter.FILTER_CONFIG_PARAM_NAME) == null)
            {
                ERROR_RECORDER.error("Filter configuration not found. Using default !");

                rBundle = ResourceBundle.getBundle(RequestAuthorizationFilter.FILTER_CONFIG_FILE_NAME);
            }
            else
            {
                rBundle = ResourceBundle.getBundle(filterConfig.getInitParameter(RequestAuthorizationFilter.FILTER_CONFIG_PARAM_NAME));
            }

            ISecurityReferenceDAO dao = new SecurityReferenceDAOImpl();
            this.serviceMap = dao.listAvailableServices();
            this.unauthorizedPage = rBundle.getString(RequestAuthorizationFilter.UNAUTHORIZED_PAGE);
            this.adminList = (StringUtils.isNotEmpty(rBundle.getString(RequestAuthorizationFilter.ADMIN_URIS))) ?
                    rBundle.getString(RequestAuthorizationFilter.ADMIN_URIS).trim().split(",") : null;
            this.ignoreURIs = (StringUtils.isNotEmpty(rBundle.getString(RequestAuthorizationFilter.IGNORE_URI_LIST))) ?
                    rBundle.getString(RequestAuthorizationFilter.IGNORE_URI_LIST).trim().split(",") : null;

            if (DEBUG)
            {
                DEBUGGER.debug("Map<String, String>: {}", this.serviceMap);
                DEBUGGER.debug("unauthorizedPage: {}", this.unauthorizedPage);
            }

        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new UnavailableException(sqx.getMessage());
        }
        catch (MissingResourceException mrx)
        {
            ERROR_RECORDER.error(mrx.getMessage(), mrx);

            throw new UnavailableException(mrx.getMessage());
        }
    }

    @Override
    public void doFilter(final ServletRequest sRequest, final ServletResponse sResponse, final FilterChain filterChain) throws IOException, ServletException
    {
        final String methodName = RequestAuthorizationFilter.CNAME + "#doFilter(final ServletRequest sRequest, final ServletResponse sResponse, final FilterChain filterChain) throws IOException, ServletException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServletRequest: {}", sRequest);
            DEBUGGER.debug("ServletResponse: {}", sResponse);
        }

        final HttpServletRequest hRequest = (HttpServletRequest) sRequest;
        final HttpServletResponse hResponse = (HttpServletResponse) sResponse;
        final HttpSession hSession = hRequest.getSession(false);
        final String requestURI = hRequest.getRequestURI();
        final String unauthorizedRedirect = hRequest.getContextPath() + this.unauthorizedPage;

        if (DEBUG)
        {
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpServletResponse: {}", hResponse);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("RequestURI: {}", requestURI);
            DEBUGGER.debug("unauthorizedRedirect: {}", unauthorizedRedirect);

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

        if (StringUtils.equals(unauthorizedRedirect, requestURI))
        {
            if (DEBUG)
            {
                DEBUGGER.debug("Request authenticated. No action taken !");
            }

            filterChain.doFilter(sRequest, sResponse);

            return;
        }

        if ((this.ignoreURIs != null) && (this.ignoreURIs.length != 0))
        {
            // hostname isnt in ignore list
            for (String uri : this.ignoreURIs)
            {
                uri = hRequest.getContextPath().trim() + uri.trim();

                if (DEBUG)
                {
                    DEBUGGER.debug(uri);
                    DEBUGGER.debug(requestURI);
                }

                if (StringUtils.startsWith(requestURI, uri))
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

        Enumeration<String> sessionAttributes = hSession.getAttributeNames();

        if (DEBUG)
        {
            DEBUGGER.debug("Enumeration<String>: {}", sessionAttributes);
        }

        if (!(sessionAttributes.hasMoreElements()))
        {
            if (DEBUG)
            {
                DEBUGGER.debug("No session attributes currently exist. Continuing");
            }

            filterChain.doFilter(sRequest, sResponse);

            return;
        }

        while (sessionAttributes.hasMoreElements())
        {
            String element = sessionAttributes.nextElement();

            if (DEBUG)
            {
                DEBUGGER.debug("sessionElement: {}", element);
            }

            Object value = hSession.getAttribute(element);

            if (DEBUG)
            {
                DEBUGGER.debug("Object: {}", value);
            }

            if (value instanceof UserAccount)
            {
                UserAccount userAccount = (UserAccount) value;

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", userAccount);
                }

                if (userAccount.getRole() != Role.SITEADMIN)
                {
                    try
                    {
                        for (String adminURI : this.adminList)
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("String: {}", adminURI);
                            }

                            if (StringUtils.startsWith(requestURI, adminURI))
                            {
                                IAccessControlService accessControl = new AccessControlServiceImpl();
                                boolean isAdminAuthorized = accessControl.accessControlService(userAccount, AdminControlType.SERVICE_ADMIN);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                                }

                                if (!(isAdminAuthorized))
                                {
                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("User is not authorized to access the requested resource. Redirecting !");
                                    }

                                    hResponse.sendRedirect(unauthorizedRedirect);

                                    return;
                                }
                            }
                        }

                        for (String key : this.serviceMap.keySet())
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("String: {}", key);
                            }

                            if (StringUtils.startsWith(requestURI, key))
                            {
                                // make sure the user is authorized for the service
                                IAccessControlService accessControl = new AccessControlServiceImpl();
                                boolean isUserAuthorized = accessControl.isUserAuthorizedForService(userAccount, this.serviceMap.get(key));

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                                }

                                if (!(isUserAuthorized))
                                {
                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("User is not authorized to access the requested resource. Redirecting !");
                                    }

                                    hResponse.sendRedirect(unauthorizedRedirect);
    
                                    return;
                                }
                            }
                        }
                    }
                    catch (AccessControlServiceException acsx)
                    {
                        ERROR_RECORDER.error(acsx.getMessage(), acsx);

                        hResponse.sendRedirect(unauthorizedRedirect);
                    
                        return;
                    }
                }

                filterChain.doFilter(sRequest, sResponse);

                return;
            }
        }
    }

    @Override
    public void destroy()
    {
        final String methodName = RequestAuthorizationFilter.CNAME + "#destroy()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.adminList = null;
        this.unauthorizedPage = null;
        this.serviceMap.clear();
        this.serviceMap = null;
    }
}
