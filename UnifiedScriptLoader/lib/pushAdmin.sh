#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  deployPlugin.sh
#         USAGE:  ./deployPlugin.sh plugin-name
#   DESCRIPTION:  Prints the specified message to the defined logfile
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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[[ -z "${APP_ROOT}" && -f ${SCRIPT_ROOT}/../lib/constants.sh ]] && . ${SCRIPT_ROOT}/../lib/constants.sh;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${APP_ROOT}" ] && print "Failed to locate configuration data. Cannot continue." && exit 1;

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

#===  FUNCTION  ===============================================================
#          NAME:  buildPlugin
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function buildPlugin
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating plugin bundle..";

    ## create a temp file with the release date in it.. this will get cleaned up afterwords
    print "${PLUGIN} version ${VERSION} built on $(date +"%Y-%m-%d %H:%M:%S") by ${IUSER_AUDIT}" > ${PLUGIN_CONF_ROOT}/etc/${PLUGIN}.version;

    for TARGET_HOSTNAME in list-of-servers
    do
        [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Distributing to ${TARGET_HOSTNAME}..";

        ## backup first
        createBackup ${SSH_USER_ACCOUNT} ${PLUGIN_CONFIG_DIR}/${PLUGIN} ${TARGET_HOSTNAME} ${APP_ROOT}/${TMP_DIRECTORY}/${PLUGIN}-${TARGET_HOSTNAME}-config-${CURRENT_TIMESTAMP};

        ## make sure backups were created
        if [ "$(ls ${APP_ROOT}/${TMP_DIRECTORY}/${PLUGIN}-${TARGET_HOSTNAME}-config)-${CURRENT_TIMESTAMP}" = "" ]
        then
            ## "ERROR" occurred
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while obtaining a backup for node ${TARGET_HOSTNAME}. Please process manually.";

            (( ERROR_COUNT += 1 ));

            continue;
        fi

        createBackup ${SSH_USER_ACCOUNT} ${PLUGIN_LIB_DIR}/${PLUGIN} ${TARGET_HOSTNAME} ${APP_ROOT}/${TMP_DIRECTORY}/${PLUGIN}-${TARGET_HOSTNAME}-lib-${CURRENT_TIMESTAMP};

        if [ "$(ls ${APP_ROOT}/${TMP_DIRECTORY}/${PLUGIN}-${TARGET_HOSTNAME}-lib)-${CURRENT_TIMESTAMP}" = "" ]
        then
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while obtaining a backup for node ${TARGET_HOSTNAME}. Please process manually.";

            (( ERROR_COUNT += 1 ));

            continue;
        fi

        [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Distributing to ${TARGET_HOSTNAME}..";

        distributePackage ${SSH_USER_ACCOUNT} ${PLUGIN_CONFIG_DIR}/${PLUGIN} ${TARGET_HOSTNAME} ${TARGET_HOSTNAME} ${APP_ROOT}/${PLUGIN_CONFIG_DIR}/${PLUGIN};
        local typeset -i RET_CODE=${?};

        if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]]
        then
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while distributing the plugin for node ${TARGET_HOSTNAME}. Please process manually.";

            (( ERROR_COUNT += 1 ));

            continue;
        fi

        distributePackage ${SSH_USER_ACCOUNT} ${PLUGIN_LIB_DIR}/${PLUGIN} ${TARGET_HOSTNAME} ${TARGET_HOSTNAME} ${APP_ROOT}/${PLUGIN_LIB_DIR}/${PLUGIN};
        local typeset -i RET_CODE=${?};

        if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]]
        then
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while distributing the plugin for node ${TARGET_HOSTNAME}. Please process manually.";

            (( ERROR_COUNT += 1 ));

            continue;
        fi

        ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Distribution to webnode ${WEBNODE} successfully completed by ${IUSER_AUDIT}.";
    done

    [ -f ${PLUGIN_CONF_ROOT}/etc/${PLUGIN}.version ] && rm ${PLUGIN_CONF_ROOT}/etc/${PLUGIN}.version;

    [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME}->exit";

    return ${ERROR_COUNT};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Build and deploy the most current version of the web administration utilities";
    print "Usage: ${CNAME} [ -v release version ] [ -p (buildRelease|installRelease) ] [ -e ] [ -h|? ]";
    print " -v    -> The release version for this build";
    print " -p    -> The process to execute. One of buildRelease or installRelease is required.";
    print " -t    -> The tarfile date. Only applicable when -p is install.";
    print " -i    -> The executing user. Only applicable when -p is install.";
    print " -e    -> Execute the request";
    print " -h|-? -> Show this help";

    [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[ ${#} -eq 0 ] && usage;

while getopts ":v:p:t:i:eh:" OPTIONS
do
    case "${OPTIONS}" in
        v)
            [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting VERSION..";

            VERSION=${OPTARG};

            [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VERSION -> ${VERSION}";
            ;;
        p)
            [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting EXECUTION_TYPE..";

            EXECUTION_TYPE=${OPTARG};

            [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EXECUTION_TYPE -> ${EXECUTION_TYPE}";
            ;;
        i)
            [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting IUSER_AUDIT..";

            IUSER_AUDIT=${OPTARG};

            [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
            ;;
        e)
            [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z ${VERSION} ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No version number was provided. Unable to continue.";
                [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME}->exit";

                RETURN_CODE=7;
            elif [ -z ${EXECUTION_TYPE} ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No command was provided. Unable to continue.";
                [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME}->exit";

                RETURN_CODE=7;
            else
                ## We have enough information to process the request, continue
                [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";

                if [ "${EXECUTION_TYPE}" = "buildRelease" ]
                then
                    [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME}->exit";

                    buildRelease;
                elif [ "${EXECUTION_TYPE}" = "installRelease" ]
                then
                    [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME}->exit";

                    installRelease;
                else
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No release phase was provided. Cannot continue.";

                    [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME}->exit";

                    RETURN_CODE=3;
                fi
            fi
            ;;
        *)
            [[ ! -z ${VERBOSE} && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME}->exit";

            usage;
            ;;
    esac
done

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
