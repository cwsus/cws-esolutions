#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  serviceControlUI.sh
#         USAGE:  ./restart_named_ui.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Provides a user interface to control named services. Includes
#                 ability to process service restarts as well as role swaps.
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
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";
typeset -i OPTIND=0;
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
[ ! -z "${ENABLE_SECURITY}" ] && [ "${ENABLE_SECURITY}" = "${_TRUE}" ] && ${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

CNAME="${THIS_CNAME}";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ ! -z "${ENABLE_SECURITY}" ] && [ "${ENABLE_SECURITY}" = "${_TRUE}" ] && [ ${RET_CODE} -ne 0 ]
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

    if [ ! -z "${IS_DNS_SVC_MGMT_ENABLED}" ] && [ "${IS_DNS_SVC_MGMT_ENABLED}" = "${_FALSE}" ]
    then
        reset; clear;

        $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service management has not been enabled. Cannot continue.");

        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<request.not.authorized\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

        exec ${MAIN_CLASS};

        exit 0;
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
        print "\t\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.available.options\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.control.rndc.generate.keys\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.control.dnssec.generate.keys\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.control.dhcpd.generate.keys\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.control.tsig.generate.keys\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read SELECTION;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION -> ${SELECTION}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${SELECTION} in
            1)
                ## RNDC
                if [[ ! -z "${IS_RNDC_MGMT_ENABLED}" || "${IS_RNDC_MGMT_ENABLED}" != "${_TRUE}" ]]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC Key management has not been enabled. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<request.not.authorized\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ## service request is role swap. process accordingly
                unset SELECTION;

                local THIS_CNAME=${CNAME};
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ${APP_ROOT}/${BIN_DIRECTORY}/obtainChangeControl.sh;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                local METHOD_NAME="${THIS_CNAME}#${0}";

                if [ -z ${CHANGE_CONTROL} ]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change request has been provided. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<change.request.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                KEYTYPE="RNDC";

                reset; clear; break;
                ;;
            2)
                ## DNSSEC
                unset SELECTION;

                if [[ ! -z "${IS_DNSSEC_MGMT_ENABLED}" || "${IS_DNSSEC_MGMT_ENABLED}" != "${_TRUE}" ]]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNSSEC Key management has not been enabled. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<request.not.authorized\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                local THIS_CNAME=${CNAME};
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ${APP_ROOT}/${BIN_DIRECTORY}/obtainChangeControl.sh;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                local METHOD_NAME="${THIS_CNAME}#${0}";

                if [ -z ${CHANGE_CONTROL} ]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change request has been provided. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<change.request.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                KEYTYPE="DNSSEC";

                reset; clear; break;
                ;;
            3)
                ## DHCPD
                unset SELECTION;

                if [[ ! -z "${IS_DHCPD_MGMT_ENABLED}" || "${IS_DHCPD_MGMT_ENABLED}" != "${_TRUE}" ]]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DHCPD Key management has not been enabled. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<request.not.authorized\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                local THIS_CNAME=${CNAME};
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ${APP_ROOT}/${BIN_DIRECTORY}/obtainChangeControl.sh;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                local METHOD_NAME="${THIS_CNAME}#${0}";

                if [ -z ${CHANGE_CONTROL} ]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change request has been provided. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<change.request.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                KEYTYPE="DHCPD";

                reset; clear; break;
                ;;
            4)
                ## TSIG
                unset SELECTION;

                if [[ ! -z "${IS_TSIG_MGMT_ENABLED}" || "${IS_TSIG_MGMT_ENABLED}" != "${_TRUE}" ]]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TSIG Key management has not been enabled. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<request.not.authorized\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                local THIS_CNAME=${CNAME};
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ${APP_ROOT}/${BIN_DIRECTORY}/obtainChangeControl.sh;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                local METHOD_NAME="${THIS_CNAME}#${0}";

                if [ -z ${CHANGE_CONTROL} ]
                then
                    unset SELECTION;

                    reset; clear;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change request has been provided. Cannot continue.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<change.request.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                KEYTYPE="TSIG";

                reset; clear; break;
                ;;
            [Xx]|[Qq]|[Cc])
                reset; clear;

                unset SELECTION;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Restart request canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                exec ${MAIN_CLASS};

                exit 0;
                ;;
            *)
                ## no valid option was provided. fail.
                unset SELECTION;

                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    serviceKeyManagement;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  serviceKeyManagement
