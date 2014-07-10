#!/${BIN_DIRECTORY}/sh
#==============================================================================
#
#          FILE:  restart_web.sh
#         USAGE:  ./restart_web.sh {start|stop|status|restart|reload}
#   DESCRIPTION:  SysV Init script to restart/reload or stop web services.
#                 This script is intended to be symbolically linked to from
#                 /etc/rc<x>.d/S92webservers, to provide automatic start of
#                 web services.
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

## Script constants
EXCEPTION_FILE=/opt/esupport/etc/eInfo/WebStartupExceptions;
TMP_PATH=/var/tmp/web;
START_SCRIPT_FILE_NAME=StartServer;
STOP_SCRIPT_FILE_NAME=StopServer;
STATUS_SCRIPT_FILE_NAME=StatServer;
CONFIGTEST_SCRIPT_FILE_NAME=ConfigTest;
SCRIPT_LOG_LOCATION=${TMP_PATH}/log;
START_LOG_FILE=StartServer.`date +"%m-%d-%Y"`.log;
STOP_LOG_FILE=StopServer.`date +"%m-%d-%Y"`.log;
STAT_LOG_FILE=StatusServer.`date + "%m-%d-%Y"`.log;
MONITOR_LOG_FILE_NAME=MonitorServer.`date + "%m-%d-%Y"`.log;
CONFIGTEST_LOG_FILE=ConfigTest.`date + "%m-%d-%Y"`.log;
IPLANET_CONFIG_FILE=config/magnus.conf;
SITE_PREFIX=https-;
ADMINSRV_NAME=admserv;
SYSTEM_HOSTNAME=`uname -n`;
RUN_AS_USER=websrv;
WEB_SYSDOWN_EMAIL="etech.team@us.hsbc.com";
A=0;
B=0;

ACTION=${1};

## Make sure the exception file is there, if it isnt,
## create it
[ ! -f ${EXCEPTION_FILE} ] && touch ${EXCEPTION_FILE};

## check for web install
if [ -d /opt/enterprise_61 ]
then
    if [ "${SYSTEM_HOSTNAME}" = "ibrw01" ] || [ "${SYSTEM_HOSTNAME}" = "ibrw02" ]
    then
        ## ist box, export appropriately
        ## this is a temporary workaround until ibrw01/2 dont have an enterprise_61 dir anymore
        WEB_SYSDOWN_SUBJECT="ALERT 0017: Web instance down - DEV";

        export SOFTTOKEN_DIR; SOFTTOKEN_DIR=/opt/enterpriseIST_61/.sunw;
        SUN_INSTALL_ROOT=/opt/enterpriseIST_61;
        SUN_INSTANCE_LIST=`ls -ltr ${SUN_INSTALL_ROOT} | grep ${SITE_PREFIX} | grep -v "acl" | grep -v ${SITE_PREFIX}admserv | grep -v ${SITE_PREFIX}${SYSTEM_HOSTNAME} | awk '{print $9}'`
    else
        WEB_SYSDOWN_SUBJECT="ALERT 0017: Web instance down - PRD";

        export SOFTTOKEN_DIR; SOFTTOKEN_DIR=/opt/enterprise_61/.sunw;
        SUN_INSTALL_ROOT=/opt/enterprise_61;
        SUN_INSTANCE_LIST=`ls -ltr ${SUN_INSTALL_ROOT} | grep ${SITE_PREFIX} | grep -v "acl" | grep -v ${SITE_PREFIX}admserv | grep -v ${SITE_PREFIX}${SYSTEM_HOSTNAME} | awk '{print $9}'`
    fi
elif [ -d /opt/enterpriseQA_61 ]
then
    WEB_SYSDOWN_SUBJECT="ALERT 0017: Web instance down - DEV";

    export SOFTTOKEN_DIR; SOFTTOKEN_DIR=/opt/enterpriseQA_61/.sunw;
    SUN_INSTALL_ROOT=/opt/enterpriseQA_61;
    SUN_INSTANCE_LIST=`ls -ltr ${SUN_INSTALL_ROOT} | grep ${SITE_PREFIX} | grep -v "acl" | grep -v ${SITE_PREFIX}admserv | grep -v ${SITE_PREFIX}${SYSTEM_HOSTNAME} | awk '{print $9}'`
