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

[ ${RET_CODE} != 0 ] && echo "Security configuration does not allow the requested action." && echo ${RET_CODE} && exit ${RET_CODE};

## unset the return code
unset RET_CODE;

## lock it
${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/lock.sh lock ${$};
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Application currently in use." && echo ${RET_CODE} && exit ${RET_CODE};

unset RET_CODE;

CNAME="$(basename "${0}")";
METHOD_NAME="${CNAME}#startup";

trap "${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/lock.sh unlock ${$}; exit" INT TERM EXIT;

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function execute_service_command
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "COMMAND_NAME -> ${COMMAND_NAME}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing service command ${COMMAND_NAME}..";

    case ${COMMAND_NAME} in
        stop)
            ## shut down the service.
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Shutting down DNS services in $(hostname)..";

            ## sudo is not required to stop the server
            ${NAMED_INIT_SCRIPT} ${COMMAND_NAME};
            RET_CODE=${?};

            if [ $(echo ${RET_CODE} | grep "${NAMED_SERVICE_STOP_TXT}" | grep -c ${_OK}) -eq 1 ]
            then
                ## service was successfully stopped. we can confirm by checking a few things...
                ## the pid file
                ## the pid
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service shutdown complete. Verifying..";

                ## pause for a bit to let the pid file go away..
                sleep "${MESSAGE_DELAY}";

                if [ -s ${NAMED_ROOT}/${NAMED_PID_FILE} ]
                then
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named pidfile still exists. Checking for validity..";

                    NAMED_PID=$(cat ${NAMED_ROOT}/${NAMED_PID_FILE});

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NAMED_PID -> ${NAMED_PID}";

                    if [ ! -z "${NAMED_PID}" ]
                    then
                        PROC_COUNT=$(ps | grep ${NAMED_PID} | grep -v grep | wc -l);

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROC_COUNT -> ${PROC_COUNT}";

                        if [ ${PROC_COUNT} -eq 0 ]
                        then
                            ## service was indeed shut down
                            ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named services shut down by ${IUSER_AUDIT}";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            RETURN_CODE=0;
                        else
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service shut down FAILED. Please try again.";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            RETURN_CODE=91;
                        fi
                    else
                        ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named services shut down by ${IUSER_AUDIT}";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        RETURN_CODE=0;
                    fi
                else
                    ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named services shut down by ${IUSER_AUDIT}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=0;
                fi
            else
                ## service failed to stop. notify
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service shut down FAILED. Please try again.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=91;
            fi
            ;;
        start|restart)
            if [ "${COMMAND_NAME}" = "start" ]
            then
                ## shut down the service.
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Starting up DNS services in $(hostname)..";

                if [ ! -z "${IS_SUDO_REQUIRED}" ] && [ "${IS_SUDO_REQUIRED}" = "${_TRUE}" ]
                then
                    ${NAMED_INIT_SCRIPT} ${COMMAND_NAME};
                else
                    sudo ${NAMED_INIT_SCRIPT} ${COMMAND_NAME};
                fi
                RET_CODE=${?};

                if [ $(echo ${RET_CODE} | grep "${NAMED_SERVICE_START_TXT}" | grep -c ${_OK}) -eq 1 ]
                then
                    ## service was successfully started. we can confirm by checking a few things...
                    ## the pid file
                    ## the pid
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service startup complete. Verifying..";

                    if [ -s ${NAMED_ROOT}/${NAMED_PID_FILE} ]
                    then
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named pidfile exists. Checking for validity..";

                        NAMED_PID=$(cat ${NAMED_ROOT}/${NAMED_PID_FILE});

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NAMED_PID -> ${NAMED_PID}";

                        if [ ! -z "${NAMED_PID}" ]
                        then
                            PROC_COUNT=$(ps | grep ${NAMED_PID} | grep -v grep | wc -l);

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROC_COUNT -> ${PROC_COUNT}";

                            if [ ${PROC_COUNT} -eq 1 ]
                            then
                                ## service was indeed shut down
                                ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named services started by ${IUSER_AUDIT}";
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                RETURN_CODE=0;
                            else
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service startup FAILED. Please try again.";
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                RETURN_CODE=91;
                            fi
                        else
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service startup FAILED. Please try again.";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            RETURN_CODE=46;
                        fi
                    else
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service startup FAILED. Please try again.";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        RETURN_CODE=46;
                    fi
                else
                    ## service failed to start. notify
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service startup FAILED. Please try again.";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=46;
                fi
            elif [ "${COMMAND_NAME}" = "restart" ]
            then
                if [ ! -z "${RELOAD_KEYS}" ] && [ "${RELOAD_KEYS}" = "${_TRUE}" ]
                then
                    ## we're restarting to reload rndc keyfiles. kill the pid,
                    ## clear the file, and then start up.
                    if [ -s ${NAMED_ROOT}/${NAMED_PID_FILE} ]
                    then
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing restart for key reload.";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Grabbing PID..";

                        NAMED_PID=$(cat ${NAMED_ROOT}/${NAMED_PID_FILE});

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NAMED_PID -> ${NAMED_PID}";

                        kill -9 ${NAMED_PID};

                        if [ $(ps | grep -c ${NAMED_PID} | grep -v grep) == 0 ]
                        then
                            ## kill failed. notify
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to kill PID ${NAMED_PID}. Cannot continue.";

                            RETURN_CODE=46;
                        else
                            ## pid killed, start the service
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PID ${NAMED_PID} killed. Starting service..";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command sudo ${NAMED_INIT_SCRIPT} start ..";

                            if [ ! -z "${IS_SUDO_REQUIRED}" ] && [ "${IS_SUDO_REQUIRED}" = "${_TRUE}" ]
                            then
                                ${NAMED_INIT_SCRIPT} start;
                            else
                                sudo ${NAMED_INIT_SCRIPT} start;
                            fi
                            RET_CODE=${?};

                            if [ $(echo ${RET_CODE} | grep "${NAMED_SERVICE_START_TXT}" | grep -c ${_OK}) -eq 1 ]
                            then
                                ## service was successfully started. we can confirm by checking a few things...
                                ## the pid file
                                ## the pid
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service startup complete. Verifying..";

                                if [ -s ${NAMED_ROOT}/${NAMED_PID_FILE} ]
                                then
                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named pidfile exists. Checking for validity..";

                                    NAMED_PID=$(cat ${NAMED_ROOT}/${NAMED_PID_FILE});

                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NAMED_PID -> ${NAMED_PID}";

                                    if [ ! -z "${NAMED_PID}" ]
                                    then
                                        PROC_COUNT=$(ps | grep ${NAMED_PID} | grep -v grep | wc -l);

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROC_COUNT -> ${PROC_COUNT}";

                                        if [ ${PROC_COUNT} -eq 1 ]
                                        then
                                            ## service was indeed shut down
                                            ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named services started by ${IUSER_AUDIT}";
                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                            RETURN_CODE=0;
                                        else
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service startup FAILED. Please try again.";
                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                            RETURN_CODE=91;
                                        fi
                                    else
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service startup FAILED. Please try again.";
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                        RETURN_CODE=46;
                                    fi
                                else
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service startup FAILED. Please try again.";
                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                    RETURN_CODE=46;
                                fi
                            else
                                ## service failed to start. notify
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service startup FAILED. Please try again.";
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                RETURN_CODE=46;
                            fi
                        fi
                    else
                        ## return an error, theres no pidfile here
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${NAMED_PID_FILE} does not exist. Cannot continue.";

                        RETURN_CODE=46;
                    fi
                else
                    ## we've been asked to restart. we're going to call back to ourself for this
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing request to stop services..";

                    ${NAMED_INIT_SCRIPT} stop;
                    RET_CODE=${?};

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Stop request issued. RET_CODE -> ${RET_CODE}";

                    if [ ${RET_CODE} -eq 0 ]
                    then
                        unset RET_CODE;
                        unset RETURN_CODE;

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing request to start services..";

                        ## service successfully stopped. start it up
                        if [ ! -z "${IS_SUDO_REQUIRED}" ] && [ "${IS_SUDO_REQUIRED}" = "${_TRUE}" ]
                        then
                            ${NAMED_INIT_SCRIPT} start;
                        else
                            sudo ${NAMED_INIT_SCRIPT} start;
                        fi
                        RET_CODE=${?};

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Start request issued. RET_CODE -> ${RET_CODE}";

                        if [ ${RET_CODE} -eq 0 ]
                        then
                            ## done. return 0
                            ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named services restarted on $(hostname) by ${IUSER_AUDIT}";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            RETURN_CODE=0;
                        else
                            ## stop request failed
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service control FAILED. Please try again.";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            RETURN_CODE=${RET_CODE};
                        fi
                    else
                        ## start request failed.
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service control FAILED. Please try again.";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        RETURN_CODE=${RET_CODE};
                    fi
                fi
            else
                ## command provided is not valid for this block
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Command request received was ${COMMAND_NAME}. This is invalid for this method.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=7;
            fi
            ;;
        reload)
            ## send a service reload. this will just reload all the zones
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reloading services on $(hostname)..";

            sudo ${NAMED_INIT_SCRIPT} ${COMMAND_NAME};
            RET_CODE=${?};

            if [ $(echo ${RET_CODE} | grep -c "${NAMED_SERVICE_RELOAD_TXT}") -eq 1 ]
            then
                ## service was successfully reloaded. we can confirm by checking a few things...
                ## the pid file
                ## the pid
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service shutdown complete. Verifying..";

                ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named services shut down by ${IUSER_AUDIT}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=0;
            else
                ## service failed to reload. notify
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named services failed to start up. Please try again.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=92;
            fi
            ;;
    esac
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function usage
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Execute RNDC (Remote Name Daemon Control) commands against a provided server";
    print "Usage: ${CNAME} [ -c command ] [ -r ] [ -e ] [ -h|? ]";
    print " -c    -> The service command to send. If no command is provided, defaults to status.";
    print " -r    -> Indicate that the restart is performed to reload RNDC keyfiles.";
    print " -e    -> Execute the request";
    print " -h|-? -> Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

while getopts ":c:reh:" OPTIONS
do
    case "${OPTIONS}" in
        c)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting COMMAND_NAME..";

            if [ -z "${OPTARG}" ]
            then
                COMMAND_NAME=status;
            else
                ## Capture the project code
                case ${OPTARG} in
                    start|stop|status|restart|reload)
                        typeset -l COMMAND_NAME="${OPTARG}";
                        ;;
                    *)
                        RETURN_CODE=51;
                        ;;
                esac
            fi

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "COMMAND_NAME -> ${COMMAND_NAME}";
            ;;
        r)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting RELOAD_KEYS..";

            RELOAD_KEYS=${_TRUE};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RELOAD_KEYS -> ${RELOAD_KEYS}";
            ;;
        e)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${COMMAND_NAME}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No command was provided. Unable to continue.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=7;
            else
                ## We have enough information to process the request, continue
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                execute_service_command;
            fi
            ;;
        *)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done

shift ${OPTIND}-1;

echo ${RETURN_CODE};
exit ${RETURN_CODE};
