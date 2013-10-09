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
    private static final String CNAME = ServerValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    @Override
    public boolean supports(final Class<?> target)
    {
        final String methodName = ServerValidator.CNAME + "#supports(final Class<?> target)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Class: ", target);
        }

        return Server.class.isAssignableFrom(target);
    }

    @Override
    public void validate(final Object target, final Errors errors)
    {
        final String methodName = ServerValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "osName", "system.os.name.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "operHostName", "system.oper.name.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "operIpAddress", "system.oper.addr.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serverType", "system.server.type.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serverStatus", "system.server.status.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serverRegion", "system.server.region.required");

        final Server server = (Server) target;
        final Pattern pattern = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", server);
        }

        if ((StringUtils.isNotEmpty(server.getMgmtHostName())) && (StringUtils.isBlank(server.getMgmtIpAddress()))
                || (StringUtils.isNotEmpty(server.getMgmtIpAddress())) && (StringUtils.isBlank(server.getMgmtHostName())))
        {
            ERROR_RECORDER.error("A management hostname or IP address was provided without its counterpart.");

            errors.reject("mgmtHostName", "system.mgmt.host.name.required");
            errors.reject("mgmtIpAddress", "system.mgmt.host.address.required");
        }
        else if ((StringUtils.isNotEmpty(server.getBkHostName())) && (StringUtils.isBlank(server.getBkIpAddress()))
                || (StringUtils.isNotEmpty(server.getBkIpAddress())) && (StringUtils.isBlank(server.getBkHostName())))
        {
            ERROR_RECORDER.error("A backup hostname or IP address was provided without its counterpart.");

            errors.reject("bkHostName", "system.backup.host.name.required");
            errors.reject("bkIpAddress", "system.backup.host.address.required");
        }
        else if ((StringUtils.isNotEmpty(server.getNasHostName())) && (StringUtils.isBlank(server.getNasIpAddress()))
                || (StringUtils.isNotEmpty(server.getNasIpAddress())) && (StringUtils.isBlank(server.getNasHostName())))
        {
            ERROR_RECORDER.error("A NAS hostname or IP address was provided without its counterpart.");

            errors.reject("nasHostName", "system.nas.host.name.required");
            errors.reject("nasIpAddress", "system.nas.host.address.required");
        }
        else if ((StringUtils.isNotEmpty(server.getNatAddress())) && (!(pattern.matcher(server.getNatAddress()).matches())))
        {
            ERROR_RECORDER.error("The NAT address provided is not a valid IP address.");

            // properly formatted nat addr
            errors.reject("natAddress", "system.server.address.invalid");
        }
        else
        {
            if (!(pattern.matcher(server.getOperIpAddress()).matches()))
            {
                ERROR_RECORDER.error("The primary IP address is not a valid IP.");

                errors.reject("operIpAddress", "system.server.address.invalid");
            }
            else if ((StringUtils.isNotEmpty(server.getMgmtIpAddress())) && (!(pattern.matcher(server.getMgmtIpAddress()).matches())))
            {
                ERROR_RECORDER.error("The management IP address is not a valid IP.");

                errors.reject("mgmtIpAddress", "system.server.address.invalid");
            }
            else if ((StringUtils.isNotEmpty(server.getBkIpAddress())) && (!(pattern.matcher(server.getBkIpAddress()).matches())))
            {
                ERROR_RECORDER.error("The backup IP address is not a valid IP.");

                errors.reject("bkIpAddress", "system.server.address.invalid");
            }
            else if ((StringUtils.isNotEmpty(server.getNasIpAddress())) && (!(pattern.matcher(server.getNasIpAddress()).matches())))
            {
                ERROR_RECORDER.error("The NAS IP address is not a valid IP.");

                errors.reject("nasIpAddress", "system.server.address.invalid");
            }
            else if ((server.getServerType() == ServerType.VIRTUALHOST) || (server.getServerType() == ServerType.DMGRSERVER) && (StringUtils.isEmpty(server.getMgrUrl())))
            {
                ERROR_RECORDER.error("Server type specified is " + ServerType.VIRTUALHOST + " but no service URL was provided");

                errors.reject("vboxManagerUrl", "system.vbox.manager.url.required");
            }
            else if ((server.getServerType() == ServerType.DMGRSERVER) && (server.getDmgrPort() == 0))
            {
                ERROR_RECORDER.error("Server type specified is " + ServerType.DMGRSERVER + " but no port was provided");

                errors.reject("vboxManagerUrl", "system.vbox.manager.url.required");
            }
        }
    }
}
