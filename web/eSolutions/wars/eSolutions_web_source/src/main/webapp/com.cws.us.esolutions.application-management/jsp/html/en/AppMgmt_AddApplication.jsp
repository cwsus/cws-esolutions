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
 * AppMgmt_AddApplication.jsp
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
    function showScmData(box)
    {
        if (box.checked)
        {
            document.getElementById('scmData').style.display = 'block';
        }
        else
        {
            document.getElementById('scmData').style.display = 'none';
        }
    }
    //-->
</script>

<div id="sidebar">
    <h1><spring:message code="app.mgmt.header" /></h1>
    <ul class="sidemenu">
        <li>
            <a href="${pageContext.request.contextPath}/ui/application-management/list-applications"
                title="<spring:message code='app.mgmt.list.applications' />"><spring:message code='app.mgmt.list.applications' /></a>
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

    <form:form id="createNewApplication" name="createNewApplication" action="${pageContext.request.contextPath}/ui/application-management/add-application" method="post">
        <p>
            <label id="txtApplicationName"><spring:message code="app.mgmt.application.name" /></label>
            <form:input path="applicationName" />
            <form:errors path="applicationName" cssClass="validationError" />
            <label id="txtApplicationVersion"><spring:message code="app.mgmt.application.version" /></label>
            <form:input path="version" />
            <form:errors path="version" cssClass="validationError" />
            <label id="txtClusterName"><spring:message code="app.mgmt.application.cluster.name" /></label>
            <form:input path="clusterName" />
            <form:errors path="clusterName" cssClass="validationError" />
            <label id="txtJvmName"><spring:message code="app.mgmt.application.jvm.name" /></label>
            <form:input path="jvmName" />
            <form:errors path="jvmName" cssClass="validationError" />
            <label id="txtApplicationProject"><spring:message code="app.mgmt.application.project" /></label>
            <c:choose>
                <c:when test="${not empty projectListing}">
                        <form:select path="project" multiple="false">
                            <option><spring:message code="theme.option.select" /></option>
                            <option><spring:message code="theme.option.spacer" /></option>
                            <c:forEach var="project" items="${projectListing}">
                                <form:option value="${project.key}" label="${project.value}" />
                            </c:forEach>
                        </form:select>
                </c:when>
                <c:otherwise>
                        <a href="${pageContext.request.contextPath}/ui/application-management/add-project"
                            title="<spring:message code='select.request.add.project' />"><spring:message code='select.request.add.project' /></a>
                </c:otherwise>
            </c:choose>
            <form:errors path="project" cssClass="validationError" />
            <label id="txtApplicationPlatform"><spring:message code="app.mgmt.application.platform" /></label>
            <c:choose>
                <c:when test="${not empty platformListing}">
                        <form:select path="platform" multiple="true">
                            <c:forEach var="platform" items="${platformListing}">
                                <form:option value="${platform.key}" label="${platform.value}" />
                            </c:forEach>
                        </form:select>
                </c:when>
                <c:otherwise>
                        <a href="${pageContext.request.contextPath}/ui/application-management/add-project"
                            title="<spring:message code='select.request.add.project' />"><spring:message code='select.request.add.project' /></a>
                </c:otherwise>
            </c:choose>
            <form:errors path="platform" cssClass="validationError" />
            <label id="txtBasePath"><spring:message code="app.mgmt.base.path" /></label>
            <form:input path="basePath" />
            <form:errors path="basePath" cssClass="validationError" />
            <label id="txtPidDir"><spring:message code="app.mgmt.application.pid.directory" /></label>
            <form:input path="pidDirectory" />
            <form:errors path="pidDirectory" cssClass="validationError" />
            <label id="txtApplicationLogsPath"><spring:message code="app.mgmt.application.applogs.path" /></label>
            <form:input path="logsPath" />
            <form:errors path="logsPath" cssClass="validationError" />
            <label id="txtApplicationInstallPath"><spring:message code="app.mgmt.application.install.path" /></label>
            <form:input path="installPath" />
            <form:errors path="installPath" cssClass="validationError" />
            <label id="txtIsScmEnabled"><spring:message code="app.mgmt.application.scm.enabled" /></label>
            <form:checkbox path="isScmEnabled" name="isScmEnabled" id="isScmEnabled" onclick="showScmData(this);" />
            <form:errors path="isScmEnabled" cssClass="validationError" />
        </p>
        <p>
            <label id="txtScmPath"><spring:message code="app.mgmt.application.scm.path" /></label>
            <form:input path="scmPath" onkeypress="if (event.keyCode == 13) { disableButton(this); validateForm(this.form, event); }" />
            <form:errors path="scmPath" cssClass="validationError" />
        </p>
        <br />
        <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
        <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
        <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
    </form:form>
</div>
