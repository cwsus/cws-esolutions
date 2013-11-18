/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
 */
package com.cws.esolutions.security.filters;

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

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.access.control.enums.AdminControlType;
import com.cws.esolutions.security.access.control.impl.UserControlServiceImpl;
import com.cws.esolutions.security.access.control.impl.AdminControlServiceImpl;
import com.cws.esolutions.security.dao.reference.impl.SecurityReferenceDAOImpl;
import com.cws.esolutions.security.access.control.interfaces.IUserControlService;
import com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO;
import com.cws.esolutions.security.access.control.interfaces.IAdminControlService;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
import com.cws.esolutions.security.access.control.exception.AdminControlServiceException;
/**
 * eSolutionsCore
 * com.cws.esolutions.security.filters
 * RequestAuthorizationFilter.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author 35033355
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * 35033355 @ Nov 15, 2013 10:29:14 AM
 *     Created.
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

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger WARN_RECORDER = LoggerFactory.getLogger(SecurityConstants.WARN_LOGGER + CNAME);
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + CNAME);

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
                WARN_RECORDER.warn("Filter configuration not found. Using default !");

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
                DEBUGGER.debug("Map<String, String>: {}", serviceMap);
                DEBUGGER.debug("unauthorizedPage: {}", unauthorizedPage);
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
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
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
            String sessionElement = sessionAttributes.nextElement();

            if (DEBUG)
            {
                DEBUGGER.debug("sessionElement: {}", sessionElement);
            }

            Object sessionValue = hSession.getAttribute(sessionElement);

            if (DEBUG)
            {
                DEBUGGER.debug("sessionValue: {}", sessionValue);
            }

            if (sessionValue instanceof UserAccount)
            {
                UserAccount userAccount = (UserAccount) sessionValue;

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", userAccount);
                }

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
                            IAdminControlService adminControl = new AdminControlServiceImpl();
                            boolean isAdminAuthorized = adminControl.adminControlService(userAccount, AdminControlType.SERVICE_ADMIN);

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

                    for (String key : serviceMap.keySet())
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("String: {}", key);
                        }

                        if (StringUtils.startsWith(requestURI, key))
                        {
                            // make sure the user is authorized for the service
                            IUserControlService userControl = new UserControlServiceImpl();
                            boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount, serviceMap.get(key));

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

                    filterChain.doFilter(sRequest, sResponse);

                    return;
                }
                catch (AdminControlServiceException acsx)
                {
                    ERROR_RECORDER.error(acsx.getMessage(), acsx);

                    hResponse.sendRedirect(unauthorizedRedirect);
                    
                    return;
                }
                catch (UserControlServiceException ucsx)
                {
                    ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

                    hResponse.sendRedirect(unauthorizedRedirect);
                    
                    return;
                }
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
