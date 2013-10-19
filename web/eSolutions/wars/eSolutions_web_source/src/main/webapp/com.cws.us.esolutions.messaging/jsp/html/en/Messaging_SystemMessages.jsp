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
 * com.cws.us.esolutions.messaging/jsp/html/en
 * Messaging_SystemMessages.jsp
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
    <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
        <div id="breadcrumb" class="lpstartover">
            <a href="${pageContext.request.contextPath}/ui/messaging/add-message"
                title="spring:message code='messaging.create.system.message' />"><spring:message code="messaging.create.system.message" /></a>
        </div>
    </c:if>

    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <c:choose>
        <c:when test="${not empty messageList}">
            <spring:message code="messaging.system.messages.list" />

            <c:forEach var="message" items="${messageList}">
                <div id="svcmessage">
                    <h3>
                        <a href="${pageContext.request.contextPath}/ui/messaging/edit-message/${message.messageId}"
                            title="<spring:message code='messaging.view.system.message.edit' />">${message.messageTitle} - ${message.messageId}</a>
                    </h3>
                    ${message.messageText}
                    <table class="kbauth">
                        <tr>
                            <td id="top" align="center" valign="middle"><strong><spring:message code="messaging.system.message.author" /></strong></td>
                            <td id="top" align="center" valign="middle"><strong><spring:message code="messaging.system.message.submit.date" /></strong></td>
                            <td id="top" align="center" valign="middle"><strong><spring:message code="messaging.system.message.expiry.date" /></strong></td>
                        </tr>
                        <tr>
                            <td align="center" valign="middle">
                                <em><a href="mailto:${message.authorEmail}?subject=Request for Comments: ${message.messageId}"
                                    title="Request for Comments: ${message.messageId}">${message.messageAuthor}</a></em>
                            </td>
                            <td align="center" valign="middle"><em>${message.fmtSubmitDate}</em></td>
                            <td align="center" valign="middle"><em>${message.fmtExpiryDate}</em></td>
                        </tr>
                    </table>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <spring:message code="messaging.no.system.messages" />
        </c:otherwise>
    </c:choose>
</div>
