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

## Application constants
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

## Application constants
CNAME="$(/usr/bin/env basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}/${0##*/}")";
SCRIPT_ROOT="$(/usr/bin/env dirname "${SCRIPT_ABSOLUTE_PATH}")";
METHOD_NAME="${CNAME}#startup";
LOCKFILE=$(mktemp);

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -f "${SCRIPT_ROOT}/../lib/plugin" ] && . "${SCRIPT_ROOT}/../lib/plugin";

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

[ -z "${APP_ROOT}" ] && awk -F "=" '/\<1\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

[ -f "${PLUGIN_LIB_DIRECTORY}/aliases" ] && . "${PLUGIN_LIB_DIRECTORY}/aliases";
[ -f "${PLUGIN_LIB_DIRECTORY}/functions" ] && . "${PLUGIN_LIB_DIRECTORY}/functions";

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

THIS_CNAME="${CNAME}";
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

## validate the input
"${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh" -a;
unset RET_CODE; typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

CNAME="${THIS_CNAME}";
typeset METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
then
    writeLogEntry "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security violation found while executing ${CNAME} by ${IUSER_AUDIT} on host ${SYSTEM_HOSTNAME}";
    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security configuration blocks execution. Please verify security configuration.";

    awk -F "=" '/\<request.not.authorized\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

    return ${RET_CODE};
fi

unset RET_CODE;
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

lockProcess "${LOCKFILE}" "${$}";
unset RET_CODE; typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

CNAME="${THIS_CNAME}";
METHOD_NAME="${THIS_CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

[ ${RET_CODE} -ne 0 ] && awk -F "=" '/\<application.in.use\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

unset RET_CODE;

