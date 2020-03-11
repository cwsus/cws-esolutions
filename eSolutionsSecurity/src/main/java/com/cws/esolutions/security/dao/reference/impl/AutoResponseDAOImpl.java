/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cws.esolutions.security.dao.reference.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.reference.impl
 * File: UserSecurityInformationDAOImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO;
/**
 * @see com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO
 */
public class AutoResponseDAOImpl implements IUserSecurityInformationDAO
{
    private static final String CNAME = AutoResponseDAOImpl.class.getName();

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#addOrUpdateSalt(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized boolean addOrUpdateSalt(final String commonName, final String saltValue, final String saltType)
    {
        final String methodName = AutoResponseDAOImpl.CNAME + "#addOrUpdateSalt(final String commonName, final String saltValue, final String saltType)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
            DEBUGGER.debug("saltType: {}", saltType);
        }

        return true;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#getUserSalt(java.lang.String, java.lang.String)
     */
    public synchronized String getUserSalt(final String commonName, final String saltType)
    {
        final String methodName = AutoResponseDAOImpl.CNAME + "#getUserSalt(final String commonName, final String saltType)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
        }

        return RandomStringUtils.random(svcBean.getConfigData().getSecurityConfig().getSaltLength());
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#removeUserData(java.lang.String, java.lang.String)
     */
    public synchronized boolean removeUserData(final String commonName, final String saltType)
    {
        final String methodName = AutoResponseDAOImpl.CNAME + "#removeUserData(final String commonName, final String saltType)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
            DEBUGGER.debug("saltType: {}", saltType);
        }

        return true;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#insertResetData(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized boolean insertResetData(final String commonName, final String resetId, final String smsCode)
    {
        final String methodName = AutoResponseDAOImpl.CNAME + "#insertResetData(final String commonName, final String resetId, final String smsCode)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
        }

        return true;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#listActiveResets()
     */
    public synchronized List<String[]> listActiveResets()
    {
        final String methodName = AutoResponseDAOImpl.CNAME + "#listActiveResets()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return null;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#getResetData(java.lang.String)
     */
    public synchronized List<Object> getResetData(final String resetId)
    {
        final String methodName = AutoResponseDAOImpl.CNAME + "#getResetData(final String resetId)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return null;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#removeResetData(java.lang.String, java.lang.String)
     */
    public synchronized boolean removeResetData(final String commonName, final String resetId)
    {
        final String methodName = AutoResponseDAOImpl.CNAME + "#removeResetData(final String commonName, final String resetId)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", resetId);
        }

        return true;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#verifySmsForReset(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized boolean verifySmsForReset(final String userGuid, final String resetId, final String smsCode)
    {
        final String methodName = AutoResponseDAOImpl.CNAME + "#verifySmsForReset(final String userGuid, final String resetId, final String smsCode)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userGuid: {}", userGuid);
        }

        return true;
    }
}
