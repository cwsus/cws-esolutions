<div id="navBar">
    <div id="sectionLinks">
        <ul>
            <c:choose>
                <c:when test="${not empty sessionScope.userAccount}">
                    <li>
                        <a href="${pageContext.request.contextPath}/ui/home/default" title="<spring:message code='link.sectionLinks.home' />">
                            <spring:message code='link.sectionLinks.home' /></a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/ui/login/logout"
                            title="<spring:message code='link.sectionLinks.logoff' />"><spring:message code='link.sectionLinks.logoff' /></a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li>
                        <a href="${pageContext.request.contextPath}/ui/login/login"
                            title="<spring:message code='link.sectionLinks.login' />"><spring:message code='link.sectionLinks.login' /></a>
                    </li>
                </c:otherwise>
            </c:choose>
            <li>
                <a href="${pageContext.request.contextPath}/ui/knowledgebase/default"
                    title="<spring:message code='link.sectionLinks.help' />"><spring:message code='link.sectionLinks.help' /></a>
            </li>
        </ul>
    </div>
</div>
