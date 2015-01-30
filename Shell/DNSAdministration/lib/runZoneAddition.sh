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

trap 'set +v; set +x' INT TERM EXIT;

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

## Application constants
typeset CNAME="$(/usr/bin/env basename "${0}")";
typeset SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}/${0##*/}")";
typeset SCRIPT_ROOT="$(/usr/bin/env dirname "${SCRIPT_ABSOLUTE_PATH}")";
typeset METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -f "${SCRIPT_ROOT}/../lib/plugin" ] && . "${SCRIPT_ROOT}/../lib/plugin";

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

[ -z "${APP_ROOT}" ] && awk -F "=" '/\<1\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

[ -f "${PLUGIN_LIB_DIRECTORY}/aliases" ] && . "${PLUGIN_LIB_DIRECTORY}/aliases";
[ -f "${PLUGIN_LIB_DIRECTORY}/functions" ] && . "${PLUGIN_LIB_DIRECTORY}/functions";

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

THIS_CNAME="${CNAME}";
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

## validate the input
"${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh" -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

CNAME="${THIS_CNAME}";
typeset typeset METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
then
    writeLogEntry "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security violation found while executing ${CNAME} by ${IUSER_AUDIT} on host ${SYSTEM_HOSTNAME}";
    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security configuration blocks execution. Please verify security configuration.";

    awk -F "=" '/\<request.not.authorized\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' && return 1;

    return ${RET_CODE};
fi

