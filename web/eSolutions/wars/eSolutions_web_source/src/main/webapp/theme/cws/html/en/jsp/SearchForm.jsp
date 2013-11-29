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
 * SearchForm.jsp
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

<div id="header">
    <c:if test="${not empty fn:trim(sessionScope.userAccount) and sessionScope.userAccount.status == 'SUCCESS'}">
        <form method="post" class="search" action="http://www.free-css.com/">
            <p>
                <input name="search_query" class="textbox" type="text" />
                <input name="search" class="button" value="Search" type="submit" />
            </p>
        </form>
    </c:if>
    <h1 id="logo"><img src="/html/eSolutions/img/logo.gif" alt="CaspersBox Web Services" /></h1>
</div>
