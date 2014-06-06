#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  validateChangeTicket.sh
#         USAGE:  ./validateChangeTicket.sh
#   DESCRIPTION:  Helper interface for add_record_ui. Pluggable, can be modified
#     or copied for all allowed record types.
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

## Application constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

function validateChangeNumber
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";
    local RETURN_CODE=0;

    [[ -z "${ENABLE_CHANGE_RECORDS}" || "${ENABLE_CHANGE_RECORDS}" != "${_TRUE}" ]] && return 0;
    [ -z "${1}" ] && return 45;

    case ${1} in
        [Cc][Rr][0-9]*|[Cc][0-9]*|[Tt][0-9]*|[Ee]*)
            ## change request # is valid
            RETURN_CODE=0;
            ;;
        *)
            ## change request # isnt valid
            ## log it and throw it back
            RETURN_CODE=1;
            ;;
    esac

    return ${RETURN_CODE};
}

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

validateChangeNumber ${1};

return ${RETURN_CODE};
