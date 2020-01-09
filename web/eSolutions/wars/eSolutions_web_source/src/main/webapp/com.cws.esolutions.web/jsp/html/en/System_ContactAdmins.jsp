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
 * Package: com.cws.esolutions.web\jsp\html\en
 * File: System_ContactAdmins.jsp
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
        if (theForm.messageSubject.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Please provide a brief subject for your request.';
            document.getElementById('txtMessageSubject').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('messageSubject').focus();
        }
        else if (theForm.messageBody.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Please provide the information regarding your request.';
            document.getElementById('txtMessageBody').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('messageSubject').focus();
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
    <h1><spring:message code="theme.messaging.send.email.message" /></h1>

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
        <form:form name="submitContactForm" method="post" action="${pageContext.request.contextPath}/ui/common/submit-contact">
            <form:hidden path="messageTo" value="${serviceEmail}" />

            <p>
                <label id="txtMessageSubject"><spring:message code="theme.add.contact.request.subject" /></label>
                <form:input path="messageSubject" />
                <form:errors path="messageSubject" cssClass="error" />
                <label id="txtRequestorEmail"><spring:message code="theme.add.contact.source.email" /></label>
                <form:input path="emailAddr" />
                <form:errors path="emailAddr" cssClass="error" />
                <label id="txtMessageBody"><spring:message code="theme.add.contact.request.body" /></label>
                <form:textarea path="messageBody" />
                <form:errors path="messageBody" cssClass="error" />
                <br /><br />
                <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
            </p>
        </form:form>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
