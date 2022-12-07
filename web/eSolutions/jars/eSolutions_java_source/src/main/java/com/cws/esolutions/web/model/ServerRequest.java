/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
 */
package com.cws.esolutions.web.model;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.model
 * File: ServerRequest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.core.processors.enums.ServerStatus;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.NetworkPartition;
/**
 * @author khuntly
 * @version 1.0
 */
public class ServerRequest implements Serializable
{
    private int cpuCount = 1; // has to be at least 1
    private int dmgrPort = 0; // only used when the servertype is dmgr
    private String mgrUrl = null; // this is used for both vmgr and dmgr - the access url
    private String osName = null;
    private String cpuType = null;
    private int installedMemory = 0; // in MB! 1GB = 1024 MB
    private String virtualId = null;
    private String natAddress = null;
    private String bkHostName = null;
    private String serverRack = null;
    private String domainName = null;
    private String owningDmgr = null;
    private String datacenter = null;
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
    private NetworkPartition networkPartition = null;

    private static final String CNAME = Server.class.getName();
    private static final long serialVersionUID = -6997903779656691703L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setVirtualId(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setVirtualId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.virtualId = value;
    }

    public final void setOsName(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setOsName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.osName = value;
    }

    public final void setDomainName(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setDomainName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.domainName = value;
    }

    public final void setOperIpAddress(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setOperIpAddress(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.operIpAddress = value;
    }

    public final void setOperHostName(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setOperHostName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.operHostName = value;
    }

    public final void setMgmtIpAddress(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setMgmtIpAddress(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.mgmtIpAddress = value;
    }

    public final void setMgmtHostName(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setMgmtHostName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.mgmtHostName = value;
    }

    public final void setBkIpAddress(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setBkIpAddress(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.bkIpAddress = value;
    }

    public final void setBkHostName(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setBkHostName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.bkHostName = value;
    }

    public final void setNasIpAddress(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setNasIpAddress(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.nasIpAddress = value;
    }

    public final void setNasHostName(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setNasHostName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.nasHostName = value;
    }

    public final void setNatAddress(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setNatAddress(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.natAddress = value;
    }

    public final void setServerRegion(final ServiceRegion value)
    {
        final String methodName = ServerRequest.CNAME + "#setServerRegion(final ServiceRegion value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverRegion = value;
    }

    public final void setServerStatus(final ServerStatus value)
    {
        final String methodName = ServerRequest.CNAME + "#setServerStatus(final Status value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverStatus = value;
    }

    public final void setServerType(final ServerType value)
    {
        final String methodName = ServerRequest.CNAME + "#setServerType(final ServerType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverType = value;
    }

    public final void setServerComments(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setServerComments(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverComments = value;
    }

    public final void setDmgrPort(final int value)
    {
        final String methodName = ServerRequest.CNAME + "#setAssignedEngineer(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dmgrPort = value;
    }

    public final void setMgrUrl(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setMgrUrl(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.mgrUrl = value;
    }

    public final void setOwningDmgr(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setOwningDmgr(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.owningDmgr = value;
    }

    public final void setCpuType(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setCpuType(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.cpuType = value;
    }

    public final void setCpuCount(final int value)
    {
        final String methodName = ServerRequest.CNAME + "#setCpuCount(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.cpuCount = value;
    }

    public final void setServerRack(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setServerRack(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverRack = value;
    }

    public final void setServerModel(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setServerModel(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverModel = value;
    }

    public final void setRackPosition(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setRackPosition(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.rackPosition = value;
    }

    public final void setSerialNumber(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setSerialNumber(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serialNumber = value;
    }

    public final void setInstalledMemory(final int value)
    {
        final String methodName = ServerRequest.CNAME + "#setInstalledMemory(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.installedMemory = value;
    }

    public final void setNetworkPartition(final NetworkPartition value)
    {
        final String methodName = ServerRequest.CNAME + "#setInstalledMemory(final NetworkPartition value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.networkPartition = value;
    }

    public final void setDatacenter(final String value)
    {
        final String methodName = ServerRequest.CNAME + "#setDatacenter(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.datacenter = value;
    }

    public final String getVirtualId()
    {
        final String methodName = ServerRequest.CNAME + "#getVirtualId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.virtualId);
        }

        return this.virtualId;
    }

    public final String getOsName()
    {
        final String methodName = ServerRequest.CNAME + "#getOsName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.osName);
        }

        return this.osName;
    }

    public final String getDomainName()
    {
        final String methodName = ServerRequest.CNAME + "#getDomainName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.domainName);
        }

        return this.domainName;
    }

    public final String getOperIpAddress()
    {
        final String methodName = ServerRequest.CNAME + "#getOperIpAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.operIpAddress);
        }

        return this.operIpAddress;
    }

    public final String getOperHostName()
    {
        final String methodName = ServerRequest.CNAME + "#getOperHostName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.operHostName);
        }

        return this.operHostName;
    }

    public final String getMgmtIpAddress()
    {
        final String methodName = ServerRequest.CNAME + "#getMgmtIpAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.mgmtIpAddress);
        }

        return this.mgmtIpAddress;
    }

    public final String getMgmtHostName()
    {
        final String methodName = ServerRequest.CNAME + "#getMgmtHostName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.mgmtHostName);
        }

        return this.mgmtHostName;
    }

    public final String getBkIpAddress()
    {
        final String methodName = ServerRequest.CNAME + "#getBkIpAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.bkIpAddress);
        }

        return this.bkIpAddress;
    }

    public final String getBkHostName()
    {
        final String methodName = ServerRequest.CNAME + "#getBkHostName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.bkHostName);
        }

        return this.bkHostName;
    }

    public final String getNasIpAddress()
    {
        final String methodName = ServerRequest.CNAME + "#getNasIpAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.nasIpAddress);
        }

        return this.nasIpAddress;
    }

    public final String getNasHostName()
    {
        final String methodName = ServerRequest.CNAME + "#getNasHostName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.nasHostName);
        }

        return this.nasHostName;
    }

    public final String getNatAddress()
    {
        final String methodName = ServerRequest.CNAME + "#getNatAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.natAddress);
        }

        return this.natAddress;
    }

    public final ServiceRegion getServerRegion()
    {
        final String methodName = ServerRequest.CNAME + "#getServerRegion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverRegion);
        }

        return this.serverRegion;
    }

