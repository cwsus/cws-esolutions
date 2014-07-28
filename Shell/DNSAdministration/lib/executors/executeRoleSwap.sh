#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  execute_role_swap.sh
#         USAGE:  ./execute_role_swap.sh
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
SCRIPT_ROOT="$(/usr/bin/env dirname ${SCRIPT_ABSOLUTE_PATH})";
METHOD_NAME="${CNAME}#startup";
LOCKFILE=$(mktemp);

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -f ${SCRIPT_ROOT}/../lib/plugin ] && . ${SCRIPT_ROOT}/../lib/plugin;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${PLUGIN_ROOT_DIR}" ] && /usr/bin/env echo "Failed to locate configuration data. Cannot continue." && return 1;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -f ${PLUGIN_LIB_DIRECTORY}/aliases ] && . ${PLUGIN_LIB_DIRECTORY}/aliases;
[ -f ${PLUGIN_LIB_DIRECTORY}/functions ] && . ${PLUGIN_LIB_DIRECTORY}/functions;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

THIS_CNAME="${CNAME}";
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

## validate the input
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

CNAME="${THIS_CNAME}";
typeset METHOD_NAME="${CNAME}#startup";

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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

lockProcess ${LOCKFILE} ${$};
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

CNAME="${THIS_CNAME}";
METHOD_NAME="${THIS_CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

[ ${RET_CODE} -ne 0 ] && echo "Application currently in use." && echo ${RET_CODE} && return ${RET_CODE};

unset RET_CODE;

