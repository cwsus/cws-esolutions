#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:    excuteServiceAddition.sh.sh
#         USAGE:  ./excuteServiceAddition.sh.sh
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
CNAME="$(/usr/bin/env basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}/${0##*/}")";
SCRIPT_ROOT="$(/usr/bin/env dirname "${SCRIPT_ABSOLUTE_PATH}")";
METHOD_NAME="${CNAME}#startup";
LOCKFILE=$(mktemp);

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -f "${SCRIPT_ROOT}/../lib/plugin" ] && . "${SCRIPT_ROOT}/../lib/plugin";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${APP_ROOT}" ] && awk -F "=" '/\<1\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[ -f "${PLUGIN_LIB_DIRECTORY}/aliases" ] && . "${PLUGIN_LIB_DIRECTORY}/aliases";
[ -f "${PLUGIN_LIB_DIRECTORY}/functions" ] && . "${PLUGIN_LIB_DIRECTORY}/functions";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

THIS_CNAME="${CNAME}";
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

## validate the input
"${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh" -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

CNAME="${THIS_CNAME}";
typeset METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
then
    "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security violation found while executing ${CNAME} by ${IUSER_AUDIT} on host ${SYSTEM_HOSTNAME}";
    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security configuration blocks execution. Please verify security configuration.";

    awk -F "=" '/\<request.not.authorized\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

    return ${RET_CODE};
fi

unset RET_CODE;
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

lockProcess "${LOCKFILE}" "${$}";
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

CNAME="${THIS_CNAME}";
METHOD_NAME="${THIS_CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

[ ${RET_CODE} -ne 0 ] && awk -F "=" '/\<application.in.use\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

unset RET_CODE;

