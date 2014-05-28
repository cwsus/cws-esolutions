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
#        AUTHOR:  Kevin Huntly <kmhuntly@gmail.com
#       COMPANY:  CaspersBox Web Services
#       VERSION:  1.0
#       CREATED:  ---
#      REVISION:  ---
#==============================================================================
###############################################################################
#       check if is running on a eSupport DR Node, exit if it is not.
###############################################################################
ES_LIB="/opt/esupport/lib"
ECOMSERVER_MODULE="${ES_LIB}/runsOnEcomServer.mod"
if [ -s ${ECOMSERVER_MODULE} ]
then
. ${ECOMSERVER_MODULE}
    runsOnEcomServer "ED"
fi

typeset -r -x PLUGIN_NAME="webadmin";

## load application-wide constants if not already done
if [ -z "${APP_ROOT}" ]
then
    case $(pwd) in
        *monitors*|*executors*|*sys*) . ${SCRIPT_ROOT}/../../../${PLUGIN_NAME}.sh ;;
        *home*) . ${SCRIPT_ROOT}/../../../${PLUGIN_NAME}.sh ;;
        *) . ${SCRIPT_ROOT}/../../${PLUGIN_NAME}.sh ;;
    esac
fi

typeset -r -x PLUGIN_ROOT_DIR=${APP_ROOT}/${PLUGIN_LIB_DIR}/${PLUGIN_NAME};
typeset -r -x PLUGIN_CONF_BASE=${APP_ROOT}/${PLUGIN_CONFIG_DIR}/${PLUGIN_NAME};

case ${EXPORT_ENVIRONMENT} in
    [Ss][Tt][Gg]|[Ss][Tt][Aa][Gg][Ee]) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/stg/ ;;
    [Uu][Aa][Tt]|[Qq][Aa]) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/qa/ ;;
    [Ii][Ss][Tt]|[Dd][Ee][Vv]|[Dd][Ee][Vv][Ee][Ll][Oo][Pp][Mm][Ee][Nn][Tt]) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/dev/ ;;
    *) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/ ;; ## default to production
esac

[[ -z "${PLUGIN_CONF_ROOT}" || ! -s ${PLUGIN_CONF_ROOT}/plugin.properties ]] && return 1;

typeset -r -x PLUGIN_LOADED=true;
typeset -r -x PLUGIN_CONFIG=${PLUGIN_CONF_ROOT}/plugin.properties;

## application information
typeset -r -x REMOTE_APP_ROOT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x TMP_DIRECTORY=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/temp_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x DATA_DIRECTORY=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/data_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x LIST_DISPLAY_MAX=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/max_list_display/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x BACKUP_LIST=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/backup_file_list/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x LOCAL_EXECUTION=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/local_execution/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ENVIRONMENT_SCRIPT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/environment_script/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x HIGH_PRIVILEGED_PORT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/high_privileged_port/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NO_CONTACT_INFO=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/no_contact_info/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x PLUGIN_PATH=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/app_path/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_PATH=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/iplanet_path/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_PATH=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/ihs_path/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x PLUGIN_LIBRARY_PATH=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/ld_library_path/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x PLUGIN_CLASS_PATH=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/classpath/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x CORE_EXCEPTION_LIST=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/core_exception_list/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x TMP_EXCEPTION_LIST=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/temp_exception_list/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x WEB_MGMT_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/web.mgmt.enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x CERT_MGMT_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/cert.mgmt.enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IS_WEB_MGMT_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/web.mgmt.enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IS_CERT_MGMT_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/web.build.enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IS_WEB_BUILD_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/cert.mgmt.enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x EINFO_WEBSITE_DEFS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/einfo_website_defs/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x EINFO_PLATFORM_DEFS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/einfo_platform_defs/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x EINFO_MACHINE_DEFS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/einfo_machine_defs/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x EINFO_CONTACT_DEFS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/einfo_contact_defs/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x INTERNET_TYPE_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/internet_type_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x INTRANET_TYPE_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/intranet_type_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ENV_TYPE_IST=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/env_type_ist/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ENV_TYPE_QA=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/env_type_qa/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ENV_TYPE_STG=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/env_type_stg/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ENV_TYPE_TRN=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/env_type_trn/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ENV_TYPE_PRD=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/env_type_prd/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SOLARIS_OPERATIONAL_IFACE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/solaris_operational_iface/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## set up an ignore list for servers that arent available or shouldnt be used
typeset -r -x SERVER_IGNORE_LIST=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/server_ignore_list/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## resource files
typeset -r -x PLUGIN_ERROR_MESSAGES=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/error_resources/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x PLUGIN_MESSAGES=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/message_resources/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## external utils
typeset -r -x DNS_SERVICE_URL=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/dns_service_url/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## security and access control
typeset -r -x PLUGIN_SECURITY_CONFIG=${PLUGIN_CONF_ROOT}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/security_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ENFORCE_SECURITY=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/enforce_system_security/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ALLOWED_SERVERS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/allowed_servers/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SECURITY_OVERRIDE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/security_override/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x RANDOM_GENERATOR=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/random_generator/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ENTROPY_GENERATOR=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/entropy_generator/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ENTROPY_FILE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/entropy_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ENTROPY_FILE_SIZE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/entropy_file_size/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_CERT_DB_PASSFILE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/iplanet_db_pass_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_CERT_DB_PASSFILE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/ihs_db_pass_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SECURITY_TOKENS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/security_tokens/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SME_NAME=$(sed '/^ *#/d;s/#.*//' /etc/passwd | awk -F  ":" '/Kevin/{print $1}');
set -A AUTHORIZED_USERS $(sed '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/authorized_users/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g'); typeset -r -x AUTHORIZED_USERS;
set -A AUTHORIZED_GROUPS $(sed '/^ *#/d;s/#.*//' ${PLUGIN_SECURITY_CONFIG} | awk -F  "=" '/authorized_groups/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g'); typeset -r -x AUTHORIZED_GROUPS;