#===  FUNCTION  ===============================================================
#          NAME:  switch_to_slave
#   DESCRIPTION:  Re-configures a master server to become a slave nameserver
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function switch_to_slave
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing role-swap from master to slave..";

    TARFILE_NAME=SWAP_SLAVE.${CHANGE_NUM}.$(date +"%m-%d-%Y").${REQUESTING_USER}.tar.gz;

    ## make sure the right directories exist
    if [ ! -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} ]
    then
        ## we dont have a master directory. fail.
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The master directory does not exist. Please process manually.";

        RETURN_CODE=23;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset TARFILE_NAME;
        unset METHOD_NAME;
        unset TARGET_SYSTEM;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

        return ${RETURN_CODE};
    fi

    ## make sure we're running on a real master server
    if [ $(ls -ltr ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} | wc -l) != 0 ]
    then
        ## good, we are. in the master switch, we check for a tarfile. we dont need a tarfile, but
        ## we do need to move the zonefiles from the master dir to the slave dir
        ## take a backup first
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backing up master zones..";

        (cd ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}; tar cf - *) | gzip -c > ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${TARFILE_NAME};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete. Verifying..";

        ## make sure we have it
        if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${TARFILE_NAME} ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup successfully verified. Performing pre-count of zone directories..";

            ## set "ERROR" count to zero
            ERROR_COUNT=0;

            ## take a count of the directories in the master directory
            PRE_MOVE_MASTER_COUNT=$(ls -ltr ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} | wc -l);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRE_MOVE_MASTER_COUNT -> ${PRE_MOVE_MASTER_COUNT}";

            ## good. lets keep going. move the zone files into the master directory
            for ZONE_DIRECTORY in $(find ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} -type d -maxdepth 1 -print)
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on ${ZONE_DIRECTORY}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Performing pre-verification count..";

                ZONE_PRE_COUNT=$(ls -ltrR ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${ZONE_DIRECTORY} | wc -l);

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_PRE_COUNT -> ${ZONE_PRE_COUNT}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moving directory ${ZONE_DIRECTORY}..";

                mv ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${ZONE_DIRECTORY} ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${ZONE_DIRECTORY};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Move complete. Verifying..";

                ## make sure it copied
                if [ -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${ZONE_DIRECTORY} ]
                then
                    ## make sure the contents are valid
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Performing post-verification count..";

                    ZONE_POST_COUNT=$(ls -ltrR ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${ZONE_DIRECTORY} | wc -l);

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_POST_COUNT -> ${ZONE_POST_COUNT}";

                    ## make sure the counts match...
                    if [ ${ZONE_PRE_COUNT} -eq ${ZONE_POST_COUNT} ]
                    then
                        ## they do. we're good here, keep going
                        ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Re-location of ${ZONE_DIRECTORY} completed by ${REQUESTING_USER}";
                    else
                        ## copy failed or something else is going on.
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Re-location of ${ZONE_DIRECTORY} failed. Please process manually.";

                        (( ERROR_COUNT += 1 ));
                    fi
                else
                    ## copy failed. directory doesnt exist
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Re-location of ${ZONE_DIRECTORY} failed. Please process manually.";

                    (( ERROR_COUNT += 1 ));
                fi

                unset ZONE_PRE_COUNT;
                unset ZONE_POST_COUNT;
            done

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "All zone moves complete. Checking for errors..";

            if [ ${ERROR_COUNT} -eq 0 ]
            then
                ## all operations were successfully performed. move forward
                ## next step is to re-configure the zone configuration files to operate as slaves
                ## update the zone config files
                ## make sure "ERROR" counter is zero
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No errors encountered during zone move. Continuing..";

                ERROR_COUNT=0;

                for ZONE_CONFIG in $(ls -ltr ${NAMED_ROOT}/${NAMED_CONF_DIR} | awk '{print $NF}')
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on ${ZONE_CONFIG}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating working copy..";

                    ## take a copy...
                    cp ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG} ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Working copy created. Creating backup..";

                    ## and back up the original..
                    cp ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG} ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${ZONE_CONFIG}.${CHANGE_NUM};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup created. Verifying..";

                    ## make sure we have our backup..
                    if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${ZONE_CONFIG}.${CHANGE_NUM} ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup verified. Verifying working copy..";

                        ## xlnt, make sure we have a working copy..
                        if [ -s ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Working copy verified. Switching from master to slave..";

                            ## get a count of zones in the file..
                            ZONE_COUNT=$(grep -c "zone" ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG});

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_COUNT -> ${ZONE_COUNT}";

                            ## lets start operating. first, change slave to master
                            sed -e "s/master/slave/g" ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} >> {PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG};

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch complete. Verifying..";

                            ## and make sure it was changed..
                            if [ $(grep -c ${NAMED_MASTER_ROOT} ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG}) -eq 0 ]
                            then
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch verified. Adding masters clause..";

                                ## great. keep going - replace the masters line with the allow-update line
                                mv {PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG} ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG};

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ZONE_NAMES..";

                                ZONE_NAMES=$(grep "zone \"" ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} | awk '{print $2}' | cut -d "\"" -f 2);

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_NAMES set. Continuing..";

                                for ZONE_NAME in ${ZONE_NAMES}
                                do
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on ${ZONE_NAME}";

                                    START_LINE_NUMBER=$(sed -n "/zone \"${ZONE_NAME}\" IN {/=" ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG});
                                    END_LINE_NUMBER=$(expr ${START_LINE_NUMBER} + 3);

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "START_LINE_NUMBER -> ${START_LINE_NUMBER}";
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "END_LINE_NUMBER -> ${END_LINE_NUMBER}";
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Placing masters clause..";

                                    ## solaris is all kinds of messed up i guess. what linux will do with
                                    ## <code>sed -e "${END_LINE_NUMBER}a\    masters         { \"${NAMED_MASTER_ACL}\"; };" ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} > ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG};</code>
                                    ## solaris cant. not sure why. so we go through this HIGHLY convoluted process here.
                                    sed -e "${END_LINE_NUMBER}a\\
                                        masters         { \"${NAMED_MASTER_ACL}\"; };" ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} > ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG};

                                    ## make it the target again...
                                    mv ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG} ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG};

                                    ## and then replace the 800 million spaces that got added
                                    sed -e "s/                                            masters         {/    masters           {/g" ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} > ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG};

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "masters clause placed. Verifying..";

                                    ## make sure it got placed
                                    if [ $(grep -n "masters         { \"${NAMED_MASTER_ACL}\"; };" ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG} | grep -c $(expr ${END_LINE_NUMBER} + 1)) -eq 0 ]
                                    then
                                        ## it did not. we fail here.
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to place masters clause in dynamic zone ${ZONE_CONFIG}. Please process manually.";

                                        (( ERROR_COUNT += 1 ));
                                    else
                                        ## success!
                                        ## we now need to update the allow-update line to none.
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "masters clause successfully added. Modifying allow-update clause..";

                                        mv ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG} ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG};

                                        ## operate a bit differently if we're on a dynamic zone..
                                        if [ $(grep -c ${NAMED_DYNAMIC_ROOT} ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG}) -ne 0 ]
                                        then
                                            ## this is a dynamic zone. change appropriately
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Dynamic zone detected. Modifying allow-update clause..";

                                            sed -e "s/allow-update    { key ${DHCPD_UPDATE_KEY}; };/allow-update    { none; };/g" ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} \
                                                >> ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG};

                                            if [ $(grep -c ${DHCPD_UPDATE_KEY} ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG}) -eq 0 ]
                                            then
                                                ## successfully modified the allow-update clause. this is done.
                                                ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Dynamic zone ${ZONE_NAME} successfully updated by ${REQUESTING_USER}.";

                                                mv ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG} ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG};
                                            else
                                                ## some form of failure..
                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to place modify allow-update clause in dynamic zone ${ZONE_CONFIG}. Please process manually.";

                                                (( ERROR_COUNT += 1 ));
                                            fi
                                        else
                                            ## not a dynamic zone, so this switch is complete
                                            ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_NAME} successfully updated by ${REQUESTING_USER}.";
                                        fi
                                    fi

                                    unset START_LINE_NUMBER;
                                    unset END_LINE_NUMBER;
                                done

                                unset ZONE_NAMES;
                                unset ZONE_NAME;

                                ## ok, now its time to move the file into place
                                ## make sure our "ERROR" counter is zero
                                if [ ${ERROR_COUNT} -eq 0 ]
                                then
                                    ## now we move the file into the proper place
                                    ## take a checksum first..
                                    TMP_CONF_CKSUM=$(cksum ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} | awk '{print $1}');

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_OP_CKSUM -> ${TMP_OP_CKSUM}";

                                    ## and move the file..
                                    mv ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG};

                                    ## take a checksum of the new file..
                                    OP_CONF_CKSUM=$(cksum ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG} | awk '{print $1}');

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_CONF_CKSUM -> ${OP_CONF_CKSUM}";

                                    ## and now verify the checksums
                                    if [ ${TMP_CONF_CKSUM} != ${OP_CONF_CKSUM} ]
                                    then
                                        ## checksum mismatch. "ERROR".
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum mismatch for ${ZONE_CONFIG}";

                                        (( ERROR_COUNT += 1 ));
                                    else
                                        ## success!
                                        ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_CONFIG} successfully re-configured by ${REQUESTING_USER}";
                                    fi
                                else
                                    ## an "ERROR" occurred re-configuring the zone
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred during reconfiguration of dynamic zone ${ZONE_CONFIG}. Please process manually.";

                                    (( ERROR_COUNT += 1 ));
                                fi
                            else
                                ## failed to reconfig this zone. "ERROR"
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Re-configuration of ${ZONE_CONFIG} failed. Failed to modify zone configuration from slave to master. Please process manually.";

                                (( ERROR_COUNT += 1 ));
                            fi
                        else
                            ## failed to make a working copy. "ERROR"
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a working copy of ${ZONE_CONFIG}. Please process manually.";

                            (( ERROR_COUNT += 1 ));
                        fi
                    else
                        ## no backup. fail.
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a backup copy of ${ZONE_CONFIG}. Please process manually.";

                        (( ERROR_COUNT += 1 ));
                    fi
                done

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone configuration file processing complete. Continuing..";

                unset ZONE_CONFIG;

                if [ ${ERROR_COUNT} -eq 0 ]
                then
                    ## ok. the process, thus far, has been successful. lets keep going
                    ## we now need to re-configure named to operate as a slave
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone configuration processing was successful. Now re-configuring named..";

                    NAMED_CONF_CHANGENAME=$(cut -d "/" -f 5 <<< ${NAMED_CONF_FILE}).${CHANGE_NUM};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NAMED_CONF_CHANGENAME -> ${NAMED_CONF_CHANGENAME}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating backup copy of primary configuration..";

                    cp ${NAMED_CONF_FILE} ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creation complete. Validating..";

                    ## and make sure it exists..
                    if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${NAMED_CONF_CHANGENAME} ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully validated backup creation. Creating working copy..";

                        ## good, we have our backup. make a working copy
                        cp ${NAMED_CONF_FILE} ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Created working copy. Validating..";

                        ## and make sure we have our working copy..
                        if [ -s ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME} ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validated working copy. Modifying query ACL.";

                            ## good. lets make our changes
                            sed -e "s/allow-query            { ${NAMED_QUERY_ACL} };/allow-query            { any; };/g" ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME} \
                                >> ${PLUGIN_TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Query ACL modified. Validating..";

                            ## make sure its there..
                            if [ $(grep -c "allow-query            { ${NAMED_QUERY_ACL} };" ${PLUGIN_TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}) -eq 0 ]
                            then
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Query ACL validated. Modifying transfer ACL..";

                                ## it is. continue.
                                mv ${PLUGIN_TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME} ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME};
                                sed -e "s/allow-transfer         { ${NAMED_TRANSFER_ACL} };/allow-transfer         { none; };/g" ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME} \
                                    >> ${PLUGIN_TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Transfer ACL modified. Validating..";

                                ## and make sure thats there now too...
                                if [ $(grep -c "allow-transfer         { ${NAMED_TRANSFER_ACL} };" ${PLUGIN_TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}) -eq 0 ]
                                then
                                    ## poifect. this means this server is now ready to be a master nameserver.
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Transfer ACL validated. Continuing..";
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Over-writing original information..";

                                    ## make it the original copy..
                                    mv ${PLUGIN_TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME} ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                                    ## checksum it..
                                    TMP_CONF_CKSUM=$(cksum ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME} | awk '{print $1}');

                                    ## move the file in.
                                    mv ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME} ${NAMED_CONF_FILE};

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moved file into primary configuration. Validating..";

                                    ## take some checksums and compare..
                                    OP_CONF_CKSUM=$(cksum ${NAMED_CONF_FILE} | awk '{print $1}');

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_CONF_CKSUM -> ${TMP_CONF_CKSUM}";
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_CONF_CKSUM -> ${OP_CONF_CKSUM}";

                                    ## and make sure they match...
                                    if [ ${TMP_CONF_CKSUM} -eq ${OP_CONF_CKSUM} ]
                                    then
                                        ## xlnt. we're done.
                                        ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server successfully re-configured as a slave nameserver by ${REQUESTING_USER}";

                                        ## now we need to update our application config
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Modifying system configuration..";

                                        ## take a backup and make a working copy
                                        TMP_NAMED_CONFIG=${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 2 <<< ${NAMED_CONF_FILE});
                                        BKUP_NAMED_CONFIG=${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/$(cut -d "/" -f 2 <<< ${NAMED_CONF_FILE}).${CHANGE_NUM};

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_NAMED_CONFIG -> ${TMP_NAMED_CONFIG}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BKUP_NAMED_CONFIG -> ${BKUP_NAMED_CONFIG}";

                                        cp ${INTERNET_DNS_CONFIG} ${TMP_NAMED_CONFIG};
                                        cp ${INTERNET_DNS_CONFIG} ${BKUP_NAMED_CONFIG};

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copies created. Validating..";

                                        if [ -s ${BKUP_NAMED_CONFIG} ]
                                        then
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup copy confirmed. Validating operational..";

                                            ## we have our backup copy...
                                            if [ -s ${TMP_NAMED_CONFIG} ]
                                            then
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Operational copy confirmed. Continuing..";
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switching master_dns from ${NAMED_MASTER} to ${MASTER_TARGET}..";

                                                ## we have our working copy. move forward
                                                sed -e "s/master_dns = ${NAMED_MASTER}/master_dns = ${MASTER_TARGET}/" ${TMP_NAMED_CONFIG} >> ${PLUGIN_TMP_DIRECTORY}/${TMP_NAMED_CONFIG};

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch complete. Verifying..";

                                                ## make sure it was changed..
                                                if [ $(grep -c "master_dns = ${NAMED_MASTER}" ${PLUGIN_TMP_DIRECTORY}/${TMP_NAMED_CONFIG}) -eq 0 ]
                                                then
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch verified. Placing file..";

                                                    ## it was. make this the active copy
                                                    mv ${PLUGIN_TMP_DIRECTORY}/${TMP_NAMED_CONFIG} ${TMP_NAMED_CONFIG};

                                                    ## take some checksums..
                                                    TMP_CONF_CKSUM=$(cksum ${TMP_NAMED_CONFIG} | awk '{print $1}');

                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_CONF_CKSUM -> ${TMP_CONF_CKSUM}";

                                                    ## move the file into place..
                                                    mv ${TMP_NAMED_CONFIG} ${INTERNET_DNS_CONFIG};

                                                    ## make sure it was moved..
                                                    if [ ! -s ${TMP_NAMED_CONFIG} ]
                                                    then
                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File moved. Verifying..";

                                                        ## and cksum..
                                                        OP_CONF_CKSUM=$(cksum ${INTERNET_DNS_CONFIG} | awk '{print $1}');

                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_CONF_CKSUM -> ${OP_CONF_CKSUM}";

                                                        ## and make sure they agree
                                                        if [ ${TMP_CONF_CKSUM} -eq ${OP_CONF_CKSUM} ]
                                                        then
                                                            ## they do. respond with success
                                                            ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local config switch -> master_nameserver modification - performed by ${REQUESTING_USER}.";
                                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                            RETURN_CODE=0;
                                                        else
                                                            ## cksum mismatch. file failed to copy
                                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum validation failed. New configuration has not been applied.";
                                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                            RETURN_CODE=90;
                                                        fi
                                                    else
                                                        ## failed to move the tmp file into place
                                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to move the new application configuration. New configuration has not been applied.";
                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                        RETURN_CODE=44;
                                                    fi
                                                else
                                                    ## failed to update the file with the proper information
                                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to apply new configuration information. Please try again.";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    RETURN_CODE=89;
                                                fi
                                            else
                                                ## failed to create a working copy of the config file
                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a working copy of the existing configuration. Cannot continue.";
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                RETURN_CODE=47;
                                            fi
                                        else
                                            ## failed to create a backup of the config file
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to back up the existing configuration. Cannot continue.";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                            RETURN_CODE=57;
                                        fi
                                    else
                                        ## failed to copy in the new file. "ERROR"
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while re-configuring server as a slave. Please try again.";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                        RETURN_CODE=83;
                                    fi
                                else
                                    ## failure updating transfer directive. cant continue
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to modify transfer directive in named configuration. Please process manually.";
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                    RETURN_CODE=79;
                                fi
                            else
                                ## failed to update the query acl. this isnt really bad but its not good either
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to modify query ACL in named configuration. Please process manually.";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                RETURN_CODE=80;
                            fi
                        else
                            ## failed to make a working copy. "ERROR"
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create temporary file. Please process manually.";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            RETURN_CODE=47;
                        fi
                    else
                        ## failed to make a backup of primary config. "ERROR"
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create primary configuration backup. Please process manually.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        RETURN_CODE=57;
                    fi
                else
                    ## failed to re-configure one or more zones. fail
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more configuration files failed to properly update. Please process manually.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=81;
                fi
            else
                ## an "ERROR" occurred moving one or more directories. fail.
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to re-locate one or more zone directories. Please process manually.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=84;
            fi
        else
            ## zone file backup failed. "ERROR".
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a backup of existing zonefiles. Please process manually.";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=57;
        fi
    else
        ## this isnt a master server. cant reconfig a slave to be a slave
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to reconfigure master nameserver. Please process manually.";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=82;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  switch_to_master
#   DESCRIPTION:  Re-configures a slave server to become a master nameserver
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function switch_to_master
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing role-swap from slave to master..";

    ## make sure the right directories exist
    if [ -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} ]
    then
        ## make sure we're running on a real slave server
        if [ $(ls -ltr ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT} | wc -l) != 0 ]
        then
            ## good, we are.
            ## ok, they do. make sure we have our tar
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Verifying tar ${MASTER_TAR}..";

            if [ -s ${MASTER_TAR} ] && [ ${OVERRIDE_TAR} != 1 ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Tarfile verified. Decompressing.";

                ## ok, we have it. unzip it in the right place
                gzip -d < ${MASTER_TAR} | (cd ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}; tar xf -);

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Decompression complete. Verifying..";

                ## and now we should have files in there...
                FILE_COUNT=$(ls -ltr ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} | wc -l);

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILE_COUNT -> ${FILE_COUNT}";

                if [ ${FILE_COUNT} > 2 ]
                then
                    ## ok. we've got our files where we want them. great news. keep going
                    ## update the zone config files
                    ## make sure "ERROR" counter is zero
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Decompression verified. Continuing..";

                    ERROR_COUNT=0;

                    for ZONE_CONFIG in $(ls -ltr ${NAMED_ROOT}/${NAMED_CONF_DIR} | awk '{print $NF}')
                    do
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on ${ZONE_CONFIG}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating working copy..";

                        ## take a copy...
                        cp ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG} ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Working copy created. Creating backup..";

                        ## and back up the original..
                        cp ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG} ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${ZONE_CONFIG}.${CHANGE_NUM};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup created. Verifying..";

                        ## make sure we have our backup..
                        if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${ZONE_CONFIG}.${CHANGE_NUM} ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup verified. Verifying working copy..";

                            ## xlnt, make sure we have a working copy..
                            if [ -s ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} ]
                            then
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Working copy verified. Switching from master to slave..";

                                ## get a count of zones in the file..
                                ZONE_COUNT=$(grep -c "zone" ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG});

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_COUNT -> ${ZONE_COUNT}";

                                ## lets start operating. first, change master to slave
                                sed -e "s/slave/master/g" ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} >> ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG};

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch complete. Verifying..";

                                ## and make sure it was changed..
                                if [ $(grep -c ${NAMED_MASTER_ROOT} ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG}) -ne 0 ]
                                then
                                    ## great. keep going - replace the masters line with the allow-update line
                                    mv ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG} ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG};

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ZONE_NAMES..";

                                    ZONE_NAMES=$(grep "zone \"" ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} | awk '{print $2}' | cut -d "\"" -f 2);

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_NAMES set. Continuing..";

                                    for ZONE_NAME in ${ZONE_NAMES}
                                    do
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on ${ZONE_NAME}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing masters clause..";

                                        sed -e "/masters         { \"${NAMED_MASTER_ACL}\"; };/d" ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} >> ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG};

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "masters clause removed. Verifying..";

                                        ## make sure it got removed
                                        if [ $(grep -c ${NAMED_MASTER_ACL} ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG}) -ne 0 ]
                                        then
                                            ## it did not. we fail here.
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to remove masters clause in zone ${ZONE_CONFIG}. Please process manually.";

                                            (( ERROR_COUNT += 1 ));
                                        else
                                            ## success!
                                            ## we now need to update the allow-update line to none.
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "masters clause successfully removed. Modifying allow-update clause..";

                                            mv ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG} ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG};

                                            ## operate a bit differently if we're on a dynamic zone..
                                            if [ $(grep -c ${NAMED_DYNAMIC_ROOT} ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG}) -ne 0 ]
                                            then
                                                ## this is a dynamic zone. change appropriately
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Dynamic zone detected. Modifying allow-update clause..";

                                                sed -e "s/allow-update    { none; };/allow-update    { key ${DHCPD_UPDATE_KEY}; };/g" ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} \
                                                    >> ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG};

                                                if [ $(grep -c ${DHCPD_UPDATE_KEY} ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG}) -ne 0 ]
                                                then
                                                    ## successfully modified the allow-update clause. this is done.
                                                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Dynamic zone ${ZONE_NAME} successfully updated by ${REQUESTING_USER}.";

                                                    mv ${PLUGIN_TMP_DIRECTORY}/${ZONE_CONFIG} ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG};
                                                else
                                                    ## some form of failure..
                                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to modify allow-update clause in dynamic zone ${ZONE_CONFIG}. Please process manually.";

                                                    (( ERROR_COUNT += 1 ));
                                                fi
                                            else
                                                ## not a dynamic zone, so this switch is complete
                                                ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_NAME} successfully updated by ${REQUESTING_USER}.";
                                            fi
                                        fi

                                        unset START_LINE_NUMBER;
                                        unset END_LINE_NUMBER;
                                    done

                                    unset ZONE_NAMES;
                                    unset ZONE_NAME;

                                    ## ok, now its time to move the file into place
                                    ## make sure our "ERROR" counter is zero
                                    if [ ${ERROR_COUNT} -eq 0 ]
                                    then
                                        ## now we move the file into the proper place
                                        ## take a checksum first..
                                        TMP_CONF_CKSUM=$(cksum ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} | awk '{print $1}');

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_OP_CKSUM -> ${TMP_OP_CKSUM}";

                                        ## and move the file..
                                        mv ${PLUGIN_WORK_DIRECTORY}/${ZONE_CONFIG} ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG};

                                        ## take a checksum of the new file..
                                        OP_CONF_CKSUM=$(cksum ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG} | awk '{print $1}');

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_CONF_CKSUM -> ${OP_CONF_CKSUM}";

                                        ## and now verify the checksums
                                        if [ ${TMP_CONF_CKSUM} != ${OP_CONF_CKSUM} ]
                                        then
                                            ## checksum mismatch. "ERROR".
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum mismatch for ${ZONE_CONFIG}";

                                            (( ERROR_COUNT += 1 ));
                                        else
                                            ## success!
                                            ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_CONFIG} successfully re-configured by ${REQUESTING_USER}";
                                        fi
                                    else
                                        ## an "ERROR" occurred re-configuring the zone
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred during reconfiguration of dynamic zone ${ZONE_CONFIG}. Please process manually.";

                                        (( ERROR_COUNT += 1 ));
                                    fi
                                else
                                    ## failed to reconfig this zone. "ERROR"
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Re-configuration of ${ZONE_CONFIG} failed. Failed to modify zone configuration from slave to master. Please process manually.";

                                    (( ERROR_COUNT += 1 ));
                                fi
                            else
                                ## failed to make a working copy. "ERROR"
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a working copy of ${ZONE_CONFIG}. Please process manually.";

                                (( ERROR_COUNT += 1 ));
                            fi
                        else
                            ## no backup. fail.
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a backup copy of ${ZONE_CONFIG}. Please process manually.";

                            (( ERROR_COUNT += 1 ));
                        fi
                    done

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone configuration file processing complete. Continuing..";

                    unset ZONE_CONFIG;

                    if [ ${ERROR_COUNT} -eq 0 ]
                    then
                        ## ok. the process, thus far, has been successful. lets keep going
                        ## we now need to re-configure named to operate as a slave
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone configuration processing was successful. Now re-configuring named..";

                        NAMED_CONF_CHANGENAME=$(cut -d "/" -f 5 <<< ${NAMED_CONF_FILE}).${CHANGE_NUM};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NAMED_CONF_CHANGENAME -> ${NAMED_CONF_CHANGENAME}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating backup copy of primary configuration..";

                        cp ${NAMED_CONF_FILE} ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creation complete. Validating..";

                        ## and make sure it exists..
                        if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${NAMED_CONF_CHANGENAME} ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully validated backup creation. Creating working copy..";

                            ## good, we have our backup. make a working copy
                            cp ${NAMED_CONF_FILE} ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Created working copy. Validating..";

                            ## and make sure we have our working copy..
                            if [ -s ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME} ]
                            then
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validated working copy. Modifying query ACL.";

                                ## good. lets make our changes
                                sed -e "s/allow-query            { any; };/allow-query            { ${NAMED_QUERY_ACL} };/g" ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME} \
                                    >> ${PLUGIN_TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Query ACL modified. Validating..";

                                ## make sure its there..
                                if [ $(grep -c "{ ${NAMED_QUERY_ACL} };" ${PLUGIN_TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}) -ne 0 ]
                                then
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Query ACL validated. Modifying transfer ACL..";

                                    ## it is. continue.
                                    mv ${PLUGIN_TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME} ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME};
                                    sed -e "s/allow-transfer         { none; };/allow-transfer         { ${NAMED_TRANSFER_ACL} };/g" ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME} \
                                        >> ${PLUGIN_TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Transfer ACL modified. Validating..";

                                    ## and make sure thats there now too...
                                    if [ $(grep -c "{ ${NAMED_TRANSFER_ACL} }" ${PLUGIN_TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}) -ne 0 ]
                                    then
                                        ## poifect. this means this server is now ready to be a master nameserver.
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Transfer ACL validated. Continuing..";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Over-writing original information..";

                                        ## make it the original copy..
                                        mv ${PLUGIN_TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME} ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                                        ## checksum it..
                                        TMP_CONF_CKSUM=$(cksum ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME} | awk '{print $1}');

                                        ## move the file in.
                                        mv ${PLUGIN_WORK_DIRECTORY}/${NAMED_CONF_CHANGENAME} ${NAMED_CONF_FILE};

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moved file into primary configuration. Validating..";

                                        ## take some checksums and compare..
                                        OP_CONF_CKSUM=$(cksum ${NAMED_CONF_FILE} | awk '{print $1}');

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_CONF_CKSUM -> ${TMP_CONF_CKSUM}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_CONF_CKSUM -> ${OP_CONF_CKSUM}";

                                        ## and make sure they match...
                                        if [ ${TMP_CONF_CKSUM} -eq ${OP_CONF_CKSUM} ]
                                        then
                                            ## xlnt. we're done.
                                            ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server successfully re-configured as a master nameserver by ${REQUESTING_USER}";

                                            ## clean up the tmp tarfile
                                            rm -rf ${MASTER_TAR};

                                            ## now we need to update our application config
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Modifying system configuration..";

                                            ## take a backup and make a working copy
                                            TMP_NAMED_CONFIG=${PLUGIN_WORK_DIRECTORY}/$(grep named_config_file ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2- | sed -e 's/^ *//g;s/ *$//g' | cut -d "/" -f 2);
                                            BKUP_NAMED_CONFIG=${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/$(grep named_config_file ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2- | sed -e 's/^ *//g;s/ *$//g' | cut -d "/" -f 2).${CHANGE_NUM};

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_NAMED_CONFIG -> ${TMP_NAMED_CONFIG}";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BKUP_NAMED_CONFIG -> ${BKUP_NAMED_CONFIG}";

                                            cp ${INTERNET_DNS_CONFIG} ${TMP_NAMED_CONFIG};
                                            cp ${INTERNET_DNS_CONFIG} ${BKUP_NAMED_CONFIG};

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copies created. Validating..";

                                            if [ -s ${BKUP_NAMED_CONFIG} ]
                                            then
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup copy confirmed. Validating operational..";

                                                ## we have our backup copy...
                                                if [ -s ${TMP_NAMED_CONFIG} ]
                                                then
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Operational copy confirmed. Continuing..";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switching master_dns from ${NAMED_MASTER} to ${MASTER_TARGET}..";

                                                    ## we have our working copy. move forward
                                                    sed -e "s/master_dns = ${NAMED_MASTER}/master_dns = ${MASTER_TARGET}/" ${TMP_NAMED_CONFIG} >> ${PLUGIN_TMP_DIRECTORY}/${TMP_NAMED_CONFIG};

                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch complete. Verifying..";

                                                    ## make sure it was changed..
                                                    if [ $(grep -c "master_dns = ${NAMED_MASTER}" ${PLUGIN_TMP_DIRECTORY}/${TMP_NAMED_CONFIG}) -eq 0 ]
                                                    then
                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch verified. Placing file..";

                                                        ## it was. make this the active copy
                                                        mv ${PLUGIN_TMP_DIRECTORY}/${TMP_NAMED_CONFIG} ${TMP_NAMED_CONFIG};

                                                        ## take some checksums..
                                                        TMP_CONF_CKSUM=$(cksum ${TMP_NAMED_CONFIG} | awk '{print $1}');

                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_CONF_CKSUM -> ${TMP_CONF_CKSUM}";

                                                        ## move the file into place..
                                                        mv ${TMP_NAMED_CONFIG} ${INTERNET_DNS_CONFIG};

                                                        ## make sure it was moved..
                                                        if [ ! -s ${TMP_NAMED_CONFIG} ]
                                                        then
                                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File moved. Verifying..";

                                                            ## and cksum..
                                                            OP_CONF_CKSUM=$(cksum ${INTERNET_DNS_CONFIG} | awk '{print $1}');

                                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_CONF_CKSUM -> ${OP_CONF_CKSUM}";

                                                            ## and make sure they agree
                                                            if [ ${TMP_CONF_CKSUM} -eq ${OP_CONF_CKSUM} ]
                                                            then
                                                                ## they do. respond with success
                                                                ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local config switch -> master_nameserver modification - performed by ${REQUESTING_USER}.";
                                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                                RETURN_CODE=0;
                                                            else
                                                                ## cksum mismatch. file failed to copy
                                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum validation failed. New configuration has not been applied.";
                                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                                RETURN_CODE=90;
                                                            fi
                                                        else
                                                            ## failed to move the tmp file into place
                                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum validation failed. New configuration has not been applied.";
                                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                            RETURN_CODE=44;
                                                        fi
                                                    else
                                                        ## failed to update the file with the proper information
                                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to apply new configuration information. Please try again.";
                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                        RETURN_CODE=89;
                                                    fi
                                                else
                                                    ## failed to create a working copy of the config file
                                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a working copy of the existing configuration. Cannot continue.";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    RETURN_CODE=47;
                                                fi
                                            else
                                                ## failed to create a backup of the config file
                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to back up the existing configuration. Cannot continue.";
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                RETURN_CODE=57;
                                            fi
                                        else
                                            ## failed to copy in the new file. "ERROR"
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while re-configuring server as a master. Please try again.";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                            RETURN_CODE=83;
                                        fi
                                    else
                                        ## failure updating transfer directive. cant continue
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to modify transfer directive in named configuration. Please process manually.";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                        RETURN_CODE=79;
                                    fi
                                else
                                    ## failed to update the query acl. this isnt really bad but its not good either
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to modify query ACL in named configuration. Please process manually.";
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                    RETURN_CODE=80;
                                fi
                            else
                                ## failed to make a working copy. "ERROR"
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create temporary file. Please process manually.";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                RETURN_CODE=47;
                            fi
                        else
                            ## failed to make a backup of primary config. "ERROR"
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create primary configuration backup. Please process manually.";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            RETURN_CODE=57;
                        fi
                    else
                        ## failed to re-configure one or more zones. fail
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more configuration files failed to properly update. Please process manually.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        RETURN_CODE=81;
                    fi
                else
                    ## ok, it didnt untar correctly. something broke. return an "ERROR"
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Decompression of master tarfile failed. Unable to continue.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=85;
                fi
            else
                ## zone file backup failed. "ERROR".
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a backup of existing zonefiles. Please process manually.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=57;
            fi
        else
            ## isnt a slave server. cant re-configure a master to be
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to reconfigure slave nameserver. Please process manually.";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=82;
        fi
    else
        ## we dont have a master directory. fail.
        ## we dont have a master directory here
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The master directory does not exist. Please process manually.";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=23;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

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

    echo "${THIS_CNAME} - Process a service re-configuration from a slave to a master\n";
    echo "Usage: ${THIS_CNAME} [ -p <tarfile> ] [ -s ] [ -f ] [ -i <requesting user> ] [ -c <change order> ] [ -e ] [ -h|-? ]
    -p         -> Re-configure server as a master server. If provided, a tarfile name must also be provided.
    -s         -> Re-configure server as a slave server
    -f         -> Force re-configuration if master tarfile does not exist
    -t         -> The name of the new master nameserver
    -i         -> The user performing the request
    -c         -> The change order associated with this request
    -e         -> Execute processing
    -h|-?      -> Show this help\n";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

[ ${#} -eq 0 ] && usage && RETURN_CODE=${?};

while getopts ":p:sft:i:c:eh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        p)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting request to swap slave to master..";

            ## Capture the site root
            REQUEST_TYPE=set_master;
            typeset -l MASTER_TAR=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_TYPE -> ${REQUEST_TYPE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MASTER_TAR -> ${MASTER_TAR}";
            ;;
        s)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting request to swap master to slave..";

            ## Capture the site root
            REQUEST_TYPE=set_slave;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_TYPE -> ${REQUEST_TYPE}";
            ;;
        f)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting tar override";

            ## Capture the site root
            OVERRIDE_TAR=1;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OVERRIDE_TAR -> ${OVERRIDE_TAR}";
            ;;
        t)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting MASTER_TARGET";

            ## Capture the site root
            typeset -l MASTER_TARGET=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MASTER_TARGET -> ${MASTER_TARGET}";
            ;;
        i)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting REQUESTING_USER..";

            ## Capture the change control
            typeset -u REQUESTING_USER=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUESTING_USER -> ${REQUESTING_USER}";
            ;;
        c)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            ## Capture the change control
            typeset -u CHANGE_NUM=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${CHANGE_NUM}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change order was provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=17;
            elif [ -z "${REQUESTING_USER}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requestors username was not provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=20;
            elif [ -z "${MASTER_TARGET}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The name of the new target master was not provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=7;
            else
                ## We have enough information to process the request, continue
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                if [ ! -z "${REQUEST_TYPE}" ]
                then
                    if [ "${REQUEST_TYPE}" = "set_slave" ]
                    then
                        switch_to_slave && RETURN_CODE=${?};
                    else
                        if [ -z "${MASTER_TAR}" ]
                        then
                            ## we're missing the tar file argument. "ERROR"
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_TYPE is blank. Cannot continue.";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            usage && RETURN_CODE=${?};
                        else
                            [ -z "${OVERRIDE_TAR}" ] && OVERRIDE_TAR=0;

                            switch_to_master && RETURN_CODE=${?};
                        fi
                    fi
                else
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_TYPE is blank. Cannot continue.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    usage && RETURN_CODE=${?};
                fi
            fi
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage && RETURN_CODE=${?};
            ;;
    esac
done

trap "unlockProcess ${LOCKFILE} ${$}; return ${RETURN_CODE}" INT TERM EXIT;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

[ -z "${RETURN_CODE}" ] && echo "1" || echo "${RETURN_CODE}";
[ -z "${RETURN_CODE}" ] && return 1 || return "${RETURN_CODE}";
