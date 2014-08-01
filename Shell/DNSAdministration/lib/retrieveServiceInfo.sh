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
#        AUTHOR:  Kevin Huntly <kmhuntly@gmail.com>
#       COMPANY:  CaspersBox Web Services
#       VERSION:  1.0
#       CREATED:  ---
#      REVISION:  ---
#==============================================================================

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

## Application constants
CNAME="$(/usr/bin/env basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}/${0##*/}")";
SCRIPT_ROOT="$(/usr/bin/env dirname "${SCRIPT_ABSOLUTE_PATH}")";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +vx;

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -f "${SCRIPT_ROOT}/../lib/plugin" ] && . "${SCRIPT_ROOT}/../lib/plugin";

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

[ -z "${APP_ROOT}" ] && awk -F "=" '/\<1\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

[ -f "${PLUGIN_LIB_DIRECTORY}/aliases" ] && . "${PLUGIN_LIB_DIRECTORY}/aliases";
[ -f "${PLUGIN_LIB_DIRECTORY}/functions" ] && . "${PLUGIN_LIB_DIRECTORY}/functions";

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

THIS_CNAME="${CNAME}";
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +vx;

## validate the input
"${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh" -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

CNAME="${THIS_CNAME}";
typeset METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
then
    "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security violation found while executing ${CNAME} by ${IUSER_AUDIT} on host ${SYSTEM_HOSTNAME}";
    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security configuration blocks execution. Please verify security configuration.";

    awk -F "=" '/\<request.not.authorized\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

    return ${RET_CODE};
fi