## backup configuration
typeset -r -x PLUGIN_BACKUP_CONFIG=${PLUGIN_CONF_ROOT}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/backup_properties/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IS_BACKUP_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/perform_full_backup/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x BACKUP_DIRECTORY=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/backup_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x BACKUP_RETENTION_TIME=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/backup_lifetime/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x BACKUP_FILE_NAME=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/backup_file_name/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x WEB_BACKUP_PREFIX=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/web_backup_prefix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x CERT_BACKUP_PREFIX=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/cert_backup_prefix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x CERT_BACKUP_DIR=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/cert_backup_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## mail data
typeset -r -x PLUGIN_MAIL_CONFIG=${PLUGIN_CONF_ROOT}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/email_configuration/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x MAILSTORE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/mailstore_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x MAIL_TEMPLATE_DIR=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/mail_template_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SEND_NOTIFIES=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/send_notifies/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_FROM_ADDRESS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_from_address/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_TYPE_NOTIFY=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_type_notify/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_TYPE_ALERT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_type_alert/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_CSR_ADDRESS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_csr_address/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_CSR_EMAIL=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_csr_email/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_VCSR_SUBJECT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_vcsr_subject/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_HCSR_SUBJECT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_hcsr_subject/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_OWNER_EMAIL=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_owner_email/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_OWNER_CERT_EXPIRY_EMAIL=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_owner_cert_expiry/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_OWNER_SUBJECT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_owner_subject/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_PEM_ADDRESS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_pem_address/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_PEM_EMAIL=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_pem_email/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_PEM_SUBJECT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_pem_subject/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ALERT_CERTIFICATE_ADDRESS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/alert_certificate_address/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ALERT_CERTIFICATE_SUBJECT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/alert_certificate_subject/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ALERT_CERTIFICATE_EMAIL=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/alert_certificate_email/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SITE_MONITOR_ADDRESS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/alert_certificate_address/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SITE_MONITOR_SUBJECT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/alert_certificate_subject/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SITE_MONITOR_EMAIL=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/alert_certificate_email/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## web config
typeset -r -x PLUGIN_WEB_CONFIG=${APP_ROOT}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/web_config_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NA_PRI_DATACENTER_ID=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/na_pri_datacenter_id/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NA_SEC_DATACENTER_ID=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/na_sec_datacenter_id/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x UK_PRI_DATACENTER_ID=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/uk_pri_datacenter_id/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x UK_SEC_DATACENTER_ID=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/uk_sec_datacenter_id/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x AVAILABLE_DATACENTERS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/available_datacenters/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x PROJECT_CODE_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/project_code_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SITE_HOSTNAME_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/site_hostname_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x PLATFORM_CODE_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/platform_code_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SERVERID_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/serverid_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_TYPE_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_type_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_INIT_SCRIPT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_init_script/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_OWNING_USER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_owning_user/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_PROCESS_USER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_process_user/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_OWNING_GROUP=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_owning_group/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_CONFIG_PATH=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_config_path/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_SERVER_CONFIG=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_server_config/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_CORE_CONFIG=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_core_config/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_CERT_DIR=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_cert_store/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_CERT_STORE_PREFIX=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_cert_store_prefix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_CERT_STORE_KEY_SUFFIX=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_cert_store_key_suffix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_CERT_STORE_CERT_SUFFIX=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_cert_store_cert_suffix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_START_SCRIPT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_start_script/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_STOP_SCRIPT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_stop_script/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_RESTART_SCRIPT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_restart_script/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_ROTATE_SCRIPT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_rotate_script/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_RECONFIG_SCRIPT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_reconfig_script/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_STARTUP_IGNORE_LIST=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_startup_ignore_list/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_PID_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_pid_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_URLHOSTS_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_urlhosts_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_SERVERNAME_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_servername_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_SECURITY_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_security_indicator/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_NICKNAME_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_nickname_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_WEB_CONFIG=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_web_config/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_PORT_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_port_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_SUDO_START_WEB=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_sudo_start_web/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_SUDO_STOP_WEB=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_sudo_stop_web/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_WEB_TMPDIR=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_web_tmpdir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_IST_ROOT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_ist_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_QA_ROOT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_qa_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_SERVER_ROOT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_server_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_WAS_FUNCTION=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_was_function/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_WAS_BOOTSTRAP=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_was_bootstrap/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_WAS_HANDLER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_was_handler/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_BASE_LOG_ROOT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_base_log_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_BASE_DOC_ROOT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_base_doc_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_TMPDIR_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_tmpdir_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_ACL_DIR=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_acl_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_ACL_NAMES=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_acl_names/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_PASSWORD_FILE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_password_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_SSL_TEMPLATE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_ssl_template/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_NOSSL_TEMPLATE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_nonssl_template/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_BOTH_TEMPLATE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_both_template/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_ACL_TEMPLATE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_acl_template/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_CERTDB_TEMPLATE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_certdb_template/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IPLANET_KEYDB_TEMPLATE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/iplanet_keydb_template/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## ihs configuration
typeset -r -x IHS_REMOTE_APP_ROOT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_TYPE_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_type_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_CERT_DIR=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_db_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_DB_STASH_SUFFIX=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_db_stash_suffix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_DB_REQ_SUFFIX=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_db_req_suffix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_DB_CRT_SUFFIX=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_db_crt_suffix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_KEY_DB_TYPE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_key_db_type/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_START_SCRIPT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_start_script/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_STOP_SCRIPT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_stop_script/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_CONFIG_PATH=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_config_path/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_SERVER_CONFIG=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_server_config/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_OWNING_USER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_owning_user/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_PROCESS_USER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_process_user/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_OWNING_GROUP=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_owning_group/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_INIT_SCRIPT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_init_script/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_WEB_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_web_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_PID_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_pid_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_WAS_MODULE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_was_module/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_WAS_PLUGIN=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_was_plugin/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_SSL_TEMPLATE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_ssl_template/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_NOSSL_TEMPLATE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_nonssl_template/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_BOTH_TEMPLATE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_both_template/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_KEYDB_TEMPLATE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_keydb_template/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_CERTDB_TEMPLATE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_certdb_template/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IHS_REQDB_TEMPLATE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ihs_reqdb_template/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x RESTART_SERVICE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/mgmt.restart.service/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ADD_EXCEPTION=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/mgmt.add.exception/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## cert configuration
typeset -r -x WEB_SSL_CONFIG=${PLUGIN_CONF_BASE}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ssl_config_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x CERTDB_STORE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/app_certdb_store/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x CSRSTORE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/csr_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x PEMSTORE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/pem_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x PKCS12STORE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/pkcs12_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x CERTSTORE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/certs_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SSL_EXCEPTION_LIST=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ssl_exception_list/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x PEM_SITES_LIST=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/pem_site_list/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SELF_SIGN_SUBJECT="$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/self_sign_subject/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NA_CSR_SUBJECT="$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/na_csr_subject/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x AU_CSR_SUBJECT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/au_csr_subject/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x CA_CSR_SUBJECT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/ca_csr_subject/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x UK_CSR_SUBJECT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/uk_csr_subject/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x CERT_BIT_LENGTH=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/csr_bitsize/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x CSR_DIRECTORY=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/csr_directory/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x METASLOT_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/metaslot_enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x METASLOT_NAME=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/metaslot_name/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x INTERNET_CERT_SIGNATORY=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/internet_cert_signatory/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x INTRANET_CERT_SIGNATORY=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/intranet_cert_signatory/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x VALIDATION_PERIOD=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/validation_period/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x KEYSTORE_CLEANUP_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/keystore_cleanup_enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x KEYSTORE_BACKUP_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/keystore_backup_enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x KEYSTORE_DIR_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/keystore_dir_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SITE_OVERRIDES=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/cert_signatory_overrides/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x CERT_STORE_ARCHIVE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/cert_store_archive/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ROOT_CERT_STORE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/root_cert_store/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x OPENSSL_CONFIG_FILE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/openssl_config_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x GENERATE_SELF_SIGNED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/generate_self_signed/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## ssl port info
typeset -r -x STD_SSL_PORT_NUMBER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/std_ssl_port/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NONSTD_SSL_PORT_NUMBER=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_WEB_CONFIG} | awk -F  "=" '/nonstd_ssl_port/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## build config
typeset -r -x WEB_BUILD_CONFIG=${PLUGIN_ROOT_DIR}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/build_config_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x BUILD_TMP_DIR=$(sed '/^ *#/d;s/#.*//' ${WEB_BUILD_CONFIG} | awk -F  "=" '/build_tmp_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x BUILD_TYPE_SSL=$(sed '/^ *#/d;s/#.*//' ${WEB_BUILD_CONFIG} | awk -F  "=" '/build_type_ssl/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x BUILD_TYPE_NOSSL=$(sed '/^ *#/d;s/#.*//' ${WEB_BUILD_CONFIG} | awk -F  "=" '/build_type_nossl/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x BUILD_TYPE_BOTH=$(sed '/^ *#/d;s/#.*//' ${WEB_BUILD_CONFIG} | awk -F  "=" '/build_type_both/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## monitoring
typeset -r -x PLUGIN_MONITOR_CONFIG=${PLUGIN_CONF_ROOT}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/monitor_config_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x MONITOR_WORK_DIR=${PLUGIN_ROOT_DIR}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MONITOR_CONFIG} | awk -F  "=" '/monitor_work_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x LOG_FILE_DELAY=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MONITOR_CONFIG} | awk -F  "=" '/log_file_delay/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x OPTIONS_MONITOR_STRING=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MONITOR_CONFIG} | awk -F  "=" '/monitor_options_string/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SVCTRACE_MONITOR_STRING=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MONITOR_CONFIG} | awk -F  "=" '/monitor_svctrace_string/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SVCTRACE_MONITOR_STRING=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MONITOR_CONFIG} | awk -F  "=" '/monitor_svctrace_string/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x MONITOR_OUTPUT_FILE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MONITOR_CONFIG} | awk -F  "=" '/monitor_output_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x MONITOR_OUTPUT_EXPIRES=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MONITOR_CONFIG} | awk -F  "=" '/monitor_output_expires/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## solaris interface info


## month directors
typeset -r -x Jan=01;
typeset -r -x Feb=02;
typeset -r -x Apr=03;
typeset -r -x Mar=04;
typeset -r -x May=05;
typeset -r -x Jun=06;
typeset -r -x Jul=07;
typeset -r -x Aug=08;
typeset -r -x Sep=09;
typeset -r -x Oct=10;
typeset -r -x Nov=11;
typeset -r -x Dec=12;

## counters
typeset -i FILE_COUNT=0;
typeset -i ERROR_COUNT=0;
typeset -i AUTHORIZATION_COUNT=0;

## export what needs to be exported
## set path, incorporating approot
typeset -x PATH=${PATH}:${PLUGIN_PATH}:${IPLANET_PATH}:${IHS_PATH};
typeset -x LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${PLUGIN_LIBRARY_PATH};
typeset -x CLASSPATH==${CLASSPATH}:${PLUGIN_CLASS_PATH};

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
