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
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

import com.cws.esolutions.agent.AgentDaemon;

public class ApplicationManagerProcessorImplTest
{
    @Before
    public void setUp()
    {
        System.setProperty("LOG_ROOT", "C:/temp");
        System.setProperty("appConfig", "/src/main/resources/eSolutionsServer/config/eSolutionsServer.xml");
        System.setProperty("logConfig", "/src/main/resources/logging/logging.xml");
        
        AgentDaemon.main(new String[] {"start"});
    }

    @Test
    public final void testManageServerState()
    {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testManageApplicationDeployment()
    {
        Assert.fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testManageApplicationState()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
}
