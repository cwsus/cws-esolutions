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
 * File: AppMgmt_ViewApplications.jsp
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

<div id="sidebar">
    <h1><spring:message code="app.mgmt.header" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/application-management/add-application"title="<spring:message code='app.mgmt.add.application' />"><spring:message code='app.mgmt.add.application' /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="app.mgmt.application.list" /></h1>

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
        <table id="appSearchResults">
            <tr>
                <td><spring:message code="app.mgmt.application.name" /></td>
                <td><spring:message code="app.mgmt.application.version" /></td>
            </tr>
            <c:forEach var="application" items="${applicationList}">
                <tr>
                    <td>
                        <a href="${pageContext.request.contextPath}/ui/application-management/application/${application.guid}"
                            title="${application.name}">${application.name}</a>
                    </td>
                    <td>${application.version}</td>
                </tr>
            </c:forEach>
        </table>
    </p>
</div>

<div id="rightbar">
    <h1 id="alert"><spring:message code="theme.important.information" /></h1>
    <spring:message code="app.mgmt.refresh.current" />
</div>
