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
 *
 */
package com.cws.esolutions.web;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web
 * File: ApplicationServiceBean.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.cws.esolutions.web.validators.EmailAddressValidator;
import com.cws.esolutions.web.validators.WebMessageValidator;
/**
 * @author cws-khuntly
 * @version 1.0
 */
@Component
public class ApplicationServiceBean
{
	@Autowired private String webURL = null;
	@Autowired private String homePage = null;
	@Autowired private String dateFormat = null;
	@Autowired private String olrRedirect = null;
	@Autowired private String fileEncoding = null;
	@Autowired private String homeRedirect = null;
	@Autowired private String isTestSystem = null;
	@Autowired private String emailAddress = null;
	@Autowired private String logonRedirect = null;
	@Autowired private String applicationId = null;
	@Autowired private String expiredRedirect = null;
	@Autowired private String applicationName = null;
	@Autowired private String unavailablePage = null;
	@Autowired private String messageOlrSetup = null;
	@Autowired private String unauthorizedPage = null;
	@Autowired private String contactAdminsPage = null;
	@Autowired private String errorResponsePage = null;
	@Autowired private String searchRequestPage = null;
	@Autowired private String themeMessageSource = null;
	@Autowired private String requestCompletePage = null;
	@Autowired private String messageAccountLocked = null;
	@Autowired private String contactAdminsRedirect = null;
	@Autowired private String messageNoSearchResults = null;
	@Autowired private String messageEmailSendFailed = null;
	@Autowired private String messageRequestCanceled = null;
	@Autowired private String messagePasswordExpired = null;
	@Autowired private String messageUserNotLoggedIn = null;
	@Autowired private String messageValidationFailed = null;
	@Autowired private String messageEmailSentSuccess = null;
	@Autowired private String messageAccountSuspended = null;
	@Autowired private String messageAccountNotAuthorized = null;
	@Autowired private EmailAddressValidator emailValidator = null;
	@Autowired private WebMessageValidator messageValidator = null;
	@Autowired private String messageRequestProcessingFailure = null;
	@Autowired private String messagePasswordLengthCheckFailed = null;

