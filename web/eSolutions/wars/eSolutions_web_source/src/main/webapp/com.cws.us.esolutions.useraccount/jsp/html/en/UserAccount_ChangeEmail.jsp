<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="user.account.update.email.address" />

    <p id="validationError" />

    <form:form name="submitEmailChange" id="submitEmailChange" action="${pageContext.request.contextPath}/ui/user-account/email" method="post">
        <table id="userauth">
            <tr>
                <td><label id="txtEmailAddr"><spring:message code="user.account.change.email.address" /></label></td>
                <td>
                    <form:input path="emailAddr" />
                    <form:errors path="emailAddr" cssClass="validationError" />
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td><label id="txtPassword"><spring:message code="user.account.change.email.address" /></label></td>
                <td>
                    <form:password path="currentPassword" />
                    <form:errors path="currentPassword" cssClass="validationError" />
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>
                    <input type="button" name="execute" value="<spring:message code='button.execute.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
                <td>
                    <input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onclick="resetForm();" />
                </td>
            </tr>
        </table>
    </form:form>
</div>
<br /><br />
