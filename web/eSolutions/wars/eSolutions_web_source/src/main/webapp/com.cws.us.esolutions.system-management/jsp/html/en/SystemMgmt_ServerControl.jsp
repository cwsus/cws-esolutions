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
 * com.cws.us.esolutions.system-management/jsp/html/en
 * SystemManagement_ServerControl.jsp
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

<div id="InfoLine"><spring:message code="system.mgmt.add.server" /></div>
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

	    <form:form id="controlServer" name="controlServer" action="${pageContext.request.contextPath}/ui/system-management/server-control" method="post">
	        <table id="serverDetail">
	            <tr>
	                <td><label id="txtTargetServer"><spring:message code="system.mgmt.server.status" /></label>
	                <td>
	                    <form:select path="targetServer">
	                        <form:options items="${serverList}" />
	                    </form:select>
	                </td>
	                <td><form:errors path="targetServer" cssClass="validationError" /></td>
	            </tr>
	            <tr>
	                <td><label id="txtOperationType"><spring:message code="system.mgmt.server.region" /></label>
	                <td>
	                    <form:select path="operationType">
	                        <form:options items="${operationTypes}" />
	                    </form:select>
	                </td>
	                <td><form:errors path="operationType" cssClass="validationError" /></td>
	            </tr>
	        </table>

	        <table id="inputItems">
	            <tr>
	                <td>
	                    <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
	                </td>
	                <td>
	                    <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
	                </td>
	                <td>
	                    <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
	                </td>
	            </tr>
	        </table>
	    </form:form>
    </div>

    <div id="content-left">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/ui/system-management/add-server"
                    title="<spring:message code='system.mgmt.add.server' />"><spring:message code="system.mgmt.add.server" /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/system-check/telnet/server/${server.serverGuid}"
                    title="<spring:message code='system.check.telnet' />"><spring:message code='system.check.telnet' /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/system-check/remote-date/server/${server.serverGuid}"
                    title="<spring:message code='system.check.date' />"><spring:message code='system.check.date' /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/system-management/install-software"
                    title="<spring:message code='system.mgmt.add.server' />"><spring:message code="system.mgmt.add.server" /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/system-management/server-control"
                    title="<spring:message code='system.mgmt.server.control.header' />"><spring:message code='system.mgmt.server.control.header' /></a>
            </li>
        </ul>
    </div>
</div>
