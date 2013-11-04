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

<div class="feature">
    <div id="breadcrumb" class="lpstartover">
        <a href="${pageContext.request.contextPath}/ui/service-management/add-datacenter"
            title="<spring:message code='select.request.add.datacenter' />"><spring:message code="select.request.add.datacenter" /></a> / 
        <a href="${pageContext.request.contextPath}/ui/service-management/list-datacenters"
            title="<spring:message code='select.request.list.datacenters' />"><spring:message code="select.request.list.datacenters" /></a> / 
        <a href="${pageContext.request.contextPath}/ui/service-management/add-project"
            title="<spring:message code='select.request.add.project' />"><spring:message code="select.request.add.project" /></a> / 
        <a href="${pageContext.request.contextPath}/ui/service-management/add-platform"
            title="<spring:message code='select.request.add.platform' />"><spring:message code="select.request.add.platform" /></a> / 
        <a href="${pageContext.request.contextPath}/ui/service-management/list-platforms"
            title="<spring:message code='select.request.list.platforms' />"><spring:message code="select.request.list.platforms" /></a>
    </div>

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

    <table id="viewProjectList">
        <c:forEach var="project" items="${projectList}">
            <tr>
                <td><label id="projectCode"><spring:message code="svc.mgmt.project.code" /></label>${project.projectCode}</td>
                <td><label id="projectContact"><spring:message code="svc.mgmt.project.email" /></label>${project.contactEmail}</td>
                <td><label id="projectPrimary"><spring:message code="svc.mgmt.project.pcontact" /></label>${project.primaryContact}</td>
                <td><label id="projectSecondary"><spring:message code="svc.mgmt.project.scontact" /></label>${project.secondaryContact}</td>
                <td><label id="projectChangeQ"><spring:message code="svc.mgmt.project.changeq" /></label>${project.changeQueue}</td>
                <td><label id="projectIncidentQ"><spring:message code="svc.mgmt.project.ticketq" /></label>${project.incidentQueue}</td>
                <td><label id="projectStatus"><spring:message code="svc.mgmt.project.status" /></label>${project.serviceStatus}</td>
            </tr>
            <tr>
                <td><label id="projectApps"><spring:message code="svc.mgmt.project.applications" /></label></td>
            </tr>
            <tr>
                <c:forEach var="application" items="${project.applicationList}">
                    <td>
                        <a href="${pageContext.request.contextPath}/ui/application-management/application/${application.applicationGuid}"
                            title="${application.applicationName}">${application.applicationName}</a>
                    </td>
                </c:forEach>
            </tr>
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
                                <a href="${pageContext.request.contextPath}/ui/service-management/list-platforms/page/${i}"
                                    title="<spring:message code='system.next.page' />">${i}</a>
                            </td>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </tr>
        </table>
    </c:if>
</div>
<br /><br />
