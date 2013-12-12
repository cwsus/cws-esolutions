/*
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
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
package com.cws.esolutions.security.keymgmt.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.keymgmt.interfaces.KeyManager;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.interfaces
 * File: IPackageDataDAO.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
public class KeyManagementFactory
{
    private static KeyManager keyManager = null;

    private static final String CNAME = KeyManagementFactory.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + CNAME);

    public static final KeyManager getKeyManager(final String className)
    {
        final String methodName = CNAME + "#getKeyManager(final String className)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", className);
        }

        if (keyManager == null)
        {
            try
            {
                keyManager = (KeyManager) Class.forName(className).newInstance();

                if (DEBUG)
                {
                    DEBUGGER.debug("KeyManager: {}", keyManager);
                }
            }
            catch (InstantiationException ix)
            {
                ERROR_RECORDER.error(ix.getMessage(), ix);
            }
            catch (IllegalAccessException iax)
            {
                ERROR_RECORDER.error(iax.getMessage(), iax);
            }
            catch (ClassNotFoundException cnx)
            {
                ERROR_RECORDER.error(cnx.getMessage(), cnx);
            }
        }

        return keyManager;
    }
}
