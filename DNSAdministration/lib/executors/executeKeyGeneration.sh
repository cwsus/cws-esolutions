#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  execute_key_generation.sh
#         USAGE:  ./execute_key_generation.sh
#   DESCRIPTION:  Designed to run as a cron job on a defined bastion host
#                 to provide bi-annually updates (or more often, as desired)
#                 to the root.servers cache file
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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -f ${SCRIPT_ROOT}/../plugin ] && . ${SCRIPT_ROOT}/../plugin;

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
#          NAME:  generateRNDCKeys
#   DESCRIPTION:  Generates rndc, tsig, and dhcp update keys
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function generateRNDCKeys
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    if [ ! -z "${IS_RNDC_MGMT_ENABLED}" ] && [ "${IS_RNDC_MGMT_ENABLED}" = "${_FALSE}" ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC Key management has not been enabled. Cannot continue.";

        RETURN_CODE=97;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generating RNDC keyfiles..";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command rndc-confgen -b ${RNDC_KEY_BITSIZE} -r ${RANDOM_GENERATOR} > ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}..";

    RETURN_KEY=$(rndc-confgen -b ${RNDC_KEY_BITSIZE} -r ${RANDOM_GENERATOR} | grep secret | head -1 | cut -d "\"" -f 2);
    KEY_GENERATION_DATE=$(date +"%m-%d-%y");

    if [ ! -z "${RETURN_KEY}" ]
    then
        if [ "${KEYTYPE}" = "${RNDC_LOCAL_KEY}" ]
        then
            [ -s ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE}) ] && rm -rf ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
            [ -s ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE}) ] && rm -rf ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
            [ -s ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE}).REMOTE ] && rm -rf ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE}).REMOTE;

            ## this is a typeset rndc key. build the file
            ## once for the rndc conf file...
            echo "# keyfile ${RNDC_LOCAL_KEY} generated by ${REQUESTING_USER} on ${GENERATION_DATE}\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
            echo "# change request number ${CHANGE_NUM}\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
            echo "key \"${RNDC_LOCAL_KEY}\" {\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
            echo "    algorithm         hmac-md5;\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
            echo "    secret            \"${RETURN_KEY}\";\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
            echo "};\n\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3);

            ## and again for rndc.key
            echo "# keyfile ${RNDC_LOCAL_KEY} generated by ${REQUESTING_USER} on ${KEY_GENERATION_DATE}\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_KEY_FILE});
            echo "# change request number ${CHANGE_NUM}\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_KEY_FILE});
            echo "key \"${RNDC_LOCAL_KEY}\" {\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_KEY_FILE});
            echo "    algorithm         hmac-md5;\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_KEY_FILE});
            echo "    secret            \"${RETURN_KEY}\";\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_KEY_FILE});
            echo "};\n\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_KEY_FILE});

            ## unset the key
            unset RETURN_KEY;

            if [ -s ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE}) ] \
                && [ -s ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_KEY_FILE}) ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${RNDC_LOCAL_KEY} generation complete. Now generating ${RNDC_REMOTE_KEY}..";

                ## generate the remote key
                RETURN_KEY=$(rndc-confgen -b ${RNDC_KEY_BITSIZE} -r ${RANDOM_GENERATOR} | grep secret | head -1 | cut -d "\"" -f 2);

                if [ ! -z "${RETURN_KEY}" ]
                then
                    ## remote key. write it out
                    ## once for the rndc conf file...
                    echo "# keyfile ${RNDC_REMOTE_KEY} generated by ${REQUESTING_USER} on ${KEY_GENERATION_DATE}\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
                    echo "# change request number ${CHANGE_NUM}\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
                    echo "key \"${RNDC_REMOTE_KEY}\" {\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
                    echo "    algorithm         hmac-md5;\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
                    echo "    secret            \"${RETURN_KEY}\";\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
                    echo "};\n\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});

                    ## and then add our default server..
                    echo "options {\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
                    echo "    default-key       \"${RNDC_LOCAL_KEY}\";\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
                    echo "    default-server    127.0.0.1;\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
                    echo "    default-port      ${RNDC_LOCAL_PORT};\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});
                    echo "};\n\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});

                    ## we dont write out the remote rndc key. just return it back to the requestor
                    ## this is because it needs to be transferred out to the slave servers. we
                    ## let the requestor handle that.
                    if [ -s ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE}) ] \
                        && [ -s ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_KEY_FILE}) ]
                    then
                        ## copy the keys in place
                        ## backup first
                        TARFILE_DATE=$(date +"%m-%d-%Y");
                        BACKUP_FILE=$(cut -d "/" -f 3 <<< ${RNDC_KEY_FILE}).${CHANGE_NUM}.${TARFILE_DATE}.${REQUESTING_USER};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BACKUP_FILE->${BACKUP_FILE}";

                        ## tar+gzip
                        tar cf ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar -C ${NAMED_ROOT}/etc/dnssec-keys $(cut -d "/" -f 3 <<< ${RNDC_KEY_FILE}) \
                            $(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE}) > /dev/null 2>&1;
                        gzip ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar > /dev/null 2>&1;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete. Validating..";

                        ## make sure our backup file got created
                        if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar.gz ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup successful. Copying files..";

                            ## unset BACKUP_FILE var
                            unset BACKUP_FILE;

                            ## now we can move the files in
                            cp -p ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_KEY_FILE}) ${NAMED_ROOT}/${RNDC_KEY_FILE};
                            cp -p ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE}) ${NAMED_ROOT}/${RNDC_CONF_FILE};

                            ## cksum verify
                            for OPERATIONAL_FILE in ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_KEY_FILE}) \
                                ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE})
                            do
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating ${OPERATIONAL_FILE}..";

                                OP_FILE_CKSUM=$(cksum ${OPERATIONAL_FILE} | awk '{print $1}');
                                MOD_FILE_CKSUM=$(cksum ${NAMED_ROOT}/etc/dnssec-keys/$(cut -d "/" -f 5 <<< ${OPERATIONAL_FILE}) | awk '{print $1}' );

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_FILE_CKSUM -> ${OP_FILE_CKSUM}";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MOD_FILE_CKSUM -> ${MOD_FILE_CKSUM}";

                                if [ ${OP_FILE_CKSUM} -eq ${MOD_FILE_CKSUM} ]
                                then
                                    ## matched. continue
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum match. Continuing..";

                                    continue;
                                else
                                    ## cksum mismatch, fail
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHECKSUM MISMATCH: ${OPERATIONAL_FILE} - ${OP_FILE_CKSUM} / ${NAMED_ROOT}/etc/dnssec-keys/$(cut -d "/" -f 4 <<< ${OPERATIONAL_FILE}) - ${MOD_FILE_CKSUM})";

                                    CKSUM_FAILURE=${_TRUE};
                                fi
                            done

                            if [ -z "${CKSUM_FAILURE}" ]
                            then
                                ## we're all set here. return the key back to the requestor.
                                echo ${RETURN_KEY};
                                RETURN_CODE=0;
                            else
                                ## an "ERROR" occurred validating cksums.
                                RETURN_CODE=90;
                            fi
                        else
                            ## backup file generation failed
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate backup file. Cannot continue.";

                            RETURN_CODE=57;
                        fi
                    else
                        ## key generation succeeded, but file write failed
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to write out key configuration. Cannot continue.";

                        RETURN_CODE=94;
                    fi
                else
                    ## key generation failed
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed generate new keys. Cannot continue.";

                    RETURN_CODE=94;
                fi
            else
                ## key generation succeeded, but we failed to write the file
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to write temporary configuration files. Cannot continue.";

                RETURN_CODE=47;
            fi
        else
            ## no valid keytype
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No valid keytype was provided for generation.";

            RETURN_CODE=21;
        fi
    else
        ## no key generated
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while generating the new keys.";

        RETURN_CODE=6;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    ## remove tmp files
    rm -rf ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_KEY_FILE});
    rm -rf ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${RNDC_CONF_FILE});

    ## unset
    unset CKSUM_FAILURE;
    unset MOD_FILE_CKSUM;
    unset OP_FILE_CKSUM;
    unset OPERATIONAL_FILE;
    unset BACKUP_FILE;
    unset TARFILE_DATE;
    unset KEY_GENERATION_DATE;
    unset RETURN_KEY;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  generateDNSSECKeys
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#==============================================================================
function generateDNSSECKeys
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    if [ ! -z "${IS_DNSSEC_MGMT_ENABLED}" ] && [ "${IS_DNSSEC_MGMT_ENABLED}" = "${_FALSE}" ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNSSEC Key management has not been enabled. Cannot continue.";

        RETURN_CODE=97;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    ## First, lets make sure the directory for the provided
    ## BU actually exists
    if [ ! -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT} ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested business unit does not have a defined group. Cannot continue.";

        RETURN_CODE=10;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_ROOT to ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT}..";

    SITE_ROOT=${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT};
    TARFILE_DATE=$(date +"%m-%d-%Y");
    DC_FILE=$(cut -d "." -f 1-2 <<< ${FILENAME});
    BACKUP_FILE=${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${REQUESTING_USER};
    KEY_DIRECTORY=${NAMED_ROOT}${DNSSEC_ROOT_DIR}/${BUSINESS_UNIT};
    ZSK_KEY_DST_FILE_NAME=${ZONESIGN_FILE_PREFIX}.${ZONE_NAME}.${PROJECT_CODE}.key;
    ZSK_PRV_DST_FILE_NAME=${ZONESIGN_FILE_PREFIX}.${ZONE_NAME}.${PROJECT_CODE}.private;
    KSK_KEY_DST_FILE_NAME=${KSKSIGN_FILE_PREFIX}.${ZONE_NAME}.${PROJECT_CODE}.key;
    KSK_PRV_DST_FILE_NAME=${KSKSIGN_FILE_PREFIX}.${ZONE_NAME}.${PROJECT_CODE}.private;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_ROOT->${SITE_ROOT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARFILE_DATE->${TARFILE_DATE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BACKUP_FILE->${BACKUP_FILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DC_FILE->${DC_FILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "KEY_DIRECTORY->${KEY_DIRECTORY}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZSK_KEY_DST_FILE_NAME->${ZSK_KEY_DST_FILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZSK_PRV_DST_FILE_NAME->${ZSK_PRV_DST_FILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "KSK_KEY_DST_FILE_NAME->${KSK_KEY_DST_FILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "KSK_PRV_DST_FILE_NAME->${KSK_PRV_DST_FILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checking for file ${FILENAME}...";

    ## Then, lets check and make sure that the zonefile
    ## actually exists
    if [ ! -f ${SITE_ROOT}/${FILENAME} ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested project code does not exist. Cannot continue.";

        RETURN_CODE=9;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File exists - backup in progress...";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting backup file to ${NAMED_ROOT}/${BACKUP_DIRECTORY}/${FILENAME}.\`date +"%m-%d-%Y"\`.${CHANGE_NUM}.${REQUESTING_USER}";

    ## Everything exists. Lets backup the zone before
    ## making any modifications
    ## why tar+gzip ? to carry the process over. we
    ## want consistency, even when it doesnt make a
    ## difference
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BACKUP_FILE->${BACKUP_FILE}";

    ## tar+gzip
    tar cf ${NAMED_ROOT}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar -C ${SITE_ROOT} ${FILENAME} ${PRIMARY_DATACENTER}/${DC_FILE} \
        ${SECONDARY_DATACENTER}/${DC_FILE} > /dev/null 2>&1;
    gzip ${NAMED_ROOT}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar > /dev/null 2>&1;

    ## make sure our backup file got created
    if [ ! -s ${NAMED_ROOT}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar.gz ]
    then
        ## no backup, no workie
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create backup file. Cannot continue.";

        RETURN_CODE=57;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    ## unset BACKUP_FILE var
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete - continuing...";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying ${FILENAME} to ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}..";

    ## make a copy of the zone for operation
    cp ${SITE_ROOT}/${FILENAME} ${PLUGIN_WORK_DIRECTORY}/${FILENAME};

    if [ ! -s ${PLUGIN_WORK_DIRECTORY}/${FILENAME} ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create copy file. Cannot continue.";

        RETURN_CODE=57;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling addServiceIndicators.sh to add "AUDIT" flags..";

    ## lets get to work
    ## first lets make sure that the project has a dnssec directory,
    ## if not make it
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating business unit key signing directory..";

    [ ! -d ${KEY_DIRECTORY} ] || mkdir -p ${KEY_DIRECTORY};

    GENERATED_ZSK_FILE=$(dnssec-keygen -K ${KEY_DIRECTORY} -a ${DNSSEC_ALGORITHM} -b ${DNSSEC_ZONESIGN_BITSIZE} -r ${RANDOM_GENERATOR} -n ZONE ${ZONE_NAME});

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Key generation complete. GENERATED_ZSK_FILE -> ${GENERATED_ZSK_FILE}";

    ## ok, we've generated our zsk's
    ## make sure they exist
    if [[ ! -s ${KEY_DIRECTORY}/${GENERATED_ZSK_FILE}.key && ! -s ${KEY_DIRECTORY}/${GENERATED_ZSK_FILE}.private ]]
    then
        ## keygeneration has failed. "ERROR" out and go no further
        ## clean up temp files and the backup file we created, since
        ## we didnt change anything
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZSK key generation FAILURE. Cleanup...";

        RETURN_CODE=99;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    ## keys successfully created, lets move forward
    ## rename the files that were created to what we
    ## want them to be
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Key generation verified. Renaming..";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying ${KEY_DIRECTORY}/${GENERATED_ZSK_FILE}.key to ${KEY_DIRECTORY}/${ZSK_KEY_DST_FILE_NAME}..";

    cp ${KEY_DIRECTORY}/${GENERATED_ZSK_FILE}.key ${KEY_DIRECTORY}/${ZSK_KEY_DST_FILE_NAME};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying ${KEY_DIRECTORY}/${GENERATED_ZSK_FILE}.private to ${KEY_DIRECTORY}/${ZSK_PRV_DST_FILE_NAME}..";

    cp ${KEY_DIRECTORY}/${GENERATED_ZSK_FILE}.private ${KEY_DIRECTORY}/${ZSK_PRV_DST_FILE_NAME};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copies complete. Validating..";

    if [ ! -s ${KEY_DIRECTORY}/${ZSK_PRV_DST_FILE_NAME} ] && [ ! -s ${KEY_DIRECTORY}/${ZSK_KEY_DST_FILE_NAME} ]
    then
        ## ksk generation failed. "ERROR" out
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate ZSK keys. Cannot continue.";

        RETURN_CODE=99;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    ## keys successfully moved.
    ## now we generate our ksk's
    ## remove the generated files..
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copy validated. Removing source files..";

    rm -rf ${KEY_DIRECTORY}/${GENERATED_ZSK_FILE}.key;
    rm -rf ${KEY_DIRECTORY}/${GENERATED_ZSK_FILE}.private;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removal complete. Generating KSK keys..";

    GENERATED_KSK_FILE=$(dnssec-keygen -K ${KEY_DIRECTORY} -a ${DNSSEC_ALGORITHM} -b ${DNSSEC_KEYSIGN_BITSIZE} -r ${RANDOM_GENERATOR} -n ZONE -f KSK ${ZONE_NAME});

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Key generation complete. GENERATED_KSK_FILE -> ${GENERATED_KSK_FILE}";

    ## ok, we've generated our zsk's
    ## make sure they exist
    if [[ ! -s ${KEY_DIRECTORY}/${GENERATED_KSK_FILE}.key && ! -s ${KEY_DIRECTORY}/${GENERATED_KSK_FILE}.private ]]
    then
        ## ksk generation failed. "ERROR" out
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate KSK keys. Cannot continue.";

        RETURN_CODE=99;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    ## keys successfully created, lets move forward
    ## rename the files that were created to what we
    ## want them to be
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Key generation verified. Renaming..";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying ${KEY_DIRECTORY}/${GENERATED_KSK_FILE}.key to ${KEY_DIRECTORY}/${KSK_KEY_DST_FILE_NAME}..";

    cp ${KEY_DIRECTORY}/${GENERATED_KSK_FILE}.key ${KEY_DIRECTORY}/${KSK_KEY_DST_FILE_NAME};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying ${KEY_DIRECTORY}/${GENERATED_KSK_FILE}.private to ${KEY_DIRECTORY}/${KSK_PRV_DST_FILE_NAME}..";

    cp ${KEY_DIRECTORY}/${GENERATED_ZSK_FILE}.private ${KEY_DIRECTORY}/${ZSK_PRV_DST_FILE_NAME};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copies complete. Validating..";

    if [[ ! -s ${KEY_DIRECTORY}/${KSK_PRV_DST_FILE_NAME} && ! -s ${KEY_DIRECTORY}/${KSK_KEY_DST_FILE_NAME} ]]
    then
        ## rename failed. "ERROR" out
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to properly rename keys. Cannot continue.";

        RETURN_CODE=99;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    ## rename successful.
    ## at this point we have our keys, now we need to include
    ## them in the zonefile and then sign it. once that's done,
    ## we can move the dsset files to the proper directory and
    ## finalize.
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Key generation complete. Updating zonefile..";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Stripping header...";

    ## pull out the header from the existing file to place into the new
    ## NOTE: This depends on the header being the same number of lines in
    ## every single file. change as appropriate if necessary.
    ## alternately, we could just sed it out.. maybe that would be better ?
    ## get our start line number
    START_LINE_NUMBER=$(sed -n "/\$TTL ${NAMED_TTL_TIME}/=" ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_DIR}/${GROUP_ID}${PROJECT_CODE}/${FILENAME});
    LAST_SERIAL=$(grep "; serial" ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_DIR}/${GROUP_ID}${PROJECT_CODE}/${FILENAME} | awk '{print $1}' | sed -e '/^$/d');

    ## set up the new serial number
    if [ $(cut -c 1-8 <<< ${LAST_SERIAL}) -eq $(date +"%Y%m%d") ]
    then
        SERIAL_NUM=$(( ${LAST_SERIAL} + 1 ));
    else
        SERIAL_NUM=${DEFAULT_SERIAL_NUMBER};
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "START_LINE_NUMBER -> ${START_LINE_NUMBER}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LAST_SERIAL -> ${LAST_SERIAL}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERIAL_NUM -> ${SERIAL_NUM}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printing \$INCLUDE ${KEY_DIRECTORY}/${ZSK_KEY_DST_FILE_NAME} ..";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printing \$INCLUDE ${KEY_DIRECTORY}/${KSK_KEY_DST_FILE_NAME} ..";

    ## then strip the header..
    ## then add the "INFO"..
    head -${START_LINE_NUMBER} ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_DIR}/${GROUP_ID}${PROJECT_CODE}/${FILENAME} | \
        sed -e "${START_LINE_NUMBER}a \$INCLUDE ${KEY_DIRECTORY}/${GENERATED_KSK_FILE}.key" \
            -e "${START_LINE_NUMBER}a \$INCLUDE ${KEY_DIRECTORY}/${GENERATED_ZSK_FILE}.key" >> ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME};

    ## now we need to add the rest of the zone content into the new file
    ## and update the serial number.
    sed -e "1,${START_LINE_NUMBER}d" -e "s/${LAST_SERIAL}/${SERIAL_NUM}/" \
        ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_DIR}/${GROUP_ID}${PROJECT_CODE}/${FILENAME} >> ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME};

    ## and now we should have two includes - lets make sure
    if [ $(grep -c INCLUDE ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME}) -ne 2 ]
    then
        ## failed to sign the zone with the keys
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to add keyfile includes in zone. Cannot continue.";

        RETURN_CODE=99;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    ## process complete, lets rock on
    ## now we sign the zone
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Keyfile inclusion complete. Signing zone..";

    ## here we actually sign the working copy of the zone we've created.
    RET_CODE=$(dnssec-signzone -K ${KEY_DIRECTORY} -d ${KEY_DIRECTORY} \
        -o ${ZONE_NAME} -r ${RANDOM_GENERATOR} -N INCREMENT -k ${KSKSIGN_FILE_PREFIX}-${ZONE_NAME}.${PROJECT_CODE} \
            -f ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME}.signed ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME} \
                ${ZSKSIGN_FILE_PREFIX}-${ZONE_NAME}.${PROJECT_CODE});

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE->${RET_CODE}";

    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
    then
        ## failed to sign the zone with the keys
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "dnssec-signzone has FAILED. Please inspect logs for cause/resolution.";

        RETURN_CODE=99;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    ## zone was successfully signed
    ## ok, we now need to add the header back
    ## move the .signed file to the real file
    mv ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME}.signed ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME};

    ## and then make sure it was created..
    if [ ! -s ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME} ]
    then
        ## file rename failed
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to rename signed zone to ${FILENAME}. Cannot continue.";

        RETURN_CODE=99;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    ## and make sure its actually the signed file..
    if [ $(grep -c dnssec_signzone ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME}) != 0 ]
    then
        ## shift failed
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully renamed signed zone to ${FILENAME}, but content could not be verified. Cannot continue.";

        RETURN_CODE=99;
    fi

    ## lets move the dsset file now..
    mv ${KEY_DIRECTORY}/dsset-${ZONE_NAME}. ${KEY_DIRECTORY}/dsset-${ZONE_NAME}.${PROJECT_CODE}.;

    ## and make sure..
    if [ -s ${KEY_DIRECTORY}/dsset-${ZONE_NAME}.${PROJECT_CODE}. ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding header..";

        head -4 ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_DIR}/${GROUP_ID}${PROJECT_CODE}/${FILENAME} \
            > ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding signed content..";
        ## header in, now add the signed content
        cat ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME}.signed >> ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME};

        ## k, this gives our proper header and a signed zone.
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Signed zone now available. Creating datacenter-specifics...";

        ## now we need to create our dc-specific files.
        ## to do this, we need to know the current ip
        CURRENT_DC=$(grep "Current" ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_DIR}/${GROUP_ID}${PROJECT_CODE}/${FILENAME} | cut -d ":" -f 2| sed -e 's/^ *//');

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CURRENT_DC->${CURRENT_DC}";

        ## set the IP flag
        [ "${CURRENT_DC}" = "${PRIMARY_DC}" ] && CURRENT_IP=${PRIMARY_DATACENTER_IP} || CURRENT_IP=${SECONDARY_DATACENTER_IP};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CURRENT_IP->${CURRENT_IP}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating datacenter specific files..";

        ## ok, lets rock out dc-specifics
        ## start with the DC its live in
        head -4 ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_DIR}/${GROUP_ID}${PROJECT_CODE}/${FILENAME}/${CURRENT_DC}/${DC_FILE} \
             > ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME}.${PRIMARY_DC};
        head -4 ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_DIR}/${GROUP_ID}${PROJECT_CODE}/${FILENAME}/${CURRENT_DC}/${DC_FILE} \
             > ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME}.${SECONDARY_DC};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Headers added. Adding signed content..";

        ## we have headers, add in the signature
        if [ "${CURRENT_DC}" = "${PRIMARY_DC}" ]
        then
            ## add in the signed content, leaving everything unchanged
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding signatures for ${PRIMARY_DC}..";

            cat ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME} >> ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME}.${PRIMARY_DC};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding signatures for ${SECONDARY_DC} and flipping IP to ${SECONDARY_DATACENTER_IP}..";

            ## and then again for the secondary
            sed -e "s/${PRIMARY_DATACENTER_IP}/${SECONDARY_DATACENTER_IP}/" ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME} \
                >> ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME}.${SECONDARY_DC};
        elif [ "${CURRENT_DC}" = "${SECONDARY_DC}" ]
        then
            ## add in the signed content, leaving everything unchanged
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding signatures for ${SECONDARY_DC}..";

            cat ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME} >> ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME}.${SECONDARY_DC};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding signatures for ${PRIMARY_DC} and flipping IP to ${PRIMARY_DATACENTER_IP}..";

            ## and then again for the secondary
            sed -e "s/${SECONDARY_DATACENTER_IP}/${PRIMARY_DATACENTER_IP}/" ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME} \
                >> ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME}.${PRIMARY_DC};
        else
            ## couldnt accurately determine what datacenter this exists in currently
            ## "ERROR" out
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unknown datacenter ${CURRENT_DC}. Cannot continue.";

            RETURN_CODE=xx;
        fi

        ## check to see if we have a return code from the above
        if [ -z "${RETURN_CODE}" ]
        then
            ## we dont, keep going
            ## at this point we have:
            ## a signed primary zone
            ## a signed primary datacenter zone with header
            ## a signed secondary datacenter zone with header
            ## we can copy in the files
            ## pull checksums
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Placing files..";

            SIGNED_TMP_CKSUM=$(cksum ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME} | awk '{print $1}');
            SIGNED_PRI_CKSUM=$(cksum ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME}.${PRIMARY_DATACENTER} | awk '{print $1}');
            SIGNED_SEC_CKSUM=$(cksum ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME}.${SECONDARY_DATACENTER} | awk '{print $1}');

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SIGNED_TMP_CKSUM -> ${SIGNED_TMP_CKSUM}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SIGNED_PRI_CKSUM -> ${SIGNED_PRI_CKSUM}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SIGNED_SEC_CKSUM -> ${SIGNED_SEC_CKSUM}";

            cp ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME} \
                ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_DIR}/${GROUP_ID}${PROJECT_CODE}/${FILENAME};

            ## validate it, if this didnt work then theres no point in continuing
            OP_TMP_CKSUM=$(cksum ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_DIR}/${GROUP_ID}${PROJECT_CODE}/${FILENAME} | awk '{print $1}');

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_TMP_CKSUM -> ${OP_TMP_CKSUM}";

            if [ ${SIGNED_TMP_CKSUM} -eq ${OP_TMP_CKSUM} ]
            then
                ## checksum match. continue.
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Operational zonefile checksum match. Continuing..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying primary datacenter files..";

                cp ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME}.${PRIMARY_DC} \
                    ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_DIR}/${GROUP_ID}${PROJECT_CODE}/${PRIMARY_DC}/${DC_FILE};

                OP_PRI_CKSUM=$(cksum ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_DIR}/${GROUP_ID}${PROJECT_CODE}/${PRIMARY_DC}/${DC_FILE} | awk '{print $1}');

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_PRI_CKSUM -> ${OP_PRI_CKSUM}";

                ## and again, check it
                ## why case ? dunno just seems to be better than
                ## copying the same code 80 bajillion times
                case ${OP_PRI_CKSUM} in
                    *)
                        ## check here and see if it matches, if doesnt issue warning.
                        ## we arent failing if it doesnt. just advising.
                        if [ ${SIGNED_PRI_CKSUM} != ${OP_PRI_CKSUM} ]
                        then
                            ##
                            ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: Primary datacenter zonefile checksum mis-match.";

                            RETURN_WARNING=${_TRUE};
                        else
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Primary datacenter zonefile checksum match. Continuing..";
                        fi

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying secondary datacenter files..";

                        cp ${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}/${FILENAME}.${SECONDARY_DC} \
                            ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_DIR}/${GROUP_ID}${PROJECT_CODE}/${SECONDARY_DC}/${DC_FILE};

                        OP_SEC_CKSUM=$(cksum ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_DIR}/${GROUP_ID}${PROJECT_CODE}/${SECONDARY_DC}/${DC_FILE} | awk '{print $1}');

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_SEC_CKSUM -> ${OP_SEC_CKSUM}";

                        case ${OP_SEC_CKSUM} in
                            *)
                                if [ ${SIGNED_SEC_CKSUM} != ${OP_SEC_CKSUM} ]
                                then
                                    ##
                                    ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: Secondary datacenter zonefile checksum mis-match.";

                                    RETURN_WARNING=${_TRUE};
                                else
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Secondary datacenter zonefile checksum match. Continuing..";
                                fi

                                ## ok, we have our shtuff. now we reload the zone for the changes to take
                                ## effect
                                ;;
                        esac
                        ;;
                esac
            else
                ## operational file failed to copy. this is fatal.
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to copy signed operational zonefile. Cannot continue.";

                RETURN_CODE=xx;
            fi
        fi
    else
        ## dsset shift failed
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to rename dsset-${ZONE_NAME}. to dsset-${ZONE_NAME}.${PROJECT_CODE}. Cannot continue.";

        RETURN_CODE=xx;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    ## unset..
    unset RETURN_WARNING;
    unset OP_SEC_CKSUM;
    unset OP_PRI_CKSUM;
    unset OP_TMP_CKSUM;
    unset SIGNED_SEC_CKSUM;
    unset SIGNED_PRI_CKSUM;
    unset SIGNED_TMP_CKSUM;
    unset CURRENT_DC;
    unset RET_CODE;
    unset SERIAL_NUM;
    unset LAST_SERIAL;
    unset START_LINE_NUMBER;
    unset GENERATED_KSK_FILE;
    unset GENERATED_ZSK_FILE;
    unset SITE_ROOT;
    unset TARFILE_DATE;
    unset DC_FILE;
    unset BACKUP_FILE;
    unset KEY_DIRECTORY;
    unset ZSK_KEY_DST_FILE_NAME;
    unset ZSK_PRV_DST_FILE_NAME;
    unset KSK_KEY_DST_FILE_NAME;
    unset KSK_PRV_DST_FILE_NAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  generateTSIGKeys
