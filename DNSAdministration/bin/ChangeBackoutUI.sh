#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  changeBackoutUI.sh
#         USAGE:  ./changeBackoutUI.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Processes a DNS failover by using information previously
#                 obtained by retrieve_site_info.sh
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

## Application constants
[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ -z "${PLUGIN_ROOT_DIR}" && -f ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && echo "Failed to locate configuration data. Cannot continue." && exit 1;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

unset METHOD_NAME;
unset CNAME;

## check security
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
RET_CODE=${?};

[ ${RET_CODE} -ne 0 ] && echo "Security configuration does not allow the requested action." && exit ${RET_CODE} || unset RET_CODE;

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    reset; clear;

    ## get the request information
    while true
    do
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
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES}  | awk -F "=" '/\<backout.enter.info\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES}  | awk -F "=" '/\<backout.request.format\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES}  | awk -F "=" '/\<backout.retrieve.all\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
        
        ## get the requested project code/url or business unit
        read SVC_LIST;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SVC_LIST -> ${SVC_LIST}";

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${SVC_LIST} in
            [Xx]|[Qq]|[Cc])
                ## cancel, return control back to dns_administration.sh
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                reset; clear;

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                exec ${MAIN_CLASS};

                exit 0;
                ;;
            A|a)
                ## retrieve a list of all available backup files
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Retrieving list of backup files..";

                reset; clear;

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/run_backout.sh -a;
                RET_CODE=${?}

                ## reset METHOD_NAME back to THIS method
                local METHOD_NAME="${CNAME}#${0}";
                CNAME="$(basename "${0}")";

                reset; clear;

                if [ ${RET_CODE} -eq 0 ]
                then
                    ## we got a list back. if there are more results than the configured
                    ## display max, we show a pageable list. otherwise, just show the results
                    unset RET_CODE;

                    if [ ${#FILE_LIST[@]} -gt ${LIST_DISPLAY_MAX} ]
                    then
                        while true
                        do
                            print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.list.available\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                            while [ ${A} -ne ${LIST_DISPLAY_MAX} ]
                            do
                                if [ ! ${B} -eq ${#FILE_LIST[@]} ]
                                then
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${B} - $(echo ${FILE_LIST[${B}]} | cut -d "/" -f 7)";

                                    # NOTE: this will need to change depending on where backups should go
                                    print "${B} - $(echo ${FILE_LIST[${B}]} | cut -d "/" -f 7)";
                                    (( A += 1 ));
                                    (( B += 1 ));
                                else
                                    B=${#FILE_LIST[@]};
                                    A=${LIST_DISPLAY_MAX};
                                fi
                            done

                            if [ $(expr ${B} - ${LIST_DISPLAY_MAX}) -eq 0 ]
                            then
                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.display.next\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                            else
                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.display.prev\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.display.next\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                            fi

                            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                            
                            read SELECTION;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION->${SELECTION}";

                            case ${SELECTION} in
                                [Nn])
                                    clear;
                                    unset SELECTION;

                                    if [ ${B} -ge ${#FILE_LIST[@]} ]
                                    then
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Cannot shift past end of data.";

                                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<forward.shift.failed\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                        B=0;
                                        A=0;

                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    else
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Shifting to next dataset..";

                                        A=0;
                                        continue;
                                    fi
                                    ;;
                                [Pp])
                                    clear;
                                    unset SELECTION;

                                    if [ $(expr ${B} - ${LIST_DISPLAY_MAX}) -eq 0 ]
                                    then
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Cannot shift past end of data.";

                                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<previous.shift.failed\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
                                        A=0;
                                        B=0;
                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    else
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Shifting to previous dataset..";

                                        A=0;
                                        (( B -= (( ${LIST_DISPLAY_MAX} * 2 )) ));
                                        continue;
                                    fi
                                    ;;
                                [0-${B}]*)
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Filename ${SVC_LIST} provided. Processing..";
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                                    unset SVC_LIST;
                                    A=0;
                                    B=0;
                                    process_backout_file;
                                    ;;
                                [Xx]|[Qq]|[Cc])
                                    unset SELECTION;
                                    unset SVC_LIST;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";

                                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                    ;;
                                *)
                                    A=0;
                                    B=0;
                                    unset SELECTION;
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    ;;
                            esac
                        done
                    else
                        unset RET_CODE;

                        while true
                        do
                            print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.list.available\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                            while [ ${A} -ne ${#FILE_LIST[@]} ]
                            do
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${A} - $(echo ${FILE_LIST[${A}]} | cut -d "/" -f 7)";

                                # NOTE: this will need to change depending on where backups should go
                                print "${A} - $(echo ${FILE_LIST[${A}]} | cut -d "/" -f 7)";
                                (( A += 1 ));
                            done

                            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                            
                            read SELECTION;

                            case ${SELECTION} in
                                [0-${A}]*)
                                    ## user selected a backout file
                                    ## process the request
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Filename ${SVC_LIST} provided. Processing..";
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                                    unset SVC_LIST;
                                    process_backout_file;
                                    ;;
                                [Xx]|[Qq]|[Cc])
                                    unset SELECTION;
                                    unset SVC_LIST;
                                    A=0;
                                    B=0;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";
                                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
                                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                    ;;
                                *)
                                    unset SELECTION;
                                    clear;
                                    A=0;
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    ;;
                            esac
                        done
                    fi
                else
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backout request FAILED. Return code -> ${RET_CODE}";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi
                ;;
            *)
                if [ -z "${SVC_LIST}" ]
                then
                    ## response provided was not valid
                    unset SVC_LIST;
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                else
                    ## we need to make sure we were given real options.
                    ## run the validator to check
                    . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validate_service_request.sh -b ${SVC_LIST} -e;
                    RET_CODE=${?};

                    if [ ${RET_CODE} -eq 0 ]
                    then
                        unset RET_CODE;

                        ## request was validated successfully,
                        ## lets process it
                        process_backout_file;
                    else
                        unset RET_CODE;

                        ## request was invalid
                        unset SVC_LIST;
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SVC_LIST} invalid";

                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi
                fi
                ;;
        esac
    done
}

