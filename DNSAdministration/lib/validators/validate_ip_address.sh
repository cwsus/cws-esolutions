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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

## Application constants
CNAME="$(basename "${0}")";

function validate_ip_addr
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
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
            ## print an "ERROR"
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
        ## throw an "ERROR"
        RETURN_CODE=45;
    fi


    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    print "${CNAME} - Validate that a change request has been successfully performed.";
    print "Usage:  ${CNAME} change-type target-datacenter record-name record-type";
    print "          target-datacenter is an optional argument that is only required when change-type is failover";
    print "          record-name is an optional argument that is only required when change-type is add/remove";
    print "          record-type is an optional argument that is only required when record-name is specified";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 3;
}

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

## make sure we have args
[ ${#} -eq 0 ] && usage || validate_ip_addr ${@};

echo ${RETURN_CODE};

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset OPTIND;
unset THIS_CNAME;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return ${RETURN_CODE};
