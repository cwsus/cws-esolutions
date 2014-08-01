#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  monitorOptions.sh
#         USAGE:  ./monitorOptions.sh server_name
#   DESCRIPTION:  Connects to the provided DNS server and restarts the named
#                 process. Utilized to apply pending changes, or to recycle
#                 the service if required for any other reason.
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
#          NAME:  monitorCertDatabases
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function monitorEnabledOptions
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing monitor on: ${HOSTNAME}";

    ## ok, lets sort out where we are and what to look for
    if [ ! -z "${WS_PLATFORM}" ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WS_PLATFORM -> ${WS_PLATFORM}";

        if [ "${WS_PLATFORM}" = "${IPLANET_TYPE_IDENTIFIER}" ]
        then
            ## ok, we know we're on an iPlanet server. poll for the list of servers to validate
            set -A VALIDATE_SERVER_LIST $(ls -ltr ${IPLANET_ROOT} | grep ${IPLANET_CERT_STORE_PREFIX} | awk '{print $NF}');

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_SERVER_LIST -> ${VALIDATE_SERVER_LIST}";

            if [ ! -z "${VALIDATE_SERVER_LIST}" ]
            then
                ## ok, validate it
                for WEBSERVER in ${VALIDATE_SERVER_LIST[*]}
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEBSERVER -> ${WEBSERVER}";

                    if [ $(grep -c ${WEBSERVER} "${APP_ROOT}"/${CORE_EXCEPTION_LIST}) -eq 0 ] \
                        && [ $(grep -c ${WEBSERVER} "${APP_ROOT}"/${TMP_EXCEPTION_LIST}) -eq 0 ] \
                        && [ $(echo ${IPLANET_STARTUP_IGNORE_LIST} | grep -c ${WEBSERVER}) -eq 0 ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checking for ${OPTIONS_MONITOR_STRING} on ${WEBSERVER}..";

                        ## if blocked, this should not be empty
                        IS_METHOD_ENABLED=$(find ${IPLANET_ROOT}/${WEBSERVER}/${IPLANET_CONFIG_PATH} -type f \
                            -exec grep "${OPTIONS_MONITOR_STRING}" {} \;);

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IS_METHOD_ENABLED -> ${IS_METHOD_ENABLED}";

                        if [ ! -z "${IS_METHOD_ENABLED}" ]
                        then
                            ## ok, good - its all blocked out. make sure trace isnt enabled via svc
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checking for ${SVCTRACE_MONITOR_STRING} on ${WEBSERVER}..";

                            ## if all commented out, this should be empty
                            IS_TRACE_ENABLED=$(find ${IPLANET_ROOT}/${WEBSERVER}/${IPLANET_CONFIG_PATH} -type f -name "*${IPLANET_WEB_CONFIG}" \
                                -exec grep "${SVCTRACE_MONITOR_STRING}" {} \; | grep -v "#");

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IS_TRACE_ENABLED -> ${IS_TRACE_ENABLED}";

                            if [ ! -z "${IS_TRACE_ENABLED}" ]
                            then
                                ## one or more obj files have trace enabled. although its blocked above,
                                ## should prolly clean it up anyway
                                "${LOGGER}" MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service TRACE is enabled on ${WEBSERVER}";
                            fi
                        else
                            ## one or more obj files arent blocking the "bad" methods
                            "${LOGGER}" MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Blocking of the following options: TRACE|TRACK|OPTIONS|PUT|DELETE - not found on ${WEBSERVER}";
                        fi
                    fi
                done

                RETURN_CODE=0;
            else
                ## no websites were found to monitor
                "${LOGGER}" MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No websites were found to monitor. Please ensure that the IPLANET_ROOT variable exists in the executing users profile and that it points to a valid location.";

                RETURN_CODE=1;
            fi
        elif [ "${WS_PLATFORM}" = "${IHS_TYPE_IDENTIFIER}" ]
        then
            ## IHS host.
            set -A VALIDATE_SERVER_LIST $(ls -ltr ${IHS_ROOT} | grep ${IHS_WEB_IDENTIFIER} | awk '{print $NF}');

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_SERVER_LIST -> ${VALIDATE_SERVER_LIST[*]}";

            if [ ! -z "${VALIDATE_SERVER_LIST}" ]
            then
                ## ok, validate it
                for WEBSERVER in ${VALIDATE_SERVER_LIST[*]}
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEBSERVER -> ${WEBSERVER}";

                    if [ $(grep -c ${WEBSERVER} "${APP_ROOT}"/${CORE_EXCEPTION_LIST}) -eq 0 ] \
                        && [ $(grep -c ${WEBSERVER} "${APP_ROOT}"/${TMP_EXCEPTION_LIST}) -eq 0 ] \
                        && [ $(echo ${IPLANET_STARTUP_IGNORE_LIST} | grep -c ${WEBSERVER}) -eq 0 ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checking for ${OPTIONS_MONITOR_STRING} on ${WEBSERVER}..";

                        ## if blocked, this should not be empty
                        IS_METHOD_ENABLED=$(find ${IHS_ROOT}/${IHS_CONFIG_PATH}/${WEBSERVER}/${IHS_SERVER_CONFIG} -type f
                            -exec grep "${OPTIONS_MONITOR_STRING}" {} \;);

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IS_METHOD_ENABLED -> ${IS_METHOD_ENABLED}";

                        if [ -z "${IS_METHOD_ENABLED}" ]
                        then
                            ## one or more obj files arent blocking the "bad" methods
                            "${LOGGER}" MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Blocking of the following options: TRACE|TRACK|OPTIONS|PUT|DELETE - not found on ${WEBSERVER}";
                        fi
                    fi
                done

                RETURN_CODE=0;
            else
                ## no websites were found to monitor
                "${LOGGER}" MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No websites were found to monitor. Please ensure that the IPLANET_ROOT variable exists in the executing users profile and that it points to a valid location.";

                RETURN_CODE=1;
            fi
        else
            ## unknown host type
            "${LOGGER}" MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An unknown server platform was determined. Please verify that the executing user has an exported variable named WS_PLATFORM and that the variable points to an valid webserver platform type.";

            RETURN_CODE=1;
        fi
    else
        ## unable to determine platform type to verify, cannot continue
        "${LOGGER}" MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No server platform was determined. Please verify that the executing user has an exported variable named WS_PLATFORM and that the variable points to an valid webserver platform type.";

        RETURN_CODE=1;
    fi

    unset IS_METHOD_ENABLED;
    unset WEBSERVER;
    unset VALIDATE_SERVER_LIST;
    unset IS_TRACE_ENABLED;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";


    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

    return ${RETURN_CODE};
}

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -s "${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin" ] && . "${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin";
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

monitorEnabledOptions;

echo ${RETURN_CODE};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;

exit ${RETURN_CODE};
