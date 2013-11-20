#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  sendNotification.sh
#         USAGE:  ./sendNotification.sh monitor-name
#   DESCRIPTION:  Executes the requested monitoring script across the known web
#                 servers.
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

## Application constants
CNAME=$(basename ${0});
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT=$(dirname "${SCRIPT_ABSOLUTE_PATH}");

#===  FUNCTION  ===============================================================
#          NAME:  sendNotificationEmail
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
sendNotificationEmail()
{
    [[ ! -z ${TRACE} && "${TRACE}" == "${_TRUE}" ]] && set -x;
    METHOD_NAME="#sendNotificationEmail()";

    [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->enter");
    [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Processing notification email for ${MESSAGE_NAME}..");

    ## make sure mail code doesnt exist
    unset MAILER_CODE;

    ## and cleanup a little..
    [ -f ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT} ] && \
        rm -rf ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT} > /dev/null 2>&1;

    if [ -s ${APP_ROOT}/${MAIL_TEMPLATE_DIR}/${MESSAGE_NAME} ]
    then
        ## the message provided exists - process
        ## create copy
        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Creating working copy of ${MESSAGE_NAME}..");

        cp ${APP_ROOT}/${MAIL_TEMPLATE_DIR}/${MESSAGE_NAME} ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT} > /dev/null 2>&1;

        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Copy complete. Operating..");

        for REPLACEMENT_ITEM in $(cat ${APP_ROOT}/${MAIL_TEMPLATE_DIR}/${MESSAGE_NAME} | grep "&" | cut -d "&" -f 2)
        do
            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "REPLACEMENT_ITEM - ${REPLACEMENT_ITEM}");

            sed -e "s/&${REPLACEMENT_ITEM}/$(eval echo \${${REPLACEMENT_ITEM}})/g" \
                ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT} > ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT}.tmp;

            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Validating...");

            if [ $(grep -c "$(eval echo \${${REPLACEMENT_ITEM}})" ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT}.tmp) != 0 ]
            then
                ## ok, move it over now..
                [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Change validated. Continuing..");

                mv ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT}.tmp \
                    ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT} > /dev/null 2>&1;

                ## and ensure..
                if [ $(grep -c "$(eval echo \${${REPLACEMENT_ITEM}})" ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT}) != 0 ]
                then
                    ## good, keep going
                    [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Change validated. Continuing..");

                    continue;
                else
                    ## ok, its not there. break out - doesnt make sense to continue
                    $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "An error occurred generating the selected notification. Please try again.");

                    MAILER_CODE=1;

                    break;
                fi
            else
                $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "An error occurred generating the selected notification. Please try again.");

                MAILER_CODE=1;

                break;
            fi
        done

        if [ -z ${MAILER_CODE} ] || [ ${MAILER_CODE} == 0 ]
        then
            ## unset mailer code so we can re-use it
            unset MAILER_CODE;

            ## message generated, mail it out
            if [ -s ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT} ]
            then
                ## good, we have an email and it has stuff in it
                ## if this is for a csr, then get the csr to put in the email
                case ${MESSAGE_NAME} in
                    ${NOTIFY_CSR_EMAIL})
                        ## csr notification
                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "This is a CSR notification. Processing..");
                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Processing notification subject..");

                        case ${CERT_SIGNER} in
                            ${INTERNET_CERT_SIGNATORY})
                                NOTIFY_SUBJECT=$(echo ${NOTIFY_VCSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_HOSTNAME}/" -e "s/{CERT_SIGNER}/${CERT_SIGNER}/");
                                ;;
                            ${INTRANET_CERT_SIGNATORY})
                                NOTIFY_SUBJECT=$(echo ${NOTIFY_HCSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_HOSTNAME}/" -e "s/{CERT_SIGNER}/${CERT_SIGNER}/");
                                ;;
                        esac

                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "NOTIFY_SUBECT -> ${NOTIFY_SUBJECT}");

                        ## print in the zone..
                        ## cut the filesize
                        PRE_FILE_SIZE=$(wc -c ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT} | awk '{print $1}');

                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "PRE_FILE_SIZE -> ${PRE_FILE_SIZE}");

                        ## echo in the zone
                        cat ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr >> ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT};

                        POST_FILE_SIZE=$(wc -c ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT} | awk '{print $1}');

                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "POST_FILE_SIZE -> ${POST_FILE_SIZE}");

                        ## and make sure it got there..
                        if [ ${PRE_FILE_SIZE} != ${POST_FILE_SIZE} ]
                        then
                            ## ok, good - csr should be in there - mail it out
                            MAILER_CODE=0;
                        else
                            ## failed to generate the email
                            $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "PEM insertion FAILED. Please process manually.");

                            MAILER_CODE=1;
                        fi
                        ;;
                    ${NOTIFY_PEM_EMAIL})
                        ## pem notification
                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "This is a PEM notification. Processing..");
                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Processing notification subject..");

                        NOTIFY_SUBJECT=$(echo ${NOTIFY_PEM_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_HOSTNAME}/");

                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "NOTIFY_SUBECT -> ${NOTIFY_SUBJECT}");

                        ## print in the zone..
                        ## cut the filesize
                        PRE_FILE_SIZE=$(wc -c ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT} | awk '{print $1}');

                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "FILE_SIZE -> ${FILE_SIZE}");

                        ## echo in the zone
                        cat ${APP_ROOT}/${PEMSTORE}/${CERTIFICATE_NICKNAME}.pem | awk 'NR>4' >> ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT};

                        POST_FILE_SIZE=$(wc -c ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT} | awk '{print $1}');

                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "POST_FILE_SIZE -> ${POST_FILE_SIZE}");

                        ## and make sure it got there..
                        if [ ${PRE_FILE_SIZE} != ${POST_FILE_SIZE} ]
                        then
                            ## ok, good - csr should be in there - mail it out
                            MAILER_CODE=0;
                        else
                            ## failed to generate the email
                            $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "PEM insertion FAILED. Please process manually.");

                            MAILER_CODE=1;
                        fi
                        ;;
                    ${NOTIFY_OWNER_EMAIL})
                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "This is a PEM notification. Processing..");
                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Processing notification subject..");

                        NOTIFY_SUBJECT=$(echo ${NOTIFY_OWNER_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_HOSTNAME}/");

                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "NOTIFY_SUBECT -> ${NOTIFY_SUBJECT}");
                        ;;
                esac

                if [ -z ${MAILER_CODE} ] || [ ${MAILER_CODE} == 0 ]
                then
                    ## send it out
                    [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Executing command mailx -s \"${NOTIFY_SUBJECT}\" -r \"${NOTIFY_FROM_ADDRESS}\" \"${TARGET_AUDIENCE}\" < ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT}");

                    if [ "${VERBOSE}" == "${_TRUE}" ]
                    then
                        mailx -s "${NOTIFY_SUBJECT}" -r "${NOTIFY_FROM_ADDRESS}" \
                            "${TARGET_AUDIENCE}" < ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT};
                    else
                        mailx -s "${NOTIFY_SUBJECT}" -r "${NOTIFY_FROM_ADDRESS}" \
                            "${TARGET_AUDIENCE}" < ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT} > ${APP_ROOT}/${LOG_ROOT}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT} 2>&1;
                    fi
                    RET_CODE=$?;

                    [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "RET_CODE -> ${RET_CODE}");

                    if [ ${RET_CODE} != 0 ]
                    then
                        ## failed to send the email
                        $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "Failed to send the requested notification. Please process manually.");

                        RETURN_CODE=1;
                    else
                        ## we're done. we no longer need the email file so lets get rid of it
                        #rm -rf ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}-${NOTIFY_PROJECT_CODE}-${IUSER_AUDIT};

                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME} -> exit");

                        ## and return
                        RETURN_CODE=0;
                    fi
                else
                    ## email didnt get generated
                    $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "Email generation insertion FAILED. Please process manually.");

                    RETURN_CODE=1;
                fi
            fi
        fi
    else
        ## the selected notification file does not exist
        $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "The selected notification, ${MESSAGE_NAME}, does not exist. Cannot continue.");

        RETURN_CODE=1;
    fi

    unset TARGET_AUDIENCE;
    unset NOTIFY_SUBJECT;
    unset MESSAGE_NAME;
    unset PRE_FILE_SIZE;
    unset POST_FILE_SIZE;
    unset REPLACEMENT_ITEM;
    unset MAILER_CODE;

    [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME} -> exit");
}

