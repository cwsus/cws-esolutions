#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  serviceQueryUI.sh
#         USAGE:  ./serviceQueryUI.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Processes a DNS failover by using information previously
#                 obtained by retrieve_site_info.sh
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
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo -n "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";
local METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[[ -z "${PLUGIN_ROOT_DIR}" && -f ${SCRIPT_ROOT}/../lib/plugin.sh ]] && . ${SCRIPT_ROOT}/../lib/plugin.sh;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${PLUGIN_ROOT_DIR}" ] && echo -n "Failed to locate configuration data. Cannot continue." && return 1;

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

unset RET_CODE;

trap "echo -n '$(awk -F "=" '/\<system.trap.signals\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%SIGNAL%/Ctrl-C/")'; sleep "${MESSAGE_DELAY}"; reset; clear; continue " 1 2 3

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        echo -n "\n
            \t\t+-------------------------------------------------------------------+
            \t\t               WELCOME TO \E[0;31m $(awk -F "=" '/\<system.application.title\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//') \033[0m
            \t\t+-------------------------------------------------------------------+
            \t\tSystem Type         : \E[0;36m ${SYSTEM_HOSTNAME} \033[0m
            \t\tSystem Uptime       : \E[0;36m ${SYSTEM_UPTIME} \033[0m
            \t\tUser                : \E[0;36m ${IUSER_AUDIT} \033[0m
            \t\t+-------------------------------------------------------------------+
            \n
        \t$(awk -F "=" '/\<dig.query.provide.address\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
        \t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        echo -n "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                unset COMPLETE;
                unset RESPONSE_DATA;
                unset RECORD_TYPE;
                unset IS_AUTHORIZED;
                unset CONTINUE;
                unset ANSWER;
                unset REVERSE_LOOKUP;
                unset TARGET_NAMESERVER;
                unset SITE_HOSTNAME;
                unset RET_CODE;
                unset REQUEST_ENTRY;
                unset RESPONSE_ENTRY;
                unset RESPONSE_NAMESERVER;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                echo -n "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                exec ${MAIN_CLASS};

                return 0;
                ;;
            *)
                if [ -z "${ANSWER}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                    echo -n "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                typeset -l SITE_HOSTNAME="${ANSWER}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME -> ${SITE_HOSTNAME}";

                requestDesiredNameserver;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

#===  FUNCTION  ===============================================================
#         NAME:  requestDesiredNameserver
#  DESCRIPTION:  Obtains and records the desired nameserver
#   PARAMETERS:  None
#==============================================================================
function requestDesiredNameserver
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        echo -n "
            \t$(awk -F "=" '/\<dig.query.request.nameservers\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<dig.query.valid.nameservers\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%NAMESERVERS%/${NAMED_SERVER_LIST}/")
            \t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        echo -n "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                unset COMPLETE;
                unset RESPONSE_DATA;
                unset RECORD_TYPE;
                unset IS_AUTHORIZED;
                unset CONTINUE;
                unset ANSWER;
                unset REVERSE_LOOKUP;
                unset TARGET_NAMESERVER;
                unset SITE_HOSTNAME;
                unset RET_CODE;
                unset REQUEST_ENTRY;
                unset RESPONSE_ENTRY;
                unset RESPONSE_NAMESERVER;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                echo -n "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                sleep ${MESSAGE_DELAY}; reset; clear; main;
                ;;
            *)
                [ ! -z "${ANSWER}" ] && TARGET_NAMESERVER="${ANSWER}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_NAMESERVER -> ${TARGET_NAMESERVER}";
                ;;
        esac

        break;
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    requestReverseResponse;

    return 0;
}

#===  FUNCTION  ===============================================================
#         NAME:  requestReverseResponse
#  DESCRIPTION:  Obtains and records the desired nameserver
#   PARAMETERS:  None
#==============================================================================
function requestReverseResponse
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        echo -n "
            \t$(awk -F "=" '/\<dig.perform.reverse.lookup\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        echo -n "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                unset COMPLETE;
                unset RESPONSE_DATA;
                unset RECORD_TYPE;
                unset IS_AUTHORIZED;
                unset CONTINUE;
                unset ANSWER;
                unset REVERSE_LOOKUP;
                unset TARGET_NAMESERVER;
                unset SITE_HOSTNAME;
                unset RET_CODE;
                unset REQUEST_ENTRY;
                unset RESPONSE_ENTRY;
                unset RESPONSE_NAMESERVER;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                echo -n "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                sleep ${MESSAGE_DELAY}; reset; clear; main;
                ;;
            [Yy][Ee][Ss]|[Yy)
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reverse lookup confirmed.";

                REVERSE_LOOKUP="${_TRUE}";
                ;;
            *)
                REVERSE_LOOKUP="${_FALSE}";
                ;;
        esac

        break;
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REVERSE_LOOKUP -> ${REVERSE_LOOKUP}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    requestLookupType;

    return 0;
}

#===  FUNCTION  ===============================================================
#         NAME:  requestLookupType
#  DESCRIPTION:  Obtains and records the desired nameserver
#   PARAMETERS:  None
#==============================================================================
function requestLookupType
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        echo -n "
            \t$(awk -F "=" '/\<dig.query.provide.type\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<diq.query.valid.types\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        echo -n "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                unset COMPLETE;
                unset RESPONSE_DATA;
                unset RECORD_TYPE;
                unset IS_AUTHORIZED;
                unset CONTINUE;
                unset ANSWER;
                unset REVERSE_LOOKUP;
                unset TARGET_NAMESERVER;
                unset SITE_HOSTNAME;
                unset RET_CODE;
                unset REQUEST_ENTRY;
                unset RESPONSE_ENTRY;
                unset RESPONSE_NAMESERVER;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                echo -n "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                sleep ${MESSAGE_DELAY}; reset; clear; main;
                ;;
            [Ll][Ii][Ss][Tt]|[Ll])
                ## list available types
                echo -n "\t$(awk -F "=" '/\<dig.query.allowed.types\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                sed '1,16d' ${ALLOWED_RECORD_LIST};

                echo -n "$(awk -F "=" '/\<system.continue.enter\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                read CONTINUE;

                reset; clear; continue;
                ;;
            *)
                [ -z "${ANSWER}" ] && ANSWER="A";

                ## make sure its an allowed type
                IS_AUTHORIZED=$(awk "/\<${ANSWER}\>/" ${ALLOWED_RECORD_LIST} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//');

                if [ -z "${IS_AUTHORIZED}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                    echo -n "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                typeset -u RECORD_TYPE="${ANSWER}";
                ;;
        esac

        break;
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TYPE -> ${RECORD_TYPE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    ## do lookup
    performLookup;

    return 0;
}

#===  FUNCTION  ===============================================================
#         NAME:  performLookup
#  DESCRIPTION:  Obtains and records the desired nameserver
#   PARAMETERS:  None
#==============================================================================
function performLookup
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    reset; clear;

    echo -n "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

    local THIS_CNAME=${CNAME};
    unset METHOD_NAME;
    unset CNAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    ## validate the input
    [[ -z "${TARGET_NAMESERVER}" && -z "${REVERSE_LOOKUP}" ]] && . ${PLUGIN_LIB_DIRECTORY}/runQuery.sh -t ${RECORD_TYPE} -u ${SITE_HOSTNAME} -e;
    [[ -z "${TARGET_NAMESERVER}" && "${REVERSE_LOOKUP}" = "${_TRUE}" ]] && . ${PLUGIN_LIB_DIRECTORY}/runQuery.sh -t ${RECORD_TYPE} -u ${SITE_HOSTNAME} -r -e;
    [[ ! -z "${TARGET_NAMESERVER}" && -z "${REVERSE_LOOKUP}" ]] && . ${PLUGIN_LIB_DIRECTORY}/runQuery.sh -s ${TARGET_NAMESERVER} -t ${RECORD_TYPE} -u ${SITE_HOSTNAME} -e;
    [[ ! -z "${TARGET_NAMESERVER}" && "${REVERSE_LOOKUP}" = "${_TRUE}" ]] && . ${PLUGIN_LIB_DIRECTORY}/runQuery.sh -s ${TARGET_NAMESERVER} -t ${RECORD_TYPE} -u ${SITE_HOSTNAME} -r -e;
    typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

    CNAME="${THIS_CNAME}";
    local METHOD_NAME="${THIS_CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. Return code -> ${RET_CODE}";

    if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]]
    then
        unset RECORD_TYPE;
        unset IS_AUTHORIZED;
        unset CONTINUE;
        unset ANSWER;
        unset REVERSE_LOOKUP;
        unset TARGET_NAMESERVER;
        unset SITE_HOSTNAME;
        unset RET_CODE;

        echo -n "$(awk -F "=" "/no.response.received/{print $2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        sleep "${MESSAGE_DELAY}"; reset; clear; main;
    fi

    ## get the answer out
    if [ ! -s ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ]
    then
        ## something happened that wasnt good
        unset RECORD_TYPE;
        unset IS_AUTHORIZED;
        unset CONTINUE;
        unset ANSWER;
        unset REVERSE_LOOKUP;
        unset TARGET_NAMESERVER;
        unset SITE_HOSTNAME;
        unset RET_CODE;

        [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && rm ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

        echo -n "$(awk -F "=" "/no.response.received/{print $2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        sleep "${MESSAGE_DELAY}"; reset; clear; main;
    fi

    local RESPONSE_DATA=$(awk '/;; ANSWER SECTION:/, /;; AUTHORITY SECTION:/' ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} | grep -w "${RECORD_TYPE}");

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE_DATA -> ${RESPONSE_DATA}";

    if [ -z "${RESPONSE_DATA}" ]
    then
        ## something happened that wasnt good
        unset RESPONSE_DATA;
        unset RECORD_TYPE;
        unset IS_AUTHORIZED;
        unset CONTINUE;
        unset ANSWER;
        unset REVERSE_LOOKUP;
        unset TARGET_NAMESERVER;
        unset SITE_HOSTNAME;
        unset RET_CODE;

        [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && rm ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

        echo -n "$(awk -F "=" "/no.response.received/{print $2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        sleep "${MESSAGE_DELAY}"; reset; clear; main;
    fi

    local REQUEST_ENTRY=$(awk '{print $1}' <<< ${RESPONSE_DATA});
    local RESPONSE_ENTRY=$(awk '{print $NF}' <<< ${RESPONSE_DATA});
    local RESPONSE_NAMESERVER=$(awk -F ":" '/;; SERVER:\>/{print $2}' ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} | cut -d "(" -f 1 | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//');

    [ -z "${RESPONSE_NAMESERVER}" ] && RESPONSE_NAMESERVER="default";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_ENTRY -> ${REQUEST_ENTRY}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE_ENTRY -> ${RESPONSE_ENTRY}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE_NAMESERVER -> ${RESPONSE_NAMESERVER}";

    reset; clear;

    echo -n "
        $(awk -F "=" '/\<dig.result.txt\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%SERVER%/${RESPONSE_NAMESERVER}/" -e "s/%URL%/${REQUEST_ENTRY}/" -e "s/%IPADDR%/${RESPONSE_ENTRY}/")\n
        $(awk -F "=" '/\<dig.review.complete.response\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

    read COMPLETE;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "COMPLETE -> ${COMPLETE}";

    echo -n "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

    case ${COMPLETE} in
        [Yy][Ee][Ss]|[Yy])
            reset; clear;

            unset COMPLETE;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printing datafile to screen..";

            cat ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

            echo -n "$(awk -F "=" '/\<system.continue.enter\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

            read CONTINUE;

            case ${CONTINUE} in
                *)
                    unset CONTINUE;

                    [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && rm -rf ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                    ;;
            esac
            ;;
        *)
            unset COMPLETE;

            [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && rm -rf ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
            ;;
    esac

    rm ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

    while true
    do
        reset; clear;

        echo -n "
            $(awk -F "=" '/\<system.request.complete\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            $(awk -F "=" '/\<system.continue.request\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        echo -n "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        case ${ANSWER} in
            [Yy][Ee][Ss]|[Yy]) break ;;
            *)
                unset COMPLETE;
                unset RESPONSE_DATA;
                unset RECORD_TYPE;
                unset IS_AUTHORIZED;
                unset CONTINUE;
                unset ANSWER;
                unset REVERSE_LOOKUP;
                unset TARGET_NAMESERVER;
                unset SITE_HOSTNAME;
                unset RET_CODE;
                unset REQUEST_ENTRY;
                unset RESPONSE_ENTRY;
                unset RESPONSE_NAMESERVER;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                echo -n "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                exec ${MAIN_CLASS};

                return 0;
                ;;
        esac

        break;
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset COMPLETE;
    unset RESPONSE_DATA;
    unset RECORD_TYPE;
    unset IS_AUTHORIZED;
    unset CONTINUE;
    unset ANSWER;
    unset REVERSE_LOOKUP;
    unset TARGET_NAMESERVER;
    unset SITE_HOSTNAME;
    unset RET_CODE;
    unset REQUEST_ENTRY;
    unset RESPONSE_ENTRY;
    unset RESPONSE_NAMESERVER;

    reset; clear; main;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 0;
}

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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

return 0;
