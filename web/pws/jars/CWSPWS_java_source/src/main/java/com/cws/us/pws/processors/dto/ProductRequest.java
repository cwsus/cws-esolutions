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

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
/**
 * CWSPWS_java_source
 * com.cws.us.pws.processors.dto
 * ProductRequest.java
 *
 * TODO: Add class description
 *
 * $Id: SearchRequest.java 2287 2013-01-03 20:52:22Z kmhuntly@gmail.com $
 * $Author: kmhuntly@gmail.com $
 * $Date: 2013-01-03 15:52:22 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2287 $
 * @author kh05451
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 30, 2012 2:45:21 PM
 *     Created.
 */
public class ProductRequest implements Serializable
{
    private Product product = null;
    private String methodName = null;
    private String productName = null;

    private static final long serialVersionUID = 8384477289554786349L;
    private static final String CNAME = ProductRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setProduct(final Product value)
    {
        this.methodName = ProductRequest.CNAME + "#setProduct(final Product value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.product = value;
    }

    public final void setProductName(final String value)
    {
        this.methodName = ProductRequest.CNAME + "#setProductName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug(value);
        }

        this.productName = value;
    }

    public final Product getProduct()
    {
        this.methodName = ProductRequest.CNAME + "#getProduct()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", this.product);
        }

        return this.product;
    }

    public final String getProductName()
    {
        this.methodName = ProductRequest.CNAME + "#getProductName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", this.productName);
        }

        return this.productName;
    }

    public final String toString()
    {
        this.methodName = ProductRequest.CNAME + "#toString()";

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
