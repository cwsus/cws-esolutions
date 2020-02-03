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
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.processors.interfaces
 * File: IFileManagerProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.cws.esolutions.core.processors.dto.FileManagerRequest;
import com.cws.esolutions.core.processors.dto.FileManagerResponse;
import com.cws.esolutions.core.processors.exception.FileManagerException;
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
public interface IFileManagerProcessor
{
    static final byte buffer[] = new byte[1024];
    static final String CNAME = IFileManagerProcessor.class.getName();

    @WebMethod
    FileManagerResponse retrieveFile(final FileManagerRequest request) throws FileManagerException;

    @WebMethod
    FileManagerResponse deployFile(final FileManagerRequest request) throws FileManagerException;

    @WebMethod
    FileManagerResponse deleteFile(final FileManagerRequest request) throws FileManagerException;
}
