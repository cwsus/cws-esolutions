#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  siteFailoverUI.sh
#         USAGE:  ./siteFailoverUI.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Processes a DNS failover by using information previously
#                 obtained by retrieve_site_info.sh
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

    reset; clear;

    ## get the request information
    while true
    do
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
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES}  | awk -F "=" '/failover.option.internet/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES}  | awk -F "=" '/failover.option.intranet/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.option.cancel/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

        ## get the requested project code/url or business unit
        read REQUEST_OPTION;

        case ${REQUEST_OPTION} in
            [Xx]|[Qq]|[Cc])
                ## user opted to cancel, remove the lockfile
                if [ -f ${APP_ROOT}/${APP_FLAG} ]
                then
                    rm -rf ${APP_ROOT}/${APP_FLAG};
                fi

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.request.canceled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                ## unset REQUEST_OPTION, we dont need it now
                unset REQUEST_OPTION;

                ## terminate this thread and return control to main
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                exec ${APP_ROOT}/${MAIN_CLASS} -c;

                exit 0;
                ;;
            1)
                unset REQUEST_OPTION;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Internet site failover requested. Transferring to requestInternetFailover ..";

                FAILOVER_TYPE=${INTERNET_TYPE_IDENTIFIER};

                reset; clear; requestInternetFailover;
                ;;
            2)
                unset REQUEST_OPTION;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Intranet site failover requested. Transferring to requestIntranetFailover ..";

                FAILOVER_TYPE=${INTRANET_TYPE_IDENTIFIER};

                reset; clear; requestIntranetFailover;
                ;;
            *)
                clear;

                unset REQUEST_OPTION;

                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/selection.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done
}

