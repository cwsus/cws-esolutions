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

import java.sql.ResultSet;
import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.security.KeyPairGenerator;
import java.security.spec.X509EncodedKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;

import com.cws.esolutions.security.SecurityConstants;
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
public class SQLKeyManager implements KeyManager
{
    private static final String CNAME = SQLKeyManager.class.getName();
    private static final DataSource dataSource = resBean.getDataSource().get(SecurityConstants.INIT_AUDITDS_MANAGER);

    @Override
    public synchronized KeyManagementResponse returnKeys(final KeyManagementRequest request) throws KeyManagementException
    {
        final String methodName = SQLKeyManager.CNAME + "#returnKeys(final KeyManagementRequest request) throws KeyManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("KeyManagementRequest: {}", request);
        }

        byte[] pubKeyBytes = null;
        Connection sqlConn = null;
        byte[] privKeyBytes = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        KeyManagementResponse response = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{ CALL retrUserKeys(?) }");
                stmt.setString(1, request.getGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("Statement: {}", stmt.toString());
                }

                resultSet = stmt.executeQuery();

                if (DEBUG)
                {
                    DEBUGGER.debug("ResultSet: {}", resultSet);
                }

                if (resultSet.next())
                {
                    resultSet.first();
                    privKeyBytes = resultSet.getBytes(1);

                    resultSet.close();
                    stmt.close();

                    resultSet = null;
                    stmt = null;
                }
                else
                {
                    // no privkey
                    throw new KeyManagementException("No private key was found for the provided user.");
                }

                stmt = sqlConn.prepareCall("{ CALL retrPublicKey(?) }");
                stmt.setString(1, request.getGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("Statement: {}", stmt.toString());
                }

                resultSet = stmt.executeQuery();

                if (DEBUG)
                {
                    DEBUGGER.debug("ResultSet: {}", resultSet);
                }

                if (resultSet.next())
                {
                    resultSet.first();
                    pubKeyBytes = resultSet.getBytes(1);
                }
                else
                {
                    // no privkey
                    throw new KeyManagementException("No public key was found for the provided user.");
                }

                // if we got this far we're probably good
                // get the public key
                if ((privKeyBytes != null) && (pubKeyBytes != null))
                {
                    // xlnt, make the keypair
                    KeyFactory keyFactory = KeyFactory.getInstance(request.getKeyAlgorithm());

                    // generate private key
                    PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privKeyBytes);
                    PrivateKey privKey = keyFactory.generatePrivate(privateSpec);

                    // generate pubkey
                    X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(pubKeyBytes);
                    PublicKey pubKey = keyFactory.generatePublic(publicSpec);

                    KeyPair keyPair = new KeyPair(pubKey, privKey);