#===  FUNCTION  ===============================================================
#          NAME:  install_zone
#   DESCRIPTION:  Searches for and replaces audit indicators for the provided
#                 filename.
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function installMasterZone
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Installation of group ${GROUP_ID}${BUSINESS_UNIT} starting..";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Tarfile name -> ${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar.gz";

    typeset CUT_ZONE_NAME=$(cut -d "." -f 1 <<< ${ZONE_NAME});
    typeset LC_BUSINESS_UNIT=$(tr "[A-Z]" "[a-z]" <<< ${BUSINESS_UNIT});
    typeset UC_PROJECT_CODE=$(tr "[a-z]" "[A-Z]" <<< ${PROJECT_CODE});
    typeset CONF_FILE=$(cut -d "/" -f 5 <<< ${NAMED_CONF_FILE});

    if [ -s "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar.gz ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Tarfile exists, proceeding..";

        ## decompress the archive
        gzip -dc "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar.gz | (cd "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"; tar xf -);

        ## make sure the tar extracted properly
        if [ ! -d "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT} ]
        then
            ## tar did not extract properly. throw out an error
            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "tarfile extraction FAILED. Cannot continue.";

            RETURN_CODE=54;
        else
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moving group directory ${GROUP_ID}${BUSINESS_UNIT} into "${NAMED_ROOT}"/${NAMED_MASTER_ROOT}";

            ## directory should exist now, lets move it into place
            mv "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT} "${NAMED_ROOT}"/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Move complete. Proceeding..";

            ## zonefiles should be in place, verify
            if [ -d "${NAMED_ROOT}"/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT} ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Move verified. Proceeding..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${LC_BUSINESS_UNIT}.${NAMED_ZONE_CONF_NAME}";

                ## generate our conf file name
                typeset -l ZONE_CONF_NAME=${BUSINESS_UNIT}.${NAMED_ZONE_CONF_NAME};

                if [ ! -z "${USE_TRANSFER_ACL}" ] && [ "${USE_TRANSFER_ACL}" = "${_TRUE}" ]
                then
                    ## ok, time to create a conf file for the new zone
                    ## and update named.conf so it gets loaded
                    echo "zone \"${ZONE_NAME}\" IN {" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    type               master;" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-update       { none; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-transfer     { key ${TSIG_TRANSFER_KEY}; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    file               \"${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.${CUT_ZONE_NAME}.${UC_PROJECT_CODE}\";" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "};\n" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                elif [ ! -z "${USE_NAMED_ACL}" ] && [ "${USE_NAMED_ACL}" = "${_TRUE}" ]
                then
                    ## ok, time to create a conf file for the new zone
                    ## and update named.conf so it gets loaded
                    echo "zone \"${ZONE_NAME}\" IN {" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    type               slave;" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    masters            { \"${NAMED_MASTER_ACL}\"; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-update       { none; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-transfer     { ${NAMED_TRANSFER_ACL}; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    file               \"${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.${CUT_ZONE_NAME}.${UC_PROJECT_CODE}\";" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "};\n" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                elif [ ! -z "${USE_NAMED_ACL}" ] && [ "${USE_NAMED_ACL}" = "${_TRUE}" ] && [ ! -z "${USE_TRANSFER_ACL}" ] && [ "${USE_TRANSFER_ACL}" = "${_TRUE}" ]
                then
                    ## ok, time to create a conf file for the new zone
                    ## and update named.conf so it gets loaded
                    echo "zone \"${ZONE_NAME}\" IN {" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    type               slave;" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    masters            { \"${NAMED_MASTER_ACL}\"; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-update       { none; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-transfer     { key ${TSIG_TRANSFER_KEY}; ${NAMED_TRANSFER_ACL}; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    file               \"${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.${CUT_ZONE_NAME}.${UC_PROJECT_CODE}\";" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "};\n" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                else
                    ## no transfer acl is specified. this could be an oversight or on purpose. assume on purpose
                    ## and specify none for allow-transfer
                    echo "zone \"${ZONE_NAME}\" IN {" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    type               slave;" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    masters            { \"${NAMED_MASTER_ACL}\"; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-update       { none; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-transfer     { none; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    file               \"${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.${CUT_ZONE_NAME}.${UC_PROJECT_CODE}\";" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "};\n" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Created "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME}";

                ## we should have our conf file created and it should contain our new zone information.
                ## lets include that file into the named conf now
                if [ -s "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME} ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printing include statement to ${NAMED_CONF_FILE}";

                    ## take a backup first
                    cp ${NAMED_CONF_FILE} "${PLUGIN_BACKUP_DIR}"/${CONF_FILE}.${CHANGE_NUM};

                    ## make sure the backup file exists -
                    if [ -s "${PLUGIN_BACKUP_DIR}"/${CONF_FILE}.${CHANGE_NUM} ]
                    then
                        echo "include \"/"${NAMED_CONF_DIR}"/${ZONE_CONF_NAME}\";" >> ${NAMED_CONF_FILE};

                        ## should have our new zone included now. verify it
                        if [ $(grep -c ${ZONE_CONF_NAME} ${NAMED_CONF_FILE}) -eq 1 ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printed include statement to ${NAMED_CONF_FILE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing tar file..";

                            ## clean up our tar
                            rm -rf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar.gz;
                            rm -rf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar;
                            rm -rf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT};

                            ## make sure it actually got removed. if not, log a warning
                            if [ -s "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar.gz ]
                            then
                                "${LOGGER}" "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to remove tarfile.";
                            fi

                            ## audit log
                            "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_NAME} installed by ${REQUESTING_USER} per change ${CHANGE_NUM} on $(date +"%m-%d-%Y") at $(date +"%H:%M:%S")";

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            ## and finally return zero
                            RETURN_CODE=0;
                        else
                            ## the new zone wasnt added to named.conf
                            ## we send back an error code informing
                            RETURN_CODE=34;
                        fi
                    else
                        ## we couldnt take a backup of the named conf file. fail out.
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create backup of ${NAMED_CONF_FILE}. Cannot continue.";

                        RETURN_CODE=57;
                    fi
                else
                    ## something happened while we were creating our new conf file. send an error back
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred creating the zone configuration file.";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=33;
                fi
            else
                ## the new zone didnt copy in. send an error back
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred copying the new zone.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=32;
            fi
        fi
    else
        ## tarfile provided doesnt exist. send an error back, we cant continue
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The provided tarfile does not exist.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=14;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    ## no matter what happens, we remove the temp files from this filesystem
    rm -rf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${PROJECT_CODE}.${CHANGE_DATE}.${ZONE_NAME}.tar.gz;
    rm -rf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${PROJECT_CODE}.${CHANGE_DATE}.${ZONE_NAME}.tar;
    rm -rf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${PROJECT_CODE};

    unset ZONE_CONF_NAME;
    unset CHANGE_DATE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  installSlaveZone
#   DESCRIPTION:  Searches for and replaces audit indicators for the provided
#                 filename.
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function installSlaveZone
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    typeset CHANGE_DATE=$(date +"%m-%d-%Y");
    typeset CUT_ZONE_NAME=$(cut -d "." -f 1 <<< ${ZONE_NAME});
    typeset LC_BUSINESS_UNIT=$(tr "[A-Z]" "[a-z]" <<< ${BUSINESS_UNIT});
    typeset UC_PROJECT_CODE=$(tr "[a-z]" "[A-Z]" <<< ${PROJECT_CODE});
    typeset CONF_FILE=$(cut -d "/" -f 5 <<< ${NAMED_CONF_FILE});

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_DATE -> ${CHANGE_DATE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Installation of group ${GROUP} starting..";

    if [ -s "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar.gz ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Tarfile exists, proceeding..";

        ## decompress the archive
        gzip -dc "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar.gz | (cd "${PLUGIN_WORK_DIRECTORY}"; tar xf -);

        ## make sure the tar extracted properly
        if [ ! -d "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT} ]
        then
            ## tar did not extract properly. throw out an error
            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "tarfile extraction FAILED. Cannot continue.";

            RETURN_CODE=54;
        else
            ## clean up the datacenter-specific directories, they do not need to be there
            ## if the install is on a slave AND the slave is not a failsafe master
            if [ ! $(grep -c $(uname -n) <<< ${EXT_SLAVES[*]}) -eq 1 ]
            then
                ## we're on an external slave. remove the site-specific directories prior to placement
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "External slave detected. Removing site-specific directories..";

                rm -rf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC};
                rm -rf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC};

                ## check if they were removed, if not, "${LOGGER}" "AUDIT" "${METHOD_NAME}"
                if [ -d "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC} ] && [ -d "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC} ]
                then
                    ## oops. theyre still there. "${LOGGER}" "AUDIT" "${METHOD_NAME}", but do not fail
                    "${LOGGER}" "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to remove site-specific directories.";
                fi
            fi

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moving group directory ${GROUP_ID}${BUSINESS_UNIT} into "${NAMED_ROOT}"/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}";

            ## directory should exist now, lets move it into place
            mv "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT} "${NAMED_ROOT}"/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Move complete. Proceeding..";

            ## zonefiles should be in place, verify
            if [ -d "${NAMED_ROOT}"/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT} ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Move verified. Proceeding..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${LC_BUSINESS_UNIT}.${NAMED_ZONE_CONF_NAME}";

                ## generate our conf file name
                typeset -l ZONE_CONF_NAME=${BUSINESS_UNIT}.${NAMED_ZONE_CONF_NAME};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_CONF_NAME -> ${ZONE_CONF_NAME}";

                if [ ! -z "${USE_TRANSFER_ACL}" ] && [ "${USE_TRANSFER_ACL}" = "${_TRUE}" ]
                then
                    ## ok, time to create a conf file for the new zone
                    ## and update named.conf so it gets loaded
                    echo "zone \"${ZONE_NAME}\" IN {" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    type               slave;" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    masters            { \"${NAMED_MASTER_ACL}\"; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-update       { none; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-transfer     { key ${TSIG_TRANSFER_KEY}; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    file               \"${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.${CUT_ZONE_NAME}.${UC_PROJECT_CODE}\";" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "};\n" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                elif [ ! -z "${USE_NAMED_ACL}" ] && [ "${USE_NAMED_ACL}" = "${_TRUE}" ]
                then
                    ## ok, time to create a conf file for the new zone
                    ## and update named.conf so it gets loaded
                    echo "zone \"${ZONE_NAME}\" IN {" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    type               slave;" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    masters            { \"${NAMED_MASTER_ACL}\"; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-update       { none; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-transfer     { ${NAMED_TRANSFER_ACL}; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    file               \"${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.${CUT_ZONE_NAME}.${UC_PROJECT_CODE}\";" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "};\n" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                elif [ ! -z "${USE_NAMED_ACL}" ] && [ "${USE_NAMED_ACL}" = "${_TRUE}" ] && [ ! -z "${USE_TRANSFER_ACL}" ] && [ "${USE_TRANSFER_ACL}" = "${_TRUE}" ]
                then
                    ## ok, time to create a conf file for the new zone
                    ## and update named.conf so it gets loaded
                    echo "zone \"${ZONE_NAME}\" IN {" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    type               slave;" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    masters            { \"${NAMED_MASTER_ACL}\"; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-update       { none; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-transfer     { key ${TSIG_TRANSFER_KEY}; ${NAMED_TRANSFER_ACL}; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    file               \"${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.${CUT_ZONE_NAME}.${UC_PROJECT_CODE}\";" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "};\n" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                else
                    ## no transfer acl is specified. this could be an oversight or on purpose. assume on purpose
                    ## and specify none for allow-transfer
                    echo "zone \"${ZONE_NAME}\" IN {" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    type               slave;" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    masters            { \"${NAMED_MASTER_ACL}\"; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-update       { none; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    allow-transfer     { none; };" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "    file               \"${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.${CUT_ZONE_NAME}.${UC_PROJECT_CODE}\";" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                    echo "};\n" >> "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME};
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Created "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME}";

                ## we should have our conf file created and it should contain our new zone information.
                ## lets include that file into the named conf now
                if [ -s "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${ZONE_CONF_NAME} ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printing include statement to ${NAMED_CONF_FILE}";

                    ## take a backup first
                    cp ${NAMED_CONF_FILE} "${PLUGIN_BACKUP_DIR}"/${CONF_FILE}.${CHANGE_NUM};

                    ## make sure the backup file exists -
                    if [ -s "${PLUGIN_BACKUP_DIR}"/${CONF_FILE}.${CHANGE_NUM} ]
                    then
                        echo "include \"/"${NAMED_CONF_DIR}"/${ZONE_CONF_NAME}\";" >> ${NAMED_CONF_FILE};

                        ## should have our new zone included now. verify it
                        if [ $(grep -c ${ZONE_CONF_NAME} ${NAMED_CONF_FILE}) -eq 1 ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printed include statement to ${NAMED_CONF_FILE}";

                            ## ok, we're done. zone has been created, installed,
                            ## conf files have been created and updated.
                            ## clean up the files we were provided
                            rm -rf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar.gz;
                            rm -rf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER}.tar;
                            rm -rf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT};

                            ## audit log
                            "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_NAME} installed by ${REQUESTING_USER} per change ${CHANGE_NUM} on $(date +"%m-%d-%Y") at $(date +"%H:%M:%S")";

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            ## and finally return zero
                            RETURN_CODE=0;
                        else
                            ## the new zone wasnt added to named.conf
                            ## we send back an error code informing
                            RETURN_CODE=34;
                        fi
                    else
                        ## we couldnt take a backup of the named conf file. fail out.
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create backup of ${NAMED_CONF_FILE}. Cannot continue.";

                        RETURN_CODE=57;
                    fi
                else
                    ## something happened while we were creating our new conf file. send an error back
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred creating the zone configuration file.";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=33;
                fi
            else
                ## the new zone didnt copy in. send an error back
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred copying the new zone.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=32;
            fi
        fi
    else
        ## tarfile provided doesnt exist. send an error back, we cant continue
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The provided tarfile does not exist.";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=14;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset ZONE_CONF_NAME;
    unset CHANGE_DATE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  update_zone_entry
#   DESCRIPTION:  Searches for and replaces audit indicators for the provided
#                 filename.
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function add_zone_entry
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    typeset CHANGE_DATE=$(date +"%m-%d-%Y");
    typeset BACKUP_FILE=${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${REQUESTING_USER};
    typeset CUT_ZONE_NAME=$(cut -d "." -f 1 <<< ${ZONE_NAME});
    typeset LC_BUSINESS_UNIT=$(tr "[A-Z]" "[a-z]" <<< ${BUSINESS_UNIT});
    typeset UC_PROJECT_CODE=$(tr "[a-z]" "[A-Z]" <<< ${PROJECT_CODE});
    typeset CONF_FILE=$(cut -d "/" -f 5 <<< ${NAMED_CONF_FILE});

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_DATE -> ${CHANGE_DATE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BACKUP_FILE -> ${BACKUP_FILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Update of zone ${ZONE_NAME} starting..";

    ## make sure our zone data exists
    if [ -d "${NAMED_ROOT}"/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT} ]
    then
        ## ok, biz unit dir exists
        SITE_ROOT="${NAMED_ROOT}"/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT};

        ## we have the biz unit, the project code, and the zone name.
        ## what we're going to do is grep for the biz unit and project code
        ## in the config files. we may get back more than one, if we do
        ## we need to search those files for the zone name to find the right
        ## file
        ZONEFILES=$(grep ${PROJECT_CODE} "${NAMED_ROOT}/${NAMED_CONF_DIR}"/${LC_BUSINESS_UNIT}.${NAMED_ZONE_CONF_NAME} \
             | awk '{print $2}' | cut -d "\"" -f 2);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILES->${ZONEFILES}";

        for ZONEFILE in ${ZONEFILES}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE->${ZONEFILE}";

            if [ $(grep "SOA" "${NAMED_ROOT}"/${NAMED_ZONE_DIR}/${ZONEFILE} | grep -c ${ZONE_NAME}) -eq 1 ]
            then
                ## we have our zone. we can start working now
                ## this should just be the filename
                ZONEFILE_NAME=$(cut -d "/" -f 3 <<< ${ZONEFILE});

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE_NAME->${ZONEFILE_NAME}";
            fi
        done

        if [ ! -z "${ZONEFILE_NAME}" ]
        then
            if [ -s ${SITE_ROOT}/${ZONEFILE_NAME} ]
            then
                ## check to see if the entry already exists
                if [ $(grep ${ENTRY_NAME} ${SITE_ROOT}/${ZONEFILE_NAME} | grep -c ${ENTRY_TYPE}) != 0 ]
                then
                    ## entry already exists. adding a new one would be silly.
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Entry type ${ENTRY_TYPE} with name ${ENTRY_NAME} already exists. Cannot add record.";

                    RETURN_CODE=43;
                else
                    ## ok, our zonefile exists. we can slide in the new entry.
                    ## take backups
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BACKUP_FILE->${BACKUP_FILE}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backing up files..";

                    ## tar+gzip
                    if [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ]
                    then
                        tar cf "${NAMED_ROOT}/${BACKUP_DIRECTORY}"/${BACKUP_FILE}.tar -C ${SITE_ROOT} ${ZONEFILE_NAME} \
                            ${PRIMARY_DC}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}) ${SECONDARY_DC}/$(cut -d "." -f 1-2<<< ${ZONEFILE_NAME});
                        gzip "${NAMED_ROOT}/${BACKUP_DIRECTORY}"/${BACKUP_FILE}.tar;
                    else
                        tar cf "${NAMED_ROOT}/${BACKUP_DIRECTORY}"/${BACKUP_FILE}.tar -C ${SITE_ROOT} ${ZONEFILE_NAME} \
                            ${PRIMARY_DC}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}) ${SECONDARY_DC}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}) > /dev/null 2>&1;
                        gzip "${NAMED_ROOT}/${BACKUP_DIRECTORY}"/${BACKUP_FILE}.tar > /dev/null 2>&1;
                    fi

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete. Validating..";

                    ## make sure our backup file got created
                    if [ -s "${NAMED_ROOT}/${BACKUP_DIRECTORY}"/${BACKUP_FILE}.tar.gz ]
                    then
                        ## unset BACKUP_FILE var
                        unset BACKUP_FILE;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup validation complete. Creating working copies..";

                        ## take copies of the files to operate against
                        ## we need to update the serial number, so lets do it here
                        cp ${SITE_ROOT}/${ZONEFILE_NAME} "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/${ZONEFILE_NAME};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding audit indicators..";

                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                        ## validate the input
                        addServiceIndicators -r ${GROUP_ID}${BUSINESS_UNIT} -f ${ZONEFILE_NAME} -t $(grep "Currently live in" ${SITE_ROOT}/${ZONEFILE_NAME} | awk '{print $5}') -i ${REQUESTING_USER} -c ${CHANGE_NUM} -e;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        ## make sure our ret code is zero. if its not, we
                        ## can keep going, but the change wont load
                        if [ ${RET_CODE} -ne 0 ]
                        then
                            ## it isnt. issue out a warning
                            "${LOGGER}" "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Return code from addServiceIndicators non-zero. Processing will continue, but changes will not be loaded.";

                            WARNING_CODE=31;
                        fi

                        cp ${SITE_ROOT}/${PRIMARY_DC}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}) \
                            "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}).${PRIMARY_DC};
                        cp ${SITE_ROOT}/${SECONDARY_DC}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}) \
                            "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}).${SECONDARY_DC};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copies created. Validating..";

                        ## make sure we have them now
                        if [ -s "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/${ZONEFILE_NAME} ] \
                            && [ -s "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}).${PRIMARY_DC} ] \
                            && [ -s "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}).${SECONDARY_DC} ]
                        then
                            ## ok, we can keep going
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copies validated. Continuing..";

                            ## ok, at this point we can add in the new entry.
                            ## we should have the information we need to do so
                            case ${ENTRY_TYPE} in
                                [Aa]|[Nn][Ss]|[Cc][Nn][Aa][Mm][Ee]|[Tt][Xx][Tt])
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding entry ${ENTRY_NAME}             IN      ${ENTRY_TYPE}           ${ENTRY_RECORD}";

                                    echo "${ENTRY_NAME}             IN      ${ENTRY_TYPE}           ${ENTRY_RECORD}" \
                                        >> "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/${ZONEFILE_NAME};

                                    echo "${ENTRY_NAME}             IN      ${ENTRY_TYPE}           ${ENTRY_RECORD}" \
                                        >> "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}).${PRIMARY_DC};

                                    echo "${ENTRY_NAME}             IN      ${ENTRY_TYPE}           ${ENTRY_RECORD}" \
                                        >> "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}).${SECONDARY_DC};

                                    ENTRY_WRITTEN=${_TRUE};
                                    ;;
                                [Mm][Xx])
                                    ## mx records will have a weight associated
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding entry ${ENTRY_NAME}             IN      ${ENTRY_TYPE}     ${ENTRY_PRIORITY}    ${ENTRY_RECORD}";

                                    echo "${ENTRY_NAME}             IN      ${ENTRY_TYPE}     ${ENTRY_PRIORITY}    ${ENTRY_RECORD}" \
                                        >> "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/${ZONEFILE_NAME};

                                    echo "${ENTRY_NAME}             IN      ${ENTRY_TYPE}     ${ENTRY_PRIORITY}    ${ENTRY_RECORD}" \
                                        >> "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}).${PRIMARY_DC};

                                    echo "${ENTRY_NAME}             IN      ${ENTRY_TYPE}     ${ENTRY_PRIORITY}    ${ENTRY_RECORD}" \
                                        >> "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}).${SECONDARY_DC};

                                    ENTRY_WRITTEN=${_TRUE};
                                    ;;
                                [Ss][Rr][Vv])
                                    ## set up our record information
                                    ## service records are special because theres ALOT of "INFO"
                                    ## in them
                                    ## service records are constructed as follows:
                                    ##_service._protocol.name TTL Class SRV Priority Weight Port Target
                                    ## sample (email record for smtp):
                                    ## _submission._tcp.email.caspersbox.com 86400 IN SRV 10 10 25 caspersb-r1b13.caspersbox.com
                                    ## see http://en.wikipedia.org/wiki/SRV_record for more "INFO"
                                    ## set up our record information
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding entry ${SRV_TYPE}.${SRV_PROTOCOL}.${SRV_NAME}    ${SRV_TTL}    ${SRV_PRIORITY}    ${SRV_WEIGHT}    ${SRV_PORT}    ${SRV_TARGET}";

                                    echo "${ENTRY_TYPE}.${ENTRY_PROTOCOL}.${ENTRY_NAME}    ${ENTRY_TTL}    ${ENTRY_PRIORITY}    ${ENTRY_WEIGHT}    ${ENTRY_PORT}    ${ENTRY_TARGET}" \
                                        >> "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/${ZONEFILE_NAME};

                                    echo "${ENTRY_TYPE}.${ENTRY_PROTOCOL}.${ENTRY_NAME}    ${ENTRY_TTL}    ${ENTRY_PRIORITY}    ${ENTRY_WEIGHT}    ${ENTRY_PORT}    ${ENTRY_TARGET}" \
                                        >> "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}).${PRIMARY_DC};

                                    echo "${ENTRY_TYPE}.${ENTRY_PROTOCOL}.${ENTRY_NAME}    ${ENTRY_TTL}    ${ENTRY_PRIORITY}    ${ENTRY_WEIGHT}    ${ENTRY_PORT}    ${ENTRY_TARGET}" \
                                        >> "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}).${SECONDARY_DC};

                                    ENTRY_WRITTEN=${_TRUE};
                                    ;;
                                *)
                                    ## invalid entry type, cant continue
                                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create backup of zone files. Cannot continue.";

                                    RETURN_CODE=49;
                                    ;;
                            esac

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Entry added. Validating..";

                            if [ ! -z "${ENTRY_WRITTEN}" ] && [ "${ENTRY_WRITTEN}" = "${_TRUE}" ]
                            then
                                ## ok, we're told the entry was written. verify it
                                if [ $(grep ${ENTRY_NAME} "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/${ZONEFILE_NAME} | grep -c ${ENTRY_TYPE}) -eq 1 ]
                                then
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully added entry to primary zone file. Checking datacenter zones..";

                                    if [ $(grep ${ENTRY_NAME} "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}).${PRIMARY_DC} | grep -c ${ENTRY_TYPE}) -eq 1 ] \
                                        && [ $(grep ${ENTRY_NAME} "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}).${SECONDARY_DC} | grep -c ${ENTRY_TYPE}) -eq 1 ]
                                    then
                                        ## ok, good everything has it. lets take our checksums
                                        TMP_FILE_CKSUM=$(cksum "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/${ZONEFILE_NAME} | awk '{print $1}');

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_FILE_CKSUM->${TMP_FILE_CKSUM}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying file..";

                                        mv "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/${ZONEFILE_NAME} ${SITE_ROOT}/${ZONEFILE_NAME};

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copy complete. Validating..";

                                        if [ -s ${SITE_ROOT}/${ZONEFILE_NAME} ]
                                        then
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copy validated. Checksummimg..";

                                            OP_FILE_CKSUM=$(cksum ${SITE_ROOT}/${ZONEFILE_NAME} | awk '{print $1}');

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_FILE_CKSUM->${OP_FILE_CKSUM}";

                                            if [ ${TMP_FILE_CKSUM} -eq ${OP_FILE_CKSUM} ]
                                            then
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksums validated. Continuing with datacenter files..";

                                                ## move completed. continue on with dc files
                                                for DATACENTER in ${PRIMARY_DC} ${SECONDARY_DC}
                                                do
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on ${DATACENTER}";

                                                    TMP_FILE_CKSUM=$(cksum "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}).${DATACENTER} | awk '{print $1}');

                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_FILE_CKSUM->${TMP_FILE_CKSUM}";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying file..";

                                                    mv "${NAMED_ROOT}/${PLUGIN_WORK_DIRECTORY}"/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}).${DATACENTER} ${SITE_ROOT}/${DATACENTER}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME});

                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copy complete. Validating..";

                                                    if [ -s ${SITE_ROOT}/${DATACENTER}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}) ]
                                                    then
                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copy validated. Checksummimg..";

                                                        OP_FILE_CKSUM=$(cksum ${SITE_ROOT}/${DATACENTER}/$(cut -d "." -f 1-2 <<< ${ZONEFILE_NAME}) | awk '{print $1}');

                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_FILE_CKSUM->${OP_FILE_CKSUM}";

                                                        if [ ${TMP_FILE_CKSUM} != ${OP_FILE_CKSUM} ]
                                                        then
                                                            ## move failed.
                                                            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${DATACENTER} checksum mismatch.";

                                                            (( ERROR_COUNT += 1 ));
                                                        fi
                                                    else
                                                        ## file empty
                                                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${DATACENTER} file empty.";

                                                        (( ERROR_COUNT += 1 ));
                                                    fi
                                                done

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Datacenter zonefiles copied. Validating..";

                                                if [ ${ERROR_COUNT} -eq 0 ]
                                                then
                                                    ## everything worked. reload the zone
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "All zonefiles successfully updated. Reloading zone ${ZONE_NAME}..";

                                                    ## call out and reload
                                                    ## reload on master first, if its good, then continue
                                                    if [ "${SPLIT_HORIZON}" = "${_TRUE}" ]
                                                    then
                                                        for HORIZON in ${HORIZONS}
                                                        do
                                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HORIZON -> ${HORIZON}";
                                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Excuting command "${NAMED_ROOT}"/${PLUGIN_LIB_DIRECTORY}/excutors/excuteRNDCCommands.sh -s ${NAMED_MASTER} -p ${RNDC_LOCAL_PORT} -y ${RNDC_LOCAL_KEY} -c reload -z "${ZONE_NAME}" -i ${HORIZON} -e";

                                                            unset METHOD_NAME;
                                                            unset CNAME;

                                                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                                                            ## validate the input
                                                            "${NAMED_ROOT}"/${PLUGIN_LIB_DIRECTORY}/excutors/excuteRNDCCommands.sh -s ${NAMED_MASTER} -p ${RNDC_LOCAL_PORT} -y ${RNDC_LOCAL_KEY} -c reload -z "${ZONE_NAME}" -i ${HORIZON} -e;
                                                            typeset -i RET_CODE=${?};

                                                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                                            CNAME="${THIS_CNAME}";
                                                            typeset METHOD_NAME="${THIS_CNAME}#${0}";

                                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                                                        done
                                                    else
                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Excuting command "${NAMED_ROOT}"/${PLUGIN_LIB_DIRECTORY}/excutors/excuteRNDCCommands.sh -s ${NAMED_MASTER} -p ${RNDC_LOCAL_PORT} -y ${RNDC_LOCAL_KEY} -c reload -z "${ZONE_NAME}" -e";

                                                        unset METHOD_NAME;
                                                        unset CNAME;

                                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                                                        ## validate the input
                                                        "${NAMED_ROOT}"/${PLUGIN_LIB_DIRECTORY}/excutors/excuteRNDCCommands.sh -s ${NAMED_MASTER} -p ${RNDC_LOCAL_PORT} -y ${RNDC_LOCAL_KEY} -c reload -z "${ZONE_NAME}" -e
                                                        typeset -i RET_CODE=${?};

                                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                                        CNAME="${THIS_CNAME}";
                                                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                                                    fi

                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE->${RET_CODE}";

                                                    if [ ${RET_CODE} -eq 0 ]
                                                    then
                                                        ## xlnt. we've reloaded. continue forward.
                                                        ## the reload does the flush for us, so we
                                                        ## dont have to go back and do it again.
                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${NAMED_MASTER} successfully reloaded change. Validating..";

                                                        ## validate the removal. run a dig for the entry
                                                        LOOKUP_RESPONSE=$(dig @${NAMED_MASTER} +short -t a ${ENTRY_NAME}.${ZONE_NAME});

                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LOOKUP_RESPONSE->${LOOKUP_RESPONSE}";

                                                        if [ ! -z "${LOOKUP_RESPONSE}" ]
                                                        then
                                                            ## xlnt, added. continue with slave zones - just reload here, we dont
                                                            ## really need to validate that it was removed. although i guess we could.

                                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reload complete and removal validated. Reloading slaves..";

                                                            for SLAVE in ${DNS_SLAVES[*]}
                                                            do
                                                                if [ "${SPLIT_HORIZON}" = "${_TRUE}" ]
                                                                then
                                                                    for HORIZON in ${HORIZONS}
                                                                    do
                                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HORIZON -> ${HORIZON}";
                                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Excuting command "${NAMED_ROOT}"/${PLUGIN_LIB_DIRECTORY}/excutors/excuteRNDCCommands.sh -s ${SLAVE} -p ${RNDC_REMOTE_PORT} -y ${RNDC_REMOTE_KEY} -c reload -z "${ZONE_NAME}" -i ${HORIZON} -e";

                                                                        unset METHOD_NAME;
                                                                        unset CNAME;

                                                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                                                                        ## validate the input
                                                                        "${NAMED_ROOT}"/${PLUGIN_LIB_DIRECTORY}/excutors/excuteRNDCCommands.sh -s ${SLAVE} -p ${RNDC_REMOTE_PORT} -y ${RNDC_REMOTE_KEY} -c reload -z "${ZONE_NAME}" -i ${HORIZON} -e
                                                                        typeset -i RET_CODE=${?};

                                                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                                                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                                                        CNAME="${THIS_CNAME}";
                                                                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                                                        if [ ${RET_CODE} -ne 0 ]
                                                                        then
                                                                            "${LOGGER}" "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: Failed to initiate service reload on ${SLAVE}. Please update manually.";

                                                                            RETURN_CODE=86;
                                                                        fi
                                                                    done
                                                                else
                                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Excuting command "${NAMED_ROOT}"/${PLUGIN_LIB_DIRECTORY}/excutors/excuteRNDCCommands.sh -s ${SLAVE} -p ${RNDC_REMOTE_PORT} -y ${RNDC_REMOTE_KEY} -c reload -z "${ZONE_NAME}" -e";

                                                                    unset METHOD_NAME;
                                                                    unset CNAME;

                                                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

                                                                    ## validate the input
                                                                    ${PLUGIN_LIB_DIRECTORY}/excutors/excuteRNDCCommands.sh -s ${SLAVE} -p ${RNDC_REMOTE_PORT} -y ${RNDC_REMOTE_KEY} -c reload -z "${ZONE_NAME}" -e;
                                                                    typeset -i RET_CODE=${?};

                                                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                                                    CNAME="${THIS_CNAME}";
                                                                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                                                    if [ ${RET_CODE} -ne 0 ]
                                                                    then
                                                                        "${LOGGER}" "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: Failed to initiate service reload on ${SLAVE}. Please update manually.";

                                                                        RETURN_CODE=86;
                                                                    fi
                                                                fi
                                                            done

                                                            ## and this completes.
                                                            if [ -z "${RETURN_CODE}" ]
                                                            then
                                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Addition of entry ${ENTRY_NAME} to ${ZONE_NAME} completed.";
                                                                "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone entry added: Zone Name: ${ZONE_NAME}; Entry Name: ${ENTRY_NAME}; Added by: ${REQUESTING_USER}";

                                                                if [ ! -z "${WARNING_CODE}" ]
                                                                then
                                                                    RETURN_CODE=${WARNING_CODE};
                                                                else
                                                                    RETURN_CODE=0;
                                                                fi
                                                            else
                                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Addition of entry ${ENTRY_NAME} to ${ZONE_NAME} completed.";
                                                                "${LOGGER}" "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone entry added: Zone Name: ${ZONE_NAME}; Entry Name: ${ENTRY_NAME}; Added by: ${REQUESTING_USER}";

                                                                RETURN_CODE=${RETURN_CODE};
                                                            fi
                                                        else
                                                            ## reload failed on the master. error out
                                                            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Entry remains on ${NAMED_MASTER}. Please confirm removal and continue manually. Cannot continue.";

                                                            RETURN_CODE=92;
                                                        fi
                                                    else
                                                        ## reload failed. since everything else is done, this isnt horrible,
                                                        ## but it does mean that we cant run the reload against the slaves
                                                        ## because the master doesnt have it and doesnt know.
                                                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "named service reload on ${NAMED_MASTER} FAILED. Cannot continue.";

                                                        RETURN_CODE=92;
                                                    fi
                                                else
                                                    ## something broke. error out
                                                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while relocating datacenter-specific zones. Cannot continue.";

                                                    RETURN_CODE=28;
                                                fi
                                            else
                                                ## checksum mismatch. error out
                                                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum mismatch for operational zonefile. Cannot continue.";

                                                RETURN_CODE=90;
                                            fi
                                        else
                                            ## move failed, file doesnt exist or is empty
                                            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to properly relocate zonefile. Cannot continue.";

                                            RETURN_CODE=28;
                                        fi
                                    else
                                        ## add failed to dc zones
                                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to properly write new data to zonefile. Cannot continue.";

                                        RETURN_CODE=42;
                                    fi
                                else
                                    ## entry wasnt written to primary. error out
                                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to properly write new data to zonefile. Cannot continue.";

                                    RETURN_CODE=28;
                                fi
                            else
                                ## the entry written variable is empty
                                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to properly write new data to zonefile. Cannot continue.";

                                RETURN_CODE=6;
                            fi
                        else
                            ## no working copies, cant continue
                            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create temporary files. Cannot continue.";

                            RETURN_CODE=47;
                        fi
                    else
                        ## no backup, no continue
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create backup of zone files. Cannot continue.";

                        RETURN_CODE=57;
                    fi
                fi
            else
                ## zonefile doesnt exist. error out
                "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested project code does not exist. Cannot continue.";

                RETURN_CODE=9;
            fi
        else
            ## no zonefile name. damn.
            "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested zone does not exist. Cannot continue.";

            RETURN_CODE=37;
        fi
    else
        ## no biz unit for site root, fail out
        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested business unit does not have a defined group. Cannot continue.";

        RETURN_CODE=10;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    ## unsets
    unset CHANGE_DATE;
    unset BACKUP_FILE;
    unset SITE_ROOT;
    unset ZONEFILES;
    unset ZONEFILE;
    unset ZONEFILE_NAME;
    unset WARNING_CODE;
    unset ENTRY_WRITTEN;
    unset TMP_FILE_CKSUM;
    unset OP_FILE_CKSUM;
    unset HORIZON;
    unset RET_CODE;
    unset LOOKUP_RESPONSE;
    unset SLAVE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=3;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    echo "${THIS_CNAME} - Excute zone additions to the DNS infrastructure.\n";
    echo "Usage: ${THIS_CNAME} [ -b <business unit> ] [ -p <project code> ] [ -z <zone name> ] [ -i <requesting user> ] [ -c <change request> ] [ -n <filename> ] [ -a <entry> ] [ -s ] [ -e ] [ -h|-? ]
    -b         -> The associated business unit
    -p         -> The associated project code
    -z         -> The zone name, eg example.com
    -i         -> The user performing the request
    -c         -> The change order associated with this request
    -n         -> Add a new zone to the DNS infrastructure. Full path to zone data required.
    -a         -> Add a new entry to an existing zone. Comma-delimited information set required.
    -s         -> Specifies whether or not to operate against a slave server. Only valid with -n.
    -e         -> Excute processing
    -h|-?      -> Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

    return ${RETURN_CODE};
}

