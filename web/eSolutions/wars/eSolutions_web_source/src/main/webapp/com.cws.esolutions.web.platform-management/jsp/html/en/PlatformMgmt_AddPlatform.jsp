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
        if (theForm.platformName.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'A platform name must be provided.';
            document.getElementById('txtPlatformName').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('platformName').focus();
        }
        else
        {
            selectElement = document.getElementById('platformStatus');

            if ((selectElement.text == 'Select....') || (selectElement.text == '------'))
            {
                clearText(theForm);

                document.getElementById('validationError').innerHTML = 'A platform status must be provided.';
                document.getElementById('txtPlatformStatus').style.color = '#FF0000';
                document.getElementById('execute').disabled = false;
                document.getElementById('platformName').focus();
            }
            else
            {
            	theForm.submit();
            }
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

        <h1><spring:message code="platform.mgmt.header" /></h1>
        <ul>
            <li><a href="${pageContext.request.contextPath}/ui/platform-management/list-platforms" title="<spring:message code='platform.mgmt.list.platforms' />"><spring:message code="platform.mgmt.list.platforms" /></a></li>
        </ul>
    </div>
</div>

<div id="container">
    <div class="wrapper">
        <div id="holder">
            <div id="validationError" style="color: #FF0000"></div>

            <h2><spring:message code="datacenter.mgmt.add.datacenter" /></h2>
            <ul id="latestnews">
                <li>
                    <p>
                        <form:form id="submitPlatformData" name="submitPlatformData" action="${pageContext.request.contextPath}/ui/platform-management/add-platform" method="post">
                            <label id="txtPlatformName"><spring:message code="platform.mgmt.name" /></label>
                            <form:input path="platformName" />
                            <form:errors path="platformName" cssClass="error" />

                            <label id="txtPlatformStatus"><spring:message code="platform.mgmt.status" /></label>
                            <form:select id="platformStatus" path="platformStatus" multiple="false">
                                <option><spring:message code="theme.option.select" /></option>
                                <option><spring:message code="theme.option.spacer" /></option>
                                <form:options items="${statusList}" />
                            </form:select>  
                            <form:errors path="platformStatus" cssClass="error" />

                            <label id="txtPlatformDescription"><spring:message code="platform.mgmt.description" /></label>
                            <form:textarea path="platformDescription" />
                            <form:errors path="platformDescription" cssClass="error" />

                            <br /><br />
                            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                            <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="redirectOnCancel('/esolutions/ui/platform-management/default');" />
                        </form:form>
                    </p>
                </li>
            </ul>
        </div>
        <br class="clear" />
    </div>
</div>
