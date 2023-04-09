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
 * Package: com.cws.esolutions.web.application-management\jsp\html\en
 * File: AppMgmt_AddApplication.jsp
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

<div id="homecontent">
    <div class="wrapper">
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

        <h2><spring:message code="datacenter.mgmt.list.datacenters" /></h2>
        <ul id="latestnews">
            <li>
                <table id="viewDatacenterList">
                    <tr>
                        <td><label id="datacenterName"><spring:message code="datacenter.mgmt.datacenter.name" /></label>${datacenter.name}</td>
                        <td><label id="datacenterStatus"><spring:message code="datacenter.mgmt.datacenter.status" /></label>${datacenter.status}</td>
                    </tr>
                    <tr>
                        <td><label id="datacenterDesc"><spring:message code="datacenter.mgmt.datacenter.description" /></label>${datacenter.description}</td>
                    </tr>
                </table>
            </li>
        </ul>
    </div>
</div>

<div id="container">
    <div class="wrapper">
        <div id="holder">
            <h1><spring:message code="datacenter.mgmt.header" /></h1>
            <ul>
                <li><a href="${pageContext.request.contextPath}/ui/datacenter-management/list-datacenters" title="<spring:message code='datacenter.mgmt.list.datacenters' />"><spring:message code="datacenter.mgmt.list.datacenters" /></a></li>
                <li><a href="${pageContext.request.contextPath}/ui/datacenter-management/add-datacenter" title="<spring:message code='datacenter.mgmt.add.datacenter' />"><spring:message code="datacenter.mgmt.add.datacenter" /></a></li>
            </ul>
        </div>
        <br class="clear" />
    </div>
</div>
