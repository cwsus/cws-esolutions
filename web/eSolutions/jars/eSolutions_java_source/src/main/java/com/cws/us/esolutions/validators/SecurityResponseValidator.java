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
package com.cws.us.esolutions.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.us.esolutions.Constants;
import com.cws.us.esolutions.dto.UserChangeRequest;
/**
 * CWSPWS_java_source
 * com.cws.us.pws.validators
 * SecurityResponseValidator.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * khuntly @ Apr 14, 2013 9:10:28 AM
 *     Created.
 */
public class SecurityResponseValidator implements Validator
{
    private static final String CNAME = SecurityResponseValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    @Override
    public boolean supports(final Class<?> clazz)
    {
        final String methodName = SecurityResponseValidator.CNAME + "#supports(final Class clazz)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Class: {}", clazz);
        }

        return UserChangeRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors)
    {
        final String methodName = SecurityResponseValidator.CNAME + "#validate(final <Class> request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("target: {}", target);
            DEBUGGER.debug("errors: {}", errors);
        }

        final UserChangeRequest request = (UserChangeRequest) target;

        if (DEBUG)
        {
            DEBUGGER.debug("UserChangeRequest: {}", request);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secQuestionOne", "security.question.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secQuestionTwo", "security.question.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secAnswerOne", "security.answer.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secAnswerTwo", "security.answer.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentPassword", "user.account.current.password.empty");

        if (StringUtils.equals(request.getSecQuestionOne(), request.getSecQuestionTwo()))
        {
            errors.rejectValue("secQuestionOne", "security.questions.cannot.match");
        }
        else if (StringUtils.equals(request.getSecAnswerOne(), request.getSecAnswerTwo()))
        {
            errors.rejectValue("secQuestionOne", "security.answers.cannot.match");
        }
        else if (StringUtils.equals(request.getSecQuestionOne(), request.getSecAnswerOne()))
        {
            errors.rejectValue("secQuestionOne", "security.questions.match.answers");
        }
        else if (StringUtils.equals(request.getSecQuestionTwo(), request.getSecAnswerTwo()))
        {
            errors.rejectValue("secQuestionOne", "security.questions.match.answers");
        }
        else if (StringUtils.equals(request.getSecQuestionOne(), request.getSecAnswerTwo()))
        {
            errors.rejectValue("secQuestionOne", "security.questions.match.answers");
        }
        else if (StringUtils.equals(request.getSecQuestionTwo(), request.getSecAnswerOne()))
        {
            errors.rejectValue("secQuestionOne", "security.questions.match.answers");
        }
    }
}
