#!/usr/bin/ksh -x
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
#        AUTHOR:  Kevin Huntly <kmhuntly@gmail.com
#       COMPANY:  CaspersBox Web Services
#       VERSION:  1.0
#       CREATED:  ---
#      REVISION:  ---
#==============================================================================

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

## Application constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[[ -z "${PLUGIN_ROOT_DIR}" && -f ${SCRIPT_ROOT}/../lib/plugin.sh ]] && . ${SCRIPT_ROOT}/../lib/plugin.sh;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${PLUGIN_ROOT_DIR}" ] && print "Failed to locate configuration data. Cannot continue." && exit 1;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

THIS_CNAME="${CNAME}";
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

## validate the input
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

CNAME="${THIS_CNAME}";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
then
    print "Security configuration does not allow the requested action.";

    return ${RET_CODE};
fi

unset RET_CODE;

trap "print '$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.trap.signals\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%SIGNAL%/Ctrl-C/")'; sleep "${MESSAGE_DELAY}"; reset; clear; continue " 1 2 3

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

    if [ -z "${IS_DNS_RECORD_ADD_ENABLED}" ] || [ "${IS_DNS_RECORD_ADD_ENABLED}" != "${_TRUE}" ]
    then
        reset; clear;

        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS zone additions has not been enabled. Cannot continue.";

        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<request.not.authorized\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        ## terminate this thread and return control to main
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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
        unset THIS_CNAME;
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
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        sleep ${MESSAGE_DELAY}; reset; clear; exec ${MAIN_CLASS};

        return 0;
    fi

    while true
    do
        reset; clear;

        print "\n";
        print "\t\t+-------------------------------------------------------------------+";
        print "\t\t               WELCOME TO \E[0;31m $(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<plugin.application.title\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g') \033[0m";
        print "\t\t+-------------------------------------------------------------------+";
        print "\t\tSystem Type         : \E[0;36m ${SYSTEM_HOSTNAME} \033[0m";
        print "\t\tSystem Uptime       : \E[0;36m ${SYSTEM_UPTIME} \033[0m";
        print "\t\tUser                : \E[0;36m ${IUSER_AUDIT} \033[0m";
        print "\t\t+-------------------------------------------------------------------+";
        print "";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.enter.business.unit\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read BIZ_UNIT;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BIZ_UNIT -> ${BIZ_UNIT}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${BIZ_UNIT} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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
                unset THIS_CNAME;
                unset RET_CODE;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                sleep ${MESSAGE_DELAY}; reset; clear; exec ${MAIN_CLASS};

                return 0;
                ;;
            *)
                if [ -z "${BIZ_UNIT}" ]
                then
                    ## business unit provided was blank
                    unset CHANGE_CONTROL;
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

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                reset; clear; provideProjectCode;

                reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        if [ ! -z "${CANCEL_REQ}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

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

            reset; clear; main;
        fi

        reset; clear;

        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.enter.prjcode\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read SITE_PRJCODE;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_PRJCODE -> ${SITE_PRJCODE}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${SITE_PRJCODE} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset SITE_PRJCODE;

                sleep ${MESSAGE_DELAY}; reset; clear; main;
                ;;
            *)
                ## we cant really validate a project code. as long as it isnt blank
                ## we'll use it.
                if [ -z "${SITE_PRJCODE}" ]
                then
                    reset; clear;

                    unset SITE_PRJCODE;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No site project code was provided. Cannot continue.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                reset; clear;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_PRJCODE -> ${SITE_PRJCODE}";

                ## keep going
                reset; clear; provideSiteHostname;

                reset; clear; break;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        if [ ! -z "${CANCEL_REQ}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

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

            reset; clear; main;
        fi

        reset; clear;

        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.enter.hostname\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.enter.format.hostname\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.enter.format.allowed.tlds\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read SITE_HOSTNAME;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME -> ${SITE_HOSTNAME}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${SITE_HOSTNAME} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                ## unset variables
                unset CHANGE_CONTROL;
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

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            [Hh])
                ## we want to print out the available record type list
                print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<allowed.gtld.list\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                awk 'NR>17' ${ALLOWED_GTLD_LIST};

                print "\nsed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES}  | awk -F "=" '/\<allowed.cctld.list\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                awk 'NR>16' ${ALLOWED_GTLD_LIST};

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.continue.enter\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                read COMPLETE;

                reset; clear; continue;
                ;;
            *)
                ## we cant validate the hostname other than to say it has
                ## only two parts, the name and the tld. other than that,
                ## not much we can do here
                if [ -z "${SITE_HOSTNAME}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No site hostname was provided. Cannot continue.";

                    unset SITE_HOSTNAME;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                if [ $(echo ${SITE_HOSTNAME} | tr -dc "." | wc -c) -ne 1 ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_HOSTNAME} is not properly formatted.";

                    unset SITE_HOSTNAME;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep ${MESSAGE_DELAY}; reset; clear; continue;
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME -> ${SITE_HOSTNAME}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating TLD..";

                local REQUESTED_TLD=$(echo ${SITE_HOSTNAME} | cut -d "." -f 2);

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUESTED_TLD -> ${REQUESTED_TLD}";

                ## make sure we got a valid tld. we're only checking the gTLD's,
                ## for a list, see http://en.wikipedia.org/wiki/List_of_Internet_top-level_domains
                typeset -i GTLD_VALID=$(sed -e '/^ *#/d;s/#.*//' ${ALLOWED_GTLD_LIST} | awk "/\<${REQUESTED_TLD}\>/{print \$1}" | sed -e 's/^ *//g;s/ *$//g');
                typeset -i CCTLD_VALID=$(sed -e '/^ *#/d;s/#.*//' ${ALLOWED_CCTLD_LIST}| awk "/\<${REQUESTED_TLD}\>/{print \$1}" | sed -e 's/^ *//g;s/ *$//g');

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "GTLD_VALID -> ${GTLD_VALID}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CCTLD_VALID -> ${CCTLD_VALID}";

                if [ -z "${GTLD_VALID}" ] || [ "${GTLD_VALID}" = "" ] && [ -z "${CCTLD_VALID}" ] || [ "${CCTLD_VALID}" = "" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_HOSTNAME} is not properly formatted.";

                    unset CCTLD_VALID;
                    unset GTLD_VALID;
                    unset REQUESTED_TLD;
                    unset SITE_HOSTNAME;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep ${MESSAGE_DELAY}; reset; clear; continue;
                fi

                unset CCTLD_VALID;
                unset GTLD_VALID;
                unset REQUESTED_TLD;

                ## make sure there isnt already a zone with this hostname
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling retrieve_service to ensure that no records exist with ${SITE_HOSTNAME}..";

                local THIS_CNAME="${CNAME}";
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/retrieveServiceInfo.sh ${INTERNET_TYPE_IDENTIFIER} u,${SITE_HOSTNAME} chk-info;
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                local METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ ${RET_CODE} -eq 12 ] || [ ${RET_CODE} -eq 13 ]
                then
                    ## this zone doesnt yet exist, so we're safe to create.
                    ## now we need to get the associated change control. we
                    ## dont need it to create the zone other than for "AUDIT"
                    ## purposes
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_HOSTNAME} does NOT already exist in the DNS infrastructure";

                    unset RET_CODE;
                    unset RETURN_CODE;

                    reset; clear; provideChangeControl;

                    if [ ! -z "${CANCEL_REQ}" ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

                        reset; clear; main;
                    fi
                else
                    ## we already have a zone file with this hostname in it. we can't create a duplicate zone
                    ## it wont load, but we can add additional records to an existing zone
                    unset RET_CODE;
                    unset RETURN_CODE;

                    reset; clear;

                    while true
                    do
                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<add.zone.already.exists\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%HOSTNAME%/${SITE_HOSTNAME}/")";
                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.subdomains\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%ZONE_NAME%/${SITE_HOSTNAME}/")";
                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                        read ADD_EXISTING;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_EXISTING -> ${ADD_EXISTING}";

                        reset; clear;

                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                        case ${ADD_EXISTING} in
                            [Yy][Ee][Ss]|[Yy])
                                ## yes, we're adding a new entry to an existing zone. take user to the
                                ## zone update ui to request "INFO"
                                unset RESPONSE;
                                unset RET_CODE;
                                unset RETURN_CODE;

                                ADD_EXISTING_RECORD=${_TRUE};

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add additional records to existing zone confirmed.";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_EXISTING_RECORD->${ADD_EXISTING_RECORD}";

                                ## need to capture change order number here
                                reset; clear; provideChangeControl;

                                if [ ! -z "${CANCEL_REQ}" ]
                                then
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

                                    reset; clear; main;
                                fi
                                ;;
                            [Nn][Oo]|[Nn])
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_HOSTNAME} already exists in the DNS infrastructure.";

                                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<add.zone.already.exists.no.add\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%HOSTNAME%/${SITE_HOSTNAME}/")";

                                unset RET_CODE;
                                unset RETURN_CODE;
                                unset SITE_HOSTNAME;

                                sleep ${MESSAGE_DELAY}; reset; clear; break;
                                ;;
                            [Xx]|[Qq]|[Cc])
                                ## cancel request
                                reset; clear;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                ## terminate this thread and return control to main
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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

                                sleep ${MESSAGE_DELAY}; reset; clear; main;
                                ;;
                            *)
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${RESPONSE} is not valid.";

                                unset RESPONSE;

                                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                sleep ${MESSAGE_DELAY}; reset; clear; continue;
                                ;;
                        esac
                    done
                fi
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        if [ ! -z "${CANCEL_REQ}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

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

            reset; clear; main;
        fi

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting change information..";

        local THIS_CNAME="${CNAME}";
        unset METHOD_NAME;
        unset CNAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        reset; clear;

        ## validate the input
        . ${APP_ROOT}/${BIN_DIRECTORY}/obtainChangeControl.sh;

        reset; clear;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

        CNAME="${THIS_CNAME}";
        local METHOD_NAME="${THIS_CNAME}#${0}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_CONTROL -> ${CHANGE_CONTROL}";

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        if [[ ! -z "${CANCEL_REQ}" && "${CANCEL_REQ}" = "${_TRUE}" ]]
        then
            reset; clear;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested canceled";

            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

            ## terminate this thread and return control to main
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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

            sleep ${MESSAGE_DELAY}; reset; clear; main;
        fi

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating zone files..";

        ## unset methodname and cname
        local THIS_CNAME="${CNAME}";
        unset METHOD_NAME;
        unset CNAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        ## validate the input
        ${PLUGIN_LIB_DIRECTORY}/createNewZone.sh -b $(echo ${BIZ_UNIT} | tr "[a-z]" "[A-Z]") -p $(echo ${SITE_PRJCODE} | tr "[a-z]" "[A-Z]") -z ${SITE_HOSTNAME} -c ${CHANGE_CONTROL} -e;
        typeset -i RET_CODE=${?};

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

        CNAME="${THIS_CNAME}";
        local METHOD_NAME="${THIS_CNAME}#${0}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. Validating...";

        if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
        then
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone creation FAILED. RET_CODE -> ${RET_CODE}";

            [ -z "${RET_CODE}" ] && print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<99\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
            [ ! -z "${RET_CODE}" ] && print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/\<\<\${RET_CODE}\>/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')\n";

            unset RETURN_CODE;
            unset RET_CODE;

            sleep ${MESSAGE_DELAY}; reset; clear; continue;
        fi

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone creation complete. Proceeding to record addition";

        reset; clear; providePrimaryAddress;

        reset; clear; break;
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        if [ ! - z "${CANCEL_REQ}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

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

            reset; clear; main;
        fi

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.enter.ipaddr.primary\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%PRIMARY_DATACENTER%/${PRIMARY_DATACENTER}/")\n";
        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read PRIMARY_INFO;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRIMARY_INFO -> ${PRIMARY_INFO}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        case ${PRIMARY_INFO} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                unset PRIMARY_INFO;
                unset SECONDARY_INFO;

                ## clean up our tmp directories
                rm -rf ${PLUGIN_TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                CANCEL_REQ=${_TRUE};

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            *)
                THIS_CNAME="${CNAME}";
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/validators/validateRecordData.sh address ${PRIMARY_INFO}
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                local METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    unset PRIMARY_INFO;
                    unset RET_CODE;

                    reset; clear;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<ip.address.improperly.formatted\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ## run the ip addr through the validator
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating record target..";

                ## unset methodname and cname
                local THIS_CNAME="${CNAME}";
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/validators/validateRecordData.sh target a ${PRIMARY_INFO};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                local METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ ${RET_CODE} -eq 0 ] || [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                then
                    if [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                    then
                        ## we got a warning on validation - we arent failing, but we do want to inform
                        ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A warning occurred during record validation - failed to validate that record is active.";

                        [ -z "${RET_CODE} ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<99\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                        [ ! -z "${RET_CODE} ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/\<\<\${RET_CODE}\>/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')\n";

                        sleep "${MESSAGE_DELAY}";
                    fi

                    ## unset methodname and cname
                    local THIS_CNAME="${CNAME}";
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t A -a "${PRIMARY_INFO}" -d ${PRIMARY_DATACENTER} -r -e
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    local METHOD_NAME="${THIS_CNAME}#${0}"

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                    then
                        ## zone failed to update with primary ip addr
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone update to add primary IP FAILED. Return code -> ${RET_CODE}";

                        [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/\<\<99\>/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')\n";
                        [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/\<\<\${RET_CODE}\>/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')\n";

                        unset RET_CODE;
                        unset RETURN_CODE;
                        unset PRIMARY_INFO;

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone successfully updated";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/\<\<add.zone.update.success\>/{print $2}" | sed -e 's/^ *//g;s/ *$//g')\n";

                    sleep ${MESSAGE_DELAY}; reset; clear; provideSecondaryAddress;

                    reset; clear; break;
                else
                    ## failed to validate record.
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Primary IP address provided failed validation. Cannot continue.";

                    [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/\<\<99\>/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')\n";
                    [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/\<\<\${RET_CODE}\>/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')\n";

                    unset RET_CODE;
                    unset RETURN_CODE;
                    unset PRIMARY_INFO;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;
    unset PRIMARY_INFO;
    unset RETURN_CODE;
    unset RET_CODE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        if [ ! - z "${CANCEL_REQ}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

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

            reset; clear; main;
        fi

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.enter.ipaddr.secondary\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%SECONDARY_DATACENTER%/${SECONDARY_DATACENTER}/")\n";
        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read SECONDARY_INFO;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SECONDARY_INFO -> ${SECONDARY_INFO}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        case ${SECONDARY_INFO} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                unset SECONDARY_INFO;
                unset SECONDARY_INFO;

                ## clean up our tmp directories
                rm -rf ${PLUGIN_TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                CANCEL_REQ=${_TRUE};

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            *)
                THIS_CNAME="${CNAME}";
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/validators/validateRecordData.sh address ${SECONDARY_INFO}
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                local METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    unset SECONDARY_INFO;
                    unset RET_CODE;

                    reset; clear;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<ip.address.improperly.formatted\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ## run the ip addr through the validator
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating record target..";

                ## unset methodname and cname
                local THIS_CNAME="${CNAME}";
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/validators/validateRecordData.sh target a ${SECONDARY_INFO};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                local METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ ${RET_CODE} -eq 0 ] || [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                then
                    if [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                    then
                        ## we got a warning on validation - we arent failing, but we do want to inform
                        ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A warning occurred during record validation - failed to validate that record is active.";

                        [ -z "${RET_CODE} ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<99\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                        [ ! -z "${RET_CODE} ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/\<\<\${RET_CODE}\>/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')\n";

                        sleep "${MESSAGE_DELAY}";
                    fi

                    ## unset methodname and cname
                    local THIS_CNAME="${CNAME}";
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t A -a "${SECONDARY_INFO}" -d ${SECONDARY_DATACENTER} -r -e
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    local METHOD_NAME="${THIS_CNAME}#${0}"

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                    then
                        ## zone failed to update with primary ip addr
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone update to add primary IP FAILED. Return code -> ${RET_CODE}";

                        [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/\<\<99\>/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')\n";
                        [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/\<\<\${RET_CODE}\>/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')\n";

                        unset RET_CODE;
                        unset RETURN_CODE;
                        unset SECONDARY_INFO;

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone successfully updated";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" "/\<\<add.zone.update.success\>/{print $2}" | sed -e 's/^ *//g;s/ *$//g')\n";

                    ## do we want to add additional apex records ?
                    while true
                    do
                        unset ADD_COMPLETE;

                        reset; clear;

                        print "\t\t\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.request.additional.records\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                        read ANSWER;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

                        reset; clear;

                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                        case ${ANSWER} in
                            [Yy][Ee][Ss]|[Yy])
                                ## user wishes to add additional records to root of zone
                                ## make sure our variables are empty and break to restart
                                unset ANSWER;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add additional records confirmed. ADD_RECORDS has been set to true. Breaking..";

                                reset; clear; provideRecordType;

                                reset; clear; continue;
                                ;;
                            [Nn][Oo]|[Nn])
                                ## user does not wish to add additional records to root zone
                                ## ask if user wishes to add subdomains to zone
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add additional records declined. Request for subdomains..";
                                unset ANSWER;

                                while true
                                do
                                    reset; clear;

                                    print "\t\t\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.subdomains\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                    read ANSWER;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

                                    reset; clear;

                                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                    case ${ANSWER} in
                                        [Yy][Ee][Ss]|[Yy])
                                            ## user wishes to now add subdomain records.
                                            ## process via add_records
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add subdomain records confirmed. ADD_SUBDOMAINS has been set to true. Breaking..";
                                            unset ANSWER;

                                            ADD_SUBDOMAINS="${_TRUE}";

                                            reset; clear; provideRecordType;

                                            reset; clear; continue;
                                            ;;
                                        [Nn][Oo]|[Nn])
                                            ## user does not wish to add subdomain records
                                            ## this completes processing, send to reviewZone
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add subdomain records declined. ADD_SUBDOMAINS has been set to false. Breaking..";
                                            unset ANSWER;

                                            reset; clear; reviewZone;

                                            reset; clear; break;
                                            ;;
                                        *)
                                            ## no valid selection provided
                                            unset ANSWER;

                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not received. Please try again";

                                            print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            ;;
                                    esac
                                done
                                ;;
                            *)
                                ## no valid response provided
                                unset ANSWER;

                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not received. Please try again";

                                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                ;;
                        esac
                    done
                else
                    ## failed to validate record.
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Primary IP address provided failed validation. Cannot continue.";

                    [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/\<\<99\>/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')\n";
                    [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/\<\<\${RET_CODE}\>/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')\n";

                    unset RET_CODE;
                    unset RETURN_CODE;
                    unset SECONDARY_INFO;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;
    unset SECONDARY_INFO;
    unset RETURN_CODE;
    unset RET_CODE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        if [ ! - z "${CANCEL_REQ}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

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

            reset; clear; main;
        fi

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.enter.record.type\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%SECONDARY_DATACENTER%/${SECONDARY_DATACENTER}/")\n";
        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read RECORD_TYPE;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TYPE -> ${RECORD_TYPE}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        case ${RECORD_TYPE} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                unset RECORD_TYPE;

                ## clean up our tmp directories
                rm -rf ${PLUGIN_TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                CANCEL_REQ=${_TRUE};

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            [Hh])
                ## we want to print out the available record type list
                awk 'NR>16' ${ALLOWED_RECORD_LIST};

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.continue.enter\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
                read CONTINUE;

                unset CONTINUE;
                unset RECORD_TYPE;

                reset; clear; continue;
                ;;
            *)
                THIS_CNAME="${CNAME}";
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/validators/validateRecordData.sh type ${RECORD_TYPE};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                local METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    unset RECORD_TYPE;
                    unset RET_CODE;

                    reset; clear;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<record.type.disallowed\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ## get record target
                reset; clear; provideDataCenter;

                reset; clear; break;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;
    unset SECONDARY_INFO;
    unset RETURN_CODE;
    unset RET_CODE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        if [ ! - z "${CANCEL_REQ}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

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

            reset; clear; main;
        fi

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.provide.datacenter\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        for DATACENTER in ${DATACENTERS[@]}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DATACENTER -> ${DATACENTER}";

            print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.available.datacenters\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%DATACENTER%/${DATACENTER}/")\n";
        done

        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.add.to.both.datacenters\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read SELECTED_DATACENTER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTED_DATACENTER -> ${SELECTED_DATACENTER}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        case ${SELECTED_DATACENTER} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                unset RECORD_TYPE;

                ## clean up our tmp directories
                rm -rf ${PLUGIN_TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                CANCEL_REQ=${_TRUE};

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            [Bb][Oo][Tt][Hh])
                ## get record target
                unset SELECTED_DATACENTER;

                reset; clear; provideRecordTarget;
                ;;
            *)
                if [ -z "${SELECTED_DATACENTER}" ]
                then
                    unset SELECTED_DATACENTER;
                    unset RET_CODE;

                    reset; clear;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                THIS_CNAME="${CNAME}";
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                contains ${DATACENTERS[@]} ${SELECTED_DATACENTER};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                local METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    unset SELECTED_DATACENTER;
                    unset RET_CODE;

                    reset; clear;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<record.type.disallowed\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ## get record target
                reset; clear;

                [ -z "${ADD_SUBDOMAINS}" ] || [ "${ADD_SUBDOMAINS}" = "${_FALSE}" ] && provideRecordTarget;
                [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_TRUE}" ] && provideRecordAlias;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;
    unset SECONDARY_INFO;
    unset RETURN_CODE;
    unset RET_CODE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting zone-level SRV record information..";

    while true
    do
        if [ ! -z "${CANCEL_REQ}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

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

            reset; clear; main;
        fi

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service name..";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.alias\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        read ALIAS;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ALIAS -> ${ALIAS}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${ALIAS} in
            [Xx]|[Qq]|[Cc])
                ## user chose to cancel
                unset SERVICE_TYPE;
                unset SERVICE_PROTO;
                unset SERVICE_NAME;
                unset RECORD_TYPE;

                CANCEL_REQ=${_TRUE};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";

                reset; clear;

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            *)
                if [ -z "${ALIAS}" ]
                then
                    ## no service name was provided, this is technically allowed,
                    ## but we're going to dis-allow it because we want to know for
                    ## sure what to add.
                    unset ALIAS;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No alias was provided. Cannot continue.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep ${MESSAGE_DELAY}; reset; clear; continue;
                fi

                reset; clear; provideRecordTarget;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        if [ ! - z "${CANCEL_REQ}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

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

            reset; clear; main;
        fi

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.target\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%RECORD_TYPE%/${RECORD_TYPE}/")\n";
        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read RECORD_TARGET;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TARGET -> ${RECORD_TARGET}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        case ${RECORD_TARGET} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                unset RECORD_TYPE;

                ## clean up our tmp directories
                rm -rf ${PLUGIN_TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                CANCEL_REQ=${_TRUE};

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            [a-zA-Z0-9.-])
                THIS_CNAME="${CNAME}";
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/validators/validateRecordData.sh target ${RECORD_TYPE} ${RECORD_TARGET};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                local METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    unset RECORD_TARGET;
                    unset RET_CODE;

                    reset; clear;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

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

                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                        local THIS_CNAME="${CNAME}";
                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                        [ -z "${SELECTED_DATACENTER}" ] && [ -z "${ADD_SUBDOMAINS}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -r -e;
                        [ -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_FALSE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -r -e;
                        [ -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_TRUE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -s -e;
                        [ ! -z "${SELECTED_DATACENTER}" ] && [ -z "${ADD_SUBDOMAINS}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -d ${SELECTED_DATACENTER} -r -e;
                        [ ! -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_FALSE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -d ${SELECTED_DATACENTER} -r -e;
                        [ ! -z "${SELECTED_DATACENTER}" ] && [ ! -z "${ADD_SUBDOMAINS}" ] || [ "${ADD_SUBDOMAINS}" = "${_TRUE}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -d ${SELECTED_DATACENTER} -s -e;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        local METHOD_NAME="${THIS_CNAME}#${0}"

                        reset; clear;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                        ;;
                    [Cc][Nn][Aa][Mm][Ee]|[Pp][Tt][Rr]|[Tt][Xx][Tt])
                        if [ ! -z "${ADD_SUBDOMAINS}" ] && [ "${ADD_SUBDOMAINS}" = "${_TRUE}" ]
                        then
                            ## selected record type cannot be added to the apex
                            unset RECORD_TARGET;

                            print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        fi

                        ## we have enough to add the record, so do it
                        ## unset methodname and cname
                        reset; clear;

                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                        local THIS_CNAME="${CNAME}";
                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                        [ -z "${SELECTED_DATACENTER}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -s -e;
                        [ ! -z "${SELECTED_DATACENTER}" ] && ${PLUGIN_LIB_DIRECTORY}/addZoneData.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -t ${RECORD_TYPE} -a "${RECORD_TARGET}" -d ${SELECTED_DATACENTER} -s -e;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        local METHOD_NAME="${THIS_CNAME}#${0}"

                        reset; clear;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                        ;;
                    [Mm][Xx])
                        ## need a priority here..
                        reset; clear; provideRecordPriority;

                        if [ -z "${RECORD_PRIORITY}" ]
                        then
                            ## no priority was provided
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No record priority was provided. Redirecting...";

                            print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<mx.priority.not.numeric\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                            sleep ${MESSAGE_DELAY}; reset; clear; provideRecordPriority;
                        fi

                        reset; clear;

                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                        local THIS_CNAME="${CNAME}";
                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
                        local METHOD_NAME="${THIS_CNAME}#${0}"

                        reset; clear;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                        then
                            echo "moo";
                        fi

                        ## add more ?
                        ## add sub ?
                        ;;
                    [Ss][Rr][Vv])
                        ## get a bunch of information here ...
                        reset; clear; provideServiceType;

                        if [ -z "${SERVICE_TYPE}" ]
                        then
                            ## no priority was provided
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No record type was provided. Redirecting...";

                            print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                            sleep ${MESSAGE_DELAY}; reset; clear; provideServiceType;
                        fi

                        reset; clear; provideServiceProtocol;

                        if [ -z "${SERVICE_PROTO}" ]
                        then
                            ## no priority was provided
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No record protocol was provided. Redirecting...";

                            print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                            sleep ${MESSAGE_DELAY}; reset; clear; provideServiceProtocol;
                        fi

                        reset; clear; provideRecordTTL;

                        if [ -z "${SERVICE_TTL}" ]
                        then
                            ## no priority was provided
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No record TTL was provided. Redirecting...";

                            print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                            sleep ${MESSAGE_DELAY}; reset; clear; provideRecordTTL;
                        fi

                        reset; clear; provideRecordPriority;

                        if [ -z "${SERVICE_PRIORITY}" ]
                        then
                            ## no priority was provided
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No record priority was provided. Redirecting...";

                            print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                            sleep ${MESSAGE_DELAY}; reset; clear; provideRecordPriority;
                        fi

                        reset; clear; provideRecordPriority;

                        if [ -z "${SERVICE_PRIORITY}" ]
                        then
                            ## no priority was provided
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No record weight was provided. Redirecting...";

                            print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                            sleep ${MESSAGE_DELAY}; reset; clear; provideRecordPriority;
                        fi

                        reset; clear; provideServicePort;

                        if [ -z "${SERVICE_WEIGHT}" ]
                        then
                            ## no priority was provided
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No record weight was provided. Redirecting...";

                            print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                            sleep ${MESSAGE_DELAY}; reset; clear; provideServicePort;
                        fi

                        reset; clear;

                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                        local THIS_CNAME="${CNAME}";
                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
                        local METHOD_NAME="${THIS_CNAME}#${0}"

                        reset; clear;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                        then
                            echo "moo";
                        fi

                        ## add more ?
                        ## add sub ?
                        ;;
                    *)
                        ## record not supported at the apex
                        unset RECORD_TARGET;

                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        ;;
                esac
                ;;
            *)
                unset RECORD_TARGET;

                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;
    unset SECONDARY_INFO;
    unset RETURN_CODE;
    unset RET_CODE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        if [ ! - z "${CANCEL_REQ}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

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

            reset; clear; main;
        fi

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.priority\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%RECORD_TYPE%/${RECORD_TYPE}/")\n";
        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                unset RECORD_TYPE;

                ## clean up our tmp directories
                rm -rf ${PLUGIN_TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                CANCEL_REQ=${_TRUE};

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
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

                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;
    unset SECONDARY_INFO;
    unset RETURN_CODE;
    unset RET_CODE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting zone-level SRV record information..";

    while true
    do
        if [ ! -z "${CANCEL_REQ}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

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

            reset; clear; main;
        fi

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service port..";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.srv.port\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        read SERVICE_PORT;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_PORT -> ${SERVICE_PORT}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${SERVICE_PORT} in
            [Xx]|[Qq]|[Cc])
                ## user chose to cancel
                unset SERVICE_PORT;

                CANCEL_REQ=${_TRUE};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";

                reset; clear;

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            ?([+-])+([0-9]))
                ## make sure its not 0 and its not > 65535
                if [ ${SERVICE_PORT} -eq 0 ] || [ ${SERVICE_PORT} -gt 65535 ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service priority provided is invalid.";

                    print "\t$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    unset SERVICE_PORT;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                reset; clear; break;
                ;;
            *)
                ## data didnt pass validation
                ## show the "ERROR" code and re-try
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service priority provided is invalid.";

                print "\t$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                unset SERVICE_WEIGHT;

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting zone-level SRV record information..";

    while true
    do
        if [ ! -z "${CANCEL_REQ}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

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

            reset; clear; main;
        fi

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service TTL..";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.srv.ttl\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        read SERVICE_TTL;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_TTL -> ${SERVICE_TTL}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${SERVICE_TTL} in
            [Xx]|[Qq]|[Cc])
                ## user chose to cancel
                unset SERVICE_TTL;

                CANCEL_REQ=${_TRUE};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";

                reset; clear;

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            *)
                [ -z "${SERVICE_TTL}" ] && SERVICE_TTL=86400;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_TTL -> ${SERVICE_TTL}";

                local THIS_CNAME="${CNAME}";
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
                local METHOD_NAME="${THIS_CNAME}#${0}"

                reset; clear;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    ## data didnt pass validation
                    ## show the "ERROR" code and re-try
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service TTL provided is invalid.";

                    print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    unset SERVICE_TTL;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                reset; clear; provideRecordPriority;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting zone-level SRV record information..";

    while true
    do
        if [ ! -z "${CANCEL_REQ}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

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

            reset; clear; main;
        fi

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service protocol..";

        reset; clear;

        ## ask for the service protocol
        ## this can be tcp or udp
        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.srv.protocol\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.srv.valid.protocols\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        ## validate the provided protocol
        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                ## user chose to cancel
                unset SERVICE_TYPE;
                unset SERVICE_PROTO;
                unset RECORD_TYPE;

                CANCEL_REQ=${_TRUE};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";

                reset; clear;

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            *)
                THIS_CNAME="${CNAME}";
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/validators/validateRecordData.sh srvproto srv ${ANSWER};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                local METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    ## data didnt pass validation
                    ## show the "ERROR" code and re-try
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service type provided is invalid.";

                    print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<record.type.disallowed\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%RECORD_TYPE%/${SERVICE_TYPE}/")";

                    unset SERVICE_PROTO;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                SERVICE_PROTOCOL="_${ANSWER}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_PROTOCOL -> ${SERVICE_PROTOCOL}";

                reset; clear; break;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        if [ ! -z "${CANCEL_REQ}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

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

            reset; clear; main;
        fi

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service protocol..";

        reset; clear;

        ## ask for the service protocol
        ## this can be tcp or udp
        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.srv.type\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        ## validate the provided protocol
        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                ## user chose to cancel
                unset SERVICE_TYPE;
                unset SERVICE_PROTO;
                unset RECORD_TYPE;

                CANCEL_REQ=${_TRUE};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";

                reset; clear;

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            [a-zA-Z0-9_])
                SERVICE_TYPE="_${ANSWER}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_TYPE -> ${SERVICE_TYPE}";

                reset; clear; break;
                ;;
            *)
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service TTL provided is invalid.";

                print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                unset ANSWER;
                unset SERVICE_NAME;

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating operational zone..";

    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" "/\<\<system.pending.message\>/{print $2}" | sed -e 's/^ *//g;s/ *$//g')";

    ## temp unset
    local THIS_CNAME="${CNAME}";
    unset METHOD_NAME;
    unset CNAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    ## validate the input
    ${PLUGIN_LIB_DIRECTORY}/runZoneAddition.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -e;
    typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

    CNAME="${THIS_CNAME}";
    local METHOD_NAME="${THIS_CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        ## return code from run_addition to create the operational zone was non-zero
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Return code from run_addition nonzero -> ${RET_CODE}";

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

        [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/\<99\>/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";
        [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/\<\<${RET_CODE}\>/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        sleep ${MESSAGE_DELAY}; reset; clear; main;
    fi

    unset RET_CODE;
    unset RETURN_CODE;

    ## operational zone file got created. continue..
    while true
    do
        reset; clear;

        print "\t\t\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.review.zone\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%ZONE%/${SITE_HOSTNAME}/")\n";

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${ANSWER} in
            [Yy][Ee][Ss]|[Yy])
                reset; clear;

                unset ANSWER;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printing zonefile content..";

                cat ${PLUGIN_TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT}/${NAMED_ZONE_PREFIX}.$(echo ${SITE_HOSTNAME} | cut -d "." -f 1).${SITE_PRJCODE};

                print "\n$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.review.accurate.zone.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%ZONE%/${SITE_HOSTNAME}/")";

                read ANSWER;

                reset; clear;

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                while true
                do
                    case ${ANSWER} in
                        [Yy][Ee][Ss]|[Yy])
                            ## zone was created and is accurate. send to master
                            unset ANSWER;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling send_zone to stage the files";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            reset; clear; send_zone;
                            ;;
                        [Nn][Oo]|[Nn])
                            ## zone wasn't approved. clear it all and restart
                            [ -d ${PLUGIN_TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT} ] && rm -rf ${PLUGIN_TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT};

                            print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.zone.inaccurate\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

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

                            sleep ${MESSAGE_DELAY}; reset; clear; main;
                            ;;
                        *)
                            ## we need a yes or no here
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reponse ${ANSWER} invalid";

                            print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

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

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling send_zone to stage the files";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                reset; clear; send_zone;
                ;;
            *)
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Invalid response received for request.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.selection.invalid "${ERROR_MESSAGES}" | awk -F "=" '/\<remote_app_root\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                unset ANSWER;

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command run_addition.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -x -e..";

    local THIS_CNAME="${CNAME}";
    unset METHOD_NAME;
    unset CNAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    ## validate the input
    ${PLUGIN_LIB_DIRECTORY}/runZoneAddition.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -c ${CHANGE_CONTROL} -x -e;
    typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

    CNAME="${THIS_CNAME}";
    local METHOD_NAME="${THIS_CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 || ${RET_CODE} -ne 66 || ${RET_CODE} -ne 52 ]]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone installation FAILED on node ${NAMED_MASTER}.";

        [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/\<${RET_CODE}\>/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";
        [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<99\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

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
        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<possible.zone.syntax.error\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        sleep "${MESSAGE_DELAY}"; reset; clear;
    fi

    unset RET_CODE;
    unset RETURN_CODE;

    ## files were copied and decompressed successfully.
    ## we've also performed the necessary reloads and
    ## validated that the service exists on the master.
    ## at this point we should perform execution against
    ## our configured slaves (if any) and call it a day.
    if [ ${#DNS_SLAVES[@]} -ne 0 ]
    then
        ## make sure A is 0
        C=0;

        while [ ${C} -ne ${#DNS_SLAVES[@]} ]
        do
            reset; clear;

            print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.zone.send.slave\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

            ## send out to slave servers
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Sending zone to slave server ${DNS_SLAVES[${C}]}";

            local THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

            ## validate the input
            ${PLUGIN_LIB_DIRECTORY}/run_addition.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -s ${DNS_SLAVES[${C}]} -e;
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

            CNAME="${THIS_CNAME}";
            local METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]]
            then
                ## something failed on the request. show the "ERROR" code and continue.
                ## increment our "ERROR" counter
                (( ERROR_COUNT += 1 ));

                unset RET_CODE;

                print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%ZONE%/${SITE_HOSTNAME}/" -e "s/%SERVER%/${DNS_SLAVES[${C}]}/")";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
            fi

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Changes successfully applied to ${DNS_SLAVES[${C}]}";

            (( C += 1 ));
        done

        ## make a zero again
        C=0;
    fi

    ## check the "ERROR" count. if its not zero, something broke on something,
    ## so we leave our temp files in place for any manual operations that may
    ## be necessary.
    if [[ ${ERROR_COUNT} -ne 0 ]]
    then
        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<slave.installation.possible.failure\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        sleep "${MESSAGE_DELAY}"; reset; clear;
    fi

    ## all processing successfully completed. we can remove our temp files
    [ -d ${PLUGIN_TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT} ] && rm -rf ${PLUGIN_TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT};

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

        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.zone.add.another\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        case ${ANSWER} in
            [Yy][Ee][Ss]|[Yy])
                ## user has selected to add more stuff
                ## set it up.
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "User has elected to add further data. Reloading..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset ANSWER;

                reset; clear; main;
                ;;
            [Nn][Oo]|[Nn])
                ## user does not wish to add more stuff
                ## redirect user back to main class
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "User has elected to add further data. Reloading..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset ANSWER;

                reset; clear;

                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                exec ${MAIN_CLASS};

                exit 0;
                ;;
            *)
                ## we need a yes/no answer here
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Response provided was blank. Cannot continue.";

                ## unset variables
                unset ANSWER;

                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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
unset THIS_CNAME;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return 0;
