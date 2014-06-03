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

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

unset METHOD_NAME;
unset CNAME;

## check security
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Security configuration does not allow the requested action." && exit ${RET_CODE} || unset RET_CODE;

trap "print '$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.trap.signals/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g' -e "s/%SIGNAL%/Ctrl-C/")'; sleep "${MESSAGE_DELAY}"; reset; clear; continue " 1 2 3

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
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    if [ ! -z "${IS_DNS_SVC_MGMT_ENABLED}" ] && [ "${IS_DNS_SVC_MGMT_ENABLED}" = "${_FALSE}" ]
    then
        reset; clear;

        $(${LOGGER} "ERROR" $METHOD_NAME ${CNAME} ${LINENO} "Service management has not been enabled. Cannot continue.");

        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/request.not.authorized/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')";

        exec ${PLUGIN_ROOT_DIR}/${MAIN_CLASS};

        exit 0;
    fi

    while true
    do
        reset; clear;

        print "\n";
        print "\t\t+-------------------------------------------------------------------+";
        print "\t\t               WELCOME TO \E[0;31m $(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/plugin.application.title/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g') \033[0m";
        print "\t\t+-------------------------------------------------------------------+";
        print "";
        print "\t\tSystem Type         : \E[0;36m ${SYSTEM_HOSTNAME} \033[0m";
        print "\t\tSystem Uptime       : \E[0;36m ${SYSTEM_UPTIME} \033[0m";
        print "\t\tUser                : \E[0;36m ${IUSER_AUDIT} \033[0m";
        print "";
        print "\t\t+-------------------------------------------------------------------+";
        print "";
        print "\t\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.available.options/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/service.control.rndc.generate.keys/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/service.control.dnssec.generate.keys/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/service.control.dhcpd.generate.keys/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/service.control.tsig.generate.keys/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')";
        print "\t\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.option.cancel/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

        read SELECTION;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION -> ${SELECTION}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

        case ${SELECTION} in
            1)
                ## RNDC
                if [[ ! -z "${IS_RNDC_MGMT_ENABLED}" || "${IS_RNDC_MGMT_ENABLED}" != "${_TRUE}" ]]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" $METHOD_NAME ${CNAME} ${LINENO} "RNDC Key management has not been enabled. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/request.not.authorized/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ## service request is role swap. process accordingly
                unset SELECTION;

                ${APP_ROOT}/${LIB_DIRECTORY}/validators/validateChangeRequest.sh;

                if [ -z ${CHANGE_CONTROL} ]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" $METHOD_NAME ${CNAME} ${LINENO} "No change request has been provided. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/change.request.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

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

                    $(${LOGGER} "ERROR" $METHOD_NAME ${CNAME} ${LINENO} "DNSSEC Key management has not been enabled. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/request.not.authorized/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ${APP_ROOT}/${LIB_DIRECTORY}/validators/validateChangeRequest.sh;

                if [ -z ${CHANGE_CONTROL} ]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" $METHOD_NAME ${CNAME} ${LINENO} "No change request has been provided. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/change.request.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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

                    $(${LOGGER} "ERROR" $METHOD_NAME ${CNAME} ${LINENO} "DHCPD Key management has not been enabled. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/request.not.authorized/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ${APP_ROOT}/${LIB_DIRECTORY}/validators/validateChangeRequest.sh;

                if [ -z ${CHANGE_CONTROL} ]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" $METHOD_NAME ${CNAME} ${LINENO} "No change request has been provided. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/change.request.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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

                    $(${LOGGER} "ERROR" $METHOD_NAME ${CNAME} ${LINENO} "TSIG Key management has not been enabled. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/request.not.authorized/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                ${APP_ROOT}/${LIB_DIRECTORY}/validators/validateChangeRequest.sh;

                if [ -z ${CHANGE_CONTROL} ]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" $METHOD_NAME ${CNAME} ${LINENO} "No change request has been provided. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/change.request.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                KEYTYPE="TSIG";

                reset; clear; break;
                ;;
            [Xx]|[Qq]|[Cc])
                reset; clear;

                unset SELECTION;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Restart request canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.request.canceled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                ## terminate this thread and return control to main
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                exec ${PLUGIN_ROOT_DIR}/${MAIN_CLASS};

                exit 0;
                ;;
            *)
                ## no valid option was provided. fail.
                unset SELECTION;

                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/selection.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

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
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Confirming request for key renewal..";

        print "\t\t\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/confirm.request/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

        read CONFIRM;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONFIRM -> ${CONFIRM}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

        case ${CONFIRM} in
            [Yy][Ee][Ss]|[Yy])
                ## unset confirmation
                unset CONFIRM;
                reset; clear;

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                ## begin processing of key renewal
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing key renewal for ${SELECTION}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runKeyGeneration.sh -g ${SELECTION},${CHANGE_CONTROL},${IUSER_AUDIT} -e";

                ## temp unset
                unset METHOD_NAME;
                unset CNAME;

                ## execute

                [[ ! -z "${KEYTYPE}" && "${KEYTYPE}" = "RNDC" ]] && ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runKeyGeneration.sh -r -d -g ${SELECTION},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                [[ ! -z "${KEYTYPE}" && "${KEYTYPE}" = "TSIG" ]] && ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runKeyGeneration.sh -r -d -g ${SELECTION},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                [[ ! -z "${KEYTYPE}" && "${KEYTYPE}" = "DNSSEC" ]] && ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runKeyGeneration.sh -r -d -g ${SELECTION},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                [[ ! -z "${KEYTYPE}" && "${KEYTYPE}" = "DHCPD" ]] && ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runKeyGeneration.sh -r -d -g ${SELECTION},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                . 

                ## capture the return code
                RET_CODE=${?};

                ## put cname and methodname back to this class
                CNAME="$(basename "${0}")";
                local METHOD_NAME="${CNAME}#${0}";

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [[ -z "${RET_CODE}" || ! -z "${RET_CODE}" && ${RET_CODE} -ne 0 ]]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No return code was received from run_rndc_request. Please review error logs.";

                    [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/99/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')";
                    [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g' -e 's/ *$//g')";

                    unset RESPONSE;
                    unset SELECTION;
                    unset CHANGE_CONTROL;
                    unset CONFIRM;
                    unset RET_CODE;

                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                fi

                unset RET_CODE;

                ## process completed successfully and all nodes were updated
                ## audit log it
                ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Key Management: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Keys: ${SELECTION} - Change Request: ${CHANGE_CONTROL} - Keys successfully renewed";

                ## we've finished our processing, and keys have been renewed
                ## what should we do now ?
                while true
                do
                    reset; clear;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/service.key.management.keys.renewed/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g' -e "s/%KEYTYPE%/${SELECTION}/")\n";
                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/service.key.management.keys.renew.more/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                    read RESPONSE;

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                    case ${RESPONSE} in
                        [Yy][Ee][Ss]|[Yy])
                            ## user has elected to perform further failovers. restart the process
                            unset RESPONSE;
                            unset SELECTION;
                            unset CHANGE_CONTROL;
                            unset CONFIRM;                                                

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Transferring control back to main..";

                            sleep "${MESSAGE_DELAY}"; reset; clear; main;
                            ;;
                        *)
                            ## user does not wish to process further failovers. let's exit out and open up the main class.
                            unset RESPONSE;
                            unset SELECTION;
                            unset CHANGE_CONTROL;
                            unset CONFIRM;

                            sleep "${MESSAGE_DELAY}"; reset; clear; exec ${PLUGIN_ROOT_DIR}/${MAIN_CLASS};

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
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Key management request canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.request.canceled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
            *)
                ## user did not provide a yes/no answer
                unset CONFIRM;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection provided is invalid";

                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/selection.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";


    return 0;
}

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

main;


return 0;
