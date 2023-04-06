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
package com.cws.esolutions.core.exception;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.exception
 * File: CoreServicesException.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @see java.lang.Exception
 * @author cws-khuntly
 * @version 1.0
 */
public class CoreServicesException extends Exception
{
    private static final long serialVersionUID = 3174420227519108655L;

	/**
     * @see java.lang.Exception#Exception(java.lang.String)
     *
     * @param message - The message for the exception
     */
    public CoreServicesException(final String message)
    {
        super(message);
    }

    /**
     * @see java.lang.Exception#Exception(java.lang.Throwable)
     *
     * @param throwable - The throwable for the exception
     */
    public CoreServicesException(final Throwable throwable)
    {
        super(throwable);
    }

    /**
     * @see java.lang.Exception#Exception(java.lang.String, java.lang.Throwable)
     *
     * @param message - The message for the exception
     * @param throwable - The throwable for the exception
     */
    public CoreServicesException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }
}
