#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  createNewZone.sh
#         USAGE:  ./createNewZone.sh
#   DESCRIPTION:  Creates a skeleton zone file and directory structure
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
CNAME="$(/usr/bin/env basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}/${0##*/}")";
SCRIPT_ROOT="$(/usr/bin/env dirname "${SCRIPT_ABSOLUTE_PATH}")";
METHOD_NAME="${CNAME}#startup";
LOCKFILE=$(mktemp);

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -f "${SCRIPT_ROOT}/../lib/plugin" ] && . "${SCRIPT_ROOT}/../lib/plugin";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${APP_ROOT}" ] && awk -F "=" '/\<1\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[ -f "${PLUGIN_LIB_DIRECTORY}/aliases" ] && . "${PLUGIN_LIB_DIRECTORY}/aliases";
[ -f "${PLUGIN_LIB_DIRECTORY}/functions" ] && . "${PLUGIN_LIB_DIRECTORY}/functions";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

THIS_CNAME="${CNAME}";
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

## validate the input
"${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh" -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

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

unset RET_CODE;
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

lockProcess "${LOCKFILE}" "${$}";
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

CNAME="${THIS_CNAME}";
METHOD_NAME="${THIS_CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

[ ${RET_CODE} -ne 0 ] && awk -F "=" '/\<application.in.use\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

unset RET_CODE;

