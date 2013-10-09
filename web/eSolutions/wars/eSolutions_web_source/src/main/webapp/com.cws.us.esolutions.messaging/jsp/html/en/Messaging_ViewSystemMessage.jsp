<div class="feature">
    <table id="contactTable">
        <tr>
            <td id="txtSubmittorUserID"><spring:message code="messaging.system.message.author" /></td>
            <td><a href="mailto:${message.authorEmail}?Subject=Request for Comments: ${message.messageId}" title="${message.messageAuthor}">${message.messageAuthor}</a></td>
        </tr>
        <tr>
            <td id="txtSysMessageSubject"><spring:message code="messaging.system.message.subject" /></td>
            <td>${messageBean.messageTitle}</td>
        </tr>
        <tr>
            <td id="txtSysMessageBody"><spring:message code="messaging.system.message.body" /></td>
            <td>${message.messageText}</td>
        </tr>
    </table>
</div>
