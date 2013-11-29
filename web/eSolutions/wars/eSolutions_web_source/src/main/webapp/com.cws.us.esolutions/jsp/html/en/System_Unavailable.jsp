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
 * com.cws.us.esolutions.application-management/jsp/html/en
 * AppMgmt_ViewFile.jsp
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
    <h1>Application Management</h1>
    <ul class="sidemenu">
        <li>
            <a href="${pageContext.request.contextPath}/ui/application-management/retrieve-files/application/${application.applicationGuid}"
                title="<spring:message code='app.mgmt.application.retrieve.files' />"><spring:message code="app.mgmt.application.retrieve.files" /></a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/ui/application-management/deploy-application/application/${application.applicationGuid}"
                title="<spring:message code='app.mgmt.application.deploy' />"><spring:message code="app.mgmt.application.deploy" /></a>
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

    <spring:message code="theme.system.service.unavailable" />

    <c:choose>
        <c:when test="${empty fn:trim(sessionScope.userAccount) or empty fn:trim(sessionScope.userAccount.status)}">
            <p>
                <a href="${pageContext.request.contextPath}/ui/login/default" title="<spring:message code='theme.navbar.login' />">
                    <spring:message code="theme.click.continue" /></a>
            </p>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${sessionScope.userAccount.status == 'SUCCESS'}">
                    <p>
                        <a href="${pageContext.request.contextPath}/ui/common/default" title="<spring:message code='theme.navbar.home' />">
                            <spring:message code="theme.click.continue" /></a>
                    </p>
                </c:when>
                <c:otherwise>
                    <p>
                        <a href="${pageContext.request.contextPath}/ui/login/default" title="<spring:message code='theme.navbar.login' />">
                            <spring:message code="theme.click.continue" /></a>
                    </p>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</div>
