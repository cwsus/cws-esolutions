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

<div id="InfoLine"><spring:message code="svc.mgmt.add.platform" /></div>
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

	    <form:form id="createNewPlatform" name="createNewPlatform" action="${pageContext.request.contextPath}/ui/service-management/submit-platform" method="post">
	        <form:hidden path="platformDmgr" />

	        <table id="applicationDetail">
	            <tr>
	                <td><label id="txtPlatformName"><spring:message code="svc.mgmt.service.name" /></label></td>
	                <td><form:input path="platformName" /></td>
	                <td><form:errors path="platformName" cssClass="validationError" /></td>
	            </tr>
	            <tr>
	                <td><label id="txtPlatformStatus"><spring:message code="svc.mgmt.service.status" /></label></td>
	                <td>
	                    <form:select path="status" multiple="false">
	                        <option><spring:message code="theme.option.select" /></option>
	                        <option><spring:message code="theme.option.spacer" /></option>
	                        <form:options items="${statusList}" />
	                    </form:select>
	                </td>
	                <td><form:errors path="status" cssClass="validationError" /></td>
	            </tr>
	            <tr>
	                <td><label id="txtPlatformDmgr"><spring:message code="svc.mgmt.platform.dmgr" /></label></td>
	                <td>${command.platformDmgrName}</td>
	            </tr>
	            <tr>
	                <c:choose>
	                    <c:when test="${not empty appServerList}">
	                        <td><label id="txtPlatformAppservers"><spring:message code="svc.mgmt.platform.appservers" /></label></td>
                            <td>
                                <form:select path="appServers" multiple="true">
                                    <c:forEach var="appserver" items="${appServerList}">
                                        <form:option value="${appserver.serverGuid}" label="${appserver.operHostName}" />
                                    </c:forEach>
                                </form:select>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <td>
                                <a href="${pageContext.request.contextPath}/ui/systems/add-server"
                                    title="<spring:message code='system.mgmt.add.server' />"><spring:message code='system.mgmt.add.server' /></a>
                            </td>
                        </c:otherwise>
                    </c:choose>
	            </tr>
	            <tr>
                    <c:choose>
                        <c:when test="${not empty webServerList}">
                            <td><label id="txtPlatformWebservers"><spring:message code="svc.mgmt.platform.webservers" /></label></td>
                            <td>
                                <form:select path="webServers" multiple="true">
                                    <c:forEach var="webserver" items="${webServerList}">
                                        <form:option value="${webserver.serverGuid}" label="${webserver.operHostName}" />
                                    </c:forEach>
                                </form:select>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <td>
                                <a href="${pageContext.request.contextPath}/ui/systems/add-server"
                                    title="<spring:message code='system.mgmt.add.server' />"><spring:message code='system.mgmt.add.server' /></a>
                            </td>
                        </c:otherwise>
                    </c:choose>
	            </tr>
	            <tr>
	                <td><label id="txtPlatformDescription"><spring:message code="svc.mgmt.service.description" /></label></td>
	                <td><form:textarea path="description" /></td>
	                <td><form:errors path="description" cssClass="validationError" /></td>
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
        </ul>
    </div>
</div>
