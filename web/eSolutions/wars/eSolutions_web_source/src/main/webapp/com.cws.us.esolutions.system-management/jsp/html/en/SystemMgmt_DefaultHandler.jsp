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
 * SystemManagement_DefaultHandler.jsp
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

<div id="InfoLine"><spring:message code="theme.search.header" /></div>
<div id="content">
    <div id="content-right">
	    <c:if test="${not empty fn:trim(messageResponse)}">
	        <p id="info">${messageResponse}</p>
	    </c:if>
	    <c:if test="${not empty fn:trim(errorResponse)}">
	        <p id="error">${errorResponse}</p>
	    </c:if>
	    <c:if test="${not empty fn:trim(responseMessage)}">
	        <p id="info"><spring:message code="${responseMessage}" /></p>
	    </c:if>
	    <c:if test="${not empty fn:trim(errorMessage)}">
	        <p id="error"><spring:message code="${errorMessage}" /></p>
	    </c:if>
	    <c:if test="${not empty fn:trim(param.responseMessage)}">
	        <p id="info"><spring:message code="${param.responseMessage}" /></p>
	    </c:if>
	    <c:if test="${not empty fn:trim(param.errorMessage)}">
	        <p id="error"><spring:message code="${param.errorMessage}" /></p>
	    </c:if>

        <span id="validationError"></span>

	    <form:form id="searchRequest" name="searchRequest" action="${pageContext.request.contextPath}/ui/system-management/search" method="post">
	        <table id="serverSearch">
	            <tr>
	                <td>
	                    <label id="txtSearchTerms"><spring:message code="theme.search.terms" /><br /></label>
	                </td>
	                <td>
	                    <form:input path="searchTerms" onkeypress="if (event.keyCode == 13) { disableButton(this); validateForm(this.form, event); }" />
	                    <form:errors path="searchTerms" cssClass="validationError" />
	                </td>
	            </tr>
	        </table>
	        <br /><br />
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

	    <c:if test="${not empty searchResults}">
	        <p id="splitter" />

	        <strong><spring:message code="theme.search.results" /></strong>
	        <br /><br />
	        <table id="searchResults">
	            <c:forEach var="result" items="${searchResults}">
	                <tr>
	                    <td><a href="${pageContext.request.contextPath}/ui/system-management/server/${result.path}" title="${result.title}">${result.title}</a></td>
	                </tr>
	            </c:forEach>
	        </table>
	    </c:if>
    </div>

    <div id="content-left">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/ui/system-management/service-consoles"
                    title="<spring:message code='system.mgmt.service.consoles' />"><spring:message code='system.mgmt.service.consoles' /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/system-management/add-server"
                    title="<spring:message code='system.mgmt.add.server' />"><spring:message code="system.mgmt.add.server" /></a>
            </li>
        </ul>
    </div>
</div>
