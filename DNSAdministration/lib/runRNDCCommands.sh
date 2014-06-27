#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  runRNDCCommands.sh
#         USAGE:  ./runRNDCCommands.sh
#   DESCRIPTION:  Processes backout requests for previously executed change
#                 requests.
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
CNAME="$(basename ${0})";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo -n "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname ${SCRIPT_ABSOLUTE_PATH})";
local METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[[ -z "${PLUGIN_ROOT_DIR}" && -f ${SCRIPT_ROOT}/../lib/plugin.sh ]] && . ${SCRIPT_ROOT}/../lib/plugin.sh;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${PLUGIN_ROOT_DIR}" ] && echo -n "Failed to locate configuration data. Cannot continue." && return 1;

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
local METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
then
    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security violation found while executing ${CNAME} by ${IUSER_AUDIT} on host ${SYSTEM_HOSTNAME}";
    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security configuration blocks execution. Please verify security configuration.";

    echo -n "Security configuration does not allow the requested action.";

    return ${RET_CODE};
fi

#===  FUNCTION  ===============================================================
#          NAME:  run_rndc
#   DESCRIPTION:  Returns a full response from DiG for a provided address
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function runCommandRequest
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";
    local RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_NAME -> ${SERVER_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "COMMAND_NAME -> ${COMMAND_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE -> ${ZONEFILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SPLIT_HORIZON -> ${SPLIT_HORIZON}";

    ## determine our listening port
    if [ -z "${LISTENING_PORT}" ]
    then
        if [ ! -z "${SERVER_NAME}" ] && [ "${SERVER_NAME}" = "${NAMED_MASTER}" ]
        then
            local LISTENING_PORT=${RNDC_LOCAL_PORT};
        else
            local LISTENING_PORT=${RNDC_REMOTE_PORT};
        fi
    fi

    ## determine our keyfile
    if [ -z "${KEYFILE}" ]
    then
        if [ ! -z "${SERVER_NAME}" ] && [ "${SERVER_NAME}" = "${NAMED_MASTER}" ]
        then
            local KEYFILE=${RNDC_LOCAL_KEY};
        else
            local KEYFILE=${RNDC_REMOTE_KEY};
        fi
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LISTENING_PORT -> ${LISTENING_PORT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "KEYFILE -> ${KEYFILE}";

    ## spawn an ssh connection to the provided server to run a DiG query
    ## check to see if we have an internal or external box
    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
    then
        if [ "${SPLIT_HORIZON}" = "${_TRUE}" ]
        then
            case ${COMMAND_NAME} in
                reload|refresh)
                    for HORIZON in ${HORIZONS}
                    do
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HORIZON -> ${HORIZON}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -i ${HORIZON} -e";

                        ${PLUGIN_LIB_DIRECTORY}/executors/executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -i ${HORIZON} -e;
                        RET_CODE=${?};
                    done
                    ;;
                reconfig|stats|status|flush|dumpdb)
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -e";

                    ${PLUGIN_LIB_DIRECTORY}/executors/executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -e;
                    local RET_CODE=${?};
                    ;;
                *)
                    ## unsupported command
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An unsupported RNDC command was provided. Cannot continue.";

                    local CHECK_CODE=1;
                    ;;
            esac
        else
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -e";

            ${PLUGIN_LIB_DIRECTORY}/executors/executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -e;
            local RET_CODE=${?};
        fi
    else
        if [ "${SPLIT_HORIZON}" = "${_TRUE}" ]
        then
            case ${COMMAND_NAME} in
                reload|refresh)
                    for HORIZON in ${HORIZONS}
                    do
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HORIZON -> ${HORIZON}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${SERVER_NAME} \"executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -i ${HORIZON} -e\"";

                        ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${SERVER_NAME} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -i ${HORIZON} -e" ${SSH_USER_NAME} ${SSH_USER_AUTH};
                        local RET_CODE=${?};
                    done
                    ;;
                reconfig|stats|status|flush|dumpdb)
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${SERVER_NAME} \"executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} -e\"";

                    ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${SERVER_NAME} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} -e" ${SSH_USER_NAME} ${SSH_USER_AUTH};
                    local RET_CODE=${?};
                    ;;
                *)
                    ## unsupported command
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An unsupported RNDC command was provided. Cannot continue.";

                    local CHECK_CODE=1;
                    ;;
            esac
        else
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${SERVER_NAME} \"executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -e\"";

            ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${SERVER_NAME} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -e" ${SSH_USER_NAME} ${SSH_USER_AUTH};
            local RET_CODE=${?};
        fi
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    ## make sure we ran a supported command
    if [ -z "${CHECK_CODE}" ] || [ ${CHECK_CODE} -eq 0 ]
    then
        ## got our return code back.
        ## check to see if its numeric,
        ## if not, lets make it numeric
        ## for display in the ui
        case ${COMMAND_NAME} in
            reconfig|reload|stats|flush|refresh)
                ## got a reload command back. we're looking for "successful"
                if [ ${RET_CODE} -eq 0 ]
                then
                    unset RET_CODE;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC command channel ${COMMAND_NAME} successfully executed.";

                    RETURN_CODE=0;
                else
                    unset RET_CODE;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC command channel ${COMMAND_NAME} FAILED. Please inspect the server logs for further detail.";

                    RETURN_CODE=52;
                fi
                ;;
            status)
                ## status command should ALWAYS return text
                RETURN_CODE=99;

                RETURN_TEXT=${RET_CODE};
                ;;
        esac
    else
        RETURN_CODE=1;
    fi

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
#   RETURNS:  0
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";
    local RETURN_CODE=3;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    echo -n "${CNAME} - Performs a DNS query against the nameserver specified.";
    echo -n "Usage: ${CNAME} [ -s server ] [ -p port ] [ -y key ] [ -c command ] [ -z zone ] [ -e ] [ -h|? ]";
    echo -n " -s    -> The server to execute the request against. If no server is provided, defaults to localhost.";
    echo -n " -p    -> The port that the requested server is listening on for requests. If no port is provided, the";
    echo -n "          server provided is interrogated and defaults are obtained from the system configuration.";
    echo -n " -y    -> The RNDC key to utilize for the request. If no key is provided, the server provided is";
    echo -n "          interrogated and defaults are obtained from the system configuration.";
    echo -n " -c    -> The RNDC command to send. If no command is provided, defaults to status.";
    echo -n " -z    -> An optional zone to execute a request against. Useful with commands reload, refresh and retransfer.";
    echo -n " -e    -> Execute the request";
    echo -n " -h|-? -> Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

