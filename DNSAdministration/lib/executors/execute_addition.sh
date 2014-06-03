#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  execute_addition.sh
#         USAGE:  ./execute_addition.sh
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

## Application constants
[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

[[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[ ${#} -eq 0 ] && usage;

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

unset METHOD_NAME;
unset CNAME;

## check security
. ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/security/check_main.sh > /dev/null 2>&1;
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Security configuration does not allow the requested action." && echo ${RET_CODE} && exit ${RET_CODE};

## unset the return code
unset RET_CODE;

## lock it
${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/lock.sh lock ${$};
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Application currently in use." && echo ${RET_CODE} && exit ${RET_CODE};

unset RET_CODE;

CNAME="$(basename "${0}")";
METHOD_NAME="${CNAME}#startup";

trap "${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/lock.sh unlock ${$}; exit" INT TERM EXIT;

#===  FUNCTION  ===============================================================
#          NAME:  install_zone
#   DESCRIPTION:  Searches for and replaces audit indicators for the provided
#                 filename.
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function install_master_zone
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    CHANGE_DATE=$(date +"%m-%d-%Y");

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Installation of group ${GROUP_ID}${BUSINESS_UNIT} starting..";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Tarfile name -> ${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${IUSER_AUDIT}.tar.gz";

    if [ -s ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${IUSER_AUDIT}.tar.gz ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Tarfile exists, proceeding..";

        ## decompress the archive
        gzip -dc ${NAMED_ROOT}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${IUSER_AUDIT}.tar.gz | (cd ${NAMED_ROOT}/${TMP_DIRECTORY}; tar xf -);

        ## make sure the tar extracted properly
        if [ ! -d ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT} ]
        then
            ## tar did not extract properly. throw out an error
            $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "tarfile extraction FAILED. Cannot continue.")

            RETURN_CODE=54;
        else
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moving group directory ${GROUP_ID}${BUSINESS_UNIT} into ${NAMED_ROOT}/${NAMED_MASTER_ROOT}";

            ## directory should exist now, lets move it into place
            mv ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT} ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Move complete. Proceeding..";

            ## zonefiles should be in place, verify
            if [ -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT} ]
            then
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Move verified. Proceeding..";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating ${NAMED_ROOT}/${NAMED_CONF_DIR}/$(echo ${BUSINESS_UNIT} | tr "[A-Z]" "[a-z]").${NAMED_ZONE_CONF_NAME}";

                ## generate our conf file name
                typeset -l ZONE_CONF_NAME=${BUSINESS_UNIT}.${NAMED_ZONE_CONF_NAME};

                if [ ! -z "${USE_TRANSFER_ACL}" ] && [ "${USE_TRANSFER_ACL}" = "${_TRUE}" ]
                then
                    ## ok, time to create a conf file for the new zone
                    ## and update named.conf so it gets loaded
                    print "zone \"${ZONE_NAME}\" IN {" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    type               master;" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    allow-update       { none; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    allow-transfer     { key ${TSIG_TRANSFER_KEY}; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    file               \"${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1).$(echo ${PROJECT_CODE} | tr "[a-z]" "[A-Z]")\";" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "};\n" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                elif [ ! -z "${USE_NAMED_ACL}" ] && [ "${USE_NAMED_ACL}" = "${_TRUE}" ]
                then
                    ## ok, time to create a conf file for the new zone
                    ## and update named.conf so it gets loaded
                    print "zone \"${ZONE_NAME}\" IN {" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    type               slave;" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    masters            { \"${NAMED_MASTER_ACL}\"; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    allow-update       { none; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    allow-transfer     { ${NAMED_TRANSFER_ACL}; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    file               \"${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1).$(echo ${PROJECT_CODE} | tr "[a-z]" "[A-Z]")\";" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "};\n" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                elif [ ! -z "${USE_NAMED_ACL}" ] && [ "${USE_NAMED_ACL}" = "${_TRUE}" ] && [ ! -z "${USE_TRANSFER_ACL}" ] && [ "${USE_TRANSFER_ACL}" = "${_TRUE}" ]
                then
                    ## ok, time to create a conf file for the new zone
                    ## and update named.conf so it gets loaded
                    print "zone \"${ZONE_NAME}\" IN {" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    type               slave;" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    masters            { \"${NAMED_MASTER_ACL}\"; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    allow-update       { none; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    allow-transfer     { key ${TSIG_TRANSFER_KEY}; ${NAMED_TRANSFER_ACL}; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    file               \"${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1).$(echo ${PROJECT_CODE} | tr "[a-z]" "[A-Z]")\";" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "};\n" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                else
                    ## no transfer acl is specified. this could be an oversight or on purpose. assume on purpose
                    ## and specify none for allow-transfer
                    print "zone \"${ZONE_NAME}\" IN {" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    type               slave;" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    masters            { \"${NAMED_MASTER_ACL}\"; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    allow-update       { none; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    allow-transfer     { none; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "    file               \"${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1).$(echo ${PROJECT_CODE} | tr "[a-z]" "[A-Z]")\";" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    print "};\n" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                fi

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Created ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME}";

                ## we should have our conf file created and it should contain our new zone information.
                ## lets include that file into the named conf now
                if [ -s ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME} ]
                then
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printing include statement to ${NAMED_CONF_FILE}";

                    ## take a backup first
                    cp ${NAMED_CONF_FILE} ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/$(echo ${NAMED_CONF_FILE} | cut -d "/" -f 5).${CHANGE_NUM};

                    ## make sure the backup file exists -
                    if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/$(echo ${NAMED_CONF_FILE} | cut -d "/" -f 5).${CHANGE_NUM} ]
                    then
                        print "include \"/${NAMED_CONF_DIR}/${ZONE_CONF_NAME}\";" >> ${NAMED_CONF_FILE};

                        ## should have our new zone included now. verify it
                        if [ $(grep -c ${ZONE_CONF_NAME} ${NAMED_CONF_FILE}) -eq 1 ]
                        then
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printed include statement to ${NAMED_CONF_FILE}";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing tar file..";

                            ## clean up our tar
                            rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${IUSER_AUDIT}.tar.gz;
                            rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${IUSER_AUDIT}.tar;
                            rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT};

                            ## make sure it actually got removed. if not, log a warning
                            if [ -s ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${IUSER_AUDIT}.tar.gz ]
                            then
                                ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to remove tarfile.";
                            fi

                            ## audit log
                            ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_NAME} installed by ${IUSER_AUDIT} per change ${CHANGE_NUM} on $(date +"%m-%d-%Y") at $(date +"%H:%M:%S")";

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            ## and finally return zero
                            RETURN_CODE=0;
                        else
                            ## the new zone wasnt added to named.conf
                            ## we send back an error code informing
                            RETURN_CODE=34;
                        fi
                    else
                        ## we couldnt take a backup of the named conf file. fail out.
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create backup of ${NAMED_CONF_FILE}. Cannot continue.";

                        RETURN_CODE=57;
                    fi
                else
                    ## something happened while we were creating our new conf file. send an error back
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred creating the zone configuration file.";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                    RETURN_CODE=33;
                fi
            else
                ## the new zone didnt copy in. send an error back
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred copying the new zone.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                RETURN_CODE=32;
            fi
        fi
    else
        ## tarfile provided doesnt exist. send an error back, we cant continue
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The provided tarfile does not exist.";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
        RETURN_CODE=14;
    fi

    ## no matter what happens, we remove the temp files from this filesystem
    rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${PROJECT_CODE}.${CHANGE_DATE}.${ZONE_NAME}.tar.gz;
    rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${PROJECT_CODE}.${CHANGE_DATE}.${ZONE_NAME}.tar;
    rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${PROJECT_CODE};

    unset ZONE_CONF_NAME;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  install_slave_zone
