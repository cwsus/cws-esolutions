#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  zoneUpdateUI.sh
#         USAGE:  ./zoneUpdateUI.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Allows processing of zone/business unit updates, such as
#                 add/remove or change records within a zone file or
#                 decommission/removal of a zone/business unit
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

    if [ ! -z "${IS_DNS_RECORD_MOD_ENABLED}" ] && [ "${IS_DNS_RECORD_MOD_ENABLED}" = "${_FALSE}" ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS zone modification has not been enabled. Cannot continue.";

        echo "$(awk -F "=" '/\<request.not.authorized\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

        sleep ${MESSAGE_DELAY}; reset; clear; exec ${MAIN_CLASS};

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
            \t$(awk -F "=" '/\<update.zone.remove.record\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<update.zone.decom.zone\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<update.zone.decom.bu\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<update.zone.remove.zone\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<update.zone.remove.bu\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        ## get the requested project code/url or business unit
        read MAINTENANCE_TYPE;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MAINTENANCE_TYPE -> ${MAINTENANCE_TYPE}";

        case ${MAINTENANCE_TYPE} in
            [Xx]|[Qq]|[Cc])
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## unset SVC_LIST, we dont need it now
                unset SVC_LIST;

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                exec ${MAIN_CLASS};

                return 0;
                ;;
            1)
                ## remove a record from an existing zone
                ## not yet functional
                ## NOTE: this probably wont work well on dnssec-signed zones.. havent tried
                ## so not really sure. zone will at a minimum probably need to be re-signed.
                ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Function not currently available.";

                echo "$(awk -F "=" '/\<system.function.not.available\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## unset SVC_LIST, we dont need it now
                unset MAINTENANCE_TYPE;

                ## exit this method and send to main
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
            2)
                ## decommission a zone
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone decommission requested. Processing..";

                echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## unset SVC_LIST, we dont need it now
                MAINTENANCE_TYPE="site_decom";

                ## exit this method and send to main
                retrieveSiteInfo;
                ;;
            3)
                ## decomssion a business unit
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Business Unit decommission requested. Processing..";

                echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## unset SVC_LIST, we dont need it now
                MAINTENANCE_TYPE="bu_decom";

                ## exit this method and send to main
                retrieveSiteInfo;
                ;;
            4)
                ## remove a zone
                ## must have already been decommissioned
                ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Function not currently available.";

                echo "$(awk -F "=" '/\<system.function.not.available\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## unset SVC_LIST, we dont need it now
                unset MAINTENANCE_TYPE;

                ## exit this method and send to main
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
            5)
                ## remove a business unit
                ## must have already been decommissioned
                ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Function not currently available.";

                echo "$(awk -F "=" '/\<system.function.not.available\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## unset SVC_LIST, we dont need it now
                unset MAINTENANCE_TYPE;

                ## exit this method and send to main
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  retrieveSiteInfo
#   DESCRIPTION:  Requests for information regarding the zone or business unit
#                 desired, and then performs a search against the configured
#                 master nameserver based on the information provided. Reacts
#                 according to response from search
#    PARAMETERS:  None
#       RETURNS:  None
#==============================================================================
function retrieveSiteInfo
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    reset; clear;

    ## get the request information
    while true
    do
        echo "
            \t$(awk -F "=" '/\<failover.request.info\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<failover.pcode.format\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<failover.bu.format\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<failover.url.format\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        ## get the requested project code/url or business unit
        read SVC_LIST;

        case ${SVC_LIST} in
            [Xx]|[Qq]|[Cc])
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                ## unset SVC_LIST, we dont need it now
                unset SVC_LIST;
                unset MAINTENANCE_TYPE;

                ## exit this method and send to main
                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                ;;
            *)
                if [ -z "${SVC_LIST}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                    echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                else
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Read in SVC_LIST -> ${SVC_LIST}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

                    reset; clear;

                    echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                    typeset THIS_CNAME=${CNAME};
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/validators/validate_service_request.sh -s ${SVC_LIST} -e;
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from call: ${RET_CODE}";

                    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                    then
                        [ ! -z "${RET_CODE}" ] && echo "$(awk -F "=" "/${RET_CODE}/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";
                        [ -z "${RET_CODE}" ] && echo "$(awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                        ## unset variables
                        unset RET_CODE;
                        unset SVC_LIST;

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    else
                        SVC_REQUEST_TYPE=$(cut -d "," -f 1 <<< ${SVC_LIST});
                        SVC_REQUEST_OPTION=$(cut -d "," -f 2 <<< ${SVC_LIST});

                        ## unset this RET_CODE
                        unset RET_CODE;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${PLUGIN_LIB_DIRECTORY}/retrieveServiceInfo.sh ${INTERNET_TYPE_IDENTIFIER} ${SVC_LIST}";

                        typeset THIS_CNAME=${CNAME};
                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                        ## validate the input
                        ${PLUGIN_LIB_DIRECTORY}/retrieveServiceInfo.sh ${INTERNET_TYPE_IDENTIFIER} ${SVC_LIST};
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "retrieve_service executed..";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from call: ${RET_CODE}";

                        if [ ${RET_CODE} -eq 0 ]
                        then
                            ## we have all the data we need, do the failover
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Information for ${SVC_LIST} obtained..";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_DETAIL -> ${SERVICE_DETAIL[@]}";

                            ## unset this RET_CODE
                            unset RET_CODE;
                            unset SVC_LIST;

                            reset; clear;

                            ## write out the data retrieved so the user
                            ## can make an informed decision
                            if [ ${#SERVICE_DETAIL[@]} -gt ${LIST_DISPLAY_MAX} ]
                            then
                                while true
                                do
                                    echo "\t$(awk -F "=" '/\<system.list.available\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                    while [ ${A} -ne ${LIST_DISPLAY_MAX} ]
                                    do
                                        if [ ! ${B} -eq ${#SERVICE_DETAIL[@]} ]
                                        then
                                            ## prints the following:
                                            ## 0 - db.example.XMPL - Live in: PH - Site URL: example.com
                                            ## 7-8 for prd
                                            echo "${B} - $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 1 | cut -d "/" -f 7-8) - Live in: $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 2) - Site URL: $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 3)";
                                            (( A += 1 ));
                                            (( B += 1 ));
                                        else
                                            B=${#SERVICE_DETAIL[@]};
                                            A=${LIST_DISPLAY_MAX};
                                        fi
                                    done

                                    if [ $(expr ${B} - ${LIST_DISPLAY_MAX}) -eq 0 ]
                                    then
                                        echo "$(awk -F "=" '/\<system.display.next\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";
                                    else
                                        echo "
                                            $(awk -F "=" '/\<system.display.prev\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n
                                            $(awk -F "=" '/\<system.display.next\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";
                                    fi

                                    ## add the option to run against all sites listed
                                    echo "
                                        A - $(awk -F "=" '/\<system.display.next\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%SITE%/all sites for ${SVC_REQUEST_OPTION}/")\n
                                        C - $(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                    read SELECTION;

                                    case ${SELECTION} in
                                        [Nn])
                                            clear;
                                            unset SELECTION;

                                            if [ ${B} -ge ${#SERVICE_DETAIL[@]} ]
                                            then
                                                echo "$(awk -F "=" '/\<forward.shift.failed\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";
                                                A=0;
                                                B=0;
                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            else
                                                A=0;
                                                continue;
                                            fi
                                            ;;
                                        [Pp])
                                            clear;
                                            unset SELECTION;

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
                                        [0-${B}]*)
                                            ## make b 0 again
                                            A=0;
                                            B=0;
                                            reset; clear;

                                            case ${MAINTENANCE_TYPE} in
                                                bu_decom|site_decom)
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    processDecomRequest;
                                                    ;;
                                                bu_removal|site_removal)
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    process_removal_request;
                                                    ;;
                                                remove_entry)
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    remove_zone_entry
                                                    ;;
                                            esac
                                            ;;
                                        [Xx]|[Qq]|[Cc])
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";
                                            ## make b 0 again
                                            A=0; B=0;

                                            echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                            ;;
                                        *)
                                            clear;
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
                                    echo "\t$(awk -F "=" '/\<system.list.available\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                    while [ ${A} -ne ${#SERVICE_DETAIL[@]} ]
                                    do
                                        ## prints the following:
                                        ## 0 - db.example.XMPL - Live in: PH - Site URL: example.com
                                        ## 7-8 for prd
                                        echo "${A} - $(echo "${SERVICE_DETAIL[${A}]}" | cut -d "|" -f 1 | cut -d "/" -f 7-8) - Live in: $(echo "${SERVICE_DETAIL[${A}]}" | cut -d "|" -f 2) - Site URL: $(echo "${SERVICE_DETAIL[${A}]}" | cut -d "|" -f 3)";
                                        (( A += 1 ));
                                    done

                                    ## add the option to run against all sites listed
                                    [ ${#SERVICE_DETAIL[@]} -ge 1 ] && echo "A - $(awk -F "=" '/\<system.display.next\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%SITE%/all sites for ${SVC_REQUEST_OPTION}/")\n";
                                    echo "$(awk -F "=" '/\<system.option.cancel\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                                    read SELECTION;

                                    case ${SELECTION} in
                                        [0-${A}]|[Aa]*)
                                            reset; clear;

                                            A=0; B=0;

                                            ## TODO: make this call the appropriate method
                                            case ${MAINTENANCE_TYPE} in
                                                bu_decom|site_decom)
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    processDecomRequest;
                                                    ;;
                                                bu_removal|site_removal)
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    process_removal_request;
                                                    ;;
                                                remove_entry)
                                                    ## remove is not a valid option when selecting ALL sites
                                                    ## check to be sure
                                                    if [[ ${SELECTION} == [Aa] ]]
                                                    then
                                                        ## invalid selection
                                                        unset SELECTION;

                                                        clear;

                                                        A=0; B=0;

                                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                                        echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                    else
                                                        ## ok keep going
                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                        remove_zone_entry;
                                                    fi
                                                    ;;
                                            esac
                                            ;;
                                        [Xx]|[Qq]|[Cc])
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";

                                            echo "$(awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                            A=0; B=0;

                                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                            ;;
                                        *)
                                            unset SELECTION;

                                            clear;

                                            A=0; B=0;

                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                            echo "$(awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            ;;
                                    esac
                                done
                            fi
                        else
                            ## result code was non-zero from retrieve_service_info
                            ## return message to user
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from retrieve_service->${RET_CODE}";

                            [ ! -z "${RET_CODE}" ] && echo "$(awk -F "=" "/${RET_CODE}/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";
                            [ -z "${RET_CODE}" ] && echo "$(awk -F "=" "/99/{print $2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                            ## unset our variables
                            unset SVC_LIST;
                            unset RET_CODE;
                            unset BU;
                            unset FNAME;
                            unset PCODE;
                            unset TDC;
                            unset CHANGE_CONTROL;
                            unset SVC_REQUEST_TYPE;
                            unset SVC_REQUEST_OPTION;

                            ## reload
                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        fi
                    fi
                fi
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  processDecomRequest
#   DESCRIPTION:  Processes a decommission request using the information
#                 previously provided. Determines, based on flags, if the decom
#                 should be performed for a single zone or an entire business
#                 unit. Executes first on the configured master, if successful,
#                 executes on configured slaves (if any)
#    PARAMETERS:  None
#       RETURNS:  None
#==============================================================================
function processDecomRequest
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    reset; clear;

    while true
    do
        echo "\t\t$(awk -F "=" '/\<confirm.request\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        read CONFIRM;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONFIRM -> ${CONFIRM}";

        reset; clear;

        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

        case ${CONFIRM} in
            [Yy][Ee][Ss]|[Yy])
                ## unset confirmation
                unset CONFIRM;

                ## we need to process the actual failover here for the requested
                ## site. this is going to require the following information:
                ##
                ## 1. the site
                ## 2. the current datacenter (so we know where to go)
                ## 3. the change/ticket number
                ## 4. the logged in user-name
                ##
                ## all of these will be used to make the actual change,
                ## while 3/4 are also used for auditing
                while true
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting change information..";

                    typeset THIS_CNAME=${CNAME};
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

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

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting up required flags..";

                if [[ "${SELECTION}" = "A" || "${SELECTION}" = "a" ]]
                then
                    ## user has selected all zones that were returned. this should all be a single business unit, so we process a BU decom
                    ## TODO: call the right runner
                    unset DC_MISMATCH;
                    unset SVC_REQUEST_OPTION;
                    unset SVC_REQUEST_TYPE;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BU -> ${BU}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TDC -> ${TDC}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_CONTROL -> ${CHANGE_CONTROL}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command run_decom.sh -b $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 3),${BU},${CHANGE_CONTROL},${IUSER_AUDIT} -e";

                    typeset THIS_CNAME=${CNAME};
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/run_decom.sh -b ${BU},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";
                else
                    ## user has selected a single zone. call out to the right runner to perform the work
                    BU=$(cut -d "/" -f 6 <<< ${SERVICE_DETAIL[${SELECTION}]} | cut -d "_" -f 3);
                    PCODE=$(cut -d "/" -f 7 <<< ${SERVICE_DETAIL[${SELECTION}]} | cut -d "|" -f 1 | cut -d "." -f 3);
                    ZNAME=$(cut -d "|" -f 3 <<< ${SERVICE_DETAIL[${SELECTION}]})

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BU -> ${BU}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FNAME -> ${FNAME}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PCODE -> ${PCODE}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TDC -> ${TDC}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_CONTROL -> ${CHANGE_CONTROL}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command run_decom.sh -s ${BU},${PCODE},${ZNAME},${CHANGE_CONTROL},${IUSER_AUDIT} -e";

                    typeset THIS_CNAME=${CNAME};
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/run_decom.sh -s ${BU},${PCODE},${ZNAME},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "run_decom executed..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from call: ${RET_CODE}";

                if [ ${RET_CODE} -eq 0 ]
                then
                    ## ok, our master went through just fine, loop through and process slaves (if any)
                    if [[ ! -z "${ERROR_COUNT}" && ${ERROR_COUNT} -ne 0 ]]
                    then
                        ## one or more slave services failed processing
                        if [ ! -z "${FAILED_SERVERS}" ]
                        then
                            echo "$(awk -F "=" '/\<failover.request.servers.success.failure\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')";

                            ## make sure d is zero
                            D=0;

                            while [ ${D} -ne ${#FAILED_SERVERS[@]} ]
                            do
                                echo "${FAILED_SERVERS[${D}]}\n";

                                (( D += 1 ));
                            done

                            ## make d zero again
                            D=0;
                        fi

                        sleep "${MESSAGE_DELAY}";
                    fi

                    ## we're all set here. "AUDIT" log it
                    if [[ "${SELECTION}" = "A" || "${SELECTION}" = "a" ]]
                    then
                        ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: All sites in ${SVC_REQUEST_OPTION} - Change Request: ${CHANGE_CONTROL} - Switched To: ${TDC}";
                    else
                        ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: $(cut -d "/" -f 9 <<< ${SERVICE_DETAIL[${SELECTION}]}) - Change Request: ${CHANGE_CONTROL} - Switched To: ${TDC}";
                    fi

                    ## unset variables
                    unset SVC_REQUEST_OPTION;
                    unset SVC_REQUEST_TYPE;
                    unset SVC_LIST;
                    unset SELECTION;
                    unset CONFIRM;
                    unset DC_MISMATCH;
                    unset BU;
                    unset FNAME;
                    unset PCODE;
                    unset RET_CODE;
                    unset TDC;
                    unset CHANGE_CONTROL;
                    unset RETURN_CODE;
                    unset ZNAME;

                    ## TODO: put in slave processing here
                    ## we've finished our processing. site(s) requested have been failed over,
                    ## reloaded and verified. what should we do now ?
                    while true
                    do
                        reset; clear;

                        echo "
                            $(awk -F "=" '/\<system.process.successful\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
                            $(awk -F "=" '/\<system.process.perform.another\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                        read RESPONSE;
                        echo "$(awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                        case ${RESPONSE} in
                            [Yy][Ee][Ss]|[Yy])
                                ## user has elected to perform further failovers. restart the process
                                unset RESPONSE;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Transferring control back to main..";

                                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                ;;
                            *)
                                ## user does not wish to process further failovers. let's exit out and open up the main class.
                                unset RESPONSE;

                                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                ;;
                        esac
                    done
                else
                    ## caught an "ERROR", log it out and
                    ## show it
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Caught an "ERROR" performing the failover. RET_CODE -> ${RET_CODE}";

                    [ -z "${RET_CODE}" ] && echo "$(awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";
                    [ ! -z "${RET_CODE}" ] && echo "$(awk -F "=" "/${RET_CODE}/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')\n";

                    unset CHANGE_CONTROL;
                    unset RET_CODE;
                    unset SELECTION;
                    unset CONFIRM;
                    unset DC_MISMATCH;
                    unset BU;
                    unset FNAME;
                    unset PCODE;
                    unset SVC_LIST;
                    unset RETURN_CODE;
                    unset ZNAME;

                    ## break out
                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                fi
                ;;
            [Nn][Oo]|[Nn])
                unset CONFIRM;
                unset SELECTION;
                unset SVC_REQUEST_OPTION;
                unset SVC_REQUEST_TYPE;

                ## user opted to cancel
                ## we leave the in-use flag in place
                ## because we aren't starting over
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";

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
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  remove_zone_entry
#   DESCRIPTION:  Removes an entry from an existing zone
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function removeZoneEntry
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

return 0;
