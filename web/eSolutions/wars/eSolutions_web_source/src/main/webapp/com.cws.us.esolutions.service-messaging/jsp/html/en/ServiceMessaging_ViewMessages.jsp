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
        <c:when test="${not empty messageList}">
            <c:forEach var="message" items="${messageList}">
                <h1>
                    <a href="${pageContext.request.contextPath}/ui/service-messaging/edit-message/message/${message.messageId}"
                        title="<spring:message code='svc.messaging.view.system.message.edit' />">${message.messageTitle} - ${message.messageId}</a>
                </h1>
                <br />
                ${message.messageText}
                <p class="post-footer align-right">
                    <a href="mailto:${message.authorEmail}?subject=Request for Comments: ${message.messageId}"
                        title="Request for Comments: ${message.messageId}">${message.messageAuthor}</a>
                    <a href="http://www.free-css.com/" class="comments">Comments (7)</a>
                    <span class="date"><fmt:formatDate value="${message.submitDate}" pattern="${dateFormat}" /></span>
                </p>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <spring:message code="svc.messaging.no.system.messages" />
        </c:otherwise>
    </c:choose>
</div>
