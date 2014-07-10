#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  execute_zone_change.sh
#         USAGE:  ./execute_zone_change.sh
#   DESCRIPTION:  Adds and updates various indicators utilized by named, as
#                 well as adding auditory information.
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
CNAME="$(/usr/bin/env basename ${0})";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; /usr/bin/env echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname ${SCRIPT_ABSOLUTE_PATH})";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[[ -z "${PLUGIN_ROOT_DIR}" && -f ${SCRIPT_ROOT}/../plugin ]] && . ${SCRIPT_ROOT}/../plugin;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${PLUGIN_ROOT_DIR}" ] && /usr/bin/env echo "Failed to locate configuration data. Cannot continue." && return 1;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

THIS_CNAME="${CNAME}";
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

## validate the input
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

CNAME="${THIS_CNAME}";
METHOD_NAME="${THIS_CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
then
    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security violation found while executing ${CNAME} by ${IUSER_AUDIT} on host ${SYSTEM_HOSTNAME}";
    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security configuration blocks execution. Please verify security configuration.";

    echo "Security configuration does not allow the requested action.";

    return ${RET_CODE};
fi

unset RET_CODE;
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

${APP_ROOT}/${LIB_DIRECTORY}/lock.sh lock ${$};
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

CNAME="${THIS_CNAME}";
METHOD_NAME="${THIS_CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

[ ${RET_CODE} -ne 0 ] && echo "Application currently in use." && echo ${RET_CODE} && return ${RET_CODE};

unset RET_CODE;

