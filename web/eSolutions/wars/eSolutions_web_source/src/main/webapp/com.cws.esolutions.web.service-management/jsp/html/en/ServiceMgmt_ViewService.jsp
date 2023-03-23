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
 * Package: com.cws.esolutions.web.service-management\jsp\html\en
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
    <h1><spring:message code="svc.mgmt.header" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/modify-service/platform/${platform.guid}" title="<spring:message code='svc.mgmt.update.service' />"><spring:message code="svc.mgmt.update.service" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/list-datacenters" title="<spring:message code='svc.mgmt.list.datacenters' />"><spring:message code="svc.mgmt.list.datacenters" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/add-datacenter" title="<spring:message code='svc.mgmt.add.datacenter' />"><spring:message code="svc.mgmt.add.datacenter" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/list-platforms" title="<spring:message code='svc.mgmt.list.platforms' />"><spring:message code="svc.mgmt.list.platforms" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/add-platform" title="<spring:message code='svc.mgmt.add.platform' />"><spring:message code="svc.mgmt.add.platform" /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="svc.mgmt.view.platform" arguments="${platform.name}" /></h1>

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
                <td><label id="txtPlatformName"><spring:message code="svc.mgmt.service.name" /></label></td>
                <td>${platform.name}</td>
                <td><label id="txtPlatformStatus"><spring:message code="svc.mgmt.service.status" /></label></td>
                <td>${platform.status}</td>
                <td><label id="txtPlatformRegion"><spring:message code="svc.mgmt.platform.region" /></label></td>
                <td>${platform.region}</td>
            </tr>
            <tr>
                <td><label id="txtPlatformDescription"><spring:message code="svc.mgmt.service.description" /></label></td>
                <td>${platform.description}</td>
            </tr>
        </table>
        <br />
        <label id="txtPlatformServers"><spring:message code="svc.mgmt.platform.servers" /></label>
        <table>
            <tr>
                <td><spring:message code="system.mgmt.oper.name" /></td>
                <td><spring:message code="system.mgmt.server.region" /></td>
            </tr>
            <tr>
                <c:forEach var="server" items="${platform.servers}">
                    <c:set var="appCount" value="${appCount + 1}" scope="page" />

                    <c:if test="${appCount eq 4}">
                        <tr>
                    </c:if>
                    <td>
                        <a href="${pageContext.request.contextPath}/ui/system-management/server/${server.serverGuid}"
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
    </p>
</div>

<div id="rightbar">&nbsp;</div>
