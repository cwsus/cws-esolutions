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
import java.io.IOException;
import javax.servlet.Filter;
import java.util.ResourceBundle;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebFilter;

import java.util.Arrays;
import java.util.MissingResourceException;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cws.esolutions.core.CoreServicesConstants;
import com.cws.esolutions.core.processors.dto.ApplicationEnablementRequest;
import com.cws.esolutions.core.processors.dto.ApplicationEnablementResponse;
import com.cws.esolutions.core.processors.impl.ApplicationEnablementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ApplicationEnablementException;
import com.cws.esolutions.core.processors.interfaces.IApplicationEnablementProcessor;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see javax.servlet.Filter
 */
@WebFilter(filterName = "ApplicationEnablementFilter", urlPatterns = {"/*"}, initParams = @WebInitParam(name = "filter-config", value = "eSolutionsCore/filters/ApplicationEnablementFilter"))
public class ApplicationEnablementFilter implements Filter
{
	private String appId = null;
	private String appName = null;
	private String[] ignoreURIs = null;
	private String svcFailureRedir = null;
	private String svcNotEnabledRedir = null;
	private String svcUnauthorizedRedir = null;

    private static final String APPID = "application.id";
    private static final String APPNAME = "application.name";
    private static final String IGNORE_URI_LIST = "ignore.uri.list";
    private static final String FILTER_CONFIG_PARAM_NAME = "filter-config";
    private static final String FILTER_CONFIG_FILE_NAME = "config/FilterConfig";
    private static final String SVC_FAILURE_REDIRECT = "request.failure.redirect";
    private static final String SVC_NOT_ENABLED_REDIR = "svc.not.enabled.redirect";
    private static final String SVC_UNAUTHORIZED_REDIR = "svc.unauthorized.redirect";
    private static final String CNAME = ApplicationEnablementFilter.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LogManager.getLogger(CoreServicesConstants.ERROR_LOGGER);

    public void init(final FilterConfig filterConfig) throws ServletException
    {
        final String methodName = ApplicationEnablementFilter.CNAME + "#init(final FilterConfig filterConfig) throws ServletException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("FilterConfig: {}", filterConfig);
        }

        ResourceBundle rBundle = null;

        try
        {
            if (StringUtils.isBlank(filterConfig.getInitParameter(ApplicationEnablementFilter.FILTER_CONFIG_PARAM_NAME)))
            {
                ERROR_RECORDER.error("Filter configuration not found. Using default !");

                rBundle = ResourceBundle.getBundle(ApplicationEnablementFilter.FILTER_CONFIG_FILE_NAME);
            }
            else
            {
                rBundle = ResourceBundle.getBundle(filterConfig.getInitParameter(ApplicationEnablementFilter.FILTER_CONFIG_PARAM_NAME));
            }

            this.appId = rBundle.getString(ApplicationEnablementFilter.APPID);
            this.appName = rBundle.getString(ApplicationEnablementFilter.APPNAME);
            this.svcNotEnabledRedir = rBundle.getString(ApplicationEnablementFilter.SVC_NOT_ENABLED_REDIR);
            this.svcFailureRedir = rBundle.getString(ApplicationEnablementFilter.SVC_FAILURE_REDIRECT);
            this.svcUnauthorizedRedir = rBundle.getString(ApplicationEnablementFilter.SVC_UNAUTHORIZED_REDIR);
            this.ignoreURIs = (
                    StringUtils.isNotEmpty(rBundle.getString(ApplicationEnablementFilter.IGNORE_URI_LIST)))
                    ? rBundle.getString(ApplicationEnablementFilter.IGNORE_URI_LIST).trim().split(",") : null;

            if (DEBUG)
            {
            	DEBUGGER.debug(this.ignoreURIs);
            	DEBUGGER.debug("appId: {}", this.appId);
            	DEBUGGER.debug("appName: {}", this.appName);
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
    	final String methodName = ApplicationEnablementFilter.CNAME + "#doFilter(final ServletRequest sRequest, final ServletResponse sResponse, final FilterChain filterChain) throws IOException, ServletException";

    	if (DEBUG)
    	{
    		DEBUGGER.debug("Value: {}", methodName);
    		DEBUGGER.debug("Value: {}", sRequest);
    		DEBUGGER.debug("Value: {}", sResponse);
    		DEBUGGER.debug("Value: {}", filterChain);
    	}

        final HttpServletRequest hRequest = (HttpServletRequest) sRequest;
        final HttpServletResponse hResponse = (HttpServletResponse) sResponse;
        final HttpSession hSession = hRequest.getSession(false);
        final String requestURI = hRequest.getRequestURI();

        if (DEBUG)
        {
        	DEBUGGER.debug("HttpServletRequest: {}", hRequest);
        	DEBUGGER.debug("HttpServletResponse: {}", hResponse);
        	DEBUGGER.debug("HttpSession: {}", hSession);
        	DEBUGGER.debug("requestURI: {}", requestURI);
        }

        if ((this.ignoreURIs != null) && (this.ignoreURIs.length != 0))
        {
            if (Arrays.asList(this.ignoreURIs).contains("ALL"))
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("ALL URIs are ignored. Breaking !");
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
                    DEBUGGER.debug("Ignored URI: {}", uri);
                    DEBUGGER.debug("RequestURI: {}", requestURI);
                }

                if (StringUtils.contains(requestURI, uri))
                {
                    // ignore
                    if (DEBUG)
                    {
                        DEBUGGER.debug("URI matched to ignore list - Breaking !");
                    }

                    filterChain.doFilter(sRequest, sResponse);

                    return;
                }
            }
        }

    	final IApplicationEnablementProcessor enabler = (IApplicationEnablementProcessor) new ApplicationEnablementProcessorImpl();

        ApplicationEnablementRequest enableRequest = new ApplicationEnablementRequest();
        enableRequest.setApplicationId(this.appId);
        enableRequest.setApplicationName(this.appName);
        enableRequest.setRequestURI(requestURI.split("/")[3]);

        if (DEBUG)
        {
        	DEBUGGER.debug("ApplicationEnablementRequest: {}", enableRequest);
        }

        try
        {
        	ApplicationEnablementResponse enableResponse = enabler.isServiceEnabled(enableRequest);

	        if (DEBUG)
	        {
	        	DEBUGGER.debug("ApplicationEnablementResponse: {}", enableResponse);
	        }

	        switch (enableResponse.getRequestStatus())
	        {
				case EXCEPTION:
					hResponse.sendRedirect(hRequest.getContextPath() + this.svcFailureRedir);

					return;
				case FAILURE:
					hResponse.sendRedirect(hRequest.getContextPath() + this.svcFailureRedir);

					return;
				case SUCCESS:
					if (enableResponse.isEnabled())
					{
						filterChain.doFilter(sRequest, sResponse);
					}
					else
					{
						hResponse.sendRedirect(hRequest.getContextPath() + this.svcNotEnabledRedir);
					}

					break;
				case UNAUTHORIZED:
					hResponse.sendRedirect(hRequest.getContextPath() + this.svcUnauthorizedRedir);

					break;
				default:
					hResponse.sendRedirect(hRequest.getContextPath() + this.svcNotEnabledRedir);
					
					return;
	        }
        }
        catch (final ApplicationEnablementException aex)
        {
        	ERROR_RECORDER.error(aex.getMessage(), aex);

        	hResponse.sendRedirect(hRequest.getContextPath() + this.svcFailureRedir);

        	return;
        }
    }
}