#===  FUNCTION  ===============================================================
#          NAME:  sendNotificationEmail
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
sendAlertNotification()
{
    [[ ! -z ${TRACE} && "${TRACE}" == "${_TRUE}" ]] && set -x;
    METHOD_NAME="#sendAlertNotification()";

    [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->enter");
    [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Processing alert notification..");

    ## and cleanup a little..
    [ -f ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME} ] && \
        rm -rf ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME} > /dev/null 2>&1;

    if [ -s ${APP_ROOT}/${MAIL_TEMPLATE_DIR}/${MESSAGE_NAME} ]
    then
        ## the message provided exists - process
        ## create copy
        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Creating working copy of ${MESSAGE_NAME}..");

        cp ${APP_ROOT}/${MAIL_TEMPLATE_DIR}/${MESSAGE_NAME} ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME} > /dev/null 2>&1;

        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Copy complete. Operating..");

        if [ -s ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME} ]
        then
            if [ -s ${APP_ROOT}/${TMP_DIRECTORY}/${MONITOR_OUTPUT_FILE} ]
            then
                ## take checksum
                PRE_FILE_CKSUM=$(cksum ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME} | awk '{print $1}');
    
                [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "PRE_FILE_CKSUM -> ${PRE_FILE_CKSUM}");
    
                ## there arent any items to replace here like the other notifications. we just pipe in the monitoring file
                cat ${APP_ROOT}/${TMP_DIRECTORY}/${MONITOR_OUTPUT_FILE} >> ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME};
    
                POST_FILE_CKSUM=$(cksum ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME} | awk '{print $1}');
    
                [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "POST_FILE_CKSUM -> ${POST_FILE_CKSUM}");
    
                if [ ${PRE_FILE_CKSUM} != ${POST_FILE_CKSUM} ]
                then
                    ## data successfully submitted, email it out

                    ## build subject
                    case ${MESSAGE_NAME} in
                        ${ALERT_CERTIFICATE_EMAIL})
                            NOTIFY_SUBJECT=${ALERT_CERTIFICATE_SUBJECT};
                            NOTIFY_ADDRESS=${ALERT_CERTIFICATE_ADDRESS};
                            ;;
                        ${SITE_MONITOR_EMAIL})
                            NOTIFY_SUBJECT=${SITE_MONITOR_SUBJECT};
                            NOTIFY_ADDRESS=${SITE_MONITOR_ADDRESS};
                            ;;
                    esac

                    if [ ! -z ${TARGET_AUDIENCE} ]
                    then
                        NOTIFY_ADDRESS=${TARGET_EMAIL};
                    fi

                    [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "NOTIFY_SUBECT -> ${NOTIFY_SUBJECT}");
                    [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "NOTIFY_ADDRESS -> ${NOTIFY_ADDRESS}");

                    if [ ! -z ${NOTIFY_ADDRESS} ]
                    then
                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Executing command mailx -s \"${NOTIFY_SUBJECT}\" -r \"${NOTIFY_FROM_ADDRESS}\" \"${NOTIFY_ADDRESS}\" < ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME}");

                        if [ "${VERBOSE}" == "${_TRUE}" ]
                        then
                            mailx -s "${NOTIFY_SUBJECT}" -r "${NOTIFY_FROM_ADDRESS}" \
                                "${NOTIFY_ADDRESS}" < ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME};
                        else
                            mailx -s "${NOTIFY_SUBJECT}" -r "${NOTIFY_FROM_ADDRESS}" \
                                "${NOTIFY_ADDRESS}" < ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME} > ${APP_ROOT}/${LOG_ROOT}/${MESSAGE_NAME}.log 2>&1;
                        fi
                        RET_CODE=$?;
    
                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "RET_CODE -> ${RET_CODE}");
    
                        if [ ${RET_CODE} != 0 ]
                        then
                            ## failed to send the email
                            $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "Failed to send the requested notification. Please process manually.");
    
                            RETURN_CODE=1;
                        else
                            ## we're done. we no longer need the email file so lets get rid of it
                            rm -rf ${APP_ROOT}/${MAILSTORE}/${MESSAGE_NAME};
    
                            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME} -> exit");
    
                            ## and return
                            RETURN_CODE=0;
                        fi
                    else
                        ## unable to determine mailto address
                        $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "Failed to determine recipient address. Please process manually.");
    
                        RETURN_CODE=1;
                    fi
                else
                    ## data didnt copy in right. fail out
                    $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "Failed to copy notification data. Please process manually.");
    
                    RETURN_CODE=1;
                fi
            else
                ## couldnt create copy, error out
                $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "Failed to locate alert data. Please process manually.");
    
                RETURN_CODE=1;
            fi
        else
            ## couldnt create copy, error out
            $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "Failed to copy notification email. Please process manually.");

            RETURN_CODE=1;
        fi
    else
        ## the selected notification file does not exist
        $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "The selected notification, ${MESSAGE_NAME}, does not exist. Cannot continue.");

        RETURN_CODE=1;
    fi

    unset TARGET_AUDIENCE;
    unset NOTIFY_SUBJECT;
    unset MESSAGE_NAME;
    unset PRE_FILE_SIZE;
    unset POST_FILE_SIZE;
    unset REPLACEMENT_ITEM;
}

