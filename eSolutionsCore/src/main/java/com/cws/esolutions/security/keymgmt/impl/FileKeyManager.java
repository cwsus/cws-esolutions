/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
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
package com.cws.esolutions.security.keymgmt.impl;

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

import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.keymgmt.interfaces.KeyManager;
import com.cws.esolutions.security.keymgmt.dto.KeyManagementRequest;
import com.cws.esolutions.security.keymgmt.dto.KeyManagementResponse;
import com.cws.esolutions.security.keymgmt.exception.KeyManagementException;
/**
 * SecurityService
 * com.cws.esolutions.security.usermgmt.impl
 * LDAPUserManager.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public class FileKeyManager implements KeyManager
{
    private static final String CNAME = FileKeyManager.class.getName();

    @Override
    public synchronized KeyManagementResponse returnKeys(final KeyManagementRequest request) throws KeyManagementException
    {
        final String methodName = FileKeyManager.CNAME + "#returnKeys(final KeyManagementRequest request)";
        
        if (DEBUG)
        {
        	DEBUGGER.debug(methodName);
            DEBUGGER.debug("KeyManagementRequest: ", request);
        }
        
        InputStream pubStream = null;
        InputStream privStream = null;
        OutputStream publicStream = null;
        OutputStream privateStream = null;
        KeyManagementResponse response = new KeyManagementResponse();

        final File keyDirectory = FileUtils.getFile(request.getKeyDirectory() + request.getGuid() + "/");

        try
        {
            if (!(request.getKeyDirectory().exists()))
            {
                throw new KeyManagementException("Configured key directory does not exist");
            }
            else
            {
                if (keyDirectory.exists())
                {
                    File publicFile = FileUtils.getFile(keyDirectory + "/" + request.getGuid() + ".pub");
                    File privateFile = FileUtils.getFile(keyDirectory + "/" + request.getGuid() + ".key");

                    if ((publicFile.exists()) && (privateFile.exists()))
                    {
                        privStream = new FileInputStream(privateFile);
                        byte[] privKeyBytes = IOUtils.toByteArray(privStream);

                        pubStream = new FileInputStream(publicFile);
                        byte[] pubKeyBytes = IOUtils.toByteArray(pubStream);

                        // files exist
                        KeyFactory keyFactory = KeyFactory.getInstance(request.getKeyAlgorithm());

                        // generate private key
                        PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privKeyBytes);
                        PrivateKey privKey = keyFactory.generatePrivate(privateSpec);

                        // generate pubkey
                        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(pubKeyBytes);
                        PublicKey pubKey = keyFactory.generatePublic(publicSpec);

                        // make the keypair
                        KeyPair keyPair = new KeyPair(pubKey, privKey);

                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                        response.setResponse("Successfully loaded user keys");
                        response.setKeyPair(keyPair);
                    }
                    else
                    {
                        // files dont exist
                        throw new KeyManagementException("Failed to locate user keys");
                    }
                }
                else
                {
                    throw new KeyManagementException("User key directory does not exist");
                }
            }
        }
        catch (FileNotFoundException fnfx)
        {
            ERROR_RECORDER.error(fnfx.getMessage(), fnfx);

            throw new KeyManagementException(fnfx.getMessage(), fnfx);
        }
        catch (InvalidKeySpecException iksx)
        {
            ERROR_RECORDER.error(iksx.getMessage(), iksx);

            throw new KeyManagementException(iksx.getMessage(), iksx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new KeyManagementException(iox.getMessage(), iox);
        }
        catch (NoSuchAlgorithmException nsax)
        {
            ERROR_RECORDER.error(nsax.getMessage(), nsax);

            throw new KeyManagementException(nsax.getMessage(), nsax);
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

            if (privStream != null)
            {
                IOUtils.closeQuietly(privStream);
            }

            if (pubStream != null)
            {
                IOUtils.closeQuietly(pubStream);
            }
        }

        return response;
    }

    @Override
    public synchronized KeyManagementResponse createKeys(final KeyManagementRequest request) throws KeyManagementException
    {
        final String methodName = FileKeyManager.CNAME + "#createKeys(final KeyManagementRequest request)";
        
        if (DEBUG)
        {
        	DEBUGGER.debug(methodName);
            DEBUGGER.debug("KeyManagementRequest: ", request);
        }
        
        InputStream pubStream = null;
        InputStream privStream = null;
        OutputStream publicStream = null;
        OutputStream privateStream = null;
        KeyManagementResponse response = new KeyManagementResponse();

        final File keyDirectory = FileUtils.getFile(request.getKeyDirectory() + request.getGuid() + "/");

        try
        {
            if (!(request.getKeyDirectory().exists()))
            {
                throw new KeyManagementException("Configured key directory does not exist");
            }
            else
            {
                if (!(keyDirectory.exists()))
                {
                    if (!(keyDirectory.mkdir()))
                    {
                        throw new IOException("Failed to create user key directory");
                    }
                }

                keyDirectory.setExecutable(true, true);

                SecureRandom random = new SecureRandom();
                KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(request.getKeyAlgorithm());
                keyGenerator.initialize(request.getKeySize(), random);
                KeyPair keyPair = keyGenerator.generateKeyPair();

                if (keyPair != null)
                {
                    File privateFile = FileUtils.getFile(keyDirectory + "/" + request.getGuid() + ".key");
                    File publicFile = FileUtils.getFile(keyDirectory + "/" + request.getGuid() + ".pub");

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
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    response.setResponse("Successfully generated and added the user keypair.");
                }
                else
                {
                    throw new KeyManagementException("Failed to generate keypair. Cannot continue.");
                }
            }
        }
        catch (FileNotFoundException fnfx)
        {
            ERROR_RECORDER.error(fnfx.getMessage(), fnfx);

            throw new KeyManagementException(fnfx.getMessage(), fnfx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new KeyManagementException(iox.getMessage(), iox);
        }
        catch (NoSuchAlgorithmException nsax)
        {
            ERROR_RECORDER.error(nsax.getMessage(), nsax);

            throw new KeyManagementException(nsax.getMessage(), nsax);
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

            if (privStream != null)
            {
                IOUtils.closeQuietly(privStream);
            }

            if (pubStream != null)
            {
                IOUtils.closeQuietly(pubStream);
            }
        }

        return response;
    }

    @Override
    public synchronized KeyManagementResponse removeKeys(final KeyManagementRequest request) throws KeyManagementException
    {
        final String methodName = FileKeyManager.CNAME + "#removeKeys(final KeyManagementRequest request)";
        
        if (DEBUG)
        {
        	DEBUGGER.debug(methodName);
            DEBUGGER.debug("KeyManagementRequest: ", request);
        }
        
        KeyManagementResponse response = new KeyManagementResponse();

        final File keyDirectory = FileUtils.getFile(request.getKeyDirectory() + request.getGuid() + "/");

        try
        {
            if (!(request.getKeyDirectory().exists()))
            {
                throw new KeyManagementException("Configured key directory does not exist");
            }
            else
            {
                if ((request.getKeyDirectory().canWrite()) && (keyDirectory.canWrite()))
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
                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                        response.setResponse("Successfully removed user keypair");
                    }
                    else
                    {
                        // failed to remove key directory (which means keys could still exist)
                        response.setRequestStatus(SecurityRequestStatus.FAILURE);
                        response.setResponse("Failed to delete user keys. Please remove manually.");
                    }
                }
                else
                {
                    throw new IOException("Unable to remove user keys");
                }
            }
        }
        catch (FileNotFoundException fnfx)
        {
            ERROR_RECORDER.error(fnfx.getMessage(), fnfx);

            throw new KeyManagementException(fnfx.getMessage(), fnfx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new KeyManagementException(iox.getMessage(), iox);
        }

        return response;
    }
}
