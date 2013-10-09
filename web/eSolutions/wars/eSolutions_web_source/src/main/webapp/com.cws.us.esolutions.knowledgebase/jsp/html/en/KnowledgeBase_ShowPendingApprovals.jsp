<div class="feature">
    <div id="breadcrumb" class="lpstartover">
        <a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article"
            title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a>
    </div>

    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <br /><br />

    <spring:message code="kbase.approve-article.select-article" />
    <br /><br />
    <table id="siteSearch">
        <c:forEach var="entry" items="${articleList}">
            <tr>
                <td><a href="${pageContext.request.contextPath}/ui/knowledgebase/article/${entry.articleId}" title="${entry.articleId}">${entry.articleId}</a></td>
                <td><a href="${pageContext.request.contextPath}/ui/knowledgebase/article/${entry.articleId}" title="${entry.title}">${entry.title}</a></td>
            </tr>
        </c:forEach>
    </table>
</div>
