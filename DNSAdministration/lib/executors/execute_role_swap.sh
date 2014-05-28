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

trap "${APP_ROOT}/${LIB_DIRECTORY}/lock.sh unlock ${$}; exit" INT TERM EXIT;

## Application constants
PLUGIN_NAME="dnsadmin";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#          NAME:  switch_to_slave
#   DESCRIPTION:  Re-configures a master server to become a slave nameserver
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function switch_to_slave
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing role-swap from master to slave..";

    TARFILE_NAME=SWAP_SLAVE.${CHANGE_NUM}.$(date +"%m-%d-%Y").${IUSER_AUDIT}.tar.gz;

    ## make sure the right directories exist
    if [ -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} ]
    then
        ## make sure we're running on a real master server
        if [ $(ls -ltr ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} | wc -l) != 0 ]
        then
            ## good, we are. in the master switch, we check for a tarfile. we dont need a tarfile, but
            ## we do need to move the zonefiles from the master dir to the slave dir
            ## take a backup first
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backing up master zones..";

            (cd ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}; tar cf - *) | gzip -c > ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete. Verifying..";

            ## make sure we have it
            if [ -s ${APP_ROOT}/${BACKUP_DIRECTORY}/${TARFILE_NAME} ]
            then
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup successfully verified. Performing pre-count of zone directories..";

                ## set error count to zero
                ERROR_COUNT=0;

                ## take a count of the directories in the master directory
                PRE_MOVE_MASTER_COUNT=$(ls -ltr ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} | wc -l);

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRE_MOVE_MASTER_COUNT -> ${PRE_MOVE_MASTER_COUNT}";

                ## good. lets keep going. move the zone files into the master directory
                for ZONE_DIRECTORY in $(ls -ltr ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} | grep -v ${LOCAL_FORWARD_ZONE} | grep -v ${LOCAL_REVERSE_ZONE} | awk '{print $9}')
                do
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on ${ZONE_DIRECTORY}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Performing pre-verification count..";

                    ZONE_PRE_COUNT=$(ls -ltrR ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${ZONE_DIRECTORY} | wc -l);

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_PRE_COUNT -> ${ZONE_PRE_COUNT}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moving directory ${ZONE_DIRECTORY}..";

                    mv ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${ZONE_DIRECTORY} ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${ZONE_DIRECTORY};

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Move complete. Verifying..";

                    ## make sure it copied
                    if [ -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${ZONE_DIRECTORY} ]
                    then
                        ## make sure the contents are valid
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Performing post-verification count..";

                        ZONE_POST_COUNT=$(ls -ltrR ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${ZONE_DIRECTORY} | wc -l);

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_POST_COUNT -> ${ZONE_POST_COUNT}";

                        ## make sure the counts match...
                        if [ ${ZONE_PRE_COUNT} -eq ${ZONE_POST_COUNT} ]
                        then
                            ## they do. we're good here, keep going
                            ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Re-location of ${ZONE_DIRECTORY} completed by ${IUSER_AUDIT}";
                        else
                            ## copy failed or something else is going on.
                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Re-location of ${ZONE_DIRECTORY} failed. Please process manually.";

                            (( ERROR_COUNT += 1 ));
                        fi
                    else
                        ## copy failed. directory doesnt exist
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Re-location of ${ZONE_DIRECTORY} failed. Please process manually.";

                        (( ERROR_COUNT += 1 ));
                    fi

                    unset ZONE_PRE_COUNT;
                    unset ZONE_POST_COUNT;
                done

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "All zone moves complete. Checking for errors..";

                if [ ${ERROR_COUNT} -eq 0 ]
                then
                    ## all operations were successfully performed. move forward
                    ## next step is to re-configure the zone configuration files to operate as slaves
                    ## update the zone config files
                    ## make sure error counter is zero
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No errors encountered during zone move. Continuing..";

                    ERROR_COUNT=0;

                    for ZONE_CONFIG in $(ls -ltr ${NAMED_ROOT}/${NAMED_CONF_DIR} | awk '{print $9}')
                    do
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on ${ZONE_CONFIG}";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating working copy..";

                        ## take a copy...
                        cp ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG} ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG};

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Working copy created. Creating backup..";

                        ## and back up the original..
                        cp ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG} ${APP_ROOT}/${BACKUP_DIRECTORY}/${ZONE_CONFIG}.${CHANGE_NUM};

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup created. Verifying..";

                        ## make sure we have our backup..
                        if [ -s ${APP_ROOT}/${BACKUP_DIRECTORY}/${ZONE_CONFIG}.${CHANGE_NUM} ]
                        then
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup verified. Verifying working copy..";

                            ## xlnt, make sure we have a working copy..
                            if [ -s ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} ]
                            then
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Working copy verified. Switching from master to slave..";

                                ## get a count of zones in the file..
                                ZONE_COUNT=$(grep -c "zone" ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG});

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_COUNT -> ${ZONE_COUNT}";

                                ## lets start operating. first, change slave to master
                                sed -e "s/master/slave/g" ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} >> ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp;

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch complete. Verifying..";

                                ## and make sure it was changed..
                                if [ $(grep -c ${NAMED_MASTER_ROOT} ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp) -eq 0 ]
                                then
                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch verified. Adding masters clause..";

                                    ## great. keep going - replace the masters line with the allow-update line
                                    mv ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG};

                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ZONE_NAMES..";

                                    ZONE_NAMES=$(grep "zone \"" ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} | awk '{print $2}' | cut -d "\"" -f 2);

                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_NAMES set. Continuing..";

                                    for ZONE_NAME in ${ZONE_NAMES}
                                    do
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on ${ZONE_NAME}";

                                        START_LINE_NUMBER=$(sed -n "/zone \"${ZONE_NAME}\" IN {/=" ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG});
                                        END_LINE_NUMBER=$(expr ${START_LINE_NUMBER} + 3);

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "START_LINE_NUMBER -> ${START_LINE_NUMBER}";
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "END_LINE_NUMBER -> ${END_LINE_NUMBER}";
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Placing masters clause..";

                                        ## solaris is all kinds of messed up i guess. what linux will do with
                                        ## <code>sed "${END_LINE_NUMBER}a\    masters         { \"${NAMED_MASTER_ACL}\"; };" ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} > ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp;</code>
                                        ## solaris cant. not sure why. so we go through this HIGHLY convoluted process here.
                                        sed "${END_LINE_NUMBER}a\\
                                            masters         { \"${NAMED_MASTER_ACL}\"; };" ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} > ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp;

                                        ## make it the target again...
                                        mv ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG};

                                        ## and then replace the 800 million spaces that got added
                                        sed "s/                                            masters         {/    masters           {/g" ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} > ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp;

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "masters clause placed. Verifying..";

                                        ## make sure it got placed
                                        if [ $(grep -n "masters         { \"${NAMED_MASTER_ACL}\"; };" ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp | grep -c $(expr ${END_LINE_NUMBER} + 1)) -eq 0 ]
                                        then
                                            ## it did not. we fail here.
                                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to place masters clause in dynamic zone ${ZONE_CONFIG}. Please process manually.";

                                            (( ERROR_COUNT += 1 ));
                                        else
                                            ## success!
                                            ## we now need to update the allow-update line to none.
                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "masters clause successfully added. Modifying allow-update clause..";

                                            mv ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG};

                                            ## operate a bit differently if we're on a dynamic zone..
                                            if [ $(grep -c ${NAMED_DYNAMIC_ROOT} ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}) -ne 0 ]
                                            then
                                                ## this is a dynamic zone. change appropriately
                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Dynamic zone detected. Modifying allow-update clause..";

                                                sed -e "s/allow-update    { key ${DHCPD_UPDATE_KEY}; };/allow-update    { none; };/g" ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} \
                                                    >> ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp;

                                                if [ $(grep -c ${DHCPD_UPDATE_KEY} ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp) -eq 0 ]
                                                then
                                                    ## successfully modified the allow-update clause. this is done.
                                                    ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Dynamic zone ${ZONE_NAME} successfully updated by ${IUSER_AUDIT}.";

                                                    mv ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG};
                                                else
                                                    ## some form of failure..
                                                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to place modify allow-update clause in dynamic zone ${ZONE_CONFIG}. Please process manually.";

                                                    (( ERROR_COUNT += 1 ));
                                                fi
                                            else
                                                ## not a dynamic zone, so this switch is complete
                                                ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_NAME} successfully updated by ${IUSER_AUDIT}.";
                                            fi
                                        fi

                                        unset START_LINE_NUMBER;
                                        unset END_LINE_NUMBER;
                                    done

                                    unset ZONE_NAMES;
                                    unset ZONE_NAME;

                                    ## ok, now its time to move the file into place
                                    ## make sure our error counter is zero
                                    if [ ${ERROR_COUNT} -eq 0 ]
                                    then
                                        ## now we move the file into the proper place
                                        ## take a checksum first..
                                        TMP_CONF_CKSUM=$(cksum ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} | awk '{print $1}');

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_OP_CKSUM -> ${TMP_OP_CKSUM}";

                                        ## and move the file..
                                        mv ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG};

                                        ## take a checksum of the new file..
                                        OP_CONF_CKSUM=$(cksum ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG} | awk '{print $1}');

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_CONF_CKSUM -> ${OP_CONF_CKSUM}";

                                        ## and now verify the checksums
                                        if [ ${TMP_CONF_CKSUM} != ${OP_CONF_CKSUM} ]
                                        then
                                            ## checksum mismatch. error.
                                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum mismatch for ${ZONE_CONFIG}";

                                            (( ERROR_COUNT += 1 ));
                                        else
                                            ## success!
                                            ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_CONFIG} successfully re-configured by ${IUSER_AUDIT}";
                                        fi
                                    else
                                        ## an error occurred re-configuring the zone
                                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred during reconfiguration of dynamic zone ${ZONE_CONFIG}. Please process manually.";

                                        (( ERROR_COUNT += 1 ));
                                    fi
                                else
                                    ## failed to reconfig this zone. error
                                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Re-configuration of ${ZONE_CONFIG} failed. Failed to modify zone configuration from slave to master. Please process manually.";

                                    (( ERROR_COUNT += 1 ));
                                fi
                            else
                                ## failed to make a working copy. error
                                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a working copy of ${ZONE_CONFIG}. Please process manually.";

                                (( ERROR_COUNT += 1 ));
                            fi
                        else
                            ## no backup. fail.
                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a backup copy of ${ZONE_CONFIG}. Please process manually.";

                            (( ERROR_COUNT += 1 ));
                        fi
                    done

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone configuration file processing complete. Continuing..";

                    unset ZONE_CONFIG;

                    if [ ${ERROR_COUNT} -eq 0 ]
                    then
                        ## ok. the process, thus far, has been successful. lets keep going
                        ## we now need to re-configure named to operate as a slave
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone configuration processing was successful. Now re-configuring named..";

                        NAMED_CONF_CHANGENAME=$(echo ${NAMED_CONF_FILE} | cut -d "/" -f 5).${CHANGE_NUM};

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NAMED_CONF_CHANGENAME -> ${NAMED_CONF_CHANGENAME}";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating backup copy of primary configuration..";

                        cp ${NAMED_CONF_FILE} ${APP_ROOT}/${BACKUP_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creation complete. Validating..";

                        ## and make sure it exists..
                        if [ -s ${APP_ROOT}/${BACKUP_DIRECTORY}/${NAMED_CONF_CHANGENAME} ]
                        then
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully validated backup creation. Creating working copy..";

                            ## good, we have our backup. make a working copy
                            cp ${NAMED_CONF_FILE} ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Created working copy. Validating..";

                            ## and make sure we have our working copy..
                            if [ -s ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME} ]
                            then
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validated working copy. Modifying query ACL.";

                                ## good. lets make our changes
                                sed -e "s/allow-query            { ${NAMED_QUERY_ACL} };/allow-query            { any; };/g" ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME} \
                                    >> ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}.tmp;

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Query ACL modified. Validating..";

                                ## make sure its there..
                                if [ $(grep -c "allow-query            { ${NAMED_QUERY_ACL} };" ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}.tmp) -eq 0 ]
                                then
                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Query ACL validated. Modifying transfer ACL..";

                                    ## it is. continue.
                                    mv ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}.tmp ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME};
                                    sed -e "s/allow-transfer         { ${NAMED_TRANSFER_ACL} };/allow-transfer         { none; };/g" ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME} \
                                        >> ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}.tmp;

                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Transfer ACL modified. Validating..";

                                    ## and make sure thats there now too...
                                    if [ $(grep -c "allow-transfer         { ${NAMED_TRANSFER_ACL} };" ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}.tmp) -eq 0 ]
                                    then
                                        ## poifect. this means this server is now ready to be a master nameserver.
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Transfer ACL validated. Continuing..";
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Over-writing original information..";

                                        ## make it the original copy..
                                        mv ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}.tmp ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                                        ## checksum it..
                                        TMP_CONF_CKSUM=$(cksum ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME} | awk '{print $1}');

                                        ## move the file in.
                                        mv ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME} ${NAMED_CONF_FILE};

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moved file into primary configuration. Validating..";

                                        ## take some checksums and compare..
                                        OP_CONF_CKSUM=$(cksum ${NAMED_CONF_FILE} | awk '{print $1}');

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_CONF_CKSUM -> ${TMP_CONF_CKSUM}";
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_CONF_CKSUM -> ${OP_CONF_CKSUM}";

                                        ## and make sure they match...
                                        if [ ${TMP_CONF_CKSUM} -eq ${OP_CONF_CKSUM} ]
                                        then
                                            ## xlnt. we're done.
                                            ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server successfully re-configured as a slave nameserver by ${IUSER_AUDIT}";

                                            ## now we need to update our application config
                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Modifying system configuration..";

                                            ## take a backup and make a working copy
                                            TMP_NAMED_CONFIG=${APP_ROOT}/${TMP_DIRECTORY}/$(grep named_config_file ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2- | sed 's| ||g' | cut -d "/" -f 2);
                                            BKUP_NAMED_CONFIG=${APP_ROOT}/${BACKUP_DIRECTORY}/$(grep named_config_file ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2- | sed 's| ||g' | cut -d "/" -f 2).${CHANGE_NUM};

                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_NAMED_CONFIG -> ${TMP_NAMED_CONFIG}";
                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BKUP_NAMED_CONFIG -> ${BKUP_NAMED_CONFIG}";

                                            cp ${INTERNET_DNS_CONFIG} ${TMP_NAMED_CONFIG};
                                            cp ${INTERNET_DNS_CONFIG} ${BKUP_NAMED_CONFIG};

                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copies created. Validating..";

                                            if [ -s ${BKUP_NAMED_CONFIG} ]
                                            then
                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup copy confirmed. Validating operational..";

                                                ## we have our backup copy...
                                                if [ -s ${TMP_NAMED_CONFIG} ]
                                                then
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Operational copy confirmed. Continuing..";
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switching master_dns from ${NAMED_MASTER} to ${MASTER_TARGET}..";

                                                    ## we have our working copy. move forward
                                                    sed -e "s/master_dns = ${NAMED_MASTER}/master_dns = ${MASTER_TARGET}/" ${TMP_NAMED_CONFIG} >> ${TMP_NAMED_CONFIG}.tmp;

                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch complete. Verifying..";

                                                    ## make sure it was changed..
                                                    if [ $(grep -c "master_dns = ${NAMED_MASTER}" ${TMP_NAMED_CONFIG}.tmp) -eq 0 ]
                                                    then
                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch verified. Placing file..";

                                                        ## it was. make this the active copy
                                                        mv ${TMP_NAMED_CONFIG}.tmp ${TMP_NAMED_CONFIG};

                                                        ## take some checksums..
                                                        TMP_CONF_CKSUM=$(cksum ${TMP_NAMED_CONFIG} | awk '{print $1}');

                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_CONF_CKSUM -> ${TMP_CONF_CKSUM}";

                                                        ## move the file into place..
                                                        mv ${TMP_NAMED_CONFIG} ${INTERNET_DNS_CONFIG};

                                                        ## make sure it was moved..
                                                        if [ ! -s ${TMP_NAMED_CONFIG} ]
                                                        then
                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File moved. Verifying..";

                                                            ## and cksum..
                                                            OP_CONF_CKSUM=$(cksum ${INTERNET_DNS_CONFIG} | awk '{print $1}');

                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_CONF_CKSUM -> ${OP_CONF_CKSUM}";

                                                            ## and make sure they agree
                                                            if [ ${TMP_CONF_CKSUM} -eq ${OP_CONF_CKSUM} ]
                                                            then
                                                                ## they do. respond with success
                                                                ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local config switch -> master_nameserver modification - performed by ${IUSER_AUDIT}.";
                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                                RETURN_CODE=0;
                                                            else
                                                                ## cksum mismatch. file failed to copy
                                                                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum validation failed. New configuration has not been applied.";
                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                                RETURN_CODE=90;
                                                            fi
                                                        else
                                                            ## failed to move the tmp file into place
                                                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to move the new application configuration. New configuration has not been applied.";
                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                            RETURN_CODE=44;
                                                        fi
                                                    else
                                                        ## failed to update the file with the proper information
                                                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to apply new configuration information. Please try again.";
                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                        RETURN_CODE=89;
                                                    fi
                                                else
                                                    ## failed to create a working copy of the config file
                                                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a working copy of the existing configuration. Cannot continue.";
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    RETURN_CODE=47;
                                                fi
                                            else
                                                ## failed to create a backup of the config file
                                                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to back up the existing configuration. Cannot continue.";
                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                RETURN_CODE=57;
                                            fi
                                        else
                                            ## failed to copy in the new file. error
                                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while re-configuring server as a slave. Please try again.";
                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                            RETURN_CODE=83;
                                        fi
                                    else
                                        ## failure updating transfer directive. cant continue
                                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to modify transfer directive in named configuration. Please process manually.";
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                        RETURN_CODE=79;
                                    fi
                                else
                                    ## failed to update the query acl. this isnt really bad but its not good either
                                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to modify query ACL in named configuration. Please process manually.";
                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                    RETURN_CODE=80;
                                fi
                            else
                                ## failed to make a working copy. error
                                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create temporary file. Please process manually.";
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                RETURN_CODE=47;
                            fi
                        else
                            ## failed to make a backup of primary config. error
                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create primary configuration backup. Please process manually.";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            RETURN_CODE=57;
                        fi
                    else
                        ## failed to re-configure one or more zones. fail
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more configuration files failed to properly update. Please process manually.";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        RETURN_CODE=81;
                    fi
                else
                    ## an error occurred moving one or more directories. fail.
                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to re-locate one or more zone directories. Please process manually.";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=84;
                fi
            else
                ## zone file backup failed. error.
                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a backup of existing zonefiles. Please process manually.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=57;
            fi
        else
            ## this isnt a master server. cant reconfig a slave to be a slave
            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to reconfigure master nameserver. Please process manually.";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=82;
        fi
    else
        ## we dont have a master directory. fail.
        ## we dont have a master directory here
        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The master directory does not exist. Please process manually.";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=23;
    fi
}

