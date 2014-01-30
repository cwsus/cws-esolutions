/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
package com.cws.esolutions.security.processors.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.impl
 * File: AccountResetProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Calendar;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.processors.dto.AccountResetRequest;
import com.cws.esolutions.security.processors.dto.AccountResetResponse;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.security.processors.exception.AccountResetException;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
import com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * @see com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor
 */
public class AccountResetProcessorImpl implements IAccountResetProcessor
{
    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor#verifyResetRequest(com.cws.esolutions.security.processors.dto.AccountResetRequest)
     */
    @Override
    public AccountResetResponse verifyResetRequest(final AccountResetRequest request) throws AccountResetException
    {
        final String methodName = IAccountResetProcessor.CNAME + "#verifyResetRequest(final AccountResetRequest request) throws AccountResetException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthenticationRequest: {}", request);
        }

        AccountResetResponse response = new AccountResetResponse();

        final Calendar cal = Calendar.getInstance();
        final RequestHostInfo reqInfo = request.getHostInfo();
        final AuthenticationData userSecurity = request.getUserSecurity();
                
        if (DEBUG)
        {
            DEBUGGER.debug("Calendar: {}", cal);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }
        
        try
        {
            cal.add(Calendar.MINUTE, secConfig.getResetTimeout());

            if (DEBUG)
            {
                DEBUGGER.debug("Reset expiry: {}", cal.getTimeInMillis());
            }

            // the request id should be in here, so lets make sure it exists
            List<String> resetData = userSec.getResetData(userSecurity.getResetRequestId());

            final String commonName = resetData.get(0);
            final Long resetTimestamp = Long.valueOf(resetData.get(1));

            if (DEBUG)
            {
                DEBUGGER.debug("commonName: {}", commonName);
                DEBUGGER.debug("resetTimestamp: {}", resetTimestamp);
            }

            // make sure the timestamp is appropriate
            if (resetTimestamp <= cal.getTimeInMillis())
            {
                if (StringUtils.isNotEmpty(commonName))
                {
                    // good, now we have something we can look for
                    List<String[]> userList = userManager.searchUsers(commonName);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("userList: {}", userList);
                    }

                    // we expect back only one
                    if ((userList != null) && (userList.size() == 1))
                    {
                        // good, we can continue
                        Object[] userData = userList.get(0);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("userData: {}", userData);
                        }

                        UserAccount userAccount = new UserAccount();
                        userAccount.setStatus(LoginStatus.RESET);
                        userAccount.setGuid((String) userData[0]);
                        userAccount.setUsername((String) userData[1]);
                        userAccount.setGivenName((String) userData[2]);
                        userAccount.setSurname((String) userData[3]);
                        userAccount.setDisplayName((String) userData[4]);
                        userAccount.setEmailAddr((String) userData[5]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserAccount: {}", userAccount);
                        }

                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                        response.setUserAccount(userAccount);
                    }
                    else
                    {
                        throw new AuthenticationException("Multiple user accounts were located for the provided information");
                    }
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("AuthenticationResponse: {}", response);
                }
            }
            else
            {
                throw new AuthenticationException("Reset request has expired.");
            }
        }
        catch (SecurityServiceException ssx)
        {
            ERROR_RECORDER.error(ssx.getMessage(), ssx);

            throw new AccountResetException(ssx.getMessage(), ssx);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AccountResetException(sqx.getMessage(), sqx);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor#resetUserPassword(com.cws.esolutions.security.processors.dto.AccountResetRequest)
     */
    @Override
    public AccountResetResponse resetUserPassword(final AccountResetRequest request) throws AccountResetException
    {
        final String methodName = IAccountResetProcessor.CNAME + "#resetUserPassword(final AccountResetRequest request) throws AccountResetException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountResetRequest: {}", request);
        }

        AccountResetResponse response = new AccountResetResponse();

        final Calendar calendar = Calendar.getInstance();
        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount reqAccount = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();

        calendar.add(Calendar.DATE, secConfig.getPasswordExpiration());

        if (DEBUG)
        {
            DEBUGGER.debug("Calendar: {}", calendar);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", reqAccount);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            if (!(StringUtils.equals(userAccount.getGuid(), reqAccount.getGuid())))
            {
                response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);

                return response;
            }

            String resetId = RandomStringUtils.randomAlphanumeric(secConfig.getResetIdLength());
            String smsReset = RandomStringUtils.randomAlphanumeric(secConfig.getSmsCodeLength());

            if (StringUtils.isNotEmpty(resetId))
            {
                boolean isComplete = userSec.insertResetData(userAccount.getGuid(), resetId, ((secConfig.getSmsResetEnabled()) ? smsReset : null));

                if (DEBUG)
                {
                    DEBUGGER.debug("isComplete: {}", isComplete);
                }

                if (isComplete)
                {
                    // load the user account for the email response
                    List<Object> userData = userManager.loadUserAccount(userAccount.getGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserData: {}", userData);
                    }

                    if ((userData != null) && (!(userData.isEmpty())))
                    {
                        UserAccount responseAccount = new UserAccount();
                        responseAccount.setGuid((String) userData.get(0));
                        responseAccount.setUsername((String) userData.get(1));
                        responseAccount.setGivenName((String) userData.get(2));
                        responseAccount.setSurname((String) userData.get(3));
                        responseAccount.setDisplayName((String) userData.get(4));
                        responseAccount.setEmailAddr((String) userData.get(5));
                        responseAccount.setPagerNumber((String) userData.get(6));
                        responseAccount.setTelephoneNumber((String) userData.get(7));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserAccount: {}", responseAccount);
                        }

                        response.setResetId(resetId);
                        response.setSmsCode(((secConfig.getSmsResetEnabled()) ? smsReset : null));
                        response.setUserAccount(responseAccount);
                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    }
                    else
                    {
                        ERROR_RECORDER.error("Failed to locate user account in authentication repository. Cannot continue.");

                        response.setRequestStatus(SecurityRequestStatus.FAILURE);
                    }
                }
                else
                {
                    ERROR_RECORDER.error("Unable to insert password identifier into database. Cannot continue.");

                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                }
            }
            else
            {
                ERROR_RECORDER.error("Unable to generate a unique identifier. Cannot continue.");

                response.setRequestStatus(SecurityRequestStatus.FAILURE);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);
            
            throw new AccountResetException(sqx.getMessage(), sqx);
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountResetException(umx.getMessage(), umx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.RESETPASS);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor#getSecurityQuestions(com.cws.esolutions.security.processors.dto.AccountResetRequest)
     */
    @Override
    public AccountResetResponse getSecurityQuestions(final AccountResetRequest request) throws AccountResetException
    {
        final String methodName = IAccountResetProcessor.CNAME + "#getSecurityQuestions(final AccountResetRequest request) throws AccountResetException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountResetRequest: {}", request);
        }

        AccountResetResponse response = new AccountResetResponse();

        final UserAccount reqUser = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getHostInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", reqUser);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            List<String> questionList = secRef.obtainSecurityQuestionList();

            if (DEBUG)
            {
                DEBUGGER.debug("questionList: {}", questionList);
            }

            response.setQuestionList(questionList);
            response.setRequestStatus(SecurityRequestStatus.SUCCESS);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountResetResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);
            
            throw new AccountResetException(sqx.getMessage(), sqx);
        }

        return response;
    }
}
