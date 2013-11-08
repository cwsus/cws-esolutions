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
 * AppMgmt_ViewApplication.jsp
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

<div id="InfoLine"><spring:message code="app.mgmt.view.application" arguments="${application.applicationName}" /></div>
<div id="content">
    <div id="content-right">
	    <c:if test="${not empty messageResponse}">
	        <p id="info">${messageResponse}</p>
	    </c:if>
	    <c:if test="${not empty errorResponse}">
	        <p id="error">${errorResponse}</p>
	    </c:if>
	    <c:if test="${not empty responseMessage}">
	        <p id="info"><spring:message code="${responseMessage}" /></p>
	    </c:if>
	    <c:if test="${not empty errorMessage}">
	        <p id="error"><spring:message code="${errorMessage}" /></p>
	    </c:if>

	    <table id="applicationDetail">
	        <tr>
	            <td><label id="txtApplicationName"><spring:message code="app.mgmt.application.name" /></label></td>
	            <td>${application.applicationName}</td>
	        </tr>
	        <tr>
	            <td><label id="txtApplicationVersion"><spring:message code="app.mgmt.application.version" /></label></td>
	            <td>${application.applicationVersion}</td>
	        </tr>
	        <c:if test="${not empty appl.scmPath}">
	            <tr>
	                <td><label id="txtScmPath"><spring:message code="app.mgmt.application.scm.path" /></label></td>
	                <td>${application.scmPath}</td>
	            </tr>
	        </c:if>
	        <tr>
	            <td><label id="txtBasePath"><spring:message code="app.mgmt.base.path" /></label></td>
	            <td>${application.basePath}</td>
	        </tr>
	        <tr>
	            <td><label id="txtApplicationLogsPath"><spring:message code="app.mgmt.application.applogs.path" /></label></td>
	            <td>${application.applicationLogsPath}</td>
	        </tr>
	        <tr>
	            <td><label id="txtApplicationInstallPath"><spring:message code="app.mgmt.application.install.path" /></label></td>
	            <td>${application.applicationInstallPath}</td>
	        </tr>
	        <tr>
	            <td><label id="txtClusterName"><spring:message code="app.mgmt.application.cluster.name" /></label></td>
	            <td>${application.applicationCluster}</td>
	        </tr>
	        <tr>
	            <td><label id="txtJvmName"><spring:message code="app.mgmt.application.jvm.name" /></label></td>
	            <td>${application.jvmName}</td>
	        </tr>
	        <tr>
	            <td><label id="txtPidDirectory"><spring:message code="app.mgmt.application.pid.directory" /></label></td>
	            <td>${application.pidDirectory}</td>
	        </tr>
	        <tr>
	            <td><label id="txtApplicationProject"><spring:message code="app.mgmt.application.project" /></label></td>
	            <td>
	                <a href="${pageContext.request.contextPath}/ui/service-management/project/${application.applicationProject.projectGuid}"
	                    title="${application.applicationProject.projectCode}">${application.applicationProject.projectCode}</a>
	            </td>
	        </tr>
	        <tr>
	            <td><label id="txtApplicationPlatform"><spring:message code="app.mgmt.application.platform" /></label></td>
	            <td>
	                <c:forEach var="platform" items="${application.applicationPlatforms}">
	                    <a href="${pageContext.request.contextPath}/ui/service-management/platform/${platform.platformGuid}"
	                        title="${platform.platformName}">${platform.platformName}</a>
	                </c:forEach>
	            </td>
	        </tr>
	    </table>
    </div>

    <div id="content-left">
        <ul>
            <li>
				<a href="${pageContext.request.contextPath}/ui/application-management/retrieve-files/application/${application.applicationGuid}"
				    title="<spring:message code='app.mgmt.application.retrieve.files' />"><spring:message code="app.mgmt.application.retrieve.files" /></a>
            </li>
            <li>
				<a href="${pageContext.request.contextPath}/ui/application-management/deploy-application/application/${application.applicationGuid}"
				    title="<spring:message code='app.mgmt.application.deploy' />"><spring:message code="app.mgmt.application.deploy" /></a>
            </li>
            <li>
				<a onclick="confirmDelete('${application.applicationName}', '${application.applicationGuid}');"
				    title="<spring:message code='app.mgmt.application.retire' />" style="cursor: pointer;"><spring:message code="app.mgmt.application.retire" /></a>
            </li>
            <li><a href="http://www.free-css.com/">Makeovers</a></li>
            <li><a href="http://www.free-css.com/">Multibrowser Support</a></li>
            <li><a href="http://www.free-css.com/">LIVE Feature</a></li>
            <li><a href="http://www.free-css.com/">Code Optimization</a></li>
            <li><a href="http://www.free-css.com/">Our Clients</a></li>
            <li><a href="http://www.free-css.com/">Benefits with Us</a></li>
        </ul>
    </div>
</div>
