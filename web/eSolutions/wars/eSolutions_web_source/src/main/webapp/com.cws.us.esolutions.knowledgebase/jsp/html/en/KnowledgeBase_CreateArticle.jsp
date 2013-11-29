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
 * com.cws.us.esolutions.application-management/jsp/html/en
 * AppMgmt_ViewFile.jsp
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
    <h1><spring:message code="kbase.header" /></h1>
    <ul>
        <li><a href="javascript:history.go(-1)" title="Back"><spring:message code="theme.previous.page" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/knowledgebase/default"
            title="<spring:message code='kbase.default' />">
            <spring:message code="kbase.default" /></a></li>
        <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
            <li>
                <a href="${pageContext.request.contextPath}/ui/knowledgebase/show-approvals"
                    title="<spring:message code='kbase.list.pending.approvals' />"><spring:message code='kbase.list.pending.approvals' /></a>
            </li>
        </c:if>
    </ul>
</div>

<div id="main">
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

    <span id="validationError"></span>
    <h1><strong>Create Article - ${command.articleId}</strong></h1>

    <form:form id="submitNewArticle" name="submitNewArticle" action="${pageContext.request.contextPath}/ui/knowledgebase/validate-article" method="post">
        <form:hidden path="articleId" value="${command.articleId" />

        <label id="txtArticleTitle"><spring:message code="kbase.article.title" /></label>
        <form:input path="title" />
        <form:errors path="title" cssClass="validationError" />
        <label id="txtArticleSymptoms"><spring:message code="kbase.article.symptoms" /></label>
        <form:input path="symptoms" />
        <form:errors path="symptoms" cssClass="validationError" />
        <label id="txtArticleCause"><spring:message code="kbase.article.cause" /></label>
        <form:input path="cause" />
        <form:errors path="cause" cssClass="validationError" />
        <label id="txtArticleKeywords"><spring:message code="kbase.article.keywords" /></label>
        <form:input path="keywords" />
        <form:errors path="keywords" cssClass="validationError" />
        <label id="txtArticleResolution"><spring:message code="kbase.article.resolution" /></label>
        <br />
        <form:textarea path="resolution" cols="65" rows="10" />
        <form:errors path="resolution" />
        <br />
        <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
        <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
        <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
    </form:form>
</div>
