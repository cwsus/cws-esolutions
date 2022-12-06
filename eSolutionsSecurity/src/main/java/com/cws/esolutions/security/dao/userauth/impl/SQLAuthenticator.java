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
package com.cws.esolutions.security.dao.userauth.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.userauth.impl
 * File: SQLAuthenticator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Arrays;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.CallableStatement;

import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
/**
 * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator
 */
public class SQLAuthenticator implements Authenticator
{
    private static final String CNAME = SQLAuthenticator.class.getName();

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#performLogon(java.lang.String, java.lang.String)
     */
    public synchronized boolean performLogon(final String userGuid, final String username, final String password) throws AuthenticatorException
    {
        final String methodName = SQLAuthenticator.CNAME + "#performLogon(final String userGuid, final String userGuid, final String user, final String password) throws AuthenticatorException";
        
        if(DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("String: {}", userGuid);
            DEBUGGER.debug("String: {}", username);
        }

        boolean isValid = false;
        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
            	DEBUGGER.debug("sqlConn: {}", sqlConn);
            }

            if ((sqlConn == null) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL retrLogonData(?, ?)}");
            stmt.setString(1, username); // guid
            stmt.setString(2, userGuid); // username

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
            }

            if (!(stmt.execute()))
            {
                throw new AuthenticatorException("No user was found for the provided user information");
            }

            resultSet = stmt.getResultSet();

            if (DEBUG)
            {
                DEBUGGER.debug("ResultSet: {}", resultSet);
            }

            if (resultSet.next())
            {
            	resultSet.first();

            	String decrypted = PasswordUtils.decryptText(resultSet.getString(3), resultSet.getString(4), secConfig.getSecretKeyAlgorithm(), secConfig.getIterations(),
            			secConfig.getKeyBits(), secConfig.getEncryptionAlgorithm(), secConfig.getEncryptionInstance(),
            			svcBean.getConfigData().getSystemConfig().getEncoding());

            	if (!(StringUtils.equals(password, decrypted)))
            	{
            		throw new AuthenticatorException("Authentication failed");
            	}

            	// user authentication succeeded, we can drop now
            	isValid = true;
            }
        }
        catch (final SQLException sqx)
        {
            throw new AuthenticatorException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
            
                if (stmt != null)
                {
                    stmt.close();
                }

                if (!(sqlConn == null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (final SQLException sqx)
            {
                throw new AuthenticatorException(sqx.getMessage(), sqx);
            }
        }

        return isValid;
    }

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#obtainSecurityData(java.lang.String, java.lang.String)
     */
    public synchronized List<String> obtainSecurityData(final String userName, final String userGuid) throws AuthenticatorException
    {
        final String methodName = SQLAuthenticator.CNAME + "#obtainSecurityData(final String userName, final String userGuid) throws AuthenticatorException";
        
        if(DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userName);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String> userSecurity = null;

        try
        {
            sqlConn = SQLAuthenticator.dataSource.getConnection();

            if (DEBUG)
            {
            	DEBUGGER.debug("sqlConn: {}", sqlConn);
            }

            if ((sqlConn == null) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL getUserByAttribute(?, ?)}");
            stmt.setString(1, userName); // guid
            stmt.setInt(2, 0); // count

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
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
                    resultSet.beforeFirst();

                    while (resultSet.next())
                    {
                        if (StringUtils.equals(resultSet.getString(2), userName))
                        {
                            String cn = resultSet.getString(1);
                            String username = resultSet.getString(2);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("String: {}", cn);
                                DEBUGGER.debug("String: {}", username);
                            }

                            resultSet.close();
                            stmt.close();

                            // found the user we want
                            stmt = sqlConn.prepareCall("{ CALL getSecurityQuestions(?, ?) }");
                            stmt.setString(1, username); // common name
                            stmt.setString(2, cn);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("CallableStatement: {}", stmt);
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
                                    userSecurity = new ArrayList<String>(
                                        Arrays.asList(
                                            resultSet.getString(1),
                                            resultSet.getString(2)));

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("userSecurity: {}", userSecurity);
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
        catch (final SQLException sqx)
        {
            throw new AuthenticatorException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
            
                if (stmt != null)
                {
                    stmt.close();
                }

                if (!(sqlConn == null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (final SQLException sqx)
            {
                throw new AuthenticatorException(sqx.getMessage(), sqx);
            }
        }

        return userSecurity;
    }
    
    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#obtainOtpSecret(java.lang.String, java.lang.String)
     */
    public synchronized String obtainOtpSecret(final String userName, final String userGuid) throws AuthenticatorException
    {
        final String methodName = SQLAuthenticator.CNAME + "#obtainOtpSecret(final String userName, final String userGuid) throws AuthenticatorException";
        
        if(DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userName);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        String otpSecret = null;
        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;

        try
        {
            sqlConn = SQLAuthenticator.dataSource.getConnection();

            if (DEBUG)
            {
            	DEBUGGER.debug("sqlConn: {}", sqlConn);
            }

            if ((sqlConn == null) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL getOtpSecret(?, ?)}");
            stmt.setString(1, userGuid); // guid
            stmt.setString(2, userName);

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
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

                    otpSecret = resultSet.getString(1);
                }
            }
        }
        catch (final SQLException sqx)
        {
            throw new AuthenticatorException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
            
                if (stmt != null)
                {
                    stmt.close();
                }

                if (!(sqlConn == null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (final SQLException sqx)
            {
                throw new AuthenticatorException(sqx.getMessage(), sqx);
            }
        }

        return otpSecret;
    }

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#verifySecurityData(java.lang.String, java.lang.String, java.util.List)
     */
    public synchronized boolean verifySecurityData(final String userId, final String userGuid, final List<String> attributes) throws AuthenticatorException
    {
        final String methodName = SQLAuthenticator.CNAME + "#verifySecurityData(final String userId, final String userGuid, final List<String> attributes) throws AuthenticatorException";

        if(DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        Connection sqlConn = null;
        CallableStatement stmt = null;

        try
        {
            sqlConn = SQLAuthenticator.dataSource.getConnection();

            if (DEBUG)
            {
            	DEBUGGER.debug("sqlConn: {}", sqlConn);
            }

            if ((sqlConn == null) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL verifySecurityQuestions(?, ?, ?, ?)}");
            stmt.setString(1, userGuid); // guid
            stmt.setString(2, userId);
            stmt.setString(3, attributes.get(0)); // username
            stmt.setString(4, attributes.get(1)); // username

            if (DEBUG)
            {
                DEBUGGER.debug("Statement: {}", stmt.toString());
            }

            return stmt.execute();
        }
        catch (final SQLException sqx)
        {
            throw new AuthenticatorException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                }

                if (!(sqlConn == null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (final SQLException sqx)
            {
                throw new AuthenticatorException(sqx.getMessage(), sqx);
            }
        }
    }
}