#   DESCRIPTION:  Provide application function usage information
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function serviceKeyManagement
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Confirming request for key renewal..";

        print "\t\t\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<confirm.request\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read CONFIRM;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONFIRM -> ${CONFIRM}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        case ${CONFIRM} in
            [Yy][Ee][Ss]|[Yy])
                ## unset confirmation
                unset CONFIRM;
                reset; clear;

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## begin processing of key renewal
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing key renewal for ${SELECTION}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runKeyGeneration.sh -g ${SELECTION},${CHANGE_CONTROL},${IUSER_AUDIT} -e";

                local THIS_CNAME=${CNAME};
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                [[ ! -z "${KEYTYPE}" && "${KEYTYPE}" = "RNDC" ]] && ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runKeyGeneration.sh -r -d -g ${SELECTION},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                [[ ! -z "${KEYTYPE}" && "${KEYTYPE}" = "TSIG" ]] && ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runKeyGeneration.sh -r -d -g ${SELECTION},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                [[ ! -z "${KEYTYPE}" && "${KEYTYPE}" = "DNSSEC" ]] && ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runKeyGeneration.sh -r -d -g ${SELECTION},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                [[ ! -z "${KEYTYPE}" && "${KEYTYPE}" = "DHCPD" ]] && ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runKeyGeneration.sh -r -d -g ${SELECTION},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                local METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [[ -z "${RET_CODE}" || ! -z "${RET_CODE}" && ${RET_CODE} -ne 0 ]]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No return code was received from run_rndc_request. Please review "ERROR" logs.";

                    [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<99\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
                    [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";

                    unset RESPONSE;
                    unset SELECTION;
                    unset CHANGE_CONTROL;
                    unset CONFIRM;
                    unset RET_CODE;

                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                fi

                unset RET_CODE;

                ## process completed successfully and all nodes were updated
                ## "AUDIT" log it
                ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Key Management: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Keys: ${SELECTION} - Change Request: ${CHANGE_CONTROL} - Keys successfully renewed";

                ## we've finished our processing, and keys have been renewed
                ## what should we do now ?
                while true
                do
                    reset; clear;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.key.management.keys.renewed\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%KEYTYPE%/${SELECTION}/")\n";
                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.key.management.keys.renew.more\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                    read RESPONSE;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                    case ${RESPONSE} in
                        [Yy][Ee][Ss]|[Yy])
                            ## user has elected to perform further failovers. restart the process
                            unset RESPONSE;
                            unset SELECTION;
                            unset CHANGE_CONTROL;
                            unset CONFIRM;                                                

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Transferring control back to main..";

                            sleep "${MESSAGE_DELAY}"; reset; clear; main;
                            ;;
                        *)
                            ## user does not wish to process further failovers. let's exit out and open up the main class.
                            unset RESPONSE;
                            unset SELECTION;
                            unset CHANGE_CONTROL;
                            unset CONFIRM;

                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                            sleep "${MESSAGE_DELAY}"; reset; clear; exec ${MAIN_CLASS};

                            exit 0;
                            ;;
                    esac
                done
                ;;
            [Nn][Oo]|[Nn])
                unset CONFIRM;
                unset SELECTION;
                unset CHANGE_CONTROL;

                ## user opted to cancel
                ## we leave the in-use flag in place
                ## because we aren't starting over
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Key management request canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
            *)
                ## user did not provide a yes/no answer
                unset CONFIRM;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection provided is invalid";

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
unset OPTIND;
unset THIS_CNAME;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return ${RETURN_CODE};
