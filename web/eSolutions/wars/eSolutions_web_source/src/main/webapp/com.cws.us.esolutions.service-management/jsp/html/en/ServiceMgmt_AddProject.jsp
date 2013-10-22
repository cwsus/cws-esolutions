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
 * ServiceMgmt_AddProject.jsp
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

    <spring:message code="svc.mgmt.add.project" />

    <p id="validationError" />

    <form:form id="createNewProject" name="createNewProject" action="${pageContext.request.contextPath}/ui/service-management/add-project" method="post">
        <table id="projectDetail">
            <tr>
                <td><label id="txtProjectCode"><spring:message code="svc.mgmt.project.code" /></label></td>
                <td><form:input path="projectCode" /></td>
                <td><form:errors path="projectCode" cssClass="validationError" /></td>
                <td><label id="txtProjectStatus"><spring:message code="svc.mgmt.project.status" /></label></td>
                <td>
                    <form:select path="projectStatus">
                        <form:option value="<spring:message code='select.value' />" />
                        <form:option value="--------" />
                        <form:options items="${statusList}" />
                    </form:select>
                </td>
                <td><form:errors path="projectStatus" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtPrimaryContact"><spring:message code="svc.mgmt.project.pcontact" /></label></td>
                <td><form:input path="primaryContact" /></td>
                <td><form:errors path="primaryContact" cssClass="validationError" /></td>
                <td><label id="txtSecondaryContact"><spring:message code="svc.mgmt.project.scontact" /></label></td>
                <td><form:input path="secondaryContact" /></td>
                <td><form:errors path="secondaryContact" cssClass="validationError" /></td>
                <td><label id="txtContactEmail"><spring:message code="svc.mgmt.project.email" /></label></td>
                <td><form:input path="contactEmail" /></td>
                <td><form:errors path="contactEmail" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtChangeQueue"><spring:message code="svc.mgmt.project.changeq" /></label></td>
                <td><form:input path="changeQueue" /></td>
                <td><form:errors path="changeQueue" cssClass="validationError" /></td>
                <td><label id="txtIncidentQueue"><spring:message code="svc.mgmt.project.ticketq" /></label></td>
                <td><form:input path="incidentQueue" /></td>
                <td><form:errors path="incidentQueue" cssClass="validationError" /></td>
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

