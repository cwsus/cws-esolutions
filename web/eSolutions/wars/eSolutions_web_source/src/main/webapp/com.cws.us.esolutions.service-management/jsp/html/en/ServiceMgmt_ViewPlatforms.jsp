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
 * ServiceMgmt_ViewPlatforms.jsp
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
            title="<spring:message code='select.request.add.platform' />"><spring:message code="select.request.add.platform" /></a> 
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

    <table id="viewPlatformList">
        <tr>
            <td><label id="platformName"><spring:message code="svc.mgmt.platform.name" /></label></td>
            <td><label id="platformRegion"><spring:message code="svc.mgmt.platform.region" /></label></td>
        </tr>
        <c:forEach var="platform" items="${platformList}">
            <tr>
                <td>
                    <a href="${pageContext.request.contextPath}/ui/service-management/platform/${platform.platformGuid}"
                        title="${platform.platformName}">${platform.platformName}</a>
                </td>
                <td>${platform.platformRegion}</td>
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
