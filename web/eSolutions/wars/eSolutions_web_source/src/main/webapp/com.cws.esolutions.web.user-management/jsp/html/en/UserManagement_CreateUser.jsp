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
 * Package: com.cws.esolutions.web.user-management\jsp\html\en
 * File: UserManagement_CreateUser.jsp
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
<%@page import="com.cws.esolutions.security.enums.SecurityUserRole" %>

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
        else if (theForm.userRole.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Please provide the information regarding your request.';
            document.getElementById('txtUserRole').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('userRole').focus();
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

<div id="homecontent">
    <div class="wrapper">
        <div id="error"></div>
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

        <form:form id="createNewUser" name="createNewUser" action="${pageContext.request.contextPath}/ui/user-management/add-user" method="post" autocomplete="off">
            <table>
                <tr>
                    <td><label id="txtUsername"><spring:message code="user.mgmt.user.name" /></label></td>
                    <td>
			            <form:input path="username" type="text" size="20" value="" name="username" id="username" />
			            <form:errors path="username" cssClass="error" />
                    </td>
                </tr>
                <tr>
                    <td><label id="txtUserRole"><spring:message code="user.mgmt.user.role" /></label></td>
                    <td>
			            <form:select path="userRole" name="userRole" id="userRole">
			                <option><spring:message code='theme.option.select' /></option>
			                <option><spring:message code="theme.option.spacer" /></option>
			                <c:forEach items="${userRoles}" var="entry">
			                    <option>${entry}</option>
			                </c:forEach>
			            </form:select>
			            <form:errors path="userRole" cssClass="error" />
			         </td>
			     </tr>
			     <tr>
			         <td><label id="txtFirstName"><spring:message code="user.mgmt.user.givenname" /></label></td>
			         <td>
			            <form:input path="givenName" type="text" size="20" value="" name="givenName" id="givenName" />
			            <form:errors path="givenName" cssClass="error" />
			         </td>
			     </tr>
			     <tr>
			         <td><label id="txtLastName"><spring:message code="user.mgmt.user.surname" /></label></td>
			         <td>
						 <form:input path="surname" type="text" size="20" value="" name="surname" id="surname" />
						 <form:errors path="surname" cssClass="error" />
					 </td>
				 </tr>
				 <tr>
                     <td><label id="txtEmailAddr"><spring:message code="user.mgmt.user.email" /></label></td>
                     <td>
                         <form:input path="emailAddr" type="text" size="20" value="" name="emailAddr" id="emailAddr" />
                         <form:errors path="emailAddr" cssClass="error" />
                     </td>
                 </tr>
                 <tr>
                     <td><label id="txtTelephoneNumber"><spring:message code="user.mgmt.user.telnum" /></label></td>
                     <td>
                         <form:input path="telephoneNumber" type="text" size="10" value="" name="telephoneNumber" id="telephoneNumber" />
                         <form:errors path="telephoneNumber" cssClass="error" />
                     </td>
                 </tr>
                 <tr>
                     <td><label id="txtPagerNumber"><spring:message code="user.mgmt.user.pagernum" /></label></td>
                     <td>
                         <form:input path="pagerNumber" type="text" size="10" value="" name="pagerNumber" id="pagerNumber" />
                         <form:errors path="pagerNumber" cssClass="error" />
                     </td>
                 </tr>
            </table>
            <br class="clear" /><br class="clear" />
            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
            <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="redirectOnCancel('/esolutions/ui/user-management/default');" />
        </form:form>
    </div>
</div>

<div id="container">
    <div class="wrapper">
        <div id="content">
		    <h1><spring:message code="user.mgmt.header" /></h1>
		    <ul>
		        <li><a href="${pageContext.request.contextPath}/ui/user-management/default" title="<spring:message code='theme.search.banner' />"><spring:message code="theme.search.banner" /></a></li>
		    </ul>
        </div>
        <br class="clear" />
    </div>
</div>
