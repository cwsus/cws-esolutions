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

<div id="InfoLine"><spring:message code="dns.lookup.service.name" /></div>
<div id="content">
    <div id="content-right">
	    <c:if test="${not empty messageResponse}">
	        <p id="info">${messageResponse}</p>
	    </c:if>
	    <c:if test="${not empty errorResponse}">
	        <p id="error">${errorResponse}</p>
	    </c:if>
	    <c:if test="${not empty responseMessage}">
	        <p id="info"><spring:message code="${responseMessage}" /></p>
	    </c:if>
	    <c:if test="${not empty errorMessage}">
	        <p id="error"><spring:message code="${errorMessage}" /></p>
	    </c:if>

        <p id="validationError" />

	    <form:form id="submitNameLookup" name="submitNameLookup" action="${pageContext.request.contextPath}/ui/dns-service/lookup" method="post">
	        <table id="dnsservice">
	            <tr>
	                <td>
	                    <label id="txtServiceName"><spring:message code="dns.service.hostname" /><br /></label>
	                </td>
	                <td>
	                    <form:input path="recordName" />
	                    <form:errors path="recordName" cssClass="validationError" />
	                </td>
	            </tr>
	            <tr>
	                <td>&nbsp;</td>
	            </tr>
	            <tr>
	                <td><label id="txtLookupType"><spring:message code="dns.lookup.record.type" /></label></td>
	                <td>
	                    <form:select path="recordType" onchange="showReverseDisclaimer(this);">
	                        <option><spring:message code="select.default" /></option>
	                        <option><spring:message code="select.spacer" /></option>
	                        <form:options items="${serviceTypes}" />
	                    </form:select>
	                </td>
	            </tr>
	        </table>
	        <br /><br />
	        <table id="inputItems">
	            <tr>
	                <td>
	                    <input type="button" name="execute" value="<spring:message code='button.execute.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
	                </td>
	                <td>
	                    <input type="button" name="reset" value="<spring:message code='button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
	                </td>
	                <td>
	                    <input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
	                </td>
	            </tr>
	        </table>
	    </form:form>

	    <c:if test="${not empty dnsEntry or not empty dnsEntries}">
	        <p id="splitter" />

	        <strong><spring:message code="dns.lookup.results" /></strong>
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
</div>
