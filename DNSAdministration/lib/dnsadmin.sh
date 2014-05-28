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

typeset -r -x PLUGIN_NAME="dnsadmin";

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

typeset -r -x REMOTE_APP_ROOT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/remote_app_root/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x TMP_DIRECTORY=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/temp_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x DATA_DIRECTORY=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/data_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x WORK_DIRECTORY=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/work_directory/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x BACKUP_LIST=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/backup_file_list/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x LOCAL_EXECUTION=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/local_execution/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x THREAD_TIMEOUT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/thread_timeout/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x OVERRIDE_TARGET_VALIDATION=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/override_target_validation/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ENVIRONMENT_SCRIPT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/environment_script/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x MODIFIED_IFS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/modified_ifs/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## enable/disable functions
typeset -r -x IS_INTRANET_FAILOVER_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/intranet.failover.enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IS_INTERNET_FAILOVER_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/internet.failover.enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IS_DNS_RECORD_ADD_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/dns.record.add.enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IS_DNS_RECORD_MOD_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/dns.record.modify.enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IS_DNS_SVC_MGMT_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/dns.svc.mgmt.enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IS_RNDC_MGMT_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/dns.rndc.mgmt.enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IS_DNSSEC_MGMT_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/dns.dnssec.mgmt.enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## path
typeset -r -x -x PLUGIN_PATH=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/app_path/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_PATH=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/named_path/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x -x LIBRARY_PATH=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/ld_library_path/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x GD_PATH=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/gd_path/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## application property files
typeset -r -x PLUGIN_ERROR_MESSAGES=${PLUGIN_CONF_ROOT}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/error_resources/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x PLUGIN_SYSTEM_MESSAGES=${PLUGIN_CONF_ROOT}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/message_resources/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ALLOWED_RECORD_LIST=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/allowed_record_list/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ALLOWED_GTLD_LIST=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/allowed_gtld_list/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ALLOWED_CCTLD_LIST=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/allowed_cctld_list/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ALLOWED_SERVICE_TYPES=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/allowed_service_names/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## resource files
typeset -r -x DIG_DATA_FILE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/dig_data_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x APP_FLAG=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/app_access_flag/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## email
typeset -r -x PLUGIN_MAIL_CONFIG=${PLUGIN_CONF_ROOT}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/mail_config_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x MAIL_TEMPLATE_DIR=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/mail_template_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x MAILSTORE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/mailstore_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x MAILER_CLASS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/mailer_class/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SEND_NOTIFIES=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/send_notifies/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_FROM_ADDRESS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_from_address/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_SUBJECT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_subject/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x DHCP_SERVER_ADMIN_EMAIL=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/dhcp_server_admin_email/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x DNS_SERVER_ADMIN_EMAIL=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/dns_server_admin_email/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_FAILOVER_EMAIL=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_site_failover/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_ZONE_CHANGE_EMAIL=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_add_change/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_ROLE_SWAP_EMAIL=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_role_swap/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_DHCPKEY_CHANGE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_dhcp_key_change/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ALERT_SUBJECT=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/alert_subject/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NOTIFY_ALERT_EMAIL=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/notify_alert/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ALERT_NOTIFICATION_EMAIL=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_MAIL_CONFIG} | awk -F  "=" '/alert_notification_email/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## security and access control
typeset -r -x PLUGIN_SEC_CONFIG=${PLUGIN_CONF_ROOT}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/security_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ENFORCE_SECURITY=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SEC_CONFIG} | awk -F  "=" '/enforce_system_security/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ALLOWED_SERVERS=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SEC_CONFIG} | awk -F  "=" '/allowed_servers/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SECURITY_OVERRIDE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SEC_CONFIG} | awk -F  "=" '/security_override/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x RANDOM_GENERATOR=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SEC_CONFIG} | awk -F  "=" '/random_generator/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x GD_PASS_FILE=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SEC_CONFIG} | awk -F  "=" '/gd_pass_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IS_SUDO_REQUIRED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_SEC_CONFIG} | awk -F  "=" '/is_sudo_required/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IUSER_AUDIT=$(who am i | awk '{print $1}');
set -A IUSER_GROUPS $(groups); typeset -r -x IUSER_GROUPS;
set -A AUTHORIZED_USERS $(sed '/^ *#/d;s/#.*//' ${PLUGIN_SEC_CONFIG} | awk -F  "=" '/authorized_users/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g'); typeset -r -x AUTHORIZED_USERS;
set -A AUTHORIZED_GROUPS $(sed '/^ *#/d;s/#.*//' ${PLUGIN_SEC_CONFIG} | awk -F  "=" '/authorized_groups/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g'); typeset -r -x AUTHORIZED_GROUPS;
set -A PROXY_SERVERS $(sed '/^ *#/d;s/#.*//' ${PLUGIN_SEC_CONFIG} | awk -F  "=" '/proxy_servers/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g'); typeset -r -x PROXY_SERVERS;

