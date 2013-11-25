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

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="submissionDate" class="java.util.Date" scope="page" />
<jsp:useBean id="expirationDate" class="java.util.Date" scope="page" />

<div id="InfoLine"><spring:message code="svc.messaging.list" /></div>
<div id="content">
    <div id="content-right">
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
	            <c:forEach var="message" items="${messageList}">
	                <div id="svcmessage">
	                    <h3>
	                        <a href="${pageContext.request.contextPath}/ui/service-messaging/edit-message/message/${message.messageId}"
	                            title="<spring:message code='svc.messaging.view.system.message.edit' />">${message.messageTitle} - ${message.messageId}</a>
	                    </h3>
	                    <br />
	                    ${message.messageText}
	                    <br />
	                    <table class="kbauth">
	                        <tr>
	                            <td id="top" align="center" valign="middle"><strong><spring:message code="svc.messaging.system.message.author" /></strong></td>
	                            <td id="top" align="center" valign="middle"><strong><spring:message code="svc.messaging.system.message.submit.date" /></strong></td>
	                            <td id="top" align="center" valign="middle"><strong><spring:message code="svc.messaging.system.message.expiry.date" /></strong></td>
	                        </tr>
	                        <tr>
	                            <td align="center" valign="middle">
	                                <em><a href="mailto:${message.authorEmail}?subject=Request for Comments: ${message.messageId}"
	                                    title="Request for Comments: ${message.messageId}">${message.messageAuthor}</a></em>
	                            </td>

	                            <td align="center" valign="middle"><em><fmt:formatDate value="${message.submitDate}" pattern="${dateFormat}" /></em></td>
	                            <td align="center" valign="middle"><em><fmt:formatDate value="${message.expiryDate}" pattern="${dateFormat}" /></em></td>
	                        </tr>
	                    </table>
	                </div>
	            </c:forEach>
	        </c:when>
	        <c:otherwise>
	            <spring:message code="svc.messaging.no.system.messages" />
	        </c:otherwise>
	    </c:choose>
    </div>

    <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
        <div id="content-left">
            <ul>
                <li>
		            <a href="${pageContext.request.contextPath}/ui/service-messaging/add-message"
		                title="spring:message code='svc.messaging.create.system.message' />"><spring:message code="svc.messaging.create.system.message" /></a>
		        </li>
            </ul>
        </div>
    </c:if>
</div>
