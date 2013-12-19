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
 * com.cws.us.esolutions.system-management/jsp/html/en
 * SystemManagement_DefaultHandler.jsp
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

<script>
<!--
    function validateForm(theForm)
    {
        if (theForm.searchTerms.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Search terms must be provided.';
            document.getElementById('txtSearchTerms').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('searchTerms').focus();
        }
        else
        {
            theForm.submit();
        }
    }
//-->
</script>

<div id="sidebar">
    <h1><spring:message code="kbase.header" /></h1>
    <ul>
        <c:if test="${not empty fn:trim(sessionScope.userAccount)}">
            <li><a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article" title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a></li>
            <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
                <li><a href="${pageContext.request.contextPath}/ui/knowledgebase/show-approvals" title="<spring:message code='kbase.list.pending.approvals' />"><spring:message code='kbase.list.pending.approvals' /></a></li>
            </c:if>
        </c:if>
        <li><a href="${pageContext.request.contextPath}/ui/common/submit-contact" title="<spring:message code="theme.submit.support.request" />"><spring:message code="theme.submit.support.request" /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="theme.search.header" /></h1>

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

    <p>
        <form:form id="searchRequest" name="searchRequest" action="${pageContext.request.contextPath}/ui/knowledgebase/search" method="post">
            <label id="txtSearchTerms"><spring:message code="theme.search.terms" /><br /></label>
            <form:input path="searchTerms" />
            <form:errors path="searchTerms" cssClass="error" />
            <br /><br />
            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
            <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
        </form:form>

        <c:if test="${not empty searchResults}">
            <h1><spring:message code="theme.search.results" /></h1>
            <br /><br />
            <table id="searchResults">
                <c:forEach var="result" items="${searchResults}">
                    <tr>
                        <td><a href="${pageContext.request.contextPath}/ui/knowledgebase/article/${result.path}" title="${result.title}">${result.title}</a></td>
                    </tr>
                </c:forEach>
            </table>

	        <c:if test="${pages gt 1}">
	            <br />
	            <hr />
	            <br />
	            <table>
	                <tr>
	                    <c:forEach begin="1" end="${pages}" var="i">
	                        <c:choose>
	                            <c:when test="${page eq i}">
	                                <td>${i}</td>
	                            </c:when>
	                            <c:otherwise>
	                                <td>
	                                    <a href="${pageContext.request.contextPath}/knowledgebase/search/terms/${searchTerms}/page/${i}" title="{i}">${i}</a>
	                                </td>
	                            </c:otherwise>
	                        </c:choose>
	                    </c:forEach>
	                </tr>
	            </table>
	        </c:if>
        </c:if>

        <c:if test="${not empty topArticles}">
            <h1><spring:message code="kbase.top.articles" /></h1>
            <br /><br />
            <table id="topArticles">
                <c:forEach var="entry" items="${topArticles}">
                    <tr>
                        <td><a href="${pageContext.request.contextPath}/ui/knowledgebase/article/${entry.articleId}" title="${entry.articleId}">${entry.articleId}</a></td>
                        <td><a href="${pageContext.request.contextPath}/ui/knowledgebase/article/${entry.articleId}" title="${entry.title}">${entry.title}</a></td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
