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

<script>
<!--
    function validateForm(theForm)
    {
        if (theForm.datacenterName.value == '')
        {
            document.getElementById('validationError').innerHTML = 'A name must be provided for the new datacenter.';
            document.getElementById('txtDatacenterName').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('datacenterName').focus();
        }
        else if (theForm.datacenterStatus.value == '')
        {
            document.getElementById('validationError').innerHTML = 'A datacenter status must be provided.';
            document.getElementById('txtDatacenterStatus').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('datacenterStatus').focus();
        }
        else if (theForm.datacenterDesc.value == '')
        {
            document.getElementById('validationError').innerHTML = 'A description of the datacenter must be provided.';
            document.getElementById('txtDatacenterDescription').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('datacenterDesc').focus();
        }
        else
        {
            theForm.submit();
        }
    }
//-->
</script>

<div id="sidebar">
    <h1><spring:message code="svc.mgmt.header" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/list-datacenters" title="<spring:message code='svc.mgmt.list.datacenters' />"><spring:message code="svc.mgmt.list.datacenters" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/list-projects" title="<spring:message code='svc.mgmt.list.projects' />"><spring:message code="svc.mgmt.list.projects" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/add-project" title="<spring:message code='svc.mgmt.add.project' />"><spring:message code="svc.mgmt.add.project" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/list-platforms" title="<spring:message code='svc.mgmt.list.platforms' />"><spring:message code="svc.mgmt.list.platforms" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/add-platform" title="<spring:message code='svc.mgmt.add.platform' />"><spring:message code="svc.mgmt.add.platform" /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="svc.mgmt.add.datacenter" /></h1>

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
        <form:form id="createNewDatacenter" name="createNewDatacenter" action="${pageContext.request.contextPath}/ui/service-management/submit-datacenter" method="post">
            <label id="txtDatacenterName"><spring:message code="svc.mgmt.service.name" /></label>
            <form:input path="datacenterName" />
            <form:errors path="datacenterName" cssClass="error" />
            <label id="txtDatacenterStatus"><spring:message code="svc.mgmt.service.status" /></label>
            <form:select path="datacenterStatus" multiple="false">
                <option><spring:message code="theme.option.select" /></option>
                <option><spring:message code="theme.option.spacer" /></option>
                <form:options items="${statusList}" />
            </form:select>
            <form:errors path="datacenterStatus" cssClass="error" />
            <label id="txtDatacenterDescription"><spring:message code="svc.mgmt.service.description" /></label>
            <form:textarea path="datacenterDesc" />
            <form:errors path="datacenterDesc" cssClass="error" />
            <br /><br />
            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
            <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
        </form:form>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
