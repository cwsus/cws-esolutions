<%@ page import="java.util.*" %>
<%--
/**
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
 * com.cws.us.esolutions/jsp/html/en
 * System_DisplayHeaders.jsp
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author cws-khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * cws-khuntly @ June 11, 2019 12:28 PM
 *     Created.
 */
--%>

<div id="sidebar">&nbsp;</div>

<div id="main">
    <h1><%= application.getServerInfo() %></h1>
    <h1>HTTP Request Headers Received</h1>

    <table border="1" cellpadding="3" cellspacing="3">
    <%
        Enumeration eNames = request.getHeaderNames();

        while (eNames.hasMoreElements())
        {
            String name = (String) eNames.nextElement();
            String value = normalize(request.getHeader(name));
    %>
        <tr><td><%= name %></td>
        <td><%= value %></td></tr>
    <%
    }
    %>
    </table>

    <h3>Extra debug</h3>
        <p>User remote: <%=request.getRemoteUser()%></p>
        <p>User principal: <%=request.getUserPrincipal()%></p>
<%!
private String normalize(String value)
{
StringBuffer sb = new StringBuffer();
for (int i = 0; i < value.length(); i++) {
char c = value.charAt(i);
sb.append(c);
if (c == ';')
sb.append("<br>");
}
return sb.toString();
}
%>



</div>

<div id="rightbar">&nbsp;</div>
