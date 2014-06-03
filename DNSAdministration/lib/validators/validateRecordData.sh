#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  validateServiceRecordData.sh
#         USAGE:  ./validateServiceRecordData.sh
#   DESCRIPTION:  Helper interface for add_record_ui. Pluggable, can be modified
#                 or copied for all allowed record types.
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

METHOD_NAME="${CNAME}#startup";

[ ${#} -eq 0 ] && usage;

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

typeset -r -x =$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/allowed_record_list/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ALLOWED_GTLD_LIST=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/allowed_gtld_list/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ALLOWED_CCTLD_LIST=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/allowed_cctld_list/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x =$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/allowed_service_names/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

#===  FUNCTION  ===============================================================
#          NAME:  validateRecordType
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#==============================================================================
function validateRecordType
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    [[ -z "${ALLOWED_RECORD_LIST}" || ! -f ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_RECORD_LIST} ]] && RETURN_CODE=0;

    egrep -v "^$|^#" ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_RECORD_LIST} | while read -r ALLOWED_RECORD
    do
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ALLOWED_RECORD -> ${ALLOWED_RECORD}";

        if [ "${1}" = "${ALLOWED_RECORD}" ]
        then
            RETURN_CODE=0;

            break;
        fi

        RETURN_CODE=1;
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset ALLOWED_RECORD;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#       RETURNS:  1
#==============================================================================
function validateType
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    [[ -z "${ALLOWED_RECORD_LIST}" || ! -f ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_RECORD_LIST} ]] && RETURN_CODE=0;

    egrep -v "^$|^#" ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_RECORD_LIST} | while read -r ALLOWED_RECORD
    do
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ALLOWED_RECORD -> ${ALLOWED_RECORD}";

        if [ "${1}" = "${ALLOWED_RECORD}" ]
        then
            RETURN_CODE=0;

            break;
        fi

        RETURN_CODE=1;
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset VALIDATE_TYPE;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#       RETURNS:  1
#==============================================================================
function usage
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    print "${CNAME} - Validate data provided for a SRV record.";
    print "Usage:  ${CNAME} validate-type validate-data";
    print "         validate-type can be one of: protocol or type";
    print "         validate-data must be the data to perform validation against";

    return 3;
}

METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

## make sure we have args
[ ${#} -eq 0 ] && usage || validate_protocol ${@};

echo ${RETURN_CODE};
return ${RETURN_CODE};
