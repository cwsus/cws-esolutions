#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  retrieveServiceInfo.sh
#         USAGE:  ./retrieveServiceInfo.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Obtains service information for the provided criteria
#                 from the zone file.
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
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

## Application constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; printf "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[[ -z "${PLUGIN_ROOT_DIR}" && -f ${SCRIPT_ROOT}/../lib/plugin.sh ]] && . ${SCRIPT_ROOT}/../lib/plugin.sh;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${PLUGIN_ROOT_DIR}" ] && print "Failed to locate configuration data. Cannot continue." && exit 1;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

THIS_CNAME="${CNAME}";
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

## validate the input
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

CNAME="${THIS_CNAME}";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
then
    print "Security configuration does not allow the requested action.";

    return ${RET_CODE};
fi

unset RET_CODE;

#===  FUNCTION  ===============================================================
#          NAME:  obtainInternetService
#   DESCRIPTION:  Executes the necessary request against the primary DNS server
#                 to further the process of a failover request.
#    PARAMETERS:  None
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function obtainInternetService
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting request detail..";

    ## configure the request indicators
    local REQUEST_TYPE=$(printf ${1} | cut -d "," -f 1);
    local REQUEST_OPTION=$(printf ${1} | cut -d "," -f 2);

    [ ! -z "${SERVICE_DETAIL}" ] && unset SERVICE_DETAIL;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_TYPE set to ${REQUEST_TYPE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_OPTION set to ${REQUEST_OPTION}";

    ## If we were invoked with verbosity turned on, carry it through
    if [ ! -z "${LOCAL_EXECUTION}" ] && [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${PLUGIN_ROOT_DIR}lib/executors/executeDataRetrieval.sh -${REQUEST_TYPE} ${REQUEST_OPTION} -e";

        set -A SERVICE_DETAIL $(${PLUGIN_ROOT_DIR}lib/executors/executeDataRetrieval.sh -${REQUEST_TYPE} ${REQUEST_OPTION} -e);
    else
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMED_MASTER} \"executeDataRetrieval.sh -${REQUEST_TYPE} ${REQUEST_OPTION} -e\"";

        ping ${NAMED_MASTER} > /dev/null 2>&1;
        PING_RCODE=${?};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

        if [ ${PING_RCODE} -eq 0 ]
        then
            set -A SERVICE_DETAIL $(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeDataRetrieval.sh -${REQUEST_TYPE} ${REQUEST_OPTION} -e" ${SSH_USER_NAME} ${SSH_USER_AUTH});
        fi
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_DETAIL->${SERVICE_DETAIL[@]}";

    if [ -z "${SERVICE_DETAIL[@]}" ] || [ ${#SERVICE_DETAIL[@]} -eq 0 ]
    then
        RETURN_CODE=12;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        unset SERVICE_DETAIL;
        unset REQUEST_OPTION;
        unset REQUEST_TYPE;
        unset METHOD_NAME;

        return ${RETURN_CODE};
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Command executed successfully on ${NAMED_MASTER}.";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing data..";

    [ ! -z "${PRINT_RESPONSE}" ] && [ "${PRINT_RESPONSE}" = "${_TRUE}" ] && print ${SERVICE_DETAIL[@]};

    local RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    unset SERVICE_DETAIL;
    unset REQUEST_OPTION;
    unset REQUEST_TYPE;
    unset METHOD_NAME;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  obtainIntranetService
#   DESCRIPTION:  Executes the necessary request against the primary DNS server
#                 to further the process of a failover request.
#    PARAMETERS:  None
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function obtainIntranetService
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    if [ -z "${1}" ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No site hostname was provided. Cannot continue.";

        local RETURN_CODE=29;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        unset SERVICE_DETAIL;
        unset REQUEST_OPTION;
        unset REQUEST_TYPE;
        unset METHOD_NAME;

        return ${RETURN_CODE};
    fi

    local SITE_VHOST_NAME=${1};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_VHOST_NAME -> ${SITE_VHOST_NAME}";

    ## we have a domain name, lets go fetch its "INFO"
    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command gdctl -k ..";

        gdctl -k | sed -e "s/^M//g" > ${PLUGIN_ROOT_DIR}/${GD_CONFIG_FILE};
    else
        ERROR_COUNT=0;

        for GD_SERVER in ${GD_SERVERS}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now validating proxy ${GD_SERVER}..";

            ping ${GD_SERVER} > /dev/null 2>&1;
            local PING_RCODE=${?}

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

            if [ ${PING_RCODE} -ne 0 ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server appears unavailable -> GD_SERVER -> ${GD_SERVER}";

                unset PING_RCODE;

                (( ERROR_COUNT += 1 ));

                continue;
            fi

            ## stop if its available and run the command
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server availability confirmed. GD_SERVER -> ${GD_SERVER}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${GD_SERVER} \"gdctl -k\" > ${PLUGIN_ROOT_DIR}/${GD_CONFIG_FILE}";

            ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${GD_SERVER} "gdctl -k" ${SSH_USER_NAME} ${SSH_USER_AUTH} | sed -e "s/^M//g" > ${PLUGIN_ROOT_DIR}/${GD_CONFIG_FILE};

            break;
        done
    fi

    if [ ${ERROR_COUNT} -ne 0 ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to obtain data from the configured hosts. Cannot continue.";

        local RETURN_CODE=29;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        unset SERVICE_DETAIL;
        unset REQUEST_OPTION;
        unset REQUEST_TYPE;
        unset METHOD_NAME;

        return ${RETURN_CODE};
    fi

    if [ ! -s ${GD_CONFIG_FILE} ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to obtain data from the configured hosts. Cannot continue.";

        local RETURN_CODE=14;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        unset SERVICE_DETAIL;
        unset REQUEST_OPTION;
        unset REQUEST_TYPE;
        unset METHOD_NAME;

        return ${RETURN_CODE};
    fi

    ## xlnt, we have the data file. get the "INFO" we need
    local VHOST_LINE_NUMBER=$(sed -n "/${SITE_VHOST_NAME}/=" ${PLUGIN_ROOT_DIR}/${GD_CONFIG_FILE} | head -1);

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VHOST_LINE_NUMBER -> ${VHOST_LINE_NUMBER}";

    if [ -z ${VHOST_LINE_NUMBER} ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to obtain data from the configured hosts. Cannot continue.";

        local RETURN_CODE=12;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        unset SERVICE_DETAIL;
        unset REQUEST_OPTION;
        unset REQUEST_TYPE;
        unset METHOD_NAME;

        return ${RETURN_CODE};
    fi

    local START_LINE_NUMBER=$((${VHOST_LINE_NUMBER}-2));
    local END_LINE_NUMBER=$((${VHOST_LINE_NUMBER}+19));
    local POP_NAMES=$(sed -n -e "${START_LINE_NUMBER},${END_LINE_NUMBER}p" ${GD_CONFIG_FILE} \ |
        grep "${GD_POP_IDENTIFIER}" | cut -d "=" -f 2 | sed -e "s/^ *//g" -e "s/;//g");

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VHOST_LINE_NUMBER -> ${VHOST_LINE_NUMBER}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "START_LINE_NUMBER -> ${START_LINE_NUMBER}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "END_LINE_NUMBER -> ${END_LINE_NUMBER}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POP_NAMES -> ${POP_NAMES}";

    if [ -z ${POP_NAMES} ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to obtain data from the configured hosts. Cannot continue.";

        local RETURN_CODE=28;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        unset SERVICE_DETAIL;
        unset REQUEST_OPTION;
        unset REQUEST_TYPE;
        unset METHOD_NAME;

        return ${RETURN_CODE};
    fi

    ## nifty, we have a pop list. we can continue forward
    for POP_NAME in ${POP_NAMES}
    do
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POP_NAME -> ${POP_NAME}";

        local POPS_LINE_NUMBER=$(grep -n "label = ${POP_NAME}" ${PLUGIN_ROOT_DIR}/${GD_CONFIG_FILE} | cut -d ":" -f 1);
        local POPE_LINE_NUMBER=$((${POPS_LINE_NUMBER}+1))
        set -A POP_STATUS ${POP_STATUS[@]} $(printf "${POP_NAME}|$(sed -n -e "${POPE_LINE_NUMBER}p" \
            ${PLUGIN_ROOT_DIR}/${GD_CONFIG_FILE} | cut -d "=" -f 2 | sed -e "s/^ *//g" -e "s/;//g")")

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POPS_LINE_NUMBER -> ${POPS_LINE_NUMBER}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POPE_LINE_NUMBER -> ${POPE_LINE_NUMBER}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POP_STATUS -> ${POP_STATUS[@]}";
    done

    if [ -z "${POP_STATUS[@]}" ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to obtain data from the configured hosts. Cannot continue.";

        local RETURN_CODE=13;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        unset SERVICE_DETAIL;
        unset REQUEST_OPTION;
        unset REQUEST_TYPE;
        unset METHOD_NAME;

        return ${RETURN_CODE};
    fi

    for STATUS in ${POP_STATUS[@]}
    do
        if [ "$(printf ${STATUS} | cut -d "|" -f 2)" = "no" ]
        then
            local ENABLE_POP=$(printf ${STATUS} | cut -d "|" -f 1);
        fi

        if [ "$(printf ${STATUS} | cut -d "|" -f 2)" = "yes" ]
        then
            local DISABLE_POP=$(printf ${STATUS} | cut -d "|" -f 1);
        fi

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENABLE_POP -> ${ENABLE_POP}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DISABLE_POP -> ${DISABLE_POP}";
    done

    local RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    unset DISABLE_POP;
    unset ENABLE_POP;
    unset STATUS;
    unset POP_STATUS;
    unset POP_NAME;
    unset POP_NAMES;
    unset END_LINE_NUMBER;
    unset START_LINE_NUMBER;
    unset VHOST_LINE_NUMBER;
    unset PING_RCODE;
    unset GD_SERVER;
    unset SITE_VHOST_NAME;
    unset POPS_LINE_NUMBER;
    unset POPE_LINE_NUMBER;
    unset METHOD_NAME;

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

    print "${CNAME} - Retrieve service information for provided values";
    print "Usage: ${CNAME} [ service type ] [ service values ]";
    print " Service type should be one of X or I - X for internet, I for intranet";
    print " Service values must be a comma-delimited set of information to return data against.";
    print "  -h|-?   Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 3;
}

[ ${#} -eq 0 ] && usage;
case ${1} in
    ${INTERNET_TYPE_IDENTIFIER})
        obtainInternetService ${2};
        ;;
    ${INTRANET_TYPE_IDENTIFIER})
        obtainIntranetService ${2};
        ;;
    *)
        RETURN_CODE=7;
        ;;
esac

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset THIS_CNAME;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return ${RETURN_CODE};
