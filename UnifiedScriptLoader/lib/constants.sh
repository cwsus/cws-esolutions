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
#
#==============================================================================

[[ ! -z "${TRACE}" && "${TRACE}" = "TRUE" ]] && set -x;

case ${SCRIPT_ROOT} in
    *lib/plugins/*/executors*|*lib/plugins/*/monitors*) LOAD_CONFIG_DIR="${SCRIPT_ROOT}/../../../../etc" ;;
    *lib/plugins/*/sys*|*lib/plugins/*/bin*|*lib/plugins/*/lib*) LOAD_CONFIG_DIR="${SCRIPT_ROOT}/../../../../etc" ;;
    *lib/plugins/*) LOAD_CONFIG_DIR="${SCRIPT_ROOT}/../../../etc" ;;
    *lib/sys*) LOAD_CONFIG_DIR="${SCRIPT_ROOT}/../../etc" ;;
    *lib*|*bin*) LOAD_CONFIG_DIR="${SCRIPT_ROOT}/../etc" ;;
    *) LOAD_CONFIG_DIR="$(pwd)/../etc" ;;
esac

typeset -r LOAD_CONFIG_DIR;

case ${EXPORT_ENVIRONMENT} in
    [Ss][Tt][Gg]|[Ss][Tt][Aa][Gg][Ee]) APP_SYS_CONFIG=${LOAD_CONFIG_DIR}/stg/application.properties ;;
    [Uu][Aa][Tt]|[Qq][Aa]) APP_SYS_CONFIG=${LOAD_CONFIG_DIR}/qa/application.properties ;;
    [Ii][Ss][Tt]|[Dd][Ee][Vv]|[Dd][Ee][Vv][Ee][Ll][Oo][Pp][Mm][Ee][Nn][Tt]) APP_SYS_CONFIG=${LOAD_CONFIG_DIR}/dev/application.properties ;;
    *) APP_SYS_CONFIG=${LOAD_CONFIG_DIR}/application.properties ;;
esac

typeset -r APP_SYS_CONFIG;

if [ ! -s ${APP_SYS_CONFIG} ]
then
    echo "Failed to locate configuration data. Cannot continue.";

    echo 1; exit 1;
fi

## source functions
## application information
typeset -r -x APP_ROOT=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/app_root/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x MAIN_CLASS=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/main_class/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x BIN_DIRECTORY=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/bin_directory/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x LIB_DIRECTORY=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/lib_directory/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x ETC_DIRECTORY=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/etc_directory/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x PLUGIN_DIR=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/plugin_dir/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x LIST_DISPLAY_MAX=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/max_list_display/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x FAILURE_THRESHOLD=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/datacenter_failure_threshold/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x THREAD_TIMEOUT=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/thread_timeout/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x SSH_THREAD_TIMEOUT=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/ssh_thread_timeout/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x MONITOR_THREAD_TIMEOUT=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/monitor_thread_timeout/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x RESTART_DELAY=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/service_restart_delay/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x MESSAGE_DELAY=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/message_delay/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x THREAD_DELAY=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/thread_delay/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x EXECUTION_DELAY=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/execution_delay/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x THREAD_INTERVAL=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/thread_interval/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x ORACLE_HOME=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/oracle_home/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x TNS_ADMIN=$(eval echo $(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/tnsadmin/{print $2}' | sed -e 's/^ *//g;s/ *$//g'));
typeset -r -x JAVA_HOME=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/java_home/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x RANDOM_GENERATOR=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/random_generator/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x MAILER_CLASS=$(eval echo $(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/mailer_class/{print $2}' | sed -e 's/^ *//g;s/ *$//g'));
typeset -r -x LOCKFILE=$(eval echo $(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/lock_file/{print $2}' | sed -e 's/^ *//g;s/ *$//g'));
## path
typeset -r -x APP_PATH=$(eval echo $(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/app_path/{print $2}' | sed -e 's/^ *//g;s/ *$//g'));
typeset -r -x SYS_PATH=$(eval echo $(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/sys_path/{print $2}' | sed -e 's/^ *//g;s/ *$//g'));
typeset -r -x APP_LIB_PATH=$(eval echo $(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/ld_library_path/{print $2}' | sed -e 's/^ *//g;s/ *$//g'));

## application property files
typeset -r -x ERROR_MESSAGES=${APP_ROOT}/$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/error_resources/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x SYSTEM_MESSAGES=${APP_ROOT}/$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/message_resources/{print $2}' | sed -e 's/^ *//g;s/ *$//g');

## logging
if [ ! -z "$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/logging_properties/{print $2}' | sed -e 's/^ *//g;s/ *$//g')" ]
then
    typeset -r -x APP_LOGGING_CONFIG=${APP_ROOT}/$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/logging_properties/{print $2}' | sed -e 's/^ *//g;s/ *$//g');

    if [ -f ${APP_LOGGING_CONFIG} ]
    then
        if [ -z "$(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/LOGGER/{print $2}' | sed -e 's/^ *//g;s/ *$//g')" ]
        then
            typeset -r -x LOGGER=/bin/false;
        else
            typeset -r -x LOGGER=$(eval echo $(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/LOGGER/{print $2}' | sed -e 's/^ *//g;s/ *$//g'));
            typeset -r -x VERBOSE=$(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/ENABLE_DEBUG/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
            typeset -r -x TRACE=$(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/ENABLE_TRACE/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
            typeset -r -x LOG_ROOT=$(eval echo $(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/LOG_DIR/{print $2}' | sed -e 's/^ *//g;s/ *$//g'));
            typeset -r -x ARCHIVE_ENABLED=$(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/ARCHIVE_ENABLED/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
            typeset -r -x ARCHIVE_LOG_ROOT=$(eval echo $(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/ARCHIVE_DIR/{print $2}' | sed -e 's/^ *//g;s/ *$//g'));
            typeset -r -x RECORDER_CONV=$(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/CONVERSION_PATTERN/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
            typeset -r -x DATE_PATTERN=$(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/DATE_PATTERN/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
            typeset -r -x ROTATE_ON_SIZE=$(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/FILE_SIZE_LIMIT/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
            typeset -r -x RETENTION_TIME=$(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/LOG_RETENTION_PERIOD/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
            typeset -r -x ROLLOVER_PERIOD=$(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/ROLLOVER_PERIOD/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
            typeset -r -x ERROR_RECORDER_FILE=$(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/ERROR_RECORDER.File/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
            typeset -r -x ERROR_RECORDER=$(echo ${ERROR_RECORDER_FILE} | sed -e "s^log^$(date +"${DATE_PATTERN}").log^");
            typeset -r -x DEBUG_RECORDER_FILE=$(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/DEBUG_RECORDER.File/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
            typeset -r -x DEBUG_RECORDER=$(echo ${DEBUG_RECORDER_FILE} | sed -e "s^log^$(date +"${DATE_PATTERN}").log^");
            typeset -r -x TRACE_RECORDER_FILE=$(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/TRACE_RECORDER.File/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
            typeset -r -x TRACE_RECORDER=$(echo ${TRACE_RECORDER_FILE} | sed -e "s^log^$(date +"${DATE_PATTERN}").log^");
            typeset -r -x AUDIT_RECORDER_FILE=$(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/AUDIT_RECORDER.File/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
            typeset -r -x AUDIT_RECORDER=$(echo ${AUDIT_RECORDER_FILE} | sed -e "s^log^$(date +"${DATE_PATTERN}").log^");
            typeset -r -x WARN_RECORDER_FILE=$(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/WARN_RECORDER.File/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
            typeset -r -x WARN_RECORDER=$(echo ${WARN_RECORDER_FILE} | sed -e "s^log^$(date +"${DATE_PATTERN}").log^");
            typeset -r -x INFO_RECORDER_FILE=$(sed -e '/^ *#/d;s/#.*//' ${APP_LOGGING_CONFIG} | awk -F  "=" '/INFO_RECORDER.File/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
            typeset -r -x INFO_RECORDER=$(echo ${INFO_RECORDER_FILE} | sed -e "s^log^$(date +"${DATE_PATTERN}").log^");
        fi
    else
        typeset -r -x LOGGER=/bin/false;
    fi
else
    typeset -r -x LOGGER=/bin/false;
fi

## uncommon constants
typeset -r -x _TRUE=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/TRUE/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x _FALSE=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/FALSE/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x _OK=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/OK/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x DATESYS=$(date +%Y%m%d_%H-%M-%S);
typeset -r -x CURRENT_DATE=$(date '+%Y%m%d');
typeset -r -x CURRENT_TIMESTAMP=$(date '+%Y%m%d%H%M');
typeset -r -x SYSTEM_HOSTNAME="$(uname -n)";
typeset -r -x SYSTEM_UPTIME="$(uptime | sed -e 's/^ *//g;s/ *$//g')";
set -A IUSER_GROUPS $(groups); typeset -r -x IUSER_GROUPS;
set -A PROXY_SERVERS $(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/proxy_servers/{print $2}' | sed -e 's/^ *//g;s/ *$//g'); typeset -r -x PROXY_SERVERS;
[ -z "$(/usr/bin/env who am i | awk '{print $1}')" ] && typeset -r -x IUSER_AUDIT=$(/usr/bin/env whoami) || typeset -r -x IUSER_AUDIT=$(/usr/bin/env who am i | awk '{print $1}');

## counters
typeset -x -i A=0;
typeset -x -i B=0;
typeset -x -i C=0;
typeset -x -i D=0;
typeset -x -i ERROR_COUNT=0;
typeset -x -i RETRY_COUNT=0;
typeset -x -i STATUS=0;

## prompts
PS3_PROMPT=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/ps3_prompt/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
PS4_PROMPT=$(sed -e '/^ *#/d;s/#.*//' ${APP_SYS_CONFIG} | awk -F  "=" '/ps4_prompt/{print $2}' | sed -e 's/^ *//g;s/ *$//g');
typeset -r -x PS3="${PS3_PROMPT}";
typeset -r -x PS4="${PS4_PROMPT}";

## set path, incorporating approot
## set path / ld_library_path
typeset -x PATH=${PATH}:${APP_PATH}:${SYS_PATH}:${PLUGIN_PATH};
typeset -x LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${APP_LIB_PATH}:${PLUGIN_LIB_PATH};

## source aliases/functions ..
[ -f ${APP_ROOT}/${LIB_DIRECTORY}/aliases ] && . ${APP_ROOT}/${LIB_DIRECTORY}/aliases;
[ -f ${APP_ROOT}/${LIB_DIRECTORY}/functions ] && . ${APP_ROOT}/${LIB_DIRECTORY}/functions;

[[ ! -z "${TRACE}" && "${TRACE}" = "TRUE" ]] && set +x;
