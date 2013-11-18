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
 * KnowledgeBase_CreateArticle.jsp
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
<jsp:useBean id="submissionDate" class="java.util.Date" scope="page" />
<div id="InfoLine"><spring:message code="kbase.article.create.banner" arguments="${command.articleId}" /></div>
<div id="content">
    <div id="content-right">
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

        <span id="validationError"></span>

	    <form:form id="submitNewArticle" name="submitNewArticle" action="${pageContext.request.contextPath}/ui/knowledgebase/validate-article" method="post">
	        <form:hidden path="author" value="${sessionScope.userAccount.username}" />
	        <form:hidden path="authorEmail" value="${sessionScope.userAccount.emailAddr}" />

	        <table id="ShowArticle">
	            <tr>
	                <td id="txtArticleId"><strong><em><spring:message code="kbase.article.id" /></em></strong></td>
	                <td><form:input path="articleId" readonly="true" /></td>
	            </tr>
	            <tr>
	                <td id="txtArticleTitle"><strong><em><spring:message code="kbase.article.title" /></em></strong></td>
	                <td><form:input path="title" /></td>
	                <td><form:errors path="title" cssClass="validationError" /></td>
	            </tr>
	            <tr>
	                <td id="txtArticleSymptoms"><strong><em><spring:message code="kbase.article.symptoms" /></em></strong></td>
	                <td><form:input path="symptoms" /></td>
	                <td><form:errors path="symptoms" cssClass="validationError" /></td>
	            </tr>
	            <tr>
	                <td id="txtArticleCause"><strong><em><spring:message code="kbase.article.cause" /></em></strong></td>
	                <td><form:input path="cause" /></td>
	                <td><form:errors path="cause" cssClass="validationError" /></td>
	            </tr>
	            <tr>
	                <td id="txtArticleKeywords"><strong><em><spring:message code="kbase.article.keywords" /></em></strong></td>
	                <td><form:input path="keywords" /></td>
	                <td><form:errors path="keywords" cssClass="validationError" /></td>
	            </tr>
	        </table>
	        <br />
	        <label id="txtArticleResolution"><strong><spring:message code="kbase.article.resolution" /></strong></label>
	        <br />
	        <form:textarea path="resolution" cols="65" rows="10" />
	        <form:errors path="resolution" />
	        <br /><br />
	        <table class="kbauth">
	            <tr>
	                <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.article.author" /></strong></td>
	                <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.article.created" /></strong></td>
	                <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.article.modifier" /></strong></td>
	                <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.article.modified" /></strong></td>
	                <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.article.approver" /></strong></td>
	                <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.article.approved" /></strong></td>
	            </tr>
	            <tr>
	                <td align="center" valign="middle"><em>${sessionScope.userAccount.username}</em></td>
	                <td align="center" valign="middle"><em><fmt:formatDate value="${submissionDate}" pattern="${dateFormat}" /></em></td>
	                <td align="center" valign="middle"><em>${modifiedBy}</em></td>
	                <td align="center" valign="middle"><em>${modifiedOn}</em></td>
	                <td align="center" valign="middle"><em>${reviewedBy}</em></td>
	                <td align="center" valign="middle"><em>${reviewedOn}</em></td>
	            </tr>
	        </table>

	        <table id="inputItems">
	            <tr>
	                <td>
	                    <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
	                </td>
	                <td>
	                    <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
	                </td>
	                <td>
	                    <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
	                </td>
	            </tr>
	        </table>
	    </form:form>
    </div>

    <div id="content-left">
        <ul>
            <li><a href="javascript:history.go(-1)" title="Back"><spring:message code="theme.previous.page" /></a></li>
            <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                <li>
                    <a href="${pageContext.request.contextPath}/ui/knowledgebase/show-approvals"
                        title="<spring:message code='kbase.list.pending.approvals' />"><spring:message code='kbase.list.pending.approvals' /></a>
                </li>
            </c:if>
        </ul>
    </div>
</div>