#===  FUNCTION  ===============================================================
#          NAME:  switch_to_master
#   DESCRIPTION:  Re-configures a slave server to become a master nameserver
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function switch_to_master
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing role-swap from slave to master..";

    ## make sure the right directories exist
    if [ -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} ]
    then
        ## make sure we're running on a real slave server
        if [ $(ls -ltr ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT} | wc -l) != 0 ]
        then
            ## good, we are.
            ## ok, they do. make sure we have our tar
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Verifying tar ${MASTER_TAR}..";

            if [ -s ${MASTER_TAR} ] && [ ${OVERRIDE_TAR} != 1 ]
            then
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Tarfile verified. Decompressing.";

                ## ok, we have it. unzip it in the right place
                gzip -d < ${MASTER_TAR} | (cd ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}; tar xf -);

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Decompression complete. Verifying..";

                ## and now we should have files in there...
                FILE_COUNT=$(ls -ltr ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} | wc -l);

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILE_COUNT -> ${FILE_COUNT}";

                if [ ${FILE_COUNT} > 2 ]
                then
                    ## ok. we've got our files where we want them. great news. keep going
                    ## update the zone config files
                    ## make sure error counter is zero
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Decompression verified. Continuing..";

                    ERROR_COUNT=0;

                    for ZONE_CONFIG in $(ls -ltr ${NAMED_ROOT}/${NAMED_CONF_DIR} | awk '{print $9}')
                    do
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on ${ZONE_CONFIG}";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating working copy..";

                        ## take a copy...
                        cp ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG} ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG};

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Working copy created. Creating backup..";

                        ## and back up the original..
                        cp ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG} ${APP_ROOT}/${BACKUP_DIRECTORY}/${ZONE_CONFIG}.${CHANGE_NUM};

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup created. Verifying..";

                        ## make sure we have our backup..
                        if [ -s ${APP_ROOT}/${BACKUP_DIRECTORY}/${ZONE_CONFIG}.${CHANGE_NUM} ]
                        then
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup verified. Verifying working copy..";

                            ## xlnt, make sure we have a working copy..
                            if [ -s ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} ]
                            then
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Working copy verified. Switching from master to slave..";

                                ## get a count of zones in the file..
                                ZONE_COUNT=$(grep -c "zone" ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG});

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_COUNT -> ${ZONE_COUNT}";

                                ## lets start operating. first, change master to slave
                                sed -e "s/slave/master/g" ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} >> ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp;

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch complete. Verifying..";

                                ## and make sure it was changed..
                                if [ $(grep -c ${NAMED_MASTER_ROOT} ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp) -ne 0 ]
                                then
                                    ## great. keep going - replace the masters line with the allow-update line
                                    mv ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG};

                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ZONE_NAMES..";

                                    ZONE_NAMES=$(grep "zone \"" ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} | awk '{print $2}' | cut -d "\"" -f 2);

                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_NAMES set. Continuing..";

                                    for ZONE_NAME in ${ZONE_NAMES}
                                    do
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on ${ZONE_NAME}";
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing masters clause..";

                                        sed "/masters         { \"${NAMED_MASTER_ACL}\"; };/d" ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} >> ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp;

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "masters clause removed. Verifying..";

                                        ## make sure it got removed
                                        if [ $(grep -c ${NAMED_MASTER_ACL} ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp) -ne 0 ]
                                        then
                                            ## it did not. we fail here.
                                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to remove masters clause in zone ${ZONE_CONFIG}. Please process manually.";

                                            (( ERROR_COUNT += 1 ));
                                        else
                                            ## success!
                                            ## we now need to update the allow-update line to none.
                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "masters clause successfully removed. Modifying allow-update clause..";

                                            mv ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG};

                                            ## operate a bit differently if we're on a dynamic zone..
                                            if [ $(grep -c ${NAMED_DYNAMIC_ROOT} ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}) -ne 0 ]
                                            then
                                                ## this is a dynamic zone. change appropriately
                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Dynamic zone detected. Modifying allow-update clause..";

                                                sed -e "s/allow-update    { none; };/allow-update    { key ${DHCPD_UPDATE_KEY}; };/g" ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} \
                                                    >> ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp;

                                                if [ $(grep -c ${DHCPD_UPDATE_KEY} ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp) -ne 0 ]
                                                then
                                                    ## successfully modified the allow-update clause. this is done.
                                                    ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Dynamic zone ${ZONE_NAME} successfully updated by ${IUSER_AUDIT}.";

                                                    mv ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG}.tmp ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG};
                                                else
                                                    ## some form of failure..
                                                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to modify allow-update clause in dynamic zone ${ZONE_CONFIG}. Please process manually.";

                                                    (( ERROR_COUNT += 1 ));
                                                fi
                                            else
                                                ## not a dynamic zone, so this switch is complete
                                                ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_NAME} successfully updated by ${IUSER_AUDIT}.";
                                            fi
                                        fi

                                        unset START_LINE_NUMBER;
                                        unset END_LINE_NUMBER;
                                    done

                                    unset ZONE_NAMES;
                                    unset ZONE_NAME;

                                    ## ok, now its time to move the file into place
                                    ## make sure our error counter is zero
                                    if [ ${ERROR_COUNT} -eq 0 ]
                                    then
                                        ## now we move the file into the proper place
                                        ## take a checksum first..
                                        TMP_CONF_CKSUM=$(cksum ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} | awk '{print $1}');

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_OP_CKSUM -> ${TMP_OP_CKSUM}";

                                        ## and move the file..
                                        mv ${APP_ROOT}/${TMP_DIRECTORY}/${ZONE_CONFIG} ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG};

                                        ## take a checksum of the new file..
                                        OP_CONF_CKSUM=$(cksum ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONFIG} | awk '{print $1}');

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_CONF_CKSUM -> ${OP_CONF_CKSUM}";

                                        ## and now verify the checksums
                                        if [ ${TMP_CONF_CKSUM} != ${OP_CONF_CKSUM} ]
                                        then
                                            ## checksum mismatch. error.
                                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum mismatch for ${ZONE_CONFIG}";

                                            (( ERROR_COUNT += 1 ));
                                        else
                                            ## success!
                                            ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_CONFIG} successfully re-configured by ${IUSER_AUDIT}";
                                        fi
                                    else
                                        ## an error occurred re-configuring the zone
                                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred during reconfiguration of dynamic zone ${ZONE_CONFIG}. Please process manually.";

                                        (( ERROR_COUNT += 1 ));
                                    fi
                                else
                                    ## failed to reconfig this zone. error
                                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Re-configuration of ${ZONE_CONFIG} failed. Failed to modify zone configuration from slave to master. Please process manually.";

                                    (( ERROR_COUNT += 1 ));
                                fi
                            else
                                ## failed to make a working copy. error
                                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a working copy of ${ZONE_CONFIG}. Please process manually.";

                                (( ERROR_COUNT += 1 ));
                            fi
                        else
                            ## no backup. fail.
                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a backup copy of ${ZONE_CONFIG}. Please process manually.";

                            (( ERROR_COUNT += 1 ));
                        fi
                    done

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone configuration file processing complete. Continuing..";

                    unset ZONE_CONFIG;

                    if [ ${ERROR_COUNT} -eq 0 ]
                    then
                        ## ok. the process, thus far, has been successful. lets keep going
                        ## we now need to re-configure named to operate as a slave
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone configuration processing was successful. Now re-configuring named..";

                        NAMED_CONF_CHANGENAME=$(echo ${NAMED_CONF_FILE} | cut -d "/" -f 5).${CHANGE_NUM};

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NAMED_CONF_CHANGENAME -> ${NAMED_CONF_CHANGENAME}";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating backup copy of primary configuration..";

                        cp ${NAMED_CONF_FILE} ${APP_ROOT}/${BACKUP_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creation complete. Validating..";

                        ## and make sure it exists..
                        if [ -s ${APP_ROOT}/${BACKUP_DIRECTORY}/${NAMED_CONF_CHANGENAME} ]
                        then
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully validated backup creation. Creating working copy..";

                            ## good, we have our backup. make a working copy
                            cp ${NAMED_CONF_FILE} ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Created working copy. Validating..";

                            ## and make sure we have our working copy..
                            if [ -s ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME} ]
                            then
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validated working copy. Modifying query ACL.";

                                ## good. lets make our changes
                                sed -e "s/allow-query            { any; };/allow-query            { ${NAMED_QUERY_ACL} };/g" ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME} \
                                    >> ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}.tmp;

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Query ACL modified. Validating..";

                                ## make sure its there..
                                if [ $(grep -c "{ ${NAMED_QUERY_ACL} };" ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}.tmp) -ne 0 ]
                                then
                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Query ACL validated. Modifying transfer ACL..";

                                    ## it is. continue.
                                    mv ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}.tmp ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME};
                                    sed -e "s/allow-transfer         { none; };/allow-transfer         { ${NAMED_TRANSFER_ACL} };/g" ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME} \
                                        >> ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}.tmp;

                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Transfer ACL modified. Validating..";

                                    ## and make sure thats there now too...
                                    if [ $(grep -c "{ ${NAMED_TRANSFER_ACL} }" ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}.tmp) -ne 0 ]
                                    then
                                        ## poifect. this means this server is now ready to be a master nameserver.
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Transfer ACL validated. Continuing..";
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Over-writing original information..";

                                        ## make it the original copy..
                                        mv ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME}.tmp ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME};

                                        ## checksum it..
                                        TMP_CONF_CKSUM=$(cksum ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME} | awk '{print $1}');

                                        ## move the file in.
                                        mv ${APP_ROOT}/${TMP_DIRECTORY}/${NAMED_CONF_CHANGENAME} ${NAMED_CONF_FILE};

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moved file into primary configuration. Validating..";

                                        ## take some checksums and compare..
                                        OP_CONF_CKSUM=$(cksum ${NAMED_CONF_FILE} | awk '{print $1}');

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_CONF_CKSUM -> ${TMP_CONF_CKSUM}";
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_CONF_CKSUM -> ${OP_CONF_CKSUM}";

                                        ## and make sure they match...
                                        if [ ${TMP_CONF_CKSUM} -eq ${OP_CONF_CKSUM} ]
                                        then
                                            ## xlnt. we're done.
                                            ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server successfully re-configured as a master nameserver by ${IUSER_AUDIT}";

                                            ## clean up the tmp tarfile
                                            rm -rf ${MASTER_TAR};

                                            ## now we need to update our application config
                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Modifying system configuration..";

                                            ## take a backup and make a working copy
                                            TMP_NAMED_CONFIG=${APP_ROOT}/${TMP_DIRECTORY}/$(grep named_config_file ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2- | sed 's| ||g' | cut -d "/" -f 2);
                                            BKUP_NAMED_CONFIG=${APP_ROOT}/${BACKUP_DIRECTORY}/$(grep named_config_file ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2- | sed 's| ||g' | cut -d "/" -f 2).${CHANGE_NUM};

                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_NAMED_CONFIG -> ${TMP_NAMED_CONFIG}";
                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BKUP_NAMED_CONFIG -> ${BKUP_NAMED_CONFIG}";

                                            cp ${INTERNET_DNS_CONFIG} ${TMP_NAMED_CONFIG};
                                            cp ${INTERNET_DNS_CONFIG} ${BKUP_NAMED_CONFIG};

                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copies created. Validating..";

                                            if [ -s ${BKUP_NAMED_CONFIG} ]
                                            then
                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup copy confirmed. Validating operational..";

                                                ## we have our backup copy...
                                                if [ -s ${TMP_NAMED_CONFIG} ]
                                                then
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Operational copy confirmed. Continuing..";
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switching master_dns from ${NAMED_MASTER} to ${MASTER_TARGET}..";

                                                    ## we have our working copy. move forward
                                                    sed -e "s/master_dns = ${NAMED_MASTER}/master_dns = ${MASTER_TARGET}/" ${TMP_NAMED_CONFIG} >> ${TMP_NAMED_CONFIG}.tmp;

                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch complete. Verifying..";

                                                    ## make sure it was changed..
                                                    if [ $(grep -c "master_dns = ${NAMED_MASTER}" ${TMP_NAMED_CONFIG}.tmp) -eq 0 ]
                                                    then
                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch verified. Placing file..";

                                                        ## it was. make this the active copy
                                                        mv ${TMP_NAMED_CONFIG}.tmp ${TMP_NAMED_CONFIG};

                                                        ## take some checksums..
                                                        TMP_CONF_CKSUM=$(cksum ${TMP_NAMED_CONFIG} | awk '{print $1}');

                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_CONF_CKSUM -> ${TMP_CONF_CKSUM}";

                                                        ## move the file into place..
                                                        mv ${TMP_NAMED_CONFIG} ${INTERNET_DNS_CONFIG};

                                                        ## make sure it was moved..
                                                        if [ ! -s ${TMP_NAMED_CONFIG} ]
                                                        then
                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File moved. Verifying..";

                                                            ## and cksum..
                                                            OP_CONF_CKSUM=$(cksum ${INTERNET_DNS_CONFIG} | awk '{print $1}');

                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_CONF_CKSUM -> ${OP_CONF_CKSUM}";

                                                            ## and make sure they agree
                                                            if [ ${TMP_CONF_CKSUM} -eq ${OP_CONF_CKSUM} ]
                                                            then
                                                                ## they do. respond with success
                                                                ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local config switch -> master_nameserver modification - performed by ${IUSER_AUDIT}.";
                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                                RETURN_CODE=0;
                                                            else
                                                                ## cksum mismatch. file failed to copy
                                                                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum validation failed. New configuration has not been applied.";
                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                                RETURN_CODE=90;
                                                            fi
                                                        else
                                                            ## failed to move the tmp file into place
                                                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum validation failed. New configuration has not been applied.";
                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                            RETURN_CODE=44;
                                                        fi
                                                    else
                                                        ## failed to update the file with the proper information
                                                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to apply new configuration information. Please try again.";
                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                        RETURN_CODE=89;
                                                    fi
                                                else
                                                    ## failed to create a working copy of the config file
                                                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a working copy of the existing configuration. Cannot continue.";
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    RETURN_CODE=47;
                                                fi
                                            else
                                                ## failed to create a backup of the config file
                                                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to back up the existing configuration. Cannot continue.";
                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                RETURN_CODE=57;
                                            fi
                                        else
                                            ## failed to copy in the new file. error
                                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while re-configuring server as a master. Please try again.";
                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                            RETURN_CODE=83;
                                        fi
                                    else
                                        ## failure updating transfer directive. cant continue
                                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to modify transfer directive in named configuration. Please process manually.";
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                        RETURN_CODE=79;
                                    fi
                                else
                                    ## failed to update the query acl. this isnt really bad but its not good either
                                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to modify query ACL in named configuration. Please process manually.";
                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                    RETURN_CODE=80;
                                fi
                            else
                                ## failed to make a working copy. error
                                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create temporary file. Please process manually.";
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                RETURN_CODE=47;
                            fi
                        else
                            ## failed to make a backup of primary config. error
                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create primary configuration backup. Please process manually.";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            RETURN_CODE=57;
                        fi
                    else
                        ## failed to re-configure one or more zones. fail
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more configuration files failed to properly update. Please process manually.";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        RETURN_CODE=81;
                    fi
                else
                    ## ok, it didnt untar correctly. something broke. return an error
                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Decompression of master tarfile failed. Unable to continue.";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=85;
                fi
            else
                ## zone file backup failed. error.
                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a backup of existing zonefiles. Please process manually.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=57;
            fi
        else
            ## isnt a slave server. cant re-configure a master to be
            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to reconfigure slave nameserver. Please process manually.";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=82;
        fi
    else
        ## we dont have a master directory. fail.
        ## we dont have a master directory here
        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The master directory does not exist. Please process manually.";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=23;
    fi
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function usage
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Process a service re-configuration from a slave to a master";
    print "Usage: ${CNAME} [ -p tarfile name ] [ -s ] [ -f ] [ -i audit user ] [ -c change order ] [ -e ] [ -? | -h ]";
    print "  -p      Re-configure server as a master server. If provided, a tarfile name must also be provided.";
    print "  -s      Re-configure server as a slave server";
    print "  -f      Force re-configuration if master tarfile does not exist";
    print "  -t      The name of the new master nameserver";
    print "  -i      The user performing the request";
    print "  -c      The change order associated with this request";
    print "  -e      Execute processing";
    print "  -?|-h   Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh || \
    echo "Failed to locate configuration data. Cannot continue.";
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

