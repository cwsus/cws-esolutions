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

<div class="feature">
    <div id="breadcrumb" class="lpstartover">
        <a href="javascript:history.go(-1)" title="Back"><spring:message code="kbase.view-article.return" /></a> /
        <a href="${pageContext.request.contextPath}/ui/knowledgebase/approve-article/${article.articleId}" title="<spring:message code='kbase.approve-article.approve-link' />"><spring:message code='kbase.approve-article.approve-link' /></a> /
        <a href="${pageContext.request.contextPath}/ui/knowledgebase/reject-article/${article.articleId}" title="<spring:message code='kbase.approve-article.reject-link' />"><spring:message code='kbase.approve-article.reject-link' /></a> /
        <a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article" title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a>
    </div>

    <table id="ShowArticle">
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-id" /></em></strong></td>
            <td>${articleInfo.articleId}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-title" /></em></strong></td>
            <td>${articleInfo.articleTitle}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-symptoms" /></em></strong></td>
            <td>${articleInfo.articleSymptoms}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-cause" /></em></strong></td>
            <td>${articleInfo.articleCause}</td>
        </tr>
        <tr>
            <td><strong><em><spring:message code="kbase.view-article.article-keywords" /></em></strong></td>
            <td>${articleInfo.articleKeywords}</td>
        </tr>
    </table>
    <br />
    <strong><spring:message code="kbase.view-article.article-resolution" /></strong>
    <br />
    ${articleInfo.articleResolution}
</div>
<br /><br />
