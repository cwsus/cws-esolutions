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
package com.cws.esolutions.core.processors.interfaces;

import org.slf4j.Logger;
import javax.jws.WebMethod;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.config.ApplicationConfig;
import com.cws.esolutions.core.processors.dto.SearchRequest;
import com.cws.esolutions.core.processors.dto.SearchResponse;
import com.cws.esolutions.core.processors.exception.SearchRequestException;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.interfaces
 * ISearchProcessor.java
 *
 *
 *
 * $Id: ISearchProcessor.java 2287 2013-01-03 20:52:22Z kmhuntly@gmail.com $
 * $Author: $
 * $Date: 2013-01-03 15:52:22 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2287 $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public interface ISearchProcessor
{
    static final String CNAME = ISearchProcessor.class.getName();
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();

    static final Logger WARN_RECORDER = LoggerFactory.getLogger(Constants.WARN_LOGGER + CNAME);
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    @WebMethod(operationName = "doApplicationSearch")
    SearchResponse doApplicationSearch(final SearchRequest request) throws SearchRequestException;

    @WebMethod(operationName = "doProjectSearch")
    SearchResponse doProjectSearch(final SearchRequest request) throws SearchRequestException;

    @WebMethod(operationName = "doMessageSearch")
    SearchResponse doMessageSearch(final SearchRequest request) throws SearchRequestException;

    @WebMethod(operationName = "doArticleSearch")
    SearchResponse doArticleSearch(final SearchRequest request) throws SearchRequestException;

    @WebMethod(operationName = "doServerSearch")
    SearchResponse doServerSearch(final SearchRequest request) throws SearchRequestException;
}
