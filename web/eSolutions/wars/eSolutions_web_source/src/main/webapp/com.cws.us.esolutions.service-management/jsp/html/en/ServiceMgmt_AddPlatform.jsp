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
 * com.cws.us.esolutions.service-management/jsp/html/en
 * ServiceMgmt_AddPlatform.jsp
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
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="svc.mgmt.add.platform" />

    <p id="validationError" />

    <form:form id="createNewPlatform" name="createNewPlatform" action="${pageContext.request.contextPath}/ui/service-management/submit-platform" method="post">
        <table id="applicationDetail">
            <tr>
                <td><label id="txtPlatformName"><spring:message code="svc.mgmt.platform.name" /></label></td>
                <td><form:input path="platformName" /></td>
                <td><form:errors path="platformName" cssClass="validationError" /></td>
                <td><label id="txtPlatformStatus"><spring:message code="svc.mgmt.platform.status" /></label></td>
                <td>
                    <form:select path="status" multiple="false">
                        <form:options items="${status}" />
                    </form:select>
                </td>
                <td><form:errors path="status" cssClass="validationError" /></td>
                <td><label id="txtPlatformRegion"><spring:message code="svc.mgmt.platform.region" /></label></td>
                <td><form:input path="platformRegion" readonly="true" /></td>
            </tr>
            <tr>
                <td><label id="txtPlatformDmgr"><spring:message code="svc.mgmt.platform.dmgr" /></label></td>
                <td><form:input path="dmgrName" readonly="true" /></td>
            </tr>
            <tr>
                <c:if test="${not empty appServerList}">
	                <td><label id="txtPlatformAppservers"><spring:message code="svc.mgmt.platform.appservers" /></label></td>
	                <c:choose>
	                    <c:when test="${not empty appServerList}">
	                        <td>
	                            <form:select path="appServers" multiple="true">
	                                <form:options items="${appServerList}" />
	                            </form:select>
	                        </td>
	                    </c:when>
	                    <c:otherwise>
	                        <td>
	                            <a href="${pageContext.request.contextPath}/ui/systems/add-server"
	                                title="<spring:message code='select.request.add.server' />"><spring:message code='select.request.add.server' /></a>
	                        </td>
	                    </c:otherwise>
	                </c:choose>
		        </c:if>
		        <c:if test="${not empty webServerList}">
	                <td><label id="txtPlatformWebservers"><spring:message code="svc.mgmt.platform.webservers" /></label></td>
	                <c:choose>
	                    <c:when test="${not empty webServerList}">
	                        <td>
	                            <form:select path="webServers" multiple="true">
	                                <form:options items="${webServerList}" />
	                            </form:select>
	                        </td>
	                    </c:when>
	                    <c:otherwise>
	                        <td>
	                            <a href="${pageContext.request.contextPath}/ui/systems/add-server"
	                                title="<spring:message code='select.request.add.server' />"><spring:message code='select.request.add.server' /></a>
	                        </td>
	                    </c:otherwise>
	                </c:choose>
	            </c:if>
	        </tr>
            <tr>
                <td><label id="txtPlatformDescription"><spring:message code="svc.mgmt.platform.description" /></label></td>
	            <td><form:textarea path="description" /></td>
	            <td><form:errors path="description" cssClass="validationError" /></td>
            </tr>
        </table>
        <br /><br />
        <table id="inputItems">
            <tr>
                <td>
                    <input type="button" name="execute" value="<spring:message code='button.execute.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
                <td>
                    <input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                </td>
                <td>
                    <input type="button" name="reset" value="<spring:message code='button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                </td>
            </tr>
        </table>
    </form:form>
</div>
