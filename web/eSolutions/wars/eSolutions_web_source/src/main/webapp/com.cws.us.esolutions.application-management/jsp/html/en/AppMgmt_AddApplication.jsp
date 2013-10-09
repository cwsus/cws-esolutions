<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="app.mgmt.add.application" />

    <p id="validationError" />

    <form:form id="createNewApplication" name="createNewApplication" action="${pageContext.request.contextPath}/ui/application-management/add-application" method="post">
        <table id="applicationDetail">
            <tr>
                <td><label id="txtApplicationName"><spring:message code="app.mgmt.application.name" /></label></td>
                <td><form:input path="applicationName" /></td>
                <td><form:errors path="applicationName" cssClass="validationError" /></td>
                <td><label id="txtApplicationVersion"><spring:message code="app.mgmt.application.version" /></label></td>
                <td><form:input path="version" /></td>
                <td><form:errors path="version" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtClusterName"><spring:message code="app.mgmt.application.cluster.name" /></label></td>
                <td><form:input path="clusterName" /></td>
                <td><form:errors path="clusterName" cssClass="validationError" /></td>
                <td><label id="txtJvmName"><spring:message code="app.mgmt.application.jvm.name" /></label></td>
                <td><form:input path="jvmName" /></td>
                <td><form:errors path="jvmName" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtApplicationProject"><spring:message code="app.mgmt.application.project" /></label></td>
                <c:choose>
                    <c:when test="${not empty projectListing}">
                        <td>
                            <form:select path="project" multiple="false">
                                <form:option value="<spring:message code='select.value' />" />
                                <form:option value="--------" />
                                <form:options items="${projectListing}" />
                            </form:select>
                        </td>
                    </c:when>
                    <c:otherwise>
                        <td>
                            <a href="${pageContext.request.contextPath}/ui/application-management/add-project"
                                title="<spring:message code='select.request.add.project' />"><spring:message code='select.request.add.project' /></a>
                        </td>
                    </c:otherwise>
                </c:choose>
                <td><form:errors path="project" cssClass="validationError" /></td>
                <td><label id="txtApplicationPlatform"><spring:message code="app.mgmt.application.platform" /></label></td>
                <c:choose>
                    <c:when test="${not empty platformListing}">
                        <td>
                            <form:select path="platform" multiple="false">
                                <form:option value="<spring:message code='select.value' />" />
                                <form:option value="--------" />
                                <form:options items="${platformListing}" />
                            </form:select>
                        </td>
                    </c:when>
                    <c:otherwise>
                        <td>
                            <a href="${pageContext.request.contextPath}/ui/application-management/add-project"
                                title="<spring:message code='select.request.add.project' />"><spring:message code='select.request.add.project' /></a>
                        </td>
                    </c:otherwise>
                </c:choose>
                <td><form:errors path="platform" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtScmPath"><spring:message code="app.mgmt.application.scm.path" /></label>
                <td><form:input path="scmPath" /></td>
                <td><form:errors path="scmPath" cssClass="validationError" />
            </tr>
            <tr>
                <td><label id="txtApplicationLogsPath"><spring:message code="app.mgmt.application.applogs.path" /></label></td>
                <td><form:input path="logsPath" /></td>
                <td><form:errors path="logsPath" cssClass="validationError" /></td>
                <td><label id="txtApplicationInstallPath"><spring:message code="app.mgmt.application.install.path" /></label></td>
                <td><form:input path="installPath" /></td>
                <td><form:errors path="installPath" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtPidDirectory"><spring:message code="app.mgmt.application.pid.path" /></label></td>
                <td><form:input path="pidDirectory" /></td>
                <td><form:errors path="pidDirectory" cssClass="validationError" /></td>
                <td><label id="txtApplicationPlatform"><spring:message code="app.mgmt.application.platform" /></label></td>
                <c:choose>
                    <c:when test="${not empty platformListing}">
                        <td>
                            <form:select path="serverType" multiple="false">
                                <form:option value="<spring:message code='select.value' />" />
                                <form:option value="--------" />
                                <form:options items="${serverType}" />
                            </form:select>
                        </td>
                    </c:when>
                    <c:otherwise>
                        <td>
                            <a href="${pageContext.request.contextPath}/ui/application-management/add-project"
                                title="<spring:message code='select.request.add.project' />"><spring:message code='select.request.add.project' /></a>
                        </td>
                    </c:otherwise>
                </c:choose>
                <td><form:errors path="serverType" cssClass="validationError" /></td>
            </tr>
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
