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
 * File: SystemMgmt_AdminConsoles.jsp
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
    <h1><spring:message code="server.mgmt.header" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/server-management/default" title="<spring:message code='theme.search.banner' />"><spring:message code='theme.search.banner' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/server-management/add-server" title="<spring:message code='server.mgmt.add.server' />"><spring:message code="server.mgmt.add.server" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/system-check/netstat/server/${server.serverGuid}" title="<spring:message code='system.check.netstat' />"><spring:message code='system.check.netstat' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/system-check/remote-date/server/${server.serverGuid}" title="<spring:message code='system.check.date' />"><spring:message code='system.check.date' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/system-check/telnet/server/${server.serverGuid}" title="<spring:message code='system.check.telnet' />"><spring:message code='system.check.telnet' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/server-management/install-software" title="<spring:message code='server.mgmt.add.server' />"><spring:message code="server.mgmt.add.server" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/server-management/server-control" title="<spring:message code='server.mgmt.server.control.header' />"><spring:message code='server.mgmt.server.control.header' /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="server.mgmt.admin.consoles" /></h1>

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
        <table id="consoles">
            <tr>
                <c:forEach var="dmgr" items="${serverList}">
                    <td><a href="${dmgr.mgrUrl}" title="${dmgr.operHostName}">${dmgr.operHostName}</a></td>
                </c:forEach>
            </tr>
        </table>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
