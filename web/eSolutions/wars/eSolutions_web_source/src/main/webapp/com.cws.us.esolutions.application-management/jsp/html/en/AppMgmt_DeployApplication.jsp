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
 * AppMgmt_RetrieveFiles.jsp
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

<div id="InfoLine"><spring:message code="app.mgmt.deploy.application" /></div>
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

	                <table id="applicationDetail">
	                    <tr>
	                        <td><label id="txtAppName"><spring:message code="app.mgmt.application.name" /></label></td>
	                        <td>${application.applicationName}</td>
	                    </tr>
	                    <tr>
	                        <td><label id="txtAppVersion"><spring:message code="app.mgmt.application.version" /></label></td>
	                        <td>${application.applicationVersion}</td>
	                    </tr>
	                    <tr>
	                        <td><label id="txtProjectName"><spring:message code="app.mgmt.application.project" /></label></td>
	                        <td>${application.applicationProject.projectCode}</td>
	                    </tr>
	                    <tr>
	                        <td><label id="txtPlatformName"><spring:message code="app.mgmt.application.platform" /></label></td>
	                        <td>${platform.platformName}</td>
	                    </tr>
	                    <c:if test="${not empty fn:trim(application.scmPath)}">
	                        <tr>
	                            <td><label id="txtScmPath"><spring:message code="app.mgmt.application.scm.path" /></label></td>
	                            <td>${application.scmPath}</td>
	                        </tr>
	                    </c:if>
	                    <tr>
	                        <td>&nbsp;</td>
	                    </tr>
	                    <tr>
	                        <td><label id="txtDeploymentType"><spring:message code="app.mgmt.deployment.type" /></label></td>
	                        <td>
	                            <form:radiobutton path="deploymentType" value="web" /><spring:message code="app.mgmt.deployment.web" />
	                            <form:radiobutton path="deploymentType" value="app" /><spring:message code="app.mgmt.deployment.app" />
	                        </td>
	                    </tr>
	                    <tr>
	                        <td><label id="txtScmVersion"><spring:message code="app.mgmt.new.version" /></label></td>
	                        <td>
	                            <form:input path="version" />
	                            <form:errors path="version" cssClass="validationError" onkeypress="if (event.keyCode == 13) { disableButton(this); validateForm(this.form, event); }" />
	                        </td>
	                    </tr>
	                    <c:if test="${empty fn:trim(application.scmPath)}">
	                        <tr>
	                            <td><label id="txtFileName"><spring:message code="app.mgmt.select.file" /></label></td>
	                            <td><input type="file" name="applicationBinary" id="applicationBinary" size="30" onkeypress="if (event.keyCode == 13) { disableButton(this); validateForm(this.form, event); }" /></td>
	                            <td><spring:message code="app.mgmt.select.file.allowed" /></td>
	                        </tr>
	                    </c:if>
	                </table>
	                <br /><br />
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
	        </c:otherwise>
	    </c:choose>
    </div>

    <div id="content-left">
        <ul>
            <li>
				<a href="${pageContext.request.contextPath}/ui/application-management/retrieve-files/application/${application.applicationGuid}"
				    title="<spring:message code='app.mgmt.application.retrieve.files' />"><spring:message code="app.mgmt.application.retrieve.files" /></a>            
            </li>
        </ul>
    </div>
</div>
