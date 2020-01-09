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
 * Package: com.cws.esolutions.web\jsp\html\en
 * File: System_RequestComplete.jsp
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

<div id="sidebar">&nbsp;</div>

<div id="main">
    <h1><spring:message code="request.complete" /></h1>
    <spring:message code="system.request.complete" />

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

    <c:choose>
        <c:when test="${empty fn:trim(sessionScope.userAccount) or empty fn:trim(sessionScope.userAccount.status)}">
            <p>
                <a href="${pageContext.request.contextPath}/ui/login/default" title="<spring:message code='theme.navbar.login' />">
                    <spring:message code="theme.click.continue" /></a>
            </p>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${sessionScope.userAccount.status == 'SUCCESS'}">
                    <p>
                        <a href="${pageContext.request.contextPath}/ui/common/default" title="<spring:message code='theme.navbar.home' />">
                            <spring:message code="theme.click.continue" /></a>
                    </p>
                </c:when>
                <c:otherwise>
                    <p>
                        <a href="${pageContext.request.contextPath}/ui/login/default" title="<spring:message code='theme.navbar.login' />">
                            <spring:message code="theme.click.continue" /></a>
                    </p>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</div>

<div id="rightbar">&nbsp;</div>
