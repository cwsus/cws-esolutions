<%--
/**
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
 * com.cws.us.esolutions/jsp/html/en
 * CommonLandingPage.jsp
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

<div id="sidebar">&nbsp;</div>

<div id="main">
    <h1><spring:message code="theme.welcome.back" arguments="${sessionScope.userAccount.givenName}" /></h1>

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
        <c:choose>
            <c:when test="${not empty messageList}">
                <c:forEach var="message" items="${messageList}">
                    <div id="svcmessage">
                        <h3>${message.messageTitle}</h3>
                        <div class="feature">
                            ${message.messageText}
                        </div>

                        <p class="post-footer align-right">
                            <spring:message code="svc.messaging.system.message.author" />: <a href="mailto:${message.messageAuthor.emailAddr}?subject=${message.messageId}" title="<spring:message code='svc.messaging.system.message.author' />">${message.messageAuthor.username}</a><br />
                            <spring:message code="svc.messaging.system.message.submit.date" /><span class="date"><fmt:formatDate value="${message.submitDate}" pattern="${dateFormat}" /></span><br />
                            <c:if test="${not empty fn:trim(message.expiryDate)}">
                                <spring:message code="svc.messaging.system.message.expiry.date" /><span class="date"><fmt:formatDate value="${message.expiryDate}" pattern="${dateFormat}" /></span>
                            </c:if>
                        </p>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <spring:message code="svc.messaging.no.system.messages" />
            </c:otherwise>
        </c:choose>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
