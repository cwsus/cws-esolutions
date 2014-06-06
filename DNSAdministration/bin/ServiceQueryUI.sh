#!/usr/bin/ksh -x
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

[[ ! -z "${TRACE}" && "${TRACE}" = "TRUE" ]] && set -x;

## Application constants
[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ -z "${PLUGIN_ROOT_DIR}" && -f ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && echo "Failed to locate configuration data. Cannot continue." && exit 1;

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
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

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
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<dig.query.provide.address\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read ANSWER;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                unset COMPLETE;
                unset RESPONSE_DATA;
                unset RECORD_TYPE;
                unset IS_AUTHORIZED;
                unset CONTINUE;
                unset ANSWER;
                unset REVERSE_LOOKUP;
                unset TARGET_NAMESERVER;
                unset SITE_HOSTNAME;
                unset RET_CODE;
                unset REQUEST_ENTRY;
                unset RESPONSE_ENTRY;
                unset RESPONSE_NAMESERVER;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## terminate this thread and return control to main
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                exec ${MAIN_CLASS};

                exit 0;
                ;;
            *)
                if [ -z "${ANSWER}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                typeset -l SITE_HOSTNAME="${ANSWER}";

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME -> ${SITE_HOSTNAME}";

                requestDesiredNameserver;
                ;;
        esac
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

#===  FUNCTION  ===============================================================
#         NAME:  requestDesiredNameserver
#  DESCRIPTION:  Obtains and records the desired nameserver
#   PARAMETERS:  None
#==============================================================================
function requestDesiredNameserver
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<dig.query.request.nameservers\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<dig.query.valid.nameservers\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%NAMESERVERS%/${NAMED_SERVER_LIST}/")";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read ANSWER;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                unset COMPLETE;
                unset RESPONSE_DATA;
                unset RECORD_TYPE;
                unset IS_AUTHORIZED;
                unset CONTINUE;
                unset ANSWER;
                unset REVERSE_LOOKUP;
                unset TARGET_NAMESERVER;
                unset SITE_HOSTNAME;
                unset RET_CODE;
                unset REQUEST_ENTRY;
                unset RESPONSE_ENTRY;
                unset RESPONSE_NAMESERVER;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## terminate this thread and return control to main
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                exec ${MAIN_CLASS};

                exit 0;
                ;;
            *)
                [ ! -z "${ANSWER}" ] && TARGET_NAMESERVER="${ANSWER}";

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_NAMESERVER -> ${TARGET_NAMESERVER}";
                ;;
        esac

        break;
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    requestReverseResponse;

    return 0;
}

#===  FUNCTION  ===============================================================
#         NAME:  requestReverseResponse
#  DESCRIPTION:  Obtains and records the desired nameserver
#   PARAMETERS:  None
#==============================================================================
function requestReverseResponse
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<dig.perform.reverse.lookup\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read ANSWER;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                unset COMPLETE;
                unset RESPONSE_DATA;
                unset RECORD_TYPE;
                unset IS_AUTHORIZED;
                unset CONTINUE;
                unset ANSWER;
                unset REVERSE_LOOKUP;
                unset TARGET_NAMESERVER;
                unset SITE_HOSTNAME;
                unset RET_CODE;
                unset REQUEST_ENTRY;
                unset RESPONSE_ENTRY;
                unset RESPONSE_NAMESERVER;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## terminate this thread and return control to main
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                exec ${MAIN_CLASS};

                exit 0;
                ;;
            [Yy][Ee][Ss]|[Yy)
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reverse lookup confirmed.";

                REVERSE_LOOKUP="${_TRUE}";
                ;;
            *)
                REVERSE_LOOKUP="${_FALSE}";
                ;;
        esac

        break;
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REVERSE_LOOKUP -> ${REVERSE_LOOKUP}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    requestLookupType;

    return 0;
}

