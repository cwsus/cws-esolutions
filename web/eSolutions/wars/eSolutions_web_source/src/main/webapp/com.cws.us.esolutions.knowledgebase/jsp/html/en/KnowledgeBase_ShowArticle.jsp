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
 * KnowledgeBase_ShowArticle.jsp
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

<div id="InfoLine"><strong>${article.articleId} - ${article.title}</strong></div>
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
	    <table class="kbauth">
	        <tr>
	            <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.article.author" /></strong></td>
	            <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.article.created" /></strong></td>
	            <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.article.modifier" /></strong></td>
	            <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.article.modified" /></strong></td>
	            <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.article.approver" /></strong></td>
	            <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.article.approved" /></strong></td>
	        </tr>
	        <tr>
	            <td align="center" valign="middle">
	                <em><a href="mailto:${article.authorEmail}?subject=Request for Comments: ${article.articleId}"
	                    title="Request for Comments: ${article.articleId}">${article.author}</a></em>
	            </td>
	            <td align="center" valign="middle"><em>${article.createDate}</em></td>
	            <td align="center" valign="middle">
	                <em><a href="mailto:${systemEmailAddress}?subject=Request for Comments: ${article.articleId}"
	                    title="Request for Comments: ${article.articleId}">${article.modifiedBy}</a></em>
	            </td>
	            <td align="center" valign="middle"><em>${article.modifiedOn}</em></td>
	            <td align="center" valign="middle">
	                <em><a href="mailto:${systemEmailAddress}?subject=Request for Comments: ${article.articleId}"
	                    title="Request for Comments: ${article.articleId}">${article.reviewedBy}</a></em>
	            </td>
	            <td align="center" valign="middle"><em>${article.reviewedOn}</em></td>
	        </tr>
	    </table>
    </div>

    <div id="content-left">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/ui/common/default" title="<spring:message code='theme.navbar.home' />">
                    <spring:message code='theme.navbar.home' /></a>
            </li>
            <c:if test="${not empty fn:trim(sessionScope.userAccount)}">
	            <li><a href="javascript:history.go(-1)" title="Back"><spring:message code="theme.previous.page" /></a></li>
	            <li>
	                <a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article"
	                    title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a>
	            </li>
	            <li>
		            <a href="${pageContext.request.contextPath}/ui/knowledgebase/edit-article/article/${article.articleId}"
		                title="<spring:message code="kbase.edit.article" />"><spring:message code="kbase.edit.article" /> ${article.articleId}</a>
	            </li>
	            <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
	                <li>
	                    <a href="#" title="<spring:message code="kbase.delete.article" /> ${article.articleId}" onclick="deleteArticle('${article.articleId}')">
	                        <spring:message code="kbase.delete.article" /> ${article.articleId}</a>
	                </li>
	                <li>
	                    <a href="${pageContext.request.contextPath}/ui/knowledgebase/show-approvals"
	                        title="<spring:message code='kbase.list.pending.approvals' />"><spring:message code='kbase.list.pending.approvals' /></a>
	                </li>
	            </c:if>
            </c:if>
	    </ul>
	</div>
</div>