#===  FUNCTION  ===============================================================
#          NAME:  install_zone
#   DESCRIPTION:  Searches for and replaces "AUDIT" indicators for the provided
#                 filename.
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function remove_master_zone
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Performing decommission of ${ZONE_NAME}";

    ## set up our zonefile name
    ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}).${PROJECT_CODE};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE_NAME -> ${ZONEFILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating that requested directories/files exist..";

    ## need to make sure the provided information actually exists in the install
    if [ -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT} ] &&
        [ -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${ZONEFILE_NAME} ] &&
        [ -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}) ] &&
        [ -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}) ]
    then
        ## take our backups
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backing up files..";

        ## take backups
        tar cf ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar \
            -C ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR} \
            ${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}).${PROJECT_CODE} \
            ${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}) \
            ${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}) >> /dev/null 2>&1;
        gzip ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar >> /dev/null 2>&1;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete. Verifying..";

        ## make sure the backup tarball exists. if it doesnt, dont continue
        if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar.gz ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup verified. Continuing..";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing files..";

            rm -rf ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}).${PROJECT_CODE};
            rm -rf ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME});
            rm -rf ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME})

            ## make sure they were indeed removed
            if [ ! -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}).${PROJECT_CODE} ] &&
                [ ! -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}) ] &&
                [ ! -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}) ]
            then
                ## find out if there are any other files in the directory. if none, kill it.
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checking for remaining files in ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}";

                if [ $(find ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT} -type f \
                    -echo | wc -l) -eq 0 ]
                then
                    ## ok, we didnt find any files, so we can remove the directory
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No files found, removing..";

                    rm -rf ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT};
                fi

                ## okay theyre out. now we can remove the entry from the decom conf file
                if [ -s ${NAMED_ROOT}/${NAMED_CONF_DIR}/${DECOM_CONF_FILE} ]
                then
                    ## it does. take a backup..
                    cp ${NAMED_ROOT}/${NAMED_CONF_DIR}/${DECOM_CONF_FILE} ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${DECOM_CONF_FILE}.${CHANGE_NUM};

                    if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${DECOM_CONF_FILE}.${CHANGE_NUM} ]
                    then
                        ## backup complete.
                        ## remove entry. get the line numbers we need
                        START_LINE_NUMBER=$(sed -n "/zone \"${ZONE_NAME}\" IN {/=" ${NAMED_ROOT}/${NAMED_CONF_DIR}/${DECOM_CONF_FILE});
                        END_LINE_NUMBER=$(expr ${START_LINE_NUMBER} + 5);

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "START_LINE_NUMBER -> ${START_LINE_NUMBER}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "END_LINE_NUMBER -> ${END_LINE_NUMBER}";

                        sed -e "${START_LINE_NUMBER},${END_LINE_NUMBER} d" ${NAMED_ROOT}/${NAMED_CONF_DIR}/${DECOM_CONF_FILE} >> ${PLUGIN_WORK_DIRECTORY}/${DECOM_CONF_FILE};

                        ## ok, should now be removed from the tmp file we've created. validate.
                        if [ $(grep -c "zone \"${ZONE_NAME}\" IN {" ${PLUGIN_WORK_DIRECTORY}/${DECOM_CONF_FILE}) -eq 0 ]
                        then
                            ## verified removal. lets make this file the active file and call it a day.
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moving ${PLUGIN_WORK_DIRECTORY}/${DECOM_CONF_FILE} to ${NAMED_ROOT}/${NAMED_CONF_DIR}/${DECOM_CONF_FILE}..";

                            ## checksum tmp file
                            CONF_TMP_CKSUM=$(cksum ${PLUGIN_WORK_DIRECTORY}/${DECOM_CONF_FILE} | awk '{print $1}');

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONF_TMP_CKSUM -> ${CONF_TMP_CKSUM}";

                            mv ${PLUGIN_WORK_DIRECTORY}/${DECOM_CONF_FILE} ${NAMED_ROOT}/${NAMED_CONF_DIR}/${DECOM_CONF_FILE};

                            ## checksum operational
                            CONF_OP_CKSUM=$(cksum ${NAMED_ROOT}/${NAMED_CONF_DIR}/${DECOM_CONF_FILE} | awk '{print $1}');

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONF_OP_CKSUM -> ${CONF_OP_CKSUM}";

                            if [ ${CONF_TMP_CKSUM} -eq ${CONF_OP_CKSUM} ]
                            then
                                ## we're done here.
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removal of ${ZONE_NAME} from ${NAMED_ROOT}/${NAMED_CONF_DIR}/${DECOM_CONF_FILE} complete.";
                                ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_NAME} removed by ${REQUESTING_USER} per change ${CHANGE_NUM} on $(date +"%m-%d-%Y") at $(date +"%H:%M:%S")";

                                RETURN_CODE=0;
                            else
                                ## checksums dont match. this probably means that the file didnt move into place properly.
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum mismatch against business unit configuration file. Validation failed.";

                                RETURN_CODE=71;
                            fi
                        else
                            ## removal of the zone from the biz conf file didn't work for some reason. return an "ERROR"
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to validate that zone has been removed from the business unit configuration.";

                            RETURN_CODE=71;
                        fi
                    else
                        ## couldnt create the backup file
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create configuration backup. Cannot continue.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        RETURN_CODE=57;
                    fi
                else
                    ## biz conf file doesnt exist ?!
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Business Unit configuration file does not exist. Please verify that the zone name provided is accurate.";

                    RETURN_CODE=14;
                fi
            else
                ## zone files werent removed
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create zone configuration backup. Cannot continue.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=71;
            fi
        else
            ## failed to create a backup of the existing configuration
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to remove zone files. Cannot continue.";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=57;
        fi
    else
        ## we were provided information necessary to perform the work, but the information given doesnt map to
        ## actual files/directories on the filesystem. cant operate with nothing to operate against
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create zone configuration backup. Cannot continue.";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=12;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset END_LINE_NUMBER;
    unset START_LINE_NUMBER;
    unset ZONEFILE_NAME;
    unset CONF_TMP_CKSUM;
    unset CONF_OP_CKSUM;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  decom_slave_zone
