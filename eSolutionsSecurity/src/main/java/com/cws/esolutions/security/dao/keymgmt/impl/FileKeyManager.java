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
package com.cws.esolutions.security.dao.keymgmt.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.keymgmt.impl
 * File: FileKeyManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.io.FileInputStream;
import java.security.PublicKey;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.io.FileNotFoundException;
import org.apache.commons.io.IOUtils;
import java.security.KeyPairGenerator;
import org.apache.commons.io.FileUtils;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException;
/**
 * @see com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager
 */
public class FileKeyManager implements KeyManager
{
    private static final String CNAME = FileKeyManager.class.getName();

    /**
     * @see com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager#returnKeys(java.lang.String)
     */
    public synchronized KeyPair returnKeys(final String guid) throws KeyManagementException
    {
        final String methodName = FileKeyManager.CNAME + "#returnKeys(final String guid) throws KeyManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
        }

        KeyPair keyPair = null;
        InputStream pubStream = null;
        InputStream privStream = null;

        final File keyDirectory = FileUtils.getFile(keyConfig.getKeyDirectory() + "/" + guid);

        try
        {
            if (!(keyDirectory.exists()))
            {
                throw new KeyManagementException("Configured key directory does not exist and unable to create it");
            }

            File publicFile = FileUtils.getFile(keyDirectory + "/" + guid + SecurityServiceConstants.PUBLICKEY_FILE_EXT);
            File privateFile = FileUtils.getFile(keyDirectory + "/" + guid + SecurityServiceConstants.PRIVATEKEY_FILE_EXT);

            if ((publicFile.exists()) && (privateFile.exists()))
            {
                privStream = new FileInputStream(privateFile);
                byte[] privKeyBytes = IOUtils.toByteArray(privStream);

                pubStream = new FileInputStream(publicFile);
                byte[] pubKeyBytes = IOUtils.toByteArray(pubStream);

                // files exist
                KeyFactory keyFactory = KeyFactory.getInstance(keyConfig.getKeyAlgorithm());

                // generate private key
                PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privKeyBytes);
                PrivateKey privKey = keyFactory.generatePrivate(privateSpec);

                // generate pubkey
                X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(pubKeyBytes);
                PublicKey pubKey = keyFactory.generatePublic(publicSpec);

                // make the keypair
                keyPair = new KeyPair(pubKey, privKey);
            }
            else
            {
                // files dont exist
                throw new KeyManagementException("Failed to locate user keys");
            }
        }
        catch (final FileNotFoundException fnfx)
        {
            throw new KeyManagementException(fnfx.getMessage(), fnfx);
        }
        catch (final InvalidKeySpecException iksx)
        {
            throw new KeyManagementException(iksx.getMessage(), iksx);
        }
        catch (final IOException iox)
        {
            throw new KeyManagementException(iox.getMessage(), iox);
        }
        catch (final NoSuchAlgorithmException nsax)
        {
            throw new KeyManagementException(nsax.getMessage(), nsax);
        }
        finally
        {
            if (privStream != null)
            {
                IOUtils.closeQuietly(privStream);
            }

            if (pubStream != null)
            {
                IOUtils.closeQuietly(pubStream);
            }
        }

        return keyPair;
    }

    /**
     * @see com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager#createKeys(java.lang.String)
     */
    public synchronized boolean createKeys(final String guid) throws KeyManagementException
    {
        final String methodName = FileKeyManager.CNAME + "#createKeys(final String guid) throws KeyManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
        }

        boolean isComplete = false;
        OutputStream publicStream = null;
        OutputStream privateStream = null;

        final File keyDirectory = FileUtils.getFile(keyConfig.getKeyDirectory() + "/" + guid);

        try
        {
            if (!(keyDirectory.exists()))
            {
                if (!(keyDirectory.mkdirs()))
                {
                    throw new KeyManagementException("Configured key directory does not exist and unable to create it");
                }
            }

            keyDirectory.setExecutable(true, true);

            SecureRandom random = new SecureRandom();
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(keyConfig.getKeyAlgorithm());
            keyGenerator.initialize(keyConfig.getKeySize(), random);
            KeyPair keyPair = keyGenerator.generateKeyPair();

            if (keyPair != null)
            {
                File privateFile = FileUtils.getFile(keyDirectory + "/" + guid + SecurityServiceConstants.PRIVATEKEY_FILE_EXT);
                File publicFile = FileUtils.getFile(keyDirectory + "/" + guid + SecurityServiceConstants.PUBLICKEY_FILE_EXT);

                if (!(privateFile.createNewFile()))
                {
                    throw new IOException("Failed to store private key file");
                }

                if (!(publicFile.createNewFile()))
                {
                    throw new IOException("Failed to store public key file");
                }

                privateFile.setWritable(true, true);
                publicFile.setWritable(true, true);

                privateStream = new FileOutputStream(privateFile);
                publicStream = new FileOutputStream(publicFile);

                IOUtils.write(keyPair.getPrivate().getEncoded(), privateStream);
                IOUtils.write(keyPair.getPublic().getEncoded(), publicStream);

                // assume success, as we'll get an IOException if the write failed
                isComplete = true;
            }
            else
            {
                throw new KeyManagementException("Failed to generate keypair. Cannot continue.");
            }
        }
        catch (final FileNotFoundException fnfx)
        {
            throw new KeyManagementException(fnfx.getMessage(), fnfx);
        }
        catch (final IOException iox)
        {
            throw new KeyManagementException(iox.getMessage(), iox);
        }
        catch (final NoSuchAlgorithmException nsax)
        {
            throw new KeyManagementException(nsax.getMessage(), nsax);
        }
        catch (final NullPointerException npe)
        {
        	throw new KeyManagementException(npe.getMessage(), npe);
        }
        finally
        {
            if (publicStream != null)
            {
                IOUtils.closeQuietly(publicStream);
            }

            if (privateStream != null)
            {
                IOUtils.closeQuietly(privateStream);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager#removeKeys(java.lang.String)
     */
    public synchronized boolean removeKeys(final String guid) throws KeyManagementException
    {
        final String methodName = FileKeyManager.CNAME + "#removeKeys(final String guid) throws KeyManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
        }
        
        boolean isComplete = false;

        final File keyDirectory = FileUtils.getFile(keyConfig.getKeyDirectory() + "/" + guid);

        try
        {
            if (!(keyDirectory.exists()))
            {
                throw new KeyManagementException("Configured key directory does not exist");
            }

            if ((FileUtils.getFile(keyConfig.getKeyDirectory()).canWrite()) && (keyDirectory.canWrite()))
            {
                // delete the files ...
                for (File file : keyDirectory.listFiles())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("File: {}", file);
                    }

                    if (!(file.delete()))
                    {
                        throw new IOException("Failed to delete file: " + file);
                    }
                }

                // ... then delete the dir
                if (keyDirectory.delete())
                {
                    isComplete = true;
                }
            }
            else
            {
                throw new IOException("Unable to remove user keys");
            }
        }
        catch (final FileNotFoundException fnfx)
        {
            throw new KeyManagementException(fnfx.getMessage(), fnfx);
        }
        catch (final IOException iox)
        {
            throw new KeyManagementException(iox.getMessage(), iox);
        }

        return isComplete;
    }
}
