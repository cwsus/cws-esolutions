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

<div id="sidebar">
    <h1><spring:message code="svc.messaging.list.messages" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/service-messaging/default" title="spring:message code='svc.messaging.list' />"><spring:message code="svc.messaging.list" /></a></li>
    </ul>
</div>

<div id="main">
    <c:choose>
        <c:when test="${not empty fn:trim(command.messageAuthor)}">
            <h1><spring:message code="svc.messaging.system.message.edit.banner" arguments="${command.messageId}" /></h1>
        </c:when>
        <c:otherwise>
            <h1><spring:message code="svc.messaging.create.system.message" /></h1>
        </c:otherwise>
    </c:choose>

    <div id="error"></div>

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
        <form:form id="submitSystemMessage" name="submitSystemMessage" action="${pageContext.request.contextPath}/ui/service-messaging/submit-message" method="post" autocomplete="off">
            <c:if test="${empty fn:trim(command.messageAuthor)}">
                <form:hidden path="isNewMessage" value="true" />
            </c:if>

            <label id="txtSysMessageSubject"><spring:message code="svc.messaging.system.message.subject" /></label>
            <form:input path="messageTitle" />
            <form:errors path="messageTitle" cssClass="error" />
            <label id="txtSysMessageBody"><spring:message code="svc.messaging.system.message.body" /></label>
            <form:textarea path="messageText" />
            <form:errors path="messageText" cssClass="error" />
            <label id="txtIsMessageActive"><spring:message code="svc.messaging.system.message.activate" /></label>
            <form:radiobutton path="isActive" value="true" /><spring:message code="svc.messaging.system.message.active" />
            <form:radiobutton path="isActive" value="false" /><spring:message code="svc.messaging.system.message.inactive" />
            <form:errors path="isActive" cssClass="error" />
            <label id="txtIsAlertMessage"><spring:message code="svc.messaging.system.message.alert" /></label>
            <form:checkbox path="isAlert" value="true" /><spring:message code="svc.messaging.system.message.alert" />
            <form:errors path="isAlert" cssClass="error" />
            <br /><br />
            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
            <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
        </form:form>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
