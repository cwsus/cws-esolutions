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
 * UserAccount_ChangeEmail.jsp
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

<div id="InfoLine"><spring:message code="user.account.update.contact.info" /></div>
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

        <span id="validationError"></span>

	    <form:form name="submitContactChange" id="submitContactChange" action="${pageContext.request.contextPath}/ui/user-account/contact" method="post">
	        <table id="userauth">
	            <tr>
	                <td><label id="txtTelNumber"><spring:message code="user.account.telephone" /></label></td>
	                <td>
	                    <form:input path="telNumber" value="${sessionScope.userAccount.telephoneNumber}" />
	                    <form:errors path="telNumber" cssClass="validationError" />
	                </td>
	            </tr>
                <tr>
                    <td><label id="txtPagerNumber"><spring:message code="user.account.pager" /></label></td>
                    <td>
                        <form:input path="pagerNumber" value="${sessionScope.userAccount.pagerNumber}" />
                        <form:errors path="pagerNumber" cssClass="validationError" />
                    </td>
                </tr>
	            <tr>
	                <td>&nbsp;</td>
	            </tr>
	            <tr>
	                <td><label id="txtPassword"><spring:message code="login.user.pwd" /></label></td>
	                <td>
	                    <form:password path="currentPassword" onkeypress="if (event.keyCode == 13) { disableButton(this); validateForm(this.form, event); }" />
	                    <form:errors path="currentPassword" cssClass="validationError" />
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
                <a href="${pageContext.request.contextPath}/ui/user-account/password"
                    title="<spring:message code='user.account.change.password' />"><spring:message code="user.account.change.password" /></a>
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
</div>
