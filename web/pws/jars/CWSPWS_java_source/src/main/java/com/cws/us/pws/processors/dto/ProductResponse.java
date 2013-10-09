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
package com.cws.us.pws.processors.dto;

import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
/**
 * CWSPWS_java_source
 * com.cws.us.pws.processors.dto
 * ProductResponse.java
 *
 * TODO: Add class description
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author kh05451
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 31, 2012 2:16:06 PM
 *     Created.
 */
public class ProductResponse implements Serializable
{
    private String response = null;
    private Product product = null;
    private String methodName = null;
    private List<Product> results = null;
    private CoreServicesStatus requestStatus = null;

    private static final long serialVersionUID = -7541177746694315851L;
    private static final String CNAME = ProductResponse.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setRequestStatus(final CoreServicesStatus value)
    {
        this.methodName = ProductResponse.CNAME + "#setRequestStatus(final CoreServicesStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("CoreServicesStatus: {}", value);
        }

        this.requestStatus = value;
    }

    public final void setResponse(final String value)
    {
        this.methodName = ProductResponse.CNAME + "#setResponse(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug(value);
        }

        this.response = value;
    }

    public final void setResults(final List<Product> value)
    {
        this.methodName = ProductResponse.CNAME + "#setResults(final List<Product> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.results = value;
    }

    public final void setProduct(final Product value)
    {
        this.methodName = ProductResponse.CNAME + "#setProduct(final Product value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.product = value;
    }

    public final CoreServicesStatus getRequestStatus()
    {
        this.methodName = ProductResponse.CNAME + "#getRequestStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("CoreServicesStatus: {}", this.requestStatus);
        }

        return this.requestStatus;
    }

    public final String getResponse()
    {
        this.methodName = ProductResponse.CNAME + "#getResponse()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug(this.response);
        }

        return this.response;
    }

    public final List<Product> getResults()
    {
        this.methodName = ProductResponse.CNAME + "#getArticle()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", this.results);
        }

        return this.results;
    }

    public final Product getProduct()
    {
        this.methodName = ProductResponse.CNAME + "#getProduct()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", this.results);
        }

        return this.product;
    }

    public final String toString()
    {
        this.methodName = ProductResponse.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
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
                    // don't do anything with it
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
