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
package com.cws.esolutions.security.dao.usermgmt.factory;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.usermgmt.factory
 * File: UserManagerFactory.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class UserManagerFactory
{
    private static UserManager userManager = null;

    private static final String CNAME = UserManagerFactory.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER + CNAME);

    public static final UserManager getUserManager(final String className)
    {
        final String methodName = CNAME + "#getUserManager(final String className)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", className);
        }

        if (userManager == null)
        {
            try
            {
                userManager = (UserManager) Class.forName(className).newInstance();

                if (DEBUG)
                {
                    DEBUGGER.debug("UserManager: {}", userManager);
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

        return userManager;
    }
}
