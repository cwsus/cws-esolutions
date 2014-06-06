#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  executeServiceFailover.sh
#         USAGE:  ./executeServiceFailover.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Processes a DNS failover by using information previously
#                 obtained by retrieve_site_info.sh
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
[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ -z "${PLUGIN_ROOT_DIR}" && -f ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && echo "Failed to locate configuration data. Cannot continue." && exit 1;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

unset METHOD_NAME;
unset CNAME;

## check security
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
RET_CODE=${?};

[ ${RET_CODE} -ne 0 ] && echo "Security configuration does not allow the requested action." && echo ${RET_CODE} && exit ${RET_CODE};

## unset the return code
unset RET_CODE;

## lock it
${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/lock.sh lock ${$};
RET_CODE=${?};

[ ${RET_CODE} -ne 0 ] && echo "Application currently in use." && echo ${RET_CODE} && exit ${RET_CODE};

unset RET_CODE;

CNAME="$(basename "${0}")";
METHOD_NAME="${CNAME}#startup";

trap "${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/lock.sh unlock ${$}; exit" INT TERM EXIT;

#===  FUNCTION  ===============================================================
#          NAME:  failoverInternetSite
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function failoverInternetSite
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    unset METHOD_NAME;
    unset CNAME;

    ## check security
    ${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -s ${NAMED_MASTER};
    RET_CODE=${?};

    [ ${RET_CODE} -ne 0 ] && echo "This command cannot be performed on a non-master nameserver." && echo ${RET_CODE} && exit ${RET_CODE};

    ## unset the return code
    unset RET_CODE;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILENAME -> ${FILENAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_DC -> ${TARGET_DC}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROJECT_CODE -> ${PROJECT_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting change array and DC_FILE..";

    DC_FILE=$(echo ${FILENAME} | cut -d "." -f 1-2);

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_ARRAY and DC_FILE configured..";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DC_FILE->${DC_FILE}";

    ## First, lets make sure the directory for the provided
    ## BU actually exists
    if [ -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT} ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_ROOT to ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT}..";

        SITE_ROOT=${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT};
        TARFILE_DATE=$(date +"%m-%d-%Y");
        BACKUP_FILE=${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_ROOT->${SITE_ROOT}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checking for file ${FILENAME}...";

        ## Then, lets check and make sure that the zonefile
        ## actually exists
        if [ -f ${SITE_ROOT}/${FILENAME} ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File exists - backup in progress...";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting backup file to ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${FILENAME}.\`date +"%m-%d-%Y"\`.${CHANGE_NUM}.${IUSER_AUDIT}";

            ## Everything exists. Lets backup the zone before
            ## making any modifications
            ## why tar+gzip ? to carry the process over. we
            ## want consistency, even when it doesnt make a
            ## difference
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "BACKUP_FILE->${BACKUP_FILE}");

            ## tar+gzip
            tar cf ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar -C ${SITE_ROOT} ${FILENAME} > /dev/null 2>&1;
            gzip ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar > /dev/null 2>&1;

            ## make sure our backup file got created
            if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar.gz ]
            then
                ## unset BACKUP_FILE var
                unset BACKUP_FILE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete - continuing...";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying ${DC_FILE} to ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}..";

                ## copy the datacenter-specific zone file for operation
                cp ${SITE_ROOT}/${TARGET_DC}/${DC_FILE} ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${FILENAME};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling addServiceIndicators.sh to add audit flags..";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                ## add our informational and audit indicators
                . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/addServiceIndicators.sh -r ${GROUP_ID}${BUSINESS_UNIT} -f ${FILENAME} -t ${TARGET_DC} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e;
                RET_CODE=${?};

                ## set method_name/cname back to this method
                local METHOD_NAME="${CNAME}#${0}";
                CNAME="$(basename "${0}")";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "addServiceIndicators processing complete. Return code->${RET_CODE}";

                if [ ${RET_CODE} -eq 0 ]
                then
                    ## All our preliminary processing has been completed,
                    ## so lets move the updated zone file into place
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${FILENAME} to ${SITE_ROOT}/${FILENAME}..";
                    cp ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${FILENAME} ${SITE_ROOT}/${FILENAME};

                    ## Lets make sure that the copied file and the new file match
                    MD5_TMP_FILE=$(cksum ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${FILENAME} | awk '{print $1}');
                    MD5_NEW_FILE=$(cksum ${SITE_ROOT}/${FILENAME} | awk '{print $1}');

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "cksum of ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${FILENAME} -> ${MD5_TMP_FILE}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "cksum of ${SITE_ROOT}/${FILENAME} -> ${MD5_NEW_FILE}";

                    if [ "${MD5_TMP_FILE}" = "${MD5_NEW_FILE}" ]
                    then
                        ## Log an audit record
                        ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: ${UNIT}/${FILENAME} - Change Request: ${CHANGE_NUM} - Switched To: ${TARGET_DC}";

                        ## Remove the temporary file
                        rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${FILENAME};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        ## clean up our variables
                        unset BUSINESS_UNIT;
                        unset FILENAME;
                        unset TARGET_DC;
                        unset PROJECT_CODE;
                        unset CHANGE_NUM;
                        unset DC_FILE;
                        unset SITE_ROOT;
                        unset MD5_TMP_FILE;
                        unset MD5_NEW_FILE;

                        ## Return 0 and exit
                        RETURN_CODE=0;
                    else
                        ## The cksum's of the placed and the tmp files dont
                        ## match. this probably means they didnt get copied
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The failover process was unsuccessful. Please try again.";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        ## clean up our variables
                        unset BUSINESS_UNIT;
                        unset FILENAME;
                        unset TARGET_DC;
                        unset PROJECT_CODE;
                        unset CHANGE_NUM;
                        unset DC_FILE;
                        unset SITE_ROOT;
                        unset MD5_TMP_FILE;
                        unset MD5_NEW_FILE;

                        RETURN_CODE=8;
                    fi
                else
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred during indicator processing. Return code->${RET_CODE}";

                    ## a failure occurred during audit processing. abort
                    RETURN_CODE=999;
                fi
            else
                ## no backup, no workie
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create backup file. Cannot continue.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## clean up our variables
                unset BUSINESS_UNIT;
                unset FILENAME;
                unset TARGET_DC;
                unset PROJECT_CODE;
                unset CHANGE_NUM;
                unset DC_FILE;
                unset SITE_ROOT;
                unset MD5_TMP_FILE;
                unset MD5_NEW_FILE;

                RETURN_CODE=57;
            fi
        else
            ## we couldnt find a zone file with the requested project
            ## code attached to it in ${SITE_ROOT}. this could be a typo,
            ## either in user entry or in the filename
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested project code does not exist. Cannot continue.";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            ## clean up our variables
            unset BUSINESS_UNIT;
            unset FILENAME;
            unset TARGET_DC;
            unset PROJECT_CODE;
            unset CHANGE_NUM;
            unset DC_FILE;
            unset SITE_ROOT;
            unset MD5_TMP_FILE;
            unset MD5_NEW_FILE;

            RETURN_CODE=9;
        fi
    else
        ## the BU provided doesnt have a directory
        ## this could be a typo, either user-provided
        ## or in the directory name
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested business unit does not have a defined group. Cannot continue.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        ## clean up our variables
        unset BUSINESS_UNIT;
        unset FILENAME;
        unset TARGET_DC;
        unset PROJECT_CODE;
        unset CHANGE_NUM;
        unset DC_FILE;
        unset SITE_ROOT;
        unset MD5_TMP_FILE;
        unset MD5_NEW_FILE;

        RETURN_CODE=10;
    fi

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  failoverIntranetSite
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function failoverIntranetSite
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    ## re-define some of the things we're re-using here..
    typeset -l ENABLE_POP=${TARGET_DC};
    typeset -l DISABLE_POP=${FILENAME};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENABLE_POP -> ${ENABLE_POP}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DISABLE_POP -> ${DISABLE_POP}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";

    ## theres not really a whole lot to this. its
    ## basically just calling global dispatch to
    ## enable one pop and disable the other
    ## first, take a backup
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backing up existing configuration..";

    BACKUP_FILE_NAME=$(echo ${BACKUP_FILE_NAME} | sed -e "s/%TYPE%.%SERVER_NAME%.%DATE%/GLD.${HOSTNAME}.$(date +"%Y-%m-%d")/");

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BACKUP_FILE_NAME -> ${BACKUP_FILE_NAME}";

    $(gdctl -k -o ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE_NAME});

    ## and make sure it actually backed up...
    if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE_NAME} ]
    then
        ## it did, make the change
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete. Disabling ${DISABLE_POP}..";

        RET_CODE=$(gdaction -t 90 -m ${HOSTNAME} -F ${PLUGIN_ROOT_DIR}/${GD_PASS_FILE} disable_pop ${DISABLE_POP});

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

        if [ ${RET_CODE} -eq 0 ]
        then
            ## service disabled
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${DISABLE_POP} disabled on host ${HOSTNAME}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Enabling ${ENABLE_POP} ..";

            RET_CODE=$(gdaction -t 90 -m ${HOSTNAME} -F ${PLUGIN_ROOT_DIR}/${GD_PASS_FILE} enable_pop ${ENABLE_POP});

            if [ ${RET_CODE} -eq 0 ]
            then
                ## processing complete
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${ENABLE_POP} enabled on host ${HOSTNAME}";
                ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Disabled: ${DISABLE_POP}; Enabled: ${ENABLE_POP} - completed by ${IUSER_AUDIT} on $(date +"%Y-%m-%d %H:%M:%S")";

                RETURN_CODE=0;
            else
                ## an error occurred enabling the service
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while enabling ${ENABLE_POP} on host ${HOSTNAME}";

                RETURN_CODE=1;
            fi
        else
            ## an error occurred disabling service
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while enabling ${ENABLE_POP} on host ${HOSTNAME}";

            RETURN_CODE=1;
        fi
    else
        ## no backup was taken
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup processing has failed. Cannot continue.";

        RETURN_CODE=57;
    fi

    unset RET_CODE;
    unset BACKUP_FILE_NAME;
    unset CHANGE_NUM;
    unset DISABLE_POP;
    unset ENABLE_POP;
    unset SITE_HOSTNAME;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  failoverDatacenter
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#==============================================================================
function failoverDatacenter
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    unset METHOD_NAME;
    unset CNAME;

    ## check security
    ${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -s ${NAMED_MASTER};
    RET_CODE=${?};

    [ ${RET_CODE} -ne 0 ] && echo "This command cannot be performed on a non-master nameserver." && echo ${RET_CODE} && exit ${RET_CODE};

    ## unset the return code
    unset RET_CODE;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_DC -> ${TARGET_DC}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Starting up..";

    ## we want to take pretty much everything
    ## in the db directory.
    for UNIT in $(ls -ltr ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} | awk '{print $9}' | grep -v '^$')
    do
        if [ $(echo ${IGNORE_LIST} | grep -c ${UNIT}) -eq 1 ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Dropping ${UNIT} per configured ignore list");
        else
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Now operating on ${UNIT}..");
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Backing up files..");

            ## create the filename
            TARFILE_DATE=$(date +"%m-%d-%Y");
            BACKUP_FILE=${UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "BACKUP_FILE->${BACKUP_FILE}");

            ## tar+gzip
            tar cf ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar \
                -C ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} ${UNIT}/${NAMED_ZONE_PREFIX}.* > /dev/null 2>&1;
            gzip ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar > /dev/null 2>&1;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Backup for ${UNIT} complete - continuing..");

            ## make sure we got a good backup
            if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar.gz ]
            then
                ## we did. keep going.
                ## create the business unit directories
                mkdir ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT};

                ## loop through the zone files
                for FILENAME in $(ls -ltr ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${UNIT} | awk '{print $9}' | cut -d ":" -f 1-1 | grep -v "[PV]H" | uniq | sed -e '/ *#/d; /^ *$/d');
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Now operating on ${UNIT}/${FILENAME}..");

                    ## copy the target datacenter file
                    cp ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${UNIT}/${TARGET_DC}/$(echo ${FILENAME} | cut -d "." -f 1-2) ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME};

                    ## setup serial numbers
                    LAST_SERIAL=$(grep "; serial" ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME} | awk '{print $1}' | sed -e '/^$/d');
                    [ $(echo ${LAST_SERIAL} | cut -c 1-8) -eq $(date +"%Y%m%d") ] && SERIAL_NUM=$(( ${LAST_SERIAL} + 1 )) || SERIAL_NUM=${DEFAULT_SERIAL_NUMBER};

                    ## fill it
                    set -A CHG_ARRAY ${LAST_SERIAL} ${TARGET_DC} $(date +"%m-%d-%Y") ${IUSER_AUDIT} ${CHANGE_NUM} ${SERIAL_NUM};

                    for INDICATOR in LAST_SERIAL DATACENTER DATE USER_NAME REQUEST_NUMBER SERIAL_NUM
                    do
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${INDICATOR} -> ${CHG_ARRAY[${A}]}";

                        ## we can't use addServiceIndicators here because the right variables
                        ## aren't set AND the sheer volume. so we're just going to do it
                        ## here. well, probably can, but its easier here. its not good
                        ## code re-use, but whatever, it should work
                        ## TODO: try and make this work with addServiceIndicators
                        . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/addServiceIndicators.sh -r ${GROUP_ID}${UNIT} -f ${FILENAME} -t ${TARGET_DC} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e;
                        RET_CODE=${?};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "RET_CODE_>${RET_CODE}");

                        if [ ${RET_CODE} -ne 0 ]
                        then
                            $(${LOGGER} "ERROR" ${METHOD_NAME} ${CNAME} ${LINENO} "Failed to update ${GROUP_ID}${UNIT}/${FILENAME}.");

                            (( ERROR_COUNT += 1 ));
                        fi

                        (( A += 1 ));
                    done

                    ## unset serials and array
                    set -A CHG_ARRAY;
                    unset LAST_SERIAL;
                    unset SERIAL_NUM;
                    unset INDICATOR;
                    A=0;
                done

                unset FILENAME;
                unset BACKUP_FILE;
            else
                ## failed to create backup file. this is not good.
                ## fail here.
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup processing FAILED. Cannot continue.";

                RETURN_CODE=57;
                break;
            fi
        fi
    done

    unset UNIT;
    unset UNIT;
    unset LAST_SERIAL;
    unset SERIAL_NUM;
    set -A CHG_ARRAY;
    A=0;

    if [ -z "${RETURN_CODE}" ]
    then
        if [ ! ${ERROR_COUNT} -ge ${FAILURE_THRESHOLD} ]
        then
            ## some errors were encountered during temp file processing,
            ## and the number meets or exceeds our failure threshold.
            ## throw back an error
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup processing FAILED. Cannot continue.";

            RETURN_CODE=57;
        else
            ## backup processing didnt fail, so keep going
            ## the above should complete processing against the zones
            ## we should be able to copy them in place at this point
            ## we'll just copy the zone files into the right places,
            ## shouldn't need to do more than that
            ERROR_COUNT=0;

            for UNIT in $(ls -ltr ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY} | awk '{print $9}' | grep -v '^$')
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Now operating on ${UNIT}..");

                ## loop through the zone files
                for FILENAME in $(ls -ltr ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT} | awk '{print $9}' | cut -d ":" -f 1-1 | uniq | sed -e '/ *#/d; /^ *$/d');
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Copying ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME} to ${NAMED_ROOT}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME}");

                    ## copy the target datacenter file
                    cp ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME} ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Copied ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME} to ${NAMED_ROOT}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME}");
                done
            done

            unset FILENAME;
            unset UNIT;

            ## files should be in place, change should be complete
            ## perform a checksum to make sure
            for UNIT in $(ls -ltr ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY} | awk '{print $9}' | grep -v '^$')
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Now operating on ${UNIT}..");

                ## loop through the zone files
                for FILENAME in $(ls -ltr ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT} | awk '{print $9}' | cut -d ":" -f 1-1 | uniq | sed -e '/ *#/d; /^ *$/d');
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Checksum ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME} <-> ${NAMED_ROOT}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME}");
                    TMP_CHECKSUM=$(cksum ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME} | awk '{print $1}');
                    OP_CHECKSUM=$(cksum ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME} | awk '{print $1}');

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "${FILENAME} -> TMP_CHECKSUM -> ${TMP_CHECKSUM}");
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "${FILENAME} -> OP_CHECKSUM -> ${OP_CHECKSUM}");

                    ## copy the target datacenter file
                    if [ ${TMP_CHECKSUM} -eq ${OP_CHECKSUM} ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Checksum ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME} <-> ${NAMED_ROOT}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME} match - continuing..");
                        ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: \`date +"%d-%m-%Y"\` - Site: ${UNIT}/${FILENAME} - Change Request: ${CHANGE_NUM} - Switched To: ${TARGET_DC}";

                        unset TMP_CHECKSUM;
                        unset OP_CHECKSUM;
                    else
                        unset TMP_CHECKSUM;
                        unset OP_CHECKSUM;

                        $(${LOGGER} "ERROR" ${METHOD_NAME} ${CNAME} ${LINENO} "Checksum ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME} <-> ${NAMED_ROOT}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME} mismatch - continuing..");

                        (( ERROR_COUNT += 1 ));

                        if [ ${ERROR_COUNT} -gt ${FAILURE_THRESHOLD} ]
                        then
                            $(${LOGGER} "ERROR" ${METHOD_NAME} ${CNAME} ${LINENO} "Checksum failure count exceeds threshold. Aborting.");

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            unset FILENAME;
                            unset CHANGE_NUM;
                            unset TARGET_DC;
                            unset UNIT;
                            B=0;

                            RETURN_CODE=8;
                        fi
                    fi
                done

                ERROR_COUNT=0;
                unset FILENAME;
            done

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing temporary files..";
            rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/*;
        fi
    fi

    unset CHANGE_NUM;
    unset TARGET_DC;
    unset UNIT;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

#===  FUNCTION  ===============================================================
#          NAME:  failoverBusinessUnit
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function failoverBusinessUnit
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    unset METHOD_NAME;
    unset CNAME;

    ## check security
    ${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -s ${NAMED_MASTER};
    RET_CODE=${?};

    [ ${RET_CODE} -ne 0 ] && echo "This command cannot be performed on a non-master nameserver." && echo ${RET_CODE} && exit ${RET_CODE};

    ## unset the return code
    unset RET_CODE;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_DC -> ${TARGET_DC}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting change array..";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_ARRAY configured..";

    if [ "${VERBOSE}" = "${_TRUE}" ]
    then
        while [ ${A} -ne ${#CHG_ARRAY[@]} ]
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_ARRAY->${CHG_ARRAY[${A}]}";
            (( A += 1 ));
        done
    fi

    ## reset counters
    A=0;

    ## First, lets make sure the directory for the provided
    ## BU actually exists
    if [ -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT} ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_ROOT..";

        SITE_ROOT=${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT};
        TARFILE_DATE=$(date +"%m-%d-%Y");
        BACKUP_FILE=${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_ROOT set -> ${SITE_ROOT}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backing up files..";

        ## backup the existing zone files. if we need to back out a change,
        ## we can do it with these. why tar+gzip ? to carry the process over. we
        ## want consistency, even when it doesnt make a difference
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "BACKUP_FILE->${BACKUP_FILE}");

        ## tar+gzip
        tar cf ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar -C ${SITE_ROOT} ${NAMED_ZONE_PREFIX}.* > /dev/null 2>&1;
        gzip ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar > /dev/null 2>&1;

        ## make sure backup file got created
        if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar.gz ]
        then
            unset BACKUP_FILE;

            ## NOTE: this looks good, leave it alone
            for FILENAME in $(ls -ltr ${SITE_ROOT} | awk '{print $9}' | cut -d ":" -f 1 | grep -v "[PV]H" | uniq | sed -e '/ *#/d' -e '/^ *$/d')
            do
                SPLIT=$(echo ${FILENAME} | tr -dc '.' | wc -c);
                DC_FILE=$(echo ${FILENAME} | cut -d "." -f 0-$(echo ${SPLIT}));

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SPLIT->${SPLIT}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DC_FILE->${DC_FILE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating operational copy in ${TMP_DIRECTORY}..";

                ## take operational copy
                cp ${SITE_ROOT}/${TARGET_DC}/${DC_FILE} ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${FILENAME};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling addServiceIndicators.sh..";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                ## add our informational and audit indicators
                . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/addServiceIndicators.sh -r ${GROUP_ID}${BUSINESS_UNIT} -f ${FILENAME} -t ${TARGET_DC} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e;
                RET_CODE=${?};

                ## set method_name and cname back to this
                CNAME="$(basename "${0}")";
                local METHOD_NAME="${CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "addServiceIndicators execution complete. Return code->${RET_CODE}";

                ## make sure we got zero back
                if [ ${RET_CODE} -eq 0 ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Indicators set - checksumming..";

                    ## set the checksum
                    set -A TMP_MD5SUM $(echo "${TMP_MD5SUM[@]}" "$(cksum ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${FILENAME} | awk '{print $1}')");

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum complete -> ${TMP_MD5SUM[${A}]}";

                    (( A += 1 ));
                else
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred during indicator processing. Return code->${RET_CODE}";

                    A=0;
                    B=0;
                    ## a failure occurred during audit processing. abort
                    RETURN_CODE=999;
                fi
            done

            ## unset variables
            unset FILENAME;
            unset SPLIT;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete and indicators set. Proceeding..";

            ## reset counters
            A=0;
            B=0;

            ## All our preliminary processing has been completed,
            ## so lets move the updated zone file into place
            for FILENAME in $(ls -ltr ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY} | awk '{print $9}' | cut -d ":" -f 1-1 | grep -v "[PV]H" | uniq | sed -e '/ *#/d; /^ *$/d')
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying ${FILENAME} to ${SITE_ROOT}..";

                cp ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${FILENAME} ${SITE_ROOT}/${FILENAME};

                ## configure the checksum array
                set -A NEW_MD5SUM $(echo "${NEW_MD5SUM[@]}" "$(cksum ${SITE_ROOT}/${FILENAME} | awk '{print $1}')");

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum complete -> ${TMP_MD5SUM[${A}]}";

                (( A += 1 ));
            done

            ## reset counters
            A=0;

            ## validate checksums - they need to match, if not,
            ## something isn't right
            while [ ${A} -ne ${C} ]
            do
                if [ "${NEW_MD5SUM[${A}]}" = "${TMP_MD5SUM[${A}]}" ]
                then
                    ## Log an audit record
                    ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: ${UNIT}/${DC_FILE} - Change Request: ${CHANGE_NUM} - Switched To: ${TARGET_DC}";
                else
                    ## some kind of error occurred
                    ## log it out and return the code
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHECKSUM FAILURE: ${NEW_MD5SUM[${A}]} !=  ${TMP_MD5SUM[${A}]}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    ## unset variables
                    unset SITE_ROOT;
                    unset FILENAME;
                    unset SPLIT;
                    set -A TMP_MD5SUM;
                    set -A NEW_MD5SUM;

                    RETURN_CODE=8;
                fi

                (( A += 1 ));
            done

            ## reset the counter
            A=0;
            C=0;

            ## if we got this far, it means the checksums
            ## match, and the failover should have succeeded.
            ## we can clean up our temp files now
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Deleting temp files..";

            rm ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/*;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            ## unset variables
            unset SITE_ROOT;
            unset FILENAME;
            unset SPLIT;
            unset DC_FILE;
            set -A TMP_MD5SUM;
            set -A NEW_MD5SUM;

            RETURN_CODE=0;
        else
            ## failed to create backup file
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested business unit does not have a defined group. Cannot continue.";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            ## unset variables
            unset SITE_ROOT;
            unset FILENAME;
            unset SPLIT;
            set -A TMP_MD5SUM;
            set -A NEW_MD5SUM;

            RETURN_CODE=57;
        fi
    else
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested business unit does not have a defined group. Cannot continue.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        ## unset variables
        unset SITE_ROOT;
        unset FILENAME;
        unset SPLIT;
        set -A TMP_MD5SUM;
        set -A NEW_MD5SUM;

        RETURN_CODE=10;
    fi
}

#===  FUNCTION  ===============================================================
#          NAME:  failoverProject
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function failoverProject
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    unset METHOD_NAME;
    unset CNAME;

    ## check security
    ${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -s ${NAMED_MASTER};
    RET_CODE=${?};

    [ ${RET_CODE} -ne 0 ] && echo "This command cannot be performed on a non-master nameserver." && echo ${RET_CODE} && exit ${RET_CODE};

    ## unset the return code
    unset RET_CODE;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROJECT_CODE -> ${PROJECT_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_DC -> ${TARGET_DC}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting change array..";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_ARRAY configured..";

    if [ "${VERBOSE}" = "${_TRUE}" ]
    then
        while [ ${A} -ne ${#CHG_ARRAY[@]} ]
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_ARRAY->${CHG_ARRAY[${A}]}";
            (( A += 1 ));
        done
    fi

    ## reset counters
    A=0;

    ## First, lets make sure the directory for the provided
    ## BU actually exists
    if [ -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT} ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_ROOT..";

        SITE_ROOT=${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT};
        TARFILE_DATE=$(date +"%m-%d-%Y");
        BACKUP_FILE=${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_ROOT set -> ${SITE_ROOT}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backing up files..";

        ## backup the existing zone files. if we need to back out a change,
        ## we can do it with these. why tar+gzip ? to carry the process over. we
        ## want consistency, even when it doesnt make a difference
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "BACKUP_FILE->${BACKUP_FILE}");

        ## tar+gzip
        tar cf ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar -C ${SITE_ROOT} ${NAMED_ZONE_PREFIX}.* > /dev/null 2>&1;
        gzip ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar > /dev/null 2>&1;

        ## make sure backup file got created
        if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar.gz ]
        then
            unset BACKUP_FILE;

            ## NOTE: this looks good, leave it alone
            for FILENAME in $(ls -ltr ${SITE_ROOT} | grep ${PROJECT_CODE} | awk '{print $9}' | cut -d ":" -f 1 | grep -v "[PV]H" | uniq | sed -e '/ *#/d' -e '/^ *$/d')
            do
                SPLIT=$(echo ${FILENAME} | tr -dc '.' | wc -c);
                DC_FILE=$(echo ${FILENAME} | cut -d "." -f 0-$(echo ${SPLIT}));

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SPLIT->${SPLIT}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DC_FILE->${DC_FILE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating operational copy in ${TMP_DIRECTORY}..";

                ## take operational copy
                cp ${SITE_ROOT}/${TARGET_DC}/${DC_FILE} ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${FILENAME};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling addServiceIndicators.sh..";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                ## add our informational and audit indicators
                . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/addServiceIndicators.sh -r ${GROUP_ID}${BUSINESS_UNIT} -f ${FILENAME} -t ${TARGET_DC} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e;
                RET_CODE=${?};

                ## set method_name and cname back to this
                CNAME="$(basename "${0}")";
                local METHOD_NAME="${CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "addServiceIndicators execution complete. Return code->${RET_CODE}";

                ## make sure we got zero back
                if [ ${RET_CODE} -eq 0 ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Indicators set - checksumming..";

                    ## set the checksum
                    set -A TMP_MD5SUM $(echo "${TMP_MD5SUM[@]}" "$(cksum ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${FILENAME} | awk '{print $1}')");

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum complete -> ${TMP_MD5SUM[${A}]}";

                    (( A += 1 ));
                else
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred during indicator processing. Return code->${RET_CODE}";

                    A=0;
                    B=0;
                    ## a failure occurred during audit processing. abort
                    RETURN_CODE=999;
                fi
            done

            ## unset variables
            unset FILENAME;
            unset SPLIT;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete and indicators set. Proceeding..";

            ## reset counters
            A=0;
            B=0;

            ## All our preliminary processing has been completed,
            ## so lets move the updated zone file into place
            for FILENAME in $(ls -ltr ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY} | awk '{print $9}' | cut -d ":" -f 1-1 | grep -v "[PV]H" | uniq | sed -e '/ *#/d; /^ *$/d')
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying ${FILENAME} to ${SITE_ROOT}..";

                cp ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${FILENAME} ${SITE_ROOT}/${FILENAME};

                ## configure the checksum array
                set -A NEW_MD5SUM $(echo "${NEW_MD5SUM[@]}" "$(cksum ${SITE_ROOT}/${FILENAME} | awk '{print $1}')");

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum complete -> ${TMP_MD5SUM[${A}]}";

                (( A += 1 ));
            done

            ## reset counters
            A=0;

            ## validate checksums - they need to match, if not,
            ## something isn't right
            while [ ${A} -ne ${C} ]
            do
                if [ "${NEW_MD5SUM[${A}]}" = "${TMP_MD5SUM[${A}]}" ]
                then
                    ## Log an audit record
                    ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: ${UNIT}/${DC_FILE} - Change Request: ${CHANGE_NUM} - Switched To: ${TARGET_DC}";
                else
                    ## some kind of error occurred
                    ## log it out and return the code
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHECKSUM FAILURE: ${NEW_MD5SUM[${A}]} !=  ${TMP_MD5SUM[${A}]}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    ## unset variables
                    unset SITE_ROOT;
                    unset FILENAME;
                    unset SPLIT;
                    set -A TMP_MD5SUM;
                    set -A NEW_MD5SUM;

                    RETURN_CODE=8;
                fi

                (( A += 1 ));
            done

            ## reset the counter
            A=0;
            C=0;

            ## if we got this far, it means the checksums
            ## match, and the failover should have succeeded.
            ## we can clean up our temp files now
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Deleting temp files..";

            rm ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/*;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            ## unset variables
            unset SITE_ROOT;
            unset FILENAME;
            unset SPLIT;
            unset DC_FILE;
            set -A TMP_MD5SUM;
            set -A NEW_MD5SUM;

            RETURN_CODE=0;
        else
            ## failed to create backup file
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested business unit does not have a defined group. Cannot continue.";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            ## unset variables
            unset SITE_ROOT;
            unset FILENAME;
            unset SPLIT;
            set -A TMP_MD5SUM;
            set -A NEW_MD5SUM;

            RETURN_CODE=57;
        fi
    else
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested business unit does not have a defined group. Cannot continue.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        ## unset variables
        unset SITE_ROOT;
        unset FILENAME;
        unset SPLIT;
        set -A TMP_MD5SUM;
        set -A NEW_MD5SUM;

        RETURN_CODE=10;
    fi
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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Execute a site failover";
    print "Usage: ${CNAME} [ -t < failover type >  ] [ -b < business unit > ] [ -p < project code > ] [ -s < site hostname > ] [ -a ] [ -x < target > ] [ -i < requesting user > ] [ -c < change request > ] [ -e ] [-h | -?]";
    print " -t     The failover type to execute - internal or external.";
    print " -b     If performing a business unit failover, please provide the unit name. Required if failing over a project code.";
    print " -p     If performing a project code failover, please provide the code name. The project's business unit must also be provided.";
    print " -s     If performing a site failover, please provide the site hostname.";
    print " -a     Selected if performing a full datacenter failover.";
    print " -x     The target datacenter to failover to.";
    print " -i     The user account requesting the action.";
    print " -c     The change request associated with the action.";
    print " -e     Execute the request";
    print " -h|?   Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[ ${#} -eq 0 ] && usage;

[ ! -d ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY} ] && mkdir ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY};

while getopts ":t:b:p:s:ax:i:c:eh:" OPTIONS
do
    case "${OPTIONS}" in
        t)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting FAILOVER_TYPE..";

            ## Capture the business unit
            typeset -u FAILOVER_TYPE="${OPTARG}"; # This will be the BU to move

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FAILOVER_TYPE -> ${FAILOVER_TYPE}";
            ;;
        b)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting BUSINESS_UNIT..";

            ## Capture the business unit
            typeset -u BUSINESS_UNIT="${OPTARG}"; # This will be the BU to move

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
            ;;
        p)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting PROJECT_CODE..";

            ## Capture the project code
            typeset -u PROJECT_CODE="${OPTARG}"; # This will be the project code to move

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROJECT_CODE -> ${PROJECT_CODE}";
            ;;
        s)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_HOSTNAME..";

            ## Capture the project code
            typeset -l SITE_HOSTNAME="${OPTARG}"; # This will be the project code to move

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME -> ${SITE_HOSTNAME}";
            ;;
        a)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_HOSTNAME..";

            ## Capture the project code
            FULL_DATACENTER="${_TRUE}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FULL_DATACENTER -> ${FULL_DATACENTER}";
            ;;
        x)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting TARGET_DC..";

            ## Capture the target datacenter
            ## this is being re-used for intra - and will be treated as "enable_pop"
            typeset -u TARGET_DC="${OPTARG}"; # This will be the target datacenter to move to

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_DC -> ${TARGET_DC}";
            ;;
        i)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting IUSER_AUDIT..";

            ## Capture the audit userid
            IUSER_AUDIT="${OPTARG}"; # This will be the target datacenter to move to

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
            ;;
        c)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            ## Capture the change control
            typeset -u CHANGE_NUM="${OPTARG}"; # This will be the project code to move

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            if [ -z "${IUSER_AUDIT}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to audit user account. Unable to continue processing.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=20;
            elif [ -z "${CHANGE_NUM}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=19;
            fi

            if [ -z "${RETURN_CODE}" ]
            then
                case ${FAILOVER_TYPE} in
                    ${INTRANET_TYPE_IDENTIFIER})
                        ## we really don't need much here. we just need to know the site name and thats kindof it.
                        if [ -z "${SITE_HOSTNAME}" ]
                        then
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            RETURN_CODE=19;
                        else
                            failoverIntranetSite;
                        fi
                        ;;
                    ${INTERNET_TYPE_IDENTIFIER})
                        ## Make sure we have enough information to process
                        ## and execute
                        if [ -z "${TARGET_DC}" ]
                        then
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target datacenter was provided. Unable to continue processing.";
        
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
        
                            RETURN_CODE=17;
                        else
                            ## We have enough information to process the request, continue
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            [[ ! -z "${BUSINESS_UNIT}" && -z "${PROJECT_CODE}" ]] && failoverBusinessUnit;
                            [[ ! -z "${PROJECT_CODE}" && ! -z "${BUSINESS_UNIT}" ]] && failoverProject;
                            [ ! -z "${SITE_HOSTNAME}" ] && failoverInternetSite;
                            [[ ! -z "${FULL_DATACENTER}" && "${FULL_DATACENTER}" = "${_TRUE}" ]] && failoverDatacenter;

                            usage;
                        fi
                        ;;
                esac
            fi
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done

shift ${OPTIND}-1;

echo ${RETURN_CODE};
exit ${RETURN_CODE};
