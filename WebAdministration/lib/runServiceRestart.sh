#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  run_service_restart.sh
#         USAGE:  ./run_service_restart.sh server_name
#   DESCRIPTION:  Connects to the provided DNS server and restarts the named
#                 process. Utilized to apply pending changes, or to recycle
#                 the service if required for any other reason.
#
#       OPTIONS:  ---
#  REQUIREMENTS:  ---
#          BUGS:  ---
#         NOTES:  ---
#        AUTHOR:  Kevin Huntly <kmhuntly@gmail.com
#       COMPANY:  CaspersBox Web Services
#       VERSION:  1.0
#       CREATED:  ---
#      REVISION:  ---
#==============================================================================

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

## Application constants
CNAME="${THIS_CNAME}";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#      NAME:  restart_service
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#   RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function restart_service
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested Server: ${SERVER_NAME}";

    ## make sure there isnt an existing ret code
    unset RET_CODE;

    ## make sure we got a valid target and
    ## that we're configured to operate against it
    if [ ! -z "${SERVER_NAME}" ] && [ $(echo ${DNS_SERVERS[@]} | grep -c ${SERVER_NAME}) -eq 1 ]
    then
        ## a server was passed in and it is configured
        ## for use
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Restarting named on ${SERVER_NAME}..";

        if [ ! -z "${RELOAD_KEYS}" ] && [ "${RELOAD_KEYS}" = "${_TRUE}" ]
        then
            ## call out restart_dns.sh - SysV init {reload} to apply the changes
            if [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution is set to TRUE.";

                ## no trace here, this is a bourne shell script
                . ${APP_ROOT}/${LIB_DIRECTORY}/executors/execute_service_restart.sh -c restart -r -e;
            else
                ## MUST execute as root - sudo is best possible option.
                ## this is NOT required if you are configured to ssh as root.
                ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${SERVER_NAME} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_service_restart.sh -c restart -r -e" ${IPLANET_OWNING_USER};
            fi
        else
            if [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution is set to TRUE.";

                ## no trace here, this is a bourne shell script
                . ${APP_ROOT}/${LIB_DIRECTORY}/executors/execute_service_restart.sh -c restart -e;
            else
                ## MUST execute as root - sudo is best possible option.
                ## this is NOT required if you are configured to ssh as root.
                ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${SERVER_NAME} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_service_restart.sh -c restart -e" ${IPLANET_OWNING_USER};
            fi
        fi

        typeset -i RET_CODE=${?};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

        case ${RET_CODE} in
            ?([+-])+([0-9]))
                ## numeric return code
                if [ ${RET_CODE} -eq 0 ]
                then
                    ## successful restart.
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service on ${1} successfully restarted.";
                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named on ${1} restarted by ${IUSER_AUDIT} on $(date +"%m-%d-%Y %H:%M:%S")";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=${RET_CODE};
                else
                    ## restart FAILED.
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named on ${1} restart FAILED. Return code -> ${RET_CODE}";
                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named on ${1} restart attempted by ${IUSER_AUDIT} on $(date +"%m-%d-%Y %H:%M:%S"). Restart FAILED.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=46;
                fi
                ;;
            *)
                ## non-numeric return code. scan through and see whats up.
                ## give it a little love first...
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE non-numeric. Interrogating..";

                RET_CODE=$(echo ${RET_CODE} | tr '\n' ' ' | sed -e 's/[ \t]*$//');

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NAMED_SERVICE_START_TXT -> ${NAMED_SERVICE_START_TXT}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ "${RET_CODE}" = "${NAMED_SERVICE_START_TXT}" ]
                then
                    ## successful restart. log it and return.
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service on ${1} successfully restarted.";
                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named on ${1} restarted by ${IUSER_AUDIT} on $(date +"%m-%d-%Y %H:%M:%S")";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    unset RET_CODE;

                    RETURN_CODE=0;
                else
                    ## something went a little wonky. "ERROR" log it, "AUDIT" log the attempt, and return.
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named on ${1} restart FAILED. Return code -> ${RET_CODE}";
                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named on ${1} restart attempted by ${IUSER_AUDIT} on $(date +"%m-%d-%Y %H:%M:%S"). Restart FAILED.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    unset RET_CODE;

                    RETURN_CODE=46;
                fi
                ;;
        esac
    else
        ## either we were blank or not configured.
        ## this should never get thrown - but just in case..
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${1} is NOT configured or is blank";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=22;
    fi
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    echo "${CNAME} - Execute RNDC (Remote Name Daemon Control) commands against a provided server";
    echo "Usage: ${CNAME} [ -s server ] [ -c command ] [ -r ] [ -e ] [ -h|? ]";
    echo " -r    -> Indicate that the restart is performed to reload RNDC keyfiles.";
    echo " -e    -> Execute the request";
    echo " -h|-? -> Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin ] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

[ ${#} -eq 0 ] && usage&& RETURN_CODE=${?};

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

while getopts ":s:reh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        s)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SERVER_NAME..";

            typeset -l SERVER_NAME="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_NAME -> ${SERVER_NAME}";
            ;;
        r)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting RELOAD_KEYS..";

            RELOAD_KEYS=${_TRUE};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RELOAD_KEYS -> ${RELOAD_KEYS}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${COMMAND_NAME}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No command was provided. Unable to continue.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=7;
            else
                ## We have enough information to process the request, continue
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                restart_service;
            fi
            ;;
        h)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage&& RETURN_CODE=${?};
            ;;
        [\?])
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage&& RETURN_CODE=${?};
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage&& RETURN_CODE=${?};
            ;;
    esac
done


echo ${RETURN_CODE};
exit ${RETURN_CODE};
