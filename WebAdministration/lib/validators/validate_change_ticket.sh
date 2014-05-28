#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  validate_change_ticket.sh
#         USAGE:  ./validate_change_ticket.sh
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
## Application constants
PLUGIN_NAME="webadmin";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

function validate_change_number
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    if [ ! -z "${1}" ]
    then
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
    else
        ## no ip address was provided
        ## throw an error
        RETURN_CODE=45;
    fi
}

METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

validate_change_number ${1};

echo ${RETURN_CODE};
return ${RETURN_CODE};
