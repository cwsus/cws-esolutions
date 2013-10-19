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
 * KnowledgeBase_EditArticle.jsp
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
        <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
            <a href="${pageContext.request.contextPath}/ui/knowledgebase/show-approvals"
                title="<spring:message code='kbase.list.pending.approvals' />"><spring:message code='kbase.list.pending.approvals' /></a> /
        </c:if>
        <a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article"
            title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a>
    </div>

    <c:choose>
        <c:when test="${not empty article}">
            <c:choose>
                <c:when test="${requestScope['isEditSuccess'] eq 'true'}">
                    <spring:message code="kbase.edit-article.edit-success" />
                </c:when>
                <c:when test="${requestScope['isEditSuccess'] eq 'false'}">
                    <spring:message code="kbase.edit-article.edit-failure" />
                </c:when>
                <c:otherwise>
                    <spring:message code="kbase.edit-article.begin-updates" />
                </c:otherwise>
            </c:choose>

            <p id="validationError" />

            <form:form id="submitArticleUpdates" name="submitArticleUpdates" action="${pageContext.request.contextPath}/ui/knowledgebase/validate-article" method="post" commandName="article">
                <form:hidden path="articleId" />

                <table id="ShowArticle">
                    <tr>
                        <td id="txtArticleId"><strong><em><spring:message code="kbase.view-article.article-id" /></em></strong></td>
                        <td>${article.articleId}</td>
                    </tr>
                    <tr>
                        <td id="txtArticleTitle"><strong><em><spring:message code="kbase.view-article.article-title" /></em></strong></td>
                        <td><form:input path="title" id="title" name="title" value="${article.title}" /></td>
                        <form:errors path="title" cssClass="validationError" />
                    </tr>
                    <tr>
                        <td id="txtArticleSymptoms"><strong><em><spring:message code="kbase.view-article.article-symptoms" /></em></strong></td>
                        <td><form:input path="symptoms" type="text" id="symptoms" name="symptoms" value="${article.symptoms}" /></td>
                        <form:errors path="symptoms" cssClass="validationError" />
                    </tr>
                    <tr>
                        <td id="txtArticleCause"><strong><em><spring:message code="kbase.view-article.article-cause" /></em></strong></td>
                        <td><form:input path="cause" type="text" id="cause" name="cause" value="${article.cause}" /></td>
                        <form:errors path="cause" cssClass="validationError" />
                    </tr>
                    <tr>
                        <td id="txtArticleKeywords"><strong><em><spring:message code="kbase.create-article.article-keywords" /></em></strong></td>
                        <td><form:input path="keywords" type="text" id="keywords" name="keywords" value="${article.keywords}" /></td>
                        <form:errors path="keywords" cssClass="validationError" />
                    </tr>
                </table>
                <br />
                <label id="txtArticleResolution"><strong><spring:message code="kbase.view-article.article-resolution" /></strong></label>
                <br />
                <c:if test="${requestScope.isHTMLEnabled eq 'true'}">
                    <spring:message code="kbase.edit.article.html.enabled" />
                </c:if>
                <br />
                <textarea id="resolution" name="resolution" cols="90" rows="10">${article.resolution}</textarea>
                <br /><br />
                <table id="inputItems">
                    <tr>
                        <td>
                            <input type="button" name="execute" value="<spring:message code='button.execute.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                        </td>
                        <td>
                            <input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                        </td>
                        <td>
                            <input type="button" name="reset" value="<spring:message code='button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                        </td>
                    </tr>
                </table>
            </form:form>
        </c:when>
        <c:otherwise>
            <strong><spring:message code="kbase.view-article.article-not-found" /></strong>
        </c:otherwise>
    </c:choose>
</div>
