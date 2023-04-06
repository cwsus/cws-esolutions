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
        if (theForm.telNumber.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Please provide a contact telephone number.';
            document.getElementById('txtTelNumber').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('telNumber').focus();
        }
        else if (theForm.pagerNumber.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Please provide a notification pager number.';
            document.getElementById('txtPagerNumber').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('pagerNumber').focus();
        }
        else
        {
            theForm.submit();
        }
    }
//-->
</script>

<div id="homecontent">
    <div class="wrapper">
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

        <h1><spring:message code="user.account.update.contact.info" /></h1>
        <form:form name="submitContactChange" id="submitContactChange" action="${pageContext.request.contextPath}/ui/user-account/contact" method="post">
            <table>
                <tr>
                    <td><label id="txtTelNumber"><spring:message code="user.account.telephone" /></label></td>
                    <td>
			            <form:input path="telNumber" value="${sessionScope.userAccount.telephoneNumber}" />
			            <form:errors path="telNumber" cssClass="error" />
                    </td>
                </tr>
                <tr>
                    <td><label id="txtPagerNumber"><spring:message code="user.account.pager" /></label></td>
                    <td>
						<form:input path="pagerNumber" value="${sessionScope.userAccount.pagerNumber}" />
						<form:errors path="pagerNumber" cssClass="error" />
				    </td>
				</tr>
				<tr>
				    <td><label id="txtPassword"><spring:message code="login.user.pwd" /><br /></label></td>
				    <td>
						<form:password path="currentPassword" />
						<form:errors path="currentPassword" cssClass="error" />
                    </td>
            </table>

            <br class="clear" /><br class="clear" />

            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
            <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="redirectOnCancel('/esolutions/ui/user-account/default');" />
        </form:form>
    </div>
</div>

<div id="container">
    <div class="wrapper">
        <div id="content">
            <h1><spring:message code="user.account.update.security" /></h1>
            <ul>
                <li><a href="${pageContext.request.contextPath}/ui/user-account/email" title="<spring:message code='user.account.change.email' />"><spring:message code="user.account.change.email" /></a></li>
                <li><a href="${pageContext.request.contextPath}/ui/user-account/password" title="<spring:message code='user.account.change.password' />"><spring:message code="user.account.change.password" /></a></li>
                <li><a href="${pageContext.request.contextPath}/ui/user-account/security" title="<spring:message code='user.account.change.security.questions' />"><spring:message code="user.account.change.security.questions" /></a></li>
                <li><a href="${pageContext.request.contextPath}/ui/user-account/regenerate-keys" title="<spring:message code='user.account.change.keys' />"><spring:message code="user.account.change.keys" /></a></li>
            </ul>
        </div>
        <br class="clear" />
    </div>
</div>
