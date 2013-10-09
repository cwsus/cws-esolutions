<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="message.system.message.create.banner" />

    <p id="validationError" />

    <form:form id="submitSystemMessage" name="submitSystemMessage" action="${pageContext.request.contextPath}/ui/messaging/submit-message" method="post" autocomplete="off">
        <table id="contactTable">
            <tr>
                <td id="txtSubmittorUserID"><spring:message code="messaging.system.message.author" /></td>
                <td>${sessionScope.userAccount.username}</td>
            </tr>
            <tr>
                <td id="txtSysMessageSubject"><spring:message code="messaging.system.message.subject" /></td>
                <td><form:input path="messageTitle" /></td>
                <td><form:errors path="messageTitle" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td id="txtSysMessageBody"><spring:message code="messaging.system.message.body" /></td>
                <td><form:textarea path="messageText" /></td>
                <td><form:errors path="messageText" cssClass="validationError" /></td>
            </tr>
        </table>
        <table id="inputTable">
            <tr>
                <td>
                    <input type="button" name="execute" value="<spring:message code='messaging.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
                <td>
                    <input type="button" name="cancel" value="<spring:message code='messaging.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
                <td>
                    <input type="button" name="reset" value="<spring:message code='messaging.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                </td>
            </tr>
        </table>
    </form:form>
</div>
