<c:choose>
    <c:when test="${sessionScope.userAccount.role == 'USERADMIN' or sessionScope.userAccount.role == 'SITEADMIN'}">
        <div class="feature">
		    <c:if test="${not empty messageResponse}">
		        <p id="info"><spring:message code="${messageResponse}" /></p>
		    </c:if>
		    <c:if test="${not empty errorResponse}">
		        <p id="error">${errorResponse}</p>
		    </c:if>

            <spring:message code="admin.create.new.account" />

            <p id="validationError" />

            <form:form id="createNewUser" name="createNewUser" action="${pageContext.request.contextPath}/ui/user-management/add-user" method="post" autocomplete="off">
                <table id="addUser">
                    <tr>
                        <td><label id="createUserName"><spring:message code="admin.account.user.id" /></label></td>
                        <td><form:input path="username" type="text" size="20" value="" name="username" id="username" /></td>
                        <td><form:errors path="username" cssClass="validationError" /></td>
                    </tr>
                    <tr>
                        <td><label id="createUserRole"><spring:message code="admin.account.user.role" /></label></td>
                        <td>
                            <form:select path="role" name="role" id="role">
                                <option value="<spring:message code='admin.account.user.select' />" selected="selected"><spring:message code='admin.account.user.select' /></option>
                                <option value=""></option>
                                <c:forEach var="role" items="${selectableRoles}">
                                    <option value="${role}">${role}</option>
                                </c:forEach>
                            </form:select>
                        </td>
                        <td><form:errors path="role" cssClass="validationError" /></td>
                    </tr>
                    <%--
                    <tr>
                        <td><label id="createUserUnit"><spring:message code="admin.account.user.dept" /></label></td>
                        <td>
                            <form:select path="dept" name="dept" id="dept">
                                <option value="<spring:message code='admin.account.user.select' />" selected="selected"><spring:message code='admin.account.user.select' /></option>
                                <option value=""></option>
                                <c:forEach var="dept" items="${selectableDepts}">
                                    <option value="${dept}">${dept}</option>
                                </c:forEach>
                            </form:select>
                        </td>
                        <td><form:errors path="dept" cssClass="validationError" /></td>
                    </tr>
                    <tr>
                        <td><label id="createUserGroup"><spring:message code="admin.account.user.group" /></label></td>
                        <td>
                            <form:select path="group" name="group" id="group">
                                <option value="<spring:message code='admin.account.user.select' />" selected="selected"><spring:message code='admin.account.user.select' /></option>
                                <option value=""></option>
                                <c:forEach var="group" items="${selectableGroups}">
                                    <option value="${group}">${group}</option>
                                </c:forEach>
                            </form:select>
                        </td>
                        <td><form:errors path="group" cssClass="validationError" /></td>
                    </tr>
                    --%>
                    <tr>
                        <td><label id="createuserFirstName"><spring:message code="admin.account.user.firstname" /></label></td>
                        <td><form:input path="givenName" type="text" size="20" value="" name="givenName" id="givenName" /></td>
                        <td><form:errors path="givenName" cssClass="validationError" /></td>
                    </tr>
                    <tr>
                        <td><label id="createUserLastName"><spring:message code="admin.account.user.lastname" /></label></td>
                        <td><form:input path="surname" type="text" size="20" value="" name="surname" id="surname" /></td>
                        <td><form:errors path="surname" cssClass="validationError" /></td>
                    </tr>
                    <tr>
                        <td><label id="createUserEmail"><spring:message code="admin.account.user.email" /></label></td>
                        <td><form:input path="emailAddr" type="text" size="20" value="" name="emailAddr" id="emailAddr" /></td>
                        <td><form:errors path="emailAddr" cssClass="validationError" /></td>
                    </tr>
                    <tr>
                        <td><label id="createUserLock"><spring:message code="admin.account.user.locked" /></label></td>
                        <td><form:checkbox path="suspended" name="suspended" id="suspended" /></td>
                        <td><form:errors path="suspended" cssClass="validationError" /></td>
                    </tr>
                    <tr>
                        <td>
                            <input type="button" name="execute" value="<spring:message code='admin.account.submit.button.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                            <input type="button" name="cancel" value="<spring:message code='admin.account.cancel.button.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                        </td>
                    </tr>
                </table>
            </form:form>
        </div>
    </c:when>
    <c:otherwise>
        <spring:message code="admin.account.not.authorized" />
        <c:if test="${requestScope.isUserLoggedIn != 'true'}">
            <p>Click <a href="${pageContext.request.contextPath}/ui/home/default" title="Home">here</a> to continue.</p>
        </c:if>
    </c:otherwise>
</c:choose>
