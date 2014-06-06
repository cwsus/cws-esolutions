#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  obtainChangeControl.sh
#         USAGE:  ./obtainChangeControl.sh
#   DESCRIPTION:  
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
#
#==============================================================================

[[ ! -z "${TRACE}" && "${TRACE}" = "TRUE" ]] && set -x;

## Application constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;

METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

trap "print '$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F  "=" '/system.trap.signals\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

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

    ## we can get started on the role swap. we need a change control here
    ## and confirmation that the requestor actually wants to make the change
    while true
    do
        reset; clear;

        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.provide.changenum\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.emergency.changenum\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read CHANGE_CONTROL;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_CONTROL -> ${CHANGE_CONTROL}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        case ${CHANGE_CONTROL} in
            [Xx]|[Qq]|[Cc])
                unset CHANGE_CONTROL;
                CANCEL_REQ="${_TRUE}";

                reset; clear;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Role swap request canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            *)
                if [[ ${CHANGE_CONTROL} == [Ee] ]]
                then
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change specified as emergency..";

                    CHANGE_CONTROL="E-$(date +"%m-%d-%Y_%H:%M:%S")";

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_CONTROL -> ${CHANGE_CONTROL}";
                fi

                RET_CODE=$(${APP_ROOT}/${LIB_DIRECTORY}/validators/validateChangeTicket.sh ${CHANGE_CONTROL})

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]]
                then
                    ## change request invalid
                    ## change control provided was invalid
                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A change was attempted with an invalid change order by ${IUSER_AUDIT}. Change request was ${CHANGE_CONTROL}.";
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid change control was provided. A valid change control number is required to process the request.";

                    unset CHANGE_CONTROL;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<change.control.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                reset; clear; break;
                ;;
        esac
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

main;

return 0;