#   DESCRIPTION:  Searches for and replaces "AUDIT" indicators for the provided
#                 filename.
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function remove_slave_zone
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Performing decommission of ${ZONE_NAME}";

    ## set up our zonefile name
    ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}).${PROJECT_CODE};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE_NAME -> ${ZONEFILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating that requested directories/files exist..";

    ## need to make sure the provided information actually exists in the install
    if [ -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT} ] &&
        [ -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${ZONEFILE_NAME} ] &&
        [ -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}) ] &&
        [ -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}) ]
    then
        ## take our backups
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backing up files..";

        ## take backups
        tar cf ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar \
            -C ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT} \
            ${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}).${PROJECT_CODE} \
            ${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}) \
            ${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}) >> /dev/null 2>&1;
        gzip ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar >> /dev/null 2>&1;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete. Verifying..";

        ## make sure the backup tarball exists. if it doesnt, dont continue
        if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar.gz ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup verified. Continuing..";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing files..";

            rm -rf ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}).${PROJECT_CODE};
            rm -rf ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME});
            rm -rf ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME});

            ## make sure they were indeed removed
            if [ ! -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}).${PROJECT_CODE} ] &&
                [ ! -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}) ] &&
                [ ! -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}) ]
            then
                ## find out if there are any other files in the directory. if none, kill it.
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checking for remaining files in ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT}";

                if [ $(find ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT} -type f \
                    -echo | wc -l) -eq 0 ]
                then
                    ## ok, we didnt find any files, so we can remove the directory
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No files found, removing..";

                    rm -rf ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR}/${GROUP_ID}${BUSINESS_UNIT};
                fi

                ## okay theyre out. now we can remove the entry from the decom conf file
                if [ -s ${NAMED_ROOT}/${NAMED_CONF_DIR}/${DECOM_CONF_FILE} ]
                then
                    ## it does. take a backup..
                    cp ${NAMED_ROOT}/${DECOM_CONF_FILE} ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${DECOM_CONF_FILE}.${CHANGE_NUM};

                    if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${DECOM_CONF_FILE}.${CHANGE_NUM} ]
                    then
                        ## backup complete.
                        ## remove entry. get the line numbers we need
                        START_LINE_NUMBER=$(sed -n "/zone \"${ZONE_NAME}\" IN {/=" ${NAMED_ROOT}/${NAMED_CONF_DIR}/${DECOM_CONF_FILE});
                        END_LINE_NUMBER=$(expr ${START_LINE_NUMBER} + 5);

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "START_LINE_NUMBER -> ${START_LINE_NUMBER}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "END_LINE_NUMBER -> ${END_LINE_NUMBER}";

                        sed -e "${START_LINE_NUMBER},${END_LINE_NUMBER} d" ${NAMED_ROOT}/${NAMED_CONF_DIR}/${DECOM_CONF_FILE} >> ${PLUGIN_WORK_DIRECTORY}/${DECOM_CONF_FILE};

                        ## ok, should now be removed from the tmp file we've created. validate.
                        if [ $(grep -c "zone \"${ZONE_NAME}\" IN {" ${PLUGIN_WORK_DIRECTORY}/${DECOM_CONF_FILE}) -eq 0 ]
                        then
                            ## verified removal. lets make this file the active file and call it a day.
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moving ${PLUGIN_WORK_DIRECTORY}/${DECOM_CONF_FILE} to ${NAMED_ROOT}/${NAMED_CONF_DIR}/${DECOM_CONF_FILE}..";

                            ## checksum tmp file
                            CONF_TMP_CKSUM=$(cksum ${PLUGIN_WORK_DIRECTORY}/${DECOM_CONF_FILE} | awk '{print $1}');

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONF_TMP_CKSUM -> ${CONF_TMP_CKSUM}";

                            mv ${PLUGIN_WORK_DIRECTORY}/${DECOM_CONF_FILE} ${NAMED_ROOT}/${NAMED_CONF_DIR}/${DECOM_CONF_FILE};

                            ## checksum operational
                            CONF_OP_CKSUM=$(cksum ${NAMED_ROOT}/${NAMED_CONF_DIR}/${DECOM_CONF_FILE} | awk '{print $1}');

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONF_OP_CKSUM -> ${CONF_OP_CKSUM}";

                            if [ ${CONF_TMP_CKSUM} -eq ${CONF_OP_CKSUM} ]
                            then
                                ## we're done here.
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removal of ${ZONE_NAME} from ${NAMED_ROOT}/${NAMED_CONF_DIR}/$(tr "[A-Z]" "[a-z]" <<< ${BUSINESS_UNIT}).${NAMED_ZONE_CONF_NAME}) complete.";
                                ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_NAME} removed by ${REQUESTING_USER} per change ${CHANGE_NUM} on $(date +"%m-%d-%Y") at $(date +"%H:%M:%S")";

                                RETURN_CODE=0;
                            else
                                ## checksums dont match. this probably means that the file didnt move into place properly.
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum mismatch against business unit configuration file. Validation failed.";

                                RETURN_CODE=71;
                            fi
                        else
                            ## removal of the zone from the biz conf file didn't work for some reason. return an "ERROR"
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to validate that zone has been removed from the business unit configuration.";

                            RETURN_CODE=71;
                        fi
                    else
                        ## couldnt create the backup file
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create configuration backup. Cannot continue.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        RETURN_CODE=57;
                    fi
                else
                    ## biz conf file doesnt exist ?!
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Business Unit configuration file does not exist. Please verify that the zone name provided is accurate.";

                    RETURN_CODE=14;
                fi
            else
                ## zone files werent removed
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create zone configuration backup. Cannot continue.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=71;
            fi
        else
            ## failed to create a backup of the existing configuration
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to remove zone files. Cannot continue.";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=57;
        fi
    else
        ## we were provided information necessary to perform the work, but the information given doesnt map to
        ## actual files/directories on the filesystem. cant operate with nothing to operate against
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create zone configuration backup. Cannot continue.";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=12;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset END_LINE_NUMBER;
    unset START_LINE_NUMBER;
    unset ZONEFILE_NAME;
    unset CONF_TMP_CKSUM;
    unset CONF_OP_CKSUM;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  remove_zone_entry
