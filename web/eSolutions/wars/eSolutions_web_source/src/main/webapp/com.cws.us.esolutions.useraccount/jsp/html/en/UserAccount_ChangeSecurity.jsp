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
 * com.cws.us.esolutions.useraccount/jsp/html/en
 * UserAccount_ChangeSecurity.jsp
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

    <spring:message code="user.account.update.security" />

    <p id="validationError" />

    <form:form name="submitSecurityInformationChange" id="submitSecurityInformationChange" action="${pageContext.request.contextPath}/ui/user-account/security" method="post" autocomplete="off">
        <table id="userauth">
            <tr>
                <td><label id="txtQuestionOne"><spring:message code="user.account.update.security.question" /></label></td>
                <td>
                    <form:select path="secQuestionOne">
                        <option><spring:message code="select.default" /></option>
                        <option><spring:message code="select.spacer" /></option>
                        <form:options items="${questionList}" />
                    </form:select>
                </td>
                <td><form:errors path="secQuestionOne" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtAnswerOne"><spring:message code="user.account.update.security.answer" /></label></td>
                <td><form:password path="secAnswerOne" /></td>
                <td><form:errors path="secAnswerOne" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td id="txtQuestionTwo"><spring:message code="user.account.update.security.question" /></td>
                <td>
                    <form:select path="secQuestionTwo">
                        <option><spring:message code="select.default" /></option>
                        <option><spring:message code="select.spacer" /></option>
                        <form:options items="${questionList}" />
                    </form:select>
                </td>
                <td><form:errors path="secQuestionTwo" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtAnswerTwo"><spring:message code="user.account.update.security.answer" /></label></td>
                <td><form:password path="secAnswerTwo" /></td>
                <td><form:errors path="secAnswerTwo" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtPassword"><spring:message code="user.account.provide.password" /></label></td>
                <td><form:password path="currentPassword" /></td>
                <td><form:errors path="currentPassword" cssClass="validationError" /></td>
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
                <td>
                    <input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
            </tr>
        </table>
    </form:form>
</div>
<br /><br />
