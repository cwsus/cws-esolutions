<%--
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
 *
 */
--%>
<%--
/**
 * Project: eSolutions_web_source
 * Package: com.cws.esolutions.web.server-management\jsp\html\en
 * File: SystemMgmt_ViewServer.jsp
 *
 * @author cws-khuntly
 * @version 1.0
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
--%>

<div id="homecontent">
    <div class="wrapper">
        <div id="error"></div>
        <div id="validationError" style="color: #FF0000"></div>
    
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

	    <h1><spring:message code="server.mgmt.view.server" arguments="${server.operHostName}" /></h1>
	    <p>
	        <table>
	            <tr>
	                <%-- OS name/type --%>
	                <td><label id="txtOsName"><spring:message code="server.mgmt.os.name" /></label></td>
	                <td>${server.osName}</td>
	                <%-- domain name --%>
	                <td><label id="txtDomainName"><spring:message code="server.mgmt.domain.name" /></label></td>
	                <td>${server.domainName}</td>
	            </tr>
	            <tr>
	                <td><label id="txtServerType"><spring:message code="server.mgmt.server.type" /></label></td>
	                <td>${server.serverType}</td>
	                <td><label id="txtServerStatus"><spring:message code="server.mgmt.server.status" /></label></td>
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
	            </tr>
	            <tr>
	                <td><label id="txtServerRegion"><spring:message code="server.mgmt.server.region" /></label></td>
	                <td>${server.serverRegion}</td>
	                <td><label id="txtServerDatacenter"><spring:message code="server.mgmt.server.datacenter" /></label></td>
	                <td>
	                    <a href="${pageContext.request.contextPath}/ui/service-management/datacenter/${server.datacenter.guid}"
	                        title="${server.datacenter.name}">${server.datacenter.name}</a>
	                </td>
	            </tr>
	            <c:if test="${not empty fn:trim(server.virtualId)}">
	                <tr>
	                    <td><label id="txtVirtualId"><spring:message code="server.mgmt.virtual.id" /></label></td>
	                    <td>${server.virtualId}</td>
	                </tr>
	            </c:if>
	        </table>
	        <c:if test="${server.serverType eq 'DMGRSERVER' or server.serverType eq 'VIRTUALHOST'}">
	            <table id="applicationDetail">
	                <tr>
	                    <td><label id="txtManagerUrl"><spring:message code="server.mgmt.manager.url" /></label></td>
	                    <td><a href="${server.mgrUrl}" title="${server.operHostName}">${server.mgrUrl}</a></td>
	                </tr>
	            </table>
	        </c:if>
	        <table>
	            <tr>
	                <td><label id="txtCpuType"><spring:message code="server.mgmt.cpu.type" /></label></td>
	                <td>${server.cpuType}</td>
	                <td><label id="txtCpuCount"><spring:message code="server.mgmt.cpu.count" /></label></td>
	                <td>${server.cpuCount}</td>
	            </tr>
	            <tr>
	                <td><label id="txtInstalledMemory"><spring:message code="server.mgmt.installed.memory" /></label></td>
	                <td>${server.installedMemory}</td>
	                <td><label id="txtServerModel"><spring:message code="server.mgmt.server.model" /></label></td>
	                <td>${server.serverModel} - ${server.serialNumber}</td>
	            </tr>
	            <c:if test="${not empty fn:trim(server.serverRack)}">
	                <tr>
	                    <td><label id="txtServerRack"><spring:message code="server.mgmt.server.rack" /></label></td>
	                    <td>${server.serverRack}</td>
	                    <td><label id="txtRackPosition"><spring:message code="server.mgmt.rack.position" /></label></td>
	                    <td>${server.rackPosition}</td>
	                </tr>
	            </c:if>
	        </table>
	        <table>
	            <tr>
	                <td><label id="txtOperHostname"><spring:message code="server.mgmt.oper.name" /></label></td>
	                <td>${server.operHostName}</td>
	                <td><label id="txtOperAddress"><spring:message code="server.mgmt.oper.address" /></label></td>
	                <td>${server.operIpAddress}</td>
	            </tr>
	            <c:if test="${not empty fn:trim(server.mgmtHostName)}">
	                <tr>
	                    <td><label id="txtMgmtHostname"><spring:message code="server.mgmt.mgmt.name" /></label></td>
	                    <td>${server.mgmtHostName}</td>
	                    <td><label id="txtMgmtAddress"><spring:message code="server.mgmt.mgmt.address" /></label></td>
	                    <td>${server.mgmtIpAddress}</td>
	                </tr>
	            </c:if>
	            <c:if test="${not empty fn:trim(server.bkHostName)}">
	                <tr>
	                    <td><label id="txtBackupHostname"><spring:message code="server.mgmt.backup.name" /></label></td>
	                    <td>${server.bkHostName}</td>
	                    <td><label id="txtBackupAddress"><spring:message code="server.mgmt.backup.address" /></label></td>
	                    <td>${server.bkIpAddress}</td>
	                </tr>
	            </c:if>
	            <c:if test="${not empty fn:trim(server.nasHostName)}">
	                <tr>
	                    <td><label id="txtNasHostname"><spring:message code="server.mgmt.nas.name" /></label></td>
	                    <td>${server.nasHostName}</td>
	                    <td><label id="txtNasAddress"><spring:message code="server.mgmt.nas.address" /></label></td>
	                    <td>${server.nasIpAddress}</td>
	                </tr>
	            </c:if>
	            <c:if test="${not empty fn:trim(server.natAddress)}">
	                <tr>
	                    <td><label id="txtNatAddress"><spring:message code="server.mgmt.nat.address" /></label></td>
	                    <td>${server.natAddress}</td>
	                </tr>
	            </c:if>
	        </table>
	        <table>
	            <tr>
	                <td><label id="txtServerEngineer"><spring:message code="server.mgmt.assigned.engineer" /></label></td>
	                <td>
	                    <a href="${pageContext.request.contextPath}/ui/user-management/view/account/${server.assignedEngineer.guid}"
	                        title="${server.assignedEngineer.username}">${server.assignedEngineer.username}</a>
	                </td>
	            </tr>
	            <tr>
	                <td><label id="txtServerComments"><spring:message code="server.mgmt.server.comments" /></label></td>
	                <td>${server.serverComments}</td>
	            </tr>
	        </table>
	    </p>
    </div>
</div>

<div id="container">
    <div class="wrapper">
        <div id="holder">
		    <h1><spring:message code="server.mgmt.header" /></h1>
		    <ul>
		        <li><a href="${pageContext.request.contextPath}/ui/server-management/default" title="<spring:message code='theme.search.banner' />"><spring:message code='theme.search.banner' /></a></li>
		        <li><a href="${pageContext.request.contextPath}/ui/server-management/service-consoles" title="<spring:message code='server.mgmt.service.consoles' />"><spring:message code='server.mgmt.service.consoles' /></a></li>
		        <li><a href="${pageContext.request.contextPath}/ui/server-management/add-server" title="<spring:message code='server.mgmt.add.server' />"><spring:message code="server.mgmt.add.server" /></a></li>
		        <li><a href="${pageContext.request.contextPath}/ui/system-check/netstat/server/${server.serverGuid}" title="<spring:message code='system.check.netstat' />"><spring:message code='system.check.netstat' /></a></li>
		        <li><a href="${pageContext.request.contextPath}/ui/system-check/remote-date/server/${server.serverGuid}" title="<spring:message code='system.check.date' />"><spring:message code='system.check.date' /></a></li>
		        <li><a href="${pageContext.request.contextPath}/ui/system-check/telnet/server/${server.serverGuid}" title="<spring:message code='system.check.telnet' />"><spring:message code='system.check.telnet' /></a></li>
		        <li><a href="${pageContext.request.contextPath}/ui/server-management/install-software" title="<spring:message code='server.mgmt.add.server' />"><spring:message code="server.mgmt.add.server" /></a></li>
		        <li><a href="${pageContext.request.contextPath}/ui/server-management/server-control" title="<spring:message code='server.mgmt.server.control.header' />"><spring:message code='server.mgmt.server.control.header' /></a></li>
		    </ul>
        </div>
        <br class="clear" />
    </div>
</div>
