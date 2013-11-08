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
 * UserManagement_EditUser.jsp
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

<div id="InfoLine"><spring:message code="user.mgmt.update.account" /></div>
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
            <c:when test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                <p id="validationError" />

	            <form:form id="createNewUser" name="createNewUser" action="${pageContext.request.contextPath}/ui/user-management/create" method="post" autocomplete="off">
	                <table id="addUser">
	                    <tr>
	                        <td><spring:message code="admin.account.user.id" /></td>
	                        <td><form:input path="username" type="text" size="20" value="${searchResults.userName}" name="username" id="username" /></td>
	                        <td><form:errors path="username" cssClass="validationError" /></td>
	                    </tr>
	                    <tr>
	                        <td><spring:message code="admin.account.user.role" /></td>
	                        <td>
	                            <select name="userRole" id="userRole">
	                                <option><spring:message code="select.default" /></option>
	                                <option><spring:message code="select.spacer" /></option>
	                                <option value="${searchResults.userRole}" selected="selected">${searchResults.userRole}</option>
	                                <c:forEach var="role" items="${selectableGroups}">
	                                    <option value="${role}">${role}</option>
	                                </c:forEach>
	                            </select>
	                        </td>
	                    </tr>
	                    <tr>
	                        <td><spring:message code="admin.account.user.firstname" /></td>
	                        <td><form:input path="givenName" type="text" size="20" value="${searchResults.givenName}" name="givenName" id="givenName" /></td>
	                        <td><form:errors path="givenName" cssClass="validationError" /></td>
	                    </tr>
	                    <tr>
	                        <td><spring:message code="admin.account.user.lastname" /></td>
	                        <td><form:input path="surname" type="text" size="20" value="${searchResults.surname}" name="surname" id="surname" /></td>
	                        <td><form:errors path="surname" cssClass="validationError" /></td>
	                    </tr>
	                    <tr>
	                        <td><spring:message code="admin.account.user.email" /></td>
	                        <td><form:input path="emailAddr" type="text" size="20" value="${searchResults.emailAddr}" name="emailAddr" id="emailAddr" /></td>
	                        <td><form:errors path="emailAddr" cssClass="validationError" /></td>
	                    </tr>
	                    <tr>
	                        <td><spring:message code="admin.account.user.locked" /></td>
	                        <td>${searchResults.isUserLocked}</td>
	                    </tr>
	                    <tr>
	                        <td><spring:message code="admin.account.user.suspension" /></td>
	                        <td>${searchResults.isPermanentSuspension}</td>
	                    </tr>
	                    <tr>
	                        <td><spring:message code="admin.account.user.password" /></td>
	                        <td>anchor for reset here</td>
	                    </tr>
	                </table>
	                <br /><br />
	                <table id="inputItems">
	                    <tr>
	                        <td>
	                            <input type="button" name="execute" value="<spring:message code='button.execute.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
	                        </td>
	                        <td>
	                            <input type="button" name="reset" value="<spring:message code='button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
	                        </td>
	                        <td>
	                            <input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
	                        </td>
	                    </tr>
	                </table>
	            </form:form>
	        </c:when>
	        <c:otherwise>
	            <spring:message code="admin.account.not.authorized" />
	            <c:if test="${requestScope.isUserLoggedIn ne 'true'}">
	                <p>Click <a href="${pageContext.request.contextPath}/ui/home/default" title="Home">here</a> to continue.</p>
	            </c:if>
	        </c:otherwise>
	    </c:choose>
    </div>

    <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
        <div id="content-left">
            <ul>
                <li>
	                <a href="${pageContext.request.contextPath}/ui/user-management/add-user"
	                    title="<spring:message code='admin.account.create.user' />"><spring:message code="admin.account.create.user" /></a>
	            </li>
	        </ul>
	    </div>
	</c:if>
</div>
