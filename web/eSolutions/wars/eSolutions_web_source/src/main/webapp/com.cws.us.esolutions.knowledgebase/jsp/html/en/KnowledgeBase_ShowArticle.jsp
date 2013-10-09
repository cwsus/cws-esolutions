<div class="feature">
    <div id="breadcrumb" class="lpstartover">
        <a href="javascript:history.go(-1)" title="Back"><spring:message code="kbase.view-article.return" /></a> /
        <a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article"
            title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a> /
        <a href="${pageContext.request.contextPath}/ui/knowledgebase/edit-article/${article.articleId}"
            title="<spring:message code="kbase.edit.article" />"><spring:message code="kbase.edit.article" />&nbsp; ${article.articleId}</a>
        <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
            / <a href="#"
                title="<spring:message code="kbase.delete.article" />&nbsp; ${article.articleId}" onclick="deleteArticle('${article.articleId}')">
                <spring:message code="kbase.delete.article" />&nbsp; ${article.articleId}</a>
            / <a href="${pageContext.request.contextPath}/ui/knowledgebase/show-approvals"
                title="<spring:message code='kbase.list.pending.approvals' />"><spring:message code='kbase.list.pending.approvals' /></a> /
        </c:if>
    </div>

    <table id="ShowArticle">
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-id" /></em></strong></td>
            <td>${article.articleId}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-title" /></em></strong></td>
            <td>${article.title}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-symptoms" /></em></strong></td>
            <td>${article.symptoms}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-cause" /></em></strong></td>
            <td>${article.cause}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-keywords" /></em></strong>
            <td>${article.keywords}</td>
        </tr>
    </table>
    <br />
    <strong><spring:message code="kbase.view-article.article-resolution" /></strong>
    <br />
    ${article.resolution}
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
