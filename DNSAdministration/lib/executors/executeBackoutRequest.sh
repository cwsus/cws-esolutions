#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  execute_backout.sh
#         USAGE:  ./execute_backout.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Backs out a previously processed DNS change request.
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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

## Application constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; printf "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[[ -z "${PLUGIN_ROOT_DIR}" && -f ${SCRIPT_ROOT}/../lib/plugin.sh ]] && . ${SCRIPT_ROOT}/../lib/plugin.sh;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${PLUGIN_ROOT_DIR}" ] && print "Failed to locate configuration data. Cannot continue." && exit 1;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

THIS_CNAME="${CNAME}";
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

## validate the input
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

CNAME="${THIS_CNAME}";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
then
    print "Security configuration does not allow the requested action.";

    return ${RET_CODE};
fi

unset RET_CODE;
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

${APP_ROOT}/${LIB_DIRECTORY}/lock.sh lock ${$};
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

CNAME="${THIS_CNAME}";
METHOD_NAME="${THIS_CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

[ ${RET_CODE} -ne 0 ] && print "Application currently in use." && print ${RET_CODE} && exit ${RET_CODE};

unset RET_CODE;

CNAME="$(basename "${0}")";
METHOD_NAME="${CNAME}#startup";

trap "${APP_ROOT}/${LIB_DIRECTORY}/lock.sh unlock ${$}; return" INT TERM EXIT;

