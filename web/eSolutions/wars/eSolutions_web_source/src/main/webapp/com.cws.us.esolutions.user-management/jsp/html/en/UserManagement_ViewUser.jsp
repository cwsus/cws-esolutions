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

<div class="feature">
    <c:choose>
        <c:when test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN' or sessionScope.userAccount.role eq 'ADMIN'}">
            <div id="breadcrumb" class="lpstartover">
                <a href="${pageContext.request.contextPath}/ui/user-management/add-user"
                    title="<spring:message code='admin.account.create.user' />"><spring:message code="admin.account.create.user" /></a> /
                <c:if test="${auditEnabled eq 'TRUE'}">
                    <a href="${pageContext.request.contextPath}/ui/user-management/audit-user/user/${userAccount.guid}"
                        title="<spring:message code='admin.account.audit.user' />"><spring:message code='admin.account.audit.user' /></a>
                </c:if>
            </div>

            <c:if test="${not empty messageResponse}">
                <p id="info">${messageResponse}</p>
            </c:if>
            <c:if test="${not empty errorResponse}">
                <p id="error">${errorResponse}</p>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <p id="error"><spring:message code="${errorMessage}" /></p>
            </c:if>

            <table id="viewUser">
                <tr>
                    <td><spring:message code="admin.account.user.id" /></td>
                    <td>${userAccount.username}</td>
                </tr>
                <tr>
                    <td><spring:message code="admin.account.user.password" /></td>
                    <td><spring:message code="admin.password.display.indicator" /></td>
                    <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role == 'SITEADMIN'}">
                        <td>
                            <a href="${pageContext.request.contextPath}/ui/user-management/reset" title="<spring:message code='admin.account.reset.user.password' />">
                                <spring:message code='admin.account.reset.user.password' /></a>
                        </td>
                    </c:if>
                </tr>
                <tr>
                    <td><spring:message code="admin.account.user.role" /></td>
                    <td>${userAccount.role}</td>
                </tr>
                <tr>
                    <td><spring:message code="admin.account.user.firstname" /></td>
                    <td>${userAccount.givenName}</td>
                </tr>
                <tr>
                    <td><spring:message code="admin.account.user.lastname" /></td>
                    <td>${userAccount.surname}</td>
                </tr>
                <tr>
                    <td><spring:message code="admin.account.user.email" /></td>
                    <td>
                        <a href="mailto:${userAccount.emailAddr}?subject=Contact Message" title="contact message">${userAccount.emailAddr}</a>
                    </td>
                </tr>
                <tr>
                    <td><spring:message code="admin.account.user.locked" /></td>
                    <td>${userAccount.failedCount}</td>
                    <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                        <c:if test="${userAccount.failedCount ge 3}">
                            <td>
                                <a href="${pageContext.request.contextPath}/ui/user-management/unlock"
                                    title="<spring:message code='admin.account.unlock.user.account' />"><spring:message code='admin.account.unlock.user.account' /></a>
                            </td>
                        </c:if>
                        <c:if test="${userAccount.failedCount le 3}">
                            <td>
                                <a href="${pageContext.request.contextPath}/ui/user-management/lock"
                                    title="<spring:message code='admin.account.lock.user.account' />"><spring:message code='admin.account.lock.user.account' /></a>
                            </td>
                        </c:if>
                    </c:if>
                </tr>
                <tr>
                    <td><spring:message code="admin.user.last.logon" /></td>
                    <td>${userAccount.lastLogin}</td>
                </tr>
                <tr>
                    <td><spring:message code="admin.account.user.suspension" /></td>
                    <td>${userAccount.suspended}</td>
                    <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                        <c:choose>
                            <c:when test="${userAccount.suspended eq 'true'}">
                                <td>
                                    <a href="${pageContext.request.contextPath}/ui/user-management/unsuspend"
                                          title="<spring:message code='admin.account.remove.user.suspension' />"><spring:message code='admin.account.remove.user.suspension' /></a>
                                </td>
                            </c:when>
                            <c:otherwise>
                                <td>
                                    <a href="${pageContext.request.contextPath}/ui/user-management/suspend"
                                          title="<spring:message code='admin.account.suspend.user' />"><spring:message code='admin.account.suspend.user' /></a>
                                </td>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </tr>
            </table>
        </c:when>
        <c:otherwise>
            <spring:message code="admin.account.not.authorized" />
            <c:if test="${requestScope.isUserLoggedIn ne 'true'}">
                <p>Click <a href="${pageContext.request.contextPath}/ui/home/default" title="Home">here</a> to continue.</p>
            </c:if>
        </c:otherwise>
    </c:choose>
</div>
<br /><br />
