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
    <h1>Common</h1>
    <ul class="sidemenu">
        <li>
            <a href="${pageContext.request.contextPath}/ui/application-management/retrieve-files/application/${application.applicationGuid}"
                title="<spring:message code='app.mgmt.application.retrieve.files' />"><spring:message code="app.mgmt.application.retrieve.files" /></a>
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

    <c:choose>
        <c:when test="${not empty messageList}">
            <spring:message code="svc.messaging.list" />

            <c:forEach var="message" items="${messageList}">
                <h1>${message.messageTitle}</h1>
                <div class="feature">${message.messageText}</div>
                <p class="post-footer align-right">
                    <a href="mailto:${message.authorEmail}?subject=${message.messageId}" title="<spring:message code='svc.messaging.system.message.author' />">${message.messageAuthor}</a>
                    <span class="date">${message.submitDate}</span>
                </p>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <spring:message code="svc.messaging.no.system.messages" />
        </c:otherwise>
    </c:choose>
</div>
