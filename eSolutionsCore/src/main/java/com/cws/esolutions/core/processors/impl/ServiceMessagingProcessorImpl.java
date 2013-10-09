/**
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
package com.cws.esolutions.core.processors.impl;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ArrayList;
import java.sql.SQLException;
import javax.mail.MessagingException;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.core.utils.EmailUtils;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.core.processors.dto.EmailMessage;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.dto.ServiceMessage;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.core.processors.dto.MessagingResponse;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.dao.processors.interfaces.IMessagingDAO;
import com.cws.esolutions.core.processors.interfaces.IMessagingProcessor;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.core.dao.processors.impl.ServiceMessagingDAOImpl;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.impl
 * ServiceMessagingProcessorImpl.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * 35033355 @ Apr 29, 2013 1:32:21 PM
 *     Created.
 */
public class ServiceMessagingProcessorImpl implements IMessagingProcessor
{
    private static final IMessagingDAO messageDAO = new ServiceMessagingDAOImpl();

    @Override
    public MessagingResponse addNewMessage(final MessagingRequest request) throws MessagingServiceException
    {
        final String methodName = IMessagingProcessor.CNAME + "#addNewMessage(final MessagingRequest request) throws MessagingServiceException";

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
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                @SuppressWarnings("unchecked")
                List<Object> messageList = new ArrayList<Object>(
                        Arrays.asList(
                                message.getMessageId(),
                                message.getMessageTitle(),
                                message.getMessageText(),
                                userAccount.getUsername(),
                                userAccount.getEmailAddr(),
                                message.getExpiryDate()));

                if (DEBUG)
                {
                    DEBUGGER.debug("messageList: {}", messageList);
                }

                // submit it
                boolean isSubmitted = messageDAO.insertMessage(messageList);

                if (DEBUG)
                {
                    DEBUGGER.debug("isSubmitted: {}", isSubmitted);
                }

                if (!(isSubmitted))
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                    response.setResponse("Failed to submit message into the datastore.");
                }
                else
                {
                    // send an email to the requestor with the information
                    // re-structure the subject just a bit
                    final String emailMessageId = RandomStringUtils.randomAlphanumeric(appConfig.getMessageIdLength());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("emailMessageId: {}", emailMessageId);
                    }

