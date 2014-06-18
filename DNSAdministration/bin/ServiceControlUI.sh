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

    if [ ! -z "${IS_DNS_SVC_MGMT_ENABLED}" ] && [ "${IS_DNS_SVC_MGMT_ENABLED}" = "${_FALSE}" ]
    then
        reset; clear;

        $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service management has not been enabled. Cannot continue.");

        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<request.not.authorized\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        unset METHOD_NAME;
        unset CNAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.control.request.type\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.control.roleswap\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.control.management\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.control.rndc.generate.keys\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read SELECTION;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION -> ${SELECTION}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${SELECTION} in
            1)
                ## service request is role swap. process accordingly
                unset SELECTION;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service role swap request has been selected. Sending to processServiceSwitch..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                processServiceSwitch;
                ;;
            2)
                ## service request is management. can be one of rndc or full service restart
                unset SELECTION;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service management request has been selected. Sending to select_management_systems..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                selectTargetSystems;
                ;;
            3)
                ## service request is management. can be one of rndc or full service restart
                unset SELECTION;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Key management request has been selected. Sending to serviceKeyManagement..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                serviceKeyManagement;
                ;;
            [Xx]|[Qq]|[Cc])
                reset; clear;

                unset SELECTION;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Restart request canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  selectTargetSystems
