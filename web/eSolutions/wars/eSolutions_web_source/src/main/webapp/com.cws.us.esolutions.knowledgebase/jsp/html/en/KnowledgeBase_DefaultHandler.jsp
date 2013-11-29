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
 * com.cws.us.esolutions.application-management/jsp/html/en
 * AppMgmt_ViewFile.jsp
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

<div id="sidebar">
    <h1><spring:message code="kbase.header" /></h1>
    <c:if test="${not empty fn:trim(sessionScope.userAccount)}">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article"
                    title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a>
            </li>
            <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                <li>
                    <a href="${pageContext.request.contextPath}/ui/knowledgebase/show-approvals"
                        title="<spring:message code='kbase.list.pending.approvals' />"><spring:message code='kbase.list.pending.approvals' /></a>
                </li>
            </c:if>
        </ul>
    </c:if>
</div>

<div id="main">
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

    <form:form id="searchRequest" name="searchRequest" action="${pageContext.request.contextPath}/ui/knowledgebase/search" method="post">
        <label id="txtSearchTerms"><spring:message code="theme.search.terms" /><br /></label>
        <form:input path="searchTerms" onkeypress="if (event.keyCode == 13) { disableButton(this); validateForm(this.form, event); }" />
        <form:errors path="searchTerms" cssClass="validationError" />
        <br />
        <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
        <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
        <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
    </form:form>

    <c:if test="${not empty searchResults}">
        <h1><spring:message code="theme.search.results" /></h1>
        <table id="searchResults">
            <c:forEach var="result" items="${searchResults}">
                <tr>
                    <td><a href="${pageContext.request.contextPath}/ui/knowledgebase/article/${result.path}" title="${result.title}">${result.title}</a></td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</div>
