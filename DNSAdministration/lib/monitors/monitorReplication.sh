#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  executeRNDCCommands.sh
#         USAGE:  ./executeRNDCCommands.sh
#   DESCRIPTION:  Designed to run as a cron job on a defined bastion host
#                 to provide bi-annually updates (or more often, as desired)
#                 to the root.servers cache file
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
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../lib/plugin.sh ]] && . ${SCRIPT_ROOT}/../lib/plugin.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

typeset -i OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function monitorSerialStatus
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checking synchronization status..";

    ## start processing records
    for CONFIG_ENTRY in $(ls -ltr ${NAMED_ROOT}/${NAMED_CONF_DIR} | grep ${NAMED_ZONE_CONF_NAME} | awk '{print $9}')
    do
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing configuration file ${CONFIG_ENTRY}..";

        for ZONE_ENTRY in $(grep "${ZONE_IDENT_STRING}" ${NAMED_ROOT}/${NAMED_CONF_DIR}/${CONFIG_ENTRY} ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | grep -v "file" | sed -e "s/${ZONE_IDENT_STRING} \"//" -e "s/\" IN {//")
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_ENTRY -> ${ZONE_ENTRY}";

            MASTER_SOA_SERIAL=$(dig @${NAMED_MASTER} +short -t ${SOA_TYPE_STRING} ${ZONE_ENTRY} | awk '{print $3}');

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MASTER_SOA_SERIAL -> ${MASTER_SOA_SERIAL}";

            if [ "$(isNaN ${MASTER_SOA_SERIAL})" = "${_FALSE}" ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MASTER_SOA_SERIAL -> is not a serial number. Obtaining..";

                MASTER_SOA_SERIAL=$(dig @${NAMED_MASTER} +short -t ${SOA_TYPE_STRING} ${MASTER_SOA_SERIAL} | awk '{print $3}');

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MASTER_SOA_SERIAL -> ${MASTER_SOA_SERIAL}";

                if [ "$(isNaN ${MASTER_SOA_SERIAL})" = "${_FALSE}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MASTER_SOA_SERIAL for ${ZONE_ENTRY} did not resolve to a number. Recursion level reached, failing out.";
                fi
            else
                if [ "$(isNaN ${MASTER_SOA_SERIAL})" = "${_TRUE}" ]
                then
                    for SLAVE_SERVER in ${DNS_SLAVES[@]}
                    do
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SLAVE_SERVER -> ${SLAVE_SERVER}";

                        SLAVE_SOA_SERIAL=$(dig @${SLAVE_SERVER} +short -t soa ${ZONE_ENTRY} | awk '{print $3}');

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SLAVE_SOA_SERIAL -> ${SLAVE_SOA_SERIAL}";

                        if [ "$(isNaN ${MASTER_SOA_SERIAL})" = "${_FALSE}" ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SLAVE_SOA_SERIAL -> is not a serial number. Obtaining..";

                            SLAVE_SOA_SERIAL=$(dig @${NAMED_MASTER} +short -t ${SOA_TYPE_STRING} ${SLAVE_SOA_SERIAL} | awk '{print $3}');

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SLAVE_SOA_SERIAL -> ${SLAVE_SOA_SERIAL}";

                            if [ "$(isNaN ${SLAVE_SOA_SERIAL})" = "${_FALSE}" ]
                            then
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SLAVE_SOA_SERIAL for ${ZONE_ENTRY} did not resolve to a number. Recursion level reached, failing out.";
                            else
                                if [ ${MASTER_SOA_SERIAL} != ${SLAVE_SOA_SERIAL} ]
                                then
                                    ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Serial number for zone ${ZONE_ENTRY} on ${NAMED_MASTER} does NOT match slave ${SLAVE_SERVER}. Master serial: ${MASTER_SOA_SERIAL}, Slave serial: ${SLAVE_SOA_SERIAL}";
                                fi
                            fi
                        else
                            if [ ${MASTER_SOA_SERIAL} != ${SLAVE_SOA_SERIAL} ]
                            then
                                ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Serial number for zone ${ZONE_ENTRY} on ${NAMED_MASTER} does NOT match slave ${SLAVE_SERVER}. Master serial: ${MASTER_SOA_SERIAL}, Slave serial: ${SLAVE_SOA_SERIAL}";
                            fi
                        fi
                    done
                fi
            fi
        done
    done

    unset CONFIG_ENTRY;
    unset ZONE_ENTRY;
    unset MASTER_SOA_SERIAL;
    unset SLAVE_SOA_SERIAL;

    RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function monitorAddressSynchronization
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
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Execute RNDC (Remote Name Daemon Control) commands against a provided server";
    print "Usage: ${CNAME} [ -s ] [ -a ] [-e] [-h|?]";
    print " -s    -> Execute serial number validation";
    print " -a    -> Execute address validation";
    print " -e    -> Execute the request";
    print " -h|-? -> Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 3;
}

[ ${#} -eq 0 ] && usage;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

[ ${#} -eq 0 ] && monitorSerialStatus && monitorAddressSynchronization;
[ "${1}" = "${MONITOR_SERIAL}" ] && monitorSerialStatus;
[ "${1}" = "${MONITOR_ADDRESS}" ] && monitorAddressSynchronization;

echo ${RETURN_CODE};

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

exit ${RETURN_CODE};