#===  FUNCTION  ===============================================================
#         NAME:  requestLookupType
#  DESCRIPTION:  Obtains and records the desired nameserver
#   PARAMETERS:  None
#==============================================================================
function requestLookupType
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<dig.query.provide.type\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<diq.query.valid.types\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read ANSWER;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${ANSWER} in
            [Xx]|[Qq]|[Cc])
                unset COMPLETE;
                unset RESPONSE_DATA;
                unset RECORD_TYPE;
                unset IS_AUTHORIZED;
                unset CONTINUE;
                unset ANSWER;
                unset REVERSE_LOOKUP;
                unset TARGET_NAMESERVER;
                unset SITE_HOSTNAME;
                unset RET_CODE;
                unset REQUEST_ENTRY;
                unset RESPONSE_ENTRY;
                unset RESPONSE_NAMESERVER;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## terminate this thread and return control to main
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                exec ${MAIN_CLASS};

                exit 0;
                ;;
            [Ll][Ii][Ss][Tt]|[Ll])
                ## list available types
                print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<dig.query.allowed.types\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                sed '1,16d' ${ALLOWED_RECORD_LIST};

                print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.continue.enter\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                read CONTINUE;

                reset; clear; continue;
                ;;
            *)
                [ -z "${ANSWER}" ] && ANSWER="A";

                ## make sure its an allowed type
                IS_AUTHORIZED=$(sed -e '/^ *#/d;s/#.*//' ${ALLOWED_RECORD_LIST} | awk "/\<${ANSWER}\>/" | sed -e 's/^ *//g;s/ *$//g');

                if [ -z "${IS_AUTHORIZED}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                typeset -u RECORD_TYPE="${ANSWER}";
                ;;
        esac

        break;
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TYPE -> ${RECORD_TYPE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    ## do lookup
    performLookup;

    return 0;
}

#===  FUNCTION  ===============================================================
#         NAME:  performLookup
#  DESCRIPTION:  Obtains and records the desired nameserver
#   PARAMETERS:  None
#==============================================================================
function performLookup
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    reset; clear;

    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

    unset CNAME;
    unset METHOD_NAME;

    [[ -z "${TARGET_NAMESERVER}" && -z "${REVERSE_LOOKUP}" ]] && . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runQuery.sh -t ${RECORD_TYPE} -u ${SITE_HOSTNAME} -e;
    [[ -z "${TARGET_NAMESERVER}" && "${REVERSE_LOOKUP}" = "${_TRUE}" ]] && . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runQuery.sh -t ${RECORD_TYPE} -u ${SITE_HOSTNAME} -r -e;
    [[ ! -z "${TARGET_NAMESERVER}" && -z "${REVERSE_LOOKUP}" ]] && . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runQuery.sh -s ${TARGET_NAMESERVER} -t ${RECORD_TYPE} -u ${SITE_HOSTNAME} -e;
    [[ ! -z "${TARGET_NAMESERVER}" && "${REVERSE_LOOKUP}" = "${_TRUE}" ]] && . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runQuery.sh -s ${TARGET_NAMESERVER} -t ${RECORD_TYPE} -u ${SITE_HOSTNAME} -r -e;
    RET_CODE=${?};

    ## reset cname and methodname
    CNAME="$(basename "${0}")";
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. Return code -> ${RET_CODE}";

    if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]]
    then
        unset RECORD_TYPE;
        unset IS_AUTHORIZED;
        unset CONTINUE;
        unset ANSWER;
        unset REVERSE_LOOKUP;
        unset TARGET_NAMESERVER;
        unset SITE_HOSTNAME;
        unset RET_CODE;

        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/no.response.received/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";

        sleep "${MESSAGE_DELAY}"; reset; clear; main;
    fi

    ## get the answer out
    if [ ! -s ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ]
    then
        ## something happened that wasnt good
        unset RECORD_TYPE;
        unset IS_AUTHORIZED;
        unset CONTINUE;
        unset ANSWER;
        unset REVERSE_LOOKUP;
        unset TARGET_NAMESERVER;
        unset SITE_HOSTNAME;
        unset RET_CODE;

        [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && rm ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/no.response.received/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";

        sleep "${MESSAGE_DELAY}"; reset; clear; main;
    fi

    local RESPONSE_DATA=$(awk '/;; ANSWER SECTION:/, /;; AUTHORITY SECTION:/' ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} | grep -w "${RECORD_TYPE}");

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE_DATA -> ${RESPONSE_DATA}";

    if [ -z "${RESPONSE_DATA}" ]
    then
        ## something happened that wasnt good
        unset RESPONSE_DATA;
        unset RECORD_TYPE;
        unset IS_AUTHORIZED;
        unset CONTINUE;
        unset ANSWER;
        unset REVERSE_LOOKUP;
        unset TARGET_NAMESERVER;
        unset SITE_HOSTNAME;
        unset RET_CODE;

        [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && rm ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/no.response.received/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";

        sleep "${MESSAGE_DELAY}"; reset; clear; main;
    fi

    local REQUEST_ENTRY=$(echo ${RESPONSE_DATA} | awk '{print $1}');
    local RESPONSE_ENTRY=$(echo ${RESPONSE_DATA} | awk '{print $NF}');
    local RESPONSE_NAMESERVER=$(awk -F ":" '/;; SERVER:/{print $2}' ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} | cut -d "(" -f 1 | sed -e 's/^ *//g;s/ *$//g');

    [ -z "${RESPONSE_NAMESERVER}" ] && RESPONSE_NAMESERVER="default";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_ENTRY -> ${REQUEST_ENTRY}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE_ENTRY -> ${RESPONSE_ENTRY}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE_NAMESERVER -> ${RESPONSE_NAMESERVER}";

    reset; clear;

    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<dig.result.txt\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%SERVER%/${RESPONSE_NAMESERVER}/" -e "s/%URL%/${REQUEST_ENTRY}/" -e "s/%IPADDR%/${RESPONSE_ENTRY}/")\n";
    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<dig.review.complete.response\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

    read COMPLETE;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "COMPLETE -> ${COMPLETE}";

    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

    case ${COMPLETE} in
        [Yy][Ee][Ss]|[Yy])
            reset; clear;

            unset COMPLETE;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printing datafile to screen..";

            cat ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.continue.enter\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

            read CONTINUE;

            case ${CONTINUE} in
                *)
                    unset CONTINUE;

                    [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && rm -rf ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                    ;;
            esac
            ;;
        *)
            unset COMPLETE;

            [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && rm -rf ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
            ;;
    esac

    rm ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

    while true
    do
        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.complete\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%SERVER%/${RESPONSE_NAMESERVER}/" -e "s/%URL%/${REQUEST_ENTRY}/" -e "s/%IPADDR%/${RESPONSE_ENTRY}/")\n";
        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.continue.request\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read ANSWER;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${ANSWER} in
            [Yy][Ee][Ss]|[Yy]) break ;;
            *)
                unset COMPLETE;
                unset RESPONSE_DATA;
                unset RECORD_TYPE;
                unset IS_AUTHORIZED;
                unset CONTINUE;
                unset ANSWER;
                unset REVERSE_LOOKUP;
                unset TARGET_NAMESERVER;
                unset SITE_HOSTNAME;
                unset RET_CODE;
                unset REQUEST_ENTRY;
                unset RESPONSE_ENTRY;
                unset RESPONSE_NAMESERVER;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## terminate this thread and return control to main
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                exec ${MAIN_CLASS};

                exit 0;
                ;;
        esac

        break;
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset COMPLETE;
    unset RESPONSE_DATA;
    unset RECORD_TYPE;
    unset IS_AUTHORIZED;
    unset CONTINUE;
    unset ANSWER;
    unset REVERSE_LOOKUP;
    unset TARGET_NAMESERVER;
    unset SITE_HOSTNAME;
    unset RET_CODE;
    unset REQUEST_ENTRY;
    unset RESPONSE_ENTRY;
    unset RESPONSE_NAMESERVER;

    reset; clear; main;

    return 0;
}

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

main;


return 0;
