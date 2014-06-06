#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  generateEntropy.sh
#         USAGE:  ./generateEntropy.sh (create|renew)
#   DESCRIPTION:  Generates a random character file for use with certutil and
#                 other utilities requiring pseudo-entropy. This is meant to run
#                 as a cron job, rotating out the existing entropy file every so
#                 often.
#
#       OPTIONS:  ---
#  REQUIREMENTS:  ---
#          BUGS:  ---
#         NOTES:  ---
#        AUTHOR:  Kevin Huntly <kmhuntly@gmail.com
#       COMPANY:  CaspersBox Web Services
#       VERSION:  1.0
#       CREATED:  ---
#      REVISION:  ---
#==============================================================================

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

## Application constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ -z "${APP_ROOT}" && ! -s ${SCRIPT_ROOT}/../lib/constants.sh ]] && echo "Failed to locate configuration data. Cannot continue." && exit 1;
[ -z "${APP_ROOT}" ] && . ${SCRIPT_ROOT}/../lib/constants.sh;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the usage of this application
#    PARAMETERS:  None
#       RETURNS:  0
#===========================================================================
function generateEntropyFile
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTROPY_FILE -> ${ENTROPY_FILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RANDOM_GENERATOR -> ${RANDOM_GENERATOR}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTROPY_FILE_SIZE -> ${ENTROPY_FILE_SIZE}";

    ENTROPY_FILE_NAME=${ENTROPY_FILE##*/};
    ENTROPY_FILE_PATH=${ENTROPY_FILE%/*};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTROPY_FILE_PATH -> ${ENTROPY_FILE_PATH}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTROPY_FILE_NAME -> ${ENTROPY_FILE_NAME}";

    if [ -s ${APP_ROOT}/${ENTROPY_FILE} ]
    then
        if [ ! -z $(find ${HOME}/${ENTROPY_FILE_PATH} -type f -name ${ENTROPY_FILE_NAME} -mtime +30) ]
        then
            ENTROPY_BACKUP_TIMESTAMP=$(date +"%m-%d-%Y");

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTROPY_BACKUP_TIMESTAMP -> ${ENTROPY_BACKUP_TIMESTAMP}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating backup file..";

            cp ${APP_ROOT}/${ENTROPY_FILE} ${APP_ROOT}/${ENTROPY_FILE}.${ENTROPY_BACKUP_TIMESTAMP} > /dev/null 2>&1;
            
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete. Continuing..";

            unset METHOD_NAME;

            createEntropyFile;
            RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            local METHOD_NAME="${CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ ${RET_CODE} -eq 0 ]
            then
                RETURN_CODE=0;
            else
                RETURN_CODE=1;
            fi
        fi
    else
        unset METHOD_NAME;

        createEntropyFile;
        RET_CODE=${?};

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
        local METHOD_NAME="${CNAME}#${0}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

        if [ ${RET_CODE} -eq 0 ]
        then
            $(${LOGGER} "INFO""${METHOD_NAME}" "${CNAME}" "${LINENO}" "Entropy file successfully generated.");

            RETURN_CODE=0;
        else
            $(${LOGGER} "INFO""${METHOD_NAME}" "${CNAME}" "${LINENO}" "Entropy file generation failed.");

            RETURN_CODE=1;
        fi
    fi

    unset ENTROPY_BACKUP_TIMESTAMP;
    unset ENTROPY_FILE_NAME;
    unset ENTROPY_FILE_PATH;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#      NAME:  usage
#   DESCRIPTION:  Provide information on the usage of this application
#    PARAMETERS:  None
#   RETURNS:  0
#===========================================================================
function createEntropyFile
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTROPY_FILE -> ${ENTROPY_FILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RANDOM_GENERATOR -> ${RANDOM_GENERATOR}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTROPY_FILE_SIZE -> ${ENTROPY_FILE_SIZE}";

    while read -r RANDOM_DATA
    do
        while true
        do
            echo ${RANDOM_DATA} >> ${APP_ROOT}/${ENTROPY_FILE};

            FILE_SIZE=$(wc -c ${APP_ROOT}/${ENTROPY_FILE} | awk '{print $1}');

            if [ ${FILE_SIZE} -ge ${ENTROPY_FILE_SIZE} ]
            then
                GENERATION_COMPLETE=${_TRUE};

                break;
            fi
        done

        if [ ! -z "${GENERATION_COMPLETE}" ] && [ "${GENERATION_COMPLETE}" = "${_TRUE}" ]
        then
            ## generation complete
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Entropy generation complete. Removing backup file..";

            if [ -s ${APP_ROOT}/${ENTROPY_FILE} ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing backup file..";

                rm ${APP_ROOT}/${ENTROPY_FILE}.${ENTROPY_BACKUP_TIMESTAMP} > /dev/null 2>&1;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup file removed. Process complete.";
            else
                ## file wasn't properly generated
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Entropy file generation has failed. Please try again.";

                RETURN_CODE=1;
            fi

            ## chmod appropriately
            chmod 600 ${APP_ROOT}/${ENTROPY_FILE};

            RETURN_CODE=0;

            break;
        fi
    done < ${RANDOM_GENERATOR};

    unset GENERATION_COMPLETE;
    unset FILE_SIZE;
    unset RANDOM_DATA;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

generateEntropyFile;

exit ${RETURN_CODE};
