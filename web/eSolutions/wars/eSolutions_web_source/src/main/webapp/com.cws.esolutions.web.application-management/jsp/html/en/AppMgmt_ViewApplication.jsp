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
            <c:if test="${not empty fn:trim(appl.scmPath)}">
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
