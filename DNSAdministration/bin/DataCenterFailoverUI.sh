#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  dataCenterFailoverUI.sh
#         USAGE:  ./dataCenterFailoverUI.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Processes a DNS failover for a particular datacenter.
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

    reset; clear;

    ## get the request information
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
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.datacenter.request/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.datacenter.format/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.option.cancel/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

        read ANSWER;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                unset ANSWER;

                reset; clear;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                exec ${APP_ROOT}/${MAIN_CLASS};

                exit 0;
                ;;
            *)
                if [ -z "${ANSWER}" ]
                then
                    ## got no response
                    ## show an error and ask for new data
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Response provided was blank. Cannot continue.";

                    ## unset variables
                    unset ANSWER;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/selection.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

                unset METHOD_NAME;
                unset CNAME;

                ## validate the request, it needs to be ph/vh,change_request
                . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validate_service_request.sh -d ${ANSWER} -e;
                RET_CODE=${?};

                ## re-set method name and cname
                local METHOD_NAME="${CNAME}#${0}";
                CNAME="$(basename "${0}")";

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validation complete. Return code -> ${RET_CODE}";

                if [ ${RET_CODE} -eq 0 ]
                then
                    ## formatting validator passed, check data
                    TDC=$(echo ${ANSWER} | cut -d "," -f 1);

                    ## capture change order here
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

                        executeFailoverProcess;
                    done
                else
                    ## formatting validator came back with a non-zero response
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Input provided, ${ANSWER}, was not valid.";

                    ## unset variables
                    unset ANSWER;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g' -e 's/ *$//g');";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi
                ;;
        esac
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

#===  FUNCTION  ===============================================================
#
#         NAME:  executeFailoverProcess
#  DESCRIPTION:  Main entry point for application.
#   PARAMETERS:  None
#      RETURNS:  0
#
#==============================================================================
function executeFailoverProcess
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request confirmation of failover..";

    while true
    do
        reset; clear;

        print "\t\t\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.request.confirmation/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

        read CONFIRM;

        case ${CONFIRM} in
            [Yy][Ee][Ss]|[Yy])
                unset CONFIRM;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request confirmed, continuing execution";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                ## call run_failover to process the request
                . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runFailoverRequest.sh -r ${INTERNET_TYPE_IDENTIFIER} -d ${ANSWER},${IUSER_AUDIT} -e;
                RET_CODE=${?};
                ## unset answer, we dont need it anymore
                unset ANSWER;

                reset; clear;

                if [ ${RET_CODE} -eq 0 ]
                then
                    ## we've finished our processing. site(s) requested have been failed over,
                    ## reloaded and verified. what should we do now ?
                    while true
                    do
                        reset; clear;

                        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.request.success/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
                        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.success.perform.another/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
                
                        read RESPONSE;
                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                        case ${RESPONSE} in
                            [Yy][Ee][Ss]|[Yy])
                                ## user has elected to perform further failovers. restart the process
                                unset RESPONSE;

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

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
                    if [ ${FAILOVER_CODE} -eq 0 ]
                    then
                        ## MOST of the process succeeded. the master nameserver has been updated
                        ## but one or more of the configured slave servers failed to reload with
                        ## the new configuration. we can drop into the restart shell or we can go
                        ## back to main and the user can handle the failed servers manually.
                        while true
                        do
                            reset; clear;

                            print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.request.positive.failure/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
                            print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.request.servers.success.failure/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g' -e "s/%SERVERS%/${FAILED_SERVERS[@]}/");";
                    
                            read RESPONSE;

                            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                            case ${RESPONSE} in
                                [Yy][Ee][Ss]|[Yy])
                                    ## user has elected to perform further failovers. restart the process
                                    unset RESPONSE;

                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

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
                        ## failover processing has failed. we can retry or ask the user to perform manually.
                        while true
                        do
                            reset; clear;

                            print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.request.failure/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
                            print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/failover.failure.retry/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
                    
                            read RESPONSE;
                            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                            case ${RESPONSE} in
                                [Yy][Ee][Ss]|[Yy])
                                    ## user has elected to perform further failovers. restart the process
                                    unset RESPONSE;

                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

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
                fi
                ;;
            [Nn][Oo]|[Nn])
                unset CONFIRM;
                unset TDC;
                unset CHANGE_CONTROL;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request aborted.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.request.canceled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            *)
                unset CONFIRM;

                ## input provided wasn't what we were looking for. assume cancel
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Input provided, ${CONFIRM}, was not valid.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.request.canceled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

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
