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

<div class="feature">
    <div id="breadcrumb" class="lpstartover">
        <c:if test="${requestScope.dnsADMINEnabled == true}">
            <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                <a href="${pageContext.request.contextPath}/ui/dns-service/failover"
                    title="<spring:message code='select.request.type.dns.failover' />"><spring:message code='select.request.type.dns.failover' /></a> /
                <a href="${pageContext.request.contextPath}/ui/dns-service/addrecord"
                    title="<spring:message code='select.request.type.create.dns.record' />"><spring:message code='select.request.type.create.dns.record' /></a> /
                <a href="${pageContext.request.contextPath}/ui/dns-service/decom"
                    title="<spring:message code='select.request.type.decom.dns.record' />"><spring:message code='select.request.type.decom.dns.record' /></a> /
                <a href="${pageContext.request.contextPath}/ui/dns-service/roleswap"
                    title="<spring:message code='select.request.type.dns.role.swap' />"><spring:message code='select.request.type.dns.role.swap' /></a>
            </c:if>
        </c:if>
    </div>

    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="dns.lookup.service.name" />

    <p id="validationError" />

    <form:form id="submitNameLookup" name="submitNameLookup" action="${pageContext.request.contextPath}/ui/dns-service/lookup" method="post">
        <table id="dnsservice">
            <tr>
                <td>
                    <label id="txtServiceName"><spring:message code="dns.service.enter.hostname" /><br /></label>
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
                    <select name="recordType" id="recordType" onchange="showReverseDisclaimer(this);">
                        <option value="A"><spring:message code="dns.lookup.forward.lookup" />
                        <option value="MX"><spring:message code="dns.lookup.mx.lookup" />
                        <option value="NS"><spring:message code="dns.lookup.ns.lookup" />
                        <option value="TXT"><spring:message code="dns.lookup.txt.lookup" />
                        <option value="PTR"><spring:message code="dns.lookup.ptr.lookup" />
                        <option value="SOA"><spring:message code="dns.lookup.soa.lookup" />
                        <option value="SRV"><spring:message code="dns.lookup.srv.lookup" />
                        <option value="CNAME"><spring:message code="dns.lookup.cname.lookup" />
                    </select>
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
               <spring:message code="dns.service.enter.hostname" /> ${dnsEntry.recordName}<br />
               <spring:message code="dns.lookup.record.type" /> ${dnsEntry.recordType}<br />
               <spring:message code="dns.lookup.record.address" /> ${dnsEntry.primaryAddress}<br />
            </c:when>
            <c:when test="${not empty dnsEntries}">
                <c:forEach var="dnsEntry" items="${dnsEntries}">
                    <spring:message code="dns.service.enter.hostname" /> ${dnsEntry.recordName}<br />
                    <spring:message code="dns.lookup.record.type" /> ${dnsEntry.recordType}<br />
                    <spring:message code="dns.lookup.record.address" /> ${dnsEntry.primaryAddress}<br />
                    <br />
                </c:forEach>
            </c:when>
        </c:choose>
	</c:if>
</div>
<br /><br />
