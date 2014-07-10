#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  monitorCertificateDatabases.sh
#         USAGE:  ./monitorCertificateDatabases.sh server_name
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
#          NAME:  monitorCertDatabases
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function monitorCertDatabases
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing monitor on: ${HOSTNAME}";

    if [ ! -z "${1}" ]
    then
        EXPIRY_EPOCH=${1};
    else
        EXPIRY_EPOCH=$(returnEpochTime $(date +"%Y %m %d") ${VALIDATION_PERIOD});
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EXPIRY_EPOCH -> ${EXPIRY_EPOCH}";

    ## ok, lets sort out where we are and what to look for
    if [ ! -z "${WS_PLATFORM}" ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WS_PLATFORM -> ${WS_PLATFORM}";

        if [ "${WS_PLATFORM}" = "${IPLANET_TYPE_IDENTIFIER}" ]
        then
            ## ok, we know we're on an iPlanet server. poll for the list of servers to validate
            unset METHOD_NAME;
            unset CNAME;

            set -A VALIDATE_SERVER_LIST $(${APP_ROOT}/${LIB_DIRECTORY}/retrieveSiteList.sh certdb);

            CNAME=$(basename ${0});
        typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_SERVER_LIST -> ${VALIDATE_SERVER_LIST}";

            if [ ! -z "${VALIDATE_SERVER_LIST}" ]
            then
                if [ "$(echo ${VALIDATE_SERVER_LIST[@]})" != "${_FALSE}" ]
                then
                    ## ok, validate it
                    for VALIDATE_SITE in ${VALIDATE_SERVER_LIST[@]}
                    do
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_SITE -> ${VALIDATE_SITE}";

                        CERT_DATABASE="${VALIDATE_SITE}-${HOSTNAME}-";
                        EXP_CERT_NICKNAME=$(grep ${IPLANET_NICKNAME_IDENTIFIER} ${IPLANET_ROOT}/${VALIDATE_SITE}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG} | \
                            sed -e "s/${IPLANET_NICKNAME_IDENTIFIER}=\"/%/" | cut -d "%" -f 2 | cut -d "\"" -f 1 | cut -d ":" -f 2| sort | uniq);

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_DATABASE -> ${CERT_DATABASE}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EXP_CERT_NICKNAME -> ${EXP_CERT_NICKNAME}";

                        if [ -O ${IPLANET_ROOT}/${IPLANET_CERT_DIR}/${CERT_DATABASE}${IPLANET_CERT_STORE_KEY_SUFFIX} ] &&
                            [ -O ${IPLANET_ROOT}/${IPLANET_CERT_DIR}/${CERT_DATABASE}${IPLANET_CERT_STORE_CERT_SUFFIX} ] ||
                            [ -r ${IPLANET_ROOT}/${IPLANET_CERT_DIR}/${CERT_DATABASE}${IPLANET_CERT_STORE_KEY_SUFFIX} ] &&
                            [ -r ${IPLANET_ROOT}/${IPLANET_CERT_DIR}/${CERT_DATABASE}${IPLANET_CERT_STORE_CERT_SUFFIX} ]
                        then
                            ## pull the data
                            CERT_NICKNAME=$(certutil -L -d ${IPLANET_ROOT}/${IPLANET_CERT_DIR} -P ${CERT_DATABASE} | grep "u,u,u" | grep "${EXP_CERT_NICKNAME}" | awk '{print $1}' 2>${APP_ROOT}/${LOG_ROOT}/certutil.$(date +"%Y-%m-%d").log);

                            if [ ! -z "${CERT_NICKNAME}" ]
                            then
                                CERT_DETAIL=$(certutil -L -d ${IPLANET_ROOT}/${IPLANET_CERT_DIR} -P ${CERT_DATABASE} -n ${CERT_NICKNAME} | \
                                    sed -e "s/CN=/%/" -e "s/Not After : /%/")
                                CERT_HOSTNAME=$(echo ${CERT_DETAIL} | cut -d "%" -f 4| cut -d "," -f 1);
                                CERT_EXPIRY=$(echo ${CERT_DETAIL} | cut -d "%" -f 3 | awk '{print $5, $2, $3}');
                                EXPIRY_MONTH=$(echo ${CERT_EXPIRY} | awk '{print $2}');

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_NICKNAME -> ${CERT_NICKNAME}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_HOSTNAME -> ${CERT_HOSTNAME}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_EXPIRY -> ${CERT_EXPIRY}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EXPIRY_MONTH -> ${EXPIRY_MONTH}";

                                if [ ! -z "${CERT_EXPIRY}" ]
                                then
                                    ## ok, we have a nickname and an expiration date. convert it
                                    EPOCH_EXPIRY=$(returnEpochTime $(echo ${CERT_EXPIRY} | sed -e "s/${EXPIRY_MONTH}/$(eval echo \${${EXPIRY_MONTH}})/"));

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EPOCH_EXPIRY -> ${EPOCH_EXPIRY}";

                                    if [ ${EPOCH_EXPIRY} -le ${EXPIRY_EPOCH} ]
                                    then
                                        ## this certificate expires within the epoch, notify
                                        ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate ${CERT_NICKNAME} for host ${CERT_HOSTNAME} expires on ${CERT_EXPIRY}";
                                    fi
                                else
                                    ## didnt get an expiration date
                                    ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate ${CERT_NICKNAME} produced no expiration date.";
                                fi
                            else
                                ## didnt get a nickname
                                ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate database ${VALIDATE_SITE} produced no certificate nickname.";
                            fi
                        else
                            ## i cant read the file, "ERROR" out
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate database ${CERT_DATABASE} cannot be read by the executing user. Cannot provide data.";
                            ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate database ${CERT_DATABASE} cannot be read by the executing user. Cannot provide data.";
                        fi
                    done
                fi

                RETURN_CODE=0;
            else
                ## no certificate databases to validate
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_SERVER_LIST was found to be empty. Cannot continue.";

                RETURN_CODE=29;
            fi
        elif [ "${WS_PLATFORM}" = "${IHS_TYPE_IDENTIFIER}" ]
        then
            ## ihs host
            set -A VALIDATE_SERVER_LIST $(ls -ltr ${IHS_CERT_DIR} | grep ${IHS_DB_CRT_SUFFIX} | awk '{print $9}');

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_SERVER_LIST -> ${VALIDATE_SERVER_LIST}";

            if [ ! -z "${VALIDATE_SERVER_LIST}" ]
            then
                ## ok, validate it
                for VALIDATE_SITE in ${VALIDATE_SERVER_LIST[@]}
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_SITE -> ${VALIDATE_SITE}";

                    if [ -O ${IHS_CERT_DIR}/${VALIDATE_SITE}${IHS_DB_CRT_SUFFIX} ] &&
                        [ -O ${IHS_CERT_DIR}/${VALIDATE_SITE}${IHS_DB_CRT_SUFFIX} ]
                    then
                        ## pull the data
                        CERT_NICKNAME=$(keyman -cert -list personal -db ${IHS_CERT_DIR}/${VALIDATE_SITE}${IHS_DB_CRT_SUFFIX} \
                            -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} | grep -v ${VALIDATE_SITE}${IHS_DB_CRT_SUFFIX} | sed -e "s/^ *//g");

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_NICKNAME -> ${CERT_NICKNAME}";

                        if [ ! -z "${CERT_NICKNAME}" ]
                        then
                            CERT_DETAIL=$(keyman -cert -details -db ${IHS_CERT_DIR}/${VALIDATE_SITE}${IHS_DB_CRT_SUFFIX} \
                                -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -label "${CERT_NICKNAME}" -type ${IHS_KEY_DB_TYPE} | \
                                sed -e "s/Subject: CN=/@/" -e "s/To:/@/");
                            CERT_HOSTNAME=$(echo ${CERT_DETAIL} | cut -d "@" -f 2 | cut -d "," -f 1);
                            CERT_EXPIRY=$(echo ${CERT_DETAIL} | cut -d "@" -f 3 | awk '{print $4, $2, $3}' | sed -e "s/,//");
                            EXPIRY_MONTH=$(echo ${CERT_EXPIRY} | awk '{print $2}' | cut -c 1-3);

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_HOSTNAME -> ${CERT_HOSTNAME}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_EXPIRY -> ${CERT_EXPIRY}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EXPIRY_MONTH -> ${EXPIRY_MONTH}";

                            if [ ! -z "${CERT_EXPIRY}" ]
                            then
                                ## ok, we have a nickname and an expiration date. convert it
                                EPOCH_EXPIRY=$(returnEpochTime $(echo ${CERT_EXPIRY} | sed -e "s/${EXPIRY_MONTH}/$(eval echo \${${EXPIRY_MONTH}})/"));

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EPOCH_EXPIRY -> ${EPOCH_EXPIRY}";

                                if [ ${EPOCH_EXPIRY} -le ${EXPIRY_EPOCH} ]
                                then
                                    ## this certificate expires within the epoch, notify
                                    ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate ${CERT_NICKNAME} for host ${CERT_HOSTNAME} expires on ${CERT_EXPIRY}";
                                fi
                            else
                                ## didnt get an expiration date
                                ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate ${CERT_NICKNAME} produced no expiration date.";
                            fi
                        else
                            ## didnt get a nickname
                            ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate database ${VALIDATE_SITE} produced no certificate nickname.";
                        fi
                    elif [ -r ${IHS_CERT_DIR}/${VALIDATE_SITE}${IHS_DB_CRT_SUFFIX} ] &&
                        [ -r ${IHS_CERT_DIR}/${VALIDATE_SITE}${IHS_DB_CRT_SUFFIX} ]
                    then
                        ## pull the data
                        CERT_NICKNAME=$(keyman -cert -list personal -db ${IHS_CERT_DIR}/${VALIDATE_SITE}${IHS_DB_CRT_SUFFIX} \
                            -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} | grep -v ${VALIDATE_SITE}${IHS_DB_CRT_SUFFIX} | sed -e "s/^ *//g");

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_NICKNAME -> ${CERT_NICKNAME}";

                        if [ ! -z "${CERT_NICKNAME}" ]
                        then
                            CERT_DETAIL=$(keyman -cert -details -db ${IHS_CERT_DIR}/${VALIDATE_SITE}${IHS_DB_CRT_SUFFIX} \
                                -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -label "${CERT_NICKNAME}" -type ${IHS_KEY_DB_TYPE} | \
                                sed -e "s/Subject: CN=/@/" -e "s/To:/@/");
                            CERT_HOSTNAME=$(echo ${CERT_DETAIL} | cut -d "@" -f 2 | cut -d "," -f 1);
                            CERT_EXPIRY=$(echo ${CERT_DETAIL} | cut -d "@" -f 3 | awk '{print $4, $2, $3}' | sed -e "s/,//");
                            EXPIRY_MONTH=$(echo ${CERT_EXPIRY} | awk '{print $2}' | cut -c 1-3);

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_HOSTNAME -> ${CERT_HOSTNAME}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_EXPIRY -> ${CERT_EXPIRY}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EXPIRY_MONTH -> ${EXPIRY_MONTH}";

                            if [ ! -z "${CERT_EXPIRY}" ]
                            then
                                ## ok, we have a nickname and an expiration date. convert it
                                EPOCH_EXPIRY=$(returnEpochTime $(echo ${CERT_EXPIRY} | sed -e "s/${EXPIRY_MONTH}/$(eval echo \${${EXPIRY_MONTH}})/"));

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EPOCH_EXPIRY -> ${EPOCH_EXPIRY}";

                                if [ ${EPOCH_EXPIRY} -le ${EXPIRY_EPOCH} ]
                                then
                                    ## this certificate expires within the epoch, notify
                                    ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate ${CERT_NICKNAME} for host ${CERT_HOSTNAME} expires on ${CERT_EXPIRY}";
                                fi
                            else
                                ## didnt get an expiration date
                                ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate ${CERT_NICKNAME} produced no expiration date.";
                            fi
                        else
                            ## didnt get a nickname
                            ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate database ${VALIDATE_SITE} produced no certificate nickname.";
                        fi
                    else
                        ## i cant read the file, "ERROR" out
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate database ${CERT_DATABASE} cannot be read by the executing user. Cannot provide data.";
                        ${LOGGER} MONITOR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate database ${CERT_DATABASE} cannot be read by the executing user. Cannot provide data.";
                    fi
                done

                RETURN_CODE=0;
            else
                ## no certificate databases to validate
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_SERVER_LIST was found to be empty. Cannot continue.";

                RETURN_CODE=29;
            fi
        fi
    else
        ## unknown platform type
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An unknown platform type was encountered. Cannot continue.";

        RETURN_CODE=29;
    fi

    unset EPOCH_EXPIRY;
    unset EXPIRY_MONTH;
    unset CERT_EXPIRY;
    unset CERT_DETAIL;
    unset CERT_NICKNAME;
    unset VALIDATE_SITE;
    unset VALIDATE_SERVER_LIST;
    unset EXPIRY_EPOCH;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin ]] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

[ ${#} -eq 0 ] && usage&& RETURN_CODE=${?};

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

monitorCertDatabases ${@};

echo ${RETURN_CODE};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

exit ${RETURN_CODE};