    private static final String CNAME = ApplicationServiceBean.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setFileEncoding(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setFileEncoding(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.fileEncoding = value;
    }

    public final void setLogonRedirect(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setLogonRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.logonRedirect = value;
    }

    public final void setMessageOlrSetup(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setMessageOlrSetup(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageOlrSetup = value;
    }

    public final void setHomeRedirect(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setHomeRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.homeRedirect = value;
    }

    public void setOlrRedirect(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setOlrRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.olrRedirect = value;
    }

    public final void setHomePage(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setHomePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.homePage = value;
    }

    public final void setUnavailablePage(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setUnavailablePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.unavailablePage = value;
    }

    public final void setUnauthorizedPage(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setUnauthorizedPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.unauthorizedPage = value;
    }

    public final void setContactAdminsPage(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setContactAdminsPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.contactAdminsPage = value;
    }

    public final void setContactAdminsRedirect(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setContactAdminsRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.contactAdminsRedirect = value;
    }

    public final void setErrorResponsePage(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setErrorResponsePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.errorResponsePage = value;
    }

    public final void setSearchRequestPage(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setSearchRequestPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.searchRequestPage = value;
    }

    public final void setRequestCompletePage(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setRequestCompletePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestCompletePage = value;
    }

    public final void setDateFormat(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setDateFormat(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dateFormat = value;
    }

    public final void setApplicationName(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setApplicationName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationName = value;
    }

    public final void setApplicationId(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setApplicationId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationId = value;
    }

    public final void setEmailAddress(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setEmailAddress(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.emailAddress = value;
    }

    public final void setMessageRequestCanceled(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setMessageRequestCanceled(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageRequestCanceled = value;
    }

    public final void setMessageEmailSendFailed(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setMessageEmailSendFailed(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageEmailSendFailed = value;
    }

    public final void setMessagePasswordExpired(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setMessagePasswordExpired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePasswordExpired = value;
    }

    public final void setMessageUserNotLoggedIn(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setMessageUserNotLoggedIn(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageUserNotLoggedIn = value;
    }

    public final void setMessageAccountNotAuthorized(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setMessageAccountNotAuthorized(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageAccountNotAuthorized = value;
    }

    public final void setMessageRequestProcessingFailure(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setMessageRequestProcessingFailure(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageRequestProcessingFailure = value;
    }

    public final void setMessageValidationFailed(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setMessageValidationFailed(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageValidationFailed = value;
    }

    public final void setMessageNoSearchResults(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setMessageNoSearchResults(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNoSearchResults = value;
    }

    public final void setMessageAccountLocked(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setMessageAccountLocked(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageAccountLocked = value;
    }

    public final void setMessageEmailSentSuccess(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setMessageEmailSentSuccess(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageEmailSentSuccess = value;
    }

    public final void setMessageValidator(final WebMessageValidator value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setMessageValidator(final WebMessageValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageValidator = value;
    }

    public final void setThemeMessageSource(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setThemeMessageSource(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.themeMessageSource = value;
    }

    public final void setExpiredRedirect(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setExpiredRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.expiredRedirect = value;
    }

    public final void setEmailValidator(final EmailAddressValidator value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getEmailValidator(final EmailAddressValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.emailValidator = value;
    }

    public final void setIsTestSystem(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setIsTestSystem(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.isTestSystem = value;
    }

    public final void setMessageAccountSuspended(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setMessageAccountSuspended(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageAccountSuspended = value;
    }

    public final void setMessagePasswordLengthCheckFailed(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setMessagePasswordLengthCheckFailed(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePasswordLengthCheckFailed = value;
    }

    public final void setWebURL(final String value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setWebURL(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.webURL = value;
    }

    public final String getFileEncoding()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getFileEncoding()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.fileEncoding);
        }

        return this.fileEncoding;
    }

    public final String getLogonRedirect()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getLogonRedirect()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.logonRedirect);
        }

        return this.logonRedirect;
    }

    public final String getHomeRedirect()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getHomeRedirect()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.homeRedirect);
        }

        return this.homeRedirect;
    }

    public final String getDateFormat()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getDateFormat()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dateFormat);
        }

        return this.dateFormat;
    }

    public final String getApplicationName()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getApplicationName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationName);
        }

        return this.applicationName;
    }

    public final String getApplicationId()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getApplicationId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationId);
        }

        return this.applicationId;
    }

    public final String getMessageOlrSetup()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getMessageOlrSetup()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageOlrSetup);
        }

