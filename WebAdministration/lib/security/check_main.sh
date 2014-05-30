#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  check_system.sh
#         USAGE:  ./check_system.sh LEVEL METHOD_NAME CLASS_NAME LINE_NUM "message"
#   DESCRIPTION:  Prints the specified message to the defined logfile
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
## Constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

function check_main
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating security attributes..";

    if [ ! -z "${ENFORCE_SECURITY}" ] && [ "${ENFORCE_SECURITY}" = "${_TRUE}" ]
    then
        if [ ! $(echo ${AUTHORIZED_USERS[@]} | grep -c $(whoami)) -eq 1 ]
        then
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "$(whoami) is not authorized to execute ${CNAME}.";
            print "$(whoami) is not authorized to execute ${CNAME}.";
            RETURN_CODE=997;
        else
            RETURN_CODE=0;
        fi

        for GROUP in $(groups)
        do
            if [ $(echo ${AUTHORIZED_GROUPS[@]} | grep -c ${GROUP}) -eq 1 ]
            then
                (( AUTHORIZATION_COUNT += 1 ));
            fi
        done

        unset GROUP;

        if [ ! $(echo ${ALLOWED_SERVERS[@]} | grep -c $(uname -n)) -eq 1 ]
        then
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} cannot be executed on $(hostname).";
            print "${CNAME} cannot be executed on $(hostname).";
            RETURN_CODE=98;
        else
            RETURN_CODE=0;
        fi
    else
        RETURN_CODE=0;
    fi

    if [ "${SSH_USER_ACCT}" = "root" ] && [ "${SECURITY_OVERRIDE}" != "${_TRUE}" ]
    then
        ## do NOT allow ssh as root.
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configured SSH user account is root. Failing configuration check.";
        print "$(grep -w 995 ""${ERROR_MESSAGES}"" | grep -v "#" | cut -d "=" -f 2)\n";
        RETURN_CODE=97;
    else
        RETURN_CODE=0;
    fi

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

check_main;

return ${RETURN_CODE};
