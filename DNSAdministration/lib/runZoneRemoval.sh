#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  run_removal.sh
#         USAGE:  ./run_removal.sh
#   DESCRIPTION:  Executes processing against a master nameserver to remove
#                 a zone or business unit.
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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

## Application constants
CNAME="$(/usr/bin/env basename ${0})";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; /usr/bin/env echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname ${SCRIPT_ABSOLUTE_PATH})";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -f ${SCRIPT_ROOT}/../lib/plugin ] && . ${SCRIPT_ROOT}/../lib/plugin;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${PLUGIN_ROOT_DIR}" ] && /usr/bin/env echo "Failed to locate configuration data. Cannot continue." && return 1;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -f ${APP_ROOT}/${LIB_DIRECTORY}/aliases ] && . ${APP_ROOT}/${LIB_DIRECTORY}/aliases;
[ -f ${APP_ROOT}/${LIB_DIRECTORY}/functions ] && . ${APP_ROOT}/${LIB_DIRECTORY}/functions;
[ -s ${PLUGIN_LIB_DIRECTORY}/aliases ] && . ${PLUGIN_LIB_DIRECTORY}/aliases;
[ -s ${PLUGIN_LIB_DIRECTORY}/functions ] && . ${PLUGIN_LIB_DIRECTORY}/functions;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

THIS_CNAME="${CNAME}";
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

## validate the input
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

CNAME="${THIS_CNAME}";
typeset METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
then
    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security violation found while executing ${CNAME} by ${IUSER_AUDIT} on host ${SYSTEM_HOSTNAME}";
    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security configuration blocks execution. Please verify security configuration.";

    echo "Security configuration does not allow the requested action.";

    return ${RET_CODE};
fi

