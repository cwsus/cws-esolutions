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
 * theme/cws/html/en/jsp
 * navbar.jsp
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

<div id="menu">
    <ul>
        <c:if test="${not empty fn:trim(sessionScope.userAccount)}">
            <c:if test="${sessionScope.userAccount.status == 'SUCCESS'}">
	            <c:if test="${sessionScope.userAccount.role ne 'USERADMIN'}">
	                <li>
	                    <a href="${pageContext.request.contextPath}/ui/application-management/default" title="<spring:message code='theme.navbar.application-mgmt' />">
	                        <spring:message code='theme.navbar.application-mgmt' /></a>
	                </li>
	                <li>
	                    <a href="${pageContext.request.contextPath}/ui/dns-service/default" title="<spring:message code='theme.navbar.dns-services' />">
	                        <spring:message code='theme.navbar.dns-services' /></a>
	                </li>
	                <li>
	                    <a href="${pageContext.request.contextPath}/ui/service-management/default" title="<spring:message code='theme.navbar.service-mgmt' />">
	                        <spring:message code='theme.navbar.service-mgmt' /></a>
	                </li>
	                <li>
	                    <a href="${pageContext.request.contextPath}/ui/system-management/default" title="<spring:message code='theme.navbar.system-mgmt' />">
	                        <spring:message code='theme.navbar.system-mgmt' /></a>
	                </li>
	                <li>
	                    <a href="${pageContext.request.contextPath}/ui/service-messaging/default" title="<spring:message code='theme.navbar.messaging' />">
	                        <spring:message code='theme.navbar.messaging' /></a>
	                </li>
	            </c:if>
	            <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
	                <li class="last">
	                    <a href="${pageContext.request.contextPath}/ui/user-management/default" title="<spring:message code='theme.navbar.useradmin' />">
	                        <spring:message code='theme.navbar.useradmin' /></a>
	                </li>
	            </c:if>
	        </c:if>
        </c:if>
    </ul>
</div>
