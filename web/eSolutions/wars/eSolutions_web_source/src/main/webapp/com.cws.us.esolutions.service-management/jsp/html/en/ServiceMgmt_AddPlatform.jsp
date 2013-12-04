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
 * com.cws.us.esolutions.service-management/jsp/html/en
 * ServiceMgmt_AddPlatform.jsp
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
        <li><a href="${pageContext.request.contextPath}/ui/service-management/list-datacenters" title="<spring:message code='svc.mgmt.list.datacenters' />"><spring:message code="svc.mgmt.list.datacenters" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/add-datacenter" title="<spring:message code='svc.mgmt.add.datacenter' />"><spring:message code="svc.mgmt.add.datacenter" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/list-projects" title="<spring:message code='svc.mgmt.list.projects' />"><spring:message code="svc.mgmt.list.projects" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/add-project" title="<spring:message code='svc.mgmt.add.project' />"><spring:message code="svc.mgmt.add.project" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/list-platforms" title="<spring:message code='svc.mgmt.list.platforms' />"><spring:message code="svc.mgmt.list.platforms" /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="svc.mgmt.add.platform" /></h1>

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
        <form:form id="submitPlatformData" name="submitPlatformData" action="${pageContext.request.contextPath}/ui/service-management/submit-platform" method="post">
            <form:hidden path="platformDmgr" />

            <label id="txtPlatformName"><spring:message code="svc.mgmt.service.name" /></label>
            <form:input path="platformName" />
            <form:errors path="platformName" cssClass="error" />
            <label id="txtPlatformStatus"><spring:message code="svc.mgmt.service.status" /></label>
            <form:select path="status" multiple="false">
                <option><spring:message code="theme.option.select" /></option>
                <option><spring:message code="theme.option.spacer" /></option>
                <form:options items="${statusList}" />
            </form:select>
            <form:errors path="status" cssClass="error" />
            <label id="txtPlatformDmgr"><spring:message code="svc.mgmt.platform.dmgr" /></label>
            ${command.platformDmgrName}
            <label id="txtPlatformAppservers"><spring:message code="svc.mgmt.platform.appservers" /></label>
            <c:choose>
                <c:when test="${not empty appServerList}">
                    <form:select path="appServers" multiple="true">
                        <c:forEach var="appserver" items="${appServerList}">
                            <form:option value="${appserver.serverGuid}" label="${appserver.operHostName}" />
                        </c:forEach>
                    </form:select>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/ui/systems/add-server" title="<spring:message code='system.mgmt.add.server' />"><spring:message code='system.mgmt.add.server' /></a>
                </c:otherwise>
            </c:choose>
            <label id="txtPlatformWebservers"><spring:message code="svc.mgmt.platform.webservers" /></label>
            <c:choose>
                <c:when test="${not empty webServerList}">
                    <form:select path="webServers" multiple="true">
                        <c:forEach var="webserver" items="${webServerList}">
                            <form:option value="${webserver.serverGuid}" label="${webserver.operHostName}" />
                        </c:forEach>
                    </form:select>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/ui/systems/add-server" title="<spring:message code='system.mgmt.add.server' />"><spring:message code='system.mgmt.add.server' /></a>
                </c:otherwise>
            </c:choose>
            <label id="txtPlatformDescription"><spring:message code="svc.mgmt.service.description" /></label>
            <form:textarea path="description" />
            <form:errors path="description" cssClass="error" />
            <br /><br />
            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
            <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
        </form:form>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
