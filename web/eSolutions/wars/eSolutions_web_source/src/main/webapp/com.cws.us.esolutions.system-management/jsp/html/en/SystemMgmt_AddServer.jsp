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
 * SystemManagement_AddServer.jsp
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

<script type="text/javascript">
    <!--
    function showOptions(obj)
    {
        if ((obj.value == 'DMGRSERVER') || (obj.value == 'VIRTUALHOST') || (obj.value == 'APPSERVER'))
        {
            document.getElementById("applicationDetail").style.display = 'block';

            if (obj.value == 'DMGRSERVER')
            {
	            document.getElementById("dmgrPort").style.display = 'block';
	            document.getElementById("owningDmgr").style.display = 'none';
	            document.getElementById("managerUrl").style.display = 'block';
            }
            else if (obj.value == 'VIRTUALHOST')
            {
                document.getElementById("dmgrPort").style.display = 'none';
                document.getElementById("owningDmgr").style.display = 'none';
                document.getElementById("managerUrl").style.display = 'block';
            }
            else if (obj.Value == 'APPSERVER')
            {
                // need to list out associated dmgr's here somehow
                document.getElementById("dmgrPort").style.display = 'none';
                document.getElementById("managerUrl").style.display = 'none';
                document.getElementById("owningDmgr").style.display = 'block';
            }
        }
        else
        {
            document.getElementById("applicationDetail").style.display = 'none';
        }
    }
    //-->
</script>

