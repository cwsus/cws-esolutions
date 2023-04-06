<%--
/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
--%>
<%--
/**
 * Project: eSolutions_web_source
 * Package: com.cws.esolutions.web.application-management\jsp\html\en
 * File: AppMgmt_AddApplication.jsp
 *
 * @author cws-khuntly
 * @version 1.0
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
--%>

<div id="homecontent">
    <div class="wrapper">
        <div id="validationError" style="color: #FF0000"></div>

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

        <h1><spring:message code="app.mgmt.view.application" arguments="${application.name}" /></h1>
        <table id="addNewApplication">
            <tr>
                <td><label id="txtApplicationName"><spring:message code="app.mgmt.application.name" /></label></td>
                <td>${application.name}</td>
            </tr>
            <tr>
                <td><label id="txtApplicationVersion"><spring:message code="app.mgmt.application.version" /></label></td>
                <td>${application.version}</td>
            </tr>
            <tr>
                <td><label id="txtApplicationPlatform"><spring:message code="app.mgmt.application.platform" /></label></td>
                <td><a href="${pageContext.request.contextPath}/ui/platform-management/platform/view/${application.platform.platformGuid}" title="${application.platform.platformName}">${application.platform.platformName}</a></td>
            </tr>
            <tr>
                <td><label id="txtBasePath"><spring:message code="app.mgmt.base.path" /></label></td>
                <td>${application.basePath}</td>
            </tr>
            <tr>
                <td><label id="txtApplicationLogsPath"><spring:message code="app.mgmt.application.applogs.path" /></label></td>
                <td>${application.logsPath}</td>
            </tr>
            <tr>
                <td><label id="txtApplicationInstallPath"><spring:message code="app.mgmt.application.install.path" /></label></td>
                <td>${application.installPath}</td>
            </tr>
        </table>
    </div>
    <br class="clear" />
</div>

<div id="container">
    <div class="wrapper">
        <div id="holder">
        <h1><spring:message code="app.mgmt.header" /></h1>
	        <ul>
	            <li><a href="${pageContext.request.contextPath}/ui/application-management/list-applications" title="<spring:message code='app.mgmt.list.applications' />"><spring:message code='app.mgmt.list.applications' /></a></li>
	            <li><a href="${pageContext.request.contextPath}/ui/application-management/add-application" title="<spring:message code='app.mgmt.add.application' />"><spring:message code='app.mgmt.add.application' /></a></li>
	        </ul>
        </div>
        <br class="clear" />
    </div>
</div>
