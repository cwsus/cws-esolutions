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
 * com.cws.us.esolutions.messaging/jsp/html/en
 * Messaging_SubmitSystemMessage.jsp
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

    <spring:message code="message.system.message.create.banner" />

    <p id="validationError" />

    <form:form id="submitSystemMessage" name="submitSystemMessage" action="${pageContext.request.contextPath}/ui/messaging/submit-message" method="post" autocomplete="off">
        <form:hidden path="isNewMessage" value="true" />

        <table id="contactTable">
            <tr>
                <td id="txtSubmittorUserID"><spring:message code="messaging.system.message.author" /></td>
                <td>${sessionScope.userAccount.username}</td>
            </tr>
            <tr>
                <td id="txtSysMessageSubject"><spring:message code="messaging.system.message.subject" /></td>
                <td><form:input path="messageTitle" /></td>
                <td><form:errors path="messageTitle" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td id="txtSysMessageBody"><spring:message code="messaging.system.message.body" /></td>
                <td><form:textarea path="messageText" /></td>
                <td><form:errors path="messageText" cssClass="validationError" /></td>
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
