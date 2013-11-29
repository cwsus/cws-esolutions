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
 * com.cws.us.esolutions.user-management/jsp/html/en
 * UserManagement_ViewUser.jsp
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
    <h1><spring:message code="svc.mgmt.header" /></h1>
    <ul>
        <li>
            <a href="${pageContext.request.contextPath}/ui/service-management/modify-service/datacenter/${datacenter.datacenterGuid}"
                title="<spring:message code='svc.mgmt.update.service' />"><spring:message code="svc.mgmt.update.service" /></a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/ui/service-management/list-datacenters"
                title="<spring:message code='svc.mgmt.list.datacenters' />"><spring:message code="svc.mgmt.list.datacenters" /></a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/ui/service-management/add-datacenter"
                title="<spring:message code='svc.mgmt.add.datacenter' />"><spring:message code="svc.mgmt.add.datacenter" /></a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/ui/service-management/list-projects"
                title="<spring:message code='svc.mgmt.list.projects' />"><spring:message code="svc.mgmt.list.projects" /></a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/ui/service-management/add-project"
                title="<spring:message code='svc.mgmt.add.project' />"><spring:message code="svc.mgmt.add.project" /></a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/ui/service-management/list-platforms"
                title="<spring:message code='svc.mgmt.list.platforms' />"><spring:message code="svc.mgmt.list.platforms" /></a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/ui/service-management/add-platform"
                title="<spring:message code='svc.mgmt.add.platform' />"><spring:message code="svc.mgmt.add.platform" /></a>
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
        <c:when test="${not empty fn:trim(command.authorEmail)}">
            <h1><spring:message code="svc.messaging.system.message.edit.banner" arguments="${message.messageId}" /></h1>
        </c:when>
        <c:otherwise>
            <h1><spring:message code="svc.messaging.create.banner" /></h1>
        </c:otherwise>
    </c:choose>
    <span id="validationError"></span>

    <form:form id="submitSystemMessage" name="submitSystemMessage" action="${pageContext.request.contextPath}/ui/service-messaging/submit-message" method="post" autocomplete="off">
        <c:if test="${empty fn:trim(command.authorEmail)}">
            <form:hidden path="isNewMessage" value="true" />
        </c:if>

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
