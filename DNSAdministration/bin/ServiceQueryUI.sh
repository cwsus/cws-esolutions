#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  serviceQueryUI.sh
#         USAGE:  ./serviceQueryUI.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
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
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        print "\n";
        print "\t\t+-------------------------------------------------------------------+";
        print "\t\t               WELCOME TO \E[0;31m $(sed -e '/^ *#/d;s/#.*//' | awk -F "=" '/plugin.application.title/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g') \033[0m";
        print "\t\t+-------------------------------------------------------------------+";
        print "\t\tSystem Type         : \E[0;36m ${SYSTEM_HOSTNAME} \033[0m";
        print "\t\tSystem Uptime       : \E[0;36m ${SYSTEM_UPTIME} \033[0m";
        print "\t\tUser                : \E[0;36m ${IUSER_AUDIT} \033[0m";
        print "";
        print "\t\t+-------------------------------------------------------------------+";
        print "";
        print "\t\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.available.options/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/dig.query.request/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/dig.query.format/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/dig.query.valid.nameservers/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g' -e "s/%NAMESERVERS%/${NAMED_SERVER_LIST}/");";
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/diq.query.valid.types/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/dig.perform.reverse.lookup/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";
        print "\t$( -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.option.cancel/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

        read ANSWER;

        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                unset ANSWER;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

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
                if [ -z "${ANSWER}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/selection.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                else
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request ${ANSWER}";

                    reset; clear;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.pending.message/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                    ## temporarily unset stuff
                    unset METHOD_NAME;
                    unset CNAME;

                    if [ "$(echo ${ANSWER} | cut -s -d "," -f 1)" != "" ]
                    then
                        ## validate the request
                        . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validate_service_request.sh -q ${ANSWER} -e;
                        RET_CODE=${?};
                    else
                        ## we were only given a name. no validation occurred
                        RET_CODE=0;
                        URL=${ANSWER};
                        SINGLE_ARG="${_TRUE}";
                    fi

                    ## reset cname and methodname
                    CNAME="$(basename "${0}")";
                    local METHOD_NAME="${CNAME}#${0}";

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validation complete. Return code -> ${RET_CODE}";

                    if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]]
                    then
                        unset ANSWER;
                        unset RET_CODE;

                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/selection.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi

                    ## unset retcode
                    unset RET_CODE;

                    if [ "${SINGLE_ARG}" != "${_TRUE}" ]
                    then
                        ## strip out the information in the request
                        SERVER=$(echo ${ANSWER} | cut -d "," -f 1);
                        typeset -u TYPE=$(echo ${ANSWER} | cut -d "," -f 2);
                        URL=$(echo ${ANSWER} | cut -d "," -f 3);
                    fi

                    unset ANSWER;

                    ## temporarily unset stuff
                    unset METHOD_NAME;
                    unset CNAME;

                    ## valid request, run the commands
                    case ${TYPE} in
                        [Rr])
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runQuery.sh -s ${SERVER} -r -u ${URL} -e";

                            if [ "${SINGLE_ARG}" != "${_TRUE}" ]
                            then
                                . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runQuery.sh -s ${SERVER} -r -u ${URL} -e;
                            else
                                . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runQuery.sh -u ${URL} -e;
                            fi
                            ;;
                        *)
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runQuery.sh -s ${SERVER} -t ${TYPE} -u ${URL} -e";

                            if [ "${SINGLE_ARG}" != "${_TRUE}" ]
                            then
                                . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runQuery.sh -s ${SERVER} -t ${TYPE} -u ${URL} -e;
                            else
                                . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runQuery.sh -u ${URL} -e;
                            fi
                            ;;
                    esac

                    RET_CODE=${?};

                    ## reset cname and methodname
                    CNAME="$(basename "${0}")";
                    local METHOD_NAME="${CNAME}#${0}";

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. Return code -> ${RET_CODE}";

                    if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]]
                    then
                        unset ANSWER;
                        unset RET_CODE;

                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/selection.invalid/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi

                    case ${TYPE} in
                        [Rr])
                            ## set type to PTR
                            TYPE="PTR";

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TYPE -> ${TYPE}";

                            if [ $(${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validate_ip_address.sh ${URL}) -ne 0 ]
                            then
                                RESPONSE_TXT=$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}${SERVER} ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} | grep -v "IN" | cut -d ";" -f 2);
                                RESULT_TXT=$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}${URL} ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} | grep ${TYPE} | grep -v "<<>>" | awk '{print $1}');

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE_TXT -> ${RESPONSE_TXT}";
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESULT_TXT -> ${RESULT_TXT}";

                                ## we got a name. this changes the way we look up and present the info.
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "We were provided a hostname. Translating to IP address..";
                                ## we need to pull apart the reverse address - it'll look something like
                                ## 4.3.2.1.in-addr.arpa. make it into the actual ip:
                                FIRST_OCTET=$(echo ${RESULT_TXT} | cut -d "." -f 4);
                                SECOND_OCTET=$(echo ${RESULT_TXT} | cut -d "." -f 3);
                                THIRD_OCTET=$(echo ${RESULT_TXT} | cut -d "." -f 2);
                                FOURTH_OCTET=$(echo ${RESULT_TXT} | cut -d "." -f 1);

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FIRST_OCTET -> ${FIRST_OCTET}";
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SECOND_OCTET -> ${SECOND_OCTET}";
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "THIRD_OCTET -> ${THIRD_OCTET}";
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FOURTH_OCTET -> ${FOURTH_OCTET}";

                                RESULT_TXT=${FIRST_OCTET}.${SECOND_OCTET}.${THIRD_OCTET}.${FOURTH_OCTET};

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESULT_TXT -> ${RESULT_TXT}";

                                ## unset the octets we dont need them anymore
                                unset FIRST_OCTET;
                                unset SECOND_OCTET;
                                unset THIRD_OCTET;
                                unset FOURTH_OCTET;

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESULT_TXT -> ${RESULT_TXT}";
                            else
                                ## need to reverse the IP we were provided to look up properly in result txt
                                FIRST_OCTET=$(echo ${URL} | cut -d "." -f 4);
                                SECOND_OCTET=$(echo ${URL} | cut -d "." -f 3);
                                THIRD_OCTET=$(echo ${URL} | cut -d "." -f 2);
                                FOURTH_OCTET=$(echo ${URL} | cut -d "." -f 1);

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FIRST_OCTET -> ${FIRST_OCTET}";
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SECOND_OCTET -> ${SECOND_OCTET}";
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "THIRD_OCTET -> ${THIRD_OCTET}";
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FOURTH_OCTET -> ${FOURTH_OCTET}";

                                ## re-initialize URL with the new data
                                LOOKUP_URL=${FIRST_OCTET}.${SECOND_OCTET}.${THIRD_OCTET}.${FOURTH_OCTET};

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LOOKUP_URL -> ${LOOKUP_URL}";

                                RESPONSE_TXT=$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}${SERVER} ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} | grep -v "IN" | cut -d ";" -f 2);
                                RESULT_TXT=$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}${LOOKUP_URL} ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} | grep ${TYPE} | grep -v "<<>>" | awk '{print $5}' | grep -v ${TYPE} | grep -v '^$');

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE_TXT -> ${RESPONSE_TXT}";
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESULT_TXT -> ${RESULT_TXT}";

                                ## unset octets and lookup url, we dont need them anymore
                                unset FIRST_OCTET;
                                unset SECOND_OCTET;
                                unset THIRD_OCTET;
                                unset FOURTH_OCTET;
                                unset LOOKUP_URL;
                            fi
                            ;;
                        *)
                            if [ "${SINGLE_ARG}" = "${_TRUE}" ]
                            then
                                RESPONSE_TXT=$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}${URL} ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} | grep -v "IN" | cut -d ";" -f 2);
                                RESULT_TXT=$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}${URL} ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} | grep "A" | grep -v ";" | grep -v ${NAMED_PRIMARY_SOA} | awk '{print $5}')
                            else
                                RESPONSE_TXT=$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}${SERVER} ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} | grep -v "IN" | cut -d ";" -f 2);
                                RESULT_TXT=$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}${URL} ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} | grep "${TYPE}" | grep -v ";" | grep -v ${NAMED_PRIMARY_SOA} | awk '{print $5}')
                            fi

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE_TXT -> ${RESPONSE_TXT}";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESULT_TXT -> ${RESULT_TXT}";
                            ;;
                    esac

                    reset; clear;

                    if [ ! -z "${RESULT_TXT}" ]
                    then
                        [ "${SINGLE_ARG}" = "${_TRUE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/dig.result.txt/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g' -e "s/%SERVER%/${NAMED_MASTER}/" -e "s/%URL%/${URL}/" -e "s/%IPADDR%/${RESULT_TXT}/");";
                        [ "${SINGLE_ARG}" != "${_TRUE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/dig.result.txt/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g' -e "s/%SERVER%/${SERVER}/" -e "s/%URL%/${URL}/" -e "s/%IPADDR%/${RESULT_TXT}/");";

                        ## unset now unused variables
                        unset RESPONSE_TXT;
                        unset RESULT_TXT;
                        unset SERVER;
                        unset TYPE;
                        unset URL;
                        unset SINGLE_ARG;

                        sleep ${MESSAGE_DELAY}; reset; clear; continue;
                    fi

                    ## unset now unused variables
                    unset RESPONSE_TXT;
                    unset RESULT_TXT;
                    unset SERVER;
                    unset TYPE;
                    unset URL;
                    unset SINGLE_ARG;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred processing request. RESPONSE_TXT -> ${RESPONSE_TXT}, RESULT_TXT -> ${RESULT_TXT}";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/no.response.received/{print \$2}" | sed -e 's/^ *//g' -e 's/ *$//g');";

                    unset RET_CODE;
                    unset ANSWER;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/dig.review.complete.response/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g')\n";

                    read COMPLETE;

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "COMPLETE -> ${COMPLETE}";

                    case ${COMPLETE} in
                        [Yy][Ee][Ss]|[Yy])
                            reset; clear;

                            unset COMPLETE;

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printing datafile to screen..";

                            cat ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

                            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.continue.enter/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');";

                            read CONTINUE;

                            case ${CONTINUE} in
                                *)
                                    unset CONTINUE;

                                    [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && rm -rf ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    ;;
                            esac
                            ;;
                        *)
                            unset COMPLETE;

                            [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && rm -rf ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                            ;;
                    esac
                fi
                ;;
        esac
    done

    [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && rm -rf ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

    unset COMPLETE;
    unset CONTINUE;
    unset ANSWER;
    unset RET_CODE;
    unset RESULT_TXT;
    unset RESPONSE_TXT;
    unset FIRST_OCTET;
    unset SECOND_OCTET;
    unset THIRD_OCTET;
    unset FOURTH_OCTET;
    unset LOOKUP_URL;
    unset TYPE;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

[ -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/${PLUGIN_NAME}.sh ] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && echo "Failed to locate configuration data. Cannot continue." && exit 1
    
OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

unset METHOD_NAME;
unset CNAME;

## check security
. ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/security/check_main.sh > /dev/null 2>&1;
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Security configuration does not allow the requested action." && exit ${RET_CODE} || unset RET_CODE;

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

main;
