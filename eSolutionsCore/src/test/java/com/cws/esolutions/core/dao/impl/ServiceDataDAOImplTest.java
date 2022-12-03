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
 * Package: com.cws.esolutions.core.dao.impl
 * File: PlatformDataDAOImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;

import com.cws.esolutions.core.processors.enums.ServiceType;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.core.dao.interfaces.IServiceDataDAO;
import com.cws.esolutions.core.listeners.CoreServicesInitializer;
import com.cws.esolutions.core.processors.enums.NetworkPartition;

public class ServiceDataDAOImplTest
{
    private static final IServiceDataDAO dao = new ServiceDataDAOImpl();
    private static final String pGuid = "a5299180-73d5-11e3-981f-0800200c9a66";
    private static final String dGuid = "e5f4cab8-038c-46f2-bbdd-fab9b84638cb";

    @BeforeAll public void setUp()
    {
        try
        {
            CoreServicesInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "logging/logging.xml", true, false);
        }
        catch (Exception ex)
        {
            Assertions.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void addServiceAsDatacenterAsPlatform()
    {
        List<String> servers = new ArrayList<String>(
                Arrays.asList(
                        "eb98ef5a-5fc0-44b3-a01b-4bdb382aa716",
                        "2a515fa2-d1a8-4817-91b0-6a0547fea48b",
                        "250892d5-03fb-436c-8125-3d9d30de8f68",
                        "40d4ba5e-ed50-4a83-a6a3-fd7d32631a26"));

        List<String> serviceData = new ArrayList<String>(
                Arrays.asList(
                        pGuid, // guid
                        ServiceType.PLATFORM.name(), // serviceType
                        "VH_I", // name
                        ServiceRegion.DEV.name(), // region
                        NetworkPartition.DRN.name(), // nwpartition
                        ServiceStatus.ACTIVE.name(), // status
                        servers.toString(), // webservers
                        "Test Service")); // desc

        try
        {
            Assertions.assertThat(dao.addService(serviceData)).isTrue();
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void addServiceAsDatacenter()
    {
        List<String> serviceData = new ArrayList<String>(
                Arrays.asList(
                        dGuid, // guid
                        ServiceType.DATACENTER.name(), // serviceType
                        "TDC", // name
                        ServiceRegion.DEV.name(), // region
                        NetworkPartition.DRN.name(), // nwpartition
                        ServiceStatus.ACTIVE.name(), // status
                        null, // webservers
                        "Test Service")); // desc

        try
        {
            Assertions.assertThat(dao.addService(serviceData)).isTrue();
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void updatePlatformData()
    {
        List<String> servers = new ArrayList<String>(
                Arrays.asList(
                        "eb98ef5a-5fc0-44b3-a01b-4bdb382aa716",
                        "2a515fa2-d1a8-4817-91b0-6a0547fea48b",
                        "250892d5-03fb-436c-8125-3d9d30de8f68",
                        "40d4ba5e-ed50-4a83-a6a3-fd7d32631a26"));

        List<String> serviceData = new ArrayList<String>(
                Arrays.asList(
                        pGuid, // guid
                        ServiceType.PLATFORM.name(), // serviceType
                        "VH_I_I", // name
                        ServiceRegion.DEV.name(), // region
                        NetworkPartition.DRN.name(), // nwpartition
                        ServiceStatus.ACTIVE.name(), // status
                        servers.toString(), // webservers
                        "Test Service")); // desc

        try
        {
            Assertions.assertThat(dao.addService(serviceData)).isTrue();
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void updateDatacenterData()
    {
        List<String> serviceData = new ArrayList<String>(
                Arrays.asList(
                        dGuid, // guid
                        ServiceType.DATACENTER.name(), // serviceType
                        "SDC", // name
                        ServiceRegion.DEV.name(), // region
                        NetworkPartition.DRN.name(), // nwpartition
                        ServiceStatus.ACTIVE.name(), // status
                        null, // webservers
                        "Test Service")); // desc

        try
        {
            Assertions.assertThat(dao.addService(serviceData)).isTrue();
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void listServices()
    {
        try
        {
            Assertions.assertThat(dao.listServices(0)).isNotEmpty();
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void getServiceByAttribute()
    {
        try
        {
            Assertions.assertThat(dao.getServicesByAttribute("TDC", 0)).isNotEmpty();
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void getPlatformData()
    {
        try
        {
            Assertions.assertThat(dao.getService(pGuid)).isNotEmpty();
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void getDatacenterData()
    {
        try
        {
            Assertions.assertThat(dao.getService(dGuid)).isNotEmpty();
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void removePlatform()
    {
        try
        {
            Assertions.assertThat(dao.removeService(pGuid));
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void removeDatacenter()
    {
        try
        {
            Assertions.assertThat(dao.removeService(dGuid));
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        CoreServicesInitializer.shutdown();
    }
}
