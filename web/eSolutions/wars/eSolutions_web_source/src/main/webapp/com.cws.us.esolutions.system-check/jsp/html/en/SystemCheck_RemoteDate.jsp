<div class="feature">
    <div id="breadcrumb" class="lpstartover">
        <a href="${pageContext.request.contextPath}/ui/system-check/telnet"
            title="<spring:message code='select.request.type.telnet' />"><spring:message code='select.request.type.telnet' /></a> / 
    </div>

    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="telnet.request.enter.information" />

    <p id="validationError" />

    <form:form id="submitRemoteDate" name="submitRemoteDate" action="${pageContext.request.contextPath}/ui/system-check/remote-date" method="post">
        <table id="remoteDate">
            <tr>
                <td><label id="txtTargetHostName"><spring:message code="remotedate.request.provide.hostname" /></label></td>
                <td>
	                <c:choose>
	                    <c:when test="${not empty serverList}">
		                    <form:select path="targetServer">
		                        <form:options items="${serverList}" />
		                    </form:select>
	                    </c:when>
	                    <c:otherwise>
			                <td><form:input path="targetServer" /></td>
			            </c:otherwise>
			        </c:choose>
			    </td>
		        <td><form:errors path="targetServer" cssClass="validationError" /></td>
            </tr>
        </table>
        <br />
        <table>
            <tr>
                <td>
                    <input type="button" name="execute" value="Continue" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
            </tr>
        </table>
    </form:form>
</div>
