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
</script>

<div class="feature">
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

    <spring:message code="app.mgmt.add.application" />

    <p id="validationError" />

    <form:form id="createNewApplication" name="createNewApplication" action="${pageContext.request.contextPath}/ui/application-management/add-application" method="post">
        <table id="applicationDetail">
            <tr>
                <td><label id="txtApplicationName"><spring:message code="app.mgmt.application.name" /></label></td>
                <td><form:input path="applicationName" /></td>
                <td><form:errors path="applicationName" cssClass="validationError" /></td>
                <td><label id="txtApplicationVersion"><spring:message code="app.mgmt.application.version" /></label></td>
                <td><form:input path="version" /></td>
                <td><form:errors path="version" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtClusterName"><spring:message code="app.mgmt.application.cluster.name" /></label></td>
                <td><form:input path="clusterName" /></td>
                <td><form:errors path="clusterName" cssClass="validationError" /></td>
                <td><label id="txtJvmName"><spring:message code="app.mgmt.application.jvm.name" /></label></td>
                <td><form:input path="jvmName" /></td>
                <td><form:errors path="jvmName" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtApplicationProject"><spring:message code="app.mgmt.application.project" /></label></td>
                <c:choose>
                    <c:when test="${not empty projectListing}">
                        <td>
                            <form:select path="project" multiple="false">
								<option><spring:message code="select.default" /></option>
								<option><spring:message code="select.spacer" /></option>
                                <form:options items="${projectListing}" />
                            </form:select>
                        </td>
                    </c:when>
                    <c:otherwise>
                        <td>
                            <a href="${pageContext.request.contextPath}/ui/application-management/add-project"
                                title="<spring:message code='select.request.add.project' />"><spring:message code='select.request.add.project' /></a>
                        </td>
                    </c:otherwise>
                </c:choose>
                <td><form:errors path="project" cssClass="validationError" /></td>
                <td><label id="txtApplicationPlatform"><spring:message code="app.mgmt.application.platform" /></label></td>
                <c:choose>
                    <c:when test="${not empty platformListing}">
                        <td>
                            <form:select path="platform" multiple="false">
								<option><spring:message code="select.default" /></option>
								<option><spring:message code="select.spacer" /></option>
                                <form:options items="${platformListing}" />
                            </form:select>
                        </td>
                    </c:when>
                    <c:otherwise>
                        <td>
                            <a href="${pageContext.request.contextPath}/ui/application-management/add-project"
                                title="<spring:message code='select.request.add.project' />"><spring:message code='select.request.add.project' /></a>
                        </td>
                    </c:otherwise>
                </c:choose>
                <td><form:errors path="platform" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtBasePath"><spring:message code="app.mgmt.base.path" /></label></td>
                <td><form:input path="basePath" /></td>
                <td><form:errors path="basePath" cssClass="validationError" /></td>
                <td><label id="txtPidDir"><spring:message code="app.mgmt.application.pid.directory" /></label></td>
                <td><form:input path="pidDirectory" /></td>
                <td><form:errors path="pidDirectory" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtApplicationLogsPath"><spring:message code="app.mgmt.application.applogs.path" /></label></td>
                <td><form:input path="logsPath" /></td>
                <td><form:errors path="logsPath" cssClass="validationError" /></td>
                <td><label id="txtApplicationInstallPath"><spring:message code="app.mgmt.application.install.path" /></label></td>
                <td><form:input path="installPath" /></td>
                <td><form:errors path="installPath" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtIsScmEnabled"><spring:message code="app.mgmt.application.scm.enabled" /></label></td>
                <td><form:checkbox path="isScmEnabled" name="isScmEnabled" id="isScmEnabled" onclick="showScmData();" /></td>
                <td><form:errors path="isScmEnabled" cssClass="validationError" /></td>
            </tr>
            <tr id="scmData" style="display: none;">
                <td><label id="txtScmPath"><spring:message code="app.mgmt.application.scm.path" /></label></td>
                <td><form:input path="scmPath" onkeypress="disableButton(this); validateForm(this.form, event);" /></td>
                <td><form:errors path="scmPath" cssClass="validationError" /></td>
            </tr>
        </table>
        <br /><br />
        <table id="inputItems">
            <tr>
				<td>
				    <input type="button" name="execute" value="<spring:message code='button.execute.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
				</td>
				<td>
				    <input type="button" name="reset" value="<spring:message code='button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
				</td>
				<td>
				    <input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
				</td>
            </tr>
        </table>
    </form:form>
</div>
<br /><br />
