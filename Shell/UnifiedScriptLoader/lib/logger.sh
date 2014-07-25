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

#[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
#[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

#===  FUNCTION  ===============================================================
#          NAME:  cleanLogArchive
#   DESCRIPTION:  Cleans up the archived log directory
#    PARAMETERS:  Archive Directory, Logfile Name, Retention Time
#       RETURNS:  0 regardless of result.
#==============================================================================
function cleanLogArchive
{
    # [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    # [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

    [ ! -d ${ARCHIVE_LOG_ROOT} ] && return 0;

    for ARCHIVED_FILE in $(find ${ARCHIVE_LOG_ROOT} -type f -name \*.log\* -ctime +${LOG_RETENTION_PERIOD})
    do
        [ ! -z "${ARCHIVED_FILE}" ] && [ -f ${ARCHIVED_FILE} ] && rm -rf ${ARCHIVED_FILE};
    done

    # [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    # [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

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
    #[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    #[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

    [ ! -f ${LOG_ROOT}/${1} ] && return 0;

    if [ $(( $(date +"%s") - $(stat -L --format %Y ${LOG_ROOT}/${1}) > $(echo "${ROLLOVER_PERIOD} * 60 * 60" | bc) )) -eq 1 ]
    then
        if [ -f ${LOG_ROOT}/${1}.${LOG_RETENTION_PERIOD} ]
        then
            [ ${ARCHIVE_ENABLED} ] && mv ${LOG_ROOT}/${1}.${LOG_RETENTION_PERIOD} ${ARCHIVE_LOG_ROOT} || rm -f ${LOG_ROOT}/${1}.${LOG_RETENTION_PERIOD};
        fi

        ## rotate logs
        A=${LOG_RETENTION_PERIOD};

        while (( ${A} -ne 0 ))
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

        while (( ${A} -ne 0 ))
        do
            [ -f ${LOG_ROOT}/${1}.${A} ] && mv ${LOG_ROOT}/${1}.${A} ${LOG_ROOT}/${1}.$(expr ${A} + 1);

            (( A -= 1 ))
        done

        mv ${LOG_ROOT}/${1} ${LOG_ROOT}/${1}.1;
        touch ${LOG_ROOT}/${1};
    fi

    #[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    #[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

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
    #[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    #[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

    ## always do the timestamp first
    typeset TIMESTAMP_OPTS=$(echo ${CONVERSION_PATTERN} | cut -d "[" -f 2 | cut -d "]" -f 1 | cut -d ":" -f 2- | sed -e '/^ *#/d;s/#.*//')

    case ${1} in
        [Ee][Rr][Rr][Oo][Rr]|[Ee])
            RECORDER=$(echo ${CONVERSION_PATTERN} | sed -e "s^${TIMESTAMP_OPTS}^$(date +"${TIMESTAMP_OPTS}")^;s^%t^${PPID}^;s^%c^${ERROR_LOG_FILE}^;s^%-5p^${1}^;s^%M^${2}^;s^%F^${3}^;s^%L^${4}^;s^%m^${5}^");
            LOG_FILE=${ERROR_LOG_FILE};
            ;;
        [Dd][Ee][Bb][Uu][Gg]|[Dd])
            RECORDER=$(echo ${CONVERSION_PATTERN} | sed -e "s^${TIMESTAMP_OPTS}^$(date +"${TIMESTAMP_OPTS}")^;s^%t^${PPID}^;s^%c^${DEBUG_LOG_FILE}^;s^%-5p^${1}^;s^%M^${2}^;s^%F^${3}^;s^%L^${4}^;s^%m^${5}^");
            LOG_FILE=${DEBUG_LOG_FILE};
            ;;
        [Aa][Uu][Dd][Ii][Tt]|[Aa])
            RECORDER=$(echo ${CONVERSION_PATTERN} | sed -e "s^${TIMESTAMP_OPTS}^$(date +"${TIMESTAMP_OPTS}")^;s^%t^${PPID}^;s^%c^${AUDIT_LOG_FILE}^;s^%-5p^${1}^;s^%M^${2}^;s^%F^${3}^;s^%L^${4}^;s^%m^${5}^");
            LOG_FILE=${AUDIT_LOG_FILE};
            ;;
        [Ww][Aa][Rr][Nn]|[Ww])
            RECORDER=$(echo ${CONVERSION_PATTERN} | sed -e "s^${TIMESTAMP_OPTS}^$(date +"${TIMESTAMP_OPTS}")^;s^%t^${PPID}^;s^%c^${WARN_LOG_FILE}^;s^%-5p^${1}^;s^%M^${2}^;s^%F^${3}^;s^%L^${4}^;s^%m^${5}^");
            LOG_FILE=${WARN_LOG_FILE};
            ;;
        [Mm][Oo][Nn][Ii][Tt][Oo][Rr]|[Mm])
            RECORDER=$(echo ${CONVERSION_PATTERN} | sed -e "s^${TIMESTAMP_OPTS}^$(date +"${TIMESTAMP_OPTS}")^;s^%t^${PPID}^;s^%c^${MONITOR_LOG_FILE}^;s^%-5p^${1}^;s^%M^${2}^;s^%F^${3}^;s^%L^${4}^;s^%m^${5}^");
            LOG_FILE=${MONITOR_LOG_FILE};
            ;;
        *)
            RECORDER=$(echo ${CONVERSION_PATTERN} | sed -e "s^${TIMESTAMP_OPTS}^$(date +"${TIMESTAMP_OPTS}")^;s^%t^${PPID}^;s^%c^${INFO_LOG_FILE}^;s^%-5p^${1}^;s^%M^${2}^;s^%F^${3}^;s^%L^${4}^;s^%m^${5}^");
            LOG_FILE=${INFO_LOG_FILE};
            ;;
    esac

    echo "${RECORDER}" >> ${LOG_ROOT}/${LOG_FILE};

    unset TIMESTAMP_OPTS;
    unset RECORDER;
    unset LOG_FILE;

    #[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    #[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

    return 0;
}

[ ${#} -ne 0 ] && writeLogEntry "${@}";

#[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
#[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

return 0;
