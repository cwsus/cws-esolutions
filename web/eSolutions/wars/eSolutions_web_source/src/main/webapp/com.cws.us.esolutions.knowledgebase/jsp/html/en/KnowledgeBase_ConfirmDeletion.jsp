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

<div id="InfoLine"><spring:message code="kbase.article.confirm.request" /></div>
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

	    <form:form id="confirmArticleDeletion" name="confirmArticleDeletion" action="${pageContext.request.contextPath}/ui/knowledgebase/delete-article" method="post">
	        <table id="confirmDeletion">
	            <tr>
	                <td><spring:message code="kbase.article.delete.confirm" arguments="${requestScope.articleId}"/>
	            </tr>
	        </table>
	        <br /><br />
	        <table id="inputItems">
	            <tr>
	                <td><input type="button" name="execute" value="<spring:message code='button.submit.text' />" id="execute" class="submit" onclick="disableButton(this);" /></td>
	                <td><input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this);" /></td>
	            </tr>
	        </table>
	    </form:form>
    </div>

    <div id="content-left">
        <ul>
            <li><a href="javascript:history.go(-1)" title="Back"><spring:message code="theme.previous.page" /></a></li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article" title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/knowledgebase/edit-article/${article.articleId}" title="<spring:message code="kbase.edit.article" />"><spring:message code="kbase.edit.article" />&nbsp; ${article.articleId}</a>
            <li>
        </ul>
    </div>
</div>
