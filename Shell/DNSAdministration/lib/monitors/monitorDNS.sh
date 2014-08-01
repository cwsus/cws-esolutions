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

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function monitorProcessPresence
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_NAME -> ${SERVER_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LISTENING_PORT -> ${LISTENING_PORT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "KEYFILE -> ${KEYFILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "COMMAND_NAME -> ${COMMAND_NAME}";

    ## generate the stuff we need
    SVC_VERIFICATION_DOMAIN=$(cut -d ":" -f 1 <<< ${MONITOR_DOMAIN_NAME});
    SVC_VERIFICATION_ADDRESS=$(cut -d ":" -f 2 <<< ${MONITOR_DOMAIN_NAME});

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SVC_VERIFICATION_DOMAIN -> ${SVC_VERIFICATION_DOMAIN}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SVC_VERIFICATION_ADDRESS -> ${SVC_VERIFICATION_ADDRESS}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checking pidfile..";

    ## check the pidfile, if it exists, check the process watch string
    if [ -s "${NAMED_ROOT}"/${NAMED_PID_FILE} ]
    then
        ## ok, pid exists
        NAMED_PID=$(<"${NAMED_ROOT}"/${NAMED_PID_FILE});

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NAMED_PID -> ${NAMED_PID}";

        ## check process
        PROCESS_OUTPUT=$(ps | grep "${NAMED_PROCESS_STRING}" | grep -v grep);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NAMED_PID -> ${NAMED_PID}";

        if [ ! -z "${PROCESS_OUTPUT}" ]
        then
            PROCESS_PID=$(awk '{print $2}' <<< ${PROCESS_OUTPUT});

            if [ ${PROCESS_PID} != ${NAMED_PID} ]
            then
                ## process pid doesnt match whats in the pidfile
                "${LOGGER}" MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PID inconsistency: Process PID: ${PROCESS_PID}; named pidfile PID: ${NAMED_PID}";
            else
                ## pids match, check to see if we can connect via rndc
                $(rndc -s ${SERVER_NAME} -p ${LISTENING_PORT} -y ${KEYFILE} ${COMMAND_NAME} ${ZONEFILE} > /dev/null 2>&1);
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ ${RET_CODE} -eq 0 ]
                then
                    ## ok, named responds to rndc. run a lookup
                    RESPONSE_TXT=$(dig @${SERVER_NAME} +short -t a ${SVC_VERIFICATION_DOMAIN} 2>/dev/null;);

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE_TXT -> ${RESPONSE_TXT}";

                    if [ -z "${RESPONSE_TXT}" ]
                    then
                        ## something broke on the lookup
                        "${LOGGER}" MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No data was returned: queried ${NAMED_PRIMARY_SOA}.${NAMED_INTERNET_SUFFIX}";
                    else
                        if [ "${RESPONSE_TXT}" != "${SVC_VERIFICATION_ADDRESS}" ]
                        then
                            ## response doesnt match what we expect
                            "${LOGGER}" MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Data returned for ${SVC_VERIFICATION_DOMAIN} does not match expected response. Expected: ${SVC_VERIFICATION_ADDRESS}, Received: ${RESPONSE_TXT}";
                        fi
                    fi
                else
                    ## rndc failed
                    "${LOGGER}" MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to connect to running named process - rndc connect failed.";
                fi
            fi
        else
            ## process does not appear to be running
            "${LOGGER}" MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named process does not appear to be running on ${HOSTNAME}";
        fi
    else
        ## pidfile doesnt exist, process may not be running
        "${LOGGER}" MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named process does not appear to be running on ${HOSTNAME}. No pidfile was found.";
    fi

    unset RESPONSE_TXT;
    unset RET_CODE;
    unset PROCESS_PID;
    unset PROCESS_OUTPUT;
    unset NAMED_PID;

    RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function monitorLogEntries
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    if [ ! -z "${MONITOR_LOG_FILES}" ]
    then
        for LOGFILE in ${MONITOR_LOG_FILES}
        do
            WATCH_PERIOD=$(obtainLogWatchStamp);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WATCH_PERIOD -> ${WATCH_PERIOD}";

            ## make sure A is zero
            A=0;

            ## temporarily set IFS (input field separator
            CURR_IFS=${IFS};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Replacing IFS...";

            IFS="^";

            for MONITORED_STRING in ${MONITOR_LOG_STRINGS}
            do
                ERROR_COUNT=$(awk '/'${WATCH_PERIOD}'/,0' "${NAMED_ROOT}"/${NAMED_LOG_DIR}/${LOGFILE} | \
                    grep -c ${MONITORED_STRING});

                if [ ${ERROR_COUNT} -ne 0 ]
                then
                    "${LOGGER}" MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${MONITORED_STRING} found in ${LOGFILE} - please review";
                fi

                ERROR_COUNT=0;

                (( A += 1 ));
            done

            ## set IFS back to what it was
            IFS=${CURR_IFS};

            ## put A back to zero
            A=0;
        done
    else
        "${LOGGER}" "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No logfiles were configured to monitor. Shutting down!";
    fi

    ERROR_COUNT=0;
    unset LOGSTRING;
    unset WATCH_PERIOD;
    unset LOGFILE;

    RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=3;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    echo "${THIS_CNAME} - Execute RNDC (Remote Name Daemon Control) commands against a provided server\n";
    echo "Usage: ${THIS_CNAME} [ -s <server> ] [ -p <port> ] [ -y <keyfile> ] [ -c <command> ] [ -z <zone> ] [ -e ] [ -h|-? ]
    -s         -> The server to execute commands against. If not provided, defaults to localhost
    -p         -> The RNDC listening port. If not provided, defaults to 953.
    -y         -> The RNDC keyfile to utilize. If not provided, defaults to rndc-key
    -c         -> The service command to send. If no command is provided, defaults to status.
    -z         -> The zone to reload. Optional.
    -i         -> The horizon to execute against. Only utilized in a split-horizon configuration.
    -p         -> Perform only process presence monitoring
    -l         -> Perform only log watch monitoring
    -e         -> Execute the request
    -h|-?      -> Show this help\n";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

    return ${RETURN_CODE};
}

