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
 * com.cws.us.esolutions.system-management/jsp/html/en
 * SystemManagement_DefaultHandler.jsp
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
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="select.request.type" />
    <br /><br />
    <table id="selectRequest">
        <tr>
            <td>
                <a href="${pageContext.request.contextPath}/ui/system-management/add-server"
                    title="<spring:message code='select.request.add.server' />"><spring:message code="select.request.add.server" /></a>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}/ui/system-management/install-software"
                    title="<spring:message code='select.request.install.server' />"><spring:message code="select.request.install.server" /></a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="${pageContext.request.contextPath}/ui/system-management/system-consoles"
                    title="<spring:message code='select.request.type.console' />"><spring:message code='select.request.type.console' /></a>
            </td>
            <td>
               <a href="${pageContext.request.contextPath}/ui/system-management/server-control"
                   title="<spring:message code='select.request.server.control' />"><spring:message code='select.request.server.control' /></a>
        </tr>
    </table>
</div>
