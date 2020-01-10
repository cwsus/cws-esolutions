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
 * File: ApplicationManagerProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.cws.esolutions.agent.AgentDaemon;
import com.cws.esolutions.agent.processors.dto.ApplicationManagerRequest;
import com.cws.esolutions.agent.processors.exception.ApplicationManagerException;
import com.cws.esolutions.agent.processors.interfaces.IApplicationManagerProcessor;

public class ApplicationManagerProcessorImplTest
{
    private static final IApplicationManagerProcessor processor = new ApplicationManagerProcessorImpl();

    @Before
    public void setUp()
    {
        System.setProperty("appConfig", "/src/test/resources/eSolutionsServer.xml");
        System.setProperty("logConfig", "/src/test/resources/logging/logging.xml");
        
        AgentDaemon.main(new String[] { "start" });
    }

    @Test
    public final void installApplication()
    {
        ApplicationManagerRequest request = new ApplicationManagerRequest();
        request.setInstallPath("C:/opt/gpmextract");
        request.setPackageInstaller("C:/Temp/gpmextract/install.bat");
        request.setInstallerOptions(null);
        request.setPackageLocation("C:/temp/gpmextract/gpmextract.zip");
        request.setVersion(1.0);
        request.setPackageName("gpmextract");

        try
        {
            processor.installApplication(request);
        }
        catch (ApplicationManagerException amx)
        {
            Assert.fail(amx.getMessage());
        }
    }

    @After
    public final void tearDown()
    {
        // AgentDaemon.main(new String[] { "stop" });
    }
}
