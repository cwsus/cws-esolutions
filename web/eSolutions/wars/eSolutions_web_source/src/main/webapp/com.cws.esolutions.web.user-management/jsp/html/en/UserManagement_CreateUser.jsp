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
 * com.cws.us.esolutions.user-management/jsp/html/en
 * UserManagement_CreateUser.jsp
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
        if (theForm.username.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Please provide a brief subject for your request.';
            document.getElementById('"txtUsername"').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('username').focus();
        }
        else if (theForm.role.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Please provide the information regarding your request.';
            document.getElementById('txtUserRole').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('role').focus();
        }
        else if (theForm.givenName.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Please provide the information regarding your request.';
            document.getElementById('txtFirstName').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('givenName').focus();
        }
        else if (theForm.surname.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Please provide the information regarding your request.';
            document.getElementById('txtLastName').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('surname').focus();
        }
        else if (theForm.emailAddr.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Please provide the information regarding your request.';
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

<div id="sidebar">
    <h1><spring:message code="user.mgmt.header" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/user-management/default" title="<spring:message code='theme.search.banner' />"><spring:message code="theme.search.banner" /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="user.mgmt.create.user" /></h1>

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
        <form:form id="createNewUser" name="createNewUser" action="${pageContext.request.contextPath}/ui/user-management/add-user" method="post" autocomplete="off">
            <label id="txtUsername"><spring:message code="user.mgmt.user.name" /></label>
            <form:input path="username" type="text" size="20" value="" name="username" id="username" />
            <form:errors path="username" cssClass="error" />

            <label id="txtUserRole"><spring:message code="user.mgmt.user.role" /></label>
            <form:select path="role" name="role" id="role">
                <option value="<spring:message code='theme.option.select' />" selected="selected"><spring:message code='theme.option.select' /></option>
                <option><spring:message code="theme.option.spacer" /></option>
                <c:forEach var="role" items="${roles}">
                    <option value="${role}">${role}</option>
                </c:forEach>
            </form:select>
            <form:errors path="role" cssClass="error" />

            <%--
            <label id="createUserUnit"><spring:message code="user.mgmt.user.dept" /></label>
            <form:select path="dept" name="dept" id="dept">
                <option><spring:message code="theme.option.select" /></option>
                <option><spring:message code="theme.option.spacer" /></option>
                <c:forEach var="dept" items="${selectableDepts}">
                    <option value="${dept}">${dept}</option>
                </c:forEach>
            </form:select>
            <form:errors path="dept" cssClass="error" />
            --%>

            <label id="txtFirstName"><spring:message code="user.mgmt.user.givenname" /></label>
            <form:input path="givenName" type="text" size="20" value="" name="givenName" id="givenName" />
            <form:errors path="givenName" cssClass="error" />

            <label id="txtLastName"><spring:message code="user.mgmt.user.surname" /></label>
            <form:input path="surname" type="text" size="20" value="" name="surname" id="surname" />
            <form:errors path="surname" cssClass="error" />

            <label id="txtEmailAddr"><spring:message code="user.mgmt.user.email" /></label>
            <form:input path="emailAddr" type="text" size="20" value="" name="emailAddr" id="emailAddr" />
            <form:errors path="emailAddr" cssClass="error" />

            <label id="txtLockout"><spring:message code="user.mgmt.user.locked" /></label>
            <form:checkbox path="suspended" name="suspended" id="suspended" />
            <form:errors path="suspended" cssClass="error" />

            <br /><br />
            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
            <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
        </form:form>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
