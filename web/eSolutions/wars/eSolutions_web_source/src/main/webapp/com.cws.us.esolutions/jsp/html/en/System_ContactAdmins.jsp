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

<div id="InfoLine"><spring:message code="theme.messaging.send.email.message" /></div>
<div id="content">
    <div id="content-right">
	    <c:if test="${not empty messageResponse}">
	        <p id="info">${messageResponse}</p>
	    </c:if>
	    <c:if test="${not empty errorResponse}">
	        <p id="error">${errorResponse}</p>
	    </c:if>
	    <c:if test="${not empty responseMessage}">
	        <p id="info"><spring:message code="${responseMessage}" /></p>
	    </c:if>
	    <c:if test="${not empty errorMessage}">
	        <p id="error"><spring:message code="${errorMessage}" /></p>
	    </c:if>

        <span id="validationError"></span>

	    <form:form name="submitContactForm" method="post" action="${pageContext.request.contextPath}/ui/common/submit-contact">
	        <table>
	            <tr>
	                <td><label id="txtMessageSubject"><spring:message code="theme.add.contact.request.subject" /></label></td>
	                <td><form:errors path="messageSubject" cssClass="validationError" /></td>
	                <td><form:input path="messageSubject" /></td>
	            </tr>
	            <tr>
	                <td><label id="txtMessageBody"><spring:message code="theme.add.contact.request.body" /></label></td>
	                <td><form:errors path="messageBody" cssClass="validationError" /></td>
	                <td><form:textarea path="messageBody" /></td>
	            </tr>
                <tr>
                    <td><label id="txtRequestorEmail"><spring:message code="theme.add.contact.source.email" /></label></td>
                    <td><form:errors path="emailAddr" cssClass="validationError" /></td>
                    <td><form:input path="emailAddr" /></td>
                </tr>
	        </table>

	        <table id="inputItems">
	            <tr>
	                <td>
	                    <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
	                </td>
	                <td>
	                    <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
	                </td>
	                <td>
	                    <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
	                </td>
	            </tr>
	        </table>
	    </form:form>
    </div>
</div>
