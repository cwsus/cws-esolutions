#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  runServiceFailover.sh
#         USAGE:  ./runServiceFailover.sh
#   DESCRIPTION:  Executes the necessary classes against the configured DNS
#                 master to fail over a single site.
#
#       OPTIONS:  ---
#  REQUIREMENTS:  ---
#          BUGS:  ---
#         NOTES:  ---
#        AUTHOR:  Kevin Huntly <kmhuntly@gmail.com>
#       COMPANY:  CaspersBox Web Services
#       VERSION:  1.0
#       CREATED:  ---
#      REVISION:  ---
#==============================================================================

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

## Application constants
CNAME="$(/usr/bin/env basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}/${0##*/}")";
SCRIPT_ROOT="$(/usr/bin/env dirname "${SCRIPT_ABSOLUTE_PATH}")";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +vx;

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -f "${SCRIPT_ROOT}/../lib/plugin" ] && . "${SCRIPT_ROOT}/../lib/plugin";

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

[ -z "${APP_ROOT}" ] && awk -F "=" '/\<1\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

[ -f "${PLUGIN_LIB_DIRECTORY}/aliases" ] && . "${PLUGIN_LIB_DIRECTORY}/aliases";
[ -f "${PLUGIN_LIB_DIRECTORY}/functions" ] && . "${PLUGIN_LIB_DIRECTORY}/functions";

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

THIS_CNAME="${CNAME}";
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +vx;

## validate the input
"${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh" -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

CNAME="${THIS_CNAME}";
typeset METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
then
    "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security violation found while executing ${CNAME} by ${IUSER_AUDIT} on host ${SYSTEM_HOSTNAME}";
    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security configuration blocks execution. Please verify security configuration.";

    awk -F "=" '/\<request.not.authorized\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

    return ${RET_CODE};
fi

