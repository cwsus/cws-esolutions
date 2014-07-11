#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  runQuery.sh.sh
#         USAGE:  ./runQuery.sh.sh
#   DESCRIPTION:  Processes backout requests for previously executed change
#                 requests.
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
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

## Application constants
CNAME="$(/usr/bin/env basename ${0})";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; /usr/bin/env echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname ${SCRIPT_ABSOLUTE_PATH})";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -f ${SCRIPT_ROOT}/../lib/plugin ] && . ${SCRIPT_ROOT}/../lib/plugin;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${PLUGIN_ROOT_DIR}" ] && /usr/bin/env echo "Failed to locate configuration data. Cannot continue." && return 1;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -f ${APP_ROOT}/${LIB_DIRECTORY}/aliases ] && . ${APP_ROOT}/${LIB_DIRECTORY}/aliases;
[ -f ${APP_ROOT}/${LIB_DIRECTORY}/functions ] && . ${APP_ROOT}/${LIB_DIRECTORY}/functions;
[ -s ${PLUGIN_LIB_DIRECTORY}/aliases ] && . ${PLUGIN_LIB_DIRECTORY}/aliases;
[ -s ${PLUGIN_LIB_DIRECTORY}/functions ] && . ${PLUGIN_LIB_DIRECTORY}/functions;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

THIS_CNAME="${CNAME}";
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

## validate the input
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

CNAME="${THIS_CNAME}";
typeset METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
then
    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security violation found while executing ${CNAME} by ${IUSER_AUDIT} on host ${SYSTEM_HOSTNAME}";
    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security configuration blocks execution. Please verify security configuration.";

    echo "Security configuration does not allow the requested action.";

    return ${RET_CODE};
fi

