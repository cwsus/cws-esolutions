#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  retrieveSiteList.sh
#         USAGE:  ./retrieveSiteList.sh server_name
#   DESCRIPTION:  Connects to the provided DNS server and restarts the named
#                 process. Utilized to apply pending changes, or to recycle
#                 the service if required for any other reason.
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
## Application constants
PLUGIN_NAME="webadmin";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#          NAME:  listVerifiableSites
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function obtainVerifiableDomains
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing monitor on: ${HOSTNAME}";

    ## ok, lets sort out where we are and what to look for
    if [ ! -z "${WS_PLATFORM}" ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WS_PLATFORM -> ${WS_PLATFORM}";

        if [ "${WS_PLATFORM}" = "${IPLANET_TYPE_IDENTIFIER}" ]
        then
            ## ok, we know we're on an iPlanet server. poll for the list of servers to validate
            set -A RETRIEVED_INSTANCE_LIST $(ls -ltr ${IPLANET_ROOT} | grep ${IPLANET_CERT_STORE_PREFIX} | awk '{print $9}');

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETRIEVED_INSTANCE_LIST -> ${RETRIEVED_INSTANCE_LIST}";

            if [ ! -z "${RETRIEVED_INSTANCE_LIST}" ]
            then
                ## ok, validate it
                for WEBSERVER in ${RETRIEVED_INSTANCE_LIST[@]}
                do
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEBSERVER -> ${WEBSERVER}";

                    if [ $(grep -c ${WEBSERVER} ${APP_ROOT}/${CORE_EXCEPTION_LIST}) == 0 ] \
                        && [ $(grep -c ${WEBSERVER} ${APP_ROOT}/${TMP_EXCEPTION_LIST}) == 0 ] \
                        && [ $(echo ${IPLANET_STARTUP_IGNORE_LIST} | grep -c ${WEBSERVER}) == 0 ]
                    then
                        ## only pull out the web instances that have ssl enabled, via security="on"
                        set -A RETRIEVED_WEB_INSTANCES ${VALIDATE_WEB_INSTANCES[@]} \
                            $(grep ${IPLANET_SECURITY_IDENTIFIER} ${IPLANET_ROOT}/${WEBSERVER}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG} | \
                                sed -e "s/${IPLANET_SERVERNAME_IDENTIFIER}=\"/@/" | cut -d "@" -f 2 | cut -d "\"" -f 1 | sort | uniq);

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_WEB_INSTANCES -> ${VALIDATE_WEB_INSTANCES[@]}";
                    fi
                done

                if [ ! -z "${RETRIEVED_WEB_INSTANCES}" ]
                then
                    ## at least one website was found and returned for validation
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Web instances listed and returned.";

                    echo ${RETRIEVED_WEB_INSTANCES[@]};

                    RETURN_CODE=0;
                else
                    ## no web instances were found or no urlhosts were identified
                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_WEB_INSTANCES was found to be empty. Cannot continue.";

                    echo ${_FALSE};

                    RETURN_CODE=0;
                fi
            else
                ## no web instances
                ${LOGGER} WARN "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_SERVER_LIST was found to be empty. No security-enabled sites were found for validation.";

                echo ${_FALSE};
                RETURN_CODE=0;
            fi
        else
            ## ihs platform
            echo "IHS";
        fi
    else
        ## unknown platform
        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Un-identified platform - WS_PLATFORM -> ${WS_PLATFORM}. Cannot continue.";

        RETURN_CODE=29;
    fi

    unset RETRIEVED_WEB_INSTANCES;
    unset RETRIEVED_INSTANCE_LIST;
    unset WEBSERVER;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

