#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  global_backup.sh
#         USAGE:  ./global_backup.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Performs a backup of the entire named installation.
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
## Application constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#          NAME:  perform_zone_backup
#   DESCRIPTION:  Main entry point for application. Currently, it is configured
#         to run both interactively and non-interactively, however, the
#         non-interactive functionality has not yet been implemented.
#    PARAMETERS:  ${CLI} - determines if the application should run interactively
#   RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function perform_zone_backup
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && typeset -ft $(typeset +f);

    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IS_BACKUP_ENABLED -> ${IS_BACKUP_ENABLED}";

    ## check if backups are enabled. in a production environment, this should always be true
    if [ ! -z "${IS_BACKUP_ENABLED}" && "${IS_BACKUP_ENABLED}" = "${_TRUE}" ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Global backups enabled - processing..");
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Search and remove backups older than ${BACKUP_RETENTION_TIME} days...");

        ## clean up the old backup files per the configured retention period...
        for BACKUP_FILE in $(find ${APP_ROOT}/${BACKUP_DIRECTORY} -name "*${ZONE_BACKUP_PREFIX}*" -mtime +${BACKUP_RETENTION_TIME} -print)
        do
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} AUDIT $METHOD_NAME} ${CNAME} ${LINENO} "Removing backup file ${BACKUP_FILE}..");

            ## and remove it
            rm -rf ${BACKUP_FILE};
        done

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Cleanup complete. Continuing..");

        TARFILE_NAME=$(echo ${BACKUP_FILE_NAME} | sed -e "s/%TYPE%/${ZONE_BACKUP_PREFIX}/" -e "s/%SERVER_NAME%/$(uname -n)/" -e "s/%DATE%/$(date +"%m-%d-%Y_%H:%M:%S")/");

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "TARFILE_NAME -> ${TARFILE_NAME}");
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Creating backup file..");

        ## we back up everything. we can check if we're on a master/slave but that doesnt take into
        ## account systems that are acting as both a master AND slave, or systems that have dynamic
        ## zones. we exclude any journal files, and tar it up.
        (cd ${NAMED_ROOT}/${NAMED_ZONE_DIR}; tar cf ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME}.tar --exclude='*.jnl' *);

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Archive created. gzipping..");

        ## then gzip it.
        gzip ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME}.tar;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Backup complete. Verifying..");

        if [ -s ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME}.tar.gz ]
        then
            ## our backup is complete. set permissions accordingly...
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Backup successfully verified. Modifying permissions..");

            ## chmod 644 and chown named:named.
            chmod 644 ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME}.tar.gz;
            chown ${NAMED_USER}:${NAMED_GROUP} ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME}.tar.gz;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete - exiting.";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=0;
        else
            ## backup failed. send a fault code
            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to verify backup tarfile generation.";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=57;
        fi
    else
        ## no backups enabled. send a notification
        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backups not currently enabled on $(uname -n). Unable to continue.";

        RETURN_CODE=88;
    fi

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  perform_master_backup
#   DESCRIPTION:  Main entry point for application. Currently, it is configured
#         to run both interactively and non-interactively, however, the
#         non-interactive functionality has not yet been implemented.
#    PARAMETERS:  ${CLI} - determines if the application should run interactively
#   RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function perform_master_backup
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && typeset -ft $(typeset +f);

    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    ## check if backups are enabled. in a production environment, this should always be true
    TARFILE_NAME=$(echo ${BACKUP_FILE_NAME} | sed -e "s/%TYPE%/${ZONE_BACKUP_PREFIX}/" -e "s/%SERVER_NAME%/$(uname -n)/" -e "s/%DATE%/$(date +"%m-%d-%Y")/");

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "TARFILE_NAME -> ${TARFILE_NAME}");
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Creating backup file..");

    ## we back up everything. we can check if we're on a master/slave but that doesnt take into
    ## account systems that are acting as both a master AND slave, or systems that have dynamic
    ## zones. we exclude any journal files, and tar it up.
    (cd ${NAMED_ROOT}/${NAMED_ZONE_DIR}; tar cf ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME}.tar --exclude='*.jnl' *);

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Archive created. gzipping..");

    ## then gzip it.
    gzip ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME}.tar;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Backup complete. Verifying..");

    if [ -s ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME}.tar.gz ]
    then
        ## our backup is complete. set permissions accordingly...
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Backup successfully verified. Modifying permissions..");

        ## chmod 644 and chown named:named.
        chmod 644 ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME}.tar.gz;
        chown ${NAMED_USER}:${NAMED_GROUP} ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME}.tar.gz;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete - exiting.";

        RETURN_CODE=0;
    else
        ## backup failed. send a fault code
        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to verify backup tarfile generation.";

        RETURN_CODE=57;
    fi

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  perform_conf_backup
#   DESCRIPTION:  Implements functionality to perform two distinct backup types:
#
#                 Zone backups
#                 Configuration backups
#
#                 Can be utilized as a cron job to perform these tasks on a
#                 schedule, or can be invoked manually specifying a backup type
#    PARAMETERS:  none
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function perform_conf_backup
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IS_BACKUP_ENABLED -> ${IS_BACKUP_ENABLED}";

    ## check if backups are enabled. in a production environment, this should always be true
    if [ ! -z "${IS_BACKUP_ENABLED}" && "${IS_BACKUP_ENABLED}" = "${_TRUE}" ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Global backups enabled - processing..");
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Search and remove backups older than ${BACKUP_RETENTION_TIME} days...");

        ## clean up the old backup files per the configured retention period...
        for BACKUP_FILE in $(find ${APP_ROOT}/${BACKUP_DIRECTORY} -name "*${ZONE_BACKUP_PREFIX}*" -mtime +${BACKUP_RETENTION_TIME} -print)
        do
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} AUDIT $METHOD_NAME} ${CNAME} ${LINENO} "Removing backup file ${BACKUP_FILE}..");

            ## and remove it
            rm -rf ${BACKUP_FILE};
        done

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Cleanup complete. Continuing..");
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Gathering directories required for backup..");

        set -A TARFILE_DIRECTORY_LIST;

        ## we need to be selective here. we only need files that are truly required for named operation. nothing else.
        ## THIS applications config files are here as well. so we ignore "properties", as no named config files have that suffix
        for CONF_FILE in $(ls -ltr ${NAMED_ROOT}/${NAMED_CONF_DIR} | grep -v properties | awk '{print $9}')
        do
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Found file ${CONF_FILE}");

            set -A TARFILE_DIRECTORY_LIST ${TARFILE_DIRECTORY_LIST[@]} ${CONF_FILE};
        done

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Completed build of directory tree. TARFILE_DIRECTORY_LIST -> ${TARFILE_DIRECTORY_LIST[@]}");
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Creating tarfile..");

        ## generate the tarfile name
        TARFILE_NAME=$(echo ${BACKUP_FILE_NAME} | sed -e "s/%TYPE%/${CONF_BACKUP_PREFIX}/" -e "s/%SERVER_NAME%/$(uname -n)/" -e "s/%DATE%/$(date +"%m-%d-%Y_%H:%M:%S")/");

        if [ ! -z "${TARFILE_DIRECTORY_LIST[@]}" && ! -z "${TARFILE_NAME}" ]
        then
            ## we've successfully generated our backup list as well as our tarfile name. move forward
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "TARFILE_NAME -> ${TARFILE_NAME}");
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Creating backup file..");

            (cd ${NAMED_ROOT}/${NAMED_CONF_DIR}; tar cf - ${TARFILE_DIRECTORY_LIST[@]}) | gzip -c > ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME}.tar.gz;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "All known files backed up. Verifying..");

            ## verify the backup exists
            if [ -s ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME}.tar.gz ]
            then
                ## our backup is complete. set permissions accordingly...
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} DEBUG $METHOD_NAME} ${CNAME} ${LINENO} "Backup successfully verified. Modifying permissions..");

                ## chmod 644 and chown named:named.
                chmod 644 ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME}.tar.gz;
                chown ${NAMED_USER}:${NAMED_GROUP} ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME}.tar.gz;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete - exiting.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=0;
            else
                ## backup failed. send a fault code
                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to verify backup tarfile generation.";

                RETURN_CODE=57;
            fi
        else
            ## failed to generate a required variable. error
            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to generate backup list.";

            RETURN_CODE=87;
        fi
    else
        ## no backups enabled. send a notification
        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backups not currently enabled on $(uname -n). Unable to continue.";

        RETURN_CODE=88;
    fi

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Perform a full or specified backup operation";
    print "Usage: ${CNAME} <no arguments> | zone | conf";
    print "  No arguments are required to perform a complete backup of all zone and operational configuration files. If";
    print "  arguments are specified, they must be one of the following:";
    print "  ";
    print "  zone - performs a backup of all zone files housed in this installation";
    print "  conf - performs a backup of all configuration files necessary to operate a DNS installation";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh || \
    echo "Failed to locate configuration data. Cannot continue.";
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

[ ${#} -eq 0 ] && perform_zone_backup && perform_conf_backup;
if [ "${1}" = "zone" || "${1}" = "conf" ]
then
    if [ ! -z "${2}" ]
    then
        [ "${2}" = "master" ] && perform_master_backup;
        [ "${2}" = "slave" ] && perform_slave_backup;
    else
        perform_${1}_backup;
    fi
else
    usage;
fi

echo ${RETURN_CODE};
return ${RETURN_CODE};