#===  FUNCTION  ===============================================================
#          NAME:  copyZoneToMaster
#   DESCRIPTION:  Creates a tarball of the provided group directory containing
#                 all subdirectories and files. This tarball is then scp'd to
#                 the master nameserver, where it is untarred and copied into
#                 the zone root for function usage. Optionally sends a flag to restart
#                 named services on completion.
#    PARAMETERS:  None
#==============================================================================
function copyZoneToMaster
{
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    if [ ! -z "${IS_DNS_RECORD_ADD_ENABLED}" ] && [ "${IS_DNS_RECORD_ADD_ENABLED}" = "${_FALSE}" ]
    then
        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS record addition has not been enabled. Cannot continue.";

        RETURN_CODE=97;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

        return ${RETURN_CODE};
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating tar file..";

    TARFILE_DATE=$(date +"%m-%d-%Y");

    ## make sure our target zone structure exists
    if [ -d "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT} ]
    then
        tar cf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar \
            -C "${PLUGIN_WORK_DIRECTORY}" ${GROUP_ID}${BUSINESS_UNIT} >> /dev/null 2>&1;
        gzip "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar >> /dev/null 2>&1;

        ## validate that the tar was indeed created
        if [ -s "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${PLUGIN_WORK_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${TARFILE_DATE}.${ZONE_NAME}.tar.gz created";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "tarfile created. Sending to DNS master ${NAMED_MASTER}..";

            if [ ! -z "${LOCAL_EXECUTION}" ] && [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution set to TRUE. Executing command executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z ${ZONE_NAME} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e..";

                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/executors/executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e;
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
            else
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command scp local-copy ${NAMED_MASTER} "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz ${REMOTE_APP_ROOT}/"${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz";

                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                ## validate the input
                scp local-copy ${NAMED_MASTER} "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz ${REMOTE_APP_ROOT}/"${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz ${SSH_USER_NAME} ${SSH_USER_AUTH};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                then
                    writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred executing the requested command. Cannot continue.";

                    break;
                fi

                unset METHOD_NAME;
                unset CNAME;

                ## copy the files up
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                ## validate the input
                ssh ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -d ${TARFILE_DATE} -e";
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";
            fi

            if [ ${RET_CODE} -eq 0 ]
            then
                ## unset return code
                unset RET_CODE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unpack and install complete.";
                writeLogEntry "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONE_NAME} installed by ${IUSER_AUDIT} per change ${CHANGE_NUM} on $(date +"%m-%d-%Y") at $(date +"%H:%M:%S")";

                ## rndc reconfig complete. validate
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                ## validate the input
                ${PLUGIN_LIB_DIRECTORY}/runRNDCCommands.sh -s ${NAMED_MASTER} -c reconfig -e;
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                CNAME="${THIS_CNAME}";
                typeset METHOD_NAME="${THIS_CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ ${RET_CODE} -eq 0 ]
                then
                    ## rndc reconfig complete. validate
                        unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/runRNDCCommands.sh -s ${NAMED_MASTER} -c reconfig -e;
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                    if [ ${RET_CODE} -eq 0 ]
                    then
                        ## new zone was successfully installed and is active on named master.
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully reconfigured ${NAMED_MASTER}. Zone has taken effect.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Sending notification email..";

                        ## send the notification email
                                unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                        ## validate the input
                        ${MAILER_CLASS} -m notifyZoneChange -p ${PROJECT_CODE} -a "${DNS_SERVER_ADMIN_EMAIL}" -e;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                        CNAME="${THIS_CNAME}";
                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        if [ ${RET_CODE} -eq 0 ]
                        then
                            ## all processing complete. notifications have been sent.
                            RETURN_CODE=0;
                        else
                            ## notifier threw an error. we re-throw as a warning.
                            RETURN_CODE=66;
                        fi
                    else
                        ## failed to validate that new zone is active. throw an error
                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to validate that new zone ${ZONE_NAME} has been successfully activated on ${NAMED_MASTER}";

                        RETURN_CODE=61;
                    fi
                else
                    ## reconfiguration failed. throw out an error.
                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server reconfiguration on ${NAMED_MASTER} FAILED.";

                    RETURN_CODE=52;
                fi
            else
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred during installation of the new zone. Return code from execute_add_record -> ${RETURN_CODE}";

                ## we know we drop back into the confirmation step, right before
                ## this method is initially executed. so lets take a minute to
                ## clean up the tarballs we've created so it doesn't cause a problem
                ## next time.

                RETURN_CODE=59;
            fi
        else
            ## tarfile never got made
            writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Archive creation failed. Cannot continue.";

            RETURN_CODE=60;
        fi
    else
        ## zone directories don't yet exist. throw out an error
        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested zone directories do not yet exists. Cannot continue.";

        RETURN_CODE=39;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    rm -rf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz;
    rm -rf "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar;

    ## unset variables
    unset TARFILE_DATE;
    unset PERFORM_COPY;

    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

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
#==============================================================================
function copyZoneToSlave
{
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    if [ ! -z "${IS_DNS_RECORD_ADD_ENABLED}" ] && [ "${IS_DNS_RECORD_ADD_ENABLED}" = "${_FALSE}" ]
    then
        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS record addition has not been enabled. Cannot continue.";

        RETURN_CODE=97;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

        return ${RETURN_CODE};
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating tar file..";

    ARCHIVE_FILE="${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.$(date +"%m-%d-%Y").${IUSER_AUDIT}.tar.gz

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ARCHIVE_FILE -> ${ARCHIVE_FILE}";

    ## we should already have a tarfile for this.
    ## check and make sure

    if [ ! -s ${ARCHIVE_FILE} ]
    then
        ## we dont have a tarfile to operate with. either the zone didnt get created yet,
        ## or the tarfile we're looking for never got created or is of a different name.
        ## either way, return an error back to the requestor
        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to locate tarball containing zone files. Please ensure that the requested zone has been created and copied to the configured master.";

        RETURN_CODE=60;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

        return ${RETURN_CODE};
    fi

    ## the tarfile we need does indeed exist. continue processing
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${PLUGIN_WORK_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${TARFILE_DATE}.${ZONE_NAME}.tar.gz exists, continuing..";

    ## having a slave server on the same server as your master probably isnt the best idea. since
    ## its physically possible, we leave the code here.. but realistically it should never ever get executed.
    for SERVER in ${SLAVE_SERVERS[*]}
    do
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER -> ${SERVER}";

        if [ ! -z "${LOCAL_EXECUTION}" ] && [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution set to TRUE. Executing command executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e..";

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

            ## validate the input
            ${PLUGIN_LIB_DIRECTORY}/executors/executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -s -e;
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ ! -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred installing the zone. Cannot continue.";

                (( ERROR_COUNT += 1 ));

                continue;
            fi

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

            ## validate the input
            ${PLUGIN_LIB_DIRECTORY}/runRNDCCommands.sh -s ${SERVER} -c reconfig -e;
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ ! -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred installing the zone. Cannot continue.";

                (( ERROR_COUNT += 1 ));

                continue;
            fi

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

            ## validate the input
            "${APP_ROOT}/${LIB_DIRECTORY}"/runQuery.sh -s "${SLAVE_SERVER}" -t A -u ${ZONE_NAME} -o -e;
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ ! -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred installing the zone. Cannot continue.";

                (( ERROR_COUNT += 1 ));

                continue;
            fi
        else
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command scp local-copy ${NAMED_MASTER} "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz ${REMOTE_APP_ROOT}/"${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz";

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

            ## validate the input
            scp local-copy ${SERVER} "${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz ${REMOTE_APP_ROOT}/"${PLUGIN_WORK_DIRECTORY}"/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz ${SSH_USER_NAME} ${SSH_USER_AUTH};
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
            then
                ## do something here
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred installing the zone. Cannot continue.";

                (( ERROR_COUNT += 1 ));

                continue;
            fi

            unset METHOD_NAME;
            unset CNAME;

            ## copy the files up
            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

            ## validate the input
            ssh ${SERVER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z \"${ZONE_NAME}\" -i ${IUSER_AUDIT} -c \"${CHANGE_NUM}\" -d ${TARFILE_DATE} -s -e";
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            if [ ! -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred installing the zone. Cannot continue.";

                (( ERROR_COUNT += 1 ));

                continue;
            fi

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

            ## validate the input
            ${PLUGIN_LIB_DIRECTORY}/runRNDCCommands.sh -s ${SERVER} -c reconfig -e;
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

            ## validate the input
            "${APP_ROOT}/${LIB_DIRECTORY}"/runQuery.sh -s "${SLAVE_SERVER}" -t A -u ${ZONE_NAME} -o -e;
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ ! -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred installing the zone. Cannot continue.";

                (( ERROR_COUNT += 1 ));

                continue;
            fi
        fi
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    ## unset variables
    ## unset variables
    unset TARFILE_DATE;
    unset SLAVE_COPY;
    unset SLAVE_SERVER;

    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#==============================================================================
function addZoneEntry
{
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    if [ ! -z "${IS_DNS_RECORD_ADD_ENABLED}" ] && [ "${IS_DNS_RECORD_ADD_ENABLED}" = "${_FALSE}" ]
    then
        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS record addition has not been enabled. Cannot continue.";

        RETURN_CODE=97;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

        return ${RETURN_CODE};
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating record data..";

    case ${ENTRY_TYPE} in
        [Aa]|[Nn][Ss]|[Cc][Nn][Aa][Mm][Ee]|[Tt][Xx][Tt])
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating entry ${ENTRY_NAME} of type ${ENTRY_TYPE}";

            if [ -z "${ENTRY_NAME}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No entry name was provided. Cannot continue.";

                RETURN_CODE=30;
            elif [ -z "${ENTRY_RECORD}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No entry target was provided. Cannot continue.";

                RETURN_CODE=30;
            else
                ## we have our data, continue
                if [ ! -z "${LOCAL_EXECUTION}" ] && [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
                then
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_NAME},${ENTRY_RECORD} -e;
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                else
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ssh ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_NAME},${ENTRY_RECORD} -e"";

                        unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                    ## validate the input
                    ssh ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_NAME},${ENTRY_RECORD} -e";
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                fi
            fi
            ;;
        [Mm][Xx])
            if [ -z "${ENTRY_NAME}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No entry name was provided. Cannot continue.";

                RETURN_CODE=30;
            elif [ -z "${ENTRY_PRIORITY}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No entry priority was provided. Cannot continue.";

                RETURN_CODE=30;
            elif [ -z "${ENTRY_RECORD}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No entry target was provided. Cannot continue.";

                RETURN_CODE=30;
            else
                ## we have our data, continue
                if [ ! -z "${LOCAL_EXECUTION}" ] && [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution set to TRUE. Executing command executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_NAME},${ENTRY_RECORD} -e..";

                        unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_NAME},${ENTRY_PRIORITY},${ENTRY_RECORD} -e;
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                else
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ssh ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_PRIORITY},${ENTRY_NAME},${ENTRY_RECORD} -e"";

                        unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                    ## validate the input
                    ssh ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_NAME},${ENTRY_PRIORITY},${ENTRY_RECORD} -e";
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                fi
            fi
            ;;
        [Ss][Rr][Vv])
            if [ -z "${ENTRY_TYPE}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service type was provided. Cannot continue.";

                RETURN_CODE=30;
            elif [ -z "${ENTRY_PROTOCOL}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service protocol was provided. Cannot continue.";

                RETURN_CODE=30;
            elif [ -z "${ENTRY_NAME}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service name was provided. Cannot continue.";

                RETURN_CODE=30;
            elif [ -z "${ENTRY_TTL}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service TTL was provided. Cannot continue.";

                RETURN_CODE=30;
            elif [ -z "${ENTRY_PRIORITY}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service priority was provided. Cannot continue.";

                RETURN_CODE=30;
            elif [ -z "${ENTRY_WEIGHT}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service weight was provided. Cannot continue.";

                RETURN_CODE=30;
            elif [ -z "${ENTRY_PORT}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service port was provided. Cannot continue.";

                RETURN_CODE=30;
            elif [ -z "${ENTRY_RECORD}" ]
            then
                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No service target was provided. Cannot continue.";

                RETURN_CODE=30;
            else
                ## we have our data, continue
                if [ ! -z "${LOCAL_EXECUTION}" ] && [ "${LOCAL_EXECUTION}" = "${_TRUE}" ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Local execution set to TRUE. Executing command executeServiceAddition.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_PROTOCOL},${ENTRY_NAME},${ENTRY_TTL},${ENTRY_PRIORITY},${ENTRY_WEIGHT},${ENTRY_PORT},${ENTRY_RECORD} -e..";

                        unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_NAME},${ENTRY_PRIORITY},${ENTRY_RECORD} -e;
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                else
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ssh ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_PROTOCOL},${ENTRY_NAME},${ENTRY_TTL},${ENTRY_PRIORITY},${ENTRY_WEIGHT},${ENTRY_PORT},${ENTRY_RECORD} -e"";

                        unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                    ## validate the input
                    ssh ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${PLUGIN_LIB_DIRECTORY}/executors/execute_add_record.sh -b ${BUSINESS_UNIT} -p ${PROJECT_CODE} -z "${ZONE_NAME}" -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -a ${ENTRY_TYPE},${ENTRY_PROTOCOL},${ENTRY_NAME},${ENTRY_TTL},${ENTRY_PRIORITY},${ENTRY_WEIGHT},${ENTRY_PORT},${ENTRY_RECORD} -e";
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                fi
            fi
            ;;
        *)
            writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The record type, ${ENTRY_TYPE}, is not yet supported.";

            RETURN_CODE=49;
            ;;
    esac

    ## capture the return code
    typeset -i RET_CODE=${?};

    CNAME="${THIS_CNAME}";
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z "${RETURN_CODE}" ]
    then
        if [ ${RET_CODE} -eq 0 ] || [ ${RET_CODE} -eq 86 ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully reconfigured ${NAMED_MASTER}. Zone has taken effect.";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Sending notification email..";

            ## send the notification email
            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

            ## validate the input
            ${MAILER_CLASS} -m notifyZoneChange -p ${PROJECT_CODE} -a "${DNS_SERVER_ADMIN_EMAIL}" -e;
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
            [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

            CNAME="${THIS_CNAME}";
            typeset METHOD_NAME="${THIS_CNAME}#${0}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ ${RET_CODE} -eq 0 ]
            then
                ## all processing complete. notifications have been sent.
                if [ ${RET_CODE} -eq 86 ]
                then
                    RETURN_CODE=${RET_CODE};
                else
                    RETURN_CODE=0;
                fi
            else
                ## notifier threw an error. we re-throw as a warning.
                RETURN_CODE=66;
            fi
        else
            ## process completed but we got an error back from rndc.
            writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while adding the new record. Please try again.";

            RETURN_CODE=${RET_CODE};
        fi
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    ## unset variables
    unset RET_CODE;
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

    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#==============================================================================
function usage
{
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
    typeset RETURN_CODE=3;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    echo "${THIS_CNAME} - Execute zone additions to the DNS infrastructure.\n" >&2;
    echo "Usage: ${THIS_CNAME} [ -b <business unit> ] [ -p <project code> ] [ -z <zone name> ] [ -c <change request> ] [ -n <filename> ] [ -a <entry> ] [ -s ] [ -e ] [ -h|-? ]
    -b         -> The associated business unit
    -p         -> The associated project code
    -z         -> The zone name, eg example.com
    -c         -> The change order associated with this request
    -n         -> Add a new zone to the DNS infrastructure. Full path to zone data required.
    -a         -> Add a new entry to an existing zone. Comma-delimited information set required.
    -s         -> Specifies whether or not to operate against a slave server. Only valid with -n.
    -e         -> Execute processing
    -?|-h      -> Show this help\n" >&2;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

    return ${RETURN_CODE};
}

[ ${#} -eq 0 ] && usage; RETURN_CODE=${?};

while getopts ":b:p:z:c:n:a:xseh:" OPTIONS 2>/dev/null
do
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    case "${OPTIONS}" in
        b)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting BUSINESS_UNIT..";

            ## Capture the site root
            typeset -u BUSINESS_UNIT="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
            ;;
        p)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting PROJECT_CODE..";

            ## Capture the site root
            typeset -u PROJECT_CODE="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROJECT_CODE -> ${PROJECT_CODE}";
            ;;
        z)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ZONE_NAME..";

            ## Capture the site root
            ZONE_NAME=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_NAME -> ${ZONE_NAME}";
            ;;
        c)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            ## Capture the change control
            typeset -u CHANGE_NUM="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        n)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting INSTALL_ZONE to TRUE..";

            ## Capture the change control
            INSTALL_ZONE=${_TRUE};
            typeset -l ZONE_DATA_FILE="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        a)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ADD_ENTRY to TRUE";

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
                    typeset -l ENTRY_RECORD=$(cut -d "," -f 3 <<< ${OPTARG});
                    typeset -l ENTRY_PRIORITY=$(cut -d "," -f 4 <<< ${OPTARG});
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
                    ENTRY_TYPE=$(cut -d "," -f 1 <<< ${OPTARG});
                    ENTRY_PROTOCOL=$(cut -d "," -f 2 <<< ${OPTARG});
                    ENTRY_NAME=$(cut -d "," -f 3 <<< ${OPTARG});
                    ENTRY_TTL=$(cut -d "," -f 4 <<< ${OPTARG});
                    ENTRY_PRIORITY=$(cut -d "," -f 5 <<< ${OPTARG});
                    ENTRY_WEIGHT=$(cut -d "," -f 6 <<< ${OPTARG});
                    ENTRY_PORT=$(cut -d "," -f 7 <<< ${OPTARG});
                    ENTRY_RECORD=$(cut -d "," -f 8 <<< ${OPTARG});
                    ;;
                *)
                    ## as-yet unsupported record type - this list should follow the list
                    ## of data helpers. if theres no record helper for it then this executor
                    ## should be able to add it.
                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid record type was provided. Cannot continue.";

                    RETURN_CODE=49;
                    ;;
            esac

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_ENTRY -> ${ADD_ENTRY}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTRY_NAME -> ${ENTRY_NAME}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTRY_TYPE -> ${ENTRY_TYPE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTRY_RECORD -> ${ENTRY_RECORD}";
            ;;
        x)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting PERFORM_COPY to true..";

            ## Capture the change control
            PERFORM_COPY=${_TRUE};
            ;;
        s)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SLAVE_COPY to true..";

            ## Capture the change control
            SLAVE_COPY=${_TRUE};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SLAVE_SERVER..";

            typeset -l SLAVE_SERVER="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SLAVE_SERVER ->${SLAVE_SERVER}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${RETURN_CODE}" ] || [ ${RETURN_CODE} -ne 0 ]
            then
                if [ -z "${BUSINESS_UNIT}" ]
                then
                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=15;
                elif [ -z "${PROJECT_CODE}" ]
                then
                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=24;
                elif [ -z "${ZONE_NAME}" ]
                then
                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=24;
                elif [ -z "${CHANGE_NUM}" ]
                then
                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change order was provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=17;
                elif [ -z "${IUSER_AUDIT}" ]
                then
                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requestors username was not provided. Unable to continue processing.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=20;
                else
                    ## We have enough information to process the request, continue
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    [ ! -z "${ADD_ENTRY}" ] && [ "${ADD_ENTRY}" = "${_TRUE}" ] && addZoneEntry;

                    if [ ! -z "${INSTALL_ZONE}" ] && [ "${INSTALL_ZONE}" = "${_TRUE}" ]
                    then
                        [ -z "${SLAVE_OPERATION}" ] && [ "${SLAVE_OPERATION}" = "${_TRUE}" ] && copyZoneToSlave || copyZoneToMaster;
                    else
                        ## no valid command type
                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No valid command type was provided. Cannot continue.";

                        RETURN_CODE=3;
                    fi
                fi
            fi
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage; RETURN_CODE=${?};
            ;;
    esac
done

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
[ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

[ -z "${RETURN_CODE}" ] && return 1 || return "${RETURN_CODE}";
