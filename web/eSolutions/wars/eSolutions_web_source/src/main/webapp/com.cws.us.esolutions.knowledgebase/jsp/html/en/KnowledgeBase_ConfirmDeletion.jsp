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
 * com.cws.us.esolutions.knowledgebase/jsp/html/en
 * KnowledgeBase_ConfirmDeletion.jsp
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

<div class="feature">
    <div id="breadcrumb" class="lpstartover">
        <a href="javascript:history.go(-1)" title="Back"><spring:message code="kbase.view-article.return" /></a> /
        <c:if test="${param.command ne 'cmd_CreateKnowledgeBaseArticle'}">
            <a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article" title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a> /
            <a href="${pageContext.request.contextPath}/ui/knowledgebase/edit-article/${article.articleId}" title="<spring:message code="kbase.edit.article" />"><spring:message code="kbase.edit.article" />&nbsp; ${article.articleId}</a>
            <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                / <a href="#" title="<spring:message code="kbase.delete.article" />&nbsp; ${article.articleId}" onclick="deleteArticle('${article.articleId}')"><spring:message code="kbase.delete.article" />&nbsp; ${article.articleId}</a>
                <c:if test="${param.command eq 'cmd_ApproveKnowledgeBaseArticle'}">
                    / <a href="${pageContext.request.contextPath}/ui/knowledgebase/approve-article/${article.articleId}"
                        title="<spring:message code='kbase.approve-article.approve-link' />"><spring:message code='kbase.approve-article.approve-link' /></a>
                    / <a href="${pageContext.request.contextPath}/ui/knowledgebase/reject-article/${article.articleId}"
                        title="<spring:message code='kbase.approve-article.reject-link' />"><spring:message code='kbase.approve-article.reject-link' /></a>
                </c:if>
            </c:if>
        </c:if>
    </div>

    <c:if test="${not empty messageResponse}">
        <p id="info">${messageResponse}</p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>
    <c:if test="${not empty responseMessage}">
        <p id="info"><spring:message code="${responseMessage}" /></p>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <p id="error"><spring:message code="${errorMessage}" /></p>
    </c:if>

    <form:form id="confirmArticleDeletion" name="confirmArticleDeletion" action="${pageContext.request.contextPath}/ui/knowledgebase/delete-article" method="post">
        <table id="confirmDeletion">
            <tr>
                <td><spring:message code="kbase.delete-article.confirm" arguments="${requestScope.articleId}"/>
            </tr>
        </table>
        <br /><br />
        <table id="inputItems">
            <tr>
                <td><input type="button" name="execute" value="<spring:message code='button.submit.text' />" id="execute" class="submit" onclick="disableButton(this);" /></td>
                <td><input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this);" /></td>
            </tr>
        </table>
    </form:form>
</div>
<br /><br />