#===  FUNCTION  ===============================================================
#          NAME:  returnResponse
#   DESCRIPTION:  Returns a full response from DiG for a provided address
#    PARAMETERS:  None
#==============================================================================
function returnResponse
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing DiG query against ${NAMESERVER} for ${SITE_URL}..";

    [ -z "${NAMESERVER}" ] && [ -z "${SHORT_RESPONSE}" ] && DIG_CMD="/usr/bin/env dig +noedns";
    [[ ! -z "${NAMESERVER}" && -z "${SHORT_RESPONSE}" ]] && DIG_CMD="/usr/bin/env dig @${NAMESERVER} +noedns";
    [[ -z "${NAMESERVER}" && "${SHORT_RESPONSE}" = "${_TRUE}" ]] && DIG_CMD="/usr/bin/env dig +noedns +short";
    [[ ! -z "${NAMESERVER}" && "${SHORT_RESPONSE}" = "${_TRUE}" ]] && DIG_CMD="/usr/bin/env dig @${NAMESERVER} +noedns +short";

    ## kill the file if it exists
    [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && rm -rf ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

    [ -z "${RECORD_TYPE}" ] && RECORD_TYPE="A";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NAMESERVER -> ${NAMESERVER}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TYPE -> ${RECORD_TYPE}";

    ## spawn an ssh connection to the provided server to run a DiG query
    ## check to see if we have an internal or external box
    if [ ! -z "${LOCAL_EXECUTION}" ] && [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution is ON. Processing..";

        ${DIG_CMD} -t ${RECORD_TYPE} ${SITE_URL} > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
    else
        for SERVER in ${NAMESERVERS[@]}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER -> ${SERVER}";

            if [ "${NAMESERVER}" = "${SERVER}" ]
            then
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ssh ${SERVER} "${DIG_CMD} +short -t ${RECORD_TYPE} ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                REQUEST_COMPLETE="${_TRUE}";

                break;
            fi

            continue;
        done

        if [[ -z "${REQUEST_COMPLETE}" || "${REQUEST_COMPLETE}" = "${_FALSE}" ]]
        then
            ## check to see if this is an internal or external host. if its internal we don't need to
            ## route through a proxy, if its external then we do
            if [[ ! -z "$(dig +short ${NAMESERVER})" || ! -z "$(dig +short -x ${NAMESERVER})" ]]
            then
                ## internal
                ${DIG_CMD} -t ${RECORD_TYPE} ${SITE_URL} > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
            else
                ## we were asked to run against an external server,
                ## so lets check our proxy list for an available
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "External service requested. Verifying proxy access.";

                for PROXY in ${PROXY_SERVERS[@]}
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROXY -> ${PROXY}";

                    typeset THIS_CNAME="${CNAME}";
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                    ## validate the input
                    validateServerAvailability ${PROXY};
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                    if [ ${RET_CODE} -eq 0 ]
                    then
                        ## stop if its available and run the command
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Proxy access confirmed. Proxy: ${PROXY}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${PROXY} \"dig @${NAMESERVER} -t ${RECORD_TYPE} ${SITE_URL}\" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE}";

                                unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                        ## validate the input
                        ssh ${PROXY} "${DIG_CMD} -t ${RECORD_TYPE} ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        break;
                    fi

                    continue;
                done
            fi
        fi
    fi

    [ -s ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && RETURN_CODE=0 || RETURN_CODE=99;
    [[ ! -z "${PRINT_RESPONSE}" && "${PRINT_RESPONSE}" = "${_TRUE}" && -s ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ]] && cat ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
    [[ ! -z "${PRINT_RESPONSE}" && "${PRINT_RESPONSE}" = "${_TRUE}" && -s ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ]] && rm -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    unset NAMESERVER;
    unset RECORD_TYPE;
    unset VALIDATE;
    unset PROXY;
    unset RET_CODE;
    unset NAMESERVER;
    unset RECORD_TYPE;
    unset SITE_URL;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  returnReverseResponse
#   DESCRIPTION:  Returns a full response from DiG for a provided address
#    PARAMETERS:  None
#==============================================================================
function returnReverseResponse
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing DiG query against ${NAMESERVER} for ${SITE_URL}..";

    ## kill the file if it exists
    [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && rm -rf ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

    [ -z "${NAMESERVER}" ] && NAMESERVER="${NAMED_MASTER}";
    [ -z "${RECORD_TYPE}" ] && RECORD_TYPE="A";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NAMESERVER -> ${NAMESERVER}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TYPE -> ${RECORD_TYPE}";

    ## spawn an ssh connection to the provided server to run a DiG query
    ## check to see if we have an internal or external box
    if [ ! -z "${LOCAL_EXECUTION}" ] && [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution is ON. Processing..";

        if [ ! -z "${SHORT_RESPONSE}" ] && [ "${SHORT_RESPONSE}" = "${_TRUE}" ]
        then
            [ -z "${NAMESERVER}" ] && dig +short -x ${SITE_URL} > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} || dig @${NAMESERVER} +short -x ${SITE_URL} > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
        else
            [ -z "${NAMESERVER}" ] && dig -x ${SITE_URL} > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} || dig @${NAMESERVER} -x ${SITE_URL} > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
        fi
    else
        for SERVER in ${DNS_SLAVES[@]} ${NAMED_MASTER}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER -> ${SERVER}";

            if [ "${NAMESERVER}" = "${SERVER}" ]
            then
                if [ ! -z "${SHORT_RESPONSE}" ] && [ "${SHORT_RESPONSE}" = "${_TRUE}" ]
                then
                    [ -z "${NAMESERVER}" ] && ssh ${NAMESERVER} "dig +short -x ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} || \
                        ssh ${NAMESERVER} "dig @${NAMESERVER} +short -x ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                else
                    [ -z "${NAMESERVER}" ] && ssh ${NAMESERVER} "dig -x ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} || \
                        ssh ${NAMESERVER} "dig @${NAMESERVER} -x ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                fi

                REQUEST_COMPLETE="${_TRUE}";

                break;
            fi

            continue;
        done

        if [[ -z "${REQUEST_COMPLETE}" || "${REQUEST_COMPLETE}" = "${_FALSE}" ]]
        then
            ## check to see if this is an internal or external host. if its internal we don't need to
            ## route through a proxy, if its external then we do
            if [[ ! -z "$(dig +short ${NAMESERVER})" || ! -z "$(dig +short -x ${NAMESERVER})" ]]
            then
                ## external
                if [ ! -z "${SHORT_RESPONSE}" ] && [ "${SHORT_RESPONSE}" = "${_TRUE}" ]
                then
                    [ -z "${NAMESERVER}" ] && dig +short -x ${SITE_URL} > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} || dig @${NAMESERVER} +short -x ${SITE_URL} > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                else
                    [ -z "${NAMESERVER}" ] && dig -x ${SITE_URL} > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} || dig @${NAMESERVER} -x ${SITE_URL} > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                fi
            else
                ## we were asked to run against an external server,
                ## so lets check our proxy list for an available
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "External service requested. Verifying proxy access.";

                for PROXY in ${PROXY_SERVERS[@]}
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROXY -> ${PROXY}";

                    typeset THIS_CNAME="${CNAME}";
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                    ## validate the input
                    validateServerAvailability ${PROXY};
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                    if [ ${RET_CODE} -eq 0 ]
                    then
                        ## stop if its available and run the command
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Proxy access confirmed. Proxy: ${PROXY}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${PROXY} \"dig @${NAMESERVER} -x ${SITE_URL}\" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE}";

                        if [ ! -z "${SHORT_RESPONSE}" ] && [ "${SHORT_RESPONSE}" = "${_TRUE}" ]
                        then
                            [ -z "${NAMESERVER}" ] && ssh ${PROXY} "dig +short -x ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} || \
                                ssh ${PROXY} "dig @${NAMESERVER} +short -x ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                        else
                            [ -z "${NAMESERVER}" ] && ssh ${PROXY} "dig -x ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} || \
                                ssh ${PROXY} "dig @${NAMESERVER} -x ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                        fi

                        break;
                    fi

                    continue;
                done
            fi
        fi
    fi

    [ -s ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && RETURN_CODE=0 || RETURN_CODE=99;
    [[ ! -z "${PRINT_RESPONSE}" && "${PRINT_RESPONSE}" = "${_TRUE}" && -s ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ]] && cat ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    unset NAMESERVER;
    unset RECORD_TYPE;
    unset VALIDATE;
    unset PROXY;
    unset RET_CODE;
    unset NAMESERVER;
    unset RECORD_TYPE;
    unset SITE_URL;

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
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=3;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    echo "${CNAME} - Performs a DNS query against the nameserver specified.\n";
    echo "Usage: ${CNAME} [ -s <nameserver> ] [ -t <record type> ] [ -u <url> ] [ -r ] [ -o ] [ -p ] [ -e ] [ -h|-? ]
    -s         -> Nameserver to query against. If no server is provided, the system default nameserver is utilized.
    -t         -> Type of record to retrieve (if blank, A assumed)
    -u         -> URL/IP address to query
    -r         -> Request reverse mapping for provided IP address
    -o         -> Return a short response instead of a full response
    -p         -> Print output to terminal
    -e         -> Execute processing
    -h|-?      -> Show this help\n";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

[ ${#} -eq 0 ] && usage&& RETURN_CODE=${?};

while getopts ":s:t:u:ropeh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        s)
            ## retrieve a list of all available zone files

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting NAMESERVER..";

            NAMESERVER="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NAMESERVER -> ${NAMESERVER}";
            ;;
        t)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting RECORD_TYPE..";

            typeset -u RECORD_TYPE="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TYPE -> ${RECORD_TYPE}";
            ;;
        u)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_URL..";

            ## Capture the filename to work on
            SITE_URL=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_URL -> ${SITE_URL}";
            ;;
        r)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting REVERSE_MAP..";

            REVERSE_MAP=${_TRUE};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REVERSE_MAP -> ${REVERSE_MAP}";
            ;;
        o)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SHORT_RESPONSE..";

            SHORT_RESPONSE=${_TRUE};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SHORT_RESPONSE -> ${SHORT_RESPONSE}";
            ;;
        p)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting PRINT_RESPONSE..";

            PRINT_RESPONSE=${_TRUE};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRINT_RESPONSE -> ${PRINT_RESPONSE}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${SITE_URL}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The site url was not provided. Unable to continue processing.";

                RETURN_CODE=29;
            else
                ## We have enough information to process the request, continue
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [[ ! -z "${REVERSE_MAP}" && "${REVERSE_MAP}" = "${_TRUE}" ]] && returnReverseResponse || returnResponse;
            fi
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage&& RETURN_CODE=${?};
            ;;
    esac
done

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

[ -z "${RETURN_CODE}" ] && return 1 || return "${RETURN_CODE}";
