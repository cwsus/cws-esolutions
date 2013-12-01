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

<div id="InfoLine"><spring:message code="kbase.article.update.data" arguments="${article.articleId}" /></div>
<div id="content">
    <div id="content-right">
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

	    <form:form id="submitArticleUpdates" name="submitArticleUpdates" action="${pageContext.request.contextPath}/ui/knowledgebase/validate-article" method="post" commandName="article">
	       <form:hidden path="articleId" />

	       <table id="ShowArticle">
	           <tr>
	               <td id="txtArticleId"><strong><em><spring:message code="kbase.article.id" /></em></strong></td>
	               <td>${article.articleId}</td>
	           </tr>
	           <tr>
	               <td id="txtArticleTitle"><strong><em><spring:message code="kbase.article.title" /></em></strong></td>
	               <td><form:input path="title" id="title" name="title" value="${article.title}" /></td>
	               <td><form:errors path="title" cssClass="validationError" /></td>
	           </tr>
	           <tr>
	               <td id="txtArticleSymptoms"><strong><em><spring:message code="kbase.article.symptoms" /></em></strong></td>
	               <td><form:input path="symptoms" type="text" id="symptoms" name="symptoms" value="${article.symptoms}" /></td>
	               <td><form:errors path="symptoms" cssClass="validationError" /></td>
	           </tr>
	           <tr>
	               <td id="txtArticleCause"><strong><em><spring:message code="kbase.article.cause" /></em></strong></td>
	               <td><form:input path="cause" type="text" id="cause" name="cause" value="${article.cause}" /></td>
	               <td><form:errors path="cause" cssClass="validationError" /></td>
	           </tr>
	           <tr>
	               <td id="txtArticleKeywords"><strong><em><spring:message code="kbase.create-article.keywords" /></em></strong></td>
	               <td><form:input path="keywords" type="text" id="keywords" name="keywords" value="${article.keywords}" /></td>
	               <td><form:errors path="keywords" cssClass="validationError" /></td>
	           </tr>
	       </table>
            <br />
	        <label id="txtArticleResolution"><strong><spring:message code="kbase.article.resolution" /></strong></label>
	        <br />
	        <c:if test="${requestScope.isHTMLEnabled eq 'true'}">
	            <spring:message code="kbase.edit.article.html.enabled" />
	        </c:if>
	        <br />
	        <textarea id="resolution" name="resolution" cols="90" rows="10">${article.resolution}</textarea>

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
            <li>
		        <a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article"
		            title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a>
		    </li>
		    <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
		        <li>
	                <a href="${pageContext.request.contextPath}/ui/knowledgebase/show-approvals"
	                    title="<spring:message code='kbase.list.pending.approvals' />"><spring:message code='kbase.list.pending.approvals' /></a>
	            </li>
	        </c:if>
	        <li>
                <a href="${pageContext.request.contextPath}/ui/common/submit-contact"
                    title="<spring:message code="theme.submit.support.request" />"><spring:message code="theme.submit.support.request" /></a>
            </li>
        </ul>
    </div>
</div>
