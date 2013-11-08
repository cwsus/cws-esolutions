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
 * SystemManagement_RemoteDate.jsp
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

<div id="InfoLine"><spring:message code="telnet.request.enter.information" /></div>
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

        <p id="validationError" />

        <form:form id="submitRemoteDate" name="submitRemoteDate" action="${pageContext.request.contextPath}/ui/system-check/remote-date" method="post">
            <form:hidden path="sourceServer" value="${server}" />

            <table id="remoteDate">
                <tr>
                    <td><label id="txtTargetHostName"><spring:message code="remotedate.request.provide.hostname" /></label></td>
                    <td>${server.operHostName}</td>
                </tr>
            </table>
            <br /><br />
            <table>
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

    <div id="content-left">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/ui/system-management/add-server"
                    title="<spring:message code='select.request.add.server' />"><spring:message code="select.request.add.server" /></a>
            </li>
            <li> 
                <a href="${pageContext.request.contextPath}/ui/system-check/telnet/server/${server.serverGuid}"
                    title="<spring:message code='select.request.type.telnet' />"><spring:message code='select.request.type.telnet' /></a>
            </li>
            <li> 
                <a href="${pageContext.request.contextPath}/ui/system-check/remote-date/server/${server.serverGuid}"
                    title="<spring:message code='select.request.type.date' />"><spring:message code='select.request.type.date' /></a>
            </li>
            <li> 
                <a href="${pageContext.request.contextPath}/ui/system-management/install-software"
                    title="<spring:message code='select.request.install.server' />"><spring:message code="select.request.install.server" /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/system-management/server-control"
                    title="<spring:message code='select.request.server.control' />"><spring:message code='select.request.server.control' /></a>
            </li>
        </ul>
    </div>
</div>