#===  FUNCTION  ===============================================================
#          NAME:  createIntranetSkeletonZone
#   DESCRIPTION:  Creates the necessary group folder, domain folders and creates
#                 skeleton zone files. Skeletons are then updated with the
#                 provided zone name.
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function createIntranetSkeletonZone
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing service indicators..";

    typeset BASE_DIRECTORY="${PLUGIN_WORK_DIRECTORY}"/${NAMED_GROUP_ID}${BUSINESS_UNIT}
    typeset ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}).${PROJECT_CODE};
    typeset DC_ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME});
    typeset ADMIN_CONTACT=$(sed -e "s/@/./" <<< ${DNS_SERVER_ADMIN_EMAIL});

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BASE_DIRECTORY -> ${BASE_DIRECTORY}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE_NAME -> ${ZONEFILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DC_ZONEFILE_NAME -> ${DC_ZONEFILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADMIN_CONTACT -> ${ADMIN_CONTACT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating directories..";

    [ -d ${BASE_DIRECTORY} ] && rm -rf ${BASE_DIRECTORY};

    ## create our directory structure
    mkdir -p ${BASE_DIRECTORY} > /dev/null 2>&1;

    if [ ! -d ${BASE_DIRECTORY} ]
    then
        ## directories werent created
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create zone directory structure. Please try again.";

        RETURN_CODE=40;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset ADDRESS;
        unset SITE_COORDINATES;
        unset DATACENTER;
        unset BASE_DIRECTORY;
        unset ZONEFILE_NAME;
        unset DC_ZONEFILE_NAME;
        unset ADMIN_CONTACT;
        unset METHOD_NAME;
        unset BUSINESS_UNIT;
        unset PROJECT_CODE;
        unset ZONE_NAME;
        unset CHANGE_NUM;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

        return ${RETURN_CODE};
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating skeleton zonefiles..";

    ## write out the file
    for DATACENTER in ${INTRANET_DATACENTERS[*]}
    do
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DATACENTER -> ${DATACENTER}";

        mkdir -p ${BASE_DIRECTORY}/${DATACENTER} > /dev/null 2>&1;

        if [ ! -d ${BASE_DIRECTORY}/${DATACENTER} ]
        then
            ## directories werent created
            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create zone directory structure. Please try again.";

            RETURN_CODE=40;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            unset ADDRESS;
            unset SITE_COORDINATES;
            unset DATACENTER;
            unset BASE_DIRECTORY;
            unset ZONEFILE_NAME;
            unset DC_ZONEFILE_NAME;
            unset ADMIN_CONTACT;
            unset METHOD_NAME;
            unset BUSINESS_UNIT;
            unset PROJECT_CODE;
            unset ZONE_NAME;
            unset CHANGE_NUM;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

            return ${RETURN_CODE};
        fi

        typeset TEMPFILE=$(mktemp);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TEMPFILE -> ${TEMPFILE}";

        echo "; zone '${ZONE_NAME}' last serial %LAST_SERIAL%\n" >> ${TEMPFILE};
        echo "; Currently live in: ${DATACENTER}\n" >> ${TEMPFILE};
        echo "; updated on %DATE% by %USER_NAME% per change order %REQUEST_NUMBER%\n" >> ${TEMPFILE};
        echo "\$ORIGIN .\n" >> ${TEMPFILE};
        echo "\$TTL ${INTRANET_NAMESERVER_TTL_TIME}\n" >> ${TEMPFILE};
        echo "${ZONE_NAME} IN SOA ${INTRANET_NAMED_MASTER}.${INTRANET_NAMESERVER_SUFFIX}. ${NAMED_PRIMARY_SOA_CONTACT}.${INTRANET_NAMESERVER_SUFFIX} (\n" >> ${TEMPFILE};
        echo "       %SERIAL_NUM%       ; serial number of this zone file\n" >> ${TEMPFILE};
        echo "       ${INTRANET_NAMESERVER_REFRESH_INTERVAL}              ; slave refresh\n" >> ${TEMPFILE};
        echo "       ${INTRANET_NAMESERVER_RETRY_INTERVAL}             ; slave retry time in case of a problem\n" >> ${TEMPFILE};
        echo "       ${INTRANET_NAMESERVER_EXPIRATION_INTERVAL}           ; slave expiration time\n" >> ${TEMPFILE};
        echo "       ${INTRANET_NAMESERVER_CACHE_INTERVAL}             ; minimum caching time in case of failed lookups\n" >> ${TEMPFILE};
        echo "       )\n" >> ${TEMPFILE};

        for SLAVE in ${INTRANET_DNS_SLAVES[*]}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SLAVE -> ${SLAVE}";

            echo "       IN    NS          ${SLAVE}.${INTRANET_NAMESERVER_SUFFIX}\n" >> ${TEMPFILE};
        done

        echo "       IN    RP          ${NAMED_PRIMARY_SOA_CONTACT}.${INTRANET_NAMESERVER_SUFFIX}\n" >> ${TEMPFILE};

        if [ ! -z "${INTRANET_ENABLE_LOC_RECORD}" ] && [ "${INTRANET_ENABLE_LOC_RECORD}" = "${_TRUE}" ]
        then
            typeset SITE_COORDINATES=$(awk -F "=" "/\<INTRANET_${DATACENTER}_SITE_COORDS\>/{print \$2}" ${INTRANET_DNS_CONFIG} | sed -e 's/^ *//g;s/ *$//g');

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_COORDINATES -> ${SITE_COORDINATES}";

            echo "       IN    LOC         ${SITE_COORDINATES}\n" >> ${TEMPFILE};
        fi

        [ -s ${TEMPFILE} ] && mv ${TEMPFILE} ${BASE_DIRECTORY}/${DATACENTER}/${DC_ZONEFILE_NAME};

        if [ ! -s ${BASE_DIRECTORY}/${DATACENTER}/${DC_ZONEFILE_NAME} ]
        then
            RETURN_CODE=40;

            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to populate zone datafile!";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            unset ADDRESS;
            unset SITE_COORDINATES;
            unset DATACENTER;
            unset BASE_DIRECTORY;
            unset ZONEFILE_NAME;
            unset DC_ZONEFILE_NAME;
            unset ADMIN_CONTACT;
            unset METHOD_NAME;
            unset BUSINESS_UNIT;
            unset PROJECT_CODE;
            unset ZONE_NAME;
            unset CHANGE_NUM;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

            return ${RETURN_CODE};
        fi
    done

    "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONEFILE_NAME} created on $(date +"%m-%d-%Y") by ${IUSER_AUDIT} per change ${CHANGE_NUM}";

    RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ -f ${TEMPFILE} ] && rm -rf ${TEMPFILE};

    unset TEMPFILE;
    unset ADDRESS;
    unset SITE_COORDINATES;
    unset DATACENTER;
    unset BASE_DIRECTORY;
    unset ZONEFILE_NAME;
    unset DC_ZONEFILE_NAME;
    unset ADMIN_CONTACT;
    unset METHOD_NAME;
    unset BUSINESS_UNIT;
    unset PROJECT_CODE;
    unset ZONE_NAME;
    unset CHANGE_NUM;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  createInternetSkeletonZone
#   DESCRIPTION:  Creates the necessary group folder, domain folders and creates
#                 skeleton zone files. Skeletons are then updated with the
#                 provided zone name.
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function createInternetSkeletonZone
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing service indicators..";

    typeset BASE_DIRECTORY="${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}
    typeset ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}).${PROJECT_CODE};
    typeset DC_ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME});
    typeset ADMIN_CONTACT=$(sed -e "s/@/./" <<< ${DNS_SERVER_ADMIN_EMAIL});

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BASE_DIRECTORY -> ${BASE_DIRECTORY}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE_NAME -> ${ZONEFILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DC_ZONEFILE_NAME -> ${DC_ZONEFILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADMIN_CONTACT -> ${ADMIN_CONTACT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating directories..";

    [ -d ${BASE_DIRECTORY} ] && rm -rf ${BASE_DIRECTORY};

    ## create our directory structure
    mkdir -p ${BASE_DIRECTORY} > /dev/null 2>&1;

    if [ ! -d ${BASE_DIRECTORY} ]
    then
        ## directories werent created
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create zone directory structure. Please try again.";

        RETURN_CODE=40;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset ADDRESS;
        unset SITE_COORDINATES;
        unset DATACENTER;
        unset BASE_DIRECTORY;
        unset ZONEFILE_NAME;
        unset DC_ZONEFILE_NAME;
        unset ADMIN_CONTACT;
        unset METHOD_NAME;
        unset BUSINESS_UNIT;
        unset PROJECT_CODE;
        unset ZONE_NAME;
        unset CHANGE_NUM;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

        return ${RETURN_CODE};
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating skeleton zonefiles..";

    ## write out the file
    for DATACENTER in ${INTERNET_DATACENTERS[*]}
    do
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DATACENTER -> ${DATACENTER}";

        mkdir -p ${BASE_DIRECTORY}/${DATACENTER} > /dev/null 2>&1;

        if [ ! -d ${BASE_DIRECTORY}/${DATACENTER} ]
        then
            ## directories werent created
            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create zone directory structure. Please try again.";

            RETURN_CODE=40;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            unset ADDRESS;
            unset SITE_COORDINATES;
            unset DATACENTER;
            unset BASE_DIRECTORY;
            unset ZONEFILE_NAME;
            unset DC_ZONEFILE_NAME;
            unset ADMIN_CONTACT;
            unset METHOD_NAME;
            unset BUSINESS_UNIT;
            unset PROJECT_CODE;
            unset ZONE_NAME;
            unset CHANGE_NUM;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

            return ${RETURN_CODE};
        fi

        typeset TEMPFILE=$(mktemp);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TEMPFILE -> ${TEMPFILE}";

        echo "; zone '${ZONE_NAME}' last serial %LAST_SERIAL%\n" >> ${TEMPFILE};
        echo "; Currently live in: ${DATACENTER}\n" >> ${TEMPFILE};
        echo "; updated on %DATE% by %USER_NAME% per change order %REQUEST_NUMBER%\n" >> ${TEMPFILE};
        echo "\$ORIGIN .\n" >> ${TEMPFILE};
        echo "\$TTL ${INTERNET_NAMESERVER_TTL_TIME}\n" >> ${TEMPFILE};
        echo "${ZONE_NAME} IN SOA ${INTERNET_NAMED_MASTER}.${INTERNET_NAMESERVER_SUFFIX}. ${NAMED_PRIMARY_SOA_CONTACT}.${INTERNET_NAMESERVER_SUFFIX} (\n" >> ${TEMPFILE};
        echo "       %SERIAL_NUM%       ; serial number of this zone file\n" >> ${TEMPFILE};
        echo "       ${INTERNET_NAMESERVER_REFRESH_INTERVAL}              ; slave refresh\n" >> ${TEMPFILE};
        echo "       ${INTERNET_NAMESERVER_RETRY_INTERVAL}             ; slave retry time in case of a problem\n" >> ${TEMPFILE};
        echo "       ${INTERNET_NAMESERVER_EXPIRATION_INTERVAL}           ; slave expiration time\n" >> ${TEMPFILE};
        echo "       ${INTERNET_NAMESERVER_CACHE_INTERVAL}             ; minimum caching time in case of failed lookups\n" >> ${TEMPFILE};
        echo "       )\n" >> ${TEMPFILE};

        for SLAVE in ${INTERNET_DNS_SLAVES[*]}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SLAVE -> ${SLAVE}";

            echo "       IN    NS          ${SLAVE}.${INTERNET_NAMESERVER_SUFFIX}\n" >> ${TEMPFILE};
        done

        echo "       IN    RP          ${NAMED_PRIMARY_SOA_CONTACT}.${INTERNET_NAMESERVER_SUFFIX}\n" >> ${TEMPFILE};

        if [ ! -z "${INTERNET_ENABLE_LOC_RECORD}" ] && [ "${INTERNET_ENABLE_LOC_RECORD}" = "${_TRUE}" ]
        then
            typeset SITE_COORDINATES=$(awk -F "=" "/\<INTERNET_${DATACENTER}_SITE_COORDS\>/{print \$2}" ${INTERNET_DNS_CONFIG} | sed -e 's/^ *//g;s/ *$//g');

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_COORDINATES -> ${SITE_COORDINATES}";

            echo "       IN    LOC         ${SITE_COORDINATES}\n" >> ${TEMPFILE};
        fi

        [ -s ${TEMPFILE} ] && mv ${TEMPFILE} ${BASE_DIRECTORY}/${DATACENTER}/${DC_ZONEFILE_NAME};

        if [ ! -s ${BASE_DIRECTORY}/${DATACENTER}/${DC_ZONEFILE_NAME} ]
        then
            RETURN_CODE=40;

            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to populate zone datafile!";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            unset ADDRESS;
            unset SITE_COORDINATES;
            unset DATACENTER;
            unset BASE_DIRECTORY;
            unset ZONEFILE_NAME;
            unset DC_ZONEFILE_NAME;
            unset ADMIN_CONTACT;
            unset METHOD_NAME;
            unset BUSINESS_UNIT;
            unset PROJECT_CODE;
            unset ZONE_NAME;
            unset CHANGE_NUM;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

            return ${RETURN_CODE};
        fi
    done

    "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONEFILE_NAME} created on $(date +"%m-%d-%Y") by ${IUSER_AUDIT} per change ${CHANGE_NUM}";

    RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ -f ${TEMPFILE} ] && rm -rf ${TEMPFILE};

    unset TEMPFILE;
    unset ADDRESS;
    unset SITE_COORDINATES;
    unset DATACENTER;
    unset BASE_DIRECTORY;
    unset ZONEFILE_NAME;
    unset DC_ZONEFILE_NAME;
    unset ADMIN_CONTACT;
    unset METHOD_NAME;
    unset BUSINESS_UNIT;
    unset PROJECT_CODE;
    unset ZONE_NAME;
    unset CHANGE_NUM;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  buildWorkingZone
#   DESCRIPTION:  Creates the zone file that the DNS servers will utilize during
#                 queries. Takes the previously configured file from the primary
#                 datacenter folder and adds required indicator flags, and then
#                 places the resultant file in the group root.
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function buildWorkingZone
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    if [ ! -z "${IS_DNS_RECORD_ADD_ENABLED}" ] && [ "${IS_DNS_RECORD_ADD_ENABLED}" = "${_FALSE}" ]
    then
        RETURN_CODE=45;

        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Record additions have not been enabled.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset BASE_DIRECTORY;
        unset INDICATOR;
        unset CHG_ARRAY;
        unset DC_ZONEFILE_NAME;
        unset ZONEFILE_NAME;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

        return ${RETURN_CODE};
    fi

    typeset BASE_DIRECTORY="${PLUGIN_WORK_DIRECTORY}"/${NAMED_GROUP_ID}${BUSINESS_UNIT};
    typeset ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}).${PROJECT_CODE};
    typeset DC_ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME});

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BASE_DIRECTORY -> ${BASE_DIRECTORY}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE_NAME -> ${ZONEFILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DC_ZONEFILE_NAME -> ${DC_ZONEFILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating operational copy..";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DATACENTER}/${DC_ZONEFILE_NAME} "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}/${DC_ZONEFILE_NAME}.${PROJECT_CODE}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding indicators..";

    TMPFILE=$(mktemp);

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMPFILE -> ${TMPFILE}";

    ## process the request, iterating through the fields that require
    ## modification for audit/track/trace etc
    [ ! -z "${PARTITION}" ] && [ "${PARTITION}" = "${INTERNET_TYPE_IDENTIFIER}" ] && sed -e "s/%LAST_SERIAL%/1/g;s/%DATE%/$(date +"%m-%d-%Y")/g;s/%USER_NAME%/${IUSER_AUDIT}/g;s/%REQUEST_NUMBER%/${CHANGE_NUM}/g;s/%SERIAL_NUM%/${DEFAULT_SERIAL_NUMBER}/g" \
        ${BASE_DIRECTORY}/${INTERNET_PRIMARY_DATACENTER}/${DC_ZONEFILE_NAME} > ${TMPFILE};
    [ ! -z "${PARTITION}" ] && [ "${PARTITION}" = "${INTRANET_TYPE_IDENTIFIER}" ] && sed -e "s/%LAST_SERIAL%/1/g;s/%DATE%/$(date +"%m-%d-%Y")/g;s/%USER_NAME%/${IUSER_AUDIT}/g;s/%REQUEST_NUMBER%/${CHANGE_NUM}/g;s/%SERIAL_NUM%/${DEFAULT_SERIAL_NUMBER}/g" \
        ${BASE_DIRECTORY}/${INTRANET_PRIMARY_DATACENTER}/${DC_ZONEFILE_NAME} > ${TMPFILE};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating file ${TMPFILE};";

    [ -s ${TMPFILE} ] && mv ${TMPFILE} ${BASE_DIRECTORY}/${ZONEFILE_NAME};

    if [ ! -s ${BASE_DIRECTORY}/${ZONEFILE_NAME} ]
    then
        RETURN_CODE=44;

        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while generating the zonefile. Please try again.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset BASE_DIRECTORY;
        unset INDICATOR;
        unset CHG_ARRAY;
        unset DC_ZONEFILE_NAME;
        unset ZONEFILE_NAME;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

        return ${RETURN_CODE};
    fi

    "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONEFILE_NAME} updated on $(date +"%m-%d-%Y") by ${IUSER_AUDIT} per change ${CHANGE_NUM}";

    RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    A=0;
    [ -f ${TMPFILE} ] && rm -rf ${TMPFILE} ${BASE_DIRECTORY}/${ZONEFILE_NAME};

    unset TMPFILE;
    unset BASE_DIRECTORY;
    unset INDICATOR;
    unset CHG_ARRAY;
    unset DC_ZONEFILE_NAME;
    unset ZONEFILE_NAME;
    unset METHOD_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

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
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=3;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    echo "${THIS_CNAME} - Create a skeleton zone file with the necessary components.\n";
    echo "Usage: ${THIS_CNAME} [ -b <business unit> ] [ -p <project code> ] [ -z <zone name> ] [ -i <partition> ] [ -c <change request> ] [ -n ] [ -e ] [ -h|-? ]
    -b         -> The associated business unit
    -p         -> The associated project code
    -z         -> The zone name, eg example.com
    -i         -> The associated partition, internet or intranet
    -c         -> The change order associated with this request
    -n         -> Build an operational zonefile
    -e         -> Execute processing
    -h|-?      -> Show this help\n";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

[ ${#} -eq 0 ] && usage && RETURN_CODE=${?};

while getopts ":b:p:z:i:c:neh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        b)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting BUSINESS_UNIT..";

            ## Capture the site root
            typeset -u BUSINESS_UNIT="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
            ;;
        p)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting PROJECT_CODE..";

            ## Capture the site root
            typeset -u PROJECT_CODE="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROJECT_CODE -> ${PROJECT_CODE}";
            ;;
        z)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ZONE_NAME..";

            ## Capture the site root
            ZONE_NAME=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_NAME -> ${ZONE_NAME}";
            ;;
        i)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting PARTITION..";

            ## Capture the change control
            typeset -u PARTITION="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PARTITION -> ${PARTITION}";
            ;;
        c)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            ## Capture the change control
            typeset -u CHANGE_NUM="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        n)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting BUILD_ZONE..";

            ## Capture the change control
            BUILD_ZONE="${_TRUE}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUILD_ZONE -> ${BUILD_ZONE}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${BUSINESS_UNIT}" ]
            then
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No business unit was provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=15;
            elif [ -z "${PROJECT_CODE}" ]
            then
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No project code was provided. Unable to continue processing.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=18;
            elif [ -z "${ZONE_NAME}" ]
            then
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=37;
            elif [ -z "${CHANGE_NUM}" ]
            then
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change order was provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=19;
            elif [ -z "${IUSER_AUDIT}" ]
            then
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requestors username was not provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=20;
            else
                ## We have enough information to process the request, continue
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${BUILD_ZONE}" ] && [ "${BUILD_ZONE}" = "${_TRUE}" ] && buildWorkingZone && RETURN_CODE=${?};
                [ ! -z "${PARTITION}" ] && [ "${PARTITION}" = "${INTERNET_TYPE_IDENTIFIER}" ] && [ -z "${BUILD_ZONE}" ] && createInternetSkeletonZone && RETURN_CODE=${?};
                [ ! -z "${PARTITION}" ] && [ "${PARTITION}" = "${INTRANET_TYPE_IDENTIFIER}" ] && [ -z "${BUILD_ZONE}" ] && createIntranetSkeletonZone && RETURN_CODE=${?};
            fi
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage && RETURN_CODE=${?};
            ;;
    esac
done

trap 'unlockProcess "${LOCKFILE}" "${$}"; return "${RETURN_CODE}"' INT TERM EXIT;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset PARTITION;
unset BUSINESS_UNIT;
unset PROJECT_CODE;
unset ZONE_NAME;
unset CHANGE_NUM;
unset BUILD_ZONE;
unset CNAME;
unset METHOD_NAME;
unset RET_CODE;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

[ -z "${RETURN_CODE}" ] && return 1 || return "${RETURN_CODE}";
