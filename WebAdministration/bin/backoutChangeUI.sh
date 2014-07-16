#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  backoutChangeUI.sh
#         USAGE:  ./backoutChangeUI.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

## Application constants
CNAME="${THIS_CNAME}";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(/usr/bin/env dirname "${SCRIPT_ABSOLUTE_PATH}")";

function main
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    ## don't allow ctrl-c to be sent
    trap "echo '$(grep -w system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SIGNAL%/Ctrl-C/")'; sleep "${MESSAGE_DELAY}"; reset; clear; continue " 1 2 3

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checking if application is already in operation...";

    ## temporarily unset stuff
    unset METHOD_NAME;
    unset CNAME;

    ## make sure someone isnt already working on stuff
    . ${APP_ROOT}/${LIB_DIRECTORY}/check_usage.sh;
    typeset -i RET_CODE=${?};

    ## reset METHOD_NAME back to THIS method
typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;
    CNAME=$(/usr/bin/env basename ${0});

    ## is the application already running a site
    ## failover ?
    if [ ${RET_CODE} -eq 0 ]
    then
        if [ ${CLI} ]
        then
            ## unset this RET_CODE
            unset RET_CODE;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Continuing execution - this is our first run";

            reset; clear;

            ## get the request information
            while true
            do
                echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                echo "\t\t\t$(grep -w backout.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                echo "\t$(grep -w backout.enter.info "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                echo "\t$(grep -w backout.request.format "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                echo "\t$(grep -w backout.retrieve.all "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                echo "\t$(grep -w system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                ## get the requested project code/url or business unit
                read SVC_LIST;

                case ${SVC_LIST} in
                    [Xx]|[Qq]|[Cc])
                        ## remove lockfile
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing lockfile ${APP_FLAG}";
                        rm -rf ${APP_ROOT}/${APP_FLAG};

                        ## cancel, return control back to dns_administration.sh
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        echo "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                        reset; clear;

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;

                        exec ${MAIN_CLASS};

                        exit 0;
                        ;;
                    A|a)
                        ## retrieve a list of all available backup files
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Retrieving list of backup files..";

                        reset; clear;

                        echo "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;

                        . ${APP_ROOT}/${LIB_DIRECTORY}/run_backout.sh -a;
                        RET_CODE=${?}

                        ## reset METHOD_NAME back to THIS method
                    typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;
                        CNAME=$(/usr/bin/env basename ${0});

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
                                    echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    echo "\t\t\t$(grep -w backout.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    echo "\t$(grep -w system.list.available "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                    while [ ${A} -ne ${LIST_DISPLAY_MAX} ]
                                    do
                                        if [ ! ${B} -eq ${#FILE_LIST[@]} ]
                                        then
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${B} - $(echo ${FILE_LIST[${B}]} | cut -d "/" -f 7)";

                                            # NOTE: this will need to change depending on where backups should go
                                            echo "${B} - $(echo ${FILE_LIST[${B}]} | cut -d "/" -f 7)";
                                            (( A += 1 ));
                                            (( B += 1 ));
                                        else
                                            B=${#FILE_LIST[@]};
                                            A=${LIST_DISPLAY_MAX};
                                        fi
                                    done

                                    if [ $(expr ${B} - ${LIST_DISPLAY_MAX}) -eq 0 ]
                                    then
                                        echo "$(grep -w system.display.next "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    else
                                        echo "$(grep -w system.display.prev "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        echo "$(grep -w system.display.next "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    fi

                                    echo "$(grep -w system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                    read SELECTION;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION->${SELECTION}";

                                    case ${SELECTION} in
                                        [Nn])
                                            clear;
                                            unset SELECTION;

                                            if [ ${B} -ge ${#FILE_LIST[@]} ]
                                            then
                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Cannot shift past end of data.";

                                                echo "$(grep -w forward.shift.failed "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
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

                                                echo "$(grep -w previous.shift.failed "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
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

                                            echo "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                            ;;
                                        *)
                                            A=0;
                                            B=0;
                                            unset SELECTION;
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                            echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            ;;
                                    esac
                                done
                            else
                                unset RET_CODE;

                                while true
                                do
                                    echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}"| grep -v "#" | cut -d "=" -f 2)\n";
                                    echo "\t\t\t$(grep -w backout.application.title "${PLUGIN_MESSAGES}"| grep -v "#" | cut -d "=" -f 2)\n";
                                    echo "\t$(grep -w system.list.available "${SYSTEM_MESSAGES}"| grep -v "#" | cut -d "=" -f 2)\n";

                                    while [ ${A} -ne ${#FILE_LIST[@]} ]
                                    do
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${A} - $(echo ${FILE_LIST[${A}]} | cut -d "/" -f 7)";

                                        # NOTE: this will need to change depending on where backups should go
                                        echo "${A} - $(echo ${FILE_LIST[${A}]} | cut -d "/" -f 7)";
                                        (( A += 1 ));
                                    done

                                    echo "$(grep -w system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

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
                                            echo "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                            ;;
                                        *)
                                            unset SELECTION;
                                            clear;
                                            A=0;
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                            echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            ;;
                                    esac
                                done
                            fi
                        else
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backout request FAILED. Return code -> ${RET_CODE}";
                            echo "$(grep ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        fi
                        ;;
                    *)
                        if [ -z "${SVC_LIST}" ]
                        then
                            ## response provided was not valid
                            unset SVC_LIST;
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                            echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        else
                            ## we need to make sure we were given real options.
                            ## run the validator to check
                            . ${APP_ROOT}/${LIB_DIRECTORY}/validators/validate_service_request.sh -b ${SVC_LIST} -e;
                            typeset -i RET_CODE=${?};

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
                                echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                            fi
                        fi
                        ;;
                esac
            done
        else
            echo "this is for esupport";
            exit 0;
        fi
    else
        ## app already in use, inform the
        ## user
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Application already in use. Cannot continue processing";
        echo "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
        unset RET_CODE;
        sleep "${MESSAGE_DELAY}"; reset; clear;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        exec ${MAIN_CLASS};

        exit 0;
    fi
}

function process_backout_file
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

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
        echo "$(grep -w request.confirmation "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%CHANGE_REQ%/${CHANGE_REQ}/" -e "s/%BUSINESS_UNIT%/${BU}/")";

        read CONFIRM;
        reset; clear;
        echo "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

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

                    . ${APP_ROOT}/${LIB_DIRECTORY}/run_backout.sh -b ${BU} -d ${CHANGE_DT} -c ${CHANGE_REQ} -e;
                else
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing run_backout.sh -f $(echo ${FILE_LIST[${SELECTION}]} | cut -d "/" -f 7 | sed -e "s/^M//g")";

                    . ${APP_ROOT}/${LIB_DIRECTORY}/run_backout.sh -f $(echo ${FILE_LIST[${SELECTION}]} | cut -d "/" -f 7 | sed -e "s/^M//g");
                fi
                typeset -i RET_CODE=${?};

                ## set method_name and cname back to this
            typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;
                CNAME=$(/usr/bin/env basename ${0});

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

                        echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                        echo "\t\t\t$(grep -w backout.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                        echo "\t$(grep -w backout.process.complete "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                        read RESPONSE;
                        echo "\t$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

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
                                reset; clear; exec ${MAIN_CLASS};
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
                        echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                        echo "\t\t\t$(grep -w backout.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                        echo "\t$(grep -w backout.select.file "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                        echo "\t$(grep -w system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                        while [ ${A} -ne ${#FILE_LIST[@]} ]
                        do
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File ${A} -> ${FILE_LIST[${A}]}";
                            echo "${A} - ${FILE_LIST[${A}]}";
                        done

                        ## allow cancel
                        echo "$(grep -w system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e 's/%SITE%/all sites for ${REQUEST_OPTION}/')\n";

                        ## read in the request
                        read SELECTION;

                        reset; clear;

                        ## did we get a valid selection ?
                        case ${SELECTION} in
                            [Xx]|[Qq]|[Cc])
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION->${SELECTION}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";

                                echo "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                ;;
                            [0-${A}]*)
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELECTION->${SELECTION}";

                                ## get the change request number/business unit from the backout file
                                CHANGE_REQ=$(echo ${FILE_LIST[${SELECTION}]} | cut -d "." -f 4);
                                BU=$(echo ${FILE_LIST[${SELECTION}]} | cut -d "_" -f 3 | cut -d "." -f 1);

                                echo "$(grep -w request.confirmation "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%CHANGE_REQ%/${CHANGE_REQ}/" -e "s/%BUSINESS_UNIT%/${BU}/")";

                                read CONFIRM;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONFIRM->${CONFIRM}";

                                case ${CONFIRM} in
                                    [Yy][Ee][Ss]|Y|y)
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request confirmed - continuing";

                                        ## temporarily unset stuff
                                        unset METHOD_NAME;
                                        unset CNAME;

                                        ## call run_backout with the filename
                                        . ${APP_ROOT}/${LIB_DIRECTORY}/run_backout.sh ${FILE_LIST[${SELECTION}]};
                                        typeset -i RET_CODE=${?};

                                        ## set method_name and cname back to this
                                    typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;
                                        CNAME=$(/usr/bin/env basename ${0});

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Return code from run_backout.sh->${RET_CODE}";

                                        if [ ${RET_CODE} -eq 0 ]
                                        then
                                            ## backout complete. advise and ask what next
                                            while true
                                            do
                                                reset; clear;

                                                echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                echo "\t\t\t$(grep -w backout.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                echo "\t$(grep -w process.complete "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                read RESPONSE;
                                                echo "\t$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

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
                                                        reset; clear; exec ${MAIN_CLASS};
                                                        exit 0;
                                                        ;;
                                                esac
                                            done
                                        else
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while processing the requested backout. Return code from call: ${RET_CODE}";

                                            echo "$(grep -w backout.failure "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        fi
                                        ;;
                                    [Nn][Oo]|N|n)
                                        ## request canceled
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backout request canceled";

                                        echo "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                        ;;
                                    *)
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection ${CONFIRM} is invalid.";
                                        echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        ;;
                                esac
                                ;;
                           *)
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection ${SELECTION} is invalid.";
                                echo "$(grep -w selection.invalid "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                ;;
                        esac
                    done
                else
                    unset RET_CODE;

                    ## an "ERROR" occurred, show the code
                    unset CONFIRM;
                    unset SELECTION;
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while processing run_backout.sh. Return code->${RET_CODE}.";
                    echo "$(grep ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
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

                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while processing run_backout.sh. Return code->${RET_CODE}.";
                echo "$(grep ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            *)
                ## no valid option was provided
                unset CONFIRM;
                unset SELECTION;
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No valid response was provided.";
                echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done
}

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin ] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

main;
