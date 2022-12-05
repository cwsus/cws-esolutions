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
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.io.File;
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
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
@XmlType(name = "certificate-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class CertificateConfig implements Serializable
{
    private int certKeySize = 2048;
    private File csrDirectory = null;
    private File rootDirectory = null;
    private File storeDirectory = null;
    private String certificateType = null;
    private File publicKeyDirectory = null;
    private File rootCertificateFile = null;
    private File privateKeyDirectory = null;
    private File certificateDirectory = null;
    private String signatureAlgorithm = null;
    private String rootCertificateName = null;
    private String certificateAlgorithm = null;
    private File intermediateCertificateFile = null;
    private String intermediateCertificateName = null;

    private static final long serialVersionUID = -8867893854548973748L;
    private static final String CNAME = CertificateConfig.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setRootDirectory(final File value)
    {
        final String methodName = CertificateConfig.CNAME + "#setRootDirectory(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.rootDirectory = value;
    }

    public final void setCsrDirectory(final File value)
    {
        final String methodName = CertificateConfig.CNAME + "#setCsrDirectory(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.csrDirectory = value;
    }

    public final void setPrivateKeyDirectory(final File value)
    {
        final String methodName = CertificateConfig.CNAME + "#setPrivateKeyDirectory(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.privateKeyDirectory = value;
    }

    public final void setPublicKeyDirectory(final File value)
    {
        final String methodName = CertificateConfig.CNAME + "#setPublicKeyDirectory(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.publicKeyDirectory = value;
    }

    public final void setCertificateDirectory(final File value)
    {
        final String methodName = CertificateConfig.CNAME + "#setCertificateDirectory(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.certificateDirectory = value;
    }

    public final void setStoreDirectory(final File value)
    {
        final String methodName = CertificateConfig.CNAME + "#setStoreDirectory(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.storeDirectory = value;
    }

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

    public final void setCertificateAlgorithm(final String value)
    {
        final String methodName = CertificateConfig.CNAME + "#setCertificateAlgorithm(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.certificateAlgorithm = value;
    }

    public final void setSignatureAlgorithm(final String value)
    {
        final String methodName = CertificateConfig.CNAME + "#setSignatureAlgorithm(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.signatureAlgorithm = value;
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

    public final void setRootCertificateFile(final File value)
    {
        final String methodName = CertificateConfig.CNAME + "#setRootCertificateFile(final File value)";

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

    public final void setIntermediateCertificateFile(final File value)
    {
        final String methodName = CertificateConfig.CNAME + "#setIntermediateCertificateFile(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.intermediateCertificateFile = value;
    }

    @XmlElement(name = "rootDirectory")
    public final File getRootDirectory()
    {
        final String methodName = CertificateConfig.CNAME + "#getRootDirectory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.rootDirectory);
        }
        
        return this.rootDirectory;
    }

    @XmlElement(name = "privateKeyDirectory")
    public final File getPrivateKeyDirectory()
    {
        final String methodName = CertificateConfig.CNAME + "#getPrivateKeyDirectory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.privateKeyDirectory);
        }

        return this.privateKeyDirectory;
    }

    @XmlElement(name = "publicKeyDirectory")
    public final File getPublicKeyDirectory()
    {
        final String methodName = CertificateConfig.CNAME + "#getPublicKeyDirectory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.publicKeyDirectory);
        }

        return this.publicKeyDirectory;
    }

    @XmlElement(name = "certificateDirectory")
    public final File getCertificateDirectory()
    {
        final String methodName = CertificateConfig.CNAME + "#getCertificateDirectory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.certificateDirectory);
        }

        return this.certificateDirectory;
    }

    @XmlElement(name = "csrDirectory")
    public final File getCsrDirectory()
    {
        final String methodName = CertificateConfig.CNAME + "#getCsrDirectory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.csrDirectory);
        }
        
        return this.csrDirectory;
    }

    @XmlElement(name = "storeDirectory")
    public final File getStoreDirectory()
    {
        final String methodName = CertificateConfig.CNAME + "#getStoreDirectory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.storeDirectory);
        }

        return this.storeDirectory;
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

    @XmlElement(name = "certificateAlgorithm")
    public final String getCertificateAlgorithm()
    {
        final String methodName = CertificateConfig.CNAME + "#getCertificateAlgorithm()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.certificateAlgorithm);
        }
        
        return this.certificateAlgorithm;
    }

    @XmlElement(name = "signatureAlgorithm")
    public final String getSignatureAlgorithm()
    {
        final String methodName = CertificateConfig.CNAME + "#getSignatureAlgorithm()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.signatureAlgorithm);
        }
        
        return this.signatureAlgorithm;
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
    public final File getRootCertificateFile()
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
    public final File getIntermediateCertificateFile()
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
                catch (IllegalAccessException iax) {}
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
