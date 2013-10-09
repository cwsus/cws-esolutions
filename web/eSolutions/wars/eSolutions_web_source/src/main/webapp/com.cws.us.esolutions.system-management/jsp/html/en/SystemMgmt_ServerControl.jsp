<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="system.mgmt.add.server" />

    <p id="validationError" />

    <form:form id="controlServer" name="controlServer" action="${pageContext.request.contextPath}/ui/system-management/server-control" method="post">
        <table id="serverDetail">
            <tr>
                <td><label id="txtTargetServer"><spring:message code="system.mgmt.server.status" /></label>
                <td>
                    <form:select path="targetServer">
                        <form:options items="${serverList}" />
                    </form:select>
                </td>
                <td><form:errors path="targetServer" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtOperationType"><spring:message code="system.mgmt.server.region" /></label>
                <td>
                    <form:select path="operationType">
                        <form:options items="${operationTypes}" />
                    </form:select>
                </td>
                <td><form:errors path="operationType" cssClass="validationError" /></td>
            </tr>
        </table>
        <br /><br />
        <table id="inputItems" name="inputItems">
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