[ ${#} -eq 0 ] && usage && RETURN_CODE=${?};

while getopts ":s:p:y:c:z:i:pleh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        s)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SERVER_NAME..";

            ## Capture the business unit
            typeset -l SERVER_NAME="${OPTARG}"; # server to operate against

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_NAME -> ${SERVER_NAME}";
            ;;
        p)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting LISTENING_PORT..";

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
                if [ -z "${OPTARG}" ]
                then
                    RETURN_CODE=50;
                else
                    if [ "${SERVER_NAME}" = "${NAMED_MASTER}" ]
                    then
                        LISTENING_PORT=${RNDC_LOCAL_PORT};
                    elif [ "${SERVER_NAME}" = "${HOSTNAME}" ]
                    then
                        LISTENING_PORT=${RNDC_LOCAL_PORT};
                    else
                        LISTENING_PORT=${RNDC_REMOTE_PORT};
                    fi
                fi
            fi

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LISTENING_PORT -> ${LISTENING_PORT}";
            ;;
        y)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting KEYFILE..";

            ## Capture the target datacenter
            if [ ! -z "OPTARG" ]
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
                    elif [ "${SERVER_NAME}" = "${HOSTNAME}" ]
                    then
                        KEYFILE=${RNDC_LOCAL_KEY};
                    else
                        KEYFILE=${RNDC_REMOTE_KEY};
                    fi
                fi
            fi

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "KEYFILE -> ${KEYFILE}";
            ;;
        c)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting COMMAND_NAME..";

            ## Capture the project code
            case ${OPTARG} in
                reload|refresh|retransfer|reconfig|stats|status|flush)
                    typeset -l COMMAND_NAME="${OPTARG}";
                    ;;
                *)
                    RETURN_CODE=51;
                    ;;
            esac

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "COMMAND_NAME -> ${COMMAND_NAME}";
            ;;
        z)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ZONEFILE..";

            typeset -l ZONEFILE="${OPTARG}"; # This will be the target datacenter to move to

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE -> ${ZONEFILE}";
            ;;
        i)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting HORIZON..";

            typeset -l HORIZON="${OPTARG}"; # This will be the target datacenter to move to

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HORIZON -> ${HORIZON}";
            ;;
        p)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHECK_PROCESS_PRESENCE..";

            CHECK_PROCESS_PRESENCE=${_TRUE};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHECK_PROCESS_PRESENCE -> ${CHECK_PROCESS_PRESENCE}";
            ;;
        l)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHECK_LOG_ENTRIES..";

            CHECK_LOG_ENTRIES=${_TRUE};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHECK_LOG_ENTRIES -> ${CHECK_LOG_ENTRIES}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${SERVER_NAME}" ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Target server not provided. Defaulting to localhost.";

                if [ "${HOSTNAME}" != "${NAMED_MASTER}" ]
                then
                    SERVER_NAME=${HOSTNAME};
                else
                    SERVER_NAME=${NAMED_MASTER};
                fi
            fi

            if [ -z "${COMMAND_NAME}" ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No command was provided. Defaulting to status.";

                COMMAND_NAME=status;
            fi

            if [ -z "${KEYFILE}" ]
            then
                if [ "${SERVER_NAME}" = "${NAMED_MASTER}" ]
                then
                    KEYFILE=${RNDC_LOCAL_KEY};
                else
                    KEYFILE=${RNDC_REMOTE_KEY};
                fi
            fi

            if [ -z "${LISTENING_PORT}" ]
            then
                if [ "${SERVER_NAME}" = "${NAMED_MASTER}" ]
                then
                    LISTENING_PORT=${RNDC_LOCAL_PORT};
                else
                    LISTENING_PORT=${RNDC_REMOTE_PORT};
                fi
            fi

            ## We have enough information to process the request, continue
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            if [ ! -z "${CHECK_LOG_ENTRIES}" ] && [ "${CHECK_LOG_ENTRIES}" = "${_TRUE}" ]
            then
                monitorLogEntries && RETURN_CODE=${?};
            elif [ ! -z "${CHECK_PROCESS_PRESENCE}" ] && [ "${CHECK_PROCESS_PRESENCE}" = "${_TRUE}" ]
            then
                monitorProcessPresence && RETURN_CODE=${?};
            else
                monitorProcessPresence && RETURN_CODE=${?};
                monitorLogEntries && RETURN_CODE=${?};
            fi
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage && RETURN_CODE=${?};
            ;;
    esac
done

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +vx;

[ -z "${RETURN_CODE}" ] && return 1 || return "${RETURN_CODE}";
