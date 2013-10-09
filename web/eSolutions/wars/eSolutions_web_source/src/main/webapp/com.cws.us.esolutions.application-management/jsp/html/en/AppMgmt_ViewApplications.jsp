<div class="feature">
    <table id="appSearchResults">
        <tr>
            <td><spring:message code="app.mgmt.application.name" /></td>
            <td><spring:message code="app.mgmt.application.project" /></td>
        </tr>
        <c:forEach var="application" items="${applicationList}">
	        <tr>
	            <td>
	                <a href="${pageContext.request.contextPath}/ui/application-management/application/${application.applicationGuid}"
	                    title="${application.applicationName}">${application.applicationName}</a>
	            </td>
	            <td>
	                <a href="${pageContext.request.contextPath}/ui/application-management/application/${application.applicationGuid}"
	                    title="${application.applicationProject.projectCode}">${application.applicationProject.projectCode}</a>
	            </td>
	        </tr>
        </c:forEach>
    </table>
</div>
