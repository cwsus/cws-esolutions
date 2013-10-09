<spring:htmlEscape defaultHtmlEscape="true" />

<div class="middle-column-box-full-standard">
    <div class="middle-column-title-standard"><spring:message code="site.search.header" /></div>
    <p id="validationErrors" />
    <form:form name="siteSearchForm" method="post" action="${pageRequest.context.contextPath}/ui/corp/search.htm">
        <div>
            <label id="searchTerms"><spring:message code="site.search.search.terms" /></label>
            <input type="button" name="execute" value="<spring:message code='site.search.button.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
        </div>
    </form:form>
</div>
