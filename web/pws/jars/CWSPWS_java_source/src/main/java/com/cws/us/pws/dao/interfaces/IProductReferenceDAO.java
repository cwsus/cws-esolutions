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
package com.cws.us.pws.dao.interfaces;

import java.util.List;
import org.slf4j.Logger;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
/**
 * CWSPWS_java_source
 * com.cws.us.pws.dao.interfaces
 * IProductReferenceDAO.java
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
 * 35033355 @ Apr 16, 2013 12:13:33 PM
 *     Created.
 */
public interface IProductReferenceDAO
{
    static final String CNAME = IProductReferenceDAO.class.getName();

    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    static final Logger WARN_RECORDER = LoggerFactory.getLogger(Constants.WARN_LOGGER + CNAME);

    List<String[]> getProductList() throws SQLException;

    List<String> getProductData(final int productId) throws SQLException;
}
