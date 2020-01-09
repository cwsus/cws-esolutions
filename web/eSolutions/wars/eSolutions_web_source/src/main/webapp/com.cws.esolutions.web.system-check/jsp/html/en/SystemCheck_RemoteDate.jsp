<%--
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
 *
 */
--%>
<%--
/**
 * Project: eSolutions_web_source
 * Package: com.cws.esolutions.web.system-check\jsp\html\en
 * File: SystemCheck_RemoteDate.jsp
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

<script>
<!--
    function validateForm(theForm)
    {
        if (theForm.targetServer.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'A target host must be provided.';
            document.getElementById('txtTargetHostName').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('targetServer').focus();
        }
        else
        {
            theForm.submit();
        }
    }
//-->
</script>

<div id="sidebar">
    <h1><spring:message code="system.check.header" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/system-management/add-server" title="<spring:message code='system.mgmt.add.server' />"><spring:message code="system.mgmt.add.server" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/system-check/list-processes/server/${server.serverGuid}" title="<spring:message code='system.check.processlist' />"><spring:message code='system.check.processlist' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/system-check/netstat/server/${server.serverGuid}" title="<spring:message code='system.check.netstat' />"><spring:message code='system.check.netstat' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/system-check/telnet/server/${server.serverGuid}" title="<spring:message code='system.check.telnet' />"><spring:message code='system.check.telnet' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/system-management/install-software" title="<spring:message code='system.mgmt.add.server' />"><spring:message code="system.mgmt.add.server" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/system-management/server-control" title="<spring:message code='system.mgmt.server.control.header' />"><spring:message code='system.mgmt.server.control.header' /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="system.check.remote.date" arguments="${server.operHostName}" /></h1>

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
        <spring:message code="svc.check.remotedate.request.return.message" arguments="${server.operHostName}, ${response}" />
    </p>
</div>

<div id="rightbar">&nbsp;</div>
