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
 * com.cws.us.esolutions.useraccount/jsp/html/en
 * UserAccount_ViewMyProfile.jsp
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author cws-khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * cws-khuntly @ Jan 16, 2013 11:53:26 AM
 *     Created.
 */
--%>
<div id="sidebar">
    <h1><spring:message code="user.account.select.options" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/email" title="<spring:message code='user.account.change.email' />"><spring:message code="user.account.change.email" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/contact" title="<spring:message code='user.account.change.contact' />"><spring:message code="user.account.change.contact" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/password" title="<spring:message code='user.account.change.password' />"><spring:message code="user.account.change.password" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/security" title="<spring:message code='user.account.change.security.questions' />"><spring:message code="user.account.change.security.questions" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/regenerate-keys" title="<spring:message code='user.account.change.keys' />"><spring:message code="user.account.change.keys" /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="user.account.view.profile" arguments="${sessionScope.userAccount.displayName}" /></h1>

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
        <table id="viewUserAccount">
            <tr>
                <td><label><spring:message code="user.account.username" /></label></td>
                <td>${sessionScope.userAccount.username}</td>
            </tr>
            <tr>
                <td><label><spring:message code="user.account.name" /></label></td>
                <td>${sessionScope.userAccount.displayName}</td>
            </tr>
            <tr>
                <td><label><spring:message code="user.account.givenname" /></label></td>
                <td>${sessionScope.userAccount.givenName}</td>
            </tr>
            <tr>
                <td><label><spring:message code="user.account.surname" /></label></td>
                <td>${sessionScope.userAccount.surname}</td>
            </tr>
            <tr>
                <td><label><spring:message code="user.account.telephone" /></label></td>
                <td>${sessionScope.userAccount.telephoneNumber}</td>
            </tr>
            <tr>
                <td><label><spring:message code="user.account.pager" /></label></td>
                <td>${sessionScope.userAccount.pagerNumber}</td>
            </tr>
            <tr>
                <td><label><spring:message code="user.account.email.addr" /></label></td>
                <td>${sessionScope.userAccount.emailAddr}</td>
            </tr>
            <tr>
                <td><label><spring:message code="user.account.role" /></label></td>
                <td>${sessionScope.userAccount.role}</td>
            </tr>
        </table>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
