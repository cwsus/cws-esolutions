#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  constants.sh
#         USAGE:  ./constants.sh
#   DESCRIPTION:  Sets and unsets system variables
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
CNAME="$(basename "${0}")";
THIS_CNAME="${CNAME}";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";
typeset -r PLUGIN_NAME="DNSAdministration";

## load application-wide constants if not already done
if [ -z "${APP_ROOT}" ]
then
    case $(pwd) in
        *monitors*|*executors*|*sys*|*bin*) . $(pwd)/../../../constants.sh ;;
        *home*|*lib*) . ${SCRIPT_ROOT}/../../../constants.sh ;;
        *) . ${SCRIPT_ROOT}/../../constants.sh ;;
    esac
fi

unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

## validate the input
[ ! -z "${ENABLE_SECURITY}" ] && [ "${ENABLE_SECURITY}" = "${_TRUE}" ] && ${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

CNAME="${THIS_CNAME}";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ ! -z "${ENABLE_SECURITY}" ] && [ "${ENABLE_SECURITY}" = "${_TRUE}" ] && [ ${RET_CODE} -ne 0 ]
then
    print "Security configuration does not allow the requested action.";

    return ${RET_CODE};
fi

unset RET_CODE;

###############################################################################
#       check if is running on a eSupport DR Node, exit if it is not.
###############################################################################
typeset -r ES_LIB="/opt/esupport/lib";
typeset -r ECOMSERVER_MODULE="${ES_LIB}/runsOnEcomServer.mod";

if [ -s ${ECOMSERVER_MODULE} ]
then
    . ${ECOMSERVER_MODULE};

    runsOnEcomServer "ED";
fi

typeset -rx PLUGIN_ROOT_DIR=${PLUGIN_DIR}/${PLUGIN_NAME};
typeset -rx PLUGIN_CONF_BASE=${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY};

case ${EXPORT_ENVIRONMENT} in
    [Ss][Tt][Gg]|[Ss][Tt][Aa][Gg][Ee]) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/stg/ ;;
    [Uu][Aa][Tt]|[Qq][Aa]) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/qa/ ;;
    [Ii][Ss][Tt]|[Dd][Ee][Vv]|[Dd][Ee][Vv][Ee][Ll][Oo][Pp][Mm][Ee][Nn][Tt]) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/dev/ ;;
    *) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/ ;; ## default to production
esac

[[ -z "${PLUGIN_CONF_ROOT}" || ! -s ${PLUGIN_CONF_ROOT}/plugin.properties ]] && return 1;

typeset -rx PLUGIN_LOADED=true;
typeset -rx PLUGIN_CONFIG=${PLUGIN_CONF_ROOT}/plugin.properties;

[ -f ${PLUGIN_CONFIG} ] && . ${PLUGIN_CONFIG};

## application information
typeset -rx REMOTE_APP_ROOT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/remote_app_root\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx TMP_DIRECTORY=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/temp_dir\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx DATA_DIRECTORY=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/data_dir\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx LIST_DISPLAY_MAX=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/max_list_display\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx BACKUP_LIST=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/backup_file_list\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx LOCAL_EXECUTION=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/local_execution\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ENVIRONMENT_SCRIPT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/environment_script\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx HIGH_PRIVILEGED_PORT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/high_privileged_port\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NO_CONTACT_INFO=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/no_contact_info\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx PLUGIN_PATH=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/app_path\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_PATH=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/iplanet_path\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_PATH=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/ihs_path\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx PLUGIN_LIBRARY_PATH=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/ld_library_path\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx PLUGIN_CLASS_PATH=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/classpath\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx CORE_EXCEPTION_LIST=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/core_exception_list\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx TMP_EXCEPTION_LIST=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/temp_exception_list\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx WEB_MGMT_ENABLED=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/web.mgmt.enabled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx CERT_MGMT_ENABLED=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/cert.mgmt.enabled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IS_WEB_MGMT_ENABLED=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/web.mgmt.enabled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IS_CERT_MGMT_ENABLED=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/web.build.enabled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IS_WEB_BUILD_ENABLED=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/cert.mgmt.enabled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx EINFO_WEBSITE_DEFS=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/einfo_website_defs\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx EINFO_PLATFORM_DEFS=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/einfo_platform_defs\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx EINFO_MACHINE_DEFS=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/einfo_machine_defs\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx EINFO_CONTACT_DEFS=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/einfo_contact_defs\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx INTERNET_TYPE_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/internet_type_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx INTRANET_TYPE_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/intranet_type_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ENV_TYPE_IST=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/env_type_ist\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ENV_TYPE_QA=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/env_type_qa\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ENV_TYPE_STG=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/env_type_stg\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ENV_TYPE_TRN=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/env_type_trn\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ENV_TYPE_PRD=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/env_type_prd\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx SOLARIS_OPERATIONAL_IFACE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/solaris_operational_iface\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');

