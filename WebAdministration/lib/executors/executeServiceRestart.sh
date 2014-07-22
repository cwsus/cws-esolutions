#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  executeServiceRestart.sh
#         USAGE:  ./executeServiceRestart.sh instance command
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
CNAME="$(/usr/bin/env basename ${0})";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; /usr/bin/env echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(/usr/bin/env dirname ${SCRIPT_ABSOLUTE_PATH})";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

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
#          NAME:  serviceControl
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function serviceControl
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    INSTANCE_NAME=${1};
    COMMAND_NAME=${2};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "INSTANCE_NAME -> ${INSTANCE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "COMMAND_NAME -> ${COMMAND_NAME}";

    ## ok, lets sort out where we are and what to look for
    if [ ! -z "${WS_PLATFORM}" ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WS_PLATFORM -> ${WS_PLATFORM}";

        if [ "${WS_PLATFORM}" = "${IPLANET_TYPE_IDENTIFIER}" ]
        then
            if [ -d ${IPLANET_ROOT}/${INSTANCE_NAME} ]
            then
                ## instance provided is valid for this server
                if [ $(grep -c ${INSTANCE_NAME} ${APP_ROOT}/${CORE_EXCEPTION_LIST}) -eq 0 ] \
                        && [ $(grep -c ${INSTANCE_NAME} ${APP_ROOT}/${TMP_EXCEPTION_LIST}) -eq 0 ] \
                        && [ $(echo ${IPLANET_STARTUP_IGNORE_LIST} | grep -c ${INSTANCE_NAME}) -eq 0 ]
                then
                    ## pull out the port number. it drives what we alert
                    ## get the pid (if it exists) too
                    SITE_PORT_NUMBER=$(grep -w "${IPLANET_PORT_IDENTIFIER}" \
                        ${IPLANET_ROOT}/${INSTANCE_NAME}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG} | \
                        sed -e "s/${IPLANET_PORT_IDENTIFIER}=/@/" | \
                        cut -d "@" -f 2 | awk '{print $1}' | sed -e "s/\"//g");
                    PID_LOG_FILE=$(grep -w ${IPLANET_PID_IDENTIFIER} ${IPLANET_ROOT}/${INSTANCE_NAME}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG} | awk '{print $2}');

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_PORT_NUMBER -> ${SITE_PORT_NUMBER}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PID_LOG_FILE -> ${PID_LOG_FILE}";

                    ## see if the pidfile exists, if it does, see if theres a pid in it
                    if [ -s ${PID_LOG_FILE} ]
                    then
                        SERVICE_PID=$(cat ${PID_LOG_FILE});

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_PID -> ${SERVICE_PID}";

                        PROCESS_OUTPUT=$(ps | grep ${SERVICE_PID} | grep -v grep | grep -v ${CNAME});

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_OUTPUT -> ${PROCESS_OUTPUT}";

                        if [ ! -z "${PROCESS_OUTPUT}" ]
                        then
                            ## server is running, check to see who its running as
                            IS_RUNNING=${_TRUE};
                        else
                            ## process doesnt appear to be running.
                            IS_RUNNING=${_FALSE};
                        fi
                    else
                        ## no pidfile, check to see if theres a running process
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No pid file was located. Checking for process..";

                        PROCESS_OUTPUT=$(ps | grep ${INSTANCE_NAME} | grep -v grep | grep -v ${CNAME});

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_OUTPUT -> ${PROCESS_OUTPUT}";

                        if [ ! -z "${PROCESS_OUTPUT}" ]
                        then
                            ## server is running, check to see who its running as
                            IS_RUNNING=${_TRUE};
                        else
                            ## process doesnt appear to be running.
                            IS_RUNNING=${_FALSE};
                        fi
                    fi

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IS_RUNNING -> ${IS_RUNNING}";

                    if [ ! -z "${IS_RUNNING}" ]
                    then
                        ## ok, we can process
                        ## available commands
                        case ${COMMAND_NAME} in
                            start)
                                if [ "${IS_RUNNING}" = "${_TRUE}" ]
                                then
                                    ## service is already running, we cant start it
                                    RETURN_CODE=39;
                                else
                                    ## service isnt running, start it up
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${INSTANCE_NAME} does not appear to be running. Restarting..";

                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                                    if [ ${SITE_PORT_NUMBER} -lt ${HIGH_PRIVILEGED_PORT} ]
                                    then
                                        ## need to use sudo here to start the web
                                        STARTUP_OUTPUT=$( { sudo ${IPLANET_SUDO_START_WEB} ${IPLANET_ROOT} ${INSTANCE_NAME}; } 2>&1 )
                                    else
                                        STARTUP_OUTPUT=$( { ${IPLANET_ROOT}/${INSTANCE_NAME}/${IPLANET_START_SCRIPT}; } 2>&1 )
                                    fi


                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "STARTUP_OUTPUT -> ${STARTUP_OUTPUT}";

                                    PID_LOG_FILE=$(grep -w ${IPLANET_PID_IDENTIFIER} ${IPLANET_ROOT}/${INSTANCE_NAME}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG} | awk '{print $2}');

                                    if [ -s ${PID_LOG_FILE} ]
                                    then
                                        SERVICE_PID=$(cat ${PID_LOG_FILE});

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PID_LOG_FILE -> ${PID_LOG_FILE}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_PID -> ${SERVICE_PID}";

                                        PROCESS_OUTPUT=$(ps -auxwww | grep -w ${SERVICE_PID} | grep -v grep);

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_OUTPUT -> ${PROCESS_OUTPUT}";

                                        if [ -z "${PROCESS_OUTPUT}" ]
                                        then
                                            ## server failed to properly start
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${INSTANCE_NAME} appears to be down. Restart attempt has failed: ${STARTUP_OUTPUT}";

                                            RETURN_CODE=40;
                                        else
                                            ${LOGGER} "INFO" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${INSTANCE_NAME} successfully started.";

                                            RETURN_CODE=0;
                                        fi
                                    else
                                        ## server failed to start
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${INSTANCE_NAME} startup attempt has failed: ${PID_LOG_FILE} does not exist.";

                                        RETURN_CODE=40;
                                    fi
                                fi
                                ;;
                            stop)
                                if [ "${IS_RUNNING}" = "${_FALSE}" ]
                                then
                                    ## service is already stopped, we cant stop it
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${INSTANCE_NAME} has already been stopped. Please try again.";

                                    RETURN_CODE=39;
                                else
                                    ## service isnt running, start it up
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Stopping ${INSTANCE_NAME} ..";

                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                                    if [ ${SITE_PORT_NUMBER} -lt ${HIGH_PRIVILEGED_PORT} ]
                                    then
                                        ## need to use sudo here to stop the web
                                        SHUTDOWN_OUTPUT=$( { sudo ${IPLANET_SUDO_STOP_WEB} ${IPLANET_ROOT} ${INSTANCE_NAME}; } 2>&1 )
                                    else
                                        SHUTDOWN_OUTPUT=$( { ${IPLANET_ROOT}/${INSTANCE_NAME}/${IPLANET_STOP_SCRIPT}; } 2>&1 )
                                    fi


                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SHUTDOWN_OUTPUT -> ${SHUTDOWN_OUTPUT}";

                                    PID_LOG_FILE=$(grep -w ${IPLANET_PID_IDENTIFIER} ${IPLANET_ROOT}/${INSTANCE_NAME}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG} | awk '{print $2}');

                                    if [ -s ${PID_LOG_FILE} ]
                                    then
                                        SERVICE_PID=$(cat ${PID_LOG_FILE});

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PID_LOG_FILE -> ${PID_LOG_FILE}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_PID -> ${SERVICE_PID}";

                                        PROCESS_OUTPUT=$(ps -auxww | grep -w ${SERVICE_PID} | grep -v grep);

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_OUTPUT -> ${PROCESS_OUTPUT}";

                                        if [ -z "${PROCESS_OUTPUT}" ]
                                        then
                                            ${LOGGER} "INFO" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${INSTANCE_NAME} successfully stopped.";

                                            RETURN_CODE=0;
                                        else
                                            ## server failed to properly stop
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${INSTANCE_NAME} appears to be down. Shutdown attempt has failed: ${SHUTDOWN_OUTPUT}";

                                            RETURN_CODE=40;
                                        fi
                                    else
                                        ## server stopped
                                        ${LOGGER} "INFO" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${INSTANCE_NAME} successfully stopped.";

                                        RETURN_CODE=0;
                                    fi
                                fi
                                ;;
                            restart)
                                ## invalid operation type
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${COMMAND_NAME} is currently unsupported. Please try again.";

                                RETURN_CODE=36;
                                ;;
                            *)
                                ## invalid operation type
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${COMMAND_NAME} is currently unsupported. Please try again.";

                                RETURN_CODE=36;
                                ;;
                        esac
                    else
                        ## unable to accurately determine server state
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to determine current state of ${INSTANCE_NAME}. Cannot continue.";

                        RETURN_CODE=36;
                    fi
                else
                    ## server is in the exception list
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${INSTANCE_NAME} was found in the exception processing list. Cannot continue.";

                    RETURN_CODE=37;
                fi
            else
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The selected instance cannot be found on this server. Please try again.";

                RETURN_CODE=32;
            fi
        elif [ "${WS_PLATFORM}" = "${IHS_TYPE_IDENTIFIER}" ]
        then
            ## IHS host.
            set -A VALIDATE_SERVER_LIST $(ls -ltr ${IHS_ROOT} | grep ${IHS_WEB_IDENTIFIER} | awk '{print $9}');

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_SERVER_LIST -> ${VALIDATE_SERVER_LIST[@]}";

            if [ ! -z "${VALIDATE_SERVER_LIST}" ]
            then
                ## ok, validate it
                for WEBSERVER in ${VALIDATE_SERVER_LIST[@]}
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEBSERVER -> ${WEBSERVER}";

                    if [ $(grep -c ${WEBSERVER} ${APP_ROOT}/${CORE_EXCEPTION_LIST}) -eq 0 ] \
                        && [ $(grep -c ${WEBSERVER} ${APP_ROOT}/${TMP_EXCEPTION_LIST}) -eq 0 ] \
                        && [ $(echo ${IPLANET_STARTUP_IGNORE_LIST} | grep -c ${WEBSERVER}) -eq 0 ]
                    then
                        ## ok, we can run it. its not in an exception list
                        PID_LOG_FILE=$(grep -w ${IHS_PID_IDENTIFIER} ${IHS_ROOT}/${IHS_CONFIG_PATH}/${WEBSERVER}/${IHS_SERVER_CONFIG} | awk '{print $2}');

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PID_LOG_FILE -> ${PID_LOG_FILE}";

                        if [ -s ${PID_LOG_FILE} ]
                        then
                            SERVICE_PID=$(cat ${PID_LOG_FILE});

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_PID -> ${SERVICE_PID}";

                            PROCESS_OUTPUT=$(ps -axww | grep -w ${SERVICE_PID} | grep -v grep);

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_OUTPUT -> ${PROCESS_OUTPUT}";

                            if [ ! -z "${PROCESS_OUTPUT}" ]
                            then
                                ## server is running, check to see who its running as
                                PROCESS_OWNER=$(echo ${PROCESS_OUTPUT} | awk '{print $1}');

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_OWNER -> ${PROCESS_OWNER}";

                                if [ "${PROCESS_OWNER}" != "${IHS_PROCESS_USER}" ]
                                then
                                    ## process is running as a user other than what we expect.
                                    ## provide notification
                                    ## TODO: build in a restart process to make it run as the user we want
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} appears to be running as a user other than the configured process owner: Current owner: ${PROCESS_OWNER}, expected owner: ${IHS_PROCESS_USER}";
                                fi
                            else
                                ## process doesnt appear to be running. make an attempt to start it
                                unset STARTUP_OUTPUT;
                                unset PID_LOG_FILE;
                                unset SERVICE_PID;
                                unset PROCESS_OUTPUT;

                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} does not appear to be running. Restarting..";

                                startIHS ${WEBSERVER};
                                typeset -i RET_CODE=${?};

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                PID_LOG_FILE=$(grep -w ${IHS_PID_IDENTIFIER} ${IHS_ROOT}/${IHS_CONFIG_PATH}/${WEBSERVER}/${IHS_SERVER_CONFIG} | awk '{print $2}');

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PID_LOG_FILE -> ${PID_LOG_FILE}";

                                if [ -s ${PID_LOG_FILE} ]
                                then
                                    SERVICE_PID=$(cat ${PID_LOG_FILE});

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_PID -> ${SERVICE_PID}";

                                    PROCESS_OUTPUT=$(ps -auxww | grep -w ${SERVICE_PID} | grep -v grep);

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_OUTPUT -> ${PROCESS_OUTPUT}";

                                    if [ -z "${PROCESS_OUTPUT}" ]
                                    then
                                        ## server failed to properly start
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} appears to be down. Restart attempt has failed: ${STARTUP_OUTPUT}";
                                    fi
                                else
                                    ## tried restart and failed
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} appears to be down. Restart attempt has failed.";
                                fi
                            fi
                        else
                            unset STARTUP_OUTPUT;
                            unset PID_LOG_FILE;
                            unset SERVICE_PID;
                            unset PROCESS_OUTPUT;

                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} does not appear to be running. Restarting..";

                            startIHS ${WEBSERVER};
                            typeset -i RET_CODE=${?};

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                            PID_LOG_FILE=$(grep -w ${IHS_PID_IDENTIFIER} ${IHS_ROOT}/${IHS_CONFIG_PATH}/${WEBSERVER}/${IHS_SERVER_CONFIG} | awk '{print $2}');

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PID_LOG_FILE -> ${PID_LOG_FILE}";

                            if [ -s ${PID_LOG_FILE} ]
                            then
                                SERVICE_PID=$(cat ${PID_LOG_FILE});

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_PID -> ${SERVICE_PID}";

                                PROCESS_OUTPUT=$(ps -auxww | grep -w ${SERVICE_PID} | grep -v grep);

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_OUTPUT -> ${PROCESS_OUTPUT}";

                                if [ -z "${PROCESS_OUTPUT}" ]
                                then
                                    ## server failed to properly start
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} appears to be down. Restart attempt has failed: ${STARTUP_OUTPUT}";
                                fi
                            else
                                ## tried restart and failed
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} appears to be down. Restart attempt has failed.";
                            fi
                        fi
                    fi

                    unset PID_LOG_FILE;
                    unset SERVICE_PID;
                    unset PROCESS_OUTPUT;
                    unset PROCESS_OWNER;
                    unset RET_CODE;
                    unset STARTUP_OUTPUT;
                done
            else
                ## no websites were found to monitor
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No websites were found to monitor. Please ensure that the IPLANET_ROOT variable exists in the executing users profile and that it points to a valid location.";
            fi
        else
            ## unknown host type
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An unknown server platform was determined. Please verify that the executing user has an exported variable named WS_PLATFORM and that the variable points to an valid webserver platform type.";
        fi
    else
        ## unable to determine platform type to verify, cannot continue
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No server platform was determined. Please verify that the executing user has an exported variable named WS_PLATFORM and that the variable points to an valid webserver platform type.";
    fi

    unset VALIDATE_SERVER_LIST;
    unset WEBSERVER;
    unset PID_LOG_FILE;
    unset SERVICE_PID;
    unset PROCESS_OUTPUT;
    unset PROCESS_OWNER;
    unset RET_CODE;
    unset STARTUP_OUTPUT;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
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

    echo "${THIS_CNAME} - stop, start, or restart a selected service.";
    echo "Usage: ${THIS_CNAME} <instance> <control command>";
    echo " No arguments are required to operate this utility.";
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

serviceControl ${@};

echo ${RETURN_CODE};
exit ${RETURN_CODE};
