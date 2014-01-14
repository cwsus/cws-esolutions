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
package com.cws.esolutions.security.jaas;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.jaas
 * File: LDAPLoginModule.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * 35033355              Jan 14, 2014                         Created.
 */

import java.util.Map;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;
import javax.security.auth.Subject;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.callback.Callback;
import javax.security.auth.login.LoginException;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.cws.esolutions.security.dto.UserGroup;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.dao.userauth.impl.LDAPAuthenticator;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
import com.cws.esolutions.security.dao.reference.impl.UserServiceInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.IUserServiceInformationDAO;
/**
 * TODO: Add class information/description
 *
 * @author 35033355
 * @version 1.0
 * @see javax.security.auth.spi.LoginModule
 */
public class LDAPLoginModule implements LoginModule
{
    private Subject subject = null;
    private UserGroup userGroups = null;
    private CallbackHandler handler = null;
    private UserAccount userAccount = null;

    private static final String CNAME = LDAPLoginModule.class.getName();
    private static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    private static final SecurityConfig secConfig = svcBean.getConfigData().getSecurityConfig();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER);

    /**
     * TODO: Add in the method description/comments
     *
     * @param subject
     * @param callbackHandler
     * @param sharedState
     * @param options
     * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
     */
    @Override
    public void initialize(final Subject subject, final CallbackHandler handler, final Map<String, ?> sharedState, final Map<String, ?> options)
    {
        final String methodName = LDAPLoginModule.CNAME + "#initialize(final Subject subject, final CallbackHandler handler, final Map<String, ?> sharedState, final Map<String, ?> options)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Subject: {}", subject);
            DEBUGGER.debug("CallbackHandler: {}", handler);
            DEBUGGER.debug("Map<String, ?>: {}", sharedState);
            DEBUGGER.debug("Map<String, ?>: {}", options);
        }

        this.handler = handler;
        this.subject = subject;
    }

    /**
     * TODO: Add in the method description/comments
     *
     * @return
     * @throws LoginException
     * @see javax.security.auth.spi.LoginModule#login()
     */
    @Override
    public boolean login() throws LoginException
    {
        final String methodName = LDAPLoginModule.CNAME + "#login() throws LoginException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        boolean isComplete = false;
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("login");
        callbacks[1] = new PasswordCallback("password", true);

        final Authenticator authenticator = new LDAPAuthenticator();
        final String username = ((NameCallback) callbacks[0]).getName();
        final IUserServiceInformationDAO svcInfo = new UserServiceInformationDAOImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("username: {}", username);
            DEBUGGER.debug("Callback[]: {}", callbacks);
        }

        try
        {
            this.handler.handle(callbacks);

            if (DEBUG)
            {
                DEBUGGER.debug("CallbackHandler: {}", this.handler);
            }

            List<Object> userData = authenticator.performLogon(username,
                    ((PasswordCallback) callbacks[1]).getPassword().toString());

            if (DEBUG)
            {
                DEBUGGER.debug("UserData: {}", userData);
            }

            if ((userData != null) && (!(userData.isEmpty())))
            {
                if (((Integer) userData.get(9) >= secConfig.getMaxAttempts()) || ((Boolean) userData.get(12)))
                {
                    // user locked
                    return false;
                }

                this.userAccount = new UserAccount(username);
                this.userAccount.setGuid((String) userData.get(1));
                this.userAccount.setUsername((String) userData.get(2));
                this.userAccount.setGivenName((String) userData.get(3));
                this.userAccount.setSurname((String) userData.get(4));
                this.userAccount.setDisplayName((String) userData.get(5));
                this.userAccount.setEmailAddr((String) userData.get(6));
                this.userAccount.setPagerNumber((String) userData.get(7));
                this.userAccount.setTelephoneNumber((String) userData.get(8));
                this.userAccount.setFailedCount((Integer) userData.get(9));
                this.userAccount.setLastLogin(new Date((Long) userData.get(10)));
                this.userAccount.setExpiryDate((Long) userData.get(11));
                this.userAccount.setSuspended((Boolean) userData.get(12));
                this.userAccount.setOlrSetup((Boolean) userData.get(13));
                this.userAccount.setOlrLocked((Boolean) userData.get(14));

                List<UserGroup> userGroups = new ArrayList<UserGroup>();
                for (String group : (List<String>) userData.get(15))
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Group: {}", group);
                    }

                    List<String> serviceList = svcInfo.listServicesForGroup(group);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<String>: {}", serviceList);
                    }

                    this.userGroups = new UserGroup(group);
                    this.userGroups.setName(group);
                    this.userGroups.setServices(serviceList);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserGroup: {}", this.userGroups);
                    }

                    userGroups.add(this.userGroups);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", this.userAccount);
                }

                // user not already logged in or concurrent auth is allowed
                if (System.currentTimeMillis() >= this.userAccount.getExpiryDate())
                {
                    this.userAccount.setStatus(LoginStatus.EXPIRED);

                    return true;
                }

                this.userAccount.setStatus(LoginStatus.SUCCESS);

                return true;
            }
        }
        catch (AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            throw new LoginException(ax.getMessage());
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new LoginException(iox.getMessage());
        }
        catch (UnsupportedCallbackException ucx)
        {
            ERROR_RECORDER.error(ucx.getMessage(), ucx);

            throw new LoginException(ucx.getMessage());
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new LoginException(sqx.getMessage());
        }

        return isComplete;
    }

    /**
     * TODO: Add in the method description/comments
     *
     * @return
     * @throws LoginException
     * @see javax.security.auth.spi.LoginModule#commit()
     */
    @Override
    public boolean commit() throws LoginException
    {
        final String methodName = LDAPLoginModule.CNAME + "#commit() throws LoginException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.subject.getPrincipals().add(this.userAccount);
        this.subject.getPrincipals().add(this.userGroups);

        return true;
    }

    /**
     * TODO: Add in the method description/comments
     *
     * @return
     * @throws LoginException
     * @see javax.security.auth.spi.LoginModule#abort()
     */
    @Override
    public boolean abort() throws LoginException
    {
        final String methodName = LDAPLoginModule.CNAME + "#abort() throws LoginException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return false;
    }

    /**
     * TODO: Add in the method description/comments
     *
     * @return
     * @throws LoginException
     * @see javax.security.auth.spi.LoginModule#logout()
     */
    @Override
    public boolean logout() throws LoginException
    {
        final String methodName = LDAPLoginModule.CNAME + "#logout() throws LoginException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.subject.getPrincipals().remove(this.userAccount);
        this.subject.getPrincipals().remove(this.userGroups);

        return false;
    }
}
