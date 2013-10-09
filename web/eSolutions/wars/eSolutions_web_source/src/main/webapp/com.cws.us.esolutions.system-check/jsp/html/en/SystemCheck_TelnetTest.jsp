<div class="feature">
    <div id="breadcrumb" class="lpstartover">
        <a href="${pageContext.request.contextPath}/ui/system-check/remote-date"
            title="<spring:message code='select.request.type.date' />"><spring:message code='select.request.type.date' /></a>
    </div>

    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="telnet.request.enter.information" />

    <p id="validationError" />

    <form:form id="submitTelnetRequest" name="submitTelnetRequest" action="${pageContext.request.contextPath}/ui/system-check/telnet" method="post">
        <table id="telnetRequest">
            <tr>
                <td><label id="txtSourceHostName"><spring:message code="telnet.request.select.source" /></label></td>
                <td>
	                <c:choose>
	                    <c:when test="${not empty serverList}">
	                        <form:select path="sourceServer">
	                            <form:options items="${serverList}" />
	                        </form:select>
	                    </c:when>
	                    <c:otherwise>
	                        <td><form:input path="sourceServer" /></td>
	                    </c:otherwise>
	                </c:choose>
	            </td>
                <td><form:errors path="sourceServer" cssclass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtTargetServer"><spring:message code="telnet.request.select.target" /></label></td>
                <td><form:input path="targetServer" /></td>
                <td><form:errors path="targetServer" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtTargetPort"><spring:message code="telnet.request.provide.port" /></label></td>
                <td><form:input path="targetPort" /></td>
                <td><form:errors path="targetPort" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td><input type="button" name="execute" value="Continue" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" /></td>
            </tr>
        </table>
    </form:form>
</div>