#===  FUNCTION  ===============================================================
#      NAME:  usage
#   DESCRIPTION:  Provide information on the usage of this application
#    PARAMETERS:  None
#   RETURNS:  0
#==============================================================================
usage()
{
    METHOD_NAME="sendNotification#usage()";

    [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->enter");

    print "$0 - Generates notification email to send to a selected audience.";
    print "Usage: $0 [ -m message name ] [ -p project code ] [ -a target audience ] [ -t notification type ] [ -e execute ] [ -?|-h show this help ]";
    print " -m    -> The message type to send.";
    print " -p    -> The project code associated with this notification";
    print " -a    -> The target audience for the email";
    print " -t    -> The type of notification. Currently available: notify, alert. If alert is chosen, the message " \
        "template should be specified as well, e.g. alert:certificates";
    print " -e    -> Execute the request";
    print " -h|-? -> Show this help";

    [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->exit");

    RETURN_CODE=3;
}

OPTIND=0;

## make sure we have arguments, if we do
## then load our constants and continue
if [ $# -eq 0 ]
then
    usage;
else
    METHOD_NAME="sendNotification.sh#init()";

    if [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]]
    then
        $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${CNAME} starting up.. Process ID $$");
        $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Provided arguments -> $@");
        $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->enter");
        $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "CNAME -> ${CNAME}");
        $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "SCRIPT_ABSOLUTE_PATH -> ${SCRIPT_ABSOLUTE_PATH}");
        $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "SCRIPT_ROOT -> ${APP_ROOT}/bin");
        $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "APP_ROOT -> ${APP_ROOT}");
        $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "APP_SYS_CONFIG -> ${APP_SYS_CONFIG}");
        $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "LOGGER -> ${LOGGER}");
    fi