#===  FUNCTION  ===============================================================
#          NAME:  listVerifiableSites
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function obtainVerifiableSSLInstances
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing monitor on: ${HOSTNAME}";

    ## ok, lets sort out where we are and what to look for
    if [ ! -z "${WS_PLATFORM}" ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WS_PLATFORM -> ${WS_PLATFORM}";

        if [ "${WS_PLATFORM}" = "${IPLANET_TYPE_IDENTIFIER}" ]
        then
            ## ok, we know we're on an iPlanet server. poll for the list of servers to validate
            set -A INSTANCE_LIST $(ls -ltr ${IPLANET_ROOT} | grep ${IPLANET_CERT_STORE_PREFIX} | awk '{print $9}');

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "INSTANCE_LIST -> ${INSTANCE_LIST}";

            if [ ! -z "${INSTANCE_LIST}" ]
            then
                ## ok, validate it
                for WEBSERVER in ${INSTANCE_LIST[@]}
                do
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEBSERVER -> ${WEBSERVER}";

                    if [ $(grep -c ${WEBSERVER} ${APP_ROOT}/${CORE_EXCEPTION_LIST}) == 0 ] \
                        && [ $(grep -c ${WEBSERVER} ${APP_ROOT}/${TMP_EXCEPTION_LIST}) == 0 ] \
                        && [ $(grep -c ${WEBSERVER} ${APP_ROOT}/${SSL_EXCEPTION_LIST}) == 0 ] \
                        && [ $(echo ${IPLANET_STARTUP_IGNORE_LIST} | grep -c ${WEBSERVER}) == 0 ]
                    then
                        ## only pull out the web instances that have ssl enabled, via security="on"
                        IS_SECURITY_ENABLED=$(grep ${IPLANET_SECURITY_IDENTIFIER} ${IPLANET_ROOT}/${WEBSERVER}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG} | \
                            sed -e "s/${IPLANET_SERVERNAME_IDENTIFIER}=\"/@/" | cut -d "@" -f 2 | cut -d "\"" -f 1 | sort | uniq);

                        if [ ! -z "${IS_SECURITY_ENABLED}" ]
                        then
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IS_SECURITY_ENABLED -> ${IS_SECURITY_ENABLED}";

                            set -A VALIDATE_INSTANCE_LIST ${VALIDATE_INSTANCE_LIST[@]} ${WEBSERVER};
                        fi
                    fi
                done

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_INSTANCE_LIST -> ${VALIDATE_INSTANCE_LIST[@]}";

                if [ ! -z "${VALIDATE_INSTANCE_LIST}" ]
                then
                    ## at least one website was found and returned for validation
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Web instances listed and returned.";

                    echo ${VALIDATE_INSTANCE_LIST[@]};

                    RETURN_CODE=0;
                else
                    ## no web instances were found or no urlhosts were identified
                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_INSTANCE_LIST was found to be empty. Cannot continue.";

                    echo ${_FALSE};

                    RETURN_CODE=0;
                fi
            else
                ## no web instances
                ${LOGGER} WARN "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_SERVER_LIST was found to be empty. No security-enabled sites were found for validation.";

                echo ${_FALSE};

                RETURN_CODE=0;
            fi
        else
            ## ihs platform
            echo "IHS";
        fi
    else
        ## unknown platform
        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Un-identified platform - WS_PLATFORM -> ${WS_PLATFORM}. Cannot continue.";

        RETURN_CODE=29;
    fi

    unset VALIDATE_INSTANCE_LIST;
    unset IS_SECURITY_ENABLED;
    unset WEBSERVER;
    unset INSTANCE_LIST;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

#===  FUNCTION  ===============================================================
#          NAME:  obtainVerifiableInstances
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function obtainVerifiableInstances
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing monitor on: ${HOSTNAME}";

    ## ok, lets sort out where we are and what to look for
    if [ ! -z "${WS_PLATFORM}" ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WS_PLATFORM -> ${WS_PLATFORM}";

        if [ "${WS_PLATFORM}" = "${IPLANET_TYPE_IDENTIFIER}" ]
        then
            ## ok, we know we're on an iPlanet server. poll for the list of servers to validate
            set -A VALIDATE_SERVER_LIST $(ls -ltr ${IPLANET_ROOT} | grep ${IPLANET_CERT_STORE_PREFIX} | awk '{print $9}');

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_SERVER_LIST -> ${VALIDATE_SERVER_LIST}";

            if [ ! -z "${VALIDATE_SERVER_LIST}" ]
            then
                echo ${VALIDATE_SERVER_LIST[@]};

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Web instances listed and returned.";

                RETURN_CODE=0;
            else
                ## no web instances were found or no urlhosts were identified
                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_SERVER_LIST was found to be empty. Cannot continue.";

                echo ${_FALSE};

                RETURN_CODE=0;
            fi
        else
            ## ihs platform
            echo "IHS";
        fi
    else
        ## unknown platform
        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Un-identified platform - WS_PLATFORM -> ${WS_PLATFORM}. Cannot continue.";

        RETURN_CODE=29;
    fi

    unset VALIDATE_INSTANCE_LIST;
    unset IS_SECURITY_ENABLED;
    unset WEBSERVER;
    unset VALIDATE_SERVER_LIST;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function usage
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Monitor and provide information for housed servers.";
    print "Usage: $0";
    print " No arguments are required to operate this utility.";
    print " -h|-? -> Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh || \
    echo "Failed to locate configuration data. Cannot continue.";
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

[ ${#} -eq 0 ] && usage;

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

[ "${1}" = "certdb" ] && obtainVerifiableSSLInstances;
[ "${1}" = "sslcert" ] && obtainVerifiableDomains;
[ "${1}" = "status" ] && obtainVerifiableInstances;

exit ${RETURN_CODE};
