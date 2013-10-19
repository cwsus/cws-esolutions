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
 * com.cws.us.esolutions.onlinereset/jsp/html/en
 * OnlineReset_ForgotUsername.jsp
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

    <spring:message code="olr.provide.email.address" />

    <p id="validationError" />

    <form:form id="submitEmailForUserSearch" name="submitEmailForUserSearch" action="${pageContext.request.contextPath}/ui/online-reset/forgot-username" method="post" autocomplete="off">
        <table id="userauth">
            <tr>
                <td><label id="txtEmailAddr"><spring:message code="olr.user.email.address" /></label></td>
                <td>
                    <form:input path="emailAddr" />
                    <form:errors path="emailAddr" cssClass="validationError" />
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}/ui/app/help/forgot-email"
                        title="<spring:message code="olr.user.forgot.email" />"><spring:message code="olr.user.forgot.email" /></a>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>
                    <input type="button" name="execute" value="<spring:message code="button.execute.text" />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
                <td>
                    <input type="button" name="cancel" value="<spring:message code="button.cancel.text" />" id="cancel" class="submit" onClick="disableButton(this); window.location.href = '${pageContext.request.contextPath}/ui/login/default';" />
                </td>
            </tr>
        </table>
    </form:form>
</div>
<br /><br />
