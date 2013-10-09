<div class="middle-column-box-full-standard">
    <div class="middle-column-title-standard"><spring:message code="add.contact.welcome" /></div>

    <p id="validationError" />

    <form:form name="submitContactForm" method="post" action="${pageRequest.request.contextPath}/ui/corp/contact.htm">
        <form:errors path="*" cssClass="error" element="div" />
        <table>
            <tr>
                <td><label id="firstName"><spring:message code="add.contact.first.name" /></label></td>
                <td><form:errors path="firstName" cssClass="validationError" /></td>
                <td><form:input path="firstName" /></td>
            </tr>
            <tr>
                <td><label id="lastName"><spring:message code="add.contact.last.name" /></label></td>
                <td><form:errors path="lastName" cssClass="validationError" /></td>
                <td><form:input path="lastName" /></td>
            </tr>
            <tr>
                <td><label id="messageTo"><spring:message code="add.contact.email.addr" /></label></td>
                <td><form:errors path="messageTo" cssClass="validationError" /></td>
                <td><form:input path="messageTo" /></td>
            </tr>
            <tr>
                <td><label id="messageSubject"><spring:message code="add.contact.request.subject" /></label></td>
                <td><form:errors path="messageSubject" cssClass="validationError" /></td>
                <td><form:input path="messageSubject" /></td>
            </tr>
            <tr>
                <td><label id="messageBody"><spring:message code="add.contact.request.body" /></label></td>
                <td><form:errors path="messageBody" cssClass="validationError" /></td>
                <td><form:textarea path="messageBody" /></td>
            </tr>
            <tr>
                <td><input type="button" name="execute" value="<spring:message code="add.contact.submit.button" />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" /></td>
            </tr>
        </table>
    </form:form>
</div>
