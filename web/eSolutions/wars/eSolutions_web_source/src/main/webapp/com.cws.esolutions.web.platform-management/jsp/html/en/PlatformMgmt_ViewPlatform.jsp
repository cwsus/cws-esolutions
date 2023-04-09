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

        <h1><spring:message code="platform.mgmt.view.platform" arguments="${platform.platformName}" /></h1>
        <p>
            <table>
                <tr>
                    <td><label id="txtPlatformName"><spring:message code="platform.mgmt.name" /></label></td>
                    <td>${platform.platformName}</td>
                </tr>
                <tr>
                    <td><label id="txtPlatformStatus"><spring:message code="platform.mgmt.status" /></label></td>
                    <td>${platform.platformStatus}</td>
                </tr>
                <tr>
                    <td><label id="txtPlatformDescription"><spring:message code="platform.mgmt.description" /></label></td>
                    <td>${platform.platformDescription}</td>
                </tr>
            </table>
        </p>
    </div>
</div>

<div id="container">
    <div class="wrapper">
        <div id="holder">
	        <h1><spring:message code="platform.mgmt.header" /></h1>
	        <ul>
	            <li><a href="${pageContext.request.contextPath}/ui/platform-management/platform/update/${platform.platformGuid}" title="<spring:message code='platform.mgmt.update.service' />"><spring:message code="platform.mgmt.update.service" /></a></li>
	            <li><a href="${pageContext.request.contextPath}/ui/platform-management/list-platforms" title="<spring:message code='platform.mgmt.list.platforms' />"><spring:message code="platform.mgmt.list.platforms" /></a></li>
	            <li><a href="${pageContext.request.contextPath}/ui/platform-management/add-platform" title="<spring:message code='platform.mgmt.add.platform' />"><spring:message code="platform.mgmt.add.platform" /></a></li>
	        </ul>
        </div>
        <br class="clear" />
    </div>
</div>
