/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
 */
package com.cws.us.esolutions.validators;

import org.slf4j.Logger;
import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.us.esolutions.Constants;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.enums.ServerType;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.validators
 * ServerValidator.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author 35033355
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * 35033355 @ May 16, 2013 8:40:08 AM
 *     Created.
 */
public class ServerValidator implements Validator
{
    private String messageOsNameRequired = null;
    private String messageHostnameRequired = null;
    private String messageServerTypeRequired = null;
    private String messageManagerUrlRequired = null;
    private String messageHostAddressRequired = null;
    private String messageNasHostnameRequired = null;
    private String messageServerStatusRequired = null;
    private String messageServerRegionRequired = null;
    private String messageServerAddressInvalid = null;
    private String messageBackupHostnameRequired = null;
    private String messageNasHostAddressRequired = null;
    private String messageBackupHostAddressRequired = null;
    private String messageManagementHostnameRequired = null;
    private String messageManagementHostAddressRequired = null;

    private static final String CNAME = ServerValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessageOsNameRequired(final String value)
    {
        final String methodName = ServerValidator.CNAME + "#setMessageOsNameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageOsNameRequired = value;
    }

    public final void setMessageHostnameRequired(final String value)
    {
        final String methodName = ServerValidator.CNAME + "#setMessageHostnameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageHostnameRequired = value;
    }

    public final void setMessageHostAddressRequired(final String value)
    {
        final String methodName = ServerValidator.CNAME + "#setMessageHostAddressRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageHostAddressRequired = value;
    }

    public final void setMessageServerTypeRequired(final String value)
    {
        final String methodName = ServerValidator.CNAME + "#setMessageServerTypeRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageServerTypeRequired = value;
    }

    public final void setMessageServerStatusRequired(final String value)
    {
        final String methodName = ServerValidator.CNAME + "#setMessageServerStatusRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageServerStatusRequired = value;
    }

    public final void setMessageServerRegionRequired(final String value)
    {
        final String methodName = ServerValidator.CNAME + "#setMessageServerRegionRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageServerRegionRequired = value;
    }

    public final void setMessageManagementHostnameRequired(final String value)
    {
        final String methodName = ServerValidator.CNAME + "#setMessageManagementHostnameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageManagementHostnameRequired = value;
    }

    public final void setMessageManagementHostAddressRequired(final String value)
    {
        final String methodName = ServerValidator.CNAME + "#setMessageManagementHostAddressRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageManagementHostAddressRequired = value;
    }

    public final void setMessageBackupHostnameRequired(final String value)
    {
        final String methodName = ServerValidator.CNAME + "#setMessageBackupHostnameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageBackupHostnameRequired = value;
    }

    public final void setMessageBackupHostAddressRequired(final String value)
    {
        final String methodName = ServerValidator.CNAME + "#setMessageBackupHostAddressRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageBackupHostAddressRequired = value;
    }

    public final void setMessageNasHostnameRequired(final String value)
    {
        final String methodName = ServerValidator.CNAME + "#setMessageNasHostnameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNasHostnameRequired = value;
    }

    public final void setMessageNasHostAddressRequired(final String value)
    {
        final String methodName = ServerValidator.CNAME + "#setMessageNasHostAddressRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNasHostAddressRequired = value;
    }

    public final void setMessageServerAddressInvalid(final String value)
    {
        final String methodName = ServerValidator.CNAME + "#setMessageServerAddressInvalid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageServerAddressInvalid = value;
    }

    public final void setMessageManagerUrlRequired(final String value)
    {
        final String methodName = ServerValidator.CNAME + "#setMessageManagerUrlRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageManagerUrlRequired = value;
    }

    @Override
    public final boolean supports(final Class<?> value)
    {
        final String methodName = ServerValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }

        final boolean isSupported = Server.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

    @Override
    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = ServerValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "osName", this.messageOsNameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "operHostName", this.messageHostnameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "operIpAddress", this.messageHostAddressRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serverType", this.messageServerTypeRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serverStatus", this.messageServerStatusRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serverRegion", this.messageServerRegionRequired);

        final Server server = (Server) target;
        final Pattern pattern = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", server);
        }

        if ((StringUtils.isNotEmpty(server.getMgmtHostName())) && (StringUtils.isBlank(server.getMgmtIpAddress()))
                || (StringUtils.isNotEmpty(server.getMgmtIpAddress())) && (StringUtils.isBlank(server.getMgmtHostName())))
        {
            errors.reject("mgmtHostName", this.messageManagementHostnameRequired);
            errors.reject("mgmtIpAddress", this.messageManagementHostAddressRequired);
        }
        else if ((StringUtils.isNotEmpty(server.getBkHostName())) && (StringUtils.isBlank(server.getBkIpAddress()))
                || (StringUtils.isNotEmpty(server.getBkIpAddress())) && (StringUtils.isBlank(server.getBkHostName())))
        {
            errors.reject("bkHostName", this.messageBackupHostnameRequired);
            errors.reject("bkIpAddress", this.messageBackupHostAddressRequired);
        }
        else if ((StringUtils.isNotEmpty(server.getNasHostName())) && (StringUtils.isBlank(server.getNasIpAddress()))
                || (StringUtils.isNotEmpty(server.getNasIpAddress())) && (StringUtils.isBlank(server.getNasHostName())))
        {
            errors.reject("nasHostName", this.messageNasHostnameRequired);
            errors.reject("nasIpAddress", this.messageNasHostAddressRequired);
        }
        else if ((StringUtils.isNotEmpty(server.getNatAddress())) && (!(pattern.matcher(server.getNatAddress()).matches())))
        {
            // properly formatted nat addr
            errors.reject("natAddress", this.messageServerAddressInvalid);
        }
        else
        {
            if (!(pattern.matcher(server.getOperIpAddress()).matches()))
            {
                errors.reject("operIpAddress", this.messageServerAddressInvalid);
            }
            else if ((StringUtils.isNotEmpty(server.getMgmtIpAddress())) && (!(pattern.matcher(server.getMgmtIpAddress()).matches())))
            {
                errors.reject("mgmtIpAddress", this.messageServerAddressInvalid);
            }
            else if ((StringUtils.isNotEmpty(server.getBkIpAddress())) && (!(pattern.matcher(server.getBkIpAddress()).matches())))
            {
                errors.reject("bkIpAddress", this.messageServerAddressInvalid);
            }
            else if ((StringUtils.isNotEmpty(server.getNasIpAddress())) && (!(pattern.matcher(server.getNasIpAddress()).matches())))
            {
                errors.reject("nasIpAddress", this.messageServerAddressInvalid);
            }
            else if ((server.getServerType() == ServerType.VIRTUALHOST) || (server.getServerType() == ServerType.DMGRSERVER) && (StringUtils.isEmpty(server.getMgrUrl())))
            {
                errors.reject("vboxManagerUrl", this.messageManagerUrlRequired);
            }
            else if ((server.getServerType() == ServerType.DMGRSERVER) && (server.getDmgrPort() == 0))
            {
                errors.reject("vboxManagerUrl", this.messageManagerUrlRequired);
            }
        }
    }
}
