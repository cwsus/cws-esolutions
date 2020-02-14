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
package com.cws.esolutions.web.processors.interfaces;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.interfaces
 * File: IMessagingProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.core.processors.dto.MessagingResponse;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;
/**
 * @author khuntly
 * @version 1.0
 */
public interface IMessagingProcessor
{
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final String CNAME = IMessagingProcessor.class.getName();

    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    MessagingResponse addNewMessage(final MessagingRequest request) throws MessagingServiceException;

    MessagingResponse updateExistingMessage(final MessagingRequest request) throws MessagingServiceException;

    MessagingResponse showAlertMessages(final MessagingRequest request) throws MessagingServiceException;

    MessagingResponse showMessages(final MessagingRequest request) throws MessagingServiceException;

    MessagingResponse showMessage(final MessagingRequest request) throws MessagingServiceException;
}
