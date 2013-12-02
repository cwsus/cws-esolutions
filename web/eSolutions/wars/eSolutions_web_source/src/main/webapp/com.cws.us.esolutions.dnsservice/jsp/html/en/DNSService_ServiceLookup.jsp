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
<div id="sidebar">&nbsp;</div>

<div id="main">
    <h1><spring:message code="dns.lookup.service.name" /></h1>

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
        <form:form id="submitNameLookup" name="submitNameLookup" action="${pageContext.request.contextPath}/ui/dns-service/service-lookup" method="post">
            <p>
                <label id="txtServiceName"><spring:message code="dns.service.hostname" /></label>
                <form:input path="recordName" />
                <form:errors path="recordName" cssClass="error" />
                <label id="txtLookupType"><spring:message code="dns.lookup.record.type" /></label>
                <form:select path="recordType" onchange="showReverseDisclaimer(this);">
                    <option><spring:message code="theme.option.select" /></option>
                    <option><spring:message code="theme.option.spacer" /></option>
                    <form:options items="${serviceTypes}" />
                </form:select>
                <br /><br />
                <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); window.location.href = '${pageContext.request.contextPath}/ui/online-reset/cancel';" />
            </p>
        </form:form>
    </p>
    <hr />
    <c:if test="${not empty dnsEntry or not empty dnsEntries}">
        <h1><spring:message code="dns.lookup.results" /></h1>
        <br /><br />
        <c:choose>
            <c:when test="${not empty dnsEntry}">
                <spring:message code="dns.service.hostname" /> <a href="${dnsEntry.recordName}" title="${dnsEntry.recordName}">${dnsEntry.recordName}</a><br />
                <spring:message code="dns.lookup.record.type" /> ${dnsEntry.recordType}<br />
                <spring:message code="dns.lookup.record.address" /> ${dnsEntry.primaryAddress}<br />
            </c:when>
            <c:when test="${not empty dnsEntries}">
                <c:forEach var="dnsEntry" items="${dnsEntries}">
                    <spring:message code="dns.service.hostname" /> <a href="${dnsEntry.recordName}" title="${dnsEntry.recordName}">${dnsEntry.recordName}</a><br />
                    <spring:message code="dns.lookup.record.type" /> ${dnsEntry.recordType}<br />
                    <spring:message code="dns.lookup.record.address" /> ${dnsEntry.primaryAddress}<br />
                    <br />
                </c:forEach>
            </c:when>
        </c:choose>
    </c:if>
</div>

<div id="rightbar">
    <p id="info"><spring:message code="theme.important.information" /></p>
    <spring:message code="dns.svc.lookup.disclaimer" />
</div>
