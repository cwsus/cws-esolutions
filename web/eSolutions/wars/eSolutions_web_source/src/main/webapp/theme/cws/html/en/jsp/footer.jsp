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
 * footer.jsp
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

<div id="Footer">
    <p>
        <c:choose>
            <c:when test="${not empty sessionScope.userAccount}">
                <spring:message code="theme.welcome.message" arguments="${sessionScope.userAccount.username}, ${sessionScope.userAccount.lastLogin}" /><br />
                <a href="${pageContext.request.contextPath}/ui/login/logout" title="<spring:message code='theme.navbar.logoff' />">
                    <spring:message code='theme.navbar.logoff' /></a> |
                <a href="${pageContext.request.contextPath}/ui/user-account/default" title="<spring:message code='theme.navbar.myaccount' />">
                    <spring:message code="theme.navbar.myaccount" /></a> |
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/ui/login/default" title="<spring:message code='theme.navbar.login' />">
                    <spring:message code='theme.navbar.login' /></a> | 
            </c:otherwise>
        </c:choose>
        <script>
            if ((window.location.pathname.search('login') == -1) && (window.location.pathname.search('common/default') == -1))
            {
                document.write('<a href="${pageContext.request.contextPath}/ui/common/default"><spring:message code="theme.navbar.home" /></a> |');
            }
        </script>
        <a href="${pageContext.request.contextPath}/ui/knowledgebase/default" title="<spring:message code='theme.navbar.help' />">
            <spring:message code='theme.navbar.help' /></a><br />
        &copy; <a href="http://www.caspersbox.com/"><spring:message code="theme.footer.copyright" /></a><br />
        <strong><spring:message code="theme.footer.more.info" /></strong><a href="http://www.caspersbox.com/cws/ui/contact/default"
            title="<spring:message code="theme.contact.us" />"><spring:message code="theme.contact.us" /></a><br />
    </p>
</div>
