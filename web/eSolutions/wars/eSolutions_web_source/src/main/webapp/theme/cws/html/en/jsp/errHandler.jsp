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
 * File: errHandler.jsp
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

<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isThreadSafe="true" isErrorPage="true" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>eSolutions - System Error</title>
        <link rel="stylesheet" type="text/css" media="all" href="/html/eSolutions/css/esolutions.css" />
        <link rel="image/x-icon" href="/html/eSolutions/img/favicon.ico" />
        <link rel="shortcut icon" href="/html/eSolutions/img/favicon.ico" type="image/x-icon" />
        <meta http-equiv="Content-Script-Type" content="text/javascript" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta http-equiv="Content-Language" content="en-US" />
        <meta http-equiv="pragma" content="no-cache" />
        <meta http-equiv="expires" content="-1" />
        <meta http-equiv="cache-control" content="no-store, no-cache, must-revalidate" />
        <meta http-equiv="max-age" content="0" />
        <meta http-equiv="refresh" content="900; ${pageContext.request.contextPath}/ui/login/logout" />
        <meta name="robots" content="index,follow,noarchive" />
        <meta name="GoogleBot" content="noarchive" />
        <meta name="Author" content="eSolutions" />
        <meta name="copyright" content="<spring:message code="theme.footer.copyright" />" />
        <meta name="description" content="eSolutionsService" />
        <meta name="keywords" content="incident, change management, incident management, infinix, caspersbox, caspersbox web services" />
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
                var ignoreURIs = new Array("/ui/login", "/ui/online-reset", "/ui/common/submit-contact");

                for (var x = 0; x < ignoreURIs.length; x++)
                {
                    if (documentURI == ignoreURIs[x])
                    {
                        break;
                    }
                    else
                    {
                        setInterval(function() { window.location.href = '${pageContext.request.contextPath}/ui/login/logout'; }, timeout);
                    }
                }
            //-->
        </script>
    </head>

    <body>
        <div id="wrap">
            <div id="header">
                <h1 id="logo"><img src="/html/eSolutions/img/logo.gif" alt="CaspersBox Web Services" /></h1>
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
  
            <div id="sidebar" >&nbsp;</div>

            <div id="main">
                <h1><spring:message code="theme.error.system.failure" /></h1>
                <p>
                    <spring:message code="theme.system.service.failure" />
                </p>
            </div>

            <div id="rightbar">&nbsp;</div>
        </div>

        <div id="footer">
            <div id="footer-content">
                <div id="footer-right">
                    &copy; <a href="http://www.caspersbox.com/"><spring:message code="theme.footer.copyright" /></a><br />
                    <strong><spring:message code="theme.footer.more.info" /></strong><a href="http://www.caspersbox.com/cws/ui/contact/default"
                        title="<spring:message code="theme.contact.us" />" target="_blank"><spring:message code="theme.contact.us" /></a><br />
                </div>
                <div id="footer-left">
                    <c:choose>
                        <c:when test="${not empty fn:trim(sessionScope.userAccount)}">
                            <spring:message code="theme.welcome.message" arguments="${sessionScope.userAccount.username}, ${sessionScope.userAccount.lastLogin}" /><br />
                            <a href="${pageContext.request.contextPath}/ui/login/logout" title="<spring:message code='theme.navbar.logoff' />">
                                <spring:message code='theme.navbar.logoff' /></a> |
                            <a href="${pageContext.request.contextPath}/ui/user-account/default" title="<spring:message code='theme.navbar.myaccount' />">
                                <spring:message code="theme.navbar.myaccount" /></a> |
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/ui/login/default" title="<spring:message code='theme.navbar.login' />">
                                <spring:message code='theme.navbar.login' /></a> | 
                        </c:otherwise>
                    </c:choose>
                    <script>
                        if ((window.location.pathname.search('login') == -1) && (window.location.pathname.search('common/default') == -1))
                        {
                            document.write('<a href="${pageContext.request.contextPath}/ui/common/default"><spring:message code="theme.navbar.home" /></a> |');
                        }
                    </script>
                </div>
            </div>
        </div>
    </body>
</html>
