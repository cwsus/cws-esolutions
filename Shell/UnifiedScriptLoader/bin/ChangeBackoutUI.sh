#!/usr/bin/env ksh
#==============================================================================
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

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

## Application constants
CNAME="$(/usr/bin/env basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}/${0##*/}")";
SCRIPT_ROOT="$(/usr/bin/env dirname "${SCRIPT_ABSOLUTE_PATH}")";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -f "${SCRIPT_ROOT}/../lib/plugin" ] && . "${SCRIPT_ROOT}/../lib/plugin";

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

[ -z "${APP_ROOT}" ] && awk -F "=" '/\<1\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

[ -f "${PLUGIN_LIB_DIRECTORY}/aliases" ] && . "${PLUGIN_LIB_DIRECTORY}/aliases";
[ -f "${PLUGIN_LIB_DIRECTORY}/functions" ] && . "${PLUGIN_LIB_DIRECTORY}/functions";

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

CNAME=${THIS_CNAME};
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

trap '$(awk -F "=" "/\<system.trap.signals\>/{print $2}" ${SYSTEM_MESSAGES} | sed -e "s/^ *//g;s/ *$//g;/^ *#/d;s/#.*//" -e "s/%SIGNAL%/Ctrl-C/"); sleep ${MESSAGE_DELAY}; reset; clear; continue' 1 2 3
trap 'set +v; set +x' INT TERM EXIT;

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
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    reset; clear;

    ## get the request information
    while true
    do
        printf "\n
            \t\t+-------------------------------------------------------------------+
            \t\t               $(awk -F "=" '/\<system.application.title\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t\t+-------------------------------------------------------------------+
            \t\t$(awk -F "=" '/\<system.application.hostname\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//') ${SYSTEM_HOSTNAME}
            \t\t$(awk -F "=" '/\<system.application.uptime\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//') ${SYSTEM_UPTIME}
            \t\t$(awk -F "=" '/\<system.application.user\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//') ${IUSER_AUDIT}
            \t\t+-------------------------------------------------------------------+\n\n";

        awk -F "=" '/\<system.available.options\>/{print "\t" $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<backout.enter.info\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<backout.request.format\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<backout.retrieve.all\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.select\>/{print $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        ## get the requested project code/url or business unit
        read SVC_LIST;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SVC_LIST -> ${SVC_LIST}";

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${SVC_LIST} in
            [Xx]|[Qq]|[Cc])
                ## cancel, return control back to dns_administration.sh
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                reset; clear;

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                sleep ${MESSAGE_DELAY}; reset; clear; /usr/bin/env -i ksh ${MAIN_CLASS};

                return 0;
                ;;
            A|a)
                ## retrieve a list of all available backup files
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Retrieving list of backup files..";

                reset; clear;

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                typeset THIS_CNAME=${CNAME};
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/run_backout.sh -a;
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                reset; clear;

                if [ ${RET_CODE} -eq 0 ]
                then
                    ## we got a list back. if there are more results than the configured
                    ## display max, we show a pageable list. otherwise, just show the results
                    unset RET_CODE;

                    if [ ${#FILE_LIST[*]} -gt ${LIST_DISPLAY_MAX} ]
                    then
                        while true
                        do
                            awk -F "=" '/\<system.list.available\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            while [ ${A} -ne ${LIST_DISPLAY_MAX} ]
                            do
                                if [ ! ${B} -eq ${#FILE_LIST[*]} ]
                                then
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${B} - $(printf ${FILE_LIST[${B}]} | cut -d "/" -f 7)";

                                    # NOTE: this will need to change depending on where backups should go
                                    printf "${B} - $(printf ${FILE_LIST[${B}]} | cut -d "/" -f 7)";

                                    (( A += 1 ));
                                    (( B += 1 ));
                                else
                                    B=${#FILE_LIST[*]};
                                    A=${LIST_DISPLAY_MAX};
                                fi
                            done

                            if [ $(( ${B} - ${LIST_DISPLAY_MAX} )) -eq 0 ]
                            then
                                awk -F "=" '/\<system.display.next\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                            else
                                awk -F "=" '/\<system.display.prev\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                                awk -F "=" '/\<system.display.next\>/{print $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                            fi

                            awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                            awk -F "=" '/\<system.option.select\>/{print $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            read SELECTION;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION->${SELECTION}";

                            case ${SELECTION} in
                                [Nn])
                                    clear;
                                    unset SELECTION;

                                    if [ ${B} -ge ${#FILE_LIST[*]} ]
                                    then
                                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Cannot shift past end of data.";

                                        awk -F "=" '/\<forward.shift.failed\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                        B=0;
                                        A=0;

                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    else
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Shifting to next dataset..";

                                        A=0;
                                        continue;
                                    fi
                                    ;;
                                [Pp])
                                    clear;
                                    unset SELECTION;

                                    if [ $(( ${B} - ${LIST_DISPLAY_MAX} )) -eq 0 ]
                                    then
                                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Cannot shift past end of data.";

                                        awk -F "=" '/\<previous.shift.failed\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                        A=0;
                                        B=0;

                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    else
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Shifting to previous dataset..";

                                        A=0;

                                        (( B -= (( LIST_DISPLAY_MAX * 2 )) ));

                                        continue;
                                    fi
                                    ;;
                                [0-${B}]*)
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Filename ${SVC_LIST} provided. Processing..";
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                                    unset SVC_LIST;

                                    A=0;
                                    B=0;

                                    process_backout_file;
                                    ;;
                                [Xx]|[Qq]|[Cc])
                                    unset SELECTION;
                                    unset SVC_LIST;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";

                                    awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                    ;;
                                *)
                                    A=0;
                                    B=0;
                                    unset SELECTION;
                                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                    awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    ;;
                            esac
                        done
                    else
                        unset RET_CODE;

                        while true
                        do
                            awk -F "=" '/\<system.list.available\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            while [ ${A} -ne ${#FILE_LIST[*]} ]
                            do
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${A} - $(printf ${FILE_LIST[${A}]} | cut -d "/" -f 7)";

                                # NOTE: this will need to change depending on where backups should go
                                printf "${A} - $(cut -d "/" -f 7 <<< ${FILE_LIST[${A}]})";
                                (( A += 1 ));
                            done

                            awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                            awk -F "=" '/\<system.option.select\>/{print $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            read SELECTION;

                            case ${SELECTION} in
                                [0-${A}]*)
                                    ## user selected a backout file
                                    ## process the request
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Filename ${SVC_LIST} provided. Processing..";
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                    unset SVC_LIST;

                                    process_backout_file;
                                    ;;
                                [Xx]|[Qq]|[Cc])
                                    unset SELECTION;
                                    unset SVC_LIST;
                                    A=0;
                                    B=0;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";

                                    awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                    ;;
                                *)
                                    unset SELECTION;

                                    clear;

                                    A=0;

                                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                    awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    ;;
                            esac
                        done
                    fi
                else
                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backout request FAILED. Return code -> ${RET_CODE}";

                    [ -z "${RET_CODE}" ] && awk -F "=" '/\<99/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                    [ ! -z "${RET_CODE}" ] && awk -F "=" "/${RET_CODE}/{print \$2}" ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi
                ;;
            *)
                if [ -z "${SVC_LIST}" ]
                then
                    ## response provided was not valid
                    unset SVC_LIST;
                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                    awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                reset; clear; process_backout_file;

                reset; clear; break;
                ;;
        esac
    done
}