[ ${#} -eq 0 ] && usage;

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

[ -z "${APP_ROOT}" ] && . ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh;

unset METHOD_NAME;
unset CNAME;

## check security
. ${PLUGIN_ROOT_DIR}/lib/security/check_main.sh;
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Security configuration does not allow the requested action." && echo ${RET_CODE} && exit ${RET_CODE};

## unset the return code
unset RET_CODE;

## lock it
${APP_ROOT}/${LIB_DIRECTORY}/lock.sh lock ${$};
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Application currently in use." && echo ${RET_CODE} && exit ${RET_CODE};

unset RET_CODE;

CNAME="$(basename "${0}")";
METHOD_NAME="${CNAME}#startup";

while getopts ":p:sft:i:c:eh:" OPTIONS
do
    case "${OPTIONS}" in
        p)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting request to swap slave to master..";

            ## Capture the site root
            REQUEST_TYPE=set_master;
            typeset -l MASTER_TAR=${OPTARG};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_TYPE -> ${REQUEST_TYPE}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MASTER_TAR -> ${MASTER_TAR}";
            ;;
        s)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting request to swap master to slave..";

            ## Capture the site root
            REQUEST_TYPE=set_slave;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_TYPE -> ${REQUEST_TYPE}";
            ;;
        f)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting tar override";

            ## Capture the site root
            OVERRIDE_TAR=1;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OVERRIDE_TAR -> ${OVERRIDE_TAR}";
            ;;
        t)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting MASTER_TARGET";

            ## Capture the site root
            typeset -l MASTER_TARGET=${OPTARG};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MASTER_TARGET -> ${MASTER_TARGET}";
            ;;
        i)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting IUSER_AUDIT..";

            ## Capture the change control
            typeset -u IUSER_AUDIT=${OPTARG};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
            ;;
        c)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            ## Capture the change control
            typeset -u CHANGE_NUM=${OPTARG};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        e)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${CHANGE_NUM}" ]
            then
                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change order was provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=17;
            elif [ -z "${IUSER_AUDIT}" ]
            then
                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requestors username was not provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=20;
            elif [ -z "${MASTER_TARGET}" ]
            then
                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The name of the new target master was not provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=7;
            else
                ## We have enough information to process the request, continue
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                if [ ! -z "${REQUEST_TYPE}" ]
                then
                    if [ "${REQUEST_TYPE}" = "set_slave" ]
                    then
                        switch_to_slave;
                    else
                        if [ -z "${MASTER_TAR}" ]
                        then
                            ## we're missing the tar file argument. error
                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_TYPE is blank. Cannot continue.";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            usage;
                        else
                            [ -z "${OVERRIDE_TAR}" ] && OVERRIDE_TAR=0;

                            switch_to_master;
                        fi
                    fi
                else
                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_TYPE is blank. Cannot continue.";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    usage;
                fi
            fi
            ;;
        h)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
        [\?])
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
        *)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done

shift ${OPTIND}-1;

echo ${RETURN_CODE};
exit ${RETURN_CODE};

