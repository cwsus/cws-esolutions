<%@ page import="java.util.*" %>
<%--
/**
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
 * com.cws.us.esolutions/jsp/html/en
 * System_DisplayHeaders.jsp
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author cws-khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * cws-khuntly @ June 11, 2019 12:28 PM
 *     Created.
 */
--%>

<div id="sidebar">&nbsp;</div>

<div id="main">
    <h1><%= application.getServerInfo() %></h1>
    <h1>HTTP Request Headers Received</h1>

    <c:if test="${not empty requestAttributes}">
        <h1><spring:message code="header.data.receive" /></h1>
        <br /><br />
        <spring:message code="dns.service.hostname" /> <a href="${dnsEntry.recordName}" title="${dnsEntry.recordName}">${dnsEntry.recordName}</a><br />
        <spring:message code="dns.lookup.record.type" /> ${dnsEntry.recordType}<br />
        <spring:message code="dns.lookup.record.address" /> ${dnsEntry.primaryAddress}<br />
    </c:if>
    <c:if test="${not empty hRequest}">
        <h1><spring:message code="header.data.receive" /></h1>
        <br /><br />
        <spring:message code="dns.service.hostname" /> <a href="${dnsEntry.recordName}" title="${dnsEntry.recordName}">${dnsEntry.recordName}</a><br />
        <spring:message code="dns.lookup.record.type" /> ${dnsEntry.recordType}<br />
        <spring:message code="dns.lookup.record.address" /> ${dnsEntry.primaryAddress}<br />
    </c:if>
    <c:if test="${not empty hSession}">
        <h1><spring:message code="header.data.receive" /></h1>
        <br /><br />
        <spring:message code="dns.service.hostname" /> <a href="${dnsEntry.recordName}" title="${dnsEntry.recordName}">${dnsEntry.recordName}</a><br />
        <spring:message code="dns.lookup.record.type" /> ${dnsEntry.recordType}<br />
        <spring:message code="dns.lookup.record.address" /> ${dnsEntry.primaryAddress}<br />
    </c:if>
</div>

<div id="rightbar">&nbsp;</div>
