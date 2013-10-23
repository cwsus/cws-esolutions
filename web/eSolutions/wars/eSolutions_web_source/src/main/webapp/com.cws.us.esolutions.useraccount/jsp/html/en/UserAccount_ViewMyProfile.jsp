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
    <c:if test="${not empty messageResponse}">
        <p id="info">${messageResponse}</p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <p id="error"><spring:message code="${errorMessage}" /></p>
    </c:if>

    <spring:message code="user.account.view.profile" />

    <p id="splitter" />

    <table id="viewUserAccount">
        <tr>
            <td><spring:message code="user.account.username" /></td>
            <td>${sessionScope.userAccount.username}</td>
            <td><a href="${pageContext.request.contextPath}/ui/user-account/password"
                    title="<spring:message code='user.account.change.password' />"><spring:message code="user.account.change.password" /></a>
            </td>
        <tr>
        <tr>
            <td><spring:message code="user.account.name" /></td>
            <td>${sessionScope.userAccount.displayName}</td>
        </tr>
        <tr>
            <td><spring:message code="user.account.email.addr" /></td>
            <td>${sessionScope.userAccount.emailAddr}</td>
            <td>
                <a href="${pageContext.request.contextPath}/ui/user-account/email"
                    title="<spring:message code='user.account.change.email' />"><spring:message code="user.account.change.email" /></a>
            </td>
        </tr>
        <tr>
            <td><spring:message code="user.account.security.questions" /></td>
            <td>
                <a href="${pageContext.request.contextPath}/ui/user-account/security"
                    title="<spring:message code='user.account.change.security.questions' />"><spring:message code="user.account.change.security.questions" /></a>
            </td>
        </tr>
    </table>
</div>
<br /><br />
