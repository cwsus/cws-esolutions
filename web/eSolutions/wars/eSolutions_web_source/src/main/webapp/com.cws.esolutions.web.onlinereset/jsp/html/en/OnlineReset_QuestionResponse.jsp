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
 * com.cws.us.esolutions.onlinereset/jsp/html/en
 * OnlineReset_QuestionResponse.jsp
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author cws-khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * cws-khuntly @ Jan 16, 2013 11:53:26 AM
 *     Created.
 */
--%>

<script>
<!--
    function validateForm(theForm)
    {
        if (theForm.secAnswerOne.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'You must provide your account username.';
            document.getElementById('txtAnswerOne').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('secAnswerOne').focus();
        }
        else if (theForm.secAnswerTwo.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'You must provide your account username.';
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

<div id="sidebar">&nbsp;</div>

<div id="main">
    <h1><spring:message code="olr.forgotpwd.message" /></h1>

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
        <form:form id="submitSecurityQuestion" name="submitSecurityQuestion" action="${pageContext.request.contextPath}/ui/online-reset/submit" method="post" autocomplete="off">
            <form:hidden path="resetType" value="${resetType}" />

            <p>
                <label id="txtQuestionOne"><spring:message code="olr.question" /></label>
                ${command.secQuestionOne}
                <label id="txtAnswerOne"><spring:message code="olr.answer" /></label>
                <form:password path="secAnswerOne" />
                <form:errors path="secAnswerOne" cssClass="error" />
                <label id="txtQuestionTwo"><spring:message code="olr.question" /></label>
                ${command.secQuestionTwo}
                <label id="txtAnswerTwo"><spring:message code="olr.answer" /></label>
                <form:password path="secAnswerTwo" />
                <form:errors path="secAnswerTwo" cssClass="error" />
                <br /><br />
                <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); window.location.href = '${pageContext.request.contextPath}/ui/online-reset/cancel';" />
            </p>
        </form:form>
    </p>
</div>

<div id="rightbar">
    <h1><spring:message code="login.user.forgot.info" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/app/help/forgot-questions" title="<spring:message code='olr.forgot.questions' />"><spring:message code="olr.forgot.questions" /></a></li>
    </ul>
</div>
