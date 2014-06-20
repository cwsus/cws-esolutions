#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  run_failover.sh
#         USAGE:  ./run_failover.sh
#   DESCRIPTION:  Executes the necessary classes against the configured DNS
#                 master to fail over a single site.
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

#===  FUNCTION  ===============================================================
#          NAME:  failover_site
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function decom_site
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  failover_bu
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function decom_bu
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the usage of this application
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Execute a DNS failover request on the master nameserver based on the provided information.";
    print "Usage: ${CNAME} [-s <request data>] [-d <request data>] [-e execute] [-?|-h show this help]";
    print "  -s      Process a site failover based on a comma-delimited information set";
    print "  -b      Process a business unit failover based on a comma-delimited information set";
    print "  -d      Process a datacenter failover based on a comma-delimited information set";
    print "  -e      Execute processing";
    print "  -h|-?   Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 3;
}

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin.sh ]] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

[ ${#} -eq 0 ] && usage;

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

while getopts ":s:b:d:eh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        s)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            DECOM_TYPE="site";

            ## comma-delimited information set, lets strip the "INFO"
            SITE_HOSTNAME=$(echo "${OPTARG}" | cut -d "," -f 1);
            UNIT=$(echo "${OPTARG}" | cut -d "," -f 2);
            FILENAME=$(echo "${OPTARG}" | cut -d "," -f 3);
            PRJCODE=$(echo "${OPTARG}" | cut -d "," -f 5);
            CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 6);
            IUSER_AUDIT=$(echo "${OPTARG}" | cut -d "," -f 7);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DECOM_TYPE->${DECOM_TYPE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILENAME->${FILENAME}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRJCODE->${PRJCODE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT->${IUSER_AUDIT}";
            ;;
        b)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            DECOM_TYPE="unit";

            ## comma-delimited information set, lets strip the "INFO"
            UNIT=$(echo "${OPTARG}" | cut -d "," -f 2);
            CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 4);
            IUSER_AUDIT=$(echo "${OPTARG}" | cut -d "," -f 5);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DECOM_TYPE->${DECOM_TYPE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT->${IUSER_AUDIT}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            case ${FAILOVER_TYPE} in
                site)
                    ## Make sure we have enough information to process
                    ## and execute
                    if [ -z "${UNIT}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No business unit was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=15;
                    elif [ -z "${FILENAME}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone filename was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=16;
                    elif [ -z "${TARGET}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target datacenter was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=17;
                    elif [ -z "${PRJCODE}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No project code was    provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=18;
                    elif [ -z "${CHG_CTRL}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=19;
                    elif [ -z "${IUSER_AUDIT}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to "AUDIT" user account. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=20;
                    else
                        ## We have enough information to process the request, continue
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        failover_site;
                    fi
                    ;;
                unit)
                    ## Make sure we have enough information to process
                    ## and execute
                    if [ -z "${UNIT}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No business unit was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=15;
                    elif [ -z "${TARGET}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target datacenter was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=17;
                    elif [ -z "${CHG_CTRL}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=19;
                    elif [ -z "${IUSER_AUDIT}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to "AUDIT" user account. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=20;
                    else
                        ## We have enough information to process the request, continue
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        failover_bu;
                    fi
                    ;;
                datacenter)
                    ## Make sure we have enough information to process
                    ## and execute
                    if [ -z "${TARGET}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target datacenter was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset TARGET;
                        unset CHG_CTRL;

                        RETURN_CODE=17;
                    elif [ -z "${CHG_CTRL}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset TARGET;
                        unset CHG_CTRL;

                        RETURN_CODE=19;
                    elif [ -z "${IUSER_AUDIT}" ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to "AUDIT" user account. Unable to continue processing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset TARGET;
                        unset CHG_CTRL;

                        RETURN_CODE=20;
                    else
                        ## We have enough information to process the request, continue
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        failover_datacenter;
                    fi
                    ;;
                *)
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No information was provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    unset FAILOVER_TYPE;
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


[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return ${RETURN_CODE};
