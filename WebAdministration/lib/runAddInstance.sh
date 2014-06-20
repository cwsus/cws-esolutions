#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  run_key_generation.sh
#         USAGE:  ./run_key_generation.sh
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
#                 NAME:  runWebInstallation
#     DESCRIPTION:  Generates a certificate signing request (CSR) for an iPlanet
#                             webserver
#    PARAMETERS:  None
#          RETURNS:  0
#==============================================================================
function runiPlanetInstallation
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    if [ -d ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID} ]
    then
        ## tar it up
        (cd ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}; tar cf - ${SERVER_ID} ${IPLANET_CERT_DIR}/${SERVER_ID}* \
            ${IPLANET_ACL_DIR}/*${SERVER_ID}*) | gzip -c > ${APP_ROOT}/${BACKUP_DIRECTORY}/${IUSER_AUDIT}-${SERVER_ID}.tar.gz;
        (cd ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}; tar cf - ${SERVER_ID} ${IPLANET_CERT_DIR}/${SERVER_ID}* \
            ${IPLANET_ACL_DIR}/*${SERVER_ID}*) | gzip -c > ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}-${SERVER_ID}.tar.gz;

        ## make sure that the backup got made
        if [ -s ${APP_ROOT}/${BACKUP_DIRECTORY}/${IUSER_AUDIT}-${SERVER_ID}.tar.gz ]
        then
            ## make sure the file we want to copy exists
            if [ -s ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}-${SERVER_ID}.tar.gz ]
            then
                ## xlnt, scp it
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Files validated. Processing renewal..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generating server list..";

                SERVER_LIST=$(getPlatformInfo | grep -w ${PLATFORM_CODE} | grep -v "#" | grep -v "none" | cut -d "|" -f 5 | sort | uniq | sed -e "s/,/ /g");

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_LIST -> ${SERVER_LIST}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server list generated. Continuing..";

                ## start the process
                if [ ! -z "${SERVER_LIST}" ]
                then
                    for WEBSERVER in ${SERVER_LIST}
                    do
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating against ${WEBSERVER}..";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now validating access..";

                        $(ping ${WEBSERVER} > /dev/null 2>&1);
                        PING_RCODE=${?}

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                        if [ ${PING_RCODE} -eq 0 ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Submitting files to ${WEBSERVER}..";

                            ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSCPConnection.exp local-copy ${WEBSERVER} \
                                ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}-${SERVER_ID}.tar.gz \
                                ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}-${SERVER_ID}.tar.gz ${IPLANET_OWNING_USER};

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File copies complete. Continuing..";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${WEBSERVER} \"${APP_ROOT}/${LIB_DIRECTORY}/executors/executeAddInstance.sh -s ${SERVER_ID} -p ${PROJECT_CODE} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e\" ${IPLANET_OWNING_USER}";

                            ## ok, files copied, run the executor
                            ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${WEBSERVER} "${APP_ROOT}/${LIB_DIRECTORY}/executors/executeAddInstance.sh -s ${SERVER_ID} -p ${PROJECT_CODE} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e" ${IPLANET_OWNING_USER};
                            INSTALLER_CODE=${?};

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "INSTALLER_CODE -> ${INSTALLER_CODE}";

                            if [ ${INSTALLER_CODE} -ne 0 ]
                            then
                                ## "ERROR"
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred performing the installation against ${WEBSERVER}. Please try again.";

                                (( ERROR_COUNT += 1 ));
                            fi
                        else
                            ## ping test failure
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${WEBSERVER} appears unavailable. PING_RCODE -> ${PING_RCODE}";

                            (( ERROR_COUNT += 1 ));
                        fi
                    done

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ERROR_COUNT -> ${ERROR_COUNT}";

                    if [ ${ERROR_COUNT} -eq 0 ]
                    then
                        ## successful implementation
                        ## create a backup of the existing files, then clean up
                        ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server installation successful: Server Identifier: ${SERVER_ID}, Build owner: ${IUSER_AUDIT}, Change request: ${CHANGE_NUM}";

                        ## remove temp files
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing working files..";

                        rm -rf ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID} ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR}/*${SERVER_ID}* \
                            ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_ACL_DIR}/*${SERVER_ID}* > /dev/null 2>&1;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Process complete.";

                        RETURN_CODE=0;
                    else
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more errors occurred during the installation process. Cannot continue.";

                        RETURN_CODE=48;
                    fi
                else
                    ## target server list is empty
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server list generation has failed. Please ensure that the proper arguments were provided and try again.";

                    RETURN_CODE=29;
                fi
            else
                ## tarfile doesnt exist
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No tarfile was found. Unable to perform installation.";
            fi
        else
            ## backup file doesnt exist
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup file could not be verified. Please try again.";
        fi
    else
        ## site build doesnt exist
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No site build was found. Please try again.";
    fi

    ## always remove the tarfile
    rm -rf ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}-${SERVER_ID}.tar.gz > /dev/null 2>&1;

    ERROR_COUNT=0;
    unset SERVER_LIST;
    unset PING_RCODE;
    unset WEBSERVER;


    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
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
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Generates a certificate signing request for a provided host.";
    print " -s    -> The server identifier to perform installation for";
    print " -p    -> The platform code to install to";
    print " -w    -> Platform type to execute against - iplanet or ihs";
    print " -c    -> The change order number.";
    print " -e    -> Execute the request";
    print " -h|-? -> Show this help";

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

while getopts ":s:p:P:w:c:eh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        s)
            ## set the server id
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SERVER_ID..";

            ## Capture the site root
            typeset -l SERVER_ID="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_ID -> ${SERVER_ID}";
            ;;
        p)
            ## set the platform
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting PLATFORM_CODE..";

            ## Capture the site root
            typeset -u PLATFORM_CODE="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLATFORM_CODE -> ${PLATFORM_CODE}";
            ;;
        P)
            ## set the platform
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting PROJECT_CODE..";

            ## Capture the site root
            typeset -l PROJECT_CODE="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROJECT_CODE -> ${PROJECT_CODE}";
            ;;
        w)
            ## set the platform
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting WS_PLATFORM..";

            ## Capture the site root
            WS_PLATFORM=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WS_PLATFORM -> ${WS_PLATFORM}";
            ;;
        c)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            ## Capture the site root
            CHANGE_NUM=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${RETURN_CODE}" ]
            then
                if [ -z "${SERVER_ID}" ]
                then
                    ## no server identifier
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No server identifier was provided. Cannot continue.";

                    RETURN_CODE=21;
                elif [ -z "${WS_PLATFORM}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No webserver platform was provided. Cannot continue.";

                    RETURN_CODE=21;
                elif [ -z "${PLATFORM_CODE}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No certificate database was provided. Cannot continue.";

                    RETURN_CODE=9;
                else
                    ## We have enough information to process the request, continue
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    if [ "${WS_PLATFORM}" = "${IPLANET_TYPE_IDENTIFIER}" ]
                    then
                        runiPlanetInstallation;
                    elif [ "${WS_PLATFORM}" = "${IHS_TYPE_IDENTIFIER}" ]
                    then
                        runIHSInstallation;
                    else
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No valid webserver platform was provided. Cannot continue.";

                        RETURN_CODE=999;
                    fi
                fi
            fi

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
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

