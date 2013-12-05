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
 * com.cws.us.esolutions.knowledgebase/jsp/html/en
 * KnowledgeBase_ReviewSubmission.jsp
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

<script>
    <!--
    function approveArticle(theArticle)
    {
        var confirmation = confirm("Are you sure you wish to approve article " + theArticle + " ?");

        if (confirmation)
        {
            window.location.href = '${pageContext.request.contextPath}/ui/knowledgebase/approve-article/article/' + theArticle;
        }
    }

    function rejectArticle(theArticle)
    {
        var confirmation = confirm("Are you sure you wish to reject article " + theArticle + " ?");

        if (confirmation)
        {
            window.location.href = '${pageContext.request.contextPath}/ui/knowledgebase/reject-article/article/' + theArticle;
        }
    }

    function deleteArticle(theArticle)
    {
        var confirmation = confirm("Are you sure you wish to delete article " + theArticle + " ?");

        if (confirmation)
        {
            window.location.href = '${pageContext.request.contextPath}/ui/knowledgebase/delete-article/article/' + theArticle;
        }
    }
    //-->
</script>

<div id="sidebar">
    <h1><spring:message code="kbase.header" /></h1>
    <ul>
        <li><a href="javascript:history.go(-1)" title="Back"><spring:message code="theme.previous.page" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/knowledgebase/default" title="<spring:message code='kbase.header' />"><spring:message code="kbase.header" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article" title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a></li>
        <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
            <li><a href="${pageContext.request.contextPath}/ui/knowledgebase/show-approvals" title="<spring:message code='kbase.list.pending.approvals' />"><spring:message code='kbase.list.pending.approvals' /></a></li>
        </c:if>
        <li><a href="${pageContext.request.contextPath}/ui/common/submit-contact" title="<spring:message code="theme.submit.support.request" />"><spring:message code="theme.submit.support.request" /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="kbase.article.review.submit" arguments="${article.articleId}" /></h1>

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

    <p>
        <table id="ShowArticle">
            <tr>
                <td><strong><em><spring:message code="kbase.article.symptoms" /></em></strong></td>
                <td>${article.symptoms}</td>
            </tr>
            <tr>
                <td><strong><em><spring:message code="kbase.article.cause" /></em></strong></td>
                <td>${article.cause}</td>
            </tr>
            <tr>
                <td><strong><em><spring:message code="kbase.article.keywords" /></em></strong></td>
                <td>${article.keywords}</td>
            </tr>
        </table>
        <br />
        <strong><spring:message code="kbase.article.resolution" /></strong>
        <br />
        ${article.resolution}
        <br /><br />
        <p class="post-footer align-right">
            <spring:message code="kbase.article.author" /> <a href="mailto:${article.author.emailAddr}?subject=Request for Comments: ${article.articleId}" title="Request for Comments: ${article.articleId}">${article.author.username}</a>
            <span class="date"><spring:message code="kbase.article.created" /> ${article.createDate}</span>
            <span class="date"><spring:message code="kbase.article.modified" /> ${article.modifiedOn}</span>
            <span class="date"><spring:message code="kbase.article.approved" /> ${article.reviewedOn}</span>
        </p>
    </p>

    <form:form id="reviewArticleData" name="reviewArticleData" action="${pageContext.request.contextPath}/ui/knowledgebase/submit-article" method="post" commandName="article">
        <form:hidden path="articleId" value="${article.articleId}" />
        <form:hidden path="title" value="${article.title}" />
        <form:hidden path="symptoms" value="${article.symptoms}" />
        <form:hidden path="cause" value="${article.cause}" />
        <form:hidden path="keywords" value="${article.keywords}" />
        <form:hidden path="resolution" value="${article.resolution}" />
        <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); this.form.submit();" />
        <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="javascript:history.go(-1);" />
    </form:form>
</div>

<div id="rightbar">
    <spring:message code="kbase.article.modifier" /> <a href="mailto:${article.modifiedBy.emailAddr}?subject=Request for Comments: ${article.articleId}" title="Request for Comments: ${article.articleId}">${article.modifiedBy.username}</a>
    <spring:message code="kbase.article.approver" /> <a href="mailto:${article.reviewedBy.emailAddr}?subject=Request for Comments: ${article.articleId}" title="Request for Comments: ${article.articleId}">${article.reviewedBy.username}</a>
</div>
