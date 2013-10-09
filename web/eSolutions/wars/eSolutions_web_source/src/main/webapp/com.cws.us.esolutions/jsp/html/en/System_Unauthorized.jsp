<div class="error">
    <spring:message code="request.unauthorized" />
    <br /><br />
    <c:choose>
        <c:when test="${requestScope.isUserLoggedIn != 'true'}">
            <p><a href="${pageContext.request.contextPath}/ui/home/default" title="<spring:message code='link.sectionLinks.home' />"><spring:message code="text.click.continue" arguments="${pageContext.request.contextPath}/ui/home/default" /></a></p>
        </c:when>
        <c:otherwise>
            <p><a href="${pageContext.request.contextPath}/ui/login/default" title="<spring:message code='link.sectionLinks.login' />"><spring:message code="text.click.continue" arguments="${pageContext.request.contextPath}/ui/auth/default" /></a></p>
        </c:otherwise>
    </c:choose>
</div>
<br /><br />
