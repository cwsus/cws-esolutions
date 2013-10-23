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
 * com.cws.us.esolutions/jsp/html/en
 * System_Unauthorized.jsp
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
    <div class="error">
        <spring:message code="request.unauthorized" />
        <br /><br />
        <c:choose>
            <c:when test="${requestScope.isUserLoggedIn != 'true'}">
                <p><a href="${pageContext.request.contextPath}/ui/home/default" title="<spring:message code='link.sectionLinks.home' />"><spring:message code="text.click.continue" arguments="${pageContext.request.contextPath}/ui/home/default" /></a></p>
            </c:when>
            <c:otherwise>
                <p><a href="${pageContext.request.contextPath}/ui/login/default" title="<spring:message code='link.sectionLinks.login' />"><spring:message code="text.click.continue" arguments="${pageContext.request.contextPath}/ui/auth/default" /></a></p>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<br /><br />
