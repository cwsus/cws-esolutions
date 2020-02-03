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
package com.cws.esolutions.core;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core
 * File: CoreServicesConstants.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
/**
 * @author cws-khuntly
 * @version 1.0
 */
public class CoreServicesConstants
{
    /* Logging constants */
    public static final String DEBUGGER = "CORE_DEBUGGER";
    public static final String ERROR_LOGGER = "ERROR_RECORDER.";

    /* Configuration constants */
    public static final String SALT = "userSalt";
    public static final String KEYFILE = "userKeyFile";
    public static final String ACCOUNT = "userAccount";
    public static final String NOT_SET = "Unconfigured";
    public static final String PASSWORD = "userPassword";
    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
    public static final String LINE_BREAK = System.getProperty("line.separator");
}
