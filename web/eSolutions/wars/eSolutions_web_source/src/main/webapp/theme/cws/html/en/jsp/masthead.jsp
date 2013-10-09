<div id="masthead">
    <img src="/html/esolutions/img/logo.gif" alt="eSolutions" title="eSolutions" />

    <c:if test="${not empty sessionScope.userAccount and sessionScope.userAccount.status ne 'EXPIRED'}">
        <c:if test="${sessionScope.userAccount.role ne 'USERADMIN'}">
            <div id="globalNav">
                <a href="${pageContext.request.contextPath}/ui/application-management/default" title="<spring:message code='link.globalNav.application-mgmt' />">
                    <spring:message code='link.globalNav.application-mgmt' /></a> |
                <a href="${pageContext.request.contextPath}/ui/dns-service/default" title="<spring:message code='link.globalNav.dns-services' />">
                   <spring:message code='link.globalNav.dns-services' /></a> |
                <a href="${pageContext.request.contextPath}/ui/service-management/default" title="<spring:message code='link.globalNav.service-mgmt' />">
                   <spring:message code='link.globalNav.service-mgmt' /></a> |
                <a href="${pageContext.request.contextPath}/ui/system-check/default" title="<spring:message code='link.globalNav.validate-systems' />">
                    <spring:message code='link.globalNav.validate-systems' /></a> |
                <a href="${pageContext.request.contextPath}/ui/system-management/default" title="<spring:message code='link.globalNav.system-mgmt' />">
                    <spring:message code='link.globalNav.system-mgmt' /></a>
            </div>
        </c:if>

        <div id="breadCrumb">
            <spring:message code="welcome.message" arguments="${sessionScope.userAccount.username}, ${sessionScope.userAccount.lastLogin}" />
            <br />
            <c:if test="${sessionScope.userAccount.role ne 'USERADMIN'}">
                | <a href="${pageContext.request.contextPath}/ui/user-account/default" title="<spring:message code='link.breadcrumb.account' />">
                    <spring:message code='link.breadcrumb.account' /></a> |
                <a href="${pageContext.request.contextPath}/ui/messaging/default" title="<spring:message code='link.breadcrumb.messaging' />">
                    <spring:message code='link.breadcrumb.messaging' /></a> |
                <c:if test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                    <a href="${pageContext.request.contextPath}/ui/user-management/default" title="<spring:message code='link.breadcrumb.useradmin' />">
                        <spring:message code='link.breadcrumb.useradmin' /></a> |
                </c:if>
            </c:if>
        </div>
    </c:if>
</div>
