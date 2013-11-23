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
 * ServiceMgmt_ViewProjects.jsp
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

<c:set var="count" value="0" scope="page" />

<div id="InfoLine"><spring:message code="svc.mgmt.list.projects" /></div>
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

	    <table id="viewProjectList">
	        <tr>
	            <td><label id="projectCode"><spring:message code="svc.mgmt.service.name" /></label></td>
	            <td><label id="projectStatus"><spring:message code="svc.mgmt.service.status" /></label></td>
	        </tr>
	        <c:forEach var="project" items="${projectList}">
	            <td>
	                <a href="${pageContext.request.contextPath}/ui/service-management/project/${project.projectGuid}"
	                    title="${project.projectCode}">${project.projectCode}</a>
	            </td>
	            <td>${project.projectStatus}</td>
	        </c:forEach>
	    </table>

	    <c:if test="${pages gt 1}">
	        <br />
	        <hr />
	        <br />
	        <table>
	            <tr>
	                <c:forEach begin="1" end="${pages}" var="i">
	                    <c:choose>
	                        <c:when test="${page eq i}">
	                            <td>${i}</td>
	                        </c:when>
	                        <c:otherwise>
	                            <td>
	                                <a href="${pageContext.request.contextPath}/ui/service-management/list-projects/page/${i}"
	                                    title="{i}">${i}</a>
	                            </td>
	                        </c:otherwise>
	                    </c:choose>
	                </c:forEach>
	            </tr>
	        </table>
	    </c:if>
    </div>

    <div id="content-left">
        <ul>
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
