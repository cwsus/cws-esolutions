<%--
/**
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
 * @author cws-khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * cws-khuntly @ Jan 16, 2013 11:53:26 AM
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

    function validateForm(theForm)
    {
        if (theForm.applicationName.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'An application name must be provided.';
            document.getElementById('txtApplicationName').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('applicationName').focus();
        }
        else if (theForm.version.value == '')
        {
            // default to 1.0
            document.getElementById('applicationName').value = '1.0';
        }
        else if (theForm.clusterName.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'A target application cluster must be provided.';
            document.getElementById('txtApplicationCluster').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('applicationName').focus();
        }
        else if (theForm.platform.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'The application must be associated with a platform.';
            document.getElementById('txtApplicationPlatform').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('applicationPlatform').focus();
        }
        else if (theForm.logsPath.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'A valid path must be provided for application logs.';
            document.getElementById('txtApplicationLogsPath').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('applicationName').focus();
        }
        else if (theForm.installPath.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'A valid path for application binaries must be provided.';
            document.getElementById('txtApplicationInstallPath').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('applicationName').focus();
        }
        else
        {
            theForm.submit();
        }
    }
    //-->
</script>

<div id="sidebar">
    <h1><spring:message code="app.mgmt.header" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/application-management/list-applications" title="<spring:message code='app.mgmt.list.applications' />"><spring:message code='app.mgmt.list.applications' /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="app.mgmt.add.application" /></h1>

    <div id="error"></div>

    <c:if test="${not empty fn:trim(messageResponse)}">
        <p id="info">${messageResponse}</p>
    </c:if>
    <c:if test="${not empty fn:trim(errorResponse)}">
        <p id="error">${errorResponse}</p>
    </c:if>
    <c:if test="${not empty fn:trim(bindResult)}">
        <p id="error">${bindResult}</p>
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
        <form:form id="createNewApplication" name="createNewApplication" action="${pageContext.request.contextPath}/ui/application-management/add-application" method="post">
            <label id="txtApplicationName"><spring:message code="app.mgmt.application.name" /></label>
            <form:input path="name" />
            <form:errors path="name" cssClass="error" />

            <label id="txtApplicationVersion"><spring:message code="app.mgmt.application.version" /></label>
            <form:input path="version" />
            <form:errors path="version" cssClass="error" />

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
                    <a href="${pageContext.request.contextPath}/ui/application-management/add-platform"
                        title="<spring:message code='select.request.add.platform' />"><spring:message code='select.request.add.platform' /></a>
                </c:otherwise>
            </c:choose>
            <form:errors path="platform" cssClass="error" />

            <label id="txtBasePath"><spring:message code="app.mgmt.base.path" /></label>
            <form:input path="basePath" />
            <form:errors path="basePath" cssClass="error" />

            <label id="txtApplicationLogsPath"><spring:message code="app.mgmt.application.applogs.path" /></label>
            <form:input path="logsPath" />
            <form:errors path="logsPath" cssClass="error" />

            <label id="txtApplicationInstallPath"><spring:message code="app.mgmt.application.install.path" /></label>
            <form:input path="installPath" />
            <form:errors path="installPath" cssClass="error" />

            <label id="txtIsScmEnabled"><spring:message code="app.mgmt.application.scm.enabled" /></label>
            <form:checkbox path="isScmEnabled" name="isScmEnabled" id="isScmEnabled" onclick="showScmData(this);" />
            <form:errors path="isScmEnabled" cssClass="error" />

            <label id="txtScmPath"><spring:message code="app.mgmt.application.scm.path" /></label>
            <form:input path="scmPath" />
            <form:errors path="scmPath" cssClass="error" />

            <br /><br />
            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
            <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
        </form:form>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
