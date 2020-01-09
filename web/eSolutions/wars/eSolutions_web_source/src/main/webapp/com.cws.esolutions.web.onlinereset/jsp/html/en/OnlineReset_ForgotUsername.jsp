<%--
/*
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
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
 * Package: com.cws.esolutions.web.onlinereset\jsp\html\en
 * File: OnlineReset_ForgotUsername.jsp
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
        if (theForm.emailAddr.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Your email address must be provided.';
            document.getElementById('txtEmailAddr').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('emailAddr').focus();
        }
        else
        {
            theForm.submit();
        }
    }
//-->
</script>

<div id="sidebar">&nbsp;</div>

<div id="main">
    <h1><spring:message code="olr.provide.email.address" /></h1>

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

    <p>
        <form:form id="submitEmailForUserSearch" name="submitEmailForUserSearch" action="${pageContext.request.contextPath}/ui/online-reset/forgot-username" method="post" autocomplete="off">
            <form:hidden path="resetType" value="${resetType}" />

            <p>
                <label id="txtEmailAddr"><spring:message code="olr.user.email.address" /></label>
                <form:input path="emailAddr" />
                <form:errors path="emailAddr" cssClass="error" />
                <br /><br />
                <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            </p>
        </form:form>
    </p>
</div>

<div id="rightbar">
    <h1><spring:message code="login.user.forgot.info" /></h1>
    <ul>
        <li>
            <a href="${pageContext.request.contextPath}/ui/app/help/forgot-email" title="<spring:message code="olr.user.forgot.email" />">
                <spring:message code="olr.user.forgot.email" /></a>
        </li>
    </ul>
</div>
