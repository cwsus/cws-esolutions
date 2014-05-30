#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  validate_ip_address.sh
#         USAGE:  ./add_mx_ui_helper.sh
#   DESCRIPTION:  Helper interface for add_record_ui. Pluggable, can be modified
#     or copied for all allowed record types.
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

function validate_ip_addr
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    if [ ! -z "${1}" ] && [ $(echo ${1} | tr -dc "." | wc -c) -eq 3 ]
    then
        FIRST_OCTET=$(echo ${1} | cut -d "." -f 1);
        SECOND_OCTET=$(echo ${1} | cut -d "." -f 2);
        THIRD_OCTET=$(echo ${1} | cut -d "." -f 3);
        FOURTH_OCTET=$(echo ${1} | cut -d "." -f 4);

        if [ ! ${FIRST_OCTET} -le 255 ] && [ ! ${SECOND_OCTET} -le 255 ] &&
            [ ! ${THIRD_OCTET} -le 255 ] && [ ! ${FOURTH_OCTET} -le 255 ]
        then
            ## provided IP address is invalid
            ## print an error
            unset RECORD_DETAIL;
            unset FIRST_OCTET;
            unset SECOND_OCTET;
            unset THIRD_OCTET;
            unset FOURTH_OCTET;

            RETURN_CODE=45;
        else
            unset RECORD_DETAIL;
            unset FIRST_OCTET;
            unset SECOND_OCTET;
            unset THIRD_OCTET;
            unset FOURTH_OCTET;

            RETURN_CODE=0;
        fi
    else
        ## the ip address is not a 4 octet string or is blank
        ## throw an error
        RETURN_CODE=45;
    fi
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

    print "${CNAME} - Validate that a change request has been successfully performed.";
    print "Usage:  ${CNAME} change-type target-datacenter record-name record-type";
    print "          target-datacenter is an optional argument that is only required when change-type is failover";
    print "          record-name is an optional argument that is only required when change-type is add/remove";
    print "          record-type is an optional argument that is only required when record-name is specified";

    return 3;
}

METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

## make sure we have args
[ ${#} -eq 0 ] && usage || validate_ip_addr ${@};

echo ${RETURN_CODE};
return ${RETURN_CODE};

