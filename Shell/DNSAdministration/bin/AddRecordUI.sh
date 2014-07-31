#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  addRecordUI.sh
#         USAGE:  ./addRecordUI.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Processes a DNS failover by using information previously
#             obtained by retrieve_site_info.sh
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
CNAME="$(/usr/bin/env basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}/${0##*/}")";
SCRIPT_ROOT="$(/usr/bin/env dirname "${SCRIPT_ABSOLUTE_PATH}")";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -f "${SCRIPT_ROOT}/../lib/plugin" ] && . "${SCRIPT_ROOT}/../lib/plugin";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${APP_ROOT}" ] && awk -F "=" '/\<1\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[ -f "${PLUGIN_LIB_DIRECTORY}/aliases" ] && . "${PLUGIN_LIB_DIRECTORY}/aliases";
[ -f "${PLUGIN_LIB_DIRECTORY}/functions" ] && . "${PLUGIN_LIB_DIRECTORY}/functions";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

THIS_CNAME="${CNAME}";
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

## validate the input
"${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh" -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

CNAME="${THIS_CNAME}";
typeset METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
then
    "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security violation found while executing ${CNAME} by ${IUSER_AUDIT} on host ${SYSTEM_HOSTNAME}";
    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security configuration blocks execution. Please verify security configuration.";

    awk -F "=" '/\<request.not.authorized\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

    return ${RET_CODE};
fi

unset RET_CODE;

## source aliases/functions ..
[ -s ${PLUGIN_LIB_DIRECTORY}/aliases ] && . ${PLUGIN_LIB_DIRECTORY}/aliases;
[ -s ${PLUGIN_LIB_DIRECTORY}/functions ] && . ${PLUGIN_LIB_DIRECTORY}/functions;

trap '$(awk -F "=" "/\<system.trap.signals\>/{print $2}" ${SYSTEM_MESSAGES} | sed -e "s/^ *//g;s/ *$//g;/^ *#/d;s/#.*//" -e "s/%SIGNAL%/Ctrl-C/"); sleep ${MESSAGE_DELAY}; reset; clear; continue' 1 2 3

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

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    if [ -z "${IS_DNS_RECORD_ADD_ENABLED}" ] || [ "${IS_DNS_RECORD_ADD_ENABLED}" != "${_TRUE}" ]
    then
        reset; clear;

        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS zone additions has not been enabled. Cannot continue.";

        awk -F "=" '/\<request.not.authorized\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        ## terminate this thread and return control to main
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset CHANGE_CONTROL;
        unset METHOD_NAME;
        unset RESPONSE;
        unset ADD_EXISTING;
        unset RETURN_CODE;
        unset RET_CODE;
        unset ADD_EXISTING_RECORD;
        unset CCTLD_VALID;
        unset GTLD_VALID;
        unset REQUESTED_TLD;
        unset SITE_HOSTNAME;
        unset COMPLETE;
        unset BIZ_UNIT;
        unset SITE_PRJCODE;
        unset CNAME;
        unset CNAME;
        unset SCRIPT_ABSOLUTE_PATH;
        unset SCRIPT_ROOT;
        unset METHOD_NAME;
        unset RET_CODE;
        unset PRIMARY_INFO;
        unset SECONDARY_INFO;
        unset CANCEL_REQ;
        unset RECORD_TYPE;
        unset COMPLETE;
        unset CONTINUE;
        unset DATACENTER;
        unset SELECTED_DATACENTER;
        unset ALIAS;
        unset RECORD_TARGET;
        unset ANSWER;
        unset RECORD_PRIORITY;
        unset SERVICE_PRIORITY;
        unset SERVICE_WEIGHT;
        unset SERVICE_PORT;
        unset SERVICE_TTL;
        unset SERVICE_PROTO;
        unset SERVICE_TYPE;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

        sleep ${MESSAGE_DELAY}; reset; clear; /usr/bin/env -i ksh ${MAIN_CLASS};

        return 0;
    fi

    while true
    do
        reset; clear;

        printf "\n
            \t\t+-------------------------------------------------------------------+
            \t\t               $(awk -F "=" '/\<system.application.title\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t\t+-------------------------------------------------------------------+
            \t\t$(awk -F "=" '/\<system.application.hostname\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//') ${SYSTEM_HOSTNAME}
            \t\t$(awk -F "=" '/\<system.application.uptime\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//') ${SYSTEM_UPTIME}
            \t\t$(awk -F "=" '/\<system.application.user\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//') ${IUSER_AUDIT}
            \t\t+-------------------------------------------------------------------+\n\n";

        awk -F "=" '/\<add.zone.select.partition\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.select\>/{print $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read SELECTION;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION -> ${SELECTION}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${SELECTION} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset PARTITION;
                unset CHANGE_CONTROL;
                unset METHOD_NAME;
                unset RESPONSE;
                unset ADD_EXISTING;
                unset RETURN_CODE;
                unset RET_CODE;
                unset ADD_EXISTING_RECORD;
                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;
                unset SITE_HOSTNAME;
                unset COMPLETE;
                unset BIZ_UNIT;
                unset SITE_PRJCODE;
                unset CNAME;
                unset CNAME;
                unset SCRIPT_ABSOLUTE_PATH;
                unset SCRIPT_ROOT;
                unset METHOD_NAME;
                unset RET_CODE;
                unset PRIMARY_INFO;
                unset SECONDARY_INFO;
                unset CANCEL_REQ;
                unset RECORD_TYPE;
                unset COMPLETE;
                unset CONTINUE;
                unset DATACENTER;
                unset SELECTED_DATACENTER;
                unset ALIAS;
                unset RECORD_TARGET;
                unset ANSWER;
                unset RECORD_PRIORITY;
                unset SERVICE_PRIORITY;
                unset SERVICE_WEIGHT;
                unset SERVICE_PORT;
                unset SERVICE_TTL;
                unset SERVICE_PROTO;
                unset SERVICE_TYPE;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                sleep ${MESSAGE_DELAY}; reset; clear; /usr/bin/env -i ksh ${MAIN_CLASS};

                return 0;
                ;;
            [Ii][Nn][Tt][Ee][Rr][Nn][Aa][Ll]|[Ii])
                PARTITION="${INTRANET_TYPE_IDENTIFIER}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PARTITION -> ${PARTITION}";

                reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                provideBusinessUnit;
                ;;
            [Ee][Xx][Tt][Ee][Rr][Nn][Aa][Ll]|[Ee])
                PARTITION="${INTERNET_TYPE_IDENTIFIER}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PARTITION -> ${PARTITION}";

                reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                provideBusinessUnit;
                ;;
            *)
                ## business unit provided was blank
                unset PARTITION;

                awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#
#         NAME:  main
#  DESCRIPTION:  Main entry point for application.
#   PARAMETERS:  None
#      RETURNS:  0
#
#==============================================================================
function provideBusinessUnit
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    while true
    do
        reset; clear;

        awk -F "=" '/\<add.enter.business.unit\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read BIZ_UNIT;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BIZ_UNIT -> ${BIZ_UNIT}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${BIZ_UNIT} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                ## unset variables
                unset PARTITION;
                unset CHANGE_CONTROL;
                unset METHOD_NAME;
                unset RESPONSE;
                unset ADD_EXISTING;
                unset RETURN_CODE;
                unset RET_CODE;
                unset ADD_EXISTING_RECORD;
                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;
                unset SITE_HOSTNAME;
                unset COMPLETE;
                unset BIZ_UNIT;
                unset SITE_PRJCODE;
                unset PRIMARY_INFO;
                unset SECONDARY_INFO;
                unset CANCEL_REQ;
                unset RECORD_TYPE;
                unset COMPLETE;
                unset CONTINUE;
                unset DATACENTER;
                unset SELECTED_DATACENTER;
                unset ALIAS;
                unset RECORD_TARGET;
                unset ANSWER;
                unset RECORD_PRIORITY;
                unset SERVICE_PRIORITY;
                unset SERVICE_WEIGHT;
                unset SERVICE_PORT;
                unset SERVICE_TTL;
                unset SERVICE_PROTO;
                unset SERVICE_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CANCEL_REQ -> ${CANCEL_REQ}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep ${MESSAGE_DELAY}; reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                main;
                ;;
            *)
                if [ -z "${BIZ_UNIT}" ]
                then
                    ## business unit provided was blank
                    unset BIZ_UNIT;

                    awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                provideProjectCode;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#
