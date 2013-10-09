<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <c:choose>
        <c:when test="${empty appServerList or empty webServerList and not empty fileList}">
            <label id="locationList"><spring:message code="select.target.location" /></label>
            <br /><br />
            <label id="currentPath"><spring:message code="app.mgmt.current.location" />${currentPath}</label>
            <br /><br />
            <table id="selectLocation">
	            <tr>
	                <td>
	                    <c:forEach var="entry" items="${fileList}">
	                        <a href="${pageContext.request.contextPath}/ui/application-management/retrieve-files/${application.applicationGuid}/server/${server.serverGuid}/currentPath/${currentPath}/path/${entry}" title="${entry}">${entry}</a>
	                        <br />
	                    </c:forEach>
	                </td>
	            </tr>
	        </table>
        </c:when>
        <c:otherwise>
            <spring:message code="app.mgmt.select.target.server" />

            <p id="validationError" />

            <table id="selectAppServer">
                <tr>
                    <td><label id="serverList"><spring:message code="select.target.app.server" /></label></td>
                    <td>
                        <c:forEach var="appserver" items="${appServerList}">
                            <a href="${pageContext.request.contextPath}/ui/application-management/retrieve-files/${application.applicationGuid}/server/${appserver.serverGuid}" title="${appserver.operHostName}">${appserver.operHostName}</a>
                        </c:forEach>
                    </td>
                </tr>
            </table>
            <table id="selectWebServer">
                <tr>
                    <td><label id="serverList"><spring:message code="select.target.web.server" /></label></td>
                    <td>
                        <c:forEach var="webserver" items="${webserver}">
                            <a href="${pageContext.request.contextPath}/ui/application-management/retrieve-files/${application.applicationGuid}/server/${webserver.serverGuid}" title="${webserver.operHostName}">${webserver.operHostName}</a>
                        </c:forEach>
                    </td>
                </tr>
            </table>
        </c:otherwise>
    </c:choose>
</div>
