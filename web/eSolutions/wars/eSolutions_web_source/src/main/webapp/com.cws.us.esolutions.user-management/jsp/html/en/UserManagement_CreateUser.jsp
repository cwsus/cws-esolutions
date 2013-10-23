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
 * UserManagement_CreateUser.jsp
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
	    <c:when test="${sessionScope.userAccount.role == 'USERADMIN' or sessionScope.userAccount.role == 'SITEADMIN'}">
            <c:if test="${not empty messageResponse}">
                <p id="info">${messageResponse}</p>
            </c:if>
            <c:if test="${not empty errorResponse}">
                <p id="error">${errorResponse}</p>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <p id="error"><spring:message code="${errorMessage}" /></p>
            </c:if>

            <spring:message code="admin.create.new.account" />

            <p id="validationError" />

            <form:form id="createNewUser" name="createNewUser" action="${pageContext.request.contextPath}/ui/user-management/add-user" method="post" autocomplete="off">
                <table id="addUser">
                    <tr>
                        <td><label id="createUserName"><spring:message code="admin.account.user.id" /></label></td>
                        <td><form:input path="username" type="text" size="20" value="" name="username" id="username" /></td>
                        <td><form:errors path="username" cssClass="validationError" /></td>
                    </tr>
                    <tr>
                        <td><label id="createUserRole"><spring:message code="admin.account.user.role" /></label></td>
                        <td>
                            <form:select path="role" name="role" id="role">
                                <option value="<spring:message code='admin.account.user.select' />" selected="selected"><spring:message code='admin.account.user.select' /></option>
                                <option value=""></option>
                                <c:forEach var="role" items="${selectableRoles}">
									<option><spring:message code="select.default" /></option>
									<option><spring:message code="select.spacer" /></option>
                                    <option value="${role}">${role}</option>
                                </c:forEach>
                            </form:select>
                        </td>
                        <td><form:errors path="role" cssClass="validationError" /></td>
                    </tr>
                    <%--
                    <tr>
                        <td><label id="createUserUnit"><spring:message code="admin.account.user.dept" /></label></td>
                        <td>
                            <form:select path="dept" name="dept" id="dept">
								<option><spring:message code="select.default" /></option>
								<option><spring:message code="select.spacer" /></option>
                                <c:forEach var="dept" items="${selectableDepts}">
                                    <option value="${dept}">${dept}</option>
                                </c:forEach>
                            </form:select>
                        </td>
                        <td><form:errors path="dept" cssClass="validationError" /></td>
                    </tr>
                    <tr>
                        <td><label id="createUserGroup"><spring:message code="admin.account.user.group" /></label></td>
                        <td>
                            <form:select path="group" name="group" id="group">
								<option><spring:message code="select.default" /></option>
								<option><spring:message code="select.spacer" /></option>
                                <c:forEach var="group" items="${selectableGroups}">
                                    <option value="${group}">${group}</option>
                                </c:forEach>
                            </form:select>
                        </td>
                        <td><form:errors path="group" cssClass="validationError" /></td>
                    </tr>
                    --%>
                    <tr>
                        <td><label id="createuserFirstName"><spring:message code="admin.account.user.firstname" /></label></td>
                        <td><form:input path="givenName" type="text" size="20" value="" name="givenName" id="givenName" /></td>
                        <td><form:errors path="givenName" cssClass="validationError" /></td>
                    </tr>
                    <tr>
                        <td><label id="createUserLastName"><spring:message code="admin.account.user.lastname" /></label></td>
                        <td><form:input path="surname" type="text" size="20" value="" name="surname" id="surname" /></td>
                        <td><form:errors path="surname" cssClass="validationError" /></td>
                    </tr>
                    <tr>
                        <td><label id="createUserEmail"><spring:message code="admin.account.user.email" /></label></td>
                        <td><form:input path="emailAddr" type="text" size="20" value="" name="emailAddr" id="emailAddr" /></td>
                        <td><form:errors path="emailAddr" cssClass="validationError" /></td>
                    </tr>
                    <tr>
                        <td><label id="createUserLock"><spring:message code="admin.account.user.locked" /></label></td>
                        <td><form:checkbox path="suspended" name="suspended" id="suspended" /></td>
                        <td><form:errors path="suspended" cssClass="validationError" /></td>
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
	        <c:if test="${requestScope.isUserLoggedIn != 'true'}">
	            <p>Click <a href="${pageContext.request.contextPath}/ui/home/default" title="Home">here</a> to continue.</p>
	        </c:if>
	    </c:otherwise>
	</c:choose>
</div>
<br /><br />
