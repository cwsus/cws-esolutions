<%--
/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
--%>
<%--
/**
 * Project: eSolutions_web_source
 * Package: com.cws.esolutions.web.dnsservice\jsp\html\en
 * File: DNSService_ServiceLookup.jsp
 *
 * @author cws-khuntly
 * @version 1.0
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
--%>

<script>
<!--
    function validateForm(theForm)
    {
        if (theForm.searchTerms.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'You must provide a hostname or IP address to perform a query against.';
            document.getElementById('txtSearchTerms').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('searchTerms').focus();

            return;
        }

        theForm.submit();
    }
//-->
</script>

<div id="homecontent">
    <div class="wrapper">
        <div id="error"></div>
        <div id="validationError" style="color: #FF0000"></div>
    
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

        <form:form id="searchUserAccounts" name="searchUserAccounts" action="${pageContext.request.contextPath}/ui/user-management/search" method="post">
            <label id="txtSearchTerms"><spring:message code="user.mgmt.search.terms" /><br /></label>
            <form:input path="searchTerms" id="searchTerms" />
            <form:errors path="searchTerms" cssClass="error" />

            <br /><br />
            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
        </form:form>

        <br class="clear" />
        <br class="clear" />

        <c:if test="${not empty fn:trim(requestScope.searchResults)}">
            <h1><spring:message code="theme.search.results" /></h1>
            <table id="userSearchResults">
                <tr>
                    <td><spring:message code="user.mgmt.user.name" /></td>
                    <td><spring:message code="user.mgmt.display.name" /></td>
                </tr>
                <c:forEach var="userResult" items="${requestScope.searchResults}">
                    <tr>
                        <td>
                            <a href="${pageContext.request.contextPath}/ui/user-management/view/account/${userResult.guid}"
                                title="${userResult.username}">${userResult.username}</a>
                        </td>
                        <td>${userResult.displayName}</td>
                    </tr>
                </c:forEach>
            </table>

            <c:if test="${pages gt 1}">
                <br />
                <hr />
                <table>
                    <tr>
                        <c:forEach begin="1" end="${pages}" var="i">
                            <c:choose>
                                <c:when test="${page eq i}">
                                    <td>${i}</td>
                                </c:when>
                                <c:otherwise>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/user-management/search/terms/${searchTerms}/type/${searchType}/page/${i}" title="{i}">${i}</a>
                                    </td>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </tr>
                </table>
            </c:if>
        </c:if>
    </div>
</div>

<div id="container">
    <div class="wrapper">
        <div id="content">
            <h1><spring:message code="user.mgmt.header" /></h1>
            <ul>
                <li><a href="${pageContext.request.contextPath}/ui/user-management/add-user" title="<spring:message code='user.mgmt.create.user' />"><spring:message code="user.mgmt.create.user" /></a></li>
            </ul>
        </div>
        <br class="clear" />
    </div>
</div>
