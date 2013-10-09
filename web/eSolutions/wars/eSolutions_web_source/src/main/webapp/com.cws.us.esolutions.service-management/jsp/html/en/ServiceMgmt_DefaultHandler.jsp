<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="select.request.type" />
    <br /><br />
    <table id="selectRequest">
        <tr>
            <td>
                <a href="${pageContext.request.contextPath}/ui/service-management/add-platform"
                    title="<spring:message code='select.request.add.platform' />"><spring:message code="select.request.add.platform" /></a>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}/ui/service-management/add-project"
                    title="<spring:message code='select.request.add.project' />"><spring:message code="select.request.add.project" /></a>
            </td>
        </tr>
    </table>
</div>
