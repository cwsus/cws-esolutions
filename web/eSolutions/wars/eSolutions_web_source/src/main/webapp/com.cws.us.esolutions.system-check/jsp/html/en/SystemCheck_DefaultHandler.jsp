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
                <a href="${pageContext.request.contextPath}/ui/system-check/telnet"
                    title="<spring:message code='select.request.telnet' />"><spring:message code="select.request.type.telnet" /></a>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}/ui/system-check/remote-date"
                    title="<spring:message code='select.request.type.date' />"><spring:message code="select.request.type.date" /></a>
            </td>
        </tr>
    </table>
</div>