        return this.messageOlrSetup;
    }

    public final String getEmailAddress()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getEmailAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.emailAddress);
        }

        return this.emailAddress;
    }

    public final String getOlrRedirect()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getChangePasswordRedirect()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.olrRedirect);
        }

        return this.olrRedirect;
    }

    public final String getHomePage()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getHomePage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.homePage);
        }

        return this.homePage;
    }

    public final String getUnavailablePage()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getUnavailablePage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.unavailablePage);
        }

        return this.unavailablePage;
    }

    public final String getUnauthorizedPage()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getUnauthorizedPage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.unauthorizedPage);
        }

        return this.unauthorizedPage;
    }

    public final String getContactAdminsPage()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getContactAdminsPage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.contactAdminsPage);
        }

        return this.contactAdminsPage;
    }

    public final String getErrorResponsePage()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getErrorResponsePage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.errorResponsePage);
        }

        return this.errorResponsePage;
    }

    public final String getSearchRequestPage()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getSearchRequestPage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.searchRequestPage);
        }

        return this.searchRequestPage;
    }

    public final String getRequestCompletePage()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getRequestCompletePage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestCompletePage);
        }

        return this.requestCompletePage;
    }

    public final String getMessageRequestCanceled()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getMessageRequestCanceled()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageRequestCanceled);
        }

        return this.messageRequestCanceled;
    }

    public final String getMessageEmailSendFailed()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getMessageEmailSendFailed()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageEmailSendFailed);
        }

        return this.messageEmailSendFailed;
    }

    public final String getMessagePasswordExpired()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getMessagePasswordExpired()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messagePasswordExpired);
        }

        return this.messagePasswordExpired;
    }

    public final String getMessagePasswordLengthCheckFailed()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getMessagePasswordLengthCheckFailed()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messagePasswordLengthCheckFailed);
        }

        return this.messagePasswordLengthCheckFailed;
    }

    public final String getMessageAccountNotAuthorized()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getMessageAccountNotAuthorized()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageAccountNotAuthorized);
        }

        return this.messageAccountNotAuthorized;
    }

    public final String getMessageUserNotLoggedIn()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getMessageUserNotLoggedIn()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageUserNotLoggedIn);
        }

        return this.messageUserNotLoggedIn;
    }

    public final String getMessageRequestProcessingFailure()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getMessageRequestProcessingFailure()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageRequestProcessingFailure);
        }

        return this.messageRequestProcessingFailure;
    }

    public final String getMessageValidationFailed()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getMessageValidationFailed()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageValidationFailed);
        }

        return this.messageValidationFailed;
    }

    public final String getMessageNoSearchResults()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getMessageNoSearchResults()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageNoSearchResults);
        }

        return this.messageNoSearchResults;
    }

    public final String getMessageEmailSentSuccess()
    {
        final String methodName = ApplicationServiceBean.CNAME + "# getMessageEmailSentSuccess()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageEmailSentSuccess);
        }

        return this.messageEmailSentSuccess;
    }

    public final WebMessageValidator getMessageValidator()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getMessageValidator(final WebMessageValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageValidator);
        }

        return this.messageValidator ;
    }

    public final String getThemeMessageSource()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getThemeMessageSource()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.themeMessageSource);
        }

        return this.themeMessageSource;
    }

    public final String getContactAdminsRedirect()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getContactAdminsRedirect()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.contactAdminsRedirect);
        }

        return this.contactAdminsRedirect;
    }

    public final EmailAddressValidator getEmailValidator()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getEmailValidator()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.emailValidator);
        }

        return this.emailValidator;
    }

    public final String getExpiredRedirect()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getExpiredRedirect()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.expiredRedirect);
        }

        return this.expiredRedirect;
    }

    public final String getMessageAccountLocked()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getMessageAccountLocked()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageAccountLocked);
        }

        return this.messageAccountLocked;
    }

    public final String getMessageAccountSuspended()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getMessageAccountSuspended()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageAccountSuspended);
        }

        return this.messageAccountSuspended;
    }
    
	public final boolean getIsTestSystem()
	{
        final String methodName = ApplicationServiceBean.CNAME + "#getIsTestSystem()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isTestSystem);
        }

        return Boolean.valueOf(this.isTestSystem);
	}

	public final boolean isTestSystem()
	{
        final String methodName = ApplicationServiceBean.CNAME + "#isTestSystem()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isTestSystem);
        }

        return Boolean.valueOf(this.isTestSystem);
	}

	public final String getWebURL()
	{
        final String methodName = ApplicationServiceBean.CNAME + "#getWebURL()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.webURL);
        }

        return this.webURL;
	}

    @Override
    public final String toString()
    {
    	StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + Constants.LINE_BREAK + "{" + Constants.LINE_BREAK);

        for (Field field : this.getClass().getDeclaredFields())
        {
            if (!(field.getName().equals("methodName")) &&
                    (!(field.getName().equals("CNAME"))) &&
                    (!(field.getName().equals("DEBUGGER"))) &&
                    (!(field.getName().equals("DEBUG"))) &&
                    (!(field.getName().equals("ERROR_RECORDER"))) &&
                    (!(field.getName().equals("serialVersionUID"))))
            {
                try
                {
                    if (field.get(this) != null)
                    {
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + Constants.LINE_BREAK);
                    }
                }
                catch (final IllegalAccessException iax) {}
            }
        }

        sBuilder.append('}');

        return sBuilder.toString();
    }
}
