/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
package com.cws.esolutions.security.services.dto;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.processors.dto
 * File: DNSEntry.java
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

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * @author khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class AccessControlServiceRequest implements Serializable
{
	private String serviceGuid = null;
	private UserAccount userAccount = null;

    private static final String CNAME = AccessControlServiceRequest.class.getName();
    private static final long serialVersionUID = 3314079583199404196L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER);

    public final void setServiceGuid(final String value)
    {
    	final String methodName = AccessControlServiceRequest.CNAME + "#setServiceGuid(final String value)";

    	if (DEBUG)
    	{
    		DEBUGGER.debug(methodName);
    		DEBUGGER.debug("Value: {}", value);
    	}

    	this.serviceGuid = value;
    }

    public final void setUserAccount(final UserAccount value)
    {
    	final String methodName = AccessControlServiceRequest.CNAME + "#setUserAccount(final UserAccount value)";

    	if (DEBUG)
    	{
    		DEBUGGER.debug(methodName);
    		DEBUGGER.debug("UserAccount: {}", value);
    	}

    	this.userAccount = value;
    }

    public final String getServiceGuid()
    {
    	final String methodName = AccessControlServiceRequest.CNAME + "#getServiceGuid()";

    	if (DEBUG)
    	{
    		DEBUGGER.debug(methodName);
    		DEBUGGER.debug("Value: {}", this.serviceGuid);
    	}

    	return this.serviceGuid;
    }

    public final UserAccount getUserAccount()
    {
    	final String methodName = AccessControlServiceRequest.CNAME + "#getUserAccount()";

    	if (DEBUG)
    	{
    		DEBUGGER.debug(methodName);
    		DEBUGGER.debug("UserAccount: {}", this.userAccount);
    	}

    	return this.userAccount;
    }

    @Override
    public final String toString()
    {
        final String methodName = AccessControlServiceRequest.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + SecurityServiceConstants.LINE_BREAK + "{" + SecurityServiceConstants.LINE_BREAK);

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
                    (!(field.getName().equals("serialVersionUID"))))
            {
                try
                {
                    if (field.get(this) != null)
                    {
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + SecurityServiceConstants.LINE_BREAK);
                    }
                }
                catch (IllegalAccessException iax)
                {
                    ERROR_RECORDER.error(iax.getMessage(), iax);
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
