<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="site.search.submit.request" />

    <p id="validationError" />

    <form:form id="searchRequest" name="searchRequest" action="${pageContext.request.contextPath}${postUrl}" method="post">
        <table id="serverSearch">
            <tr>
                <td>
                    <label id="txtSearchData"><spring:message code="search.data" /><br /></label>
                </td>
                <td>
                    <form:input path="searchTerms" />
                    <form:errors path="searchTerms" cssClass="validationError" />
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>
                    <input type="button" name="execute" value="<spring:message code="button.execute.text" />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
            </tr>
        </table>
    </form:form>

    <c:if test="${not empty searchResults}">
        <p id="splitter" />

        <strong><spring:message code="search.results" /></strong>
        <br /><br />
        <table id="searchResults">
            <c:forEach var="result" items="${searchResults}">
                <tr>
                    <td><a href="${pageContext.request.contextPath}${requestUrl}${result.path}" title="${result.title}">${result.title}</a></td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</div>
