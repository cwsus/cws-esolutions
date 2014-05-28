#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  validate_srv_type.sh
#         USAGE:  ./add_mx_ui_helper.sh
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
## Application constants
PLUGIN_NAME="dnsadmin";
CNAME="$(basename "${0}")";

function validate_service_type
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    if [ ! -z "${1}" ]
    then
        ## capitalize so we can validate
        typeset -u VALIDATE_TYPE=${1};

        if [ $(grep -c ${VALIDATE_TYPE} ${2}) -eq 1 ]
        then
            ## service type requested is in the allow list
            unset VALIDATE_TYPE;
            RETURN_CODE=0;
        else
            ## service type requested is NOT in the allow list
            unset VALIDATE_TYPE;
            RETURN_CODE=1;
        fi
    else
        RETURN_CODE=1;
    fi

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
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    print "${CNAME} - Validate that a change request has been successfully performed.";
    print "Usage:  ${CNAME} change-type target-datacenter record-name record-type";
    print "          target-datacenter is an optional argument that is only required when change-type is failover";
    print "          record-name is an optional argument that is only required when change-type is add/remove";
    print "          record-type is an optional argument that is only required when record-name is specified";

    return 3;
}

METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

## make sure we have args
[ ${#} -eq 0 ] && usage || validate_service_type ${@};

echo ${RETURN_CODE};
return ${RETURN_CODE};
