<%@ page import="java.util.*" %>
<%-- TODO: do i really want to keep this?? --%>
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
 * Package: com.cws.esolutions.web\jsp\html\en
 * File: System_DisplayHeaders.jsp
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
