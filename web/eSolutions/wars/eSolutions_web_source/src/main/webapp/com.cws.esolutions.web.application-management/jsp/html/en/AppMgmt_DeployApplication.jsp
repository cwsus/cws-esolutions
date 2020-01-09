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
 * File: AppMgmt_DeployApplication.jsp
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
    function validateForm(theForm)
    {
        if (!((theForm.deploymentType1.checked)) && (!(theForm.deploymentType2.checked)))
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Please provide the deployment type.';
            document.getElementById('txtDeploymentType').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
        }
        else if ((theForm.version.value == '') || (theForm.version.value == '0.0'))
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Please provide the software version to deploy.';
            document.getElementById('txtScmVersion').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('version').focus();
        }
        else if ((theForm.file) && (theForm.file.value == ''))
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Please provide the file to deploy.';
            document.getElementById('txtMessageSubject').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('messageSubject').focus();
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
        <li><a href="${pageContext.request.contextPath}/ui/application-management/retrieve-files/application/${application.applicationGuid}" title="<spring:message code='app.mgmt.file.retrieval' />"><spring:message code="app.mgmt.file.retrieval" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/application-management/list-applications" title="<spring:message code='app.mgmt.list.applications' />"><spring:message code='app.mgmt.list.applications' /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/application-management/add-application" title="<spring:message code='app.mgmt.add.application' />"><spring:message code='app.mgmt.add.application' /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="app.mgmt.deploy.application" /></h1>

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
        <c:choose>
            <c:when test="${not empty platformList}">
                <h1><spring:message code="select.target.app.platform" /></h1>

                <table id="platformList">
                    <tr>
                        <td><spring:message code="svc.mgmt.platform.name" /></td>
                        <td><spring:message code="svc.mgmt.platform.region" /></td>
                    </tr>
                    <c:forEach var="platform" items="${platformList}">
                        <tr>
                            <td>
                                <a href="${pageContext.request.contextPath}/ui/application-management/deploy-application/application/${application.applicationGuid}/platform/${platform.platformGuid}"
                                    title="${platform.platformName}">${platform.platformName}</a>
                            </td>
                            <td>${platform.platformRegion}</td>
                        </tr>
                    </c:forEach>
                </table>
            </c:when>
            <c:otherwise>
                <h1><spring:message code="app.mgmt.provide.deployment" /></h1>

                <form:form id="deployApplication" name="deployApplication" action="${pageContext.request.contextPath}/ui/application-management/deploy-application" method="post" enctype="multipart/form-data">
                    <form:hidden path="platform" value="${platform.platformGuid}" />
                    <form:hidden path="applicationGuid" value="${application.applicationGuid}" />

                    <label id="txtAppName"><spring:message code="app.mgmt.application.name" /></label>
                    ${application.applicationName}

                    <label id="txtAppVersion"><spring:message code="app.mgmt.application.version" /></label>
                    ${application.applicationVersion}

                    <label id="txtPlatformName"><spring:message code="app.mgmt.application.platform" /></label>
                    ${platform.platformName}

                    <c:if test="${not empty fn:trim(application.scmPath)}">
                        <label id="txtScmPath"><spring:message code="app.mgmt.application.scm.path" /></label>
                        ${application.scmPath}
                    </c:if>

                    <label id="txtDeploymentType"><spring:message code="app.mgmt.deployment.type" /></label>
                    <form:radiobutton path="deploymentType" value="web" /><spring:message code="app.mgmt.deployment.web" />
                    <form:radiobutton path="deploymentType" value="app" /><spring:message code="app.mgmt.deployment.app" />

                    <label id="txtScmVersion"><spring:message code="app.mgmt.new.version" /></label>
                    <form:input path="version" />
                    <form:errors path="version" />

                    <c:if test="${empty fn:trim(application.scmPath)}">
                        <script type="text/javascript">
                            <!--
                                document.getElementById('allowedFiles').style.display = 'block';
                            //-->
                        </script>
                        <label id="txtFileName"><spring:message code="app.mgmt.select.file" /></label>
                        <input type="file" name="applicationBinary" id="applicationBinary" size="30" />
                    </c:if>

                    <br /><br />
                    <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                    <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                    <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </form:form>
            </c:otherwise>
        </c:choose>
    </p>
</div>

<div id="rightbar">
    <p id="allowedFiles" style="display: none;">
        <spring:message code="app.mgmt.select.file.allowed" />
    </p>
</div>
