/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
 */
package com.cws.esolutions.web.model;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.model
 * File: LoginRequest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.web.Constants;
/**
 * @author khuntly
 * @version 1.0
 */
public class SearchRequest implements Serializable
{
	private String searchType = null;
    private String searchTerms = null;
    private String searchExtras = null;

    private static final String CNAME = SearchRequest.class.getName();
    private static final long serialVersionUID = -7043606830867233708L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setSearchType(final String value)
    {
        final String methodName = SearchRequest.CNAME + "#setSearchType(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.searchType = value;
    }

    public final void setSearchTerms(final String value)
    {
        final String methodName = SearchRequest.CNAME + "#setSearchTerms(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.searchTerms = value;
    }

    public final void setSearchExtras(final String value)
    {
        final String methodName = SearchRequest.CNAME + "#setSearchExtras(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.searchExtras = value;
    }

    public final String getSearchType()
    {
        final String methodName = SearchRequest.CNAME + "#getSearchType";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.searchType);
        }

        return this.searchType;
    }

    public final String getSearchTerms()
    {
        final String methodName = SearchRequest.CNAME + "#getSearchTerms";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.searchTerms);
        }

        return this.searchTerms;
    }

    public final String getSearchExtras()
    {
        final String methodName = SearchRequest.CNAME + "#getSearchExtras";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.searchExtras);
        }

        return this.searchExtras;
    }

    @Override
    public final String toString()
    {
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
                    (!(field.getName().equals("loginPass"))) &&
                    (!(field.getName().equals("otpValue"))) &&
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
