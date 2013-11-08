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
 * com.cws.us.esolutions.application-management/jsp/html/en
 * AppMgmt_ViewApplications.jsp
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

<div id="InfoLine"><spring:message code="app.mgmt.application.list" /></div>
<div id="content">
    <div id="content-right">
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

	    <table id="appSearchResults">
	        <tr>
	            <td><spring:message code="app.mgmt.application.name" /></td>
	            <td><spring:message code="app.mgmt.application.version" /></td>
	            <td><spring:message code="app.mgmt.application.project" /></td>
	        </tr>
	        <c:forEach var="application" items="${applicationList}">
	            <tr>
	                <td>
	                    <a href="${pageContext.request.contextPath}/ui/application-management/application/${application.applicationGuid}"
	                        title="${application.applicationName}">${application.applicationName}</a>
	                </td>
	                <td>${application.applicationVersion}</td>
	                <td>
	                    <a href="${pageContext.request.contextPath}/ui/service-management/project/${application.applicationProject.projectGuid}"
	                        title="${application.applicationProject.projectCode}">${application.applicationProject.projectCode}</a>
	                </td>
	            </tr>
	        </c:forEach>
	    </table>
    </div>

    <div id="content-left">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/ui/application-management/add-application"
                    title="<spring:message code='app.mgmt.add.application' />"><spring:message code='app.mgmt.add.application' /></a>
            </li>
        </ul>
    </div>
</div>
