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

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the usage of this application
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function usage
{
    print "${CNAME} - Watchdog for process execution";
    print "Usage: ${CNAME} [ -t <timeout> ] [ -i <interval> ] [ -d <delay> ] <command> [-?|-h show this help]";
    print "  -t      Number of seconds to wait for command completion. Default value: ${THREAD_TIMEOUT} seconds.";
    print "  -i      Interval between checks if the process is still alive. Positive integer, default value: ${THREAD_INTERVAL} seconds.";
    print "  -d      Delay between posting the SIGTERM signal and destroying the process by SIGKILL. Default value: ${THREAD_DELAY} seconds.";
    print "  -h|-?   Show this help";

    return 3;
}


## make sure we have arguments, if we do
## then load our constants and continue
[ ${#} -eq 0 ] && usage;

[ -f ${APP_ROOT}/lib/functions ] && . ${APP_ROOT}/lib/functions;
[ -f ${APP_ROOT}/lib/aliases ] && . ${APP_ROOT}/lib/aliases;

while getopts ":t:i:d:" OPTIONS 2>/dev/null; do
    case ${OPTIONS} in
        t) [[ ! -z "${OPTARG}" && $(isNaN ${OPTARG}) -eq 0 ]] && TIMEOUT=${OPTARG} || TIMEOUT=${THREAD_TIMEOUT} ;;
        n) [[ ! -z "${OPTARG}" && $(isNaN ${OPTARG}) -eq 0 ]] && INTERVAL=${OPTARG} || INTERVAL=${THREAD_INTERVAL} ;;
        d) [[ ! -z "${OPTARG}" && $(isNaN ${OPTARG}) -eq 0 ]] && DELAY=${OPTARG} || DELAY=${THREAD_DELAY} ;;
        *) usage; exit 1 ;;
    esac
done

shift $((OPTIND - 1))

(
    (( t = TIMEOUT ))

    while ((t > 0))
    do
        sleep ${INTERVAL}
        kill -0 ${$} || exit 0

        (( t -= ${INTERVAL} ))
    done

    print "Terminating process - timeout threshold exceeded";

    kill -s SIGTERM ${$} && kill -0 ${$} || exit 1
    sleep ${DELAY} && kill -s SIGKILL ${$} && kill -0 ${$} || exit 1
) 2> /dev/null &

exec "${@}"