#===  FUNCTION  ===============================================================
#          NAME:  decom_site
#   DESCRIPTION:  Decommissions a provided site in the DNS service
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function decom_site
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT -> ${UNIT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILENAME -> ${FILENAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRJCODE -> ${PRJCODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL -> ${CHG_CTRL}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";

    ## our request type is p <project code> or u <url>
    ## both can be run against the same script, as its
    ## a single-zone change
    ## spawn an ssh connection to the DNS master
    if [ ! -z "${LOCAL_EXECUTION}" ] && [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution is true. Processing..";

        THIS_CNAME="${CNAME}";
        unset METHOD_NAME;
        unset CNAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        ## validate the input
        ${PLUGIN_LIB_DIRECTORY}/executors/executeDecommission.sh -b ${UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -s -e;
        typeset -i RET_CODE=${?};

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

        CNAME="${THIS_CNAME}";
        typeset METHOD_NAME="${THIS_CNAME}#${0}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
    fi

    ## capture the return code from the operations
    typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ ${RET_CODE} -eq 0 ]
    then
        ## failover was successful. lets do some more work...
        unset RET_CODE;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover completed. Reloading server configuration..";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runRNDCCommands.sh -s ${NAMED_MASTER} -c reload -e..";

        THIS_CNAME="${CNAME}";
        unset METHOD_NAME;
        unset CNAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        ## validate the input
        ${PLUGIN_LIB_DIRECTORY}/runRNDCCommands.sh -s ${NAMED_MASTER} -c reload -e;
        typeset -i RET_CODE=${?};

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

        CNAME="${THIS_CNAME}";
        typeset METHOD_NAME="${THIS_CNAME}#${0}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

        if [ ${RET_CODE} -eq 0 ]
        then
            ## we've successfully reloaded our configuration. verify that the change was indeed made
            unset RET_CODE;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Decommission complete. Sending notifies..";

            unset RET_CODE;
            unset RETURN_TEXT;

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

            ## validate the input
            ${MAILER_CLASS} -m notifyDecommission -p ${PROJECT_CODE} -a "${DNS_SERVER_ADMIN_EMAIL}" -e;
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS decommission: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: ${UNIT}/${PRJCODE}/${FILENAME} - Change Request: ${CHG_CTRL} - Switched To: ${TARGET}")";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=0;
        else
            ## our server reload failed. throw an "ERROR", we can't recover from this here.
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server reload on ${NAMED_MASTER} has failed. Unable to proceed.";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=52;
        fi
    else
        ## failover process has failed. inform.
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover processing on ${NAMED_MASTER} has failed. Unable to proceed.";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=${FAILOVER_CODE};
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    unset UNIT;
    unset FILENAME;
    unset TARGET;
    unset PRJCODE;
    unset CHG_CTRL;
    unset FAILOVER_CODE;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  decom_bu
#   DESCRIPTION:  Decommissions a business unit and all services therein
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function decom_bu
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";

    ## our request type is p <project code> or u <url>
    ## both can be run against the same script, as its
    ## a single-zone change
    ## spawn an ssh connection to the DNS master
    if [ ! -z "${LOCAL_EXECUTION}" ] && [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution is true. Processing..";

        if [[ ! -z "${SLAVE_COPY}" && "${SLAVE_COPY}" = "${_TRUE}" ]]
        then
            ## execute on a slave, provide the necessary options
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Slave server targetted. Executing..";

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

            ## validate the input
            ${PLUGIN_LIB_DIRECTORY}/executors/execute_decom.sh -b ${UNIT} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e;
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
        else
            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

            ## validate the input
            ${PLUGIN_LIB_DIRECTORY}/executors/execute_decom.sh -b ${UNIT} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -s -e;
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
        fi
    else
        ## remote exec
        if [[ ! -z "${SLAVE_COPY}" && "${SLAVE_COPY}" = "${_TRUE}" ]]
        then
            ## execute on a slave, provide the necessary options
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Slave server targetted. Executing against ${SLAVE_SERVER}..";

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

            ## validate the input
            ssh ${SLAVE_SERVER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/execute_decom.sh -b ${UNIT} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -s -e";
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
        else
            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

            ## validate the input
            ssh ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/execute_decom.sh -b ${UNIT} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e";
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
        fi
    fi

    ## capture the return code from the operations
    typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ ${RET_CODE} -eq 0 ]
    then
        ## failover was successful. lets do some more work...
        unset RET_CODE;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover completed. Reloading server configuration..";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runRNDCCommands.sh -s ${NAMED_MASTER} -c reload -e..";

        THIS_CNAME="${CNAME}";
        unset METHOD_NAME;
        unset CNAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        ## validate the input
        ${PLUGIN_LIB_DIRECTORY}/runRNDCCommands.sh -s ${NAMED_MASTER} -c reload -e;
        ssh ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/execute_decom.sh -b ${UNIT} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e";
        typeset -i RET_CODE=${?};

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

        CNAME="${THIS_CNAME}";
        typeset METHOD_NAME="${THIS_CNAME}#${0}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

        if [ ${RET_CODE} -eq 0 ]
        then
            ## we've successfully reloaded our configuration. verify that the change was indeed made
            unset RNDC_CODE;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Decommission complete. Sending notifies..";

            unset RET_CODE;
            unset RETURN_TEXT;

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

            ## validate the input
            ${MAILER_CLASS} -m notifyDecommission -p ${PROJECT_CODE} -a "${DNS_SERVER_ADMIN_EMAIL}" -e;
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS decommission: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: ${UNIT}/${PRJCODE}/${FILENAME} - Change Request: ${CHG_CTRL} - Switched To: ${TARGET}")";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=0;
        else
            ## our server reload failed. throw an "ERROR", we can't recover from this here.
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server reload on ${NAMED_MASTER} has failed. Unable to proceed.";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=52;
        fi
    else
        ## failover process has failed. inform.
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover processing on ${NAMED_MASTER} has failed. Unable to proceed.";

        RETURN_CODE=${FAILOVER_CODE};
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    unset UNIT;
    unset FILENAME;
    unset TARGET;
    unset PRJCODE;
    unset CHG_CTRL;
    unset FAILOVER_CODE;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  remove_zone_entry
#   DESCRIPTION:  Removes a record from the provided zonefile
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function remove_zone_entry
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=3;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    echo "${CNAME} - Execute a DNS decommission or removal. All arguments require a comma-delimited information set to properly process.\n";
    echo "Usage: ${CNAME} [ -s <request data> ] [ -b <request data> ] [ -p <request data> ] [ -x <request data> ] [ -e <execute> ] [ -h|-? ]
    -s         -> Processes a site decommission.
    -b         -> Processes a business unit decommission
    -p         -> Processes a project code decommission
    -x         -> Removes a provided entry from an existing zone.
    -e         -> Execute processing
    -h|-?      -> Show this help\n";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

[ ${#} -eq 0 ] && usage&& RETURN_CODE=${?};

while getopts ":s:b:p:x:eh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        s)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            DECOM_TYPE="site";

            ## comma-delimited information set, lets strip the "INFO"
            SITE_HOSTNAME=$(echo "${OPTARG}" | cut -d "," -f 1);
            UNIT=$(echo "${OPTARG}" | cut -d "," -f 2);
            FILENAME=$(echo "${OPTARG}" | cut -d "," -f 3);
            PRJCODE=$(echo "${OPTARG}" | cut -d "," -f 5);
            CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 6);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DECOM_TYPE->${DECOM_TYPE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILENAME->${FILENAME}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRJCODE->${PRJCODE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT->${IUSER_AUDIT}";
            ;;
        b)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            DECOM_TYPE="unit";

            ## comma-delimited information set, lets strip the "INFO"
            UNIT=$(echo "${OPTARG}" | cut -d "," -f 2);
            CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 4);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DECOM_TYPE->${DECOM_TYPE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT->${IUSER_AUDIT}";
            ;;
        x)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            DECOM_TYPE="entry";

            ## comma-delimited information set, lets strip the "INFO"
            SITE_HOSTNAME=$(echo "${OPTARG}" | cut -d "," -f 1);
            UNIT=$(echo "${OPTARG}" | cut -d "," -f 2);
            FILENAME=$(echo "${OPTARG}" | cut -d "," -f 3);
            PRJCODE=$(echo "${OPTARG}" | cut -d "," -f 5);
            CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 6);
            ZONE_ENTRY=$(echo "${OPTARG}" | cut -d "," -f 8);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DECOM_TYPE->${DECOM_TYPE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILENAME->${FILENAME}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRJCODE->${PRJCODE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT->${IUSER_AUDIT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_ENTRY->${ZONE_ENTRY}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            case ${DECOM_TYPE} in
                site)
                    ## Make sure we have enough information to process
                    ## and execute
                    if [ -z "${UNIT}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No business unit was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=15;
                    elif [ -z "${FILENAME}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone filename was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=16;
                    elif [ -z "${TARGET}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target datacenter was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=17;
                    elif [ -z "${PRJCODE}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No project code was    provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=18;
                    elif [ -z "${CHG_CTRL}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=19;
                    elif [ -z "${IUSER_AUDIT}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to "AUDIT" user account. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=20;
                    else
                        ## We have enough information to process the request, continue
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        decom_site&& RETURN_CODE=${?};
                    fi
                    ;;
                unit)
                    ## Make sure we have enough information to process
                    ## and execute
                    if [ -z "${UNIT}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No business unit was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=15;
                    elif [ -z "${TARGET}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target datacenter was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=17;
                    elif [ -z "${CHG_CTRL}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=19;
                    elif [ -z "${IUSER_AUDIT}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to "AUDIT" user account. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=20;
                    else
                        ## We have enough information to process the request, continue
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        decom_bu&& RETURN_CODE=${?};
                    fi
                    ;;
                project)
                    ## Make sure we have enough information to process
                    ## and execute
                    if [ -z "${TARGET}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target datacenter was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset TARGET;
                        unset CHG_CTRL;

                        RETURN_CODE=17;
                    elif [ -z "${CHG_CTRL}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset TARGET;
                        unset CHG_CTRL;

                        RETURN_CODE=19;
                    elif [ -z "${IUSER_AUDIT}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to "AUDIT" user account. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset TARGET;
                        unset CHG_CTRL;

                        RETURN_CODE=20;
                    else
                        ## We have enough information to process the request, continue
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        decom_project&& RETURN_CODE=${?};
                    fi
                    ;;
                entry)
                    ## Make sure we have enough information to process
                    ## and execute
                    if [ -z "${UNIT}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No business unit was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=15;
                    elif [ -z "${FILENAME}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone filename was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=16;
                    elif [ -z "${TARGET}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target datacenter was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=17;
                    elif [ -z "${PRJCODE}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No project code was    provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=18;
                    elif [ -z "${CHG_CTRL}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=19;
                    elif [ -z "${IUSER_AUDIT}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to "AUDIT" user account. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=20;
                    elif [ -z "${ZONE_ENTRY}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone entry was provided to remove. Cannot continue.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset DECOM_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=28;
                    else
                        ## We have enough information to process the request, continue
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        decom_site&& RETURN_CODE=${?};
                    fi
                    ;;
                *)
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No information was provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    usage&& RETURN_CODE=${?};
                    ;;
            esac
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage&& RETURN_CODE=${?};
            ;;
    esac
done

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

[ -z "${RETURN_CODE}" ] && return 1 || return "${RETURN_CODE}";
