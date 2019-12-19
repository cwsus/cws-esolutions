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
package com.cws.esolutions.security.dao.userauth.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.userauth.impl
 * File: FileAuthenticator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   		12/17/2019 22:39:20             Created.
 */
import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileNotFoundException;

import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
/**
 * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator
 */
public class FileAuthenticator implements Authenticator
{
	
    private static final String CNAME = FileAuthenticator.class.getName();

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#performLogon(java.lang.String, java.lang.String)
     */
    public synchronized List<Object> performLogon(final String username, final String password) throws AuthenticatorException
    {
        final String methodName = FileAuthenticator.CNAME + "#performLogon(final String username, final String password) throws AuthenticatorException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("String: {}", username);
        }

        Scanner scanner = null;
        List<Object> userAccount = null;

        try
        {
        	scanner = new Scanner(new File(passwordConfig.getPasswordFile()));

	        if (DEBUG)
	        {
	        	DEBUGGER.debug("Scanner: {}", scanner);
	        }

	    	while (scanner.hasNext())
	    	{
	    		String lineEntry = scanner.nextLine();

	    		if (DEBUG)
	    		{
	    			DEBUGGER.debug("lineEntry: {}", lineEntry);
	    		}
    		
	    		if (lineEntry.contains(username) && lineEntry.contains(password))
	    		{
	    			String[] userAttributes = lineEntry.split(":");

	        		if (DEBUG)
	        		{
	        			for (int x = 0; x != userAttributes.length; x++)
	        			{
	        				DEBUGGER.debug("userAttributes: {}", (Object) userAttributes[x]);
	        			}
	        		}

	    			userAccount = new ArrayList<Object>();
	    			userAccount.add(userAttributes);
	    		}
	    	}
        }
        catch (FileNotFoundException fnfx)
        {
        	throw new AuthenticatorException(fnfx.getMessage(), fnfx);
        }
        finally
        {
        	try
        	{
        		scanner.close();
        	}
        	catch (IllegalStateException isx) {} // dont do anything with it
        }

        return userAccount;
    }

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#obtainSecurityData(java.lang.String, java.lang.String)
     */
    public synchronized List<String> obtainSecurityData(final String userId, final String userGuid) throws AuthenticatorException
    {
        final String methodName = FileAuthenticator.CNAME + "#obtainSecurityData(final String userId, final String userGuid) throws AuthenticatorException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        List<String> userSecurity = null;

        return userSecurity;
    }

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#obtainOtpSecret(java.lang.String, java.lang.String)
     */
    public synchronized String obtainOtpSecret(final String userId, final String userGuid) throws AuthenticatorException
    {
        final String methodName = FileAuthenticator.CNAME + "#obtainOtpSecret(final String userId, final String userGuid) throws AuthenticatorException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        String otpSecret = null;

        return otpSecret;
    }

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#verifySecurityData(java.lang.String, java.lang.String, java.util.List)
     */
    public synchronized boolean verifySecurityData(final String userId, final String userGuid, List<String> values) throws AuthenticatorException
    {
        final String methodName = FileAuthenticator.CNAME + "#verifySecurityData(final String userId, final String userGuid, List<String> values) throws AuthenticatorException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        boolean isComplete = false;

        return isComplete;
    }
}
