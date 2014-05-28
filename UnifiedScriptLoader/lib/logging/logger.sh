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
#        AUTHOR:  Kevin Huntly <kmhuntly@gmail.com>
#       COMPANY:  CaspersBox Web Services
#       VERSION:  1.0
#       CREATED:  ---
#      REVISION:  ---
#
#==============================================================================

[ ! -d ${LOG_ROOT} ] && mkdir -p ${LOG_ROOT};
[ ! -d ${ARCHIVE_LOG_ROOT} ] && mkdir -p ${ARCHIVE_LOG_ROOT};

function rotateLogs
{
}


function writeLogEntry
{
    ## always do the timestamp first
    TIMESTAMP_OPTS=$(echo $RECORDER_CONV | cut -d "[" -f 2 | cut -d "]" -f 1 | cut -d ":" -f 2- | sed -e '/^ *#/d;s/#.*//')
    LOG_TIMESTAMP=$(date +"${TIMESTAMP_OPTS}");
    RECORDER=$(echo ${RECORDER_CONV} | sed -e "s^${TIMESTAMP_OPTS}^${LOG_TIMESTAMP}^");

    ## then continue forth
    RECORDER=$(echo ${RECORDER} | sed -e "s^%t^${PPID}^" -e "s^%-5p^${1}^" -e "s^%F^${3}^" -e "s^%L^${4}^" -e "s^%m^${5}^" \
        -e "s^%M^${2}^" -e "s^%p^${1}^");

    case ${1} in
        ERROR)
            RECORDER=$(echo ${RECORDER} | sed -e "s^%c^${ERROR_RECORDER_FILE}^");

            print "${RECORDER}" >> ${LOG_ROOT}/${ERROR_RECORDER};
            ;;
        DEBUG)
            RECORDER=$(echo ${RECORDER} | sed -e "s^%c^${DEBUG_RECORDER_FILE}^");

            print "${RECORDER}" >> ${LOG_ROOT}/${DEBUG_RECORDER};
            ;;
        AUDIT)
            RECORDER=$(echo ${RECORDER} | sed -e "s^%c^${AUDIT_RECORDER_FILE}^");

            print "${RECORDER}" >> ${LOG_ROOT}/${AUDIT_RECORDER};
            ;;
        WARN)
            RECORDER=$(echo ${RECORDER} | sed -e "s^%c^${WARN_RECORDER_FILE}^");

            print "${RECORDER}" >> ${LOG_ROOT}/${WARN_RECORDER};
            ;;
        INFO)
            RECORDER=$(echo ${RECORDER} | sed -e "s^%c^${INFO_RECORDER_FILE}^");

            print "${RECORDER}" >> ${LOG_ROOT}/${INFO_RECORDER};
            ;;
        *)
            RECORDER=$(echo ${RECORDER} | sed -e "s^%c^${INFO_RECORDER_FILE}^");

            print "${RECORDER}" >> ${LOG_ROOT}/${INFO_RECORDER};
            ;;
    esac

    return 0;
}

[ ${#} -ne 0 ] && writeLogEntry "${@}" > /dev/null 2>&1;

return 0;