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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

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
    print "Failed to locate configuration data. Cannot continue.";

    printf 1; exit 1;
fi

. ${APP_SYS_CONFIG};

## uncommon constants
typeset -rx DATESYS=$(date +%Y%m%d_%H-%M-%S);
typeset -rx CURRENT_DATE=$(date '+%Y%m%d');
typeset -rx CURRENT_TIMESTAMP=$(date '+%Y%m%d%H%M');
typeset -rx SYSTEM_HOSTNAME="$(uname -n)";
typeset -rx SYSTEM_UPTIME="$(uptime | sed -e 's/^ *//g;s/ *$//g')";
set -A IUSER_GROUPS $(groups); typeset -rx IUSER_GROUPS;
[ -z "$(/usr/${BIN_DIRECTORY}/env who am i | awk '{print $1}')" ] && typeset -rx IUSER_AUDIT=$(/usr/${BIN_DIRECTORY}/env whoami) || typeset -rx IUSER_AUDIT=$(/usr/${BIN_DIRECTORY}/env who am i | awk '{print $1}');

## path
typeset -x PATH=${PATH}:${APP_PATH}:${SYS_PATH};
typeset -x LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${APP_LIB_PATH};

## counters
typeset -x -i A=0;
typeset -x -i B=0;
typeset -x -i C=0;
typeset -x -i D=0;
typeset -x -i ERROR_COUNT=0;
typeset -x -i RETRY_COUNT=0;
typeset -x -i STATUS=0;

[ ! -d ${TEMP_DIRECTORY} ] && mkdir -p ${TEMP_DIRECTORY};

## source aliases/functions ..
[ -f ${APP_ROOT}/${LIB_DIRECTORY}/aliases ] && . ${APP_ROOT}/${LIB_DIRECTORY}/aliases;
[ -f ${APP_ROOT}/${LIB_DIRECTORY}/functions ] && . ${APP_ROOT}/${LIB_DIRECTORY}/functions;