    public final ServerStatus getServerStatus()
    {
        final String methodName = ServerRequest.CNAME + "#getServerStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverStatus);
        }

        return this.serverStatus;
    }

    public final ServerType getServerType()
    {
        final String methodName = ServerRequest.CNAME + "#getServerType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverType);
        }

        return this.serverType;
    }

    public final String getServerComments()
    {
        final String methodName = ServerRequest.CNAME + "#getServerComments()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverComments);
        }

        return this.serverComments;
    }

    public final int getDmgrPort()
    {
        final String methodName = ServerRequest.CNAME + "#getAssignedEngineer()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dmgrPort);
        }

        return this.dmgrPort;
    }

    public final String getMgrUrl()
    {
        final String methodName = ServerRequest.CNAME + "#getMgrUrl()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.mgrUrl);
        }

        return this.mgrUrl;
    }

    public final String getCpuType()
    {
        final String methodName = ServerRequest.CNAME + "#getCpuType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.cpuType);
        }

        return this.cpuType;
    }

    public final int getCpuCount()
    {
        final String methodName = ServerRequest.CNAME + "#getCpuCount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.cpuCount);
        }

        return this.cpuCount;
    }

    public final String getServerRack()
    {
        final String methodName = ServerRequest.CNAME + "#getServerRack()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverRack);
        }

        return this.serverRack;
    }

    public final String getServerModel()
    {
        final String methodName = ServerRequest.CNAME + "#getServerModel()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverModel);
        }

        return this.serverModel;
    }

    public final String getRackPosition()
    {
        final String methodName = ServerRequest.CNAME + "#getRackPosition()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.rackPosition);
        }

        return this.rackPosition;
    }

    public final String getSerialNumber()
    {
        final String methodName = ServerRequest.CNAME + "#getSerialNumber()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serialNumber);
        }

        return this.serialNumber;
    }

    public final String getOwningDmgr()
    {
        final String methodName = ServerRequest.CNAME + "#getOwningDmgr()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.owningDmgr);
        }

        return this.owningDmgr;
    }

    public final int getInstalledMemory()
    {
        final String methodName = ServerRequest.CNAME + "#getInstalledMemory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.installedMemory);
        }

        return this.installedMemory;
    }

    public final NetworkPartition getNetworkPartition()
    {
        final String methodName = ServerRequest.CNAME + "#getNetworkPartition()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.networkPartition);
        }

        return this.networkPartition;
    }

    public final String getDatacenter()
    {
        final String methodName = ServerRequest.CNAME + "#getDatacenter()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.datacenter);
        }

        return this.datacenter;
    }

    @Override
    public final String toString()
    {
        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + Constants.LINE_BREAK + "{" + Constants.LINE_BREAK);

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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + Constants.LINE_BREAK);
                    }
                }
                catch (final IllegalAccessException iax) {}
            }
        }

        sBuilder.append('}');

        return sBuilder.toString();
    }
}