#   DESCRIPTION:  Main entry point for application. Currently, it is configured
#                 to run both interactively and non-interactively, however, the
#                 non-interactive functionality has not yet been implemented.
#    PARAMETERS:  None
#==============================================================================
function selectTargetSystems
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    if [ ${#DNS_SERVERS[@]} -gt ${LIST_DISPLAY_MAX} ]
    then
        while true
        do
            reset; clear;

            print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<named.reset.select.servers\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

            while [ ${A} -ne ${LIST_DISPLAY_MAX} ]
            do
                if [ ! ${B} -eq ${#DNS_SERVERS[@]} ]
                then
                    print "${B} - ${DNS_SERVERS[${B}]}";

                    (( A += 1 ));
                    (( B += 1 ));
                else
                    B=${#DNS_SERVERS[@]};
                    A=${LIST_DISPLAY_MAX};
                fi
            done

            if [ $(expr ${B} - ${LIST_DISPLAY_MAX}) -eq 0 ]
            then
                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.display.next\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
            else
                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.display.prev\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.display.next\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
            fi

            ## add the option to run against all sites listed
            print "A - $(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.all\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%SITE%/all sites for ${SVC_REQUEST_OPTION}/")\n";
            print "C - $(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

            read SELECTED_SERVER;

            reset; clear;

            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTED_SERVER -> ${SELECTED_SERVER}";

            case ${SELECTED_SERVER} in
                [Nn])
                    clear;
                    unset SELECTED_SERVER;

                    if [ ${B} -ge ${#DNS_SERVERS[@]} ]
                    then
                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<forward.shift.failed\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                        A=0; B=0;

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    else
                        A=0;

                        continue;
                    fi
                    ;;
                [Pp])
                    clear;
                    unset SELECTED_SERVER;

                    if [ $(expr ${B} - ${LIST_DISPLAY_MAX}) -eq 0 ]
                    then
                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<previous.shift.failed\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                        A=0; B=0;

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    else
                        A=0;
                        (( B -= (( ${LIST_DISPLAY_MAX} * 2 )) ));
                        continue;
                    fi
                    ;;
                [0-${B}]*|[Aa])
                    ## set a/b back to 0
                    A=0; B=0;

                    ## ask the user what we want to do
                    ## either restart or do some rndc work
                    while true
                    do
                        reset; clear;

                        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<process.service.type\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<process.service.restart\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<process.rndc.command\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<}system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                        read REQUEST_TYPE;

                        reset; clear;
                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_TYPE -> ${REQUEST_TYPE}";

                        case ${REQUEST_TYPE} in
                            1)
                                unset REQUEST_TYPE;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to execute service restart received. Processing through request_rndc_command..";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                                request_rndc_command;
                                ;;
                            2)
                                ## user chose to execute an rndc command
                                unset REQUEST_TYPE;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to execute RNDC commands received. Processing through request_rndc_command..";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                                request_rndc_command;
                                ;;
                            [Xx]|[Qq]|[Cc])
                                ## user chose to cancel
                                ## unset our variables and reload
                                reset; clear;

                                unset REQUEST_TYPE;
                                unset SELECTED_SERVER;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Restart request canceled.";

                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                ;;
                            *)
                                clear;
                                unset REQUEST_TYPE;

                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                ;;
                        esac
                    done
                    ;;
                [Xx]|[Qq]|[Cc])
                    reset; clear;

                    unset SELECTION;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Restart request canceled.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                    ## terminate this thread and return control to main
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    sleep "${MESSAGE_DELAY}"; reset; clear; main;
                    ;;
                *)
                    reset; clear;

                    A=0; B=0;

                    unset SELECTION;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    ;;
            esac
        done
    else
        while true
        do
            reset; clear;

            print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.list.available\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

            while [ ${A} -ne ${#DNS_SERVERS[@]} ]
            do
                print "${A} - ${DNS_SERVERS[${A}]}";

                (( A += 1 ));
            done

            ## add the option to run against all sites listed
            [ ! ${#DNS_SERVERS[@]} -eq 1 ] && print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.all\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%SITE%/all sites for ${SVC_REQUEST_OPTION}/")\n";
            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

            read SELECTION;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION -> ${SELECTION}";

            reset; clear;

            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

            case ${SELECTION} in
                [0-${A}]|[Aa]*)
                    ## reset a back to 0
                    A=0;

                    ## ask the user what we want to do
                    ## either restart or do some rndc work
                    while true
                    do
                        reset; clear;

                        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.control.type.management\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<process.service.restart\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<process.rndc.command\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                        read REQUEST_TYPE;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_TYPE -> ${REQUEST_TYPE}";

                        reset; clear;

                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                        case ${REQUEST_TYPE} in
                            1)
                                unset REQUEST_TYPE;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to execute service restart received. Processing through request_rndc_command..";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                processServiceRestart;
                                ;;
                            2)
                                ## user chose to execute an rndc command
                                unset REQUEST_TYPE;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to execute RNDC commands received. Processing through request_rndc_command..";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                provideCommandName;
                                ;;
                            [Xx]|[Qq]|[Cc])
                                ## user chose to cancel
                                ## unset our variables and reload
                                reset; clear;

                                unset REQUEST_TYPE;
                                unset SELECTED_SERVER;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Restart request canceled.";

                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                ;;
                            *)
                                clear;
                                unset REQUEST_TYPE;
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                ;;
                        esac
                    done
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

                    sleep ${MESSAGE_DELAY}; reset; clear; main;
                    ;;
                *)
                    unset SELECTION;
                    reset; clear;

                    A=0;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    ;;
            esac
        done
    fi

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  provideCommandName
#   DESCRIPTION:  Provide application function usage information
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function provideCommandName
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<rndc.select.command\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<rndc.reload.command\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<rndc.refresh.command\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<rndc.reconfig.command\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<rndc.stats.command\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<rndc.status.command\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<rndc.flush.command\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        read COMMAND;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "COMMAND -> ${COMMAND}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${COMMAND} in
            reload|refresh)
                ## reload command can be run on any server, refresh command can only be run on slaves
                ## check to see if we're running refresh, if so, make sure its a slave
                if [ "${COMMAND}" = "refresh" ]
                then
                    if [ "${SELECTED_SERVER}" = "[Aa]" ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received control request ${COMMAND}. This control request is only applicable to slave servers.");

                        ## can't run a refresh command on the master. the DNS_SERVERS array contains the
                        ## DNS master in it, so we're re-initializing it as the DNS_SLAVES array to work
                        set -A DNS_SERVERS ${DNS_SLAVES[@]};
                    else [ "${DNS_SERVERS[${SELECTED_SERVER}]}" != "${NAMED_MASTER}" ]
                        ## refresh command with named master. not gonna work.
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC control command ${COMMAND} is not valid for master nameservers.";

                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<rndc.refresh.master.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%COMMAND%/${COMMAND}/" -e "s/%SERVER%/${DNS_SERVERS[${SELECTED_SERVER}]}/")";

                        unset COMMAND;

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing request for ${COMMAND}. Requesting optional zone..";

                reset; clear;

                print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<rndc.provide.zone\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                read ZONE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE -> ${ZONE}";

                reset; clear;

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                case ${ZONE} in
                    [Xx]|[Qq]|[Cc])
                        ## user has requested to cancel
                        reset; clear;

                        unset ZONE;
                        unset COMMAND;
                        unset SELECTION;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC command request canceled.";

                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                        ## terminate this thread and return control to main
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        ## break out
                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                        ;;
                    *)
                        ## refresh command requires a zone. make sure we have one, if not, we can't continue
                        if [ -z "${ZONE}" ] && [ "${COMMAND}" = "refresh" ]
                        then
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC control command ${COMMAND} is not valid for master nameservers.";

                            print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<rndc.refresh.requires.zone\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%COMMAND%/${COMMAND}/")\n";

                            unset COMMAND;

                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        else
                            ## at this point we have enough information to operate against. call out to run_rndc_request to execute commands
                            if [ "${SELECTED_SERVER}" = "[Aa]" ]
                            then
                                ## make sure D is zero
                                D=0;

                                ## user has requested to execute commands against all available DNS servers
                                while [ ${D} -ne ${#DNS_SERVERS[@]} ]
                                do
                                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<named.processing.restart\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%NODE%/${DNS_SERVERS[${D}]}/")\n";

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing runRNDCCommands.sh -s ${DNS_SERVER[${D}]} -c ${COMMAND} -z "${ZONE}" -e ..";

                                    local THIS_CNAME=${CNAME};
                                    unset METHOD_NAME;
                                    unset CNAME;

                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                                    ## validate the input
                                    ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runRNDCCommands.sh -s ${DNS_SERVER[${D}]} -c ${COMMAND} -z "${ZONE}" -e;
                                    typeset -i RET_CODE=${?};

                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                    CNAME="${THIS_CNAME}";
                                    local METHOD_NAME="${THIS_CNAME}#${0}";

                                    case ${RET_CODE} in
                                        ## we could get text back or a number. check so we can display accordingly.
                                        ?([+-])+([0-9]))
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${COMMAND} executed - return code ${RET_CODE}";

                                            if [ ${RET_CODE} -eq 0 ]
                                            then
                                                print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<rndc.command.executed\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%COMMAND%/${COMMAND}/")\n";
                                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.complete\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                                continue;
                                            else
                                                [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g' -e "s/%NODE%/${DNS_SERVERS[${SELECTED_SERVER}]}/")";
                                                [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/99/{print \$2}" | sed -e 's/^ *//g;s/ *$//g' -e "s/%NODE%/${DNS_SERVERS[${SELECTED_SERVER}]}/")";
                                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.complete\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                                continue;
                                            fi
                                            ;;
                                        *)
                                            [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" "/rndc.command.execution.status/{print \$2}" | sed -e 's/^ *//g;s/ *$//g' -e "s/%COMMAND%/${COMMAND}/")";

                                            print "${RET_CODE}\n";

                                            continue;
                                            ;;
                                    esac

                                    (( D += 1 ));
                                done

                                ## reset d back to 0
                                D=0;

                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" "/system.continue.enter/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";

                                read RESPONSE;

                                case ${RESPONSE} in
                                    *)
                                        unset CONFIRMATION;
                                        unset RET_CODE;
                                        unset RETURN_CODE;
                                        unset COMMAND;
                                        unset ZONE;
                                        unset SELECTED_SERVER;
                                        unset REQUEST_TYPE;

                                        set -A DNS_SERVERS ${DNS_SERVERS[@]};

                                        sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                        ;;
                                esac
                            else
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing runRNDCCommands.sh -s ${DNS_SERVER[${SELECTED_SERVER}]} -c ${COMMAND} -z "${ZONE}" -e ..";

                                local THIS_CNAME=${CNAME};
                                unset METHOD_NAME;
                                unset CNAME;

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                                ## validate the input
                                ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runRNDCCommands.sh -s ${DNS_SERVERS[${SELECTED_SERVER}]} -c ${COMMAND} -z "${ZONE}" -e;
                                typeset -i RET_CODE=${?};

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                CNAME="${THIS_CNAME}";
                                local METHOD_NAME="${THIS_CNAME}#${0}";

                                case ${RET_CODE} in
                                    ## we could get text back or a number. check so we can display accordingly.
                                    ?([+-])+([0-9]))
                                        [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g' -e "s/%COMMAND%/${COMMAND}/")\n";
                                        [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<rndc.command.executed\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%COMMAND%/${COMMAND}/")\n";

                                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.complete\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                        sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                        ;;
                                    *)
                                        reset; clear;

                                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<rndc.command.execution.status\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                        print "${RET_CODE}\n";

                                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.complete\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<system.continue.enter\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                        read CONFIRMATION;

                                        ## unset variables
                                        unset CONFIRMATION;
                                        unset RET_CODE;
                                        unset RETURN_CODE;
                                        unset COMMAND;
                                        unset ZONE;
                                        unset SELECTED_SERVER;
                                        unset REQUEST_TYPE;
                                        set -A DNS_SERVERS ${DNS_SERVERS[@]};

                                        sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                        ;;
                                esac
                            fi
                        fi

                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.complete\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<system.continue.enter\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                        read CONFIRMATION;

                        ## unset variables
                        unset CONFIRMATION;
                        unset RET_CODE;
                        unset RETURN_CODE;
                        unset COMMAND;
                        unset ZONE;
                        unset SELECTED_SERVER;
                        unset REQUEST_TYPE;
                        set -A DNS_SERVERS ${DNS_SERVERS[@]};

                        sleep "${MESSAGE_DELAY}"; reset; clear; main;
                        ;;
                esac
                ;;
            reconfig|stats|status|flush)
                ## these commands require no further information to properly execute. call out to run_rndc_request to execute
                if [ "${SELECTED_SERVER}" = "[Aa]" ]
                then
                    ## make sure d is zero
                    D=0;

                    ## user has requested to execute commands against all available DNS servers
                    while [ ${D} -ne ${#DNS_SERVERS[@]} ]
                    do
                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<named.processing.request\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing runRNDCCommands.sh -s ${DNS_SERVER[${D}]} -c ${COMMAND} -z "${ZONE}" -e ..";

                        local THIS_CNAME=${CNAME};
                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                        ## validate the input
                        ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runRNDCCommands.sh -s ${DNS_SERVER[${D}]} -c ${COMMAND} -e;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        local METHOD_NAME="${THIS_CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]]
                        then
                            [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<99\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                            [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')\n";

                            continue;
                        fi

                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<rndc.command.execution.status\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                        print "${RET_CODE}";

                        (( D += 1 ));
                    done

                    ## reset d back to 0
                    D=0;
                else
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing runRNDCCommands.sh -s ${DNS_SERVER[${SELECTED_SERVER}]} -c ${COMMAND} -e ..";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<named.processing.request\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing runRNDCCommands.sh -s ${DNS_SERVER[${D}]} -c ${COMMAND} -z "${ZONE}" -e ..";

                    local THIS_CNAME=${CNAME};
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                    ## validate the input
                    ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runRNDCCommands.sh -s ${DNS_SERVER[${D}]} -c ${COMMAND} -e;
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    local METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                    if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]]
                    then
                        [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<99\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                        [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')\n";

                        continue;
                    fi

                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<rndc.command.execution.status\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                    print "${RET_CODE}";
                fi

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.complete\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<system.continue.enter\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                read CONFIRMATION;

                ## unset variables
                unset CONFIRMATION;
                unset RET_CODE;
                unset RETURN_CODE;
                unset COMMAND;
                unset ZONE;
                unset SELECTED_SERVER;
                unset REQUEST_TYPE;
                set -A DNS_SERVERS ${DNS_SERVERS[@]};

                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                ;;
            [Xx]|[Qq]|[Cc])
                ## user has requested to cancel
                reset; clear;

                unset ZONE;
                unset COMMAND;
                unset SELECTION;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC command request canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## break out
                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            *)
                ## a valid command name was not provided, inform and reset
                unset COMMAND;
                reset; clear;
                A=0;
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  request_rndc_command
#   DESCRIPTION:  Provide application function usage information
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function processServiceRestart
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<confirm.request\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        read CONFIRM;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONFIRM -> ${CONFIRM}";

        reset; clear;
        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${CONFIRM} in
            [Yy][Ee][Ss]|[Yy])
                ## user confirms, process the restart
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service restart CONFIRMED. Proceeding..";

                ## call out to request_rndc_command
                if [ "${SELECTION}" = "[Aa]" ]
                then
                    ## make sure d is zero
                    D=0;

                    for HOST in ${DNS_SERVERS[@]}
                    do
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HOST -> ${HOST}";

                        ## loop through all configured nameservers and process the restart
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing restart on ${HOST}..";

                        ## temp unset
                        local THIS_CNAME=${CNAME};
                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                        ## validate the input
                        ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runControlRequest.sh -s ${HOST} -c restart -e;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        local METHOD_NAME="${THIS_CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        [ -z "${RET_CODE}" ] && print "${HOST}: $(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";
                        [[ ! -z "${RET_CODE}" && ${RET_CODE} -ne 0 ]] && print "${HOST}: $(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";
                        [[ ! -z "${RET_CODE}" && ${RET_CODE} -eq 0 ]] && print "${HOST}: $(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" "/request.complete/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";

                        unset RET_CODE;
                        unset RETURN_CODE;
                    done

                    ## restart all done here. loop back into main.
                    unset RET_CODE;
                    unset RETURN_CODE;
                    unset SELECTION;
                    unset REQUEST_TYPE;
                    unset CONFIRM;

                    sleep "${MESSAGE_DELAY}"; reset; clear; main;
                else
                    ## only one server was selected, process
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing restart on ${DNS_SERVERS[${SELECTION}]}..";

                    ## temp unset
                    local THIS_CNAME=${CNAME};
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                    ## validate the input
                    ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runControlRequest.sh -s ${DNS_SERVERS[${SELECTION}]} -c restart -e;
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    local METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. RET_CODE -> ${RET_CODE}";

                    [ -z "${RET_CODE}" ] && print "${HOST}: $(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";
                    [[ ! -z "${RET_CODE}" && ${RET_CODE} -ne 0 ]] && print "${HOST}: $(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";
                    [[ ! -z "${RET_CODE}" && ${RET_CODE} -eq 0 ]] && print "${HOST}: $(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" "/request.complete/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";

                    ## restart all done here. loop back into main.
                    unset RET_CODE;
                    unset RETURN_CODE;
                    unset SELECTION;
                    unset REQUEST_TYPE;
                    unset CONFIRM;

                    sleep "${MESSAGE_DELAY}"; reset; clear; main;
                fi
                ;;
            [Nn][Oo]|[Nn])
                ## user does NOT wish to restart services
                ## unset variables and kick back to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service restart ABORTED.";

                unset CONFIRM;
                unset SELECTION;
                unset REQUEST_TYPE;

                reset; clear;

                print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<named.restart.skipped\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                ;;
            [Xx]|[Qq]|[Cc])
                ## user has requested to cancel
                reset; clear;

                unset CONFIRM;
                unset SELECTION;
                unset REQUEST_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC command request canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## break out
                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                ;;
            *)
                ## a valid command name was not provided, inform and reset
                unset CONFIRM;
                reset; clear;

                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

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

        print "\t\t\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.key.management.title\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t\t\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.key.management.request\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        for KEYTYPE in ${RNDC_LOCAL_KEY} ${TSIG_TRANSFER_KEY} ${DHCPD_UPDATE_KEY}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "KEYTYPE -> ${KEYTYPE}";

            print "${KEYTYPE} - $(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} ${PLUGIN_CONFIG} | awk -F "=" "/service.key.management.${KEYTYPE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";
        done

        unset KEYTYPE;

        read SELECTION;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION -> ${SELECTION}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${SELECTION} in
            ${RNDC_LOCAL_KEY}|${TSIG_TRANSFER_KEY}|${DHCPD_UPDATE_KEY}|${DNSSEC_KEYS})
                ## these are all valid keytypes
                ## request a change order and then confirm it -
                ## if we like it, we'll go do it
                if [[ "${SELECTION}" = "${RNDC_LOCAL_KEY}" && -z "${IS_RNDC_MGMT_ENABLED}" || "${IS_RNDC_MGMT_ENABLED}" != "${_TRUE}" ]]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC Key management has not been enabled. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<request.not.authorized\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                if [[ "${SELECTION}" = "${TSIG_TRANSFER_KEY}" && -z "${IS_TSIG_MGMT_ENABLED}" || "${IS_TSIG_MGMT_ENABLED}" != "${_TRUE}" ]]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TSIG Key management has not been enabled. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<request.not.authorized\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                if [[ "${SELECTION}" = "${DHCPD_UPDATE_KEY}" && -z "${IS_DHCPD_MGMT_ENABLED}" || "${IS_DHCPD_MGMT_ENABLED}" != "${_TRUE}" ]]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DHCPD Key management has not been enabled. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<request.not.authorized\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                if [[ "${SELECTION}" = "${DNSSEC_KEYS}" && -z "${IS_DNSSEC_MGMT_ENABLED}" || "${IS_DNSSEC_MGMT_ENABLED}" != "${_TRUE}" ]]
                then
                    unset SELECTION;

                    reset; clear;

                    $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNSSEC Key management has not been enabled. Cannot continue.");

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<request.not.authorized\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                reset; clear;

                while true
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting change information..";

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

                    if [[ ! -z "${CANCEL_REQ}" && "${CANCEL_REQ}" = "${_TRUE}" ]]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                        ## unset SVC_LIST, we dont need it now
                        unset SVC_LIST;

                        ## terminate this thread and return control to main
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;

                        sleep ${MESSAGE_DELAY}; reset; clear; main;
                    fi

                    break;
                done

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
                        ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runKeyGeneration.sh -g ${SELECTION},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
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
                ;;
            [Xx]|[Qq]|[Cc])
                ## user has requested to cancel
                reset; clear;

                unset SELECTION;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Key management request canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## break out
                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                ;;
            *)
                ## a valid command name was not provided, inform and reset
                unset SELECTION;

                reset; clear;

                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

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

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide application function usage information
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function processServiceSwitch
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    if [ ! -z "${AVAILABLE_MASTER_SERVERS[@]}" ] && [ ${#AVAILABLE_MASTER_SERVERS[@]} -ne 0 ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validation complete. Requesting target..";

        ## we have a set of slave servers that we can operate against
        while true
        do
            reset; clear;

            print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.control.target.roleswap\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

            for AVAILABLE_HOST in ${AVAILABLE_MASTER_SERVERS[@]}
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "AVAILABLE_HOST -> ${AVAILABLE_HOST}";

                print "${AVAILABLE_HOST}\n";
            done

            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

            read SWAP_SYSTEM;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SWAP_SYSTEM -> ${SWAP_SYSTEM}";

            reset; clear;

            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

            ## value must be either alpha, numeric, or alphanumeric
            case ${SWAP_SYSTEM} in
                [Xx]|[Qq]|[Cc])
                    ## request to cancel
                    unset SWAP_SYSTEM;

                    reset; clear;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Role swap request canceled.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                    ;;
                +([a-z]|[A-Z]|[0-9]|['-']))
                    ## we have a properly formatted system name. verify that it is indeed authorized
                    ## and that it is not the current mastern
                    if [ "${SWAP_SYSTEM}" = "${NAMED_MASTER}" ]
                    then
                        ## the requested system is the existing master. can't continue.
                        unset SWAP_SYSTEM;

                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided target is equal to the existing master. Cannot continue.";

                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<target.nameserver.matches.existing\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi

                    for AVAILABLE_HOST in ${AVAILABLE_MASTER_SERVERS[@]}
                    do
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "AVAILABLE_HOST -> ${AVAILABLE_HOST}";

                        [ "${AVAILABLE_HOST}" = "${SWAP_SYSTEM}" ] && break;

                        (( ERROR_COUNT += 1 ));
                    done

                    if [ ${ERROR_COUNT} -ne 0 ]
                    then
                        ## provided system is not authorized to be switched to a master role
                        unset SWAP_SYSTEM;

                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided target is not an authorized to become a master nameserver. Cannot continue.";

                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<target.nameserver.matches.existing\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Target validated. Requesting change information..";

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

                    if [[ ! -z "${CANCEL_REQ}" && "${CANCEL_REQ}" = "${_TRUE}" ]]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                        ## unset SVC_LIST, we dont need it now
                        unset SVC_LIST;

                        ## terminate this thread and return control to main
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;

                        sleep ${MESSAGE_DELAY}; reset; clear; main;
                    fi

                    ## we can get started on the role swap. we need a change control here
                    ## and confirmation that the requestor actually wants to make the change
                    while true
                    do
                        reset; clear;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change control accepted. Proceeding..";

                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.confirm.roleswap.target.master\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%MASTER_SERVER%/${SWAP_SYSTEM}/")";
                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.confirm.roleswap.target.slave\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%SLAVE_SERVER%/${NAMED_MASTER}/")";
                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<confirm.request\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                        read CONFIRM;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONFIRM -> ${CONFIRM}";

                        reset; clear;

                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                        case ${CONFIRM} in
                            [Yy][Ee][Ss]|[Yy])
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Roleswap confirmed. Processing..";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing runRoleSwap.sh -s ${NAMED_MASTER} -t ${SWAP_SYSTEM} -c ${CHANGE_CONTROL} -i ${IUSER_AUDIT} -e ..";

                                local THIS_CNAME=${CNAME};
                                unset METHOD_NAME;
                                unset CNAME;

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                                ## validate the input
                                ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runRoleSwap.sh -s ${NAMED_MASTER} -t ${SWAP_SYSTEM} -c ${CHANGE_CONTROL} -i ${IUSER_AUDIT} -e;
                                typeset -i RET_CODE=${?};

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                CNAME="${THIS_CNAME}";
                                local METHOD_NAME="${THIS_CNAME}#${0}";

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]]
                                then
                                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RET_CODE%/${RET_CODE}/")";

                                    unset RET_CODE;
                                    unset SUCCESSFUL_ROLE_SWAP;
                                    unset CONFIRM;
                                    unset CHANGE_CONTROL;
                                    unset SWAP_SYSTEM;
                                    unset SELECTION;

                                    sleep "${MESSAGE_DELAY}"; reset; main;
                                fi

                                ## swap succeeded. now we must modify our local configuration.
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Role swap completed successfully. Now modifying local configuration..";

                                reset; clear;

                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                local THIS_CNAME=${CNAME};
                                unset METHOD_NAME;
                                unset CNAME;

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                                ## validate the input
                                ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runRoleSwap.sh -l -e;
                                typeset -i RET_CODE=${?};

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                CNAME="${THIS_CNAME}";
                                local METHOD_NAME="${THIS_CNAME}#${0}";

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<roleswap.local.config.mod.failed\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
                                [ ${RET_CODE} -eq 0 ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<service.roleswap.complete\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                unset RET_CODE;
                                unset SUCCESSFUL_ROLE_SWAP;
                                unset CONFIRM;
                                unset CHANGE_CONTROL;
                                unset SWAP_SYSTEM;
                                unset SELECTION;

                                sleep "${MESSAGE_DELAY}"; reset; main;
                                ;;
                            [Nn][Oo]|[Nn])
                                unset CONFIRM;
                                unset CHANGE_CONTROL;
                                unset SWAP_SYSTEM;

                                reset; clear;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request canceled.";

                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                ;;
                            *)
                                unset CONFIRM;

                                reset; clear;

                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                ;;
                        esac
                    done
                    ;;
                [Xx]|[Qq]|[Cc])
                    unset SWAP_SYSTEM;

                    reset; clear;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Role swap request canceled.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                    ;;
                *)
                    unset SWAP_SYSTEM;

                    reset; clear;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    ;;
            esac
        done
    else
        ## we dont have any slave servers configured. this could be an "ERROR"
        ## or maybe thats the way it really is.
        unset SELECTION;

        reset; clear;

        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "There are no nameservers configured as authorized systems. Cannot perform role-swap.";

        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<no.available.target.systems\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
        sleep "${MESSAGE_DELAY}"; reset; clear; main;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 0;
}

function rndcKeyfileGeneration
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";


    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 1;
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

return ${RETURN_CODE};
