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
package com.cws.esolutions.web.ws.interfaces;
/*
 * Project: eSolutions_web_source
 * Package: com.cws.esolutions.web.webservices.interfaces
 * File: IQuoteService.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import javax.jws.WebMethod;
import javax.jws.WebService;
import org.slf4j.LoggerFactory;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.cws.esolutions.web.Constants;
/**
 * API for the webservice that we havent done a damn thing with yet
 * and hope it works without any testing lol
 *
 * @author cws-khuntly
 * @version 1.0
 */
@WebService
@SOAPBinding(style = Style.DOCUMENT)
public interface IQuoteService
{
    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Inserts audit-related data into the audit datastore
     *
     * @param auditRequest - A <code>List</code> of the audit data to insert
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
     */
    @WebMethod
    String getQuote();
}
