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
 * com.cws.us.esolutions.login/jsp/html/en
 * LoginOTP.jsp
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
        if (theForm.password.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Your username must be provided.';
            document.getElementById('txtPassword').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('password').focus();
        }
        else if (theForm.otpValue.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Your username must be provided.';
            document.getElementById('txtOtpValue').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('otpValue').focus();
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
    <h1><spring:message code="login.user.otp.message" /></h1>

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
        <form:form name="submitOTP" action="${pageContext.request.contextPath}/ui/login/otp" method="post" autocomplete="off">
            <input type="hidden" name="vpath" id="vpath" value="${param.vpath}" />

            <p>
                <label id="txtPassword"><spring:message code="login.user.pwd" /></label>
                <form:password path="password" />
                <form:errors path="password" cssClass="error" />
                <label id="txtOtpValue"><spring:message code="login.user.otp" /></label>
                <form:password path="otpValue" />
                <form:errors path="otpValue" cssClass="error" />
                <br /><br />
                <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
            </p>
        </form:form>
    </p>
</div>

<div id="rightbar">
    <c:if test="${not empty fn:trim(allowUserReset) and allowUserReset eq 'true'}">
        <h1><spring:message code="login.user.forgot.info" /></h1>
        <ul>
            <li><a href="${pageContext.request.contextPath}/ui/online-reset/forgot-password" title="<spring:message code='login.user.forgot_pwd' />"> <spring:message code="login.user.forgot_pwd" /></a></li>
            <li><a href="${pageContext.request.contextPath}/ui/common/submit-contact" title="<spring:message code='login.user.lostotp' />"> <spring:message code="login.user.lostotp" /></a></li>
        </ul>
    </c:if>
</div>
