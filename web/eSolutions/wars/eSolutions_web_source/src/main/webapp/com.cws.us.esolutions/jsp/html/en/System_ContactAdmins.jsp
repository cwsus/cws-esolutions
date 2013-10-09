<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="messaging.send.email.message" />

    <p id="validationError" />

    <form:form name="submitContactForm" method="post" action="${pageRequest.request.contextPath}/ui/messaging/send-email">
        <table>
            <tr>
                <td><label id="txtMessageSubject"><spring:message code="add.contact.request.subject" /></label></td>
                <td><form:errors path="messageSubject" cssClass="validationError" /></td>
                <td><form:input path="messageSubject" /></td>
            </tr>
            <tr>
                <td><label id="txtMessageBody"><spring:message code="add.contact.request.body" /></label></td>
                <td><form:errors path="messageBody" cssClass="validationError" /></td>
                <td><form:textarea path="messageBody" /></td>
            </tr>
        </table>
        <table id="inputItems">
            <tr>
                <td>
                    <input type="button" name="execute" value="<spring:message code='button.execute.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
                <td>
                    <input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
                <td>
                    <input type="button" name="reset" value="<spring:message code='button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                </td>
            </tr>
        </table>
    </form:form>
</div>
<br /><br />
