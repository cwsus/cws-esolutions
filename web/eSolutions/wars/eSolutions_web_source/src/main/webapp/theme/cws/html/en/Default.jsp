<%--
/**
 * Copyright 2008 - 2009 CaspersBox Web Services
 * All rights reserved.
 */
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--
/*
 * Default.jsp
 * Default theme file for application display. Only used when the
 * doGet() method is invoked on the ServiceRequestServlet.
 *
 * History
 *
 * Author               Date                           Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         11/23/2008 22:39:20            Created.
 * Kevin Huntly         12/22/2009 14:58:03            Added escape chars
 */
 --%>
<%@page contentType="text/html" pageEncoding="UTF-8" isThreadSafe="true" %>

<html xml:lang="en" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.w3.org/1999/xhtml http://www.w3.org/MarkUp/SCHEMA/xhtml2.xsd">

    <tiles:insertAttribute name="head" />

    <body>
        <tiles:insertAttribute name="masthead" />

        <div id="content">
            <%-- BEGIN CONTENT --%>
            <tiles:insertAttribute name="body" />
            <%-- END CONTENT --%>
            <br /><br />
        </div>

        <tiles:insertAttribute name="navbar" />
        <tiles:insertAttribute name="footer" />
    </body>

</html>