#===  FUNCTION  ===============================================================
#          NAME:  requestInternetFailover
#   DESCRIPTION:  Obtains information necessary to process an internet site
#                 failover
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
requestInternetFailover
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Continuing execution - this is our first run";

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
                ## user opted to cancel, remove the lockfile
                if [ -f ${APP_ROOT}/${APP_FLAG} ]
                then
                    rm -rf ${APP_ROOT}/${APP_FLAG};
                fi

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.request.canceled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                ## unset SVC_LIST, we dont need it now
                unset SVC_LIST;

                ## terminate this thread and return control to main
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;

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
                    . ${APP_ROOT}/${LIB_DIRECTORY}/validators/validate_service_request.sh -s ${SVC_LIST} -e;
                    RET_CODE=${?};

                    ## reset METHOD_NAME back to THIS method
                    local METHOD_NAME="${CNAME}#${0}";
                    CNAME="$(basename "${0}")";

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from call: ${RET_CODE}";

                    if [ ${RET_CODE} -ne 0 ]
                    then
                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g' -e 's/ *$//g');";

                        ## unset variables
                        unset RET_CODE;
                        unset SVC_LIST;

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    else
                        SVC_REQUEST_TYPE=$(echo ${SVC_LIST} | cut -d "," -f 1);
                        SVC_REQUEST_OPTION=$(echo ${SVC_LIST} | cut -d "," -f 2);
                        
                        ## unset this RET_CODE
                        unset RET_CODE;

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Obtaining information for ${SVC_LIST} from #retrieve_service";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/${LIB_DIRECTORY}/retrieveServiceInfo.sh ${INTERNET_TYPE_IDENTIFIER} ${SVC_LIST}";

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;

                        ## call out the service method to obtain
                        ## url information for the provided data
                        . ${APP_ROOT}/${LIB_DIRECTORY}/retrieveServiceInfo.sh ${INTERNET_TYPE_IDENTIFIER} ${SVC_LIST};
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
                                    print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.list.available/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                                    ## set up our domain match here. makes sense to do it one time instead of possibly several
                                    set -A DC_MATCH;

                                    while [ ${C} -ne ${#SERVICE_DETAIL[@]} ]
                                    do
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DC_MATCH -> $(echo ${SERVICE_DETAIL[${C}]} | cut -d "|" -f 2)";
                        
                                        set -A DC_MATCH "$(echo ${SERVICE_DETAIL[${C}]} | cut -d "|" -f 2)";
                        
                                        (( C += 1 ));
                                    done

                                    C=0;

                                    while [ ${A} -ne ${LIST_DISPLAY_MAX} ]
                                    do
                                        if [ ! ${B} -eq ${#SERVICE_DETAIL[@]} ]
                                        then
                                            ## prints the following:
                                            ## 0 - db.example.XMPL - Live in: PH - Site URL: example.com
                                            ## 7-8 for prd
                                            print "${B} - $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 1 | cut -d "/" -f 7-8) - Live in: $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 2) - Site URL: $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 3)";

                                            (( A += 1 )); (( B += 1 ));
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
                                    print "A - $(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.option.all/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                                    print "C - $(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.option.cancel/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                                    read SELECTION;

                                    case ${SELECTION} in
                                        [Nn])
                                            clear;
                                            unset SELECTION;

                                            if [ ${B} -ge ${#SERVICE_DETAIL[@]} ]
                                            then
                                                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/forward.shift.failed/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                                                A=0; B=0;

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
                                            A=0; B=0;

                                            reset; clear; processFailoverRequest;
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
                                ## set up our domain match here. makes sense to do it one time instead of possibly several
                                set -A DC_MATCH;

                                while [ ${C} -ne ${#SERVICE_DETAIL[@]} ]
                                do
                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DC_MATCH -> $(echo ${SERVICE_DETAIL[${C}]} | cut -d "|" -f 2)";

                                    set -A DC_MATCH "$(echo ${SERVICE_DETAIL[${C}]} | cut -d "|" -f 2)";

                                    (( C += 1 ));
                                done

                                C=0;

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
                                    [ ! ${#SERVICE_DETAIL[@]} -gt 1 ] && print "A - $(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.option.all/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                                    print "C - $(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.option.cancel/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                                    read SELECTION;

                                    case ${SELECTION} in
                                        [0-${A}]|[Aa]*)
                                            reset; clear;
                                            A=0; B=0;

                                            processFailoverRequest;
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
}

#===  FUNCTION  ===============================================================
#          NAME:  requestIntranetFailover
#   DESCRIPTION:  Obtains information necessary to process an intranet site
#                 failover
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function requestIntranetFailover
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    reset; clear;

    ## get the request information
    while true
    do
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.request.info/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.url.format/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.option.cancel/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

        ## get the requested project code/url or business unit
        read SVC_LIST;

        case ${SVC_LIST} in
            [Xx]|[Qq]|[Cc])
                ## user opted to cancel, remove the lockfile
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.request.canceled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                ## unset SVC_LIST, we dont need it now
                unset SVC_LIST;

                ## terminate this thread and return control to main
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;

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
                    . ${APP_ROOT}/${LIB_DIRECTORY}/validators/validate_service_request.sh -s ${SVC_LIST} -e;
                    RET_CODE=${?};

                    ## reset METHOD_NAME back to THIS method
                    local METHOD_NAME="${CNAME}#${0}";
                    CNAME="$(basename "${0}")";

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from call: ${RET_CODE}";

                    if [ ${RET_CODE} -ne 0 ]
                    then
                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g' -e 's/ *$//g');";

                        ## unset variables
                        unset RET_CODE;
                        unset SVC_LIST;

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    else
                        ## unset this RET_CODE
                        unset RET_CODE;

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Obtaining information for ${SVC_LIST} from #retrieve_service";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/${LIB_DIRECTORY}/retrieveServiceInfo.sh ${INTRANET_TYPE_IDENTIFIER} ${SVC_LIST}";

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;

                        ## call out the service method to obtain
                        ## url information for the provided data
                        . ${APP_ROOT}/${LIB_DIRECTORY}/retrieveServiceInfo.sh ${INTRANET_TYPE_IDENTIFIER} ${SVC_LIST};
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
                            if [ ! -z "ENABLE_POP" ] && [ ! -z "${DISABLE_POP}" ]
                            then
                                print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.list.available/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                                print "${SVC_LIST} - $(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.gd.enable.pop/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g'); - ${ENABLE_POP}";
                                print "${SVC_LIST} - $(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.gd.disable.pop/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g'); - ${DISABLE_POP}";

                                print "C - $(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/system.option.cancel/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                                read SELECTION;

                                case ${SELECTION} in
                                    [No][Oo]|[Nn])
                                        unset SELECTION;
                                        unset SVC_LIST;
                                        unset ENABLE_POP;
                                        unset DISABLE_POP;
                                        unset FAILOVER_TYPE;
                                        unset REQUEST_OPTION;

                                        clear; reset; clear; main;
                                        ;;
                                    [Yy][Ee][Ss]|[Yy])
                                        ## make b 0 again
                                        reset; clear; processFailoverRequest;
                                        ;;
                                    [Xx]|[Qq]|[Cc])
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";
                                        ## make b 0 again
                                        A=0;
                                        B=0;

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
                            else
                                ## no pop information was returned
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/pop.not.determined/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
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
}

#===  FUNCTION  ===============================================================
#          NAME:  processFailoverRequest
#   DESCRIPTION:  Obtains information necessary to process an intranet site
#                 failover
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function processFailoverRequest
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    case ${FAILOVER_TYPE} in
        ${INTRANET_TYPE_IDENTIFIER})
            print "\t\t\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.application.title  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
            print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.provide.changenum  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
            print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.emergency.changenum  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

            read CHANGE_CONTROL;

            reset; clear;

            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

            if [[ ${CHANGE_CONTROL} == [Ee] ]]
            then
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change specified as emergency..";
                CHANGE_CONTROL="E-$(date +"%m-%d-%Y_%H:%M:%S")";
            fi

            if [ $(${APP_ROOT}/${LIB_DIRECTORY}/validators/validate_change_ticket.sh ${CHANGE_CONTROL}) -ne 0 ]
            then
                ## change control provided was invalid
                ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A change was attempted with an invalid change order by ${IUSER_AUDIT}. Change request was ${CHANGE_CONTROL}.";
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid change control was provided. A valid change control number is required to process the request.";

                unset CHANGE_CONTROL;

                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/change.control.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
            else
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_CONTROL -> ${CHANGE_CONTROL}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runFailoverRequest.sh -r ${INTERNET_TYPE_IDENTIFIER} -s $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 3),${BU},${FNAME},${TDC},${PCODE},${CHANGE_CONTROL},${IUSER_AUDIT} -e";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;
                unset RETURN_CODE;

                ## remember the current indicator
                CURR_OPTIND=${OPTIND};

                . ${APP_ROOT}/${LIB_DIRECTORY}/runFailoverRequest.sh -r ${INTERNET_TYPE_IDENTIFIER} -s $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 3),${BU},${FNAME},${TDC},${PCODE},${CHANGE_CONTROL},${IUSER_AUDIT} -e;

                ## capture the return code
                RET_CODE=${?};

                ## reset optind and clear placeholder
                OPTIND=${CURR_OPTIND};
                unset CURR_OPTIND;

                ## reset METHOD_NAME back to THIS method
                local METHOD_NAME="${CNAME}#${0}";
                CNAME="$(basename "${0}")";

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "run_failover executed..";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from call: ${RET_CODE}";

                if [ ${RET_CODE} -eq 0 ]
                then
                    if [ ! -z "${ERROR_COUNT}" ] && [ ${ERROR_COUNT} != 0 ]
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
                    ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: ${SVC_LIST} - Change Request: ${CHANGE_CONTROL} - Switched To: ${ENABLE_POP}";
                else
                    ## caught an error, log it out and
                    ## show it
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Caught an error performing the failover->${RET_CODE}";

                    [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g' -e 's/ *$//g');";
                    [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/99/{print \$2}" | sed -e 's/^ *//g' -e 's/ *$//g');";

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

                    ## break out
                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
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

                ## we've finished our processing. site(s) requested have been failed over,
                ## reloaded and verified. what should we do now ?
                while true
                do
                    reset; clear;

                    print "\t\t\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.application.title  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                    print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}failover.request.success  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
                    print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}failover.success.perform.another  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                    read RESPONSE;
                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                    case ${RESPONSE} in
                        [Yy][Ee][Ss]|[Yy])
                            ## user has elected to perform further failovers. restart the process
                            unset RESPONSE;

                            ## remove the lockfile
                            if [ -s ${APP_ROOT}/${APP_FLAG} ]
                            then
                                sed -e "/${IUSER_AUDIT}/d" ${APP_ROOT}/${APP_FLAG} >> ${APP_ROOT}/${TMP_DIRECTORY}/tmp;
                                    mv ${APP_ROOT}/${TMP_DIRECTORY}/tmp ${APP_ROOT}/${APP_FLAG};
                            fi

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
            fi
            ;;
        ${INTERNET_TYPE_IDENTIFIER})
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested selection -> ${SELECTION}";

            reset; clear;

            ## we have enough information to proceed with a failover,
            ## so lets send out a request to confirm, and if so, kick it
            ## off
            if [ "${SELECTION}" = "A" ] || [ "${SELECTION}" = "a" ]
            then
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request confirmation of failover for ALL sites in ${SVC_REQUEST_OPTION}";

                print "\t\t\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.application.title  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}failover.request.confirmation ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2- | sed -e "s/%SITE%/all sites for ${SVC_REQUEST_OPTION}/")";
            else
                FAILOVER_SITE=$(echo ${SERVICE_DETAIL[${SELECTION}]} | cut -d "|" -f 3);
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request confirmation of failover for ${SERVICE_DETAIL[${SELECTION}]}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FAILOVER_SITE -> ${FAILOVER_SITE}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Print message -> sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}failover.request.confirmation ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e 's/^ *//' -e "s/%SITE%/${FAILOVER_SITE}/")";

                print "\t\t\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.application.title  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}failover.request.confirmation ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e 's/^ *//' -e "s/%SITE%/${FAILOVER_SITE}/")";

                unset FAILOVER_SITE;
            fi

            read CONFIRM;

            reset; clear;
            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

            case ${CONFIRM} in
                [Yy][Ee][Ss]|[Yy])
                    ## unset confirmation
                    unset CONFIRM;
                    reset; clear;

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
                    print "\t\t\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.application.title  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                    print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.provide.changenum  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                    print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.emergency.changenum  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                    read CHANGE_CONTROL;
                    reset; clear;
                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                    if [[ ${CHANGE_CONTROL} == [Ee] ]]
                    then
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change specified as emergency..";
                        CHANGE_CONTROL="E-$(date +"%m-%d-%Y_%H:%M:%S")";
                    fi

                    if [ $(${APP_ROOT}/${LIB_DIRECTORY}/validators/validate_change_ticket.sh ${CHANGE_CONTROL}) -ne 0 ]
                    then
                        ## change control provided was invalid
                        ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A change was attempted with an invalid change order by ${IUSER_AUDIT}. Change request was ${CHANGE_CONTROL}.";
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid change control was provided. A valid change control number is required to process the request.";

                        unset CHANGE_CONTROL;

                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/change.control.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    else
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting up required flags..";

                        if [[ "${SELECTION}" = "A" || "${SELECTION}" = "a" ]]
                        then
                            ## run a check to see if all the sites are in the same dc
                            ## make sure B is 0

                            while [ ${C} -ne ${#DC_MATCH[@]} ]
                            do
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${DC_MATCH[${C}]} -> ${DC_INFO[${C}]}";

                                ## set up a match here, we need to make sure
                                ## that everything is in the same datacenter,
                                ## and if not, we need to ask where to go
                                if [[ "${DC_MATCH[${C}]}" != "$(echo ${SERVICE_DETAIL[${C}]} | cut -d "|" -f 2)" || "${DC_MATCH[${C}]}" = "%DATACENTER%" ]]
                                then
                                    DC_MISMATCH=${_TRUE};
                                fi

                                (( C += 1 ));
                            done

                            ## reset the counter
                            C=0;

                            ## we only need a couple options to failover
                            ## a business unit, so lets set them here
                            BU=${SVC_REQUEST_OPTION};
                            ## take the first DC, they should all be the
                            ## the same and go from there
                            if [ "${DC_MISMATCH}" = "${_TRUE}" ]
                            then
                                print "\t\t\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.application.title  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}failover.datacenter.mismatch ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%BUSINESS_UNIT%/${SVC_REQUEST_OPTION}/")";

                                read TDC;

                                typeset -u TDC;

                                reset; clear;

                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
                            else
                                ## take the first entry because theyre all the same
                                [ "$(echo ${SERVICE_DETAIL[0]} | cut -d "|" -f 2)" = "${PRIMARY_DC}" ] && TDC=${SECONDARY_DC} || TDC=${PRIMARY_DC};
                            fi

                            unset DC_MISMATCH;
                            unset SVC_REQUEST_OPTION;
                            unset SVC_REQUEST_TYPE;

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BU -> ${BU}";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TDC -> ${TDC}";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_CONTROL -> ${CHANGE_CONTROL}";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runFailoverRequest.sh -r ${INTERNET_TYPE_IDENTIFIER} -b $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 3),${BU},${TDC},${CHANGE_CONTROL},${IUSER_AUDIT} -e";

                            ## temporarily unset stuff
                            unset METHOD_NAME;
                            unset CNAME;
                            unset RETURN_CODE;

                            ## remember the current indicator
                            CURR_OPTIND=${OPTIND};

                            . ${APP_ROOT}/${LIB_DIRECTORY}/runFailoverRequest.sh -r ${INTERNET_TYPE_IDENTIFIER} -b ${BU},${TDC},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                        else
                            if [ "$(echo ${SERVICE_DETAIL[${SELECTION}]} | cut -d "|" -f 2)" = "%DATACENTER%" ]
                            then
                                reset; clear;

                                print "\t\t\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.application.title  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}failover.datacenter.unknown ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2- | sed -e "s/%SITE%/$(echo ${SERVICE_DETAIL[${SELECTION}]} | cut -d "|" -f 3)/")";

                                read TDC;

                                typeset -u TDC;

                                reset; clear;

                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
                            else
                                [ "$(echo ${SERVICE_DETAIL[${SELECTION}]} | cut -d "|" -f 2)" = "${PRIMARY_DC}" ] && TDC=${SECONDARY_DC} || TDC=${PRIMARY_DC};
                            fi

                            BU=$(echo ${SERVICE_DETAIL[${SELECTION}]} | cut -d "/" -f 6 | cut -d "_" -f 3);
                            FNAME=$(echo ${SERVICE_DETAIL[${SELECTION}]} | cut -d "/" -f 7 | cut -d "|" -f 1);
                            PCODE=$(echo ${SERVICE_DETAIL[${SELECTION}]} | cut -d "/" -f 7 | cut -d "|" -f 1 | cut -d "." -f 3);

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BU -> ${BU}";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FNAME -> ${FNAME}";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PCODE -> ${PCODE}";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TDC -> ${TDC}";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_CONTROL -> ${CHANGE_CONTROL}";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runFailoverRequest.sh -r ${INTERNET_TYPE_IDENTIFIER} -s $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 3),${BU},${FNAME},${TDC},${PCODE},${CHANGE_CONTROL},${IUSER_AUDIT} -e";

                            ## temporarily unset stuff
                            unset METHOD_NAME;
                            unset CNAME;
                            unset RETURN_CODE;

                            ## remember the current indicator
                            CURR_OPTIND=${OPTIND};

                            . ${APP_ROOT}/${LIB_DIRECTORY}/runFailoverRequest.sh -r ${INTERNET_TYPE_IDENTIFIER} -s $(echo "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 3),${BU},${FNAME},${TDC},${PCODE},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                        fi
                        ## capture the return code
                        RET_CODE=${?};

                        ## reset optind and clear placeholder
                        OPTIND=${CURR_OPTIND};
                        unset CURR_OPTIND;

                        ## reset METHOD_NAME back to THIS method
                        local METHOD_NAME="${CNAME}#${0}";
                        CNAME="$(basename "${0}")";

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "run_failover executed..";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from call: ${RET_CODE}";

                        if [ ${RET_CODE} -eq 0 ]
                        then
                            if [ ! -z "${ERROR_COUNT}" ] && [ ${ERROR_COUNT} != 0 ]
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

                            ## we've finished our processing. site(s) requested have been failed over,
                            ## reloaded and verified. what should we do now ?
                            while true
                            do
                                reset; clear;

                                print "\t\t\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.application.title  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                                print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}failover.request.success  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
                                print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}failover.success.perform.another  | awk -F "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                                read RESPONSE;
                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                case ${RESPONSE} in
                                    [Yy][Ee][Ss]|[Yy])
                                        ## user has elected to perform further failovers. restart the process
                                        unset RESPONSE;

                                        ## remove the lockfile
                                        if [ -s ${APP_ROOT}/${APP_FLAG} ]
                                        then
                                            sed -e "/${IUSER_AUDIT}/d" ${APP_ROOT}/${APP_FLAG} >> ${APP_ROOT}/${TMP_DIRECTORY}/tmp;
                                            mv ${APP_ROOT}/${TMP_DIRECTORY}/tmp ${APP_ROOT}/${APP_FLAG};
                                        fi

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
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Caught an error performing the failover->${RET_CODE}";

                            [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g' -e 's/ *$//g');";
                            [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/99/{print \$2}" | sed -e 's/^ *//g' -e 's/ *$//g');";

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

                            ## break out
                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                        fi
                    fi
                    ;;
                [Nn][Oo]|[Nn])
                    unset CONFIRM;
                    unset SELECTION;
                    set -A DC_MATCH;
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
            ;;
    esac

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

unset METHOD_NAME;
unset CNAME;

## check security
. ${PLUGIN_ROOT_DIR}/lib/security/check_main.sh > /dev/null 2>&1;
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Security configuration does not allow the requested action." && exit ${RET_CODE} || unset RET_CODE;

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

main;
