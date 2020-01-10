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
 * cws-khuntly           11/23/2008 22:39:20             Created.
 */
import java.io.File;
import java.util.List;
import java.security.Key;
import java.util.Calendar;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.io.FileInputStream;
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
import java.security.cert.CertificateFactory;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.operator.ContentSigner;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import com.cws.esolutions.security.dao.certmgmt.interfaces.ICertificateManager;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.dao.certmgmt.exception.CertificateManagementException;
/**
 * @see com.cws.esolutions.security.dao.certmgmt.interfaces
 */
public class CertificateManagerImpl implements ICertificateManager
{
    /**
     * @see com.cws.esolutions.security.dao.certmgmt.interfaces.ICertificateManager#createCertificateRequest(List, String, int, int)
     */
    public synchronized File createCertificateRequest(final List<String> subjectData, final String storePassword, final int validityPeriod, final int keySize) throws CertificateManagementException
    {
        final String methodName = ICertificateManager.CNAME + "#createCertificateRequest(final List<String> subjectData, final String storePassword, final int validityPeriod, final int keySize) throws CertificateManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", subjectData);
            DEBUGGER.debug("Value: {}", validityPeriod);
            DEBUGGER.debug("Value: {}", keySize);
        }

        final File rootDirectory = certConfig.getRootDirectory();
        final String signatureAlgorithm = certConfig.getSignatureAlgorithm();
        final String certificateAlgorithm = certConfig.getCertificateAlgorithm();
        final File privateKeyDirectory = FileUtils.getFile(certConfig.getPrivateKeyDirectory() + "/" + subjectData.get(0));
        final File publicKeyDirectory = FileUtils.getFile(certConfig.getPublicKeyDirectory() + "/" + subjectData.get(0));
        final File csrDirectory = FileUtils.getFile(certConfig.getCsrDirectory() + "/" + subjectData.get(0));
        final File storeDirectory = FileUtils.getFile(certConfig.getStoreDirectory() + "/" + subjectData.get(0));
        final X500Name x500Name = new X500Name("CN=" + subjectData.get(0) + ",OU=" + subjectData.get(1) + ",O=" + subjectData.get(2) + ",L=" + subjectData.get(3) + ",ST=" + subjectData.get(4) + ",C=" + subjectData.get(5) + ",E=" + subjectData.get(6));

        if (DEBUG)
        {
            DEBUGGER.debug("rootDirectory: {}", rootDirectory);
            DEBUGGER.debug("signatureAlgorithm: {}", signatureAlgorithm);
            DEBUGGER.debug("certificateAlgorithm: {}", certificateAlgorithm);
            DEBUGGER.debug("privateKeyDirectory: {}", privateKeyDirectory);
            DEBUGGER.debug("publicKeyDirectory: {}", publicKeyDirectory);
            DEBUGGER.debug("csrDirectory: {}", csrDirectory);
            DEBUGGER.debug("storeDirectory: {}", storeDirectory);
            DEBUGGER.debug("x500Name: {}", x500Name);
        }

        File csrFile = null;
        JcaPEMWriter csrPemWriter = null;
        JcaPEMWriter publicKeyWriter = null;
        JcaPEMWriter privateKeyWriter = null;
        FileOutputStream csrFileStream = null;
        FileOutputStream keyStoreStream = null;
        FileOutputStream publicKeyFileStream = null;
        FileOutputStream privateKeyFileStream = null;
        OutputStreamWriter csrFileStreamWriter = null;
        OutputStreamWriter privateKeyStreamWriter = null;
        OutputStreamWriter publicKeyStreamWriter = null;

        try
        {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, storePassword.toCharArray());

            if (DEBUG)
            {
                DEBUGGER.debug("KeyStore: {}", keyStore);
            }

            SecureRandom random = new SecureRandom();
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(certificateAlgorithm);
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
                final Signature sig = Signature.getInstance(signatureAlgorithm);
                final PrivateKey privateKey = keyPair.getPrivate();
                final PublicKey publicKey = keyPair.getPublic();

                if (DEBUG)
                {
                    DEBUGGER.debug("Signature: {}", sig);
                    DEBUGGER.debug("PrivateKey: {}", privateKey);
                    DEBUGGER.debug("PublicKey: {}", publicKey);
                }

                sig.initSign(privateKey, random);
                ContentSigner signGen = new JcaContentSignerBuilder(signatureAlgorithm).build(privateKey);

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

                CertificateFactory certFactory = CertificateFactory.getInstance(certConfig.getCertificateType());

                if (DEBUG)
                {
                    DEBUGGER.debug("CertificateFactory: {}", certFactory);
                }

                X509Certificate[] issuerCert = new X509Certificate[] { (X509Certificate) certFactory.generateCertificate(new FileInputStream(certConfig.getIntermediateCertificateFile())) };

                if (DEBUG)
                {
                    DEBUGGER.debug("X509Certificate[]: {}", (Object) issuerCert);
                }

                keyStore.setCertificateEntry(certConfig.getRootCertificateName(), 
                        certFactory.generateCertificate(new FileInputStream(FileUtils.getFile(certConfig.getRootCertificateFile()))));
                keyStore.setCertificateEntry(certConfig.getIntermediateCertificateName(), 
                        certFactory.generateCertificate(new FileInputStream(FileUtils.getFile(certConfig.getIntermediateCertificateFile()))));

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

                // write private key
                File privateKeyFile = FileUtils.getFile(privateKeyDirectory + "/" + subjectData.get(0) + SecurityServiceConstants.PRIVATEKEY_FILE_EXT);

                if (DEBUG)
                {
                    DEBUGGER.debug("privateKeyFile: {}", privateKeyFile);
                }

                if (!(privateKeyFile.createNewFile()))
                {
                    throw new IOException("Failed to store private file");
                }

                privateKeyFileStream = new FileOutputStream(privateKeyFile);
                privateKeyStreamWriter = new OutputStreamWriter(privateKeyFileStream);

                if (DEBUG)
                {
                    DEBUGGER.debug("privateKeyFileStream: {}", privateKeyFileStream);
                    DEBUGGER.debug("privateKeyStreamWriter: {}", privateKeyStreamWriter);
                }

                privateKeyWriter = new JcaPEMWriter(privateKeyStreamWriter);
                privateKeyWriter.writeObject(privateKey);
                privateKeyWriter.flush();
                privateKeyStreamWriter.flush();
                privateKeyFileStream.flush();

                // write public key
                File publicKeyFile = FileUtils.getFile(publicKeyDirectory + "/" + subjectData.get(0) + SecurityServiceConstants.PUBLICKEY_FILE_EXT);

                if (DEBUG)
                {
                    DEBUGGER.debug("publicKeyFile: {}", publicKeyFile);
                }

                if (!(publicKeyFile.createNewFile()))
                {
                    throw new IOException("Failed to store public key file");
                }

                publicKeyFileStream = new FileOutputStream(publicKeyFile);
                publicKeyStreamWriter = new OutputStreamWriter(publicKeyFileStream);

                if (DEBUG)
                {
                    DEBUGGER.debug("publicKeyFileStream: {}", publicKeyFileStream);
                    DEBUGGER.debug("publicKeyStreamWriter: {}", publicKeyStreamWriter);
                }

                publicKeyWriter = new JcaPEMWriter(publicKeyStreamWriter);
                publicKeyWriter.writeObject(publicKey);
                publicKeyWriter.flush();
                publicKeyStreamWriter.flush();
                publicKeyFileStream.flush();

                // write csr
                csrFile = FileUtils.getFile(csrDirectory + "/" + subjectData.get(0) + SecurityServiceConstants.CSR_FILE_EXT);

                if (DEBUG)
                {
                    DEBUGGER.debug("csrFile: {}", csrFile);
                }

                if (!(csrFile.createNewFile()))
                {
                    throw new IOException("Failed to store CSR file");
                }

                csrFileStream = new FileOutputStream(csrFile);
                csrFileStreamWriter = new OutputStreamWriter(csrFileStream);

                if (DEBUG)
                {
                    DEBUGGER.debug("publicKeyFileStream: {}", publicKeyFileStream);
                    DEBUGGER.debug("publicKeyStreamWriter: {}", publicKeyStreamWriter);
                }

                csrPemWriter = new JcaPEMWriter(csrFileStreamWriter);
                csrPemWriter.writeObject(csr);
                csrPemWriter.flush();
                csrFileStreamWriter.flush();
                csrFileStream.flush();

                File keyStoreFile = FileUtils.getFile(storeDirectory + "/" + subjectData.get(0) + "." + KeyStore.getDefaultType());

                if (DEBUG)
                {
                    DEBUGGER.debug("keyStoreFile: {}", keyStoreFile);
                }

                keyStoreStream = FileUtils.openOutputStream(keyStoreFile);

                if (DEBUG)
                {
                    DEBUGGER.debug("keyStoreStream: {}", keyStoreStream);
                }

                keyStore.setKeyEntry(subjectData.get(0), (Key) keyPair.getPrivate(), storePassword.toCharArray(), issuerCert);
                keyStore.store(keyStoreStream, storePassword.toCharArray());
                keyStoreStream.flush();

                if (DEBUG)
                {
                    DEBUGGER.debug("KeyStore: {}", keyStore);
                }
            }
            else
            {
                throw new CertificateManagementException("Failed to generate keypair. Cannot continue.");
            }
        }
        catch (FileNotFoundException fnfx)
        {
            throw new CertificateManagementException(fnfx.getMessage(), fnfx);
        }
        catch (IOException iox)
        {
            throw new CertificateManagementException(iox.getMessage(), iox);
        }
        catch (NoSuchAlgorithmException nsax)
        {
            throw new CertificateManagementException(nsax.getMessage(), nsax);
        }
        catch (IllegalStateException isx)
        {
            throw new CertificateManagementException(isx.getMessage(), isx);
        }
        catch (InvalidKeyException ikx)
        {
            throw new CertificateManagementException(ikx.getMessage(), ikx);
        }
        catch (OperatorCreationException ocx)
        {
            throw new CertificateManagementException(ocx.getMessage(), ocx);
        }
        catch (KeyStoreException ksx)
        {
            throw new CertificateManagementException(ksx.getMessage(), ksx);
        }
        catch (CertificateException cx)
        {
            throw new CertificateManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if (csrFileStreamWriter != null)
            {
                IOUtils.closeQuietly(csrFileStreamWriter);
            }

            if (csrFileStream != null)
            {
                IOUtils.closeQuietly(csrFileStream);
            }

            if (csrPemWriter != null)
            {
                IOUtils.closeQuietly(csrPemWriter);
            }

            if (publicKeyFileStream != null)
            {
                IOUtils.closeQuietly(publicKeyFileStream);
            }

            if (publicKeyStreamWriter != null)
            {
                IOUtils.closeQuietly(publicKeyStreamWriter);
            }

            if (publicKeyWriter != null)
            {
                IOUtils.closeQuietly(publicKeyWriter);
            }

            if (privateKeyFileStream != null)
            {
                IOUtils.closeQuietly(privateKeyFileStream);
            }

            if (privateKeyStreamWriter != null)
            {
                IOUtils.closeQuietly(privateKeyStreamWriter);
            }

            if (privateKeyWriter != null)
            {
                IOUtils.closeQuietly(privateKeyWriter);
            }

            if (keyStoreStream != null)
            {
                IOUtils.closeQuietly(keyStoreStream);
            }
        }

        return csrFile;
    }

    /**
     * @see com.cws.esolutions.security.dao.certmgmt.interfaces.ICertificateManager#applyCertificateRequest(String, File, File, String)
     */
    public synchronized boolean applyCertificateRequest(final String commonName, final File certificateFile, final File keystoreFile, final String storePassword) throws CertificateManagementException
    {
        final String methodName = ICertificateManager.CNAME + "#applyCertificateRequest(final String commonName, final File certificateFile, final File keystoreFile, final String storePassword) throws CertificateManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", commonName);
            DEBUGGER.debug("Value: {}", certificateFile);
            DEBUGGER.debug("Value: {}", keystoreFile);
        }

        final File rootDirectory = certConfig.getRootDirectory();
        final File certificateDirectory = FileUtils.getFile(certConfig.getCertificateDirectory() + "/" + commonName);
        final File storeDirectory = FileUtils.getFile(certConfig.getStoreDirectory() + "/" + commonName);

        if (DEBUG)
        {
            DEBUGGER.debug("rootDirectory: {}", rootDirectory);
            DEBUGGER.debug("certificateDirectory: {}", certificateDirectory);
            DEBUGGER.debug("storeDirectory: {}", storeDirectory);
            DEBUGGER.debug("certificateFile: {}", certificateFile);
            DEBUGGER.debug("keystoreFile: {}", keystoreFile);
        }

        boolean isComplete = false;
        FileInputStream certStream = null;
        FileOutputStream storeStream = null;
        FileInputStream keystoreInput = null;
        FileInputStream rootCertStream = null;
        FileInputStream intermediateCertStream = null;

        try
        {
            if (!(rootDirectory.exists()))
            {
                throw new CertificateManagementException("Root certificate directory either does not exist or cannot be written to. Cannot continue.");
            }

            if (!(rootDirectory.canWrite()))
            {
                throw new CertificateManagementException("Root certificate directory either does not exist or cannot be written to. Cannot continue.");             
            }

            if (!(certConfig.getRootCertificateFile().exists()))
            {
                throw new CertificateManagementException("Root certificate file does not exist. Cannot continue."); 
            }

            if (!(certConfig.getIntermediateCertificateFile().exists()))
            {
                throw new CertificateManagementException("Intermediate certificate file does not exist. Cannot continue."); 
            }

            if (!(storeDirectory.canWrite()))
            {
                throw new CertificateManagementException("Keystore directory either does not exist or cannot be written to. Cannot continue.");
            }

            if (!(keystoreFile.canWrite()))
            {
                throw new CertificateManagementException("Unable to write to applicable keystore. Cannot continue.");
            }

            keystoreInput = FileUtils.openInputStream(keystoreFile);
            certStream = FileUtils.openInputStream(certificateFile);

            if (DEBUG)
            {
                DEBUGGER.debug("keystoreInput: {}", keystoreInput);
                DEBUGGER.debug("certStream: {}", certStream);
            }

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(keystoreInput, storePassword.toCharArray());

            if (DEBUG)
            {
                DEBUGGER.debug("KeyStore: {}", keyStore);
            }

            Key privateKey = keyStore.getKey(commonName, storePassword.toCharArray());
            CertificateFactory certFactory = CertificateFactory.getInstance(certConfig.getCertificateType());

            if (DEBUG)
            {
                DEBUGGER.debug("CertificateFactory: {}", certFactory);
            }

            rootCertStream = FileUtils.openInputStream(FileUtils.getFile(certConfig.getRootCertificateFile()));
            intermediateCertStream = FileUtils.openInputStream(FileUtils.getFile(certConfig.getIntermediateCertificateFile()));

            if (DEBUG)
            {
                DEBUGGER.debug("rootCertStream: {}", rootCertStream);
                DEBUGGER.debug("intermediateCertStream: {}", intermediateCertStream);
            }

            X509Certificate[] responseCert = new X509Certificate[] { (X509Certificate) certFactory.generateCertificate(rootCertStream),
                    (X509Certificate) certFactory.generateCertificate(intermediateCertStream),
                    (X509Certificate) certFactory.generateCertificate(certStream) };

            if (DEBUG)
            {
                DEBUGGER.debug("X509Certificate[]", (Object) responseCert);
            }

            storeStream = FileUtils.openOutputStream(keystoreFile);
            keyStore.setKeyEntry(commonName, privateKey, storePassword.toCharArray(), responseCert);
            keyStore.store(storeStream, storePassword.toCharArray());

            isComplete = true;
        }
        catch (FileNotFoundException fnfx)
        {
            throw new CertificateManagementException(fnfx.getMessage(), fnfx);
        }
        catch (IOException iox)
        {
            throw new CertificateManagementException(iox.getMessage(), iox);
        }
        catch (NoSuchAlgorithmException nsax)
        {
            throw new CertificateManagementException(nsax.getMessage(), nsax);
        }
        catch (IllegalStateException isx)
        {
            throw new CertificateManagementException(isx.getMessage(), isx);
        }
        catch (KeyStoreException ksx)
        {
            throw new CertificateManagementException(ksx.getMessage(), ksx);
        }
        catch (CertificateException cx)
        {
            throw new CertificateManagementException(cx.getMessage(), cx);
        }
        catch (UnrecoverableKeyException ukx)
        {
            throw new CertificateManagementException(ukx.getMessage(), ukx);
        }
        finally
        {
            if (storeStream != null)
            {
                IOUtils.closeQuietly(storeStream);
            }

            if (intermediateCertStream != null)
            {
                IOUtils.closeQuietly(intermediateCertStream);
            }

            if (rootCertStream != null)
            {
                IOUtils.closeQuietly(rootCertStream);
            }

            if (certStream != null)
            {
                IOUtils.closeQuietly(certStream);
            }

            if (keystoreInput != null)
            {
                IOUtils.closeQuietly(keystoreInput);
            }
        }

        return isComplete;
    }
}
