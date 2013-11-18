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
package com.cws.esolutions.security.processors.impl;

import javax.crypto.Cipher;
import java.io.IOException;
import java.security.KeyPair;
import java.security.Signature;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.commons.io.IOUtils;
import java.io.FileNotFoundException;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.FileSecurityRequest;
import com.cws.esolutions.security.processors.dto.FileSecurityResponse;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.security.audit.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.processors.exception.FileSecurityException;
import com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.processors.interfaces.IFileSecurityProcessor;
/**
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.processors.impl
 * File: FileSecurityProcessorImpl.java
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
 * 35033355 @ Jul 12, 2013 3:04:41 PM
 *     Created.
 */
public class FileSecurityProcessorImpl implements IFileSecurityProcessor
{
    public synchronized FileSecurityResponse signFile(final FileSecurityRequest request) throws FileSecurityException
    {
        final String methodName = IFileSecurityProcessor.CNAME + "#signFile(final FileSecurityRequest request) throws FileSecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("FileSecurityRequest: {}", request);
        }

        FileSecurityResponse response = new FileSecurityResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount userAccount = request.getUserAccount();
        final KeyPair keys = request.getUserAccount().getUserKeys();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount", userAccount);
        }

        try
        {
            Signature signature = Signature.getInstance(request.getAlgorithm());
            signature.initSign(keys.getPrivate());
            signature.update(IOUtils.toByteArray(new FileInputStream(request.getUnsignedFile())));

            if (DEBUG)
            {
                DEBUGGER.debug("Signature: {}", signature);
            }

            byte[] sig = signature.sign();

            if (DEBUG)
            {
                DEBUGGER.debug("Signature: {}", sig);
            }

            IOUtils.write(sig, new FileOutputStream(request.getSignedFile()));

            if ((request.getSignedFile().exists()) && (request.getSignedFile().length() != 0))
            {
                response.setSignedFile(request.getSignedFile());
                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                response.setResponse("Signature successfully generated for provided file.");
            }
            else
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
                response.setResponse("Failed to generate signature for provided file.");
            }
        }
        catch (NoSuchAlgorithmException nsax)
        {
            ERROR_RECORDER.error(nsax.getMessage(), nsax);

            throw new FileSecurityException(nsax.getMessage(), nsax);
        }
        catch (FileNotFoundException fnfx)
        {
            ERROR_RECORDER.error(fnfx.getMessage(), fnfx);

            throw new FileSecurityException(fnfx.getMessage(), fnfx);
        }
        catch (InvalidKeyException ikx)
        {
            ERROR_RECORDER.error(ikx.getMessage(), ikx);

            throw new FileSecurityException(ikx.getMessage(), ikx);
        }
        catch (SignatureException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new FileSecurityException(sx.getMessage(), sx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new FileSecurityException(iox.getMessage(), iox);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.SIGNFILE);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getAppName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                IAuditProcessor auditor = new AuditProcessorImpl();
                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    public synchronized FileSecurityResponse verifyFile(final FileSecurityRequest request) throws FileSecurityException
    {
        final String methodName = IFileSecurityProcessor.CNAME + "#verifyFile(final FileSecurityRequest request) throws FileSecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("FileSecurityRequest: {}", request);
        }

        FileSecurityResponse response = new FileSecurityResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount userAccount = request.getUserAccount();
        final KeyPair keys = request.getUserAccount().getUserKeys();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount", userAccount);
        }

        try
        {
            // read in the file signature
            byte[] sigToVerify = IOUtils.toByteArray(new FileInputStream(request.getSignedFile()));

            if (DEBUG)
            {
                DEBUGGER.debug("sigToVerify: {}", sigToVerify);
            }

            Signature signature = Signature.getInstance(request.getAlgorithm());
            signature.initVerify(keys.getPublic());
            signature.update(IOUtils.toByteArray(new FileInputStream(request.getUnsignedFile())));

            if (DEBUG)
            {
                DEBUGGER.debug("Signature: {}", signature);
            }

            response.setRequestStatus(SecurityRequestStatus.SUCCESS);
            response.setIsSignatureValid(signature.verify(sigToVerify));
        }
        catch (NoSuchAlgorithmException nsax)
        {
            ERROR_RECORDER.error(nsax.getMessage(), nsax);

            throw new FileSecurityException(nsax.getMessage(), nsax);
        }
        catch (FileNotFoundException fnfx)
        {
            ERROR_RECORDER.error(fnfx.getMessage(), fnfx);

            throw new FileSecurityException(fnfx.getMessage(), fnfx);
        }
        catch (InvalidKeyException ikx)
        {
            ERROR_RECORDER.error(ikx.getMessage(), ikx);

            throw new FileSecurityException(ikx.getMessage(), ikx);
        }
        catch (SignatureException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new FileSecurityException(sx.getMessage(), sx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new FileSecurityException(iox.getMessage(), iox);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.VERIFYFILE);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getAppName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                IAuditProcessor auditor = new AuditProcessorImpl();
                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    public synchronized FileSecurityResponse encryptFile(final FileSecurityRequest request) throws FileSecurityException
    {
        final String methodName = IFileSecurityProcessor.CNAME + "#encryptFile(final FileSecurityRequest request) throws FileSecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("FileSecurityRequest: {}", request);
        }

        FileSecurityResponse response = new FileSecurityResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount userAccount = request.getUserAccount();
        final KeyPair keys = request.getUserAccount().getUserKeys();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount", userAccount);
        }

        try
        {
            Cipher cipher = Cipher.getInstance(request.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, keys.getPrivate());

            if (DEBUG)
            {
                DEBUGGER.debug("Cipher: {}", cipher);
            }

            CipherOutputStream cipherOut = new CipherOutputStream(new FileOutputStream(request.getEncryptedFile()), cipher);

            if (DEBUG)
            {
                DEBUGGER.debug("CipherOutputStream: {}", cipherOut);
            }

            byte[] data = IOUtils.toByteArray(new FileInputStream(request.getDecryptedFile()));
            IOUtils.write(data, cipherOut);

            cipherOut.flush();
            cipherOut.close();

            if ((request.getEncryptedFile().exists()) && (request.getEncryptedFile().length() != 0))
            {
                response.setSignedFile(request.getEncryptedFile());
                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                response.setResponse("Signature successfully generated for provided file.");
            }
            else
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
                response.setResponse("Failed to generate signature for provided file.");
            }
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new FileSecurityException(iox.getMessage(), iox);
        }
        catch (NoSuchAlgorithmException nsax)
        {
            ERROR_RECORDER.error(nsax.getMessage(), nsax);

            throw new FileSecurityException(nsax.getMessage(), nsax);
        }
        catch (NoSuchPaddingException nspx)
        {
            ERROR_RECORDER.error(nspx.getMessage(), nspx);

            throw new FileSecurityException(nspx.getMessage(), nspx);
        }
        catch (InvalidKeyException ikx)
        {
            ERROR_RECORDER.error(ikx.getMessage(), ikx);

            throw new FileSecurityException(ikx.getMessage(), ikx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.ENCRYPTFILE);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getAppName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                IAuditProcessor auditor = new AuditProcessorImpl();
                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    public synchronized FileSecurityResponse decryptFile(final FileSecurityRequest request) throws FileSecurityException
    {
        final String methodName = IFileSecurityProcessor.CNAME + "#decryptFile(final FileSecurityRequest request) throws FileSecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("FileSecurityRequest: {}", request);
        }

        FileSecurityResponse response = new FileSecurityResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount userAccount = request.getUserAccount();
        final KeyPair keys = request.getUserAccount().getUserKeys();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount", userAccount);
        }

        try
        {
            Cipher cipher = Cipher.getInstance(request.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, keys.getPublic());

            if (DEBUG)
            {
                DEBUGGER.debug("Cipher: {}", cipher);
            }

            IOUtils.write(IOUtils.toByteArray(new CipherInputStream(new FileInputStream(request.getEncryptedFile()), cipher)), new FileOutputStream(request.getDecryptedFile()));

            if ((request.getEncryptedFile().exists()) && (request.getEncryptedFile().length() != 0))
            {
                response.setSignedFile(request.getEncryptedFile());
                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                response.setResponse("Signature successfully generated for provided file.");
            }
            else
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
                response.setResponse("Failed to generate signature for provided file.");
            }
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new FileSecurityException(iox.getMessage(), iox);
        }
        catch (NoSuchAlgorithmException nsax)
        {
            ERROR_RECORDER.error(nsax.getMessage(), nsax);

            throw new FileSecurityException(nsax.getMessage(), nsax);
        }
        catch (NoSuchPaddingException nspx)
        {
            ERROR_RECORDER.error(nspx.getMessage(), nspx);

            throw new FileSecurityException(nspx.getMessage(), nspx);
        }
        catch (InvalidKeyException ikx)
        {
            ERROR_RECORDER.error(ikx.getMessage(), ikx);

            throw new FileSecurityException(ikx.getMessage(), ikx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.DECRYPTFILE);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getAppName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                IAuditProcessor auditor = new AuditProcessorImpl();
                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }
}