#===  FUNCTION  ===============================================================
#          NAME:  obtainInternetService
#   DESCRIPTION:  Executes the necessary request against the primary DNS server
#                 to further the process of a failover request.
#    PARAMETERS:  None
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function obtainInternetService
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting request detail..";

    ## configure the request indicators
    typeset REQUEST_TYPE=$(cut -d "," -f 1 <<< "${1}");
    typeset REQUEST_OPTION=$(cut -d "," -f 2 <<< "${1}");

    [ ! -z "${SERVICE_DETAIL}" ] && unset SERVICE_DETAIL;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_TYPE set to ${REQUEST_TYPE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_OPTION set to ${REQUEST_OPTION}";

    ## If we were invoked with verbosity turned on, carry it through
    if [ ! -z "${LOCAL_EXECUTION}" ] && [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command executeDataRetrieval.sh -${REQUEST_TYPE} ${REQUEST_OPTION} -e";

        set -A SERVICE_DETAIL $("${PLUGIN_LIB_DIRECTORY}/executors/executeDataRetrieval.sh -${REQUEST_TYPE} ${REQUEST_OPTION} -e");
    else
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ssh ${NAMED_MASTER} \"executeDataRetrieval.sh -${REQUEST_TYPE} ${REQUEST_OPTION} -e\"";

        for DNS_SERVER in ${INTERNET_DNS_SERVERS[*]}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS_SERVER -> ${DNS_SERVER}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating availability...";

            typeset THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

            ## validate the input
            validateServerAvailability ${DNS_SERVER};
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ ! -z "${RET_CODE}" ] && [ ${RET_CODE} -eq 0 ]
            then
                set -A SERVICE_DETAIL $(ssh ${DNS_SERVER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeDataRetrieval.sh -${REQUEST_TYPE} ${REQUEST_OPTION} -e");

                break;
            fi

            "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${DNS_SERVER} appears to be unavailable. Continuing.";

            continue;
        done
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_DETAIL -> ${SERVICE_DETAIL[*]}";

    [ ! -z "${SERVICE_DETAIL[*]}" ] && [ ! -z "${PRINT_RESPONSE}" ] && [ "${PRINT_RESPONSE}" = "${_TRUE}" ] && echo ${SERVICE_DETAIL[*]};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset SERVICE_DETAIL;
    unset REQUEST_OPTION;
    unset REQUEST_TYPE;
    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

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
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    if [ -z "${1}" ]
    then
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No site hostname was provided. Cannot continue.";

        RETURN_CODE=29;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset DISABLE_POP;
        unset ENABLE_POP;
        unset STATUS;
        unset POP_STATUS;
        unset POP_NAME;
        unset POP_NAMES;
        unset END_LINE_NUMBER;
        unset START_LINE_NUMBER;
        unset VHOST_LINE_NUMBER;
        unset RET_CODE;
        unset GD_SERVER;
        unset SITE_VHOST_NAME;
        unset POPS_LINE_NUMBER;
        unset POPE_LINE_NUMBER;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        return ${RETURN_CODE};
    fi

    typeset SITE_VHOST_NAME="${1}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_VHOST_NAME -> ${SITE_VHOST_NAME}";

    ## we have a domain name, lets go fetch its "INFO"
    if [ ! -z "${LOCAL_EXECUTION}" ] && [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command gdctl -k ..";

        gdctl -k | sed -e "s/^M//g" > "${PLUGIN_CONF_ROOT}/${GD_CONFIG_FILE}";
    else
        ERROR_COUNT=0;

        for GD_SERVER in ${GD_SERVERS}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now validating proxy ${GD_SERVER}..";

            typeset THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

            ## validate the input
            validateServerAvailability ${GD_SERVER};
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ ${RET_CODE} -ne 0 ]
            then
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server appears unavailable -> GD_SERVER -> ${GD_SERVER}";

                unset RET_CODE;

                (( ERROR_COUNT += 1 ));

                continue;
            fi

            ## stop if its available and run the command
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server availability confirmed. GD_SERVER -> ${GD_SERVER}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ssh ${GD_SERVER} \"gdctl -k\" > ${TMP_DIRECTORY}/${GD_CONFIG_FILE}";

            typeset THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

            ## validate the input
            ssh ${GD_SERVER} "gdctl -k" | sed -e "s/^M//g" > "${TMP_DIRECTORY}/${GD_CONFIG_FILE}";
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            break;
        done
    fi

    if [ ${ERROR_COUNT} -ne 0 ]
    then
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to obtain data from the configured hosts. Cannot continue.";

        RETURN_CODE=29;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset DISABLE_POP;
        unset ENABLE_POP;
        unset STATUS;
        unset POP_STATUS;
        unset POP_NAME;
        unset POP_NAMES;
        unset END_LINE_NUMBER;
        unset START_LINE_NUMBER;
        unset VHOST_LINE_NUMBER;
        unset RET_CODE;
        unset GD_SERVER;
        unset SITE_VHOST_NAME;
        unset POPS_LINE_NUMBER;
        unset POPE_LINE_NUMBER;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        return ${RETURN_CODE};
    fi

    if [ ! -s ${GD_CONFIG_FILE} ]
    then
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to obtain data from the configured hosts. Cannot continue.";

        RETURN_CODE=14;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset DISABLE_POP;
        unset ENABLE_POP;
        unset STATUS;
        unset POP_STATUS;
        unset POP_NAME;
        unset POP_NAMES;
        unset END_LINE_NUMBER;
        unset START_LINE_NUMBER;
        unset VHOST_LINE_NUMBER;
        unset RET_CODE;
        unset GD_SERVER;
        unset SITE_VHOST_NAME;
        unset POPS_LINE_NUMBER;
        unset POPE_LINE_NUMBER;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        return ${RETURN_CODE};
    fi

    ## xlnt, we have the data file. get the "INFO" we need
    typeset VHOST_LINE_NUMBER=$(sed -n "/${SITE_VHOST_NAME}/=" "${TMP_DIRECTORY}/${GD_CONFIG_FILE}" | head -1);

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VHOST_LINE_NUMBER -> ${VHOST_LINE_NUMBER}";

    if [ -z ${VHOST_LINE_NUMBER} ]
    then
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to obtain data from the configured hosts. Cannot continue.";

        RETURN_CODE=12;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset DISABLE_POP;
        unset ENABLE_POP;
        unset STATUS;
        unset POP_STATUS;
        unset POP_NAME;
        unset POP_NAMES;
        unset END_LINE_NUMBER;
        unset START_LINE_NUMBER;
        unset VHOST_LINE_NUMBER;
        unset RET_CODE;
        unset GD_SERVER;
        unset SITE_VHOST_NAME;
        unset POPS_LINE_NUMBER;
        unset POPE_LINE_NUMBER;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        return ${RETURN_CODE};
    fi

    typeset START_LINE_NUMBER=$((${VHOST_LINE_NUMBER}-2));
    typeset END_LINE_NUMBER=$((${VHOST_LINE_NUMBER}+19));
    typeset POP_NAMES=$(sed -n -e "${START_LINE_NUMBER},${END_LINE_NUMBER}p" ${GD_CONFIG_FILE} \ |
        grep "${GD_POP_IDENTIFIER}" | cut -d "=" -f 2 | sed -e "s/^ *//g" -e "s/;//g");

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VHOST_LINE_NUMBER -> ${VHOST_LINE_NUMBER}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "START_LINE_NUMBER -> ${START_LINE_NUMBER}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "END_LINE_NUMBER -> ${END_LINE_NUMBER}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POP_NAMES -> ${POP_NAMES}";

    if [ -z ${POP_NAMES} ]
    then
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to obtain data from the configured hosts. Cannot continue.";

        RETURN_CODE=28;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset DISABLE_POP;
        unset ENABLE_POP;
        unset STATUS;
        unset POP_STATUS;
        unset POP_NAME;
        unset POP_NAMES;
        unset END_LINE_NUMBER;
        unset START_LINE_NUMBER;
        unset VHOST_LINE_NUMBER;
        unset RET_CODE;
        unset GD_SERVER;
        unset SITE_VHOST_NAME;
        unset POPS_LINE_NUMBER;
        unset POPE_LINE_NUMBER;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        return ${RETURN_CODE};
    fi

    ## nifty, we have a pop list. we can continue forward
    for POP_NAME in ${POP_NAMES}
    do
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POP_NAME -> ${POP_NAME}";

        typeset POPS_LINE_NUMBER=$(grep -n "label = ${POP_NAME}" "${TMP_DIRECTORY}/${GD_CONFIG_FILE}" | cut -d ":" -f 1);
        typeset POPE_LINE_NUMBER=$((${POPS_LINE_NUMBER}+1))
        set -A POP_STATUS ${POP_STATUS[*]} $(echo "${POP_NAME}|$(sed -n -e "${POPE_LINE_NUMBER}p" \
            "${PLUGIN_ROOT_DIR}"/${GD_CONFIG_FILE} | cut -d "=" -f 2 | sed -e "s/^ *//g" -e "s/;//g")")

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POPS_LINE_NUMBER -> ${POPS_LINE_NUMBER}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POPE_LINE_NUMBER -> ${POPE_LINE_NUMBER}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POP_STATUS -> ${POP_STATUS[*]}";
    done

    if [ -z "${POP_STATUS[*]}" ]
    then
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to obtain data from the configured hosts. Cannot continue.";

        RETURN_CODE=13;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset DISABLE_POP;
        unset ENABLE_POP;
        unset STATUS;
        unset POP_STATUS;
        unset POP_NAME;
        unset POP_NAMES;
        unset END_LINE_NUMBER;
        unset START_LINE_NUMBER;
        unset VHOST_LINE_NUMBER;
        unset RET_CODE;
        unset GD_SERVER;
        unset SITE_VHOST_NAME;
        unset POPS_LINE_NUMBER;
        unset POPE_LINE_NUMBER;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        return ${RETURN_CODE};
    fi

    for STATUS in ${POP_STATUS[*]}
    do
        if [ "$(cut -d "|" -f 2 <<< ${STATUS})" = "no" ]
        then
            typeset ENABLE_POP=$(cut -d "|" -f 1 <<< ${STATUS});
        fi

        if [ "$(cut -d "|" -f 2 <<< ${STATUS})" = "yes" ]
        then
            typeset DISABLE_POP=$(cut -d "|" -f 1 <<< ${STATUS});
        fi

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENABLE_POP -> ${ENABLE_POP}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DISABLE_POP -> ${DISABLE_POP}";
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset DISABLE_POP;
    unset ENABLE_POP;
    unset STATUS;
    unset POP_STATUS;
    unset POP_NAME;
    unset POP_NAMES;
    unset END_LINE_NUMBER;
    unset START_LINE_NUMBER;
    unset VHOST_LINE_NUMBER;
    unset RET_CODE;
    unset GD_SERVER;
    unset SITE_VHOST_NAME;
    unset POPS_LINE_NUMBER;
    unset POPE_LINE_NUMBER;
    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  retrieveZoneFiles
#   DESCRIPTION:  Executes the necessary request against the primary DNS server
#                 to further the process of a failover request.
#    PARAMETERS:  None
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function retrieveZoneFiles
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;
    RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting request detail..";

    ## If we were invoked with verbosity turned on, carry it through
    if [ ! -z "${LOCAL_EXECUTION}" ] && [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
    then
        ## do some stuff here
        cp -R ${NAMED_SLAVE_DIR}/${GROUP_ID}${BUSINESS_UNIT} "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT} > /dev/null 2>&1;
    else
        [ "${TYPE_IDENTIFIER}" = "${INTERNET_TYPE_IDENTIFIER}" ] && SERVER_LIST=${INTERNET_DNS_SERVERS[*]};
        [ "${TYPE_IDENTIFIER}" = "${INTRANET_TYPE_IDENTIFIER}" ] && SERVER_LIST=${INTRANET_DNS_SERVERS[*]};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_LIST -> ${SERVER_LIST[*]}";

        for DNS_SERVER in ${SERVER_LIST[*]}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS_SERVER -> ${DNS_SERVER}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating availability...";

            typeset THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

            ## validate the input
            validateServerAvailability ${DNS_SERVER};
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ ! -z "${RET_CODE}" ] && [ ${RET_CODE} -eq 0 ]
            then
                [ "${DNS_SERVER}" = "${NAMED_MASTER}" ] && IS_SOURCE_MASTER="${_TRUE}";
                TARGET_SERVER="${DNS_SERVER}"

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IS_SOURCE_MASTER -> ${IS_SOURCE_MASTER}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_SERVER -> ${TARGET_SERVER}";

                break;
            fi

            "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${DNS_SERVER} appears to be unavailable. Continuing.";

            continue;
        done

        if [ -z "${TARGET_SERVER}" ]
        then
            RETURN_CODE=12;

            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to locate a suitable server to retrieve data from. Cannot continue.";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            unset METHOD_NAME;
            unset RET_CODE;
            unset IS_SOURCE_MASTER;
            unset TARGET_SERVER;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

            return ${RETURN_CODE};
        fi

        typeset THIS_CNAME="${CNAME}";
        unset METHOD_NAME;
        unset CNAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        ## validate the input
        [ ! -z "${IS_SOURCE_MASTER}" ] && [ "${IS_SOURCE_MASTER}" = "${_TRUE}" ] && "${APP_ROOT}/${LIB_DIRECTORY}"/tcl/runSCPConnection.exp remote-copy ${NAMED_MASTER_DIR}/${GROUP_ID}${BUSINESS_UNIT} "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT} ${TARGET_SERVER} ${SSH_USER_NAME} ${SSH_USER_AUTH};
        [ -z "${IS_SOURCE_MASTER}" ] && "${APP_ROOT}/${LIB_DIRECTORY}"/tcl/runSCPConnection.exp remote-copy ${NAMED_SLAVE_DIR}/${GROUP_ID}${BUSINESS_UNIT} "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT} ${TARGET_SERVER} ${SSH_USER_NAME} ${SSH_USER_AUTH};
        typeset -i RET_CODE=${?};

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;

        CNAME="${THIS_CNAME}";
        typeset METHOD_NAME="${THIS_CNAME}#${0}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

        if [ -z "${RET_CODE}" ] || [ "${RET_CODE}" -ne 0 ]
        then
            RETURN_CODE=50;

            "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while obtaining data from ${TARGET_HOST}. Cannot continue.";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            unset METHOD_NAME;
            unset RET_CODE;
            unset IS_SOURCE_MASTER;
            unset TARGET_SERVER;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

            return ${RETURN_CODE};
        fi
    fi

    if [ ! -d "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT} ]
    then
        RETURN_CODE=50;

        "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while obtaining data from ${TARGET_HOST}. Cannot continue.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset METHOD_NAME;
        unset RET_CODE;
        unset IS_SOURCE_MASTER;
        unset TARGET_SERVER;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        return ${RETURN_CODE};
    fi

    for DATACENTER in ${DATACENTERS[*]}
    do
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DATACENTER -> ${DATACENTER}";

        [ ! -d "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}/${DATACENTER} ] && (( ERROR_COUNT += 1 ));
    done

    if [ ${ERROR_COUNT} -ne 0 ]
    then
        RETURN_CODE=50;

        "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while obtaining data from ${TARGET_HOST}. Cannot continue.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset METHOD_NAME;
        unset RET_CODE;
        unset IS_SOURCE_MASTER;
        unset TARGET_SERVER;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

        return ${RETURN_CODE};
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset METHOD_NAME;
    unset RET_CODE;
    unset IS_SOURCE_MASTER;
    unset TARGET_SERVER;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -vx;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=3;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    echo "${THIS_CNAME} - Add zone entries to a given zonefile\n";
    echo "Usage: ${THIS_CNAME} [ -i < type identifier> ] [ -b <business unit> ] [ -r ] [ -e ] [ -h|-? ]
    -b         -> The associated business unit.
    -i         -> The associated type identifier, internet or intranet.
    -r         -> Retrieve zone files from the remote system.
    -s         -> Execute processing
    -h|-?      -> Show this help\n";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;

    return ${RETURN_CODE};
}

[ ${#} -eq 0 ] && usage && RETURN_CODE=${?};

while getopts "b:i:reh" OPTIONS 2>/dev/null
do
    case ${OPTIONS} in
        b)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting BUSINESS_UNIT..";

            typeset -u BUSINESS_UNIT="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
            ;;
        i)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting TYPE_IDENTIFIER..";

            typeset -u TYPE_IDENTIFIER="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TYPE_IDENTIFIER -> ${TYPE_IDENTIFIER}";
            ;;
        r)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting RETRIEVE_FILES..";

            RETRIEVE_FILES="${_TRUE}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETRIEVE_FILES -> ${RETRIEVE_FILES}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating data..";

            if [ -z "${BUSINESS_UNIT}" ]
            then
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No business unit was provided. Unable to continue processing.";

                RETURN_CODE=15;
            elif [ -z "${TYPE_IDENTIFIER}" ]
            then
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No type identifier was provided. Unable to continue processing.";

                RETURN_CODE=24;
            else
                [ ! -z "${RETRIEVE_FILES}" ] && [ "${RETRIEVE_FILES}" = "${_TRUE}" ] && retrieveZoneFiles && RETURN_CODE=${?};
                [ ! -z "${TYPE_IDENTIFIER}" ] && [ "${TYPE_IDENTIFIER}" = "${INTERNET_TYPE_IDENTIFIER}" ] && obtainInternetService && RETURN_CODE=${?};
                [ ! -z "${TYPE_IDENTIFIER}" ] && [ "${TYPE_IDENTIFIER}" = "${INTRANET_TYPE_IDENTIFIER}" ] && obtainIntranetService && RETURN_CODE=${?};
            fi
            ;;
        *)
            usage && RETURN_CODE=${?};
            ;;
    esac
done

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset BUSINESS_UNIT;
unset TYPE_IDENTIFIER;
unset RETRIEVE_FILES;
unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +vx;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +vx;

[ -z "${RETURN_CODE}" ] && return 1 || return "${RETURN_CODE}";
