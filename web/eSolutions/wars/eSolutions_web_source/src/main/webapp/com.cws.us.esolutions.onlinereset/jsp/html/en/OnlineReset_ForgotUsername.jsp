<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="olr.provide.email.address" />

    <p id="validationError" />

    <form:form id="submitEmailForUserSearch" name="submitEmailForUserSearch" action="${pageContext.request.contextPath}/ui/online-reset/forgot-username" method="post" autocomplete="off">
        <table id="userauth">
            <tr>
                <td><label id="txtEmailAddr"><spring:message code="olr.user.email.address" /></label></td>
                <td>
                    <form:input path="emailAddr" />
                    <form:errors path="emailAddr" cssClass="validationError" />
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}/ui/app/help/forgot-email"
                        title="<spring:message code="olr.user.forgot.email" />"><spring:message code="olr.user.forgot.email" /></a>
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
