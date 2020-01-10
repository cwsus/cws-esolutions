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
package com.cws.esolutions.web;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web
 * File: Constants.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   		11/23/2008 22:39:20             Created.
 */
/**
 * @author cws-khuntly
 * @version 1.0
 */
public class Constants
{
    public static final String INFO_LOGGER = "INFO_RECORDER.";
    public static final String ERROR_LOGGER = "ERROR_RECORDER.";
    public static final String AUDIT_LOGGER = "AUDIT_RECORDER.";
    public static final String DEBUGGER = "ESOLUTIONS_DEBUGGER";

    public static final String COMMAND = "command";
    public static final String BIND_RESULT = "bindResult";
    public static final String USER_ACCOUNT = "userAccount";
    public static final String USER_SECURITY = "userSecurity";
    public static final String ALLOW_RESET = "allowUserReset";
    public static final String ERROR_MESSAGE = "errorMessage"; // returned from controllers
    public static final String ERROR_RESPONSE = "errorResponse"; // returned from the getResponse() method on DTO objects from esol core
    public static final String SEARCH_RESULTS = "searchResults";
    public static final String RESPONSE_MESSAGE = "responseMessage"; // returned from controllers
    public static final String MESSAGE_RESPONSE = "messageResponse"; // returned from the getResponse() method on DTO objects from esol core
    public static final String LINE_BREAK = System.getProperty("line.separator");
}
