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

<div id="InfoLine"><spring:message code="user.mgmt.audit.trail" arguments="${userAccount.username}" /></div>
<div id="content">
    <div id="content-right">
	    <c:if test="${not empty messageResponse}">
	        <p id="info">${messageResponse}</p>
	    </c:if>
	    <c:if test="${not empty errorResponse}">
	        <p id="error">${errorResponse}</p>
	    </c:if>
	    <c:if test="${not empty responseMessage}">
	        <p id="info"><spring:message code="${responseMessage}" /></p>
	    </c:if>
	    <c:if test="${not empty errorMessage}">
	        <p id="error"><spring:message code="${errorMessage}" /></p>
	    </c:if>

        <c:choose>
            <c:when test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
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
	                                    
	                                        <a href="${pageContext.request.contextPath}/user-management/audit/account/${userAccount.guid}/page/${i}"
	                                            title="{i}">${i}</a>
	                                    </td>
	                                </c:otherwise>
	                            </c:choose>
	                        </c:forEach>
	                    </tr>
	                </table>
	            </c:if>
	        </c:when>
	        <c:otherwise>
	            <spring:message code="theme.system.request.unauthorized" />
	            <c:if test="${requestScope.isUserLoggedIn ne 'true'}">
	                <p>Click <a href="${pageContext.request.contextPath}/ui/home/default" title="Home">here</a> to continue.</p>
	            </c:if>
	        </c:otherwise>
	    </c:choose>
    </div>

            <div id="breadcrumb" class="lpstartover">

            </div>

    <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
        <div id="content-left">
            <ul>
                <li>
                    <a href="${pageContext.request.contextPath}/ui/user-management/view/account/${userAccount.guid}"
                        title="<spring:message code='theme.previous.page' />"><spring:message code='theme.previous.page' /></a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/ui/user-management/add-user"
                        title="<spring:message code='user.mgmt.create.user' />"><spring:message code="user.mgmt.create.user" /></a>
                </li>
            </ul>
        </div>
    </c:if>
</div>
