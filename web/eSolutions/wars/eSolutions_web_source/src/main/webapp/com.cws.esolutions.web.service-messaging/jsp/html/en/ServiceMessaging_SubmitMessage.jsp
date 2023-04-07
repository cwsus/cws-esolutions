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
        if (theForm.messageTitle.value == '')
        {
            document.getElementById('validationError').innerHTML = 'A message subject must be provided.';
            document.getElementById('txtSysMessageSubject').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('messageTitle').focus();
        }
        else if (theForm.messageText.value == '')
        {
            document.getElementById('validationError').innerHTML = 'A message body must be provided.';
            document.getElementById('txtSysMessageBody').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('datacenterStatus').focus();
        }
        else if (!((theForm.isActive1.checked)) && (!(theForm.isActive2.checked)))
        {
            document.getElementById('validationError').innerHTML = 'A message status must be provided.';
            document.getElementById('txtIsMessageActive').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
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

        <h2><spring:message code="svc.messaging.create.banner" /></h2>
        <form:form id="submitSystemMessage" name="" action="${pageContext.request.contextPath}/ui/service-messaging/submit-message" method="post">
            <c:if test="${empty fn:trim(command.messageAuthor)}">
                <form:hidden path="isNewMessage" value="true" />
            </c:if>

            <table id="serviceMessage">
                <tr>
                    <td><label id="txtSysMessageSubject"><spring:message code="svc.messaging.system.message.subject" /></label></td>
                    <td>
                        <form:input path="messageTitle" />
                        <form:errors path="messageTitle" cssClass="error" />
                    </td>
                    <td><label id="txtSysMessageBody"><spring:message code="svc.messaging.system.message.body" /></label></td>
                    <td>
                        <form:input path="messageText" />
                        <form:errors path="messageText" cssClass="error" />
                    </td>
                </tr>
                <tr>
                    <td><label id="txtIsMessageActive"><spring:message code="svc.messaging.system.message.activate" /></label></td>
                    <td>
                        <form:radiobutton path="isActive" value="true" /><spring:message code="svc.messaging.system.message.active" />
                        <form:radiobutton path="isActive" value="false" /><spring:message code="svc.messaging.system.message.inactive" />
                        <form:errors path="isActive" cssClass="error" />
                    </td>
                    <td><label id="txtIsAlertMessage"><spring:message code="svc.messaging.system.message.alert" /></label></td>
                    <td>
                        <form:checkbox path="isAlert" value="true" /><spring:message code="svc.messaging.system.message.alert" />
                        <form:errors path="isAlert" cssClass="error" />
                    </td>
                </tr>
            </table>
            <br class="clear" />
            <br class="clear" />
            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
            <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="redirectOnCancel('/esolutions/ui/service-messaging/default');" />
        </form:form>
    </div>
</div>

<div id="container">
    <div class="wrapper">
        <div id="holder">
            <h1><spring:message code="svc.messaging.list.messages" /></h1>
            <ul>
                <li><a href="${pageContext.request.contextPath}/ui/service-messaging/default" title="spring:message code='svc.messaging.list' />"><spring:message code="svc.messaging.list" /></a></li>
            </ul>
        </div>
        <br class="clear" />
    </div>
</div>