#   DESCRIPTION:  Searches for and replaces audit indicators for the provided
#                 filename.
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function install_slave_zone
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    CHANGE_DATE=$(date +"%m-%d-%Y");

    ## make sure we are indeed running on a slave server. executing
    ## these steps on a master would break.
    if [ ! $(echo ${DNS_SLAVES[@]} | grep -c $(uname -n)) -eq 1 ]
    then
        ## we are not on a slave server we know about. abort.
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Slave zone installations are only supported on configured slave nameservers. Aborting.";

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=998;
    else
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Installation of group ${GROUP} starting..";

        if [ -s ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${IUSER_AUDIT}.tar.gz ]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Tarfile exists, proceeding..";

            ## decompress the archive
            gzip -dc ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${IUSER_AUDIT}.tar.gz | (cd ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}; tar xf -);

            ## make sure the tar extracted properly
            if [ ! -d ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT} ]
            then
                ## tar did not extract properly. throw out an error
                $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "tarfile extraction FAILED. Cannot continue.")

                RETURN_CODE=54;
            else
                ## clean up the datacenter-specific directories, they do not need to be there
                ## if the install is on a slave AND the slave is not a failsafe master
                if [ ! $(echo ${EXT_SLAVES[@]} | grep -c $(uname -n)) -eq 1 ]
                then
                    ## we're on an external slave. remove the site-specific directories prior to placement
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "External slave detected. Removing site-specific directories..";

                    rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC};
                    rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC};

                    ## check if they were removed, if not, warn
                    if [ -d ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC} ] && [ -d ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC} ]
                    then
                        ## oops. theyre still there. warn, but do not fail
                        ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to remove site-specific directories.";
                    fi
                fi

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moving group directory ${GROUP_ID}${BUSINESS_UNIT} into ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}";

                ## directory should exist now, lets move it into place
                mv ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT} ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT};

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Move complete. Proceeding..";

                ## zonefiles should be in place, verify
                if [ -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT} ]
                then
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Move verified. Proceeding..";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating ${NAMED_ROOT}/${NAMED_CONF_DIR}/$(echo ${BUSINESS_UNIT} | tr "[A-Z]" "[a-z]").${NAMED_ZONE_CONF_NAME}";

                    ## generate our conf file name
                    typset -l ZONE_CONF_NAME=${BUSINESS_UNIT}.${NAMED_ZONE_CONF_NAME};

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_CONF_NAME -> ${ZONE_CONF_NAME}";

                    if [ ! -z "${USE_TRANSFER_ACL}" ] && [ "${USE_TRANSFER_ACL}" = "${_TRUE}" ]
                    then
                        ## ok, time to create a conf file for the new zone
                        ## and update named.conf so it gets loaded
                        print "zone \"${ZONE_NAME}\" IN {" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    type               slave;" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    masters            { \"${NAMED_MASTER_ACL}\"; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    allow-update       { none; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    allow-transfer     { key ${TSIG_TRANSFER_KEY}; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    file               \"${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1).$(echo ${PROJECT_CODE} | tr "[a-z]" "[A-Z]")\";" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "};\n" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    elif [ ! -z "${USE_NAMED_ACL}" ] && [ "${USE_NAMED_ACL}" = "${_TRUE}" ]
                    then
                        ## ok, time to create a conf file for the new zone
                        ## and update named.conf so it gets loaded
                        print "zone \"${ZONE_NAME}\" IN {" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    type               slave;" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    masters            { \"${NAMED_MASTER_ACL}\"; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    allow-update       { none; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    allow-transfer     { ${NAMED_TRANSFER_ACL}; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    file               \"${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1).$(echo ${PROJECT_CODE} | tr "[a-z]" "[A-Z]")\";" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "};\n" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    elif [ ! -z "${USE_NAMED_ACL}" ] && [ "${USE_NAMED_ACL}" = "${_TRUE}" ] && [ ! -z "${USE_TRANSFER_ACL}" ] && [ "${USE_TRANSFER_ACL}" = "${_TRUE}" ]
                    then
                        ## ok, time to create a conf file for the new zone
                        ## and update named.conf so it gets loaded
                        print "zone \"${ZONE_NAME}\" IN {" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    type               slave;" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    masters            { \"${NAMED_MASTER_ACL}\"; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    allow-update       { none; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    allow-transfer     { key ${TSIG_TRANSFER_KEY}; ${NAMED_TRANSFER_ACL}; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    file               \"${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1).$(echo ${PROJECT_CODE} | tr "[a-z]" "[A-Z]")\";" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "};\n" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    else
                        ## no transfer acl is specified. this could be an oversight or on purpose. assume on purpose
                        ## and specify none for allow-transfer
                        print "zone \"${ZONE_NAME}\" IN {" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    type               slave;" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    masters            { \"${NAMED_MASTER_ACL}\"; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    allow-update       { none; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    allow-transfer     { none; };" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "    file               \"${NAMED_SLAVE_ROOT}/${GROUP_ID}${BUSINESS_UNIT}/${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1).$(echo ${PROJECT_CODE} | tr "[a-z]" "[A-Z]")\";" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                        print "};\n" >> ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME};
                    fi

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Created ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME}";

                    ## we should have our conf file created and it should contain our new zone information.
                    ## lets include that file into the named conf now
                    if [ -s ${NAMED_ROOT}/${NAMED_CONF_DIR}/${ZONE_CONF_NAME} ]
                    then
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printing include statement to ${NAMED_CONF_FILE}";

                        ## take a backup first
                        cp ${NAMED_CONF_FILE} ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/$(echo ${NAMED_CONF_FILE} | cut -d "/" -f 5).${CHANGE_NUM};

                        ## make sure the backup file exists -
                        if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/$(echo ${NAMED_CONF_FILE} | cut -d "/" -f 5).${CHANGE_NUM} ]
                        then
                            print "include \"/${NAMED_CONF_DIR}/${ZONE_CONF_NAME}\";" >> ${NAMED_CONF_FILE};

                            ## should have our new zone included now. verify it
                            if [ $(grep -c ${ZONE_CONF_NAME} ${NAMED_CONF_FILE}) -eq 1 ]
                            then
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printed include statement to ${NAMED_CONF_FILE}";

                                ## ok, we're done. zone has been created, installed,
                                ## conf files have been created and updated.
                                ## clean up the files we were provided
                                rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${IUSER_AUDIT}.tar.gz;
                                rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${CHANGE_DATE}.${IUSER_AUDIT}.tar;
                                rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT};

                                ## audit log
                                ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_NAME} installed by ${IUSER_AUDIT} per change ${CHANGE_NUM} on $(date +"%m-%d-%Y") at $(date +"%H:%M:%S")";

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                ## and finally return zero
                                RETURN_CODE=0;
                            else
                                ## the new zone wasnt added to named.conf
                                ## we send back an error code informing
                                RETURN_CODE=34;
                            fi
                        else
                            ## we couldnt take a backup of the named conf file. fail out.
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create backup of ${NAMED_CONF_FILE}. Cannot continue.";

                            RETURN_CODE=57;
                        fi
                    else
                        ## something happened while we were creating our new conf file. send an error back
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred creating the zone configuration file.";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                        RETURN_CODE=33;
                    fi
                else
                    ## the new zone didnt copy in. send an error back
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred copying the new zone.";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                    RETURN_CODE=32;
                fi
            fi
        else
            ## tarfile provided doesnt exist. send an error back, we cant continue
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The provided tarfile does not exist.";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
            RETURN_CODE=14;
        fi
    fi

    unset ZONE_CONF_NAME;

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
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    CHANGE_DATE=$(date +"%m-%d-%Y");
    TARFILE_DATE=$(date +"%m-%d-%Y");
    BACKUP_FILE=${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT};

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Update of zone ${ZONE_NAME} starting..";

    ## make sure our zone data exists
    if [ -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT} ]
    then
        ## ok, biz unit dir exists
        SITE_ROOT=${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT};

        ## we have the biz unit, the project code, and the zone name.
        ## what we're going to do is grep for the biz unit and project code
        ## in the config files. we may get back more than one, if we do
        ## we need to search those files for the zone name to find the right
        ## file
        ZONEFILES=$(grep ${PROJECT_CODE} ${NAMED_ROOT}/${NAMED_CONF_DIR}/$(echo ${BUSINESS_UNIT} | tr "[A-Z]" "[a-z]").${NAMED_ZONE_CONF_NAME} \
             | awk '{print $2}' | cut -d "\"" -f 2);

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILES->${ZONEFILES}";

        for ZONEFILE in ${ZONEFILES}
        do
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE->${ZONEFILE}";

            if [ $(grep "SOA" ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${ZONEFILE} | grep -c ${ZONE_NAME}) == 1 ]
            then
                ## we have our zone. we can start working now
                ## this should just be the filename
                ZONEFILE_NAME=$(echo ${ZONEFILE} | cut -d "/" -f 3);

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE_NAME->${ZONEFILE_NAME}";
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
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Entry type ${ENTRY_TYPE} with name ${ENTRY_NAME} already exists. Cannot add record.";

                    RETURN_CODE=43;
                else
                    ## ok, our zonefile exists. we can slide in the new entry.
                    ## take backups
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "BACKUP_FILE->${BACKUP_FILE}");
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Backing up files..");

                    ## tar+gzip
                    if [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]]
                    then
                        tar cf ${NAMED_ROOT}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar -C ${SITE_ROOT} ${ZONEFILE_NAME} \
                            ${PRIMARY_DC}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2) ${SECONDARY_DC}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2);
                        gzip ${NAMED_ROOT}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar;
                    else
                        tar cf ${NAMED_ROOT}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar -C ${SITE_ROOT} ${ZONEFILE_NAME} \
                            ${PRIMARY_DC}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2) ${SECONDARY_DC}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2) > /dev/null 2>&1;
                        gzip ${NAMED_ROOT}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar > /dev/null 2>&1;
                    fi

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Backup complete. Validating..");

                    ## make sure our backup file got created
                    if [ -s ${NAMED_ROOT}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar.gz ]
                    then
                        ## unset BACKUP_FILE var
                        unset BACKUP_FILE;

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Backup validation complete. Creating working copies..");

                        ## take copies of the files to operate against
                        ## we need to update the serial number, so lets do it here 
                        cp ${SITE_ROOT}/${ZONEFILE_NAME} ${NAMED_ROOT}/${TMP_DIRECTORY}/${ZONEFILE_NAME};

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Adding audit indicators..");

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;

                        ## add our informational and audit indicators
                        . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/addServiceIndicators.sh -r ${GROUP_ID}${BUSINESS_UNIT} -f ${ZONEFILE_NAME} -t $(grep "Currently live in" ${SITE_ROOT}/${ZONEFILE_NAME} | awk '{print $5}') -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e;
                        RET_CODE=${?};

                        ## set method_name/cname back to this method
                        local METHOD_NAME="${CNAME}#${0}";
                        CNAME="$(basename "${0}")";

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "RET_CODE->${RET_CODE}");

                        ## make sure our ret code is zero. if its not, we
                        ## can keep going, but the change wont load
                        if [ ${RET_CODE} != 0 ]
                        then
                            ## it isnt. issue out a warning
                            $(${LOGGER} "WARN" ${METHOD_NAME} ${CNAME} ${LINENO} "Return code from addServiceIndicators non-zero. Processing will continue, but changes will not be loaded.");

                            WARNING_CODE=31;
                        fi

                        cp ${SITE_ROOT}/${PRIMARY_DC}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2) \
                            ${NAMED_ROOT}/${TMP_DIRECTORY}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2).${PRIMARY_DC};
                        cp ${SITE_ROOT}/${SECONDARY_DC}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2) \
                            ${NAMED_ROOT}/${TMP_DIRECTORY}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2).${SECONDARY_DC};

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Copies created. Validating..");

                        ## make sure we have them now
                        if [ -s ${NAMED_ROOT}/${TMP_DIRECTORY}/${ZONEFILE_NAME} ] \
                            && [ -s ${NAMED_ROOT}/${TMP_DIRECTORY}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2).${PRIMARY_DC} ] \
                            && [ -s ${NAMED_ROOT}/${TMP_DIRECTORY}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2).${SECONDARY_DC} ]
                        then
                            ## ok, we can keep going
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Copies validated. Continuing..");

                            ## ok, at this point we can add in the new entry.
                            ## we should have the information we need to do so
                            case ${ENTRY_TYPE} in
                                [Aa]|[Nn][Ss]|[Cc][Nn][Aa][Mm][Ee]|[Tt][Xx][Tt])
                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Adding entry ${ENTRY_NAME}             IN      ${ENTRY_TYPE}           ${ENTRY_RECORD}");

                                    print "${ENTRY_NAME}             IN      ${ENTRY_TYPE}           ${ENTRY_RECORD}" \
                                        >> ${NAMED_ROOT}/${TMP_DIRECTORY}/${ZONEFILE_NAME};

                                    print "${ENTRY_NAME}             IN      ${ENTRY_TYPE}           ${ENTRY_RECORD}" \
                                        >> ${NAMED_ROOT}/${TMP_DIRECTORY}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2).${PRIMARY_DC};
                                        
                                    print "${ENTRY_NAME}             IN      ${ENTRY_TYPE}           ${ENTRY_RECORD}" \
                                        >> ${NAMED_ROOT}/${TMP_DIRECTORY}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2).${SECONDARY_DC};

                                    ENTRY_WRITTEN=${_TRUE};
                                    ;;
                                [Mm][Xx])
                                    ## mx records will have a weight associated
                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Adding entry ${ENTRY_NAME}             IN      ${ENTRY_TYPE}     ${ENTRY_PRIORITY}    ${ENTRY_RECORD}");

                                    print "${ENTRY_NAME}             IN      ${ENTRY_TYPE}     ${ENTRY_PRIORITY}    ${ENTRY_RECORD}" \
                                        >> ${NAMED_ROOT}/${TMP_DIRECTORY}/${ZONEFILE_NAME};

                                    print "${ENTRY_NAME}             IN      ${ENTRY_TYPE}     ${ENTRY_PRIORITY}    ${ENTRY_RECORD}" \
                                        >> ${NAMED_ROOT}/${TMP_DIRECTORY}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2).${PRIMARY_DC};

                                    print "${ENTRY_NAME}             IN      ${ENTRY_TYPE}     ${ENTRY_PRIORITY}    ${ENTRY_RECORD}" \
                                        >> ${NAMED_ROOT}/${TMP_DIRECTORY}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2).${SECONDARY_DC};

                                    ENTRY_WRITTEN=${_TRUE};
                                    ;;
                                [Ss][Rr][Vv])
                                    ## set up our record information
                                    ## service records are special because theres ALOT of info
                                    ## in them
                                    ## service records are constructed as follows:
                                    ##_service._protocol.name TTL Class SRV Priority Weight Port Target
                                    ## sample (email record for smtp):
                                    ## _submission._tcp.email.caspersbox.com 86400 IN SRV 10 10 25 caspersb-r1b13.caspersbox.com
                                    ## see http://en.wikipedia.org/wiki/SRV_record for more info
                                    ## set up our record information
                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Adding entry ${SRV_TYPE}.${SRV_PROTOCOL}.${SRV_NAME}    ${SRV_TTL}    ${SRV_PRIORITY}    ${SRV_WEIGHT}    ${SRV_PORT}    ${SRV_TARGET}");

                                    print "${ENTRY_TYPE}.${ENTRY_PROTOCOL}.${ENTRY_NAME}    ${ENTRY_TTL}    ${ENTRY_PRIORITY}    ${ENTRY_WEIGHT}    ${ENTRY_PORT}    ${ENTRY_TARGET}" \
                                        >> ${NAMED_ROOT}/${TMP_DIRECTORY}/${ZONEFILE_NAME};

                                    print "${ENTRY_TYPE}.${ENTRY_PROTOCOL}.${ENTRY_NAME}    ${ENTRY_TTL}    ${ENTRY_PRIORITY}    ${ENTRY_WEIGHT}    ${ENTRY_PORT}    ${ENTRY_TARGET}" \
                                        >> ${NAMED_ROOT}/${TMP_DIRECTORY}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2).${PRIMARY_DC};

                                    print "${ENTRY_TYPE}.${ENTRY_PROTOCOL}.${ENTRY_NAME}    ${ENTRY_TTL}    ${ENTRY_PRIORITY}    ${ENTRY_WEIGHT}    ${ENTRY_PORT}    ${ENTRY_TARGET}" \
                                        >> ${NAMED_ROOT}/${TMP_DIRECTORY}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2).${SECONDARY_DC};

                                    ENTRY_WRITTEN=${_TRUE};
                                    ;;
                                *)
                                    ## invalid entry type, cant continue
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create backup of zone files. Cannot continue.";

                                    RETURN_CODE=49;
                                    ;;
                            esac

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Entry added. Validating..");

                            if [ ! -z "${ENTRY_WRITTEN}" ] && [ "${ENTRY_WRITTEN}" = "${_TRUE}" ]
                            then
                                ## ok, we're told the entry was written. verify it
                                if [ $(grep ${ENTRY_NAME} ${NAMED_ROOT}/${TMP_DIRECTORY}/${ZONEFILE_NAME} | grep -c ${ENTRY_TYPE}) == 1 ]
                                then
                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Successfully added entry to primary zone file. Checking datacenter zones..");

                                    if [ $(grep ${ENTRY_NAME} ${NAMED_ROOT}/${TMP_DIRECTORY}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2).${PRIMARY_DC} | grep -c ${ENTRY_TYPE}) == 1 ] \
                                        && [ $(grep ${ENTRY_NAME} ${NAMED_ROOT}/${TMP_DIRECTORY}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2).${SECONDARY_DC} | grep -c ${ENTRY_TYPE}) == 1 ]
                                    then
                                        ## ok, good everything has it. lets take our checksums
                                        TMP_FILE_CKSUM=$(cksum ${NAMED_ROOT}/${TMP_DIRECTORY}/${ZONEFILE_NAME} | awk '{print $1}');

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "TMP_FILE_CKSUM->${TMP_FILE_CKSUM}");
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Copying file..");

                                        mv ${NAMED_ROOT}/${TMP_DIRECTORY}/${ZONEFILE_NAME} ${SITE_ROOT}/${ZONEFILE_NAME};

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Copy complete. Validating..");

                                        if [ -s ${SITE_ROOT}/${ZONEFILE_NAME} ]
                                        then
                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Copy validated. Checksummimg..");

                                            OP_FILE_CKSUM=$(cksum ${SITE_ROOT}/${ZONEFILE_NAME} | awk '{print $1}');

                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "OP_FILE_CKSUM->${OP_FILE_CKSUM}");

                                            if [ ${TMP_FILE_CKSUM} == ${OP_FILE_CKSUM} ]
                                            then
                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Checksums validated. Continuing with datacenter files..");

                                                ## move completed. continue on with dc files
                                                for DATACENTER in ${PRIMARY_DC} ${SECONDARY_DC}
                                                do
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Now operating on ${DATACENTER}");

                                                    TMP_FILE_CKSUM=$(cksum ${NAMED_ROOT}/${TMP_DIRECTORY}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2).${DATACENTER} | awk '{print $1}');

                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "TMP_FILE_CKSUM->${TMP_FILE_CKSUM}");
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Copying file..");

                                                    mv ${NAMED_ROOT}/${TMP_DIRECTORY}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2).${DATACENTER} ${SITE_ROOT}/${DATACENTER}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2);

                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Copy complete. Validating..");

                                                    if [ -s ${SITE_ROOT}/${DATACENTER}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2) ]
                                                    then
                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Copy validated. Checksummimg..");

                                                        OP_FILE_CKSUM=$(cksum ${SITE_ROOT}/${DATACENTER}/$(echo ${ZONEFILE_NAME} | cut -d "." -f 1-2) | awk '{print $1}');

                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "OP_FILE_CKSUM->${OP_FILE_CKSUM}");

                                                        if [ ${TMP_FILE_CKSUM} != ${OP_FILE_CKSUM} ]
                                                        then
                                                            ## move failed.
                                                            $(${LOGGER} "ERROR" ${METHOD_NAME} ${CNAME} ${LINENO} "${DATACENTER} checksum mismatch.");

                                                            (( ERROR_COUNT += 1 ));
                                                        fi
                                                    else
                                                        ## file empty
                                                        $(${LOGGER} "ERROR" ${METHOD_NAME} ${CNAME} ${LINENO} "${DATACENTER} file empty.");

                                                        (( ERROR_COUNT += 1 ));
                                                    fi
                                                done

                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Datacenter zonefiles copied. Validating..");

                                                if [ ${ERROR_COUNT} == 0 ]
                                                then
                                                    ## everything worked. reload the zone
                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "All zonefiles successfully updated. Reloading zone ${ZONE_NAME}..");

                                                    ## call out and reload
                                                    ## reload on master first, if its good, then continue
                                                    if [ "${SPLIT_HORIZON}" = "${_TRUE}" ]
                                                    then
                                                        for HORIZON in ${HORIZONS}
                                                        do
                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HORIZON -> ${HORIZON}";
                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${NAMED_ROOT}/${LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${NAMED_MASTER} -p ${RNDC_LOCAL_PORT} -y ${RNDC_LOCAL_KEY} -c reload -z "${ZONE_NAME}" -i ${HORIZON} -e";

                                                            RET_CODE=$(${NAMED_ROOT}/${LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${NAMED_MASTER} -p ${RNDC_LOCAL_PORT} -y ${RNDC_LOCAL_KEY} -c reload -z "${ZONE_NAME}" -i ${HORIZON} -e);
                                                        done
                                                    else
                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${NAMED_ROOT}/${LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${NAMED_MASTER} -p ${RNDC_LOCAL_PORT} -y ${RNDC_LOCAL_KEY} -c reload -z "${ZONE_NAME}" -e";

                                                        RET_CODE=$(${NAMED_ROOT}/${LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${NAMED_MASTER} -p ${RNDC_LOCAL_PORT} -y ${RNDC_LOCAL_KEY} -c reload -z "${ZONE_NAME}" -e);
                                                    fi

                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE->${RET_CODE}";

                                                    if [ ${RET_CODE} == 0 ]
                                                    then
                                                        ## xlnt. we've reloaded. continue forward.
                                                        ## the reload does the flush for us, so we
                                                        ## dont have to go back and do it again.
                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${NAMED_MASTER} successfully reloaded change. Validating..";

                                                        ## validate the removal. run a dig for the entry
                                                        LOOKUP_RESPONSE=$(dig @${NAMED_MASTER} +short -t a ${ENTRY_NAME}.${ZONE_NAME});

                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LOOKUP_RESPONSE->${LOOKUP_RESPONSE}";

                                                        if [ ! -z "${LOOKUP_RESPONSE}" ]
                                                        then
                                                            ## xlnt, added. continue with slave zones - just reload here, we dont
                                                            ## really need to validate that it was removed. although i guess we could.

                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reload complete and removal validated. Reloading slaves..";

                                                            for SLAVE in ${DNS_SLAVES[@]}
                                                            do
                                                                if [ "${SPLIT_HORIZON}" = "${_TRUE}" ]
                                                                then
                                                                    for HORIZON in ${HORIZONS}
                                                                    do
                                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HORIZON -> ${HORIZON}";
                                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${NAMED_ROOT}/${LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${SLAVE} -p ${RNDC_REMOTE_PORT} -y ${RNDC_REMOTE_KEY} -c reload -z "${ZONE_NAME}" -i ${HORIZON} -e";

                                                                        RET_CODE=$(${NAMED_ROOT}/${LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${SLAVE} -p ${RNDC_REMOTE_PORT} -y ${RNDC_REMOTE_KEY} -c reload -z "${ZONE_NAME}" -i ${HORIZON} -e);

                                                                        if [ ${RET_CODE} != 0 ]
                                                                        then
                                                                            ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: Failed to initiate service reload on ${SLAVE}. Please update manually.";

                                                                            RETURN_CODE=86;
                                                                        fi
                                                                    done
                                                                else
                                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${NAMED_ROOT}/${LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${SLAVE} -p ${RNDC_REMOTE_PORT} -y ${RNDC_REMOTE_KEY} -c reload -z "${ZONE_NAME}" -e";

                                                                    RET_CODE=$(${NAMED_ROOT}/${LIB_DIRECTORY}/executors/executeRNDCCommands.sh -s ${SLAVE} -p ${RNDC_REMOTE_PORT} -y ${RNDC_REMOTE_KEY} -c reload -z "${ZONE_NAME}" -e);

                                                                    if [ ${RET_CODE} != 0 ]
                                                                    then
                                                                        ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: Failed to initiate service reload on ${SLAVE}. Please update manually.";

                                                                        RETURN_CODE=86;
                                                                    fi
                                                                fi
                                                            done

                                                            ## and this completes.
                                                            if [ -z "${RETURN_CODE}" ]
                                                            then
                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Addition of entry ${ENTRY_NAME} to ${ZONE_NAME} completed.";
                                                                ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone entry added: Zone Name: ${ZONE_NAME}; Entry Name: ${ENTRY_NAME}; Added by: ${IUSER_AUDIT}";

                                                                if [ ! -z "${WARNING_CODE}" ]
                                                                then
                                                                    RETURN_CODE=${WARNING_CODE};
                                                                else
                                                                    RETURN_CODE=0;
                                                                fi
                                                            else
                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Addition of entry ${ENTRY_NAME} to ${ZONE_NAME} completed.";
                                                                ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone entry added: Zone Name: ${ZONE_NAME}; Entry Name: ${ENTRY_NAME}; Added by: ${IUSER_AUDIT}";

                                                                RETURN_CODE=${RETURN_CODE};
                                                            fi
                                                        else
                                                            ## reload failed on the master. error out
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
                                                    ## something broke. error out
                                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while relocating datacenter-specific zones. Cannot continue.";

                                                    RETURN_CODE=28;
                                                fi
                                            else
                                                ## checksum mismatch. error out
                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum mismatch for operational zonefile. Cannot continue.";

                                                RETURN_CODE=90;
                                            fi
                                        else
                                            ## move failed, file doesnt exist or is empty
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to properly relocate zonefile. Cannot continue.";

                                            RETURN_CODE=28;
                                        fi
                                    else
                                        ## add failed to dc zones
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to properly write new data to zonefile. Cannot continue.";

                                        RETURN_CODE=42;
                                    fi
                                else
                                    ## entry wasnt written to primary. error out
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to properly write new data to zonefile. Cannot continue.";

                                    RETURN_CODE=28;
                                fi
                            else
                                ## the entry written variable is empty
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to properly write new data to zonefile. Cannot continue.";

                                RETURN_CODE=6;
                            fi
                        else
                            ## no working copies, cant continue
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create temporary files. Cannot continue.";

                            RETURN_CODE=47;
                        fi
                    else
                        ## no backup, no continue
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create backup of zone files. Cannot continue.";

                        RETURN_CODE=57;
                    fi
                fi
            else
                ## zonefile doesnt exist. error out
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested project code does not exist. Cannot continue.";

                RETURN_CODE=9;
            fi
        else
            ## no zonefile name. damn.
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested zone does not exist. Cannot continue.";

            RETURN_CODE=37;
        fi
    else
        ## no biz unit for site root, fail out
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested business unit does not have a defined group. Cannot continue.";

        RETURN_CODE=10;
    fi

    ## unsets
    unset OP_FILE_CKSUM;
    unset TMP_FILE_CKSUM;
    unset ENTRY_WRITTEN;
    unset ZONEFILE_NAME;
    unset ZONEFILE;
    unset ZONEFILES;
    unset SITE_ROOT;
    unset CHANGE_DATE;
    unset TARFILE_DATE;
    unset BACKUP_FILE;
    unset DATACENTER;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "${METHOD_NAME} -> exit");

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
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Execute zone additions to the DNS infrastructure.";
    print "Usage: ${CNAME} [ -b business unit ] [ -p project code ] [ -z zone name ] [ -i requestor ] [ -c change request ] [ -n filename ] [ -a entry ] [-s] [-e] [-?|-h]";
    print "  -b      The associated business unit";
    print "  -p      The associated project code";
    print "  -z      The zone name, eg example.com";
    print "  -i      The user performing the request";
    print "  -c      The change order associated with this request";
    print "  -n      Add a new zone to the DNS infrastructure. Full path to zone data required.";
    print "  -a      Add a new entry to an existing zone. Comma-delimited information set required.";
    print "  -s      Specifies whether or not to operate against a slave server. Only valid with -n.";
    print "  -e      Execute processing";
    print "  -?|-h   Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

while getopts ":b:p:z:i:c:n:a:seh:" OPTIONS
do
    case "${OPTIONS}" in
        b)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting BUSINESS_UNIT..";

            ## Capture the site root
            typeset -u BUSINESS_UNIT="${OPTARG}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
            ;;
        p)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting PROJECT_CODE..";

            ## Capture the site root
            typeset -u PROJECT_CODE="${OPTARG}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROJECT_CODE -> ${PROJECT_CODE}";
            ;;
        z)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ZONE_NAME..";

            ## Capture the site root
            ZONE_NAME=${OPTARG};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_NAME -> ${ZONE_NAME}";
            ;;
        i)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting IUSER_AUDIT..";

            ## Capture the change control
            IUSER_AUDIT="${OPTARG}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
            ;;
        c)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            ## Capture the change control
            typeset -u CHANGE_NUM="${OPTARG}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        n)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting INSTALL_ZONE to TRUE..";

            ## Capture the change control
            INSTALL_ZONE=${_TRUE};
            typeset -l ZONE_DATA_FILE="${OPTARG}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        a)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ADD_ENTRY to TRUE";

            ## Capture the change control
            ADD_ENTRY=${_TRUE};
            typeset -u ENTRY_TYPE=$(echo "${OPTARG}" | cut -d "," -f 1);

            case ${ENTRY_TYPE} in
                [Aa]|[Nn][Ss]|[Cc][Nn][Aa][Mm][Ee]|[Tt][Xx][Tt])
                    ## these only have a target, no other data associated with them
                    typeset -l ENTRY_NAME=$(echo "${OPTARG}" | cut -d "," -f 2);
                    typeset -l ENTRY_RECORD=$(echo "${OPTARG}" | cut -d "," -f 3);
                    ;;
                [Mm][Xx])
                    ## mx records will have a weight associated
                    typeset -l ENTRY_NAME=$(echo "${OPTARG}" | cut -d "," -f 2);
                    typeset -l ENTRY_PRIORITY=$(echo "${OPTARG}" | cut -d "," -f 2);
                    typeset -l ENTRY_RECORD=$(echo "${OPTARG}" | cut -d "," -f 4);
                    ;;
                [Ss][Rr][Vv])
                    ## set up our record information
                    ## service records are special because theres ALOT of info
                    ## in them
                    ## service records are constructed as follows:
                    ##_service._protocol.name TTL Class SRV Priority Weight Port Target
                    ## sample (email record for smtp):
                    ## _submission._tcp.email.caspersbox.com 86400 IN SRV 10 10 25 caspersb-r1b13.caspersbox.com
                    ## see http://en.wikipedia.org/wiki/SRV_record for more info
                    ## set up our record information
                    ENTRY_TYPE=$(echo ${IP_ADDR} | cut -d "," -f 1);
                    ENTRY_PROTOCOL=$(echo ${IP_ADDR} | cut -d "," -f 2);
                    ENTRY_NAME=$(echo ${IP_ADDR} | cut -d "," -f 3);
                    ENTRY_TTL=$(echo ${IP_ADDR} | cut -d "," -f 4);
                    ENTRY_PRIORITY=$(echo ${IP_ADDR} | cut -d "," -f 5);
                    ENTRY_WEIGHT=$(echo ${IP_ADDR} | cut -d "," -f 6);
                    ENTRY_PORT=$(echo ${IP_ADDR} | cut -d "," -f 7);
                    ENTRY_TARGET=$(echo ${IP_ADDR} | cut -d "," -f 8);
                    ;;
                *)
                    ## as-yet unsupported record type - this list should follow the list
                    ## of data helpers. if theres no record helper for it then this executor
                    ## should be able to add it.
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid record type was provided. Cannot continue.";

                    RETURN_CODE=49;
                    ;;
            esac

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_ENTRY -> ${ADD_ENTRY}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTRY_NAME -> ${ENTRY_NAME}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTRY_TYPE -> ${ENTRY_TYPE}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTRY_TARGET -> ${ENTRY_TARGET}";
            ;;
        e)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${RETURN_CODE}" ]
            then
                if [ -z "${BUSINESS_UNIT}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=15;
                elif [ -z "${PROJECT_CODE}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=24;
                elif [ -z "${ZONE_NAME}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=24;
                elif [ -z "${CHANGE_NUM}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change order was provided. Unable to continue processing.";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=17;
                elif [ -z "${IUSER_AUDIT}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requestors username was not provided. Unable to continue processing.";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=20;
                else
                    ## We have enough information to process the request, continue
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    if [ ! -z "${ADD_ENTRY}" ] && [ "${ADD_ENTRY}" = "${_TRUE}" ]
                    then
                        add_zone_entry;
                    elif [ ! -z "${INSTALL_ZONE}" ] && [ "${INSTALL_ZONE}" = "${_TRUE}" ]
                    then
                        if [ -z "${SLAVE_OPERATION}" ] && [ "${SLAVE_OPERATION}" = "${_TRUE}" ]
                        then
                            install_slave_zone;
                        else
                            install_master_zone;
                        fi
                    else
                        ## no valid command type
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No valid command type was provided. Cannot continue.";

                        RETURN_CODE=3;
                    fi
                fi
            fi
            ;;
        *)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done

shift ${OPTIND}-1;

echo ${RETURN_CODE};
exit ${RETURN_CODE};

