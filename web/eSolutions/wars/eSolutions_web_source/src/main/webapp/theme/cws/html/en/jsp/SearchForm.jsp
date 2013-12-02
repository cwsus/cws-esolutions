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
    <%--
    <c:if test="${not empty fn:trim(sessionScope.userAccount) and sessionScope.userAccount.status == 'SUCCESS'}">
        <form:form id="searchRequest" name="searchRequest" class="search" action="${pageContext.request.contextPath}/eSolutions/ui/common/search" method="post">
            <p>
                <form:input path="searchTerms" class="textbox" type="text" />
                <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            </p>
        </form:form>
    </c:if>
    --%>
    <h1 id="logo"><img src="/html/eSolutions/img/logo.gif" alt="CaspersBox Web Services" /></h1>
</div>
