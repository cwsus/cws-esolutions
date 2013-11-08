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
        <c:if test="${not empty sessionScope.userAccount}">
            <spring:message code="welcome.message" arguments="${sessionScope.userAccount.username}, ${sessionScope.userAccount.lastLogin}" />
        </c:if>
        <spring:message code="footer.copyright" arguments="http://www.caspersbox.com/cws/ui/home" />
        <spring:message code="footer.more.info" arguments="http://www.caspersbox.com/cws/ui/contact" />
        <spring:message code="footer.validation" arguments="http://validator.w3.org/check?uri=referer" />
        <spring:message code="footer.license" />
    </p>
</div>