function process_backout_file
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    if [ ${#FILE_LIST[*]} -eq 0 ]
    then
        ## set up our messaging replacementes
        BU=$(printf ${SVC_LIST} | cut -d "," -f 1);
        CHANGE_REQ=$(printf ${SVC_LIST} | cut -d "," -f 2);
        CHANGE_DT=$(printf ${SVC_LIST} | cut -d "," -f 3);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BU -> ${BU}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_REQ -> ${CHANGE_REQ}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_DT -> ${CHANGE_DT}";
    else
        ## set up our messaging replacementes
        CHANGE_REQ=$(printf ${FILE_LIST[${SELECTION}]} | cut -d "." -f 4);
        BU=$(printf ${FILE_LIST[${SELECTION}]} | cut -d "_" -f 4 | cut -d "." -f 1);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BU -> ${BU}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_REQ -> ${CHANGE_REQ}";
    fi

    reset; clear;

    while true
    do
        ## provide user a chance to confirm the request
        ## prior to execution
        awk -F "=" '/\<backout.request.confirmation\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%CHANGE_REQ%/${CHANGE_REQ}/" -e "s/%BUSINESS_UNIT%/${BU}/";

        read CONFIRM;

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONFIRM -> ${CONFIRM}";

        case ${CONFIRM} in
            [Yy][Ee][Ss]|[Yy])
                ## user confirmed, send off to run_failover
                ## temporarily unset stuff
                typeset THIS_CNAME=${CNAME};
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                ## validate the input
                [ ${#FILE_LIST[*]} -eq 0 ] && ${PLUGIN_LIB_DIRECTORY}/run_backout.sh -b ${BU} -d ${CHANGE_DT} -c ${CHANGE_REQ} -e || ${PLUGIN_LIB_DIRECTORY}/run_backout.sh -f $(printf ${FILE_LIST[${SELECTION}]} | cut -d "/" -f 7 | sed -e "s/^M//g");
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

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

                        awk -F "=" '/\<backout.process.complete\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        read RESPONSE;

                        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                        case ${RESPONSE} in
                            [Yy][Ee][Ss]|[Yy])
                                ## user has elected to perform further backouts. reload.
                                unset RESPONSE;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reloading to process additional backout requests..";

                                sleep ${MESSAGE_DELAY}; reset; clear;

                                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                                main;
                                ;;
                            *)
                                ## user has elected not to perform further backouts. send to main class
                                unset RESPONSE;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Additional backout requests not required. Exiting.";

                                reset; clear;

                                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                                sleep ${MESSAGE_DELAY}; reset; clear; /usr/bin/env -i ksh ${MAIN_CLASS};

                                return 0;
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
                        awk -F "=" '/\<backout.select.file\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        while [ ${A} -ne ${#FILE_LIST[*]} ]
                        do
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File ${A} -> ${FILE_LIST[${A}]}";

                            printf "${A} - ${FILE_LIST[${A}]}";
                        done

                        ## allow cancel
                        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                        awk -F "=" '/\<system.option.select\>/{print $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        ## read in the request
                        read SELECTION;

                        reset; clear;

                        ## did we get a valid selection ?
                        case ${SELECTION} in
                            [Xx]|[Qq]|[Cc])
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION->${SELECTION}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";

                                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                ;;
                            [0-${A}]*)
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION->${SELECTION}";

                                ## get the change request number/business unit from the backout file
                                CHANGE_REQ=$(printf ${FILE_LIST[${SELECTION}]} | cut -d "." -f 4);
                                BU=$(printf ${FILE_LIST[${SELECTION}]} | cut -d "_" -f 3 | cut -d "." -f 1);

                                awk -F "=" '/\<backout.request.confirmation\>/{print "\t" $2 "\n"}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%CHANGE_REQ%/${CHANGE_REQ}/" -e "s/%BUSINESS_UNIT%/${BU}/";

                                read CONFIRM;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONFIRM->${CONFIRM}";

                                case ${CONFIRM} in
                                    [Yy][Ee][Ss]|Y|y)
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request confirmed - continuing";

                                        ## temporarily unset stuff
                                        typeset THIS_CNAME=${CNAME};
                                        unset METHOD_NAME;
                                        unset CNAME;

                                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                                        ## validate the input
                                        ${PLUGIN_LIB_DIRECTORY}/run_backout.sh ${FILE_LIST[${SELECTION}]};
                                        typeset -i RET_CODE=${?};

                                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                                        CNAME="${THIS_CNAME}";
                                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                        if [ ${RET_CODE} -eq 0 ]
                                        then
                                            ## backout complete. advise and ask what next
                                            while true
                                            do
                                                reset; clear;

                                                awk -F "=" '/\<backout.process.complete\>/{print "\t" $2 "\n"}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                                read RESPONSE;

                                                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                                case ${RESPONSE} in
                                                    [Yy][Ee][Ss]|[Yy])
                                                        ## user has elected to perform further backouts. reload.
                                                        unset RESPONSE;

                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reloading to process additional backout requests..";

                                                        sleep ${MESSAGE_DELAY}; reset; clear;

                                                        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                                                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                                                        main;
                                                        ;;
                                                    *)
                                                        ## user has elected not to perform further backouts. send to main class
                                                        unset RESPONSE;

                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Additional backout requests not required. Exiting.";

                                                        reset; clear;

                                                        unset METHOD_NAME;
                                                        unset CNAME;

                                                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                                                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                                                        sleep ${MESSAGE_DELAY}; reset; clear; /usr/bin/env -i ksh ${MAIN_CLASS};

                                                        return 0;
                                                        ;;
                                                esac
                                            done
                                        else
                                            writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while processing the requested backout. Return code from call: ${RET_CODE}";

                                            awk -F "=" '/\<backout.failure\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        fi
                                        ;;
                                    [Nn][Oo]|N|n)
                                        ## request canceled
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backout request canceled";

                                        awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                        ;;
                                    *)
                                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection ${CONFIRM} is invalid.";

                                        awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        ;;
                                esac
                                ;;
                           *)
                                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection ${SELECTION} is invalid.";

                                awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                ;;
                        esac
                    done
                else
                    unset RET_CODE;

                    ## an error occurred, show the code
                    unset CONFIRM;
                    unset SELECTION;

                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while processing run_backout.sh. Return code->${RET_CODE}.";

                    [ -z "${RET_CODE}" ] && awk -F "=" '/\<99\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                    [ ! -z "${RET_CODE}" ] && awk -F "=" "/${RET_CODE}/{print \$2}" ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

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

                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while processing run_backout.sh. Return code->${RET_CODE}.";

                [ -z "${RET_CODE}" ] && awk -F "=" '/\<99\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                [ ! -z "${RET_CODE}" ] && awk -F "=" "/${RET_CODE}/{print \$2}" ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            *)
                ## no valid option was provided
                unset CONFIRM;
                unset SELECTION;

                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No valid response was provided.";

                awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

    return 0;
}

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

main;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

return 0;
