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

## Application constants
[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

[[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

unset METHOD_NAME;
unset CNAME;

## check security
. ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/security/check_main.sh > /dev/null 2>&1;
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

    if [ ! -z "${IS_DNS_RECORD_MOD_ENABLED}" ] && [ "${IS_DNS_RECORD_MOD_ENABLED}" = "${_FALSE}" ]
    then
        $(${LOGGER} "ERROR" $METHOD_NAME ${CNAME} ${LINENO} "DNS zone modification has not been enabled. Cannot continue.");

        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/request.not.authorized/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

        exec ${APP_ROOT}/${MAIN_CLASS};

        exit 0;
    fi

    while true
    do
        reset; clear;

        print "\n";
        print "\t\t+-------------------------------------------------------------------+";
        print "\t\t               WELCOME TO \E[0;31m $(sed -e '/^ *#/d;s/#.*//' | awk -F "=" '/plugin.application.title/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g') \033[0m";
        print "\t\t+-------------------------------------------------------------------+";
        print "";
        print "\t\tSystem Type         : \E[0;36m ${SYSTEM_HOSTNAME} \033[0m";
        print "\t\tSystem Uptime       : \E[0;36m ${SYSTEM_UPTIME} \033[0m";
        print "\t\tUser                : \E[0;36m ${IUSER_AUDIT} \033[0m";
        print "";
        print "\t\t+-------------------------------------------------------------------+";
        print "";
        print "\t\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.available.options/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/update.zone.remove.record/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/update.zone.decom.zone/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/update.zone.decom.bu/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/update.zone.remove.zone/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/update.zone.remove.bu/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.option.cancel/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

        ## get the requested project code/url or business unit
        read MAINTENANCE_TYPE;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MAINTENANCE_TYPE -> ${MAINTENANCE_TYPE}";

        case ${MAINTENANCE_TYPE} in
            [Xx]|[Qq]|[Cc])
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.request.canceled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                ## unset SVC_LIST, we dont need it now
                unset SVC_LIST;

                ## terminate this thread and return control to main
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                exec ${APP_ROOT}/${MAIN_CLASS};

                exit 0;
                ;;
            1)
                ## remove a record from an existing zone
                ## not yet functional
                ## NOTE: this probably wont work well on dnssec-signed zones.. havent tried
                ## so not really sure. zone will at a minimum probably need to be re-signed.
                ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Function not currently available.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.function.not.available/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                ## unset SVC_LIST, we dont need it now
                unset MAINTENANCE_TYPE;

                ## exit this method and send to main
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
            2)
                ## decommission a zone
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone decommission requested. Processing..";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                ## unset SVC_LIST, we dont need it now
                MAINTENANCE_TYPE=site_decom;

                ## exit this method and send to main
                retrieveSiteInfo;
                ;;
            3)
                ## decomssion a business unit
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Business Unit decommission requested. Processing..";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                ## unset SVC_LIST, we dont need it now
                MAINTENANCE_TYPE=bu_decom;

                ## exit this method and send to main
                retrieveSiteInfo;
                ;;
            4)
                ## remove a zone
                ## must have already been decommissioned
                ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Function not currently available.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.function.not.available/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                ## unset SVC_LIST, we dont need it now
                unset MAINTENANCE_TYPE;

                ## exit this method and send to main
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
            5)
                ## remove a business unit
                ## must have already been decommissioned
                ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Function not currently available.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.function.not.available/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                ## unset SVC_LIST, we dont need it now
                unset MAINTENANCE_TYPE;

                ## exit this method and send to main
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done


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
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    reset; clear;

    ## get the request information
    while true
    do
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.request.info/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.pcode.format/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.bu.format/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.url.format/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.option.cancel/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

        ## get the requested project code/url or business unit
        read SVC_LIST;

        case ${SVC_LIST} in
            [Xx]|[Qq]|[Cc])
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.request.canceled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

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

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/selection.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                else
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Read in SVC_LIST -> ${SVC_LIST}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

                    reset; clear;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                    ## temporarily unset stuff
                    unset METHOD_NAME;
                    unset CNAME;

                    ## validate the input
                    . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validate_service_request.sh -s ${SVC_LIST} -e;
                    RET_CODE=${?};

                     ## reset METHOD_NAME back to THIS method
                    local METHOD_NAME="${CNAME}#${0}";
                    CNAME="$(basename "${0}")";

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from call: ${RET_CODE}";

                    if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]]
                    then
                        [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g' -e 's/ *$//g');";
                        [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/99/{print \$2}" | sed -e 's/^ *//g' -e 's/ *$//g');";

                        ## unset variables
                        unset RET_CODE;
                        unset SVC_LIST;

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    else
                        SVC_REQUEST_TYPE=$(echo ${SVC_LIST} | cut -d "," -f 1);
                        SVC_REQUEST_OPTION=$(echo ${SVC_LIST} | cut -d "," -f 2);
                        ## unset this RET_CODE
                        unset RET_CODE;

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/retrieveServiceInfo.sh ${INTERNET_TYPE_IDENTIFIER} ${SVC_LIST}";

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;

                        ## call out the service method to obtain
                        ## url information for the provided data
                        . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/retrieveServiceInfo.sh ${INTERNET_TYPE_IDENTIFIER} ${SVC_LIST};
                        RET_CODE=${?};

                        ## reset METHOD_NAME back to THIS method
                        local METHOD_NAME="${CNAME}#${0}";
                        CNAME="$(basename "${0}")";

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "retrieve_service executed..";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from call: ${RET_CODE}";

                        if [ ${RET_CODE} -eq 0 ]
                        then
                            ## we have all the data we need, do the failover
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Information for ${SVC_LIST} obtained..";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_DETAIL -> ${SERVICE_DETAIL[@]}";

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
                                    print "\t\t\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.application.title/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                                    print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.list.available/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                                    while [ ${A} -ne ${LIST_DISPLAY_MAX} ]
                                    do
                                        if [ ! ${B} -eq ${#SERVICE_DETAIL[@]} ]
                                        then
                                            ## prints the following:
                                            ## 0 - db.example.XMPL - Live in: PH - Site URL: example.com
                                            ## 7-8 for prd
                                            print "${B} - $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 1 | cut -d "/" -f 7-8) - Live in: $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 2) - Site URL: $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 3)";
                                            (( A += 1 ));
                                            (( B += 1 ));
                                        else
                                            B=${#SERVICE_DETAIL[@]};
                                            A=${LIST_DISPLAY_MAX};
                                        fi
                                    done

                                    if [ $(expr ${B} - ${LIST_DISPLAY_MAX}) -eq 0 ]
                                    then
                                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.display.next/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                                    else
                                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.display.prev/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.display.next/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                                    fi

                                    ## add the option to run against all sites listed
                                    print "A - $(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.display.next/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g' -e "s/%SITE%/all sites for ${SVC_REQUEST_OPTION}/")\n";
                                    print "C - $(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.option.cancel/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                                    read SELECTION;

                                    case ${SELECTION} in
                                        [Nn])
                                            clear;
                                            unset SELECTION;

                                            if [ ${B} -ge ${#SERVICE_DETAIL[@]} ]
                                            then
                                                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/forward.shift.failed/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
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
                                                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/previous.shift.failed/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

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
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    processDecomRequest;
                                                    ;;
                                                bu_removal|site_removal)
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    process_removal_request;
                                                    ;;
                                                remove_entry)
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    remove_zone_entry
                                                    ;;
                                            esac
                                            ;;
                                        [Xx]|[Qq]|[Cc])
                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";
                                            ## make b 0 again
                                            A=0; B=0;

                                            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.request.canceled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                            ;;
                                        *)
                                            clear;
                                            A=0; B=0;

                                            unset SELECTION;

                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                            print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/selection.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            ;;
                                    esac
                                done
                            else
                                while true
                                do
                                    print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.list.available/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                                    while [ ${A} -ne ${#SERVICE_DETAIL[@]} ]
                                    do
                                        ## prints the following:
                                        ## 0 - db.example.XMPL - Live in: PH - Site URL: example.com
                                        ## 7-8 for prd
                                        print "${A} - $(echo "${SERVICE_DETAIL[${A}]}" | cut -d "|" -f 1 | cut -d "/" -f 7-8) - Live in: $(echo "${SERVICE_DETAIL[${A}]}" | cut -d "|" -f 2) - Site URL: $(echo "${SERVICE_DETAIL[${A}]}" | cut -d "|" -f 3)";
                                        (( A += 1 ));
                                    done

                                    ## add the option to run against all sites listed
                                    [ ${#SERVICE_DETAIL[@]} -ge 1 ] && print "A - $(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.display.next/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g' -e "s/%SITE%/all sites for ${SVC_REQUEST_OPTION}/")\n";
                                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.option.cancel/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                                    read SELECTION;

                                    case ${SELECTION} in
                                        [0-${A}]|[Aa]*)
                                            reset; clear;

                                            A=0; B=0;

                                            ## TODO: make this call the appropriate method
                                            case ${MAINTENANCE_TYPE} in
                                                bu_decom|site_decom)
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    processDecomRequest;
                                                    ;;
                                                bu_removal|site_removal)
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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

                                                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/selection.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                    else
                                                        ## ok keep going
                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                        remove_zone_entry;
                                                    fi
                                                    ;;
                                            esac
                                            ;;
                                        [Xx]|[Qq]|[Cc])
                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";

                                            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.request.canceled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                                            A=0; B=0;

                                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                            ;;
                                        *)
                                            unset SELECTION;

                                            clear;

                                            A=0; B=0;

                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                            print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/selection.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            ;;
                                    esac
                                done
                            fi
                        else
                            ## result code was non-zero from retrieve_service_info
                            ## return message to user
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from retrieve_service->${RET_CODE}";

                            [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g' -e 's/ *$//g');";
                            [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/99/{print \$2}" | sed -e 's/^ *//g' -e 's/ *$//g');";

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
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    reset; clear;

    while true
    do
        print "\t\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/confirm.request/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

        read CONFIRM;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONFIRM -> ${CONFIRM}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

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
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting change information..";

                    ${PLUGIN_ROOT_DIR}/${BIN_DIRECTORY}/obtainChangeControl.sh;

                    if [[ ! -z "${CANCEL_REQ}" && "${CANCEL_REQ}" = "${_TRUE}" ]]
                    then
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.request.canceled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                        ## unset SVC_LIST, we dont need it now
                        unset SVC_LIST;

                        ## terminate this thread and return control to main
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;

                        sleep ${MESSAGE_DELAY}; reset; clear; main;
                    fi

                    break;
                done

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting up required flags..";

                if [[ "${SELECTION}" = "A" || "${SELECTION}" = "a" ]]
                then
                    ## user has selected all zones that were returned. this should all be a single business unit, so we process a BU decom
                    ## TODO: call the right runner
                    unset DC_MISMATCH;
                    unset SVC_REQUEST_OPTION;
                    unset SVC_REQUEST_TYPE;

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BU -> ${BU}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TDC -> ${TDC}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_CONTROL -> ${CHANGE_CONTROL}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command run_decom.sh -b $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 3),${BU},${CHANGE_CONTROL},${IUSER_AUDIT} -e";

                    ## temporarily unset stuff
                    unset METHOD_NAME;
                    unset CNAME;

                    . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/run_decom.sh -b ${BU},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                else
                    ## user has selected a single zone. call out to the right runner to perform the work
                    BU=$(echo ${SERVICE_DETAIL[${SELECTION}]} | cut -d "/" -f 6 | cut -d "_" -f 3);
                    PCODE=$(echo ${SERVICE_DETAIL[${SELECTION}]} | cut -d "/" -f 7 | cut -d "|" -f 1 | cut -d "." -f 3);
                    ZNAME=$(echo ${SERVICE_DETAIL[${SELECTION}]} | cut -d "|" -f 3)

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BU -> ${BU}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FNAME -> ${FNAME}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PCODE -> ${PCODE}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TDC -> ${TDC}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_CONTROL -> ${CHANGE_CONTROL}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command run_decom.sh -s ${BU},${PCODE},${ZNAME},${CHANGE_CONTROL},${IUSER_AUDIT} -e";

                    ## temporarily unset stuff
                    unset METHOD_NAME;
                    unset CNAME;

                    . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/run_decom.sh -s ${BU},${PCODE},${ZNAME},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                fi

                ## capture the return code
                RET_CODE=${?};

                ## reset METHOD_NAME back to THIS method
                local METHOD_NAME="${CNAME}#${0}";
                CNAME="$(basename "${0}")";

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "run_decom executed..";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from call: ${RET_CODE}";

                if [ ${RET_CODE} -eq 0 ]
                then
                    ## ok, our master went through just fine, loop through and process slaves (if any)
                    if [[ ! -z "${ERROR_COUNT}" && ${ERROR_COUNT} != 0 ]]
                    then
                        ## one or more slave services failed processing
                        if [ ! -z "${FAILED_SERVERS}" ]
                        then
                            print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}failover.request.servers.success.failure  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                            ## make sure d is zero
                            D=0;

                            while [ ${D} -ne ${#FAILED_SERVERS[@]} ]
                            do
                                print "${FAILED_SERVERS[${D}]}\n";
                                (( D += 1 ));
                            done

                            ## make d zero again
                            D=0;
                        fi

                        sleep "${MESSAGE_DELAY}";
                    fi

                    ## we're all set here. audit log it
                    if [[ "${SELECTION}" = "A" || "${SELECTION}" = "a" ]]
                    then
                        ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: All sites in ${SVC_REQUEST_OPTION} - Change Request: ${CHANGE_CONTROL} - Switched To: ${TDC}";
                    else
                        ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: $(echo ${SERVICE_DETAIL[${SELECTION}]} | cut -d "/" -f 9) - Change Request: ${CHANGE_CONTROL} - Switched To: ${TDC}";
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

                        print "\t\t\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.application.title  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                        print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.process.successful ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%PROCESS%/decommission/")";
                        print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.process.perform.another ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%PROCESS%/decommission/")\n";

                        read RESPONSE;
                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                        case ${RESPONSE} in
                            [Yy][Ee][Ss]|[Yy])
                                ## user has elected to perform further failovers. restart the process
                                unset RESPONSE;

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Transferring control back to main..";

                                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                ;;
                            *)
                                ## user does not wish to process further failovers. let's exit out and open up the main class.
                                unset RESPONSE;

                                sleep "${MESSAGE_DELAY}"; reset; clear; exec ${APP_ROOT}/${MAIN_CLASS};
                                exit 0;
                                ;;
                        esac
                    done
                else
                    ## caught an error, log it out and
                    ## show it
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Caught an error performing the failover. RET_CODE -> ${RET_CODE}";

                    [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/99/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                    [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g' -e 's/ *$//g')\n";

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
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";

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

#===  FUNCTION  ===============================================================
#          NAME:  remove_zone_entry
#   DESCRIPTION:  Removes an entry from an existing zone
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function removeZoneEntry
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";


    return 0;
}

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

main;


return 0;
