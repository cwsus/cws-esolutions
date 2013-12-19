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
package com.cws.esolutions.core.processors.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.impl
 * File: PackageManagementProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   Dec 19, 2013                    Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.processors.dto.PackageManagementRequest;
import com.cws.esolutions.core.processors.dto.PackageManagementResponse;
import com.cws.esolutions.core.processors.exception.PackageManagementException;
import com.cws.esolutions.core.processors.interfaces.IPackageManagementProcessor;
/**
 * TODO: Add class information/description
 *
 * @author kmhuntly@gmail.com
 * @version 1.0
 */
public class PackageManagementProcessorImpl implements IPackageManagementProcessor
{
    private static final String CNAME = PackageManagementProcessorImpl.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory .getLogger(Constants.ERROR_LOGGER);

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IPackageManagementProcessor#addNewPackage(com.cws.esolutions.core.processors.dto.PackageManagementRequest)
     */
    @Override
    public PackageManagementResponse addNewPackage(final PackageManagementRequest request) throws PackageManagementException
    {
        final String methodName = PackageManagementProcessorImpl.CNAME + "#addNewPackage(final PackageManagementRequest request) throws PackageManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", request);
        }

        return null;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IPackageManagementProcessor#updatePackageData(com.cws.esolutions.core.processors.dto.PackageManagementRequest)
     */
    @Override
    public PackageManagementResponse updatePackageData(final PackageManagementRequest request) throws PackageManagementException
    {
        final String methodName = PackageManagementProcessorImpl.CNAME + "#updatePackageData(final PackageManagementRequest request) throws PackageManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", request);
        }

        return null;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IPackageManagementProcessor#removePackageData(com.cws.esolutions.core.processors.dto.PackageManagementRequest)
     */
    @Override
    public PackageManagementResponse removePackageData(final PackageManagementRequest request) throws PackageManagementException
    {
        final String methodName = PackageManagementProcessorImpl.CNAME + "#removePackageData(final PackageManagementRequest request) throws PackageManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", request);
        }

        return null;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IPackageManagementProcessor#getPackageData(com.cws.esolutions.core.processors.dto.PackageManagementRequest)
     */
    @Override
    public PackageManagementResponse getPackageData(final PackageManagementRequest request) throws PackageManagementException
    {
        final String methodName = PackageManagementProcessorImpl.CNAME + "#getPackageData(final PackageManagementRequest request) throws PackageManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", request);
        }

        return null;
    }
}
