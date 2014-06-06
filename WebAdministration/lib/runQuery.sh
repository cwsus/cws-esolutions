#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  run_query.sh
#         USAGE:  ./run_query.sh
#   DESCRIPTION:  Processes backout requests for previously executed change
#                 requests.
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
#          NAME:  return_query_response
#   DESCRIPTION:  Returns a full response from DiG for a provided address
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function returnARecord
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing DiG query for ${SITE_URL}..";

    ## go local first
    RETURNED_IP=$(dig +short -t a ${SITE_URL});

    if [ -z "${RETURNED_IP}" ]
    then
        ## no local results found, go out
        ## spawn an ssh connection to the provided server to run a DiG query
        ## check to see if we have an internal or external box
        if [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution is ON. Processing..";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command wget -q -O - \"\${@}\" " \
                "$(echo ${DNS_SERVICE_URL} | sed -e "s/{SITE_HOSTNAME}/${SITE_URL}/") | awk 'NR>10' | awk 'NR<5' | grep -v \";;\" | " \
                "awk '{print $5}' | sed -e "s^<br^ ^g" | tail -2");

            RETURNED_IP=$(wget -q -O - "\${@}" $(echo ${DNS_SERVICE_URL} | sed -e "s/{SITE_HOSTNAME}/${SITE_URL}/") | \
                awk 'NR>10' | awk 'NR<5' | grep -v ";;" | awk '{print $5}' | sed -e "s^<br^ ^g" | tail -2);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURNED_IP -> ${RETURNED_IP}";
        else
            ## we were asked to run against an external server,
            ## so lets check our proxy list for an available
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "External service requested. Verifying proxy access.";

            for PROXY in ${PROXY_SERVERS[@]}
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now validating proxy ${PROXY}..";

                $(ping ${PROXY} > /dev/null 2>&1);

                PING_RCODE=${?}

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                if [ ${PING_RCODE} -eq 0 ]
                then
                    ## stop if its available and run the command
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Proxy access confirmed. Proxy: ${PROXY}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/lib/tcl/runSSHConnection.exp ${PROXY} \"wget -q -O - \"\${@}\" $(echo ${DNS_SERVICE_URL} | sed -e "s/{SITE_HOSTNAME}/${SITE_URL}/") > ${APP_ROOT}/${DIG_DATA_FILE}";

                    RETURNED_IP=$(${APP_ROOT}/lib/tcl/runSSHConnection.exp ${PROXY} "wget -q -O - \"\${@}\" \
                        $(echo ${DNS_SERVICE_URL} | sed -e "s/{SITE_HOSTNAME}/${SITE_URL}/")" | awk 'NR>10' | awk 'NR<5' | \
                        grep -v ";;" | awk '{print $5}' | sed -e "s^<br^ ^g" | tail -2);

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURNED_IP -> ${RETURNED_IP}";

                    break;
                else
                    ## first one wasnt available, check the remaining
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Proxy access failed. Proxy: ${PROXY}";

                    unset PING_RCODE;
                    continue;
                fi
            done

            ## unset unneeded variable
            unset PROXY;
            unset PING_RCODE;
            unset PING_CMD;
        fi

        if [ -z "${RETURNED_IP}" ]
        then
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while processing the request. Please try again.";

            RETURN_CODE=999;
        fi
    fi

    if [ ! -z "${RETURNED_IP}" ] && [ -z "${RETURN_CODE}" ]
    then
        ## determine what datacenter this IP is associated with
        RETURNED_DATACENTER_ID=$(getWebInfo | grep ${SITE_URL} | grep -v "#" | grep ${RETURNED_IP} | \
            cut -d "|" -f 2 | cut -d "_" -f 1 | sort | uniq);

        if [ ! -z "${RETURNED_DATACENTER_ID}" ]
        then
            ## determine where the alternate is
            for DATACENTER in ${DATACENTERS}
            do
                if [ "${RETURNED_DATACENTER_ID}" != "${DATACENTER}" ]
                then
                    ALTERNATE_DATACENTER=${DATACENTER};
                fi
            done

            if [ ! -z "${ALTERNATE_DATACENTER}" ]
            then
                ## we have our alternate.
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Alternate datcenter located. ALTERNATE_DATACENTER -> ${ALTERNATE_DATACENTER}";

                RETURN_CODE=0;
            else
                ## no alternate found.
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to accurately determine alternate datacenter.";

                RETURN_CODE=1;
            fi
        else
            ## unable to accurately determine datacenter. we'll have to ask
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to accurately determine returned datacenter id.";

            RETURN_CODE=1;
        fi
    fi

    unset RETURNED_IP;
    unset PROXY;
    unset PING_RCODE;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    RETURN_CODE=${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#      NAME:  usage
#   DESCRIPTION:  Provide information on the usage of this application
#    PARAMETERS:  None
#   RETURNS:  0
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Performs a DNS query against the nameserver specified.";
    print "Usage: ${CNAME} [-u url] [-e execute] [-?|-h show this help]";
    print "  -u      URL/IP address to query";
    print "  -e      Execute processing";
    print "  -h|-?   Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

[ ${#} -eq 0 ] && usage;

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

while getopts ":u:eh:" OPTIONS
do
    case "${OPTIONS}" in
        u)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_URL..";

            ## Capture the filename to work on
            typeset -l SITE_URL="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_URL -> ${SITE_URL}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${SITE_URL}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The site url was not provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=29;
            else
                ## We have enough information to process the request, continue
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                returnARecord;
            fi
            ;;
        h)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
        [\?])
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done

shift ${OPTIND}-1;

return ${RETURN_CODE};
