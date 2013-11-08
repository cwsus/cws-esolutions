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
 * theme/cws/html/en/jsp
 * head.jsp
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

<head>
    <title><tiles:insertAttribute name="pageTitle" /></title>
    <link rel="stylesheet" type="text/css" media="all" href="/html/esolutions/css/esolutions.css" />
    <link rel="image/x-icon" href="/favicon.ico" />
    <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon" />
    <meta http-equiv="Content-Script-Type" content="text/javascript" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="Content-Language" content="en-US" />
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="expires" content="-1" />
    <meta http-equiv="cache-control" content="no-store, no-cache, must-revalidate" />
    <meta http-equiv="max-age" content="0" />
    <meta http-equiv="refresh" content="${pageContext.session.maxInactiveInterval}; url=${pageContext.request.contextPath}/ui/login/logout" />
    <meta name="robots" content="index,follow,noarchive" />
    <meta name="GoogleBot" content="noarchive" />
    <meta name="Author" content="eSolutions" />
    <meta name="copyright" content="<spring:message code="footer.copyright" />" />
    <meta name="description" content="eSolutionsService" />
    <meta name="keywords" content="incident, change management, incident management, infinix, caspersbox, caspersbox web services" />
    <script type="text/javascript" src="/html/esolutions/js/Scripts.js"></script>
    <script type="text/javascript" src="/html/esolutions/js/FormHandler.js"></script>
    <script type="text/javascript">
        <!--
        if (top != self)
        {
            top.location = self.location;
        }
        //-->
    </script>
    <script type="text/javascript">
        <!--
            var enable = false;

            if (enable)
            {
                var documentURI = window.location.pathname + window.location.search;

                if (documentURI != '${pageContext.request.contextPath}/ui/login/warn')
                {
                    var popupURI = "${pageContext.request.contextPath}/ui/login/warn";

                    setInterval("popup(popupURI, 'Timeout', '280', '170', '0', '0', '0', '0')", ${pageContext.session.maxInactiveInterval} - 90);
                }
            }
        //-->
    </script>
</head>
