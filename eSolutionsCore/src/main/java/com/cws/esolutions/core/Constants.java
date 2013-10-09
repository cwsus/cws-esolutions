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
package com.cws.esolutions.core;
/**
 * eSolutionsCore
 * com.cws.esolutions.core
 * Constants.java
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
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public class Constants
{
    /* Logging constants */
    public static final String DEBUGGER = "CORE_DEBUGGER";
    public static final String WARN_LOGGER = "WARN_RECORDER.";
    public static final String INFO_LOGGER = "INFO_RECORDER.";
    public static final String AUDIT_LOGGER = "AUDIT_RECORDER.";
    public static final String ERROR_LOGGER = "ERROR_RECORDER.";

    /* Configuration constants */
    public static final String NOT_SET = "Unconfigured";
    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
    public static final String LINE_BREAK = System.getProperty("line.separator");
}
