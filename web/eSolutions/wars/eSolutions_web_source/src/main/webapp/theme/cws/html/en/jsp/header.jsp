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
 * Package: theme\cws\html\en\jsp
 * File: Default.jsp
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

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isThreadSafe="true" errorPage="/theme/cws/html/en/jsp/errHandler.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<html xml:lang="en" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.w3.org/1999/xhtml http://www.w3.org/MarkUp/SCHEMA/xhtml2.xsd">

    <head>
        <title>eSolutions</title>
        <link rel="stylesheet" type="text/css" media="all" href="/static/css/esolutions.css" />
        <link rel="image/x-icon" href="/static/img/favicon.ico" />
        <link rel="shortcut icon" href="/static/img/favicon.ico" type="image/x-icon" />
        <meta http-equiv="Content-Script-Type" content="text/javascript" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta http-equiv="Content-Language" content="en-US" />
        <meta http-equiv="pragma" content="no-cache" />
        <meta http-equiv="expires" content="-1" />
        <meta http-equiv="cache-control" content="no-store, no-cache, must-revalidate" />
        <meta http-equiv="max-age" content="0" />
        <meta http-equiv="refresh" content="900; ${pageContext.request.contextPath}/ui/logout" />
        <meta name="robots" content="index,follow,noarchive" />
        <meta name="GoogleBot" content="noarchive" />
        <meta name="Author" content="eSolutions" />
        <meta name="copyright" content="<spring:message code="theme.footer.copyright" />" />
        <meta name="description" content="eSolutionsService" />
        <meta name="keywords" content="incident, change management, incident management, infinix, caspersbox, caspersbox web services" />
        <script type="text/javascript" src="/static/js/Scripts.js"></script>
        <script type="text/javascript" src="/static/js/FormHandler.js"></script>
        <script type="text/javascript">
            <!--
                if (top != self)
                {
                    top.location = self.location;
                }
            //-->
        </script>
        <script type="text/javascript">
            <!--
                var timeout = ${pageContext.session.maxInactiveInterval} * 1000;
                var documentURI = window.location.pathname + window.location.search;
                var ignoreURIs = new Array("/ui/login", "/ui/logout", "/ui/online-reset", "/ui/common/submit-contact");
    
                for (var x = 0; x < ignoreURIs.length; x++)
                {
                    if (documentURI == ignoreURIs[x])
                    {
                        break;
                    }
                    else
                    {
                        setInterval(function() { window.location.href = '${pageContext.request.contextPath}/ui/logout'; }, timeout);
                    }
                }
            //-->
        </script>
    </head>

    <body>
        <div id="wrap">
            <div id="header">
                <h1 id="logo"><img src="/static/img/logo.gif" alt="CaspersBox Web Services" /></h1>
            </div>

            <div id="menu">
                <ul>
                    <c:if test="${not empty fn:trim(sessionScope.userAccount)}">
                        <c:if test="${sessionScope.userAccount.status == 'SUCCESS'}">
                            <c:if test="${sessionScope.userAccount.role ne 'USERADMIN'}">
                                <li>
                                    <a href="${pageContext.request.contextPath}/ui/application-management/default" title="<spring:message code='theme.navbar.application-mgmt' />">
                                        <spring:message code='theme.navbar.application-mgmt' /></a>
                                </li>
                                <li>
                                    <a href="${pageContext.request.contextPath}/ui/dns-service/default" title="<spring:message code='theme.navbar.dns-services' />">
                                        <spring:message code='theme.navbar.dns-services' /></a>
                                </li>
                                <li>
                                    <a href="${pageContext.request.contextPath}/ui/service-management/default" title="<spring:message code='theme.navbar.service-mgmt' />">
                                        <spring:message code='theme.navbar.service-mgmt' /></a>
                                </li>
                                <li>
                                    <a href="${pageContext.request.contextPath}/ui/system-management/default" title="<spring:message code='theme.navbar.system-mgmt' />">
                                        <spring:message code='theme.navbar.system-mgmt' /></a>
                                </li>
                                <li>
                                    <a href="${pageContext.request.contextPath}/ui/service-messaging/default" title="<spring:message code='theme.navbar.messaging' />">
                                        <spring:message code='theme.navbar.messaging' /></a>
                                </li>
                            </c:if>
                            <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                                <li class="last">
                                    <a href="${pageContext.request.contextPath}/ui/user-management/default" title="<spring:message code='theme.navbar.useradmin' />">
                                        <spring:message code='theme.navbar.useradmin' /></a>
                                </li>
                            </c:if>
                        </c:if>
                    </c:if>
                </ul>
            </div>