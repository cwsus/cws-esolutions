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
    <table id="projectDetail">
        <c:forEach var="project" items="projectList">
            <c:set var="counter" value="${count + 1}" />

            <c:if test="${counter eq 4}">
                <tr>
            </c:if>

            <td><a href="${pageContext.request.contextPath}/ui/service-management/project/${project.projectGuid}" title="${project.projectCode}">${project.projectcode}</a></td>

            <c:if test="${counter eq 4}">
                <c:set var="counter" value="0" />
                </tr>
            </c:if>
        </c:forEach>
    </table>
</div>
