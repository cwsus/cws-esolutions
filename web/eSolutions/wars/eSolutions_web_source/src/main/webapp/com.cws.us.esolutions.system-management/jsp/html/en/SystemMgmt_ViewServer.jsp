<%--
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
 *
 * eSolutions_web_source
 * com.cws.us.esolutions.system-management/jsp/html/en
 * SystemManagement_ViewServer.jsp
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author kh05451
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 16, 2013 11:53:26 AM
 *     Created.
 */
--%>

<script type="text/javascript">
    <!--
    function changeView()
    {
        document.getElementById('serverStatusInput').style.display = 'none';
        document.getElementById('serverStatusModify').style.display = 'block';
        document.getElementById('selectStatusChange').style.display = 'none';
        document.getElementById('submitStatusChange').style.display = 'block';
    }

    function submitStatusChange(selectable)
    {
        var newStatus = selectable.options[selectable.selectedIndex].text;

        window.location.href = '${pageContext.request.contextPath}/ui/system-management/change-status/server/${server.serverGuid}/status/' + newStatus;
    }
    //-->
</script>

<div id="InfoLine"><spring:message code="system.mgmt.view.server" arguments="${server.osName}" /></div>
<div id="content">
    <div id="content-right">
	    <c:if test="${not empty fn:trim(messageResponse)}">
	        <p id="info">${messageResponse}</p>
	    </c:if>
	    <c:if test="${not empty fn:trim(errorResponse)}">
	        <p id="error">${errorResponse}</p>
	    </c:if>
	    <c:if test="${not empty fn:trim(responseMessage)}">
	        <p id="info"><spring:message code="${responseMessage}" /></p>
	    </c:if>
	    <c:if test="${not empty fn:trim(errorMessage)}">
	        <p id="error"><spring:message code="${errorMessage}" /></p>
	    </c:if>
	    <c:if test="${not empty fn:trim(param.responseMessage)}">
	        <p id="info"><spring:message code="${param.responseMessage}" /></p>
	    </c:if>
	    <c:if test="${not empty fn:trim(param.errorMessage)}">
	        <p id="error"><spring:message code="${param.errorMessage}" /></p>
	    </c:if>

	    <table id="serverDetail">
	        <tr>
	            <%-- OS name/type --%>
	            <td><label id="txtOsName"><spring:message code="system.mgmt.os.name" /></label>
	            <td>${server.osName}</td>
	            <%-- domain name --%>
	            <td><label id="txtDomainName"><spring:message code="system.mgmt.domain.name" /></label>
	            <td>${server.domainName}</td>
	        </tr>
	        <tr>
	            <td><label id="txtServerType"><spring:message code="system.mgmt.server.type" /></label>
	            <td>${server.serverType}</td>
	            <td><label id="txtServerStatus"><spring:message code="system.mgmt.server.status" /></label>
	            <td id="serverStatusInput" style="display: block;">${server.serverStatus}</td>
	            <td id="serverStatusModify" style="display: none;">
                    <select name="status" id="status">
                        <option value="${server.serverStatus}" selected="selected">${server.serverStatus}</option>
                        <option><spring:message code="theme.option.select" /></option>
                        <option><spring:message code="theme.option.spacer" /></option>
                        <c:forEach var="serverStatus" items="${statusList}">
                            <option value="${serverStatus}">${serverStatus}</option>
                        </c:forEach>
                    </select>
                </td>
                <td>
                    <a id="selectStatusChange" href="#" onclick="changeView();"
                        title="<spring:message code='server.mgmt.change.status' />" style="display: block;"><spring:message code='server.mgmt.change.status' /></a>
                    <a id="submitStatusChange" href="#" onclick="submitStatusChange(document.getElementById('status'));"
                        title="<spring:message code='server.mgmt.change.status' />" style="display: none;"><spring:message code='server.mgmt.change.status' /></a>
                </td>
	        </tr>
	        <tr>
	            <td><label id="txtServerRegion"><spring:message code="system.mgmt.server.region" /></label>
	            <td>${server.serverRegion}</td>
	            <td><label id="txtServerDatacenter"><spring:message code="system.mgmt.server.datacenter" /></label>
	            <td>
	                <a href="${pageContext.request.contextPath}/ui/service-management/datacenter/${server.datacenter.datacenterGuid}"
	                    title="${server.datacenter.datacenterName}">${server.datacenter.datacenterName}</a>
	            </td>
	        </tr>
	    </table>
	    <c:if test="${server.serverType eq 'DMGRSERVER' or server.serverType eq 'VIRTUALHOST'}">
	        <table id="applicationDetail">
	            <tr>
	                <td><label id="txtManagerUrl"><spring:message code="system.mgmt.manager.url" /></label>
	                <td><a href="${server.mgrUrl}" title="${server.operHostName}">${server.mgrUrl}</a></td>
	            </tr>
	        </table>
	    </c:if>
	    <table id="hardwareDetail">
	        <tr>
	            <td><label id="txtCpuType"><spring:message code="system.mgmt.cpu.type" /></label>  
	            <td>${server.cpuType}</td>
	            <td><label id="txtCpuCount"><spring:message code="system.mgmt.cpu.count" /></label>
	            <td>${server.cpuCount}</td>
	        </tr>
	        <tr>
	            <td><label id="txtInstalledMemory"><spring:message code="system.mgmt.installed.memory" /></label>
	            <td>${server.installedMemory}</td>
	            <td><label id="txtServerModel"><spring:message code="system.mgmt.server.model" /></label>
	            <td>${server.serverModel}</td>
	        </tr>
	        <tr>
	            <td><label id="txtServerRack"><spring:message code="system.mgmt.server.rack" /></label>
	            <td>${server.serverRack}</td>
	            <td><label id="txtRackPosition"><spring:message code="system.mgmt.rack.position" /></label>
	            <td>${server.rackPosition}</td>
	        </tr>
	    </table>
	    <table id="networkDetail">
	        <tr>
	            <td><label id="txtOperHostname"><spring:message code="system.mgmt.oper.name" /></label>  
	            <td>${server.operHostName}</td>
	            <td><label id="txtOperAddress"><spring:message code="system.mgmt.oper.address" /></label>
	            <td>${server.operIpAddress}</td>
	        </tr>
	        <tr>
	            <td><label id="txtMgmtHostname"><spring:message code="system.mgmt.mgmt.name" /></label>
	            <td>${server.mgmtHostName}</td>
	            <td><label id="txtMgmtAddress"><spring:message code="system.mgmt.mgmt.address" /></label>
	            <td>${server.mgmtIpAddress}</td>
	        </tr>
	        <tr>
	            <td><label id="txtBackupHostname"><spring:message code="system.mgmt.backup.name" /></label>
	            <td>${server.bkHostName}</td>
	            <td><label id="txtBackupAddress"><spring:message code="system.mgmt.backup.address" /></label>
	            <td>${server.bkIpAddress}</td>
	        </tr>
	        <tr>
	            <td><label id="txtNasHostname"><spring:message code="system.mgmt.nas.name" /></label>
	            <td>${server.nasHostName}</td>
	            <td><label id="txtNasAddress"><spring:message code="system.mgmt.nas.address" /></label>
	            <td>${server.nasIpAddress}</td>
	        </tr>
	        <tr>
	            <td><label id="txtNatAddress"><spring:message code="system.mgmt.nat.address" /></label>
	            <td>${server.natAddress}</td>
	            <td><label id="txtSerialNumber"><spring:message code="system.mgmt.serial.number" /></label>
	            <td>${server.serialNumber}</td>
	        </tr>
	    </table>
	    <table id="comments">
	        <tr>
	            <td><label id="txtServerComments"><spring:message code="system.mgmt.server.comments" /></label>
	            <td>${server.serverComments}</td>
	        </tr>
	    </table>
    </div>

    <div id="content-left">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/ui/system-management/service-consoles"
                    title="<spring:message code='system.mgmt.service.consoles' />"><spring:message code='system.mgmt.service.consoles' /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/system-management/add-server"
                    title="<spring:message code='system.mgmt.add.server' />"><spring:message code="system.mgmt.add.server" /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/system-check/remote-date/server/${server.serverGuid}"
                    title="<spring:message code='system.check.date' />"><spring:message code="system.check.date" /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/system-check/telnet/server/${server.serverGuid}"
                    title="<spring:message code='system.check.telnet' />"><spring:message code="system.check.telnet" /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/system-check/netstat/server/${server.serverGuid}"
                    title="<spring:message code='system.check.netstat' />"><spring:message code="system.check.netstat" /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/system-check/list-processes/server/${server.serverGuid}"
                    title="<spring:message code='system.check.processlist' />"><spring:message code="system.check.processlist" /></a>
            </li>
        </ul>
    </div>
</div>
