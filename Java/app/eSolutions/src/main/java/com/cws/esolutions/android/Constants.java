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
package com.cws.esolutions.android;
/*
 * eSolutions
 * com.cws.esolutions.android
 * Constants.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public final class Constants
{
    /*
     * Application constants
     */
    public static final String USER_DATA = "userAccount";
    public static final String APPLICATION_NAME = "eSolutions";
    public static final String LINE_BREAK = System.getProperty("line.separator");
    public static final String APPLICATION_ID = "5446d6f1-cab8-439f-9bc7-f72141cc81bc";

    // repo config
    public static final String REPO_TYPE = "repoType";
    public static final String IS_SECURE = "isSecure";
    public static final String TRUST_FILE= "trustStoreFile";
    public static final String TRUST_PASS = "trustStorePass";
    public static final String TRUST_TYPE = "trustStoreType";
    public static final String CONN_DRIVER = "repositoryDriver";
    public static final String REPOSITORY_HOST = "repositoryHost";
    public static final String REPOSITORY_PORT = "repositoryPort";
    public static final String MIN_CONNECTIONS = "minConnections";
    public static final String MAX_CONNECTIONS = "maxConnections";
    public static final String REPOSITORY_USER = "repositoryUser";
    public static final String REPOSITORY_PASS = "repositoryPass";
    public static final String REPOSITORY_SALT = "repositorySalt";
    public static final String CONN_TIMEOUT = "repositoryConnTimeout";
    public static final String READ_TIMEOUT = "repositoryReadTimeout";

    /* Logging constants */
    public static final String DEBUGGER = "DEBUGGER";
    public static final String ERROR_LOGGER = "ERROR_RECORDER.";
}
