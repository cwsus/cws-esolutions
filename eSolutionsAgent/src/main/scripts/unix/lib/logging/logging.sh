#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  write_log.sh
#         USAGE:  ./write_log.sh LEVEL METHOD_NAME CLASS_NAME LINE_NUM "message"
#   DESCRIPTION:  Prints the specified message to the defined logfile
#
#       OPTIONS:  ---
#  REQUIREMENTS:  ---
#          BUGS:  ---
#         NOTES:  ---
#        AUTHOR:  $Author$
#       COMPANY:  CaspersBox Web Services
#       VERSION:  $Revision$
#       CREATED:  $Date$
#      REVISION:  ---
#==============================================================================

## Constants
CNAME=$(basename ${0});

#TIMESTAMP=$(date +"%d %b %Y %k:%M:%S",)$(($(date +%N) / 1000000 ));
TIMESTAMP=$(date +"%d %b %Y %H:%M:%S");

RECORDER_CONV=$(echo ${RECORDER_CONV} | sed "s^%d^${TIMESTAMP}^");
RECORDER_CONV=$(echo ${RECORDER_CONV} | sed "s^%t^${2}^");
RECORDER_CONV=$(echo ${RECORDER_CONV} | sed "s^%-5p^${1}^");
RECORDER_CONV=$(echo ${RECORDER_CONV} | sed "s^%F^${3}^");
RECORDER_CONV=$(echo ${RECORDER_CONV} | sed "s^%L^${4}^");
RECORDER_CONV=$(echo ${RECORDER_CONV} | sed "s^%m^${5}^");

[ ! -z ${1} ] && [ "${1}" == "ERROR" ] && print "${RECORDER_CONV}" >> ${APP_ROOT}/${LOG_ROOT}/${BASE_LOG_NAME}-${ERROR_RECORDER};
[ ! -z ${1} ] && [ "${1}" == "DEBUG" ] && print "${RECORDER_CONV}" >> ${APP_ROOT}/${LOG_ROOT}/${BASE_LOG_NAME}-${DEBUG_RECORDER};
[ ! -z ${1} ] && [ "${1}" == "AUDIT" ] && print "${RECORDER_CONV}" >> ${APP_ROOT}/${LOG_ROOT}/${BASE_LOG_NAME}-${AUDIT_RECORDER};
[ ! -z ${1} ] && [ "${1}" == "WARN" ] && print "${RECORDER_CONV}" >> ${APP_ROOT}/${LOG_ROOT}/${BASE_LOG_NAME}-${WARN_RECORDER};
[ ! -z ${1} ] && [ "${1}" == "INFO" ] && print "${RECORDER_CONV}" >> ${APP_ROOT}/${LOG_ROOT}/${BASE_LOG_NAME}-${INFO_RECORDER};
[ ! -z ${1} ] && [ "${1}" == "MONITOR" ] && print "${RECORDER_CONV}" >> ${APP_ROOT}/${LOG_ROOT}/${BASE_LOG_NAME}-${MONITOR_RECORDER};
