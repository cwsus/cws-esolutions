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
 * com.cws.us.esolutions.useraccount/jsp/html/en
 * UserAccount_ChangePassword.jsp
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

<div id="InfoLine"><spring:message code="user.account.update.password" /></div>
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

        <spring:message code="user.account.update.password.rqmts" />

        <span id="validationError"></span>

	    <form:form name="submitPasswordChange" id="submitPasswordChange" action="${pageContext.request.contextPath}/ui/user-account/password" method="POST">
	        <form:hidden path="isReset" value="${command.isReset}" />

	        <table id="userauth">
	            <c:if test="${not command.isReset}">
	                <tr>
	                    <td><label id="txtCurrentPassword"><spring:message code="user.account.update.password.current" /><br /></label></td>
	                    <td>
	                        <form:password path="currentPassword" />
	                        <form:errors path="currentPassword" cssClass="validationError" />
	                    </td>
	                </tr>
	            </c:if>
	            <tr>
	                <td><label id="txtNewPassword"><spring:message code="user.account.update.password.new" /><br /></label></td>
	                <td>
	                    <form:password path="newPassword" />
	                    <form:errors path="newPassword" cssClass="validationError" />
	                </td>
	            </tr>
	            <tr>
	                <td><label id="txtConfirmPassword"><spring:message code="user.account.update.password.confirm" /><br /></label></td>
	                <td>
	                    <form:password path="confirmPassword" onkeypress="if (event.keyCode == 13) { disableButton(this); validateForm(this.form, event); }" />
	                    <form:errors path="confirmPassword" cssClass="validationError" />
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
    </div>

    <c:if test="${sessionScope.userAccount.status eq 'SUCCESS'}">
	    <div id="content-left">
	        <ul>
	            <li>
	                <a href="${pageContext.request.contextPath}/ui/user-account/default"
	                    title="<spring:message code='user.account.view' />"><spring:message code="user.account.view" /></a>
	            </li>
	            <li>
	                <a href="${pageContext.request.contextPath}/ui/user-account/email"
	                    title="<spring:message code='user.account.change.email' />"><spring:message code="user.account.change.email" /></a>
	            </li>
	            <li>
	                <a href="${pageContext.request.contextPath}/ui/user-account/contact"
	                    title="<spring:message code='user.account.change.contact' />"><spring:message code="user.account.change.contact" /></a>
	            </li>
	            <li>
	                <a href="${pageContext.request.contextPath}/ui/user-account/security"
	                    title="<spring:message code='user.account.change.security.questions' />"><spring:message code="user.account.change.security.questions" /></a>
	            </li>
	            <li>
	                <a href="${pageContext.request.contextPath}/ui/user-account/regenerate-keys"
	                    title="<spring:message code='user.account.change.keys' />"><spring:message code="user.account.change.keys" /></a>
	            </li>
	        </ul>
	    </div>
	</c:if>
</div>
