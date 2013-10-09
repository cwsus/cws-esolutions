<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="login.user.pwd.message" />

    <p id="validationError" />

    <form:form name="submitOTP" method="post" action="${pageContext.request.contextPath}/ui/login/otp">
        <table id="userauth">
            <tr>
                <td><label id="txtPassword"><spring:message code="login.user.pwd" /></label></td>
                <td>
                    <form:password path="password" />
                    <form:errors path="password" cssClass="validationError" />
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
                <td><label id="otpValue"><spring:message code="login.user.otp" /></label></td>
                <td><form:password path="otpValue" /></td>
                <td><form:errors path="otpValue" cssClass="validationErrors" /></td>
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
