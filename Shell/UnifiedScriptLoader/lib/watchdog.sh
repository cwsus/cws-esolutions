#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  watchdog.sh
#         USAGE:  ./watchdog.sh
#   DESCRIPTION:  Process watchdog to ensure the given execution does not
#                 a provided timeout value. If so, the process is terminated.
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
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the usage of this application
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=3;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    echo "${THIS_CNAME} - Watchdog for process execution\n";
    echo "Usage: ${THIS_CNAME} [ -t <timeout> ] [ -i <interval> ] [ -d <delay> ] <command> [-?|-h show this help]
    -t         -> Number of seconds to wait for command completion. Default value: ${THREAD_TIMEOUT} seconds.
    -i         -> Interval between checks if the process is still alive. Positive integer, default value: ${THREAD_INTERVAL} seconds.
    -d         -> Delay between posting the SIGTERM signal and destroying the process by SIGKILL. Default value: ${THREAD_DELAY} seconds.
    -h|-?      -> Show this help\n";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

## make sure we have arguments, if we do
## then load our constants and continue
[ ${#} -eq 0 ] && usage && RETURN_CODE=${?};

[ -f ${APP_ROOT}/${LIB_DIRECTORY}/functions ] && . ${APP_ROOT}/${LIB_DIRECTORY}/functions;
[ -f ${APP_ROOT}/${LIB_DIRECTORY}/aliases ] && . ${APP_ROOT}/${LIB_DIRECTORY}/aliases;

while getopts ":t:i:d:" OPTIONS 2>/dev/null
do
    case ${OPTIONS} in
        t) [ ! -z "${OPTARG}" ] && typeset -i TIMEOUT=${OPTARG} || typeset -i TIMEOUT=${THREAD_TIMEOUT} ;;
        n) [ ! -z "${OPTARG}" ] && typeset -i INTERVAL=${OPTARG} || typeset -i INTERVAL=${THREAD_INTERVAL} ;;
        d) [ ! -z "${OPTARG}" ] && typeset -i DELAY=${OPTARG} || typeset -i DELAY=${THREAD_DELAY} ;;
        *) usage && RETURN_CODE=${?}; exit 1 ;;
    esac
done

shift $((OPTIND - 1))
(
    (( t = TIMEOUT ))

    while ((t > 0))
    do
        sleep ${INTERVAL};
        kill -0 ${$} || exit 0;

        (( t -= ${INTERVAL} ));
    done

    echo "Terminating process - timeout threshold exceeded";

    kill -s SIGTERM ${$} && kill -0 ${$} || return 1;
    sleep ${DELAY} && kill -s SIGKILL ${$} && kill -0 ${$} || return 1;
) 2> /dev/null &

exec "${@}"