#   DESCRIPTION:  Removes a selected entry from a provided zone file
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function remove_zone_entry
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Performing removal of ${ZONE_ENTRY} from ${ZONE_NAME}..";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating that requested directories/files exist..";

    CHANGE_DATE=$(date +"%m-%d-%Y");
    TARFILE_NAME=${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar.gz;
    ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(cut -d "." -f 1 <<< ${ZONE_NAME}).${PROJECT_CODE};
    SITE_ROOT=${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT};

    ## need to make sure the provided information actually exists in the install
    if [ -d ${SITE_ROOT} ]
    then
        ## take our backups
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backing up zone files..";

        (cd ${SITE_ROOT}; tar cf - ${ZONEFILE_NAME} ${PRIMARY_DC}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}) \
            ${SECONDARY_DC}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME})) | gzip -c > ${NAMED_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete. Validating..";

        if [ -s ${NAMED_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME} ]
        then
            ## ok, we've backed up the zonefiles required for operation. make
            ## a copy to work on
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup validated. Creating working copies..";

            ## cut all our copies at once
            ## we need to update the serial number, so lets do it here
            cp ${SITE_ROOT}/${ZONEFILE_NAME} ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME} > /dev/null 2>&1;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding "AUDIT" indicators..";

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

            ## validate the input
            addServiceIndicators -r ${GROUP_ID}${BUSINESS_UNIT} -f ${ZONEFILE_NAME} -t $(grep "Currently live in" ${SITE_ROOT}/${ZONEFILE_NAME} | awk '{print $5}') -i ${REQUESTING_USER} -c ${CHANGE_NUM} -e;
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            ## make sure our ret code is zero. if its not, we
            ## can keep going, but the change wont load
            if [ ${RET_CODE} -ne 0 ]
            then
                ## it isnt. issue out a warning
                ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Return code from addServiceIndicators non-zero. Processing will continue, but changes will not be loaded.";

                WARNING_CODE=31;
            fi

            cp ${SITE_ROOT}/${PRIMARY_DC}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}) \
                ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME}.${PRIMARY_DC} > /dev/null 2>&1;
            cp ${SITE_ROOT}/${SECONDARY_DC}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}) \
                ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME}.${SECONDARY_DC} > /dev/null 2>&1;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Working copies created. Validating..";

            ## and make sure it exists
            if [ -s ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME} ] \
                && [ -s ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME}.${PRIMARY_DC} ] \
                && [ -s ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME}.${SECONDARY_DC} ]
            then
                ## we have a working file. we're going to get some "INFO"
                ## and then move forward.
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Working copy validated. Gathering data..";

                ## get the line count of the file. we'll use this in part to validate
                ## the entry was removed as well as to validate that everything else
                ## wasnt
                CURRENT_LINE_COUNT=$(wc -l ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME} | awk '{print $1}');

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CURRENT_LINE_COUNT -> ${CURRENT_LINE_COUNT}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing entry ${ZONE_ENTRY}..";

                TMPFILE=$(mktemp);

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMPFILE -> ${TMPFILE}";

                ## ok, lets go forward and remove the entry
                ## update the serial number too, otherwise this
                ## is really kinda pointless
                sed -e "/${ZONE_ENTRY}/d" ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME} > ${TMPFILE};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${ZONE_ENTRY} removed. Validating..";

                ## make sure tmp file was created and has content
                if [ -s ${${TMPFILE}} ]
                then
                    ## removal complete, rename
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Temporary file successfully created. Renaming..";

                    mv ${TMPFILE} ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Rename complete. Validating..";

                    ## make sure the move worked
                    if [ -s ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME} ]
                    then
                        ## it did. validate that the entry was indeed removed
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Rename validated. Validating removal..";

                        REMOVAL_LINE_COUNT=$((( CURRENT_LINE_COUNT -= 1 )));

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REMOVAL_LINE_COUNT->${REMOVAL_LINE_COUNT}";

                        ## validate that the removal line count matches the current line count (post-removal) and
                        ## that the entry doesnt exist in the zone anymore
                        if [ $(wc -l ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME} | awk '{print $1}') -eq ${REMOVAL_LINE_COUNT} ] \
                            && [ $(grep -c ${ZONE_ENTRY} ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME}) -eq 0 ]
                        then
                            ## it indeed was. we can now move forward to the dc-specifics and then
                            ## copy everything in
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removal validation completed. Removing entry from datacenter zones..";

                            for DATACENTER in ${PRIMARY_DC} ${SECONDARY_DC}
                            do
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on ${DATACENTER}..";

                                ## get the line count of the file. we'll use this in part to validate
                                ## the entry was removed as well as to validate that everything else
                                ## wasnt
                                CURRENT_LINE_COUNT=$(wc -l ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME}.${DATACENTER} | awk '{print $1}');

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CURRENT_LINE_COUNT -> ${CURRENT_LINE_COUNT}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing entry ${ZONE_ENTRY}..";

                                ## ok, lets go forward and remove the entry
                                TMP
                                sed -e "/${ZONE_ENTRY}/d" ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME}.${DATACENTER} > ${TMPFILE};

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${ZONE_ENTRY} removed. Validating..";

                                ## make sure tmp file was created and has content
                                if [ -s ${TMPFILE} ]
                                then
                                    ## removal complete, rename
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Temporary file successfully created. Renaming..";

                                    mv ${TMPFILE} ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME}.${DATACENTER};

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Rename complete. Validating..";

                                    ## make sure the move worked
                                    if [ -s ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME}.${DATACENTER} ]
                                    then
                                        ## it did. validate that the entry was indeed removed
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Rename validated. Validating removal..";

                                        REMOVAL_LINE_COUNT=$((( CURRENT_LINE_COUNT -= 1 )));

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REMOVAL_LINE_COUNT->${REMOVAL_LINE_COUNT}";

                                        ## validate that the removal line count matches the current line count (post-removal) and
                                        ## that the entry doesnt exist in the zone anymore
                                        if [ $(wc -l ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME}.${DATACENTER} | awk '{print $1}') -eq ${REMOVAL_LINE_COUNT} ] \
                                            && [ $(grep -c ${ZONE_ENTRY} ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME}.${DATACENTER}) -eq 0 ]
                                        then
                                            ## it indeed was. we can now move forward to the dc-specifics and then
                                            ## copy everything in
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removal validation completed. Continuing..";
                                        else
                                            ## entry did not get removed. die.
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while removing ${ZONE_ENTRY} from ${ZONEFILE_NAME}.${DATACENTER}. Please perform manually.";

                                            (( ERROR_COUNT += 1 ));
                                        fi
                                    else
                                        ## didnt move the file back or the moved file was empty.
                                        ## die.
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while removing ${ZONE_ENTRY} from ${ZONEFILE_NAME}.${DATACENTER}. Please perform manually.";

                                        (( ERROR_COUNT += 1 ));
                                    fi
                                else
                                    ## tmp file wasnt created or was empty. would seem nothing got removed
                                    ## or everything got removed. either way this is bad.
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while removing ${ZONE_ENTRY} from ${ZONEFILE_NAME}.${DATACENTER}. Please perform manually.";

                                    (( ERROR_COUNT += 1 ));
                                fi
                            done

                            if [ ${ERROR_COUNT} -ne 0 ]
                            then
                                ## "WARN" - something happened with the dc zones.
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: Failed to update datacenter-specific zonefiles. Please update manually.";

                                WARNING_CODE=86;
                            fi

                            ## copy the files in place and then reload
                            ## with the changes
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "All changes successfully applied. Re-locating files..";

                            TMP_FILE_CKSUM=$(cksum ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME} | awk '{print $1}');

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_FILE_CKSUM->${TMP_FILE_CKSUM}";

                            ## we have our checksums, lets move in files
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moving file ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME} ..";

                            mv ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME} ${SITE_ROOT}/${ZONEFILE_NAME};

                            ## move complete. validate.
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Move complete. Validating..";

                            OP_FILE_CKSUM=$(cksum ${SITE_ROOT}/${ZONEFILE_NAME} | awk '{print $1}');

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_FILE_CKSUM->${OP_FILE_CKSUM}";

                            if [ ${TMP_FILE_CKSUM} -eq ${OP_FILE_CKSUM} ]
                            then
                                ## move complete and verified. move in the dc files
                                unset DATACENTER;
                                unset TMP_FILE_CKSUM;
                                unset OP_FILE_CKSUM;

                                for DATACENTER in ${PRIMARY_DC} ${SECONDARY_DC}
                                do
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moving file ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME}.${DATACENTER} ..";

                                    TMP_FILE_CKSUM=$(cksum ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME}.${DATACENTER} | awk '{print $1}');

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_FILE_CKSUM -> ${TMP_FILE_CKSUM}";

                                    mv ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${ZONEFILE_NAME}.${DATACENTER} ${SITE_ROOT}/${DATACENTER}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME});

                                    ## move complete. validate.
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Move complete. Validating..";

                                    OP_FILE_CKSUM=$(cksum ${SITE_ROOT}/${DATACENTER}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}) | awk '{print $1}');

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_FILE_CKSUM->${OP_FILE_CKSUM}";

                                    if [ ${TMP_FILE_CKSUM} -eq ${OP_FILE_CKSUM} ]
                                    then
                                        ## successful.
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${ZONEFILE_NAME} for ${DATACENTER} has been updated.";
                                    else
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while moving ${ZONEFILE_NAME} to ${DATACENTER}. Please perform manually.";

                                        (( ERROR_COUNT += 1 ));
                                    fi
                                done

                                unset TMP_FILE_CKSUM;
                                unset OP_FILE_CKSUM;
                                unset DATACENTER;

                                if [ ${ERROR_COUNT} -ne 0 ]
                                then
                                    ## an "ERROR" occurred copying one or more dc zones.
                                    ## this isnt good but isnt fatal either. "WARN".
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: Failed to update datacenter-specific zonefiles. Please update manually.";

                                    WARNING_CODE=86;
                                fi

                                ## this completes our excersize. reload changes.
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Changes complete. Now reloading zone..";

                                ## reload on master first, if its good, then continue
                                if [ "${SPLIT_HORIZON}" = "${_TRUE}" ]
                                then
                                    for HORIZON in ${HORIZONS}
                                    do
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HORIZON -> ${HORIZON}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${NAMED_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${NAMED_MASTER} -p ${RNDC_LOCAL_PORT} -y ${RNDC_LOCAL_KEY} -c reload -z "${ZONE_NAME}" -i ${HORIZON} -e";

                                                                unset METHOD_NAME;
                                        unset CNAME;

                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                                        ## validate the input
                                        ${NAMED_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${NAMED_MASTER} -p ${RNDC_LOCAL_PORT} -y ${RNDC_LOCAL_KEY} -c reload -z "${ZONE_NAME}" -i ${HORIZON} -e;
                                        typeset -i RET_CODE=${?};

                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                        CNAME="${THIS_CNAME}";
                                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                                    done
                                else
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${NAMED_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${NAMED_MASTER} -p ${RNDC_LOCAL_PORT} -y ${RNDC_LOCAL_KEY} -c reload -z "${ZONE_NAME}" -e";

                                                        unset METHOD_NAME;
                                    unset CNAME;

                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                                    ## validate the input
                                    ${NAMED_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${NAMED_MASTER} -p ${RNDC_LOCAL_PORT} -y ${RNDC_LOCAL_KEY} -c reload -z "${ZONE_NAME}" -e;
                                    typeset -i RET_CODE=${?};

                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                    CNAME="${THIS_CNAME}";
                                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                                fi

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE->${RET_CODE}";

                                if [ ${RET_CODE} -eq 0 ]
                                then
                                    ## xlnt. we've reloaded. continue forward.
                                    ## the reload does the flush for us, so we
                                    ## dont have to go back and do it again.
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${NAMED_MASTER} successfully reloaded change. Validating..";

                                    ## validate the removal. run a dig for the entry
                                    LOOKUP_RESPONSE=$(dig @${NAMED_MASTER} +short -t a ${ZONE_ENTRY});

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LOOKUP_RESPONSE->${LOOKUP_RESPONSE}";

                                    if [ -z "${LOOKUP_RESPONSE}" ]
                                    then
                                        ## xlnt, removed. continue with slave zones - just reload here, we dont
                                        ## really need to validate that it was removed. although i guess we could.

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reload complete and removal validated. Reloading slaves..";

                                        for SLAVE in ${DNS_SLAVES[@]}
                                        do
                                            if [ "${SPLIT_HORIZON}" = "${_TRUE}" ]
                                            then
                                                for HORIZON in ${HORIZONS}
                                                do
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HORIZON -> ${HORIZON}";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${NAMED_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${SLAVE} -p ${RNDC_REMOTE_PORT} -y ${RNDC_REMOTE_KEY} -c reload -z "${ZONE_NAME}" -i ${HORIZON} -e";

                                                                                        unset METHOD_NAME;
                                                    unset CNAME;

                                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                                                    ## validate the input
                                                    ${NAMED_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${SLAVE} -p ${RNDC_REMOTE_PORT} -y ${RNDC_REMOTE_KEY} -c reload -z "${ZONE_NAME}" -i ${HORIZON} -e;
                                                    typeset -i RET_CODE=${?};

                                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                                    CNAME="${THIS_CNAME}";
                                                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                                    if [ ${RET_CODE} -ne 0 ]
                                                    then
                                                        ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: Failed to initiate service reload on ${SLAVE}. Please update manually.";

                                                        RETURN_CODE=86;
                                                    fi
                                                done
                                            else
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${NAMED_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${SLAVE} -p ${RNDC_REMOTE_PORT} -y ${RNDC_REMOTE_KEY} -c reload -z "${ZONE_NAME}" -e";

                                                                                unset METHOD_NAME;
                                                unset CNAME;

                                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                                                ## validate the input
                                                ${NAMED_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${SLAVE} -p ${RNDC_REMOTE_PORT} -y ${RNDC_REMOTE_KEY} -c reload -z "${ZONE_NAME}" -e;
                                                typeset -i RET_CODE=${?};

                                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                                CNAME="${THIS_CNAME}";
                                                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                                if [ ${RET_CODE} -ne 0 ]
                                                then
                                                    ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: Failed to initiate service reload on ${SLAVE}. Please update manually.";

                                                    RETURN_CODE=86;
                                                fi
                                            fi
                                        done

                                        ## and this completes.
                                        if [ -z "${RETURN_CODE}" ]
                                        then
                                            RETURN_CODE=0;
                                        else
                                            if [ ! -z "${WARNING_CODE}" ]
                                            then
                                                RETURN_CODE=${WARNING_CODE};
                                            else
                                                RETURN_CODE=${RETURN_CODE};
                                            fi
                                        fi
                                    else
                                        ## reload failed on the master. "ERROR" out
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Entry remains on ${NAMED_MASTER}. Please confirm removal and continue manually. Cannot continue.";

                                        RETURN_CODE=92;
                                    fi
                                else
                                    ## reload failed. since everything else is done, this isnt horrible,
                                    ## but it does mean that we cant run the reload against the slaves
                                    ## because the master doesnt have it and doesnt know.
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service reload on ${NAMED_MASTER} FAILED. Cannot continue.";

                                    RETURN_CODE=92;
                                fi
                            else
                                ## operational zonefile failed to copy.
                                ## cant continue from this.
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to copy temporary file to operational file. Cannot continue.";

                                RETURN_CODE=68;
                            fi
                        else
                            ## entry did not get removed. die.
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to copy temporary file to operational file. Cannot continue.";

                            RETURN_CODE=68;
                        fi
                    else
                        ## didnt move the file back or the moved file was empty.
                        ## die.
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create zone configuration temp file. Cannot continue.";

                        RETURN_CODE=47;
                    fi
                else
                    ## tmp file wasnt created or was empty. would seem nothing got removed
                    ## or everything got removed. either way this is bad.
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create zone configuration temp file. Cannot continue.";

                    RETURN_CODE=47;
                fi
            else
                ## couldnt make a working copy. cant continue without.
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create zone configuration temp file. Cannot continue.";

                RETURN_CODE=47;
            fi
        else
            ## no backup
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create zone configuration backup. Cannot continue.";

            RETURN_CODE=57;
        fi
    else
        ## we were provided information necessary to perform the work, but the information given doesnt map to
        ## actual files/directories on the filesystem. cant operate with nothing to operate against
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to locate neccessary operating directories. Cannot continue.";

        RETURN_CODE=10;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    ERROR_COUNT=0;
    [ -f ${TMPFILE} ] && rm -rf ${TMPFILE};

    unset TMPFILE;
    unset TMP_FILE_CKSUM;
    unset OP_FILE_CKSUM;
    unset DATACENTER;
    unset REMOVAL_LINE_COUNT;
    unset CURRENT_LINE_COUNT;
    unset CHANGE_DATE;
    unset TARFILE_NAME;
    unset ZONEFILE_NAME;
    unset SITE_ROOT;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
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

    echo "${CNAME} - Execute modifications against a zone\n";
    echo "Usage: ${CNAME} [ -b <business unit> ] [ -p <project code> ] [ -z <zone name> ] [ -i <requesting user> ] [ -c <change request> ] [ -r <entry> ] [ -x ] [ -s ] [ -e ] [ -h|-? ]
    -b         -> The associated business unit
    -p         -> The associated project code
    -z         -> The zone name, eg example.com
    -i         -> The user performing the request
    -c         -> The change order associated with this request
    -r         -> Remove an entry from an existing zone. Entry name is required.
    -x         -> Remove a previously decommissioned zone.
    -s         -> Specifies whether or not to operate against a slave server
    -e         -> Execute processing
    -h|-?      -> Show this help\n";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

[ ${#} -eq 0 ] && usage&& RETURN_CODE=${?};

while getopts ":b:p:z:i:c:r:xseh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        b)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting BUSINESS_UNIT..";

            ## Capture the site root
            typeset -u BUSINESS_UNIT="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
            ;;
        p)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting PROJECT_CODE..";

            ## Capture the site root
            typeset -u PROJECT_CODE="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROJECT_CODE -> ${PROJECT_CODE}";
            ;;
        z)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ZONE_NAME..";

            ## Capture the site root
            ZONE_NAME=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_NAME -> ${ZONE_NAME}";
            ;;
        i)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting REQUESTING_USER..";

            ## Capture the change control
            REQUESTING_USER="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUESTING_USER -> ${REQUESTING_USER}";
            ;;
        c)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            ## Capture the change control
            typeset -u CHANGE_NUM="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        r)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting REMOVE_ENTRY to TRUE..";

            ## Capture the change control
            REMOVE_ENTRY=${_TRUE};
            typeset -l ZONE_ENTRY="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REMOVE_ENTRY -> ${REMOVE_ENTRY}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_ENTRY -> ${ZONE_ENTRY}";
            ;;
        x)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting REMOVE_ZONE to TRUE..";

            ## Capture the change control
            REMOVE_ZONE=${_TRUE};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REMOVE_ZONE -> ${REMOVE_ZONE}";
            ;;
        s)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SLAVE_OPERATION to true..";

            ## Capture the change control
            SLAVE_OPERATION=${_TRUE};
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${BUSINESS_UNIT}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=15;
            elif [ -z "${PROJECT_CODE}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=24;
            elif [ -z "${ZONE_NAME}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=24;
            elif [ -z "${CHANGE_NUM}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change order was provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=17;
            elif [ -z "${REQUESTING_USER}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requestors username was not provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=20;
            else
                if [ ! -z "${REMOVE_ENTRY}" ] && [ "${REMOVE_ENTRY}" = "${_TRUE}" ]
                then
                    if [ -z "${ZONE_ENTRY}" ]
                    then
                        ## we were asked to remove a zone entry but werent provided one.
                        ## oops.
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requestors username was not provided. Unable to continue processing.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        RETURN_CODE=29;
                    else
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        remove_zone_entry&& RETURN_CODE=${?};
                    fi
                elif [ ! -z "${REMOVE_ZONE}" ] && [ "${REMOVE_ZONE}" = "${_TRUE}" ]
                then
                    ## remove the zone entirely. we check for slave_operation
                    ## because config files need to be modified, and those changes
                    ## need to be performed on slaves too
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    [ ! -z "${SLAVE_OPERATION}" ] && [ "${SLAVE_OPERATION}" = "${_TRUE}" ] && remove_slave_zone && RETURN_CODE=${?} || remove_master_zone&& RETURN_CODE=${?};
                else
                    ## we didn't get a performance argument. since we dont know what action to take,
                    ## we fail here.
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validation failed. Terminating.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=67;
                fi
            fi
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage&& RETURN_CODE=${?};
            ;;
    esac
done

trap "${APP_ROOT}/${LIB_DIRECTORY}/lock.sh unlock ${$}; return ${RETURN_CODE}" INT TERM EXIT;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

[ -z "${RETURN_CODE}" ] && echo "1" || echo "${RETURN_CODE}";
[ -z "${RETURN_CODE}" ] && return 1 || return "${RETURN_CODE}";
