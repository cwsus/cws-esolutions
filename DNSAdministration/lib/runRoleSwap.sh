#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  run_role_swap.sh
#         USAGE:  ./run_role_swap.sh
#   DESCRIPTION:  Executes the necessary classes against the configured DNS
#                 master to fail over a single site.
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
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";
typeset -i OPTIND=0;
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
[ ! -z "${ENABLE_SECURITY}" ] && [ "${ENABLE_SECURITY}" = "${_TRUE}" ] && ${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

CNAME="${THIS_CNAME}";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ ! -z "${ENABLE_SECURITY}" ] && [ "${ENABLE_SECURITY}" = "${_TRUE}" ] && [ ${RET_CODE} -ne 0 ]
then
    print "Security configuration does not allow the requested action.";

    return ${RET_CODE};
fi

unset RET_CODE;

#===  FUNCTION  ===============================================================
#          NAME:  failover_site
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function run_role_swap
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SLAVE_TARGET -> ${SLAVE_TARGET}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MASTER_TARGET -> ${MASTER_TARGET}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";

    TARFILE_NAME=$(echo ${BACKUP_FILE_NAME} | sed -e "s/%TYPE%/${ZONE_BACKUP_PREFIX}/" -e "s/%SERVER_NAME%/$(uname -n)/" -e "s/%DATE%/$(date +"%m-%d-%Y")/");

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARFILE_NAME -> ${TARFILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing zone backup on ${NAMED_MASTER}..";

    ## first things first. we need to get a tarfile from the existing master and pull it down.
    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
    then
        . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/sys/systemBackup.sh zone master;
    else
        ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/sys/systemBackup.sh zone master" ${SSH_USER_NAME} ${SSH_USER_AUTH};
    fi

    ## capture the return code
    typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. RET_CODE -> ${RET_CODE}";

    if [[ ! -z "${RET_CODE}" && ${RET_CODE} -eq 0 ]]
    then
        unset RET_CODE;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup successfully created. Obtaining files..";

        ## got our file. bring it over.
        if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
        then
            cp ${NAMED_ROOT}/${TMP_DIRECTORY}/${TARFILE_NAME} ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${TARFILE_NAME};
        else
            ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSCPConnection.exp remote-copy ${NAMED_MASTER} ${NAMED_ROOT}/${TMP_DIRECTORY}/${TARFILE_NAME} ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${TARFILE_NAME} ${SSH_USER_NAME} ${SSH_USER_AUTH};
        fi

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File transfer complete. Verifying..";

        if [ -s ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${TARFILE_NAME} ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File transfer successfully verified. Continuing..";

            ## got our file. now we can switch the existing slave to a master, and if thats successful, the existing master to a slave.
            ## we know what the existing master is, we know where to switch it to. we need to make sure that the target is authorized
            ## to be a master, and that it isnt already the master
            if [ "${MASTER_TARGET}" != ${NAMED_MASTER} ] && [ $(echo ${AVAILABLE_MASTER_SERVERS} | grep -c ${MASTER_TARGET}) -ne 0 ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Our target nameserver has been validated. Processing role swap from slave to master against ${MASTER_TARGET}..";

                ## our target master is not the same as the one we're on today and its authorized. move forward.
                ## check if we're local - if so, we just execute the commands. if not, copy first, the exec
                if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "We are in local execution mode. Working..";

                    ## run the switch
                    ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/executors/execute_role_swap.sh -p ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${TARFILE_NAME} -t ${MASTER_TARGET} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e;
                else
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Transferring ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${TARFILE_NAME} to ${REMOTE_APP_ROOT}/${TMP_DIRECTORY}/${TARFILE_NAME}";

                    ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSCPConnection.exp local-copy ${MASTER_TARGET} ${NAMED_ROOT}/${TMP_DIRECTORY}/${TARFILE_NAME} ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${TARFILE_NAME} ${SSH_USER_NAME} ${SSH_USER_AUTH};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Transfer complete. Executing execute_role_swap.sh -p ${REMOTE_APP_ROOT}/${TMP_DIRECTORY}/${TARFILE_NAME} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e ..";

                    ## once the copy is complete we run the switch.
                    ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${MASTER_TARGET} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_role_swap.sh -p ${REMOTE_APP_ROOT}/${TMP_DIRECTORY}/${TARFILE_NAME} -t ${MASTER_TARGET} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e";
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. RET_CODE -> ${RET_CODE}";

                    if [[ ! -z "${RET_CODE}" && "${RET_CODE}" -eq 0 ]]
                    then
                        unset RET_CODE;

                        ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Role swap from slave to master completed for ${MASTER_TARGET} by ${IUSER_AUDIT}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Role swap from slave to master complete for ${MASTER_TARGET}.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switching ${NAMED_MASTER} to slave..";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command execute_role_swap.sh -s -t ${MASTER_TARGET} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e ..";

                        ## good, we're done with that. now we move forward and re-config the existing master as a slave
                        ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/execute_role_swap.sh -s -t ${MASTER_TARGET} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e" ${SSH_USER_NAME} ${SSH_USER_AUTH};
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. RET_CODE -> ${RET_CODE}";

                        if [[ ! -z "${RET_CODE}" && "${RET_CODE}" -eq 0 ]]
                        then
                            ## good. our existing master is now a slave.
                            ## now we re-configure ourself
                            ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Role swap from master to slave complete for ${NAMED_MASTER}, performed by ${IUSER_AUDIT}.";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Sending notification...";

                            . ${MAILER_CLASS} -m notifyRoleSwap -p ${PROJECT_CODE} -a "${DNS_SERVER_ADMIN_EMAIL}" -e;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            SUCCESSFUL_ROLE_SWAP=${_TRUE};
                            RETURN_CODE=0;
                        else
                            ## our existing master did not properly flip. this is not good, so send out a warning
                            ## send the warning but switch our local config anyway.
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Role swap from master to slave FAILED for ${NAMED_MASTER}. Please try again.";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            RETURN_CODE=${RET_CODE};
                        fi
                    else
                        ## failed to switch the slave to a master. sound out the alarm
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to reconfigure ${MASTER_TARGET} as a master nameserver. Please process manually.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        RETURN_CODE=${RET_CODE};
                    fi
                fi
            else
                ## our target is either not authorized or is already the master nameserver. cant continue
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${MASTER_TARGET} is not an authorized system for master nameservices. Please try again.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=${RET_CODE};
            fi
        else
            ## no file, cant keep going. "ERROR"
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The master tar file failed to copy. Please try again.";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=${RET_CODE};
        fi
    else
        ## unable to generate a master tar. cant continue
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate a master tarfile from ${NAMED_MASTER}. Unable to continue.";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=${RET_CODE};
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -? ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  failover_bu
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function switch_local_config
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    if [[ ! -z "${SUCCESSFUL_ROLE_SWAP}" && "${SUCCESSFUL_ROLE_SWAP}" = "${_TRUE}" ]]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MASTER_TARGET -> ${MASTER_TARGET}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating backup and operational copies..";

        ## take a backup and make a working copy
        TMP_NAMED_CONFIG=${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/$(grep named_config_file ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2- | sed -e 's| ||g' | cut -d "/" -f 2);
        BKUP_NAMED_CONFIG=${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/$(grep named_config_file ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2- | sed -e 's| ||g' | cut -d "/" -f 2).${CHANGE_NUM};

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
                sed -e "s/master_dns = ${NAMED_MASTER}/master_dns = ${MASTER_TARGET}/" ${TMP_NAMED_CONFIG} >> ${TMP_NAMED_CONFIG}.tmp;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch complete. Verifying..";

                ## make sure it was changed..
                if [ $(grep -c "master_dns = ${NAMED_MASTER}" ${TMP_NAMED_CONFIG}.tmp) -eq 0 ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Switch verified. Placing file..";

                    ## it was. make this the active copy
                    mv ${TMP_NAMED_CONFIG}.tmp ${TMP_NAMED_CONFIG};

                    ## take some checksums..
                    TMP_CONF_CKSUM=$(cksum ${TMP_NAMED_CONFIG} | awk '{print $1}');

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TMP_CONF_CKSUM -> ${TMP_CONF_CKSUM}";

                    ## move the file into place..
                    mv ${TMP_NAMED_CONFIG} ${INTERNET_DNS_CONFIG};

                    ## make sure it was moved..
                    if [ -s ${TMP_NAMED_CONFIG} ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File moved. Verifying..";

                        ## and cksum..
                        OP_CONF_CKSUM=$(cksum ${INTERNET_DNS_CONFIG} | awk '{print $1}');

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_CONF_CKSUM -> ${OP_CONF_CKSUM}";

                        ## and make sure they agree
                        if [ ${TMP_CONF_CKSUM} -eq ${OP_CONF_CKSUM} ]
                        then
                            ## they do. respond with success
                            ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local config switch -> master_nameserver modification - performed by ${IUSER_AUDIT}.";

                            RETURN_CODE=0;
                        else
                            ## cksum mismatch. file failed to copy
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum validation failed. New configuration has not been applied.";

                            RETURN_CODE=90;
                        fi
                    else
                        ## failed to move the tmp file into place
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum validation failed. New configuration has not been applied.";

                        RETURN_CODE=44;
                    fi
                else
                    ## failed to update the file with the proper information
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to apply new configuration information. Please try again.";

                    RETURN_CODE=89;
                fi
            else
                ## failed to create a working copy of the config file
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create a working copy of the existing configuration. Cannot continue.";

                RETURN_CODE=47;
            fi
        else
            ## failed to create a backup of the config file
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to back up the existing configuration. Cannot continue.";

            RETURN_CODE=57;
        fi
    else
        ## this can only be done after a confirmed successful role swap. any other time is invalid.
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A successful role-swap has not yet been performed. Unable to modify local configuration.";

        RETURN_CODE=93;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Execute a DNS failover request on the master nameserver based on the provided information.";
    print "Usage: ${CNAME} [ -s switch from ] [ -t switch to ] [ -c change order ] [ -e ] [-?|-h show this help]";
    print "  -s      Switch services on this server to that provided with the -t option";
    print "  -t      Switch services to this server from that provided with the -s option.";
    print "  -l      Switch local configuration. This option is only valid after a completed role-swap.";
    print "  -c      Change order number";
    print "  -e      Execute processing";
    print "  -h|-?   Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 3;
}

[ ${#} -eq 0 ] && usage;

while getopts ":s:t:lc:eh" OPTIONS
do
    case "${OPTIONS}" in
        s)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting target for switch from master to slave..";

            ## comma-delimited information set, lets strip the "INFO"
            typeset -u SLAVE_TARGET="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SLAVE_TARGET -> ${SLAVE_TARGET}";
            ;;
        t)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting target for switch slave to master..";

            ## comma-delimited information set, lets strip the "INFO"
            typeset -u MASTER_TARGET="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MASTER_TARGET -> ${MASTER_TARGET}";
            ;;
        l)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting LOCAL_CONFIG to true..";

            ## Capture the change control
            LOCAL_CONFIG=${_TRUE};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LOCAL_CONFIG -> ${LOCAL_CONFIG}";
            ;;
        c)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            ## Capture the change control
            typeset -u CHANGE_NUM="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${SLAVE_TARGET}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No slave system was selected for processing. Cannot continue.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=22;
            elif [ -z "${MASTER_TARGET}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No master system was selected for processing. Cannot continue.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=22;
            elif [ -z "${IUSER_AUDIT}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requestors username was not provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=20;
            elif [ -z "${CHANGE_NUM}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change control number was provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=19;
            else
                ## We have enough information to process the request, continue
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                [ ! -z "${LOCAL_CONFIG}" ] && [ "${LOCAL_CONFIG}" = "${_TRUE}" ] && switch_local_config || run_role_swap;
            fi
            ;;
        h|[\?])
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done

shift ${OPTIND}-1;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset OPTIND;
unset THIS_CNAME;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return ${RETURN_CODE};
