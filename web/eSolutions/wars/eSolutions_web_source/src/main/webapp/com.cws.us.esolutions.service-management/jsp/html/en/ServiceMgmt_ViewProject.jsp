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
 * ServiceMgmt_ViewProject.jsp
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

<div id="InfoLine"><spring:message code="svc.mgmt.view.project" arguments="${project.projectCode}" /></div>
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

	    <table id="projectDetail">
	        <tr>
	            <td><label id="txtProjectCode"><spring:message code="svc.mgmt.service.name" /></label>
	            <td>${project.projectCode}</td>
	        </tr>
	        <tr>
	            <td><label id="txtProjectStatus"><spring:message code="svc.mgmt.service.status" /></label>  
	            <td>${project.projectStatus}</td>
	        </tr>
	        <tr>
	            <td><label id="txtPrimaryContact"><spring:message code="svc.mgmt.project.pcontact" /></label>
	            <td>${project.primaryContact}</td>
	        </tr>
	        <tr>
	            <td><label id="txtSecondaryContact"><spring:message code="svc.mgmt.project.scontact" /></label>
	            <td>${project.secondaryContact}</td>
	        </tr>
            <tr>
                <td><label id="txtContactEmail"><spring:message code="svc.mgmt.project.dev.group" /></label></td>
                <td>${project.devEmail}</td>
                <td><label id="txtContactEmail"><spring:message code="svc.mgmt.project.prod.group" /></label></td>
                <td>${project.prodEmail}</td>
            </tr>
	        <tr>
	            <td><label id="txtChangeQueue"><spring:message code="svc.mgmt.project.changeq" /></label>
	            <td>${project.changeQueue}</td>
	        </tr>
	        <tr>
	            <td><label id="txtIncidentQueue"><spring:message code="svc.mgmt.project.ticketq" /></label>
	            <td>${project.incidentQueue}</td>
	        </tr>
	        <tr>
	            <td><label id="txtApplications"><spring:message code="svc.mgmt.project.applications" /></label>
	        </tr>
	        <c:forEach var="application" items="${project.applicationList}">
	            <tr>
	                <td>
	                    <a href="${pageContext.request.contextPath}/ui/application-management/application/${application.applicationGuid}"
	                        title="${application.applicationName}">${application.applicationName}</a>
	                </td>
	            </tr>
	        </c:forEach>
	    </table>
    </div>

    <div id="content-left">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/ui/service-management/modify-service/project/${project.projectGuid}"
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
