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
package com.cws.esolutions.agent.processors.impl;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.processors.impl
 * File: FileManagerProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.junit.Test;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Assert;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

import com.cws.esolutions.agent.AgentDaemon;
import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.agent.processors.exception.FileManagerException;
import com.cws.esolutions.agent.processors.interfaces.IFileManagerProcessor;
import com.cws.esolutions.core.processors.dto.FileManagerRequest;
import com.cws.esolutions.core.processors.dto.FileManagerResponse;

public class FileManagerProcessorImplTest
{
    private static final IFileManagerProcessor processor = new FileManagerProcessorImpl();

    @Before
    public void setUp()
    {
        System.setProperty("LOG_ROOT", "C:/temp");
        System.setProperty("appConfig", "/src/main/resources/eSolutionsServer/config/eSolutionsServer.xml");
        System.setProperty("logConfig", "/src/main/resources/logging/logging.xml");
        
        AgentDaemon.main(new String[] {"start"});
    }

    @Test
    public final void testRetrieveFile()
    {
        FileManagerRequest request = new FileManagerRequest();
        request.setRequestFile("C:\\temp\\followup.txt");

        try
        {
            FileManagerResponse response = processor.retrieveFile(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (FileManagerException fmx)
        {
            Assert.fail(fmx.getMessage());
        }
    }

    @Test
    public final void testListDirectory()
    {
        FileManagerRequest request = new FileManagerRequest();
        request.setRequestFile("C:\\Temp");

        try
        {
            FileManagerResponse response = processor.retrieveFile(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (FileManagerException fmx)
        {
            Assert.fail(fmx.getMessage());
        }
    }

    @Test
    public final void testDeployFile()
    {
        try
        {
            FileManagerRequest request = new FileManagerRequest();
            request.setRequestFile("C:\\var\\mydir\\somedir\\myfile");
            request.setFileData(FileUtils.readFileToByteArray(FileUtils.getFile("C:\\Temp\\cust.sql")));

            FileManagerResponse response = processor.deployFile(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (FileManagerException fmx)
        {
            Assert.fail(fmx.getMessage());
        }
        catch (IOException iox)
        {
            Assert.fail(iox.getMessage());
        }
    }

    @Test
    public final void testDeployFiles()
    {
        try
        {
            List<String> fileSet = new ArrayList<String>(
                    Arrays.asList(
                            "C:\\var\\temp\\acct.sql",
                            "C:\\var\\temp\\cust.sql",
                            "C:\\var\\temp\\dns.sql"));

            List<byte[]> dataSet = new ArrayList<byte[]>(
                    Arrays.asList(
                            FileUtils.readFileToByteArray(FileUtils.getFile("C:\\temp\\acct.sql")),
                            FileUtils.readFileToByteArray(FileUtils.getFile("C:\\temp\\cust.sql")),
                            FileUtils.readFileToByteArray(FileUtils.getFile("C:\\temp\\dns.sql"))));

            FileManagerRequest request = new FileManagerRequest();
            request.setSourceFiles(dataSet);
            request.setTargetFiles(fileSet);
            // request.setType(FileManagementType.DEPLOY);

            FileManagerResponse response = processor.deployFile(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (FileManagerException fmx)
        {
            Assert.fail(fmx.getMessage());
        }
        catch (IOException iox)
        {
            Assert.fail(iox.getMessage());
        }
    }

    @Test
    public final void testDeleteFiles()
    {
        List<String> fileSet = new ArrayList<String>(
                Arrays.asList(
                        "C:\\var\\temp\\acct.sql",
                        "C:\\var\\temp\\cust.sql",
                        "C:\\var\\temp\\dns.sql",
                        "C:\\var"));

        FileManagerRequest request = new FileManagerRequest();
        request.setTargetFiles(fileSet);

        try
        {
            FileManagerResponse response = processor.deleteFile(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (FileManagerException fmx)
        {
            Assert.fail(fmx.getMessage());
        }
    }
}