                    EmailMessage emailMessage = new EmailMessage();
                    emailMessage.setIsAlert(false);
                    emailMessage.setMessageDate(Calendar.getInstance().getTime());
                    emailMessage.setMessageFrom(new ArrayList<String>(Arrays.asList(appConfig.getEmailAliasId())));
                    emailMessage.setMessageId(emailMessageId);
                    emailMessage.setMessageSubject("[ " + emailMessageId + "] - Service message submission");
                    emailMessage.setMessageTo(new ArrayList<String>(Arrays.asList(userAccount.getEmailAddr())));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("EmailMessage: {}", message);
                    }

                    try
                    {
                        EmailUtils.sendEmailMessage(emailMessage);
                    }
                    catch (MessagingException mx)
                    {
                        ERROR_RECORDER.error(mx.getMessage(), mx);
                    }

                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setMessageId(message.getMessageId());
                    response.setResponse("Successfully inserted contact message into datastore.");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("MessagingResponse: {}", response);
                    }
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
                response.setResponse("The requested user was not authorized to perform the operation");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new MessagingServiceException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new MessagingServiceException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setReqInfo(reqInfo);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuditType(AuditType.ADDSVCMESSAGE);
                auditEntry.setAuditDate(System.currentTimeMillis());

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

    @Override
    public MessagingResponse updateExistingMessage(final MessagingRequest request) throws MessagingServiceException
    {
        final String methodName = IMessagingProcessor.CNAME + "#updateExistingMessage(final MessagingRequest request) throws MessagingServiceException";
        
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
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                @SuppressWarnings("unchecked")
                List<Object> messageList = new ArrayList<Object>(
                        Arrays.asList(
                                message.getMessageId(),
                                message.getMessageTitle(),
                                message.getMessageText(),
                                userAccount.getUsername(),
                                userAccount.getEmailAddr(),
                                message.getExpiryDate()));

                if (DEBUG)
                {
                    DEBUGGER.debug("messageList: {}", messageList);
                }

                // submit it
                boolean isUpdated = messageDAO.updateMessage(message.getMessageId(), messageList);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUpdated: {}", isUpdated);
                }

                if (isUpdated)
                {
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setMessageId(message.getMessageId());
                    response.setResponse("Successfully inserted contact message into datastore.");
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                    response.setResponse("Failed to submit message into the datastore.");
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("MessagingResponse: {}", response);
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
                response.setResponse("The requested user was not authorized to perform the operation");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new MessagingServiceException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new MessagingServiceException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setReqInfo(reqInfo);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuditType(AuditType.ADDSVCMESSAGE);
                auditEntry.setAuditDate(System.currentTimeMillis());

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

    @Override
    public MessagingResponse showMessages(final MessagingRequest request) throws MessagingServiceException
    {
        final String methodName = IMessagingProcessor.CNAME + "#showMessages(final MessagingRequest request) throws MessagingServiceException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MessagingRequest: {}", request);
        }

        MessagingResponse response = new MessagingResponse();

        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            List<Object[]> data = messageDAO.retrieveMessages();

            if (DEBUG)
            {
                DEBUGGER.debug("data: {}", data);
            }

            if ((data == null) || (data.isEmpty()))
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
                response.setResponse("No messages were located.");
            }
            else
            {
                List<ServiceMessage> svcMessages = new ArrayList<ServiceMessage>();

                for (Object[] object : data)
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Object: {}", object);
                    }

                    ServiceMessage message = new ServiceMessage();
                    message.setMessageId((String) object[0]);
                    message.setMessageTitle((String) object[1]);
                    message.setMessageText((String) object[2]);
                    message.setMessageAuthor((String) object[3]);
                    message.setAuthorEmail((String) object[4]);
                    message.setSubmitDate(new Date((Long) object[5]));
                    message.setExpiryDate(new Date((Long) object[6]));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServiceMessage: {}", message);
                    }

                    svcMessages.add(message);
                }

                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                response.setResponse("Successfully loaded service messages");
                response.setSvcMessages(svcMessages);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("MessageResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new MessagingServiceException(sqx.getMessage(), sqx);
        }

        return response;
    }

    @Override
    public MessagingResponse showMessage(final MessagingRequest request) throws MessagingServiceException
    {
        final String methodName = IMessagingProcessor.CNAME + "#showMessage(final MessagingRequest request) throws MessagingServiceException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MessagingRequest: {}", request);
        }
        
        MessagingResponse response = new MessagingResponse();

        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final ServiceMessage reqMessage = request.getServiceMessage();

        if (DEBUG)
        {
            DEBUGGER.debug("Message: {}", reqMessage);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                List<Object> responseList = messageDAO.retrieveMessage(reqMessage.getMessageId());

                if (DEBUG)
                {
                    DEBUGGER.debug("Response list: {}", responseList);
                }

                if ((responseList == null) || (responseList.isEmpty()))
                {
                    throw new MessagingServiceException("No results were provided. Cannot continue");
                }
                else
                {
                    if ((responseList != null) && (responseList.size() != 0))
                    {
                        ServiceMessage svcMessage = new ServiceMessage();
                        svcMessage.setMessageId((String) responseList.get(0));
                        svcMessage.setMessageTitle((String) responseList.get(1));
                        svcMessage.setMessageText((String) responseList.get(2));
                        svcMessage.setMessageAuthor((String) responseList.get(3));
                        svcMessage.setAuthorEmail((String) responseList.get(4));
                        svcMessage.setSubmitDate(new Date((Long) responseList.get(5)));
                        svcMessage.setExpiryDate(new Date((Long) responseList.get(6)));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServiceMessage: {}", svcMessage);
                        }

                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                        response.setResponse("Successfully retrieved message");
                        response.setServiceMessage(svcMessage);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("MessageResponse: {}", response);
                        }
                    }
                    else
                    {
                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                        response.setResponse("No messages were located.");
                    }
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
                response.setResponse("The requested user was not authorized to perform the operation");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new MessagingServiceException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

            throw new MessagingServiceException(ucsx.getMessage(), ucsx);
        }

        return response;
    }
}
