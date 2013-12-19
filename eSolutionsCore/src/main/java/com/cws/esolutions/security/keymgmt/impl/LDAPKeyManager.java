/*
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
/*
 * Project: eSolutionsCore
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
import java.sql.PreparedStatement;
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
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import java.security.spec.InvalidKeySpecException;

import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.keymgmt.interfaces.KeyManager;
import com.cws.esolutions.security.keymgmt.exception.KeyManagementException;
/**
 * @see com.cws.esolutions.security.keymgmt.interfaces.KeyManager
 */
public class LDAPKeyManager implements KeyManager
{
    private static final String CNAME = LDAPKeyManager.class.getName();
    private static final DataSource dataSource = resBean.getDataSource().get(SecurityConstants.INIT_AUDITDS_MANAGER);

    /**
     * @see com.cws.esolutions.security.keymgmt.interfaces.KeyManager#returnKeys(java.lang.String)
     */
    @Override
    public synchronized KeyPair returnKeys(final String guid) throws KeyManagementException
    {
        final String methodName = LDAPKeyManager.CNAME + "#returnKeys(final String guid) throws KeyManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", guid);
        }

        String userDN = null;
        KeyPair keyPair = null;
        byte[] pubKeyBytes = null;
        Connection sqlConn = null;
        ResultSet resultSet = null;
        byte[] privKeyBytes = null;
        Filter searchFilter = null;
        PreparedStatement stmt = null;
        SearchRequest searchReq = null;
        LDAPConnection ldapConn = null;
        SearchResultEntry entry = null;
        SearchResult searchResult = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) resBean.getAuthDataSource();
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("sqlConn: {}", sqlConn);
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (!(ldapPool.isClosed()) && (!(sqlConn.isClosed())))
            {
                ldapConn = ldapPool.getConnection();

                if (DEBUG)
                {
                    DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                }

                if (ldapConn.isConnected())
                {
                    // need to get the DN
                    searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                            "(&("  + authData.getCommonName() +  "=" + guid + ")))");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchFilter: {}", searchFilter);
                    }

                    searchReq = new SearchRequest(
                            authRepo.getRepositoryBaseDN(),
                            SearchScope.SUB,
                            searchFilter,
                            authData.getUserId(),
                            authData.getCommonName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchRequest: {}", searchReq);
                    }

                    searchResult = ldapConn.search(searchReq);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchResult: {}", searchResult);
                    }

                    if ((searchResult.getResultCode() == ResultCode.SUCCESS) && (searchResult.getSearchEntries().size() == 1))
                    {
                        entry = searchResult.getSearchEntries().get(0);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SearchResultEntry: {}", entry);
                        }

                        userDN = entry.getDN();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Value: {}", userDN);
                        }
                    }
                    else
                    {
                        throw new KeyManagementException("No users were located with the search data provided");
                    }
                    
                    // privkey will always be stored in the database
                    // i probably shouldnt mix this but im going to anyway
                    stmt = sqlConn.prepareCall("{ CALL retrUserKeys(?) }");
                    stmt.setString(1, guid);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Statement: {}", stmt.toString());
                    }

                    if (stmt.execute())
                    {
                        resultSet = stmt.getResultSet();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ResultSet: {}", resultSet);
                        }

                        if (resultSet.next())
                        {
                            resultSet.first();
                            privKeyBytes = resultSet.getBytes(1);
                        }
                        else
                        {
                            // no privkey
                            throw new KeyManagementException("No private key was found for the provided user.");
                        }
                    }
                    else
                    {
                        throw new KeyManagementException("No private key was found for the provided user.");
                    }

                    // get the public key
                    searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                            "(&("  + authData.getCommonName() +  "=" + guid + ")))");

                    searchReq = new SearchRequest(
                            authRepo.getRepositoryBaseDN(),
                            SearchScope.SUB,
                            searchFilter,
                            authData.getPublicKey());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchFilter: {}", searchFilter);
                        DEBUGGER.debug("searchRequest: {}", searchReq);
                    }

                    searchResult = ldapConn.search(searchReq);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchResult: {}", searchResult);
                    }

                    if ((searchResult.getResultCode() == ResultCode.SUCCESS) && (searchResult.getSearchEntries().size() == 1))
                    {
                        entry = searchResult.getSearchEntries().get(0);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SearchResultEntry: {}", entry);
                        }

                        pubKeyBytes = entry.getAttributeValueBytes(authData.getPublicKey());

                        if ((privKeyBytes != null) && (pubKeyBytes != null))
                        {
                            // xlnt, keypair !
                            KeyFactory keyFactory = KeyFactory.getInstance(keyConfig.getKeyAlgorithm());

                            // generate private key
                            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privKeyBytes);
                            PrivateKey privKey = keyFactory.generatePrivate(privateSpec);

                            // generate pubkey
                            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(pubKeyBytes);
                            PublicKey pubKey = keyFactory.generatePublic(publicSpec);

                            keyPair = new KeyPair(pubKey, privKey);
                        }
                    }
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
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new KeyManagementException(lx.getMessage(), lx);
        }
        finally
        {
            try
            {
                if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
                {
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

        return keyPair;
    }

    /**
     * @see com.cws.esolutions.security.keymgmt.interfaces.KeyManager#createKeys(java.lang.String)
     */
    @Override
    public synchronized boolean createKeys(final String guid) throws KeyManagementException
    {
        final String methodName = LDAPKeyManager.CNAME + "#createKeys(final String guid) throws KeyManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", guid);
        }

        String userDN = null;
        Connection sqlConn = null;
        boolean isComplete = false;
        ResultSet resultSet = null;
        Filter searchFilter = null;
        PreparedStatement stmt = null;
        SearchRequest searchReq = null;
        LDAPConnection ldapConn = null;
        SearchResultEntry entry = null;
        SearchResult searchResult = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) resBean.getAuthDataSource();
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("sqlConn: {}", sqlConn);
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (!(ldapPool.isClosed()) && (!(sqlConn.isClosed())))
            {
                ldapConn = ldapPool.getConnection();

                if (DEBUG)
                {
                    DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                }

                if (ldapConn.isConnected())
                {
                    // need to get the DN
                    searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                            "(&("  + authData.getCommonName() +  "=" + guid + ")))");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchFilter: {}", searchFilter);
                    }

                    searchReq = new SearchRequest(
                            authRepo.getRepositoryBaseDN(),
                            SearchScope.SUB,
                            searchFilter,
                            authData.getUserId(),
                            authData.getCommonName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchRequest: {}", searchReq);
                    }

                    searchResult = ldapConn.search(searchReq);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchResult: {}", searchResult);
                    }

                    if ((searchResult.getResultCode() == ResultCode.SUCCESS) && (searchResult.getSearchEntries().size() == 1))
                    {
                        entry = searchResult.getSearchEntries().get(0);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SearchResultEntry: {}", entry);
                        }

                        userDN = entry.getDN();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Value: {}", userDN);
                        }
                    }
                    else
                    {
                        throw new KeyManagementException("No users were located with the search data provided");
                    }

                    KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(keyConfig.getKeyAlgorithm());
                    keyGenerator.initialize(keyConfig.getKeySize(), new SecureRandom());
                    KeyPair keyPair = keyGenerator.generateKeyPair();

                    if (keyPair != null)
                    {
                        // store the privkey
                        // privkey ALWAYS goes into db
                        stmt = sqlConn.prepareCall("{CALL addUserKeys(?, ?)}");
                        stmt.setString(1, guid);
                        stmt.setBytes(2, keyPair.getPrivate().getEncoded());

                        if (DEBUG)
                        {
                            DEBUGGER.debug(stmt.toString());
                        }

                        if (!(stmt.execute()))
                        {
                            List<Modification> modifyList = new ArrayList<>(
                                    Arrays.asList(
                                            new Modification(ModificationType.ADD, authData.getPublicKey(), keyPair.getPublic().getEncoded())));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("modifyList: {}", modifyList);
                            }

                            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(userDN, modifyList));

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
                                    DEBUGGER.debug("stmt: {}", stmt);
                                }

                                if (!(stmt.execute()))
                                {
                                    ERROR_RECORDER.error("Failed to remove generated private key for the provided user");
                                }

                                return isComplete;
                            }

                            isComplete = true;
                        }
                    }
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
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new KeyManagementException(lx.getMessage(), lx);
        }
        finally
        {
            try
            {
                if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
                {
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

    /**
     * @see com.cws.esolutions.security.keymgmt.interfaces.KeyManager#removeKeys(java.lang.String)
     */
    @Override
    public synchronized boolean removeKeys(final String guid) throws KeyManagementException
    {
        final String methodName = LDAPKeyManager.CNAME + "#removeKeys(final String guid) throws KeyManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", guid);
        }

        String userDN = null;
        Connection sqlConn = null;
        ResultSet resultSet = null;
        Filter searchFilter = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;
        SearchRequest searchReq = null;
        LDAPConnection ldapConn = null;
        SearchResultEntry entry = null;
        SearchResult searchResult = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) resBean.getAuthDataSource();
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("sqlConn: {}", sqlConn);
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (!(ldapPool.isClosed()) && (!(sqlConn.isClosed())))
            {
                ldapConn = ldapPool.getConnection();

                if (DEBUG)
                {
                    DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                }

                if (ldapConn.isConnected())
                {
                    // need to get the DN
                    searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                            "(&("  + authData.getCommonName() +  "=" + guid + ")))");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchFilter: {}", searchFilter);
                    }

                    searchReq = new SearchRequest(
                            authRepo.getRepositoryBaseDN(),
                            SearchScope.SUB,
                            searchFilter,
                            authData.getUserId(),
                            authData.getCommonName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchRequest: {}", searchReq);
                    }

                    searchResult = ldapConn.search(searchReq);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchResult: {}", searchResult);
                    }

                    if ((searchResult.getResultCode() == ResultCode.SUCCESS) && (searchResult.getSearchEntries().size() == 1))
                    {
                        entry = searchResult.getSearchEntries().get(0);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SearchResultEntry: {}", entry);
                        }

                        userDN = entry.getDN();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Value: {}", userDN);
                        }
                    }
                    else
                    {
                        throw new KeyManagementException("No users were located with the search data provided");
                    }
                    
                    // remove the user keys from the store
                    // first, we have to get it from the db
                    stmt = sqlConn.prepareCall("{ CALL deleteUserKeys(?) }");
                    stmt.setString(1, guid);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Statement: {}", stmt.toString());
                    }

                    if (!(stmt.execute()))
                    {
                        // and then delete the pubkey
                        List<Modification> modifyList = new ArrayList<>(
                                Arrays.asList(
                                        new Modification(ModificationType.DELETE, authData.getPublicKey())));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("modifyList: {}", modifyList);
                        }

                        LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(userDN, modifyList));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("LDAPResult: {}", ldapResult);
                        }

                        isComplete = true;
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new KeyManagementException(sqx.getMessage(), sqx);
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new KeyManagementException(lx.getMessage(), lx);
        }
        finally
        {
            try
            {
                if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
                {
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
