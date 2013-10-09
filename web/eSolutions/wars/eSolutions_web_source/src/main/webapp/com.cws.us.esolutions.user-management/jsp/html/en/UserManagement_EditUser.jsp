<c:choose>
    <c:when test="${sessionScope.userAccount.role eq 'USERADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
        <div class="feature">
            <div id="breadcrumb" class="lpstartover">
                <a href="${pageContext.request.contextPath}/ui/user-management/add-user"
                    title="<spring:message code='admin.account.create.user' />"><spring:message code="admin.account.create.user" /></a>
            </div>

		    <c:if test="${not empty messageResponse}">
		        <p id="info"><spring:message code="${messageResponse}" /></p>
		    </c:if>
		    <c:if test="${not empty errorResponse}">
		        <p id="error">${errorResponse}</p>
		    </c:if>

            <spring:message code="admin.account.enter.search.criteria" />

            <p id="validationError" />

            <form:form id="createNewUser" name="createNewUser" action="${pageContext.request.contextPath}/ui/user-management/create" method="post" autocomplete="off">
                <table id="addUser">
                    <tr>
                        <td><spring:message code="admin.account.user.id" /></td>
                        <td><form:input path="username" type="text" size="20" value="${searchResults.userName}" name="username" id="username" /></td>
                        <td><form:errors path="username" cssClass="validationError" /></td>
                    </tr>
                    <tr>
                        <td><spring:message code="admin.account.user.role" /></td>
                        <td>
                            <select name="userRole" id="userRole">
                                <option value="${searchResults.userRole}" selected="selected">${searchResults.userRole}</option>
                                <option value=""></option>
                                <option value="<spring:message code='admin.account.user.select' />"><spring:message code="admin.account.user.select" /></option>
                                <option value=""></option>
                                <c:forEach var="role" items="${selectableGroups}">
                                    <option value="${role}">${role}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td><spring:message code="admin.account.user.firstname" /></td>
                        <td><form:input path="givenName" type="text" size="20" value="${searchResults.givenName}" name="givenName" id="givenName" /></td>
                        <td><form:errors path="givenName" cssClass="validationError" /></td>
                    </tr>
                    <tr>
                        <td><spring:message code="admin.account.user.lastname" /></td>
                        <td><form:input path="surname" type="text" size="20" value="${searchResults.surname}" name="surname" id="surname" /></td>
                        <td><form:errors path="surname" cssClass="validationError" /></td>
                    </tr>
                    <tr>
                        <td><spring:message code="admin.account.user.email" /></td>
                        <td><form:input path="emailAddr" type="text" size="20" value="${searchResults.emailAddr}" name="emailAddr" id="emailAddr" /></td>
                        <td><form:errors path="emailAddr" cssClass="validationError" /></td>
                    </tr>
                    <tr>
                        <td><spring:message code="admin.account.user.locked" /></td>
                        <td>${searchResults.isUserLocked}</td>
                    </tr>
                    <tr>
                        <td><spring:message code="admin.account.user.suspension" /></td>
                        <td>${searchResults.isPermanentSuspension}</td>
                    </tr>
                    <tr>
                        <td><spring:message code="admin.account.user.password" /></td>
                        <td>anchor for reset here</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="button" name="execute" value="<spring:message code='admin.account.search.button.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                            <input type="button" name="deleteUser" value="<spring:message code='admin.account.delete.user' />" id="deleteUser" class="submit" onclick="disableButton(this)" />
                        </td>
                    </tr>
                </table>
            </form:form>
        </div>
    </c:when>
    <c:otherwise>
        <spring:message code="admin.account.not.authorized" />
        <c:if test="${requestScope.isUserLoggedIn ne 'true'}">
            <p>Click <a href="${pageContext.request.contextPath}/ui/home/default" title="Home">here</a> to continue.</p>
        </c:if>
    </c:otherwise>
</c:choose>
