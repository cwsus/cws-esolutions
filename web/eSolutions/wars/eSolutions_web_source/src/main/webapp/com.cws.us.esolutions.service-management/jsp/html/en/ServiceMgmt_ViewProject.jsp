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
