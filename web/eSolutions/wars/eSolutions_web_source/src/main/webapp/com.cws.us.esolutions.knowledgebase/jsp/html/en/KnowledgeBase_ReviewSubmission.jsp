<div class="feature">
    <div id="breadcrumb" class="lpstartover">
        <a href="javascript:history.go(-1)" title="Back"><spring:message code="kbase.view-article.return" /></a> /
        <c:if test="${param.command ne 'cmd_CreateKnowledgeBaseArticle'}">
            <a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article" title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a> /
            <a href="${pageContext.request.contextPath}/ui/knowledgebase/edit-article/${article.articleId}" title="<spring:message code="kbase.edit.article" />"><spring:message code="kbase.edit.article" />&nbsp; ${article.articleId}</a>
            <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                / <a href="#" title="<spring:message code="kbase.delete.article" />&nbsp; ${article.articleId}" onclick="deleteArticle('${article.articleId}')"><spring:message code="kbase.delete.article" />&nbsp; ${article.articleId}</a>
                <c:if test="${param.command eq 'cmd_ApproveKnowledgeBaseArticle'}">
                    / <a href="${pageContext.request.contextPath}/ui/knowledgebase/approve-article/${article.articleId}"
                        title="<spring:message code='kbase.approve-article.approve-link' />"><spring:message code='kbase.approve-article.approve-link' /></a>
                    / <a href="${pageContext.request.contextPath}/ui/knowledgebase/reject-article/${article.articleId}"
                        title="<spring:message code='kbase.approve-article.reject-link' />"><spring:message code='kbase.approve-article.reject-link' /></a>
                </c:if>
            </c:if>
        </c:if>
    </div>

    <table id="ShowArticle">
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-id" /></em></strong></td>
            <td>${article.articleId}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-title" /></em></strong></td>
            <td>${article.articleTitle}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-symptoms" /></em></strong></td>
            <td>${article.articleSymptoms}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-cause" /></em></strong></td>
            <td>${article.articleCause}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-keywords" /></em></strong>
            <td>${article.articleKeywords}</td>
        </tr>
    </table>
    <br />
    <strong><spring:message code="kbase.view-article.article-resolution" /></strong>
    <br />
    ${article.articleResolution}
    <br /><br />
    <table class="kbauth">
        <tr>
            <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.view-article.article-author" /></strong></td>
            <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.view-article.article-created" /></strong></td>
            <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.view-article.article-modifier" /></strong></td>
            <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.view-article.article-modified" /></strong></td>
            <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.view-article.article-approver" /></strong></td>
            <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.view-article.article-approved" /></strong></td>
        </tr>
        <tr>
            <td align="center" valign="middle">
                <em><a href="mailto:${article.articleAuthorEmail}?subject=Request for Comments: ${article.articleId}"
                    title="Request for Comments: ${article.articleId}">${article.articleAuthor}</a></em>
            </td>
            <td align="center" valign="middle"><em>${article.articleCreateDate}</em></td>
            <td align="center" valign="middle">
                <em><a href="mailto:${systemEmailAddress}?subject=Request for Comments: ${article.articleId}"
                    title="Request for Comments: ${article.articleId}">${article.articleModifiedBy}</a></em>
            </td>
            <td align="center" valign="middle"><em>${article.articleModifiedOn}</em></td>
            <td align="center" valign="middle">
                <em><a href="mailto:${systemEmailAddress}?subject=Request for Comments: ${article.articleId}"
                    title="Request for Comments: ${article.articleId}">${article.articleReviewedBy}</a></em>
            </td>
            <td align="center" valign="middle"><em>${article.articleReviewedOn}</em></td>
        </tr>
    </table>
</div>