#===  FUNCTION  ===============================================================
#          NAME:  retrieve_file_list
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function retrieve_file_list
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILE_NAME -> ${FILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_DATE -> ${CHANGE_DATE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Retrieving backout files..";

    if [ -d ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY} ]
    then
        ## see if theres an available backup file
        set -A FILE_LIST $(find ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY} -type f -name "*.tar.gz");

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Search complete. Processing..";

        if [ ${#FILE_LIST[@]} -eq 0 ]
        then
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No backup files were found. Please try again.";

            RETURN_CODE=12;
        else
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Multiple backout files located.. listing.";

            ## check if the list exists, if it does, clear it
            [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_LIST} ] && cat /dev/null > ${PLUGIN_ROOT_DIR}/${BACKUP_LIST};

            ## found more than 1 backup file. we need to get clarification
            ## before we can continue - so we'll write out the list to a
            ## to a file to send back to the UI
            while [ ${A} -ne ${#FILE_LIST[@]} ]
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILE_LIST->${FILE_LIST[${A}]}";

                printf ${FILE_LIST[${A}]} >> ${PLUGIN_ROOT_DIR}/${BACKUP_LIST};

                (( A += 1 ));
            done

            ## reset the counter
            A=0;

            RETURN_CODE=0;
        fi
    else
        ## configured backup directory doesnt exist,
        ## cant continue
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The configured backup directory does not exist. Please verify application configuration.";

        RETURN_CODE=11;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  backout_change
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function backout_change
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILE_NAME -> ${FILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_DATE -> ${CHANGE_DATE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checking for available backup file...";

    if [ -d ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY} ]
    then
        if [ -z "${FILE_NAME}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No FILE_NAME was provided. Searching for files..";

            ## see if theres an available backup file
            set -A FILE_LIST $(find ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY} -type f -name "${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_DATE}*.${CHANGE_NUM}.*");

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Search complete. Processing..";

            if [ ${#FILE_LIST[@]} -eq 0 ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No backup files were found based on the provided criteria. Please try again.";

                RETURN_CODE=12;
            else
                if [ ${#FILE_LIST[@]} -gt 1 ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Multiple backout files located.. listing.";

                    ## found more than 1 backup file. we need to get clarification
                    ## before we can continue - so we'll write out the list to a
                    ## to a file to send back to the UI
                    while [ ${A} -ne ${#FILE_LIST[@]} ]
                    do
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILE_LIST->${FILE_LIST[${A}]}";

                        printf ${FILE_LIST[${A}]} >> ${PLUGIN_ROOT_DIR}/${BACKUP_LIST};

                        (( A += 1 ));
                    done

                    ## reset the counter
                    A=0;

                    RETURN_CODE=27;
                else
                    ## we found 1 file, so lets process the backout
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing backout file ${FILE_LIST[${A}]}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting TAR_FILE..";

                    ## get the tar file nice
                    TAR_FILE=$(printf ${FILE_LIST[${A}]} | cut -d "." -f 0-6);

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TAR_FILE->${TAR_FILE}";

                    ## unzip/untar
                    gunzip ${FILE_LIST[${A}]};
                    tar xf ${TAR_FILE};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backout complete.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "gzipping ${TAR_FILE}..";

                    ## gzip it again so we can use it again
                    gzip ${TAR_FILE};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing ${FILE_LIST[${A}]} complete.";

                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backout processed by ${REQUESTING_USER}: filename: ${FILE_LIST[${A}]}";

                    ## unset variables
                    unset TAR_FILE;
                    set -A FILE_LIST;
                    unset BUSINESS_UNIT;
                    unset CHANGE_NUM;
                    unset CHANGE_DATE;

                    ## backout complete, return 0
                    RETURN_CODE=0;
                fi
            fi
        else
            ## got a filename to operate against.
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing backout file ${FILE_NAME}";

            BIZ_UNIT=$(printf ${FILE_NAME} | cut -d "." -f 1);
            CHG_NUM=$(printf ${FILE_NAME} | cut -d "." -f 2);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BIZ_UNIT -> ${BIZ_UNIT}";

            ## copy the backup tarfile to the tmp directory to work on
            cp ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${FILE_NAME} ${PLUGIN_TMP_DIRECTORY}/${FILE_NAME};

            ## make sure the file copied
            if [ -s ${PLUGIN_TMP_DIRECTORY}/${FILE_NAME} ]
            then
                ## ok, we have it. decompress it -
                gzip -dc ${PLUGIN_TMP_DIRECTORY}/${FILE_NAME} | (cd ${PLUGIN_TMP_DIRECTORY}; tar xf -)

                ## and make sure it was indeed decompressed...
                if [ -d ${PLUGIN_TMP_DIRECTORY}/${BIZ_UNIT} ]
                then
                    ## we can be pretty confident that it was indeed extracted. move forward.
                    ## make a backup of the existing files just in case we need them for some reason
                    SITE_ROOT=${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BIZ_UNIT};

                    ## make sure our site root exists. if it doesnt we'll handle this differently.
                    if [ -d ${SITE_ROOT} ]
                    then
                        TARFILE_DATE=$(date +"%m-%d-%Y");
                        BACKUP_FILE=${GROUP_ID}${BIZ_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${REQUESTING_USER};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BIZ_UNIT -> ${BIZ_UNIT}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARFILE_DATE -> ${TARFILE_DATE}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_ROOT -> ${SITE_ROOT}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BACKUP_FILE -> ${BACKUP_FILE}";

                        ## we dont know what the tarfile contains, so backup everything
                        gzip ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar | (cd ${SITE_ROOT}; tar cf * > /dev/null 2>&1);

                        ## make sure that it did indeed get created
                        if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar.gz ]
                        then
                            ## it did, lets keep going.
                            ## copy, not move, the files into place
                            ## make sure error_count is zero
                            ERROR_COUNT=0;

                            for BACKUP_FILE in $(find ${PLUGIN_TMP_DIRECTORY}/${BIZ_UNIT} -type f -name "db.*" -print)
                            do
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on ${BACKUP_FILE}";

                                ## copy it...
                                cp ${BACKUP_FILE} ${SITE_ROOT}/${BACKUP_FILE};

                                ## checksum it...
                                BACKUP_FILE_CKSUM=$(cksum ${BACKUP_FILE} | awk '{print $1}');
                                OP_FILE_CKSUM=$(cksum ${SITE_ROOT}/${BACKUP_FILE} | awk '{print $1}');

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BACKUP_FILE_CKSUM -> ${BACKUP_FILE_CKSUM}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_FILE_CKSUM -> ${OP_FILE_CKSUM}";

                                if [ ${BACKUP_FILE_CKSUM} -ne ${OP_FILE_CKSUM} ]
                                then
                                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${BACKUP_FILE} successfully restored by ${REQUESTING_USER} on ${TARFILE_DATE} at $(date +"%H:%M:%S").";
                                else
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred restoring backup file ${BACKUP_FILE}.";
                                    (( ERROR_COUNT += 1 ));
                                fi
                            done

                            unset BACKUP_FILE;

                            ## our files should be copied. lets make sure.
                            if [ ${ERROR_COUNT} -eq 0 ]
                            then
                                ## success! we've "AUDIT" logged the file restorations as they happened, so we have nothing further to do here.
                                RETURN_CODE=0;
                            else
                                ## one or more files failed to restore. we are going to fail the entire process and undo what we've done.
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more files failed to restore. Reverting to pristine state..";

                                ## first, kill off everything in our tmp directory so as not to cloud issues.
                                rm -rf ${PLUGIN_TMP_DIRECTORY}/${BIZ_UNIT};
                                rm -rf ${PLUGIN_TMP_DIRECTORY}/${FILE_NAME};

                                ## ok. we should be clean now. start the process of reversion
                                cp ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar.gz ${PLUGIN_TMP_DIRECTORY}/${BACKUP_FILE}.tar.gz

                                ## copy complete, now untar and move the stuff back where it belongs
                                gzip -dc ${PLUGIN_TMP_DIRECTORY}/${BACKUP_FILE}.tar.gz | (cd ${PLUGIN_TMP_DIRECTORY}; tar xf -);

                                ## we're unzipped and untarred. make sure
                                if [ -d ${PLUGIN_TMP_DIRECTORY}/${BIZ_UNIT} ]
                                then
                                    ## we're good here. run the copies.
                                    ERROR_COUNT=0;

                                    for BACKOUT_FILE in $(find ${PLUGIN_TMP_DIRECTORY}/${BIZ_UNIT} -type f -name "db.*" -print)
                                    do
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on ${BACKOUT_FILE}";

                                        ## copy it...
                                        cp ${BACKOUT_FILE} ${SITE_ROOT}/${BACKOUT_FILE};

                                        ## checksum it...
                                        BACKOUT_FILE_CKSUM=$(cksum ${BACKOUT_FILE} | awk '{print $1}');
                                        OP_FILE_CKSUM=$(cksum ${SITE_ROOT}/${BACKOUT_FILE} | awk '{print $1}');

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BACKOUT_FILE_CKSUM -> ${BACKOUT_FILE_CKSUM}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_FILE_CKSUM -> ${OP_FILE_CKSUM}";

                                        if [ ${BACKOUT_FILE_CKSUM} -ne ${OP_FILE_CKSUM} ]
                                        then
                                            ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${BACKOUT_FILE} successfully restored by ${REQUESTING_USER} on ${TARFILE_DATE} at $(date +"%H:%M:%S").";
                                        else
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred restoring backup file ${BACKOUT_FILE}.";
                                            (( ERROR_COUNT += 1 ));
                                        fi
                                    done

                                    unset BACKOUT_FILE;

                                    ## make sure it worked
                                    if [ ${ERROR_COUNT} -eq 0 ]
                                    then
                                        ## it did. restoration of backup files failed, but we did recover from the failure.
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backout processing has failed, but recovery completed successfully.";

                                        RETURN_CODE=72;
                                    else
                                        ## recovery efforts have failed..
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to recover from file restoration. Please recover manually.";

                                        RETURN_CODE=73;
                                    fi
                                else
                                    ## recovery efforts have failed.. failed to ensure that the backup tarball we took to recover
                                    ## from actually got untarred
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to decompress backup of existing configuration data. Cannot continue.";

                                    RETURN_CODE=73;
                                fi
                            fi
                        else
                            ## failed to verify that a backup of the current configuration was created. fail out
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to perform backup of existing configuration.";

                            RETURN_CODE=57;
                        fi
                    else
                        ## heres the problem: site root doesnt exist. this means that the zone .conf file probably
                        ## doesnt exist either. and it probably isnt in named either. we should check to see if it
                        ## exists, if it doesnt, we need to build it, if it does, well, hopefully it has everything...
                        ## our site root doesnt exist. we're gonna skip taking a backup of the existing files
                        ## because there arent any and then move the contents of the backup to the named zone root
                        ## make the directory
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating ${SITE_ROOT}..";

                        mkdir ${SITE_ROOT};
                        mkdir ${SITE_ROOT}/${PRIMARY_DC};
                        mkdir ${SITE_ROOT}/${SECONDARY_DC};

                        ## make sure it was indeed created
                        if [ -d ${SITE_ROOT} ] && [ -d ${SITE_ROOT}/${PRIMARY_DC} ] && [ -d ${SITE_ROOT}/${SECONDARY_DC} ]
                        then
                            ## it was. keep going
                            ## copy, not move, the files into place
                            ## make sure error_count is zero
                            ERROR_COUNT=0;

                            for BACKUP_FILE in $(find ${PLUGIN_TMP_DIRECTORY}/${BIZ_UNIT} -type f -name "db.*" -print)
                            do
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on ${BACKUP_FILE}";

                                ## copy it...
                                cp ${BACKUP_FILE} ${SITE_ROOT}/${BACKUP_FILE};

                                ## checksum it...
                                BACKUP_FILE_CKSUM=$(cksum ${BACKUP_FILE} | awk '{print $1}');
                                OP_FILE_CKSUM=$(cksum ${SITE_ROOT}/${BACKUP_FILE} | awk '{print $1}');

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BACKUP_FILE_CKSUM -> ${BACKUP_FILE_CKSUM}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_FILE_CKSUM -> ${OP_FILE_CKSUM}";

                                if [ ${BACKUP_FILE_CKSUM} -ne ${OP_FILE_CKSUM} ]
                                then
                                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${BACKUP_FILE} successfully restored by ${REQUESTING_USER} on ${TARFILE_DATE} at $(date +"%H:%M:%S").";
                                else
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred restoring backup file ${BACKUP_FILE}.";
                                    (( ERROR_COUNT += 1 ));
                                fi
                            done

                            unset BACKUP_FILE;

                            ## our files should be copied. lets make sure.
                            if [ ${ERROR_COUNT} -eq 0 ]
                            then
                                ## success! we've "AUDIT" logged the file restorations as they happened, so all we need to do is build the zone conf file
                                ## find out if we have a conf file
                                if [ -s $(printf ${BIZ_UNIT} "[A-Z]" "[a-z]").${NAMED_ZONE_CONF_NAME}.${CHG_NUM} ]
                                then
                                    ## wooo, we've already got a file to use. this is pretty straight-forward, copy it in
                                    ## and $include it into named.conf
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying existing files..";

                                    cp ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/$(printf ${BIZ_UNIT} "[A-Z]" "[a-z]").${NAMED_ZONE_CONF_NAME}.${CHG_NUM} \
                                        ${NAMED_ROOT}/${NAMED_CONF_DIR}/$(printf ${BIZ_UNIT} "[A-Z]" "[a-z]").${NAMED_ZONE_CONF_NAME};

                                    ## ok, copy should be done
                                    if [ -s ${NAMED_ROOT}/${NAMED_CONF_DIR}/$(printf ${BIZ_UNIT} "[A-Z]" "[a-z]").${NAMED_ZONE_CONF_NAME} ]
                                    then
                                        ## we've copied, now we need to $include
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copy complete. Including configuration file..";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Including configuration file..";

                                        print "include \"/${NAMED_ROOT}/${NAMED_CONF_DIR}/$(printf ${BIZ_UNIT} "[A-Z]" "[a-z]").${NAMED_ZONE_CONF_NAME}\";" >> ${NAMED_CONF_FILE};

                                        ## should have our new zone included now. verify it
                                        if [ $(grep -c ${NAMED_ROOT}/${NAMED_CONF_DIR}/$(printf ${BIZ_UNIT} "[A-Z]" "[a-z]").${NAMED_ZONE_CONF_NAME} ${NAMED_CONF_FILE}) -eq 1 ]
                                        then
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printed include statement to ${NAMED_CONF_FILE}";

                                            ## "AUDIT" log
                                            ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_NAME} restored by ${REQUESTING_USER} per change ${CHANGE_NUM} on $(date +"%m-%d-%Y") at $(date +"%H:%M:%S")";

                                            ## and finally return zero
                                            RETURN_CODE=0;
                                        else
                                            ## the new zone wasnt added to named.conf
                                            ## we send back an "ERROR" code informing
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to add new zone to named core configuration. Please process manually.";

                                            RETURN_CODE=34;
                                        fi
                                    else
                                        ## failed to copy the backup zone config file.
                                        ## we can re-build from scratch, or "ERROR" out
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a backup of the zone configuration. Cannot continue.";

                                        RETURN_CODE=14;
                                    fi
                                else
                                    ## drat. we dont already have a file. now we need to build one.
                                    ## get our filenames
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Obtaining list of zonefiles from tarball..";

                                    ZONE_LIST=$(tar tvf ${FILE_NAME} | awk '{print $6}' | cut -d "/" -f 2 | grep -v "[PV]H");
                                    ZONE_CONF_NAME=$(printf ${BIZ_UNIT} "[A-Z]" "[a-z]").${NAMED_ZONE_CONF_NAME};

                                    for ZONE in ${ZONE_LIST}
                                    do
                                        ## we should have a list of zones. we need to build a zone conf file.
                                        ZONE_NAME=$(printf ${ZONE} | cut -d "." -f 2 | tru '-' '.');

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Placing ${ZONE_NAME} into config file..";

                                        print "zone \"${ZONE_NAME}\" IN {" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                                        print "    type         master;" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                                        print "    file         \"${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.$(printf ${ZONE_NAME} | cut -d "." -f 1).$(printf ${PROJECT_CODE} | tr "[a-z]" "[A-Z]")\";" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                                        print "    allow-update { none; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                                        print "};\n" >> ${NAMED_ROOT}/${NAME_CONF_DIR}/${ZONE_CONF_NAME};

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Placement complete..";
                                    done

                                    ## ok, so the zone config file shouldve been built. lets see if we have the same number of
                                    ## zone entries as we do in the tar file
                                    if [ $(grep -c "zone" ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME}) -eq ${#ZONE_LIST[@]} ]
                                    then
                                        ## we do. write out the $include entry
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Decompression and zone configuration successfully built. Adding into primary config file..";

                                        print "include \"/${NAMED_CONF_DIR}/${ZONE_CONF_NAME}\";" >> ${NAMED_CONF_FILE};

                                        ## make sure it was printed..
                                        if [ $(grep -c ${ZONE_CONF_NAME} ${NAMED_CONF_FILE}) -eq 1 ]
                                        then
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printed include statement to ${NAMED_CONF_FILE}";

                                            ## and finally return zero
                                            RETURN_CODE=0;
                                        else
                                            ## include entry didnt print. this is all that remains. we "ERROR" out and inform
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to include the new zone information into the core configuration. Cannot continue.";

                                            RETURN_CODE=76;
                                        fi
                                    else
                                        ## somewhere along the road something didnt work right. we dont know exactly
                                        ## what went wrong or whats missing. we have no choice but to fail out the
                                        ## process here.
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to properly update the zone configuration file. Cannot continue.";

                                        RETURN_CODE=76;
                                    fi
                                fi
                            else
                                ## theres nothing to recover from here because theres nothing to back out TO. the site root
                                ## didnt exist to begin with. i guess we'll just rm the files and call it a day.
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred restoring backup file ${BACKUP_FILE}.";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backing out changes..";

                                rm -rf ${SITE_ROOT};

                                RETURN_CODE=73;
                            fi
                        else
                            ## failed to create site root. we cant recover from this.
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create ${SITE_ROOT}. Unable to continue..";

                            RETURN_CODE=75;
                        fi
                    fi
                else
                    ## decompression of the backup file failed. cant really do anything
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to decompress the requested backout file. Unable to continue..";

                    RETURN_CODE=74;
                fi
            else
                ## unable to copy tarball. either it doesnt exist or something else happened.
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to create copy of backup file. Cannot continue.";

                RETURN_CODE=14;
            fi
        fi
    else
        ## configured backup directory doesnt exist,
        ## cant continue
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The configured backup directory does not exist. Please verify application configuration.";

        ## unset variables
        unset TAR_FILE;
        set -A FILE_LIST;
        unset BUSINESS_UNIT;
        unset CHANGE_NUM;
        unset CHANGE_DATE;

        RETURN_CODE=11;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Baackout a previously executed change request";
    print "Usage:  ${CNAME} [-a] [-f filename (optional)] [-b business unit] [-c change control] [-d date] [-e <execute>] [-h] [-?]";
    print "  -a      Retrieve a list of all available backout files";
    print "  -f      The file name to process (optional)";
    print "  -b      The business unit that will be failed over";
    print "  -c      The change control associated with the request to be backed out";
    print "  -d      The date of the change";
    print "  -e      Execute the request";
    print "  -h|-?   Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 3;
}

[ ${#} -eq 0 ] && usage;

while getopts ":af:b:c:d:eh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        a)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            retrieve_file_list;
            ;;
        f)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting FILE_NAME..";

            ## we were provided with a filename. process it
            if [ -z "${OPTARG}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No file name was provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=25;
            else
                FILE_NAME="${OPTARG}"; # This will be the BU to move

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILE_NAME -> ${FILE_NAME}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                backout_change;
            fi
            ;;
        b)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting BUSINESS_UNIT..";

            ## Capture the business unit
            typeset -u BUSINESS_UNIT="${OPTARG}"; # This will be the BU to move

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
            ;;
        c)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            ## Capture the change control
            typeset -u CHANGE_NUM="${OPTARG}"; # This will be the project code to move

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        d)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_DATE..";

            ## Capture the "AUDIT" userid
            CHANGE_DATE="${OPTARG}"; # This will be the target datacenter to move to

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_DATE -> ${CHANGE_DATE}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${BUSINESS_UNIT}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No business unit was provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=15;
            elif [ -z "${CHANGE_NUM}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change order was provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=19;
            elif [ -z "${CHANGE_DATE}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change date was provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=26;
            else
                ## We have enough information to process the request, continue
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                backout_change;
            fi
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done


printf ${RETURN_CODE};

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset THIS_CNAME;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return ${RETURN_CODE};
