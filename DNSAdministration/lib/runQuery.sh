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
## Application constants
[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

[[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[ ${#} -eq 0 ] && usage;

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

unset METHOD_NAME;
unset CNAME;

## check security
. ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/security/check_main.sh > /dev/null 2>&1;
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Security configuration does not allow the requested action." && exit ${RET_CODE};

## unset the return code
unset RET_CODE;

CNAME="$(basename "${0}")";
METHOD_NAME="${CNAME}#startup";

#===  FUNCTION  ===============================================================
#          NAME:  returnResponse
#   DESCRIPTION:  Returns a full response from DiG for a provided address
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function returnResponse
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing DiG query against ${NAMESERVER} for ${SITE_URL}..";

    ## kill the file if it exists
    [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && rm -rf ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

    if [ -z "${NAMESERVER}" ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Nameserver not provided. Defaulting to ${NAMED_MASTER}";
        NAMESERVER=${NAMED_MASTER};
    fi

    if [ -z "${RECORD_TYPE}" ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Record type not provided. Defaulting to A";
        RECORD_TYPE=A;
    fi

    ## spawn an ssh connection to the provided server to run a DiG query
    ## check to see if we have an internal or external box
    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution is ON. Processing..";

        if [ ${JAVA_RUNNABLE} ]
        then
            $(dig @${NAMESERVER} -t ${RECORD_TYPE} ${SITE_URL});
        else
            $(dig @${NAMESERVER} -t ${RECORD_TYPE} ${SITE_URL}) > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
        fi
    else
        if [ $(echo ${DNS_SLAVES[@]} | grep -c ${NAMESERVER}) -eq 1 ] || [ "${NAMESERVER}" = "${NAMED_MASTER}" ]
        then
            unset VALIDATE;
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Nameserver provided is DNS master. Processing..";

            if [ ${JAVA_RUNNABLE} ]
            then
                ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMESERVER} "dig @${NAMESERVER} -t ${RECORD_TYPE} ${SITE_URL}";
            else
                ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMESERVER} "dig @${NAMESERVER} -t ${RECORD_TYPE} ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
            fi
        else
            ## we were asked to run against an external server,
            ## so lets check our proxy list for an available
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "External service requested. Verifying proxy access.";

            for PROXY in ${PROXY_SERVERS[@]}
            do
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now validating proxy ${PROXY}..";

                $(ping ${PROXY} > /dev/null 2>&1);
                PING_RCODE=${?};

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                if [ ${PING_RCODE} == 0 ]
                then
                    ## stop if its available and run the command
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Proxy access confirmed. Proxy: ${PROXY}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${PROXY} \"dig @${NAMESERVER} -t ${RECORD_TYPE} ${SITE_URL}\" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE}";

                    if [ ${JAVA_RUNNABLE} ]
                    then
                        ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${PROXY} "dig @${NAMESERVER} -t ${RECORD_TYPE} ${SITE_URL}";
                    else
                        ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${PROXY} "dig @${NAMESERVER} -t ${RECORD_TYPE} ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                    fi

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
    fi

    if [ ! ${JAVA_RUNNABLE} ]
    then
        if [ -s ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
            unset NAMESERVER;
            unset RECORD_TYPE;
            unset SITE_URL;
            RETURN_CODE=0;
        else
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while processing the request. Please try again.";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
            unset NAMESERVER;
            unset RECORD_TYPE;
            unset SITE_URL;
            RETURN_CODE=999;
        fi
    fi

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  returnShortResponse
#   DESCRIPTION:  Returns a short response from DiG for a provided address
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function returnShortResponse
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing DiG query against ${NAMESERVER} for ${SITE_URL}..";

    ## kill the file if it exists
    [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ] && rm -rf ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

    if [ -z "${NAMESERVER}" ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Nameserver not provided. Defaulting to ${NAMED_MASTER}";
        NAMESERVER=${NAMED_MASTER};
    fi

    ## spawn an ssh connection to the provided server to run a DiG query
    ## check to see if we have an internal or external box
    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
    then
        if [ ${JAVA_RUNNABLE} ]
        then
            dig @${NAMESERVER} +short -t ${RECORD_TYPE} ${SITE_URL};
        else
            dig @${NAMESERVER} +short -t ${RECORD_TYPE} ${SITE_URL} > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
        fi
    else
        if [ $(echo ${DNS_SLAVES[@]} | grep -c ${NAMESERVER}) -eq 1 ] || [ "${NAMESERVER}" = "${NAMED_MASTER}" ]
        then
            if [ ${JAVA_RUNNABLE} ]
            then
                ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMESERVER} "dig @${NAMESERVER} +short -t ${RECORD_TYPE} ${SITE_URL}";
            else
                ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMESERVER} "dig @${NAMESERVER} +short -t ${RECORD_TYPE} ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
            fi
        else
            ## we were asked to run against an external server,
            ## so lets check our proxy list for an available
            for PROXY in ${PROXY_SERVERS[@]}
            do
                $(ping ${PROXY} > /dev/null 2>&1);

                PING_RCODE=${?}

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                if [ ${PING_RCODE} == 0 ]
                then
                    ## stop if its available and run the command
                    if [ ${JAVA_RUNNABLE} ]
                    then
                        ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${PROXY} "dig @${NAMESERVER} +short -t ${RECORD_TYPE} ${SITE_URL}";
                    else
                        ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${PROXY} "dig @${NAMESERVER} +short -t ${RECORD_TYPE} ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                    fi

                    break;
                ## first one wasnt available, check the remaining
                else
                    continue;
                fi
            done

            ## unset unneeded variable
            unset PING_RCODE;
            unset PROXY;
        fi
    fi

    if [ ! ${JAVA_RUNNABLE} ]
    then
        if [ -s ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
            unset NAMESERVER;
            unset RECORD_TYPE;
            unset SITE_URL;
            RETURN_CODE=0;
        else
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while processing the request. Please try again.";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
            unset NAMESERVER;
            unset RECORD_TYPE;
            unset SITE_URL;
            RETURN_CODE=999;
        fi
    fi

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  returnReverseResponse
#   DESCRIPTION:  Returns a full reverse lookup response from DiG for a
#                 provided address
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function returnReverseResponse
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing DiG query against ${NAMESERVER} for ${SITE_URL}..";

    ## kill the file if it exists
    [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE}-${SERVER} ] && rm -rf ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE}-${SERVER};

    if [ -z "${NAMESERVER}" ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Nameserver not provided. Defaulting to ${NAMED_MASTER}";
        NAMESERVER=${NAMED_MASTER};
    fi

    ## make sure we got an IP address. if we didn't we need to translate.
    if [ $(${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validate_ip_address.sh ${SITE_URL}) -ne 0 ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "We were provided a hostname. Translating to IP address..";

        ## we got a name. translate it back to an IP.
        if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
        then
            SITE_URL=$(dig @${NAMESERVER} +short -t A ${SITE_URL});
        else
            if [ $(echo ${DNS_SLAVES[@]} | grep -c ${NAMESERVER}) -eq 1 ] || [ "${NAMESERVER}" = "${NAMED_MASTER}" ]
            then
                SITE_URL=$(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMESERVER} "dig @${NAMESERVER} +short -t A ${SITE_URL}");
            else
                ## we were asked to run against an external server,
                ## so lets check our proxy list for an available
                for PROXY in ${PROXY_SERVERS[@]}
                do
                    ## stop if its available and run the command
                    $(ping ${PROXY} > /dev/null 2>&1);

                    PING_RCODE=${?}

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                    if [ ${PING_RCODE} == 0 ]
                    then
                        SITE_URL=$(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${PROXY} "dig @${NAMESERVER} +short -t A ${SITE_URL}");
                        break;
                    fi
                done

                ## unset unneeded variable
                unset PING_RCODE;
                unset PROXY;
            fi
        fi
    fi

    if [ ! -z "${SITE_URL}" ]
    then
        ## spawn an ssh connection to the provided server to run a DiG query
        ## check to see if we have an internal or external box
        if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
        then
            if [ ${JAVA_RUNNABLE} ]
            then
                dig @${NAMESERVER} -x ${SITE_URL};
            else
                dig @${NAMESERVER} -x ${SITE_URL} > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
            fi
        else
            if [ $(echo ${DNS_SLAVES[@]} | grep -c ${NAMESERVER}) -eq 1 ] || [ "${NAMESERVER}" = "${NAMED_MASTER}" ]
            then
                if [ ${JAVA_RUNNABLE} ]
                then
                    ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMESERVER} "dig @${NAMESERVER} -x ${SITE_URL}";
                else
                    ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMESERVER} "dig @${NAMESERVER} -x ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                fi
            else
                ## we were asked to run against an external server,
                ## so lets check our proxy list for an available
                for PROXY in ${PROXY_SERVERS[@]}
                do
                    ## stop if its available and run the command
                    $(ping ${PROXY} > /dev/null 2>&1);

                    PING_RCODE=${?}

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                    if [ ${PING_RCODE} == 0 ]
                    then
                        if [ ${JAVA_RUNNABLE} ]
                        then
                            ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${PROXY} "dig @${NAMESERVER} -x ${SITE_URL}";
                        else
                            ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${PROXY} "dig @${NAMESERVER} -x ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                        fi

                        break;
                    ## first one wasnt available, check the remaining
                    else
                        continue;
                    fi
                done

                ## unset unneeded variable
                unset PING_RCODE;
                unset PROXY;
            fi
        fi

        if [ ! ${JAVA_RUNNABLE} ]
        then
            if [ -s ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ]
            then
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                unset NAMESERVER;
                unset RECORD_TYPE;
                unset SITE_URL;
                RETURN_CODE=0;
            else
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while processing the request. Please try again.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                unset NAMESERVER;
                unset RECORD_TYPE;
                unset SITE_URL;
                RETURN_CODE=999;
            fi
        fi
    else
        ## we were given a name to do a reverse check on,
        ## and we couldnt translate to an ip.
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while processing the request. Please try again.";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
        
        unset NAMESERVER;
        unset SITE_URL;
        
        RETURN_CODE=999;
    fi

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  returnShortReverseResponse
#   DESCRIPTION:  Returns a short reverse lookup response from DiG for a
#                 provided address
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function returnShortReverseResponse
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing DiG query against ${NAMESERVER} for ${SITE_URL}..";

    ## kill the file if it exists
    [ -f ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE}-${SERVER} ] && rm -rf ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};

    if [ -z "${NAMESERVER}" ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Nameserver not provided. Defaulting to ${NAMED_MASTER}";
        NAMESERVER=${NAMED_MASTER};
    fi

    ## make sure we got an IP address. if we didn't we need to translate.
    if [ $(${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validate_ip_address.sh ${SITE_URL}) -ne 0 ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "We were provided a hostname. Translating to IP address..";

        ## we got a name. translate it back to an IP.
        if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
        then
            SITE_URL=$(dig @${NAMESERVER} +short -t A ${SITE_URL});
        else
            if [ $(echo ${DNS_SLAVES[@]} | grep -c ${NAMESERVER}) -eq 1 ] || [ "${NAMESERVER}" = "${NAMED_MASTER}" ]
            then
                SITE_URL=$(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMESERVER} "dig @${NAMESERVER} +short -t A ${SITE_URL}");
            else
                ## we were asked to run against an external server,
                ## so lets check our proxy list for an available
                for PROXY in ${PROXY_SERVERS[@]}
                do
                    ## stop if its available and run the command
                    $(ping ${PROXY} > /dev/null 2>&1);

                    PING_RCODE=${?}

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                    if [ ${PING_RCODE} == 0 ]
                    then
                        SITE_URL=$(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${PROXY} "dig @${NAMESERVER} +short -t A ${SITE_URL}");
                        break;
                    fi
                done

                ## unset unneeded variable
                unset PING_RCODE;
                unset PROXY;
            fi
        fi
    fi

    if [ ! -z "${SITE_URL}" ]
    then
        ## spawn an ssh connection to the provided server to run a DiG query
        ## check to see if we have an internal or external box
        if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
        then
            if [ ${JAVA_RUNNABLE} ]
            then
                dig @${NAMESERVER} -x ${SITE_URL};
            else
                dig @${NAMESERVER} -x ${SITE_URL} > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
            fi
        else
            if [ $(echo ${DNS_SLAVES[@]} | grep -c ${NAMESERVER}) -eq 1 ] || [ "${NAMESERVER}" = "${NAMED_MASTER}" ]
            then
                if [ ${JAVA_RUNNABLE} ]
                then
                    ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMESERVER} "dig @${NAMESERVER} +short -x ${SITE_URL}";
                else
                    ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMESERVER} "dig @${NAMESERVER} +short -x ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                fi
            else
                ## we were asked to run against an external server,
                ## so lets check our proxy list for an available
                for PROXY in ${PROXY_SERVERS[@]}
                do
                    ## stop if its available and run the command
                    $(ping ${PROXY} > /dev/null 2>&1);

                    PING_RCODE=${?}

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                    if [ ${PING_RCODE} == 0 ]
                    then
                        if [ ${JAVA_RUNNABLE} ]
                        then
                            ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${PROXY} "dig @${NAMESERVER} +short -x ${SITE_URL}";
                        else
                            ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${PROXY} "dig @${NAMESERVER} +short -x ${SITE_URL}" > ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE};
                        fi

                        break;
                    ## first one wasnt available, check the remaining
                    else
                        continue;
                    fi
                done

                ## unset unneeded variable
                unset PING_RCODE;
                unset PROXY;
            fi
        fi

        if [ ! ${JAVA_RUNNABLE} ]
        then
            if [ -s ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ]
            then
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                unset NAMESERVER;
                unset RECORD_TYPE;
                unset SITE_URL;
                RETURN_CODE=0;
            else
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while processing the request. Please try again.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                unset NAMESERVER;
                unset RECORD_TYPE;
                unset SITE_URL;
                RETURN_CODE=999;
            fi
        fi
    else
        ## we were given a name to do a reverse check on,
        ## and we couldnt translate to an ip.
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while processing the request. Please try again.";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
        
        unset NAMESERVER;
        unset SITE_URL;
        
        RETURN_CODE=999;
    fi

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
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Performs a DNS query against the nameserver specified.";
    print "Usage: ${CNAME} [-s nameserver] [-t record type] [-u url] [-r] [-e execute] [-?|-h show this help]";
    print "  -s      Nameserver to query against. If no server is provided, the configured master nameserver is assumed.";
    print "  -t      Type of record to retrieve (if blank, A assumed)";
    print "  -u      URL/IP address to query";
    print "  -r      Request reverse mapping for provided IP address";
    print "  -o      Return a short response instead of a full response";
    print "  -e      Execute processing";
    print "  -h|-?   Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

while getopts ":s:t:u:roeh:" OPTIONS
do
    case "${OPTIONS}" in
        s)
            ## retrieve a list of all available zone files

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting NAMESERVER..";

            NAMESERVER="${OPTARG}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NAMESERVER -> ${NAMESERVER}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
            ;;
        t)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting RECORD_TYPE..";

            typeset -u RECORD_TYPE;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TYPE -> ${RECORD_TYPE}";
            ;;
        u)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_URL..";

            ## Capture the filename to work on
            SITE_URL=${OPTARG};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_URL -> ${SITE_URL}";
            ;;
        r)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting REVERSE_MAP..";

            REVERSE_MAP=${_TRUE};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REVERSE_MAP -> ${REVERSE_MAP}";
            ;;
        o)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SHORT_RESPONSE..";

            SHORT_RESPONSE=${_TRUE};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SHORT_RESPONSE -> ${SHORT_RESPONSE}";
            ;;
        e)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${SITE_URL}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The site url was not provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset VALIDATE;
                unset SITE_URL;
                RETURN_CODE=29;
            else
                ## We have enough information to process the request, continue
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset VALIDATE;

                if [ ! -z "${REVERSE_MAP}" ]
                then
                    [ ! -z "${SHORT_RESPONSE}" ] && returnShortReverseResponse || returnReverseResponse;
                else
                    [ ! -z "${SHORT_RESPONSE}" ] && returnShortResponse || returnResponse;
                fi
            fi
            ;;
        h|[\?])
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
        *)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done

shift $OPTIND-1;

return ${RETURN_CODE};
