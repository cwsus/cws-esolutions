<%--
/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
 *
 * eSolutions_web_source
 * com.cws.us.esolutions.useraccount/jsp/html/en
 * UserAccount_ChangeSecurity.jsp
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author kh05451
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 16, 2013 11:53:26 AM
 *     Created.
 */
--%>
<div id="sidebar">
    <h1><spring:message code="user.account.update.security" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/email" title="<spring:message code='user.account.change.email' />"><spring:message code="user.account.change.email" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/contact" title="<spring:message code='user.account.change.contact' />"><spring:message code="user.account.change.contact" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/password" title="<spring:message code='user.account.change.password' />"><spring:message code="user.account.change.password" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/regenerate-keys" title="<spring:message code='user.account.change.keys' />"><spring:message code="user.account.change.keys" /></a></li>
    </ul>
</div>

<div id="main">
    <div id="error"></div>

    <c:if test="${not empty fn:trim(messageResponse)}">
        <p id="info">${messageResponse}</p>
    </c:if>
    <c:if test="${not empty fn:trim(errorResponse)}">
        <p id="error">${errorResponse}</p>
    </c:if>
    <c:if test="${not empty fn:trim(responseMessage)}">
        <p id="info"><spring:message code="${responseMessage}" /></p>
    </c:if>
    <c:if test="${not empty fn:trim(errorMessage)}">
        <p id="error"><spring:message code="${errorMessage}" /></p>
    </c:if>
    <c:if test="${not empty fn:trim(param.responseMessage)}">
        <p id="info"><spring:message code="${param.responseMessage}" /></p>
    </c:if>
    <c:if test="${not empty fn:trim(param.errorMessage)}">
        <p id="error"><spring:message code="${param.errorMessage}" /></p>
    </c:if>

    <h1><spring:message code="user.account.update.security" /></h1>
    <p>
        <form:form name="submitSecurityInformationChange" id="submitSecurityInformationChange" action="${pageContext.request.contextPath}/ui/user-account/security" method="post" autocomplete="off">
            <p>
                <label id="txtQuestionOne"><spring:message code="user.account.update.security.question" /></label>
                <form:select path="secQuestionOne">
                    <option><spring:message code="theme.option.select" /></option>
                    <option><spring:message code="theme.option.spacer" /></option>
                    <form:options items="${questionList}" />
                </form:select>
                <form:errors path="secQuestionOne" cssClass="error" />
                <label id="txtAnswerOne"><spring:message code="user.account.update.security.answer" /></label>
                <form:password path="secAnswerOne" />
                <form:errors path="secAnswerOne" cssClass="error" />
                <label id="txtQuestionTwo"><spring:message code="user.account.update.security.question" /></label>
                <form:select path="secQuestionTwo">
                    <option><spring:message code="theme.option.select" /></option>
                    <option><spring:message code="theme.option.spacer" /></option>
                    <form:options items="${questionList}" />
                </form:select>
                <form:errors path="secQuestionTwo" cssClass="error" />            
                <label id="txtAnswerTwo"><spring:message code="user.account.update.security.answer" /></label>
                <form:password path="secAnswerTwo" />
                <form:errors path="secAnswerTwo" cssClass="error" />
                <label id="txtPassword"><spring:message code="user.account.update.password.current" /></label>
                <form:password path="currentPassword" />
                <form:errors path="currentPassword" cssClass="error" />
                <br /><br />
                <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            </p>
        </form:form>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
