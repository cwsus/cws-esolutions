#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  run_decom.sh
#         USAGE:  ./run_decom.sh
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

[[ ! -z "${TRACE}" && "${TRACE}" = "TRUE" ]] && set -x;

## Application constants
[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ -z "${PLUGIN_ROOT_DIR}" && -f ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && echo "Failed to locate configuration data. Cannot continue." && exit 1;

[[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

unset METHOD_NAME;
unset CNAME;

## check security
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Security configuration does not allow the requested action." && exit ${RET_CODE};

## unset the return code
unset RET_CODE;

CNAME="$(basename "${0}")";
METHOD_NAME="${CNAME}#startup";

#===  FUNCTION  ===============================================================
#          NAME:  failover_site
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function decom_site
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT -> ${UNIT}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRJCODE -> ${PRJCODE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_NAME -> ${ZONE_NAME}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL -> ${CHG_CTRL}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";

    ## our request type is p <project code> or u <url>
    ## both can be run against the same script, as its
    ## a single-zone change
    ## spawn an ssh connection to the DNS master
    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution is true. Processing..";

        if [ ! -z "${SLAVE_COPY}" ] && [ "${SLAVE_COPY}" = "${_TRUE}" ]
        then
            ## execute on a slave, provide the necessary options
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Slave server targetted. Processing..";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command execute_decom.sh -b ${UNIT} -p ${PRJCODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -s -e ..";

            unset CNAME;
            unset METHOD_NAME;

            . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/executors/execute_decom.sh -b ${UNIT} -p ${PRJCODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -s -e;

            CNAME="$(basename "${0}")";
            local METHOD_NAME="${CNAME}#${0}";
        else
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command execute_decom.sh -b ${UNIT} -p ${PRJCODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -s -e ..";

            ## temp unset
            unset CNAME;
            unset METHOD_NAME;

            . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/executors/execute_decom.sh -b ${UNIT} -p ${PRJCODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -e;

            CNAME="$(basename "${0}")";
            local METHOD_NAME="${CNAME}#${0}";
        fi
    else
        ## remote exec
        if [ ! -z "${SLAVE_COPY}" ] && [ "${SLAVE_COPY}" = "${_TRUE}" ]
        then
            ## execute on a slave, provide the necessary options
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Slave server targetted. Executing against ${SLAVE_SERVER}..";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${SLAVE_SERVER} ${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_decom.sh -b ${UNIT} -p ${PRJCODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -s -e ..";

            ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${SLAVE_SERVER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_decom.sh -b ${UNIT} -p ${PRJCODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -s -e";
        else
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${SLAVE_SERVER} ${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_decom.sh -b ${UNIT} -p ${PRJCODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -e ..";

            ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_decom.sh -b ${UNIT} -p ${PRJCODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -e";
        fi
    fi

    ## capture the return code from the operations
    RET_CODE=${?};

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Command execution complete. RET_CODE -> ${RET_CODE}";

    if [ ${RET_CODE} -eq 0 ]
    then
        ## failover was successful. lets do some more work...
        unset RET_CODE;
        unset RETURN_CODE;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover completed. Reloading server configuration..";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runRNDCCommands.sh -s ${NAMED_MASTER} -c reload -e..";

        unset CNAME;
        unset METHOD_NAME;

        ## send an rndc reload to the server to make it active
        . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runRNDCCommands.sh -s ${NAMED_MASTER} -c reload -e;
        RNDC_CODE=${?};

        CNAME="$(basename "${0}")";
        local METHOD_NAME="${CNAME}#${0}";

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC_CODE -> ${RNDC_CODE}";

        if [ ${RNDC_CODE} -eq 0 ]
        then
            ## we've successfully reloaded our configuration. verify that the change was indeed made
            unset RNDC_CODE;
            unset RETURN_CODE;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Decommission complete. Sending notifies..";

            unset RETURN_CODE;
            unset RET_CODE;
            unset RETURN_TEXT;

            ## temp unset
            unset CNAME;
            unset METHOD_NAME;

            ## send out a notification email advising of the failover.
            . ${MAILER_CLASS} -m notifyDecommission -p ${PROJECT_CODE} -a "${DNS_SERVER_ADMIN_EMAIL}" -e;

            CNAME="$(basename "${0}")";
            local METHOD_NAME="${CNAME}#${0}";

            ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "$(${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS decommission: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: ${UNIT}/${PRJCODE}/${FILENAME} - Change Request: ${CHG_CTRL} - Switched To: ${TARGET}")";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=0;
        else
            ## our server reload failed. throw an error, we can't recover from this here.
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server reload on ${NAMED_MASTER} has failed. Unable to proceed.";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=52;
        fi
    else
        ## failover process has failed. inform.
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover processing on ${NAMED_MASTER} has failed. Unable to proceed.";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=${RET_CODE};
    fi

    unset UNIT;
    unset FILENAME;
    unset TARGET;
    unset PRJCODE;
    unset CHG_CTRL;
    unset FAILOVER_CODE;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  failover_bu
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function decom_bu
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";

    ## our request type is p <project code> or u <url>
    ## both can be run against the same script, as its
    ## a single-zone change
    ## spawn an ssh connection to the DNS master
    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution is true. Processing..";

        if [ ! -z "${SLAVE_COPY}" ] && [ "${SLAVE_COPY}" = "${_TRUE}" ]
        then
            ## execute on a slave, provide the necessary options
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Slave server targetted. Executing..";

            unset CNAME;
            unset METHOD_NAME;

            . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/executors/execute_decom.sh -b ${UNIT} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -s -e;

            CNAME="$(basename "${0}")";
            local METHOD_NAME="${CNAME}#${0}";
        else
            unset CNAME;
            unset METHOD_NAME;

            . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/executors/execute_decom.sh -b ${UNIT} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -s -e;

            CNAME="$(basename "${0}")";
            local METHOD_NAME="${CNAME}#${0}";
        fi
    else
        ## remote exec
        if [ ! -z "${SLAVE_COPY}" ] && [ "${SLAVE_COPY}" = "${_TRUE}" ]
        then
            ## execute on a slave, provide the necessary options
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Slave server targetted. Executing against ${SLAVE_SERVER}..";

            ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${SLAVE_SERVER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_decom.sh -b ${UNIT} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -s -e";
        else
            ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_decom.sh -b ${UNIT} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e";
        fi
    fi

    ## capture the return code from the operations
    RET_CODE=${?};

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ ${FAILOVER_CODE} -eq 0 ]
    then
        ## failover was successful. lets do some more work...
        unset RET_CODE;
        unset RETURN_CODE;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover completed. Reloading server configuration..";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runRNDCCommands.sh -s ${NAMED_MASTER} -c reload -e..";

        unset CNAME;
        unset METHOD_NAME;

        ## send an rndc reload to the server to make it active
        . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runRNDCCommands.sh -s ${NAMED_MASTER} -c reload -e;
        RNDC_CODE=${?};

        CNAME="$(basename "${0}")";
        local METHOD_NAME="${CNAME}#${0}";

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC_CODE -> ${RNDC_CODE}";

        if [ ${RNDC_CODE} -eq 0 ]
        then
            ## we've successfully reloaded our configuration. verify that the change was indeed made
            unset RNDC_CODE;
            unset RETURN_CODE;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Decommission complete. Sending notifies..";

            unset RETURN_CODE;
            unset RET_CODE;
            unset RETURN_TEXT;

            ## temp unset
            unset CNAME;
            unset METHOD_NAME;

            ## send out a notification email advising of the decom.
            ## only send if we made a change on the master
            if [ -z "${SLAVE_COPY}" ] || [ "${SLAVE_COPY}" != "${_TRUE}" ]
            then
                . ${MAILER_CLASS} -m notifyDecommission -p ${PROJECT_CODE} -a "${DNS_SERVER_ADMIN_EMAIL}" -e;
            fi

            CNAME="$(basename "${0}")";
            local METHOD_NAME="${CNAME}#${0}";

            ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "$(${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS decommission: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: ${UNIT}/${PRJCODE}/${FILENAME} - Change Request: ${CHG_CTRL} - Switched To: ${TARGET}")";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=0;
        else
            ## our server reload failed. throw an error, we can't recover from this here.
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server reload on ${NAMED_MASTER} has failed. Unable to proceed.";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=52;
        fi
    else
        ## failover process has failed. inform.
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover processing on ${NAMED_MASTER} has failed. Unable to proceed.";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=${FAILOVER_CODE};
    fi

    unset UNIT;
    unset FILENAME;
    unset TARGET;
    unset PRJCODE;
    unset CHG_CTRL;
    unset FAILOVER_CODE;

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
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Execute a DNS failover request on the master nameserver based on the provided information.";
    print "Usage: ${CNAME} [ -s <request data> ] [ -b requestdata ] [ -d server ] [ -e execute ] [-?|-h show this help]";
    print "  -s      Process a site failover based on a comma-delimited information set";
    print "  -b      Process a business unit failover based on a comma-delimited information set";
    print "  -d      If provided, operates against a provided slave server";
    print "  -e      Execute processing";
    print "  -h|-?   Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[ ${#} -eq 0 ] && usage;

while getopts ":s:b:d:eh:" OPTIONS
do
    case "${OPTIONS}" in
        s)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            DECOM_TYPE="site";

            ## comma-delimited information set, lets strip the info
            UNIT=$(echo "${OPTARG}" | cut -d "," -f 1);
            PRJCODE=$(echo "${OPTARG}" | cut -d "," -f 2);
            ZONE_NAME=$(echo "${OPTARG}" | cut -d "," -f 3);
            CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 4);
            IUSER_AUDIT=$(echo "${OPTARG}" | cut -d "," -f 5);

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DECOM_TYPE -> ${DECOM_TYPE}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT -> ${UNIT}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRJCODE -> ${PRJCODE}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_NAME -> ${ZONE_NAME}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL -> ${CHG_CTRL}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
            ;;
        b)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            DECOM_TYPE="unit";

            ## comma-delimited information set, lets strip the info
            UNIT=$(echo "${OPTARG}" | cut -d "," -f 2);
            CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 4);
            IUSER_AUDIT=$(echo "${OPTARG}" | cut -d "," -f 5);

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DECOM_TYPE->${DECOM_TYPE}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT->${IUSER_AUDIT}";
            ;;
        d)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SLAVE_COPY to true..";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SLAVE_SYSTEM..";

            SLAVE_COPY=${_TRUE};
            typeset -l SLAVE_SERVER="${OPTARG}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SLAVE_COPY -> ${SLAVE_COPY}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SLAVE_SERVER -> ${SLAVE_SERVER}";
            ;;
        e)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            if [ -z "${UNIT}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No business unit was provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset FAILOVER_TYPE;
                unset UNIT;
                unset FILENAME;
                unset TARGET;
                unset PRJCODE;
                unset CHG_CTRL;

                RETURN_CODE=15;
            elif [ -z "${CHG_CTRL}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset FAILOVER_TYPE;
                unset UNIT;
                unset FILENAME;
                unset TARGET;
                unset PRJCODE;
                unset CHG_CTRL;

                RETURN_CODE=19;
            elif [ -z "${IUSER_AUDIT}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to audit user account. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset FAILOVER_TYPE;
                unset UNIT;
                unset FILENAME;
                unset TARGET;
                unset PRJCODE;
                unset CHG_CTRL;

                RETURN_CODE=20;
            else
                ## We have enough information to process the request, continue
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                if [ ! -z "${DECOM_TYPE}" ]
                then
                    if [ "${DECOM_TYPE}" = "unit" ]
                    then
                        decom_bu;
                    elif [ "${DECOM_TYPE}" = "site" ]
                    then
                        if [ -z "${ZONE_NAME}" ] || [ -z "${PRJCODE}" ]
                        then
                            ## require a zone name for a site decom
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid zone name or project code was not provided. Cannot continue.";

                            RETURN_CODE=3;
                        else
                            decom_site;
                        fi
                    else
                        ## no valid decommission type was provided
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid decommission request type was not provided. Cannot continue.";

                        RETURN_CODE=3;
                    fi
                else
                    ## no decommission type was provided
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No decommission request type was provided. Cannot continue.";

                    RETURN_CODE=3;
                fi
            fi
            ;;
        h|[\?])
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
        *)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done

shift ${OPTIND}-1;

return ${RETURN_CODE};
