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
package com.cws.esolutions.security.dto;

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.security.SecurityConstants;
/**
 * SecurityService
 * com.cws.esolutions.security.dto
 * UserSecurity.java
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
 * kh05451 @ Nov 2, 2012 12:16:43 PM
 *     Created.
 */
public class UserSecurity implements Serializable
{
    private String userSalt = null;
    private String sequence = null;
    private String otpValue = null;
    private String password = null;
    private String secAnswer = null;
    private String secQuestion = null;
    private String newPassword = null;
    private String secAnswerOne = null;
    private String secAnswerTwo = null;
    private String secQuestionOne = null;
    private String secQuestionTwo = null;
    private String resetRequestId = null;

    private static final String CNAME = UserSecurity.class.getName();
    private static final long serialVersionUID = -1253092123530982249L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER);

    public final void setUserSalt(final String value)
    {
        final String methodName = UserSecurity.CNAME + "#setUserSalt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userSalt = value;
    }

    public final void setPassword(final String value)
    {
        final String methodName = UserSecurity.CNAME + "#setPassword(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.password = value;
    }

    public final void setNewPassword(final String value)
    {
        final String methodName = UserSecurity.CNAME + "#setNewPassword(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.newPassword = value;
    }

    public final void setOtpValue(final String value)
    {
        final String methodName = UserSecurity.CNAME + "#setOtpValue(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.otpValue = value;
    }

    public final void setSequence(final String value)
    {
        final String methodName = UserSecurity.CNAME + "#setSequence(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sequence = value;
    }

    public final void setSecQuestion(final String value)
    {
        final String methodName = UserSecurity.CNAME + "#setSecQuestion(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secQuestion = value;
    }

    public final void setSecQuestionOne(final String value)
    {
        final String methodName = UserSecurity.CNAME + "#setSecQuestionOne(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secQuestionOne = value;
    }

    public final void setSecQuestionTwo(final String value)
    {
        final String methodName = UserSecurity.CNAME + "#setSecQuestionTwo(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secQuestionTwo = value;
    }

    public final void setSecAnswer(final String value)
    {
        final String methodName = UserSecurity.CNAME + "#setSecAnswer(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secAnswer = value;
    }

    public final void setSecAnswerOne(final String value)
    {
        final String methodName = UserSecurity.CNAME + "#setSecAnswerOne(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secAnswerOne = value;
    }

    public final void setSecAnswerTwo(final String value)
    {
        final String methodName = UserSecurity.CNAME + "#setSecAnswerTwo(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secAnswerTwo = value;
    }

    public final void setResetRequestId(final String value)
    {
        final String methodName = UserSecurity.CNAME + "#setResetRequestId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.resetRequestId = value;
    }

    public final String getUserSalt()
    {
        final String methodName = UserSecurity.CNAME + "#getUserSalt()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userSalt);
        }

        return this.userSalt;
    }

    public final String getPassword()
    {
        final String methodName = UserSecurity.CNAME + "#getPassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.password);
        }

        return this.password;
    }

    public final String getNewPassword()
    {
        final String methodName = UserSecurity.CNAME + "#getNewPassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.newPassword);
        }

        return this.newPassword;
    }

    public final String getOtpValue()
    {
        final String methodName = UserSecurity.CNAME + "#getOtpValue()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.otpValue);
        }

        return this.otpValue;
    }

    public final String getSequence()
    {
        final String methodName = UserSecurity.CNAME + "#getSequence()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sequence);
        }

        return this.sequence;
    }

    public final String getSecQuestion()
    {
        final String methodName = UserSecurity.CNAME + "#getSecQuestion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secQuestion);
        }

        return this.secQuestion;
    }

    public final String getSecQuestionOne()
    {
        final String methodName = UserSecurity.CNAME + "#getSecQuestionOne()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secQuestionOne);
        }

        return this.secQuestionOne;
    }

    public final String getSecQuestionTwo()
    {
        final String methodName = UserSecurity.CNAME + "#getSecQuestionTwo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secQuestionTwo);
        }

        return this.secQuestionTwo;
    }

    public final String getSecAnswer()
    {
        final String methodName = UserSecurity.CNAME + "#getSecAnswer()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secAnswer);
        }

        return this.secAnswer;
    }

    public final String getSecAnswerOne()
    {
        final String methodName = UserSecurity.CNAME + "#getSecAnswerOne()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secAnswerOne);
        }

        return this.secAnswerOne;
    }

    public final String getSecAnswerTwo()
    {
        final String methodName = UserSecurity.CNAME + "#getSecAnswerTwo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secAnswerTwo);
        }

        return this.secAnswerTwo;
    }

    public final String getResetRequestId()
    {
        final String methodName = UserSecurity.CNAME + "#getResetRequestId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.resetRequestId);
        }

        return this.resetRequestId;
    }

    @Override
    public final String toString()
    {
        final String methodName = UserSecurity.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + Constants.LINE_BREAK + "{" + Constants.LINE_BREAK);

        for (Field field : this.getClass().getDeclaredFields())
        {
            if (DEBUG)
            {
                DEBUGGER.debug("field: {}", field);
            }

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
                catch (IllegalAccessException iax)
                {
                    ERROR_RECORDER.error(iax.getMessage(), iax);
                }
            }
        }

        sBuilder.append('}');

        if (DEBUG)
        {
            DEBUGGER.debug("sBuilder: {}", sBuilder);
        }

        return sBuilder.toString();
    }
}
