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

[[ ! -z "${TRACE}" && "${TRACE}" = "TRUE" ]] && set -x;

[ ! -d ${LOG_ROOT} ] && mkdir -p ${LOG_ROOT};
[ ! -d ${ARCHIVE_LOG_ROOT} ] && mkdir -p ${ARCHIVE_LOG_ROOT};

#===  FUNCTION  ===============================================================
#          NAME:  cleanLogArchive
#   DESCRIPTION:  Cleans up the archived log directory
#    PARAMETERS:  Archive Directory, Logfile Name, Retention Time
#       RETURNS:  0 regardless of result.
#==============================================================================
function cleanLogArchive
{
    [ ! -d ${ARCHIVE_LOG_ROOT} ] && return 0;

    for ARCHIVED_FILE in $(find ${ARCHIVE_LOG_ROOT} -type f -name \*.log\* -ctime +${RETENTION_TIME})
    do
        [ -f ${ARCHIVED_FILE} ] && rm -f ${ARCHIVED_FILE} >/dev/null 2>&1;
    done

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  rotateLog
#   DESCRIPTION:  Rotates log files based on size or time.
#    PARAMETERS:  The log file name to take action against
#       RETURNS:  0 regardless of result.
#==============================================================================
function rotateLogs
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "TRUE" ]] && set -x;

    [ ! -f ${LOG_ROOT}/${1} ] && return 0;

    if [ $(( $(date +"%s") - $(stat -L --format %Y ${LOG_ROOT}/${1}) > $(echo "${ROLLOVER_PERIOD} * 60 * 60" | bc) )) == 1 ]
    then
        if [ -f ${LOG_ROOT}/${1}.${LOG_RETENTION_PERIOD} ]
        then
            [ ${ARCHIVE_ENABLED} ] && mv ${LOG_ROOT}/${1}.${LOG_RETENTION_PERIOD} ${ARCHIVE_LOG_ROOT} || rm -f ${LOG_ROOT}/${1}.${LOG_RETENTION_PERIOD};
        fi

        ## rotate logs
        A=${LOG_RETENTION_PERIOD};

        while (( ${A} != 0 ))
        do
            [ -f ${LOG_ROOT}/${1}.${A} ] && mv ${LOG_ROOT}/${1}.${A} ${LOG_ROOT}/${1}.$(expr ${A} + 1);

            (( A -= 1 ))
        done

        mv ${LOG_ROOT}/${1} ${LOG_ROOT}/${1}.1;
        touch ${LOG_ROOT}/${1};
    fi

    if [ $(/usr/bin/env stat -c %s "${LOG_ROOT}/${1}") -gt $(echo "${ROTATE_ON_SIZE} * 1024" | bc) ]
    then
        if [ -f ${LOG_ROOT}/${1}.${LOG_RETENTION_PERIOD} ]
        then
            [ ${ARCHIVE_ENABLED} ] && mv ${LOG_ROOT}/${1}.${LOG_RETENTION_PERIOD} ${ARCHIVE_LOG_ROOT} || rm -f ${LOG_ROOT}/${1}.${LOG_RETENTION_PERIOD};
        fi

        ## rotate logs
        A=${LOG_RETENTION_PERIOD};

        while (( ${A} != 0 ))
        do
            [ -f ${LOG_ROOT}/${1}.${A} ] && mv ${LOG_ROOT}/${1}.${A} ${LOG_ROOT}/${1}.$(expr ${A} + 1);

            (( A -= 1 ))
        done

        mv ${LOG_ROOT}/${1} ${LOG_ROOT}/${1}.1;
        touch ${LOG_ROOT}/${1};
    fi

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  writeLogEntry
#   DESCRIPTION:  Cleans up the archived log directory
#    PARAMETERS:  Archive Directory, Logfile Name, Retention Time
#       RETURNS:  0 regardless of result.
#==============================================================================
function writeLogEntry
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "TRUE" ]] && set -x;

    ## always do the timestamp first
    TIMESTAMP_OPTS=$(echo ${RECORDER_CONV} | cut -d "[" -f 2 | cut -d "]" -f 1 | cut -d ":" -f 2- | sed -e '/^ *#/d;s/#.*//')
    LOG_TIMESTAMP=$(date +"${TIMESTAMP_OPTS}");
    RECORDER=$(echo ${RECORDER_CONV} | sed -e "s^${TIMESTAMP_OPTS}^${LOG_TIMESTAMP}^");

    ## then continue forth
    RECORDER=$(echo ${RECORDER} | sed -e "s^%t^${PPID}^" -e "s^%-5p^${1}^" -e "s^%F^${3}^" -e "s^%L^${4}^" -e "s^%m^${5}^" \
        -e "s^%M^${2}^" -e "s^%p^${1}^");

    case ${1} in
        ERROR)
            RECORDER=$(echo ${RECORDER} | sed -e "s^%c^${ERROR_LOG_FILE}^");

            print "${RECORDER}" >> ${LOG_ROOT}/${ERROR_LOG_FILE};
            ;;
        DEBUG)
            RECORDER=$(echo ${RECORDER} | sed -e "s^%c^${DEBUG_LOG_FILE}^");

            print "${RECORDER}" >> ${LOG_ROOT}/${DEBUG_LOG_FILE};
            ;;
        AUDIT)
            RECORDER=$(echo ${RECORDER} | sed -e "s^%c^${AUDIT_LOG_FILE}^");

            print "${RECORDER}" >> ${LOG_ROOT}/${AUDIT_LOG_FILE};
            ;;
        WARN)
            RECORDER=$(echo ${RECORDER} | sed -e "s^%c^${WARN_LOG_FILE}^");

            print "${RECORDER}" >> ${LOG_ROOT}/${WARN_LOG_FILE};
            ;;
        *)
            RECORDER=$(echo ${RECORDER} | sed -e "s^%c^${INFO_LOG_FILE}^");

            print "${RECORDER}" >> ${LOG_ROOT}/${INFO_LOG_FILE};
            ;;
    esac

    unset TIMESTAMP_OPTS;
    unset LOG_TIMESTAMP;
    unset RECORDER;

    return 0;
}

[ ${#} -ne 0 ] && writeLogEntry "${@}" > /dev/null 2>&1;

return 0;