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
package com.cws.us.pws.processors.exception;
/**
 * CWSPWS_java_source
 * com.cws.us.pws.processors.exception
 * ProductRequestException.java
 *
 * TODO: Add class description
 *
 * $Id: cws-codetemplates.xml 2286 2013-01-03 20:50:12Z kmhuntly@gmail.com $
 * $Author: kmhuntly@gmail.com $
 * $Date: 2013-01-03 15:50:12 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2286 $
 * @author 35033355
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * 35033355 @ Apr 18, 2013 9:01:25 AM
 *     Created.
 */
public class ProductRequestException extends Exception
{
    private static final long serialVersionUID = 6429307516068916511L;

    public ProductRequestException(final String message)
    {
        super(message);
    }

    public ProductRequestException(final Throwable throwable)
    {
        super(throwable);
    }

    public ProductRequestException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }
}
