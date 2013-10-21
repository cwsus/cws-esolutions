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
 * System_ContactAdmins.jsp
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

    <spring:message code="messaging.send.email.message" />

    <p id="validationError" />

    <form:form name="submitContactForm" method="post" action="${pageRequest.request.contextPath}/ui/messaging/send-email">
        <table>
            <tr>
                <td><label id="txtMessageSubject"><spring:message code="add.contact.request.subject" /></label></td>
                <td><form:errors path="messageSubject" cssClass="validationError" /></td>
                <td><form:input path="messageSubject" /></td>
            </tr>
            <tr>
                <td><label id="txtMessageBody"><spring:message code="add.contact.request.body" /></label></td>
                <td><form:errors path="messageBody" cssClass="validationError" /></td>
                <td><form:textarea path="messageBody" /></td>
            </tr>
        </table>
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
