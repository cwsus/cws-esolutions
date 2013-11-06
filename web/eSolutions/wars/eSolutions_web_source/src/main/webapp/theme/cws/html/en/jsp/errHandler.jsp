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
<%@page contentType="text/html" pageEncoding="UTF-8" isThreadSafe="true" errorPage="true" %>

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
	    <title><tiles:insertAttribute name="pageTitle" /></title>
        <link rel="stylesheet" type="text/css" media="only screen and (max-device-width: 320px)" href="/html/esolutions/css/esolutions-mobile.css" />
        <link rel="stylesheet" type="text/css" media="only screen and (max-device-width: 600px)" href="/html/esolutions/css/esolutions-tablet.css" />
        <link rel="stylesheet" type="text/css" media="only screen and (max-device-width: 801px)" href="/html/esolutions/css/esolutions.css" />
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
	    <meta name="copyright" content="<spring:message code="footer.copyright" />" />
	    <meta name="description" content="eSolutionsService" />
	    <meta name="keywords" content="incident, change management, incident management, infinix, caspersbox, caspersbox web services" />
	    <script type="text/javascript" src="/html/esolutions/js/Scripts.js"></script>
	    <script type="text/javascript" src="/html/esolutions/js/FormHandler.js"></script>
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
		<div id="masthead">
		    <img src="/html/esolutions/img/logo.gif" alt="eSolutions" title="eSolutions" />

		    <c:if test="${not empty sessionScope.userAccount and sessionScope.userAccount.status ne 'EXPIRED'}">
		        <c:if test="${sessionScope.userAccount.role ne 'USERADMIN'}">
		            <div id="globalNav">
		                <a href="${pageContext.request.contextPath}/ui/service-management/default" title="<spring:message code='link.globalNav.service-mgmt' />">
		                   <spring:message code='link.globalNav.service-mgmt' /></a> |
		                <a href="${pageContext.request.contextPath}/ui/system-management/default" title="<spring:message code='link.globalNav.system-mgmt' />">
		                    <spring:message code='link.globalNav.system-mgmt' /></a> |
		                <a href="${pageContext.request.contextPath}/ui/system-check/default" title="<spring:message code='link.globalNav.validate-systems' />">
		                    <spring:message code='link.globalNav.validate-systems' /></a> |
		                <a href="${pageContext.request.contextPath}/ui/application-management/default" title="<spring:message code='link.globalNav.application-mgmt' />">
		                    <spring:message code='link.globalNav.application-mgmt' /></a> |
		                <a href="${pageContext.request.contextPath}/ui/dns-service/default" title="<spring:message code='link.globalNav.dns-services' />">
		                   <spring:message code='link.globalNav.dns-services' /></a>
		            </div>
		        </c:if>

		        <div id="breadCrumb">
		            <spring:message code="welcome.message" arguments="${sessionScope.userAccount.username}, ${sessionScope.userAccount.lastLogin}" />
		            <br />
		            <c:if test="${sessionScope.userAccount.role ne 'USERADMIN'}">
		                | <a href="${pageContext.request.contextPath}/ui/user-account/default" title="<spring:message code='link.breadcrumb.account' />">
		                    <spring:message code='link.breadcrumb.account' /></a> |
		                <a href="${pageContext.request.contextPath}/ui/messaging/default" title="<spring:message code='link.breadcrumb.messaging' />">
		                    <spring:message code='link.breadcrumb.messaging' /></a> |
		                <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
		                    <a href="${pageContext.request.contextPath}/ui/user-management/default" title="<spring:message code='link.breadcrumb.useradmin' />">
		                        <spring:message code='link.breadcrumb.useradmin' /></a> |
		                </c:if>
		            </c:if>
		        </div>
		    </c:if>
		</div>

        <div id="content">
	        <spring:message code="error.processing.request.operation" />
	        <br /><br />
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
        </div>

		<div id="navBar">
		    <div id="sectionLinks">
		        <ul>
		            <c:choose>
		                <c:when test="${not empty sessionScope.userAccount}">
		                    <li>
		                        <a href="${pageContext.request.contextPath}/ui/home/default" title="<spring:message code='link.sectionLinks.home' />">
		                            <spring:message code='link.sectionLinks.home' /></a>
		                    </li>
		                    <li>
		                        <a href="${pageContext.request.contextPath}/ui/login/logout"
		                            title="<spring:message code='link.sectionLinks.logoff' />"><spring:message code='link.sectionLinks.logoff' /></a>
		                    </li>
		                </c:when>
		                <c:otherwise>
		                    <li>
		                        <a href="${pageContext.request.contextPath}/ui/login/default"
		                            title="<spring:message code='link.sectionLinks.login' />"><spring:message code='link.sectionLinks.login' /></a>
		                    </li>
		                </c:otherwise>
		            </c:choose>
		            <li>
		                <a href="${pageContext.request.contextPath}/ui/app/help"
		                    title="<spring:message code='link.sectionLinks.help' />"><spring:message code='link.sectionLinks.help' /></a>
		            </li>
		        </ul>
		    </div>
		</div>

        <div id="sectionLinks">
		    <ul>
		        <c:choose>
                    <c:when test="${not empty sessionScope.userAccount}">
                        <li>
                            <a href="${pageContext.request.contextPath}/ui/home/default" title="<spring:message code='link.sectionLinks.home' />">
                                <spring:message code='link.sectionLinks.home' /></a>
		                </li>
		                <li>
		                    <a href="${pageContext.request.contextPath}/ui/login/logout" title="<spring:message code='link.sectionLinks.logoff' />">
		                        <spring:message code='link.sectionLinks.logoff' /></a>
		                </li>
		            </c:when>
		            <c:otherwise>
		                <li>
		                    <a href="${pageContext.request.contextPath}/ui/login/default"
		                        title="<spring:message code='link.sectionLinks.login' />"><spring:message code='link.sectionLinks.login' /></a>
		                </li>
		            </c:otherwise>
		        </c:choose>
		        <li>
		            <a href="${pageContext.request.contextPath}/ui/app/help"
		                title="<spring:message code='link.sectionLinks.help' />"><spring:message code='link.sectionLinks.help' /></a>
		        </li>
		    </ul>
		</div>

        <div id="siteInfo">
            <spring:message code="footer.copyright" />
        </div>
    </body>
</html>
