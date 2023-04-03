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
 * Package: com.cws.esolutions.web.dnsservice\jsp\html\en
 * File: DNSService_ServiceLookup.jsp
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
        if (theForm.name.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'You must provide a hostname or IP address to perform a query against.';
            document.getElementById('txtAppName').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('name').focus();

			return;
        }

		theForm.submit();
    }
//-->
</script>

<div id="homecontent">
	<div class="wrapper">	
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

		<h1><spring:message code="app.mgmt.search.applications" /></h1>
	    <p>
	        <form:form id="searchRequest" name="searchRequest" action="${pageContext.request.contextPath}/ui/application-management/search" method="post">
	            <label id="txtAppName"><spring:message code="theme.search.terms" /></label>
	            <form:input path="name" />
	            <form:errors path="name" cssClass="error" />
	            <br /><br />
	            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
	            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
	        </form:form>
	    </p>
	</div>
</div>

<div id="container">
    <div class="wrapper">
        <div id="content">
            <h1><spring:message code="app.mgmt.header" /></h1>
            <ul>
                <li><a href="${pageContext.request.contextPath}/ui/application-management/list-applications" title="<spring:message code='app.mgmt.list.applications' />"><spring:message code='app.mgmt.list.applications' /></a></li>
                <li><a href="${pageContext.request.contextPath}/ui/application-management/add-application" title="<spring:message code='app.mgmt.add.application' />"><spring:message code='app.mgmt.add.application' /></a></li>
            </ul>
        </div>
        <c:if test="${not empty searchResults}">
            <h1><spring:message code="theme.search.results" /></h1>
            <table id="searchResults">
                <c:forEach var="result" items="${searchResults}">
                    <tr>
                        <td><a href="${pageContext.request.contextPath}/ui/application-management/application/${result.path}" title="${result.title}">${result.title}</a></td>
                    </tr>
                </c:forEach>
            </table>

            <c:if test="${pages gt 1}">
                <br />
                <hr />
                <br />
                <table>
                    <tr>
                        <c:forEach begin="1" end="${pages}" var="i">
                            <c:choose>
                                <c:when test="${page eq i}">
                                    <td>${i}</td>
                                </c:when>
                                <c:otherwise>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/application-management/search/terms/${searchTerms}/page/${i}" title="{i}">${i}</a>
                                    </td>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </tr>
                </table>
            </c:if>
        </c:if>
        <br class="clear" />
    </div>
</div>