function process_backout_file
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    if [ ${#FILE_LIST[@]} -eq 0 ]
    then
        ## set up our messaging replacementes
        BU=$(echo ${SVC_LIST} | cut -d "," -f 1);
        CHANGE_REQ=$(echo ${SVC_LIST} | cut -d "," -f 2);
        CHANGE_DT=$(echo ${SVC_LIST} | cut -d "," -f 3);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BU -> ${BU}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_REQ -> ${CHANGE_REQ}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_DT -> ${CHANGE_DT}";
    else
        ## set up our messaging replacementes
        CHANGE_REQ=$(echo ${FILE_LIST[${SELECTION}]} | cut -d "." -f 4);
        BU=$(echo ${FILE_LIST[${SELECTION}]} | cut -d "_" -f 4 | cut -d "." -f 1);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BU -> ${BU}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_REQ -> ${CHANGE_REQ}";
    fi

    reset; clear;

    while true
    do
        ## provide user a chance to confirm the request
        ## prior to execution
        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<backout.request.confirmation\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%CHANGE_REQ%/${CHANGE_REQ}/" -e "s/%BUSINESS_UNIT%/${BU}/")";

        read CONFIRM;

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONFIRM -> ${CONFIRM}";

        case ${CONFIRM} in
            [Yy][Ee][Ss]|[Yy])
                ## user confirmed, send off to run_failover
                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                ## call out to run_backout.sh
                if [ ${#FILE_LIST[@]} -eq 0 ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing run_backout.sh -b ${BU} -d ${CHANGE_DT} -c ${CHANGE_REQ} -e..";

                    . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/run_backout.sh -b ${BU} -d ${CHANGE_DT} -c ${CHANGE_REQ} -e;
                else
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing run_backout.sh -f $(echo ${FILE_LIST[${SELECTION}]} | cut -d "/" -f 7 | sed -e "s/^M//g")";

                    . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/run_backout.sh -f $(echo ${FILE_LIST[${SELECTION}]} | cut -d "/" -f 7 | sed -e "s/^M//g");
                fi
                RET_CODE=${?};

                ## set method_name and cname back to this
                local METHOD_NAME="${CNAME}#${0}";
                CNAME="$(basename "${0}")";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing complete. Return code -> ${RET_CODE}";

                if [ ${RET_CODE} -eq 0 ]
                then
                    unset RET_CODE;
                    unset CHANGE_DT;
                    unset BU;
                    unset CHANGE_DT;

                    ## backout complete. advise and ask what next
                    while true
                    do
                        reset; clear;

                        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<backout.process.complete\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                        read RESPONSE;
                        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                        case ${RESPONSE} in
                            [Yy][Ee][Ss]|[Yy])
                                ## user has elected to perform further backouts. reload.
                                unset RESPONSE;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reloading to process additional backout requests..";

                                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                ;;
                            *)
                                ## user has elected not to perform further backouts. send to main class
                                unset RESPONSE;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Additional backout requests not required. Exiting.";

                                reset; clear;

                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                                exec ${MAIN_CLASS};

                                exit 0;
                                ;;
                        esac
                    done
                elif [ ${RET_CODE} -eq 27 ]
                then
                    unset RET_CODE;

                    ## multiple backup files were found. get the list
                    ## and display so the requestor can make an informed
                    ## decision
                    while true
                    do
                        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<backout.select.file\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                        while [ ${A} -ne ${#FILE_LIST[@]} ]
                        do
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File ${A} -> ${FILE_LIST[${A}]}";

                            print "${A} - ${FILE_LIST[${A}]}";
                        done

                        ## allow cancel
                        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                        
                        ## read in the request
                        read SELECTION;

                        reset; clear;

                        ## did we get a valid selection ?
                        case ${SELECTION} in
                            [Xx]|[Qq]|[Cc])
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION->${SELECTION}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";

                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                ;;
                            [0-${A}]*)
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION->${SELECTION}";

                                ## get the change request number/business unit from the backout file
                                CHANGE_REQ=$(echo ${FILE_LIST[${SELECTION}]} | cut -d "." -f 4);
                                BU=$(echo ${FILE_LIST[${SELECTION}]} | cut -d "_" -f 3 | cut -d "." -f 1);

                                print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<backout.request.confirmation\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%CHANGE_REQ%/${CHANGE_REQ}/" -e "s/%BUSINESS_UNIT%/${BU}/")\n";

                                read CONFIRM;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONFIRM->${CONFIRM}";

                                case ${CONFIRM} in
                                    [Yy][Ee][Ss]|Y|y)
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request confirmed - continuing";

                                        ## temporarily unset stuff
                                        unset METHOD_NAME;
                                        unset CNAME;

                                        ## call run_backout with the filename
                                        . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/run_backout.sh ${FILE_LIST[${SELECTION}]};
                                        RET_CODE=${?};

                                        ## set method_name and cname back to this
                                        local METHOD_NAME="${CNAME}#${0}";
                                        CNAME="$(basename "${0}")";

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Return code from run_backout.sh->${RET_CODE}";

                                        if [ ${RET_CODE} -eq 0 ]
                                        then
                                            ## backout complete. advise and ask what next
                                            while true
                                            do
                                                reset; clear;

                                                print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MESSAGES} | awk -F "=" '/\<backout.process.complete\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                                read RESPONSE;

                                                print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                                case ${RESPONSE} in
                                                    [Yy][Ee][Ss]|[Yy])
                                                        ## user has elected to perform further backouts. reload.
                                                        unset RESPONSE;

                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reloading to process additional backout requests..";

                                                        sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                                        ;;
                                                    *)
                                                        ## user has elected not to perform further backouts. send to main class
                                                        unset RESPONSE;

                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Additional backout requests not required. Exiting.";

                                                        reset; clear;

                                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                                                        exec ${MAIN_CLASS};

                                                        exit 0;
                                                        ;;
                                                esac
                                            done
                                        else
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while processing the requested backout. Return code from call: ${RET_CODE}";

                                            print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<backout.failure\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        fi
                                        ;;
                                    [Nn][Oo]|N|n)
                                        ## request canceled
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backout request canceled";

                                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                        ;;
                                    *)
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection ${CONFIRM} is invalid.";

                                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        ;;
                                esac
                                ;;
                           *)
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection ${SELECTION} is invalid.";

                                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                ;;
                        esac
                    done
                else
                    unset RET_CODE;

                    ## an error occurred, show the code
                    unset CONFIRM;
                    unset SELECTION;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while processing run_backout.sh. Return code->${RET_CODE}.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                fi
                ;;
            [Nn][Oo]|[Nn])
                ## user decided not to perform the backout
                unset CONFIRM;
                unset CHANGE_REQ;
                unset BU;
                A=0;
                B=0;

                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while processing run_backout.sh. Return code->${RET_CODE}.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            *)
                ## no valid option was provided
                unset CONFIRM;
                unset SELECTION;

                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No valid response was provided.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done
}

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

main;


return 0;
