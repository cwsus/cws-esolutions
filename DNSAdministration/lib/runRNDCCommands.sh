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

[ ${#} -eq 0 ] && usage;

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
#          NAME:  run_rndc
#   DESCRIPTION:  Returns a full response from DiG for a provided address
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function runCommandRequest
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_NAME -> ${SERVER_NAME}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "COMMAND_NAME -> ${COMMAND_NAME}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE -> ${ZONEFILE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SPLIT_HORIZON -> ${SPLIT_HORIZON}";

    ## determine our listening port
    if [ -z "${LISTENING_PORT}" ]
    then
        if [ ! -z "${SERVER_NAME}" ] && [ "${SERVER_NAME}" = "${NAMED_MASTER}" ]
        then
            LISTENING_PORT=${RNDC_LOCAL_PORT};
        else
            LISTENING_PORT=${RNDC_REMOTE_PORT};
        fi
    fi

    ## determine our keyfile
    if [ -z "${KEYFILE}" ]
    then
        if [ ! -z "${SERVER_NAME}" ] && [ "${SERVER_NAME}" = "${NAMED_MASTER}" ]
        then
            KEYFILE=${RNDC_LOCAL_KEY};
        else
            KEYFILE=${RNDC_REMOTE_KEY};
        fi
    fi

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LISTENING_PORT -> ${LISTENING_PORT}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "KEYFILE -> ${KEYFILE}";

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
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HORIZON -> ${HORIZON}";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -i ${HORIZON} -e";

                        RET_CODE=$(${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/executors/executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -i ${HORIZON} -e);
                    done
                    ;;
                reconfig|stats|status|flush|dumpdb)
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -e";

                    RET_CODE=$(${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/executors/executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -e);
                    ;;
                *)
                    ## unsupported command
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An unsupported RNDC command was provided. Cannot continue.";

                    CHECK_CODE=1;
                    ;;
            esac
        else
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -e";

            RET_CODE=$(${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/executors/executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -e);
        fi
    else
        if [ "${SPLIT_HORIZON}" = "${_TRUE}" ]
        then
            case ${COMMAND_NAME} in
                reload|refresh)
                    for HORIZON in ${HORIZONS}
                    do
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HORIZON -> ${HORIZON}";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${SERVER_NAME} \"executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -i ${HORIZON} -e\"";

                        RET_CODE=$(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${SERVER_NAME} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -i ${HORIZON} -e");
                    done
                    ;;
                reconfig|stats|status|flush|dumpdb)
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${SERVER_NAME} \"executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} -e\"";

                    RET_CODE=$(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${SERVER_NAME} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} -e");
                    ;;
                *)
                    ## unsupported command
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An unsupported RNDC command was provided. Cannot continue.";

                    CHECK_CODE=1;
                    ;;
            esac
        else
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${SERVER_NAME} \"executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -e\"";

            RET_CODE=$(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${SERVER_NAME} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/executeRNDCommands.sh -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} -c ${COMMAND_NAME} ${ZONEFILE} -e");
        fi
    fi

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    ## make sure we ran a supported command
    if [ -z "${CHECK_CODE}" ] || [ ${CHECK_CODE} == 0 ]
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
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC command channel ${COMMAND_NAME} successfully executed.";

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

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Performs a DNS query against the nameserver specified.";
    print "Usage: ${CNAME} [ -s server ] [ -p port ] [ -y key ] [ -c command ] [ -z zone ] [ -e ] [ -h|? ]";
    print " -s    -> The server to execute the request against. If no server is provided, defaults to localhost.";
    print " -p    -> The port that the requested server is listening on for requests. If no port is provided, the";
    print "          server provided is interrogated and defaults are obtained from the system configuration.";
    print " -y    -> The RNDC key to utilize for the request. If no key is provided, the server provided is";
    print "          interrogated and defaults are obtained from the system configuration.";
    print " -c    -> The RNDC command to send. If no command is provided, defaults to status.";
    print " -z    -> An optional zone to execute a request against. Useful with commands reload, refresh and retransfer.";
    print " -e    -> Execute the request";
    print " -h|-? -> Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

while getopts ":s:p:y:c:z:eh:" OPTIONS
do
    case "${OPTIONS}" in
        s)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SERVER_NAME..";

            ## Capture the business unit
            typeset -l SERVER_NAME="${OPTARG}"; # server to operate against

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_NAME -> ${SERVER_NAME}";
            ;;
        p)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting LISTENING_PORT..";

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

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LISTENING_PORT -> ${LISTENING_PORT}";
            ;;
        y)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting KEYFILE..";

            ## Capture the target datacenter
            typeset -l KEYFILE="${OPTARG}"; # This will be the target datacenter to move to

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "KEYFILE -> ${KEYFILE}";
            ;;
        c)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting COMMAND_NAME..";

            ## Capture the project code
            case ${OPTARG} in
                reload|refresh|retransfer|reconfig|stats|status|flush)
                    typeset -l COMMAND_NAME="${OPTARG}";
                    ;;
                *)
                    RETURN_CODE=51;
                    ;;
            esac

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "COMMAND_NAME -> ${COMMAND_NAME}";
            ;;
        z)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ZONEFILE..";

            typeset -l ZONEFILE="${OPTARG}"; # This will be the target datacenter to move to

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE -> ${ZONEFILE}";
            ;;
        e)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${SERVER_NAME}" ]
            then
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Target server not provided. Defaulting to localhost.";

                SERVER_NAME=${NAMED_MASTER};

                continue;
            elif [ -z "${COMMAND_NAME}" ]
            then
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No command was provided. Defaulting to status.";

                COMMAND_NAME=status;

                continue;
            else
                ## We have enough information to process the request, continue
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                run_rndc;
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
