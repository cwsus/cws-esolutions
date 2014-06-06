#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  executeRNDCCommands.sh
#         USAGE:  ./executeRNDCCommands.sh
#   DESCRIPTION:  Designed to run as a cron job on a defined bastion host
#                 to provide bi-annually updates (or more often, as desired)
#                 to the root.servers cache file
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

## Application constants
[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ -z "${PLUGIN_ROOT_DIR}" && -f ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && echo "Failed to locate configuration data. Cannot continue." && exit 1;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

unset METHOD_NAME;
unset CNAME;

## check security
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
RET_CODE=${?};

[ ${RET_CODE} -ne 0 ] && echo "Security configuration does not allow the requested action." && echo ${RET_CODE} && exit ${RET_CODE};

## unset the return code
unset RET_CODE;

CNAME="$(basename "${0}")";
METHOD_NAME="${CNAME}#startup";

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function execute_rndc_request
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_NAME -> ${SERVER_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LISTENING_PORT -> ${LISTENING_PORT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "KEYFILE -> ${KEYFILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "COMMAND_NAME -> ${COMMAND_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE -> ${ZONEFILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HORIZON -> ${HORIZON}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command rndc -c ${NAMED_ROOT}/${DNSSEC_ROOT_DIR}/${RNDC_CONF_FILE} -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} ${COMMAND_NAME} ${ZONEFILE}..";

    COMMAND_TIMESTAMP=$(date +"%d-%h-%Y %H:%M");

    ## send out the rndc request. no ssh/scp/anything like that needed here, rndc takes
    ## care of the connectivity for us as long as the firewall allows the connection
    if [ ! -z "${HORIZON}" ]
    then
        RET_CODE=$(rndc -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} ${COMMAND_NAME} ${ZONEFILE} in ${HORIZON} 2>/dev/null);
    else
        RET_CODE=$(rndc -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} ${COMMAND_NAME} ${ZONEFILE} 2>/dev/null);
    fi

    case ${COMMAND_NAME} in
        flush)
            ## for some silly reason flush/stats dont return ANY response, numeric or otherwise, unless
            ## rndc fails to communicate with the server (eg down, firewall blocked, etc)
            ## read in the last 5 lines of the log and look for a completion message
            if [ $(grep "${COMMAND_TIMESTAMP}" ${NAMED_ROOT}/${NAMED_LOG_FILE} | grep -c "flushing caches in all views succeeded") -eq 1 ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC command channel ${COMMAND_NAME} succeeded.";

                RETURN_CODE=0;
            else
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC command channel ${COMMAND_NAME} FAILED.";

                RETURN_CODE=52;
            fi
            ;;
        stats)
            ## for some silly reason flush/stats dont return ANY response, numeric or otherwise, unless
            ## rndc fails to communicate with the server (eg down, firewall blocked, etc)
            ## read in the last 5 lines of the log and look for a completion message
            if [ $(grep "${COMMAND_TIMESTAMP}" ${NAMED_ROOT}/${NAMED_LOG_FILE} | grep -c "dumpstats complete") -eq 1 ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC command channel ${COMMAND_NAME} succeeded.";

                RETURN_CODE=0;
            else
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC command channel ${COMMAND_NAME} FAILED.";

                RETURN_CODE=52;
            fi
            ;;
        reconfig|reload)
            ## for some silly reason flush/stats dont return ANY response, numeric or otherwise, unless
            ## rndc fails to communicate with the server (eg down, firewall blocked, etc)
            ## read in the last 5 lines of the log and look for a completion message
            ## UPDATE: 9.7.3-1 prints output. so lets check for that first, if we cant find it
            ## then fall back to logs
            ## if we're doing a reload, then run a flush too. just makes sense.
            ## we dont need to get the return code back, its not awful important.
            ## not here anyway.
            if [ ! -z "${HORIZON}" ]
            then
                rndc -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} flush 2>/dev/null;
            else
                rndc -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} flush 2>/dev/null;
            fi

            if [ "${RET_CODE}" = "zone reload up-to-date" ] || [ "${RET_CODE}" = "zone reload queued" ]
            then
                ## and return out.
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC command channel ${COMMAND_NAME} succeeded.";

                RETURN_CODE=0;
            elif [ $(grep "${COMMAND_TIMESTAMP}" ${NAMED_ROOT}/${NAMED_LOG_FILE} | grep -c "reloading configuration succeeded") -eq 1 ] ||
                [ $(grep "${COMMAND_TIMESTAMP}" ${NAMED_ROOT}/${NAMED_LOG_FILE} | grep -c "loading configuration from '/etc/named.conf'") -eq 1 ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC command channel ${COMMAND_NAME} succeeded.";

                RETURN_CODE=0;
            else
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC command channel ${COMMAND_NAME} FAILED.";

                RETURN_CODE=52;
            fi
            ;;
        *)
            case ${RETURN_CODE} in
                ?([+-])+([0-9]))
                    ## numeric ret code
                    RETURN_CODE=${RET_CODE};
                    ;;
                *)
                    ## non-numeric return code probably indicates a failure.
                    ## we don't know that its truly a failure. so we're going
                    ## to return the entire string down.
                    RETURN_CODE=$(echo ${RET_CODE} | tr '\n' ' ');
                    ;;
            esac
            ;;
    esac

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Execute RNDC (Remote Name Daemon Control) commands against a provided server";
    print "Usage: ${CNAME} [-s server] [-p port] [-y keyfile] [-c command] [-z zone] [-e] [-h|?]";
    print " -s    -> The server to execute commands against. If not provided, defaults to localhost";
    print " -p    -> The RNDC listening port. If not provided, defaults to 953.";
    print " -y    -> The RNDC keyfile to utilize. If not provided, defaults to rndc-key";
    print " -c    -> The service command to send. If no command is provided, defaults to status.";
    print " -z    -> The zone to reload. Optional.";
    print " -i    -> The horizon to execute against. Only utilized in a split-horizon configuration.";
    print " -e    -> Execute the request";
    print " -h|-? -> Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[ ${#} -eq 0 ] && usage;

