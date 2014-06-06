#!/bin/sh
#==============================================================================
#
#          FILE:  restart_dns.sh
#         USAGE:  ./restart_dns.sh {start|stop|status|restart|reload}
#   DESCRIPTION:  SysV Init script to restart/reload or stop the named service.
#                 This script is intended to be symbolically linked to from
#                 /etc/rc<x>.d/S92named, to provide automatic start of the
#                 named service.
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

[[ ! -z "${TRACE}" && "${TRACE}" = "TRUE" ]] && set -x;

## Application constants
NAMED_BIN=/usr/sbin/named;
NAMED_CHKCONF=/usr/sbin/named-checkconf;
NAMED_CHECKZONE=/usr/sbin/named-checkzone;
NAMED_CONF=/var/named/etc/named.conf;
NAMED_PID=/var/named/var/run/named.pid;
NAMED_OWNER=named;
NAMED_CHROOT=/var/named;
NAMED_CHKCONF_OPTS="-t ${NAMED_CHROOT} -z";
NAMED_OPTS="-4 -c /etc/named.conf -m usage -n 1 -p 53 -t ${NAMED_CHROOT} -u ${NAMED_OWNER}";
NAMED_LOG_DIR=/var/named/var/log;
RNDC_BIN=/usr/sbin/rndc;
RNDC_OPTS="-c /var/named/etc/dnssec-keys/rndc.conf";
PLATFORM_TYPE=`uname`;
KRB5_KTNAME=${KEYTAB_FILE:-/etc/named.keytab};

[ -f /etc/rc.d/init.d/functions ] && . /etc/rc.d/init.d/functions;
[ -r /etc/sysconfig/named ] && . /etc/sysconfig/named;
[ -r /etc/sysconfig/network ] && . /etc/sysconfig/network;

## find out what our platform is
## we currently support solaris and
## linux, hooray
if [ "${PLATFORM_TYPE}" = "SunOS" ]
then
    ## alias grep to /usr/xpg4/bin/grep because /usr/bin/grep doesnt have a -x option
    alias grep='/usr/xpg4/bin/grep';

    ## alias ps to /usr/ucb/ps -auxwww
    alias ps='/usr/ucb/ps -auxww';
elif [ "${PLATFORM_TYPE}" = "Linux" ]
then
    ## alias ps to ps -ef
    alias ps='/bin/ps -ef';

    ## alias grep to grep without the colors
    alias grep='/bin/grep';
fi

ACTION=${1};

[ -f ${NAMED_BIN} ] || exit 1;
[ -f ${NAMED_CONF} ] || exit 1;

# See how we were called.
case "${ACTION}" in
    start)
        if [ -n "`pidof -s -n -m ${NAMED_BIN}`" ]
        then
            echo -n $"named: already running:"
            ${RNDC_BIN} ${RNDC_OPTS} status;

            exit 0;
        fi

        # check if configuration is correct
        if [ -x ${NAMED_CHKCONF} ] && [ -x ${NAMED_CHKZONE} ] && ${NAMED_CHKCONF} ${NAMED_CHKCONF_OPTS} ${NAMED_CONF} >/dev/null 2>&1
        then
            daemon --pidfile ${NAMED_PID} ${NAMED_BIN} ${NAMED_OPTS};

            RETVAL=${?};

            if [ ${RETVAL} -eq 0 ]
            then
                rm -f /var/run/${NAMED_PID};
                ln -s ${NAMED_PID} /var/run/named.pid;
            fi
        else
            named_err="`${NAMED_CHKCONF} ${NAMED_CHKCONF_OPTS} ${NAMED_CONF} 2>&1`";

            echo
            echo "Error in named configuration:";
            echo "${named_err}";

            [ -x /usr/bin/logger ] && echo "${named_err}" | /usr/bin/logger -pdaemon.error -tnamed;

            exit 2;
        fi

        if [ ${RETVAL} -eq 0 ]
        then
            touch /var/lock/subsys/named;
        else
            exit 7;
        fi
        ;;
    stop)
        if [ ! -s ${NAMED_PID} ]
        then
            echo "Service named not running";

            exit 1;
        else
            echo "Shutting down named: ";
            ${RNDC_BIN} ${RNDC_OPTS} stop;

            sleep 5;

            if [ -s ${NAMED_PID} ]
            then
                kill `cat ${NAMED_PID}`;
                rm -rf ${NAMED_PID};
            fi

            echo "[ OK ]";

            exit 0;
        fi
        ;;
    checkconfig)
        # check if configuration is correct
        if [ -x ${NAMED_CHKCONF} ] && [ -x ${NAMED_CHKZONE} ]
        then
            ${NAMED_CHKCONF} ${NAMED_CHKCONF_OPTS} ${NAMED_CONF} >/dev/null 2>&1
        else
            exit 2;
        fi
        ;;
    status)
        ${RNDC_BIN} ${RNDC_OPTS} status;
        exit ${?}
        ;;
    restart)
        ${0} stop;
        ${0} start;
        exit ${?};
        ;;
    reload)
        ${RNDC_BIN} ${RNDC_OPTS} reload;
        exit ${?};
        ;;
    probe)
        # named knows how to reload intelligently; we don't want linuxconf
        # to offer to restart every time
        ${RNDC_BIN} ${RNDC_OPTS} reload > ${NAMED_LOG_DIR}/rndc-reload.log 2>&1 || echo start;
        exit 0;
        ;;
    *)
        echo "Usage: named {start|stop|status|restart|reload}";
        exit 1;
esac
