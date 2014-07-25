/*
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
package com.cws.esolutions.web.validators;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.validators
 * File: SecurityResponseValidator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.web.dto.UserChangeRequest;
/**
 * @author khuntly
 * @version 1.0
 * @see org.springframework.validation.Validator
 */
public class SecurityResponseValidator implements Validator
{
    private String messageCurrentPasswordEmpty = null;
    private String messageSecurityAnswersMatch = null;
    private String messageQuestionsAnswersMatch = null;
    private String messageSecurityQuestionsMatch = null;
    private String messageSecurityAnswerRequired = null;
    private String messageSecurityQuestionRequired = null;

    private static final String CNAME = SecurityResponseValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessageSecurityQuestionRequired(final String value)
    {
        final String methodName = SecurityResponseValidator.CNAME + "#setMessageSecurityQuestionRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageSecurityQuestionRequired = value;
    }
        
    public final void setMessageSecurityAnswerRequired(final String value)
    {
        final String methodName = SecurityResponseValidator.CNAME + "#setMessageSecurityAnswerRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageSecurityAnswerRequired = value;
    }

    public final void setMessageCurrentPasswordEmpty(final String value)
    {
        final String methodName = SecurityResponseValidator.CNAME + "#setMessageCurrentPasswordEmpty(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageCurrentPasswordEmpty = value;
    }

    public final void setMessageSecurityQuestionsMatch(final String value)
    {
        final String methodName = SecurityResponseValidator.CNAME + "#setMessageSecurityQuestionsMatch(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageSecurityQuestionsMatch = value;
    }

    public final void setMessageSecurityAnswersMatch(final String value)
    {
        final String methodName = SecurityResponseValidator.CNAME + "#setMessageSecurityAnswersMatch(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageSecurityAnswersMatch = value;
    }

    public final void setMessageQuestionsAnswersMatch(final String value)
    {
        final String methodName = SecurityResponseValidator.CNAME + "#setMessageQuestionsAnswersMatch(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageQuestionsAnswersMatch = value;
    }

    @Override
    public final boolean supports(final Class<?> value)
    {
        final String methodName = SecurityResponseValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Class: {}", value);
        }

        final boolean isSupported = UserChangeRequest.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

    @Override
    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = SecurityResponseValidator.CNAME + "#validate(final <Class> request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("target: {}", target);
            DEBUGGER.debug("errors: {}", errors);
        }

        final UserChangeRequest request = (UserChangeRequest) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secQuestionOne", this.messageSecurityQuestionRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secQuestionTwo", this.messageSecurityQuestionRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secAnswerOne", this.messageSecurityAnswerRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secAnswerTwo", this.messageSecurityAnswerRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentPassword", this.messageCurrentPasswordEmpty);

        if (StringUtils.equals(request.getSecQuestionOne(), request.getSecQuestionTwo()))
        {
            errors.rejectValue("secQuestionOne", this.messageSecurityQuestionsMatch);
        }
        else if (StringUtils.equals(request.getSecAnswerOne(), request.getSecAnswerTwo()))
        {
            errors.rejectValue("secQuestionOne", this.messageSecurityAnswersMatch);
        }
        else if (StringUtils.equals(request.getSecQuestionOne(), request.getSecAnswerOne()))
        {
            errors.rejectValue("secQuestionOne", this.messageQuestionsAnswersMatch);
        }
        else if (StringUtils.equals(request.getSecQuestionTwo(), request.getSecAnswerTwo()))
        {
            errors.rejectValue("secQuestionOne", this.messageQuestionsAnswersMatch);
        }
        else if (StringUtils.equals(request.getSecQuestionOne(), request.getSecAnswerTwo()))
        {
            errors.rejectValue("secQuestionOne", this.messageQuestionsAnswersMatch);
        }
        else if (StringUtils.equals(request.getSecQuestionTwo(), request.getSecAnswerOne()))
        {
            errors.rejectValue("secQuestionOne", this.messageQuestionsAnswersMatch);
        }
    }
}
