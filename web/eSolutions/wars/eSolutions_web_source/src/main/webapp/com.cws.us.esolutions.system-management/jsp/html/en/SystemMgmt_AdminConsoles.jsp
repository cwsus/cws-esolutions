<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <table id="consoles">
        <tr>
            <c:forEach var="dmgr" items="${serverList}">
                <td><a href="${dmgr.mgrUrl}" title="${dmgr.operHostName}">${dmgr.operHostName}</a></td>
            </c:forEach>
        </tr>
    </table>
</div>
