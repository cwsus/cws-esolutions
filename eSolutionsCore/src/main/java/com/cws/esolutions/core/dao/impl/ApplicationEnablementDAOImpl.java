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
package com.cws.esolutions.core.dao.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.interfaces
 * File: IPackageDataDAO.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.HashMap;
import java.util.Objects;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import com.cws.esolutions.core.dao.interfaces.IApplicationEnablementDAO;
/**
 * @see com.cws.esolutions.core.dao.interfaces.IServiceDataDAO
 */
public class ApplicationEnablementDAOImpl implements IApplicationEnablementDAO
{
	public synchronized boolean isServiceEnabled(final String requestURI) throws SQLException
	{
	    final String methodName = IApplicationEnablementDAO.CNAME + "#isServiceEnabled(final String requestURI) throws SQLException";

	    if (DEBUG)
	    {
	        DEBUGGER.debug(methodName);
	    }

	    boolean isEnabled = false;
	    Connection sqlConn = null;
	    ResultSet resultSet = null;
	    PreparedStatement stmt = null;

	    if (Objects.isNull(dataSource))
	    {
	    	throw new SQLException("A datasource connection could not be obtained.");
	    }

	    try
	    {
	        sqlConn = dataSource.getConnection();

	        if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
	        {
	            throw new SQLException("Unable to obtain application datasource connection");
	        }

	        sqlConn.setAutoCommit(true);
	        stmt = sqlConn.prepareStatement("{ CALL isServiceEnabled(?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	        stmt.setString(1, requestURI);

	        if (DEBUG)
	        {
	            DEBUGGER.debug("PreparedStatement: {}", stmt);
	        }

	        resultSet = stmt.executeQuery();

	        if (DEBUG)
	        {
	        	DEBUGGER.debug("ResultSet: {}", resultSet);
	        }

	        if (resultSet.next())
	        {
	        	resultSet.first();

	        	isEnabled = resultSet.getBoolean(1);
	        }
	    }
	    catch (final SQLException sqx)
	    {
	        throw new SQLException(sqx.getMessage(), sqx);
	    }
	    finally
	    {
	        if (!(Objects.isNull(resultSet)))
	        {
	            resultSet.close();
	        }

	        if (!(Objects.isNull(stmt)))
	        {
	            stmt.close();
	        }

	        if ((sqlConn != null) && (!(sqlConn.isClosed())))
	        {
	            sqlConn.close();
	        }
	    }

	    return isEnabled;
	}

	/*
	/**
	 * @see com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO#obtainAvailableServices()
	 */
	public synchronized HashMap<String, String> obtainAvailableServices() throws SQLException
	{
	    final String methodName = IApplicationEnablementDAO.CNAME + "#obtainAvailableServices() throws SQLException";

	    if (DEBUG)
	    {
	        DEBUGGER.debug(methodName);
	    }

	    Connection sqlConn = null;
	    ResultSet resultSet = null;
	    PreparedStatement stmt = null;
	    HashMap<String, String> serviceMap = null;

	    if (Objects.isNull(dataSource))
	    {
	    	throw new SQLException("A datasource connection could not be obtained.");
	    }

	    try
	    {
	        sqlConn = dataSource.getConnection();

	        if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
	        {
	            throw new SQLException("Unable to obtain application datasource connection");
	        }

	        sqlConn.setAutoCommit(true);
	        stmt = sqlConn.prepareStatement("{ CALL retrAvailableServices() }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

	        if (DEBUG)
	        {
	            DEBUGGER.debug("PreparedStatement: {}", stmt);
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
	                serviceMap = new HashMap<String, String>();

	                while (resultSet.next())
	                {
	                    serviceMap.put(resultSet.getString(1), resultSet.getString(2));
	                }

	                if (DEBUG)
	                {
	                    DEBUGGER.debug("Map<String, String>: {}", serviceMap);
	                }
	            }
	        }
	    }
	    catch (final SQLException sqx)
	    {
	        throw new SQLException(sqx.getMessage(), sqx);
	    }
	    finally
	    {
	        if (!(Objects.isNull(resultSet)))
	        {
	            resultSet.close();
	        }

	        if (!(Objects.isNull(stmt)))
	        {
	            stmt.close();
	        }

	        if ((sqlConn != null) && (!(sqlConn.isClosed())))
	        {
	            sqlConn.close();
	        }
	    }

	    return serviceMap;
	}
}