                    response = new KeyManagementResponse();
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    response.setResponse("Successfully loaded user keys");
                    response.setKeyPair(keyPair);
                }
                else
                {
                    response = new KeyManagementResponse();
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                    response.setResponse("Failed to load user keys");
                }
            }
        }
        catch (InvalidKeySpecException iksx)
        {
            ERROR_RECORDER.error(iksx.getMessage(), iksx);

            throw new KeyManagementException(iksx.getMessage(), iksx);
        }
        catch (NoSuchAlgorithmException nsax)
        {
            ERROR_RECORDER.error(nsax.getMessage(), nsax);

            throw new KeyManagementException(nsax.getMessage(), nsax);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new KeyManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                    resultSet = null;
                }

                if (stmt != null)
                {
                    stmt.close();
                    stmt = null;
                }

                if ((sqlConn != null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                    sqlConn = null;
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }

        return response;
    }

    @Override
    public synchronized KeyManagementResponse createKeys(final KeyManagementRequest request) throws KeyManagementException
    {
        final String methodName = SQLKeyManager.CNAME + "#keyManager(final KeyManagementRequest request) throws KeyManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("KeyManagementRequest: {}", request);
        }

        Connection sqlConn = null;
        PreparedStatement stmt = null;
        KeyManagementResponse response = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);

                SecureRandom random = new SecureRandom();
                KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(request.getKeyAlgorithm());
                keyGenerator.initialize(request.getKeySize(), random);
                KeyPair keyPair = keyGenerator.generateKeyPair();

                // ok we should have a keypair now
                if (keyPair != null)
                {
                    // store the private key
                    PrivateKey privKey = keyPair.getPrivate();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("PrivateKey: {}", privKey);
                    }

                    if (privKey != null)
                    {
                        stmt = sqlConn.prepareCall("{ CALL addUserKeys(?, ?) }");

                        if (DEBUG)
                        {
                            DEBUGGER.debug("stmt: {}", stmt);
                        }

                        if (stmt != null)
                        {
                            stmt.setString(1, request.getGuid()); // guid
                            stmt.setBytes(2, privKey.getEncoded()); // privkey

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Statement: {}", stmt);
                            }

                            int update = stmt.executeUpdate();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Response: {}", update);
                            }

                            if (update != 0)
                            {
                                // roll back the request
                                sqlConn.rollback();

                                throw new KeyManagementException("Failed to insert private key. Cannot continue.");
                            }
                        }
                        else
                        {
                            throw new SQLException("Failed to create callable statement against connection");
                        }
                    }
                    else
                    {
                        throw new KeyManagementException("Private key is null. Cannot continue.");
                    }

                    PublicKey pubKey = keyPair.getPublic();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("PublicKey: {}", pubKey);
                    }

                    if (pubKey != null)
                    {
                        stmt = sqlConn.prepareCall("{ CALL addPublicKey(?, ?) }");
                        stmt.setString(1, request.getGuid()); // guid
                        stmt.setBytes(2, pubKey.getEncoded()); // privkey

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Statement: {}", stmt);
                        }

                        int update = stmt.executeUpdate();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Response: {}", update);
                        }

                        if (update != 0)
                        {
                            sqlConn.rollback();

                            throw new KeyManagementException("Failed to insert public key. Cannot continue.");
                        }
                    }
                    else
                    {
                        // roll back the privkey'
                        sqlConn.rollback();

                        if (stmt != null)
                        {
                            stmt.close();
                        }

                        stmt = null;
                        stmt = sqlConn.prepareCall("{ CALL deleteUserKeys(?) }");
                        stmt.setString(1, request.getGuid());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Statement: {}", stmt);
                        }

                        stmt.execute();

                        throw new KeyManagementException("Public key is null. Cannot continue.");
                    }

                    response = new KeyManagementResponse();
                    response.setResponse("Successfully created user keypair");
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                }
                else
                {
                    // failed to generate keypair
                    throw new KeyManagementException("Failed to generate a user keypair");
                }
            }
        }
        catch (NoSuchAlgorithmException nsax)
        {
            ERROR_RECORDER.error(nsax.getMessage(), nsax);

            throw new KeyManagementException(nsax.getMessage(), nsax);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new KeyManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                    stmt = null;
                }

                if ((sqlConn != null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                    sqlConn = null;
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }

        return response;
    }

    @Override
    public synchronized KeyManagementResponse removeKeys(final KeyManagementRequest request) throws KeyManagementException
    {
        final String methodName = SQLKeyManager.CNAME + "#removeKeys(final KeyManagementRequest request) throws KeyManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("KeyManagementRequest: {}", request);
        }

        Connection sqlConn = null;
        PreparedStatement stmt = null;
        KeyManagementResponse response = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);
                response = new KeyManagementResponse();

                // remove the user keys from the store
                stmt = sqlConn.prepareCall("{CALL deleteUserKeys(?)}");
                stmt.setString(1, request.getGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("Statement: {}", stmt);
                }

                if (!(stmt.execute()))
                {
                    // good
                    response = new KeyManagementResponse();
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    response.setResponse("Successfully removed user keypair");
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                    response.setResponse("Failed to removed user keypair");
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new KeyManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                    stmt = null;
                }

                if ((sqlConn != null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                    sqlConn = null;
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }

        return response;
    }
}