#         NAME:  provideProjectCode
#  DESCRIPTION:  Main entry point for application.
#   PARAMETERS:  None
#      RETURNS:  0
#
#==============================================================================
function provideProjectCode
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    while true
    do
        reset; clear;

        awk -F "=" '/\<add.enter.prjcode\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read SITE_PRJCODE;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_PRJCODE -> ${SITE_PRJCODE}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${SITE_PRJCODE} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                ## unset variables
                unset CHANGE_CONTROL;
                unset METHOD_NAME;
                unset RESPONSE;
                unset ADD_EXISTING;
                unset RETURN_CODE;
                unset RET_CODE;
                unset ADD_EXISTING_RECORD;
                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;
                unset SITE_HOSTNAME;
                unset COMPLETE;
                unset BIZ_UNIT;
                unset SITE_PRJCODE;
                unset PRIMARY_INFO;
                unset SECONDARY_INFO;
                unset CANCEL_REQ;
                unset RECORD_TYPE;
                unset COMPLETE;
                unset CONTINUE;
                unset DATACENTER;
                unset SELECTED_DATACENTER;
                unset ALIAS;
                unset RECORD_TARGET;
                unset ANSWER;
                unset RECORD_PRIORITY;
                unset SERVICE_PRIORITY;
                unset SERVICE_WEIGHT;
                unset SERVICE_PORT;
                unset SERVICE_TTL;
                unset SERVICE_PROTO;
                unset SERVICE_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CANCEL_REQ -> ${CANCEL_REQ}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep ${MESSAGE_DELAY}; reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                main;
                ;;
            *)
                ## we cant really validate a project code. as long as it isnt blank
                ## we'll use it.
                if [ -z "${SITE_PRJCODE}" ]
                then
                    reset; clear;

                    unset SITE_PRJCODE;

                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No site project code was provided. Cannot continue.";

                    awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ## keep going
                reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                provideSiteHostname;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#
