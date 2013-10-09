<div class="feature">
    <c:choose>
        <c:when test="${not empty messageList}">
            <spring:message code="messaging.system.messages.list" />

            <c:forEach var="message" items="${messageList}">
                <div id="svcmessage">
                    <h3>${message.messageSubject}</h3>
                    <div class="feature">
                        ${message.messageBody}
                    </div>

                    <div class="kbauth">
                        <table id="svcMessageAuthor">
                            <tr>
                                <td><spring:message code="messaging.view.system.message.author" />&nbsp; ${message.messageAuthor}</td>
                                <td>
                                    <spring:message code="messaging.view.system.message.email" />&nbsp; <a href="mailto:${message.messageContact}?subject=<spring:message code='messaging.comments.subject' /> ${message.messageId}"
                                        title="<spring:message code="messaging.view.system.message.email" />">${message.messageContact}</a>
                                </td>
                                <td><spring:message code="messaging.view.system.message.timestamp" /> &nbsp; ${message.messageTimestamp}</td>
                            </tr>
                        </table>
                    </div>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <spring:message code="messaging.system.messages.no.messages" />
        </c:otherwise>
    </c:choose>
</div>
<br /><br />
