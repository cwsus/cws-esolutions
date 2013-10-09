<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="user.account.update.security" />

    <p id="validationError" />

    <form:form name="submitSecurityInformationChange" id="submitSecurityInformationChange" action="${pageContext.request.contextPath}/ui/user-account/security" method="post" autocomplete="off">
        <table id="userauth">
            <tr>
                <td><label id="txtQuestionOne"><spring:message code="user.account.update.security.question" /></label></td>
                <td>
                    <form:select path="secQuestionOne">
                        <form:options items="${questionList}" />
                    </form:select>
                </td>
                <td><form:errors path="secQuestionOne" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtAnswerOne"><spring:message code="user.account.update.security.answer" /></label></td>
                <td><form:password path="secAnswerOne" /></td>
                <td><form:errors path="secAnswerOne" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td id="txtQuestionTwo"><spring:message code="user.account.update.security.question" /></td>
                <td>
                    <form:select path="secQuestionTwo">
                        <form:options items="${questionList}" />
                    </form:select>
                </td>
                <td><form:errors path="secQuestionTwo" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtAnswerTwo"><spring:message code="user.account.update.security.answer" /></label></td>
                <td><form:password path="secAnswerTwo" /></td>
                <td><form:errors path="secAnswerTwo" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtPassword"><spring:message code="user.account.provide.password" /></label></td>
                <td><form:password path="currentPassword" /></td>
                <td><form:errors path="currentPassword" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>
                    <input type="button" name="execute" value="<spring:message code='button.execute.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
                <td>
                    <input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onClick="resetForm();" />
                </td>
            </tr>
        </table>
    </form:form>
</div>
<br /><br />
