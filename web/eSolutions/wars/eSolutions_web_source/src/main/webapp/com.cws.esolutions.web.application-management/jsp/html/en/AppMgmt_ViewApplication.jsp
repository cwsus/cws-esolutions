<%--
/*
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
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
 * File: AppMgmt_ViewApplication.jsp
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

<script>
    <!--
    function confirmDelete(name, id)
    {
        var confirmation = confirm("Are you sure you wish to retire application " + name + " ?");
    
        if (confirmation)
        {
            window.location.href = '${pageContext.request.contextPath}/ui/application-management/retire-application/application/' + id;
        }
    }
    //-->
</script>

<div id="sidebar">
    <h1><spring:message code="app.mgmt.header" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/application-management/deploy-application/application/${application.guid}" title="<spring:message code='app.mgmt.deploy.application' />"><spring:message code="app.mgmt.deploy.application" /></a></li>
        <li><a onclick="confirmDelete('${application.name}', '${application.guid}');" title="<spring:message code='app.mgmt.application.retire' />" style="cursor: pointer;"><spring:message code="app.mgmt.application.retire" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/application-management/list-applications" title="<spring:message code='app.mgmt.list.applications' />"><spring:message code='app.mgmt.list.applications' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/application-management/add-application" title="<spring:message code='app.mgmt.add.application' />"><spring:message code='app.mgmt.add.application' /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="app.mgmt.view.application" arguments="${application.name}" /></h1>

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
        <table id="applicationDetail">
            <tr>
                <td><label id="txtApplicationName"><spring:message code="app.mgmt.application.name" /></label></td>
                <td>${application.name}</td>
            </tr>
            <tr>
                <td><label id="txtApplicationVersion"><spring:message code="app.mgmt.application.version" /></label></td>
                <td>${application.version}</td>
            </tr>
            <tr>
                <td><label id="txtPackageLocation"><spring:message code="app.mgmt.application.package.location" /></label></td>
                <td>${application.packageLocation}</td>
            </tr>
            <tr>
                <td><label id="txtPackageInstaller"><spring:message code="app.mgmt.application.package.installer" /></label></td>
                <td>${application.packageInstaller}</td>
            </tr>
            <tr>
                <td><label id="txtInstallerOptions"><spring:message code="app.mgmt.application.installer.options" /></label></td>
                <td>${application.installerOptions}</td>
            </tr>
            <tr>
                <td><label id="txtBasePath"><spring:message code="app.mgmt.install.path" /></label></td>
                <td>${application.installPath}</td>
            </tr>
            <tr>
                <td><label id="txtApplicationLogsPath"><spring:message code="app.mgmt.application.applogs.path" /></label></td>
                <td>${application.logsDirectory}</td>
            </tr>
            <tr>
                <td><label id="txtApplicationPlatform"><spring:message code="app.mgmt.application.platform" /></label></td>
                <td>
                    <c:forEach var="platform" items="${application.applicationPlatforms}">
                        <a href="${pageContext.request.contextPath}/ui/service-management/platform/${platform.guid}"
                            title="${platform.name}">${platform.name}</a>
                    </c:forEach>
                </td>
            </tr>
        </table>
    </p>
</div>

<div id="rightbar">
    <h1 id="alert"><spring:message code="theme.important.information" /></h1>
    <br />
    <spring:message code="app.mgmt.refresh.current" />
</div>