#===  FUNCTION  ===============================================================
#          NAME:  createSelfSignedCertificate
#   DESCRIPTION:  Generates a self-signed certificate for a new SSL-enabled
#                 web instance
#    PARAMETERS:  None
#==============================================================================
function createSelfSignedCertificate
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    if [ ! -z "${GENERATE_SELF_SIGNED}" ] && [ "${GENERATE_SELF_SIGNED}" = "${_FALSE}" ]
    then
        RETURN_CODE=59;

        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Self-signed certificates are not currently enabled.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
        [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
        [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
        [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
        [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
        [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
        [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
        [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};

        unset OPENSSL_CNF;
        unset OPENSSL_PASSIN;
        unset OPENSSL_PASSOUT;
        unset CERTUTIL_PASSIN;
        unset WORK_DIRECTORY;
        unset SITE_KEYFILE;
        unset SITE_CSRFILE;
        unset SITE_CRTFILE;
        unset SITE_PFXFILE;
        unset RET_CODE;
        unset CERTIFICATE_DATASTORE;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

        return ${RETURN_CODE};
    fi

    [ -f "${LOG_ROOT}"/openssl.${CONTEXT_ROOT} ] && rm -rf "${LOG_ROOT}"/openssl.${CONTEXT_ROOT};
    [ -f "${LOG_ROOT}"/certutil.${CONTEXT_ROOT} ] && rm -rf "${LOG_ROOT}"/certutil.${CONTEXT_ROOT};
    [ -f "${LOG_ROOT}"/pk12util.${CONTEXT_ROOT} ] && rm -rf "${LOG_ROOT}"/pk12util.${CONTEXT_ROOT};
    [ -f "${LOG_ROOT}"/keyman.${CONTEXT_ROOT} ] && rm -rf "${LOG_ROOT}"/keyman.${CONTEXT_ROOT};

    typeset OPENSSL_CNF=$(mktemp);
    typeset OPENSSL_PASSIN=$(mktemp);
    typeset OPENSSL_PASSOUT=$(mktemp);
    typeset CERTUTIL_PASSIN=$(mktemp);
    typeset WORK_DIRECTORY=${PLUGIN_WORK_DIR}/${CONTEXT_ROOT};
    typeset SITE_KEYFILE="${KEY_DIR}/${CONTEXT_ROOT}.key";
    typeset SITE_CSRFILE="${CSR_DIR}/${CONTEXT_ROOT}.csr";
    typeset SITE_CRTFILE="${CERTS_DIR}/${CONTEXT_ROOT}.crt";
    typeset SITE_PFXFILE="${PKCS12_DIR}/${CONTEXT_ROOT}.p12";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPENSSL_CNF -> ${OPENSSL_CNF}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPENSSL_PASSIN -> ${OPENSSL_PASSIN}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPENSSL_PASSOUT -> ${OPENSSL_PASSOUT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERTUTIL_PASSIN -> ${CERTUTIL_PASSIN}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WORK_DIRECTORY -> ${WORK_DIRECTORY}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_KEYFILE -> ${SITE_KEYFILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_CSRFILE -> ${SITE_CSRFILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_CRTFILE -> ${SITE_CRTFILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_PFXFILE -> ${SITE_PFXFILE}";

    echo "$(returnRandomCharacters ${PASSWORD_LENGTH})" > ${OPENSSL_PASSIN};
    echo "$(returnRandomCharacters ${PASSWORD_LENGTH})" > ${OPENSSL_PASSOUT};
    echo "$(returnRandomCharacters ${PASSWORD_LENGTH})" > ${CERTUTIL_PASSIN};
    chmod 600 ${OPENSSL_PASSIN} ${OPENSSL_PASSOUT} ${CERTUTIL_PASSIN};

    ## modify the provided openssl conf to include proper hostname
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Modifying OpenSSL configuration...";
    sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/" ${OPENSSL_CONFIG} > ${OPENSSL_CNF};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Modification complete. Validating...";

    if [ ! -z "$(grep "%SITE_HOSTNAME%" ${OPENSSL_CNF})" ]
    then
        RETURN_CODE=59;

        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to modify OpenSSL configuration file. Please try again.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
        [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
        [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
        [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
        [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
        [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
        [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
        [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};

        unset OPENSSL_CNF;
        unset OPENSSL_PASSIN;
        unset OPENSSL_PASSOUT;
        unset CERTUTIL_PASSIN;
        unset WORK_DIRECTORY;
        unset SITE_KEYFILE;
        unset SITE_CSRFILE;
        unset SITE_CRTFILE;
        unset SITE_PFXFILE;
        unset RET_CODE;
        unset CERTIFICATE_DATASTORE;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

        return ${RETURN_CODE};
    fi

    ## generate key
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && openssl genrsa -out ${SITE_KEYFILE} -passout file:${OPENSSL_PASSIN} >> "${LOG_ROOT}"/openssl.${CONTEXT_ROOT} 2>&1;
    [ -z "${ENABLE_DEBUG}" ] || [ "${ENABLE_DEBUG}" = "${_FALSE}" ] && openssl genrsa -out ${SITE_KEYFILE} -passout file:${OPENSSL_PASSIN} > /dev/null 2>&1;
    unset RET_CODE; typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        RETURN_CODE=59;

        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a keyfile: RET_CODE -> ${RET_CODE}. Please try again.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
        [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
        [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
        [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
        [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
        [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
        [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
        [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};

        unset OPENSSL_CNF;
        unset OPENSSL_PASSIN;
        unset OPENSSL_PASSOUT;
        unset CERTUTIL_PASSIN;
        unset WORK_DIRECTORY;
        unset SITE_KEYFILE;
        unset SITE_CSRFILE;
        unset SITE_CRTFILE;
        unset SITE_PFXFILE;
        unset RET_CODE;
        unset CERTIFICATE_DATASTORE;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

        return ${RETURN_CODE};
    fi

    ## validate rsa key
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && openssl rsa -in ${SITE_KEYFILE} -check -passin file:${OPENSSL_PASSIN} >> "${LOG_ROOT}"/openssl.${CONTEXT_ROOT} 2>&1;
    [ -z "${ENABLE_DEBUG}" ] || [ "${ENABLE_DEBUG}" = "${_FALSE}" ] && openssl rsa -in ${SITE_KEYFILE} -check -passin file:${OPENSSL_PASSIN} > /dev/null 2>&1;
    unset RET_CODE; typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        RETURN_CODE=59;

        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a keyfile: RET_CODE -> ${RET_CODE}. Please try again.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
        [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
        [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
        [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
        [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
        [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
        [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
        [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};

        unset OPENSSL_CNF;
        unset OPENSSL_PASSIN;
        unset OPENSSL_PASSOUT;
        unset CERTUTIL_PASSIN;
        unset WORK_DIRECTORY;
        unset SITE_KEYFILE;
        unset SITE_CSRFILE;
        unset SITE_CRTFILE;
        unset SITE_PFXFILE;
        unset RET_CODE;
        unset CERTIFICATE_DATASTORE;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

        return ${RETURN_CODE};
    fi

    ## generate csr
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && openssl req -config ${OPENSSL_CONFIG} -batch -extensions ${EXTENSIONS} -new -key ${SITE_KEYFILE} -out ${SITE_CSRFILE} -passin file:${OPENSSL_PASSIN} >> "${LOG_ROOT}"/openssl.${CONTEXT_ROOT} 2>&1;
    [ -z "${ENABLE_DEBUG}" ] || [ "${ENABLE_DEBUG}" = "${_FALSE}" ] && openssl req -config ${OPENSSL_CONFIG} -batch -extensions ${EXTENSIONS} -new -key ${SITE_KEYFILE} -out ${SITE_CSRFILE} -passin file:${OPENSSL_PASSIN} > /dev/null 2>&1;
    unset RET_CODE; typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        RETURN_CODE=59;

        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a keyfile: RET_CODE -> ${RET_CODE}. Please try again.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
        [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
        [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
        [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
        [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
        [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
        [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
        [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};

        unset OPENSSL_CNF;
        unset OPENSSL_PASSIN;
        unset OPENSSL_PASSOUT;
        unset CERTUTIL_PASSIN;
        unset WORK_DIRECTORY;
        unset SITE_KEYFILE;
        unset SITE_CSRFILE;
        unset SITE_CRTFILE;
        unset SITE_PFXFILE;
        unset RET_CODE;
        unset CERTIFICATE_DATASTORE;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

        return ${RETURN_CODE};
    fi

    ## validate csr
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && openssl req -text -noout -verify -in ${SITE_CSRFILE} >> "${LOG_ROOT}"/openssl.${CONTEXT_ROOT} 2>&1;
    [ -z "${ENABLE_DEBUG}" ] || [ "${ENABLE_DEBUG}" = "${_FALSE}" ] && openssl req -text -noout -verify -in ${SITE_CSRFILE} > /dev/null 2>&1;
    unset RET_CODE; typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        RETURN_CODE=59;

        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a keyfile: RET_CODE -> ${RET_CODE}. Please try again.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
        [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
        [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
        [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
        [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
        [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
        [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
        [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};

        unset OPENSSL_CNF;
        unset OPENSSL_PASSIN;
        unset OPENSSL_PASSOUT;
        unset CERTUTIL_PASSIN;
        unset WORK_DIRECTORY;
        unset SITE_KEYFILE;
        unset SITE_CSRFILE;
        unset SITE_CRTFILE;
        unset SITE_PFXFILE;
        unset RET_CODE;
        unset CERTIFICATE_DATASTORE;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

        return ${RETURN_CODE};
    fi

    ## generate certificate
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && openssl x509 -req -in ${SITE_CSRFILE} -signkey ${SITE_KEYFILE} -out ${SITE_CRTFILE} -passin file:${OPENSSL_PASSIN} >> "${LOG_ROOT}"/openssl.${CONTEXT_ROOT} 2>&1;
    [ -z "${ENABLE_DEBUG}" ] || [ "${ENABLE_DEBUG}" = "${_FALSE}" ] && openssl x509 -req -in ${SITE_CSRFILE} -signkey ${SITE_KEYFILE} -out ${SITE_CRTFILE} -passin file:${OPENSSL_PASSIN} > /dev/null 2>&1;
    unset RET_CODE; typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        RETURN_CODE=59;

        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a keyfile: RET_CODE -> ${RET_CODE}. Please try again.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
        [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
        [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
        [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
        [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
        [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
        [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
        [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};

        unset OPENSSL_CNF;
        unset OPENSSL_PASSIN;
        unset OPENSSL_PASSOUT;
        unset CERTUTIL_PASSIN;
        unset WORK_DIRECTORY;
        unset SITE_KEYFILE;
        unset SITE_CSRFILE;
        unset SITE_CRTFILE;
        unset SITE_PFXFILE;
        unset RET_CODE;
        unset CERTIFICATE_DATASTORE;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

        return ${RETURN_CODE};
    fi

    ## validate certificate
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && openssl x509 -text -noout -in ${SITE_CRTFILE} >> "${LOG_ROOT}"/openssl.${CONTEXT_ROOT} 2>&1;
    [ -z "${ENABLE_DEBUG}" ] || [ "${ENABLE_DEBUG}" = "${_FALSE}" ] && openssl x509 -text -noout -in ${SITE_CRTFILE} > /dev/null 2>&1;
    unset RET_CODE; typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        RETURN_CODE=59;

        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a keyfile: RET_CODE -> ${RET_CODE}. Please try again.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
        [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
        [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
        [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
        [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
        [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
        [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
        [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};

        unset OPENSSL_CNF;
        unset OPENSSL_PASSIN;
        unset OPENSSL_PASSOUT;
        unset CERTUTIL_PASSIN;
        unset WORK_DIRECTORY;
        unset SITE_KEYFILE;
        unset SITE_CSRFILE;
        unset SITE_CRTFILE;
        unset SITE_PFXFILE;
        unset RET_CODE;
        unset CERTIFICATE_DATASTORE;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

        return ${RETURN_CODE};
    fi

    ## convert to pkcs12
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && openssl pkcs12 -export -out ${SITE_PFXFILE} -inkey ${SITE_KEYFILE} -in ${SITE_CRTFILE} -certfile ${SITE_CRTFILE} -passin file:${OPENSSL_PASSIN} -passout file:${OPENSSL_PASSOUT} >> "${LOG_ROOT}"/openssl.${CONTEXT_ROOT} 2>&1;
    [ -z "${ENABLE_DEBUG}" ] || [ "${ENABLE_DEBUG}" = "${_FALSE}" ] && openssl pkcs12 -export -out ${SITE_PFXFILE} -inkey ${SITE_KEYFILE} -in ${SITE_CRTFILE} -certfile ${SITE_CRTFILE} -passin file:${OPENSSL_PASSIN} -passout file:${OPENSSL_PASSOUT} > /dev/null 2>&1;
    unset RET_CODE; typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        RETURN_CODE=59;

        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a PKCS12 file: RET_CODE -> ${RET_CODE}. Please try again.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
        [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
        [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
        [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
        [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
        [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
        [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
        [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};

        unset OPENSSL_CNF;
        unset OPENSSL_PASSIN;
        unset OPENSSL_PASSOUT;
        unset CERTUTIL_PASSIN;
        unset WORK_DIRECTORY;
        unset SITE_KEYFILE;
        unset SITE_CSRFILE;
        unset SITE_CRTFILE;
        unset SITE_PFXFILE;
        unset RET_CODE;
        unset CERTIFICATE_DATASTORE;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

        return ${RETURN_CODE};
    fi

    ## validate
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && openssl pkcs12 -info -noout -in ${SITE_PFXFILE} -passin file:${OPENSSL_PASSOUT} >> "${LOG_ROOT}"/openssl.${CONTEXT_ROOT} 2>&1;
    [ -z "${ENABLE_DEBUG}" ] || [ "${ENABLE_DEBUG}" = "${_FALSE}" ] && openssl pkcs12 -info -noout -in ${SITE_PFXFILE} -passin file:${OPENSSL_PASSOUT} > /dev/null 2>&1;
    unset RET_CODE; typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        RETURN_CODE=59;

        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to validate PKCS12: RET_CODE -> ${RET_CODE}. Please try again.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
        [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
        [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
        [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
        [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
        [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
        [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
        [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};

        unset OPENSSL_CNF;
        unset OPENSSL_PASSIN;
        unset OPENSSL_PASSOUT;
        unset CERTUTIL_PASSIN;
        unset WORK_DIRECTORY;
        unset SITE_KEYFILE;
        unset SITE_CSRFILE;
        unset SITE_CRTFILE;
        unset SITE_PFXFILE;
        unset RET_CODE;
        unset CERTIFICATE_DATASTORE;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

        return ${RETURN_CODE};
    fi

    ## i don't like this because it requires code changes
    ## to add in new platforms, but offhand i can't think
    ## of a better way... TODO
    case ${WS_PLATFORM} in
        "IPLANET")
            typeset CERTIFICATE_DATASTORE="${IPLANET_CERT_STORE_PREFIX}${CONTEXT_ROOT}-";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERTIFICATE_DATASTORE -> ${CERTIFICATE_DATASTORE}";

            ## import for iplanet
            ## create database (remember, this is a new site)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && certutil -N -d ${WORK_DIRECTORY} -P ${CERTIFICATE_DATASTORE} >> "${LOG_ROOT}"/certutil.${CONTEXT_ROOT} 2>&1;
            [ -z "${ENABLE_DEBUG}" ] || [ "${ENABLE_DEBUG}" = "${_FALSE}" ] && certutil -N -d ${WORK_DIRECTORY} -P ${CERTIFICATE_DATASTORE} > /dev/null 2>&1;
            unset RET_CODE; typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
            then
                RETURN_CODE=59;

                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a certificate database: RET_CODE -> ${RET_CODE}. Please try again.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
                [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
                [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
                [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
                [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
                [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
                [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
                [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};
                [ ! -z "${IHS_KEYSTORE}" ] && [ -f ${IHS_KEYSTORE} ] && rm -rf ${IHS_KEYSTORE};
                [ ! -z "${CERTIFICATE_DATASTORE}" ] && [ -f ${IHS_KEYSTORE} ] && rm -rf ${CERTIFICATE_DATASTORE};

                unset OPENSSL_CNF;
                unset OPENSSL_PASSIN;
                unset OPENSSL_PASSOUT;
                unset CERTUTIL_PASSIN;
                unset WORK_DIRECTORY;
                unset SITE_KEYFILE;
                unset SITE_CSRFILE;
                unset SITE_CRTFILE;
                unset SITE_PFXFILE;
                unset RET_CODE;
                unset CERTIFICATE_DATASTORE;
                unset IHS_KEYSTORE;
                unset METHOD_NAME;

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                return ${RETURN_CODE};
            fi

            ## perform the import into the certdb
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && pk12util -i ${SITE_PFXFILE} -d ${WORK_DIRECTORY} -P ${CERTIFICATE_DATASTORE} -k ${CERTUTIL_PASSIN} -w ${OPENSSL_PASSOUT} >> "${LOG_ROOT}"/pk12util.${CONTEXT_ROOT} 2>&1;
            [ -z "${ENABLE_DEBUG}" ] || [ "${ENABLE_DEBUG}" = "${_FALSE}" ] && pk12util -i ${SITE_PFXFILE} -d ${WORK_DIRECTORY} -P ${CERTIFICATE_DATASTORE} -k ${CERTUTIL_PASSIN} -w ${OPENSSL_PASSOUT} > /dev/null 2>&1;
            unset RET_CODE; typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
            then
                RETURN_CODE=59;

                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a certificate database: RET_CODE -> ${RET_CODE}. Please try again.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
                [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
                [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
                [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
                [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
                [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
                [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
                [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};
                [ ! -z "${IHS_KEYSTORE}" ] && [ -f ${IHS_KEYSTORE} ] && rm -rf ${IHS_KEYSTORE};
                [ ! -z "${CERTIFICATE_DATASTORE}" ] && [ -f ${IHS_KEYSTORE} ] && rm -rf ${CERTIFICATE_DATASTORE};

                unset OPENSSL_CNF;
                unset OPENSSL_PASSIN;
                unset OPENSSL_PASSOUT;
                unset CERTUTIL_PASSIN;
                unset WORK_DIRECTORY;
                unset SITE_KEYFILE;
                unset SITE_CSRFILE;
                unset SITE_CRTFILE;
                unset SITE_PFXFILE;
                unset RET_CODE;
                unset CERTIFICATE_DATASTORE;
                unset IHS_KEYSTORE;
                unset METHOD_NAME;

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                return ${RETURN_CODE};
            fi

            ## validate
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && certutil -L -d ${WORK_DIRECTORY} -P ${CERTIFICATE_DATASTORE} -n "${CONTEXT_ROOT}" >> "${LOG_ROOT}"/certutil.${CONTEXT_ROOT} 2>&1;
            [ -z "${ENABLE_DEBUG}" ] || [ "${ENABLE_DEBUG}" = "${_FALSE}" ] && certutil -L -d ${WORK_DIRECTORY} -P ${CERTIFICATE_DATASTORE} -n "${CONTEXT_ROOT}" > /dev/null 2>&1;
            unset RET_CODE; typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
            then
                RETURN_CODE=59;

                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a certificate database: RET_CODE -> ${RET_CODE}. Please try again.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
                [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
                [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
                [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
                [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
                [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
                [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
                [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};
                [ ! -z "${IHS_KEYSTORE}" ] && [ -f ${IHS_KEYSTORE} ] && rm -rf ${IHS_KEYSTORE};
                [ ! -z "${CERTIFICATE_DATASTORE}" ] && [ -f ${IHS_KEYSTORE} ] && rm -rf ${CERTIFICATE_DATASTORE};

                unset OPENSSL_CNF;
                unset OPENSSL_PASSIN;
                unset OPENSSL_PASSOUT;
                unset CERTUTIL_PASSIN;
                unset WORK_DIRECTORY;
                unset SITE_KEYFILE;
                unset SITE_CSRFILE;
                unset SITE_CRTFILE;
                unset SITE_PFXFILE;
                unset RET_CODE;
                unset CERTIFICATE_DATASTORE;
                unset IHS_KEYSTORE;
                unset METHOD_NAME;

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                return ${RETURN_CODE};
            fi

            ## DONE.
            ;;
        "IHS")
            ## create database
            typeset IHS_KEYSTORE=${IHS_DB_DIR}/${CONTEXT_ROOT}.${IHS_KEY_DB_TYPE};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IHS_KEYSTORE -> ${IHS_KEYSTORE}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && keyman -keydb -create -db ${IHS_KEYSTORE} -type ${IHS_KEY_DB_TYPE} -stash -populate -pw $(<${OPENSSL_PASSOUT}) >> "${LOG_ROOT}"/keyman.${CONTEXT_ROOT} 2>&1;
            [ -z "${ENABLE_DEBUG}" ] || [ "${ENABLE_DEBUG}" = "${_FALSE}" ] && keyman -keydb -create -db ${IHS_KEYSTORE} -type ${IHS_KEY_DB_TYPE} -stash -populate -pw $(<${OPENSSL_PASSOUT}) > /dev/null 2>&1;
            unset RET_CODE; typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
            then
                RETURN_CODE=59;

                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a keyfile: RET_CODE -> ${RET_CODE}. Please try again.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
                [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
                [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
                [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
                [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
                [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
                [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
                [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};
                [ ! -z "${IHS_KEYSTORE}" ] && [ -f ${IHS_KEYSTORE} ] && rm -rf ${IHS_KEYSTORE};
                [ ! -z "${CERTIFICATE_DATASTORE}" ] && [ -f ${IHS_KEYSTORE} ] && rm -rf ${CERTIFICATE_DATASTORE};

                unset OPENSSL_CNF;
                unset OPENSSL_PASSIN;
                unset OPENSSL_PASSOUT;
                unset CERTUTIL_PASSIN;
                unset WORK_DIRECTORY;
                unset SITE_KEYFILE;
                unset SITE_CSRFILE;
                unset SITE_CRTFILE;
                unset SITE_PFXFILE;
                unset RET_CODE;
                unset CERTIFICATE_DATASTORE;
                unset IHS_KEYSTORE;
                unset METHOD_NAME;

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                return ${RETURN_CODE};
            fi

            ## stash password
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && keyman -keydb -stashpw -db ${IHS_KEYSTORE} -pw $(<${OPENSSL_PASSOUT}) -type ${IHS_KEY_DB_TYPE} >> "${LOG_ROOT}"/keyman.${CONTEXT_ROOT} 2>&1;
            [ -z "${ENABLE_DEBUG}" ] || [ "${ENABLE_DEBUG}" = "${_FALSE}" ] && keyman -keydb -stashpw -db ${IHS_KEYSTORE} -pw $(<${OPENSSL_PASSOUT}) -type ${IHS_KEY_DB_TYPE} > /dev/null 2>&1;
            unset RET_CODE; typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
            then
                RETURN_CODE=59;

                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a keyfile: RET_CODE -> ${RET_CODE}. Please try again.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
                [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
                [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
                [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
                [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
                [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
                [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
                [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};
                [ ! -z "${IHS_KEYSTORE}" ] && [ -f ${IHS_KEYSTORE} ] && rm -rf ${IHS_KEYSTORE};
                [ ! -z "${CERTIFICATE_DATASTORE}" ] && [ -f ${IHS_KEYSTORE} ] && rm -rf ${CERTIFICATE_DATASTORE};

                unset OPENSSL_CNF;
                unset OPENSSL_PASSIN;
                unset OPENSSL_PASSOUT;
                unset CERTUTIL_PASSIN;
                unset WORK_DIRECTORY;
                unset SITE_KEYFILE;
                unset SITE_CSRFILE;
                unset SITE_CRTFILE;
                unset SITE_PFXFILE;
                unset RET_CODE;
                unset CERTIFICATE_DATASTORE;
                unset IHS_KEYSTORE;
                unset METHOD_NAME;

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                return ${RETURN_CODE};
            fi

            ## import
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && keyman -cert -import -file ${SITE_PFXFILE} -pw $(<${OPENSSL_PASSOUT}) -type pkcs12 -target ${IHS_KEYSTORE} -target_pw $(<${OPENSSL_PASSOUT}) -target_type ${IHS_KEY_DB_TYPE} >> "${LOG_ROOT}"/keyman.${CONTEXT_ROOT} 2>&1;
            [ -z "${ENABLE_DEBUG}" ] || [ "${ENABLE_DEBUG}" = "${_FALSE}" ] && keyman -cert -import -file ${SITE_PFXFILE} -pw $(<${OPENSSL_PASSOUT}) -type pkcs12 -target ${IHS_KEYSTORE} -target_pw $(<${OPENSSL_PASSOUT}) -target_type ${IHS_KEY_DB_TYPE} > /dev/null 2>&1;
            unset RET_CODE; typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
            then
                RETURN_CODE=59;

                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a keyfile: RET_CODE -> ${RET_CODE}. Please try again.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
                [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
                [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
                [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
                [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
                [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
                [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
                [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};
                [ ! -z "${IHS_KEYSTORE}" ] && [ -f ${IHS_KEYSTORE} ] && rm -rf ${IHS_KEYSTORE};
                [ ! -z "${CERTIFICATE_DATASTORE}" ] && [ -f ${IHS_KEYSTORE} ] && rm -rf ${CERTIFICATE_DATASTORE};

                unset OPENSSL_CNF;
                unset OPENSSL_PASSIN;
                unset OPENSSL_PASSOUT;
                unset CERTUTIL_PASSIN;
                unset WORK_DIRECTORY;
                unset SITE_KEYFILE;
                unset SITE_CSRFILE;
                unset SITE_CRTFILE;
                unset SITE_PFXFILE;
                unset RET_CODE;
                unset CERTIFICATE_DATASTORE;
                unset IHS_KEYSTORE;
                unset METHOD_NAME;

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                return ${RETURN_CODE};
            fi

            ## and verify
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && keyman -cert -details -db ${IHS_KEYSTORE} -label ${CONTEXT_ROOT} -pw $(<${OPENSSL_PASSOUT}) -type ${IHS_KEY_DB_TYPE} >> "${LOG_ROOT}"/keyman.${CONTEXT_ROOT} 2>&1;
            [ -z "${ENABLE_DEBUG}" ] || [ "${ENABLE_DEBUG}" = "${_FALSE}" ] && keyman -cert -details -db ${IHS_KEYSTORE} -label ${CONTEXT_ROOT} -pw $(<${OPENSSL_PASSOUT}) -type ${IHS_KEY_DB_TYPE} > /dev/null 2>&1;
            unset RET_CODE; typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
            then
                RETURN_CODE=59;

                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a keyfile: RET_CODE -> ${RET_CODE}. Please try again.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
                [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
                [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};
                [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
                [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
                [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};
                [ ! -z "${SITE_CRTFILE}" ] && [ -f ${SITE_CRTFILE} ] && rm -rf ${SITE_CRTFILE};
                [ ! -z "${SITE_PFXFILE}" ] && [ -f ${SITE_PFXFILE} ] && rm -rf ${SITE_PFXFILE};
                [ ! -z "${IHS_KEYSTORE}" ] && [ -f ${IHS_KEYSTORE} ] && rm -rf ${IHS_KEYSTORE};
                [ ! -z "${CERTIFICATE_DATASTORE}" ] && [ -f ${IHS_KEYSTORE} ] && rm -rf ${CERTIFICATE_DATASTORE};

                unset OPENSSL_CNF;
                unset OPENSSL_PASSIN;
                unset OPENSSL_PASSOUT;
                unset CERTUTIL_PASSIN;
                unset WORK_DIRECTORY;
                unset SITE_KEYFILE;
                unset SITE_CSRFILE;
                unset SITE_CRTFILE;
                unset SITE_PFXFILE;
                unset RET_CODE;
                unset CERTIFICATE_DATASTORE;
                unset IHS_KEYSTORE;
                unset METHOD_NAME;

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                return ${RETURN_CODE};
            fi
            ;;
    esac

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
    [ ! -z "${CERTUTIL_PASSIN}" ] && [ -f ${CERTUTIL_PASSIN} ] && rm -rf ${CERTUTIL_PASSIN};
    [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
    [ ! -z "${OPENSSL_PASSOUT}" ] && [ -f ${OPENSSL_PASSOUT} ] && rm -rf ${OPENSSL_PASSOUT};

    unset OPENSSL_CNF;
    unset OPENSSL_PASSIN;
    unset OPENSSL_PASSOUT;
    unset CERTUTIL_PASSIN;
    unset WORK_DIRECTORY;
    unset SITE_KEYFILE;
    unset SITE_CSRFILE;
    unset SITE_CRTFILE;
    unset SITE_PFXFILE;
    unset RET_CODE;
    unset CERTIFICATE_DATASTORE;
    unset IHS_KEYSTORE;
    unset METHOD_NAME;

    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  createNewCertificate
#   DESCRIPTION:  Generates a certificate request for signing by an authority
#                 web instance
#    PARAMETERS:  None
#==============================================================================
function createNewCertificate
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    typeset OPENSSL_CNF=$(mktemp);
    typeset OPENSSL_PASSIN=$(mktemp);
    typeset WORK_DIRECTORY=${PLUGIN_WORK_DIR}/${CONTEXT_ROOT};
    typeset SITE_KEYFILE="${KEY_DIR}/${CONTEXT_ROOT}.key";
    typeset SITE_CSRFILE="${CSR_DIR}/${CONTEXT_ROOT}.csr";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPENSSL_CNF -> ${OPENSSL_CNF}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPENSSL_PASSIN -> ${OPENSSL_PASSIN}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WORK_DIRECTORY -> ${WORK_DIRECTORY}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_CSRFILE -> ${SITE_CSRFILE}";

    echo "$(returnRandomCharacters ${PASSWORD_LENGTH})" > ${OPENSSL_PASSIN};
    chmod 600 ${OPENSSL_PASSIN};

    ## modify the provided openssl conf to include proper hostname
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Modifying OpenSSL configuration...";
    sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/" ${OPENSSL_CONFIG} > ${OPENSSL_CNF};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Modification complete. Validating...";

    if [ ! -z "$(grep "%SITE_HOSTNAME%" ${OPENSSL_CNF})" ]
    then
        RETURN_CODE=59;

        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to modify OpenSSL configuration file. Please try again.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
        [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
        [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
        [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};

        unset OPENSSL_CNF;
        unset OPENSSL_PASSIN;
        unset WORK_DIRECTORY;
        unset SITE_KEYFILE;
        unset SITE_CSRFILE;
        unset RET_CODE;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

        return ${RETURN_CODE};
    fi

    ## generate key
    openssl genrsa -out ${SITE_KEYFILE} -passout file:${OPENSSL_PASSIN} > /dev/null 2>&1;
    unset RET_CODE; typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        RETURN_CODE=59;

        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a keyfile: RET_CODE -> ${RET_CODE}. Please try again.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
        [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
        [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
        [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};

        unset OPENSSL_CNF;
        unset OPENSSL_PASSIN;
        unset WORK_DIRECTORY;
        unset SITE_KEYFILE;
        unset SITE_CSRFILE;
        unset RET_CODE;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

        return ${RETURN_CODE};
    fi

    ## validate rsa key
    openssl rsa -in ${SITE_KEYFILE} -check -passin file:${OPENSSL_PASSIN} > /dev/null 2>&1;
    unset RET_CODE; typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        RETURN_CODE=59;

        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a keyfile: RET_CODE -> ${RET_CODE}. Please try again.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
        [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
        [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
        [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};

        unset OPENSSL_CNF;
        unset OPENSSL_PASSIN;
        unset WORK_DIRECTORY;
        unset SITE_KEYFILE;
        unset SITE_CSRFILE;
        unset RET_CODE;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

        return ${RETURN_CODE};
    fi

    ## generate csr
    openssl req -config ${OPENSSL_CONFIG} -batch -extensions ${EXTENSIONS} -new -key ${SITE_KEYFILE} -out ${SITE_CSRFILE} -passin file:${OPENSSL_PASSIN} > /dev/null 2>&1;
    unset RET_CODE; typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        RETURN_CODE=59;

        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a keyfile: RET_CODE -> ${RET_CODE}. Please try again.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
        [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
        [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
        [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};

        unset OPENSSL_CNF;
        unset OPENSSL_PASSIN;
        unset WORK_DIRECTORY;
        unset SITE_KEYFILE;
        unset SITE_CSRFILE;
        unset RET_CODE;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

        return ${RETURN_CODE};
    fi

    ## validate csr
    openssl req -text -noout -verify -in ${SITE_CSRFILE} > /dev/null 2>&1;
    unset RET_CODE; typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        RETURN_CODE=59;

        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a keyfile: RET_CODE -> ${RET_CODE}. Please try again.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
        [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};
        [ ! -z "${SITE_KEYFILE}" ] && [ -f ${SITE_KEYFILE} ] && rm -rf ${SITE_KEYFILE};
        [ ! -z "${SITE_CSRFILE}" ] && [ -f ${SITE_CSRFILE} ] && rm -rf ${SITE_CSRFILE};

        unset OPENSSL_CNF;
        unset OPENSSL_PASSIN;
        unset WORK_DIRECTORY;
        unset SITE_KEYFILE;
        unset SITE_CSRFILE;
        unset RET_CODE;
        unset METHOD_NAME;

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

        return ${RETURN_CODE};
    fi

    ## send CSR to authority
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Sending CSR notification...";

    typeset THIS_CNAME="${CNAME}";
    unset METHOD_NAME;
    unset CNAME;

    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

    ## validate the input
    . ${MAILER_CLASS} -m ${NOTIFY_CSR_EMAIL} -t ${NOTIFY_CSR_ADDRESS} -a ${SITE_CSRFILE} -e
    unset RET_CODE; typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

    CNAME="${THIS_CNAME}";
    typeset METHOD_NAME="${THIS_CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Email notification of certificate requested failed. Please send request manually.";
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${OPENSSL_CNF}" ] && [ -f ${OPENSSL_CNF} ] && rm -rf ${OPENSSL_CNF};
    [ ! -z "${OPENSSL_PASSIN}" ] && [ -f ${OPENSSL_PASSIN} ] && rm -rf ${OPENSSL_PASSIN};

    unset OPENSSL_CNF;
    unset OPENSSL_PASSIN;
    unset WORK_DIRECTORY;
    unset SITE_KEYFILE;
    unset SITE_CSRFILE;
    unset RET_CODE;
    unset METHOD_NAME;

    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

    return ${RETURN_CODE};
}

function createIHSCSR
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERTDB_STORE -> ${CERTDB_STORE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CSRSTORE -> ${CSRSTORE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MAILSTORE -> ${MAILSTORE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NA_CSR_SUBJECT -> ${NA_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CA_CSR_SUBJECT -> ${CA_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "AU_CSR_SUBJECT -> ${AU_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UK_CSR_SUBJECT -> ${UK_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERTIFICATE_DATABASE -> ${CERTIFICATE_DATABASE}";

    SITE_IDENTIFIER=$(echo ${TARGET_PLATFORM_CODE} | cut -d "_" -f 1);

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_IDENTIFIER -> ${SITE_IDENTIFIER}";

    if [ -s "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_STASH_SUFFIX} ] \
        && [ -s "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_REQ_SUFFIX} ] \
        && [ -s "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} ]
    then
        ## ok. we have a cert db and we've been asked to generate a csr. do it.
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generating CSR for ${SITE_DOMAIN_NAME}..";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Obtaining certificate information..";

        CERT_NICKNAME=$(keyman -cert -list personal -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
            -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} | grep -v ${CERTIFICATE_DATABASE} | sed -e "s/^ *//g");
        CERT_HOSTNAME=$(keyman -cert -details -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
            -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) -label "${CERT_NICKNAME}" -type ${IHS_KEY_DB_TYPE} | \
                grep Subject | cut -d "=" -f 2 | cut -d "," -f 1);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_NICKNAME -> ${CERT_NICKNAME}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_HOSTNAME -> ${CERT_HOSTNAME}";

        ## gsk7 kindof annoys me in that it isnt allowing me to create a
        ## csr of the same name as the certificate in the db. i do not know
        ## why. export the cert to p12, then convert it to a pem so we can
        ## use it later in the owner notify
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Exporting certificate..";

        keyman -cert -export -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
            -label ${CERT_NICKNAME} -target "${APP_ROOT}"/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs \
            -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) -target_pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) \
            -target_type pkcs12 -type ${IHS_KEY_DB_TYPE} -encryption strong;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate exported. Validating..";

        if [ -s "${APP_ROOT}"/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Export validated. Generating PEM..";

            openssl pkcs12 -nodes -nocerts -in "${APP_ROOT}"/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs \
                -out "${APP_ROOT}"/${PEMSTORE}/${CERTIFICATE_DATABASE}.pem \
                -password file:"${APP_ROOT}"/${IHS_CERT_DB_PASSFILE} \
                -passout pass:$("${APP_ROOT}"/${IHS_CERT_DB_PASSFILE});

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Pem generated. Validating..";

            if [ -s "${APP_ROOT}"/${PEMSTORE}/${CERTIFICATE_DATABASE}.pem ]
            then
                ## ok, pem was built
                rm -rf "${APP_ROOT}"/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs > /dev/null 2>&1;

                if [ "${CERT_HOSTNAME}" != "${SITE_DOMAIN_NAME}" ]
                then
                    ## hostname mismatch. use the one in the cert db, but writeLogEntry "AUDIT" "${METHOD_NAME}" of it
                    writeLogEntry writeLogEntry "AUDIT" "${METHOD_NAME}" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_DOMAIN_NAME does not match CERT_HOSTNAME. SITE_DOMAIN_NAME -> ${SITE_DOMAIN_NAME}, CERT_HOSTNAME -> ${CERT_HOSTNAME}. Using CERT_HOSTNAME.";

                    SITE_DOMAIN_NAME=${CERT_HOSTNAME};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_DOMAIN_NAME -> ${SITE_DOMAIN_NAME}";
                fi

                ## determine the subject to utilize
                if [ ! -z "$(grep -w ${SITE_DOMAIN_NAME} "${APP_ROOT}"/${SITE_OVERRIDES})" ]
                then
                    ## site exists in the site overrides file
                    CERT_SIGNER=$(grep -w ${SITE_DOMAIN_NAME} "${APP_ROOT}"/${SITE_OVERRIDES} | cut -d ":" -f 2);
                    CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                else
                    if [ "$(echo ${TARGET_PLATFORM_CODE} | cut -d "_" -f 2)" = "I" ]
                    then
                        CERT_SIGNER=${INTRANET_CERT_SIGNATORY};
                        CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                    elif [ "$(echo ${TARGET_PLATFORM_CODE} | cut -d "_" -f 2)" = "X" ]
                    then
                        CERT_SIGNER=${INTERNET_CERT_SIGNATORY};

                        case ${SITE_IDENTIFIER} in
                            [Pp][Hh]|[Vv][Hh]|[Bb][Rr]|[Aa][Hh]|[Bb][Uu]|[Vv][Oo]|[Vv][Oo][Vv][Hh])
                                CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                ;;
                            [Nn][Ww]|[Ss][Yy]|[Nn][Ww][Nn]|[Ss][Yy][Gg]|[Ss][Hh]|[Ll][Pp])
                                if [ ! -z "$(echo ${SITE_DOMAIN_NAME} | grep .ca)" ]
                                then
                                    CERT_SUBJECT=$(echo ${CA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                elif [ ! -z "$(echo ${SITE_DOMAIN_NAME} | grep .au)" ]
                                then
                                    CERT_SUBJECT=$(echo ${AU_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                else
                                    ## default to north america
                                    CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                fi
                                ;;
                            *)
                                ## unknown site identifier, default to north america for now
                                CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                ;;
                        esac
                    else
                        ## platform code doesn't specify an I or an X in the second field
                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An unknown platform type was encountered. Cannot continue.";

                        RETURN_CODE=4;
                    fi
                fi

                if [ -z "${RETURN_CODE}" ]
                then
                    ## ihs doesnt like ; or E=, so remove them
                    CERT_SUBJECT=$(echo ${CERT_SUBJECT} | cut -d ";" -f 1-6 | sed -e "s/;/,/g");

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_SIGNER -> ${CERT_SIGNER}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_SUBJECT -> ${CERT_SUBJECT}";

                    ## clean up the certificate database
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing certificate from database..";

                    keyman -cert -delete -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                        -label ${CERT_NICKNAME} -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} > /dev/null 2>&1;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing certificate request from database..";

                    keyman -certreq -delete -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                        -label ${CERT_NICKNAME} -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} > /dev/null 2>&1;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing keyman -certreq -create -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} -label ${CERT_NICKNAME} -file "${APP_ROOT}"/${CSRSTORE}/${CERT_NICKNAME}.csr -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} -dn ${CERT_SUBJECT} -size ${CERT_BIT_LENGTH}";

                    if [ "${VERBOSE}" = "${_TRUE}" ]
                    then
                        keyman -certreq -create -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                            -label ${CERT_NICKNAME} -file "${APP_ROOT}"/${CSRSTORE}/${CERT_NICKNAME}.csr -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) \
                            -type ${IHS_KEY_DB_TYPE} -dn "${CERT_SUBJECT}" -size ${CERT_BIT_LENGTH};
                    else
                        keyman -certreq -create -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                            -label ${CERT_NICKNAME} -file "${APP_ROOT}"/${CSRSTORE}/${CERT_NICKNAME}.csr -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) \
                            -type ${IHS_KEY_DB_TYPE} -dn "${CERT_SUBJECT}" -size ${CERT_BIT_LENGTH} > "${APP_ROOT}/${LOG_ROOT}"/keyman.csr-gen.${SITE_DOMAIN_NAME}.${IUSER_AUDIT} 2>&1;
                    fi

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "keyman executed..";

                    if [ -s "${APP_ROOT}"/${CSRSTORE}/${CERT_NICKNAME}.csr ]
                    then
                        ## cool, we have a csr. mail it out.
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generation complete. Mailing CSR..";

                        unset CNAME;
                        unset METHOD_NAME;

                        . ${MAILER_CLASS} -m ${NOTIFY_CSR_EMAIL} -p ${WEB_PROJECT_CODE} -a "${NOTIFY_CSR_ADDRESS}" -t ${NOTIFY_TYPE_NOTIFY} -e;
                        MAILER_CODE=${?};

                        CNAME=${THIS_CNAME};
                        typeset METHOD_NAME="${CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MAILER_CODE -> ${MAILER_CODE}";

                        if [ ${MAILER_CODE} -ne 0 ]
                        then
                            ## notification failed to send. writeLogEntry "AUDIT" "${METHOD_NAME}" but dont error
                            writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to send notification.";

                            RETURN_CODE=95;
                        fi

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Email sent. Continuing..";

                        RETURN_CODE=0;
                    else
                        ## no csr was generated. error out
                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No CSR was generated. Cannot continue.";

                        RETURN_CODE=5;
                    fi
                fi
            else
                ## no pem file, the owner notify wont generate properly
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No PEM was generated. Cannot continue.";

                RETURN_CODE=5;
            fi
        else
            ## no pkcs file, cant generate pem, the owner notify wont generate
            writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No PKCS#12 was generated. Cannot continue.";

            RETURN_CODE=5;
        fi
    else
        ## we dont have a cert database, so lets go out and get it
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate database not found for ${SITE_DOMAIN_NAME}. Obtaining..";

        SOURCE_CERT_DATABASE=$(echo ${CERTIFICATE_DATABASE} | cut -d "-" -f 1);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SOURCE_CERT_DATABASE -> ${SOURCE_CERT_DATABASE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating access to ${SOURCE_WEB_SERVER}..";

        $(ping ${SOURCE_WEB_SERVER} > /dev/null 2>&1);

        PING_RCODE=${?}

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

        if [ ${PING_RCODE} -eq 0 ]
        then
            ## run_scp_connection...
            for SUFFIX in ${IHS_DB_STASH_SUFFIX} ${IHS_DB_REQ_SUFFIX} ${IHS_DB_CRT_SUFFIX}
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command "${APP_ROOT}/${LIB_DIRECTORY}"/tcl/runSCPConnection.exp remote-copy ${SOURCE_WEB_SERVER} ${IHS_CERT_DIR}/${SOURCE_CERT_DATABASE}${SUFFIX} "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${SUFFIX} ${IHS_OWNING_USER}";

                "${APP_ROOT}/${LIB_DIRECTORY}"/tcl/runSCPConnection.exp remote-copy ${SOURCE_WEB_SERVER} \
                    ${IHS_CERT_DIR}/${SOURCE_CERT_DATABASE}${SUFFIX} \
                    "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${SUFFIX} ${IHS_OWNING_USER};

                if [ -s "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${SUFFIX} ]
                then
                    (( FILE_COUNT += 1 ));
                fi
            done

            unset SUFFIX;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILE_COUNT -> ${FILE_COUNT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Databases copied. Validating..";

            ## make sure we got the files..
            if [ ${FILE_COUNT} -eq 3 ]
            then
                ## ok. we have a cert db and we've been asked to generate a csr. do it.
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generating CSR for ${SITE_DOMAIN_NAME}..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Obtaining certificate information..";

                CERT_NICKNAME=$(keyman -cert -list personal -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                    -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} | grep -v ${CERTIFICATE_DATABASE} | sed -e "s/^ *//g");
                CERT_HOSTNAME=$(keyman -cert -details -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                    -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) -label ${CERT_NICKNAME} -type ${IHS_KEY_DB_TYPE} | \
                        grep Subject | cut -d "=" -f 2 | cut -d "," -f 1);

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_NICKNAME -> ${CERT_NICKNAME}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_HOSTNAME -> ${CERT_HOSTNAME}";

                ## gsk7 kindof annoys me in that it isnt allowing me to create a
                ## csr of the same name as the certificate in the db. i do not know
                ## why. export the cert to p12, then convert it to a pem so we can
                ## use it later in the owner notify
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Exporting certificate..";

                keyman -cert -export -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                    -label ${CERT_NICKNAME} -target "${APP_ROOT}"/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs \
                    -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) -target_pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) \
                    -target_type pkcs12 -type ${IHS_KEY_DB_TYPE} -encryption strong;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate exported. Validating..";

                if [ -s "${APP_ROOT}"/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Export validated. Generating PEM..";

                    openssl pkcs12 -in "${APP_ROOT}"/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs \
                        -out "${APP_ROOT}"/${PEMSTORE}/${CERTIFICATE_DATABASE}.pem \
                        -password file:"${APP_ROOT}"/${IHS_CERT_DB_PASSFILE} \
                        -passout pass:$("${APP_ROOT}"/${IHS_CERT_DB_PASSFILE});

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Pem generated. Validating..";

                    if [ -s "${APP_ROOT}"/${PEMSTORE}/${CERTIFICATE_DATABASE}.pem ]
                    then
                        ## ok, pem was built
                        rm -rf "${APP_ROOT}"/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs > /dev/null 2>&1;

                        if [ "${CERT_HOSTNAME}" != "${SITE_DOMAIN_NAME}" ]
                        then
                            ## hostname mismatch. use the one in the cert db, but writeLogEntry "AUDIT" "${METHOD_NAME}" of it
                            writeLogEntry writeLogEntry "AUDIT" "${METHOD_NAME}" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_DOMAIN_NAME does not match CERT_HOSTNAME. SITE_DOMAIN_NAME -> ${SITE_DOMAIN_NAME}, CERT_HOSTNAME -> ${CERT_HOSTNAME}. Using CERT_HOSTNAME.";

                            RETURN_CODE=99;

                            SITE_DOMAIN_NAME=${CERT_HOSTNAME};

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_DOMAIN_NAME -> ${SITE_DOMAIN_NAME}";
                        fi

                        ## determine the subject to utilize
                        if [ "$(echo ${TARGET_PLATFORM_CODE} | cut -d "_" -f 2)" = "I" ]
                        then
                            if [[ "${SITE_IDENTIFIER}" = "[Cc][Ll]" ]]
                            then
                                ## clarity cert. required for verisign.
                                CERT_SIGNER=${INTERNET_CERT_SIGNATORY};
                            else
                                CERT_SIGNER=${INTRANET_CERT_SIGNATORY};
                            fi

                            CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                        elif [ "$(echo ${TARGET_PLATFORM_CODE} | cut -d "_" -f 2)" = "X" ]
                        then
                            CERT_SIGNER=${INTERNET_CERT_SIGNATORY};

                            case ${SITE_IDENTIFIER} in
                                [Pp][Hh]|[Vv][Hh]|[Bb][Rr]|[Aa][Hh]|[Bb][Uu]|[Vv][Oo]|[Vv][Oo][Vv][Hh])
                                    CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                    ;;
                                [Nn][Ww]|[Ss][Yy]|[Nn][Ww][Nn]|[Ss][Yy][Gg]|[Ss][Hh]|[Ll][Pp])
                                    if [ ! -z "$(echo ${SITE_DOMAIN_NAME} | grep .ca)" ]
                                    then
                                        CERT_SUBJECT=$(echo ${CA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                    elif [ ! -z "$(echo ${SITE_DOMAIN_NAME} | grep .au)" ]
                                    then
                                        CERT_SUBJECT=$(echo ${AU_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                    else
                                        ## default to north america
                                        CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                    fi
                                    ;;
                                *)
                                    ## unknown site identifier, default to north america for now
                                    CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                    ;;
                            esac
                        else
                            ## platform code doesn't specify an I or an X in the second field
                            writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An unknown platform type was encountered. Cannot continue.";

                            RETURN_CODE=4;
                        fi

                        if [ -z "${RETURN_CODE}" ] || [ ${RETURN_CODE} -eq 99 ]
                        then
                            ## ihs doesnt like ; or E=, so remove them
                            CERT_SUBJECT=$(echo ${CERT_SUBJECT} | cut -d ";" -f 1-6 | sed -e "s/;/,/g");

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_SIGNER -> ${CERT_SIGNER}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_SUBJECT -> ${CERT_SUBJECT}";

                            ## clean up the certificate database
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing certificate from database..";

                            keyman -cert -delete -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                                -label ${CERT_NICKNAME} -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} > /dev/null 2>&1;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing certificate request from database..";

                            keyman -certreq -delete -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                                -label ${CERT_NICKNAME} -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} > /dev/null 2>&1;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing keyman -certreq -create -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} -label ${CERT_NICKNAME} -file "${APP_ROOT}"/${CSRSTORE}/${CERT_NICKNAME}.csr -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} -dn ${CERT_SUBJECT} -size ${CERT_BIT_LENGTH}";

                            if [ "${VERBOSE}" = "${_TRUE}" ]
                            then
                                keyman -certreq -create -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                                    -label ${CERT_NICKNAME} -file "${APP_ROOT}"/${CSRSTORE}/${CERT_NICKNAME}.csr -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) \
                                    -type $(echo ${IHS_DB_CRT_SUFFIX} | sed -e "s/.//") -dn "${CERT_SUBJECT}" -size ${CERT_BIT_LENGTH};
                            else
                                keyman -certreq -create -db "${APP_ROOT}"/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                                    -label ${CERT_NICKNAME} -file "${APP_ROOT}"/${CSRSTORE}/${CERT_NICKNAME}.csr -pw $(cat "${APP_ROOT}"/${IHS_CERT_DB_PASSFILE}) \
                                    -type $(echo ${IHS_DB_CRT_SUFFIX} | sed -e "s/.//") -dn "${CERT_SUBJECT}" -size ${CERT_BIT_LENGTH} > "${APP_ROOT}/${LOG_ROOT}"/keyman.csr-gen.${SITE_DOMAIN_NAME}.${IUSER_AUDIT} 2>&1;
                            fi

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "keyman executed..";

                            if [ -s "${APP_ROOT}"/${CSRSTORE}/${CERT_NICKNAME}.csr ]
                            then
                                ## cool, we have a csr. mail it out.
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generation complete. Mailing CSR..";

                                unset CNAME;
                                unset METHOD_NAME;

                                . ${MAILER_CLASS} -m ${NOTIFY_CSR_EMAIL} -p ${WEB_PROJECT_CODE} -a "${NOTIFY_CSR_ADDRESS}" -t ${NOTIFY_TYPE_NOTIFY} -e;
                                MAILER_CODE=${?};

                                CNAME=${THIS_CNAME};
                                typeset METHOD_NAME="${CNAME}#${0}";

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MAILER_CODE -> ${MAILER_CODE}";

                                if [ ${MAILER_CODE} -ne 0 ]
                                then
                                    ## notification failed to send. writeLogEntry "AUDIT" "${METHOD_NAME}" but dont error
                                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to send notification.";

                                    RETURN_CODE=95;
                                else
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Email sent. Continuing..";

                                    RETURN_CODE=0;
                                fi
                            else
                                ## no csr was generated. error out
                                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No CSR was generated. Cannot continue.";

                                RETURN_CODE=5;
                            fi
                        fi
                    else
                        ## no pem file, the owner notify wont generate properly
                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No PEM was generated. Cannot continue.";

                        RETURN_CODE=5;
                    fi
                else
                    ## no pkcs file, cant generate pem, the owner notify wont generate
                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No PKCS#12 was generated. Cannot continue.";

                    RETURN_CODE=5;
                fi
            else
                ## failed to obtain the cert db, cant generate a csr
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to obtain the necessary certificate databases. Cannot continue.";

                RETURN_CODE=6;
            fi
        else
            ## source web server appears unavailable, so we cant go get our files
            writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to obtain the necessary certificate databases. Cannot continue.";

            RETURN_CODE=24;
        fi
    fi

    FILE_COUNT=0;
    unset FILE_COUNT;
    unset CERT_HOSTNAME;
    unset CERT_SIGNER;
    unset CERT_SUBJECT;
    unset SUFFIX;
    unset RET_CODE;
    unset MAILER_CODE;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=3;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    echo "${THIS_CNAME} - Create a skeleton zone file with the necessary components.\n";
    echo "Usage: ${THIS_CNAME}[ -t <request type> ] [ -c <context root> ] [ -h <site hostname> ] [ -w <webserver> ] [ -o <openssl config> ] [ -n ] [ -r ] [ -s ] [ -e ] [ -h|-? ]
    -c         -> The context root for the web instance
    -h         -> The site hostname for the web instance
    -w         -> The web platform, such as httpd
    -o         -> The OpenSSL configuration file to utilize
    -n         -> Create a new certificate request
    -r         -> Create a certificate renewal request
    -s         -> Create a self-signed certificate
    -e         -> Execute processing
    -h|-?      -> Show this help\n";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

    return ${RETURN_CODE};
}
[ ${#} -eq 0 ] && usage; RETURN_CODE=${?};

while getopts "c:h:w:o:nrseh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        c)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CONTEXT_ROOT..";

            ## Capture the site root
            typeset CONTEXT_ROOT=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONTEXT_ROOT -> ${CONTEXT_ROOT}";
            ;;
        h)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_HOSTNAME..";

            ## Capture the change control
            typeset -l SITE_HOSTNAME="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME -> ${SITE_HOSTNAME}";
            ;;
        w)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting WS_PLATFORM..";

            ## Capture the change control
            typeset -u WS_PLATFORM="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WS_PLATFORM -> ${WS_PLATFORM}";
            ;;
        o)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting OPENSSL_CONFIG..";

            ## Capture the change control
            [ -z "${OPTARG}" ] && typeset OPENSSL_CONFIG="${OPENSSL_CONFIG_FILE}" || typeset OPENSSL_CONFIG="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPENSSL_CONFIG -> ${OPENSSL_CONFIG}";
            ;;
        n)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting NEW_CERTIFICATE..";

            ## Capture the change control
            typeset NEW_CERTIFICATE="${_TRUE}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NEW_CERTIFICATE -> ${NEW_CERTIFICATE}";
            ;;
        r)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting RENEW_CERTIFICATE..";

            ## Capture the change control
            typeset RENEW_CERTIFICATE="${_TRUE}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RENEW_CERTIFICATE -> ${RENEW_CERTIFICATE}";
            ;;
        s)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SELF_SIGNED..";

            ## Capture the change control
            typeset SELF_SIGNED="${_TRUE}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SELF_SIGNED -> ${SELF_SIGNED}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${CONTEXT_ROOT}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No context root was provided. Unable to continue processing.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=15;
            elif [ -z "${SITE_HOSTNAME}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No site hostname was provided. Unable to continue processing.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=18;
            elif [ -z "${WS_PLATFORM}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No webserver platform was provided. Unable to continue processing.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=37;
            else
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                if [ ! -s ${BUILD_CONFIG_FILE} ]
                then
                    RETURN_CODE=1;

                    writeLogEntry "INFO" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Build processing has been disabled.";
                else
                    . ${BUILD_CONFIG_FILE};

                    if [ -z "${BUILD_LOADED}" ]
                    then
                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to load build configuration. Cannot continue.";

                        RETURN_CODE=21;
                    else
                        if [ ! -f ${PLATFORM_CONFIG_FILES}/${WS_PLATFORM}.config ]
                        then
                            writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to load platform configuration. Cannot continue.";

                            RETURN_CODE=21;
                        else
                            . ${PLATFORM_CONFIG_FILES}/${WS_PLATFORM}.config

                            if [ ! -z "${NEW_CERTIFICATE}" ] && [ "${NEW_CERTIFICATE}" = "${_TRUE}" ]
                            then
                                [ ! -z "${SELF_SIGNED}" ] && [ "${SELF_SIGNED}" = "${_TRUE}" ] && createSelfSignedCertificate && RETURN_CODE=${?} || createNewCertificate; RETURN_CODE=${?};
                            else
                                createCertificateRenewal; RETURN_CODE=${?};
                            fi
                        fi
                    fi
                fi
            fi
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage; RETURN_CODE=${?};
            ;;
    esac
done

trap 'unlockProcess "${LOCKFILE}" "${$}"; return "${RETURN_CODE}"' INT TERM EXIT;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset RET_CODE;
unset CONTEXT_ROOT;
unset SITE_HOSTNAME;
unset WS_PLATFORM;
unset OPENSSL_CONFIG;
unset NEW_CERTIFICATE;
unset RENEW_CERTIFICATE;
unset SELF_SIGNED;
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

[ -z "${RETURN_CODE}" ] && return 1 || return "${RETURN_CODE}";
