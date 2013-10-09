<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="olr.user.provide.username" />

    <p id="validationError" />

    <form:form id="submitUsernameForSearch" name="submitUsernameForSearch" action="${pageContext.request.contextPath}/ui/online-reset/forgot-password" method="post" autocomplete="off">
        <table id="userauth">
            <tr>
                <td><label id="txtUsername"><spring:message code="olr.username" /></label></td>
                <td>
                    <form:input path="olrUser" />
                    <form:errors path="olrUser" cssClass="validationError" />
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}/ui/app/help/forgot-username"
                        title="<spring:message code="olr.user.forgot.username" />"><spring:message code="olr.user.forgot.username" /></a>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>
                    <input type="button" name="execute" value="<spring:message code="button.execute.text" />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
                <td>
                    <input type="button" name="cancel" value="<spring:message code="button.cancel.text" />" id="cancel" class="submit" onClick="disableButton(this); validateForm(this.form, event);" />
                </td>
            </tr>
        </table>
    </form:form>
</div>
<br /><br />
