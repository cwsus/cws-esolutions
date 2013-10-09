/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
 */
package com.cws.us.pws.dao.impl;

import java.util.List;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCallback;

import com.cws.us.pws.dao.interfaces.IProductReferenceDAO;
/**
 * CWSPWS_java_source
 * com.cws.us.pws.dao.interfaces
 * ProductReferenceDAOImpl.java
 *
 * TODO: Add class description
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author 35033355
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * 35033355 @ Apr 16, 2013 12:19:37 PM
 *     Created.
 */
public class ProductReferenceDAOImpl implements IProductReferenceDAO
{
    private String methodName = null;
    @Autowired private JdbcTemplate jdbcTemplate;

    public final void setJdbcTemplate(final JdbcTemplate template)
    {
        this.methodName = IProductReferenceDAO.CNAME + "#setJdbcTemplate(final JdbcTemplate template)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("JdbcTemplate: {}", template);
        }

        this.jdbcTemplate = template;
    }

    /**
     * TODO: Add in the method description/comments
     *
     * @return
     * @throws SQLException
     * @see com.cws.us.pws.dao.interfaces.IProductReferenceDAO#getProductList()
     */
    @Override
    public List<String[]> getProductList() throws SQLException
    {
        this.methodName = IProductReferenceDAO.CNAME + "#getProductList()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
        }

        List<String[]> response = this.jdbcTemplate.execute("myproc", new PreparedStatementCallback<List<String[]>>()
        {
            @Override
            public List<String[]> doInPreparedStatement(final PreparedStatement stmt) throws SQLException, DataAccessException
            {
                final String methodName = PreparedStatementCallback.class.getName() + "#doInPreparedStatement(final PreparedStatement stmt) throws SQLException, DataAccessException";
                
                if (DEBUG)
                {
                    DEBUGGER.debug(methodName);
                    DEBUGGER.debug("PreparedStatement: {}", stmt);
                }

                ResultSet resultSet = null;
                List<String[]> results = null;

                try
                {
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
                            results = new ArrayList<String[]>();

                            while (resultSet.next())
                            {
                                String[] data = new String[] {
                                        resultSet.getString(1),
                                        resultSet.getString(2),
                                        resultSet.getString(3)
                                };

                                if (DEBUG)
                                {
                                    for (String str : data)
                                    {
                                        DEBUGGER.debug("Data: {}", str);
                                    }
                                }

                                results.add(data);
                            }

                            if (DEBUG)
                            {
                                DEBUGGER.debug("results: {}", results);
                            }
                        }
                        else
                        {
                            // no data found
                        }
                    }
                    else
                    {
                        throw new SQLException("No records were returned for the provided data");
                    }
                }
                catch (SQLException sqx)
                {
                    ERROR_RECORDER.error(sqx.getMessage(), sqx);

                    throw new SQLException(sqx.getMessage(), sqx);
                }
                finally
                {
                    if (resultSet != null)
                    {
                        resultSet.close();
                    }

                    if (stmt != null)
                    {
                        stmt.close();
                    }
                }

                return results;
            }
        });

        return response;
    }

    /**
     * TODO: Add in the method description/comments
     *
     * @param productId
     * @return
     * @throws SQLException
     * @see com.cws.us.pws.dao.interfaces.IProductReferenceDAO#getProductData(int)
     */
    @Override
    public List<String> getProductData(final int productId) throws SQLException
    {
        this.methodName = IProductReferenceDAO.CNAME + "#getProductData(final int productId) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Product ID: {}", productId);
        }

        List<String> response = null;

        this.jdbcTemplate.execute(
                new CallableStatementCreator()
                {
                    public CallableStatement createCallableStatement(final Connection sqlConn) throws SQLException
                    {
                        final String methodName = IProductReferenceDAO.CNAME + "#createCallableStatement(final Connection sqlConn) throws SQLException";

                        if (DEBUG)
                        {
                            DEBUGGER.debug(methodName);
                            DEBUGGER.debug("Connection: {}", sqlConn);
                        }

                        CallableStatement stmt = sqlConn.prepareCall("{CALL getProductById(?)}");
                        stmt.setInt(1, productId);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("CallableStatement: {}", stmt);
                        }

                        return stmt;
                    }
                },
                new CallableStatementCallback<List<String>>()
                {
                    public List<String> doInCallableStatement(final CallableStatement stmt) throws SQLException
                    {
                        final String methodName = IProductReferenceDAO.CNAME + "#createCallableStatement(final CallableStatement stmt) throws SQLException";

                        if (DEBUG)
                        {
                            DEBUGGER.debug(methodName);
                            DEBUGGER.debug("CallableStatement: {}", stmt);
                        }

                        ResultSet resultSet = null;
                        List<String> results = null;

                        try
                        {
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
                                    results = new ArrayList<String>();

                                    while (resultSet.next())
                                    {
                                        results.add(resultSet.getString(1));
                                        results.add(resultSet.getString(2));
                                        results.add(resultSet.getString(3));
                                    }

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("results: {}", results);
                                    }
                                }
                                else
                                {
                                    throw new SQLException("No records were returned for the provided data");
                                }
                            }
                            else
                            {
                                throw new SQLException("No records were returned for the provided data");
                            }
                        }
                        catch (SQLException sqx)
                        {
                            ERROR_RECORDER.error(sqx.getMessage(), sqx);
        
                            throw new SQLException(sqx.getMessage(), sqx);
                        }
                        finally
                        {
                            if (resultSet != null)
                            {
                                resultSet.close();
                            }
        
                            if (stmt != null)
                            {
                                stmt.close();
                            }
                        }

                        return results;
                    }
                });

        if (DEBUG)
        {
            DEBUGGER.debug("response: {}", response);
        }

        return response;
    }
}