#===  FUNCTION  ===============================================================
#          NAME:  runInternetSiteFailover
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function runInternetSiteFailover
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILENAME->${FILENAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET->${TARGET}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRJCODE-> ${PRJCODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LOCAL_EXECUTION -> ${LOCAL_EXECUTION}";

    ## our request type is p <project code> or u <url>
    ## both can be run against the same script, as its
    ## a single-zone change
    ## spawn an ssh connection to the DNS master
    if [ ! -z "${LOCAL_EXECUTION}" ] && [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command executeSiteFailover.sh -d x -b ${UNIT} -f ${FILENAME} -t ${TARGET} -p ${PRJCODE} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e";

        THIS_CNAME="${CNAME}";
        unset METHOD_NAME;
        unset CNAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        ## validate the input
        [ -z "${FAILOVER_TYPE}" ] && [ "${FAILOVER_TYPE}" = "unit" ] && ${PLUGIN_LIB_DIRECTORY}/executors/executeServiceFailover.sh -t ${INTERNET_TYPE_IDENTIFIER} -b ${UNIT} -x ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e;
        [ -z "${FAILOVER_TYPE}" ] && [ "${FAILOVER_TYPE}" = "project" ] && ${PLUGIN_LIB_DIRECTORY}/executors/executeServiceFailover.sh -t ${INTERNET_TYPE_IDENTIFIER} -b ${UNIT} -p ${PRJCODE} -x ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e;
        [ -z "${FAILOVER_TYPE}" ] && [ "${FAILOVER_TYPE}" = "datacenter" ] && ${PLUGIN_LIB_DIRECTORY}/executors/executeServiceFailover.sh -t ${INTERNET_TYPE_IDENTIFIER} -a -x ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e;
        [ -z "${FAILOVER_TYPE}" ] && [ "${FAILOVER_TYPE}" = "site" ] && ${PLUGIN_LIB_DIRECTORY}/executors/executeServiceFailover.sh -t ${INTERNET_TYPE_IDENTIFIER} -a -x ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e;
        typeset -i RET_CODE=${?};

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;

        CNAME="${THIS_CNAME}";
        typeset METHOD_NAME="${THIS_CNAME}#${0}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
    else
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${NAMED_MASTER} \"executeSiteFailover.sh -d x -b ${UNIT} -f ${FILENAME} -t ${TARGET} -p ${PRJCODE} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e\"";

        THIS_CNAME="${CNAME}";
        unset METHOD_NAME;
        unset CNAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        ssh ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeServiceFailover.sh -t ${INTERNET_TYPE_IDENTIFIER} -b ${UNIT} -x ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e";
        ssh ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeServiceFailover.sh -t ${INTERNET_TYPE_IDENTIFIER} -b ${UNIT} -p ${PRJCODE} -x ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e";
        ssh ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeServiceFailover.sh -t ${INTERNET_TYPE_IDENTIFIER} -a -x ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e";
        ssh ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeServiceFailover.sh -t ${INTERNET_TYPE_IDENTIFIER} -a -x ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e";
        typeset -i RET_CODE=${?};

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;

        CNAME="${THIS_CNAME}";
        typeset METHOD_NAME="${THIS_CNAME}#${0}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
    fi

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        ## failover process has failed. inform.
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover processing on ${NAMED_MASTER} has failed. Unable to proceed.";

        RETURN_CODE=${RET_CODE};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset UNIT;
        unset FILENAME;
        unset TARGET;
        unset PRJCODE;
        unset CHG_CTRL;
        unset FAILOVER_CODE;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        return ${RETURN_CODE};
    fi

    ## failover was successful. lets do some more work...
    unset RET_CODE;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover completed. Reloading server configuration..";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runRNDCCommands.sh -s ${NAMED_MASTER} -c reload -e..";

    unset CNAME;
    unset METHOD_NAME;

    THIS_CNAME="${CNAME}";
    unset METHOD_NAME;
    unset CNAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

    ## validate the input
    ${PLUGIN_LIB_DIRECTORY}/runRNDCCommands.sh -s ${NAMED_MASTER} -c reload -e;
    typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;

    CNAME="${THIS_CNAME}";
    typeset METHOD_NAME="${THIS_CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        ## failover process has failed. inform.
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover processing on ${NAMED_MASTER} has failed. Unable to proceed.";

        RETURN_CODE=${RET_CODE};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset UNIT;
        unset FILENAME;
        unset TARGET;
        unset PRJCODE;
        unset CHG_CTRL;
        unset FAILOVER_CODE;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        return ${RETURN_CODE};
    fi

    ## we've successfully reloaded our configuration. verify that the change was indeed made
    unset RET_CODE;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server configuration successfully reloaded. Validating change..";

    ## sleep for configured thread delay to allow changes to propagate
    sleep "${MESSAGE_DELAY}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing query against ${NAMED_MASTER}..";

    THIS_CNAME="${CNAME}";
    unset METHOD_NAME;
    unset CNAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

    ## validate the input
    "${APP_ROOT}/${LIB_DIRECTORY}"/runQuery.sh -s ${NAMED_MASTER} -t A -u ${SITE_HOSTNAME} -o -e;
    typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;

    CNAME="${THIS_CNAME}";
    typeset METHOD_NAME="${THIS_CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        ## failover process has failed. inform.
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover processing on ${NAMED_MASTER} has failed. Unable to proceed.";

        RETURN_CODE=${RET_CODE};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset UNIT;
        unset FILENAME;
        unset TARGET;
        unset PRJCODE;
        unset CHG_CTRL;
        unset FAILOVER_CODE;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        return ${RETURN_CODE};
    fi

    ## we've applied and activated our change. reload changes into the configured
    ## slave servers
    unset RET_CODE;
    unset RETURN_TEXT;

    for SERVER in ${SLAVE_SERVERS[*]}
    do
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER -> ${SERVER}";

        ## make sure its alive
        typeset THIS_CNAME="${CNAME}";
        unset METHOD_NAME;
        unset CNAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        ## validate the input
        validateServerAvailability ${SERVER};
        typeset -i RET_CODE=${?};

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;

        CNAME="${THIS_CNAME}";
        typeset METHOD_NAME="${THIS_CNAME}#${0}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

        if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
        then
            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to reload service configuration on host ${SERVER} - host appears to be unavailable. Please reload manually.";

            (( ERROR_COUNT += 1 ));

            continue;
        fi

        ## stop if its available and run the command
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Proxy access confirmed. Proxy: ${PROXY}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ssh ${PROXY} \"dig @${NAMESERVER} -t ${RECORD_TYPE} ${SITE_URL}\" > "${DIG_DATA_FILE}";

        THIS_CNAME="${CNAME}";
        unset METHOD_NAME;
        unset CNAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        ## validate the input
        ${PLUGIN_LIB_DIRECTORY}/runRNDCCommands.sh -s ${SERVER} -c reload -e;
        typeset -i RET_CODE=${?};

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;

        CNAME="${THIS_CNAME}";
        typeset METHOD_NAME="${THIS_CNAME}#${0}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

        if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
        then
            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to reload service configuration on host ${SERVER}. Please reload manually.";

            (( ERROR_COUNT += 1 ));
        fi
    done

    THIS_CNAME="${CNAME}";
    unset METHOD_NAME;
    unset CNAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

    ## validate the input
    ${MAILER_CLASS} -m notifySiteFailover -p ${PROJECT_CODE} -a "${DNS_SERVER_ADMIN_EMAIL}" -e;
    typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;

    CNAME="${THIS_CNAME}";
    typeset METHOD_NAME="${THIS_CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" ""${LOGGER}" "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: ${UNIT}/${PRJCODE}/${FILENAME} - Change Request: ${CHG_CTRL} - Switched To: ${TARGET}")";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset UNIT;
    unset FILENAME;
    unset TARGET;
    unset PRJCODE;
    unset CHG_CTRL;
    unset FAILOVER_CODE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  runIntranetSiteFailover
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function runIntranetSiteFailover
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME->${SITE_HOSTNAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENABLE_POP->${ENABLE_POP}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DISABLE_POP->${DISABLE_POP}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";

    ## our request type is p <project code> or u <url>
    ## both can be run against the same script, as its
    ## a single-zone change
    ## spawn an ssh connection to the DNS master
    if [ ! -z "${LOCAL_EXECUTION}" ] && [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command executeSiteFailover.sh -d x -b ${UNIT} -f ${FILENAME} -t ${TARGET} -p ${PRJCODE} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e";

        unset METHOD_NAME;
        unset CNAME;

        FAILOVER_CODE=$(${PLUGIN_LIB_DIRECTORY}/executors/executeSiteFailover.sh -d i -f ${DISABLE_POP} -t ${ENABLE_POP} -c ${CHANGE_NUM} -i ${IUSER_AUDIT} -e);

        CNAME="${THIS_CNAME}";
        typeset METHOD_NAME="${CNAME}#${0}";
        unset RFR_OPTIND;
    else
        for GD_SERVER in ${GD_SERVERS}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now validating server ${GD_SERVER}..";

            typeset THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

            ## validate the input
            validateServerAvailability ${GD_SERVER};
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ ${RET_CODE} -eq 0 ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${GD_SERVER} \"executeSiteFailover.sh -d i -f ${DISABLE_POP} -t ${ENABLE_POP} -c ${CHANGE_NUM} -i ${IUSER_AUDIT} -e\"";

                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

                ## validate the input
                ssh ${GD_SERVER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeSiteFailover.sh -d i -f ${DISABLE_POP} -t ${ENABLE_POP} -c ${CHANGE_NUM} -i ${IUSER_AUDIT} -e";
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                ## rock out our validation here
                if [ ${RET_CODE} -eq 0 ]
                then
                    ## failover was successful. lets do some more work...
                    unset RET_CODE;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover completed. Reloading server configuration..";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runRNDCCommands.sh -s ${NAMED_MASTER} -c reload -e..";

                    ## validate that it was indeed changed
                    unset CNAME;
                    unset METHOD_NAME;

                    ## TODO: work out validation here. probably retrieveServiceInfo -validate or some such

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${CNAME}#${0}";
                    typeset RETURN_CODE=0;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC_CODE -> ${RNDC_CODE}";

                    RETURN_CODE=0;
                else
                    ## failover process has failed. inform.
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover processing on ${GD_SERVER} has failed. Unable to proceed.";

                    RETURN_CODE=${FAILOVER_CODE};
                fi
            else
                ## server access failed
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server appears unavailable -> GD_SERVER -> ${GD_SERVER}";

                unset RET_CODE;
                continue;
            fi
        done
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset UNIT;
    unset FILENAME;
    unset TARGET;
    unset PRJCODE;
    unset CHG_CTRL;
    unset FAILOVER_CODE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=3;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    echo "${THIS_CNAME} - Execute a DNS failover request on the master nameserver based on the provided information.\n";
    echo "Usage: ${THIS_CNAME} [ -s <request data> ] [ -b <request data> ] [ -p <request data> ] [ -d <request data> ] [ -e ] [ -h|-? ]
    -s         -> Process a site failover based on a comma-delimited information set
    -b         -> Process a business unit failover based on a comma-delimited information set
    -p         -> Process a project code failover based on a comma-delimited information set
    -d         -> Process a datacenter failover based on a comma-delimited information set
    -e         -> Execute processing
    -h|-?      -> Show this help\n";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

    return ${RETURN_CODE};
}

[ ${#} -eq 0 ] && usage && RETURN_CODE=${?};

while getopts ":r:s:b:p:d:eh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        r)
            ## set up the service partition. this determines where we go
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            typeset -l PARTITION="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PARTITION -> ${PARTITION}";
            ;;
        s)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            FAILOVER_TYPE="site";

            case ${PARTITION} in
                ${INTRANET_TYPE_IDENTIFIER})
                    SITE_HOSTNAME=$(echo "${OPTARG}" | cut -d "," -f 1);
                    ENABLE_POP=$(echo "${OPTARG}" | cut -d "," -f 2);
                    DISABLE_POP=$(echo "${OPTARG}" | cut -d "," -f 3);
                    CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 6);
                    ;;
                ${INTERNET_TYPE_IDENTIFIER})
                    ## comma-delimited information set, lets strip the "INFO"
                    SITE_HOSTNAME=$(echo "${OPTARG}" | cut -d "," -f 1);
                    UNIT=$(echo "${OPTARG}" | cut -d "," -f 2);
                    FILENAME=$(echo "${OPTARG}" | cut -d "," -f 3);
                    TARGET=$(echo "${OPTARG}" | cut -d "," -f 4);
                    PRJCODE=$(echo "${OPTARG}" | cut -d "," -f 5);
                    CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 6);
                    IUSER_AUDIT=$(echo "${OPTARG}" | cut -d "," -f 7);
                    ;;
            esac

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FAILOVER_TYPE->${FAILOVER_TYPE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILENAME->${FILENAME}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET->${TARGET}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRJCODE->${PRJCODE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT->${IUSER_AUDIT}";
            ;;
        b)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            FAILOVER_TYPE="unit";

            ## comma-delimited information set, lets strip the "INFO"
            UNIT=$(echo "${OPTARG}" | cut -d "," -f 2);
            TARGET=$(echo "${OPTARG}" | cut -d "," -f 3);
            CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 4);
            IUSER_AUDIT=$(echo "${OPTARG}" | cut -d "," -f 5);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FAILOVER_TYPE->${FAILOVER_TYPE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET->${TARGET}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT->${IUSER_AUDIT}";
            ;;
        p)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            FAILOVER_TYPE="project";

            ## comma-delimited information set, lets strip the "INFO"
            PRJCODE=$(echo "${OPTARG}" | cut -d "," -f 2);
            UNIT=$(echo "${OPTARG}" | cut -d "," -f 3);
            TARGET=$(echo "${OPTARG}" | cut -d "," -f 4);
            CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 5);
            IUSER_AUDIT=$(echo "${OPTARG}" | cut -d "," -f 6);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FAILOVER_TYPE->${FAILOVER_TYPE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRJCODE->${PRJCODE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET->${TARGET}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT->${IUSER_AUDIT}";
            ;;
        d)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            FAILOVER_TYPE="datacenter";

            ## comma-delimited information set, lets strip the "INFO"
            TARGET=$(echo "${OPTARG}" | cut -d "," -f 1);
            CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 2);
            IUSER_AUDIT=$(echo "${OPTARG}" | cut -d "," -f 3);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FAILOVER_TYPE->${FAILOVER_TYPE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET->${TARGET}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT->${IUSER_AUDIT}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            case ${FAILOVER_TYPE} in
                site)
                    ## Make sure we have enough information to process
                    ## and execute
                    if [ -z "${UNIT}" ]
                    then
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No business unit was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=15;
                    elif [ -z "${FILENAME}" ]
                    then
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone filename was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=16;
                    elif [ -z "${TARGET}" ]
                    then
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target datacenter was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=17;
                    elif [ -z "${PRJCODE}" ]
                    then
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No project code was    provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=18;
                    elif [ -z "${CHG_CTRL}" ]
                    then
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=19;
                    else
                        ## We have enough information to process the request, continue
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;

                        case ${PARTITION} in
                            ${INTRANET_TYPE_IDENTIFIER})
                                runIntranetSiteFailover && RETURN_CODE=${?};
                                ;;
                            ${INTERNET_TYPE_IDENTIFIER})
                                runInternetSiteFailover && RETURN_CODE=${?};
                                ;;
                        esac
                    fi
                    ;;
                unit)
                    ## Make sure we have enough information to process
                    ## and execute
                    if [ -z "${UNIT}" ]
                    then
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No business unit was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=15;
                    elif [ -z "${TARGET}" ]
                    then
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target datacenter was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=17;
                    elif [ -z "${CHG_CTRL}" ]
                    then
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=19;
                    else
                        ## We have enough information to process the request, continue
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        failover_bu && RETURN_CODE=${?};
                    fi
                    ;;
                datacenter)
                    ## Make sure we have enough information to process
                    ## and execute
                    if [ -z "${TARGET}" ]
                    then
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target datacenter was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset TARGET;
                        unset CHG_CTRL;

                        RETURN_CODE=17;
                    elif [ -z "${CHG_CTRL}" ]
                    then
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset TARGET;
                        unset CHG_CTRL;

                        RETURN_CODE=19;
                    else
                        ## We have enough information to process the request, continue
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        failover_datacenter && RETURN_CODE=${?};
                    fi
                    ;;
                *)
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No information was provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=999;
                    ;;
            esac
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage && RETURN_CODE=${?};
            ;;
    esac
done

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +vx;

[ -z "${RETURN_CODE}" ] && return 1 || return "${RETURN_CODE}";
