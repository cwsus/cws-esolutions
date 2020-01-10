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
package com.cws.esolutions.web.webservices.interfaces;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.webservices.interfaces
 * File: IServiceCheckProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import javax.jws.WebMethod;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.agent.AgentConstants;
import com.cws.esolutions.agent.processors.dto.ServiceCheckRequest;
import com.cws.esolutions.agent.processors.dto.ServiceCheckResponse;
import com.cws.esolutions.agent.processors.exception.ServiceCheckException;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author cws-khuntly
 * @version 1.0
 */
@WebService
@SOAPBinding(style = Style.DOCUMENT)
public interface IServiceCheckProcessor
{
    static final byte buffer[] = new byte[1024];
    static final String CNAME = IServiceCheckProcessor.class.getName();

    static final Logger DEBUGGER = LoggerFactory.getLogger(AgentConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(AgentConstants.ERROR_LOGGER + CNAME);

    @WebMethod
    ServiceCheckResponse runSystemCheck(final ServiceCheckRequest request) throws ServiceCheckException;
}
