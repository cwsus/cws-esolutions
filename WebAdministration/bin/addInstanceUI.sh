[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

## Application constants
CNAME="$(/usr/bin/env basename ${0})";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; /usr/bin/env echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(/usr/bin/env dirname ${SCRIPT_ABSOLUTE_PATH})";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -f ${SCRIPT_ROOT}/../lib/plugin ] && . ${SCRIPT_ROOT}/../lib/plugin;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${PLUGIN_ROOT_DIR}" ] && /usr/bin/env echo "Failed to locate configuration data. Cannot continue." && return 1;

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

unset RET_CODE;

trap "echo '$(awk -F "=" '/\<system.trap.signals\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%SIGNAL%/Ctrl-C/")'; sleep "${MESSAGE_DELAY}"; reset; clear; continue " 1 2 3

#===  FUNCTION  ===============================================================
#
#         NAME:  main
#  DESCRIPTION:  Main entry point for application.
#   PARAMETERS:  None
#      RETURNS:  0
#
#==============================================================================
function main
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    if [ -z "${IS_WEB_BUILD_ENABLED}" ] || [ "${IS_WEB_BUILD_ENABLED}" != "${_TRUE}" ]
    then
        reset; clear;

        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Web additions have not been enabled. Cannot continue.";

        echo "$(awk -F "=" '/\<request.not.authorized\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        ## terminate this thread and return control to main
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        A=0;

        unset RET_CODE;
        unset PLATFORM;
        unset WS_PLATFORM;
        unset TYPE;
        unset BUILD_TYPE;
        unset HOSTNAME;
        unset CONTEXT_ROOT;
        unset PORT_NUMBER;
        unset PORT_CONFIRMATION;
        unset ENABLE_APPSERVER;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

        sleep ${MESSAGE_DELAY}; reset; clear; main;

        return ${RETURN_CODE};
    fi

    while true
    do
        reset; clear;

        echo "\n
            \t\t+-------------------------------------------------------------------+
            \t\t               WELCOME TO $(awk -F "=" '/\<system.application.title\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t\t+-------------------------------------------------------------------+
            \t\tSystem Type         : ${SYSTEM_HOSTNAME}
            \t\tSystem Uptime       : ${SYSTEM_UPTIME}
            \t\tUser                : ${IUSER_AUDIT}
            \t\t+-------------------------------------------------------------------+
            \n
            \t$(awk -F "=" '/\<createsite.launch.message\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        read SELECTION;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION -> ${SELECTION}";

        reset; clear;

        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        case ${SELECTION} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

                echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                A=0;

                unset RET_CODE;
                unset PLATFORM;
                unset WS_PLATFORM;
                unset TYPE;
                unset BUILD_TYPE;
                unset HOSTNAME;
                unset CONTEXT_ROOT;
                unset PORT_NUMBER;
                unset PORT_CONFIRMATION;
                unset ENABLE_APPSERVER;
                unset METHOD_NAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                sleep ${MESSAGE_DELAY}; reset; clear; exec ${MAIN_CLASS};

                return ${RETURN_CODE};
                ;;
            *)
                reset; clear;

                echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                requestSiteConfig;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  requestBuildConfig
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function requestBuildConfig
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    ## get the web type and build type
    while true
    do
        reset; clear;

        echo "\n\t$(awk -F "=" '/\<createsite.provide.platform\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        A=0;

        for PLATFORM in ${SUPPORTED_WEBSERVERS[@]}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLATFORM -> ${PLATFORM}";

            echo "\t${A} - ${PLATFORM}";

            (( A += 1 ));
        done

        echo "\t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        read WS_PLATFORM;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WS_PLATFORM -> ${WS_PLATFORM}";

        reset; clear;

        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        case ${WS_PLATFORM} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

                echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                A=0;

                unset RET_CODE;
                unset PLATFORM;
                unset WS_PLATFORM;
                unset TYPE;
                unset BUILD_TYPE;
                unset HOSTNAME;
                unset CONTEXT_ROOT;
                unset PORT_NUMBER;
                unset PORT_CONFIRMATION;
                unset ENABLE_APPSERVER;
                unset METHOD_NAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                sleep ${MESSAGE_DELAY}; reset; clear; main;

                return ${RETURN_CODE};
                ;;
            *)
                if [ -z "${SUPPORTED_WEBSERVERS[${A}]}" ]
                then
                    unset WS_PLATFORM;

                    echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                typeset WS_PLATFORM=${SUPPORTED_WEBSERVERS[${A}]};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WS_PLATFORM -> ${WS_PLATFORM}";

                ## get build type
                while true
                do
                    reset; clear;

                    echo "\n\t$(awk -F "=" '/\<createsite.provide.buildtype\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                    A=0;

                    for TYPE in ${SUPPORTED_BUILD_TYPES[@]}
                    do
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TYPE -> ${TYPE}";

                        echo "\t${A} - ${TYPE}";

                        (( A += 1 ));
                    done

                    echo "\t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                    read BUILD_TYPE;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONTEXT_ROOT -> ${CONTEXT_ROOT}";

                    reset; clear;

                    echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    case ${BUILD_TYPE} in
                        [Xx]|[Qq]|[Cc])
                            reset; clear;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

                            echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                            ## terminate this thread and return control to main
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            A=0;

                            unset RET_CODE;
                            unset PLATFORM;
                            unset WS_PLATFORM;
                            unset TYPE;
                            unset BUILD_TYPE;
                            unset HOSTNAME;
                            unset CONTEXT_ROOT;
                            unset PORT_NUMBER;
                            unset PORT_CONFIRMATION;
                            unset ENABLE_APPSERVER;
                            unset METHOD_NAME;

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            sleep ${MESSAGE_DELAY}; reset; clear; main;

                            return ${RETURN_CODE};
                            ;;
                        *)
                            if [ -z "${BUILD_TYPE[${A}]}" ]
                            then
                                unset BUILD_TYPE;

                                echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                            fi

                            typeset BUILD_TYPE=${SUPPORTED_BUILD_TYPES[${A}]};

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUILD_TYPE -> ${BUILD_TYPE}";

                            reset; clear;

                            echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            requestSiteConfig;
                            ;;
                    esac
                done
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${TMPFILE}" ] && [ -f ${TMPFILE} ] && rm -rf ${TMPFILE};

    unset PORT_NUMBER;
    unset PORT_CONFIRMATION;
    unset NONSSL_LISTEN_PORT;
    unset ENABLE_APPSERVER;
    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  requestSiteConfig
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function requestSiteConfig
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    ## get the hostname, web type, build type, and context root
    while true
    do
        reset; clear;

        echo "\n
            \t$(awk -F "=" '/\<createsite.provide.hostname\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        read HOSTNAME;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HOSTNAME -> ${HOSTNAME}";

        reset; clear;

        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        case ${HOSTNAME} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

                echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                A=0;

                unset RET_CODE;
                unset PLATFORM;
                unset WS_PLATFORM;
                unset TYPE;
                unset BUILD_TYPE;
                unset HOSTNAME;
                unset CONTEXT_ROOT;
                unset PORT_NUMBER;
                unset PORT_CONFIRMATION;
                unset ENABLE_APPSERVER;
                unset METHOD_NAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                sleep ${MESSAGE_DELAY}; reset; clear; main;

                return ${RETURN_CODE};
                ;;
            *)
                if [ -z "${HOSTNAME}" ]
                then
                    unset HOSTNAME;

                    echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ## check if theres a www or something here, if not, re-define to add
                [ $(tr -dc "." <<< ${HOSTNAME} | wc -c) -ne 2 ] && typeset HOSTNAME="www.${HOSTNAME}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HOSTNAME -> ${HOSTNAME}";

                ## get the context root
                while true
                do
                    reset; clear;

                    echo "\n
                        \t$(awk -F "=" '/\<createsite.provide.context\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
                        \t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                    read CONTEXT_ROOT;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONTEXT_ROOT -> ${CONTEXT_ROOT}";

                    reset; clear;

                    echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    case ${CONTEXT_ROOT} in
                        [Xx]|[Qq]|[Cc])
                            reset; clear;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

                            echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                            ## terminate this thread and return control to main
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            A=0;

                            unset RET_CODE;
                            unset PLATFORM;
                            unset WS_PLATFORM;
                            unset TYPE;
                            unset BUILD_TYPE;
                            unset HOSTNAME;
                            unset CONTEXT_ROOT;
                            unset PORT_NUMBER;
                            unset PORT_CONFIRMATION;
                            unset ENABLE_APPSERVER;
                            unset METHOD_NAME;

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            sleep ${MESSAGE_DELAY}; reset; clear; main;

                            return ${RETURN_CODE};
                            ;;
                        *)
                            if [ -z "${CONTEXT_ROOT}" ]
                            then
                                unset CONTEXT_ROOT;

                                echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                            fi

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONTEXT_ROOT -> ${CONTEXT_ROOT}";

                            reset; clear;

                            echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            requestWebConfig;
                            ;;
                    esac
                done
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${TMPFILE}" ] && [ -f ${TMPFILE} ] && rm -rf ${TMPFILE};

    unset PORT_NUMBER;
    unset PORT_CONFIRMATION;
    unset NONSSL_LISTEN_PORT;
    unset ENABLE_APPSERVER;
    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  requestWebConfig
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function requestWebConfig
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    ## get the listen address and port #
    while true
    do
        reset; clear;

        echo "\n
            \t$(awk -F "=" '/\<createsite.provide.listenaddress\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        read LISTEN_ADDRESS;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LISTEN_ADDRESS -> ${LISTEN_ADDRESS}";

        reset; clear;

        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        case ${LISTEN_ADDRESS} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

                echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                A=0;

                unset RET_CODE;
                unset PLATFORM;
                unset WS_PLATFORM;
                unset TYPE;
                unset BUILD_TYPE;
                unset HOSTNAME;
                unset CONTEXT_ROOT;
                unset PORT_NUMBER;
                unset PORT_CONFIRMATION;
                unset ENABLE_APPSERVER;
                unset METHOD_NAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                sleep ${MESSAGE_DELAY}; reset; clear; main;

                return ${RETURN_CODE};
                ;;
            *)
                if [ -z "${LISTEN_ADDRESS}" ]
                then
                    unset LISTEN_ADDRESS;

                    echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                typeset THIS_CNAME="${CNAME}";
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                ## validate the input
                validateServerAvailability ${LISTEN_ADDRESS};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    ## not a number
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Host is not currently available.";

                    echo "$(awk -F "=" '/\<host.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                    ## unset SVC_LIST, we dont need it now
                    unset LISTEN_ADDRESS;

                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                fi

                ## get the port #
                while true
                do
                    reset; clear;

                    echo "\n
                        \t$(awk -F "=" '/\<createsite.provide.port\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
                        \t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                    read PORT_NUMBER;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PORT_NUMBER -> ${PORT_NUMBER}";

                    reset; clear;

                    echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    case ${PORT_NUMBER} in
                        [Xx]|[Qq]|[Cc])
                            reset; clear;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

                            echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                            ## terminate this thread and return control to main
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            A=0;

                            unset RET_CODE;
                            unset PLATFORM;
                            unset WS_PLATFORM;
                            unset TYPE;
                            unset BUILD_TYPE;
                            unset HOSTNAME;
                            unset CONTEXT_ROOT;
                            unset PORT_NUMBER;
                            unset PORT_CONFIRMATION;
                            unset ENABLE_APPSERVER;
                            unset METHOD_NAME;

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            sleep ${MESSAGE_DELAY}; reset; clear; main;

                            return ${RETURN_CODE};
                            ;;
                        *)
                            if [ -z "${PORT_NUMBER}" ]
                            then
                                unset PORT_NUMBER;

                                echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                            fi

                            typeset THIS_CNAME="${CNAME}";
                            unset METHOD_NAME;
                            unset CNAME;

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            ## validate the input
                            isNaN ${PORT_NUMBER};

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                            CNAME="${THIS_CNAME}";
                            typeset METHOD_NAME="${THIS_CNAME}#${0}";

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                            if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                            then
                                ## not a number
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid port number was provided.";

                                echo "$(awk -F "=" '/\<host.invalid\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                ## unset SVC_LIST, we dont need it now
                                unset LISTEN_ADDRESS;

                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                            fi

                            ## privileged port... confirm
                            if [ ${PORT_NUMBER} -le ${HIGH_PRIVILEGED_PORT} ]
                            then
                                ## user is requesting a privileged port - confirm
                                while true
                                do
                                    reset; clear;

                                    echo "\n
                                        \t$(awk -F "=" '/\<createsite.privileged.port\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                    read PORT_CONFIRMATION;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PORT_CONFIRMATION -> ${PORT_CONFIRMATION}";

                                    reset; clear;

                                    echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                    case ${PORT_CONFIRMATION} in
                                        [Yy][Ee][Ss]|[Yy])
                                            reset; clear; break;
                                            ;;
                                        [Nn][Oo]|[Nn])
                                            reset; clear;

                                            unset PORT_CONFIRMATION;
                                            unset PORT_NUMBER;

                                            IS_PORT_VALID=${_FALSE};

                                            reset; clear; break;
                                            ;;
                                        *)
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid response was provided.";

                                            echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                            ## unset SVC_LIST, we dont need it now
                                            unset PORT_CONFIRMATION;

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            ;;
                                    esac
                                done
                            fi

                            typeset THIS_CNAME="${CNAME}";
                            unset METHOD_NAME;
                            unset CNAME;

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            ## validate the input
                            ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validatePortNumber.sh ${PORT_NUMBER};
                            typeset -i RET_CODE=${?};

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                            CNAME="${THIS_CNAME}";
                            typeset METHOD_NAME="${THIS_CNAME}#${0}";

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                            if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                            then
                                ## not a number
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Port number provided is already in use.";

                                echo "$(awk -F "=" '/\<provided.port.already.used\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                ## unset SVC_LIST, we dont need it now
                                unset PORT_NUMBER;

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                            fi

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NONSSL_LISTEN_PORT -> ${NONSSL_LISTEN_PORT}";

                            reset; clear;

                            echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            requestAppConfig;
                            ;;
                    esac
                done
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${TMPFILE}" ] && [ -f ${TMPFILE} ] && rm -rf ${TMPFILE};

    unset PORT_NUMBER;
    unset PORT_CONFIRMATION;
    unset NONSSL_LISTEN_PORT;
    unset ENABLE_APPSERVER;
    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  requestAppConfig
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function requestAppConfig
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        echo "\n\t$(awk -F "=" '/\<createsite.provide.app.server\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        A=0;

        for PLATFORM in ${SUPPORTED_APPSERVERS[@]}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLATFORM -> ${PLATFORM}";

            echo "\t${A} - ${PLATFORM}";

            (( A += 1 ));
        done

        echo "\t$(awk -F "=" '/\<createsite.no.appserver\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";
        echo "\t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        read APPSERVER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENABLE_APPSERVER -> ${ENABLE_APPSERVER}";

        reset; clear;

        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        case ${APPSERVER} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

                echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                A=0;

                unset APPSERVER;
                unset RET_CODE;
                unset PLATFORM;
                unset WS_PLATFORM;
                unset TYPE;
                unset BUILD_TYPE;
                unset HOSTNAME;
                unset CONTEXT_ROOT;
                unset APP_PORT_NUMBER;
                unset PORT_CONFIRMATION;
                unset ENABLE_APPSERVER;
                unset METHOD_NAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                sleep ${MESSAGE_DELAY}; reset; clear; main;

                return ${RETURN_CODE};
                ;;
            [Nn][Oo][Nn][Ee])
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No application server integration has been requested.";

                reset; clear;

                echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                buildWebInstance;
                ;;
            *)
                if [ -z "${SUPPORTED_APPSERVERS[${A}]}" ]
                then
                    unset WS_PLATFORM;

                    echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                typeset APP_PLATFORM=${SUPPORTED_APPSERVERS[${A}]};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "APP_PLATFORM -> ${APP_PLATFORM}";

                ## get app hostname
                while true
                do
                    reset; clear;

                    echo "\n
                        \t$(awk -F "=" '/\<createsite.provide.app.hostname\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
                        \t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                    read APP_HOSTNAME;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "APP_HOSTNAME -> ${APP_HOSTNAME}";

                    reset; clear;

                    echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    case ${APP_HOSTNAME} in
                        [Xx]|[Qq]|[Cc])
                            reset; clear;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

                            echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                            ## terminate this thread and return control to main
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            A=0;

                            unset RET_CODE;
                            unset PLATFORM;
                            unset WS_PLATFORM;
                            unset TYPE;
                            unset BUILD_TYPE;
                            unset HOSTNAME;
                            unset CONTEXT_ROOT;
                            unset APP_PORT_NUMBER;
                            unset PORT_CONFIRMATION;
                            unset ENABLE_APPSERVER;
                            unset METHOD_NAME;

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            sleep ${MESSAGE_DELAY}; reset; clear; main;

                            return ${RETURN_CODE};
                            ;;
                        *)
                            if [ -z "${APP_HOSTNAME}" ]
                            then
                                unset APP_HOSTNAME;

                                echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                            fi

                            typeset THIS_CNAME="${CNAME}";
                            unset METHOD_NAME;
                            unset CNAME;

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            ## validate the input
                            validateServerAvailability ${APP_HOSTNAME};

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                            CNAME="${THIS_CNAME}";
                            typeset METHOD_NAME="${THIS_CNAME}#${0}";

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                            if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                            then
                                ## not a number
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Host is not currently available.";

                                echo "$(awk -F "=" '/\<host.invalid\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                ## unset SVC_LIST, we dont need it now
                                unset LISTEN_ADDRESS;

                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                            fi

                            ## get the port #
                            while true
                            do
                                reset; clear;

                                echo "\n
                                    \t$(awk -F "=" '/\<createsite.provide.port\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
                                    \t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                read APP_APP_PORT_NUMBER;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "APP_APP_PORT_NUMBER -> ${APP_APP_PORT_NUMBER}";

                                reset; clear;

                                echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                case ${APP_APP_PORT_NUMBER} in
                                    [Xx]|[Qq]|[Cc])
                                        reset; clear;

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

                                        echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                        ## terminate this thread and return control to main
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                        A=0;

                                        unset RET_CODE;
                                        unset PLATFORM;
                                        unset WS_PLATFORM;
                                        unset TYPE;
                                        unset BUILD_TYPE;
                                        unset HOSTNAME;
                                        unset CONTEXT_ROOT;
                                        unset APP_PORT_NUMBER;
                                        unset PORT_CONFIRMATION;
                                        unset ENABLE_APPSERVER;
                                        unset METHOD_NAME;

                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                                        sleep ${MESSAGE_DELAY}; reset; clear; main;

                                        return ${RETURN_CODE};
                                        ;;
                                    *)
                                        if [ -z "${APP_PORT_NUMBER}" ]
                                        then
                                            unset APP_PORT_NUMBER;

                                            echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        fi

                                        typeset THIS_CNAME="${CNAME}";
                                        unset METHOD_NAME;
                                        unset CNAME;

                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                                        ## validate the input
                                        isNaN ${APP_PORT_NUMBER};

                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                        CNAME="${THIS_CNAME}";
                                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                        if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                                        then
                                            ## not a number
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid port number was provided.";

                                            echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                            ## unset SVC_LIST, we dont need it now
                                            unset LISTEN_ADDRESS;

                                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                        fi

                                        ## privileged port... confirm
                                        if [ ${APP_PORT_NUMBER} -le ${HIGH_PRIVILEGED_PORT} ]
                                        then
                                            ## user is requesting a privileged port - confirm
                                            while true
                                            do
                                                reset; clear;

                                                echo "\n
                                                    \t$(awk -F "=" '/\<createsite.privileged.port\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                                read PORT_CONFIRMATION;

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PORT_CONFIRMATION -> ${PORT_CONFIRMATION}";

                                                reset; clear;

                                                echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                                case ${PORT_CONFIRMATION} in
                                                    [Yy][Ee][Ss]|[Yy])
                                                        reset; clear; break;
                                                        ;;
                                                    [Nn][Oo]|[Nn])
                                                        reset; clear;

                                                        unset APP_PORT_NUMBER;

                                                        reset; clear; break;
                                                        ;;
                                                    *)
                                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid response was provided.";

                                                        echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                                        ## unset SVC_LIST, we dont need it now
                                                        unset PORT_CONFIRMATION;

                                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                        ;;
                                                esac
                                            done
                                        fi

                                        typeset THIS_CNAME="${CNAME}";
                                        unset METHOD_NAME;
                                        unset CNAME;

                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                                        ## validate the input
                                        ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validatePortNumber.sh ${APP_PORT_NUMBER};
                                        typeset -i RET_CODE=${?};

                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                        CNAME="${THIS_CNAME}";
                                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                        if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                                        then
                                            ## not a number
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Port number provided is already in use.";

                                            echo "$(awk -F "=" '/\<provided.port.already.used\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                            ## unset SVC_LIST, we dont need it now
                                            unset APP_PORT_NUMBER;

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        fi

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NONSSL_LISTEN_PORT -> ${NONSSL_LISTEN_PORT}";

                                        reset; clear;

                                        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                                        buildWebInstance;
                                        ;;
                                esac
                            done
                            ;;
                    esac
                done
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${TMPFILE}" ] && [ -f ${TMPFILE} ] && rm -rf ${TMPFILE};

    unset PORT_NUMBER;
    unset PORT_CONFIRMATION;
    unset NONSSL_LISTEN_PORT;
    unset ENABLE_APPSERVER;
    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  buildWebInstance
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function buildWebInstance
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    typeset THIS_CNAME="${CNAME}";
    unset METHOD_NAME;
    unset CNAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    [ ! -z "${APPSERVER_TYPE}" ] && ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/createWebInstance.sh -w ${WS_PLATFORM} -b ${BUILD_TYPE} -l ${HOST_ADDRESS} -p ${PORT_NUMBER} -a -t ${APPSERVER_TYPE} -h ${APPSERVER_HOST} -P ${APPSERVER_PORT} -e;
    [ -z "${APPSERVER_TYPE}" ] && ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/createWebInstance.sh -w ${WS_PLATFORM} -b ${BUILD_TYPE} -l ${HOST_ADDRESS} -p ${PORT_NUMBER} -e;
    typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

    CNAME="${THIS_CNAME}";
    typeset METHOD_NAME="${THIS_CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        reset; clear;

        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred during the build. Please review logs for any issues.";

        [ -z "${RET_CODE}" ] && echo "\t$(awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";
        [ ! -z "${RET_CODE}" ] && echo "\t$(awk -F "=" '/\<\${RET_CODE}\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        sleep ${MESSAGE_DELAY};
    fi

    while true
    do
        reset; clear;

        echo "\n
            \t$(awk -F "=" '/\<createsite.build.complete\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<createsite.install.server\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        read SELECTION;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION -> ${SELECTION}";

        reset; clear;

        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        case ${SELECTION} in
            [Yy][Ee][Ss]|[Yy])
                reset; clear;

                echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                performSiteInstallation;

                return ${RETURN_CODE};
                ;;
            *)
                reset; clear;

                echo "\n\t$(awk -F "=" '/\<createsite.perform.more.tasks\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                read SELECTION;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION -> ${SELECTION}";

                reset; clear;

                echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                case ${SELECTION} in
                    [Yy][Ee][Ss]|[Yy])
                        reset; clear;

                        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                        main;

                        return ${RETURN_CODE};
                        ;;
                    *)
                        reset; clear;

                        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                        reset; clear; exec ${MAIN_CLASS};

                        return ${RETURN_CODE};
                        ;;
                esac
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    A=0;

    unset RET_CODE;
    unset PLATFORM;
    unset WS_PLATFORM;
    unset TYPE;
    unset BUILD_TYPE;
    unset HOSTNAME;
    unset CONTEXT_ROOT;
    unset PORT_NUMBER;
    unset PORT_CONFIRMATION;
    unset ENABLE_APPSERVER;
    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

reset; clear;

echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

main;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return ${RETURN_CODE};
