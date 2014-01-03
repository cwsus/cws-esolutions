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
package com.cws.esolutions.core.processors.dto;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.dto
 * File: Server.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Date;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.core.processors.enums.ServerStatus;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.NetworkPartition;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class Server implements Serializable
{
    private int cpuCount = 1; // has to be at least 1
    private int dmgrPort = 0; // only used when the servertype is dmgr
    private String mgrUrl = null; // this is used for both vmgr and dmgr - the access url
    private String osName = null;
    private String cpuType = null;
    private int installedMemory = 0; // in MB! 1GB = 1024 MB
    private Date onlineDate = null;
    private Service service = null;
    private Date offlineDate = null;
    private String virtualId = null;
    private String serverGuid = null;
    private String natAddress = null;
    private String bkHostName = null;
    private String serverRack = null;
    private String domainName = null;
    private Server owningDmgr = null;
    private String serverModel = null;
    private String nasHostName = null;
    private String bkIpAddress = null;
    private String rackPosition = null;
    private String serialNumber = null;
    private String mgmtHostName = null;
    private String operHostName = null;
    private String nasIpAddress = null;
    private String operIpAddress = null;
    private String mgmtIpAddress = null;
    private String serverComments = null;
    private ServerType serverType = null;
    private ServerStatus serverStatus = null;
    private ServiceRegion serverRegion = null;
    private UserAccount assignedEngineer = null;
    private NetworkPartition networkPartition = null;

    private static final String CNAME = Server.class.getName();
    private static final long serialVersionUID = 9028945840047154190L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER);

    public final void setServerGuid(final String value)
    {
        final String methodName = Server.CNAME + "#setServerGuid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverGuid = value;
    }

    public final void setVirtualId(final String value)
    {
        final String methodName = Server.CNAME + "#setVirtualId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.virtualId = value;
    }

    public final void setOsName(final String value)
    {
        final String methodName = Server.CNAME + "#setOsName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.osName = value;
    }

    public final void setDomainName(final String value)
    {
        final String methodName = Server.CNAME + "#setDomainName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.domainName = value;
    }

    public final void setOperIpAddress(final String value)
    {
        final String methodName = Server.CNAME + "#setOperIpAddress(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.operIpAddress = value;
    }

    public final void setOperHostName(final String value)
    {
        final String methodName = Server.CNAME + "#setOperHostName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.operHostName = value;
    }

    public final void setMgmtIpAddress(final String value)
    {
        final String methodName = Server.CNAME + "#setMgmtIpAddress(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.mgmtIpAddress = value;
    }

    public final void setMgmtHostName(final String value)
    {
        final String methodName = Server.CNAME + "#setMgmtHostName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.mgmtHostName = value;
    }

    public final void setBkIpAddress(final String value)
    {
        final String methodName = Server.CNAME + "#setBkIpAddress(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.bkIpAddress = value;
    }

    public final void setBkHostName(final String value)
    {
        final String methodName = Server.CNAME + "#setBkHostName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.bkHostName = value;
    }

    public final void setNasIpAddress(final String value)
    {
        final String methodName = Server.CNAME + "#setNasIpAddress(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.nasIpAddress = value;
    }

    public final void setNasHostName(final String value)
    {
        final String methodName = Server.CNAME + "#setNasHostName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.nasHostName = value;
    }

    public final void setNatAddress(final String value)
    {
        final String methodName = Server.CNAME + "#setNatAddress(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.natAddress = value;
    }

    public final void setServerRegion(final ServiceRegion value)
    {
        final String methodName = Server.CNAME + "#setServerRegion(final ServiceRegion value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverRegion = value;
    }

    public final void setServerStatus(final ServerStatus value)
    {
        final String methodName = Server.CNAME + "#setServerStatus(final Status value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverStatus = value;
    }

    public final void setServerType(final ServerType value)
    {
        final String methodName = Server.CNAME + "#setServerType(final ServerType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverType = value;
    }

    public final void setServerComments(final String value)
    {
        final String methodName = Server.CNAME + "#setServerComments(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverComments = value;
    }

    public final void setAssignedEngineer(final UserAccount value)
    {
        final String methodName = Server.CNAME + "#setAssignedEngineer(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.assignedEngineer = value;
    }

    public final void setDmgrPort(final int value)
    {
        final String methodName = Server.CNAME + "#setAssignedEngineer(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dmgrPort = value;
    }

    public final void setMgrUrl(final String value)
    {
        final String methodName = Server.CNAME + "#setMgrUrl(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.mgrUrl = value;
    }

    public final void setOwningDmgr(final Server value)
    {
        final String methodName = Server.CNAME + "#setOwningDmgr(final Server value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.owningDmgr = value;
    }

    public final void setCpuType(final String value)
    {
        final String methodName = Server.CNAME + "#setCpuType(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.cpuType = value;
    }

    public final void setCpuCount(final int value)
    {
        final String methodName = Server.CNAME + "#setCpuCount(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.cpuCount = value;
    }

    public final void setServerRack(final String value)
    {
        final String methodName = Server.CNAME + "#setServerRack(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverRack = value;
    }

    public final void setServerModel(final String value)
    {
        final String methodName = Server.CNAME + "#setServerModel(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverModel = value;
    }

    public final void setRackPosition(final String value)
    {
        final String methodName = Server.CNAME + "#setRackPosition(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.rackPosition = value;
    }

    public final void setSerialNumber(final String value)
    {
        final String methodName = Server.CNAME + "#setSerialNumber(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serialNumber = value;
    }

    public final void setInstalledMemory(final int value)
    {
        final String methodName = Server.CNAME + "#setInstalledMemory(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.installedMemory = value;
    }

    public final void setNetworkPartition(final NetworkPartition value)
    {
        final String methodName = Server.CNAME + "#setInstalledMemory(final NetworkPartition value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.networkPartition = value;
    }

    public final void setService(final Service value)
    {
        final String methodName = Server.CNAME + "#setService(final Service value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.service = value;
    }

    public final void setOnlineDate(final Date value)
    {
        final String methodName = Server.CNAME + "#setOnlineDate(final Date value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.onlineDate = value;
    }

    public final void setOfflineDate(final Date value)
    {
        final String methodName = Server.CNAME + "#setOfflineDate(final Date value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.offlineDate = value;
    }

    public final String getServerGuid()
    {
        final String methodName = Server.CNAME + "#getServerGuid()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverGuid);
        }

        return this.serverGuid;
    }

    public final String getVirtualId()
    {
        final String methodName = Server.CNAME + "#getVirtualId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.virtualId);
        }

        return this.virtualId;
    }

    public final String getOsName()
    {
        final String methodName = Server.CNAME + "#getOsName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.osName);
        }

        return this.osName;
    }

    public final String getDomainName()
    {
        final String methodName = Server.CNAME + "#getDomainName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.domainName);
        }

        return this.domainName;
    }

    public final String getOperIpAddress()
    {
        final String methodName = Server.CNAME + "#getOperIpAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.operIpAddress);
        }

        return this.operIpAddress;
    }

    public final String getOperHostName()
    {
        final String methodName = Server.CNAME + "#getOperHostName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.operHostName);
        }

        return this.operHostName;
    }

    public final String getMgmtIpAddress()
    {
        final String methodName = Server.CNAME + "#getMgmtIpAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.mgmtIpAddress);
        }

        return this.mgmtIpAddress;
    }

    public final String getMgmtHostName()
    {
        final String methodName = Server.CNAME + "#getMgmtHostName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.mgmtHostName);
        }

        return this.mgmtHostName;
    }

    public final String getBkIpAddress()
    {
        final String methodName = Server.CNAME + "#getBkIpAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.bkIpAddress);
        }

        return this.bkIpAddress;
    }

    public final String getBkHostName()
    {
        final String methodName = Server.CNAME + "#getBkHostName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.bkHostName);
        }

        return this.bkHostName;
    }

    public final String getNasIpAddress()
    {
        final String methodName = Server.CNAME + "#getNasIpAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.nasIpAddress);
        }

        return this.nasIpAddress;
    }

    public final String getNasHostName()
    {
        final String methodName = Server.CNAME + "#getNasHostName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.nasHostName);
        }

        return this.nasHostName;
    }

    public final String getNatAddress()
    {
        final String methodName = Server.CNAME + "#getNatAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.natAddress);
        }

        return this.natAddress;
    }

    public final ServiceRegion getServerRegion()
    {
        final String methodName = Server.CNAME + "#getServerRegion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverRegion);
        }

        return this.serverRegion;
    }

    public final ServerStatus getServerStatus()
    {
        final String methodName = Server.CNAME + "#getServerStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverStatus);
        }

        return this.serverStatus;
    }

    public final ServerType getServerType()
    {
        final String methodName = Server.CNAME + "#getServerType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverType);
        }

        return this.serverType;
    }

    public final String getServerComments()
    {
        final String methodName = Server.CNAME + "#getServerComments()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverComments);
        }

        return this.serverComments;
    }

    public final UserAccount getAssignedEngineer()
    {
        final String methodName = Server.CNAME + "#getAssignedEngineer()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.assignedEngineer);
        }

        return this.assignedEngineer;
    }

    public final int getDmgrPort()
    {
        final String methodName = Server.CNAME + "#getAssignedEngineer()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dmgrPort);
        }

        return this.dmgrPort;
    }

    public final String getMgrUrl()
    {
        final String methodName = Server.CNAME + "#getMgrUrl()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.mgrUrl);
        }

        return this.mgrUrl;
    }

    public final String getCpuType()
    {
        final String methodName = Server.CNAME + "#getCpuType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.cpuType);
        }

        return this.cpuType;
    }

    public final int getCpuCount()
    {
        final String methodName = Server.CNAME + "#getCpuCount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.cpuCount);
        }

        return this.cpuCount;
    }

    public final String getServerRack()
    {
        final String methodName = Server.CNAME + "#getServerRack()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverRack);
        }

        return this.serverRack;
    }

    public final String getServerModel()
    {
        final String methodName = Server.CNAME + "#getServerModel()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverModel);
        }

        return this.serverModel;
    }

    public final String getRackPosition()
    {
        final String methodName = Server.CNAME + "#getRackPosition()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.rackPosition);
        }

        return this.rackPosition;
    }

    public final String getSerialNumber()
    {
        final String methodName = Server.CNAME + "#getSerialNumber()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serialNumber);
        }

        return this.serialNumber;
    }

    public final Server getOwningDmgr()
    {
        final String methodName = Server.CNAME + "#getOwningDmgr()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.owningDmgr);
        }

        return this.owningDmgr;
    }

    public final int getInstalledMemory()
    {
        final String methodName = Server.CNAME + "#getInstalledMemory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.installedMemory);
        }

        return this.installedMemory;
    }

    public final NetworkPartition getNetworkPartition()
    {
        final String methodName = Server.CNAME + "#getNetworkPartition()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.networkPartition);
        }

        return this.networkPartition;
    }

    public final Service getService()
    {
        final String methodName = Server.CNAME + "#getService()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.service);
        }

        return this.service;
    }

    public final Date getOnlineDate()
    {
        final String methodName = Server.CNAME + "#getOnlineDate()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.onlineDate);
        }

        return this.onlineDate;
    }

    public final Date getOfflineDate()
    {
        final String methodName = Server.CNAME + "#getOfflineDate()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.offlineDate);
        }

        return this.offlineDate;
    }

    @Override
    public final String toString()
    {
        final String methodName = Server.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + CoreServiceConstants.LINE_BREAK + "{" + CoreServiceConstants.LINE_BREAK);

        for (Field field : this.getClass().getDeclaredFields())
        {
            if (DEBUG)
            {
                DEBUGGER.debug("field: {}", field);
            }

            if (!(field.getName().equals("methodName")) &&
                    (!(field.getName().equals("CNAME"))) &&
                    (!(field.getName().equals("DEBUGGER"))) &&
                    (!(field.getName().equals("DEBUG"))) &&
                    (!(field.getName().equals("ERROR_RECORDER"))) &&
                    (!(field.getName().equals("serialVersionUID"))))
            {
                try
                {
                    if (field.get(this) != null)
                    {
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + CoreServiceConstants.LINE_BREAK);
                    }
                }
                catch (IllegalAccessException iax)
                {
                    ERROR_RECORDER.error(iax.getMessage(), iax);
                }
            }
        }

        sBuilder.append('}');

        if (DEBUG)
        {
            DEBUGGER.debug("sBuilder: {}", sBuilder);
        }

        return sBuilder.toString();
    }
}