#         NAME:  provideSiteHostname
#  DESCRIPTION:  Main entry point for application.
#   PARAMETERS:  None
#      RETURNS:  0
#
#==============================================================================
function provideSiteHostname
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    while true
    do
        reset; clear;

        awk -F "=" '/\<add.enter.hostname\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<add.enter.format.hostname\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<add.enter.format.allowed.tlds\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read SITE_HOSTNAME;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME -> ${SITE_HOSTNAME}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${SITE_HOSTNAME} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                ## unset variables
                unset CHANGE_CONTROL;
                unset METHOD_NAME;
                unset RESPONSE;
                unset ADD_EXISTING;
                unset RETURN_CODE;
                unset RET_CODE;
                unset ADD_EXISTING_RECORD;
                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;
                unset SITE_HOSTNAME;
                unset COMPLETE;
                unset BIZ_UNIT;
                unset SITE_PRJCODE;
                unset PRIMARY_INFO;
                unset SECONDARY_INFO;
                unset CANCEL_REQ;
                unset RECORD_TYPE;
                unset COMPLETE;
                unset CONTINUE;
                unset DATACENTER;
                unset SELECTED_DATACENTER;
                unset ALIAS;
                unset RECORD_TARGET;
                unset ANSWER;
                unset RECORD_PRIORITY;
                unset SERVICE_PRIORITY;
                unset SERVICE_WEIGHT;
                unset SERVICE_PORT;
                unset SERVICE_TTL;
                unset SERVICE_PROTO;
                unset SERVICE_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CANCEL_REQ -> ${CANCEL_REQ}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep ${MESSAGE_DELAY}; reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                main;
                ;;
            [Hh])
                ## we want to print out the available record type list
                awk -F "=" '/\<allowed.gtld.list\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                awk 'NR>17' ${ALLOWED_GTLD_LIST};

                nawk -F "=" '/\<allowed.cctld.list\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                awk 'NR>16' ${ALLOWED_GTLD_LIST};

                awk -F "=" '/\<system.continue.enter\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                read COMPLETE;

                reset; clear; continue;
                ;;
            *)
                ## we cant validate the hostname other than to say it has
                ## only two parts, the name and the tld. other than that,
                ## not much we can do here
                if [ -z "${SITE_HOSTNAME}" ]
                then
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No site hostname was provided. Cannot continue.";

                    unset SITE_HOSTNAME;

                    awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                if [ $(tr -dc "." <<< ${SITE_HOSTNAME} | wc -c) -ne 1 ]
                then
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_HOSTNAME} is not properly formatted.";

                    unset SITE_HOSTNAME;

                    awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    sleep ${MESSAGE_DELAY}; reset; clear; continue;
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME -> ${SITE_HOSTNAME}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating TLD..";

                typeset REQUESTED_TLD=$(cut -d "." -f 2 <<< ${SITE_HOSTNAME});

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUESTED_TLD -> ${REQUESTED_TLD}";

                ## make sure we got a valid tld. we're only checking the gTLD's,
                ## for a list, see http://en.wikipedia.org/wiki/List_of_Internet_top-level_domains
                typeset -i GTLD_VALID=$(awk "/\<${REQUESTED_TLD}\>/{print $1}" ${ALLOWED_GTLD_LIST} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//');
                typeset -i CCTLD_VALID=$(awk "/\<${REQUESTED_TLD}\>/{print $1}" ${ALLOWED_CCTLD_LIST} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//');

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "GTLD_VALID -> ${GTLD_VALID}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CCTLD_VALID -> ${CCTLD_VALID}";

                if [ -z "${GTLD_VALID}" ] || [ "${GTLD_VALID}" = "" ] && [ -z "${CCTLD_VALID}" ] || [ "${CCTLD_VALID}" = "" ]
                then
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_HOSTNAME} is not properly formatted.";

                    unset CCTLD_VALID;
                    unset GTLD_VALID;
                    unset REQUESTED_TLD;
                    unset SITE_HOSTNAME;

                    awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    sleep ${MESSAGE_DELAY}; reset; clear; continue;
                fi

                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;

                ## make sure there isnt already a zone with this hostname
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling retrieve_service to ensure that no records exist with ${SITE_HOSTNAME}..";

                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/retrieveServiceInfo.sh -b ${BIZ_UNIT} -i ${PARTITION} -e;
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ ! -z "${SERVICE_DETAIL[*]}" ]
                then
                    ## we already have a zone file with this hostname in it. we can't create a duplicate zone
                    ## it wont load, but we can add additional records to an existing zone
                    unset RET_CODE;
                    unset RETURN_CODE;

                    reset; clear;

                    while true
                    do
                        awk -F "=" '/\<add.zone.already.exists\>/{print "\t" $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%HOSTNAME%/${SITE_HOSTNAME}/";
                        awk -F "=" '/\<add.record.subdomains\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%ZONE_NAME%/${SITE_HOSTNAME}/";
                        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        read ADD_EXISTING;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_EXISTING -> ${ADD_EXISTING}";

                        reset; clear;

                        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        case ${ADD_EXISTING} in
                            [Yy][Ee][Ss]|[Yy])
                                ## yes, we're adding a new entry to an existing zone. take user to the
                                ## zone update ui to request "INFO"
                                unset RESPONSE;
                                unset RET_CODE;
                                unset RETURN_CODE;

                                ADD_EXISTING_RECORD=${_TRUE};

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add additional records to existing zone confirmed.";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_EXISTING_RECORD->${ADD_EXISTING_RECORD}";

                                ## need to capture change order number here
                                reset; clear; provideChangeControl;

                                if [ ! -z "${CANCEL_REQ}" ]
                                then
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

                                    reset; clear;

                                    awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                                    main;
                                fi
                                ;;
                            [Nn][Oo]|[Nn])
                                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_HOSTNAME} already exists in the DNS infrastructure.";

                                awk -F "=" '/\<add.zone.already.exists.no.add\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%HOSTNAME%/${SITE_HOSTNAME}/";

                                unset RET_CODE;
                                unset RETURN_CODE;
                                unset SITE_HOSTNAME;

                                reset; clear;

                                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                                main;
                                ;;
                            [Xx]|[Qq]|[Cc])
                                reset; clear;

                                ## unset variables
                                unset CHANGE_CONTROL;
                                unset METHOD_NAME;
                                unset RESPONSE;
                                unset ADD_EXISTING;
                                unset RETURN_CODE;
                                unset RET_CODE;
                                unset ADD_EXISTING_RECORD;
                                unset CCTLD_VALID;
                                unset GTLD_VALID;
                                unset REQUESTED_TLD;
                                unset SITE_HOSTNAME;
                                unset COMPLETE;
                                unset BIZ_UNIT;
                                unset SITE_PRJCODE;
                                unset PRIMARY_INFO;
                                unset SECONDARY_INFO;
                                unset CANCEL_REQ;
                                unset RECORD_TYPE;
                                unset COMPLETE;
                                unset CONTINUE;
                                unset DATACENTER;
                                unset SELECTED_DATACENTER;
                                unset ALIAS;
                                unset RECORD_TARGET;
                                unset ANSWER;
                                unset RECORD_PRIORITY;
                                unset SERVICE_PRIORITY;
                                unset SERVICE_WEIGHT;
                                unset SERVICE_PORT;
                                unset SERVICE_TTL;
                                unset SERVICE_PROTO;
                                unset SERVICE_TYPE;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CANCEL_REQ -> ${CANCEL_REQ}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                reset; clear;

                                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                                main;
                                ;;
                            *)
                                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${RESPONSE} is not valid.";

                                unset RESPONSE;

                                awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                sleep ${MESSAGE_DELAY}; reset; clear; continue;
                                ;;
                        esac
                    done
                else
                    ## this zone doesnt yet exist, so we're safe to create.
                    ## now we need to get the associated change control. we
                    ## dont need it to create the zone other than for audit
                    ## purposes
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_HOSTNAME} does NOT already exist in the DNS infrastructure";

                    unset RET_CODE;
                    unset RETURN_CODE;

                    reset; clear;

                    awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                    provideChangeControl;

                    if [ ! -z "${CANCEL_REQ}" ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

                        reset; clear;

                        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                        main;
                    fi
                fi
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#
#         NAME:  provideChangeControl
#  DESCRIPTION:  Main entry point for application.
#   PARAMETERS:  None
#      RETURNS:  0
#
#==============================================================================
function provideChangeControl
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    while true
    do
        [ ! -z "${CANCEL_REQ}" ] && [ "${CANCEL_REQ}" = "${_TRUE}" ] && break;

        reset; clear;

        typeset THIS_CNAME="${CNAME}";
        unset METHOD_NAME;
        unset CNAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

        reset; clear;

        ## validate the input
        . "${APP_ROOT}/${BIN_DIRECTORY}"/obtainChangeControl.sh;

        reset; clear;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

        CNAME="${THIS_CNAME}";
        typeset METHOD_NAME="${THIS_CNAME}#${0}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_CONTROL -> ${CHANGE_CONTROL}";

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        if [[ ! -z "${CANCEL_REQ}" && "${CANCEL_REQ}" = "${_TRUE}" ]]
        then
            reset; clear;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

            awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

            ## terminate this thread and return control to main
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            unset CHANGE_CONTROL;
            unset METHOD_NAME;
            unset RESPONSE;
            unset ADD_EXISTING;
            unset RETURN_CODE;
            unset RET_CODE;
            unset ADD_EXISTING_RECORD;
            unset CCTLD_VALID;
            unset GTLD_VALID;
            unset REQUESTED_TLD;
            unset SITE_HOSTNAME;
            unset COMPLETE;
            unset BIZ_UNIT;
            unset SITE_PRJCODE;

            reset; clear;

            awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

            main;
        fi

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating zone files..";

        ## unset methodname and cname
        typeset THIS_CNAME="${CNAME}";
        unset METHOD_NAME;
        unset CNAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

        ## validate the input
        ${PLUGIN_LIB_DIRECTORY}/createNewZone.sh -b $(tr "[a-z]" "[A-Z]" <<< ${BIZ_UNIT}) -p $(tr "[a-z]" "[A-Z]" <<< ${SITE_PRJCODE}) -z ${SITE_HOSTNAME} -i "${PARTITION}" -c ${CHANGE_CONTROL} -e;
        typeset -i RET_CODE=${?};

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

        CNAME="${THIS_CNAME}";
        typeset METHOD_NAME="${THIS_CNAME}#${0}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. Validating...";

        if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
        then
            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone creation FAILED. RET_CODE -> ${RET_CODE}";

            [ -z "${RET_CODE}" ] && awk -F "=" '/\<99\>/{print "\t" $2" \n"}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
            [ ! -z "${RET_CODE}" ] && awk -F "=" '/\<\${RET_CODE}\>/{print "\t" \$2 "\n"}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

            unset RETURN_CODE;
            unset RET_CODE;

            sleep ${MESSAGE_DELAY}; reset; clear; continue;
        fi

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone creation complete. Proceeding to record addition";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

        providePrimaryAddress;
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  providePrimaryAddress
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#==============================================================================
function providePrimaryAddress
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    while true
    do
        reset; clear;

        awk -F "=" '/\<add.enter.ipaddr.primary\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%PRIMARY_DATACENTER%/${PRIMARY_DATACENTER}/";
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read PRIMARY_INFO;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRIMARY_INFO -> ${PRIMARY_INFO}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${PRIMARY_INFO} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                CANCEL_REQ="${_TRUE}";

                ## unset variables
                unset CHANGE_CONTROL;
                unset METHOD_NAME;
                unset RESPONSE;
                unset ADD_EXISTING;
                unset RETURN_CODE;
                unset RET_CODE;
                unset ADD_EXISTING_RECORD;
                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;
                unset SITE_HOSTNAME;
                unset COMPLETE;
                unset BIZ_UNIT;
                unset SITE_PRJCODE;
                unset PRIMARY_INFO;
                unset SECONDARY_INFO;
                unset CANCEL_REQ;
                unset RECORD_TYPE;
                unset COMPLETE;
                unset CONTINUE;
                unset DATACENTER;
                unset SELECTED_DATACENTER;
                unset ALIAS;
                unset RECORD_TARGET;
                unset ANSWER;
                unset RECORD_PRIORITY;
                unset SERVICE_PRIORITY;
                unset SERVICE_WEIGHT;
                unset SERVICE_PORT;
                unset SERVICE_TTL;
                unset SERVICE_PROTO;
                unset SERVICE_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CANCEL_REQ -> ${CANCEL_REQ}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep ${MESSAGE_DELAY}; reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                main;
                ;;
            *)
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/validators/validateRecordData.sh datacenter ${PRIMARY_INFO}
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    unset PRIMARY_INFO;
                    unset RET_CODE;

                    reset; clear;

                    awk -F "=" '/\<ip.address.improperly.formatted\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ## run the ip addr through the validator
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating record target..";

                ## unset methodname and cname
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/validators/validateRecordData.sh target a ${PRIMARY_INFO};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ ${RET_CODE} -eq 0 ] || [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                then
                    if [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                    then
                        ## we got a warning on validation - we arent failing, but we do want to inform
                        "${LOGGER}" "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A warning occurred during record validation - failed to validate that record is active.";

                        [ -z "${RET_CODE}" ] && awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                        [ ! -z "${RET_CODE}" ] && awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        sleep "${MESSAGE_DELAY}";
                    fi

                    ## unset methodname and cname
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t A -a "${PRIMARY_INFO}" -d ${PRIMARY_DATACENTER} -i "${PARTITION}" -r -e
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                    then
                        ## zone failed to update with primary ip addr
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone update to add primary IP FAILED. Return code -> ${RET_CODE}";

                        [ -z "${RET_CODE}" ] && awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                        [ ! -z "${RET_CODE}" ] && awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        unset RET_CODE;
                        unset RETURN_CODE;
                        unset PRIMARY_INFO;

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone successfully updated";

                    awk -F "=" '/\<add.zone.update.success\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    reset; clear;

                    awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                    provideSecondaryAddress;
                else
                    ## failed to validate record.
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Primary IP address provided failed validation. Cannot continue.";

                    [ -z "${RET_CODE}" ] && awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                    [ ! -z "${RET_CODE}" ] && awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    unset RET_CODE;
                    unset RETURN_CODE;
                    unset PRIMARY_INFO;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;
    unset PRIMARY_INFO;
    unset RETURN_CODE;
    unset RET_CODE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  provideSecondaryAddress
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#==============================================================================
function provideSecondaryAddress
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    while true
    do
        reset; clear;

        awk -F "=" '/\<add.enter.ipaddr.secondary\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%SECONDARY_DATACENTER%/${SECONDARY_DATACENTER}/";
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read SECONDARY_INFO;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SECONDARY_INFO -> ${SECONDARY_INFO}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${SECONDARY_INFO} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                CANCEL_REQ="${_TRUE}";

                ## unset variables
                unset CHANGE_CONTROL;
                unset METHOD_NAME;
                unset RESPONSE;
                unset ADD_EXISTING;
                unset RETURN_CODE;
                unset RET_CODE;
                unset ADD_EXISTING_RECORD;
                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;
                unset SITE_HOSTNAME;
                unset COMPLETE;
                unset BIZ_UNIT;
                unset SITE_PRJCODE;
                unset PRIMARY_INFO;
                unset SECONDARY_INFO;
                unset CANCEL_REQ;
                unset RECORD_TYPE;
                unset COMPLETE;
                unset CONTINUE;
                unset DATACENTER;
                unset SELECTED_DATACENTER;
                unset ALIAS;
                unset RECORD_TARGET;
                unset ANSWER;
                unset RECORD_PRIORITY;
                unset SERVICE_PRIORITY;
                unset SERVICE_WEIGHT;
                unset SERVICE_PORT;
                unset SERVICE_TTL;
                unset SERVICE_PROTO;
                unset SERVICE_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep ${MESSAGE_DELAY}; reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                main;
                ;;
            *)
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/validators/validateRecordData.sh datacenter ${SECONDARY_INFO}
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    unset SECONDARY_INFO;
                    unset RET_CODE;

                    reset; clear;

                    awk -F "=" '/\<ip.address.improperly.formatted\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ## run the ip addr through the validator
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating record target..";

                ## unset methodname and cname
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/validators/validateRecordData.sh target a ${SECONDARY_INFO};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ ${RET_CODE} -eq 0 ] || [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                then
                    if [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                    then
                        ## we got a warning on validation - we arent failing, but we do want to inform
                        "${LOGGER}" "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A warning occurred during record validation - failed to validate that record is active.";

                        [ -z "${RET_CODE}" ] && awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                        [ ! -z "${RET_CODE}" ] && awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        sleep "${MESSAGE_DELAY}";
                    fi

                    ## unset methodname and cname
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t A -a "${SECONDARY_INFO}" -d ${SECONDARY_DATACENTER} -i "${PARTITION}" -r -e
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                    then
                        ## zone failed to update with primary ip addr
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone update to add primary IP FAILED. Return code -> ${RET_CODE}";

                        [ -z "${RET_CODE}" ] && awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                        [ ! -z "${RET_CODE}" ] && awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        unset RET_CODE;
                        unset RETURN_CODE;
                        unset SECONDARY_INFO;

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone successfully updated";

                    awk -F "=" '/\<add.zone.update.success\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    ## do we want to add additional apex records ?
                    while true
                    do
                        unset ADD_COMPLETE;

                        reset; clear;

                        awk -F "=" '/\<add.request.additional.records\>/{print "\t" $2 "\n"}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        read ANSWER;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

                        reset; clear;

                        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        case ${ANSWER} in
                            [Yy][Ee][Ss]|[Yy])
                                ## user wishes to add additional records to root of zone
                                ## make sure our variables are empty and break to restart
                                unset ANSWER;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add additional records confirmed. ADD_RECORDS has been set to true. Breaking..";

                                reset; clear;

                                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                                provideRecordType;
                                ;;
                            [Nn][Oo]|[Nn])
                                ## user does not wish to add additional records to root zone
                                ## ask if user wishes to add subdomains to zone
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add additional records declined. Request for subdomains..";
                                unset ANSWER;

                                while true
                                do
                                    reset; clear;

                                    awk -F "=" '/\<add.record.subdomains\>/{print "\t" $2 "\n"}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                    read ANSWER;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

                                    reset; clear;

                                    awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                    case ${ANSWER} in
                                        [Yy][Ee][Ss]|[Yy])
                                            ## user wishes to now add subdomain records.
                                            ## process via add_records
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add subdomain records confirmed. ADD_SUBDOMAINS has been set to true. Breaking..";
                                            unset ANSWER;

                                            ADD_SUBDOMAINS="${_TRUE}";

                                            reset; clear;

                                            awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                                            provideRecordType;
                                            ;;
                                        [Nn][Oo]|[Nn])
                                            ## user does not wish to add subdomain records
                                            ## this completes processing, send to reviewZone
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add subdomain records declined. ADD_SUBDOMAINS has been set to false. Breaking..";
                                            unset ANSWER;

                                            reset; clear;

                                            awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                                            reviewZone;
                                            ;;
                                        *)
                                            ## no valid selection provided
                                            unset ANSWER;

                                            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not received. Please try again";

                                            awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            ;;
                                    esac
                                done
                                ;;
                            *)
                                ## no valid response provided
                                unset ANSWER;

                                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not received. Please try again";

                                awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                ;;
                        esac
                    done
                else
                    ## failed to validate record.
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Secondary IP address provided failed validation. Cannot continue.";

                    [ -z "${RET_CODE}" ] && awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                    [ ! -z "${RET_CODE}" ] && awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    unset RET_CODE;
                    unset RETURN_CODE;
                    unset SECONDARY_INFO;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;
    unset SECONDARY_INFO;
    unset RETURN_CODE;
    unset RET_CODE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  provideRecordType
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#==============================================================================
function provideRecordType
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    while true
    do
        reset; clear;

        awk -F "=" '/\<add.enter.record.type\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%SECONDARY_DATACENTER%/${SECONDARY_DATACENTER}/";
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read RECORD_TYPE;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TYPE -> ${RECORD_TYPE}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${RECORD_TYPE} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                ## unset variables
                unset CHANGE_CONTROL;
                unset METHOD_NAME;
                unset RESPONSE;
                unset ADD_EXISTING;
                unset RETURN_CODE;
                unset RET_CODE;
                unset ADD_EXISTING_RECORD;
                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;
                unset SITE_HOSTNAME;
                unset COMPLETE;
                unset BIZ_UNIT;
                unset SITE_PRJCODE;
                unset PRIMARY_INFO;
                unset SECONDARY_INFO;
                unset CANCEL_REQ;
                unset RECORD_TYPE;
                unset COMPLETE;
                unset CONTINUE;
                unset DATACENTER;
                unset SELECTED_DATACENTER;
                unset ALIAS;
                unset RECORD_TARGET;
                unset ANSWER;
                unset RECORD_PRIORITY;
                unset SERVICE_PRIORITY;
                unset SERVICE_WEIGHT;
                unset SERVICE_PORT;
                unset SERVICE_TTL;
                unset SERVICE_PROTO;
                unset SERVICE_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CANCEL_REQ -> ${CANCEL_REQ}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep ${MESSAGE_DELAY}; reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                main;
                ;;
            [Hh])
                ## we want to print out the available record type list
                awk 'NR>16' ${ALLOWED_RECORD_LIST};

                awk -F "=" '/\<system.continue.enter\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                read CONTINUE;

                unset CONTINUE;
                unset RECORD_TYPE;

                reset; clear; continue;
                ;;
            *)
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/validators/validateRecordData.sh type ${RECORD_TYPE};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    unset RECORD_TYPE;
                    unset RET_CODE;

                    reset; clear;

                    awk -F "=" '/\<record.type.disallowed\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ## get record target
                reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                provideDataCenter;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;
    unset SECONDARY_INFO;
    unset RETURN_CODE;
    unset RET_CODE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  provideDataCenter
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#==============================================================================
function provideDataCenter
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    while true
    do
        reset; clear;

        awk -F "=" '/\<add.record.provide.datacenter\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        for DATACENTER in ${DATACENTERS[*]}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DATACENTER -> ${DATACENTER}";

            awk -F "=" '/\<add.record.available.datacenters\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%DATACENTER%/${DATACENTER}/";
        done

        awk -F "=" '/\<add.record.add.to.both.datacenters\>/{print "\t" $2}' | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read SELECTED_DATACENTER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTED_DATACENTER -> ${SELECTED_DATACENTER}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${SELECTED_DATACENTER} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                ## unset variables
                unset CHANGE_CONTROL;
                unset METHOD_NAME;
                unset RESPONSE;
                unset ADD_EXISTING;
                unset RETURN_CODE;
                unset RET_CODE;
                unset ADD_EXISTING_RECORD;
                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;
                unset SITE_HOSTNAME;
                unset COMPLETE;
                unset BIZ_UNIT;
                unset SITE_PRJCODE;
                unset PRIMARY_INFO;
                unset SECONDARY_INFO;
                unset CANCEL_REQ;
                unset RECORD_TYPE;
                unset COMPLETE;
                unset CONTINUE;
                unset DATACENTER;
                unset SELECTED_DATACENTER;
                unset ALIAS;
                unset RECORD_TARGET;
                unset ANSWER;
                unset RECORD_PRIORITY;
                unset SERVICE_PRIORITY;
                unset SERVICE_WEIGHT;
                unset SERVICE_PORT;
                unset SERVICE_TTL;
                unset SERVICE_PROTO;
                unset SERVICE_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CANCEL_REQ -> ${CANCEL_REQ}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep ${MESSAGE_DELAY}; reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                main;
                ;;
            [Bb][Oo][Tt][Hh])
                ## get record target
                unset SELECTED_DATACENTER;

                reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                [ -z "${ADD_SUBDOMAINS}" ] && provideRecordTarget;
                [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_TRUE}" ] && provideRecordAlias;
                ;;
            *)
                if [ -z "${SELECTED_DATACENTER}" ]
                then
                    unset SELECTED_DATACENTER;
                    unset RET_CODE;

                    reset; clear;

                    awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                ## validate the input
                contains ${SELECTED_DATACENTER} ${DATACENTERS[*]};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    unset SELECTED_DATACENTER;
                    unset RET_CODE;

                    reset; clear;

                    awk -F "=" '/\<record.type.disallowed\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ## get record target
                reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                [ -z "${ADD_SUBDOMAINS}" ] && provideRecordTarget;
                [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_TRUE}" ] && provideRecordAlias;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;
    unset SECONDARY_INFO;
    unset RETURN_CODE;
    unset RET_CODE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  provideRecordAlias
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#==============================================================================
function provideRecordAlias
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting zone-level SRV record information..";

    while true
    do
        reset; clear;

        awk -F "=" '/\<add.record.alias\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read ALIAS;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ALIAS -> ${ALIAS}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${ALIAS} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                ## unset variables
                unset CHANGE_CONTROL;
                unset METHOD_NAME;
                unset RESPONSE;
                unset ADD_EXISTING;
                unset RETURN_CODE;
                unset RET_CODE;
                unset ADD_EXISTING_RECORD;
                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;
                unset SITE_HOSTNAME;
                unset COMPLETE;
                unset BIZ_UNIT;
                unset SITE_PRJCODE;
                unset PRIMARY_INFO;
                unset SECONDARY_INFO;
                unset CANCEL_REQ;
                unset RECORD_TYPE;
                unset COMPLETE;
                unset CONTINUE;
                unset DATACENTER;
                unset SELECTED_DATACENTER;
                unset ALIAS;
                unset RECORD_TARGET;
                unset ANSWER;
                unset RECORD_PRIORITY;
                unset SERVICE_PRIORITY;
                unset SERVICE_WEIGHT;
                unset SERVICE_PORT;
                unset SERVICE_TTL;
                unset SERVICE_PROTO;
                unset SERVICE_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CANCEL_REQ -> ${CANCEL_REQ}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep ${MESSAGE_DELAY}; reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                main;
                ;;
            *)
                if [ -z "${ALIAS}" ]
                then
                    ## no service name was provided, this is technically allowed,
                    ## but we're going to dis-allow it because we want to know for
                    ## sure what to add.
                    unset ALIAS;

                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No alias was provided. Cannot continue.";

                    awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    sleep ${MESSAGE_DELAY}; reset; clear; continue;
                fi

                reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                provideRecordTarget;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  provideRecordTarget
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#==============================================================================
function provideRecordTarget
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    while true
    do
        reset; clear;

        awk -F "=" '/\<add.record.target\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%RECORD_TYPE%/${RECORD_TYPE}/";
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read RECORD_TARGET;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TARGET -> ${RECORD_TARGET}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${RECORD_TARGET} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                ## unset variables
                unset CHANGE_CONTROL;
                unset METHOD_NAME;
                unset RESPONSE;
                unset ADD_EXISTING;
                unset RETURN_CODE;
                unset RET_CODE;
                unset ADD_EXISTING_RECORD;
                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;
                unset SITE_HOSTNAME;
                unset COMPLETE;
                unset BIZ_UNIT;
                unset SITE_PRJCODE;
                unset PRIMARY_INFO;
                unset SECONDARY_INFO;
                unset CANCEL_REQ;
                unset RECORD_TYPE;
                unset COMPLETE;
                unset CONTINUE;
                unset DATACENTER;
                unset SELECTED_DATACENTER;
                unset ALIAS;
                unset RECORD_TARGET;
                unset ANSWER;
                unset RECORD_PRIORITY;
                unset SERVICE_PRIORITY;
                unset SERVICE_WEIGHT;
                unset SERVICE_PORT;
                unset SERVICE_TTL;
                unset SERVICE_PROTO;
                unset SERVICE_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CANCEL_REQ -> ${CANCEL_REQ}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep ${MESSAGE_DELAY}; reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                main;
                ;;
            [a-zA-Z0-9.-]*)
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/validators/validateRecordData.sh target ${RECORD_TYPE} ${RECORD_TARGET};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    unset RECORD_TARGET;
                    unset RET_CODE;

                    reset; clear;

                    awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ## get record target
                reset; clear;

                ## these are apex records - only a handful are allowed
                case ${RECORD_TYPE} in
                    [Aa]+|[Ll][Oo][Cc]|[Nn][Ss])
                        ## we have enough to add the record, so do it
                        ## unset methodname and cname
                        reset; clear;

                        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                        [ -z "${SELECTED_DATACENTER}" ] && [ -z "${ADD_SUBDOMAINS}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -i "${PARTITION}" -r -e;
                        [ -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_FALSE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -i "${PARTITION}" -r -e;
                        [ -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_TRUE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -i "${PARTITION}" -s -e;
                        [ ! -z "${SELECTED_DATACENTER}" ] && [ -z "${ADD_SUBDOMAINS}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -d ${SELECTED_DATACENTER} -i "${PARTITION}" -r -e;
                        [ ! -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_FALSE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -d ${SELECTED_DATACENTER} -i "${PARTITION}" -r -e;
                        [ ! -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] || [ "${ADD_SUBDOMAINS}" = "${_TRUE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -d ${SELECTED_DATACENTER} -i "${PARTITION}" -s -e;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                        reset; clear;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                        ;;
                    [Cc][Nn][Aa][Mm][Ee]|[Pp][Tt][Rr]|[Tt][Xx][Tt])
                        if [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_TRUE}" ]
                        then
                            ## selected record type cannot be added to the apex
                            unset RECORD_TARGET;

                            awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        fi

                        ## we have enough to add the record, so do it
                        ## unset methodname and cname
                        reset; clear;

                        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                        [ -z "${SELECTED_DATACENTER}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -i "${PARTITION}" -s -e;
                        [ ! -z "${SELECTED_DATACENTER}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -d ${SELECTED_DATACENTER} -i "${PARTITION}" -s -e;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                        reset; clear;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                        ;;
                    [Mm][Xx])
                        ## need a priority here..
                        reset; clear; provideRecordPriority;

                        if [ -z "${RECORD_PRIORITY}" ]
                        then
                            ## no priority was provided
                            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No record priority was provided. Redirecting...";

                            awk -F "=" '/\<mx.priority.not.numeric\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            sleep ${MESSAGE_DELAY}; reset; clear; provideRecordPriority;
                        fi

                        reset; clear;

                        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                        ## validate the input
                        [ -z "${SELECTED_DATACENTER}" ] && [ -z "${ADD_SUBDOMAINS}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET},${RECORD_PRIORITY}" -r -e;
                        [ -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_FALSE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET},${RECORD_PRIORITY}" -r -e;
                        [ -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_TRUE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET},${RECORD_PRIORITY}" -s -e;
                        [ ! -z "${SELECTED_DATACENTER}" ] && [ -z "${ADD_SUBDOMAINS}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET},${RECORD_PRIORITY}" -d ${SELECTED_DATACENTER} -r -e;
                        [ ! -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_FALSE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET},${RECORD_PRIORITY}" -d ${SELECTED_DATACENTER} -r -e;
                        [ ! -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] || [ "${ADD_SUBDOMAINS}" = "${_TRUE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET},${RECORD_PRIORITY}" -d ${SELECTED_DATACENTER} -s -e;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                        reset; clear;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                        then
                            printf "moo";
                        fi

                        ## add more ?
                        ## add sub ?
                        ;;
                    [Ss][Rr][Vv])
                        ## get a bunch of information here ...
                        reset; clear;

                        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                        provideServiceType;

                        if [ -z "${SERVICE_TYPE}" ]
                        then
                            ## no priority was provided
                            reset; clear;

                            awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            provideServiceType;
                        fi

                        reset; clear;

                        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                        provideServiceProtocol;

                        if [ -z "${SERVICE_PROTO}" ]
                        then
                            ## no priority was provided
                            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No record protocol was provided. Redirecting...";

                            reset; clear;

                            awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            provideServiceProtocol;
                        fi

                        reset; clear;

                        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                        provideRecordTTL;

                        if [ -z "${SERVICE_TTL}" ]
                        then
                            ## no priority was provided
                            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No record TTL was provided. Redirecting...";

                            reset; clear;

                            awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            provideRecordTTL;
                        fi

                        reset; clear;

                        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                        provideRecordPriority;

                        if [ -z "${SERVICE_PRIORITY}" ]
                        then
                            ## no priority was provided
                            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No record priority was provided. Redirecting...";

                            reset; clear;

                            awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            provideRecordPriority;
                        fi

                        reset; clear;

                        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                        reset; clear; provideServicePort;

                        if [ -z "${SERVICE_WEIGHT}" ]
                        then
                            ## no priority was provided
                            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No record weight was provided. Redirecting...";

                            reset; clear;

                            awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            provideServicePort;
                        fi

                        reset; clear;

                        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                        ## validate the input
                        [ -z "${SELECTED_DATACENTER}" ] && [ -z "${ADD_SUBDOMAINS}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET},${RECORD_PRIORITY}" -i "${PARTITION}" -r -e;
                        [ -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_FALSE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET},${RECORD_PRIORITY}" -i "${PARTITION}" -r -e;
                        [ -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_TRUE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET},${RECORD_PRIORITY}" -i "${PARTITION}" -s -e;
                        [ ! -z "${SELECTED_DATACENTER}" ] && [ -z "${ADD_SUBDOMAINS}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET},${RECORD_PRIORITY}" -d ${SELECTED_DATACENTER} -i "${PARTITION}" -r -e;
                        [ ! -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_FALSE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET},${RECORD_PRIORITY}" -d ${SELECTED_DATACENTER} -i "${PARTITION}" -r -e;
                        [ ! -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] || [ "${ADD_SUBDOMAINS}" = "${_TRUE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET},${RECORD_PRIORITY}" -d ${SELECTED_DATACENTER} -i "${PARTITION}" -s -e;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                        reset; clear;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                        then
                            printf "moo";
                        fi

                        ## add more ?
                        ## add sub ?
                        ;;
                    *)
                        ## record not supported at the apex
                        unset RECORD_TARGET;

                        awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        ;;
                esac
                ;;
            *)
                unset RECORD_TARGET;

                awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;
    unset SECONDARY_INFO;
    unset RETURN_CODE;
    unset RET_CODE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  provideRecordPriority
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#==============================================================================
function provideRecordPriority
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    while true
    do
        reset; clear;

        awk -F "=" '/\<add.record.priority\>/{print "\t" $2}' | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%RECORD_TYPE%/${RECORD_TYPE}/";
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                ## unset variables
                unset CHANGE_CONTROL;
                unset METHOD_NAME;
                unset RESPONSE;
                unset ADD_EXISTING;
                unset RETURN_CODE;
                unset RET_CODE;
                unset ADD_EXISTING_RECORD;
                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;
                unset SITE_HOSTNAME;
                unset COMPLETE;
                unset BIZ_UNIT;
                unset SITE_PRJCODE;
                unset PRIMARY_INFO;
                unset SECONDARY_INFO;
                unset CANCEL_REQ;
                unset RECORD_TYPE;
                unset COMPLETE;
                unset CONTINUE;
                unset DATACENTER;
                unset SELECTED_DATACENTER;
                unset ALIAS;
                unset RECORD_TARGET;
                unset ANSWER;
                unset RECORD_PRIORITY;
                unset SERVICE_PRIORITY;
                unset SERVICE_WEIGHT;
                unset SERVICE_PORT;
                unset SERVICE_TTL;
                unset SERVICE_PROTO;
                unset SERVICE_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CANCEL_REQ -> ${CANCEL_REQ}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep ${MESSAGE_DELAY}; reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                main;
                ;;
            [\d])
                ## numeric weight. we dont really care what the value is because it could be anything
                [[ "${RECORD_TYPE}" = [Mm][Xx] ]] && RECORD_PRIORITY=${ANSWER};
                [[ "${RECORD_TYPE}" = [Ss][Rr][Vv] ]] && [ -z "${SERVICE_PRIORITY}" ] && SERVICE_PRIORITY=${ANSWER};
                [[ "${RECORD_TYPE}" = [Ss][Rr][Vv] ]] && [ -z "${SERVICE_WEIGHT}" ] && [ ! -z "${SERVICE_PRIORITY}" ] && SERVICE_WEIGHT=${ANSWER};

                reset; clear; break;
                ;;
            *)
                ## business unit provided was blank
                unset ANSWER;

                awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;
    unset SECONDARY_INFO;
    unset RETURN_CODE;
    unset RET_CODE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  provideServicePort
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#==============================================================================
function provideServicePort
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting zone-level SRV record information..";

    while true
    do
        reset; clear;

        awk -F "=" '/\<add.record.srv.port\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read SERVICE_PORT;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_PORT -> ${SERVICE_PORT}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${SERVICE_PORT} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                ## unset variables
                unset CHANGE_CONTROL;
                unset METHOD_NAME;
                unset RESPONSE;
                unset ADD_EXISTING;
                unset RETURN_CODE;
                unset RET_CODE;
                unset ADD_EXISTING_RECORD;
                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;
                unset SITE_HOSTNAME;
                unset COMPLETE;
                unset BIZ_UNIT;
                unset SITE_PRJCODE;
                unset PRIMARY_INFO;
                unset SECONDARY_INFO;
                unset CANCEL_REQ;
                unset RECORD_TYPE;
                unset COMPLETE;
                unset CONTINUE;
                unset DATACENTER;
                unset SELECTED_DATACENTER;
                unset ALIAS;
                unset RECORD_TARGET;
                unset ANSWER;
                unset RECORD_PRIORITY;
                unset SERVICE_PRIORITY;
                unset SERVICE_WEIGHT;
                unset SERVICE_PORT;
                unset SERVICE_TTL;
                unset SERVICE_PROTO;
                unset SERVICE_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CANCEL_REQ -> ${CANCEL_REQ}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep ${MESSAGE_DELAY}; reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                main;
                ;;
            ?([+-])+([0-9]))
                ## make sure its not 0 and its not > 65535
                if [ ${SERVICE_PORT} -eq 0 ] || [ ${SERVICE_PORT} -gt 65535 ]
                then
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service priority provided is invalid.";

                    awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    unset SERVICE_PORT;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                reset; clear; break;
                ;;
            *)
                ## data didnt pass validation
                ## show the error code and re-try
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service priority provided is invalid.";

                awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                unset SERVICE_WEIGHT;

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  provideRecordTTL
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#==============================================================================
function provideRecordTTL
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting zone-level SRV record information..";

    while true
    do
        reset; clear;

        awk -F "=" '/\<add.record.srv.ttl\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read SERVICE_TTL;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_TTL -> ${SERVICE_TTL}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${SERVICE_TTL} in
            [Xx]|[Qq]|[Cc])
                ## user chose to cancel
                reset; clear;

                ## unset variables
                unset CHANGE_CONTROL;
                unset METHOD_NAME;
                unset RESPONSE;
                unset ADD_EXISTING;
                unset RETURN_CODE;
                unset RET_CODE;
                unset ADD_EXISTING_RECORD;
                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;
                unset SITE_HOSTNAME;
                unset COMPLETE;
                unset BIZ_UNIT;
                unset SITE_PRJCODE;
                unset PRIMARY_INFO;
                unset SECONDARY_INFO;
                unset CANCEL_REQ;
                unset RECORD_TYPE;
                unset COMPLETE;
                unset CONTINUE;
                unset DATACENTER;
                unset SELECTED_DATACENTER;
                unset ALIAS;
                unset RECORD_TARGET;
                unset ANSWER;
                unset RECORD_PRIORITY;
                unset SERVICE_PRIORITY;
                unset SERVICE_WEIGHT;
                unset SERVICE_PORT;
                unset SERVICE_TTL;
                unset SERVICE_PROTO;
                unset SERVICE_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CANCEL_REQ -> ${CANCEL_REQ}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep ${MESSAGE_DELAY}; reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                main;
                ;;
            *)
                [ -z "${SERVICE_TTL}" ] && SERVICE_TTL=86400;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_TTL -> ${SERVICE_TTL}";

                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                isNaN ${SERVICE_TTL};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                reset; clear;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    ## data didnt pass validation
                    ## show the error code and re-try
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service TTL provided is invalid.";

                    awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    unset SERVICE_TTL;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                provideRecordPriority;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  provideServiceProtocol
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#==============================================================================
function provideServiceProtocol
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting zone-level SRV record information..";

    while true
    do
        reset; clear;

        ## ask for the service protocol
        ## this can be tcp or udp
        awk -F "=" '/\<add.record.srv.protocol\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<add.record.srv.valid.protocols\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        ## validate the provided protocol
        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                ## unset variables
                unset CHANGE_CONTROL;
                unset METHOD_NAME;
                unset RESPONSE;
                unset ADD_EXISTING;
                unset RETURN_CODE;
                unset RET_CODE;
                unset ADD_EXISTING_RECORD;
                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;
                unset SITE_HOSTNAME;
                unset COMPLETE;
                unset BIZ_UNIT;
                unset SITE_PRJCODE;
                unset PRIMARY_INFO;
                unset SECONDARY_INFO;
                unset CANCEL_REQ;
                unset RECORD_TYPE;
                unset COMPLETE;
                unset CONTINUE;
                unset DATACENTER;
                unset SELECTED_DATACENTER;
                unset ALIAS;
                unset RECORD_TARGET;
                unset ANSWER;
                unset RECORD_PRIORITY;
                unset SERVICE_PRIORITY;
                unset SERVICE_WEIGHT;
                unset SERVICE_PORT;
                unset SERVICE_TTL;
                unset SERVICE_PROTO;
                unset SERVICE_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CANCEL_REQ -> ${CANCEL_REQ}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep ${MESSAGE_DELAY}; reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                main;
                ;;
            *)
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/validators/validateRecordData.sh srvproto srv ${ANSWER};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    ## data didnt pass validation
                    ## show the error code and re-try
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service type provided is invalid.";

                    awk -F "=" '/\<record.type.disallowed\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%RECORD_TYPE%/${SERVICE_TYPE}/";

                    unset SERVICE_PROTO;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                SERVICE_PROTOCOL="_${ANSWER}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_PROTOCOL -> ${SERVICE_PROTOCOL}";

                reset; clear; break;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  provideServiceType
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#==============================================================================
function provideServiceType
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    while true
    do
        reset; clear;

        ## ask for the service protocol
        ## this can be tcp or udp
        awk -F "=" '/\<add.record.srv.type\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        ## validate the provided protocol
        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                ## unset variables
                unset CHANGE_CONTROL;
                unset METHOD_NAME;
                unset RESPONSE;
                unset ADD_EXISTING;
                unset RETURN_CODE;
                unset RET_CODE;
                unset ADD_EXISTING_RECORD;
                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;
                unset SITE_HOSTNAME;
                unset COMPLETE;
                unset BIZ_UNIT;
                unset SITE_PRJCODE;
                unset PRIMARY_INFO;
                unset SECONDARY_INFO;
                unset CANCEL_REQ;
                unset RECORD_TYPE;
                unset COMPLETE;
                unset CONTINUE;
                unset DATACENTER;
                unset SELECTED_DATACENTER;
                unset ALIAS;
                unset RECORD_TARGET;
                unset ANSWER;
                unset RECORD_PRIORITY;
                unset SERVICE_PRIORITY;
                unset SERVICE_WEIGHT;
                unset SERVICE_PORT;
                unset SERVICE_TTL;
                unset SERVICE_PROTO;
                unset SERVICE_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CANCEL_REQ -> ${CANCEL_REQ}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep ${MESSAGE_DELAY}; reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                main;
                ;;
            [a-zA-Z0-9_])
                SERVICE_TYPE="_${ANSWER}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_TYPE -> ${SERVICE_TYPE}";

                reset; clear; break;
                ;;
            *)
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service TTL provided is invalid.";

                awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                unset ANSWER;
                unset SERVICE_NAME;

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  reviewZone
#   DESCRIPTION:  Allows review and processing of the newly created zone file
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function reviewZone
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating operational zone..";

    awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

    ## temp unset
    typeset THIS_CNAME="${CNAME}";
    unset METHOD_NAME;
    unset CNAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    ## validate the input
    ${PLUGIN_LIB_DIRECTORY}/runZoneAddition.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -e;
    typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

    CNAME="${THIS_CNAME}";
    typeset METHOD_NAME="${THIS_CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        ## return code from run_addition to create the operational zone was non-zero
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Return code from run_addition nonzero -> ${RET_CODE}";

        unset CHANGE_CONTROL;
        unset METHOD_NAME;
        unset RESPONSE;
        unset ADD_EXISTING;
        unset RETURN_CODE;
        unset RET_CODE;
        unset ADD_EXISTING_RECORD;
        unset CCTLD_VALID;
        unset GTLD_VALID;
        unset REQUESTED_TLD;
        unset SITE_HOSTNAME;
        unset COMPLETE;
        unset BIZ_UNIT;
        unset SITE_PRJCODE;
        unset PRIMARY_INFO;
        unset SECONDARY_INFO;
        unset CANCEL_REQ;
        unset RECORD_TYPE;
        unset COMPLETE;
        unset CONTINUE;
        unset DATACENTER;
        unset SELECTED_DATACENTER;
        unset ALIAS;
        unset RECORD_TARGET;
        unset ANSWER;
        unset RECORD_PRIORITY;
        unset SERVICE_PRIORITY;
        unset SERVICE_WEIGHT;
        unset SERVICE_PORT;
        unset SERVICE_TTL;
        unset SERVICE_PROTO;
        unset SERVICE_TYPE;

        [ -z "${RET_CODE}" ] && awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        [ ! -z "${RET_CODE}" ] && awk -F "=" '/\<${RET_CODE}\>/{print \$2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        sleep ${MESSAGE_DELAY}; reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

        main;
    fi

    unset RET_CODE;
    unset RETURN_CODE;

    ## operational zone file got created. continue..
    while true
    do
        reset; clear;

        awk -F "=" '/\<add.review.zone\>/{print "\t" $2 "\n"}' | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%ZONE%/${SITE_HOSTNAME}/";

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${ANSWER} in
            [Yy][Ee][Ss]|[Yy])
                reset; clear;

                unset ANSWER;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printing zonefile content..";

                cat "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BIZ_UNIT}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${SITE_HOSTNAME}).${SITE_PRJCODE};

                awk -F "=" '/\<add.review.accurate.zone.pending.message\>/{print "\t" $2 "\n"}' | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%ZONE%/${SITE_HOSTNAME}/";

                read ANSWER;

                reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                while true
                do
                    case ${ANSWER} in
                        [Yy][Ee][Ss]|[Yy])
                            ## zone was created and is accurate. send to master
                            unset ANSWER;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling send_zone to stage the files";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            reset; clear;

                            awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            send_zone;
                            ;;
                        [Nn][Oo]|[Nn])
                            ## zone wasn't approved. clear it all and restart
                            [ -d "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BIZ_UNIT} ] && rm -rf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BIZ_UNIT};

                            awk -F "=" '/\<add.zone.inaccurate\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            unset CHANGE_CONTROL;
                            unset METHOD_NAME;
                            unset RESPONSE;
                            unset ADD_EXISTING;
                            unset RETURN_CODE;
                            unset RET_CODE;
                            unset ADD_EXISTING_RECORD;
                            unset CCTLD_VALID;
                            unset GTLD_VALID;
                            unset REQUESTED_TLD;
                            unset SITE_HOSTNAME;
                            unset COMPLETE;
                            unset BIZ_UNIT;
                            unset SITE_PRJCODE;
                            unset PRIMARY_INFO;
                            unset SECONDARY_INFO;
                            unset CANCEL_REQ;
                            unset RECORD_TYPE;
                            unset COMPLETE;
                            unset CONTINUE;
                            unset DATACENTER;
                            unset SELECTED_DATACENTER;
                            unset ALIAS;
                            unset RECORD_TARGET;
                            unset ANSWER;
                            unset RECORD_PRIORITY;
                            unset SERVICE_PRIORITY;
                            unset SERVICE_WEIGHT;
                            unset SERVICE_PORT;
                            unset SERVICE_TTL;
                            unset SERVICE_PROTO;
                            unset SERVICE_TYPE;

                            sleep ${MESSAGE_DELAY}; reset; clear;

                            awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            main;
                            ;;
                        *)
                            ## we need a yes or no here
                            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reponse ${ANSWER} invalid";

                            awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            unset RET_CODE;
                            unset RETURN_CODE;
                            unset ANSWER;

                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                            ;;
                    esac
                done
                ;;
            [Nn][Oo]|[Nn])
                ## user chose not to review the zone - send it up
                unset ANSWER;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling send_zone to stage the files";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                send_zone;
                ;;
            *)
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Invalid response received for request.";

                awk -F "=" '/\<system.selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                unset ANSWER;

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  sendZone
#   DESCRIPTION:  Executes commands to send the created zone information
#             to the configured master nameserver
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function sendZone
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command run_addition.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -x -e..";

    typeset THIS_CNAME="${CNAME}";
    unset METHOD_NAME;
    unset CNAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    ## validate the input
    ${PLUGIN_LIB_DIRECTORY}/runZoneAddition.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -x -e;
    typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

    CNAME="${THIS_CNAME}";
    typeset METHOD_NAME="${THIS_CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 || ${RET_CODE} -ne 66 || ${RET_CODE} -ne 52 ]]
    then
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone installation FAILED on node ${NAMED_MASTER}.";

        [ ! -z "${RET_CODE}" ] && awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        [ -z "${RET_CODE}" ] && awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        unset RET_CODE;
        unset RETURN_CODE;
        unset ANSWER;

        sleep "${MESSAGE_DELAY}"; reset; clear; return 1;
    fi

    ## 0 - all good
    ## 66 - validation failed
    ## 52 - reconfiguration failed
    if [ ${RET_CODE} -eq 52 ]
    then
        ## our zone installed just fine. server failed to reconfig with the new
        ## data, probably because of invalid syntax in a file.
        awk -F "=" '/\<possible.zone.syntax.error\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        sleep "${MESSAGE_DELAY}"; reset; clear;
    fi

    unset RET_CODE;
    unset RETURN_CODE;

    ## files were copied and decompressed successfully.
    ## we've also performed the necessary reloads and
    ## validated that the service exists on the master.
    ## at this point we should perform execution against
    ## our configured slaves (if any) and call it a day.
    if [ ${#DNS_SLAVES[*]} -ne 0 ]
    then
        ## make sure A is 0
        C=0;

        while [ ${C} -ne ${#DNS_SLAVES[*]} ]
        do
            reset; clear;

            awk -F "=" '/\<add.zone.send.slave\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

            ## send out to slave servers
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Sending zone to slave server ${DNS_SLAVES[${C}]}";

            typeset THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

            ## validate the input
            ${PLUGIN_LIB_DIRECTORY}/run_addition.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -s ${DNS_SLAVES[${C}]} -e;
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
            then
                ## something failed on the request. show the error code and continue.
                ## increment our error counter
                (( ERROR_COUNT += 1 ));

                [ -z "${RET_CODE}" ] && awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                [ ! -z "${RET_CODE}" ] && awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
            fi

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Changes successfully applied to ${DNS_SLAVES[${C}]}";

            (( C += 1 ));
        done

        ## make a zero again
        C=0;
    fi

    ## check the error count. if its not zero, something broke on something,
    ## so we leave our temp files in place for any manual operations that may
    ## be necessary.
    if [[ ${ERROR_COUNT} -ne 0 ]]
    then
        awk -F "=" '/\<slave.installation.possible.failure\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        sleep "${MESSAGE_DELAY}"; reset; clear;
    fi

    ## all processing successfully completed. we can remove our temp files
    [ -d "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT} ] && rm -rf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT};

    unset CHANGE_CONTROL;
    unset METHOD_NAME;
    unset RESPONSE;
    unset ADD_EXISTING;
    unset RETURN_CODE;
    unset RET_CODE;
    unset ADD_EXISTING_RECORD;
    unset CCTLD_VALID;
    unset GTLD_VALID;
    unset REQUESTED_TLD;
    unset SITE_HOSTNAME;
    unset COMPLETE;
    unset BIZ_UNIT;
    unset SITE_PRJCODE;
    unset PRIMARY_INFO;
    unset SECONDARY_INFO;
    unset CANCEL_REQ;
    unset RECORD_TYPE;
    unset COMPLETE;
    unset CONTINUE;
    unset DATACENTER;
    unset SELECTED_DATACENTER;
    unset ALIAS;
    unset RECORD_TARGET;
    unset ANSWER;
    unset RECORD_PRIORITY;
    unset SERVICE_PRIORITY;
    unset SERVICE_WEIGHT;
    unset SERVICE_PORT;
    unset SERVICE_TTL;
    unset SERVICE_PROTO;
    unset SERVICE_TYPE;

    while true
    do
        reset; clear;

        awk -F "=" '/\<add.zone.add.another\>/{print "\t" $2 "\n"}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${ANSWER} in
            [Yy][Ee][Ss]|[Yy])
                ## user has selected to add more stuff
                ## set it up.
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "User has elected to add further data. Reloading..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset ANSWER;

                sleep ${MESSAGE_DELAY}; reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                main;
                ;;
            [Nn][Oo]|[Nn])
                ## user does not wish to add more stuff
                ## redirect user back to main class
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "User has elected to add further data. Reloading..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset ANSWER;

                reset; clear;

                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                sleep ${MESSAGE_DELAY}; reset; clear; /usr/bin/env -i ksh ${MAIN_CLASS};

                return 0;
                ;;
            *)
                ## we need a yes/no answer here
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Response provided was blank. Cannot continue.";

                ## unset variables
                unset ANSWER;

                awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

reset; clear;

awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

main;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return 0;
