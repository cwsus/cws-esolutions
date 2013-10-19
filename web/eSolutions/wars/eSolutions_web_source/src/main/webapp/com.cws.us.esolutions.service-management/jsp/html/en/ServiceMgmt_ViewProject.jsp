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
 * ServiceMgmt_ViewProject.jsp
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
    <table id="projectDetail">
        <tr>
            <td><label id="txtProjectCode"><spring:message code="svc.mgmt.project.code" /></label>
            <td>${project.projectCode}</td>
        </tr>
        <tr>
            <td><label id="txtProjectStatus"><spring:message code="svc.mgmt.project.status" /></label>  
            <td>${project.projectStatus}</td>
        </tr>
        <tr>
            <td><label id="txtPrimaryContact"><spring:message code="svc.mgmt.project.pcontact" /></label>
            <td>${project.primaryContact}</td>
        </tr>
        <tr>
            <td><label id="txtSecondaryContact"><spring:message code="svc.mgmt.project.scontact" /></label>
            <td>${project.secondaryContact}</td>
        </tr>
        <tr>
            <td><label id="txtContactEmail"><spring:message code="svc.mgmt.project.email" /></label>
            <td>${project.contactEmail}</td>
        </tr>
        <tr>
            <td><label id="txtChangeQueue"><spring:message code="svc.mgmt.project.changeq" /></label>
            <td>${project.changeQueue}</td>
        </tr>
        <tr>
            <td><label id="txtIncidentQueue"><spring:message code="svc.mgmt.project.ticketq" /></label>
            <td>${project.incidentQueue}</td>
        </tr>
        <tr>
            <td><label id="txtApplications"><spring:message code="svc.mgmt.project.applications" /></label>
            <td>${project.applicationList}</td>
        </tr>
    </table>
</div>
