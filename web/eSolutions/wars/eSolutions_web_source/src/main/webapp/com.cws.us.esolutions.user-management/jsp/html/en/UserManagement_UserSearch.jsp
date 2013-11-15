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
 * com.cws.us.esolutions.user-management/jsp/html/en
 * UserManagement_UserSearch.jsp
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

<div id="InfoLine"><spring:message code="user.mgmt.search.header" /></div>
<div id="content">
    <div id="content-right">
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

	    <c:choose>
	        <c:when test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                <span id="validationError"></span>
	
	            <form:form id="searchUserAccounts" name="searchUserAccounts" action="${pageContext.request.contextPath}/ui/user-management/search" method="post">
	                <table id="searchAccounts">
	                    <tr>
	                        <td>
	                            <label id="txtUserName"><spring:message code="user.mgmt.user.name" /><br /></label>
	                        </td>
	                        <td>
	                            <form:input path="username" id="username" />
	                            <form:errors path="username" cssClass="validationError" />
	                        </td>
	                    </tr>
	                    <tr>
	                        <td>
	                            <label id="txtUserGuid"><spring:message code="user.mgmt.user.guid" /><br /></label>
	                        </td>
	                        <td>
	                            <form:input path="guid" id="guid" />
	                            <form:errors path="guid" cssClass="validationError" />
	                        </td>
	                    </tr>
	                    <tr>
	                        <td>
	                            <label id="txtEmailAddress"><spring:message code="user.mgmt.user.email" /><br /></label>
	                        </td>
	                        <td>
	                            <form:input path="emailAddr" id="emailAddress" />
	                            <form:errors path="emailAddr" cssClass="validationError" />
	                        </td>
	                    </tr>
	                    <tr>
	                        <td>
	                            <label id="txtDisplayName"><spring:message code="user.mgmt.display.name" /><br /></label>
	                        </td>
	                        <td>
	                            <form:input path="displayName" id="displayName" />
	                            <form:errors path="displayName" cssClass="validationError" />
	                        </td>
	                    </tr>
	                </table>

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
	
	            <c:if test="${not empty requestScope.searchResults}">
	                <p id="splitter" />
	
	                <strong><spring:message code="theme.search.results" /></strong>
	                <br />
	                <table id="userSearchResults">
	                    <tr>
	                        <td><spring:message code="user.mgmt.user.name" /></td>
	                        <td><spring:message code="user.mgmt.display.name" /></td>
	                    </tr>
	                    <c:forEach var="userResult" items="${requestScope.searchResults}">
	                        <tr>
	                            <td>
	                                <a href="${pageContext.request.contextPath}/ui/user-management/view/account/${userResult.guid}"
	                                    title="${userResult.username}">${userResult.username}</a>
	                            </td>
	                            <td>${userResult.displayName}</td>
	                        </tr>
	                    </c:forEach>
	                </table>
	            </c:if>
	        </c:when>
	        <c:otherwise>
	            <spring:message code="theme.system.request.unauthorized" />
	            <c:if test="${requestScope.isUserLoggedIn ne 'true'}">
	                <p>Click <a href="${pageContext.request.contextPath}/ui/common/default" title="Home">here</a> to continue.</p>
	            </c:if>
	        </c:otherwise>
	    </c:choose>
    </div>

    <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
	    <div id="content-left">
	        <ul>
	            <li>
                    <a href="${pageContext.request.contextPath}/ui/user-management/add-user"
                        title="<spring:message code='user.mgmt.create.user' />"><spring:message code="user.mgmt.create.user" /></a>
	            </li>
	        </ul>
	    </div>
    </c:if>
</div>
