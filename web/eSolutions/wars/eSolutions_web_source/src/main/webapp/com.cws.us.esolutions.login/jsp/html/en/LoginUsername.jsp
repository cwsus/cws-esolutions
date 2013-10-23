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
 * com.cws.us.esolutions.login/jsp/html/en
 * LoginUsername.jsp
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
        <p id="info">${messageResponse}</p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <p id="error"><spring:message code="${errorMessage}" /></p>
    </c:if>

    <spring:message code="login.user.uid.message" />

    <p id="validationError" />

    <form:form id="submitUserLogin" name="submitUserLogin" action="${pageContext.request.contextPath}/ui/login/username" method="post">
        <table id="userauth">
            <tr>
                <td><label id="txtUsername"><spring:message code="login.user.name" /></label></td>
                <td>
                    <form:input path="loginUser" />
                    <form:errors path="username" cssClass="validationError" />
                </td>
                <td>
                    <c:if test="${not empty forgotUsernameUrl}">
                        <a href="<c:out value="${pageContext.request.contextPath}/${forgotUsernameUrl}" />" title="<spring:message code='login.user.forgot_uid' />">
                            <spring:message code="login.user.forgot_uid" />
                        </a>
                    </c:if>
                </td>
            </tr>
        </table>
        <br /><br />
        <table id="inputItems">
            <tr>
                <td>
                    <input type="button" name="execute" value="<spring:message code='button.execute.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
                <td>
                    <input type="button" name="reset" value="<spring:message code='button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                </td>
            </tr>
        </table>
    </form:form>
</div>
<br /><br />
