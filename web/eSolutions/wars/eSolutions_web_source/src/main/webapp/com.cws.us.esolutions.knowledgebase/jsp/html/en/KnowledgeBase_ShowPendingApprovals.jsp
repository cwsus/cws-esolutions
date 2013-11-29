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
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/knowledgebase/default"
            title="<spring:message code='kbase.default' />">
            <spring:message code="kbase.default" /></a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article"
                title="<spring:message code='kbase.create.article' />">
                <spring:message code="kbase.create.article" /></a>
        </li>
        <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
            <li>
                <a href="${pageContext.request.contextPath}/ui/knowledgebase/show-approvals"
                    title="<spring:message code='kbase.list.pending.approvals' />">
                    <spring:message code='kbase.list.pending.approvals' /></a>
            </li>
        </c:if>
    </ul>
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

    <h1><spring:message code="kbase.list.pending.articles" /></h1>
    <c:choose>
        <c:when test="${not empty fn:trim(articleList)}">
            <table id="siteSearch">
                <c:forEach var="entry" items="${articleList}">
                    <tr>
                        <td><a href="${pageContext.request.contextPath}/ui/knowledgebase/article/${entry.articleId}" title="${entry.articleId}">${entry.articleId}</a></td>
                        <td><a href="${pageContext.request.contextPath}/ui/knowledgebase/article/${entry.articleId}" title="${entry.title}">${entry.title}</a></td>
                    </tr>
                </c:forEach>
            </table>
        </c:when>
        <c:otherwise>
            <spring:message code="kbase.message.no.pending.articles" />
        </c:otherwise>
    </c:choose>
</div>
