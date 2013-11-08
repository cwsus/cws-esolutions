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
        <div id="Container">
            <div id="Top">
                <h1><img src="/esolutions/html/img/logo.gif" alt="Cool Web site Designs" class="logo" /><spring:message code="theme.company.name" /></h1>
                <h2><spring:message code="theme.app.welcome" /></h2>
            </div>

			<div id="nav">
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
			            <a href="${pageContext.request.contextPath}/ui/knowledgebase/default"
			                title="<spring:message code='link.sectionLinks.help' />"><spring:message code='link.sectionLinks.help' /></a>
			        </li>
			    </ul>
			</div>

            <div id="TopImage"><img src="/esolutions/html/img/top.jpg" alt="" width="800" height="174" /></div>
            <div id="InfoLine"><spring:message code="system.error" /></div>

			<div id="content">
			    <div id="content-right">
                    <spring:message code="system.failure.message" />
			    </div>
			</div>

			<div id="Footer">
			    <p>
			        <c:if test="${not empty sessionScope.userAccount}">
			            <spring:message code="welcome.message" arguments="${sessionScope.userAccount.username}, ${sessionScope.userAccount.lastLogin}" />
			        </c:if>
			        <spring:message code="footer.copyright" arguments="http://www.caspersbox.com/cws/ui/home" />
			        <spring:message code="footer.more.info" arguments="http://www.caspersbox.com/cws/ui/contact" />
			        <spring:message code="footer.validation" arguments="http://validator.w3.org/check?uri=referer" />
			        <spring:message code="footer.license" />
			    </p>
			</div>
        </div>
    </body>