#   DESCRIPTION:  Returns a full response from DiG for a provided address
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function generateTSIGKeys
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    if [ ! -z "${IS_TSIG_MGMT_ENABLED}" ] && [ "${IS_TSIG_MGMT_ENABLED}" = "${_FALSE}" ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TSIG Key management has not been enabled. Cannot continue.";

        return 97;
    fi

    ## remove the file if it exists
    [ -s ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE}) ] && rm -rf ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE});

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generating TSIG keyfiles..";

    ## generate the key
    RETURN_KEY=$(rndc-confgen -b ${RNDC_KEY_BITSIZE} -r ${RANDOM_GENERATOR} | grep secret | head -1 | cut -d "\"" -f 2);

    ## normally we "DEBUG" the ret code. but since these are keys, we dont.

    if [ ! -z "${RETURN_KEY}" ]
    then
        ## this is a typeset rndc key. build the file
        echo "# keyfile ${TSIG_TRANSFER_KEY} generated by ${REQUESTING_USER} on ${KEY_GENERATION_DATE}\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE});
        echo "# change request number ${CHANGE_NUM}\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE});
        echo "key \"${TSIG_TRANSFER_KEY}\" {\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE});
        echo "    algorithm    hmac-md5;\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE});
        echo "    secret       \"${RETURN_KEY}\";\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE});
        echo "};\n\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE});

        A=0;

        ## add each slave server into the file
        while [ ${A} -ne ${#DNS_SLAVE_IPS[@]} ]
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS_SLAVE_IPS -> ${DNS_SLAVE_IPS[${A}]}";

            echo "server ${DNS_SLAVE_IPS[${A}]} {\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE});
            echo "    keys { ${TSIG_TRANSFER_KEY}; };\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE});
            echo "};\n\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE});

            (( A += 1 ));
        done

        ## unset the key and put a back to zero
        A=0;

        if [ -s ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE}) ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${TSIG_TRANSFER_KEY} generation complete.";

            ## echo back the key to the requestor for build against slaves
            if [ -s ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE}) ]
            then
                ## copy the keys in place
                ## backup first
                TARFILE_DATE=$(date +"%m-%d-%Y");
                BACKUP_FILE=$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE}).${CHANGE_NUM}.${TARFILE_DATE}.${REQUESTING_USER};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BACKUP_FILE->${BACKUP_FILE}";

                ## tar+gzip
                tar cf ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar -C ${NAMED_ROOT}/etc/dnssec-keys $(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE}) > /dev/null 2>&1;
                gzip ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar > /dev/null 2>&1;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete. Validating..";

                ## make sure our backup file got created
                if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar.gz ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup successful. Copying files..";

                    ## unset BACKUP_FILE var
                    unset BACKUP_FILE;

                    ## now we can move the files in
                    cp -p ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE}) ${NAMED_ROOT}/${TRANSFER_KEY_FILE};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating checksums..";

                    OP_FILE_CKSUM=$(cksum ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE}) | awk '{print $1}');
                    MOD_FILE_CKSUM=$(cksum ${NAMED_ROOT}/${TRANSFER_KEY_FILE} | awk '{print $1}');

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_FILE_CKSUM -> ${OP_FILE_CKSUM}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MOD_FILE_CKSUM -> ${MOD_FILE_CKSUM}";

                    if [ ${OP_FILE_CKSUM} -eq ${MOD_FILE_CKSUM} ]
                    then
                        ## we're all set here. return the key back to the requestor.
                        ## remove tmp files
                        rm -rf ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE});
                        rm -rf ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${TRANSFER_KEY_FILE});

                        echo ${RETURN_KEY};
                        RETURN_CODE=0;
                    else
                        ## an "ERROR" occurred validating cksums.
                        RETURN_CODE=90;
                    fi
                else
                    ## backup file generation failed
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate backup file. Cannot continue.";

                    RETURN_CODE=57;
                fi
            else
                ## key generation succeeded, but file write failed
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to write out key configuration. Cannot continue.";

                RETURN_CODE=94;
            fi
        else
            ## failed to create working file. throw "ERROR"
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to write out temporary key configuration. Cannot continue.";

            RETURN_CODE=47;
        fi
    else
        ## key generation failed
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed generate new keys. Cannot continue.";

        RETURN_CODE=94;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  generateDHCPDKeys
