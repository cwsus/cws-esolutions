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
package com.cws.esolutions.security.dao.usermgmt.impl;
import java.io.BufferedReader;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.usermgmt.impl
 * File: SQLUserManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager
 */
public class FileUserManager implements UserManager
{
    private static final String CNAME = FileUserManager.class.getName();

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#validateUserAccount(java.lang.String, java.lang.String)
     */
    public synchronized boolean validateUserAccount(final String userId, final String userGuid) throws UserManagementException
    {
        final String methodName = FileUserManager.CNAME + "#validateUserAccount(final String userId, final String userGuid) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        Scanner scanner = null;
        boolean isValid = false;

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
    		
	    		if (lineEntry.contains(userId) && lineEntry.contains(userGuid))
	    		{
	    			String[] userAttributes = lineEntry.split(":");

	        		if (DEBUG)
	        		{
	        			for (int x = 0; x != userAttributes.length; x++)
	        			{
	        				DEBUGGER.debug("userAttributes: {}", (Object) userAttributes[x]);
	        			}
	        		}

	    			isValid = true;
	    		}
	    	}
        }
        catch (FileNotFoundException fnfx)
        {
        	throw new UserManagementException(fnfx.getMessage(), fnfx);
        }
        finally
        {
        	try
        	{
        		scanner.close();
        	}
        	catch (IllegalStateException isx) {} // dont do anything with it
        }

        return isValid;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#addUserAccount(java.util.List, java.util.List)
     */
    public synchronized boolean addUserAccount(final List<String> userAccount, final List<String> roles) throws UserManagementException
    {
        final String methodName = FileUserManager.CNAME + "#addUserAccount(final List<String> userAccount, final List<String> roles) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userAccount);
            DEBUGGER.debug("Value: {}", roles);
        }

        boolean isComplete = false;

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#removeUserAccount(java.lang.String)
     */
    public synchronized boolean removeUserAccount(final String userId) throws UserManagementException
    {
        final String methodName = FileUserManager.CNAME + "#removeUserAccount(final String userId) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
        }

        boolean isComplete = false;

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#searchUsers(java.lang.String)
     */
    public synchronized List<String[]> searchUsers(final String searchData) throws UserManagementException
    {
        final String methodName = FileUserManager.CNAME + "#searchUsers(final String searchData) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", searchData);
        }

        List<String[]> results = null;

        return results;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#loadUserAccount(java.lang.String)
     */
    public synchronized List<Object> loadUserAccount(final String userGuid) throws UserManagementException
    {
        final String methodName = FileUserManager.CNAME + "#loadUserAccount(final String guid) throws UserManagementException";
        
        if(DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        List<Object> userAccount = null;

        return userAccount;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#listUserAccounts()
     */
    public synchronized List<String[]> listUserAccounts() throws UserManagementException
    {
        final String methodName = FileUserManager.CNAME + "#listUserAccounts() throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        List<String[]> results = null;

        return results;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserEmail(java.lang.String, java.lang.String)
     */
    public synchronized boolean modifyUserEmail(final String userId, final String value) throws UserManagementException
    {
        final String methodName = FileUserManager.CNAME + "#modifyUserEmail(final String userId, final String value) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", value);
        }

        boolean isComplete = false;

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserContact(java.lang.String, java.util.List)
     */
    public synchronized boolean modifyUserContact(final String userId, final List<String> values) throws UserManagementException
    {
        final String methodName = FileUserManager.CNAME + "#modifyUserContact(final String userId, final List<String> values) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Values: {}", values);
        }

        boolean isComplete = false;

        return isComplete;
    }
    
    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserSuspension(java.lang.String, boolean)
     */
    public synchronized boolean modifyUserSuspension(final String userId, final boolean isSuspended) throws UserManagementException
    {
        final String methodName = FileUserManager.CNAME + "#modifyUserSuspension(final String userId, final boolean isSuspended) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", isSuspended);
        }

        boolean isComplete = false;

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserGroups(java.lang.String, java.lang.Object[])
     */
    public synchronized boolean modifyUserGroups(final String userId, final Object[] values) throws UserManagementException
    {
        final String methodName = FileUserManager.CNAME + "#modifyUserGroups(final String userId, final Object[] values) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", values);
        }

        boolean isComplete = false;

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyOlrLock(java.lang.String, boolean)
     */
    public synchronized boolean modifyOlrLock(final String userId, final boolean isLocked) throws UserManagementException
    {
        final String methodName = FileUserManager.CNAME + "#modifyOlrLock(final String userId, final boolean value) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", isLocked);
        }

        boolean isComplete = false;

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserLock(java.lang.String, boolean, int)
     */
    public synchronized boolean modifyUserLock(final String userId, final boolean isLocked, final int increment) throws UserManagementException
    {
        final String methodName = FileUserManager.CNAME + "#modifyUserLock(final String userId, final boolean int, final boolean increment) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", isLocked);
            DEBUGGER.debug("Value: {}", increment);
        }

        boolean isComplete = false;

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserPassword(java.lang.String, java.lang.String)
     */
    public synchronized boolean modifyUserPassword(final String userId, final String newPass) throws UserManagementException
    {
        final String methodName = FileUserManager.CNAME + "#modifyUserPassword(final String userId, final String newPass) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
        }

        boolean isComplete = false;

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyOtpSecret(java.lang.String, boolean, java.lang.String)
     */
    public synchronized boolean modifyOtpSecret(final String userId, final boolean addSecret, final String secret) throws UserManagementException
    {
        final String methodName = FileUserManager.CNAME + "#modifyOtpSecret(final String userId, final boolean addSecret, final String secret) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
        }

        boolean isComplete = false;

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserSecurity(java.lang.String, java.util.List)
     */
    public synchronized boolean modifyUserSecurity(final String userId, final List<String> values) throws UserManagementException
    {
        final String methodName = FileUserManager.CNAME + "#modifyUserSecurity(final String userId, final List<String> values) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
        }

        boolean isComplete = false;

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyLastLogin(java.lang.String, java.util.String, java.util.Long)
     */
	public synchronized boolean performSuccessfulLogin(final String userId, final String guid, final int lockCount, final long timestamp) throws UserManagementException
	{
        final String methodName = FileUserManager.CNAME + "#performSuccessfulLogin(final String userId, final String guid, final int lockCount, final long timestamp) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", guid);
            DEBUGGER.debug("Value: {}", lockCount);
            DEBUGGER.debug("Value: {}", timestamp);
        }

    	String lineEntry = null;
    	String inputString = null;
    	boolean isComplete = false;
    	StringBuffer sBuffer = null;
    	BufferedReader bReader = null;
    	FileOutputStream fileOut = null;
    	File textFile = new File(passwordConfig.getPasswordFile());

		try
		{
			bReader = new BufferedReader(new FileReader(textFile));
			sBuffer = new StringBuffer();
			lineEntry = null;

			while ((lineEntry = bReader.readLine()) != null)
			{
				if (DEBUG)
				{
					DEBUGGER.debug("lineEntry: {}", lineEntry);
				}

				sBuffer.append(lineEntry);
				sBuffer.append(System.lineSeparator());
			}

			inputString = sBuffer.toString();

			if (DEBUG)
			{
				DEBUGGER.debug("inputString: {}", inputString);
			}

			if ((inputString.contains(userId)) && inputString.contains(guid))
			{
				inputString = inputString.replace(String.valueOf(timestamp), String.valueOf(System.currentTimeMillis()));
				inputString = inputString.replace(String.valueOf(lockCount), String.valueOf(0));
			}

			fileOut = new FileOutputStream(textFile);
			fileOut.write(inputString.getBytes());

			isComplete = true;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (fileOut != null)
				{
					fileOut.flush();
					fileOut.close();
				}

				if (bReader != null)
				{
					bReader.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return isComplete;
	}
}
