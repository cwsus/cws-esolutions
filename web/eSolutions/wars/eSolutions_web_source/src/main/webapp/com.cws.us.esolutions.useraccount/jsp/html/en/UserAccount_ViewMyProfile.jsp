<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="user.account.view.profile" />

    <p id="splitter" />

    <table id="viewUserAccount">
        <tr>
            <td><spring:message code="user.account.username" /></td>
            <td>${sessionScope.userAccount.username}</td>
            <td><a href="${pageContext.request.contextPath}/ui/user-account/password"
                    title="<spring:message code='user.account.change.password' />"><spring:message code="user.account.change.password" /></a>
            </td>
        <tr>
        <tr>
            <td><spring:message code="user.account.name" /></td>
            <td>${sessionScope.userAccount.displayName}</td>
        </tr>
        <tr>
            <td><spring:message code="user.account.email.addr" /></td>
            <td>${sessionScope.userAccount.emailAddr}</td>
            <td>
                <a href="${pageContext.request.contextPath}/ui/user-account/email"
                    title="<spring:message code='user.account.change.email' />"><spring:message code="user.account.change.email" /></a>
            </td>
        </tr>
        <tr>
            <td><spring:message code="user.account.security.questions" /></td>
            <td>
                <a href="${pageContext.request.contextPath}/ui/user-account/security"
                    title="<spring:message code='user.account.change.security.questions' />"><spring:message code="user.account.change.security.questions" /></a>
            </td>
        </tr>
    </table>
</div>
<br /><br />
