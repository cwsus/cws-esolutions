#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  validate_service_request.sh
#         USAGE:  ./validate_service_request.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Verifies that the provided site information is in a format
#                 that can be utilized by the application to process the request.
#                 This is a utility class, therefore, it cannot be run by itself..
#                 it must be invoked by the a requesting class.
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
[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";
CNAME="$(basename "${0}")";

#===  FUNCTION  ===============================================================
#          NAME:  validate_service_request
#   DESCRIPTION:  Interactive interface to provide the required information to
#                 obtain DNS information
#    PARAMETERS:  None
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function validate_site_request
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST->${SITE_REQUEST}";

    ## make sure the request was formatted correctly
    SITE_REQUEST_TYPE=$(echo ${SITE_REQUEST} | cut -d "," -f 1);
    SITE_REQUEST_OPTION=$(echo ${SITE_REQUEST} | cut -d "," -f 2);

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST_TYPE -> ${SITE_REQUEST_TYPE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST_OPTION -> ${SITE_REQUEST_OPTION}";

    ## check to make sure that we got a valid type indicator
    [ "${SITE_REQUEST_TYPE}" = "b" ] || [ "${SITE_REQUEST_TYPE}" = "p" ] || [ "${SITE_REQUEST_TYPE}" = "u" ] && SITE_REQ_TYPE=0 || SITE_REQ_TYPE=30;
    [ -z "${SITE_REQUEST_OPTION}" ] && SITE_REQ_OPT=30 || SITE_REQ_OPT=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQ_TYPE -> ${SITE_REQ_TYPE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQ_OPT -> ${SITE_REQ_OPT}";

    if [ ${SITE_REQ_TYPE} -eq 0 ] && [ ${SITE_REQ_OPT} -eq 0 ]
    then
        ## unset our variable
        unset SITE_REQUEST_TYPE;
        unset SITE_REQUEST_OPTION;
        unset SITE_REQUEST;
        unset SITE_REQ_TYPE;
        unset SITE_REQ_OPT;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=0;
    else
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_REQUEST} not properly formatted.";
        ## unset our variable
        unset SITE_REQUEST_TYPE;
        unset SITE_REQUEST_OPTION;
        unset SITE_REQUEST;
        unset SITE_REQ_TYPE;
        unset SITE_REQ_OPT;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=30;
    fi

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  validate_datacenter_request
#   DESCRIPTION:  Interactive interface to provide the required information to
#                 obtain DNS information
#    PARAMETERS:  None
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function validate_datacenter_request
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST->${SITE_REQUEST}";

    ## make sure the request was formatted correctly
    SITE_REQUEST_TYPE=$(echo ${SITE_REQUEST} | cut -d "," -f 1);
    SITE_REQUEST_OPTION=$(echo ${SITE_REQUEST} | cut -d "," -f 2);

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST_TYPE -> ${SITE_REQUEST_TYPE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST_OPTION -> ${SITE_REQUEST_OPTION}";

    ## check to make sure that we got a valid type indicator
    [ "${SITE_REQUEST_TYPE}" = "ph" ] || [ "${SITE_REQUEST_TYPE}" = "vh" ] && SITE_REQ_TYPE=0 || SITE_REQ_TYPE=30;
    [ -z "${SITE_REQUEST_OPTION}" ] && SITE_REQ_OPT=30 || SITE_REQ_OPT=0;

    if [ ${SITE_REQ_TYPE} -eq 0 ] && [ ${SITE_REQ_OPT} -eq 0 ]
    then
        ## unset our variable
        unset SITE_REQUEST_TYPE;
        unset SITE_REQUEST_OPTION;
        unset SITE_REQUEST;
        unset SITE_REQ_TYPE;
        unset SITE_REQ_OPT;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=0;
    else
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_REQUEST} not properly formatted.";
        ## unset our variable
        unset SITE_REQUEST_TYPE;
        unset SITE_REQUEST_OPTION;
        unset SITE_REQUEST;
        unset SITE_REQ_TYPE;
        unset SITE_REQ_OPT;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=30;
    fi

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  validate_query_request
#   DESCRIPTION:  Interactive interface to provide the required information to
#                 obtain DNS information
#    PARAMETERS:  None
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function validate_backout_request
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST->${SITE_REQUEST}";

    ## make sure the request was formatted correctly
    SITE_REQUEST_TYPE=$(echo ${SITE_REQUEST} | cut -d "," -f 1);
    SITE_REQUEST_OPTION=$(echo ${SITE_REQUEST} | cut -d "," -f 2);
    SITE_REQUEST_URL=$(echo ${SITE_REQUEST} | cut -d "," -f 3);

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST_TYPE -> ${SITE_REQUEST_TYPE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST_OPTION -> ${SITE_REQUEST_OPTION}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST_URL -> ${SITE_REQUEST_URL}";

    ## check to make sure that we got a valid type indicator
    [ -z "${SITE_REQUEST_TYPE}" ] && SITE_REQ_TYPE=30 || SITE_REQ_TYPE=0;
    [ -z "${SITE_REQUEST_OPTION}" ] && SITE_REQ_OPT=30 || SITE_REQ_OPT=0;
    [ -z "${SITE_REQUEST_URL}" ] && SITE_REQ_URL=30 || SITE_REQ_URL=0;

    if [ ${SITE_REQ_TYPE} -eq 0 ] && [ ${SITE_REQ_OPT} -eq 0 ] && [ ${SITE_REQ_URL} -eq 0 ]
    then
        ## unset our variable
        unset SITE_REQUEST_TYPE;
        unset SITE_REQUEST_OPTION;
        unset SITE_REQUEST;
        unset SITE_REQ_TYPE;
        unset SITE_REQ_OPT;
        unset SITE_REQ_URL;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=0;
    else
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_REQUEST} not properly formatted.";
        ## unset our variable
        unset SITE_REQUEST_TYPE;
        unset SITE_REQUEST_OPTION;
        unset SITE_REQUEST;
        unset SITE_REQ_TYPE;
        unset SITE_REQ_OPT;
        unset SITE_REQ_URL;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=30;
    fi

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  validate_query_request
#   DESCRIPTION:  Interactive interface to provide the required information to
#                 obtain DNS information
#    PARAMETERS:  None
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function validate_query_request
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST->${SITE_REQUEST}";

    ## make sure the request was formatted correctly
    SITE_REQUEST_TYPE=$(echo ${SITE_REQUEST} | cut -d "," -f 1);
    SITE_REQUEST_OPTION=$(echo ${SITE_REQUEST} | cut -d "," -f 2);
    SITE_REQUEST_URL=$(echo ${SITE_REQUEST} | cut -d "," -f 3);

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST_TYPE -> ${SITE_REQUEST_TYPE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST_OPTION -> ${SITE_REQUEST_OPTION}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST_URL -> ${SITE_REQUEST_URL}";

    ## check to make sure that we got a valid type indicator
    [ -z "${SITE_REQUEST_TYPE}" ] && SITE_REQ_TYPE=30 || SITE_REQ_TYPE=0;
    [ -z "${SITE_REQUEST_OPTION}" ] && SITE_REQ_OPT=30 || SITE_REQ_OPT=0;
    [ -z "${SITE_REQUEST_URL}" ] && SITE_REQ_URL=30 || SITE_REQ_URL=0;

    if [ ${SITE_REQ_TYPE} -eq 0 ] && [ ${SITE_REQ_OPT} -eq 0 ] && [ ${SITE_REQ_URL} -eq 0 ]
    then
        ## unset our variable
        unset SITE_REQUEST_TYPE;
        unset SITE_REQUEST_OPTION;
        unset SITE_REQUEST;
        unset SITE_REQ_TYPE;
        unset SITE_REQ_OPT;
        unset SITE_REQ_URL;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=0;
    else
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_REQUEST} not properly formatted.";
        ## unset our variable
        unset SITE_REQUEST_TYPE;
        unset SITE_REQUEST_OPTION;
        unset SITE_REQUEST;
        unset SITE_REQ_TYPE;
        unset SITE_REQ_OPT;
        unset SITE_REQ_URL;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=30;
    fi

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#   RETURNS:  0
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Add audit indicators and other flags to the failover zone file";
    print "Usage:  ${CNAME} [-s request data] [-d request data] [-b request data] [-q request data] [-e execute] [-?|-h show this help]";
    print "  -s      Validate a site failover request.";
    print "  -d      Validate a datacenter failover request.";
    print "  -b      Validate a backout request";
    print "  -q      Validate a DiG query request.";
    print "  -e      Execute processing";
    print "  -h|-?   Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 3;
}

## make sure we have arguments
[ ${#} -eq 0 ] && usage;

typeset -i OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CNAME -> ${CNAME}";

while getopts ":s:d:b:q:eh:" OPTIONS
do
    case "${OPTIONS}" in
        s)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_REQUEST..";

            ## Capture the target datacenter
            VALIDATE="validate-site";
            SITE_REQUEST="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST -> ${SITE_REQUEST}";
            ;;
        d)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_REQUEST..";

            ## Capture the target datacenter
            VALIDATE="validate-dc";
            SITE_REQUEST="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST -> ${SITE_REQUEST}";
            ;;
        b)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_REQUEST..";

            ## Capture the target datacenter
            VALIDATE="validate-backout";
            SITE_REQUEST="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST -> ${SITE_REQUEST}";
            ;;
        q)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_REQUEST..";

            ## Capture the target datacenter
            VALIDATE="validate-query";
            SITE_REQUEST="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_REQUEST -> ${SITE_REQUEST}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            case ${VALIDATE} in
                validate-site)
                    if [ -z "${SITE_REQUEST}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No site information was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset VALIDATE;
                        unset SITE_REQUEST;
                        RETURN_CODE=7;
                    else
                        unset VALIDATE;
                        validate_site_request;
                    fi
                    ;;
                validate-dc)
                    if [ -z "${SITE_REQUEST}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No datacenter information was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset VALIDATE;
                        unset SITE_REQUEST;
                        RETURN_CODE=7;
                    else
                        unset VALIDATE;
                        validate_datacenter_request;
                    fi
                    ;;
                validate-backout)
                    if [ -z "${SITE_REQUEST}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No backout information was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset VALIDATE;
                        unset SITE_REQUEST;
                        RETURN_CODE=7;
                    else
                        unset VALIDATE;
                        validate_backout_request;
                    fi
                    ;;
                validate-query)
                    if [ -z "${SITE_REQUEST}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No query information was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset VALIDATE;
                        unset SITE_REQUEST;
                        RETURN_CODE=7;
                    else
                        unset VALIDATE;
                        validate_query_request;
                    fi
                    ;;
                *)
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No information was provided. Unable to continue processing.";

                    unset VALIDATE;
                    unset SITE_REQUEST;
                    RETURN_CODE=999;
                    ;;
            esac
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done

shift ${OPTIND}-1;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return ${RETURN_CODE};