## backup configuration
typeset -r -x PLUGIN_BACKUP_CONFIG=${PLUGIN_CONF_ROOT}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/backup_properties/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IS_BACKUP_ENABLED=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/perform_full_backup/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x BACKUP_DIRECTORY=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/backup_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x BACKUP_RETENTION_TIME=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/backup_lifetime/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x BACKUP_FILE_NAME=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/backup_file_name/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ZONE_BACKUP_PREFIX=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/zone_backup_prefix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x CONF_BACKUP_PREFIX=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_BACKUP_CONFIG} | awk -F  "=" '/conf_backup_prefix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## intranet dns configuration
typeset -r -x INTRANET_DNS_CONFIG=${PLUGIN_CONF_ROOT}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/intranet_dns_config/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x INTRANET_TYPE_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${INTRANET_DNS_CONFIG} | awk -F  "=" '/intranet_type_identifier ${INTRANET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x GD_CONFIG_FILE=$(sed '/^ *#/d;s/#.*//' ${INTRANET_DNS_CONFIG} | awk -F  "=" '/gd_config_file ${INTRANET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x GD_SERVERS=$(sed '/^ *#/d;s/#.*//' ${INTRANET_DNS_CONFIG} | awk -F  "=" '/gd_servers ${INTRANET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x GD_IGNORE_LIST=$(sed '/^ *#/d;s/#.*//' ${INTRANET_DNS_CONFIG} | awk -F  "=" '/ignore_list ${INTRANET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x GD_POP_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${INTRANET_DNS_CONFIG} | awk -F  "=" '/gd_pop_identifier ${INTRANET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x GD_VHOST_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${INTRANET_DNS_CONFIG} | awk -F  "=" '/gd_vhost_identifier ${INTRANET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## internet dns config
typeset -r -x INTERNET_DNS_CONFIG=${PLUGIN_CONF_ROOT}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/internet_dns_config/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x INTERNET_TYPE_IDENTIFIER=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/internet_type_identifier ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_MASTER=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/master_dns ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_MASTER_ADDRESS=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/master_dns_address ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x AVAILABLE_MASTER_SERVERS=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/available_masters ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
set -A DNS_SLAVES $(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/slave_dns/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g'); typeset -r -x DNS_SLAVES;
set -A DNS_SLAVE_IPS $(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/slave_dns_addresses/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g'); typeset -r -x DNS_SLAVE_IPS;
set -A EXT_SLAVES $(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/ext_slave_dns/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g'); typeset -r -x EXT_SLAVES;
set -A DNS_SERVERS ${NAMED_MASTER} ${DNS_SLAVES[@]}; typeset -r -x DNS_SERVERS;
typeset -r -x NAMED_ZONE_PREFIX=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_zone_prefix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_ROOT=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_root ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_KEY_DIR=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_key_dir/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_ZONE_DIR=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_zone_dir ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_MASTER_ROOT=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_master_dir ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_SLAVE_ROOT=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_slave_dir ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_DYNAMIC_ROOT=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_dynamic_dir ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_LOG_FILE=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_log_file ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_SERVICE_START_TXT=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_service_restart_txt ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_SERIAL_START=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/initial_serial_number ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_ZONE_PREFIX=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_zone_prefix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_TTL_TIME=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/nameserver_ttl_time/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_REFRESH_INTERVAL=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/nameserver_refresh_interval/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_RETRY_INTERVAL=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/nameserver_retry_interval/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_EXPIRATION_INTERVAL=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/nameserver_expiration_interval/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_CACHE_INTERVAL=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/nameserver_cache_interval/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
set -A NAMED_INTERNET_ADDR $(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/nameserver_internet_addr/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g'); typeset -r -x NAMED_INTERNET_ADDR;
typeset -r -x NAMED_INTERNET_SUFFIX=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/nameserver_internet_suffix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_PRIMARY_SOA=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/nameserver_primary_soa/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_PRIMARY_SOA_CONTACT=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/nameserver_primary_soa_contact/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_ZONE_PREFIX=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_zone_prefix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_CONF_DIR=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_conf_dir ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_ZONE_CONF_NAME=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_zone_conf_name ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_CONF_FILE=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_conf_file ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_CONF_DIR=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_conf_dir ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_LOG_DIR=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_log_dir ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_BACKUP_DIRECTORY=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_backup_dir ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_USER=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_owning_user ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_GROUP=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_owning_group ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_SERVER_LIST="${NAMED_MASTER}, $(echo ${DNS_SLAVES[@]} | sed -e 's/ /, /g')";
typeset -r -x NAMED_ROOT_CACHE=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/root_server_cache ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_MASTER_ACL=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_master_acl ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_QUERY_ACL=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_query_acl ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_TRANSFER_ACL=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_transfer_acl ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x PRIMARY_DATACENTER_IP=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/primary_datacenter_ip ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SECONDARY_DATACENTER_IP=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/secondary_datacenter_ip ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_DECOM_DIR=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/dns_decom_dir ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x DECOM_CONF_FILE=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/dns_decom_conf ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x REAL_ALIAS_NAMES=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/real_alias_names ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x LOCAL_FORWARD_ZONE=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/local_forward_zone ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x LOCAL_REVERSE_ZONE=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/local_reverse_zone ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_PID_FILE=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_pid_file ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_SERVICE_STOP_TXT=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_service_stop_txt ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_SERVICE_START_TXT=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_service_start_txt ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_SERVICE_RELOAD_TXT=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_service_reload_txt ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_INIT_SCRIPT=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_init_script ${INTERNET_DNS_CONFIG}/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x IGNORE_LIST=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/ignore_list/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SPLIT_HORIZON=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/split_horizon/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x HORIZONS=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/reload_horizons/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ZONE_DATA_RETRIEVALS=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/zone_data_retrievals/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_PROCESS_NAME=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/named_process_name/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## enable apex location record
typeset -r -x ENABLE_LOC_RECORD=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/enable_loc_record/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## zone layout information
typeset -r -x PRIMARY_DC=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/primary_datacenter/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SECONDARY_DC=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/secondary_datacenter/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x GROUP_ID=$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/datacenter_group_identifier/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## rndc configuration
typeset -r -x RNDC_LOCAL_KEY=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/local_key_name/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x RNDC_REMOTE_KEY=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/remote_key_name/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x TSIG_TRANSFER_KEY=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/transfer_key_name/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x DHCPD_UPDATE_KEY=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/dhcpd_key_name/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x RNDC_LOCAL_PORT=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/local_listen_port/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x RNDC_REMOTE_PORT=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/remote_listen_port/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x RNDC_KEY_BITSIZE=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/rndc_key_bitsize/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x RNDC_KEY_FILE=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/rndc_key_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x RNDC_CONF_FILE=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/rndc_conf_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x TRANSFER_KEY_FILE=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/transfer_key_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x DHCPD_KEY_FILE=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/dhcpd_key_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## dnssec configuration
typeset -r -x DNSSEC_ENABLED=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/dnssec_enabled/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x DNSSEC_ALGORITHM=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/dnssec_algorithm/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x DNSSEC_ZONESIGN_BITSIZE=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/dnssec_zsk_bitsize/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x DNSSEC_KEYSIGN_BITSIZE=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/dnssec_ksk_bitsize/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x KEYSIGN_FILE_PREFIX=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/keysign_file_prefix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ZONESIGN_FILE_PREFIX=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/zonesign_file_prefix/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x USE_TSIG_ACL=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/use_tsig_acl/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x USE_NAMED_ACL=$(sed '/^ *#/d;s/#.*//' ${INTERNET_DNS_CONFIG} | awk -F  "=" '/use_named_acl/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## monitoring
typeset -r -x APP_MONITOR_CONFIG=${PLUGIN_CONF_ROOT}/$(sed '/^ *#/d;s/#.*//' ${PLUGIN_CONFIG} | awk -F  "=" '/monitor_config_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x MONITOR_OUTPUT_FILE=$(sed '/^ *#/d;s/#.*//' ${APP_MONITOR_CONFIG} | awk -F  "=" '/monitor_output_file/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x -x LOG_FILE_DELAY=$(sed '/^ *#/d;s/#.*//' ${APP_MONITOR_CONFIG} | awk -F  "=" '/log_file_delay/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x NAMED_PROCESS_STRING=$(sed '/^ *#/d;s/#.*//' ${APP_MONITOR_CONFIG} | awk -F  "=" '/named_process_string/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x MONITOR_DOMAIN_NAME=$(sed '/^ *#/d;s/#.*//' ${APP_MONITOR_CONFIG} | awk -F  "=" '/monitored_domain_name/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x MONITOR_LOG_FILES=$(sed '/^ *#/d;s/#.*//' ${APP_MONITOR_CONFIG} | awk -F  "=" '/monitor_log_files/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x MONITOR_LOG_STRINGS=$(sed '/^ *#/d;s/#.*//' ${APP_MONITOR_CONFIG} | awk -F  "=" '/monitor_log_strings/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x ZONE_IDENT_STRING=$(sed '/^ *#/d;s/#.*//' ${APP_MONITOR_CONFIG} | awk -F  "=" '/zone_identification_string/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');
typeset -r -x SOA_TYPE_STRING=$(sed '/^ *#/d;s/#.*//' ${APP_MONITOR_CONFIG} | awk -F  "=" '/soa_type_string/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g');

## counters
typeset -i AUTHORIZATION_COUNT; AUTHORIZATION_COUNT=0;

## common aliases
[ -s ${PLUGIN_ROOT_DIR}/lib/aliases ] && . ${PLUGIN_ROOT_DIR}/lib/aliases;
[ -s ${PLUGIN_ROOT_DIR}/lib/functions ] && . ${PLUGIN_ROOT_DIR}/lib/functions;

