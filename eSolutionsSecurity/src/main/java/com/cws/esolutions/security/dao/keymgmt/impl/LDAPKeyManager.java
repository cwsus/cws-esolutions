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
package com.cws.esolutions.security.dao.keymgmt.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.keymgmt.impl
 * File: LDAPKeyManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Arrays;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.net.ConnectException;
import java.sql.CallableStatement;
import java.security.SecureRandom;
import com.unboundid.ldap.sdk.Filter;
import java.security.KeyPairGenerator;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.security.spec.X509EncodedKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import java.security.spec.InvalidKeySpecException;

import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager;
import com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException;
/**
 * @see com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager
 */
public class LDAPKeyManager implements KeyManager
{
    private static final String CNAME = LDAPKeyManager.class.getName();
    private static final DataSource dataSource = svcBean.getDataSources().get(SecurityServiceConstants.INIT_SECURITYDS_MANAGER);

    /**
     * @see com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager#createKeys(java.lang.String)
     */
    public synchronized boolean createKeys(final String guid) throws KeyManagementException
    {
        final String methodName = LDAPKeyManager.CNAME + "#createKeys(final String guid) throws KeyManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(keyConfig.getKeyAlgorithm());
            keyGenerator.initialize(keyConfig.getKeySize(), new SecureRandom());
            KeyPair keyPair = keyGenerator.generateKeyPair();

            if (keyPair == null)
            {
                throw new KeyManagementException("Failed to generate keypair.");
            }

            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("sqlConn: {}", sqlConn);
            }

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain datasource connection");
            }

            // put in the private key first
            // privkey ALWAYS goes into db
            stmt = sqlConn.prepareCall("{CALL addPrivateKey(?, ?)}");
            stmt.setString(1, guid);
            stmt.setBytes(2, keyPair.getPrivate().getEncoded());

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
            }

            stmt.execute();

            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if ((ldapPool.isClosed()) || (sqlConn.isClosed()))
            {
                throw new ConnectException("Failed to connect to datasources");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new LDAPException(ResultCode.CONNECT_ERROR, "Failed to obtain connection to LDAP host");
            }

            // need to get the DN
            Filter searchFilter = Filter.create("(&(objectClass=" + repoConfig.getBaseObject() + ")" +
                    "(&(cn=" + guid + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchReq = new SearchRequest(
                    repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN(),
                    SearchScope.SUB,
                    searchFilter);

            if (DEBUG)
            {
                DEBUGGER.debug("searchRequest: {}", searchReq);
            }

            SearchResult searchResult = ldapConn.search(searchReq);

            if (DEBUG)
            {
                DEBUGGER.debug("SearchResult: {}", searchResult);
            }

            if ((searchResult.getResultCode() != ResultCode.SUCCESS) || (searchResult.getSearchEntries().size() != 1))
            {
                throw new KeyManagementException("No users were located with the search data provided");
            }

            List<Modification> modifyList = new ArrayList<Modification>(
                    Arrays.asList(
                            new Modification(ModificationType.ADD, keyConfig.getPublicKeyAttribute(), keyPair.getPublic().getEncoded())));

            if (DEBUG)
            {
                DEBUGGER.debug("List<Modification>: {}", modifyList);
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(searchResult.getSearchEntries().get(0).getDN(), modifyList));

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPResult: {}", ldapResult);
            }

            if (ldapResult.getResultCode() != ResultCode.SUCCESS)
            {
                // failed to insert pubkey
                // delete the private key we already inserted
                stmt.close();
                stmt = null;

                stmt = sqlConn.prepareCall("{ CALL deleteUserKeys(?) }");
                stmt.setString(1, guid);

                if (DEBUG)
                {
                    DEBUGGER.debug("CallableStatement: {}", stmt);
                }

                if (!(stmt.execute()))
                {
                    ERROR_RECORDER.error("Failed to remove generated private key for the provided user");
                }

                return false;
            }

            return true;
        }
        catch (NoSuchAlgorithmException nsax)
        {
            throw new KeyManagementException(nsax.getMessage(), nsax);
        }
        catch (SQLException sqx)
        {
            throw new KeyManagementException(sqx.getMessage(), sqx);
        }
        catch (LDAPException lx)
        {
            throw new KeyManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            throw new KeyManagementException(cx.getMessage(), cx);
        }
        finally
        {
            try
            {
                if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
                {
                    ldapConn.close();
                    ldapPool.releaseConnection(ldapConn);
                }

                if (resultSet != null)
                {
                    resultSet.close();
                }

                if (stmt != null)
                {
                    stmt.close();
                }

                if ((sqlConn != null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }
    }

    /**
     * @see com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager#returnKeys(java.lang.String)
     */
    public synchronized KeyPair returnKeys(final String guid) throws KeyManagementException
    {
        final String methodName = LDAPKeyManager.CNAME + "#returnKeys(final String guid) throws KeyManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", sqlConn);
            }

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
            byte[] privKeyBytes = resultSet.getBytes(1);

            if (privKeyBytes == null)
            {
                throw new KeyManagementException("Unable to load key data from datasource");
            }

            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to connect to datasources");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new LDAPException(ResultCode.CONNECT_ERROR, "Failed to obtain connection to LDAP host");
            }

            // need to get the DN
            Filter searchFilter = Filter.create("(&(objectClass=" + repoConfig.getBaseObject() + ")" +
                    "(&(cn=" + guid + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchReq = new SearchRequest(
                    repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN(),
                    SearchScope.SUB,
                    searchFilter,
                    keyConfig.getPublicKeyAttribute());

            if (DEBUG)
            {
                DEBUGGER.debug("searchRequest: {}", searchReq);
            }

            SearchResult searchResult = ldapConn.search(searchReq);

            if (DEBUG)
            {
                DEBUGGER.debug("searchResult: {}", searchResult);
            }

            if ((searchResult.getResultCode() != ResultCode.SUCCESS) && (searchResult.getSearchEntries().size() != 1))
            {
                throw new KeyManagementException("No users were located with the search data provided");
            }

            byte[] pubKeyBytes = searchResult.getSearchEntries().get(0).getAttributeValueBytes(keyConfig.getPublicKeyAttribute());

            if (pubKeyBytes == null)
            {
                throw new KeyManagementException("Failed to obtain key data. Cannot continue");
            }

            // xlnt, keypair !
            KeyFactory keyFactory = KeyFactory.getInstance(keyConfig.getKeyAlgorithm());
            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privKeyBytes);
            PrivateKey privKey = keyFactory.generatePrivate(privateSpec);
            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(pubKeyBytes);
            PublicKey pubKey = keyFactory.generatePublic(publicSpec);

            return new KeyPair(pubKey, privKey);
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
        catch (LDAPException lx)
        {
            throw new KeyManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            throw new KeyManagementException(cx.getMessage(), cx);
        }
        finally
        {
            try
            {
                if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
                {
                    ldapConn.close();
                    ldapPool.releaseConnection(ldapConn);
                }

                if (resultSet != null)
                {
                    resultSet.close();
                }

                if (stmt != null)
                {
                    stmt.close();
                }

                if ((sqlConn != null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }
    }

    /**
     * @see com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager#removeKeys(java.lang.String)
     */
    public synchronized boolean removeKeys(final String guid) throws KeyManagementException
    {
        final String methodName = LDAPKeyManager.CNAME + "#removeKeys(final String guid) throws KeyManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("sqlConn: {}", sqlConn);
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if ((ldapPool.isClosed()) || (sqlConn.isClosed()))
            {
                throw new ConnectException("Failed to connect to datasources");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new LDAPException(ResultCode.CONNECT_ERROR, "Failed to obtain connection to LDAP host");
            }

            // need to get the DN
            Filter searchFilter = Filter.create("(&(objectClass=" + repoConfig.getBaseObject() + ")" +
                    "(&(cn=" + guid + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchReq = new SearchRequest(
                    repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN(),
                    SearchScope.SUB,
                    searchFilter);

            if (DEBUG)
            {
                DEBUGGER.debug("searchRequest: {}", searchReq);
            }

            SearchResult searchResult = ldapConn.search(searchReq);

            if (DEBUG)
            {
                DEBUGGER.debug("searchResult: {}", searchResult);
            }

            if ((searchResult.getResultCode() != ResultCode.SUCCESS) && (searchResult.getSearchEntries().size() != 1))
            {
                throw new KeyManagementException("No users were located with the search data provided");
            }

            // delete the privkey
            stmt = sqlConn.prepareCall("{ CALL deleteUserKeys(?) }");
            stmt.setString(1, guid);

            if (!(stmt.execute()))
            {
                List<Modification> modifyList = new ArrayList<Modification>(
                        Arrays.asList(
                                new Modification(ModificationType.DELETE, keyConfig.getPublicKeyAttribute())));

                if (DEBUG)
                {
                    DEBUGGER.debug("modifyList: {}", modifyList);
                }

                LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(searchResult.getSearchEntries().get(0).getDN(), modifyList));

                if (DEBUG)
                {
                    DEBUGGER.debug("LDAPResult: {}", ldapResult);
                }

                if (ldapResult.getResultCode() == ResultCode.SUCCESS)
                {
                    return true;
                }
            }
        }
        catch (SQLException sqx)
        {
            throw new KeyManagementException(sqx.getMessage(), sqx);
        }
        catch (LDAPException lx)
        {
            throw new KeyManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            throw new KeyManagementException(cx.getMessage(), cx);
        }
        finally
        {
            try
            {
                if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
                {
                    ldapConn.close();
                    ldapPool.releaseConnection(ldapConn);
                }

                if (resultSet != null)
                {
                    resultSet.close();
                }

                if (stmt != null)
                {
                    stmt.close();
                }

                if ((sqlConn != null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }

        return isComplete;
    }
}
