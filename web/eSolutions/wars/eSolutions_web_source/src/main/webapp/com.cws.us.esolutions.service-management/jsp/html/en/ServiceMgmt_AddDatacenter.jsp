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
    <h1><spring:message code="svc.mgmt.header" /></h1>
    <ul>
        <li>
            <a href="${pageContext.request.contextPath}/ui/service-management/list-datacenters"
                title="<spring:message code='svc.mgmt.list.datacenters' />"><spring:message code="svc.mgmt.list.datacenters" /></a>
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

    <span id="validationError"></span>
    <h1><strong><spring:message code="svc.mgmt.add.datacenter" /></strong></h1>

    <form:form id="submitNewArticle" name="submitNewArticle" action="${pageContext.request.contextPath}/ui/knowledgebase/validate-article" method="post">
        <label id="txtDatacenterName"><spring:message code="svc.mgmt.service.name" /></label>
        <form:input path="datacenterName" />
        <form:errors path="datacenterName" cssClass="validationError" />
        <label id="txtDatacenterStatus"><spring:message code="svc.mgmt.service.status" /></label>
        <form:select path="datacenterStatus" multiple="false">
            <option><spring:message code="theme.option.select" /></option>
            <option><spring:message code="theme.option.spacer" /></option>
            <form:options items="${statusList}" />
        </form:select>
        <form:errors path="datacenterStatus" cssClass="validationError" />
        <label id="txtDatacenterDescription"><spring:message code="svc.mgmt.service.description" /></label>
        <form:textarea path="datacenterDesc" />
        <form:errors path="datacenterDesc" cssClass="validationError" />
        <br />
        <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
        <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
        <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
    </form:form>
</div>
