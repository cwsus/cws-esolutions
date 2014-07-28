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

## source aliases/functions ..
[ -s ${PLUGIN_LIB_DIRECTORY}/aliases ] && . ${PLUGIN_LIB_DIRECTORY}/aliases;
[ -s ${PLUGIN_LIB_DIRECTORY}/functions ] && . ${PLUGIN_LIB_DIRECTORY}/functions;

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

    if [ ! -z "${IS_DNS_SVC_MGMT_ENABLED}" ] && [ "${IS_DNS_SVC_MGMT_ENABLED}" = "${_FALSE}" ]
    then
        reset; clear;

        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service management has not been enabled. Cannot continue.";

        echo "$(awk -F "=" '/\<request.not.authorized\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        unset METHOD_NAME;
        unset CNAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

        sleep ${MESSAGE_DELAY}; reset; clear; /usr/bin/env -i ksh ${MAIN_CLASS};

        return 0;
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
            \t$(awk -F "=" '/\<system.available.options\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n
            \t$(awk -F "=" '/\<service.control.request.type\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n
            \t$(awk -F "=" '/\<service.control.roleswap\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<service.control.management\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<service.control.rndc.generate.keys\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        read SELECTION;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION -> ${SELECTION}";

        reset; clear;

        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

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

                echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                sleep ${MESSAGE_DELAY}; reset; clear; /usr/bin/env -i ksh ${MAIN_CLASS};

                return 0;
                ;;
            *)
                ## no valid option was provided. fail.
                unset SELECTION;

                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

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
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    if [ ${#DNS_SERVERS[@]} -gt ${LIST_DISPLAY_MAX} ]
    then
        while true
        do
            reset; clear;

            echo "\t$(awk -F "=" '/\<named.reset.select.servers\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

            while [ ${A} -ne ${LIST_DISPLAY_MAX} ]
            do
                if [ ! ${B} -eq ${#DNS_SERVERS[@]} ]
                then
                    echo "${B} - ${DNS_SERVERS[${B}]}";

                    (( A += 1 ));
                    (( B += 1 ));
                else
                    B=${#DNS_SERVERS[@]};
                    A=${LIST_DISPLAY_MAX};
                fi
            done

            if [ $(expr ${B} - ${LIST_DISPLAY_MAX}) -eq 0 ]
            then
                echo "$(awk -F "=" '/\<system.display.next\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";
            else
                echo "
                    $(awk -F "=" '/\<system.display.prev\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
                    $(awk -F "=" '/\<system.display.next\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";
            fi

            ## add the option to run against all sites listed
            echo "
                A - $(awk -F "=" '/\<system.option.all\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%SITE%/all sites for ${SVC_REQUEST_OPTION}/")\n
                C - $(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

            read SELECTED_SERVER;

            reset; clear;

            echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTED_SERVER -> ${SELECTED_SERVER}";

            case ${SELECTED_SERVER} in
                [Nn])
                    clear;
                    unset SELECTED_SERVER;

                    if [ ${B} -ge ${#DNS_SERVERS[@]} ]
                    then
                        echo "$(awk -F "=" '/\<forward.shift.failed\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

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
                        echo "$(awk -F "=" '/\<previous.shift.failed\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

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

                        echo "
                            \t$(awk -F "=" '/\<process.service.type\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n
                            \t$(awk -F "=" '/\<process.service.restart\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
                            \t$(awk -F "=" '/\<process.rndc.command\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
                            \t$(awk -F "=" '/\<}system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                        read REQUEST_TYPE;

                        reset; clear;
                        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

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

                                echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";
                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                ;;
                            *)
                                clear;
                                unset REQUEST_TYPE;

                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                ;;
                        esac
                    done
                    ;;
                [Xx]|[Qq]|[Cc])
                    reset; clear;

                    unset SELECTION;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Restart request canceled.";

                    echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                    ## terminate this thread and return control to main
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    sleep "${MESSAGE_DELAY}"; reset; clear; main;
                    ;;
                *)
                    reset; clear;

                    A=0; B=0;

                    unset SELECTION;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                    echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    ;;
            esac
        done
    else
        while true
        do
            reset; clear;

            echo "\t$(awk -F "=" '/\<system.list.available\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

            while [ ${A} -ne ${#DNS_SERVERS[@]} ]
            do
                echo "${A} - ${DNS_SERVERS[${A}]}";

                (( A += 1 ));
            done

            ## add the option to run against all sites listed
            [ ! ${#DNS_SERVERS[@]} -eq 1 ] && echo "$(awk -F "=" '/\<system.option.all\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%SITE%/all sites for ${SVC_REQUEST_OPTION}/")\n";
            echo "$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

            read SELECTION;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION -> ${SELECTION}";

            reset; clear;

            echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

            case ${SELECTION} in
                [0-${A}]|[Aa]*)
                    ## reset a back to 0
                    A=0;

                    ## ask the user what we want to do
                    ## either restart or do some rndc work
                    while true
                    do
                        reset; clear;

                        echo "
                            \t$(awk -F "=" '/\<service.control.type.management\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n
                            \t$(awk -F "=" '/\<process.service.restart\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";
                            \t$(awk -F "=" '/\<process.rndc.command\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";
                            \t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                        read REQUEST_TYPE;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_TYPE -> ${REQUEST_TYPE}";

                        reset; clear;

                        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

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

                                echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                ;;
                            *)
                                clear;
                                unset REQUEST_TYPE;
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                ;;
                        esac
                    done
                    ;;
               [Xx]|[Qq]|[Cc])
                    reset; clear;
                    unset SELECTION;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Restart request canceled.";

                    echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

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

                    echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    ;;
            esac
        done
    fi

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

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
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        echo "
            \t$(awk -F "=" '/\<rndc.select.command\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n
            $(awk -F "=" '/\<rndc.reload.command\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            $(awk -F "=" '/\<rndc.refresh.command\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            $(awk -F "=" '/\<rndc.reconfig.command\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            $(awk -F "=" '/\<rndc.stats.command\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            $(awk -F "=" '/\<rndc.status.command\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            $(awk -F "=" '/\<rndc.flush.command\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            $(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        read COMMAND;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "COMMAND -> ${COMMAND}";

        reset; clear;

        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        case ${COMMAND} in
            reload|refresh)
                ## reload command can be run on any server, refresh command can only be run on slaves
                ## check to see if we're running refresh, if so, make sure its a slave
                if [ "${COMMAND}" = "refresh" ]
                then
                    if [ "${SELECTED_SERVER}" = "[Aa]" ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received control request ${COMMAND}. This control request is only applicable to slave servers.";

                        ## can't run a refresh command on the master. the DNS_SERVERS array contains the
                        ## DNS master in it, so we're re-initializing it as the DNS_SLAVES array to work
                        set -A DNS_SERVERS ${DNS_SLAVES[@]};
                    else [ "${DNS_SERVERS[${SELECTED_SERVER}]}" != "${NAMED_MASTER}" ]
                        ## refresh command with named master. not gonna work.
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC control command ${COMMAND} is not valid for master nameservers.";

                        echo "$(awk -F "=" '/\<rndc.refresh.master.pending.message\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%COMMAND%/${COMMAND}/" -e "s/%SERVER%/${DNS_SERVERS[${SELECTED_SERVER}]}/")";

                        unset COMMAND;

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing request for ${COMMAND}. Requesting optional zone..";

                reset; clear;

                echo "
                    \t$(awk -F "=" '/\<rndc.provide.zone\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n
                    $(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                read ZONE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE -> ${ZONE}";

                reset; clear;

                echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                case ${ZONE} in
                    [Xx]|[Qq]|[Cc])
                        ## user has requested to cancel
                        reset; clear;

                        unset ZONE;
                        unset COMMAND;
                        unset SELECTION;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC command request canceled.";

                        echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

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

                            echo "$(awk -F "=" '/\<rndc.refresh.requires.zone\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%COMMAND%/${COMMAND}/")\n";

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
                                    echo "$(awk -F "=" '/\<named.processing.restart\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%NODE%/${DNS_SERVERS[${D}]}/")\n";

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing runRNDCCommands.sh -s ${DNS_SERVER[${D}]} -c ${COMMAND} -z "${ZONE}" -e ..";

                                    typeset THIS_CNAME=${CNAME};
                                    unset METHOD_NAME;
                                    unset CNAME;

                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                                    ## validate the input
                                    ${PLUGIN_LIB_DIRECTORY}/runRNDCCommands.sh -s ${DNS_SERVER[${D}]} -c ${COMMAND} -z "${ZONE}" -e;
                                    typeset -i RET_CODE=${?};

                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                    CNAME="${THIS_CNAME}";
                                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                                    case ${RET_CODE} in
                                        ## we could get text back or a number. check so we can display accordingly.
                                        ?([+-])+([0-9]))
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${COMMAND} executed - return code ${RET_CODE}";

                                            if [ ${RET_CODE} -eq 0 ]
                                            then
                                                echo "
                                                    $(awk -F "=" '/\<rndc.command.executed\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%COMMAND%/${COMMAND}/")\n
                                                    $(awk -F "=" '/\<system.request.complete\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                                continue;
                                            else
                                                [ ! -z "${RET_CODE}" ] && echo "$(awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%NODE%/${DNS_SERVERS[${SELECTED_SERVER}]}/")";
                                                [ -z "${RET_CODE}" ] && echo "$(awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%NODE%/${DNS_SERVERS[${SELECTED_SERVER}]}/")";
                                                echo "$(awk -F "=" '/\<system.request.complete\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                                continue;
                                            fi
                                            ;;
                                        *)
                                            [ -z "${RET_CODE}" ] && echo "$(awk -F "=" '/\<rndc.command.execution.status\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%COMMAND%/${COMMAND}/")";

                                            echo "${RET_CODE}\n";

                                            continue;
                                            ;;
                                    esac

                                    (( D += 1 ));
                                done

                                ## reset d back to 0
                                D=0;

                                echo "$(awk -F "=" '/\<system.continue.enter\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

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

                                typeset THIS_CNAME=${CNAME};
                                unset METHOD_NAME;
                                unset CNAME;

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                                ## validate the input
                                ${PLUGIN_LIB_DIRECTORY}/runRNDCCommands.sh -s ${DNS_SERVERS[${SELECTED_SERVER}]} -c ${COMMAND} -z "${ZONE}" -e;
                                typeset -i RET_CODE=${?};

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                CNAME="${THIS_CNAME}";
                                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                                case ${RET_CODE} in
                                    ## we could get text back or a number. check so we can display accordingly.
                                    ?([+-])+([0-9]))
                                        [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ] && echo "$(awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%COMMAND%/${COMMAND}/")\n";
                                        [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ] && echo "$(awk -F "=" '/\<rndc.command.executed\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%COMMAND%/${COMMAND}/")\n";

                                        echo "$(awk -F "=" '/\<system.request.complete\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                        sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                        ;;
                                    *)
                                        reset; clear;

                                        echo "$(awk -F "=" '/\<rndc.command.execution.status\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                        echo "${RET_CODE}\n";

                                        echo "
                                            $(awk -F "=" '/\<system.request.complete\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n
                                            $(awk -F "=" '/\<system.continue.enter\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

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

                        echo "
                            $(awk -F "=" '/\<system.request.complete\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n
                            $(awk -F "=" '/\<system.continue.enter\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

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
                        echo "$(awk -F "=" '/\<named.processing.request\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing runRNDCCommands.sh -s ${DNS_SERVER[${D}]} -c ${COMMAND} -z "${ZONE}" -e ..";

                        typeset THIS_CNAME=${CNAME};
                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                        ## validate the input
                        ${PLUGIN_LIB_DIRECTORY}/runRNDCCommands.sh -s ${DNS_SERVER[${D}]} -c ${COMMAND} -e;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                        then
                            [ -z "${RET_CODE}" ] && echo "$(awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";
                            [ ! -z "${RET_CODE}" ] && echo "$(awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                            continue;
                        fi

                        echo "$(awk -F "=" '/\<rndc.command.execution.status\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                        (( D += 1 ));
                    done

                    ## reset d back to 0
                    D=0;
                else
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing runRNDCCommands.sh -s ${DNS_SERVER[${SELECTED_SERVER}]} -c ${COMMAND} -e ..";

                    echo "$(awk -F "=" '/\<named.processing.request\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing runRNDCCommands.sh -s ${DNS_SERVER[${D}]} -c ${COMMAND} -z "${ZONE}" -e ..";

                    typeset THIS_CNAME=${CNAME};
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/runRNDCCommands.sh -s ${DNS_SERVER[${D}]} -c ${COMMAND} -e;
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                    then
                        [ -z "${RET_CODE}" ] && echo "$(awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";
                        [ ! -z "${RET_CODE}" ] && echo "$(awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                        continue;
                    fi

                    echo "$(awk -F "=" '/\<rndc.command.execution.status\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";
                fi

                echo "
                    $(awk -F "=" '/\<system.request.complete\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n
                    $(awk -F "=" '/\<system.continue.enter\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

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

                echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

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

                echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

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
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        echo "
            $(awk -F "=" '/\<confirm.request\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            $(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        read CONFIRM;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONFIRM -> ${CONFIRM}";

        reset; clear;
        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

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
                        typeset THIS_CNAME=${CNAME};
                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                        ## validate the input
                        ${PLUGIN_LIB_DIRECTORY}/runControlRequest.sh -s ${HOST} -c restart -e;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        [ -z "${RET_CODE}" ] && echo "${HOST}: $(awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";
                        [ ! -z "${RET_CODE}" ] && [ ${RET_CODE} -ne 0 ] && echo "${HOST}: $(awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";
                        [ ! -z "${RET_CODE}" ] && [ ${RET_CODE} -eq 0 ] && echo "${HOST}: $(awk -F "=" '/\<request.complete\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

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
                    typeset THIS_CNAME=${CNAME};
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/runControlRequest.sh -s ${DNS_SERVERS[${SELECTION}]} -c restart -e;
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. RET_CODE -> ${RET_CODE}";

                    [ -z "${RET_CODE}" ] && echo "${HOST}: $(awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";
                    [ ! -z "${RET_CODE}" ] && [ ${RET_CODE} -ne 0 ] && echo "${HOST}: $(awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";
                    [ ! -z "${RET_CODE}" ] && [ ${RET_CODE} -eq 0 ] && echo "${HOST}: $(awk -F "=" '/\<request.complete\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

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

                echo "$(awk -F "=" '/\<named.restart.skipped\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                ;;
            [Xx]|[Qq]|[Cc])
                ## user has requested to cancel
                reset; clear;

                unset CONFIRM;
                unset SELECTION;
                unset REQUEST_TYPE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC command request canceled.";

                echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

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

                echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

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
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        echo "
            \t\t$(awk -F "=" '/\<service.key.management.title\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t\t$(awk -F "=" '/\<service.key.management.request\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        for KEYTYPE in ${RNDC_LOCAL_KEY} ${TSIG_TRANSFER_KEY} ${DHCPD_UPDATE_KEY}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "KEYTYPE -> ${KEYTYPE}";

            echo "${KEYTYPE} - $(awk -F "=" '/\<service.key.management.${KEYTYPE}\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";
        done

        unset KEYTYPE;

        read SELECTION;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION -> ${SELECTION}";

        reset; clear;

        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        case ${SELECTION} in
            ${RNDC_LOCAL_KEY}|${TSIG_TRANSFER_KEY}|${DHCPD_UPDATE_KEY}|${DNSSEC_KEYS})
                ## these are all valid keytypes
                ## request a change order and then confirm it -
                ## if we like it, we'll go do it
                if [[ "${SELECTION}" = "${RNDC_LOCAL_KEY}" && -z "${IS_RNDC_MGMT_ENABLED}" || "${IS_RNDC_MGMT_ENABLED}" != "${_TRUE}" ]]
                then
                    unset SELECTION;

                    reset; clear;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC Key management has not been enabled. Cannot continue.";

                    echo "$(awk -F "=" '/\<request.not.authorized\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                if [[ "${SELECTION}" = "${TSIG_TRANSFER_KEY}" && -z "${IS_TSIG_MGMT_ENABLED}" || "${IS_TSIG_MGMT_ENABLED}" != "${_TRUE}" ]]
                then
                    unset SELECTION;

                    reset; clear;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TSIG Key management has not been enabled. Cannot continue.";

                    echo "$(awk -F "=" '/\<request.not.authorized\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                if [[ "${SELECTION}" = "${DHCPD_UPDATE_KEY}" && -z "${IS_DHCPD_MGMT_ENABLED}" || "${IS_DHCPD_MGMT_ENABLED}" != "${_TRUE}" ]]
                then
                    unset SELECTION;

                    reset; clear;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DHCPD Key management has not been enabled. Cannot continue.";

                    echo "$(awk -F "=" '/\<request.not.authorized\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                if [[ "${SELECTION}" = "${DNSSEC_KEYS}" && -z "${IS_DNSSEC_MGMT_ENABLED}" || "${IS_DNSSEC_MGMT_ENABLED}" != "${_TRUE}" ]]
                then
                    unset SELECTION;

                    reset; clear;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNSSEC Key management has not been enabled. Cannot continue.";

                    echo "$(awk -F "=" '/\<request.not.authorized\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                reset; clear;

                while true
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting change information..";

                    typeset THIS_CNAME=${CNAME};
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                    ## validate the input
                    ${APP_ROOT}/${BIN_DIRECTORY}/obtainChangeControl.sh;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    if [[ ! -z "${CANCEL_REQ}" && "${CANCEL_REQ}" = "${_TRUE}" ]]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                        echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

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

                echo "\t\t$(awk -F "=" '/\<confirm.request\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                read CONFIRM;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONFIRM -> ${CONFIRM}";

                reset; clear;

                echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                case ${CONFIRM} in
                    [Yy][Ee][Ss]|[Yy])
                        ## unset confirmation
                        unset CONFIRM;
                        reset; clear;

                        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                        ## begin processing of key renewal
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing key renewal for ${SELECTION}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${PLUGIN_LIB_DIRECTORY}/runKeyGeneration.sh -g ${SELECTION},${CHANGE_CONTROL},${IUSER_AUDIT} -e";

                        typeset THIS_CNAME=${CNAME};
                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                        ## validate the input
                        ${PLUGIN_LIB_DIRECTORY}/runKeyGeneration.sh -g ${SELECTION},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        if [[ -z "${RET_CODE}" || ! -z "${RET_CODE}" && ${RET_CODE} -ne 0 ]]
                        then
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No return code was received from run_rndc_request. Please review "ERROR" logs.";

                            [ -z "${RET_CODE}" ] && echo "$(awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";
                            [ ! -z "${RET_CODE}" ] && echo "$(awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

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

                            echo "
                                $(awk -F "=" '/\<service.key.management.keys.renewed\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%KEYTYPE%/${SELECTION}/")\n
                                $(awk -F "=" '/\<service.key.management.keys.renew.more\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                            read RESPONSE;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                            echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

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

                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                                    sleep "${MESSAGE_DELAY}"; reset; clear; exec ${MAIN_CLASS};

                                    return 0;
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

                        echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        ;;
                    *)
                        ## user did not provide a yes/no answer
                        unset CONFIRM;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection provided is invalid";

                        echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        ;;
                esac
                ;;
            [Xx]|[Qq]|[Cc])
                ## user has requested to cancel
                reset; clear;

                unset SELECTION;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Key management request canceled.";

                echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

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

                echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

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
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    if [ ! -z "${AVAILABLE_MASTER_SERVERS[@]}" ] && [ ${#AVAILABLE_MASTER_SERVERS[@]} -ne 0 ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validation complete. Requesting target..";

        ## we have a set of slave servers that we can operate against
        while true
        do
            reset; clear;

            echo "\t$(awk -F "=" '/\<service.control.target.roleswap\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

            for AVAILABLE_HOST in ${AVAILABLE_MASTER_SERVERS[@]}
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "AVAILABLE_HOST -> ${AVAILABLE_HOST}";

                echo "${AVAILABLE_HOST}\n";
            done

            echo "$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

            read SWAP_SYSTEM;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SWAP_SYSTEM -> ${SWAP_SYSTEM}";

            reset; clear;

            echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

            ## value must be either alpha, numeric, or alphanumeric
            case ${SWAP_SYSTEM} in
                [Xx]|[Qq]|[Cc])
                    ## request to cancel
                    unset SWAP_SYSTEM;

                    reset; clear;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Role swap request canceled.";

                    echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

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

                        echo "$(awk -F "=" '/\<target.nameserver.matches.existing\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

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

                        echo "$(awk -F "=" '/\<target.nameserver.matches.existing\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Target validated. Requesting change information..";

                    typeset THIS_CNAME=${CNAME};
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                    ## validate the input
                    ${APP_ROOT}/${BIN_DIRECTORY}/obtainChangeControl.sh;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    if [[ ! -z "${CANCEL_REQ}" && "${CANCEL_REQ}" = "${_TRUE}" ]]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                        echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

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

                        echo "
                            $(awk -F "=" '/\<service.confirm.roleswap.target.master\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%MASTER_SERVER%/${SWAP_SYSTEM}/")
                            $(awk -F "=" '/\<service.confirm.roleswap.target.slave\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%SLAVE_SERVER%/${NAMED_MASTER}/")
                            $(awk -F "=" '/\<confirm.request\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                        read CONFIRM;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONFIRM -> ${CONFIRM}";

                        reset; clear;

                        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                        case ${CONFIRM} in
                            [Yy][Ee][Ss]|[Yy])
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Roleswap confirmed. Processing..";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing runRoleSwap.sh -s ${NAMED_MASTER} -t ${SWAP_SYSTEM} -c ${CHANGE_CONTROL} -i ${IUSER_AUDIT} -e ..";

                                typeset THIS_CNAME=${CNAME};
                                unset METHOD_NAME;
                                unset CNAME;

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                                ## validate the input
                                ${PLUGIN_LIB_DIRECTORY}/runRoleSwap.sh -s ${NAMED_MASTER} -t ${SWAP_SYSTEM} -c ${CHANGE_CONTROL} -i ${IUSER_AUDIT} -e;
                                typeset -i RET_CODE=${?};

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                CNAME="${THIS_CNAME}";
                                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                                then
                                    [ -z "${RET_CODE} ] && echo "$(awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";
                                    [ ! -z "${RET_CODE} ] && echo "$(awk -F "=" "/\<${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                    unset RET_CODE;
                                    unset SUCCESSFUL_ROLE_SWAP;
                                    unset CONFIRM;
                                    unset CHANGE_CONTROL;
                                    unset SWAP_SYSTEM;
                                    unset SELECTION;

                                    sleep "${MESSAGE_DELAY}"; reset; main;
                                fi

                                ## swap succeeded. now we must modify our typeset configuration.
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Role swap completed successfully. Now modifying typeset configuration..";

                                reset; clear;

                                echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                typeset THIS_CNAME=${CNAME};
                                unset METHOD_NAME;
                                unset CNAME;

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                                ## validate the input
                                ${PLUGIN_LIB_DIRECTORY}/runRoleSwap.sh -l -e;
                                typeset -i RET_CODE=${?};

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                CNAME="${THIS_CNAME}";
                                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ] && echo "$(awk -F "=" '/\<roleswap.local.config.mod.failed\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";
                                [ ${RET_CODE} -eq 0 ] && echo "$(awk -F "=" '/\<service.roleswap.complete\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

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

                                echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";
                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                ;;
                            *)
                                unset CONFIRM;

                                reset; clear;

                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                ;;
                        esac
                    done
                    ;;
                [Xx]|[Qq]|[Cc])
                    unset SWAP_SYSTEM;

                    reset; clear;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Role swap request canceled.";

                    echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                    ;;
                *)
                    unset SWAP_SYSTEM;

                    reset; clear;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                    echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

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

        echo "$(awk -F "=" '/\<no.available.target.systems\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
        sleep "${MESSAGE_DELAY}"; reset; clear; main;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return 0;
}

function rndcKeyfileGeneration
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";


    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

    return 1;
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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

return 0;
