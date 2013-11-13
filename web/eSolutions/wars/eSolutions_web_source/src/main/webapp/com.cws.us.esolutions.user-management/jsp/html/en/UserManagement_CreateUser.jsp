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

<div id="InfoLine"><spring:message code="user.mgmt.create.user" /></div>
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
            <c:when test="${sessionScope.userAccount.role == 'USERADMIN' or sessionScope.userAccount.role == 'SITEADMIN'}">

                <span id="validationError"></span>

	            <form:form id="createNewUser" name="createNewUser" action="${pageContext.request.contextPath}/ui/user-management/add-user" method="post" autocomplete="off">
	                <table id="addUser">
	                    <tr>
	                        <td><label id="txtUsername"><spring:message code="user.mgmt.user.name" /></label></td>
	                        <td><form:input path="username" type="text" size="20" value="" name="username" id="username" /></td>
	                        <td><form:errors path="username" cssClass="validationError" /></td>
	                    </tr>
	                    <tr>
	                        <td><label id="txtUserRole"><spring:message code="user.mgmt.user.role" /></label></td>
	                        <td>
	                            <form:select path="role" name="role" id="role">
	                                <option value="<spring:message code='theme.option.select' />" selected="selected"><spring:message code='theme.option.select' /></option>
	                                <option><spring:message code="theme.option.spacer" /></option>
	                                <c:forEach var="role" items="${roles}">
	                                    <option value="${role}">${role}</option>
	                                </c:forEach>
	                            </form:select>
	                        </td>
	                        <td><form:errors path="role" cssClass="validationError" /></td>
	                    </tr>
	                    <%--
	                    <tr>
	                        <td><label id="createUserUnit"><spring:message code="user.mgmt.user.dept" /></label></td>
	                        <td>
	                            <form:select path="dept" name="dept" id="dept">
	                                <option><spring:message code="theme.option.select" /></option>
	                                <option><spring:message code="theme.option.spacer" /></option>
	                                <c:forEach var="dept" items="${selectableDepts}">
	                                    <option value="${dept}">${dept}</option>
	                                </c:forEach>
	                            </form:select>
	                        </td>
	                        <td><form:errors path="dept" cssClass="validationError" /></td>
	                    </tr>
	                    --%>
	                    <tr>
	                        <td><label id="txtFirstName"><spring:message code="user.mgmt.user.givenname" /></label></td>
	                        <td><form:input path="givenName" type="text" size="20" value="" name="givenName" id="givenName" /></td>
	                        <td><form:errors path="givenName" cssClass="validationError" /></td>
	                    </tr>
	                    <tr>
	                        <td><label id="txtLastName"><spring:message code="user.mgmt.user.surname" /></label></td>
	                        <td><form:input path="surname" type="text" size="20" value="" name="surname" id="surname" /></td>
	                        <td><form:errors path="surname" cssClass="validationError" /></td>
	                    </tr>
	                    <tr>
	                        <td><label id="txtEmailAddr"><spring:message code="user.mgmt.user.email" /></label></td>
	                        <td><form:input path="emailAddr" type="text" size="20" value="" name="emailAddr" id="emailAddr" /></td>
	                        <td><form:errors path="emailAddr" cssClass="validationError" /></td>
	                    </tr>
	                    <tr>
	                        <td><label id="txtLockout"><spring:message code="user.mgmt.user.locked" /></label></td>
	                        <td><form:checkbox path="suspended" name="suspended" id="suspended" /></td>
	                        <td><form:errors path="suspended" cssClass="validationError" /></td>
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
	        </c:when>
	        <c:otherwise>
	            <spring:message code="theme.system.request.unauthorized" />
	            <c:if test="${requestScope.isUserLoggedIn != 'true'}">
	                <p>Click <a href="${pageContext.request.contextPath}/ui/home/default" title="Home">here</a> to continue.</p>
	            </c:if>
	        </c:otherwise>
	    </c:choose>
    </div>

    <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
        <div id="content-left">
            <ul>
                <li>
                    <a href="${pageContext.request.contextPath}/ui/user-management/default"
                        title="<spring:message code='user.mgmt.search.header' />"><spring:message code="user.mgmt.search.header" /></a>
                </li>
            </ul>
        </div>
    </c:if>
</div>
