#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  updateExceptions.sh
#         USAGE:  ./updateExceptions.sh monitor-name
#   DESCRIPTION:  Executes the requested monitoring script across the known web
#                 servers.
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
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; printf "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#          NAME:  addStartupException
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function addStartupException
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing previous temporary files..";

    EXCEPTION_FILE_NAME=$(printf ${CORE_EXCEPTION_LIST} | cut -d "/" -f 2);
    TMP_EXCEPTION_FILE=${APP_ROOT}/${TMP_DIRECTORY}/${EXCEPTION_FILE_NAME};
    NEW_EXCEPTION_FILE=${APP_ROOT}/${TMP_DIRECTORY}/${EXCEPTION_FILE_NAME}.new;

    ## remove tmp and patch files if they exist
    [ -f ${TMP_EXCEPTION_FILE} ] && rm -rf ${TMP_EXCEPTION_FILE};
    [ -f ${NEW_EXCEPTION_FILE} ] && rm -rf ${NEW_EXCEPTION_FILE};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Cleanup complete. Continuing..";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EXCEPTION_FILE_NAME -> ${EXCEPTION_FILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_EXCEPTION_FILE -> ${TMP_EXCEPTION_FILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NEW_EXCEPTION_FILE -> ${NEW_EXCEPTION_FILE}";

    ## populate our tmp file with the current contents
    cat ${APP_ROOT}/${CORE_EXCEPTION_LIST} > ${TMP_EXCEPTION_FILE};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Population complete. Validating..";

    if [ -f ${TMP_EXCEPTION_FILE} ]
    then
        ## file exists, continue
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validation complete. Adding new entry..";

        if [ ! -z "${1}" ]
        then
            for SITE_ENTRY in ${1}
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding ${SITE_ENTRY}..";

                ## get the site "INFO"
                WEB_PROJECT_CODE=$(getWebInfo | grep -w ${SITE_ENTRY} | grep -v "#" | \
                    cut -d "|" -f 1 | cut -d ":" -f 2 | sort | uniq); ## get the webcode

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_PROJECT_CODE -> ${WEB_PROJECT_CODE}";

                if [ ! -z "${WEB_PROJECT_CODE}" ]
                then
                    [ -z "$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
                        cut -d "|" -f 10 | sort | uniq | grep enterprise)" ] \
                        && WEBSERVER_PLATFORM=${IHS_TYPE_IDENTIFIER} \
                        || WEBSERVER_PLATFORM=${IPLANET_TYPE_IDENTIFIER};

                    case ${WEBSERVER_PLATFORM} in
                        ${IPLANET_TYPE_IDENTIFIER})
                            INSTANCE_NAME=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
                                cut -d "|" -f 10 | cut -d "/" -f 4 | sort | uniq); ## web instance name
                            ;;
                        ${IHS_TYPE_IDENTIFIER})
                            INSTANCE_NAME=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
                                cut -d "|" -f 10 | cut -d "/" -f 5 | sort | uniq); ## web instance name
                            ;;
                    esac

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "INSTANCE_NAME -> ${INSTANCE_NAME}";

                    if [ ! -z "${INSTANCE_NAME}" ]
                    then
                        if [ $(grep -c ${INSTANCE_NAME} ${APP_ROOT}/${CORE_EXCEPTION_LIST}) -eq 0 ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding ${SITE_ENTRY} / ${INSTANCE_NAME} to ${CORE_EXCEPTION_LIST} ..";

                            print ${INSTANCE_NAME} >> ${TMP_EXCEPTION_FILE};

                            if [ $(grep -c ${INSTANCE_NAME} ${TMP_EXCEPTION_FILE}) -eq 0 ]
                            then
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to add ${SITE_ENTRY}, ${INSTANCE_NAME} to exception file";

                                (( ERROR_COUNT += 1 ));
                            fi
                        else
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No server root was found for ${SITE_ENTRY}, ${WEB_PROJECT_CODE}";

                            (( ERROR_COUNT += 1 ));
                        fi
                    else
                        ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_ENTRY} / ${INSTANCE_NAME} has already been added to the exception list. Skipping.";
                    fi
                else
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No configuration information was found for ${SITE_ENTRY}.";

                    (( ERROR_COUNT += 1 ));
                fi
            done
        else
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding ${SITE_HOSTNAME}..";

            ## get the site "INFO"
            WEB_PROJECT_CODE=$(getWebInfo | grep -w ${SITE_HOSTNAME} | grep -v "#" | \
                cut -d "|" -f 1 | cut -d ":" -f 2 | sort | uniq); ## get the webcode

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_PROJECT_CODE -> ${WEB_PROJECT_CODE}";

            if [ ! -z "${WEB_PROJECT_CODE}" ]
            then
                [ -z "$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
                    cut -d "|" -f 10 | sort | uniq | grep enterprise)" ] \
                    && WEBSERVER_PLATFORM=${IHS_TYPE_IDENTIFIER} \
                    || WEBSERVER_PLATFORM=${IPLANET_TYPE_IDENTIFIER};

                case ${WEBSERVER_PLATFORM} in
                    ${IPLANET_TYPE_IDENTIFIER})
                        INSTANCE_NAME=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
                            cut -d "|" -f 10 | cut -d "/" -f 4 | sort | uniq); ## web instance name
                        ;;
                    ${IHS_TYPE_IDENTIFIER})
                        INSTANCE_NAME=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
                            cut -d "|" -f 10 | cut -d "/" -f 5 | sort | uniq); ## web instance name
                        ;;
                esac

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "INSTANCE_NAME -> ${INSTANCE_NAME}";

                if [ ! -z "${INSTANCE_NAME}" ]
                then
                    if [ $(grep -c ${INSTANCE_NAME} ${APP_ROOT}/${CORE_EXCEPTION_LIST}) -eq 0 ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding ${SITE_HOSTNAME} / ${INSTANCE_NAME} to ${CORE_EXCEPTION_LIST} ..";

                        print ${INSTANCE_NAME} >> ${TMP_EXCEPTION_FILE};

                        if [ $(grep -c ${INSTANCE_NAME} ${TMP_EXCEPTION_FILE}) -eq 0 ]
                        then
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to add ${SITE_HOSTNAME}, ${INSTANCE_NAME} to exception file";

                            (( ERROR_COUNT += 1 ));
                        fi
                    else
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No server root was found for ${SITE_HOSTNAME}, ${WEB_PROJECT_CODE}";

                        (( ERROR_COUNT += 1 ));
                    fi
                else
                    ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_HOSTNAME} / ${INSTANCE_NAME} has already been added to the exception list. Skipping.";
                fi
            else
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No configuration information was found for ${SITE_HOSTNAME}.";

                (( ERROR_COUNT += 1 ));
            fi
        fi

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ERROR_COUNT -> ${ERROR_COUNT}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "New entry added. Validating..";

        if [ ${ERROR_COUNT} -eq 0 ]
        then
            ## entry added
            ## cksum the copy
            ## backup the existing file..
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backing up configuration..";

            BACKUP_FILENAME=$(printf ${CORE_EXCEPTION_LIST} | cut -d "/" -f 2).$(date +"%m-%d-%Y").${IUSER_AUDIT};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BACKUP_FILENAME -> ${BACKUP_FILENAME}";

            cp -p ${APP_ROOT}/${CORE_EXCEPTION_LIST} ${APP_ROOT}/${BACKUP_DIRECTORY}/${BACKUP_FILENAME} > /dev/null 2>&1;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete. Validating..";

            ## make sure we have a backup..
            if [ -f ${APP_ROOT}/${BACKUP_DIRECTORY}/${BACKUP_FILENAME} ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup validated. Patching files..";

                ## backup complete. checksum the original pre-patch..
                ORIG_CKSUM=$(cksum ${APP_ROOT}/${CORE_EXCEPTION_LIST} | awk '{print $1}');

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ORIG_CKSUM -> ${ORIG_CKSUM}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Performing diff..";

                ## our additions to the file are complete. now we need to patch the existing with the new
                ## first diff..
                diff ${APP_ROOT}/${CORE_EXCEPTION_LIST} \
                    ${TMP_EXCEPTION_FILE} > ${NEW_EXCEPTION_FILE} 2>/dev/null;
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Diff complete. Validating output..";

                if [ ${RET_CODE} -eq 1 ]
                then
                    ## make sure the diff got printed..
                    if [ -s ${NEW_EXCEPTION_FILE} ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing patch..";

                        ## got our diff.. patch..
                        patch ${APP_ROOT}/${CORE_EXCEPTION_LIST} < ${NEW_EXCEPTION_FILE} > /dev/null 2>&1;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        if [ ${RET_CODE} -eq 0 ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Patch complete. Validating..";

                            ## checksum the updates..
                            UPDATE_CKSUM=$(cksum ${NEW_EXCEPTION_FILE} | awk '{print $1}');

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UPDATE_CKSUM -> ${UPDATE_CKSUM}";

                            ## make sure file got updated..
                            if [ ${ORIG_CKSUM} -ne ${UPDATE_CKSUM} ]
                            then
                                ## we can be pretty confident that our updates exist.
                                ## push them out
                                for MONITORED_HOST in $(getMachineInfo | grep -v "^#" | grep WEB | grep sol8 | cut -d "|" -f 1 | sort | uniq; \
                                    getMachineInfo | grep -v "^#" | grep WEB | grep sol9 | cut -d "|" -f 1 | sort | uniq; \
                                    getMachineInfo | grep -v "^#" | grep WEB | grep sol10 | cut -d "|" -f 1 | sort | uniq;)
                                do
                                    if [ $(printf ${SERVER_IGNORE_LIST} | grep -c ${MONITORED_HOST}) -eq 0 ]
                                    then
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating against ${MONITORED_HOST}..";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now validating proxy ${PROXY}..";

                                        $(ping ${MONITORED_HOST} > /dev/null 2>&1);

                                        PING_RCODE=${?}

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                                        if [ ${PING_RCODE} -eq 0 ]
                                        then
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${MONITORED_HOST} \"${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/monitors/${MONITORING_SCRIPT}.sh\" websrv";

                                            ## unset ret code from prior execution
                                            unset RET_CODE;

                                            $(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSCPConnection.exp local-copy ${MONITORED_HOST} ${APP_ROOT}/${CORE_EXCEPTION_LIST} ${REMOTE_APP_ROOT}/${CORE_EXCEPTION_LIST} websrv);

                                            ## make sure it was copied..
                                            REMOTE_CKSUM=$(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${MONITORED_HOST} "cksum ${APP_ROOT}/${CORE_EXCEPTION_LIST}" websrv);

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REMOTE_CKSUM -> ${REMOTE_CKSUM}";

                                            if [ ${REMOTE_CKSUM} -eq ${UPDATE_CKSUM} ]
                                            then
                                                ## successfully copied.
                                                ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SSL_EXCEPTION_LIST} updated: Site: ${SITE_HOSTNAME}; User: ${IUSER_AUDIT}; Server: ${MONITORED_HOST}";
                                            else
                                                ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to update ${SSL_EXCEPTION_LIST} on host ${MONITORED_HOST}.";

                                                (( ERROR_COUNT += 1 ));
                                            fi
                                        else
                                            ## ping test failure
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${MONITORED_HOST} appears unavailable. PING_RCODE -> ${PING_RCODE}";

                                            (( ERROR_COUNT += 1 ));
                                        fi
                                    else
                                        ## server found in exclusion list
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${MONITORED_HOST} was found in exclusion list. Skippiing.";
                                    fi
                                done

                                if [ ${ERROR_COUNT} -eq 0 ]
                                then
                                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SSL_EXCEPTION_LIST} updated: Site: ${SITE_HOSTNAME}; User: ${IUSER_AUDIT}; Server: ${MONITORED_HOST}";

                                    RETURN_CODE=0;
                                else
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more servers failed to update properly. Please update manually.";

                                    RETURN_CODE=46;
                                fi
                            else
                                ## checksum mismatch
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHECKSUM MATCH: ORIG_CKSUM -> ${ORIG_CKSUM}, UPDATE_CKSUM -> ${UPDATE_CKSUM}";

                                RETURN_CODE=43;
                            fi
                        else
                            ## return code from the patch call was non-zero
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Patch processing FAILED. RET_CODE -> ${RET_CODE}";

                            RETURN_CODE=44;
                        fi
                    else
                        ## new exception list is empty
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "New exception list is empty. No differences were applied.";

                        RETURN_CODE=44;
                    fi
                else
                    if [ ${RET_CODE} -eq 0 ]
                    then
                        ## files are the same, no differences
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No differences were found. Cannot continue.";

                        RETURN_CODE=44;
                    else
                        ## bad juju
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while executing diff. RET_CODE -> ${RET_CODE}";

                        RETURN_CODE=44;
                    fi
                fi
            else
                ## backup file creation failed
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a backup of the current configuration. Cannot continue.";

                RETURN_CODE=20;
            fi
        else
            ## entry wasnt added, "ERROR" out
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while adding the new entry. Cannot continue.";

            RETURN_CODE=44;
        fi
    else
        ## copy of exception list doesnt exist
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create working copy of the exception list. Cannot continue.";

        RETURN_CODE=44;
    fi

    ERROR_COUNT=0;
    unset PING_RCODE;
    unset REMOTE_CKSUM;
    unset UPDATE_CKSUM;
    unset RET_CODE;
    unset ORIG_CKSUM;
    unset BACKUP_FILENAME;
    unset NEW_EXCEPTION_FILE;
    unset TMP_EXCEPTION_FILE;
    unset EXCEPTION_FILE_NAME;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

#===  FUNCTION  ===============================================================
#          NAME:  addSSLException
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function addSSLException
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing previous temporary files..";

    EXCEPTION_FILE_NAME=$(printf ${SSL_EXCEPTION_LIST} | cut -d "/" -f 2);
    TMP_EXCEPTION_FILE=${APP_ROOT}/${TMP_DIRECTORY}/${EXCEPTION_FILE_NAME};
    NEW_EXCEPTION_FILE=${APP_ROOT}/${TMP_DIRECTORY}/${EXCEPTION_FILE_NAME}.new;

    ## remove tmp and patch files if they exist
    [ -f ${TMP_EXCEPTION_FILE} ] && rm -rf ${TMP_EXCEPTION_FILE};
    [ -f ${NEW_EXCEPTION_FILE} ] && rm -rf ${NEW_EXCEPTION_FILE};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Cleanup complete. Continuing..";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EXCEPTION_FILE_NAME -> ${EXCEPTION_FILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_EXCEPTION_FILE -> ${TMP_EXCEPTION_FILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NEW_EXCEPTION_FILE -> ${NEW_EXCEPTION_FILE}";

    ## populate our tmp file with the current contents
    cat ${APP_ROOT}/${SSL_EXCEPTION_LIST} > ${TMP_EXCEPTION_FILE};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Population complete. Validating..";

    if [ -f ${TMP_EXCEPTION_FILE} ]
    then
        ## file exists, continue
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validation complete. Adding new entry..";

        if [ ! -z "${1}" ]
        then
            for SITE_ENTRY in ${1}
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding ${SITE_ENTRY}..";

                ## get the site "INFO"
                WEB_PROJECT_CODE=$(getWebInfo | grep -w ${SITE_ENTRY} | grep -v "#" | \
                    cut -d "|" -f 1 | cut -d ":" -f 2 | sort | uniq); ## get the webcode

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_PROJECT_CODE -> ${WEB_PROJECT_CODE}";

                if [ ! -z "${WEB_PROJECT_CODE}" ]
                then
                    [ -z "$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
                        cut -d "|" -f 10 | sort | uniq | grep enterprise)" ] \
                        && WEBSERVER_PLATFORM=${IHS_TYPE_IDENTIFIER} \
                        || WEBSERVER_PLATFORM=${IPLANET_TYPE_IDENTIFIER};

                    case ${WEBSERVER_PLATFORM} in
                        ${IPLANET_TYPE_IDENTIFIER})
                            INSTANCE_NAME=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
                                cut -d "|" -f 10 | cut -d "/" -f 4 | sort | uniq); ## web instance name
                            ;;
                        ${IHS_TYPE_IDENTIFIER})
                            INSTANCE_NAME=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
                                cut -d "|" -f 10 | cut -d "/" -f 5 | sort | uniq); ## web instance name
                            ;;
                    esac

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "INSTANCE_NAME -> ${INSTANCE_NAME}";

                    if [ ! -z "${INSTANCE_NAME}" ]
                    then
                        if [ $(grep -c ${INSTANCE_NAME} ${APP_ROOT}/${SSL_EXCEPTION_LIST}) -eq 0 ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding ${SITE_ENTRY} / ${INSTANCE_NAME} to ${SSL_EXCEPTION_LIST} ..";

                            print ${INSTANCE_NAME} >> ${TMP_EXCEPTION_FILE};

                            if [ $(grep -c ${INSTANCE_NAME} ${TMP_EXCEPTION_FILE}) -eq 0 ]
                            then
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to add ${SITE_ENTRY}, ${INSTANCE_NAME} to exception file";

                                (( ERROR_COUNT += 1 ));
                            fi
                        else
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No server root was found for ${SITE_ENTRY}, ${WEB_PROJECT_CODE}";

                            (( ERROR_COUNT += 1 ));
                        fi
                    else
                        ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_ENTRY} / ${INSTANCE_NAME} has already been added to the exception list. Skipping.";
                    fi
                else
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No configuration information was found for ${SITE_ENTRY}.";

                    (( ERROR_COUNT += 1 ));
                fi
            done
        else
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding ${SITE_HOSTNAME}..";

            ## get the site "INFO"
            WEB_PROJECT_CODE=$(getWebInfo | grep -w ${SITE_HOSTNAME} | grep -v "#" | \
                cut -d "|" -f 1 | cut -d ":" -f 2 | sort | uniq); ## get the webcode

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_PROJECT_CODE -> ${WEB_PROJECT_CODE}";

            if [ ! -z "${WEB_PROJECT_CODE}" ]
            then
                [ -z "$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
                    cut -d "|" -f 10 | sort | uniq | grep enterprise)" ] \
                    && WEBSERVER_PLATFORM=${IHS_TYPE_IDENTIFIER} \
                    || WEBSERVER_PLATFORM=${IPLANET_TYPE_IDENTIFIER};

                case ${WEBSERVER_PLATFORM} in
                    ${IPLANET_TYPE_IDENTIFIER})
                        INSTANCE_NAME=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
                            cut -d "|" -f 10 | cut -d "/" -f 4 | sort | uniq); ## web instance name
                        ;;
                    ${IHS_TYPE_IDENTIFIER})
                        INSTANCE_NAME=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
                            cut -d "|" -f 10 | cut -d "/" -f 5 | sort | uniq); ## web instance name
                        ;;
                esac

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "INSTANCE_NAME -> ${INSTANCE_NAME}";

                if [ ! -z "${INSTANCE_NAME}" ]
                then
                    if [ $(grep -c ${INSTANCE_NAME} ${APP_ROOT}/${SSL_EXCEPTION_LIST}) -eq 0 ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding ${SITE_HOSTNAME} / ${INSTANCE_NAME} to ${SSL_EXCEPTION_LIST} ..";

                        print ${INSTANCE_NAME} >> ${TMP_EXCEPTION_FILE};

                        if [ $(grep -c ${INSTANCE_NAME} ${TMP_EXCEPTION_FILE}) -eq 0 ]
                        then
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to add ${SITE_HOSTNAME}, ${INSTANCE_NAME} to exception file";

                            (( ERROR_COUNT += 1 ));
                        fi
                    else
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No server root was found for ${SITE_HOSTNAME}, ${WEB_PROJECT_CODE}";

                        (( ERROR_COUNT += 1 ));
                    fi
                else
                    ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_HOSTNAME} / ${INSTANCE_NAME} has already been added to the exception list. Skipping.";
                fi
            else
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No configuration information was found for ${SITE_HOSTNAME}.";

                (( ERROR_COUNT += 1 ));
            fi
        fi

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ERROR_COUNT -> ${ERROR_COUNT}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "New entry added. Validating..";

        if [ ${ERROR_COUNT} -eq 0 ]
        then
            ## entry added
            ## cksum the copy
            ## backup the existing file..
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backing up configuration..";

            BACKUP_FILENAME=$(printf ${SSL_EXCEPTION_LIST} | cut -d "/" -f 2).$(date +"%m-%d-%Y").${IUSER_AUDIT};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BACKUP_FILENAME -> ${BACKUP_FILENAME}";

            cp -p ${APP_ROOT}/${SSL_EXCEPTION_LIST} ${APP_ROOT}/${BACKUP_DIRECTORY}/${BACKUP_FILENAME} > /dev/null 2>&1;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete. Validating..";

            ## make sure we have a backup..
            if [ -f ${APP_ROOT}/${BACKUP_DIRECTORY}/${BACKUP_FILENAME} ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup validated. Patching files..";

                ## backup complete. checksum the original pre-patch..
                ORIG_CKSUM=$(cksum ${APP_ROOT}/${SSL_EXCEPTION_LIST} | awk '{print $1}');

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ORIG_CKSUM -> ${ORIG_CKSUM}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Performing diff..";

                ## our additions to the file are complete. now we need to patch the existing with the new
                ## first diff..
                diff ${APP_ROOT}/${SSL_EXCEPTION_LIST} \
                    ${TMP_EXCEPTION_FILE} > ${NEW_EXCEPTION_FILE} 2>/dev/null;
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Diff complete. Validating output..";

                if [ ${RET_CODE} -eq 1 ]
                then
                    ## make sure the diff got printed..
                    if [ -s ${NEW_EXCEPTION_FILE} ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing patch..";

                        ## got our diff.. patch..
                        patch ${APP_ROOT}/${SSL_EXCEPTION_LIST} < ${NEW_EXCEPTION_FILE} > /dev/null 2>&1;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        if [ ${RET_CODE} -eq 0 ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Patch complete. Validating..";

                            ## checksum the updates..
                            UPDATE_CKSUM=$(cksum ${NEW_EXCEPTION_FILE} | awk '{print $1}');

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UPDATE_CKSUM -> ${UPDATE_CKSUM}";

                            ## make sure file got updated..
                            if [ ${ORIG_CKSUM} -ne ${UPDATE_CKSUM} ]
                            then
                                ## we can be pretty confident that our updates exist.
                                ## push them out
                                for MONITORED_HOST in $(getMachineInfo | grep -v "^#" | grep WEB | grep sol8 | cut -d "|" -f 1 | sort | uniq; \
                                    getMachineInfo | grep -v "^#" | grep WEB | grep sol9 | cut -d "|" -f 1 | sort | uniq; \
                                    getMachineInfo | grep -v "^#" | grep WEB | grep sol10 | cut -d "|" -f 1 | sort | uniq;)
                                do
                                    if [ $(printf ${SERVER_IGNORE_LIST} | grep -c ${MONITORED_HOST}) -eq 0 ]
                                    then
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating against ${MONITORED_HOST}..";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now validating proxy ${PROXY}..";

                                        $(ping ${MONITORED_HOST} > /dev/null 2>&1);

                                        PING_RCODE=${?}

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                                        if [ ${PING_RCODE} -eq 0 ]
                                        then
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command run_ssh_connection.exp monitor ${MONITORED_HOST} \"${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/monitors/${MONITORING_SCRIPT}.sh\" websrv";

                                            ## unset ret code from prior execution
                                            unset RET_CODE;

                                            $(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSCPConnection.exp local-copy ${MONITORED_HOST} ${APP_ROOT}/${SSL_EXCEPTION_LIST} ${REMOTE_APP_ROOT}/${SSL_EXCEPTION_LIST} websrv);

                                            ## make sure it was copied..
                                            REMOTE_CKSUM=$(${APP_ROOT}/${LIB_DIRECTORY}/tcl/run_ssh_connection.exp return-data ${MONITORED_HOST} "cksum ${APP_ROOT}/${SSL_EXCEPTION_LIST}" websrv);

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REMOTE_CKSUM -> ${REMOTE_CKSUM}";

                                            if [ ${REMOTE_CKSUM} -eq ${UPDATE_CKSUM} ]
                                            then
                                                ## successfully copied.
                                                ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SSL_EXCEPTION_LIST} updated: Site: ${SITE_HOSTNAME}; User: ${IUSER_AUDIT}; Server: ${MONITORED_HOST}";
                                            else
                                                ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to update ${SSL_EXCEPTION_LIST} on host ${MONITORED_HOST}.";

                                                (( ERROR_COUNT += 1 ));
                                            fi
                                        else
                                            ## ping test failure
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${MONITORED_HOST} appears unavailable. PING_RCODE -> ${PING_RCODE}";

                                            (( ERROR_COUNT += 1 ));
                                        fi
                                    else
                                        ## server found in exclusion list
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${MONITORED_HOST} was found in exclusion list. Skippiing.";
                                    fi
                                done

                                if [ ${ERROR_COUNT} -eq 0 ]
                                then
                                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SSL_EXCEPTION_LIST} updated: Site: ${SITE_HOSTNAME}; User: ${IUSER_AUDIT}; Server: ${MONITORED_HOST}";

                                    RETURN_CODE=0;
                                else
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more servers failed to update properly. Please update manually.";

                                    RETURN_CODE=46;
                                fi
                            else
                                ## checksum mismatch
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHECKSUM MATCH: ORIG_CKSUM -> ${ORIG_CKSUM}, UPDATE_CKSUM -> ${UPDATE_CKSUM}";

                                RETURN_CODE=43;
                            fi
                        else
                            ## return code from the patch call was non-zero
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Patch processing FAILED. RET_CODE -> ${RET_CODE}";

                            RETURN_CODE=44;
                        fi
                    else
                        ## new exception list is empty
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "New exception list is empty. No differences were applied.";

                        RETURN_CODE=44;
                    fi
                else
                    if [ ${RET_CODE} -eq 0 ]
                    then
                        ## files are the same, no differences
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No differences were found. Cannot continue.";

                        RETURN_CODE=44;
                    else
                        ## bad juju
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while executing diff. RET_CODE -> ${RET_CODE}";

                        RETURN_CODE=44;
                    fi
                fi
            else
                ## backup file creation failed
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a backup of the current configuration. Cannot continue.";

                RETURN_CODE=20;
            fi
        else
            ## entry wasnt added, "ERROR" out
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while adding the new entry. Cannot continue.";

            RETURN_CODE=44;
        fi
    else
        ## copy of exception list doesnt exist
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create working copy of the exception list. Cannot continue.";

        RETURN_CODE=44;
    fi

    ERROR_COUNT=0;
    unset PING_RCODE;
    unset REMOTE_CKSUM;
    unset UPDATE_CKSUM;
    unset RET_CODE;
    unset ORIG_CKSUM;
    unset BACKUP_FILENAME;
    unset NEW_EXCEPTION_FILE;
    unset TMP_EXCEPTION_FILE;
    unset EXCEPTION_FILE_NAME;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the usage of this application
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Updates a selected exception list.";
    print "Usage: ${CNAME} <exception list>";
    print "    Valid exceptions lists:";
    print "    ${CORE_EXCEPTION_LIST}";
    print "    ${SSL_EXCEPTION_LIST}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin.sh ]] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

[ ${#} -eq 0 ] && usage;

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";


[ "${1}" = "${SSL_EXCEPTION_LIST}" ] && addSSLException "${2}";
[ "${1}" = "${CORE_EXCEPTION_LIST}" ] && addStartupException "${2}";

return ${RETURN_CODE};

