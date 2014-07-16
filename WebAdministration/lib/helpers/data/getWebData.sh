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
#        AUTHOR:  Kevin Huntly <kmhuntly@gmail.com>
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
SCRIPT_ROOT="$(/usr/bin/env dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#          NAME:  obtainWebData
#   DESCRIPTION:  Generates a certificate signing request (CSR) for an iPlanet
#                 webserver
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function obtainWebData
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    if [ $(getWebInfo | grep -w ${SITE_HOSTNAME} | wc -l) != 0 ]
    then
        ## we've been provided a hostname, lets get the data from esupport
        ## this sets up the project code
        ## first, the stuff out of URL_Defs
        WEB_PROJECT_CODE=$(getWebInfo | grep -w ${SITE_HOSTNAME} | grep -v "#" | \
            cut -d "|" -f 1 | cut -d ":" -f 2 | sort | uniq); ## get the webcode
        PLATFORM_CODE=$(getWebInfo | grep "${SITE_HOSTNAME}" | grep -v "#" | \
            cut -d "|" -f 2 | sort | uniq | tr "[\n]" "[ ]"); ## get the platform code, if multiples spit with space
        MASTER_WEBSERVER=$(getPlatformInfo | grep -w $(echo ${PLATFORM_CODE} | awk '{print $1}') | \
            grep -v "#" | cut -d "|" -f 5 | sort | uniq | sed -e "s/,/ /g" | awk '{print $1}');
        [ -z "$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
            cut -d "|" -f 10 | sort | uniq | grep enterprise)" ] \
            && WEBSERVER_PLATFORM=${IHS_TYPE_IDENTIFIER} \
            || WEBSERVER_PLATFORM=${IPLANET_TYPE_IDENTIFIER};
        ENVIRONMENT_TYPE=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
            cut -d "|" -f 3 | sort | uniq); ## the environment type (dev, ist etc) TODO: fix this cut, it isnt right
        SERVER_ROOT=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
            cut -d "|" -f 10 | cut -d "/" -f 1-3 | sort | uniq); ## web instance name
        CONTACT_CODE=$(getWebInfo | grep -w ${SITE_HOSTNAME} | grep -v "#" | \
            cut -d "|" -f 14 | cut -d ":" -f 2 | sort | uniq); ## get the contact code
        OWNER_DIST=$(getContactInfo | grep -w ${CONTACT_CODE} | grep -v "#" | \
            cut -d "|" -f 7 | sort | uniq); ## get the contact dist list

        ## make sure we have a valid and supported platform
        if [ "${WEBSERVER_PLATFORM}" != "${IPLANET_TYPE_IDENTIFIER}" ] \
            && [ "${WEBSERVER_PLATFORM}" != "${IHS_TYPE_IDENTIFIER}" ]
        then
            ## unsupported platform
            ## unset SVC_LIST, we dont need it now
            unset REQUEST_OPTION;
            unset SITE_HOSTNAME;
            unset WEB_PROJECT_CODE;
            unset PLATFORM_CODE;
            unset MASTER_WEBSERVER;
            unset ENVIRONMENT_TYPE;
            unset SERVER_ROOT;
            unset CONTACT_CODE;
            unset OWNER_DIST;

            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unsupported platform detected - Renewal process aborted";

            DATA_CODE=41;
        else
            if [ "${WEBSERVER_PLATFORM}" = "${IPLANET_TYPE_IDENTIFIER}" ]
            then
                INSTANCE_NAME=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
                    cut -d "|" -f 10 | cut -d "/" -f 4 | sort | uniq); ## web instance name
            elif [ "${WEBSERVER_PLATFORM}" = "${IHS_TYPE_IDENTIFIER}" ]
            then
                INSTANCE_NAME=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
                    cut -d "|" -f 10 | cut -d "/" -f 5 | sort | uniq); ## web instance name
            fi

            CERTDB=${INSTANCE_NAME}-${IUSER_AUDIT}-;

            if [ "${ENVIRONMENT_TYPE}" = "${ENV_TYPE_PRD}" ]
            then
                ## find out where its live right now
                unset METHOD_NAME;
                unset CNAME;
                unset RET_CODE;

                . ${APP_ROOT}/${LIB_DIRECTORY}/runQuery.sh -u ${SITE_HOSTNAME} -e;
                RET_CODE=${?}

                CNAME=$(/usr/bin/env basename ${0});
            typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ACTIVE_DATACENTER -> ${ACTIVE_DATACENTER}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                DATA_CODE=0;

                if [ ! -z "${ACTIVE_DATACENTER}" ] && [ ${RET_CODE} -eq 0 ]
                then
                    unset RET_CODE;
                    unset RETURN_CODE;

                    ## we have an active datacenter, re-order the server list
                    PRI_PLATFORM_CODE=$(getWebInfo | grep "${SITE_HOSTNAME}" | grep -v "#" | grep -v ${ACTIVE_DATACENTER} | \
                        cut -d "|" -f 2 | sort | uniq); ## get the platform code, if multiples spit with space
                    SEC_PLATFORM_CODE=$(getWebInfo | grep "${SITE_HOSTNAME}" | grep -v "#" | grep ${ACTIVE_DATACENTER} | \
                        cut -d "|" -f 2 | sort | uniq); ## get the platform code, if multiples spit with space

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRI_PLATFORM_CODE -> ${PRI_PLATFORM_CODE}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SEC_PLATFORM_CODE -> ${SEC_PLATFORM_CODE}";

                    DATA_CODE=0;
                else
                    ## unable to accurately determine current datacenter
                    ## return a warning
                    ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Active datacenter could not be established.";

                    DATA_CODE=91;
                fi
            fi
        fi
    else
        ## unable to find data
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to locate configuration data in ${EINFO_WEBSITE_DEFS}. Cannot continue.";

        DATA_CODE=42;
    fi

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
usage
{
    METHOD_NAME="${CNAME}#usage";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    echo "${THIS_CNAME} - Generates a certificate signing request for a provided host.";
    echo " -s    -> The site domain name to operate against";
    echo " -v    -> The source server to obtain the necessary key databases from";
    echo " -w    -> Platform type to execute against - iplanet or ihs";
    echo " -p    -> The webserver base path (e.g. /opt/IBMIHS70)";
    echo " -d    -> The certificate database to work against";
    echo " -c    -> The target platform code.";
    echo " -t    -> The requestor telephone number";
    echo " -e    -> Execute the request";
    echo " -h|-? -> Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 3;
}

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin ] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

[ ${#} -eq 0 ] && usage&& RETURN_CODE=${?};

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

obtainWebData "${@}";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return ${DATA_CODE};

