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
 * eSolutionsCore
 * com.cws.esolutions.core.processors.dto
 * Product.java
 *
 * TODO: Add class description
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author 35033355
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * 35033355 @ Apr 16, 2013 11:22:17 AM
 *     Created.
 */
public class Product implements Serializable
{
    private int productId = 0;
    private String methodName = null;
    private String productName = null;
    private String productDesc = null;
    private String productCost = null;

    private static final String CNAME = Product.class.getName();
    private static final long serialVersionUID = 1356041731091642819L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setProductId(final int value)
    {
        this.methodName = Product.CNAME + "#setProductId(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.productId = value;
    }

    public final void setProductName(final String value)
    {
        this.methodName = Product.CNAME + "#setProductName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.productName = value;
    }

    public final void setProductDesc(final String value)
    {
        this.methodName = Product.CNAME + "#setProductDesc(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.productDesc = value;
    }

    public final void setProductCost(final String value)
    {
        this.methodName = Product.CNAME + "#setProductCost(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.productCost = value;
    }

    public final int getProductId()
    {
        this.methodName = Product.CNAME + "#getProductId()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", this.productId);
        }

        return this.productId;
    }

    public final String getProductName()
    {
        this.methodName = Product.CNAME + "#getProductName()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", this.productName);
        }

        return this.productName;
    }

    public final String getProductDesc()
    {
        this.methodName = Product.CNAME + "#getProductDesc()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", this.productDesc);
        }

        return this.productDesc;
    }

    public final String getProductCost()
    {
        this.methodName = Product.CNAME + "#getProductCost()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", this.productCost);
        }

        return this.productCost;
    }

    public final String toString()
    {
        this.methodName = Product.CNAME + "#toString()";

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