[ ${#} -eq 0 ] && usage && RETURN_CODE=${?};

while getopts ":s:p:y:c:z:eh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        s)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SERVER_NAME..";

            ## Capture the business unit
            typeset -l SERVER_NAME="${OPTARG}"; # server to operate against

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_NAME -> ${SERVER_NAME}";
            ;;
        p)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting LISTENING_PORT..";

            ## Capture the request filename
            case ${OPTARG} in
                ?([+-])+([0-9]))
                    LISTENING_PORT="${OPTARG}"; # This will be the source filename
                    ;;
                *)
                    ## non-numeric port
                    RETURN_CODE=50;
                    ;;
            esac

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LISTENING_PORT -> ${LISTENING_PORT}";
            ;;
        y)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting KEYFILE..";

            ## Capture the target datacenter
            typeset -l KEYFILE="${OPTARG}"; # This will be the target datacenter to move to

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "KEYFILE -> ${KEYFILE}";
            ;;
        c)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting COMMAND_NAME..";

            ## Capture the project code
            case ${OPTARG} in
                reload|refresh|retransfer|reconfig|stats|status|flush)
                    typeset -l COMMAND_NAME="${OPTARG}";
                    ;;
                *)
                    RETURN_CODE=51;
                    ;;
            esac

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "COMMAND_NAME -> ${COMMAND_NAME}";
            ;;
        z)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ZONEFILE..";

            typeset -l ZONEFILE="${OPTARG}"; # This will be the target datacenter to move to

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE -> ${ZONEFILE}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${SERVER_NAME}" ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Target server not provided. Defaulting to localhost.";

                SERVER_NAME=${NAMED_MASTER};

                continue;
            elif [ -z "${COMMAND_NAME}" ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No command was provided. Defaulting to status.";

                COMMAND_NAME=status;

                continue;
            else
                ## We have enough information to process the request, continue
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                run_rndc;
            fi
            ;;
        h|[\?])
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage && RETURN_CODE=${?};
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage && RETURN_CODE=${?};
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
