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
        if (theForm.recordName.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'You must provide a hostname or IP address to perform a query against.';
            document.getElementById('txtServiceName').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('recordName').focus();

			return;
        }
        else if ((theForm.recordType.value == 'Select....') || (theForm.recordType.value == '------'))
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'You must provide a query record type.';
            document.getElementById('txtLookupType').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('recordName').focus();

            return;
        }

		theForm.submit();
    }
//-->
</script>

<div id="homecontent">
	<div class="wrapper">
	    <h1><spring:message code="dns.lookup.service.name" /></h1>
	
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
	
	    <p>
	        <form:form id="submitNameLookup" name="submitNameLookup" action="${pageContext.request.contextPath}/ui/dns-service/search" method="post">
	            <p>
	                <label id="txtServiceName"><spring:message code="dns.service.hostname" /></label>
	                <form:input path="recordName" />
	                <form:errors path="recordName" cssClass="error" />
	                <label id="txtLookupType"><spring:message code="dns.lookup.record.type" /></label>
	                <form:select path="recordType">
	                    <option><spring:message code="theme.option.select" /></option>
	                    <option><spring:message code="theme.option.spacer" /></option>
	                    <form:options items="${serviceTypes}" />
	                </form:select>
	                <br /><br />
	                <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
	                <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
	            </p>
	        </form:form>
	    </p>
	</div>
</div>

<c:if test="${not empty dnsEntry or not empty dnsEntries}">
	<div id="container">
		<div class="wrapper">
			<div id="content">
				<h2><spring:message code="dns.lookup.results" /></h2>
				<p>
					<c:choose>
						<c:when test="${not empty dnsEntry}">
								<spring:message code="dns.service.hostname" /> <a href="${dnsEntry.recordName}" title="${dnsEntry.recordName}">${dnsEntry.recordName}</a><br />
								<spring:message code="dns.lookup.record.type" /> ${dnsEntry.recordType}<br />
								<spring:message code="dns.lookup.record.address" /> ${dnsEntry.recordAddress}<br />
						</c:when>
						<c:when test="${not empty dnsEntries}">
							<c:forEach var="dnsEntry" items="${dnsEntries}">
								<spring:message code="dns.service.hostname" /> <a href="${dnsEntry.recordName}" title="${dnsEntry.recordName}">${dnsEntry.recordName}</a><br />
								<spring:message code="dns.lookup.record.type" /> ${dnsEntry.recordType}<br />
								<spring:message code="dns.lookup.record.address" /> ${dnsEntry.recordAddress}<br />
								<br />
							</c:forEach>
						</c:when>
					</c:choose>
				</p>
			</div>
			<br class="clear" />
		</div>
	</div>
</c:if>
