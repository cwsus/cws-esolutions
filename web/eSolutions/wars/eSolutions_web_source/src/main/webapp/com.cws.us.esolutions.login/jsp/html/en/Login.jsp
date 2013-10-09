<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="login.user.uid.message" />

    <p id="validationError" />

    <form:form id="submitUserLogin" name="submitUserLogin" action="${pageContext.request.contextPath}/ui/login/login" method="post">
        <table id="userauth">
            <tr>
                <td><label id="txtUsername"><spring:message code="login.user.name" /></label></td>
                <td>
                    <form:input path="loginUser" />
                    <form:errors path="loginUser" cssClass="validationError" />
                </td>
                <td>
                    <c:if test="${not empty forgotUsernameUrl}">
                        <a href="<c:out value="${pageContext.request.contextPath}/${forgotUsernameUrl}" />" title="<spring:message code='login.user.forgot_uid' />">
                            <spring:message code="login.user.forgot_uid" />
                        </a>
                    </c:if>
                </td>
            </tr>
            <tr>
                <td><label id="txtPassword"><spring:message code="login.user.pwd" /></label></td>
                <td>
                    <form:password path="loginPass" />
                    <form:errors path="loginPass" cssClass="validationError" />
                </td>
                <td>
                    <c:if test="${not empty forgotPasswordUrl}">
                        <a href="<c:out value="${pageContext.request.contextPath}/${forgotPasswordUrl}" />" title="<spring:message code='login.user.forgot_pwd' />">
                            <spring:message code="login.user.forgot_pwd" />
                        </a>
                    </c:if>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>
                    <input type="button" name="execute" value="<spring:message code='button.execute.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
            </tr>
        </table>
    </form:form>
</div>
<br /><br />
