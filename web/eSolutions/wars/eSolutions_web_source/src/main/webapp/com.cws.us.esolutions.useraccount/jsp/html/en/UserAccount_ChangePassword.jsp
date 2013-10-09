<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="user.account.update.password" />
    <br /><br />
    <spring:message code="user.account.update.password.rqmts" />

    <p id="validationError" />

    <form:form name="submitPasswordChange" id="submitPasswordChange" action="${pageContext.request.contextPath}/ui/user-account/password" method="POST">
        <table id="userauth">
            <c:if test="${not command.isReset}">
	            <tr>
	                <td><label id="txtCurrentPassword"><spring:message code="user.account.update.password.current" /><br /></label></td>
	                <td>
	                    <form:password path="currentPassword" />
	                    <form:errors path="currentPassword" cssClass="validationError" />
	                </td>
	            </tr>
	        </c:if>
            <tr>
                <td><label id="txtNewPassword"><spring:message code="user.account.update.password.new" /><br /></label></td>
                <td>
                    <form:password path="newPassword" />
                    <form:errors path="newPassword" cssClass="validationError" />
                </td>
            </tr>
            <tr>
                <td><label id="txtConfirmPassword"><spring:message code="user.account.update.password.confirm" /><br /></label></td>
                <td>
                    <form:password path="confirmPassword" />
                    <form:errors path="confirmPassword" cssClass="validationError" />
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>
                    <input type="button" name="execute" value="<spring:message code='button.execute.text' />" id="execute" class="submit" onclick="disableButton(this); validatePassword(this.form, event);" />
                </td>
                <td>
                    <input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onClick="resetForm();" />
                </td>
            </tr>
        </table>
    </form:form>
</div>
<br /><br />
