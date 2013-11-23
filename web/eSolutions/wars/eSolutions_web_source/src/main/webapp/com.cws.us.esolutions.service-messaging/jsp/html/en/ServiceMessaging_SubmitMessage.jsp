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
 * Messaging_SubmitSystemMessage.jsp
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

<c:choose>
    <c:when test="${not empty fn:trim(command.authorEmail)}">
        <div id="InfoLine"><spring:message code="svc.messaging.system.message.edit.banner" arguments="${message.messageId}" /></div>
    </c:when>
    <c:otherwise>
        <div id="InfoLine"><spring:message code="svc.messaging.create.banner" /></div>
    </c:otherwise>
</c:choose>

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

        <span id="validationError"></span>

        <form:form id="submitSystemMessage" name="submitSystemMessage" action="${pageContext.request.contextPath}/ui/service-messaging/submit-message" method="post" autocomplete="off">
            <table id="contactTable">
                <tr>
                    <td id="txtSysMessageSubject"><spring:message code="svc.messaging.system.message.subject" /></td>
                    <td><form:input path="messageTitle" /></td>
                    <td><form:errors path="messageTitle" cssClass="validationError" /></td>
                </tr>
                <tr>
                    <td id="txtSysMessageBody"><spring:message code="svc.messaging.system.message.body" /></td>
                    <td><form:textarea path="messageText" /></td>
                    <td><form:errors path="messageText" cssClass="validationError" /></td>
                </tr>
                <tr>
                    <td><label id="txtIsMessageActive"><spring:message code="svc.messaging.system.message.activate" /></label></td>
                    <td>
                        <form:radiobutton path="isActive" value="true" /><spring:message code="svc.messaging.system.message.active" />
                        <form:radiobutton path="isActive" value="false" /><spring:message code="svc.messaging.system.message.inactive" />
                        <form:errors path="isActive" cssClass="validationError" />
                    </td>
                </tr>
            </table>

            <table id="inputItems">
                <tr>
                    <td>
                        <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                    </td>
                    <td>
                        <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                    </td>
                    <td>
                        <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                    </td>
                </tr>
            </table>
        </form:form>
    </div>

    <div id="content-left">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/ui/common/default" title="<spring:message code='theme.navbar.home' />">
                    <spring:message code='theme.navbar.home' /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/service-messaging/default"
                    title="<spring:message code='svc.messaging.list.messages' />"><spring:message code='svc.messaging.list.messages' /></a>
            </li>
        </ul>
    </div>
</div>
