#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  validate_change_request.sh
#         USAGE:  ./validate_change_request.sh
#   DESCRIPTION:  Performs service validation to ensure that a requested change
#                 has indeed been completed.
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

## Application constants
CNAME="$(basename "${0}")";

function validate_change_request
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    ## make sure our output file exists
    if [ -s ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE} ]
    then
        ## we need to know what got changed and what it got changed to.
        ## this could be the addition of a new zone, the removal of an
        ## existing zone or some form of failover.
        if [ ! -z "${1}" ] && [ "${1}" = "failover" ]
        then
            ## we're asked to provide validation for a failover request.
            ## we need to know where the site was failed over to
            typset -u TARGET_DATACENTER=${2};

            if [ ! -z "${TARGET_DATACENTER}" ] && [[ ${TARGET_DATACENTER} == [PV]H ]]
            then
                ## we got some data back for the query. lets see what it is...
                ## we've configured the first two octets of each datacenter, so
                ## we can check for their existence in the file
                if [ $(grep -c ${PRIMARY_DATACENTER_IP} ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE}) -eq 1 ]
                then
                    ## site is live in the configured primary datacenter.
                    if [ "${TARGET_DATACENTER}" = "${PRIMARY_DC}" ]
                    then
                        ## IP address matches the request.
                        RETURN_CODE=0;
                    else
                        ## no dice. the IP we got doesnt match where we were told it should be.
                        RETURN_CODE=1;
                    fi
                elif [ $(grep -c ${SECONDARY_DATACENTER_IP} ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE}) -eq 1 ]
                then
                    ## site is live in the configured secondary datacenter.
                    if [ "${TARGET_DATACENTER}" = "${SECONDARY_DC}" ]
                    then
                        ## IP address matches the request.
                        RETURN_CODE=0;
                    else
                        ## no dice. the IP we got doesnt match where we were told it should be.
                        RETURN_CODE=1;
                    fi
                else
                    ## the ip we got back doesn't match the configured primary or secondary.
                    ## we cant accurately validate it.
                    RETURN_CODE=1;
                fi
            else
                ## we dont know what datacenter the service was failed over to. we can
                ## guess or we can fail. we should probably fail due to the lack of "INFO".
                ## NOTE: there are other ways to check, like a TXT record in the zone
                ## that we can dig. but not everyone does this.
                RETURN_CODE=1;
            fi
        elif [ ! -z "${1}" ] && [ "${1}" = "add" ]
        then
            ## we've been asked to validate that a new record was added to the installation.
            ## we need to know what the record name and type is. if we get a response back
            ## from DNS via DiG, its there, otherwise, it isnt.
            ## create our variables
            typeset -l VALIDATE_SERVER="${2}";
            typeset -u VALIDATE_TYPE="${3}";
            typeset -l VALIDATE_URL="${4}";

            ## make sure our variables contain actual data
            if [ ! -z "${VALIDATE_SERVER}" ] && [ ! -z "${VALIDATE_TYPE}" ] && [ ! -z "${VALIDATE_URL}" ]
            then
                ## all our vars have data. we can continue.
                ${PLUGIN_ROOT_DIR}/lib/runQuery.sh -s ${3} -t ${4} -u ${5} -o -e;
                typeset -i RET_CODE=${?};

                ## check our retcode
                if [ ${RET_CODE} -eq 0 ]
                then
                    ## our request was met with a response. this means that whatever we
                    ## we were asked to validate did so successfully, or the file would
                    ## be empty ( or non-existent, either way)
                    RETURN_CODE=0;
                else
                    ## an "ERROR" occurred running the query. we havent
                    ## successfully completed our validation, so we fail out.
                    RETURN_CODE=1;
                fi
            else
                ## one or more of the variables we need is blank. we cant move forward without
                RETURN_CODE=1;
            fi
        elif [ ! -z "${1}" ] && [ "${1}" = "remove" ]
        then
            ## we've been asked to validate that a particular record has been removed
            ## just run a DiG request against the necessary server and ensure we don't
            ## get a record back
            ## create our variables
            typeset -l VALIDATE_SERVER="${2}";
            typeset -u VALIDATE_TYPE="${3}";
            typeset -l VALIDATE_URL="${4}";

            ## make sure our variables contain actual data
            if [ ! -z "${VALIDATE_SERVER}" ] && [ ! -z "${VALIDATE_TYPE}" ] && [ ! -z "${VALIDATE_URL}" ]
            then
                ## all our vars have data. we can continue.
                ${PLUGIN_ROOT_DIR}/lib/runQuery.sh -s ${3} -t ${4} -u ${5} -o -e;
                typeset -i RET_CODE=${?};

                ## check our retcode
                if [ ${RET_CODE} -eq 0 ]
                then
                    ## our request was met with a response. this means that whatever we
                    ## we were asked to validate returned a response, and it has thusly
                    ## not been removed.
                    RETURN_CODE=1;
                else
                    ## we got a non-zero response back from run_query. this means that
                    ## what we were asked to validate doesn't exist anymore on the target.
                    RETURN_CODE=0;
                fi
            else
                ## one or more of the variables we need is blank. we cant move forward without
                RETURN_CODE=1;
            fi
        else
            ## we didn't get a type argument. cant continue without
            RETURN_CODE=1;
        fi
    else
        ## output file doesnt exist, we cant validate here
        RETURN_CODE=1;
    fi

    ## unset variables
    unset VALIDATE_SERVER;
    unset VALIDATE_TYPE;
    unset VALIDATE_URL;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#       RETURNS:  1
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    print "${CNAME} - Validate that a change request has been successfully performed.";
    print "Usage:  ${CNAME} change-type target-datacenter record-name record-type";
    print "          target-datacenter is an optional argument that is only required when change-type is failover";
    print "          record-name is an optional argument that is only required when change-type is add/remove";
    print "          record-type is an optional argument that is only required when record-name is specified";

    return 3;
}

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

## make sure we have args
[ ${#} -eq 0 ] && usage || validate_change_request ${@};

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset THIS_CNAME;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return ${RETURN_CODE};
