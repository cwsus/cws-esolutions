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
 * Package: theme\cws\html\en\jsp
 * File: navbar.jsp
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

<div id="menu">
    <ul>
        <c:if test="${not empty fn:trim(sessionScope.userAccount)}">
            <c:if test="${sessionScope.userAccount.status == 'SUCCESS'}">
                <c:if test="${sessionScope.userAccount.role ne 'USERADMIN'}">
                    <li>
                        <a href="${pageContext.request.contextPath}/ui/application-management/default" title="<spring:message code='theme.navbar.application-mgmt' />">
                            <spring:message code='theme.navbar.application-mgmt' /></a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/ui/dns-service/default" title="<spring:message code='theme.navbar.dns-services' />">
                            <spring:message code='theme.navbar.dns-services' /></a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/ui/service-management/default" title="<spring:message code='theme.navbar.service-mgmt' />">
                            <spring:message code='theme.navbar.service-mgmt' /></a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/ui/system-management/default" title="<spring:message code='theme.navbar.system-mgmt' />">
                            <spring:message code='theme.navbar.system-mgmt' /></a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/ui/service-messaging/default" title="<spring:message code='theme.navbar.messaging' />">
                            <spring:message code='theme.navbar.messaging' /></a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                    <li class="last">
                        <a href="${pageContext.request.contextPath}/ui/user-management/default" title="<spring:message code='theme.navbar.useradmin' />">
                            <spring:message code='theme.navbar.useradmin' /></a>
                    </li>
                </c:if>
            </c:if>
        </c:if>
    </ul>
</div>
