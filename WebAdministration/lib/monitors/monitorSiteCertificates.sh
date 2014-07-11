#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  run_service_restart.sh
#         USAGE:  ./run_service_restart.sh server_name
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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

## Application constants
CNAME="${THIS_CNAME}";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#          NAME:  monitorCertExpiry
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function monitorCertExpiry
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    EXPIRY_EPOCH=$(returnEpochTime $(date +"%Y %m %d") ${VALIDATION_PERIOD});

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VERIFIABLE_TIME -> ${VERIFIABLE_TIME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing monitor on: ${HOSTNAME}";

    ## this method will only ever run on an ecom. it'll obtain the list of verifiable sites
    ## and then execute openssl to obtain the current certificate expiration
    ## first, build the list of servers to execute against
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generating server list...";

    for WEBSERVER in $(getMachineInfo | grep -v "^#" | grep WEB | grep sol8 | cut -d "|" -f 1 | sort | uniq; \
        getMachineInfo | grep -v "^#" | grep WEB | grep sol9 | cut -d "|" -f 1 | sort | uniq; \
        getMachineInfo | grep -v "^#" | grep WEB | grep sol10 | cut -d "|" -f 1 | sort | uniq;)
    do
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating against ${WEBSERVER}..";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${WEBSERVER} \"${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/monitors/monitorCertificates.sh listSites\" ${IPLANET_OWNING_USER}";

        SERVER_PARTITION=$(getMachineInfo | grep -w ${WEBSERVER} | grep -v "#" | cut -d "|" -f 3 | cut -d "/" -f 1-3 | sort | uniq); ## webserver type
        SERVER_REGION=$(getMachineInfo | grep -w ${WEBSERVER} | grep -v "#" | cut -d "|" -f 4 | sort | uniq); ## webserver type
        VERIFIABLE_SITES=$(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${WEBSERVER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/monitors/monitorCertificates.sh listSites" ${IPLANET_OWNING_USER});

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_ROOT -> ${SERVER_ROOT}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_REGION -> ${SERVER_REGION}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_PARTITION -> ${SERVER_PARTITION}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VERIFIABLE_SITES -> ${VERIFIABLE_SITES}";

        if [ ! -z "${VERIFIABLE_SITES}" ]
        then
            for WEBSITE in ${VERIFIABLE_SITES}
            do
                if [ "${SERVER_PARTITION}" = "${INTERNET_TYPE_IDENTIFIER}" ]
                then
                    for PROXY in ${PROXY_SERVERS[@]}
                    do
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now validating proxy ${PROXY}..";

                        $(ping ${PROXY} > /dev/null 2>&1);
                        PING_RCODE=${?};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                        if [ ${PING_RCODE} -eq 0 ]
                        then
                            ## stop if its available and run the command
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Proxy access confirmed. Proxy: ${PROXY}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command OpenSSL...";

                            ## everything is supposed to be on standard port numbers. sadly, not everything is.
                            ## check standard first, if that fails, check non-standard
                            RETURNED_EXPIRY=$(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${PROXY} "echo \"quit\n\" | \
                                openssl s_client -connect ${WEBSITE}:${STD_SSL_PORT_NUMBER} -nbio -ssl3 -mtu 1500 -bugs -rand file:${RANDOM_GENERATOR} 2>/dev/null | \
                                sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' | openssl x509 -noout -subject -dates | grep notAfter" | cut -d "=" -f 2 | \
                                awk '{print $4, $1, $2}');

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURNED_EXPIRY -> ${RETURNED_EXPIRY}";

                            if [ -z "${RETURNED_EXPIRY}" ]
                            then
                                ## guess its not a standard port. unset and re-try
                                unset RETURNED_EXPIRY;

                                RETURNED_EXPIRY=$(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${PROXY} "echo \"quit\n\" | \
                                    openssl s_client -connect ${WEBSITE}:${NONSTD_SSL_PORT_NUMBER} -nbio -ssl3 -mtu 1500 -bugs -rand file:${RANDOM_GENERATOR} 2>/dev/null | \
                                    sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' | openssl x509 -noout -subject -dates | grep notAfter" | cut -d "=" -f 2 | \
                                    awk '{print $4, $1, $2}');

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURNED_EXPIRY -> ${RETURNED_EXPIRY}";

                                if [ -z "${RETURNED_EXPIRY}" ]
                                then
                                    ## this site may not have an ssl channel
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to connect to ${WEBSITE} using both standard (${STD_SSL_PORT_NUMBER}) and non-standard (${NONSTD_SSL_PORT_NUMBER}).";
                                else
                                    ## got back a date, convert it
                                    EXPIRY_MONTH=$(echo ${RETURNED_EXPIRY} | awk '{print $2}');

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EXPIRY_MONTH -> ${EXPIRY_MONTH}";

                                    if [ ! -z "${EXPIRY_MONTH}" ]
                                    then
                                        ## ok, we have a nickname and an expiration date. convert it
                                        EPOCH_EXPIRY=$(returnEpochTime $(echo ${RETURNED_EXPIRY} | sed -e "s/${EXPIRY_MONTH}/$(eval echo \${${EXPIRY_MONTH}})/"));

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EPOCH_EXPIRY -> ${EPOCH_EXPIRY}";

                                        if [ ${EPOCH_EXPIRY} -le ${EXPIRY_EPOCH} ]
                                        then
                                            ## this certificate expires within the epoch, notify
                                            ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate ${CERT_NICKNAME} for host ${CERT_HOSTNAME} expires on ${CERT_EXPIRY}";
                                        fi
                                    else
                                        ## didnt get an expiration date
                                        ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No expiration date was received for ${WEBSITE}.";
                                    fi
                                fi
                            else
                                EXPIRY_MONTH=$(echo ${RETURNED_EXPIRY} | awk '{print $2}');

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EXPIRY_MONTH -> ${EXPIRY_MONTH}";

                                if [ ! -z "${EXPIRY_MONTH}" ]
                                then
                                    ## ok, we have a nickname and an expiration date. convert it
                                    EPOCH_EXPIRY=$(returnEpochTime $(echo ${RETURNED_EXPIRY} | sed -e "s/${EXPIRY_MONTH}/$(eval echo \${${EXPIRY_MONTH}})/"));

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EPOCH_EXPIRY -> ${EPOCH_EXPIRY}";

                                    if [ ${EPOCH_EXPIRY} -le ${EXPIRY_EPOCH} ]
                                    then
                                        ## this certificate expires within the epoch, notify
                                        ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate ${CERT_NICKNAME} for host ${CERT_HOSTNAME} expires on ${CERT_EXPIRY}";
                                    fi
                                else
                                    ## didnt get an expiration date
                                    ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No expiration date was received for ${WEBSITE}.";
                                fi
                            fi

                            break;
                        else
                            ## first one wasnt available, check the remaining
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Proxy access failed. Proxy: ${PROXY}";

                            unset PING_RCODE;
                            continue;
                        fi
                    done
                elif [ "${SERVER_PARTITION}" = "${INTRANET_TYPE_IDENTIFIER}" ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command OpenSSL...";

                    ## everything is supposed to be on standard port numbers. sadly, not everything is.
                    ## check standard first, if that fails, check non-standard
                    RETURNED_EXPIRY=$(echo "quit\n" | \
                        openssl s_client -connect ${WEBSITE}:${STD_SSL_PORT_NUMBER} -nbio -ssl3 -mtu 1500 -bugs -rand file:${RANDOM_GENERATOR} 2>/dev/null | \
                        sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' | openssl x509 -noout -subject -dates | grep notAfter | cut -d "=" -f 2 | \
                        awk '{print $4, $1, $2}');

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURNED_EXPIRY -> ${RETURNED_EXPIRY}";

                    if [ -z "${RETURNED_EXPIRY}" ]
                    then
                        ## guess its not a standard port. unset and re-try
                        unset RETURNED_EXPIRY;

                        RETURNED_EXPIRY=$(echo "quit\n" | \
                            openssl s_client -connect ${WEBSITE}:${NONSTD_SSL_PORT_NUMBER} -nbio -ssl3 -mtu 1500 -bugs -rand file:${RANDOM_GENERATOR} 2>/dev/null | \
                            sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' | openssl x509 -noout -subject -dates | grep notAfter | cut -d "=" -f 2 | \
                            awk '{print $4, $1, $2}');

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURNED_EXPIRY -> ${RETURNED_EXPIRY}";

                        if [ -z "${RETURNED_EXPIRY}" ]
                        then
                            ## this site may not have an ssl channel
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to connect to ${WEBSITE} using both standard (${STD_SSL_PORT_NUMBER}) and non-standard (${NONSTD_SSL_PORT_NUMBER}).";
                        else
                            ## got back a date, convert it
                            EXPIRY_MONTH=$(echo ${RETURNED_EXPIRY} | awk '{print $2}');

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EXPIRY_MONTH -> ${EXPIRY_MONTH}";

                            if [ ! -z "${EXPIRY_MONTH}" ]
                            then
                                ## ok, we have a nickname and an expiration date. convert it
                                EPOCH_EXPIRY=$(returnEpochTime $(echo ${RETURNED_EXPIRY} | sed -e "s/${EXPIRY_MONTH}/$(eval echo \${${EXPIRY_MONTH}})/"));

                                [ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EPOCH_EXPIRY -> ${EPOCH_EXPIRY}";

                                if [ ${EPOCH_EXPIRY} -le ${EXPIRY_EPOCH} ]
                                then
                                    ## this certificate expires within the epoch, notify
                                    ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate ${CERT_NICKNAME} for host ${CERT_HOSTNAME} expires on ${CERT_EXPIRY}";
                                fi
                            else
                                ## didnt get an expiration date
                                ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No expiration date was received for ${WEBSITE}.";
                            fi
                        fi
                    else
                        EXPIRY_MONTH=$(echo ${RETURNED_EXPIRY} | awk '{print $2}');

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EXPIRY_MONTH -> ${EXPIRY_MONTH}";

                        if [ ! -z "${EXPIRY_MONTH}" ]
                        then
                            ## ok, we have a nickname and an expiration date. convert it
                            EPOCH_EXPIRY=$(returnEpochTime $(echo ${RETURNED_EXPIRY} | sed -e "s/${EXPIRY_MONTH}/$(eval echo \${${EXPIRY_MONTH}})/"));

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EPOCH_EXPIRY -> ${EPOCH_EXPIRY}";

                            if [ ${EPOCH_EXPIRY} -le ${EXPIRY_EPOCH} ]
                            then
                                ## this certificate expires within the epoch, notify
                                ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate ${CERT_NICKNAME} for host ${CERT_HOSTNAME} expires on ${CERT_EXPIRY}";
                            fi
                        else
                            ## didnt get an expiration date
                            ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No expiration date was received for ${WEBSITE}.";
                        fi
                    fi
                fi
            done
        else
            ## no verifiable sites were returned
            ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SERVER} - No sites were found to verify.";
        fi
    done

    unset EXPIRY_MONTH;
    unset EPOCH_EXPIRY;
    unset EXPIRY_MONTH;
    unset RETURNED_EXPIRY;
    unset PING_RCODE;
    unset PROXY;
    unset VERIFIABLE_SITES;
    unset SERVER_REGION;
    unset SERVER_PARTITION;
    unset WEBSERVER;
    unset EXPIRY_EPOCH;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    RETURN_CODE=0;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin ] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

monitorCertExpiry;

echo ${RETURN_CODE};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

exit ${RETURN_CODE};
