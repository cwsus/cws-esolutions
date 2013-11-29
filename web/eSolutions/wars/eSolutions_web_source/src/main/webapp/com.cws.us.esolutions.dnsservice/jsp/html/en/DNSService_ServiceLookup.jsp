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
 * com.cws.us.esolutions.dnsservice/jsp/html/en
 * DNSService_ServiceLookup.jsp
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

<div id="sidebar">
    <h1><spring:message code="dns.service.header" /></h1>
    <ul class="sidemenu">
        <li><a href="http://www.free-css.com/">Home</a></li>
        <li><a href="#TemplateInfo">Template Info</a></li>
        <li><a href="#SampleTags">Sample Tags</a></li>
        <li><a href="http://www.free-css.com/">More Free Templates</a></li>
        <li><a href="http://www.free-css.com/">Premium Template</a></li>
    </ul>
</div>

<div id="main">
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

    <span id="validationError"></span>

    <form:form id="submitNameLookup" name="submitNameLookup" action="${pageContext.request.contextPath}/ui/dns-service/service-lookup" method="post">
        <p>
            <label id="txtServiceName"><spring:message code="dns.service.hostname" /></label>
            <form:input path="recordName" />
            <form:errors path="recordName" cssClass="validationError" />
            <label id="txtLookupType"><spring:message code="dns.lookup.record.type" /></label>            
            <form:select path="recordType" onchange="showReverseDisclaimer(this);">
                <option><spring:message code="theme.option.select" /></option>
                <option><spring:message code="theme.option.spacer" /></option>
                <form:options items="${serviceTypes}" />
            </form:select>
            <br />
            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
            <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
        </p>
    </form:form>

    <c:if test="${not empty dnsEntry or not empty dnsEntries}">
        <h1><spring:message code="dns.lookup.results" /></h1>
	    <br /><br />
	    <c:choose>
	        <c:when test="${not empty dnsEntry}">
	            <spring:message code="dns.service.hostname" /> ${dnsEntry.recordName}<br />
	            <spring:message code="dns.lookup.record.type" /> ${dnsEntry.recordType}<br />
	            <spring:message code="dns.lookup.record.address" /> ${dnsEntry.primaryAddress}<br />
	        </c:when>
	        <c:when test="${not empty dnsEntries}">
	            <c:forEach var="dnsEntry" items="${dnsEntries}">
	                <spring:message code="dns.service.hostname" /> ${dnsEntry.recordName}<br />
	                <spring:message code="dns.lookup.record.type" /> ${dnsEntry.recordType}<br />
	                <spring:message code="dns.lookup.record.address" /> ${dnsEntry.primaryAddress}<br />
	                <br />
	            </c:forEach>
	        </c:when>
	    </c:choose>
	</c:if>
</div>

<div id="rightbar">
    <h1><spring:message code="theme.important.information" /></h1>
    <p>
        <spring:message code="dns.svc.lookup.disclaimer" />
    </p>
</div>