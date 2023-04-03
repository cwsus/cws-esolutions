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
 * File: SystemMgmt_ServerControl.jsp
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
    <h1><spring:message code="system.mgmt.header" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/server-management/service-consoles" title="<spring:message code='system.mgmt.service.consoles' />"><spring:message code='system.mgmt.service.consoles' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/server-management/add-server" title="<spring:message code='system.mgmt.add.server' />"><spring:message code="system.mgmt.add.server" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/system-check/netstat/server/${server.serverGuid}" title="<spring:message code='system.check.netstat' />"><spring:message code='system.check.netstat' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/system-check/remote-date/server/${server.serverGuid}" title="<spring:message code='system.check.date' />"><spring:message code='system.check.date' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/system-check/telnet/server/${server.serverGuid}" title="<spring:message code='system.check.telnet' />"><spring:message code='system.check.telnet' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/server-management/install-software" title="<spring:message code='system.mgmt.add.server' />"><spring:message code="system.mgmt.add.server" /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="system.mgmt.server.control.header" /></h1>

    <div id="error"></div>

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
        <form:form id="controlServer" name="controlServer" action="${pageContext.request.contextPath}/ui/server-management/server-control" method="post">
            <form:hidden path="serverGuid" value="${server.serverGuid}" />

            <label id="txtOperationType"><spring:message code="system.mgmt.server.region" /></label>
            <form:select path="operationType">
                <form:options items="${operationTypes}" />
            </form:select>
            <form:errors path="operationType" cssClass="error" />
            <br /><br />
            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="redirectOnCancel('/esolutions/ui/server-management/default');" />
        </form:form>
    </p>
</div>

<div id="rightbar">
    <c:if test="${not empty fn:trim(response)}">
        ${response}
    </c:if>
</div>