<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="system.mgmt.add.server" />

    <p id="validationError" />

    <form:form id="createNewServer" name="createNewServer" action="${pageContext.request.contextPath}/ui/system-management/add-server" method="post">
        <table id="serverDetail">
            <tr>
                <%-- OS name/type --%>
                <td><label id="txtOsName"><spring:message code="system.mgmt.os.name" /></label>
                <td><form:input path="osName" /></td>
                <td><form:errors path="osName" cssClass="validationError" /></td>
                <%-- domain name --%>
                <td><label id="txtDomainName"><spring:message code="system.mgmt.domain.name" /></label>
                <td><form:input path="domainName" /></td>
                <td><form:errors path="domainName" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtServerType"><spring:message code="system.mgmt.server.type" /></label>
                <td>
                    <form:select path="serverType" onchange="showOptions(this);">
                        <form:options items="${serverTypes}" />
                    </form:select>
                </td>
                <td><form:errors path="serverType" cssClass="validationError" /></td>
                <td><label id="txtServerStatus"><spring:message code="system.mgmt.server.status" /></label>
                <td>
                    <form:select path="serverStatus">
                        <form:options items="${serverStatuses}" />
                    </form:select>
                </td>
                <td><form:errors path="serverStatus" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtServerRegion"><spring:message code="system.mgmt.server.region" /></label>
                <td>
                    <form:select path="serverRegion">
                        <form:options items="${serverRegions}" />
                    </form:select>
                </td>
                <td><form:errors path="serverRegion" cssClass="validationError" /></td>
                <td><label id="txtSerialNumber"><spring:message code="system.mgmt.serial.number" /></label>
                <td><form:input path="serialNumber" /></td>
                <td><form:errors path="serialNumber" cssClass="validationError" /></td>
            </tr>
        </table>
        <table id="applicationDetail" style="display: none">
            <tr id="dmgrPort" style="display: none">
                <td><label id="txtDmgrPort"><spring:message code="system.mgmt.dmgr.port" /></label>
                <td><form:input path="dmgrPort" /></td>
                <td><form:errors path="dmgrPort" cssClass="validationError" /></td>
            </tr>
            <tr id="managerUrl" style="display: none">
                <td><label id="txtManagerUrl"><spring:message code="system.mgmt.manager.url" /></label>
                <td><form:input path="mgrUrl" /></td>
                <td><form:errors path="mgrUrl" cssClass="validationError" /></td>
            </tr>
            <tr id="owningDmgr" style="display: none">
                <td><label id="txtOwningDmgr"><spring:message code="system.mgmt.owning.dmgr" /></label>
                <td>
                    <c:choose>
                        <c:when test="${not empty dmgrList}">
		                    <form:select path="owningDmgr">
		                        <form:options items="${dmgrList}" />
		                    </form:select>
	                    </c:when>
	                    <c:otherwise>
	                        <td><form:input path="owningDmgr" /></td>
	                    </c:otherwise>
	                </c:choose>
                </td>
                <td><form:errors path="owningDmgr" cssClass="validationError" /></td>
            </tr>
        </table>
        <table id="hardwareDetail">
            <tr>
                <td><label id="txtCpuType"><spring:message code="system.mgmt.cpu.type" /></label>  
                <td><form:input path="cpuType" /></td>
                <td><form:errors path="cpuType" cssClass="validationError" /></td>
                <td><label id="txtCpuCount"><spring:message code="system.mgmt.cpu.count" /></label>
                <td><form:input path="cpuCount" /></td>
                <td><form:errors path="cpuCount" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtInstalledMemory"><spring:message code="system.mgmt.installed.memory" /></label>
                <td><form:input path="installedMemory" /></td>
                <td><form:errors path="installedMemory" cssClass="validationError" /></td>
                <td><label id="txtServerModel"><spring:message code="system.mgmt.server.model" /></label>
                <td><form:input path="serverModel" /></td>
                <td><form:errors path="serverModel" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtServerRack"><spring:message code="system.mgmt.server.rack" /></label>
                <td><form:input path="serverRack" /></td>
                <td><form:errors path="serverRack" cssClass="validationError" /></td>
                <td><label id="txtRackPosition"><spring:message code="system.mgmt.rack.position" /></label>
                <td><form:input path="rackPosition" /></td>
                <td><form:errors path="rackPosition" cssClass="validationError" /></td>
            </tr>
        </table>
        <table id="networkDetail">
            <tr>
                <td><label id="txtOperHostname"><spring:message code="system.mgmt.oper.name" /></label>  
                <td><form:input path="operHostName" /></td>
                <td><form:errors path="operHostName" cssClass="validationError" /></td>
                <td><label id="txtOperAddress"><spring:message code="system.mgmt.oper.address" /></label>
                <td><form:input path="operIpAddress" /></td>
                <td><form:errors path="operIpAddress" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtMgmtHostname"><spring:message code="system.mgmt.mgmt.name" /></label>
                <td><form:input path="mgmtHostName" /></td>
                <td><form:errors path="mgmtHostName" cssClass="validationError" /></td>
                <td><label id="txtMgmtAddress"><spring:message code="system.mgmt.mgmt.address" /></label>
                <td><form:input path="mgmtIpAddress" /></td>
                <td><form:errors path="mgmtIpAddress" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtBackupHostname"><spring:message code="system.mgmt.backup.name" /></label>
                <td><form:input path="bkHostName" /></td>
                <td><form:errors path="bkHostName" cssClass="validationError" /></td>
                <td><label id="txtBackupAddress"><spring:message code="system.mgmt.backup.address" /></label>
                <td><form:input path="bkIpAddress" /></td>
                <td><form:errors path="bkIpAddress" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtNasHostname"><spring:message code="system.mgmt.nas.name" /></label>
                <td><form:input path="nasHostName" /></td>
                <td><form:errors path="nasHostName" cssClass="validationError" /></td>
                <td><label id="txtNasAddress"><spring:message code="system.mgmt.nas.address" /></label>
                <td><form:input path="nasIpAddress" /></td>
                <td><form:errors path="nasIpAddress" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtNatAddress"><spring:message code="system.mgmt.nat.address" /></label>
                <td><form:input path="natAddress" /></td>
                <td><form:errors path="natAddress" cssClass="validationError" /></td>
            </tr>
        </table>
        <table id="comments">
            <tr>
                <td><label id="txtServerComments"><spring:message code="system.mgmt.server.comments" /></label>
                <td><form:textarea path="serverComments" /></td>
                <td><form:errors path="serverComments" cssClass="validationError" /></td>
            </tr>
        </table>
        <br /><br />
        <table id="inputItems">
            <tr>
				<td>
				    <input type="button" name="execute" value="<spring:message code='button.execute.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
				</td>
				<td>
				    <input type="button" name="reset" value="<spring:message code='button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
				</td>
				<td>
				    <input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
				</td>
            </tr>
        </table>
    </form:form>
</div>
<br /><br />
