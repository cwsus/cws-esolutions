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

<script>
<!--
    function validateForm(theForm)
    {
        if (theForm.projectName.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'A project name must be provided.';
            document.getElementById('projectName').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('projectName').focus();
        }
        else if (theForm.projectStatus.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'A project status must be provided.';
            document.getElementById('txtProjectStatus').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('projectCode').focus();
        }
        else if (theForm.primaryContact.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'A project contact must be provided.';
            document.getElementById('txtPrimaryContact').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('projectCode').focus();
        }
        else if (theForm.devEmail.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'A contact email must be provided.';
            document.getElementById('txtContactEmail').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('devEmail').focus();
        }
        else if (theForm.prodEmail.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'A contact email must be provided.';
            document.getElementById('txtContactEmail').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('prodEmail').focus();
        }
        else if (theForm.changeQueue.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'A change queue must be provided.';
            document.getElementById('txtChangeQueue').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('projectCode').focus();
        }
        else if (theForm.incidentQueue.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'An incident queue must be provided.';
            document.getElementById('txtIncidentQueue').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('projectCode').focus();
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
        <li><a href="${pageContext.request.contextPath}/ui/service-management/add-datacenter" title="<spring:message code='svc.mgmt.add.datacenter' />"><spring:message code="svc.mgmt.add.datacenter" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/list-projects" title="<spring:message code='svc.mgmt.list.projects' />"><spring:message code="svc.mgmt.list.projects" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/list-platforms" title="<spring:message code='svc.mgmt.list.platforms' />"><spring:message code="svc.mgmt.list.platforms" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/add-platform" title="<spring:message code='svc.mgmt.add.platform' />"><spring:message code="svc.mgmt.add.platform" /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="svc.mgmt.add.project" /></h1>

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
        <form:form id="createNewProject" name="createNewProject" action="${pageContext.request.contextPath}/ui/service-management/add-project" method="post">
            <table id="projectDetail">
                <tr>
                    <td><label id="txtProjectInfo"><spring:message code="svc.mgmt.add.info" /></label></td>
                </tr>
                <tr>
                    <td><label id="txtProjectCode"><spring:message code="svc.mgmt.service.name" /></label></td>
                    <td><form:input path="projectName" /></td>
                    <td><form:errors path="projectName" cssClass="error" /></td>
                    <td><label id="txtProjectStatus"><spring:message code="svc.mgmt.service.status" /></label></td>
                    <td>
                        <form:select path="projectStatus">
                            <option><spring:message code="theme.option.select" /></option>
                            <option><spring:message code="theme.option.spacer" /></option>
                            <form:options items="${statusList}" />
                        </form:select>
                    </td>
                    <td><form:errors path="projectStatus" cssClass="error" /></td>
                </tr>
                <tr>
                    <td><label id="txtContactInfo"><spring:message code="svc.mgmt.project.contact" /></label></td>
                </tr>
                <tr>
                    <td><label id="txtPrimaryContact"><spring:message code="svc.mgmt.project.pcontact" /></label></td>
                    <td>
                        <form:select path="primaryContact">
                            <option><spring:message code="theme.option.select" /></option>
                            <option><spring:message code="theme.option.spacer" /></option>
                            <c:forEach var="userAccount" items="${userList}">
                                <form:option value="${userAccount.guid}" label="${userAccount.displayName}" />
                            </c:forEach>
                        </form:select>
                    </td>
                    <td><form:errors path="primaryContact" cssClass="error" /></td>
                    <td><label id="txtSecondaryContact"><spring:message code="svc.mgmt.project.scontact" /></label></td>
                    <td>
                        <form:select path="secondaryContact">
                            <option><spring:message code="theme.option.select" /></option>
                            <option><spring:message code="theme.option.spacer" /></option>
                            <c:forEach var="userAccount" items="${userList}">
                                <form:option value="${userAccount.guid}" label="${userAccount.displayName}" />
                            </c:forEach>
                        </form:select>
                    </td>
                    <td><form:errors path="secondaryContact" cssClass="error" /></td>
                </tr>
                <tr>
                    <td><label id="txtContactEmail"><spring:message code="svc.mgmt.project.dev.group" /></label></td>
                    <td><form:input path="devEmail" /></td>
                    <td><form:errors path="devEmail" cssClass="error" /></td>
                    <td><label id="txtContactEmail"><spring:message code="svc.mgmt.project.prod.group" /></label></td>
                    <td><form:input path="prodEmail" /></td>
                    <td><form:errors path="prodEmail" cssClass="error" /></td>
                </tr>
                <tr>
                    <td><label id="txtChangeQueue"><spring:message code="svc.mgmt.project.changeq" /></label></td>
                    <td><form:input path="changeQueue" /></td>
                    <td><form:errors path="changeQueue" cssClass="error" /></td>
                    <td><label id="txtIncidentQueue"><spring:message code="svc.mgmt.project.ticketq" /></label></td>
                    <td><form:input path="incidentQueue" /></td>
                    <td><form:errors path="incidentQueue" cssClass="error" /></td>
                </tr>
            </table>
            <br /><br />
            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
            <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
        </form:form>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