elif [ -d /opt/enterpriseIST_61 ]
then
    WEB_SYSDOWN_SUBJECT="ALERT 0017: Web instance down - DEV";

    export SOFTTOKEN_DIR; SOFTTOKEN_DIR=/opt/enterpriseIST_61/.sunw;
    SUN_INSTALL_ROOT=/opt/enterpriseIST_61;
    SUN_INSTANCE_LIST=`ls -ltr ${SUN_INSTALL_ROOT} | grep ${SITE_PREFIX} | grep -v "acl" | grep -v ${SITE_PREFIX}admserv | grep -v ${SITE_PREFIX}${SYSTEM_HOSTNAME} | awk '{print $9}'`
else
    exit 1;
fi

## Make sure the right directories exist for temp files
## logs, start scripts, etc

if [ "${ACTION}" != "monitor" ]
then
    echo "A little housekeeping..."

    if [ -d ${TMP_PATH} ]
    then
        if [ -d ${SCRIPT_LOG_LOCATION} ]
        then
            echo "${TMP_PATH} and ${SCRIPT_LOG_LOCATION} exist, continuing..";
        else
            echo "${SCRIPT_LOG_LOCATION} does not exist, creating...";
            mkdir ${SCRIPT_LOG_LOCATION};
        fi
    else
        echo "${TMP_PATH} does not exist, creating...";
        mkdir ${TMP_PATH};

        if [ -d ${SCRIPT_LOG_LOCATION} ]
        then
            echo "${TMP_PATH} and ${SCRIPT_LOG_LOCATION} exist, continuing..";
        else
            echo "${SCRIPT_LOG_LOCATION} does not exist, creating...";
            mkdir ${SCRIPT_LOG_LOCATION};
        fi
    fi
fi