while getopts ":s:p:y:c:z:i:eh:" OPTIONS
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
            if [ ! -z "${OPTARG}" ]
            then
                case ${OPTARG} in
                    ?([+-])+([0-9]))
                        LISTENING_PORT="${OPTARG}"; # This will be the source filename
                        ;;
                    *)
                        RETURN_CODE=50;
                        ;;
                esac
            else
                if [ -z "${SERVER_NAME}" ]
                then
                    RETURN_CODE=50;
                else
                    if [ "${SERVER_NAME}" = "${NAMED_MASTER}" ]
                    then
                        LISTENING_PORT=${RNDC_LOCAL_PORT};
                    else
                        LISTENING_PORT=${RNDC_REMOTE_PORT};
                    fi
                fi
            fi

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LISTENING_PORT -> ${LISTENING_PORT}";
            ;;
        y)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting KEYFILE..";

            ## Capture the target datacenter
            if [ ! -z "${OPTARG}" ]
            then
                typeset -l KEYFILE="${OPTARG}"; # This will be the target datacenter to move to
            else
                if [ -z "${SERVER_NAME}" ]
                then
                    RETURN_CODE=50;
                else
                    if [ "${SERVER_NAME}" = "${NAMED_MASTER}" ]
                    then
                        KEYFILE=${RNDC_LOCAL_KEY};
                    else
                        KEYFILE=${RNDC_REMOTE_KEY};
                    fi
                fi
            fi

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
        i)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting HORIZON..";

            typeset -l HORIZON="${OPTARG}"; # This will be the target datacenter to move to

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HORIZON -> ${HORIZON}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${SERVER_NAME}" ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Target server not provided. Defaulting to localhost.";

                SERVER_NAME=${NAMED_MASTER};
            elif [ -z "${COMMAND_NAME}" ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No command was provided. Defaulting to status.";

                COMMAND_NAME=status;
            else
                ## We have enough information to process the request, continue
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                execute_rndc_request;
            fi
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done

shift ${OPTIND}-1;

echo ${RETURN_CODE};
exit ${RETURN_CODE};

