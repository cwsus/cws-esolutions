<div class="feature">
    <div id="breadcrumb" class="lpstartover">
        <a href="${pageContext.request.contextPath}/ui/knowledgebase/default" title="<spring:message code='kbase.search.title' />">
            <spring:message code='kbase.search.title' /></a>
    </div>

    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="kbase.create-article.begin-updates" />

    <p id="validationError" />

    <form:form id="submitNewArticle" name="submitNewArticle" action="${pageContext.request.contextPath}/ui/knowledgebase/create-article" method="post">
        <table id="ShowArticle">
            <tr>
                <td id="txtArticleId"><strong><em><spring:message code="kbase.view-article.article-id" /></em></strong></td>
                <td><form:input path="articleId" readonly="true" /></td>
            </tr>
            <tr>
                <td id="txtArticleTitle"><strong><em><spring:message code="kbase.view-article.article-title" /></em></strong></td>
                <td><form:input path="title" /></td>
                <td><form:errors path="title" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td id="txtArticleSymptoms"><strong><em><spring:message code="kbase.view-article.article-symptoms" /></em></strong></td>
                <td><form:input path="symptoms" /></td>
                <td><form:errors path="symptoms" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td id="txtArticleCause"><strong><em><spring:message code="kbase.view-article.article-cause" /></em></strong></td>
                <td><form:input path="cause" /></td>
                <td><form:errors path="cause" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td id="txtArticleKeywords"><strong><em><spring:message code="kbase.create-article.article-keywords" /></em></strong></td>
                <td><form:input path="keywords" /></td>
                <td><form:errors path="keywords" cssClass="validationError" /></td>
            </tr>
        </table>
        <br />
        <label id="txtArticleResolution"><strong><spring:message code="kbase.view-article.article-resolution" /></strong></label>
        <br />
        <form:textarea path="resolution" cols="90" rows="10" />
        <form:errors path="resolution" />
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
	            <td align="center" valign="middle"><em>${command.author}</em></td>
	            <td align="center" valign="middle"><em>${command.createDate}</em></td>
	            <td align="center" valign="middle"><em>${command.modifiedBy}</em></td>
	            <td align="center" valign="middle"><em>${command.modifiedOn}</em></td>
	            <td align="center" valign="middle"><em>${command.reviewedBy}</em></td>
	            <td align="center" valign="middle"><em>${command.reviewedOn}</em></td>
	        </tr>
	    </table>
	    <br /><br />
        <table id="inputItems">
            <tr>
                <td>
                    <input type="button" name="execute" value="<spring:message code='button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
                <td>
                    <input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
                <td>
                    <input type="button" name="reset" value="<spring:message code='button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                </td>
            </tr>
        </table>
    </form:form>
</div>
