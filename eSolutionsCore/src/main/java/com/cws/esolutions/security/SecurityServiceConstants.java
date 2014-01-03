/*
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
package com.cws.esolutions.security;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security
 * File: SecurityServiceConstants.java
 *
 * History
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 * Kevin Huntly         12/05/2008 13:36:09             Added method to process change requests
 */
public class SecurityServiceConstants
{
    // defaults
    public static final String SYSADM = "SYSADM";
    public static final int DEF_SALT_LENGTH = 64;
    public static final String DEF_AUTH_REPO = "ldap";
    public static final int DEF_AUTH_MAX_ATTEMPTS = 3;
    public static final int DEF_AUTH_PASS_EXPIRY = 45;
    public static final int DEF_AUTH_PASS_MINLENGTH = 10;
    public static final int DEF_TIMEOUT_WARNING = 840000;
    public static final int DEF_SESSION_TIMEOUT = 900000;
    public static final int DEF_AUTH_PASS_MAXLENGTH = 128;
    public static final boolean DEF_ALLOW_USER_RESET = true;
    public static final String DEF_AUTH_ALGORITHM = "SHA-512";
    public static final boolean DEF_AUTH_ALLOW_CONCURRENT = false;

    /**
     * Logging constants
     */
    public static final String DEBUGGER = "SECURITY_DEBUGGER";
    public static final String INFO_LOGGER = "INFO_RECORDER.";
    public static final String FATAL_LOGGER = "FATAL_RECORDER.";
    public static final String ERROR_LOGGER = "ERROR_RECORDER.";
    public static final String AUDIT_LOGGER = "AUDIT_RECORDER.";
    public static final String WARN_LOGGER = "WARNING_RECORDER.";

    /**
     * Application constants
     */
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String NOT_SET = "Unconfigured";
    public static final String DS_CONTEXT = "java:comp/env";
    public static final String INIT_AUDITDS_MANAGER = "AuditDataSource";
    public static final String INIT_CONFIG_FILE = "SecurityServiceConfig";
    public static final String INIT_SESSIONDS_MANAGER = "SessionDataSource";
    public static final String INIT_SECURITYDS_MANAGER = "SecurityDataSource";
    public static final String LINE_BREAK = System.getProperty("line.separator");
}
