#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  execute_addition.sh
#         USAGE:  ./execute_addition.sh
#   DESCRIPTION:  Adds and updates various indicators utilized by named, as
#                 well as adding auditory information.
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
CNAME="$(/usr/bin/env basename ${0})";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; /usr/bin/env echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(/usr/bin/env dirname ${SCRIPT_ABSOLUTE_PATH})";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -f ${SCRIPT_ROOT}/../lib/plugin ] && . ${SCRIPT_ROOT}/../lib/plugin;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${PLUGIN_ROOT_DIR}" ] && /usr/bin/env echo "Failed to locate configuration data. Cannot continue." && return 1;

#===  FUNCTION  ===============================================================
#          NAME:  install_zone
#   DESCRIPTION:  Searches for and replaces "AUDIT" indicators for the provided
#                 filename.
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function installiPlanetInstance
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    ## pull the host ip addr
    BIND_ADDR=$(ifconfig ${SOLARIS_OPERATIONAL_IFACE} | grep inet | awk '{print $2}');

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_ID -> ${SERVER_ID}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROJECT_CODE -> ${PROJECT_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";

    if [ -z "${BIND_ADDR}" ]
    then
        ## unable to determine bind addr. make it localhost for now
        BIND_ADDR=127.0.0.1;
    fi

    ## xlnt, lets get to work. not much to it here, we're untarring a tarfile and updating the server config
    ## with the ip address. make directories as necessary. not much else really.
    ## untar it
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating tarball..";

    if [ -s ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}-${SERVER_ID}.tar.gz ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Tarball validated. Exploding web instance into ${IPLANET_ROOT} ..";

        gzip -dc ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}-${SERVER_ID}.tar.gz | (cd ${IPLANET_ROOT}; tar xf -);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Explosion complete. Validating ..";

        ## make sure it worked
        if [ -d ${IPLANET_ROOT}/${SERVER_ID} ]
        then
            ## xlnt. update server config with ip address
            ## and create directories as necessary
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding ${BIND_ADDR} to ${IPLANET_SERVER_CONFIG}..";

            sed -e "s/{BIND_ADDR}/${BIND_ADDR}/g" ${IPLANET_ROOT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG} \
                > ${IPLANET_ROOT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG}.tmp;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Modification complete. Validating..";

            if [ -s ${IPLANET_ROOT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG}.tmp ]
            then
                if [ $(grep -c ${BIND_ADDR} ${IPLANET_ROOT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG}.tmp) != 0 ]
                then
                    ## confirmed. relocate
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Modification verified. Renaming ..";

                    mv ${IPLANET_ROOT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG}.tmp \
                        ${IPLANET_ROOT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG} > /dev/null 2>&1;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Rename complete. Validating..";

                    if [ -s ${IPLANET_ROOT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG} ]
                    then
                        if [ $(grep -c ${BIND_ADDR} ${IPLANET_ROOT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG}) != 0 ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validation complete. Confirming directory access and creating as necessary..";

                            ## its there. get the directories we need so we can create them
                            WEB_LOG_ROOT=${IPLANET_BASE_LOG_ROOT}/${SERVER_ID};
                            WEB_DOC_ROOT=$(echo ${IPLANET_BASE_DOC_ROOT} | sed -e "s/%PROJECT_CODE%/${PROJECT_CODE}/");
                            WEB_TMP_DIR=$(grep ${IPLANET_TMPDIR_IDENTIFIER} ${IPLANET_ROOT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG} | awk '{print $2}');

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_LOG_ROOT -> ${WEB_LOG_ROOT}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_DOC_ROOT -> ${WEB_DOC_ROOT}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_TMP_DIR -> ${WEB_TMP_DIR}";

                            for DIRECTORY in ${WEB_LOG_ROOT} ${WEB_DOC_ROOT} ${WEB_TMP_DIR}
                            do
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating directory ${DIRECTORY} ..";

                                if [ ! -d ${DIRECTORY} ]
                                then
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating ${DIRECTORY} ..";

                                    mkdir -p ${DIRECTORY};

                                    if [ ! -d ${DIRECTORY} ]
                                    then
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to make directory ${DIRECTORY}.";

                                        (( ERROR_COUNT += 1 ));
                                    fi
                                fi
                            done

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ERROR_COUNT -> ${ERROR_COUNT}";

                            if [ ${ERROR_COUNT} -eq 0 ]
                            then
                                ## change web tmp dir to 700 otherwise iplanet complains
                                [ -d ${WEB_TMP_DIR} ] && chmod 700 ${WEB_TMP_DIR} > /dev/null 2>&1;

                                ## if this is an ssl-enabled site, make sure that the certificate databases are here
                                for SUFFIX in ${IPLANET_CERT_STORE_KEY_SUFFIX} ${IPLANET_CERT_STORE_CERT_SUFFIX}
                                do
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating file ${SERVER_ID}-${IUSER_AUDIT}-${SUFFIX}..";

                                    if [ -s ${IPLANET_ROOT}/${IPLANET_CERT_DIR}/${SERVER_ID}-${IUSER_AUDIT}-${SUFFIX} ]
                                    then
                                        ## rename it
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renaming certificate database ${SERVER_ID}-${IUSER_AUDIT}-${SUFFIX}..";

                                        mv ${IPLANET_ROOT}/${IPLANET_CERT_DIR}/${SERVER_ID}-${IUSER_AUDIT}-${SUFFIX} ${IPLANET_ROOT}/${IPLANET_CERT_DIR}/${SERVER_ID}-${HOSTNAME}-${SUFFIX} > /dev/null 2>&1;

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Rename complete. Validating..";

                                        if [ -s ${IPLANET_ROOT}/${IPLANET_CERT_DIR}/${SERVER_ID}-${HOSTNAME}-${SUFFIX} ]
                                        then
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Rename validated.";

                                            (( FILE_COUNT += 1 ));
                                        else
                                            ## rename failed
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to rename certificate database.";

                                            (( ERROR_COUNT += 1 ));
                                        fi
                                    else
                                        ## unable to locate certificate database
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to locate certificate database ${SERVER_ID}-${IUSER_AUDIT}-${SUFFIX} in ${IPLANET_ROOT}/${IPLANET_CERT_DIR}.";

                                        (( ERROR_COUNT += 1 ));
                                    fi
                                done

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILE_COUNT -> ${FILE_COUNT}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ERROR_COUNT -> ${ERROR_COUNT}";

                                if [ ${FILE_COUNT} -eq 2 ]
                                then
                                    if [ ${ERROR_COUNT} -eq 0 ]
                                    then
                                        ## validate the acl files
                                        FILE_COUNT=0;

                                        ## set up the separator
                                        CURR_IFS=${IFS};
                                        IFS=${MODIFIED_IFS};

                                        for ACL in ${IPLANET_ACL_NAMES}
                                        do
                                            ACL_FILE=$(echo ${ACL} | sed -e "s/%SERVER_ID%/${SERVER_ID}/");

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ACL_FILE -> ${ACL_FILE}";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating file ${ACL_FILE} ..";

                                            if [ -s ${IPLANET_ROOT}/${IPLANET_ACL_DIR}/${ACL_FILE} ]
                                            then
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${ACL_FILE} validated.";

                                                (( FILE_COUNT += 1 ));
                                            else
                                                ## unable to locate certificate database
                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to locate ${ACL_FILE} in ${IPLANET_ROOT}/${IPLANET_ACL_DIR}.";

                                                (( ERROR_COUNT += 1 ));
                                            fi
                                        done

                                        ## and change it back
                                        IFS=${CURR_IFS};

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILE_COUNT -> ${FILE_COUNT}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ERROR_COUNT -> ${ERROR_COUNT}";

                                        if [ ${FILE_COUNT} -eq 2 ]
                                        then
                                            if [ ${ERROR_COUNT} -eq 0 ]
                                            then
                                                ## xlnt. this means we're done.
                                                ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server installation successful: Server ID: ${SERVER_ID}, Requestor: ${IUSER_AUDIT}.";

                                                ## clean up temporary files
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing temporary installation files..";

                                                rm -rf ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}-${SERVER_ID}.tar ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}-${SERVER_ID}.tar.gz > /dev/null 2>&1;

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Cleanup complete. Setting permissions on scripts ..";

                                                ## ensure permissions on scripts
                                                for SCRIPT in ${IPLANET_START_SCRIPT} ${IPLANET_STOP_SCRIPT} ${IPLANET_RECONFIG_SCRIPT} ${IPLANET_RESTART_SCRIPT} ${IPLANET_ROTATE_SCRIPT}
                                                do
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SCRIPT -> ${SCRIPT}";

                                                    chmod 754 ${IPLANET_ROOT}/${SERVER_ID}/${SCRIPT} > /dev/null 2>&1;
                                                done

                                                ## start the server
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Permission changes complete. Starting server..";

                                                RET_CODE=$(. ${APP_ROOT}/${LIB_DIRECTORY}/executors/executeServiceRestart.sh ${SERVER_ID} start;);

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                                if [ ${RET_CODE} -eq 0 ]
                                                then
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server started successfully.";

                                                    RETURN_CODE=0;
                                                else
                                                    ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: Server failed to properly start up. Please investigate server logs.";

                                                    RETURN_CODE=40;
                                                fi
                                            else
                                                ## "ERROR" occurred copying acl files
                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to install ACL files. Please install manually.";

                                                RETURN_CODE=53;
                                            fi
                                        else
                                            ## an "ERROR" occurred copying acl files
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to install ACL files. Please install manually.";

                                            RETURN_CODE=53;
                                        fi
                                    else
                                        ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more errors occurred while copying certificate databases. Cannot continue.";

                                        RETURN_CODE=8;
                                    fi
                                else
                                    ## one or more cert databases aren't here
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate database copy failed. Cannot continue.";

                                    RETURN_CODE=8;
                                fi
                            else
                                ## one or more web directories failed to create
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more web directories failed to create. Cannot continue.";

                                RETURN_CODE=50;
                            fi
                        else
                            ## failed to validate bind address
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to insert binding interface address. Cannot continue.";

                            RETURN_CODE=51;
                        fi
                    else
                        ## failed to copy temporary file
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to insert binding interface address. Cannot continue.";

                        RETURN_CODE=47;
                    fi
                else
                    ## failed to insert bind address
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to insert binding interface address. Cannot continue.";

                    RETURN_CODE=51;
                fi
            else
                ## failed to create temporary file
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to insert binding interface address. Cannot continue.";

                RETURN_CODE=47;
            fi
        else
            ## installation failure
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to install new web instance. Cannot continue.";

            RETURN_CODE=32;
        fi
    else
        ## tarfile doesnt exist
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to locate web instance package. Cannot continue.";

        RETURN_CODE=52;
    fi

    ERROR_COUNT=0;
    FILE_COUNT=0;
    unset CURR_IFS;
    unset RET_CODE;
    unset SUFFIX;
    unset DIRECTORY;
    unset WEB_TMP_DIR;
    unset WEB_DOC_ROOT;
    unset WEB_LOG_ROOT;
    unset BIND_ADDR;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the usage of this application
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    echo "${THIS_CNAME} - Execute zone additions to the DNS infrastructure.";
    echo "Usage: ${THIS_CNAME} [ -s server id ] [ -p project code ] [ -i requestor ] [ -c change request ] [ -e ] [ -?|-h ]";
    echo "  -s      The associated server identifier";
    echo "  -p      The associated project code";
    echo "  -i      The user performing the request";
    echo "  -c      The change order associated with this request";
    echo "  -e      Execute processing";
    echo "  -?|-h   Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

    return 3;
}

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin ] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

