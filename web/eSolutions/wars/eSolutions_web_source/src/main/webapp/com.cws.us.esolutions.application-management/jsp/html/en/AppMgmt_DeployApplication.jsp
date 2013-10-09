<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="app.mgmt.deploy.application" />

    <p id="validationError" />

    <form:form id="deployApplication" name="deployApplication" action="${pageContext.request.contextPath}/ui/application-management/deploy-application" method="post">
        <table id="applicationDetail">
            <tr>
                <td><label id="txtAppName">${requestScope.applicationName}</label></td>
                <td><label id="txtAppVersion">${requestScope.application.applicationVersion}</label></td>
                <td><label id="txtProjectName">${requestScope.application.project.projectCode}</label></td>
                <td><label id="txtPlatformName">${requestScope.application.platform.platformCode}</label></td>
            </tr>
            <c:choose>
                <c:when test="${empty requestScope.scmPath}">
	                <tr>
	                    <td><input type="file" name="applicationBinary" id="applicationBinary" size="30" /></td>
	                </tr>
	            </c:when>
	            <c:otherwise>
	                <td><label id="txtScmPath">${requestScope.scmPath}</label></td>
	                <td><label id="txtScmVersion"><spring:message code="app.mgmt.scm.version" /></label></td>
	                <td>
	                    <form:input path="applicationVersion" />
	                    <form:errors path="applicationVersion" cssClass="validationError" />
	                </td>
	            </c:otherwise>
	        </c:choose>
        </table>
        <br /><br />
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
