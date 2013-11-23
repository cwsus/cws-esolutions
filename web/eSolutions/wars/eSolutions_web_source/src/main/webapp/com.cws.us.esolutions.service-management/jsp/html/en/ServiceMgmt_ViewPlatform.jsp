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
 * com.cws.us.esolutions.service-management/jsp/html/en
 * ServiceMgmt_ViewPlatform.jsp
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

<div id="InfoLine"><spring:message code="svc.mgmt.view.platform" arguments="${platform.platformName}" /></div>
<div id="content">
    <div id="content-right">
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

	    <table id="platformDetail">
	        <tr>
	            <td><label id="txtPlatformName"><spring:message code="svc.mgmt.service.name" /></label></td>
	            <td>${platform.platformName}</td>
	            <td><label id="txtPlatformStatus"><spring:message code="svc.mgmt.service.status" /></label></td>
	            <td>${platform.status}</td>
	            <td><label id="txtPlatformRegion"><spring:message code="svc.mgmt.platform.region" /></label></td>
	            <td>${platform.platformRegion}</td>
	        </tr>
	        <tr>
	            <td><label id="txtPlatformDmgr"><spring:message code="svc.mgmt.platform.dmgr" /></label></td>
	            <td>
	                <a href="${pageContext.request.contextPath}/ui/system-management/server/${platform.platformDmgr.serverGuid}"
	                    title="${platform.platformDmgr.operHostName}">${platform.platformDmgr.operHostName}</a>
	            </td>
	        </tr>
	        <tr>
	            <td><label id="txtPlatformDescription"><spring:message code="svc.mgmt.service.description" /></label></td>
	            <td>${platform.description}</td>
	        </tr>
	    </table>
	    <br />
	    <label id="txtPlatformAppservers"><spring:message code="svc.mgmt.platform.appservers" /></label>
	    <table id="appServerList">
	        <tr>
	            <c:forEach var="appServer" items="${platform.appServers}">
	                <c:set var="appCount" value="${appCount + 1}" scope="page" />

	                <c:if test="${appCount eq 4}">
	                    <tr>
	                </c:if>
	                <td>
	                    <a href="${pageContext.request.contextPath}/ui/system-management/server/${appServer.serverGuid}"
	                        title="${appServer.operHostName}">${appServer.operHostName}</a>
	                </td>
	                <c:if test="${appCount eq 4}">
	                    <c:set var="appCount" value="0" scope="page" /> <%-- reset the counter --%>
	                    </tr>
	                </c:if>
	            </c:forEach>
	        </tr>
	    </table>
	    <br />
	    <label id="txtPlatformWebservers"><spring:message code="svc.mgmt.platform.webservers" /></label>
	    <table id="webServerList">
	        <tr>
	            <c:forEach var="webServer" items="${platform.webServers}">
	                <c:set var="webCount" value="${webCount + 1}" scope="page" />

	                <c:if test="${webCount eq 4}">
	                    <tr>
	                </c:if>
	                <td>
	                    <a href="${pageContext.request.contextPath}/ui/system-management/server/${webServer.serverGuid}"
	                        title="${webServer.operHostName}">${webServer.operHostName}</a>
	                </td>
	                <c:if test="${webCount eq 4}">
	                    <c:set var="webCount" value="0" scope="page" /> <%-- reset the counter --%>
	                    </tr>
	                </c:if>
	            </c:forEach>
	        </tr>
	    </table>
    </div>

    <div id="content-left">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/ui/service-management/modify-service/platform/${platform.platformGuid}"
                    title="<spring:message code='svc.mgmt.update.service' />"><spring:message code="svc.mgmt.update.service" /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/service-management/list-datacenters"
                    title="<spring:message code='svc.mgmt.list.datacenters' />"><spring:message code="svc.mgmt.list.datacenters" /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/service-management/add-datacenter"
                    title="<spring:message code='svc.mgmt.add.datacenter' />"><spring:message code="svc.mgmt.add.datacenter" /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/service-management/list-projects"
                    title="<spring:message code='svc.mgmt.list.projects' />"><spring:message code="svc.mgmt.list.projects" /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/service-management/add-project"
                    title="<spring:message code='svc.mgmt.add.project' />"><spring:message code="svc.mgmt.add.project" /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/service-management/list-platforms"
                    title="<spring:message code='svc.mgmt.list.platforms' />"><spring:message code="svc.mgmt.list.platforms" /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/service-management/add-platform"
                    title="<spring:message code='svc.mgmt.add.platform' />"><spring:message code="svc.mgmt.add.platform" /></a>
            </li>
        </ul>
    </div>
</div>