#   DESCRIPTION:  Returns a full response from DiG for a provided address
#    PARAMETERS:  None
#==============================================================================
function generateDHCPDKeys
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    ## remove the file if it exists
    [ -s ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${DHCPD_KEY_FILE}) ] && rm -rf ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${DHCPD_KEY_FILE});

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generating DHCP keyfiles..";

    ## generate the key
    RETURN_KEY=$(rndc-confgen -b ${RNDC_KEY_BITSIZE} -r ${RANDOM_GENERATOR} | grep secret | head -1 | cut -d "\"" -f 2);

    ## normally we "DEBUG" the ret code. but since these are keys, we dont.

    if [ ! -z "${RETURN_KEY}" ]
    then
        ## this is a typeset rndc key. build the file
        echo "# keyfile ${DHCPD_UPDATE_KEY} generated by ${REQUESTING_USER} on ${KEY_GENERATION_DATE}\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${DHCPD_KEY_FILE});
        echo "# change request number ${CHANGE_NUM}\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${DHCPD_KEY_FILE});
        echo "key ${DHCPD_UPDATE_KEY} {\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${DHCPD_KEY_FILE});
        echo "    algorithm    hmac-md5;\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${DHCPD_KEY_FILE});
        echo "    secret       \"${RETURN_KEY}\";\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${DHCPD_KEY_FILE});
        echo "};\n\n" >> ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${DHCPD_KEY_FILE});

        if [ -s ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${DHCPD_KEY_FILE}) ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${DHCPD_UPDATE_KEY} generation complete.";

            ## echo back the key to the requestor for build against slaves
            if [ -s ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${DHCPD_KEY_FILE}) ]
            then
                ## copy the keys in place
                ## backup first
                TARFILE_DATE=$(date +"%m-%d-%Y");
                BACKUP_FILE=$(cut -d "/" -f 3 <<< ${DHCPD_KEY_FILE}).${CHANGE_NUM}.${TARFILE_DATE}.${REQUESTING_USER};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BACKUP_FILE->${BACKUP_FILE}";

                ## tar+gzip
                tar cf ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar -C ${NAMED_ROOT}/etc/dnssec-keys $(cut -d "/" -f 3 <<< ${DHCPD_KEY_FILE}) > /dev/null 2>&1;
                gzip ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar > /dev/null 2>&1;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete. Validating..";

                ## make sure our backup file got created
                if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar.gz ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup successful. Copying files..";

                    ## unset BACKUP_FILE var
                    unset BACKUP_FILE;

                    ## now we can move the files in
                    cp -p ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${DHCPD_KEY_FILE}) ${NAMED_ROOT}/${DHCPD_KEY_FILE};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating checksums..";

                    OP_FILE_CKSUM=$(cksum ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${DHCPD_KEY_FILE}) | awk '{print $1}');
                    MOD_FILE_CKSUM=$(cksum ${NAMED_ROOT}/${DHCPD_KEY_FILE} | awk '{print $1}');

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_FILE_CKSUM -> ${OP_FILE_CKSUM}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MOD_FILE_CKSUM -> ${MOD_FILE_CKSUM}";

                    if [ ${OP_FILE_CKSUM} -eq ${MOD_FILE_CKSUM} ]
                    then
                        ## we're all set here. return the key back to the requestor.
                        ## remove tmp files
                        rm -rf ${PLUGIN_WORK_DIRECTORY}/$(cut -d "/" -f 3 <<< ${DHCPD_KEY_FILE});

                        echo ${RETURN_KEY};
                        RETURN_CODE=0;
                    else
                        ## an "ERROR" occurred validating cksums.
                        RETURN_CODE=90;
                    fi
                else
                    ## backup file generation failed
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate backup file. Cannot continue.";

                    RETURN_CODE=57;
                fi
            else
                ## key generation succeeded, but file write failed
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to write out key configuration. Cannot continue.";

                RETURN_CODE=94;
            fi
        else
            ## failed to create working file. throw "ERROR"
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to write out temporary key configuration. Cannot continue.";

            RETURN_CODE=47;
        fi
    else
        ## key generation failed
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed generate new keys. Cannot continue.";

        RETURN_CODE=94;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  None
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=3;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    echo "${THIS_CNAME} - Execute key generation requests\n";
    echo "Usage: ${THIS_CNAME} [ -r <request information> ] [ -k <request information> ] [ -i <requesting user> ] [ -e ] [ -h|-? ]
    -r         -> Generate new RNDC keys. Requires a comma-delimited information set to execute the request.
    -k         -> Generate DNSSEC keys. Requires a comma-delimited information set to execute the request.
    -e         -> Execute the request
    -h|-?      -> Show this help\n";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

