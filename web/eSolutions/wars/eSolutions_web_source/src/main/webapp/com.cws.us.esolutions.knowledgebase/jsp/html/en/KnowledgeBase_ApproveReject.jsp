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
 * KnowledgeBase_ApproveReject.jsp
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

<div id="InfoLine"><spring:message code="kbase.approve.reject.article" arguments="${article.articleId}" /></div>
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

	    <table id="ShowArticle">
	        <tr>
	            <td><strong><em><spring:message code="kbase.article.id" /></em></strong></td>
	            <td>${articleInfo.articleId}</td>
	        </tr>
	        <tr>
	            <td><strong><em><spring:message code="kbase.article.title" /></em></strong></td>
	            <td>${articleInfo.articleTitle}</td>
	        </tr>
	        <tr>
	            <td><strong><em><spring:message code="kbase.article.symptoms" /></em></strong></td>
	            <td>${articleInfo.articleSymptoms}</td>
	        </tr>
	        <tr>
	            <td><strong><em><spring:message code="kbase.article.cause" /></em></strong></td>
	            <td>${articleInfo.articleCause}</td>
	        </tr>
	        <tr>
	            <td><strong><em><spring:message code="kbase.article.keywords" /></em></strong></td>
	            <td>${articleInfo.articleKeywords}</td>
	        </tr>
	    </table>
	    <br />
	    <strong><spring:message code="kbase.article.resolution" /></strong>
	    <br />
	    ${articleInfo.articleResolution}
    </div>

    <div id="content-left">
        <ul>
            <li><a href="javascript:history.go(-1)" title="Back"><spring:message code="theme.previous.page" /></a></li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/knowledgebase/approve-article/${article.articleId}"
                    title="<spring:message code='kbase.article.approve' />"><spring:message code='kbase.article.approve' /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/knowledgebase/reject-article/${article.articleId}"
                    title="<spring:message code='kbase.article.reject' />"><spring:message code='kbase.article.reject' /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article"
                    title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ui/common/submit-contact"
                    title="<spring:message code="theme.submit.support.request" />"><spring:message code="theme.submit.support.request" /></a>
            </li>
        </ul>
    </div>
</div>
