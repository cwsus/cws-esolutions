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
 * com.cws.us.esolutions.application-management/jsp/html/en
 * AppMgmt_RetrieveFiles.jsp
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
        <a href="${pageContext.request.contextPath}/ui/application-management/deploy-application/application/${application.applicationGuid}"
            title="<spring:message code='app.mgmt.application.deploy' />"><spring:message code="app.mgmt.application.deploy" /></a> / 
    </div>

    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <c:choose>
        <c:when test="${empty platformList and empty appServerList or empty webServerList and not empty fileList}">
            <label id="locationList"><spring:message code="select.target.location" /></label>
            <br /><br />
            <label id="currentPath"><spring:message code="app.mgmt.current.location" />${currentPath}</label>
            <br /><br />
            <table id="selectLocation">
                <tr>
                    <td>
                        <c:forEach var="entry" items="${fileList}">
                            <c:choose>
                                <c:when test="${empty param.vpath}">
                                    <a href="${pageContext.request.contextPath}/ui/application-management/list-files/application/${application.applicationGuid}/platform/${platform.platformGuid}/server/${server.serverGuid}?vpath=${entry}" title="${entry}">${entry}</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/ui/application-management/list-files/application/${application.applicationGuid}/platform/${platform.platformGuid}/server/${server.serverGuid}?vpath=${currentPath}/${entry}" title="${entry}">${entry}</a>
                                </c:otherwise>
                            </c:choose>
                            <br />
                        </c:forEach>
                    </td>
                </tr>
            </table>
        </c:when>
        <c:when test="${not empty platformList}">
            <label id="platformList"><spring:message code="select.target.app.platform" /></label>
            <table id="platformList">
                <tr>
                    <td><spring:message code="svc.mgmt.platform.name" /></td>
                    <td><spring:message code="svc.mgmt.platform.region" /></td>
                </tr>
                <c:forEach var="platform" items="${platformList}">
                    <tr>
                        <td>
                            <a href="${pageContext.request.contextPath}/ui/application-management/retrieve-files/application/${application.applicationGuid}/platform/${platform.platformGuid}"
                                title="${platform.platformName}">${platform.platformName}</a>
                        </td>
                        <td>${platform.platformRegion}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:when>
        <c:otherwise>
            <spring:message code="app.mgmt.select.target.server" />

            <p id="validationError" />

            <label id="serverList"><spring:message code="select.target.app.server" /></label>
		    <table id="appServerList">
		        <tr>
		            <td><spring:message code="system.mgmt.oper.name" /></td>
		            <td><spring:message code="system.mgmt.server.type" /></td>
		            <td><spring:message code="system.mgmt.server.region" /></td>
		        </tr>
		        <c:forEach var="appserver" items="${appServerList}">
		            <tr>
		                <td>
		                    <a href="${pageContext.request.contextPath}/ui/application-management/retrieve-files/application/${application.applicationGuid}/platform/${platform.platformGuid}/server/${appserver.serverGuid}"
		                        title="${appserver.operHostName}">${appserver.operHostName}</a>
		                </td>
		                <td>${appserver.serverType}</td>
		                <td>${appserver.serverRegion}</td>
		            </tr>
		        </c:forEach>
		    </table>
            <label id="serverList"><spring:message code="select.target.web.server" /></label>
		    <table id="webServerList">
		        <tr>
		            <td><spring:message code="system.mgmt.oper.name" /></td>
		            <td><spring:message code="system.mgmt.server.type" /></td>
		            <td><spring:message code="system.mgmt.server.region" /></td>
		        </tr>
		        <c:forEach var="webserver" items="${webServerList}">
		            <tr>
		                <td>
		                    <a href="${pageContext.request.contextPath}/ui/application-management/retrieve-files/application/${application.applicationGuid}/platform/${platform.platformGuid}/server/${webserver.serverGuid}"
		                        title="${webserver.operHostName}">${webserver.operHostName}</a>
		                </td>
		                <td>${webserver.serverType}</td>
		                <td>${webserver.serverRegion}</td>
		            </tr>
		        </c:forEach>
		    </table>
        </c:otherwise>
    </c:choose>
</div>
<br /><br />
