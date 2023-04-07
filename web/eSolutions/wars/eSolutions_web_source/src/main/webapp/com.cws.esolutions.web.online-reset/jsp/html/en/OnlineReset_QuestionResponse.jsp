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
 * Package: com.cws.esolutions.web.application-management\jsp\html\en
 * File: AppMgmt_AddApplication.jsp
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
        if (theForm.secAnswerOne.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Please provide an answer for your security question.';
            document.getElementById('txtAnswerOne').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('secAnswerOne').focus();
        }
        else if (theForm.secAnswerTwo.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Please provide an answer for your security question.';
            document.getElementById('txtAnswerTwo').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('secAnswerOne').focus();
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

        <h1><spring:message code="olr.forgotpwd.message" /></h1>
        <form:form id="submitSecurityQuestion" name="submitSecurityQuestion" action="${pageContext.request.contextPath}/ui/online-reset/submit" method="post" autocomplete="off">
            <form:hidden path="resetType" value="${resetType}" />
            <form:hidden path="guid" value="${resetGuid}" />
            <form:hidden path="username" value="${resetUsername}" />

            <table>
                <tr>
                    <td><label id="txtQuestionOne"><spring:message code="user.account.update.security.question" /></label></td>
                    <td>
                        <form:select path="secQuestionOne">
                            <option><spring:message code="theme.option.select" /></option>
                            <option><spring:message code="theme.option.spacer" /></option>
                            <form:options items="${questionList}" />
                        </form:select>
                        <form:errors path="secQuestionOne" cssClass="error" />
                    </td>
                    <td><label id="txtAnswerOne"><spring:message code="user.account.update.security.answer" /></label></td>
                    <td>
                        <form:password path="secAnswerOne" />
                        <form:errors path="secAnswerOne" cssClass="error" />
                    </td>
                </tr>
                <tr>
                    <td><label id="txtQuestionTwo"><spring:message code="user.account.update.security.question" /></label></td>
                    <td>
                        <form:select path="secQuestionTwo">
                            <option><spring:message code="theme.option.select" /></option>
                            <option><spring:message code="theme.option.spacer" /></option>
                            <form:options items="${questionList}" />
                        </form:select>
                        <form:errors path="secQuestionTwo" cssClass="error" />
                    </td>
                    <td><label id="txtAnswerTwo"><spring:message code="user.account.update.security.answer" /></label></td>
                    <td>
                        <form:password path="secAnswerTwo" />
                        <form:errors path="secAnswerTwo" cssClass="error" />
                    </td>
                </tr>
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
        <div id="holder">
	        <h1><spring:message code="login.user.forgot.info" /></h1>
	        <ul>
	            <li><a href="${pageContext.request.contextPath}/ui/app/help/forgot-questions" title="<spring:message code='olr.forgot.questions' />"><spring:message code="olr.forgot.questions" /></a></li>
	        </ul>
        </div>
        <br class="clear" />
    </div>
</div>
