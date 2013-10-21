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
 * KnowledgeBase_ShowPendingApprovals.jsp
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
        <a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article"
            title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a>
    </div>

    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <br /><br />

    <spring:message code="kbase.approve-article.select-article" />
    <br /><br />
    <table id="siteSearch">
        <c:forEach var="entry" items="${articleList}">
            <tr>
                <td><a href="${pageContext.request.contextPath}/ui/knowledgebase/article/${entry.articleId}" title="${entry.articleId}">${entry.articleId}</a></td>
                <td><a href="${pageContext.request.contextPath}/ui/knowledgebase/article/${entry.articleId}" title="${entry.title}">${entry.title}</a></td>
            </tr>
        </c:forEach>
    </table>
</div>
<br /><br />
