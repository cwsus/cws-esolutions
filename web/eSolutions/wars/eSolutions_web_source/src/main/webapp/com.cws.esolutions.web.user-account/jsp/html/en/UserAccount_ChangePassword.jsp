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
 * Package: com.cws.esolutions.web.useraccount\jsp\html\en
 * File: UserAccount_ViewAccount.jsp
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

<div id="homecontent">
    <div class="wrapper">
        <div id="error" style="color: #FF0000"></div>
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

        <h1><spring:message code="user.account.update.password" /></h1>
        <form:form name="submitPasswordChange" id="submitPasswordChange" action="${pageContext.request.contextPath}/ui/user-account/password" method="POST">
            <form:hidden path="isReset" value="${command.isReset}" />
            <form:hidden path="resetKey" value="${param.resetKey}" />

            <p>
	            <label id="txtCurrentPassword"><spring:message code="user.account.update.password.current" /></label>
	            <form:password path="currentPassword" />
	            <form:errors path="currentPassword" cssClass="error" />
	
	            <br /><br />
	
	            <label id="txtNewPassword"><spring:message code="user.account.update.password.new" /><br /></label>
	            <form:password path="newPassword" />
	            <form:errors path="newPassword" cssClass="error" />
	
	            <br /><br />
	
	            <label id="txtConfirmPassword"><spring:message code="user.account.update.password.confirm" /><br /></label>
	            <form:password path="confirmPassword" />
	            <form:errors path="confirmPassword" cssClass="error" />
	
	            <br /><br />
	
	            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
	            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
	            <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            </p>
        </form:form>
    </div>
</div>

<div id="container">
    <div class="wrapper">
        <div id="content">
            <h1><spring:message code="user.account.update.security" /></h1>
            <ul>
                <li><a href="${pageContext.request.contextPath}/ui/user-account/email" title="<spring:message code='user.account.change.email' />"><spring:message code="user.account.change.email" /></a></li>
                <li><a href="${pageContext.request.contextPath}/ui/user-account/contact" title="<spring:message code='user.account.change.contact' />"><spring:message code="user.account.change.contact" /></a></li>
                <li><a href="${pageContext.request.contextPath}/ui/user-account/security" title="<spring:message code='user.account.change.security.questions' />"><spring:message code="user.account.change.security.questions" /></a></li>
                <li><a href="${pageContext.request.contextPath}/ui/user-account/regenerate-keys" title="<spring:message code='user.account.change.keys' />"><spring:message code="user.account.change.keys" /></a></li>
            </ul>
        </div>
        <br class="clear" />
    </div>
</div>
