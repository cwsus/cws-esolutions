#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  runAddZone.sh
#         USAGE:  ./runAddZone.sh
#   DESCRIPTION:  Executes the addition of a new or updated zone to the DNS
#                 master.
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

[[ ! -z "${TRACE}" && "${TRACE}" = "TRUE" ]] && set -x;

## Application constants
[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ -z "${PLUGIN_ROOT_DIR}" && -f ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && echo "Failed to locate configuration data. Cannot continue." && exit 1;

[[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

unset METHOD_NAME;
unset CNAME;

## check security
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Security configuration does not allow the requested action." && exit ${RET_CODE};

## unset the return code
unset RET_CODE;

CNAME="$(basename "${0}")";
METHOD_NAME="${CNAME}#startup";

#===  FUNCTION  ===============================================================
#          NAME:  buildWorkingZone
#   DESCRIPTION:  Creates the zone file that the DNS servers will utilize during
#                 queries. Takes the previously configured file from the primary
#                 datacenter folder and adds required indicator flags, and then
#                 places the resultant file in the group root.
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function buildWorkingZone
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    if [ ! -z "${IS_DNS_RECORD_ADD_ENABLED}" ] && [ "${IS_DNS_RECORD_ADD_ENABLED}" = "${_TRUE}" ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting change array..";

        ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1).${PROJECT_CODE};
        DC_ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1);

        ## set up the change array
        set -A CHG_ARRAY;
        set -A CHG_ARRAY ${NAMED_SERIAL_START} ${PRIMARY_DC} $(date +"%m-%d-%Y") ${IUSER_AUDIT} ${CHANGE_NUM} $(date +"%Y%m%d")00;

        while [ ${A} -ne ${#CHG_ARRAY[@]} ]
        do
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_ARRAY -> ${CHG_ARRAY[${A}]}";

            (( A += 1 ));
        done

        ## reset the counter
        A=0;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating operational copy..";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME} ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC_ZONEFILE_NAME}.${PROJECT_CODE}";

        ## create the operational copy - this will be the copy named actually uses
        cp ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME} ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC_ZONEFILE_NAME}.${PROJECT_CODE};

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding indicators..";

        ## process the request, iterating through the fields that require
        ## modification for audit/track/trace etc
        for INDICATOR in LAST_SERIAL DATACENTER DATE USER_NAME REQUEST_NUMBER SERIAL_NUM
        do
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${INDICATOR} -> ${CHG_ARRAY[${A}]}";

            sed -e "s/%${INDICATOR}%/${CHG_ARRAY[${A}]}/" ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC_ZONEFILE_NAME}.${PROJECT_CODE} \
                > ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC_ZONEFILE_NAME}.${PROJECT_CODE}.tmp;
            mv ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC_ZONEFILE_NAME}.${PROJECT_CODE}.tmp \
                ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC_ZONEFILE_NAME}.${PROJECT_CODE};

            (( A += 1 ));
        done

        ## reset our variables
        A=0;
        unset INDICATOR;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating file ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC_ZONEFILE_NAME}.${PROJECT_CODE}..";

        if [ -s ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC_ZONEFILE_NAME}.${PROJECT_CODE} ]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
            RETURN_CODE=0;
        else
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File verification FAILED. Please try again.";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
            RETURN_CODE=44;
        fi
    else
        $(${LOGGER} "ERROR" ${METHOD_NAME} ${CNAME} ${LINENO} "DNS record addition has not been enabled. Cannot continue.");

        RETURN_CODE=97;
    fi

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  copyZoneToMaster
#   DESCRIPTION:  Creates a tarball of the provided group directory containing
#                 all subdirectories and files. This tarball is then scp'd to
#                 the master nameserver, where it is untarred and copied into
#                 the zone root for function usage. Optionally sends a flag to restart
#                 named services on completion.
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function copyZoneToMaster
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    if [ ! -z "${IS_DNS_RECORD_ADD_ENABLED}" ] && [ "${IS_DNS_RECORD_ADD_ENABLED}" = "${_TRUE}" ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating tar file..";

        TARFILE_DATE=$(date +"%m-%d-%Y");

        ## make sure our target zone structure exists
        if [ -d ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT} ]
        then
            tar cf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar \
                -C ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY} ${GROUP_ID}${BUSINESS_UNIT} >> /dev/null 2>&1;
            gzip ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar >> /dev/null 2>&1;

            ## validate that the tar was indeed created
            if [ -s ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz ]
            then
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${TARFILE_DATE}.${ZONE_NAME}.tar.gz created";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "tarfile created. Sending to DNS master ${NAMED_MASTER}..";

                if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
                then
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution set to TRUE. Executing command executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e..";

                    unset METHOD_NAME;
                    unset CNAME;

                    . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/executors/executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e;

                    CNAME="$(basename "${0}")";
                    local METHOD_NAME="${CNAME}#${0}";

                else
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSCPConnection.exp local-copy ${NAMED_MASTER} ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz ${REMOTE_APP_ROOT}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz";

                    unset METHOD_NAME;
                    unset CNAME;

                    ## copy the files up
                    ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSCPConnection.exp local-copy ${NAMED_MASTER} ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz ${REMOTE_APP_ROOT}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz;

                    CNAME="$(basename "${0}")";
                    local METHOD_NAME="${CNAME}#${0}";

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Secure copy complete. Unpacking..";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${NAMED_MASTER} \"executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -d ${TARFILE_DATE} -e..\"";

                    ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -d ${TARFILE_DATE} -e";
                fi

                ## capture the return code
                RETURN_CODE=${?};

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";

                if [ ${RETURN_CODE} -eq 0 ]
                then
                    ## unset return code
                    unset RETURN_CODE;

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unpack and install complete.";
                    ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_NAME} installed by ${IUSER_AUDIT} per change ${CHANGE_NUM} on $(date +"%m-%d-%Y") at $(date +"%H:%M:%S")";

                    ## call out to run_rndc_request to re-configure the installation with the new data
                    unset METHOD_NAME;
                    unset CNAME;

                    . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runRNDCCommands.sh -s ${NAMED_MASTER} -c reconfig -e
                    RETURN_CODE=${?}

                    CNAME="$(basename "${0}")";
                    local METHOD_NAME="${CNAME}#${0}";

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";

                    if [ ${RETURN_CODE} -eq 0 ]
                    then
                        unset RETURN_CODE;
                        ## rndc reconfig complete. validate
                        unset METHOD_NAME;
                        unset CNAME;

                        . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validate_change_request.sh add ${NAMED_MASTER} A ${ZONE_NAME};
                        RETURN_CODE=${?};

                        CNAME="$(basename "${0}")";
                        local METHOD_NAME="${CNAME}#${0}";

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";

                        if [ ${RETURN_CODE} -eq 0 ]
                        then
                            unset RETURN_CODE;
                            ## new zone was successfully installed and is active on named master.
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully reconfigured ${NAMED_MASTER}. Zone has taken effect.";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Sending notification email..";

                            ## send the notification email
                            unset METHOD_NAME;
                            unset CNAME;

                            . ${MAILER_CLASS} -m notifyZoneChange -p ${PROJECT_CODE} -a "${DNS_SERVER_ADMIN_EMAIL}" -e;
                            RETURN_CODE=${?}

                            CNAME="$(basename "${0}")";
                            local METHOD_NAME="${CNAME}#${0}";

                            if [ ${RETURN_CODE} -eq 0 ]
                            then
                                unset RETURN_CODE;
                                ## all processing complete. notifications have been sent.
                                RETURN_CODE=0;
                            else
                                unset RETURN_CODE;
                                ## notifier threw an error. we re-throw as a warning.
                                RETURN_CODE=66;
                            fi
                        else
                            ## failed to validate that new zone is active. throw an error
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to validate that new zone ${ZONE_NAME} has been successfully activated on ${NAMED_MASTER}";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            RETURN_CODE=61;
                        fi
                    else
                        ## reconfiguration failed. throw out an error.
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server reconfiguration on ${NAMED_MASTER} FAILED.";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        RETURN_CODE=52;
                    fi
                else
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred during installation of the new zone. Return code from execute_add_record -> ${RETURN_CODE}";

                    ## we know we drop back into the confirmation step, right before
                    ## this method is initially executed. so lets take a minute to
                    ## clean up the tarballs we've created so it doesn't cause a problem
                    ## next time.
                    rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz;
                    rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar;

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=59;
                fi
            else
                ## tarfile never got made
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Archive creation failed. Cannot continue.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=60;
            fi
        else
            ## zone directories don't yet exist. throw out an error
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested zone directories do not yet exists. Cannot continue.";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=39;
        fi

        ## unset variables
        unset TARFILE_DATE;
        unset PERFORM_COPY;
    else
        $(${LOGGER} "ERROR" ${METHOD_NAME} ${CNAME} ${LINENO} "DNS record addition has not been enabled. Cannot continue.");

        RETURN_CODE=97;
    fi

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  copyZoneToMaster
#   DESCRIPTION:  Creates a tarball of the provided group directory containing
#                 all subdirectories and files. This tarball is then scp'd to
#                 the master nameserver, where it is untarred and copied into
#                 the zone root for function usage. Optionally sends a flag to restart
#                 named services on completion.
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function copyZoneToSlave
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    if [ ! -z "${IS_DNS_RECORD_ADD_ENABLED}" ] && [ "${IS_DNS_RECORD_ADD_ENABLED}" = "${_TRUE}" ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating tar file..";

        TARFILE_DATE=$(date +"%m-%d-%Y");

        ## we should already have a tarfile for this.
        ## check and make sure

        if [ -s ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz ]
        then
            ## the tarfile we need does indeed exist. continue processing
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${TARFILE_DATE}.${ZONE_NAME}.tar.gz exists, continuing..";

            ## having a slave server on the same server as your master probably isnt the best idea. since
            ## its physically possible, we leave the code here.. but realistically it should never ever get executed.
            if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
            then
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution set to TRUE. Executing command executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e..";

                unset METHOD_NAME;
                unset CNAME;

                . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/executors/executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -s -e;

                CNAME="$(basename "${0}")";
                local METHOD_NAME="${CNAME}#${0}";
            else
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSCPConnection.exp local-copy ${SLAVE_SERVER} ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${TARFILE_DATE}.${ZONE_NAME}.tar.gz ${REMOTE_APP_ROOT}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz";

                ## copy the files up
                unset METHOD_NAME;
                unset CNAME;

                ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSCPConnection.exp local-copy ${SLAVE_SERVER} ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz ${REMOTE_APP_ROOT}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz;

                CNAME="$(basename "${0}")";
                local METHOD_NAME="${CNAME}#${0}";

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Secure copy complete. Unpacking..";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${SLAVE_SERVER} \"executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -d ${TARFILE_DATE} -s -e..\"";

                ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${SLAVE_SERVER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -d ${TARFILE_DATE} -s -e";
            fi

            ## capture the return code
            RETURN_CODE=${?};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";

            if [ ${RETURN_CODE} -eq 0 ]
            then
                ## unset return code
                unset RETURN_CODE;

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unpack and install complete.";
                ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_NAME} installed by ${IUSER_AUDIT} per change ${CHANGE_NUM} on $(date +"%m-%d-%Y") at $(date +"%H:%M:%S")";

                ## reload the installation with the new data
                unset METHOD_NAME;
                unset CNAME;

                . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runRNDCCommands.sh -s ${SLAVE_SERVER} -p ${RNDC_LOCAL_PORT} -y ${RNDC_LOCAL_KEY} -c reconfig -e
                RETURN_CODE=${?}

                CNAME="$(basename "${0}")";
                local METHOD_NAME="${CNAME}#${0}";

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";

                if [ ${RETURN_CODE} -eq 0 ]
                then
                    ## rndc reconfig complete. validate
                    ## so now we have the new zone installed. run an rndc reconfig to make it take effect.
                    ## sleep for the configured thread delay before the call out to run_rndc_request
                    ## this will provide some time for the reload/refresh to complete
                    sleep ${THREAD_DELAY};

                    unset METHOD_NAME;
                    unset CNAME;

                    . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validate_change_request.sh add ${SLAVE_SERVER} A ${ZONE_NAME};
                    RETURN_CODE=${?};

                    CNAME="$(basename "${0}")";
                    local METHOD_NAME="${CNAME}#${0}";

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";

                    if [ ${RETURN_CODE} -eq 0 ]
                    then
                        ## new zone was successfully installed and is active on named slave.
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully reconfigured ${SLAVE_SERVER}. Zone has taken effect.";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        RETURN_CODE=0;
                    else
                        ## failed to validate that new zone is active. throw an error
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to validate that new zone ${ZONE_NAME} has been successfully activated on ${SLAVE_SERVER}";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        RETURN_CODE=61;
                    fi
                else
                    ## reconfiguration failed. throw out an error.
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server reconfiguration on ${SLAVE_SERVER} FAILED.";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=52;
                fi
            else
                ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred during installation of the new zone to ${SLAVE_SERVER}. Return code from execute_add_record -> ${RETURN_CODE}";

                RETURN_CODE=59;
            fi
        else
            ## we dont have a tarfile to operate with. either the zone didnt get created yet,
            ## or the tarfile we're looking for never got created or is of a different name.
            ## either way, return an error back to the requestor
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to locate tarball containing zone files. Please ensure that the requested zone has been created and copied to the configured master.";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=60;
        fi

        ## unset variables
        ## unset variables
        unset TARFILE_DATE;
        unset SLAVE_COPY;
        unset SLAVE_SERVER;
    else
        $(${LOGGER} "ERROR" ${METHOD_NAME} ${CNAME} ${LINENO} "DNS record addition has not been enabled. Cannot continue.");

        RETURN_CODE=97;
    fi

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function addZoneEntry
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    if [ ! -z "${IS_DNS_RECORD_ADD_ENABLED}" ] && [ "${IS_DNS_RECORD_ADD_ENABLED}" = "${_TRUE}" ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating record data..";

        case ${ENTRY_TYPE} in
            [Aa]|[Nn][Ss]|[Cc][Nn][Aa][Mm][Ee]|[Tt][Xx][Tt])
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Validating entry ${ENTRY_NAME} of type ${ENTRY_TYPE}");

                if [ -z "${ENTRY_NAME}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No entry name was provided. Cannot continue.";

                    RETURN_CODE=30;
                elif [ -z "${ENTRY_RECORD}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No entry target was provided. Cannot continue.";

                    RETURN_CODE=30;
                else
                    ## we have our data, continue
                    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
                    then
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution set to TRUE. Executing command executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_NAME},${ENTRY_RECORD} -e..";

                        unset METHOD_NAME;
                        unset CNAME;

                        . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_NAME},${ENTRY_RECORD} -e;
                    else
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_NAME},${ENTRY_RECORD} -e"";

                        ## copy the files up
                        unset METHOD_NAME;
                        unset CNAME;

                        ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_NAME},${ENTRY_RECORD} -e";
                    fi
                fi
                ;;
            [Mm][Xx])
                if [ -z "${ENTRY_NAME}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No entry name was provided. Cannot continue.";

                    RETURN_CODE=30;
                elif [ -z "${ENTRY_PRIORITY}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No entry priority was provided. Cannot continue.";

                    RETURN_CODE=30;
                elif [ -z "${ENTRY_RECORD}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No entry target was provided. Cannot continue.";

                    RETURN_CODE=30;
                else
                    ## we have our data, continue
                    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
                    then
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution set to TRUE. Executing command executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_NAME},${ENTRY_RECORD} -e..";

                        unset METHOD_NAME;
                        unset CNAME;

                        . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_NAME},${ENTRY_PRIORITY},${ENTRY_RECORD} -e;
                    else
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_PRIORITY},${ENTRY_NAME},${ENTRY_RECORD} -e"";

                        ## copy the files up
                        unset METHOD_NAME;
                        unset CNAME;

                        ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_NAME},${ENTRY_PRIORITY},${ENTRY_RECORD} -e";
                    fi
                fi
                ;;
            [Ss][Rr][Vv])
                if [ -z "${ENTRY_TYPE}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service type was provided. Cannot continue.";

                    RETURN_CODE=30;
                elif [ -z "${ENTRY_PROTOCOL}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service protocol was provided. Cannot continue.";

                    RETURN_CODE=30;
                elif [ -z "${ENTRY_NAME}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service name was provided. Cannot continue.";

                    RETURN_CODE=30;
                elif [ -z "${ENTRY_TTL}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service TTL was provided. Cannot continue.";

                    RETURN_CODE=30;
                elif [ -z "${ENTRY_PRIORITY}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service priority was provided. Cannot continue.";

                    RETURN_CODE=30;
                elif [ -z "${ENTRY_WEIGHT}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service weight was provided. Cannot continue.";

                    RETURN_CODE=30;
                elif [ -z "${ENTRY_PORT}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service port was provided. Cannot continue.";

                    RETURN_CODE=30;
                elif [ -z "${ENTRY_RECORD}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service target was provided. Cannot continue.";

                    RETURN_CODE=30;
                else
                    ## we have our data, continue
                    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
                    then
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution set to TRUE. Executing command executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_PROTOCOL},${ENTRY_NAME},${ENTRY_TTL},${ENTRY_PRIORITY},${ENTRY_WEIGHT},${ENTRY_PORT},${ENTRY_RECORD} -e..";

                        unset METHOD_NAME;
                        unset CNAME;

                        . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_NAME},${ENTRY_PRIORITY},${ENTRY_RECORD} -e;
                    else
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_PROTOCOL},${ENTRY_NAME},${ENTRY_TTL},${ENTRY_PRIORITY},${ENTRY_WEIGHT},${ENTRY_PORT},${ENTRY_RECORD} -e"";

                        ## copy the files up
                        unset METHOD_NAME;
                        unset CNAME;

                        ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_PROTOCOL},${ENTRY_NAME},${ENTRY_TTL},${ENTRY_PRIORITY},${ENTRY_WEIGHT},${ENTRY_PORT},${ENTRY_RECORD} -e";
                    fi
                fi
                ;;
            *)
                $(${LOGGER} "ERROR" ${METHOD_NAME} ${CNAME} ${LINENO} "The record type, ${ENTRY_TYPE}, is not yet supported.");

                RETURN_CODE=49;
                ;;
        esac

        ## capture the return code
        RET_CODE=${?};

        CNAME="$(basename "${0}")";
        local METHOD_NAME="${CNAME}#${0}";

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

        if [ -z "${RETURN_CODE}" ]
        then
            if [ ${RET_CODE} == 0 ] || [ ${RET_CODE} == 86 ]
            then
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully reconfigured ${NAMED_MASTER}. Zone has taken effect.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Sending notification email..";

                ## send the notification email
                unset METHOD_NAME;
                unset CNAME;

                . ${MAILER_CLASS} -m notifyZoneChange -p ${PROJECT_CODE} -a "${DNS_SERVER_ADMIN_EMAIL}" -e;
                RETURN_CODE=${?}

                CNAME="$(basename "${0}")";
                local METHOD_NAME="${CNAME}#${0}";

                if [ ${RETURN_CODE} -eq 0 ]
                then
                    unset RETURN_CODE;

                    ## all processing complete. notifications have been sent.
                    if [ ${RET_CODE} == 86 ]
                    then
                        RETURN_CODE=${RET_CODE};
                    else
                        RETURN_CODE=0;
                    fi
                else
                    unset RETURN_CODE;
                    ## notifier threw an error. we re-throw as a warning.
                    RETURN_CODE=66;
                fi
            else
                ## process completed but we got an error back from rndc.
                $(${LOGGER} "ERROR" ${METHOD_NAME} ${CNAME} ${LINENO} "An error occurred while adding the new record. Please try again.");

                RETURN_CODE=${RET_CODE};
            fi
        fi
    else
        $(${LOGGER} "ERROR" ${METHOD_NAME} ${CNAME} ${LINENO} "DNS record addition has not been enabled. Cannot continue.");

        RETURN_CODE=97;
    fi

    unset RET_CODE;
    unset RETURN_CODE;
    unset ENTRY_NAME;
    unset ENTRY_RECORD;
    unset ENTRY_NAME;
    unset ENTRY_PRIORITY;
    unset ENTRY_RECORD;
    unset ENTRY_TYPE;
    unset ENTRY_PROTOCOL;
    unset ENTRY_NAME;
    unset ENTRY_TTL;
    unset ENTRY_PRIORITY;
    unset ENTRY_WEIGHT;
    unset ENTRY_PORT;
    unset ENTRY_RECORD;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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
    print "Usage: ${CNAME} [ -b business unit ] [ -p project code ] [ -z zone name ] [ -c change request ] [ -n filename ] [ -a entry ] [-s] [-e] [-?|-h]";
    print "  -b      The associated business unit";
    print "  -p      The associated project code";
    print "  -z      The zone name, eg example.com";
    print "  -c      The change order associated with this request";
    print "  -n      Add a new zone to the DNS infrastructure. Full path to zone data required.";
    print "  -a      Add a new entry to an existing zone. Comma-delimited information set required.";
    print "  -s      Specifies whether or not to operate against a slave server. Only valid with -n.";
    print "  -e      Execute processing";
    print "  -?|-h   Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[ ${#} -eq 0 ] && usage;

while getopts ":b:p:z:c:n:a:xseh:" OPTIONS
do
    METHOD_NAME="${CNAME}#startup";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

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
                    typeset -l ENTRY_NAME="${OPTARG}";
                    typeset -l ENTRY_PRIORITY="${OPTARG}";
                    typeset -l ENTRY_RECORD="${OPTARG}";
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
                    ENTRY_RECORD=$(echo ${IP_ADDR} | cut -d "," -f 8);
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
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTRY_RECORD -> ${ENTRY_RECORD}";
            ;;
        x)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting PERFORM_COPY to true..";

            ## Capture the change control
            PERFORM_COPY=${_TRUE};
            ;;
        s)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SLAVE_COPY to true..";

            ## Capture the change control
            SLAVE_COPY=${_TRUE};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SLAVE_SERVER..";

            typeset -l SLAVE_SERVER="${OPTARG}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SLAVE_SERVER ->${SLAVE_SERVER}";
            ;;
        e)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [[ -z "${RETURN_CODE}" || ${RETURN_CODE} != 0 ]]
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

                    [ ! -z "${ADD_ENTRY}" ] && [ "${ADD_ENTRY}" = "${_TRUE}" ] && addZoneEntry;

                    if [ ! -z "${INSTALL_ZONE}" ] && [ "${INSTALL_ZONE}" = "${_TRUE}" ]
                    then
                        [ -z "${SLAVE_OPERATION}" ] && [ "${SLAVE_OPERATION}" = "${_TRUE}" ] && copyZoneToSlave || copyZoneToMaster;
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
return ${RETURN_CODE};