fi

while getopts ":m:p:a:t:eh:" OPTIONS
do
    METHOD_NAME="sendNotification.sh#init()";

    [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->enter");

    case "${OPTIONS}" in
        m)
            ## set the message
            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "OPTARG -> ${OPTARG}");
            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Setting MESSAGE_NAME..");

            ## Capture the site root
            MESSAGE_NAME=${OPTARG};

            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "MESSAGE_NAME -> ${MESSAGE_NAME}");
            ;;
        p)
            ## set the message
            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "OPTARG -> ${OPTARG}");
            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Setting NOTIFY_PROJECT_CODE..");

            ## Capture the site root
            NOTIFY_PROJECT_CODE=${OPTARG};

            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "NOTIFY_PROJECT_CODE -> ${NOTIFY_PROJECT_CODE}");
            ;;
        a)
            ## set the message
            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "OPTARG -> ${OPTARG}");
            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Setting TARGET_AUDIENCE..");

            ## Capture the site root
            TARGET_AUDIENCE=$(echo ${OPTARG} | tr "[A-Z]" "[a-z]");

            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "TARGET_AUDIENCE -> ${TARGET_AUDIENCE}");
            ;;
        t)
            ## set the message
            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "OPTARG -> ${OPTARG}");
            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Setting NOTIFY_TYPE..");

            ## Capture the site root
            NOTIFY_TYPE=${OPTARG};

            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "NOTIFY_TYPE -> ${NOTIFY_TYPE}");
            ;;
        e)
            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Validating request..");

            ## Make sure we have enough information to process
            ## and execute
            if [ -z ${RETURN_CODE} ] || [ ${RETURN_CODE} == 0 ]
            then
                if [ ! -z ${NOTIFY_TYPE} ]
                then
                    if [ "${NOTIFY_TYPE}" == "${NOTIFY_TYPE_ALERT}" ]
                    then
                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Request validated - executing");
                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->exit");

                        if [ -z ${MESSAGE_NAME} ]
                        then
                            $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "No message name was provided. Cannot continue.");
                            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->exit");
    
                            RETURN_CODE=1;
                        else
                            sendAlertNotification;
                        fi
                    elif [ "${NOTIFY_TYPE}" == "${NOTIFY_TYPE_NOTIFY}" ]
                    then
                        if [ -z ${MESSAGE_NAME} ]
                        then
                            $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "No message name was provided. Cannot continue.");
                            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->exit");
    
                            RETURN_CODE=1;
                        elif [ -z ${NOTIFY_PROJECT_CODE} ]
                        then
                            $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "No project code was provided. Cannot continue.");
                            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->exit");
    
                            RETURN_CODE=1;
                        elif [ -z ${TARGET_AUDIENCE} ]
                        then
                            $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "No target audience was provided. Cannot continue.");
                            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->exit");
    
                            RETURN_CODE=1;
                        else
                            ## We have enough information to process the request, continue
                            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "Request validated - executing");
                            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->exit");
    
                            sendNotificationEmail;
                        fi
                    else
                        ## no valid notification type
                        $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "No valid notification type was provided. Cannot continue.");
                        [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->exit");

                        usage;
                    fi
                else
                    $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "No notification type was provided. Cannot continue.");
                    [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->exit");

                    usage;
                fi
            fi
            ;;
        h)
            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->exit");

            usage;
            ;;
        [\?])
            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->exit");

            usage;
            ;;
        *)
            [[ ! -z ${VERBOSE} && "${VERBOSE}" == "${_TRUE}" ]] && $(${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME}->exit");

            usage;
            ;;
    esac
done

shift ${OPTIND}-1;
return ${RETURN_CODE};
