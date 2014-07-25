#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  update_root_servers.sh
#         USAGE:  ./update_root_servers.sh
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

function obtainAndInstallRoots
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Retrieving updates to ${NAMED_ROOT_CACHE}..";

    ## check if we have a tmp file, if we do, kill
    [ -s ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE} ] && rm -rf ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE};

    if [ $(grep -c $(uname -n) <<< ${EXT_SLAVES[@]}) -eq 1 ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "This is an external server. Executing DiG..";

        ## we're on an external slave. we don't need to run through a proxy
        $(dig +bufsize=1200 +norec NS . @a.root-servers.net > ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE});
        typeset -i RET_CODE=${?};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. Validating..";

        if [ ${RET_CODE} -eq 0 ]
        then
            ## we should have a populated file. lets check
            if [ -s ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE} ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Updated root file created..";

                ## file exists, make sure it doesn't contain errors
                if [ $(grep -c "couldn\'t get address" ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE}) -ne 0 ] ||
                    [ $(grep -c "connection timed out" ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE}) -ne 0 ]
                then
                    ## an "ERROR" occurred and the root server list could not be obtained
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to obtain updated root server list from a.root-servers.net.";

                    RETURN_CODE=77;
                else
                    ## got some updates, lets go ahead and apply them
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying updates to named installation..";

                    ## backup the existing root servers file
                    (cd ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}; \
                        tar cf - ${NAMED_ROOT_CACHE}) | gzip -c > ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${TARFILE_NAME};

                    ## take a pre-file checksum
                    PRE_FILE_CKSUM=$(cksum ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE} | awk '{print $1}');

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRE_FILE_CKSUM -> ${PRE_FILE_CKSUM}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing existing ${NAMED_ROOT_CACHE} ..";

                    ## remove the existing and copy in the new
                    rm -rf ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_ROOT_CACHE} > /dev/null 2>&1;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removal complete. Validating..";

                    ## make sure it was removed..
                    if [ ! -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_ROOT_CACHE} ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removal validated. Moving new file..";

                        ## ok, it was, move in the new
                        mv ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE} \
                            ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_ROOT_CACHE} > /dev/null 2>&1;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Move complete. Validating..";

                        ## and make sure it worked..
                        if [ -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_ROOT_CACHE} ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File validation complete. Validating content..";

                            POST_FILE_CKSUM=$(cksum ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_ROOT_CACHE} | awk '{print $2}');

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POST_FILE_CKSUM -> ${POST_FILE_CKSUM}";

                            if [ ${PRE_FILE_CKSUM} -eq ${POST_FILE_CKSUM} ]
                            then
                                ## all set
                                ${LOGGER} "INFO" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Installation of ${NAMED_ROOT_CACHE} complete.";

                                RETURN_CODE=0;
                            else
                                ## some "ERROR" occurred
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred during file installation. Please process manually.";

                                RETURN_CODE=78;
                            fi
                        else
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred during file installation. Please process manually.";

                            RETURN_CODE=78;
                        fi
                    else
                        ## existing file wasnt removed
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred during file installation. Please process manually.";

                        RETURN_CODE=78;
                    fi
                fi
            else
                ## unpopulated update file
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred obtaining root server updates. Please process manually.";

                RETURN_CODE=77;
            fi
        else
            ## return code from the dig call was non-zero
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred obtaining root server updates. Please process manually.";

            RETURN_CODE=77;
        fi
    else
        ## the dig command can only be run on a proxy,
        ## the master nameserver won't have internet access
        for EXTERNAL_SERVER in ${EXT_SLAVES[@]}
        do
            ## stop if its available and run the command
            $(ping ${EXTERNAL_SERVER} > /dev/null 2>&1);

            PING_RCODE=${?}

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

            if [ ${PING_RCODE} -eq 0 ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing ssh ${EXTERNAL_SERVER} \"dig +bufsize=1200 +norec NS . @a.root-servers.net\" > ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE}";

                ssh ${EXTERNAL_SERVER} "dig +bufsize=1200 +norec NS . @a.root-servers.net" > ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE};

                ## we should have a populated file. lets check
                if [ -s ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE} ]
                then
                    ## file exists, make sure it doesn't contain errors
                    if [ $(grep -c "couldn\'t get address" ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE}) -ne 0 ] ||
                        [ $(grep -c "connection timed out" ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE}) -ne 0 ]
                    then
                        ## an "ERROR" occurred and the root server list could not be obtained
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to obtain updated root server list from a.root-servers.net.";

                        GENERATION_FAILURE=${_TRUE};
                        RETURN_CODE=77;
                    else
                        ## we got our file and its valid. break out to continue execution
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File obtained. Clearing "ERROR" counters..";

                        ## check if our failure points are set, if they are, unset them
                        if [ ! -z "${RETURN_CODE}" ] || [ ! -z "${GENERATION_FAILURE}" ] || [ ! -z "${PROXY_FAILURE}" ]
                        then
                            unset PROXY_FAILURE;
                            unset GENERATION_FAILURE;
                        fi

                        break;
                    fi
                else
                    ## root cache file didnt get created
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while creating the root cache file. Cannot continue.";

                    GENERATION_FAILURE=${_TRUE};
                fi
            else
                ## first one wasnt available, check the remaining
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${EXTERNAL_SERVER} not available, continuing..";

                PROXY_FAILURE=${_TRUE};
            fi
        done

        if [ -z "${RETURN_CODE}" ] && [ -z "${GENERATION_FAILURE}" ] && [ -z "${PROXY_FAILURE}" ]
        then
            ## we should have a populated file. lets check
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying updates to named installation..";

            ## backup the existing root servers file
            (cd ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}; \
                tar cf - ${NAMED_ROOT_CACHE}) | gzip -c > ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${TARFILE_NAME};

            ## take a pre-file checksum
            PRE_FILE_CKSUM=$(cksum ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE} | awk '{print $1}');

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRE_FILE_CKSUM -> ${PRE_FILE_CKSUM}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing existing ${NAMED_ROOT_CACHE} ..";

            ## remove the existing and copy in the new
            rm -rf ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_ROOT_CACHE} > /dev/null 2>&1;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removal complete. Validating..";

            ## make sure it was removed..
            if [ ! -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_ROOT_CACHE} ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removal validated. Moving new file..";

                ## ok, it was, move in the new
                mv ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE} \
                    ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_ROOT_CACHE} > /dev/null 2>&1;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Move complete. Validating..";

                ## and make sure it worked..
                if [ -s ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_ROOT_CACHE} ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "File validation complete. Validating content..";

                    POST_FILE_CKSUM=$(cksum ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_ROOT_CACHE} | awk '{print $2}');

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POST_FILE_CKSUM -> ${POST_FILE_CKSUM}";

                    if [ ${PRE_FILE_CKSUM} -eq ${POST_FILE_CKSUM} ]
                    then
                        ## all set
                        ${LOGGER} "INFO" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Installation of ${NAMED_ROOT_CACHE} complete.";

                        RETURN_CODE=0;
                    else
                        ## some "ERROR" occurred
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred during file installation. Please process manually.";

                        RETURN_CODE=78;
                    fi
                else
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred during file installation. Please process manually.";

                    RETURN_CODE=78;
                fi
            else
                ## existing file wasnt removed
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred during file installation. Please process manually.";

                RETURN_CODE=78;
            fi
        fi
    fi

    if [ ! -z "${CAUGHT_ERROR}" ] || [ ! -z "${CAUGHT_WARNING}" ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing completed with warnings.";

        RETURN_CODE=78;
    else
        ${LOGGER} "INFO" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing complete.";

        RETURN_CODE=0;
    fi

    ## post-execution cleanup
    [ -s ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE} ] && rm -rf ${PLUGIN_WORK_DIRECTORY}/${NAMED_ROOT_CACHE} > /dev/null 2>&1;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

obtainAndInstallRoots && RETURN_CODE=${?};

trap "unlockProcess ${LOCKFILE} ${$}; return ${RETURN_CODE}" INT TERM EXIT;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

[ -z "${RETURN_CODE}" ] && return 1 || return "${RETURN_CODE}";