[ ${#} -eq 0 ] && usage&& RETURN_CODE=${?};

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

while getopts ":s:p:i:c:seh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        s)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SERVER_ID..";

            ## Capture the site root
            SERVER_ID=${OPTARG}

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_ID -> ${SERVER_ID}";
            ;;
        p)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting PROJECT_CODE..";

            ## Capture the site root
            PROJECT_CODE=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROJECT_CODE -> ${PROJECT_CODE}";
            ;;
        i)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting IUSER_AUDIT..";

            ## Capture the change control
            IUSER_AUDIT=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
            ;;
        c)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            ## Capture the change control
            typeset -u CHANGE_NUM="${OPTARG}";

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
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No server id was provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=15;
                elif [ -z "${PROJECT_CODE}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=24;
                elif [ -z "${CHANGE_NUM}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change order was provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=17;
                elif [ -z "${IUSER_AUDIT}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requestors username was not provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=20;
                else
                    ## We have enough information to process the request, continue
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    if [ ! -z "${WS_PLATFORM}" ]
                    then
                        if [ "${WS_PLATFORM}" = "${IPLANET_TYPE_IDENTIFIER}" ]
                        then
                            installiPlanetInstance;
                        elif [ "${WS_PLATFORM}" = "${IHS_TYPE_IDENTIFIER}" ]
                        then
                            installIHSInstance;
                        else
                            ## no valid platform type found
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No valid platform type was found. Cannot continue.";

                            RETURN_CODE=3;
                        fi
                    else
                        ## no valid platform indicator
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No valid platform type was found. Cannot continue.";

                        RETURN_CODE=3;
                    fi
                fi
            fi
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage&& RETURN_CODE=${?};
            ;;
    esac
done


echo ${RETURN_CODE};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

exit ${RETURN_CODE};

