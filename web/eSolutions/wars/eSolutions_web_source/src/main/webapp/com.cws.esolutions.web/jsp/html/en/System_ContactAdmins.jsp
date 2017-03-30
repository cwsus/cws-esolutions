<%--
/**
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
 * com.cws.us.esolutions/jsp/html/en
 * System_ContactAdmins.jsp
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