rm -rf ${SCRIPT_LOG_LOCATION}/*;
rm -rf ${TMP_PATH}/StartServer*;
rm -rf ${TMP_PATH}/StopServer*;
rm -rf ${TMP_PATH}/StatServer*;

## MAIN
case ${ACTION} in
    start)
        if [ ! -z "${2}" ]
        then
            ## we were provided a particular instance to start. do it.
            echo "Starting ${2}..";

            if [ "`/usr/ucb/whoami`" = "${RUN_AS_USER}" ]
            then
                ${SUN_INSTALL_ROOT}/${2}/start > ${SCRIPT_LOG_LOCATION}/${START_SCRIPT_FILE_NAME}${2}.log 2>&1 & PIDID=$!;
            else
                su - ${RUN_AS_USER} -c "${SUN_INSTALL_ROOT}/${2}/start" > ${SCRIPT_LOG_LOCATION}/${START_SCRIPT_FILE_NAME}${2}.log 2>&1 & PIDID=$!;
            fi

            wait ${PIDID};
            unset PIDID;

            PIDFILE=`grep PidLog ${SUN_INSTALL_ROOT}/${2}/${IPLANET_CONFIG_FILE} | awk '{print $2}'`;
            PID=`cat ${PIDFILE} 2>/dev/null`;

            if [ ! -z "${PID}" ]
            then
                IS_ACTIVE=`ps -ef | grep ${PID} | grep -v grep`;

                if [ -z "${IS_ACTIVE}" ]
                then
                    unset IS_ACTIVE;

                    exit 1;
                else
                    unset IS_ACTIVE;

                    exit 0;
                fi
            else
                unset IS_ACTIVE;

                exit 1;
            fi
        else
            ## start up the admin server and esupport server first
            echo "Starting https-admserv...";

            if [ "`/usr/ucb/whoami`" = "${RUN_AS_USER}" ]
            then
                ${SUN_INSTALL_ROOT}/${SITE_PREFIX}${ADMINSRV_NAME}/start > ${SCRIPT_LOG_LOCATION}/${START_SCRIPT_FILE_NAME}${ADMINSRV_NAME}.log 2>&1 & PIDID=$!
            else
                su - ${RUN_AS_USER} -c "${SUN_INSTALL_ROOT}/${SITE_PREFIX}${ADMINSRV_NAME}/start" > ${SCRIPT_LOG_LOCATION}/${START_SCRIPT_FILE_NAME}${ADMINSRV_NAME}.log 2>&1 & PIDID=$!;
            fi

            wait ${PIDID};
            unset PIDID;

            ## and start up esupport
            echo "Starting https-${SYSTEM_HOSTNAME}...";

            if [ "`/usr/ucb/whoami`" = "${RUN_AS_USER}" ]
            then
                ${SUN_INSTALL_ROOT}/${SITE_PREFIX}${SYSTEM_HOSTNAME}/start > ${SCRIPT_LOG_LOCATION}/${START_SCRIPT_FILE_NAME}${SYSTEM_HOSTNAME}.log 2>&1 & PIDID=$!;
            else
                su - ${RUN_AS_USER} -c "${SUN_INSTALL_ROOT}/${SITE_PREFIX}${SYSTEM_HOSTNAME}/start" > ${SCRIPT_LOG_LOCATION}/${START_SCRIPT_FILE_NAME}${SYSTEM_HOSTNAME}.log 2>&1 & PIDID=$!;
            fi

            wait ${PIDID};
            unset PIDID;

            ## ok, everything should be started now
            [ ! -z "${SUN_INSTANCE_LIST}" ] && set ${SUN_INSTANCE_LIST} || exit 1;

            while [ ${#} -ne 0 ]
    		do
    			echo "Starting web servers...";

                if [ "`/usr/ucb/whoami`" = "${RUN_AS_USER}" ]
                then
        			[ ! -z "${1}" ] && [ `grep -w -c ${1} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${1}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${2}" ] && [ `grep -w -c ${2} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${2}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${3}" ] && [ `grep -w -c ${3} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${3}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${4}" ] && [ `grep -w -c ${4} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${4}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${5}" ] && [ `grep -w -c ${5} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${5}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${6}" ] && [ `grep -w -c ${6} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${6}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${7}" ] && [ `grep -w -c ${7} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${7}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${8}" ] && [ `grep -w -c ${8} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${8}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${9}" ] && [ `grep -w -c ${9} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${9}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
    			else
        			[ ! -z "${1}" ] && [ `grep -w -c ${1} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${1}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${2}" ] && [ `grep -w -c ${2} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${2}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${3}" ] && [ `grep -w -c ${3} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${3}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${4}" ] && [ `grep -w -c ${4} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${4}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${5}" ] && [ `grep -w -c ${5} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${5}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${6}" ] && [ `grep -w -c ${6} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${6}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${7}" ] && [ `grep -w -c ${7} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${7}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${8}" ] && [ `grep -w -c ${8} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${8}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${9}" ] && [ `grep -w -c ${9} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${9}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
                fi

                echo "wait;" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
    		    chmod 755 ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;

    			exec ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh > ${SCRIPT_LOG_LOCATION}/${START_LOG_FILE}.${A} 2>&1 & PIDID=$!;
    			wait ${PIDID};
    			unset PIDID;

    			A=`expr "$A" + 1`;

    			[ ${#} -gt 9 ] && shift 9 > /dev/null 2>&1 || shift ${#} > /dev/null 2>&1;
    	    done

            A=0;
        fi
        ;;
    stop)
        if [ ! -z "${2}" ]
        then
            echo "Stopping ${2}..";

            if [ "`/usr/ucb/whoami`" = "${RUN_AS_USER}" ]
            then
                ${SUN_INSTALL_ROOT}/${2}/stop > ${SCRIPT_LOG_LOCATION}/${STOP_SCRIPT_FILE_NAME}${2}.log 2>&1 & PIDID=$!;
            else
                su - ${RUN_AS_USER} -c "${SUN_INSTALL_ROOT}/${2}/stop" > ${SCRIPT_LOG_LOCATION}/${STOP_SCRIPT_FILE_NAME}${2}.log 2>&1 & PIDID=$!;
            fi

            wait ${PIDID};
            unset PIDID;
        else
            [ ! -z "${SUN_INSTANCE_LIST}" ] && set ${SUN_INSTANCE_LIST} || exit 1;

            while [ ${#} -ne 0 ]
    		do
    			echo "Stopping web servers..."

                if [ "`/usr/ucb/whoami`" = "${RUN_AS_USER}" ]
                then
        			[ ! -z "${1}" ] && [ `grep -w -c ${1} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${1}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${2}" ] && [ `grep -w -c ${2} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${2}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${3}" ] && [ `grep -w -c ${3} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${3}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${4}" ] && [ `grep -w -c ${4} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${4}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${5}" ] && [ `grep -w -c ${5} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${5}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${6}" ] && [ `grep -w -c ${6} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${6}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${7}" ] && [ `grep -w -c ${7} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${7}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${8}" ] && [ `grep -w -c ${8} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${8}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${9}" ] && [ `grep -w -c ${9} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${9}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
    			else
        			[ ! -z "${1}" ] && [ `grep -w -c ${1} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${1}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${2}" ] && [ `grep -w -c ${2} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${2}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${3}" ] && [ `grep -w -c ${3} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${3}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${4}" ] && [ `grep -w -c ${4} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${4}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${5}" ] && [ `grep -w -c ${5} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${5}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${6}" ] && [ `grep -w -c ${6} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${6}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${7}" ] && [ `grep -w -c ${7} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${7}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${8}" ] && [ `grep -w -c ${8} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${8}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
        			[ ! -z "${9}" ] && [ `grep -w -c ${9} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${9}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
                fi

                echo "wait;" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
    		    chmod 755 ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;

    			exec ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh > ${SCRIPT_LOG_LOCATION}/${STOP_LOG_FILE}.${A} 2>&1 & PIDID=$!;
    			wait ${PIDID};
    			unset PIDID;

    			A=`expr "$A" + 1`;

    			[ ${#} -gt 9 ] && shift 9 > /dev/null 2>&1 || shift ${#} > /dev/null 2>&1;
    		done

            echo "Stopping https-admserv...";

            if [ "`/usr/ucb/whoami`" = "${RUN_AS_USER}" ]
            then
                ${SUN_INSTALL_ROOT}/${SITE_PREFIX}${ADMINSRV_NAME}/stop > ${SCRIPT_LOG_LOCATION}/${START_SCRIPT_FILE_NAME}${ADMINSRV_NAME}.log 2>&1 & PIDID=$!;
            else
                su - ${RUN_AS_USER} -c "${SUN_INSTALL_ROOT}/${SITE_PREFIX}${ADMINSRV_NAME}/stop" > ${SCRIPT_LOG_LOCATION}/${START_SCRIPT_FILE_NAME}${ADMINSRV_NAME}.log 2>&1 & PIDID=$!;
            fi

            wait ${PIDID};
            unset PIDID;

            ## and stop esupport
            echo "Stopping https-${SYSTEM_HOSTNAME}...";

            if [ "`/usr/ucb/whoami`" = "${RUN_AS_USER}" ]
            then
                ${SUN_INSTALL_ROOT}/${SITE_PREFIX}${SYSTEM_HOSTNAME}/stop > ${SCRIPT_LOG_LOCATION}/${START_SCRIPT_FILE_NAME}${SYSTEM_HOSTNAME}.log 2>&1 & PIDID=$!;
            else
                su - ${RUN_AS_USER} -c "${SUN_INSTALL_ROOT}/${SITE_PREFIX}${SYSTEM_HOSTNAME}/stop" > ${SCRIPT_LOG_LOCATION}/${START_SCRIPT_FILE_NAME}${SYSTEM_HOSTNAME}.log 2>&1 & PIDID=$!;
            fi

            wait ${PIDID};
            unset PIDID;

            A=0;
        fi
        ;;
    status)
        [ ! -z "${SUN_INSTANCE_LIST}" ] && set ${SUN_INSTANCE_LIST} || exit 1;

        while [ ${#} -ne 0 ]
		do
			[ ! -z "${1}" ] && [ `grep -w -c ${1} ${EXCEPTION_FILE}` -eq 0 ] && [ ! -z "`/usr/ucb/ps -auxwww | grep \`cat ${SUN_INSTALL_ROOT}/${1}/logs/pid 2> /dev/null\``" ] && echo "${1} is running..." || echo "${1} not currently started..";
			[ ! -z "${2}" ] && [ `grep -w -c ${2} ${EXCEPTION_FILE}` -eq 0 ] && [ ! -z "`/usr/ucb/ps -auxwww | grep \`cat ${SUN_INSTALL_ROOT}/${2}/logs/pid 2> /dev/null\``" ] && echo "${2} is running..." || echo "${2} not currently started..";
			[ ! -z "${3}" ] && [ `grep -w -c ${3} ${EXCEPTION_FILE}` -eq 0 ] && [ ! -z "`/usr/ucb/ps -auxwww | grep \`cat ${SUN_INSTALL_ROOT}/${3}/logs/pid 2> /dev/null\``" ] && echo "${3} is running..." || echo "${3} not currently started..";
			[ ! -z "${4}" ] && [ `grep -w -c ${4} ${EXCEPTION_FILE}` -eq 0 ] && [ ! -z "`/usr/ucb/ps -auxwww | grep \`cat ${SUN_INSTALL_ROOT}/${4}/logs/pid 2> /dev/null\``" ] && echo "${4} is running..." || echo "${4} not currently started..";
			[ ! -z "${5}" ] && [ `grep -w -c ${5} ${EXCEPTION_FILE}` -eq 0 ] && [ ! -z "`/usr/ucb/ps -auxwww | grep \`cat ${SUN_INSTALL_ROOT}/${5}/logs/pid 2> /dev/null\``" ] && echo "${5} is running..." || echo "${5} not currently started..";
			[ ! -z "${6}" ] && [ `grep -w -c ${6} ${EXCEPTION_FILE}` -eq 0 ] && [ ! -z "`/usr/ucb/ps -auxwww | grep \`cat ${SUN_INSTALL_ROOT}/${6}/logs/pid 2> /dev/null\``" ] && echo "${6} is running..." || echo "${6} not currently started..";
			[ ! -z "${7}" ] && [ `grep -w -c ${7} ${EXCEPTION_FILE}` -eq 0 ] && [ ! -z "`/usr/ucb/ps -auxwww | grep \`cat ${SUN_INSTALL_ROOT}/${7}/logs/pid 2> /dev/null\``" ] && echo "${7} is running..." || echo "${7} not currently started..";
			[ ! -z "${8}" ] && [ `grep -w -c ${8} ${EXCEPTION_FILE}` -eq 0 ] && [ ! -z "`/usr/ucb/ps -auxwww | grep \`cat ${SUN_INSTALL_ROOT}/${8}/logs/pid 2> /dev/null\``" ] && echo "${8} is running..." || echo "${8} not currently started..";
            [ ! -z "${9}" ] && [ `grep -w -c ${9} ${EXCEPTION_FILE}` -eq 0 ] && [ ! -z "`/usr/ucb/ps -auxwww | grep \`cat ${SUN_INSTALL_ROOT}/${9}/logs/pid 2> /dev/null\``" ] && echo "${9} is running..." || echo "${9} not currently started..";

			A=`expr "$A" + 1`;

			[ ${#} -gt 9 ] && shift 9 > /dev/null 2>&1 || shift ${#} > /dev/null 2>&1;
		done

        A=0;
        ;;
    restart)
        if [ ! -z "${2}" ]
        then
            echo "Stopping ${2}...";
            $0 stop ${2} & PIDID=$!;
            echo "Waiting for commands to complete..";
            wait ${PIDID};
            unset PIDID;

            echo "Starting web servers...";
            $0 start ${2} & PIDID=$!;
            echo "Waiting for commands to complete..";
            wait ${PIDID};
            unset PIDID;
        else
            echo "Stopping web servers...";
            $0 stop & PIDID=$!;
            echo "Waiting for commands to complete..";
            wait ${PIDID};
            unset PIDID;

            echo "Starting web servers...";
            $0 start & PIDID=$!;
            echo "Waiting for commands to complete..";
            wait ${PIDID};
            unset PIDID;
        fi
        ;;
    config-test)
        [ ! -z "${SUN_INSTANCE_LIST}" ] && set ${SUN_INSTANCE_LIST} || exit 1;

        while [ ${#} -ne 0 ]
		do
            if [ "`/usr/ucb/whoami`" = "${RUN_AS_USER}" ]
            then
        		[ ! -z "${1}" ] && [ `grep -w -c ${1} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${1}/start -configtest &" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${2}" ] && [ `grep -w -c ${2} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${2}/start -configtest &" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${3}" ] && [ `grep -w -c ${3} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${3}/start -configtest &" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${4}" ] && [ `grep -w -c ${4} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${4}/start -configtest &" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${5}" ] && [ `grep -w -c ${5} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${5}/start -configtest &" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${6}" ] && [ `grep -w -c ${6} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${6}/start -configtest &" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${7}" ] && [ `grep -w -c ${7} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${7}/start -configtest &" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${8}" ] && [ `grep -w -c ${8} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${8}/start -configtest &" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${9}" ] && [ `grep -w -c ${9} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${9}/start -configtest &" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
    		else
			    [ ! -z "${1}" ] && [ `grep -w -c ${2} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${1}/start -configtest &\"" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
			    [ ! -z "${2}" ] && [ `grep -w -c ${3} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${2}/start -configtest &\"" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
			    [ ! -z "${3}" ] && [ `grep -w -c ${4} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${3}/start -configtest &\"" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
			    [ ! -z "${4}" ] && [ `grep -w -c ${5} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${4}/start -configtest &\"" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
			    [ ! -z "${5}" ] && [ `grep -w -c ${6} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${5}/start -configtest &\"" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
			    [ ! -z "${6}" ] && [ `grep -w -c ${7} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${6}/start -configtest &\"" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
			    [ ! -z "${7}" ] && [ `grep -w -c ${8} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${7}/start -configtest &\"" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
                [ ! -z "${8}" ] && [ `grep -w -c ${9} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${8}/start -configtest &\"" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
                [ ! -z "${9}" ] && [ `grep -w -c ${9} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${9}/start -configtest &\"" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
            fi

            echo "wait;" >> ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;
		    chmod 755 ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh;

			exec ${TMP_PATH}/${CONFIGTEST_SCRIPT_FILE_NAME}-${A}.sh > ${SCRIPT_LOG_LOCATION}/${CONFIGTEST_LOG_FILE}.${A} 2>&1 & PIDID=$!;
			wait ${PIDID};
			unset PIDID;

			A=`expr "$A" + 1`;

			[ ${#} -gt 9 ] && shift 9 > /dev/null 2>&1 || shift ${#} > /dev/null 2>&1;
		done

        A=0;
        ;;
    test-start)
        [ ! -z "${SUN_INSTANCE_LIST}" ] && set ${SUN_INSTANCE_LIST} || exit 1;

        while [ ${#} -ne 0 ]
		do
            if [ "`/usr/ucb/whoami`" = "${RUN_AS_USER}" ]
            then
        		[ ! -z "${1}" ] && [ `grep -w -c ${1} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${1}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${2}" ] && [ `grep -w -c ${2} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${2}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${3}" ] && [ `grep -w -c ${3} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${3}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${4}" ] && [ `grep -w -c ${4} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${4}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${5}" ] && [ `grep -w -c ${5} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${5}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${6}" ] && [ `grep -w -c ${6} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${6}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${7}" ] && [ `grep -w -c ${7} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${7}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${8}" ] && [ `grep -w -c ${8} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${8}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${9}" ] && [ `grep -w -c ${9} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${9}/start &" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
    		else
        		[ ! -z "${1}" ] && [ `grep -w -c ${1} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${1}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${2}" ] && [ `grep -w -c ${2} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${2}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${3}" ] && [ `grep -w -c ${3} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${3}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${4}" ] && [ `grep -w -c ${4} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${4}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${5}" ] && [ `grep -w -c ${5} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${5}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${6}" ] && [ `grep -w -c ${6} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${6}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${7}" ] && [ `grep -w -c ${7} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${7}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${8}" ] && [ `grep -w -c ${8} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${8}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
        		[ ! -z "${9}" ] && [ `grep -w -c ${9} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${9}/start &\"" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
            fi

            echo "wait;" >> ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;
		    chmod 755 ${TMP_PATH}/${START_SCRIPT_FILE_NAME}-${A}.sh;

			A=`expr "$A" + 1`;

			[ ${#} -gt 9 ] && shift 9 > /dev/null 2>&1 || shift ${#} > /dev/null 2>&1;
		done

        A=0;

        echo "Listing server startup scripts...";

        for NODE in `ls -ltr ${TMP_PATH} | grep ${START_SCRIPT_FILE_NAME} | awk '{print $9}'`
        do
            echo ${TMP_PATH}/${NODE};
        done

        echo "Start scripts created - no further action. Argument provided was ${ACTION}.";
        exit 0;
        ;;
    test-stop)
        [ ! -z "${SUN_INSTANCE_LIST}" ] && set ${SUN_INSTANCE_LIST} || exit 1;

        while [ ${#} -ne 0 ]
		do
            if [ "`/usr/ucb/whoami`" = "${RUN_AS_USER}" ]
            then
       			[ ! -z "${1}" ] && [ `grep -w -c ${1} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${1}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${2}" ] && [ `grep -w -c ${2} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${2}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${3}" ] && [ `grep -w -c ${3} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${3}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${4}" ] && [ `grep -w -c ${4} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${4}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${5}" ] && [ `grep -w -c ${5} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${5}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${6}" ] && [ `grep -w -c ${6} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${6}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${7}" ] && [ `grep -w -c ${7} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${7}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${8}" ] && [ `grep -w -c ${8} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${8}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${9}" ] && [ `grep -w -c ${9} ${EXCEPTION_FILE}` -eq 0 ] && echo "${SUN_INSTALL_ROOT}/${9}/stop &" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
 			else
       			[ ! -z "${1}" ] && [ `grep -w -c ${1} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${1}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${2}" ] && [ `grep -w -c ${2} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${2}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${3}" ] && [ `grep -w -c ${3} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${3}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${4}" ] && [ `grep -w -c ${4} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${4}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${5}" ] && [ `grep -w -c ${5} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${5}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${6}" ] && [ `grep -w -c ${6} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${6}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${7}" ] && [ `grep -w -c ${7} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${7}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${8}" ] && [ `grep -w -c ${8} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${8}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
       			[ ! -z "${9}" ] && [ `grep -w -c ${9} ${EXCEPTION_FILE}` -eq 0 ] && echo "su - ${RUN_AS_USER} -c \"${SUN_INSTALL_ROOT}/${9}/stop &\"" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh;
            fi

            echo "wait;" >> ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh
		    chmod 755 ${TMP_PATH}/${STOP_SCRIPT_FILE_NAME}-${A}.sh

			A=`expr "$A" + 1`;

    		[ ${#} -gt 9 ] && shift 9 > /dev/null 2>&1 || shift ${#} > /dev/null 2>&1;
		done

        A=0;

        echo "Listing server shutdown scripts..."

        for NODE in `ls -ltr ${TMP_PATH} | grep ${STOP_SCRIPT_FILE_NAME} | awk '{print $9}'`
        do
            echo ${TMP_PATH}/${NODE}
        done

        echo "Shutdown scripts created - no further action. Argument provided was ${ACTION}."
        exit 0;
        ;;
    monitor)
        if [ -z "${SUN_INSTANCE_LIST}" ]
        then
            exit 0;
        else
            for SERVER_LIST in ${SUN_INSTANCE_LIST}
            do
                if [ `grep -w -c ${SERVER_LIST} ${EXCEPTION_FILE}` -eq 0 ]
                then
                    PIDFILE=`grep PidLog ${SUN_INSTALL_ROOT}/${SERVER_LIST}/${IPLANET_CONFIG_FILE} | awk '{print $2}'`;
                    PID=`cat ${PIDFILE} 2>/dev/null`;

                    if [ ! -z "${PID}" ]
                    then
                        IS_ACTIVE=`ps -ef | grep ${PID} | grep -v grep`;

                        if [ -z "${IS_ACTIVE}" ]
                        then
                            unset PID;
                            unset IS_ACTIVE;

                            su - ${RUN_AS_USER} -c "${SUN_INSTALL_ROOT}/${SERVER_LIST}/start";

                            PID=`cat ${PIDFILE} 2>/dev/null`;

                            if [ ! -z "${PID}" ]
                            then
                               RESTART_COMPLETE=`ps -ef | grep ${PID} | grep -v grep`;

                               if [ -z "${RESTART_COMPLETE}" ]
                               then
                                   echo "Failed to start ${SERVER_LIST}" >> ${SCRIPT_LOG_LOCATION}/${MONITOR_LOG_FILE_NAME};
                               fi
                            fi
                        fi
                    else
                        echo "${SERVER_LIST} is not running" >> ${SCRIPT_LOG_LOCATION}/${MONITOR_LOG_FILE_NAME};
                    fi
                fi
            done
        fi

        unset SUN_INSTANCE_LIST;
        unset PIDFILE;
        unset PID;
        unset IS_ACTIVE;
        unset RESTART_COMPLETE;

        if [ -s ${SCRIPT_LOG_LOCATION}/${MONITOR_LOG_FILE_NAME} ]
        then
            mailx -s ${WEB_SYSDOWN_SUBJECT} ${WEB_SYSDOWN_EMAIL} < ${SCRIPT_LOG_LOCATION}/${MONITOR_LOG_FILE_NAME};
        fi
        ;;
    *)
        echo "Usage: $0 <action>";
        echo "actions: {start | stop | status | restart | config-test | test-start | test-stop | monitor}";
        exit 1;
        ;;
esac
