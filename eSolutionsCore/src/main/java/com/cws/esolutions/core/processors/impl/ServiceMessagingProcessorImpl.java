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
package com.cws.esolutions.core.processors.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.impl
 * File: ServiceMessagingProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;
import org.apache.commons.lang3.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.core.processors.dto.ServiceMessage;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.core.processors.dto.MessagingResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.core.processors.interfaces.IServiceMessagingProcessor;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;
import com.cws.esolutions.security.processors.impl.AccountControlProcessorImpl;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
/**
 * @see com.cws.esolutions.core.processors.interfaces.IServiceMessagingProcessor
 */
public class ServiceMessagingProcessorImpl implements IServiceMessagingProcessor
{
    /**
     * @see com.cws.esolutions.core.processors.interfaces.IServiceMessagingProcessor#addNewMessage(com.cws.esolutions.core.processors.dto.MessagingRequest)
     */
    public MessagingResponse addNewMessage(final MessagingRequest request) throws MessagingServiceException
    {
        final String methodName = IServiceMessagingProcessor.CNAME + "#addNewMessage(final MessagingRequest request) throws MessagingServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MessagingRequest: {}", request);
        }

        MessagingResponse response = new MessagingResponse();

        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final ServiceMessage message = request.getServiceMessage();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("ServiceMessage: {}", message);
        }

        try
        {
            String messageId = RandomStringUtils.randomAlphanumeric(appConfig.getMessageIdLength());

            if (DEBUG)
            {
                DEBUGGER.debug("messageId: {}", messageId);
            }

            List<Object> messageList = new ArrayList<Object>(
                    Arrays.asList(
                            messageId,
                            message.getMessageTitle(),
                            message.getMessageText(),
                            userAccount.getGuid(),
                            message.getIsActive(),
                            message.isAlert(),
                            message.getDoesExpire(),
                            message.getExpiryDate()));

            // submit it
            boolean isSubmitted = webMessengerDAO.insertMessage(messageList);

            if (DEBUG)
            {
                DEBUGGER.debug("isSubmitted: {}", isSubmitted);
            }

            if (!(isSubmitted))
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                response.setMessageId(messageId);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingResponse: {}", response);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new MessagingServiceException(sqx.getMessage(), sqx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.ADDSVCMESSAGE);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuthorized(Boolean.TRUE);
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
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IServiceMessagingProcessor#updateExistingMessage(com.cws.esolutions.core.processors.dto.MessagingRequest)
     */
    public MessagingResponse updateExistingMessage(final MessagingRequest request) throws MessagingServiceException
    {
        final String methodName = IServiceMessagingProcessor.CNAME + "#updateExistingMessage(final MessagingRequest request) throws MessagingServiceException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MessagingRequest: {}", request);
        }
        
        MessagingResponse response = new MessagingResponse();

        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final ServiceMessage message = request.getServiceMessage();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("ServiceMessage: {}", message);
        }

        try
        {
            List<Object> messageList = new ArrayList<Object>(
                    Arrays.asList(
                            message.getMessageTitle(),
                            message.getMessageText(),
                            message.getIsActive(),
                            message.isAlert(),
                            message.getDoesExpire(),
                            message.getExpiryDate(),
                            userAccount.getUsername()));

            // submit it
            boolean isUpdated = webMessengerDAO.updateMessage(message.getMessageId(), messageList);

            if (DEBUG)
            {
                DEBUGGER.debug("isUpdated: {}", isUpdated);
            }

            if (isUpdated)
            {
                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                response.setMessageId(message.getMessageId());
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingResponse: {}", response);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingResponse: {}", response);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new MessagingServiceException(sqx.getMessage(), sqx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.EDITSVCMESSAGE);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuthorized(Boolean.TRUE);
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
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IServiceMessagingProcessor#showMessages(com.cws.esolutions.core.processors.dto.MessagingRequest)
     */
    public MessagingResponse showMessages(final MessagingRequest request) throws MessagingServiceException
    {
        final String methodName = IServiceMessagingProcessor.CNAME + "#showMessages(final MessagingRequest request) throws MessagingServiceException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MessagingRequest: {}", request);
        }

        MessagingResponse response = new MessagingResponse();

        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final IAccountControlProcessor acctControl = new AccountControlProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            List<Object[]> data = webMessengerDAO.retrieveMessages();

            if (DEBUG)
            {
                DEBUGGER.debug("data: {}", data);
            }

            if ((data == null) || (data.isEmpty()))
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);

                return response;
            }

            List<ServiceMessage> svcMessages = new ArrayList<ServiceMessage>();

            for (Object[] object : data)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("Object: {}", object);
                }

                ServiceMessage message = new ServiceMessage();
                message.setMessageId((String) object[0]); // svc_message_id
                message.setMessageTitle((String) object[1]); // svc_message_title
                message.setMessageText((String) object[2]); // svc_message_txt
                message.setSubmitDate((Date) object[4]); // svc_message_submitdate
                message.setIsActive((Boolean) object[5]); // svc_message_active
                message.setIsAlert((Boolean) object[6]); // svc_message_active
                message.setDoesExpire((Boolean) object[7]); //svc_message_expires
                message.setExpiryDate((Date) object[8]); // svc_message_expirydate

                UserAccount searchAccount = new UserAccount();
                searchAccount.setGuid((String) object[3]);

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", searchAccount);
                }

                UserAccount svcAccount = new UserAccount();
                svcAccount.setUsername(serviceAccount.getAccountName());
                svcAccount.setGuid(serviceAccount.getAccountGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", svcAccount);
                }

                AccountControlRequest searchRequest = new AccountControlRequest();
                searchRequest.setHostInfo(request.getRequestInfo());
                searchRequest.setUserAccount(searchAccount);
                searchRequest.setApplicationName(request.getApplicationName());
                searchRequest.setApplicationId(request.getApplicationId());
                searchRequest.setRequestor(svcAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", searchRequest);
                }

                try
                {
                    AccountControlResponse searchResponse = acctControl.loadUserAccount(searchRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AccountControlResponse: {}", searchResponse);
                    }

                    if (searchResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                    {
                        message.setMessageAuthor(searchResponse.getUserAccount()); // svc_message_author
                    }
                }
                catch (final AccountControlException acx)
                {
                    ERROR_RECORDER.error(acx.getMessage(), acx);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceMessage: {}", message);
                }

                svcMessages.add(message);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("List<ServiceMessage>: {}", svcMessages);
            }

            response.setRequestStatus(CoreServicesStatus.SUCCESS);
            response.setSvcMessages(svcMessages);

            if (DEBUG)
            {
                DEBUGGER.debug("MessageResponse: {}", response);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new MessagingServiceException(sqx.getMessage(), sqx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.SHOWMESSAGES);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuthorized(Boolean.TRUE);
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
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IServiceMessagingProcessor#showAlertMessages(com.cws.esolutions.core.processors.dto.MessagingRequest)
     */
    public MessagingResponse showAlertMessages(final MessagingRequest request) throws MessagingServiceException
    {
        final String methodName = IServiceMessagingProcessor.CNAME + "#showAlertMessages(final MessagingRequest request) throws MessagingServiceException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MessagingRequest: {}", request);
        }

        MessagingResponse response = new MessagingResponse();

        try
        {
            List<Object[]> data = webMessengerDAO.retrieveAlertMessages();

            if (DEBUG)
            {
                DEBUGGER.debug("data: {}", data);
            }

            if ((data == null) || (data.isEmpty()))
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);

                return response;
            }

            List<ServiceMessage> svcMessages = new ArrayList<ServiceMessage>();

            for (Object[] object : data)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("Object: {}", object);
                }

                ServiceMessage message = new ServiceMessage();
                message.setMessageTitle((String) object[1]); // svc_message_title
                message.setMessageText((String) object[2]); // svc_message_txt

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceMessage: {}", message);
                }

                svcMessages.add(message);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("List<ServiceMessage>: {}", svcMessages);
            }

            response.setRequestStatus(CoreServicesStatus.SUCCESS);
            response.setSvcMessages(svcMessages);

            if (DEBUG)
            {
                DEBUGGER.debug("MessageResponse: {}", response);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new MessagingServiceException(sqx.getMessage(), sqx);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IServiceMessagingProcessor#showMessage(com.cws.esolutions.core.processors.dto.MessagingRequest)
     */
    public MessagingResponse showMessage(final MessagingRequest request) throws MessagingServiceException
    {
        final String methodName = IServiceMessagingProcessor.CNAME + "#showMessage(final MessagingRequest request) throws MessagingServiceException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MessagingRequest: {}", request);
        }
        
        MessagingResponse response = new MessagingResponse();

        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final ServiceMessage reqMessage = request.getServiceMessage();
        final IAccountControlProcessor acctControl = new AccountControlProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("Message: {}", reqMessage);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            List<Object> responseList = webMessengerDAO.retrieveMessage(reqMessage.getMessageId());

            if (DEBUG)
            {
                DEBUGGER.debug("Response list: {}", responseList);
            }

            if ((responseList == null) || (responseList.isEmpty()))
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);

                return response;
            }

            ServiceMessage message = new ServiceMessage();
            message.setMessageId((String) responseList.get(0)); // svc_message_id
            message.setMessageTitle((String) responseList.get(1)); // svc_message_title
            message.setMessageText((String) responseList.get(2)); // svc_message_txt
            message.setSubmitDate((Date) responseList.get(4)); // svc_message_submitdate
            message.setIsActive((Boolean) responseList.get(5)); // svc_message_active
            message.setIsAlert((Boolean) responseList.get(6)); // svc_message_active
            message.setDoesExpire((Boolean) responseList.get(7)); //svc_message_expires
            message.setExpiryDate((Date) responseList.get(8)); // svc_message_expirydate

            UserAccount searchAccount = new UserAccount();
            searchAccount.setGuid((String) responseList.get(3));

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", searchAccount);
            }

            UserAccount svcAccount = new UserAccount();
            svcAccount.setUsername(serviceAccount.getAccountName());
            svcAccount.setGuid(serviceAccount.getAccountGuid());

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", svcAccount);
            }

            AccountControlRequest searchRequest = new AccountControlRequest();
            searchRequest.setHostInfo(request.getRequestInfo());
            searchRequest.setUserAccount(searchAccount);
            searchRequest.setApplicationName(request.getApplicationName());
            searchRequest.setApplicationId(request.getApplicationId());
            searchRequest.setRequestor(svcAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountControlRequest: {}", searchRequest);
            }

            try
            {
                AccountControlResponse searchResponse = acctControl.loadUserAccount(searchRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlResponse: {}", searchResponse);
                }

                if (searchResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    message.setMessageAuthor(searchResponse.getUserAccount()); // svc_message_author
                }
            }
            catch (final AccountControlException acx)
            {
                ERROR_RECORDER.error(acx.getMessage(), acx);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("ServiceMessage: {}", message);
            }

            response.setRequestStatus(CoreServicesStatus.SUCCESS);
            response.setServiceMessage(message);

            if (DEBUG)
            {
                DEBUGGER.debug("MessageResponse: {}", response);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingResponse: {}", response);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new MessagingServiceException(sqx.getMessage(), sqx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LOADMESSAGE);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuthorized(Boolean.TRUE);
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
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }
}
