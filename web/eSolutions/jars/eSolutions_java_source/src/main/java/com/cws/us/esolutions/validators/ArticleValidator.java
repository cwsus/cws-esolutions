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
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.us.esolutions.Constants;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.validators
 * ArticleValidator.java
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
 * 35033355 @ May 16, 2013 8:40:08 AM
 *     Created.
 */
public class ArticleValidator implements Validator
{
    private String messageArticleCauseRequired = null;
    private String messageArticleTitleRequired = null;
    private String messageArticleKeywordsRequired = null;
    private String messageArticleSymptomsRequired = null;
    private String messageArticleResolutionRequired = null;

    private static final String CNAME = ArticleValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessageArticleTitleRequired(final String value)
    {
        final String methodName = ArticleValidator.CNAME + "#setMessageArticleTitleRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageArticleTitleRequired = value;
    }

    public final void setMessageArticleCauseRequired(final String value)
    {
        final String methodName = ArticleValidator.CNAME + "#setMessageArticleCauseRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageArticleCauseRequired = value;
    }

    public final void setMessageArticleKeywordsRequired(final String value)
    {
        final String methodName = ArticleValidator.CNAME + "#setMessageArticleKeywordsRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageArticleKeywordsRequired = value;
    }

    public final void setMessageArticleSymptomsRequired(final String value)
    {
        final String methodName = ArticleValidator.CNAME + "#setMessageArticleSymptomsRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageArticleSymptomsRequired = value;
    }

    public final void setMessageArticleResolutionRequired(final String value)
    {
        final String methodName = ArticleValidator.CNAME + "#setMessageArticleResolutionRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageArticleResolutionRequired = value;
    }

    @Override
    public final boolean supports(final Class<?> value)
    {
        final String methodName = ArticleValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }

        final boolean isSupported = ArticleValidator.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", value);
        }

        return isSupported;
    }

    @Override
    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = ArticleValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", this.messageArticleTitleRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cause", this.messageArticleCauseRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "keywords", this.messageArticleKeywordsRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "symptoms", this.messageArticleSymptomsRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "resolution", this.messageArticleResolutionRequired);
    }
}
