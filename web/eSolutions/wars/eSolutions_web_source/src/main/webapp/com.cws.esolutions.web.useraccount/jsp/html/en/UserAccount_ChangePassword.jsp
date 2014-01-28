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

<script>
<!--
    function validateForm(theForm)
    {
        if ((theForm.currentPassword) && (theForm.currentPassword.value == ''))
        {
            document.getElementById('validationError').innerHTML = 'You must provide your current password.';
            document.getElementById('txtCurrentPassword').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('currentPassword').focus();
        }
        else if (theForm.newPassword.value == '')
        {
            document.getElementById('validationError').innerHTML = 'You must provide your new password.';
            document.getElementById('txtNewPassword').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('newPassword').focus();
        }
        else if (theForm.confirmPassword.value == '')
        {
            document.getElementById('validationError').innerHTML = 'Your new password must be confirmed.';
            document.getElementById('txtConfirmPassword').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('confirmPassword').focus();
        }
        else
        {
            if ((theForm.currentPassword) && (theForm.currentPassword.value == theForm.newPassword.value))
            {
                clearForm();

                document.getElementById('validationError').innerHTML = 'Your new password cannot match your existing password.';
                document.getElementById('execute').disabled = false;
                document.getElementById('currentPassword').focus();
            }
            else if ((theForm.currentPassword) && (theForm.currentPassword.value == theForm.confirmPassword.value))
            {
                clearForm();

                document.getElementById('validationError').innerHTML = 'Your new password cannot match your existing password.';
                document.getElementById('execute').disabled = false;
                document.getElementById('currentPassword').focus();
            }
            else if (theForm.newPassword.value != theForm.confirmPassword.value)
            {
                clearForm();

                document.getElementById('validationError').innerHTML = 'Your new and confirmed passwords must match.';
                document.getElementById('execute').disabled = false;
                document.getElementById('currentPassword').focus();
            }
            else
            {
                theForm.submit();
            }
        }
    }
//-->
</script>

<div id="sidebar">
    <h1><spring:message code="user.account.update.security" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/email" title="<spring:message code='user.account.change.email' />"><spring:message code="user.account.change.email" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/contact" title="<spring:message code='user.account.change.contact' />"><spring:message code="user.account.change.contact" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/security" title="<spring:message code='user.account.change.security.questions' />"><spring:message code="user.account.change.security.questions" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/regenerate-keys" title="<spring:message code='user.account.change.keys' />"><spring:message code="user.account.change.keys" /></a></li>
    </ul>
</div>

<div id="main">
    <div id="error"></div>

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

    <h1><spring:message code="user.account.update.password" /></h1>
    <p>
        <form:form name="submitPasswordChange" id="submitPasswordChange" action="${pageContext.request.contextPath}/ui/user-account/password" method="POST">
            <form:hidden path="isReset" value="${command.isReset}" />
            <form:hidden path="resetKey" value="${param.resetKey}" />
            <p>
                <label id="txtCurrentPassword"><spring:message code="user.account.update.password.current" /></label>
                <form:password path="currentPassword" />
                <form:errors path="currentPassword" cssClass="error" />

                <label id="txtNewPassword"><spring:message code="user.account.update.password.new" /><br /></label>
                <form:password path="newPassword" />
                <form:errors path="newPassword" cssClass="error" />

                <label id="txtConfirmPassword"><spring:message code="user.account.update.password.confirm" /><br /></label>
                <form:password path="confirmPassword" />
                <form:errors path="confirmPassword" cssClass="error" />

                <br /><br />
                <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            </p>
        </form:form>
    </p>
</div>

<div id="rightbar">
    <spring:message code="user.account.update.password.rqmts" />
</div>
