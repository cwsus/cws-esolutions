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
 *
 */
package com.cws.esolutions.web.validators;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.validators
 * File: ServiceValidator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.core.processors.dto.Service;
import com.cws.esolutions.core.processors.enums.ServiceType;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.validation.Validator
 */
public class ServiceValidator implements Validator
{
    private String messageNameRequired = null;
    private String messageTypeRequired = null;
    private String messageStatusRequired = null;
    private String messageRegionRequired = null;
    private String messageServersRequired = null;
    private String messagePartitionRequired = null;
    private String messageDescriptionRequired = null;

    private static final String CNAME = ServiceValidator.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessageNameRequired(final String value)
    {
        final String methodName = ServiceValidator.CNAME + "#setMessageNameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNameRequired = value;
    }

    public final void setMessageStatusRequired(final String value)
    {
        final String methodName = ServiceValidator.CNAME + "#setMessageStatusRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageStatusRequired = value;
    }

    public final void setMessageDescriptionRequired(final String value)
    {
        final String methodName = ServiceValidator.CNAME + "#setMessageDescriptionRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageDescriptionRequired = value;
    }

    public final void setMessageRegionRequired(final String value)
    {
        final String methodName = ServiceValidator.CNAME + "#setMessageRegionRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageRegionRequired = value;
    }

    public final void setMessageServersRequired(final String value)
    {
        final String methodName = ServiceValidator.CNAME + "#setMessageServersRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageServersRequired = value;
    }

    public final void setMessagePartitionRequired(final String value)
    {
        final String methodName = ServiceValidator.CNAME + "#setMessagePartitionRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePartitionRequired = value;
    }

    public final void setMessageTypeRequired(final String value)
    {
        final String methodName = ServiceValidator.CNAME + "#setMessageTypeRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageTypeRequired = value;
    }

    public final boolean supports(final Class<?> value)
    {
        final String methodName = ServiceValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        final boolean isSupported = Service.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", value);
        }

        return isSupported;
    }

    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = ServiceValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        final Service service = (Service) target;

        if (DEBUG)
        {
            DEBUGGER.debug("Service: {}", service);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", this.messageNameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", this.messageTypeRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", this.messageDescriptionRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "status", this.messageStatusRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "region", this.messageRegionRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "partition", this.messagePartitionRequired);

        if ((service.getType() == ServiceType.PLATFORM) && ((service.getServers() == null) || (service.getServers().size() == 0)))
        {
            errors.rejectValue("servers", this.messageServersRequired);
        }
    }
}
