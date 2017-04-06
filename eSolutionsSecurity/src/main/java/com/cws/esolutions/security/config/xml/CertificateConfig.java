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
package com.cws.esolutions.security.config.xml;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.config.xml
 * File: RepositoryConfig.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * @author khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
@XmlType(name = "certificate-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class CertificateConfig implements Serializable
{
	private int certKeySize = 2048;
	private String certAlgorithm = null;
	private String certificateType = null;
    private String rootCertificateName = null;
    private String rootCertificateFile = null;
    private String intermediateCertificateName = null;
    private String intermediateCertificateFile = null;

    private static final long serialVersionUID = -8867893854548973748L;
    private static final String CNAME = CertificateConfig.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER);

    public final void setCertKeySize(final int value)
    {
        final String methodName = CertificateConfig.CNAME + "#setCertKeySize(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.certKeySize = value;
    }

    public final void setCertificateType(final String value)
    {
        final String methodName = CertificateConfig.CNAME + "#setCertificateType(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.certificateType = value;
    }

    public final void setCertAlgorithm(final String value)
    {
        final String methodName = CertificateConfig.CNAME + "#setCertAlgorithm(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.certAlgorithm = value;
    }

    public final void setRootCertificateName(final String value)
    {
        final String methodName = CertificateConfig.CNAME + "#setRootCertificateName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.rootCertificateName = value;
    }

    public final void setRootCertificateFile(final String value)
    {
        final String methodName = CertificateConfig.CNAME + "#setRootCertificateFile(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.rootCertificateFile = value;
    }

    public final void setIntermediateCertificateName(final String value)
    {
        final String methodName = CertificateConfig.CNAME + "#setIntermediateCertificateName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.intermediateCertificateName = value;
    }

    public final void setIntermediateCertificateFile(final String value)
    {
        final String methodName = CertificateConfig.CNAME + "#setIntermediateCertificateFile(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.intermediateCertificateFile = value;
    }

    @XmlElement(name = "certKeySize")
    public final int getCertKeySize()
    {
        final String methodName = CertificateConfig.CNAME + "#getCertKeySize()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.certKeySize);
        }
        
        return this.certKeySize;
    }

    @XmlElement(name = "certAlgorithm")
    public final String getCertAlgorithm()
    {
        final String methodName = CertificateConfig.CNAME + "#getCertAlgorithm()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.certAlgorithm);
        }
        
        return this.certAlgorithm;
    }

    @XmlElement(name = "certificateType")
    public final String getCertificateType()
    {
        final String methodName = CertificateConfig.CNAME + "#getCertificateType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.certificateType);
        }
        
        return this.certificateType;
    }

    @XmlElement(name = "rootCertificateName")
    public final String getRootCertificateName()
    {
        final String methodName = CertificateConfig.CNAME + "#getRootCertificateName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.rootCertificateName);
        }
        
        return this.rootCertificateName;
    }

    @XmlElement(name = "rootCertificateFile")
    public final String getRootCertificateFile()
    {
        final String methodName = CertificateConfig.CNAME + "#getRootCertificateFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.rootCertificateFile);
        }
        
        return this.rootCertificateFile;
    }

    @XmlElement(name = "intermediateCertificateName")
    public final String getIntermediateCertificateName()
    {
        final String methodName = CertificateConfig.CNAME + "#getIntermediateCertificateName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.intermediateCertificateName);
        }
        
        return this.intermediateCertificateName;
    }

    @XmlElement(name = "intermediateCertificateFile")
    public final String getIntermediateCertificateFile()
    {
        final String methodName = CertificateConfig.CNAME + "#getIntermediateCertificateFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.intermediateCertificateFile);
        }
        
        return this.intermediateCertificateFile;
    }

    @Override
    public final String toString()
    {
        final String methodName = CertificateConfig.CNAME + "#toString()";

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
