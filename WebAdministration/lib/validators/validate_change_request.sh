#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  validate_change_request.sh
#         USAGE:  ./validate_change_request.sh
#   DESCRIPTION:  Determines what type of change to execute. Possible options
#                 are:
#                 CSR generation
#                 Certificate application (pre-implementation)
#                 Certificate application (implementation)
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

function validate_change_request
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    WEB_PLATFORM_TYPE=${1};
    VALIDATE_CERT_DB=${2};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_PLATFORM_TYPE -> ${WEB_PLATFORM_TYPE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_CERT_DB -> ${VALIDATE_CERT_DB}";

    if [ "${WEB_PLATFORM_TYPE}" = "${IPLANET_TYPE_IDENTIFIER}" ]
    then
        if [ -s ${APP_ROOT}/${CERTDB_STORE}/${VALIDATE_CERT_DB}${IPLANET_CERT_STORE_KEY_SUFFIX} ] \
            && [ -s ${APP_ROOT}/${CERTDB_STORE}/${VALIDATE_CERT_DB}${IPLANET_CERT_STORE_CERT_SUFFIX} ]
        then
            VALIDATE_CERT_NICKNAME=$(certutil -L -d ${APP_ROOT}/${CERTDB_STORE} -P ${VALIDATE_CERT_DB} | grep "u,u,u" | awk '{print $1}');

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_CERT_DB -> ${VALIDATE_CERT_DB}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_CERT_NICKNAME -> ${VALIDATE_CERT_NICKNAME}";

            if [ ! -z "${VALIDATE_CERT_NICKNAME}" ]
            then
                ## ok, lets find out what we're doing here.
                if [ -s ${APP_ROOT}/${CSRSTORE}/${VALIDATE_CERT_NICKNAME}.csr ]
                then
                    ## ok, we have a csr too. we could be in pre-implementation or implementation.
                    ## lets find out
                    if [ -s ${APP_ROOT}/${CERTSTORE}/${VALIDATE_CERT_NICKNAME}.cer ]
                    then
                        ## implementation.
                        OPERATION_TYPE="impl";
                        RETURN_CODE=0;
                    else
                        OPERATION_TYPE="preimp";
                        RETURN_CODE=0;
                    fi
                else
                    ## no csr, but we do have cert databases. generate a csr
                    OPERATION_TYPE="csrgen";
                    RETURN_CODE=0;
                fi
            else
                ## no cert databases, start from scratch
                OPERATION_TYPE="csrgen";
                RETURN_CODE=0;
            fi
        else
            OPERATION_TYPE="csrgen";
            RETURN_CODE=0;
        fi
    elif [ "${WEB_PLATFORM_TYPE}" = "${IHS_TYPE_IDENTIFIER}" ]
    then
        REAL_CERTDB_NAME=$(echo ${VALIDATE_CERT_DB} | cut -d "-" -f 1);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_CERT_DB -> ${VALIDATE_CERT_DB}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REAL_CERTDB_NAME -> ${REAL_CERTDB_NAME}";

        if [ -s ${APP_ROOT}/${CERTDB_STORE}/${VALIDATE_CERT_DB}${IHS_DB_STASH_SUFFIX} ] \
            && [ -s ${APP_ROOT}/${CERTDB_STORE}/${VALIDATE_CERT_DB}${IHS_DB_REQ_SUFFIX} ] \
            && [ -s ${APP_ROOT}/${CERTDB_STORE}/${VALIDATE_CERT_DB}${IHS_DB_CRT_SUFFIX} ]
        then
            ## part of the csr generation process is to delete the existing certificate.
            ## thus, this diverges a bit from the iplanet section above, in that we cant
            ## retreive a nickname for a certificate that doesnt exist.
            ## instead, the csr should be named after the certdb (without the userid) with a ".csr" extension
            ## ok, lets find out what we're doing here.
            typeset -u VALIDATE_CERT_NICKNAME=$(grep friendlyName ${APP_ROOT}/${PEMSTORE}/${VALIDATE_CERT_DB}.pem | cut -d ":" -f 2 | \
                sed -e "s/^ *//g" | sort | uniq);

            if [ -s ${APP_ROOT}/${CSRSTORE}/${VALIDATE_CERT_NICKNAME}.csr ]
            then
                ## ok, we have a csr too. we could be in pre-implementation or implementation.
                ## lets find out
                if [ -s ${APP_ROOT}/${CERTSTORE}/${VALIDATE_CERT_NICKNAME}.cer ]
                then
                    ## implementation.
                    OPERATION_TYPE="impl";
                    RETURN_CODE=0;
                else
                    OPERATION_TYPE="preimp";
                    RETURN_CODE=0;
                fi
            else
                ## no csr, but we do have cert databases. generate a csr
                OPERATION_TYPE="csrgen";
                RETURN_CODE=0;
            fi
        else
            OPERATION_TYPE="csrgen";
            RETURN_CODE=0;
        fi
    fi

    unset WEB_PLATFORM_TYPE;
    unset VALIDATE_CERT_DB;
    unset VALIDATE_CERT_NICKNAME;
    unset REAL_CERTDB_NAME;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the usage of this application
#    PARAMETERS:  None
#       RETURNS:  1
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    echo "${CNAME} - Validate that a change request has been successfully performed.";
    echo "Usage: ${CNAME} certdb";
    echo "          certdb is the name of the certificate database (e.g. https-site.name_project-hostname-) to validate";

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

validate_change_request ${@};

echo ${OPERATION_TYPE};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return ${RETURN_CODE};
