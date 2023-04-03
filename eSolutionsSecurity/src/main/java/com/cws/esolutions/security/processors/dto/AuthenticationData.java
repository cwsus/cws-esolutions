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
package com.cws.esolutions.security.processors.dto;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.dto
 * File: AuthenticationData.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.io.Serializable;
import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class AuthenticationData implements Serializable
{
	private String resetKey = null;
	private String username = null;
    private String userSalt = null;
    private char[] password = null;
    private char[] newPassword = null;
    private char[] secAnswerOne = null;
    private char[] secAnswerTwo = null;
    private String secQuestionOne = null;
    private String secQuestionTwo = null;

    private static final long serialVersionUID = 1920284352649895644L;
    private static final String CNAME = AuthenticationData.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setUsername(final String value)
    {
        final String methodName = AuthenticationData.CNAME + "#setUsername(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.username = value;
    }

    public final void setUserSalt(final String value)
    {
        final String methodName = AuthenticationData.CNAME + "#setUserSalt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userSalt = value;
    }

    public final void setPassword(final char[] value)
    {
        final String methodName = AuthenticationData.CNAME + "#setPassword(final char[] value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.password = value;
    }

    public final void setNewPassword(final char[] value)
    {
        final String methodName = AuthenticationData.CNAME + "#setNewPassword(final char[] value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.newPassword = value;
    }

    public final void setSecQuestionOne(final String value)
    {
        final String methodName = AuthenticationData.CNAME + "#setSecQuestionOne(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secQuestionOne = value;
    }

    public final void setSecQuestionTwo(final String value)
    {
        final String methodName = AuthenticationData.CNAME + "#setSecQuestionTwo(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secQuestionTwo = value;
    }

    public final void setSecAnswerOne(final char[] value)
    {
        final String methodName = AuthenticationData.CNAME + "#setSecAnswerOne(final char[] value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secAnswerOne = value;
    }

    public final void setSecAnswerTwo(final char[] value)
    {
        final String methodName = AuthenticationData.CNAME + "#setSecAnswerTwo(final char[] value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secAnswerTwo = value;
    }

    public final void setResetKey(final String value)
    {
        final String methodName = AuthenticationData.CNAME + "#setResetKey(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.resetKey = value;
    }

    public final String getUsername()
    {
        final String methodName = AuthenticationData.CNAME + "#getUsername()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.username);
        }

        return this.username;
    }

    public final String getUserSalt()
    {
        final String methodName = AuthenticationData.CNAME + "#getUserSalt()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userSalt);
        }

        return this.userSalt;
    }

    public final char[] getPassword()
    {
        final String methodName = AuthenticationData.CNAME + "#getPassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.password);
        }

        return this.password;
    }

    public final char[] getNewPassword()
    {
        final String methodName = AuthenticationData.CNAME + "#getNewPassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.newPassword);
        }

        return this.newPassword;
    }

    public final String getSecQuestionOne()
    {
        final String methodName = AuthenticationData.CNAME + "#getSecQuestionOne()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secQuestionOne);
        }

        return this.secQuestionOne;
    }

    public final String getSecQuestionTwo()
    {
        final String methodName = AuthenticationData.CNAME + "#getSecQuestionTwo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secQuestionTwo);
        }

        return this.secQuestionTwo;
    }

    public final char[] getSecAnswerOne()
    {
        final String methodName = AuthenticationData.CNAME + "#getSecAnswerOne()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secAnswerOne);
        }

        return this.secAnswerOne;
    }

    public final char[] getSecAnswerTwo()
    {
        final String methodName = AuthenticationData.CNAME + "#getSecAnswerTwo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secAnswerTwo);
        }

        return this.secAnswerTwo;
    }

    public final String getResetKey()
    {
        final String methodName = AuthenticationData.CNAME + "#getResetKey()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.resetKey);
        }

        return this.resetKey;
    }

    @Override
    public final String toString()
    {
        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + SecurityServiceConstants.LINE_BREAK + "{" + SecurityServiceConstants.LINE_BREAK);

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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + SecurityServiceConstants.LINE_BREAK);
                    }
                }
                catch (final IllegalAccessException iax) {}
            }
        }

        sBuilder.append('}');

        return sBuilder.toString();
    }
}
