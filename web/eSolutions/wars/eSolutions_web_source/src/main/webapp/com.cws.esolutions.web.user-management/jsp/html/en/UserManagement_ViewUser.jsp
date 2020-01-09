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
 * Package: com.cws.esolutions.web.user-management\jsp\html\en
 * File: UserAccount_ViewUser.jsp
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

<script type="text/javascript">
    <!--
    function changeView()
    {
        document.getElementById('userRoleInput').style.display = 'none';
        document.getElementById('userRoleModify').style.display = 'block';
        document.getElementById('selectRoleChange').style.display = 'none';
        document.getElementById('submitRoleChange').style.display = 'block';
    }

    function submitRoleChange(selectable)
    {
        var newRole = selectable.options[selectable.selectedIndex].text;

        window.location.href = '${pageContext.request.contextPath}/ui/user-management/change-role/account/${userAccount.guid}/role/' + newRole;
    }
    //-->
</script>

<div id="sidebar">
    <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN' or sessionScope.userAccount.role eq 'ADMIN'}">
        <h1><spring:message code="user.mgmt.header" /></h1>
        <ul>
            <li><a href="${pageContext.request.contextPath}/ui/user-management/default" title="<spring:message code='theme.search.banner' />"><spring:message code="theme.search.banner" /></a></li>
            <li><a href="${pageContext.request.contextPath}/ui/user-management/add-user" title="<spring:message code='user.mgmt.create.user' />"><spring:message code="user.mgmt.create.user" /></a></li>
            <li><a href="${pageContext.request.contextPath}/ui/user-management/services/account/${userAccount.guid}" title="<spring:message code='user.mgmt.user.services' />"><spring:message code='user.mgmt.user.services' /></a></li>
            <li><a href="${pageContext.request.contextPath}/ui/user-management/audit/account/${userAccount.guid}" title="<spring:message code='user.mgmt.audit.user' />"><spring:message code='user.mgmt.audit.user' /></a></li>
        </ul>
    </c:if>
</div>

<div id="main">
    <h1><spring:message code="user.mgmt.view.user" arguments="${userAccount.username}" /></h1>

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

    <table id="viewUser">
        <tr>
            <td><label><spring:message code="user.mgmt.user.name" /></label></td>
            <td>${userAccount.username}</td>
            <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN' or sessionScope.userAccount.role eq 'ADMIN'}">
                <td>
                    <a href="${pageContext.request.contextPath}/ui/user-management/reset/account/${userAccount.guid}"
                        title="<spring:message code='user.account.change.password' />"><spring:message code='user.account.change.password' /></a>
                </td>
            </c:if>
        </tr>
        <tr>
            <td><label><spring:message code="user.mgmt.user.role" /></label></td>
            <td id="userRoleInput" style="display: block;">${userAccount.role}</td>
            <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN' or sessionScope.userAccount.role eq 'ADMIN'}">
                <td id="userRoleModify" style="display: none;">
                    <select name="selectRole" id="selectRole">
                        <option value="${userAccount.role}" selected="selected">${userAccount.role}</option>
                        <option><spring:message code="theme.option.select" /></option>
                        <option><spring:message code="theme.option.spacer" /></option>
                        <c:forEach var="role" items="${userRoles}">
                            <option value="${role}">${role}</option>
                        </c:forEach>
                    </select>
                </td>
                <td>
                    <a id="selectRoleChange" href="#" onclick="changeView();"
                        title="<spring:message code='user.mgmt.change.role' />" style="display: block;"><spring:message code='user.mgmt.change.role' /></a>
                    <a id="submitRoleChange" href="#" onclick="submitRoleChange(document.getElementById('selectRole'));"
                        title="<spring:message code='user.mgmt.change.role' />" style="display: none;"><spring:message code='user.mgmt.change.role' /></a>
                </td>
            </c:if>
        </tr>
        <tr>
            <td><label><spring:message code="user.mgmt.user.givenname" /></label></td>
            <td>${userAccount.givenName}</td>
        </tr>
        <tr>
            <td><label><spring:message code="user.mgmt.user.surname" /></label></td>
            <td>${userAccount.surname}</td>
        </tr>
        <tr>
            <td><label><spring:message code="user.mgmt.user.email" /></label></td>
            <td>
                <a href="mailto:${userAccount.emailAddr}" title="">${userAccount.emailAddr}</a>
            </td>
        </tr>
        <tr>
            <td><label><spring:message code="user.mgmt.user.locked" /></label></td>
            <c:choose>
                <c:when test="${userAccount.failedCount ge 3}">
                    <td><spring:message code="theme.true" /></td>
                    <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN' or sessionScope.userAccount.role eq 'ADMIN'}">
                        <td>
                            <a href="${pageContext.request.contextPath}/ui/user-management/unlock/account/${userAccount.guid}"
                                title="<spring:message code='user.mgmt.unlock.account' />"><spring:message code='user.mgmt.unlock.account' /></a>
                        </td>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <td><spring:message code="theme.false" /></td>
                    <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN' or sessionScope.userAccount.role eq 'ADMIN'}">
                        <td>
                            <a href="${pageContext.request.contextPath}/ui/user-management/lock/account/${userAccount.guid}"
                                title="<spring:message code='user.mgmt.lock.account' />"><spring:message code='user.mgmt.lock.account' /></a>
                        </td>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </tr>
        <tr>
            <td><label><spring:message code="user.mgmt.last.logon" /></label></td>
            <td>${userAccount.lastLogin}</td>
        </tr>
        <tr>
            <td><label><spring:message code="user.mgmt.suspend.account" /></label></td>
            <td>${userAccount.suspended}</td>
            <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                <c:choose>
                    <c:when test="${userAccount.suspended eq 'true'}">
                        <td>
                            <a href="${pageContext.request.contextPath}/ui/user-management/unsuspend/account/${userAccount.guid}"
                                  title="<spring:message code='user.mgmt.unsuspend.account' />"><spring:message code='user.mgmt.unsuspend.account' /></a>
                        </td>
                    </c:when>
                    <c:otherwise>
                        <td>
                            <a href="${pageContext.request.contextPath}/ui/user-management/suspend/account/${userAccount.guid}"
                                  title="<spring:message code='user.mgmt.suspend.account' />"><spring:message code='user.mgmt.suspend.account' /></a>
                        </td>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </tr>
    </table>
</div>

<div id="rightbar">&nbsp;</div>
