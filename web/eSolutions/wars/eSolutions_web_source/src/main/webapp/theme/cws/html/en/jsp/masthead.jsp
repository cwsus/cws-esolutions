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
 * masthead.jsp
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

<div id="masthead">
    <img src="/html/esolutions/img/logo.gif" alt="eSolutions" title="eSolutions" />

    <c:if test="${not empty sessionScope.userAccount and sessionScope.userAccount.status ne 'EXPIRED'}">
        <c:if test="${sessionScope.userAccount.role ne 'USERADMIN'}">
            <div id="globalNav">
                <a href="${pageContext.request.contextPath}/ui/application-management/default" title="<spring:message code='link.globalNav.application-mgmt' />">
                    <spring:message code='link.globalNav.application-mgmt' /></a> |
                <a href="${pageContext.request.contextPath}/ui/dns-service/default" title="<spring:message code='link.globalNav.dns-services' />">
                   <spring:message code='link.globalNav.dns-services' /></a> |
                <a href="${pageContext.request.contextPath}/ui/service-management/default" title="<spring:message code='link.globalNav.service-mgmt' />">
                   <spring:message code='link.globalNav.service-mgmt' /></a> |
                <a href="${pageContext.request.contextPath}/ui/system-management/default" title="<spring:message code='link.globalNav.system-mgmt' />">
                    <spring:message code='link.globalNav.system-mgmt' /></a>
            </div>
        </c:if>

        <div id="breadCrumb">
            <spring:message code="welcome.message" arguments="${sessionScope.userAccount.username}, ${sessionScope.userAccount.lastLogin}" />
            <br />
            <c:if test="${sessionScope.userAccount.role ne 'USERADMIN'}">
                | <a href="${pageContext.request.contextPath}/ui/user-account/default" title="<spring:message code='link.breadcrumb.account' />">
                    <spring:message code='link.breadcrumb.account' /></a> |
                <a href="${pageContext.request.contextPath}/ui/messaging/default" title="<spring:message code='link.breadcrumb.messaging' />">
                    <spring:message code='link.breadcrumb.messaging' /></a> |
                <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                    <a href="${pageContext.request.contextPath}/ui/user-management/default" title="<spring:message code='link.breadcrumb.useradmin' />">
                        <spring:message code='link.breadcrumb.useradmin' /></a> |
                </c:if>
            </c:if>
        </div>
    </c:if>
</div>
