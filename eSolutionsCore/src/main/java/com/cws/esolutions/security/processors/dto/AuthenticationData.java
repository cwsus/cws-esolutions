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
package com.cws.esolutions.security.processors.dto;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.processors.dto
 * File: AuthenticationData.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.io.Serializable;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class AuthenticationData implements Serializable
{
    private String userSalt = null;
    private String otpValue = null;
    private String password = null;
    private String newPassword = null;
    private String secAnswerOne = null;
    private String secAnswerTwo = null;
    private String secQuestionOne = null;
    private String secQuestionTwo = null;
    private String resetRequestId = null;

    private static final long serialVersionUID = -1680121237315483191L;
    private static final String CNAME = AuthenticationData.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setUserSalt(final String value)
    {
        final String methodName = AuthenticationData.CNAME + "#setUserSalt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.userSalt = value;
    }

    public final void setPassword(final String value)
    {
        final String methodName = AuthenticationData.CNAME + "#setPassword(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.password = value;
    }

    public final void setNewPassword(final String value)
    {
        final String methodName = AuthenticationData.CNAME + "#setNewPassword(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.newPassword = value;
    }

    public final void setOtpValue(final String value)
    {
        final String methodName = AuthenticationData.CNAME + "#setOtpValue(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.otpValue = value;
    }

    public final void setSecQuestionOne(final String value)
    {
        final String methodName = AuthenticationData.CNAME + "#setSecQuestionOne(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.secQuestionOne = value;
    }

    public final void setSecQuestionTwo(final String value)
    {
        final String methodName = AuthenticationData.CNAME + "#setSecQuestionTwo(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.secQuestionTwo = value;
    }

    public final void setSecAnswerOne(final String value)
    {
        final String methodName = AuthenticationData.CNAME + "#setSecAnswerOne(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.secAnswerOne = value;
    }

    public final void setSecAnswerTwo(final String value)
    {
        final String methodName = AuthenticationData.CNAME + "#setSecAnswerTwo(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.secAnswerTwo = value;
    }

    public final void setResetRequestId(final String value)
    {
        final String methodName = AuthenticationData.CNAME + "#setResetRequestId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.resetRequestId = value;
    }

    public final String getUserSalt()
    {
        final String methodName = AuthenticationData.CNAME + "#getUserSalt()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.userSalt;
    }

    public final String getPassword()
    {
        final String methodName = AuthenticationData.CNAME + "#getPassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.password;
    }

    public final String getNewPassword()
    {
        final String methodName = AuthenticationData.CNAME + "#getNewPassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.newPassword;
    }

    public final String getOtpValue()
    {
        final String methodName = AuthenticationData.CNAME + "#getOtpValue()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.otpValue;
    }

    public final String getSecQuestionOne()
    {
        final String methodName = AuthenticationData.CNAME + "#getSecQuestionOne()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.secQuestionOne;
    }

    public final String getSecQuestionTwo()
    {
        final String methodName = AuthenticationData.CNAME + "#getSecQuestionTwo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.secQuestionTwo;
    }

    public final String getSecAnswerOne()
    {
        final String methodName = AuthenticationData.CNAME + "#getSecAnswerOne()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.secAnswerOne;
    }

    public final String getSecAnswerTwo()
    {
        final String methodName = AuthenticationData.CNAME + "#getSecAnswerTwo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.secAnswerTwo;
    }

    public final String getResetRequestId()
    {
        final String methodName = AuthenticationData.CNAME + "#getResetRequestId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.resetRequestId;
    }
}
