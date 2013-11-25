<%--
/**
 * Copyright 2008 - 2009 CaspersBox Web Services
 * All rights reserved.
 */
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--
/**
 * eSolutions_web_source
 * theme/cws/html/en/jsp
 * errHandler.jsp
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
<%@page contentType="text/html" pageEncoding="UTF-8" isThreadSafe="true" isErrorPage="true" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<html xml:lang="en" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.w3.org/1999/xhtml http://www.w3.org/MarkUp/SCHEMA/xhtml2.xsd">

	<head>
	    <title>eSolutions - System Error</title>
        <link rel="stylesheet" type="text/css" media="all" href="/html/esolutions/css/esolutions.css" />
	    <link rel="image/x-icon" href="/favicon.ico" />
	    <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon" />
	    <meta http-equiv="Content-Script-Type" content="text/javascript" />
	    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	    <meta http-equiv="Content-Language" content="en-US" />
	    <meta http-equiv="pragma" content="no-cache" />
	    <meta http-equiv="expires" content="-1" />
	    <meta http-equiv="cache-control" content="no-store, no-cache, must-revalidate" />
	    <meta http-equiv="max-age" content="0" />
	    <meta http-equiv="refresh" content="${pageContext.session.maxInactiveInterval}; url=${pageContext.request.contextPath}/ui/login/logout" />
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
	            var enable = false;
	
	            if (enable)
	            {
	                var documentURI = window.location.pathname + window.location.search;
	
	                if (documentURI != '${pageContext.request.contextPath}/ui/login/warn')
	                {
	                    var popupURI = "${pageContext.request.contextPath}/ui/login/warn";
	
	                    setInterval("popup(popupURI, 'Timeout', '280', '170', '0', '0', '0', '0')", ${pageContext.session.maxInactiveInterval} - 90);
	                }
	            }
	        //-->
	    </script>
	</head>

    <body>
        <div id="Container">
            <div id="nav">
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

            <div id="InfoLine"><spring:message code="theme.error.system.fai2lure" /></div>

			<div id="content">
			    <div id="content-right">
                    <spring:message code="theme.system.service.failure" />
			    </div>
			</div>

            <div id="Footer">
                <p>
                    <c:choose>
                        <c:when test="${not empty sessionScope.userAccount}">
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
                    <a href="${pageContext.request.contextPath}/ui/common/default" title="<spring:message code='theme.navbar.home' />">
                        <spring:message code='theme.navbar.home' /></a> |
                    <a href="${pageContext.request.contextPath}/ui/knowledgebase/default" title="<spring:message code='theme.navbar.help' />">
                        <spring:message code='theme.navbar.help' /></a> |
                    <a href="${pageContext.request.contextPath}/ui/common/submit-contact"
                        title="<spring:message code="theme.submit.support.request" />"><spring:message code="theme.submit.support.request" /></a><br />
                    &copy; <spring:message code="theme.footer.copyright" /><br />
                    <strong><spring:message code="theme.footer.more.info" /></strong><a href="http://www.caspersbox.com/cws/ui/contact/default"
                        title="<spring:message code="theme.contact.us" />"><spring:message code="theme.contact.us" /></a><br />
                </p>
            </div>
        </div>
    </body>
</html>

