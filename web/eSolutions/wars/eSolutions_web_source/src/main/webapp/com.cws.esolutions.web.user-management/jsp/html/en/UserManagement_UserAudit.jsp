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
 * Package: com.cws.esolutions.web.user-management\jsp\html\en
 * File: UserManagement_UserAudit.jsp
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

        <h1><spring:message code="user.mgmt.audit.trail" arguments="${foundUser.username}" /></h1>
        <c:choose>
            <c:when test="${not empty auditList}">
	            <table>
	                <thead>
		                <tr>
                            <th><label><spring:message code="user.mgmt.audit.timestamp" /></label></th>
                            <th><label><spring:message code="user.mgmt.audit.role" /></label></th>
                            <th><label><spring:message code="user.mgmt.audit.authorized" /></label></th>
                            <th><label><spring:message code="user.mgmt.audit.type" /></label></th>
                            <th><label><spring:message code="user.mgmt.audit.application" /></label></th>
                            <th><label><spring:message code="user.mgmt.audit.hostinfo" /></label></th>
	                    </tr>
	                </thead>
	                <tbody>
	                    <c:forEach var="entry" items="${auditList}">
        	                <tr>
                                <td>${entry.auditDate}</td>
	                            <td>${entry.userRole}</td>
	                            <td>${entry.authorized}</td>
	                            <td>${entry.auditType}</td>
	                            <td>${entry.applicationName}</td>
	                            <td>${entry.hostInfo.get(1)}</td>
                            </tr>
   		                </c:forEach>
	                </tbody>
	            </table>
                <br class="clear" />
                <br class="clear" />
	            <c:if test="${pages gt 1}">
	                <br />
	                <hr />
	                <table>
	                    <tr>
	                        <c:forEach begin="1" end="${pages}" var="i">
	                            <c:set var="pageCount" value="${i}" />
	
	                            <c:choose>
	                                <c:when test="${page eq i}">
	                                    <td>${i}</td>
	                                    <c:if test="${pageCount gt 10 and i eq 10}">
	                                        <td><a href="${pageContext.request.contextPath}/user-management/audit/account/${userAccount.guid}/page/10" title="Next">Next</a></td>
	                                    </c:if>
	                                </c:when>
	                                <c:otherwise>
	                                    <td>
	                                        <a href="${pageContext.request.contextPath}/user-management/audit/account/${userAccount.guid}/page/${i}" title="{i}">${i}</a>
	                                    </td>
	                                </c:otherwise>
	                            </c:choose>
	                        </c:forEach>
	                    </tr>
	                </table>
	            </c:if>
	        </c:when>
	        <c:otherwise>
                <spring:message code="user.mgmt.no.audit.found" />
            </c:otherwise>
        </c:choose>
    </div>
</div>

<div id="container">
    <div class="wrapper">
        <div id="content">
		    <h1><spring:message code="user.mgmt.header" /></h1>
		    <ul>
		        <li><a href="${pageContext.request.contextPath}/ui/user-management/default" title="<spring:message code='theme.search.banner' />"><spring:message code="theme.search.banner" /></a></li>
		        <li><a href="${pageContext.request.contextPath}/ui/user-management/add-user" title="<spring:message code='user.mgmt.create.user' />"><spring:message code="user.mgmt.create.user" /></a></li>
		        <li><a href="${pageContext.request.contextPath}/ui/user-management/view/account/${userAccount.guid}" title="<spring:message code='theme.previous.page' />"><spring:message code='theme.previous.page' /></a></li>
		    </ul>
        </div>
        <br class="clear" />
    </div>
</div>
