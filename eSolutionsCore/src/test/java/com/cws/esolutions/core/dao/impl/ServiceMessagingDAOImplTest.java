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
package com.cws.esolutions.core.dao.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.impl
 * File: ServiceMessagingDAOImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;

import org.junit.Test;
import org.junit.After;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;

import java.util.Calendar;
import java.util.ArrayList;
import java.sql.SQLException;

import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.web.dao.impl.ServiceMessagingDAOImpl;
import com.cws.esolutions.web.dao.interfaces.IMessagingDAO;

public class ServiceMessagingDAOImplTest
{
    private static final IMessagingDAO dao = new ServiceMessagingDAOImpl();
    private static final String MESSAGE_ID = RandomStringUtils.randomAlphanumeric(8).toUpperCase();

    @Before
    public void setUp()
    {
        try
        {
            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "logging/logging.xml");
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test
    public void testDeleteMessage()
    {
        try
        {
            ServiceMessagingDAOImplTest.dao.deleteMessage(ServiceMessagingDAOImplTest.MESSAGE_ID);
        }
        catch (SQLException sqx)
        {
            // don't care
        }
    }

    @Test
    public void testInsertMessage()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 30);

        List<Object> data = new ArrayList<Object>(
                Arrays.asList(
                        ServiceMessagingDAOImplTest.MESSAGE_ID,
                        "Test Message",
                        "This is a test message",
                        "khuntly",
                        "kmhuntly@gmail.com",
                        cal.getTimeInMillis()));

        try
        {
            Assert.assertTrue(ServiceMessagingDAOImplTest.dao.insertMessage(data));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void testUpdateMessage()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 30);

        List<Object> data = new ArrayList<Object>(
                Arrays.asList(
                        "Test Message",
                        "This is a test message",
                        "khuntly",
                        "kmhuntly@gmail.com",
                        cal.getTimeInMillis(),
                        "khuntly"));

        try
        {
            Assert.assertTrue(ServiceMessagingDAOImplTest.dao.updateMessage(ServiceMessagingDAOImplTest.MESSAGE_ID, data));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void testRetrieveMessage()
    {
        try
        {
            Assert.assertNotNull(ServiceMessagingDAOImplTest.dao.retrieveMessage(ServiceMessagingDAOImplTest.MESSAGE_ID));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void testRetrieveMessages()
    {
        try
        {
            Assert.assertNotNull(ServiceMessagingDAOImplTest.dao.retrieveMessages());
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        CoreServiceInitializer.shutdown();
    }
}
