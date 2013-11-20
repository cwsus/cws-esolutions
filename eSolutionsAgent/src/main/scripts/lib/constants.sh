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
#        AUTHOR:  $Author$
#       COMPANY:  CaspersBox Web Services
#       VERSION:  $Revision$
#       CREATED:  $Date$
#      REVISION:  ---
#==============================================================================

APP_SYS_CONFIG=/opt/cws/eSolutions/config/application.properties;

if [ ! -s ${APP_SYS_CONFIG} ]
then
    echo "Failed to locate configuration data. Cannot continue.";
    echo 1; #exit 1;
fi

## application information
APP_ROOT=$(cat ${APP_SYS_CONFIG} | grep -w app_root | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
MAIN_CLASS=$(cat ${APP_SYS_CONFIG} | grep -w main_class | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
JAVA_HOME=$(cat ${APP_SYS_CONFIG} | grep -w java_home | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
TMP_DIRECTORY=$(cat ${APP_SYS_CONFIG} | grep -w temp_dir | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
DATA_DIRECTORY=$(cat ${APP_SYS_CONFIG} | grep -w data_dir | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
THREAD_TIMEOUT=$(cat ${APP_SYS_CONFIG} | grep -w thread_timeout | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
SSH_THREAD_TIMEOUT=$(cat ${APP_SYS_CONFIG} | grep -w ssh_thread_timeout | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
RESTART_DELAY=$(cat ${APP_SYS_CONFIG} | grep -w service_restart_delay | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
ENVIRONMENT_SCRIPT=$(cat ${APP_SYS_CONFIG} | grep -w environment_script | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
MODIFIED_IFS=$(cat ${APP_SYS_CONFIG} | grep -w modified_ifs | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
LINE_TERMINATOR=$(cat ${APP_SYS_CONFIG} | grep -w line_terminator | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//g");
JAVA_ENDORSED_DIRS=$(cat ${APP_SYS_CONFIG} | grep -w java_endorsed_dirs | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
JAVA_EXT_DIRS=$(cat ${APP_SYS_CONFIG} | grep -w java_ext_dirs | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
JAVA_OPTS=$(cat ${APP_SYS_CONFIG} | grep -w java_opts | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//g");
JAVA_OPTS=$(echo ${JAVA_OPTS} | sed -e "s^@JAVA_HOME^${JAVA_HOME}^g" -e "s^@JAVA_ENDORSED_DIRS^${JAVA_ENDORSED_DIRS}^");
JAVA_OPTS=$(echo ${JAVA_OPTS} | sed -e "s^@JAVA_EXT_DIRS^${JAVA_EXT_DIRS}^" -e "s^@TMP_DIRECTORY^${TMP_DIRECTORY}^" -e "s^@APP_ROOT^${APP_ROOT}^");
SERVER_CONFIG=$(cat ${APP_SYS_CONFIG} | grep -w server_config | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//" -e "s^@APP_ROOT^${APP_ROOT}/");
SERVER_LOGGING=$(cat ${APP_SYS_CONFIG} | grep -w server_logging | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//" -e "s^@APP_ROOT^${APP_ROOT}/");

## resource files
ERROR_MESSAGES=$(cat ${APP_SYS_CONFIG} | grep -w error_resources | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
SYSTEM_MESSAGES=$(cat ${APP_SYS_CONFIG} | grep -w message_resources | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");

## security stuff
APP_SEC_CONFIG=${APP_ROOT}/$(cat ${APP_SYS_CONFIG} | grep -w security_config | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
RANDOM_GENERATOR=$(cat ${APP_SEC_CONFIG} | grep -w random_generator | grep -v "#" | cut -d "=" -f 2- | sed 's/^ *//');
ENTROPY_GENERATOR=$(cat ${APP_SEC_CONFIG} | grep -w entropy_generator | grep -v "#" | cut -d "=" -f 2- | sed 's/^ *//');
ENTROPY_FILE=$(cat ${APP_SEC_CONFIG} | grep -w entropy_file | grep -v "#" | cut -d "=" -f 2- | sed 's/^ *//');
ENTROPY_FILE_SIZE=$(cat ${APP_SEC_CONFIG} | grep -w entropy_file_size | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
HIGH_PRIVILEGED_PORT=$(cat ${APP_SEC_CONFIG} | grep -w high_privileged_port | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");

## logging
APP_LOGGING_CONFIG=${APP_ROOT}/$(cat ${APP_SYS_CONFIG} | grep -w logging_properties | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
LOGGER=$(cat ${APP_LOGGING_CONFIG} | grep -w LOGGER | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
VERBOSE=$(cat ${APP_LOGGING_CONFIG} | grep -w ENABLE_DEBUG | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
TRACE=$(cat ${APP_LOGGING_CONFIG} | grep -w ENABLE_TRACE | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
LOG_ROOT=$(cat ${APP_LOGGING_CONFIG} | grep -w LOG_ROOT | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
ARCHIVE_LOG_ROOT=$(cat ${APP_LOGGING_CONFIG} | grep -w ARCHIVE_LOG_ROOT | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
PS4=$(cat ${APP_LOGGING_CONFIG} | grep -w TRACE_CONVERSION_PATTERN | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
RECORDER_CONV=$(cat ${APP_LOGGING_CONFIG} | grep -w CONVERSION_PATTERN | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
RECORDER_FORMAT=$(cat ${APP_LOGGING_CONFIG} | grep -w DATE_PATTERN | grep -v "#" | cut -d "=" -f 2 | sed -e "s/^ *//" | sed -e "s^yyyy-MM-dd^$(date +"%Y-%m-%d")^");
ROTATE_ON_SIZE=$(cat ${APP_LOGGING_CONFIG} | grep -w FILE_SIZE_LIMIT | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
RETENTION_TIME=$(cat ${APP_LOGGING_CONFIG} | grep -w LOG_RETENTION_PERIOD | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
ROLLOVER_PERIOD=$(cat ${APP_LOGGING_CONFIG} | grep -w ROLLOVER_PERIOD | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
BASE_LOG_NAME=$(cat ${APP_LOGGING_CONFIG} | grep -w BASE_LOG_NAME | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
ERROR_RECORDER_FILE=$(cat ${APP_LOGGING_CONFIG} | grep -w ERROR_RECORDER.File | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
ERROR_RECORDER=$(echo ${ERROR_RECORDER_FILE} | sed -e "s^log^${RECORDER_FORMAT}.log^");
DEBUG_RECORDER_FILE=$(cat ${APP_LOGGING_CONFIG} | grep -w DEBUG_RECORDER.File | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
DEBUG_RECORDER=$(echo ${DEBUG_RECORDER_FILE} | sed -e "s^log^${RECORDER_FORMAT}.log^");
AUDIT_RECORDER_FILE=$(cat ${APP_LOGGING_CONFIG} | grep -w AUDIT_RECORDER.File | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
AUDIT_RECORDER=$(echo ${AUDIT_RECORDER_FILE}| sed -e "s^log^${RECORDER_FORMAT}.log^");
WARN_RECORDER_FILE=$(cat ${APP_LOGGING_CONFIG} | grep -w WARN_RECORDER.File | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
WARN_RECORDER=$(echo ${WARN_RECORDER_FILE}| sed -e "s^log^${RECORDER_FORMAT}.log^");
MONITOR_RECORDER_FILE=$(cat ${APP_LOGGING_CONFIG} | grep -w MONITOR_RECORDER.File | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
MONITOR_RECORDER=$(echo ${MONITOR_RECORDER_FILE}| sed -e "s^log^${RECORDER_FORMAT}.log^");

## backup configuration
APP_BACKUP_CONFIG=${APP_ROOT}/$(cat ${APP_SYS_CONFIG} | grep -w backup_properties | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
IS_BACKUP_ENABLED=$(cat ${APP_BACKUP_CONFIG} | grep -w perform_full_backup | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
BACKUP_DIRECTORY=$(cat ${APP_BACKUP_CONFIG} | grep -w backup_dir | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
BACKUP_RETENTION_TIME=$(cat ${APP_BACKUP_CONFIG} | grep -w backup_lifetime | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
BACKUP_FILE_NAME=$(cat ${APP_BACKUP_CONFIG} | grep -w backup_file_name | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");

## path
APP_PATH=$(cat ${APP_SYS_CONFIG} | grep -w app_path | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
SYS_PATH=$(cat ${APP_SYS_CONFIG} | grep -w sys_path | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
LIBRARY_PATH=$(cat ${APP_SYS_CONFIG} | grep -w ld_library_path | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
CLASS_PATH=$(cat ${APP_SYS_CONFIG} | grep -w classpath | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//' | sed -e "s^@APP_ROOT^${APP_ROOT}^");

## mail data
APP_MAIL_CONFIG=${APP_ROOT}/$(cat ${APP_SYS_CONFIG} | grep -w email_configuration | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
MAILER_CLASS=$(cat ${APP_MAIL_CONFIG} | grep -w mailer_class | grep -v "#" | cut -d "=" -f 2- | sed 's/^ *//');
MAILSTORE=$(cat ${APP_MAIL_CONFIG} | grep -w mailstore_dir | grep -v "#" | cut -d "=" -f 2- | sed 's/^ *//');
MAIL_TEMPLATE_DIR=$(cat ${APP_MAIL_CONFIG} | grep -w mail_template_dir | grep -v "#" | cut -d "=" -f 2- | sed 's/^ *//');
SEND_NOTIFIES=$(cat ${APP_MAIL_CONFIG} | grep -w send_notifies | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
NOTIFY_FROM_ADDRESS=$(cat ${APP_MAIL_CONFIG} | grep -w notify_from_address | grep -v "#" | cut -d "=" -f 2- | sed 's/^ *//g');

## alert types
NOTIFY_TYPE_NOTIFY=$(cat ${APP_MAIL_CONFIG} | grep -w notify_type_notify | grep -v "#" | cut -d "=" -f 2- | sed 's/^ *//g');
NOTIFY_TYPE_ALERT=$(cat ${APP_MAIL_CONFIG} | grep -w notify_type_alert | grep -v "#" | cut -d "=" -f 2- | sed 's/^ *//g');

## monitoring
APP_MONITOR_CONFIG=${APP_ROOT}/$(cat ${APP_SYS_CONFIG} | grep -w monitor_config_file | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
MONITOR_WORK_DIR=${APP_ROOT}/$(cat ${APP_SYS_CONFIG} | grep -w monitor_work_dir | grep -v "#" | cut -d "=" -f 2- | sed -e "s/^ *//");
LOG_FILE_DELAY=$(cat ${APP_MONITOR_CONFIG} | grep -w log_file_delay | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
OPTIONS_MONITOR_STRING=$(cat ${APP_MONITOR_CONFIG} | grep -w monitor_options_string | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
SVCTRACE_MONITOR_STRING=$(cat ${APP_MONITOR_CONFIG} | grep -w monitor_svctrace_string | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
SVCTRACE_MONITOR_STRING=$(cat ${APP_MONITOR_CONFIG} | grep -w monitor_svctrace_string | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
MONITOR_OUTPUT_FILE=$(cat ${APP_MONITOR_CONFIG} | grep -w monitor_output_file | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
MONITOR_OUTPUT_EXPIRES=$(cat ${APP_MONITOR_CONFIG} | grep -w monitor_output_expires | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
MONITOR_THREAD_TIMEOUT=$(cat ${APP_MONITOR_CONFIG} | grep -w monitor_thread_timeout | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');

## uncommon constants
_TRUE=$(cat ${APP_SYS_CONFIG} | grep -w TRUE | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
_FALSE=$(cat ${APP_SYS_CONFIG} | grep -w FALSE | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');
_OK=$(cat ${APP_SYS_CONFIG} | grep -w OK | grep -v "#" | cut -d "=" -f 2 | sed 's/^ *//');

## month directors
Jan=01;
Feb=02;
Apr=03;
Mar=04;
May=05;
Jun=06;
Jul=07;
Aug=08;
Sep=09;
Oct=10;
Nov=11;
Dec=12;

## counters
typeset -i A                        ; A=0;
typeset -i B                        ; B=0;
typeset -i C                        ; C=0;
typeset -i D                        ; D=0;
typeset -i FILE_COUNT               ; FILE_COUNT=0;
typeset -i ERROR_COUNT              ; ERROR_COUNT=0;
typeset -i AUTHORIZATION_COUNT      ; AUTHORIZATION_COUNT=0;

## export what needs to be exported
## set path, incorporating approot
export PATH                         ; PATH=${PATH}:${APP_PATH}:${SYS_PATH}:${IPLANET_PATH}:${IHS_PATH};
export LD_LIBRARY_PATH              ; LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${LIBRARY_PATH};
export CLASSPATH                    ; CLASSPATH=${CLASSPATH}:${CLASS_PATH};

## export app root
export APP_ROOT;

## export config files
export APP_SYS_CONFIG;
export APP_SEC_CONFIG;
export APP_LOGGING_CONFIG;
export APP_WEB_CONFIG;
export ENVIRONMENT_SCRIPT;

## export logging detail
export LOG_ROOT;
export ARCHIVE_LOG_ROOT;
export TRACE;
export RECORDER_CONV;
export RECORDER_FORMAT;
export ROTATE_ON_SIZE;
export RETENTION_TIME;
export ROLLOVER_PERIOD;
export BASE_LOG_NAME;
export ERROR_RECORDER_FILE;
export ERROR_RECORDER;
export DEBUG_RECORDER_FILE;
export DEBUG_RECORDER;
export AUDIT_RECORDER_FILE;
export AUDIT_RECORDER;
export WARN_RECORDER;
export WARN_RECORDER_FILE;
export MONITOR_RECORDER;
export MONITOR_RECORDER_FILE;

## find out what our platform is
## we currently support solaris and
## linux, hooray
if [ "$(uname)" == "SunOS" ]
then
    if [ -s /usr/sbin/cryptoadm ]
    then
        alias getTokenName='/usr/sbin/cryptoadm list metaslot | grep -w token | cut -d ":" -f 2 | sed -e "s/^ *//g"';
        alias getKeyStoreDir='/usr/sbin/svccfg -s scakiod listprop | grep -w ${KEYSTORE_DIR_IDENTIFIER}';
    fi

    ## alias grep -w to /usr/xpg4/bin/grep -w because /usr/bin/grep -w doesnt have a -x option
    alias grep='/usr/xpg4/bin/grep';

    ## alias ps to /usr/ucb/ps -auxwww
    alias ps='/usr/ucb/ps -auxwww';
    alias awk='/usr/xpg4/bin/awk';
elif [ "$(uname)" == "Linux" ]
then
    ## alias ps to ps -ef
    alias ps='/bin/ps -ef';

    ## alias grep -w to grep -w without the colors
    alias grep='/bin/grep';

    ## alias ping to ping with options
    alias ping='ping -c 1 -w 3';
fi

## If you ened to specify a custom location to java,
## do so here. otherwise system default is used
alias java='$(which java)';

## common aliases

## set up our date convertor
## converts YYYY MM DD (2011 08 17) to epoch
function returnEpochTime
{
    if [ $# -eq 0 ]
    then
        print "$0 - Generates and returns the Unix epoch for a provided date (or date range).";
        print " Required arguments: The date to convert in form YYYY MM DD, quoted. (if provided, MUST be qouted)";
        print " Optional arguments: A number of days to calculate a future epoch, for example, 45.";
        print " Example: returnEpochTime \"2011 11 18\" 45";

        return 1;
    else
        if [ $# -eq 2 ] || [ $# -eq 4 ]
        then
            echo $* | perl -MTime::Local -ane '
                qw(timelocal);
    
                my $seconds = (3600 * 24);
                my $now = timelocal(0, 0, 0, $F[2], $F[1] - 1, $F[0]);
                my $validationPeriod = $now + ($F[3] * $seconds);
    
                print "$validationPeriod\n"; ';
        elif [ $# -eq 1 ] || [ $# -eq 3 ]
        then
            ## supposed to be quoted but support it anyway
            echo $* | perl -MTime::Local -ane '
                qw(timelocal);
    
                my $seconds = (3600 * 24);
                my $now = timelocal(0, 0, 0, $F[2], $F[1] - 1, $F[0]);
                my $validationPeriod = $now + ($F[3] * $seconds);
    
                print "$validationPeriod\n"; ';
        else
            print "$0 - Generates and returns the Unix epoch for a provided date (or date range).";
            print " Required arguments: The date to convert in form YYYY MM DD, quoted. (if provided, MUST be qouted)";
            print " Optional arguments: A number of days to calculate a future epoch, for example, 45.";
            print " Example: returnEpochTime \"2011 11 18\" 45";

            return 1;
        fi

        return 0;
    fi
}

function returnRandomCharacters
{
    if [ $# -eq 0 ]
    then
        echo "Must provide a string length.";

        return 1;
    else
        echo $* | perl -MTime::Local -ane '
            my $password;
            my $_rand;

            my $password_length = $_[0];

            if (!$password_length)
            {
                $password_length = 10;
            }

            my @chars = split(" ",
                "a b c d e f g h i j k l m n o
                p q r s t u v w x y z A B C D
                E F G H I J K L M N O P Q R S
                T U V W X Y Z 0 1 2 3 4 5 6 7
                8 9");

            srand;

            for (my $i=0; $i <= $password_length ;$i++)
            {
                $_rand = int(rand 71);

                $password .= $chars[$_rand];
            }

            print "$password\n";';

        return 0;
    fi
}

function isNaN
{
    if [ ! -z ${1} ]
    then
        case ${1} in
            ?([+-])+([0-9]))
                echo ${_TRUE};
                ;;
            *)
                echo ${_FALSE};
                ;;
        esac
    else
        return 1;
    fi
}

## create logging directories if they dont already exist..
[ ! -d ${APP_ROOT}/${LOG_ROOT} ] && mkdir -p ${APP_ROOT}/${LOG_ROOT};
[ ! -d ${APP_ROOT}/${LOG_ROOT}/${ARCHIVE_LOG_ROOT} ] && mkdir ${APP_ROOT}/${LOG_ROOT}/${ARCHIVE_LOG_ROOT};

## clean up old logs
cd ${APP_ROOT}/${LOG_ROOT};

for ARCHIVEABLE_LOG in $(find -name "*${BASE_LOG_NAME}*" ! -name \*.tar -ctime +${RETENTION_TIME} -print)
do
    if [ -f ${APP_ROOT}/${LOG_ROOT}/${ARCHIVE_LOG_ROOT}/${BASE_LOG_NAME}-$(date +"%Y-%m-%d").tar ]
    then
        tar uf ${APP_ROOT}/${LOG_ROOT}/${ARCHIVE_LOG_ROOT}/${BASE_LOG_NAME}-$(date +"%Y-%m-%d").tar ${ARCHIVEABLE_LOG};
    else
        tar cf ${APP_ROOT}/${LOG_ROOT}/${ARCHIVE_LOG_ROOT}/${BASE_LOG_NAME}-$(date +"%Y-%m-%d").tar ${ARCHIVEABLE_LOG};
    fi

    rm -rf ${ARCHIVEABLE_LOG};
done

## check to see if the files in the log root
## are bigger than the desired rotate limit
## if they are, rotate them out
for LOG_FILE in $(find -name "*${BASE_LOG_NAME}*" ! -name \*.tar -print)
do
    if [ $(du -k ${LOG_FILE} | awk '{print $1}') -ge ${ROTATE_ON_SIZE} ]
    then
        if [ -f ${APP_ROOT}/${LOG_ROOT}/${ARCHIVE_LOG_ROOT}/${BASE_LOG_NAME}-$(date +"%Y-%m-%d").tar ]
        then
            tar uf ${APP_ROOT}/${LOG_ROOT}/${ARCHIVE_LOG_ROOT}/${BASE_LOG_NAME}-$(date +"%Y-%m-%d").tar ${LOG_FILE};
        else
            tar cf ${APP_ROOT}/${LOG_ROOT}/${ARCHIVE_LOG_ROOT}/${BASE_LOG_NAME}-$(date +"%Y-%m-%d").tar ${LOG_FILE};
        fi

        cat /dev/null > ${LOG_FILE};
    fi
done

unset ARCHIVEABLE_LOG;
unset LOG_FILE;

cd;

## and finally make sure our directories exist
[ ! -d ${APP_ROOT}/${TMP_DIRECTORY} ] && mkdir -p ${APP_ROOT}/${TMP_DIRECTORY};
[ ! -d ${APP_ROOT}/${BACKUP_DIRECTORY} ] && mkdir -p ${APP_ROOT}/${BACKUP_DIRECTORY};
[ ! -d ${APP_ROOT}/${DATA_DIRECTORY} ] && mkdir -p ${APP_ROOT}/${DATA_DIRECTORY};

## clean up temp directory
rm -rf ${APP_ROOT}/${TMP_DIRECTORY}/* > /dev/null 2>&1;
