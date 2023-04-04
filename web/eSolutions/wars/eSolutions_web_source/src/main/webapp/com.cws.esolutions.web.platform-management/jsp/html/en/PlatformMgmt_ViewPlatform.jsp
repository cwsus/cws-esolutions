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
 * Package: com.cws.esolutions.web.platform-management\jsp\html\en
 * File: ServiceMgmt_ViewService.jsp
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

<div id="sidebar">
    <h1><spring:message code="platform.mgmt.header" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/platform-management/modify-service/platform/${platform.guid}" title="<spring:message code='platform.mgmt.update.service' />"><spring:message code="platform.mgmt.update.service" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/datacenter-management/list-datacenters" title="<spring:message code='platform.mgmt.list.datacenters' />"><spring:message code="platform.mgmt.list.datacenters" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/datacenter-management/add-datacenter" title="<spring:message code='platform.mgmt.add.datacenter' />"><spring:message code="platform.mgmt.add.datacenter" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/platform-management/list-platforms" title="<spring:message code='platform.mgmt.list.platforms' />"><spring:message code="platform.mgmt.list.platforms" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/platform-management/add-platform" title="<spring:message code='platform.mgmt.add.platform' />"><spring:message code="platform.mgmt.add.platform" /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="platform.mgmt.view.platform" arguments="${platform.name}" /></h1>
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

    <p>
        <table id="platformDetail">
            <tr>
                <td><label id="txtPlatformName"><spring:message code="platform.mgmt.service.name" /></label></td>
                <td>${platform.name}</td>
                <td><label id="txtPlatformStatus"><spring:message code="platform.mgmt.service.status" /></label></td>
                <td>${platform.status}</td>
                <td><label id="txtPlatformRegion"><spring:message code="platform.mgmt.platform.region" /></label></td>
                <td>${platform.region}</td>
            </tr>
            <tr>
                <td><label id="txtPlatformDescription"><spring:message code="platform.mgmt.service.description" /></label></td>
                <td>${platform.description}</td>
            </tr>
        </table>
        <c:if test="${not empty platform.servers}">
	        <br />
	        <label id="txtPlatformServers"><spring:message code="platform.mgmt.platform.servers" /></label>
	        <table>
	            <tr>
	                <td><spring:message code="server.mgmt.oper.name" /></td>
	                <td><spring:message code="server.mgmt.server.region" /></td>
	            </tr>
	            <tr>
	                <c:forEach var="server" items="${platform.servers}">
	                    <c:set var="appCount" value="${appCount + 1}" scope="page" />
	
	                    <c:if test="${appCount eq 4}">
	                        <tr>
	                    </c:if>
	                    <td>
	                        <a href="${pageContext.request.contextPath}/ui/server-management/server/${server.serverGuid}"
	                            title="${server.operHostName}">${server.operHostName}</a>
	                    </td>
	                    <td>${server.serverRegion}</td>
	                    <c:if test="${appCount eq 4}">
	                        <c:set var="appCount" value="0" scope="page" /> <%-- reset the counter --%>
	                        </tr>
	                    </c:if>
	                </c:forEach>
	            </tr>
	        </table>
        </c:if>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
