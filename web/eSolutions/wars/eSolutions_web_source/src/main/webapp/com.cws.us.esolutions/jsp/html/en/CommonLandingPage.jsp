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

<div id="InfoLine"><spring:message code="system.welcome.back" /></div>
<div id="content">
    <div id="content-right">
        <h1></h1>
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
	        <c:when test="${not empty messageList}">
	            <spring:message code="messaging.system.messages.list" />

	            <c:forEach var="message" items="${messageList}">
	                <div id="svcmessage">
	                    <h3>${message.messageSubject}</h3>
	                    <div class="feature">
	                        ${message.messageBody}
	                    </div>
	
	                    <div class="kbauth">
	                        <table id="svcMessageAuthor">
	                            <tr>
	                                <td><spring:message code="messaging.view.system.message.author" />&nbsp; ${message.messageAuthor}</td>
	                                <td>
	                                    <spring:message code="messaging.view.system.message.email" />&nbsp; <a href="mailto:${message.messageContact}?subject=<spring:message code='messaging.comments.subject' /> ${message.messageId}"
	                                        title="<spring:message code="messaging.view.system.message.email" />">${message.messageContact}</a>
	                                </td>
	                                <td><spring:message code="messaging.view.system.message.timestamp" /> &nbsp; ${message.messageTimestamp}</td>
	                            </tr>
	                        </table>
	                    </div>
	                </div>
	            </c:forEach>
	        </c:when>
	        <c:otherwise>
	            <spring:message code="messaging.system.messages.no.messages" />
	        </c:otherwise>
	    </c:choose>
    </div>
</div>
