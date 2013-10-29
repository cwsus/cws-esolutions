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
 * theme/cws/html/en/jsp
 * navbar.jsp
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

<div id="navBar">
    <div id="sectionLinks">
        <ul>
            <c:choose>
                <c:when test="${not empty sessionScope.userAccount}">
                    <li>
                        <a href="${pageContext.request.contextPath}/ui/home/default" title="<spring:message code='link.sectionLinks.home' />">
                            <spring:message code='link.sectionLinks.home' /></a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/ui/login/logout"
                            title="<spring:message code='link.sectionLinks.logoff' />"><spring:message code='link.sectionLinks.logoff' /></a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li>
                        <a href="${pageContext.request.contextPath}/ui/login/default"
                            title="<spring:message code='link.sectionLinks.login' />"><spring:message code='link.sectionLinks.login' /></a>
                    </li>
                </c:otherwise>
            </c:choose>
            <li>
                <a href="${pageContext.request.contextPath}/ui/knowledgebase/default"
                    title="<spring:message code='link.sectionLinks.help' />"><spring:message code='link.sectionLinks.help' /></a>
            </li>
        </ul>
    </div>
</div>