[ ${#} -eq 0 ] && usage && RETURN_CODE=${?};

while getopts ":b:p:z:i:c:n:a:seh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        b)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting BUSINESS_UNIT..";

            ## Capture the site root
            typeset -u BUSINESS_UNIT="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
            ;;
        p)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting PROJECT_CODE..";

            ## Capture the site root
            typeset -u PROJECT_CODE="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROJECT_CODE -> ${PROJECT_CODE}";
            ;;
        z)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ZONE_NAME..";

            ## Capture the site root
            ZONE_NAME=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_NAME -> ${ZONE_NAME}";
            ;;
        i)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting REQUESTING_USER..";

            ## Capture the change control
            REQUESTING_USER="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUESTING_USER -> ${REQUESTING_USER}";
            ;;
        c)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            ## Capture the change control
            typeset -u CHANGE_NUM="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        n)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting INSTALL_ZONE to TRUE..";

            ## Capture the change control
            INSTALL_ZONE=${_TRUE};
            typeset -l ZONE_DATA_FILE="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        a)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ADD_ENTRY to TRUE";

            ## Capture the change control
            ADD_ENTRY=${_TRUE};
            typeset -u ENTRY_TYPE=$(cut -d "," -f 1 <<< ${OPTARG});

            case ${ENTRY_TYPE} in
                [Aa]|[Nn][Ss]|[Cc][Nn][Aa][Mm][Ee]|[Tt][Xx][Tt])
                    ## these only have a target, no other data associated with them
                    typeset -l ENTRY_NAME=$(cut -d "," -f 2 <<< ${OPTARG});
                    typeset -l ENTRY_RECORD=$(cut -d "," -f 3 <<< ${OPTARG});
                    ;;
                [Mm][Xx])
                    ## mx records will have a weight associated
                    typeset -l ENTRY_NAME=$(cut -d "," -f 2 <<< ${OPTARG});
                    typeset -l ENTRY_PRIORITY=$(cut -d "," -f 3 <<< ${OPTARG});
                    typeset -l ENTRY_RECORD=$(cut -d "," -f 4 <<< ${OPTARG});
                    ;;
                [Ss][Rr][Vv])
                    ## set up our record information
                    ## service records are special because theres ALOT of "INFO"
                    ## in them
                    ## service records are constructed as follows:
                    ##_service._protocol.name TTL Class SRV Priority Weight Port Target
                    ## sample (email record for smtp):
                    ## _submission._tcp.email.caspersbox.com 86400 IN SRV 10 10 25 caspersb-r1b13.caspersbox.com
                    ## see http://en.wikipedia.org/wiki/SRV_record for more "INFO"
                    ## set up our record information
                    ENTRY_TYPE=$(cut -d "," -f 2 <<< ${OPTARG});
                    ENTRY_PROTOCOL=$(cut -d "," -f 3 <<< ${OPTARG});
                    ENTRY_NAME=$(cut -d "," -f 4 <<< ${OPTARG});
                    ENTRY_TTL=$(cut -d "," -f 5 <<< ${OPTARG});
                    ENTRY_PRIORITY=$(cut -d "," -f 6 <<< ${OPTARG});
                    ENTRY_WEIGHT=$(cut -d "," -f 7 <<< ${OPTARG});
                    ENTRY_PORT=$(cut -d "," -f 8 <<< ${OPTARG});
                    ENTRY_TARGET=$(cut -d "," -f 9 <<< ${OPTARG});
                    ;;
                *)
                    ## as-yet unsupported record type - this list should follow the list
                    ## of data helpers. if theres no record helper for it then this excutor
                    ## should be able to add it.
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid record type was provided. Cannot continue.";

                    RETURN_CODE=49;
                    ;;
            esac

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_ENTRY -> ${ADD_ENTRY}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTRY_NAME -> ${ENTRY_NAME}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTRY_TYPE -> ${ENTRY_TYPE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTRY_TARGET -> ${ENTRY_TARGET}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and excute
            if [ -z "${RETURN_CODE}" ]
            then
                if [ -z "${BUSINESS_UNIT}" ]
                then
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=15;
                elif [ -z "${PROJECT_CODE}" ]
                then
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=24;
                elif [ -z "${ZONE_NAME}" ]
                then
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=24;
                elif [ -z "${CHANGE_NUM}" ]
                then
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change order was provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=17;
                elif [ -z "${REQUESTING_USER}" ]
                then
                    "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requestors username was not provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=20;
                else
                    ## We have enough information to process the request, continue
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - excuting";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    if [ ! -z "${ADD_ENTRY}" ] && [ "${ADD_ENTRY}" = "${_TRUE}" ]
                    then
                        add_zone_entry && RETURN_CODE=${?};
                    elif [ ! -z "${INSTALL_ZONE}" ] && [ "${INSTALL_ZONE}" = "${_TRUE}" ]
                    then
                        [ -z "${SLAVE_OPERATION}" ] && [ "${SLAVE_OPERATION}" = "${_TRUE}" ] && installSlaveZone && RETURN_CODE=${?} || installMasterZone && RETURN_CODE=${?};
                    else
                        ## no valid command type
                        "${LOGGER}" "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No valid command type was provided. Cannot continue.";

                        RETURN_CODE=3;
                    fi
                fi
            fi
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage && RETURN_CODE=${?};
            ;;
    esac
done

trap 'unlockProcess "${LOCKFILE}" "${$}"; return "${RETURN_CODE}"' INT TERM EXIT;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && "${LOGGER}" "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

[ -z "${RETURN_CODE}" ] && echo "1" || echo "${RETURN_CODE}";
[ -z "${RETURN_CODE}" ] && return 1 || return "${RETURN_CODE}";