## set up an ignore list for servers that arent available or shouldnt be used
typeset -rx SERVER_IGNORE_LIST=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/server_ignore_list\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');

## resource files
typeset -rx PLUGIN_ERROR_MESSAGES=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/error_resources\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx PLUGIN_MESSAGES=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/message_resources\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');

## external utils
typeset -rx DNS_SERVICE_URL=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/dns_service_url\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');

## security and access control
typeset -rx PLUGIN_SECURITY_CONFIG=${PLUGIN_CONF_ROOT}/$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/security_file\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ENFORCE_SECURITY=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/enforce_system_security\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ALLOWED_SERVERS=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/allowed_servers\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx SECURITY_OVERRIDE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/security_override\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx RANDOM_GENERATOR=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/random_generator\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ENTROPY_GENERATOR=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/entropy_generator\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ENTROPY_FILE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/entropy_file\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ENTROPY_FILE_SIZE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/entropy_file_size\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_CERT_DB_PASSFILE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/iplanet_db_pass_file\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_CERT_DB_PASSFILE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/ihs_db_pass_file\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx SECURITY_TOKENS=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/security_tokens\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx SME_NAME=$(sed -e '/^ *#/d;s/#.*//' /etc/passwd | awk -F  ":" '/Kevin/{print $1}');
set -A AUTHORIZED_USERS $(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/authorized_users\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g'); typeset -rx AUTHORIZED_USERS;
set -A AUTHORIZED_GROUPS $(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/authorized_groups\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g'); typeset -rx AUTHORIZED_GROUPS;

## backup configuration
typeset -rx PLUGIN_BACKUP_CONFIG=${PLUGIN_CONF_ROOT}/$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/backup_properties\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IS_BACKUP_ENABLED=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/perform_full_backup\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx BACKUP_DIRECTORY=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/backup_dir\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx BACKUP_RETENTION_TIME=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/backup_lifetime\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx BACKUP_FILE_NAME=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/backup_file_name\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx WEB_BACKUP_PREFIX=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/web_backup_prefix\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx CERT_BACKUP_PREFIX=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/cert_backup_prefix\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx CERT_BACKUP_DIR=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/cert_backup_dir\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');

## mail data
typeset -rx PLUGIN_MAIL_CONFIG=${PLUGIN_CONF_ROOT}/$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/email_configuration\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx MAILSTORE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/mailstore_dir\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx MAIL_TEMPLATE_DIR=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/mail_template_dir\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx SEND_NOTIFIES=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/send_notifies\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NOTIFY_FROM_ADDRESS=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_from_address\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NOTIFY_TYPE_NOTIFY=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_type_notify\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NOTIFY_TYPE_ALERT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_type_alert\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NOTIFY_CSR_ADDRESS=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_csr_address\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NOTIFY_CSR_EMAIL=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_csr_email\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NOTIFY_VCSR_SUBJECT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_vcsr_subject\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NOTIFY_HCSR_SUBJECT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_hcsr_subject\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NOTIFY_OWNER_EMAIL=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_owner_email\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NOTIFY_OWNER_CERT_EXPIRY_EMAIL=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_owner_cert_expiry\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NOTIFY_OWNER_SUBJECT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_owner_subject\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NOTIFY_PEM_ADDRESS=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_pem_address\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NOTIFY_PEM_EMAIL=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_pem_email\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NOTIFY_PEM_SUBJECT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_pem_subject\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ALERT_CERTIFICATE_ADDRESS=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/alert_certificate_address\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ALERT_CERTIFICATE_SUBJECT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/alert_certificate_subject\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ALERT_CERTIFICATE_EMAIL=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/alert_certificate_email\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx SITE_MONITOR_ADDRESS=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/alert_certificate_address\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx SITE_MONITOR_SUBJECT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/alert_certificate_subject\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx SITE_MONITOR_EMAIL=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/alert_certificate_email\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');

## web config
typeset -rx PLUGIN_WEB_CONFIG=${APP_ROOT}/$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/web_config_file\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NA_PRI_DATACENTER_ID=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/na_pri_datacenter_id\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NA_SEC_DATACENTER_ID=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/na_sec_datacenter_id\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx UK_PRI_DATACENTER_ID=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/uk_pri_datacenter_id\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx UK_SEC_DATACENTER_ID=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/uk_sec_datacenter_id\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx AVAILABLE_DATACENTERS=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/available_datacenters\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx PROJECT_CODE_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/project_code_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx SITE_HOSTNAME_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/site_hostname_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx PLATFORM_CODE_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/platform_code_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx SERVERID_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/serverid_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_TYPE_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_type_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_INIT_SCRIPT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_init_script\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_OWNING_USER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_owning_user\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_PROCESS_USER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_process_user\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_OWNING_GROUP=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_owning_group\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_CONFIG_PATH=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_config_path\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_SERVER_CONFIG=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_server_config\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_CORE_CONFIG=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_core_config\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_CERT_DIR=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_cert_store\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_CERT_STORE_PREFIX=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_cert_store_prefix\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_CERT_STORE_KEY_SUFFIX=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_cert_store_key_suffix\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_CERT_STORE_CERT_SUFFIX=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_cert_store_cert_suffix\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_START_SCRIPT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_start_script\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_STOP_SCRIPT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_stop_script\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_RESTART_SCRIPT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_restart_script\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_ROTATE_SCRIPT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_rotate_script\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_RECONFIG_SCRIPT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_reconfig_script\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_STARTUP_IGNORE_LIST=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_startup_ignore_list\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_PID_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_pid_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_URLHOSTS_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_urlhosts_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_SERVERNAME_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_servername_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_SECURITY_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_security_indicator\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_NICKNAME_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_nickname_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_WEB_CONFIG=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_web_config\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_PORT_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_port_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_SUDO_START_WEB=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_sudo_start_web\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_SUDO_STOP_WEB=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_sudo_stop_web\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_WEB_TMPDIR=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_web_tmpdir\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_IST_ROOT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_ist_root\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_QA_ROOT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_qa_root\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_SERVER_ROOT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_server_root\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_WAS_FUNCTION=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_was_function\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_WAS_BOOTSTRAP=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_was_bootstrap\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_WAS_HANDLER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_was_handler\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_BASE_LOG_ROOT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_base_log_root\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_BASE_DOC_ROOT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_base_doc_root\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_TMPDIR_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_tmpdir_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_ACL_DIR=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_acl_dir\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_ACL_NAMES=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_acl_names\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_PASSWORD_FILE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_password_file\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_SSL_TEMPLATE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_ssl_template\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_NOSSL_TEMPLATE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_nonssl_template\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_BOTH_TEMPLATE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_both_template\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_ACL_TEMPLATE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_acl_template\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_CERTDB_TEMPLATE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_certdb_template\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IPLANET_KEYDB_TEMPLATE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_keydb_template\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');

## ihs configuration
typeset -rx IHS_REMOTE_APP_ROOT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_app_root\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_TYPE_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_type_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_CERT_DIR=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_db_dir\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_DB_STASH_SUFFIX=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_db_stash_suffix\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_DB_REQ_SUFFIX=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_db_req_suffix\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_DB_CRT_SUFFIX=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_db_crt_suffix\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_KEY_DB_TYPE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_key_db_type\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_START_SCRIPT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_start_script\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_STOP_SCRIPT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_stop_script\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_CONFIG_PATH=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_config_path\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_SERVER_CONFIG=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_server_config\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_OWNING_USER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_owning_user\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_PROCESS_USER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_process_user\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_OWNING_GROUP=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_owning_group\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_INIT_SCRIPT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_init_script\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_WEB_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_web_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_PID_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_pid_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_WAS_MODULE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_was_module\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_WAS_PLUGIN=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_was_plugin\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_SSL_TEMPLATE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_ssl_template\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_NOSSL_TEMPLATE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_nonssl_template\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_BOTH_TEMPLATE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_both_template\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_KEYDB_TEMPLATE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_keydb_template\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_CERTDB_TEMPLATE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_certdb_template\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx IHS_REQDB_TEMPLATE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_reqdb_template\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx RESTART_SERVICE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/mgmt.restart.service\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ADD_EXCEPTION=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/mgmt.add.exception\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');

## cert configuration
typeset -rx WEB_SSL_CONFIG=${PLUGIN_CONF_BASE}/$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ssl_config_file\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx CERTDB_STORE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/app_certdb_store\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx CSRSTORE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/csr_dir\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx PEMSTORE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/pem_dir\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx PKCS12STORE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/pkcs12_dir\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx CERTSTORE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/certs_dir\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx SSL_EXCEPTION_LIST=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ssl_exception_list\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx PEM_SITES_LIST=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/pem_site_list\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx SELF_SIGN_SUBJECT="$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/self_sign_subject\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NA_CSR_SUBJECT="$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/na_csr_subject\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx AU_CSR_SUBJECT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/au_csr_subject\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx CA_CSR_SUBJECT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ca_csr_subject\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx UK_CSR_SUBJECT=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/uk_csr_subject\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx CERT_BIT_LENGTH=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/csr_bitsize\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx CSR_DIRECTORY=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/csr_directory\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx METASLOT_ENABLED=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/metaslot_enabled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx METASLOT_NAME=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/metaslot_name\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx INTERNET_CERT_SIGNATORY=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/internet_cert_signatory\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx INTRANET_CERT_SIGNATORY=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/intranet_cert_signatory\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx VALIDATION_PERIOD=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/validation_period\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx KEYSTORE_CLEANUP_ENABLED=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/keystore_cleanup_enabled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx KEYSTORE_BACKUP_ENABLED=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/keystore_backup_enabled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx KEYSTORE_DIR_IDENTIFIER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/keystore_dir_identifier\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx SITE_OVERRIDES=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/cert_signatory_overrides\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx CERT_STORE_ARCHIVE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/cert_store_archive\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx ROOT_CERT_STORE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/root_cert_store\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx OPENSSL_CONFIG_FILE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/openssl_config_file\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx GENERATE_SELF_SIGNED=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/generate_self_signed\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');

## ssl port "INFO"
typeset -rx STD_SSL_PORT_NUMBER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/std_ssl_port\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx NONSTD_SSL_PORT_NUMBER=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/nonstd_ssl_port\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');

## build config
typeset -rx WEB_BUILD_CONFIG=${PLUGIN_ROOT_DIR}/$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/build_config_file\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx BUILD_TMP_DIR=$(sed -e '/^ *#/d;s/#.*//' ${WEB_BUILD_CONFIG} | awk -F  "=" '/build_tmp_dir\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx BUILD_TYPE_SSL=$(sed -e '/^ *#/d;s/#.*//' ${WEB_BUILD_CONFIG} | awk -F  "=" '/build_type_ssl\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx BUILD_TYPE_NOSSL=$(sed -e '/^ *#/d;s/#.*//' ${WEB_BUILD_CONFIG} | awk -F  "=" '/build_type_nossl\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx BUILD_TYPE_BOTH=$(sed -e '/^ *#/d;s/#.*//' ${WEB_BUILD_CONFIG} | awk -F  "=" '/build_type_both\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');

## monitoring
typeset -rx PLUGIN_MONITOR_CONFIG=${PLUGIN_CONF_ROOT}/$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/monitor_config_file\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx MONITOR_WORK_DIR=${PLUGIN_ROOT_DIR}/$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MONITOR_CONFIG} | awk -F  "=" '/monitor_work_dir\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx LOG_FILE_DELAY=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MONITOR_CONFIG} | awk -F  "=" '/log_file_delay\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx OPTIONS_MONITOR_STRING=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MONITOR_CONFIG} | awk -F  "=" '/monitor_options_string\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx SVCTRACE_MONITOR_STRING=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MONITOR_CONFIG} | awk -F  "=" '/monitor_svctrace_string\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx SVCTRACE_MONITOR_STRING=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MONITOR_CONFIG} | awk -F  "=" '/monitor_svctrace_string\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx MONITOR_OUTPUT_FILE=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MONITOR_CONFIG} | awk -F  "=" '/monitor_output_file\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -rx MONITOR_OUTPUT_EXPIRES=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MONITOR_CONFIG} | awk -F  "=" '/monitor_output_expires\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g');

