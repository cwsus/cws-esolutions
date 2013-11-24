/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cws.esolutions.security.audit.enums;
/**
 * SecurityService
 * com.cws.esolutions.security.enums
 * AuditType.java
 *
 *
 *
 * $Id: AuditType.java 2287 2013-01-03 20:52:22Z kmhuntly@gmail.com $
 * $Author: $
 * $Date: 2013-01-03 15:52:22 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2287 $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 17, 2012 10:16:29 AM
 *     Created.
 */
public enum AuditType
{
    NONE,

    // authentication
    LOGON,
    LOGOFF,
    FORCELOGOFF,
    CHANGEPASS,
    RESETPASS,
    LOADSECURITY,
    VERIFYSECURITY,
    VERIFYRESET,

    // user management
    CREATEUSER,
    MODIFYUSER,
    DELETEUSER,
    SUSPENDUSER,
    PSUSPENDUSER,
    LISTUSERS,
    UNSUSPENDUSER,
    LOCKUSER,
    UNLOCKUSER,
    ADDSECURITY,
    SHOWAUDIT,
    SEARCHACCOUNTS,
    LOADACCOUNT,
    CHANGEKEYS,

    // file security
    DECRYPTFILE,
    ENCRYPTFILE,
    VERIFYFILE,
    SIGNFILE,

    // emailing
    SENDEMAIL,

    // knowledgebase
    CREATEARTICLE,
    UPDATEARTICLE,
    DELETEARTICLE,
    REJECTARTICLE,
    APPROVEARTICLE,
    SHOWARTICLE,
    SHOWPENDING,

    // service messaging
    SHOWMESSAGES,
    LOADMESSAGE,
    ADDSVCMESSAGE,
    EDITSVCMESSAGE,

    // app mgmt
    ADDAPP,
    UPDATEAPP,
    DELETEAPP,
    LISTAPPS,
    LOADAPP,
    GETFILES,
    DEPLOYAPP,

    // sysmgmt
    KILL,
    EXECCMD,
    ADDSERVER,
    DELETESERVER,
    UPDATESERVER,
    LISTSERVERS,
    GETSERVER,
    TELNET,
    REMOTEDATE,
    NETSTAT,
    PROCESSLIST,
    STOP,
    START,
    RESTART,
    SUSPEND,

    // project mgmt
    ADDPROJECT,
    UPDATEPROJECT,
    LISTPROJECTS,
    LOADPROJECT,

    // platform mgmt
    ADDPLATFORM,
    UPDATEPLATFORM,
    LISTPLATFORMS,
    LOADPLATFORM,

    // dns mgmt
    CREATEDNSRECORD,
    PUSHDNSRECORD,
    SITETXFR,
    LOADRECORD,

    // datacenter mgmt
    ADDDATACENTER,
    LISTDATACENTERS,
    LOADDATACENTER,
    UPDATEDATACENTER,

    // added to satisfy service tests
    // DO NOT REMOVE
    JUNIT;
}
