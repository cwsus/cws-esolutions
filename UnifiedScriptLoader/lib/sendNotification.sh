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
#        AUTHOR:  Kevin Huntly <kmhuntly@gmail.com>
#       COMPANY:  CaspersBox Web Services
#       VERSION:  1.0
#       CREATED:  ---
#      REVISION:  ---
#
#==============================================================================

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

## Application constants
CNAME="$(/usr/bin/env basename ${0})";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[ -z "${APP_ROOT}" ] && [ -f ${SCRIPT_ROOT}/../lib/constants ] && . ${SCRIPT_ROOT}/../lib/constants;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${APP_ROOT}" ] && echo "Failed to locate configuration data. Cannot continue." && exit 1;

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

[ -z "${ENABLE_EMAIL}" ] || [ "${ENABLE_EMAIL}" = "${_FALSE}" ] && return 0;

#===  FUNCTION  ===============================================================
#          NAME:  sendNotificationEmail
#   DESCRIPTION:  Sends an email based on template and provided flags
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function sendNotificationEmail
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing notification email for ${MESSAGE_TEMPLATE}..";

    [ -z "${MAILRC_ACCOUNT_NAME}" ] && MAIL_CMD="/usr/bin/env mailx" || MAIL_CMD="/usr/bin/env mailx -A ${MAILRC_ACCOUNT_NAME}";
    typeset MESSAGE_FILE=${MAILSTORE}/${MESSAGE_TEMPLATE}-${IUSER_AUDIT}.${DATESYS};
    typeset TEMPLATE_FILE=${MAIL_TEMPLATE_DIR}/${MESSAGE_TEMPLATE};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MAIL_CMD -> ${MESSAGE_FILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MESSAGE_FILE -> ${MESSAGE_FILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TEMPLATE_FILE -> ${TEMPLATE_FILE}";

    ## and cleanup a little..
    [ -f ${MESSAGE_FILE} ] && rm -rf ${MESSAGE_FILE} > /dev/null 2>&1;

    if [ -s ${TEMPLATE_FILE} ]
    then
        ## the message provided exists - process
        ## create copy
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating working copy of ${MESSAGE_TEMPLATE}..";

        typeset MESSAGE_SUBJECT=$(head -1 ${TEMPLATE_FILE});

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MESSAGE_SUBJECT - ${MESSAGE_SUBJECT}";

        sed -e '1,2d' ${TEMPLATE_FILE} > ${MESSAGE_FILE} 2>/dev/null;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copy complete. Operating..";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Obtaining message subject from template..";

        for REPLACEMENT_ITEM in $(grep "&" ${TEMPLATE_FILE} | cut -d "&" -f 2)
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REPLACEMENT_ITEM - ${REPLACEMENT_ITEM}";

            sed -e "s/&${REPLACEMENT_ITEM}/$(eval echo \${${REPLACEMENT_ITEM}})/g" ${MESSAGE_FILE} > ${MESSAGE_FILE}.tmp;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating...";

            if [ ! -z "$(grep $(eval echo \${${REPLACEMENT_ITEM}}) ${MESSAGE_FILE}.tmp)" ]
            then
                ## ok, move it over now..
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                mv ${MAILSTORE}/${MESSAGE_FILE}.tmp ${MESSAGE_FILE}> /dev/null 2>&1;

                ## and ensure..
                if [ ! -z "$(grep $(eval echo \${${REPLACEMENT_ITEM}}) ${MESSAGE_FILE})" ]
                then
                    ## good, keep going
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                    continue;
                else
                    ## ok, its not there. break out - doesnt make sense to continue
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the selected notification. Please try again.";

                    typeset MAILER_CODE=1;

                    break;
                fi
            else
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the selected notification. Please try again.";

                typeset MAILER_CODE=1;

                break;
            fi
        done

        if [ -z "${MAILER_CODE}" ] || [ ${MAILER_CODE} -eq 0 ]
        then
            ## unset mailer code so we can re-use it
            unset MAILER_CODE;

            ## message generated, mail it out
            if [ -s ${MESSAGE_FILE} ]
            then
                if [ ! -z "${FILE_CONTENT}" ]
                then
                    ## we've been asked to include file content within the email. slide it in ...
                    ## print in the zone..
                    ## cut the filesize
                    typeset PRE_FILE_SIZE=$(wc -c ${MESSAGE_FILE} | awk '{print $1}');

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRE_FILE_SIZE -> ${PRE_FILE_SIZE}";

                    ## echo in the zone
                    cat ${FILE_CONTENT} >> ${MESSAGE_FILE};

                    typeset POST_FILE_SIZE=$(wc -c ${MESSAGE_FILE} | awk '{print $1}');

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POST_FILE_SIZE -> ${POST_FILE_SIZE}";

                    ## and make sure it got there..
                    if [ ${PRE_FILE_SIZE} != ${POST_FILE_SIZE} ]
                    then
                        ## ok, good - csr should be in there - mail it out
                        RETURN_CODE=0;
                    else
                        ## failed to generate the email
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File content insertion FAILED. Please process manually.";

                        RETURN_CODE=1;
                    fi
                fi

                if [ -z "${RETURN_CODE}" ] || [ ${RETURN_CODE} -eq 0 ]
                then
                    ## send it out
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command mailx -s \"${NOTIFY_SUBJECT}\" -r \"${NOTIFY_FROM_ADDRESS}\" \"${TARGET_AUDIENCE}\" < ${MAILSTORE}/${MESSAGE_TEMPLATE}-${IUSER_AUDIT}";

                    if [ ! -z "${ATTACH_FILE}" ]
                    then

                        if [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ]
                        then
                            /usr/bin/env uuencode ${ATTACH_FILE} $(/usr/bin/env basename ${ATTACH_FILE}) | ${MAIL_CMD} -v -s "${MESSAGE_SUBJECT}" -r "${NOTIFY_FROM_ADDRESS}" \
                                "${TARGET_AUDIENCE}" < ${MESSAGE_FILE} > ${LOG_ROOT}/${MESSAGE_TEMPLATE}.log 2>&1;
                        else
                            /usr/bin/env uuencode ${ATTACH_FILE} $(/usr/bin/env basename ${ATTACH_FILE}) | ${MAIL_CMD} -s "${MESSAGE_SUBJECT}" -r "${NOTIFY_FROM_ADDRESS}" \
                                "${TARGET_AUDIENCE}" < ${MESSAGE_FILE};
                        fi
                    else
                        if [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ]
                        then
                            ${MAIL_CMD} -v -s "${MESSAGE_SUBJECT}" -r "${NOTIFY_FROM_ADDRESS}" \
                                "${TARGET_AUDIENCE}" < ${MESSAGE_FILE} > ${LOG_ROOT}/${MESSAGE_TEMPLATE}.log 2>&1;
                        else
                            ${MAIL_CMD} -s "${MESSAGE_SUBJECT}" -r "${NOTIFY_FROM_ADDRESS}" "${TARGET_AUDIENCE}" < ${MESSAGE_FILE};
                        fi
                    fi
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                    if [ ${RET_CODE} -ne 0 ]
                    then
                        ## failed to send the email
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to send the requested notification. Please process manually.";

                        RETURN_CODE=1;
                    else
                        ## we're done. we no longer need the email file so lets get rid of it
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        ## and return
                        RETURN_CODE=0;
                    fi
                else
                    ## email didnt get generated
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Email generation insertion FAILED. Please process manually.";

                    RETURN_CODE=1;
                fi
            fi
        fi
    else
        ## the selected notification file does not exist
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The selected notification, ${MESSAGE_TEMPLATE}, does not exist. Cannot continue.";

        RETURN_CODE=1;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ -f ${MESSAGE_FILE} ] && rm -rf ${MESSAGE_FILE};

    unset MESSAGE_TEMPLATE;
    unset FILE_CONTENT;
    unset TARGET_AUDIENCE;
    unset ATTACH_FILE;
    unset MAILER_CODE;
    unset REPLACEMENT_ITEM;
    unset PRE_FILE_SIZE;
    unset POST_FILE_SIZE;
    unset RET_CODE;
    unset METHOD_NAME;
    unset CNAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the usage of this application
#    PARAMETERS:  None
#   RETURNS:  0
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=3;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    echo "${THIS_CNAME} - Generates notification email to send to a selected audience.";
    echo "Usage: ${THIS_CNAME} [ -m <message template> ] [ -f <file> ] [ -t <send to> ] [ -a ] [ -e ] [ -h|-? ]
    -m         -> The message template to utilize.
    -f         -> If the content of a file should be applied to the message, the file should be specified here.
    -t         -> The target audience for the email
    -a         -> Add an option attachment. If specified, path to the attachment and filename must be provided as an argument.
    -e         -> Execute processing.
    -h|-?      -> Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

[ ${#} -eq 0 ] && usage&& RETURN_CODE=${?};

while getopts ":m:f:t:a:eh" OPTIONS 2>/dev/null
do
    METHOD_NAME="${CNAME}#init()";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    case "${OPTIONS}" in
        m)
            ## set the message
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting MESSAGE_TEMPLATE..";

            ## Capture the site root
            MESSAGE_TEMPLATE=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MESSAGE_TEMPLATE -> ${MESSAGE_TEMPLATE}";
            ;;
        f)
            ## set the message
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting FILE_CONTENT..";

            ## Capture the site root
            FILE_CONTENT=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILE_CONTENT -> ${FILE_CONTENT}";
            ;;
        t)
            ## set the message
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting TARGET_AUDIENCE..";

            ## Capture the site root
            typeset -l TARGET_AUDIENCE="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_AUDIENCE -> ${TARGET_AUDIENCE}";
            ;;
        a)
            ## set the message
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ATTACH_FILE..";

            ## Capture the site root
            [ ! -z "${OPTARG}" ] && ATTACH_FILE=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ATTACH_FILE -> ${ATTACH_FILE}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${RETURN_CODE}" ] || [ ${RETURN_CODE} -eq 0 ]
            then
                if [ -z "${MESSAGE_TEMPLATE}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No message template was provided. Cannot continue.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=1;
                elif [ -z "${TARGET_AUDIENCE}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target audience was provided. Cannot continue.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=1;
                else
                    ## We have enough information to process the request, continue
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    sendNotificationEmail && RETURN_CODE=${?};
                fi
            fi
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage&& RETURN_CODE=${?};
            ;;
    esac
done

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

[ -z "${RETURN_CODE}" ] && return 1 || return "${RETURN_CODE}";
