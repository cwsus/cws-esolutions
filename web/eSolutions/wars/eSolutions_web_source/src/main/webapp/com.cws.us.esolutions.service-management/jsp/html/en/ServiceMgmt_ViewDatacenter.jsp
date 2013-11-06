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
 * ServiceMgmt_AddDatacenter.jsp
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

<div class="feature">
    <div id="breadcrumb" class="lpstartover">
        <a href="${pageContext.request.contextPath}/ui/service-management/add-datacenter"
            title="<spring:message code='select.request.add.datacenter' />"><spring:message code="select.request.add.datacenter" /></a> / 
        <a href="${pageContext.request.contextPath}/ui/service-management/list-datacenters"
            title="<spring:message code='select.request.list.datacenters' />"><spring:message code="select.request.list.datacenters" /></a> / 
        <a href="${pageContext.request.contextPath}/ui/service-management/add-project"
            title="<spring:message code='select.request.add.project' />"><spring:message code="select.request.add.project" /></a> / 
        <a href="${pageContext.request.contextPath}/ui/service-management/list-projects"
            title="<spring:message code='select.request.list.projects' />"><spring:message code="select.request.list.projects" /></a> / 
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

    <spring:message code="svc.mgmt.add.datacenter" />

    <p id="validationError" />

    <table id="viewDatacenter">
        <tr>
            <td><label id="txtDatacenterName"><spring:message code="svc.mgmt.datacenter.name" /></label></td>
            <td>${datacenter.datacenterName}</td>
        </tr>
        <tr>
            <td><label id="txtDatacenterStatus"><spring:message code="svc.mgmt.datacenter.status" /></label></td>
            <td>${datacenter.datacenterStatus}</td>
        </tr>
        <tr>
            <td><label id="txtDatacenterDescription"><spring:message code="svc.mgmt.datacenter.description" /></label></td>
	        <td>${datacenter.datacenterDesc}</td>
        </tr>
    </table>
</div>
<br /><br />
