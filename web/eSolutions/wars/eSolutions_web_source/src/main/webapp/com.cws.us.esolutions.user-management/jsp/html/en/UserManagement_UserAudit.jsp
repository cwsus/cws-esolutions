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
 * UserManagement_UserAudit.jsp
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
    <h1><spring:message code="user.mgmt.header" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/user-management/default" title="<spring:message code='theme.search.banner' />"><spring:message code="theme.search.banner" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-management/add-user" title="<spring:message code='user.mgmt.create.user' />"><spring:message code="user.mgmt.create.user" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-management/view/account/${userAccount.guid}" title="<spring:message code='theme.previous.page' />"><spring:message code='theme.previous.page' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-management/services/account/${userAccount.guid}" title="<spring:message code='user.mgmt.user.services' />"><spring:message code='user.mgmt.user.services' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-management/lock/account/${userAccount.guid}" title="<spring:message code='user.mgmt.lock.account' />"><spring:message code='user.mgmt.lock.account' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-management/suspend/account/${userAccount.guid}" title="<spring:message code='user.mgmt.suspend.account' />"><spring:message code='user.mgmt.suspend.account' /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="user.mgmt.audit.trail" arguments="${userAccount.username}" /></h1>

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
        <c:if test="${not empty auditEntries}">
            <table id="viewAuditTrail">
                <tr>
                    <td><spring:message code="user.mgmt.audit.timestamp" /></td>
                    <td><spring:message code="user.mgmt.audit.type" /></td>
                    <td><spring:message code="user.mgmt.audit.application" /></td>
                    <td><spring:message code="user.mgmt.audit.hostinfo" /></td>
                </tr>
                <c:forEach var="entry" items="${auditEntries}">
                    <tr>
                        <td>${entry.auditDate}</td>
                        <td>${entry.auditType}</td>
                        <td>${entry.applicationName}</td>
                        <td>${entry.hostInfo.hostName} / ${entry.hostInfo.hostAddress}</td>
                    </tr>
                </c:forEach>
            </table>

            <c:if test="${pages gt 1}">
                <br />
                <hr />
                <br />
                <table>
                    <tr>
                        <c:forEach begin="1" end="${pages}" var="i">
                            <c:choose>
                                <c:when test="${page eq i}">
                                    <td>${i}</td>
                                </c:when>
                                <c:otherwise>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/user-management/audit/account/${userAccount.guid}/page/${i}" title="{i}">${i}</a>
                                    </td>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </tr>
                </table>
            </c:if>
        </c:if>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
