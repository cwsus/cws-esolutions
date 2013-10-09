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
package com.cws.us.pws.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.us.pws.Constants;
import com.cws.esolutions.core.processors.dto.EmailMessage;
/**
 * CWSPWS_java_source
 * com.cws.us.pws.validators
 * ContactValidator.java
 *
 * TODO: Add class description
 *
 * $Id: cws-codetemplates.xml 2286 2013-01-03 20:50:12Z kmhuntly@gmail.com $
 * $Author: kmhuntly@gmail.com $
 * $Date: 2013-01-03 15:50:12 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2286 $
 * @author khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * khuntly @ Apr 14, 2013 9:10:28 AM
 *     Created.
 */
public class SearchValidator implements Validator
{
    private String methodName = null;

    private static final String CNAME = SearchValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * TODO: Add in the method description/comments
     *
     * @param clazz
     * @return
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @Override
    public boolean supports(final Class<?> clazz)
    {
        this.methodName = SearchValidator.CNAME + "#supports(final Class clazz)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Class: {}", clazz);
        }

        return EmailMessage.class.isAssignableFrom(clazz);
    }

    /**
     * TODO: Add in the method description/comments
     *
     * @param target
     * @param errors
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    @Override
    public void validate(final Object target, final Errors errors)
    {
        this.methodName = SearchValidator.CNAME + "#validate(final <Class> request)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("target: {}", target);
            DEBUGGER.debug("errors: {}", errors);
        }

        final EmailMessage request = (EmailMessage) target;

        if (DEBUG)
        {
            DEBUGGER.debug("EmailMessage: {}", request);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "email.first.name.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "email.last.name.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "messageTo", "email.source.addr.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "messageSubject", "email.message.subject.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "messageBody", "email.message.body.required");
    }
}
