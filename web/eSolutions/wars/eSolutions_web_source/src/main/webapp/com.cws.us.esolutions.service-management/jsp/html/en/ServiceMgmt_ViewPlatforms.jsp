<c:set var="count" value="0" scope="page" />

<div class="feature">
    <table id="platformDetail">
        <c:forEach var="platform" items="platformList">
            <c:set var="counter" value="${count + 1}" />

            <c:if test="${counter eq 4}">
                <tr>
            </c:if>

            <td><a href="${pageContext.request.contextPath}/ui/service-management/platform/${platform.platformGuid}" title="${platform.platformName}">${platform.platformName}</a></td>

            <c:if test="${counter eq 4}">
                <c:set var="counter" value="0" />
                </tr>
            </c:if>
        </c:forEach>
    </table>
</div>
