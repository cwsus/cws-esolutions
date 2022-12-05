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
 * File: SQLKeyManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.sql.CallableStatement;
import java.security.KeyPairGenerator;
import java.security.spec.X509EncodedKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;

import com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager;
import com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException;
/**
 * @see com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager
 */
public class SQLKeyManager implements KeyManager
{
    private static final String CNAME = SQLKeyManager.class.getName();

    /**
     * @see com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager#createKeys(java.lang.String)
     */
    public synchronized boolean createKeys(final String guid) throws KeyManagementException
    {
        final String methodName = SQLKeyManager.CNAME + "#keyManager(final String guid) throws KeyManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        CallableStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(keyConfig.getKeyAlgorithm());
            keyGenerator.initialize(keyConfig.getKeySize(), new SecureRandom());
            KeyPair keyPair = keyGenerator.generateKeyPair();

            // ok we should have a keypair now
            if (keyPair != null)
            {
                // store the private key
                PrivateKey privKey = keyPair.getPrivate();
                PublicKey pubKey = keyPair.getPublic();

                if (DEBUG)
                {
                    DEBUGGER.debug("PrivateKey: {}", privKey);
                    DEBUGGER.debug("PublicKey: {}", pubKey);
                }

                if ((privKey == null) || (pubKey == null))
                {
                    throw new KeyManagementException("Unable to generate keys. Cannot continue.");
                }

                stmt = sqlConn.prepareCall("{ CALL addUserKeys(?, ?, ?) }");
                stmt.setString(1, guid); // guid
                stmt.setBytes(2, pubKey.getEncoded());
                stmt.setBytes(3, privKey.getEncoded());

                if (DEBUG)
                {
                    DEBUGGER.debug("CallableStatement: {}", stmt);
                }

                if (stmt.executeUpdate() != 0)
                {
                    // roll back the request
                    sqlConn.rollback();

                    stmt.close();
                    stmt = null;
                    stmt = sqlConn.prepareCall("{ CALL deleteUserKeys(?) }");
                    stmt.setString(1, guid);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Statement: {}", stmt);
                    }

                    stmt.execute();

                    throw new KeyManagementException("Failed to insert private key. Cannot continue.");
                }

                return true;
            }
        }
        catch (NoSuchAlgorithmException nsax)
        {
            throw new KeyManagementException(nsax.getMessage(), nsax);
        }
        catch (SQLException sqx)
        {
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
                throw new KeyManagementException(sqx.getMessage(), sqx);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager#returnKeys(java.lang.String)
     */
    public synchronized KeyPair returnKeys(final String guid) throws KeyManagementException
    {
        final String methodName = SQLKeyManager.CNAME + "#returnKeys(final String guid) throws KeyManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
        }

        KeyPair keyPair = null;
        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{ CALL retrUserKeys(?) }");
            stmt.setString(1, guid);

            if (DEBUG)
            {
                DEBUGGER.debug("Statement: {}", stmt.toString());
            }

            if (!(stmt.execute()))
            {
                throw new KeyManagementException("No data was obtained for the provided information. Cannot continue.");
            }

            resultSet = stmt.getResultSet();

            if (DEBUG)
            {
                DEBUGGER.debug("ResultSet: {}", resultSet);
            }

            if (!(resultSet.next()))
            {
                throw new KeyManagementException("No data was obtained for the provided information. Cannot continue.");
            }

            resultSet.first();
            byte[] pubKeyBytes = resultSet.getBytes(1);
            byte[] privKeyBytes = resultSet.getBytes(2);

            if ((privKeyBytes == null) || (pubKeyBytes == null))
            {
                throw new KeyManagementException("Unable to load key data from datasource");
            }

            // xlnt, make the keypair
            KeyFactory keyFactory = KeyFactory.getInstance(keyConfig.getKeyAlgorithm());

            // generate private key
            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privKeyBytes);
            PrivateKey privKey = keyFactory.generatePrivate(privateSpec);

            // generate pubkey
            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(pubKeyBytes);
            PublicKey pubKey = keyFactory.generatePublic(publicSpec);

            keyPair = new KeyPair(pubKey, privKey);
        }
        catch (InvalidKeySpecException iksx)
        {
            throw new KeyManagementException(iksx.getMessage(), iksx);
        }
        catch (NoSuchAlgorithmException nsax)
        {
            throw new KeyManagementException(nsax.getMessage(), nsax);
        }
        catch (SQLException sqx)
        {
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
                throw new KeyManagementException(sqx.getMessage(), sqx);
            }
        }

        return keyPair;
    }

    /**
     * @see com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager#removeKeys(java.lang.String)
     */
    public synchronized boolean removeKeys(final String guid) throws KeyManagementException
    {
        final String methodName = SQLKeyManager.CNAME + "#removeKeys(final String guid) throws KeyManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        CallableStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            // remove the user keys from the store
            stmt = sqlConn.prepareCall("{CALL deleteUserKeys(?)}");
            stmt.setString(1, guid);

            if (DEBUG)
            {
                DEBUGGER.debug("Statement: {}", stmt);
            }

            if (!(stmt.execute()))
            {
                // good
                isComplete = true;
            }
        }
        catch (SQLException sqx)
        {
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
                throw new KeyManagementException(sqx.getMessage(), sqx);
            }
        }

        return isComplete;
    }
}
