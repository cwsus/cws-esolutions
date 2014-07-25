#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  monitorWebs.sh
#         USAGE:  ./monitorWebs.sh server_name
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

[ -f ${PLUGIN_LIB_DIRECTORY}/aliases ] && . ${PLUGIN_LIB_DIRECTORY}/aliases;
[ -f ${PLUGIN_LIB_DIRECTORY}/functions ] && . ${PLUGIN_LIB_DIRECTORY}/functions;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

#===  FUNCTION  ===============================================================
#          NAME:  monitorWebInstances
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function monitorWebInstances
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing monitor on: ${HOSTNAME}";

    ## ok, lets sort out where we are and what to look for
    if [ ! -z "${WS_PLATFORM}" ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WS_PLATFORM -> ${WS_PLATFORM}";

        if [ "${WS_PLATFORM}" = "${IPLANET_TYPE_IDENTIFIER}" ]
        then
            ## ok, we know we're on an iPlanet server. poll for the list of servers to validate
            unset METHOD_NAME;
            unset CNAME;

            set -A VALIDATE_SERVER_LIST $(${APP_ROOT}/${LIB_DIRECTORY}/retrieveSiteList.sh status);

            CNAME=$(/usr/bin/env basename ${0});
        typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_SERVER_LIST -> ${VALIDATE_SERVER_LIST}";

            if [ -z "${VALIDATE_SERVER_LIST}" ] || [ "$(echo ${VALIDATE_SERVER_LIST[@]})" = "${_FALSE}" ]
            then
                ## no websites were found to monitor
                ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No websites were found to monitor. Please ensure that the IPLANET_ROOT variable exists in the executing users profile and that it points to a valid location.";
            else
                ## ok, validate it
                for WEBSERVER in ${VALIDATE_SERVER_LIST[@]}
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEBSERVER -> ${WEBSERVER}";

                    if [ $(grep -c ${WEBSERVER} ${APP_ROOT}/${CORE_EXCEPTION_LIST}) -eq 0 ] \
                        && [ $(grep -c ${WEBSERVER} ${APP_ROOT}/${TMP_EXCEPTION_LIST}) -eq 0 ] \
                        && [ $(echo ${IPLANET_STARTUP_IGNORE_LIST} | grep -c ${WEBSERVER}) -eq 0 ]
                    then
                        ## pull out the port number. it drives what we alert
                        set -A SITE_PORT_NUMBER $(grep -w "${IPLANET_PORT_IDENTIFIER}" \
                            ${IPLANET_ROOT}/${WEBSERVER}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG} | \
                            sed -e "s/${IPLANET_PORT_IDENTIFIER}=/@/" | \
                            cut -d "@" -f 2 | awk '{print $1}' | sed -e "s/\"//g");

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_PORT_NUMBER -> ${SITE_PORT_NUMBER[@]}";

                        A=0;

                        for PORT in ${SITE_PORT_NUMBER[@]}
                        do
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PORT -> ${PORT}";

                            if [ ${PORT} -lt ${HIGH_PRIVILEGED_PORT} ]
                            then
                                ## process is running as a user other than what we expect.
                                (( A += 1 ));
                            fi
                        done

                        unset PORT;

                        if [ ${A} -eq 0 ]
                        then
                            IS_PRIVILEGED=${_FALSE};
                        else
                            IS_PRIVILEGED=${_TRUE};
                        fi

                        A=0;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IS_PRIVILEGED -> ${IS_PRIVILEGED}";

                        ## ok, we can run it. its not in an exception list
                        PID_LOG_FILE=$(grep -w ${IPLANET_PID_IDENTIFIER} ${IPLANET_ROOT}/${WEBSERVER}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG} | awk '{print $2}');

                        if [ -s ${PID_LOG_FILE} ]
                        then
                            SERVICE_PID=$(cat ${PID_LOG_FILE});

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PID_LOG_FILE -> ${PID_LOG_FILE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_PID -> ${SERVICE_PID}";

                            PROCESS_OUTPUT=$(ps -auxww | grep -w ${SERVICE_PID} | grep -v grep);

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_OUTPUT -> ${PROCESS_OUTPUT}";

                            if [ ! -z "${PROCESS_OUTPUT}" ]
                            then
                                ## server is running, check to see who its running as
                                PROCESS_OWNER=$(echo ${PROCESS_OUTPUT} | awk '{print $1}');

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_OWNER -> ${PROCESS_OWNER}";

                                if [ "${PROCESS_OWNER}" != "${IPLANET_PROCESS_USER}" ]
                                then
                                    if [ ! -z "${IS_PRIVILEGED}" ] && [ "${IS_PRIVILEGED}" = "${_FALSE}" ]
                                    then
                                        ## TODO: build in a restart process to make it run as the user we want
                                        ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} appears to be running as a user other than the configured process owner: Current owner: ${PROCESS_OWNER}, expected owner: ${IPLANET_PROCESS_USER}";
                                    fi
                                fi
                            else
                                ## process doesnt appear to be running. make an attempt to start it
                                unset STARTUP_OUTPUT;
                                unset PID_LOG_FILE;
                                unset SERVICE_PID;
                                unset PROCESS_OUTPUT;

                                ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} does not appear to be running. Restarting..";

                                ## temporarily turn off trace. the way we're doing things here,
                                ## it breaks the variable output
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                                if [ ! -z "${IS_PRIVILEGED}" ] && [ "${IS_PRIVILEGED}" = "${_FALSE}" ]
                                then
                                    STARTUP_OUTPUT=$( { ${IPLANET_ROOT}/${WEBSERVER}/${IPLANET_START_SCRIPT}; } 2>&1 )
                                else
                                    ## need to use sudo here to start the web
                                    STARTUP_OUTPUT=$( { sudo ${IPLANET_SUDO_START_WEB} ${IPLANET_ROOT} ${WEBSERVER}; } 2>&1 )
                                fi

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "STARTUP_OUTPUT -> ${STARTUP_OUTPUT}";

                                PID_LOG_FILE=$(grep -w ${IPLANET_PID_IDENTIFIER} ${IPLANET_ROOT}/${WEBSERVER}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG} | awk '{print $2}');

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
                                        ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} appears to be down. Restart attempt has failed: ${STARTUP_OUTPUT}";
                                    fi
                                else
                                    ## server failed to start
                                    ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} startup attempt has failed: ${PID_LOG_FILE} does not exist.";
                                fi
                            fi
                        else
                            ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} does not appear to be running. Restarting..";

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                            if [ ! -z "${IS_PRIVILEGED}" ] && [ "${IS_PRIVILEGED}" = "${_FALSE}" ]
                            then
                                STARTUP_OUTPUT=$( { ${IPLANET_ROOT}/${WEBSERVER}/${IPLANET_START_SCRIPT}; } 2>&1 )
                            else
                                ## need to use sudo here to start the web
                                STARTUP_OUTPUT=$( { sudo ${IPLANET_SUDO_START_WEB} ${IPLANET_ROOT} ${WEBSERVER}; } 2>&1 )
                            fi

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "STARTUP_OUTPUT -> ${STARTUP_OUTPUT}";

                            PID_LOG_FILE=$(grep -w ${IPLANET_PID_IDENTIFIER} ${IPLANET_ROOT}/${WEBSERVER}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG} | awk '{print $2}');

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
                                    ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} appears to be down. Restart attempt has failed: ${STARTUP_OUTPUT}";
                                fi
                            else
                                ## server failed to start
                                ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} startup attempt has failed: ${PID_LOG_FILE} does not exist.";
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
                                    ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} appears to be running as a user other than the configured process owner: Current owner: ${PROCESS_OWNER}, expected owner: ${IHS_PROCESS_USER}";
                                fi
                            else
                                ## process doesnt appear to be running. make an attempt to start it
                                unset STARTUP_OUTPUT;
                                unset PID_LOG_FILE;
                                unset SERVICE_PID;
                                unset PROCESS_OUTPUT;

                                ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} does not appear to be running. Restarting..";

                                startIHS ${WEBSERVER};
                                typeset -i RET_CODE=${?};

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                PID_LOG_FILE=$(grep -w ${IHS_PID_IDENTIFIER} ${IHS_ROOT}/${IHS_CONFIG_PATH}/${WEBSERVER}/${IHS_SERVER_CONFIG} | awk '{print $2}');

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PID_LOG_FILE -> ${PID_LOG_FILE}";

                                if [ -s ${PID_LOG_FILE} ]
                                then
                                    SERVICE_PID=$(cat ${PID_LOG_FILE});

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_PID -> ${SERVICE_PID}";

                                    PROCESS_OUTPUT=$(ps -auxwww | grep -w ${SERVICE_PID} | grep -v grep);

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_OUTPUT -> ${PROCESS_OUTPUT}";

                                    if [ -z "${PROCESS_OUTPUT}" ]
                                    then
                                        ## server failed to properly start
                                        ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} appears to be down. Restart attempt has failed: ${STARTUP_OUTPUT}";
                                    fi
                                else
                                    ## tried restart and failed
                                    ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} appears to be down. Restart attempt has failed.";
                                fi
                            fi
                        else
                            unset STARTUP_OUTPUT;
                            unset PID_LOG_FILE;
                            unset SERVICE_PID;
                            unset PROCESS_OUTPUT;

                            ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} does not appear to be running. Restarting..";

                            startIHS ${WEBSERVER};
                            typeset -i RET_CODE=${?};

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                            PID_LOG_FILE=$(grep -w ${IHS_PID_IDENTIFIER} ${IHS_ROOT}/${IHS_CONFIG_PATH}/${WEBSERVER}/${IHS_SERVER_CONFIG} | awk '{print $2}');

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PID_LOG_FILE -> ${PID_LOG_FILE}";

                            if [ -s ${PID_LOG_FILE} ]
                            then
                                SERVICE_PID=$(cat ${PID_LOG_FILE});

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_PID -> ${SERVICE_PID}";

                                PROCESS_OUTPUT=$(ps -auxwww | grep -w ${SERVICE_PID} | grep -v grep);

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_OUTPUT -> ${PROCESS_OUTPUT}";

                                if [ -z "${PROCESS_OUTPUT}" ]
                                then
                                    ## server failed to properly start
                                    ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} appears to be down. Restart attempt has failed: ${STARTUP_OUTPUT}";
                                fi
                            else
                                ## tried restart and failed
                                ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} appears to be down. Restart attempt has failed.";
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
                ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No websites were found to monitor. Please ensure that the IPLANET_ROOT variable exists in the executing users profile and that it points to a valid location.";
            fi
        else
            ## unknown host type
            ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An unknown server platform was determined. Please verify that the executing user has an exported variable named WS_PLATFORM and that the variable points to an valid webserver platform type.";
        fi
    else
        ## unable to determine platform type to verify, cannot continue
        ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No server platform was determined. Please verify that the executing user has an exported variable named WS_PLATFORM and that the variable points to an valid webserver platform type.";
    fi

    unset VALIDATE_SERVER_LIST;
    unset WEBSERVER;
    unset PID_LOG_FILE;
    unset SERVICE_PID;
    unset PROCESS_OUTPUT;
    unset PROCESS_OWNER;
    unset RET_CODE;
    unset STARTUP_OUTPUT;

    RETURN_CODE=0;
}

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin ] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

monitorWebInstances;

echo ${RETURN_CODE};
exit ${RETURN_CODE};
