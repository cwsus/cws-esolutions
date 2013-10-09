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

    <form:form id="confirmArticleDeletion" name="confirmArticleDeletion" action="${pageContext.request.contextPath}/ui/knowledgebase/delete-article" method="post">
        <table id="confirmDeletion">
            <tr>
                <td><spring:message code="kbase.delete-article.confirm" arguments="${requestScope.articleId}"/>
            </tr>
        </table>
        <table id="inputItems">
            <tr>
                <td><input type="button" name="execute" value="<spring:message code='button.submit.text' />" id="execute" class="submit" onclick="disableButton(this);" /></td>
                <td><input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this);" /></td>
            </tr>
        </table>
    </form:form>
</div>
