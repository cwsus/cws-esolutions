<div class="feature">
    <div id="breadcrumb" class="lpstartover">
        <a href="${pageContext.request.contextPath}/ui/application-management/retrieve-files/${appl.applicationGuid}"
            title="<spring:message code='app.mgmt.application.retrieve.files' />"><spring:message code="app.mgmt.application.retrieve.files" /></a> / 
        <a href="${pageContext.request.contextPath}/ui/application-management/deploy-application/${appl.applicationGuid}"
            title="<spring:message code='app.mgmt.application.deploy' />"><spring:message code="app.mgmt.application.deploy" /></a> / 
    </div>

    <table id="applicationDetail">
        <tr>
            <td><label id="txtApplicationName"><spring:message code="app.mgmt.application.name" /></label></td>
            <td>${appl.applicationName}</td>
            <td><label id="txtApplicationVersion"><spring:message code="app.mgmt.application.version" /></label></td>
            <td>${appl.applicationVersion}</td>
        </tr>
        <tr>
            <td><label id="txtClusterName"><spring:message code="app.mgmt.application.cluster.name" /></label></td>
            <td>${appl.applicationCluster}</td>
            <td><label id="txtJvmName"><spring:message code="app.mgmt.application.jvm.name" /></label></td>
            <td>${appl.jvmName}</td>
        </tr>
        <tr>
            <td><label id="txtApplicationProject"><spring:message code="app.mgmt.application.project" /></label></td>
            <td><a href="${pageContext.request.contextPath}/ui/service-management/project/${project.projectGuid}" title="${project.projectCode}">${project.projectCode}</a></td>
            <td><label id="txtApplicationPlatform"><spring:message code="app.mgmt.application.platform" /></label></td>
            <td><a href="${pageContext.request.contextPath}/ui/service-management/platform/${platform.platformGuid}" title="${platform.platformName}">${platform.platformName}</a></td>
        </tr>
        <c:if test="${not empty appl.scmPath}">
	        <tr>
	            <td><label id="txtScmPath"><spring:message code="app.mgmt.application.scm.path" /></label></td>
	            <td>${appl.scmPath}</td>
	        </tr>
	    </c:if>
        <tr>
            <td><label id="txtApplicationLogsPath"><spring:message code="app.mgmt.application.applogs.path" /></label></td>
            <td>${appl.applicationLogsPath}</td>
            <td><label id="txtApplicationInstallPath"><spring:message code="app.mgmt.application.install.path" /></label></td>
            <td>${appl.applicationInstallPath}</td>
        </tr>
        <tr>
            <td><label id="txtPidDirectory"><spring:message code="app.mgmt.application.pid.path" /></label></td>
            <td>${appl.pidDirectory}</td>
            <td><label id="txtServerType"><spring:message code="app.mgmt.application.server.type" /></label></td>
            <td>${appl.serverType}</td>
        </tr>
    </table>
</div>
