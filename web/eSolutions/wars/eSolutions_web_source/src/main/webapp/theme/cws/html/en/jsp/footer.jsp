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
 * Package: theme\cws\html\en\jsp
 * File: footer.jsp
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

<div id="footer">
    <div id="footer-content">
        <div id="footer-right">
            &copy; <a href="http://www.caspersbox.com/"><spring:message code="theme.footer.copyright" /></a><br />
            <strong><spring:message code="theme.footer.more.info" /></strong><a href="http://www.caspersbox.com/cws/ui/contact/default"
                title="<spring:message code="theme.contact.us" />" target="_blank"><spring:message code="theme.contact.us" /></a><br />
        </div>
        <div id="footer-left">
            <c:choose>
                <c:when test="${not empty fn:trim(sessionScope.userAccount)}">
                    <spring:message code="theme.welcome.message" arguments="${sessionScope.userAccount.username}, ${sessionScope.userAccount.lastLogin}" /><br />
                    <a href="${pageContext.request.contextPath}/ui/login/logout" title="<spring:message code='theme.navbar.logoff' />">
                        <spring:message code='theme.navbar.logoff' /></a> |
                    <a href="${pageContext.request.contextPath}/ui/user-account/default" title="<spring:message code='theme.navbar.myaccount' />">
                        <spring:message code="theme.navbar.myaccount" /></a> |
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/ui/login/default" title="<spring:message code='theme.navbar.login' />">
                        <spring:message code='theme.navbar.login' /></a> | 
                </c:otherwise>
            </c:choose>
            <script>
                if ((window.location.pathname.search('login') == -1) && (window.location.pathname.search('common/default') == -1))
                {
                    document.write(' | <a href="${pageContext.request.contextPath}/ui/common/default"><spring:message code="theme.navbar.home" /></a>');
                }
            </script>
            <%-- we want to do intl here but we don't have any nls files for it. commenting for now --%>
            <%--
            <br />
            <a href="?lang=en"><img class="img-flag" src="/static/img/img_flag_england.gif" title="<spring:message code="theme.top-nav.switch.en" />" alt=""/></a> | 
            <a href="?lang=es"><img class="img-flag" src="/static/img/img_flag_spain.gif" title="<spring:message code="theme.top-nav.switch.es" />" alt=""/></a> | 
            <a href="?lang=fr"><img class="img-flag" src="/static/img/img_flag_france.gif" title="<spring:message code="theme.top-nav.switch.fr" />" alt=""/></a>
            --%>
        </div>
    </div>
</div>
