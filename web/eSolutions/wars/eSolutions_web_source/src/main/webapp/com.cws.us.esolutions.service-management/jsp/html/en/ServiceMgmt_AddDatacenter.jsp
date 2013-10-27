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
 * ServiceMgmt_AddDatacenter.jsp
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
        <p id="info">${messageResponse}</p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <p id="error"><spring:message code="${errorMessage}" /></p>
    </c:if>

    <spring:message code="svc.mgmt.add.datacenter" />

    <p id="validationError" />

    <form:form id="createNewDatacenter" name="createNewDatacenter" action="${pageContext.request.contextPath}/ui/service-management/submit-datacenter" method="post">
        <table id="detail">
            <tr>
                <td><label id="txtDatacenterName"><spring:message code="svc.mgmt.datacenter.name" /></label></td>
                <td><form:input path="datacenterName" /></td>
                <td><form:errors path="datacenterName" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtDatacenterStatus"><spring:message code="svc.mgmt.datacenter.status" /></label></td>
                <td>
                    <form:select path="datacenterStatus" multiple="false">
						<option><spring:message code="select.default" /></option>
						<option><spring:message code="select.spacer" /></option>
                        <form:options items="${statusList}" />
                    </form:select>
                </td>
                <td><form:errors path="datacenterStatus" cssClass="validationError" /></td>
            </tr>
            <tr>
                <td><label id="txtDatacenterDescription"><spring:message code="svc.mgmt.dataacenter.description" /></label></td>
	            <td><form:textarea path="datacenterDesc" /></td>
	            <td><form:errors path="datacenterDesc" cssClass="validationError" /></td>
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
