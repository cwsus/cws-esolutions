<div class="feature">
    <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
        <div id="breadcrumb" class="lpstartover">
            <a href="${pageContext.request.contextPath}/ui/messaging/add-message"
                title="spring:message code='messaging.create.system.message' />"><spring:message code="messaging.create.system.message" /></a>
        </div>
    </c:if>

    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <c:choose>
        <c:when test="${not empty messageList}">
            <spring:message code="messaging.system.messages.list" />

	        <c:forEach var="message" items="${messageList}">
	            <div id="svcmessage">
	                <h3>
	                    <a href="${pageContext.request.contextPath}/ui/messaging/edit-message/${message.messageId}"
	                        title="<spring:message code='messaging.view.system.message.edit' />">${message.messageTitle} - ${message.messageId}</a>
	                </h3>
	                <div class="feature">
	                    ${message.messageText}
	                </div>
				    <table class="kbauth">
				        <tr>
				            <td id="top" align="center" valign="middle"><strong><spring:message code="messaging.system.message.author" /></strong></td>
				            <td id="top" align="center" valign="middle"><strong><spring:message code="messaging.system.message.submit.date" /></strong></td>
				            <td id="top" align="center" valign="middle"><strong><spring:message code="messaging.system.message.expiry.date" /></strong></td>
				        </tr>
				        <tr>
				            <td align="center" valign="middle">
				                <em><a href="mailto:${message.messageAuthor}?subject=Request for Comments: ${message.messageId}"
				                    title="Request for Comments: ${message.messageId}">${message.messageId}</a></em>
				            </td>
				            <td align="center" valign="middle"><em>${message.submitDate}</em></td>
				            <td align="center" valign="middle"><em>${message.expiryDate}</em></td>
				        </tr>
				    </table>
	            </div>
	        </c:forEach>
	    </c:when>
	    <c:otherwise>
	        <spring:message code="messaging.no.system.messages" />
	    </c:otherwise>
	</c:choose>
</div>
