<div class="feature">
    <div id="breadcrumb" class="lpstartover">
        <a href="javascript:history.go(-1)" title="Back"><spring:message code="kbase.view-article.return" /></a> /
        <a href="${pageContext.request.contextPath}/ui/knowledgebase/approve-article/${article.articleId}" title="<spring:message code='kbase.approve-article.approve-link' />"><spring:message code='kbase.approve-article.approve-link' /></a> /
        <a href="${pageContext.request.contextPath}/ui/knowledgebase/reject-article/${article.articleId}" title="<spring:message code='kbase.approve-article.reject-link' />"><spring:message code='kbase.approve-article.reject-link' /></a> /
        <a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article" title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a>
    </div>

    <table id="ShowArticle">
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-id" /></em></strong></td>
            <td>${articleInfo.articleId}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-title" /></em></strong></td>
            <td>${articleInfo.articleTitle}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-symptoms" /></em></strong></td>
            <td>${articleInfo.articleSymptoms}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-cause" /></em></strong></td>
            <td>${articleInfo.articleCause}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-keywords" /></em></strong>
            <td>${articleInfo.articleKeywords}</td>
        </tr>
    </table>
    <br />
    <strong><spring:message code="kbase.view-article.article-resolution" /></strong>
    <br />
    ${articleInfo.articleResolution}
</div>
