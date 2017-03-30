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
package com.cws.esolutions.security.dao.certmgmt.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.keymgmt.impl
 * File: CertificateManagerImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.io.File;
import java.util.Date;
import java.util.List;
import java.security.Key;
import java.util.Calendar;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;
import java.io.FileNotFoundException;
import org.apache.commons.io.IOUtils;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import org.apache.commons.io.FileUtils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x500.X500Name;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.operator.ContentSigner;
import java.security.cert.CertificateException;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import com.cws.esolutions.security.dao.certmgmt.interfaces.ICertificateManager;
import com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException;
/**
 * @see com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager
 */
public class CertificateManagerImpl implements ICertificateManager
{
    /**
     * @see com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager#createCertificateRequest(final String commonName, final String orgUnitName, final String orgName, final String localityName, final String stateName, final String contactEmail, final int validityPeriod, final int keySize)
     */
    public synchronized File createCertificateRequest(final List<String> subjectData, final String storePassword, final int validityPeriod, final int keySize) throws KeyManagementException
    {
    	final String methodName = ICertificateManager.CNAME + "#createCertificateRequest(final List<String> subjectData, final String storePassword, final int validityPeriod, final int keySize) throws KeyManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", subjectData);
            DEBUGGER.debug("Value: {}", validityPeriod);
            DEBUGGER.debug("Value: {}", keySize);
        }

        final String sigAlg = keyConfig.getSignatureAlgorithm();
        final File keyDirectory = FileUtils.getFile(keyConfig.getKeyDirectory() + "/" + subjectData.get(0));
        final X500Name x500Name = new X500Name("CN=" + subjectData.get(0) + ",OU=" + subjectData.get(1) + ",O=" + subjectData.get(2) + ",L=" + subjectData.get(3) + ",ST=" + subjectData.get(4) + ",C=" + subjectData.get(5) + ",E=" + subjectData.get(6));

        if (DEBUG)
        {
        	DEBUGGER.debug("sigAlg: {}", sigAlg);
        	DEBUGGER.debug("keyDirectory: {}", keyDirectory);
        	DEBUGGER.debug("X500Name: {}", x500Name);
        }

        File csrFile = null;
        JcaPEMWriter pemWriter = null;

        try
        {
            if (!(keyDirectory.exists()))
            {
                if (!(keyDirectory.mkdirs()))
                {
                    throw new KeyManagementException("Configured key directory does not exist and unable to create it");
                }
            }

            File keyStoreFile = FileUtils.getFile(keyDirectory + "/" + subjectData.get(0) + "." + KeyStore.getDefaultType().toString());

            if (DEBUG)
            {
            	DEBUGGER.debug("File: {}", keyStoreFile);
            }

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, storePassword.toCharArray());

            if (DEBUG)
            {
            	DEBUGGER.debug("KeyStore: {}", keyStore);
            }

            keyDirectory.setExecutable(true, true);

            SecureRandom random = new SecureRandom();
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(keyConfig.getKeyAlgorithm());
            keyGenerator.initialize(keySize, random);

            if (DEBUG)
            {
            	DEBUGGER.debug("KeyGenerator: {}", keyGenerator);
            }

            KeyPair keyPair = keyGenerator.generateKeyPair();

            if (DEBUG)
            {
            	DEBUGGER.debug("KeyPair: {}", keyPair);
            }

            if (keyPair != null)
            {
                final Signature sig = Signature.getInstance(sigAlg);
                final PrivateKey privateKey = keyPair.getPrivate();
                final PublicKey publicKey = keyPair.getPublic();

                if (DEBUG)
                {
                	DEBUGGER.debug("Signature: {}", sig);
                	DEBUGGER.debug("PrivateKey: {}", privateKey);
                	DEBUGGER.debug("PublicKey: {}", publicKey);
                }

                sig.initSign(privateKey, random);
                ContentSigner signGen = new JcaContentSignerBuilder(sigAlg).build(privateKey);

                if (DEBUG)
                {
                	DEBUGGER.debug("ContentSigner: {}", signGen);
                }

                Calendar expiry = Calendar.getInstance();
                expiry.add(Calendar.DAY_OF_YEAR, validityPeriod);

                if (DEBUG)
                {
                	DEBUGGER.debug("Calendar: {}", expiry);
                }
 
                X509v3CertificateBuilder x509CertBuilder = new JcaX509v3CertificateBuilder(x500Name, BigInteger.valueOf(1), new Date(System.currentTimeMillis()),
                		new Date(expiry.getTimeInMillis()), x500Name, keyPair.getPublic());

                if (DEBUG)
                {
                	DEBUGGER.debug("X509v3CertificateBuilder: {}", x509CertBuilder);
                }

                X509Certificate x509Certificate = new JcaX509CertificateConverter().setProvider("BC").getCertificate(x509CertBuilder.build(signGen));
                X509Certificate[] certChain = new X509Certificate[1];
                certChain[0] = x509Certificate;

                if (DEBUG)
                {
                	DEBUGGER.debug("x509Certificate: {}", (Object) x509Certificate);
                	DEBUGGER.debug("X509Certificate[]: {}", (Object) certChain);
                }

                keyStore.setKeyEntry(subjectData.get(0), keyPair.getPrivate(), storePassword.toCharArray(), certChain);
                keyStore.store(new FileOutputStream(keyStoreFile), storePassword.toCharArray());

                if (DEBUG)
                {
                	DEBUGGER.debug("X509Certificate: {}", x509Certificate);
                }

                PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(x500Name, publicKey);

                if (DEBUG)
                {
                	DEBUGGER.debug("PKCS10CertificationRequestBuilder: {}", builder);
                }

                PKCS10CertificationRequest csr = builder.build(signGen);

                if (DEBUG)
                {
                	DEBUGGER.debug("PKCS10CertificationRequest: {}", csr);
                }

                keyStore.setKeyEntry(subjectData.get(0), (Key) keyPair.getPrivate(), storePassword.toCharArray(), certChain);

                csrFile = FileUtils.getFile(keyDirectory + "/" + subjectData.get(0) + ".csr");

                if (DEBUG)
                {
                	DEBUGGER.debug("CSR File: {}", csrFile);
                }

                if (!(csrFile.createNewFile()))
                {
                    throw new IOException("Failed to store CSR file");
                }

                pemWriter = new JcaPEMWriter(new OutputStreamWriter(new FileOutputStream(csrFile)));
                pemWriter.writeObject(csr);
            }
            else
            {
                throw new KeyManagementException("Failed to generate keypair. Cannot continue.");
            }
        }
        catch (FileNotFoundException fnfx)
        {
            throw new KeyManagementException(fnfx.getMessage(), fnfx);
        }
        catch (IOException iox)
        {
            throw new KeyManagementException(iox.getMessage(), iox);
        }
        catch (NoSuchAlgorithmException nsax)
        {
            throw new KeyManagementException(nsax.getMessage(), nsax);
        }
        catch (IllegalStateException isx)
        {
			throw new KeyManagementException(isx.getMessage(), isx);
		}
        catch (InvalidKeyException ikx)
        {
        	throw new KeyManagementException(ikx.getMessage(), ikx);
		}
        catch (OperatorCreationException ocx)
        {
        	throw new KeyManagementException(ocx.getMessage(), ocx);
		}
        catch (KeyStoreException ksx)
        {
        	throw new KeyManagementException(ksx.getMessage(), ksx);
		}
        catch (CertificateException cx)
        {
        	throw new KeyManagementException(cx.getMessage(), cx);
		}
        finally
        {
        	if (pemWriter != null)
        	{
        		IOUtils.closeQuietly(pemWriter);
        	}
        }

        return csrFile;
    }
}