[ ${#} -eq 0 ] && usage && RETURN_CODE=${?};

while getopts ":r:k:i:eh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        r)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting generateRNDCKeys..";

            GENERATE_RNDC_KEYS=${_TRUE};

            typeset -l KEYTYPE=$(echo "${OPTARG}" | cut -d "," -f 1);
            typeset -u CHANGE_NUM=$(echo "${OPTARG}" | cut -d "," -f 2);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "GENERATE_RNDC_KEYS -> ${GENERATE_RNDC_KEYS}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "KEYTYPE -> ${KEYTYPE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUESTING_USER -> ${REQUESTING_USER}";
            ;;
        k)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting generateDNSSECKeys..";

            GENERATE_DNSSEC_KEYS=${_TRUE};

            typeset -l KEYTYPE=$(echo "${OPTARG}" | cut -d "," -f 1);
            typeset -u CHANGE_NUM=$(echo "${OPTARG}" | cut -d "," -f 2);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "GENERATE_DNSSEC_KEYS -> ${GENERATE_DNSSEC_KEYS}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "KEYTYPE -> ${KEYTYPE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUESTING_USER -> ${REQUESTING_USER}";
            ;;
        i)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting REQUESTING_USER..";

            REQUESTING_USER="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUESTING_USER -> ${REQUESTING_USER}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            if [ -z "${KEYTYPE}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No key type was provided. Unable to continue processing.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=95;
            elif [ -z "${CHANGE_NUM}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=19;
            elif [ -z "${REQUESTING_USER}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No auditable user account was provided. Unable to continue processing.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=20;
            else
                ## We have enough information to process the request, continue
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${GENERATE_DNSSEC_KEYS}" ] && [ "${GENERATE_DNSSEC_KEYS}" = "${_TRUE}" ] && generateDNSSECKeys && RETURN_CODE=${?};
                [ ! -z "${GENERATE_TSIG_KEYS}" ] && [ "${GENERATE_TSIG_KEYS}" = "${_TRUE}" ] && generateTSIGKeys && RETURN_CODE=${?};
                [ ! -z "${GENERATE_DHCPD_KEYS}" ] && [ "${GENERATE_DHCPD_KEYS}" = "${_TRUE}" ] && generateDHCPDKeys && RETURN_CODE=${?};
                [ ! -z "${GENERATE_RNDC_KEYS}" ] && [ "${GENERATE_RNDC_KEYS}" = "${_TRUE}" ] && generateRNDCKeys && RETURN_CODE=${?};
            fi
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage && RETURN_CODE=${?};
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
