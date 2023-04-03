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
 * Package: com.cws.esolutions.web.login\jsp\html\en
 * File: Login.jsp
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
        if (theForm.username.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Your username must be provided.';
            document.getElementById('txtUsername').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('username').focus();
        }
        else if (theForm.password.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Your password must be provided.';
            document.getElementById('txtPassword').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('username').focus();
        }
        else
        {
            theForm.submit();
        }
    }
//-->
</script>
<c:set var="resetAllowed" value="${allowUserReset}" />

<div id="homecontent">
	<div class="wrapper">
	    <h1><spring:message code="login.user.combined.message" /></h1>

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

        <form:form id="submitCombinedLogin" name="submitCombinedLogin" action="${pageContext.request.contextPath}/ui/auth/submit" method="post">
            <input type="hidden" name="vpath" id="vpath" value="${param.vpath}" />

            <p>
                <label id="txtUsername"><spring:message code="login.user.name" /></label>
                <form:input path="username" /> <c:if test="${resetAllowed eq true}"><a href="${pageContext.request.contextPath}/ui/online-reset/forgot-username"
                	title="<spring:message code='login.user.forgot_uid' />"><spring:message code="login.user.forgot_uid" /></a></c:if>
                <form:errors path="username" cssClass="error" />
                <br /><br />
                <label id="txtPassword"><spring:message code="login.user.pwd" /></label>
                <form:password path="password" /> <c:if test="${resetAllowed eq true}"><a href="${pageContext.request.contextPath}/ui/online-reset/forgot-password"
	            	title="<spring:message code='login.user.forgot_pwd' />"><spring:message code="login.user.forgot_pwd" /></a></c:if>
                <form:errors path="password" cssClass="error" />
                <br /><br />
                <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
            </p>
        </form:form>
  		<br class="clear" />
	</div>
</div>
