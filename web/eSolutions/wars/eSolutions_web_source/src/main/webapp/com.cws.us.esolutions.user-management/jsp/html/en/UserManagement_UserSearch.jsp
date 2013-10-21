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

<div class="feature">
    <c:choose>
        <c:when test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
            <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                <div id="breadcrumb" class="lpstartover">
                    <a href="${pageContext.request.contextPath}/ui/user-management/add-user"
                        title="<spring:message code='admin.account.create.user' />"><spring:message code="admin.account.create.user" /></a>
                </div>
            </c:if>

		    <c:if test="${not empty messageResponse}">
		        <p id="info"><spring:message code="${messageResponse}" /></p>
		    </c:if>
		    <c:if test="${not empty errorResponse}">
		        <p id="error">${errorResponse}</p>
		    </c:if>

            <spring:message code="admin.account.enter.search.criteria" />

            <p id="validationError" />

            <form:form id="searchUserAccounts" name="searchUserAccounts" action="${pageContext.request.contextPath}/ui/user-management/search" method="post">
                <table id="searchAccounts">
                    <tr>
                        <td>
                            <label id="txtUserName"><spring:message code="admin.account.search.username" /><br /></label>
                        </td>
                        <td>
                            <form:input path="username" id="username" />
                            <form:errors path="username" cssClass="validationError" />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label id="txtUserGuid"><spring:message code="admin.account.search.userguid" /><br /></label>
                        </td>
                        <td>
                            <form:input path="guid" id="guid" />
                            <form:errors path="guid" cssClass="validationError" />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label id="txtEmailAddress"><spring:message code="admin.account.search.useremail" /><br /></label>
                        </td>
                        <td>
                            <form:input path="emailAddr" id="emailAddress" />
                            <form:errors path="emailAddr" cssClass="validationError" />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label id="txtDisplayName"><spring:message code="admin.account.search.userdisplayname" /><br /></label>
                        </td>
                        <td>
                            <form:input path="displayName" id="displayName" />
                            <form:errors path="displayName" cssClass="validationError" />
                        </td>
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

            <c:if test="${not empty requestScope.searchResults}">
                <p id="splitter" />

                <strong><spring:message code="admin.account.search.results" /></strong>
                <br />
                <table id="userSearchResults">
                    <tr>
                        <td><spring:message code="admin.account.user.id" /></td>
                        <td><spring:message code="admin.account.user.displayname" /></td>
                    </tr>
                    <c:forEach var="userResult" items="${requestScope.searchResults}">
                        <tr>
                            <td>
                                <a href="${pageContext.request.contextPath}/ui/user-management/account/${userResult.guid}"
                                    title="${userResult.username}">${userResult.username}</a>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/ui/user-management/account/${userResult.guid}"
                                  title="${userResult.displayName}">${userResult.displayName}</a>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </c:if>
        </c:when>
        <c:otherwise>
            <spring:message code="admin.account.not.authorized" />
            <c:if test="${requestScope.isUserLoggedIn ne 'true'}">
                <p>Click <a href="${pageContext.request.contextPath}/ui/home/default" title="Home">here</a> to continue.</p>
            </c:if>
        </c:otherwise>
    </c:choose>
</div>
<br /><br />
