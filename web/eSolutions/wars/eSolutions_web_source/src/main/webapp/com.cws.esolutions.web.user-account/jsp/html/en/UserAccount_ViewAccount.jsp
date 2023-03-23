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
 * Package: com.cws.esolutions.web.useraccount\jsp\html\en
 * File: UserAccount_ViewAccount.jsp
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

        <h2 class="title"><spring:message code="user.account.view.profile" arguments="${sessionScope.userAccount.displayName}" /></h2>
        <table id="viewUserAccount">
            <tr>
                <td><label><spring:message code="user.account.username" /></label></td>
                <td>${sessionScope.userAccount.username}</td>
            </tr>
            <tr>
                <td><label><spring:message code="user.account.name" /></label></td>
                <td>${sessionScope.userAccount.displayName}</td>
            </tr>
            <tr>
                <td><label><spring:message code="user.account.givenname" /></label></td>
                <td>${sessionScope.userAccount.givenName}</td>
            </tr>
            <tr>
                <td><label><spring:message code="user.account.surname" /></label></td>
                <td>${sessionScope.userAccount.surname}</td>
            </tr>
            <tr>
                <td><label><spring:message code="user.account.telephone" /></label></td>
                <td>${sessionScope.userAccount.telephoneNumber}</td>
            </tr>
            <tr>
                <td><label><spring:message code="user.account.pager" /></label></td>
                <td>${sessionScope.userAccount.pagerNumber}</td>
            </tr>
            <tr>
                <td><label><spring:message code="user.account.email.addr" /></label></td>
                <td>${sessionScope.userAccount.emailAddr}</td>
            </tr>
        </table>
    </div>
</div>

<div id="container">
    <div class="wrapper">
        <div id="content">
            <h1><spring:message code="user.account.select.options" /></h1>
			<ul>
				<li><a href="${pageContext.request.contextPath}/ui/user-account/email" title="<spring:message code='user.account.change.email' />"><spring:message code="user.account.change.email" /></a></li>
				<li><a href="${pageContext.request.contextPath}/ui/user-account/contact" title="<spring:message code='user.account.change.contact' />"><spring:message code="user.account.change.contact" /></a></li>
				<li><a href="${pageContext.request.contextPath}/ui/user-account/password" title="<spring:message code='user.account.change.password' />"><spring:message code="user.account.change.password" /></a></li>
				<li><a href="${pageContext.request.contextPath}/ui/user-account/security" title="<spring:message code='user.account.change.security.questions' />"><spring:message code="user.account.change.security.questions" /></a></li>
				<li><a href="${pageContext.request.contextPath}/ui/user-account/regenerate-keys" title="<spring:message code='user.account.change.keys' />"><spring:message code="user.account.change.keys" /></a></li>
			</ul>
        </div>
        <br class="clear" />
    </div>
</div>
