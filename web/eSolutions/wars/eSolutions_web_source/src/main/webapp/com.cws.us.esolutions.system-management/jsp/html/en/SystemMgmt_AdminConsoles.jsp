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
 * com.cws.us.esolutions.user-management/jsp/html/en
 * UserManagement_ViewUser.jsp
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

<div id="sidebar">
    <h1><spring:message code="svc.mgmt.header" /></h1>
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

<div id="main">
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

    <h1><spring:message code="system.mgmt.view.server" arguments="${server.operHostName}" /></h1>
    <table id="consoles">
        <tr>
            <c:forEach var="dmgr" items="${serverList}">
                <td><a href="${dmgr.mgrUrl}" title="${dmgr.operHostName}">${dmgr.operHostName}</a></td>
            </c:forEach>
        </tr>
    </table>
</div>
