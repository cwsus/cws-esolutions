/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
package com.cws.esolutions.security.processors.dto;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.dto
 * File: CertificateRequest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   03/28/2017 01:41:00             Created.
 */
import org.slf4j.Logger;
import java.io.Serializable;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * @author khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class CertificateRequest implements Serializable
{
	private int keySize = 2048;
	private String stateName = null;
	private int validityPeriod = 365;
	private String commonName = null;
	private String countryName = null;
	private String localityName = null;
	private String contactEmail = null;
    private String applicationId = null;
    private String storePassword = null;
    private String applicationName = null;
	private UserAccount userAccount = null;
	private String organizationName = null;
	private RequestHostInfo hostInfo = null;
	private String organizationalUnit = null;

	private static final long serialVersionUID = -4071795817942006166L;
	private static final String CNAME = CertificateRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER);

    public final void setApplicationId(final String value)
    {
        final String methodName = CertificateRequest.CNAME + "#setApplicationId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationId = value;
    }

    public final void setApplicationName(final String value)
    {
        final String methodName = CertificateRequest.CNAME + "#setApplicationName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationName = value;
    }

    public final void setHostInfo(final RequestHostInfo value)
    {
        final String methodName = CertificateRequest.CNAME + "#setHostInfo(final RequestHostInfo value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.hostInfo = value;
    }

    public final void setUserAccount(final UserAccount value)
    {
        final String methodName = CertificateRequest.CNAME + "#setReqInfo(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userAccount = value;
    }

    public final void setCommonName(final String value)
    {
        final String methodName = CertificateRequest.CNAME + "#setCommonName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.commonName = value;
    }

    public final void setOrganizationalUnit(final String value)
    {
        final String methodName = CertificateRequest.CNAME + "#setOrganizationalUnit(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.organizationalUnit = value;
    }

    public final void setOrganizationName(final String value)
    {
        final String methodName = CertificateRequest.CNAME + "#setOrganizationName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.organizationName = value;
    }

    public final void setLocalityName(final String value)
    {
        final String methodName = CertificateRequest.CNAME + "#setLocalityName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.localityName = value;
    }

    public final void setStateName(final String value)
    {
        final String methodName = CertificateRequest.CNAME + "#setStateName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.stateName = value;
    }

    public final void setCountryName(final String value)
    {
        final String methodName = CertificateRequest.CNAME + "#setCountryName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.countryName = value;
    }

    public final void setContactEmail(final String value)
    {
        final String methodName = CertificateRequest.CNAME + "#setContactEmail(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.contactEmail = value;
    }

    public final void setValidityPeriod(final int value)
    {
        final String methodName = CertificateRequest.CNAME + "#setValidityPeriod(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.validityPeriod = value;
    }

    public final void setKeySize(final int value)
    {
        final String methodName = CertificateRequest.CNAME + "#setKeySize(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.keySize = value;
    }

    public final void setStorePassword(final String value)
    {
        final String methodName = CertificateRequest.CNAME + "#setStorePassword(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.storePassword = value;
    }

    public final String getApplicationId()
    {
        final String methodName = CertificateRequest.CNAME + "#getApplicationId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationId);
        }

        return this.applicationId;
    }

    public final String getApplicationName()
    {
        final String methodName = CertificateRequest.CNAME + "#getApplicationName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationName);
        }

        return this.applicationName;
    }

    public final RequestHostInfo getHostInfo()
    {
        final String methodName = CertificateRequest.CNAME + "#getHostInfo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.hostInfo);
        }

        return this.hostInfo;
    }

    public final UserAccount getUserAccount()
    {
        final String methodName = CertificateRequest.CNAME + "#getReqInfo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userAccount);
        }

        return this.userAccount;
    }

    public final String getCommonName()
    {
        final String methodName = CertificateRequest.CNAME + "#getCommonName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.commonName);
        }

        return this.commonName;
    }

    public final String getOrganizationalUnit()
    {
        final String methodName = CertificateRequest.CNAME + "#getOrganizationalUnit()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.organizationalUnit);
        }

        return this.organizationalUnit;
    }

    public final String getOrganizationName()
    {
        final String methodName = CertificateRequest.CNAME + "#getOrganizationName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.organizationName);
        }

        return this.organizationName;
    }

    public final String getLocalityName()
    {
        final String methodName = CertificateRequest.CNAME + "#getLocalityName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.localityName);
        }

        return this.localityName;
    }

    public final String getStateName()
    {
        final String methodName = CertificateRequest.CNAME + "#getStateName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.stateName);
        }

        return this.stateName;
    }

    public final String getCountryName()
    {
        final String methodName = CertificateRequest.CNAME + "#getCountryName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.validityPeriod);
        }

        return this.countryName;
    }

    public final String getContactEmail()
    {
        final String methodName = CertificateRequest.CNAME + "#getContactEmail()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.validityPeriod);
        }

        return this.contactEmail;
    }

    public final int getValidityPeriod()
    {
        final String methodName = CertificateRequest.CNAME + "#getValidityPeriod()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.validityPeriod);
        }

        return this.validityPeriod;
    }

    public final int getKeySize()
    {
        final String methodName = CertificateRequest.CNAME + "#getKeySize()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.keySize);
        }

        return this.keySize;
    }

    public final String getStorePassword()
    {
        final String methodName = CertificateRequest.CNAME + "#getStorePassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.storePassword);
        }

        return this.storePassword;
    }

    @Override
    public final String toString()
    {
        final String methodName = CertificateRequest.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + SecurityServiceConstants.LINE_BREAK + "{" + SecurityServiceConstants.LINE_BREAK);

        for (Field field : this.getClass().getDeclaredFields())
        {
            if (DEBUG)
            {
                DEBUGGER.debug("field: {}", field);
            }

            if (!(field.getName().equals("methodName")) &&
                    (!(field.getName().equals("CNAME"))) &&
                    (!(field.getName().equals("DEBUGGER"))) &&
                    (!(field.getName().equals("DEBUG"))) &&
                    (!(field.getName().equals("ERROR_RECORDER"))) &&
                    (!(field.getName().equals("userSecurity"))) &&
                    (!(field.getName().equals("serialVersionUID"))))
            {
                try
                {
                    if (field.get(this) != null)
                    {
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + SecurityServiceConstants.LINE_BREAK);
                    }
                }
                catch (IllegalAccessException iax)
                {
                    ERROR_RECORDER.error(iax.getMessage(), iax);
                }
            }
        }

        sBuilder.append('}');

        if (DEBUG)
        {
            DEBUGGER.debug("sBuilder: {}", sBuilder);
        }

        return sBuilder.toString();
    }
}