## solaris interface "INFO"


## month directors
typeset -rx Jan=01;
typeset -rx Feb=02;
typeset -rx Apr=03;
typeset -rx Mar=04;
typeset -rx May=05;
typeset -rx Jun=06;
typeset -rx Jul=07;
typeset -rx Aug=08;
typeset -rx Sep=09;
typeset -rx Oct=10;
typeset -rx Nov=11;
typeset -rx Dec=12;

## counters
typeset -i FILE_COUNT=0;
typeset -i ERROR_COUNT=0;
typeset -i AUTHORIZATION_COUNT=0;

## export what needs to be exported
## set path, incorporating approot
typeset -x PATH=${PATH}:${PLUGIN_PATH}:${IPLANET_PATH}:${IHS_PATH};
typeset -x LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${PLUGIN_LIBRARY_PATH};
typeset -x CLASSPATH=${CLASSPATH}:${PLUGIN_CLASS_PATH};

[ -s ${PLUGIN_ROOT_DIR}/lib/aliases ] && . ${PLUGIN_ROOT_DIR}/lib/aliases;
[ -s ${PLUGIN_ROOT_DIR}/lib/functions ] && . ${PLUGIN_ROOT_DIR}/lib/functions;

## and finally make sure our directories exist
[ ! -d ${APP_ROOT}/${TMP_DIRECTORY} ] && mkdir -p ${APP_ROOT}/${TMP_DIRECTORY};
[ ! -d ${APP_ROOT}/${CSRSTORE} ] && mkdir -p ${APP_ROOT}/${CSRSTORE};
[ ! -d ${APP_ROOT}/${MAILSTORE} ] && mkdir -p ${APP_ROOT}/${MAILSTORE};
[ ! -d ${APP_ROOT}/${PEMSTORE} ] && mkdir -p ${APP_ROOT}/${PEMSTORE};
[ ! -d ${APP_ROOT}/${PKCS12STORE} ] && mkdir -p ${APP_ROOT}/${PKCS12STORE};
[ ! -d ${APP_ROOT}/${CERTSTORE} ] && mkdir -p ${APP_ROOT}/${CERTSTORE};
[ ! -d ${APP_ROOT}/${BACKUP_DIRECTORY} ] && mkdir -p ${APP_ROOT}/${BACKUP_DIRECTORY};
[ ! -d ${APP_ROOT}/${DATA_DIRECTORY} ] && mkdir -p ${APP_ROOT}/${DATA_DIRECTORY};
[ ! -d ${APP_ROOT}/${BUILD_TMP_DIR} ] && mkdir -p ${APP_ROOT}/${BUILD_TMP_DIR};

## make sure our exception lists exist
[ ! -f ${APP_ROOT}/${CORE_EXCEPTION_LIST} ] && touch ${APP_ROOT}/${CORE_EXCEPTION_LIST};
[ ! -f ${APP_ROOT}/${TMP_EXCEPTION_LIST} ] && touch ${APP_ROOT}/${TMP_EXCEPTION_LIST};
[ ! -f ${APP_ROOT}/${SSL_EXCEPTION_LIST} ] && touch ${APP_ROOT}/${SSL_EXCEPTION_LIST};
