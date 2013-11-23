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
package com.cws.esolutions.security.exception;

import java.util.Arrays;
import java.util.ArrayList;

import com.unboundid.ldap.sdk.ResultCode;
import com.cws.esolutions.core.utils.EmailUtils;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.ExceptionConfig;
import com.cws.esolutions.core.processors.dto.EmailMessage;
/**
 * SecurityService
 * com.cws.esolutions.security.exception
 * SecurityServiceException.java
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
 * kh05451 @ Oct 29, 2012 3:41:35 PM
 *     Created.
 */
public class SecurityServiceException extends Exception
{
    private static final long serialVersionUID = -5953286132656674063L;

    public SecurityServiceException(final String message)
    {
        super(message);

        this.sendExceptionLetter(message);
    }

    public SecurityServiceException(final Throwable throwable)
    {
        super(throwable);

        this.sendExceptionLetter(throwable.getMessage());
    }

    public SecurityServiceException(final String message, final Throwable throwable)
    {
        super(message, throwable);

        this.sendExceptionLetter(message);
    }

    public SecurityServiceException(@SuppressWarnings("unused") final ResultCode code, final String message, final Throwable throwable)
    {
        super(message, throwable);

        this.sendExceptionLetter(message);
    }

    private void sendExceptionLetter(final String message)
    {
        final SecurityServiceBean bean = SecurityServiceBean.getInstance();
        final ExceptionConfig config = bean.getConfigData().getExceptionConfig();

        if (config.getSendExceptionNotifications())
        {
            StringBuilder builder = new StringBuilder()
                .append("A CoreServiceException was thrown: \n")
                .append("Message: " + message + "\n")
                .append("Stacktrace: \n\n");

            StackTraceElement[] elements = Thread.currentThread().getStackTrace();

            for (StackTraceElement element : elements)
            {
                builder.append(element + "\n");
            }

            EmailMessage email = new EmailMessage();
            email.setIsAlert(true);
            email.setMessageSubject("SecurityServiceException occurred !");
            email.setEmailAddr(new ArrayList<>(Arrays.asList(config.getEmailFrom())));
            email.setMessageTo(config.getNotificationAddress());
            email.setMessageBody(builder.toString());

            // modified "sendEmailMessage(), removing thrown exceptions
            EmailUtils.sendExceptionLetter(email);
        }
    }
}
