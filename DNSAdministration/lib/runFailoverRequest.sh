#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  runServiceFailover.sh
#         USAGE:  ./runServiceFailover.sh
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
## Application constants
PLUGIN_NAME="dnsadmin";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#          NAME:  runInternetSiteFailover
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function runInternetSiteFailover
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILENAME->${FILENAME}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET->${TARGET}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRJCODE-> ${PRJCODE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LOCAL_EXECUTION -> ${LOCAL_EXECUTION}";

    ## our request type is p <project code> or u <url>
    ## both can be run against the same script, as its
    ## a single-zone change
    ## spawn an ssh connection to the DNS master
    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command executeSiteFailover.sh -d x -b ${UNIT} -f ${FILENAME} -t ${TARGET} -p ${PRJCODE} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e";

        unset METHOD_NAME;
        unset CNAME;

        ## capture the current optind
        RFR_OPTIND=${OPTIND};

        FAILOVER_CODE=$(${APP_ROOT}/lib/executors/executeSiteFailover.sh -d x -b ${UNIT} -f ${FILENAME} -t ${TARGET} -p ${PRJCODE} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e);

        CNAME="$(basename "${0}")";
        [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

        ## and put it back
        OPTIND=${RFR_OPTIND};

        unset RFR_OPTIND;
    else
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${NAMED_MASTER} \"${REMOTE_APP_ROOT}/lib/executors/executeSiteFailover.sh -d x -b ${UNIT} -f ${FILENAME} -t ${TARGET} -p ${PRJCODE} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e\"";

        ${APP_ROOT}/lib/tcl/runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/lib/executors/executeSiteFailover.sh -d x -b ${UNIT} -f ${FILENAME} -t ${TARGET} -p ${PRJCODE} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e";

        FAILOVER_CODE=${?};
    fi

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FAILOVER_CODE -> ${FAILOVER_CODE}";

    if [ ${FAILOVER_CODE} -eq 0 ]
    then
        ## failover was successful. lets do some more work...
        unset FAILOVER_CODE;
        unset RETURN_CODE;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover completed. Reloading server configuration..";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command run_rndc_request.sh -s ${NAMED_MASTER} -c reload -e..";

        unset CNAME;
        unset METHOD_NAME;

        ## capture the current optind
        RFR_OPTIND=${OPTIND};

        ## send an rndc reload to the server to make it active
        . ${APP_ROOT}/lib/run_rndc_request.sh -s ${NAMED_MASTER} -c reload -e;
        RNDC_CODE=${?};

        CNAME="$(basename "${0}")";
        [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

        ## and put it back
        OPTIND=${RFR_OPTIND};

        unset RFR_OPTIND;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC_CODE -> ${RNDC_CODE}";

        if [ ${RNDC_CODE} -eq 0 ]
        then
            ## we've successfully reloaded our configuration. verify that the change was indeed made
            unset RNDC_CODE;
            unset RETURN_CODE;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server configuration successfully reloaded. Validating change..";

            ## sleep for configured thread delay to allow changes to propagate
            sleep "${MESSAGE_DELAY}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing query against ${NAMED_MASTER}..";

            unset METHOD_NAME;
            unset CNAME;

            ## capture the current optind
            RFR_OPTIND=${OPTIND};

            ## ok, we know where it was failed over. we can use this information
            ## to determine if the change was applied.
            . ${APP_ROOT}/lib/runQuery.sh -s ${NAMED_MASTER} -t A -u ${SITE_HOSTNAME} -o -e;

            CNAME="$(basename "${0}")";
            [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

            ## and put it back
            OPTIND=${RFR_OPTIND};

            unset RFR_OPTIND;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Query complete. Validating..";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing validate_change_request.sh failover ${TARGET}..";

            unset METHOD_NAME;
            unset CNAME;

            ## validate the change
            . ${APP_ROOT}/lib/validators/validate_change_request.sh failover ${TARGET};
            VALIDATE_CODE=${?};

            CNAME="$(basename "${0}")";
            [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VALIDATE_CODE -> ${VALIDATE_CODE}";

            if [ ${VALIDATE_CODE} -eq 0 ]
            then
                ## we've applied and activated our change. reload changes into the configured
                ## slave servers
                unset VALIDATE_CODE;
                unset RETURN_CODE;
                unset RET_CODE;
                unset RETURN_TEXT;

                unset CNAME;
                unset METHOD_NAME;

                ## capture the current optind
                RFR_OPTIND=${OPTIND};

                ## send out a notification email advising of the failover.
                . ${MAILER_CLASS} -m notifySiteFailover -p ${PROJECT_CODE} -a "${DNS_SERVER_ADMIN_EMAIL}" -e;

                CNAME="$(basename "${0}")";
                [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

                ## and put it back
                OPTIND=${RFR_OPTIND};

                unset RFR_OPTIND;

                ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "$(${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: ${UNIT}/${PRJCODE}/${FILENAME} - Change Request: ${CHG_CTRL} - Switched To: ${TARGET}");";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change successfully validated on ${NAMED_MASTER}. Processing against slaves..";

                ## make sure that we have slaves to operate against
                if [ ${#DNS_SLAVES[@]} -ne 0 ]
                then
                    ## we have slaves to process against. do so.
                    ## make sure D is 0, ERROR_COUNT is 0
                    D=0;
                    ERROR_COUNT=0;
                    unset RETURN_CODE;

                    while [ ${D} -ne ${#DNS_SLAVES[@]} ]
                    do
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating against ${DNS_SLAVES[${D}]}..";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command run_rndc_request.sh -s ${DNS_SLAVES[${D}]} -c reload -e..";

                        ## temp unset
                        unset METHOD_NAME;
                        unset CNAME;

                        ## capture the current optind
                        RFR_OPTIND=${OPTIND};

                        ## send an rndc reload to the server to make it active
                        . ${APP_ROOT}/lib/run_rndc_request.sh -s ${DNS_SLAVES[${D}]} -c reload -e;
                        RNDC_CODE=${?};

                        CNAME="$(basename "${0}")";
                        [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

                        ## and put it back
                        OPTIND=${RFR_OPTIND};

                        unset RFR_OPTIND;

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${DNS_SLAVES[${D}]} RNDC_CODE -> ${RNDC_CODE}";

                        if [ ${RNDC_CODE} -eq 0 ]
                        then
                            unset RNDC_CODE;
                            unset RETURN_CODE;
                            unset RET_CODE;
                            unset RETURN_TEXT;

                            ## reload successful. validate change.
                            ## sleep for the configured thread delay
                            ## to allow changes to process
                            sleep "${MESSAGE_DELAY}";

                            unset METHOD_NAME;
                            unset CNAME;

                            ## capture the current optind
                            RFR_OPTIND=${OPTIND};

                            ## ok, we know where it was failed over. we can use this information
                            ## to determine if the change was applied.
                            . ${APP_ROOT}/lib/runQuery.sh -s ${DNS_SLAVES[${D}]} -t A -u ${SITE_HOSTNAME} -o -e;

                            CNAME="$(basename "${0}")";
                            [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

                            ## and put it back
                            OPTIND=${RFR_OPTIND};

                            unset RFR_OPTIND;

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Query complete against ${DNS_SLAVES[${D}]}. Validating..";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing validate_change_request.sh failover ${TARGET}..";

                            unset METHOD_NAME;
                            unset CNAME;

                            ## validate the change
                            . ${APP_ROOT}/lib/validators/validate_change_request.sh failover ${TARGET};
                            VALIDATE_CODE=${?};

                            CNAME="$(basename "${0}")";
                            [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${DNS_SLAVES[${D}]} VALIDATE_CODE -> ${VALIDATE_CODE}";

                            if [ ${VALIDATE_CODE} -eq 0 ]
                            then
                                ## all set. validated successfully.
                                unset VALIDATE_CODE;
                                unset RETURN_CODE;

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change successfully validated on ${DNS_SLAVES[${D}]}.";
                            else
                                ## an error occurred during validation. either we
                                ## we dont have enough info to validate or the info
                                ## is wrong or it just hasnt taken effect yet.
                                unset VALIDATE_CODE;
                                unset RETURN_CODE;

                                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to validate new configuration on ${DNS_SLAVES[${D}]}.";

                                (( ERROR_COUNT += 1 ));
                                set -A FAILED_SERVERS ${FAILED_SERVERS[@]} ${DNS_SLAVES[${D}]};
                            fi
                        else
                            ## rndc request failed.
                            unset RNDC_CODE;
                            unset RETURN_CODE;
                            unset RET_CODE;
                            unset RETURN_TEXT;
                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to reload new configuration on ${DNS_SLAVES[${D}]}.";

                            (( ERROR_COUNT += 1 ));
                            set -A FAILED_SERVERS ${FAILED_SERVERS[@]} ${DNS_SLAVES[${D}]};
                        fi

                        ## increment d and unset the return code
                        (( D += 1 ));
                    done
                else
                    ## we have no slaves to operate against. return success since the servers we have
                    ## are done
                    RETURN_CODE=0;
                fi
            else
                ## failed to validate that the change was successfully implemented. either the
                ## reload failed or we didnt have the right/enough information to perform
                ## the validation
                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validation failed. Unable to confirm that site was failed over properly.";

                RETURN_CODE=61;
            fi
        else
            ## our server reload failed. throw an error, we can't recover from this here.
            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server reload on ${NAMED_MASTER} has failed. Unable to proceed.";
            RETURN_CODE=52;
        fi
    else
        ## failover process has failed. inform.
        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover processing on ${NAMED_MASTER} has failed. Unable to proceed.";

        RETURN_CODE=${FAILOVER_CODE};
    fi

    unset UNIT;
    unset FILENAME;
    unset TARGET;
    unset PRJCODE;
    unset CHG_CTRL;
    unset FAILOVER_CODE;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  runIntranetSiteFailover
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function runIntranetSiteFailover
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME->${SITE_HOSTNAME}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENABLE_POP->${ENABLE_POP}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DISABLE_POP->${DISABLE_POP}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";

    ## our request type is p <project code> or u <url>
    ## both can be run against the same script, as its
    ## a single-zone change
    ## spawn an ssh connection to the DNS master
    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command executeSiteFailover.sh -d x -b ${UNIT} -f ${FILENAME} -t ${TARGET} -p ${PRJCODE} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e";

        unset METHOD_NAME;
        unset CNAME;

        ## capture the current optind
        RFR_OPTIND=${OPTIND};

        FAILOVER_CODE=$(${APP_ROOT}/lib/executors/executeSiteFailover.sh -d i -f ${DISABLE_POP} -t ${ENABLE_POP} -c ${CHANGE_NUM} -i ${IUSER_AUDIT} -e);

        CNAME="$(basename "${0}")";
        [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

        ## and put it back
        OPTIND=${RFR_OPTIND};

        unset RFR_OPTIND;
    else
        for GD_SERVER in ${GD_SERVERS}
        do
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now validating server ${GD_SERVER}..";

            $(ping ${GD_SERVER} > /dev/null 2>&1);

            PING_RCODE=${?}

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

            if [ ${PING_RCODE} == 0 ]
            then
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${GD_SERVER} \"${REMOTE_APP_ROOT}/lib/executors/executeSiteFailover.sh -d i -f ${DISABLE_POP} -t ${ENABLE_POP} -c ${CHANGE_NUM} -i ${IUSER_AUDIT} -e\"";

                ${APP_ROOT}/lib/tcl/runSSHConnection.exp ${GD_SERVER} "${REMOTE_APP_ROOT}/lib/executors/executeSiteFailover.sh -d i -f ${DISABLE_POP} -t ${ENABLE_POP} -c ${CHANGE_NUM} -i ${IUSER_AUDIT} -e";
                FAILOVER_CODE=${?};

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FAILOVER_CODE -> ${FAILOVER_CODE}";

                ## rock out our validation here
                if [ ${FAILOVER_CODE} -eq 0 ]
                then
                    ## failover was successful. lets do some more work...
                    unset FAILOVER_CODE;
                    unset RETURN_CODE;

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover completed. Reloading server configuration..";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command run_rndc_request.sh -s ${NAMED_MASTER} -c reload -e..";

                    ## validate that it was indeed changed
                    unset CNAME;
                    unset METHOD_NAME;

                    ## TODO: work out validation here. probably retrieveServiceInfo -validate or some such

                    CNAME="$(basename "${0}")";
                    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RNDC_CODE -> ${RNDC_CODE}";

                    RETURN_CODE=0;
                else
                    ## failover process has failed. inform.
                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover processing on ${GD_SERVER} has failed. Unable to proceed.";

                    RETURN_CODE=${FAILOVER_CODE};
                fi
            else
                ## server access failed
                ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server appears unavailable -> GD_SERVER -> ${GD_SERVER}";

                unset PING_RCODE;
                continue;
            fi
        done
    fi

    unset UNIT;
    unset FILENAME;
    unset TARGET;
    unset PRJCODE;
    unset CHG_CTRL;
    unset FAILOVER_CODE;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  failover_bu
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function failover_bu
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET->${TARGET}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing failover for business unit..";

    ## we need to run a business unit failover
    ## call out to execute_bu_failover.sh
    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/lib/executors/execute_bu_failover.sh -b ${UNIT} -t ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e";

        unset METHOD_NAME;
        unset CNAME;

        ## capture the current optind
        RFR_OPTIND=${OPTIND};

        FAILOVER_CODE-$(${APP_ROOT}/lib/executors/execute_bu_failover.sh -b ${UNIT} -t ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e);

        CNAME="$(basename "${0}")";
        [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

        ## and put it back
        OPTIND=${RFR_OPTIND};

        unset RFR_OPTIND;
    else
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${NAMED_MASTER} \"${REMOTE_APP_ROOT}/lib/executors/execute_bu_failover.sh -b ${UNIT} -t ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e\"";

        ${APP_ROOT}/lib/tcl/runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/lib/executors/execute_bu_failover.sh -b ${UNIT} -t ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e";

        FAILOVER_CODE=${?};
    fi

    if [ ${FAILOVER_CODE} -eq 0 ]
    then
        ## failover was successful. lets do some more work...
        unset RETURN_CODE;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover completed. Reloading server configuration..";

        unset METHOD_NAME;
        unset CNAME;

        ## capture the current optind
        RFR_OPTIND=${OPTIND};

        ## send an rndc reload to the server to make it active
        . ${APP_ROOT}/lib/run_rndc_request.sh -s ${NAMED_MASTER} -c reload -e;
        RETURN_CODE=${?};

        ## and put it back
        OPTIND=${RFR_OPTIND};

        unset RFR_OPTIND;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";

        if [ ${RETURN_CODE} -eq 0 ]
        then
            ## we've successfully reloaded our configuration. move forward
            ## with a reload against our configured slaves.
            unset RETURN_CODE;

            ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "$(${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: ${UNIT}/${PRJCODE}/${FILENAME} - Change Request: ${CHG_CTRL} - Switched To: ${TARGET}");";

            ## sleep for the configured thread delay to allow processing changes in the master
            sleep "${MESSAGE_DELAY}";

            ## make sure that we have slaves to operate against
            if [ ${#DNS_SLAVES[@]} -ne 0 ]
            then
                ## we have slaves to process against. do so.
                ## make sure D is 0, ERROR_COUNT is 0
                D=0;
                ERROR_COUNT=0;

                while [ ${D} -ne ${#DNS_SLAVES[@]} ]
                do
                    ## temp unset
                    unset METHOD_NAME;
                    unset CNAME;

                    ## capture the current optind
                    RFR_OPTIND=${OPTIND};

                    ## send an rndc reload to the server to make it active
                    . ${APP_ROOT}/lib/run_rndc_request.sh -s ${DNS_SLAVE[${D}]} -c reload -e;
                    RETURN_CODE=${?};

                    ## and put it back
                    OPTIND=${RFR_OPTIND};

                    unset RFR_OPTIND;

                    if [ ${RETURN_CODE} -eq 0 ]
                    then
                        ## reloaded config on slave.
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully reloaded configuration on ${DNS_SLAVE[${D}]}";
                    else
                        ## rndc request failed.
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reload request on ${DNS_SLAVE[${D}]} has failed.";
                        set -A FAILED_SERVER ${FAILED_SERVER[@]} ${DNS_SLAVE[${D}]};

                        (( ERROR_COUNT += 1 ));
                    fi

                    ## increment d and unset the return code
                    (( D += 1 ));
                    unset RETURN_CODE;
                done

                if [ ${ERROR_COUNT} -eq 0 ]
                then
                    ## all servers were successfully reloaded. return 0
                    unset RETURN_CODE;
                    ERROR_COUNT=0;
                    D=0;

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully reloaded configuration on ${DNS_SLAVE[${D}]}";

                    RETURN_CODE=0;
                else
                    ## one or more of our slave servers failed to reload. this is fatal to that server, but not the entire process.
                    ## notify as such
                    FAILED_SERVERS=${FAILED_SERVER[@]};
                    RETURN_CODE=62;
                fi
            else
                ## we have no slaves to operate against. return success since the servers we have
                ## are done
                RETURN_CODE=0;
            fi
        else
            ## our server reload failed. throw an error, we can't recover from this here.
            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server reload on ${NAMED_MASTER} has failed. Unable to proceed.";
            RETURN_CODE=52;
        fi
    else
        ## failover process has failed. inform.
        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover processing on ${NAMED_MASTER} has failed. Unable to proceed.";

        RETURN_CODE=${FAILOVER_CODE};
    fi

    unset UNIT;
    unset TARGET;
    unset CHG_CTRL;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. Return code->${RETURN_CODE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  failover_project
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function failover_project
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRJCODE->${PRJCODE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET->${TARGET}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing failover for project code..";

    ## we need to run a business unit failover
    ## call out to execute_bu_failover.sh
    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/lib/executors/execute_project_failover.sh -b ${UNIT} -p ${PRJCODE} -t ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e";

        unset METHOD_NAME;
        unset CNAME;

        ## capture the current optind
        RFR_OPTIND=${OPTIND};

        FAILOVER_CODE=$(${APP_ROOT}/lib/executors/execute_project_failover.sh -b ${UNIT} -p ${PRJCODE} -t ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e);

        CNAME="$(basename "${0}")";
        [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

        ## and put it back
        OPTIND=${RFR_OPTIND};

        unset RFR_OPTIND;
    else
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${NAMED_MASTER} \"${REMOTE_APP_ROOT}/lib/executors/execute_project_failover.sh -b ${UNIT} -p ${PRJCODE} -t ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e\"";

        ${APP_ROOT}/lib/tcl/runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/lib/executors/execute_project_failover.sh -b ${UNIT} -p ${PRJCODE} -t ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e";
    fi

    if [ ${FAILOVER_CODE} -eq 0 ]
    then
        ## failover was successful. lets do some more work...
        unset RETURN_CODE;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover completed. Reloading server configuration..";

        unset METHOD_NAME;
        unset CNAME;

        ## capture the current optind
        RFR_OPTIND=${OPTIND};

        ## send an rndc reload to the server to make it active
        . ${APP_ROOT}/lib/run_rndc_request.sh -s ${NAMED_MASTER} -c reload -e;
        RETURN_CODE=${?};

        CNAME="$(basename "${0}")";
        [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

        ## and put it back
        OPTIND=${RFR_OPTIND};

        unset RFR_OPTIND;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";

        if [ ${RETURN_CODE} -eq 0 ]
        then
            ## we've successfully reloaded our configuration. move forward
            ## with a reload against our configured slaves.
            unset RETURN_CODE;

            ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "$(${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: ${UNIT}/${PRJCODE}/${FILENAME} - Change Request: ${CHG_CTRL} - Switched To: ${TARGET}");";

            ## sleep for the configured thread delay to allow processing changes in the master
            sleep "${MESSAGE_DELAY}";

            ## make sure that we have slaves to operate against
            if [ ${#DNS_SLAVES[@]} -ne 0 ]
            then
                ## we have slaves to process against. do so.
                ## make sure D is 0, ERROR_COUNT is 0
                D=0;
                ERROR_COUNT=0;

                while [ ${D} -ne ${#DNS_SLAVES[@]} ]
                do
                    ## temp unset
                    unset METHOD_NAME;
                    unset CNAME;

                    ## capture the current optind
                    RFR_OPTIND=${OPTIND};

                    ## send an rndc reload to the server to make it active
                    . ${APP_ROOT}/lib/run_rndc_request.sh -s ${DNS_SLAVE[${D}]} -c reload -e;
                    RETURN_CODE=${?};

                    CNAME="$(basename "${0}")";
                    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                    ## and put it back
                    OPTIND=${RFR_OPTIND};

                    if [ ${RETURN_CODE} -eq 0 ]
                    then
                        ## reloaded config on slave.
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully reloaded configuration on ${DNS_SLAVE[${D}]}";
                    else
                        ## rndc request failed.
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reload request on ${DNS_SLAVE[${D}]} has failed.";
                        set -A FAILED_SERVER ${FAILED_SERVER[@]} ${DNS_SLAVE[${D}]};

                        (( ERROR_COUNT += 1 ));
                    fi

                    ## increment d and unset the return code
                    (( D += 1 ));
                    unset RETURN_CODE;
                done

                if [ ${ERROR_COUNT} -eq 0 ]
                then
                    ## all servers were successfully reloaded. return 0
                    unset RETURN_CODE;
                    ERROR_COUNT=0;
                    D=0;

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully reloaded configuration on ${DNS_SLAVE[${D}]}";

                    RETURN_CODE=0;
                else
                    ## one or more of our slave servers failed to reload. this is fatal to that server, but not the entire process.
                    ## notify as such
                    FAILED_SERVERS=${FAILED_SERVER[@]};
                    RETURN_CODE=62;
                fi
            else
                ## we have no slaves to operate against. return success since the servers we have
                ## are done
                RETURN_CODE=0;
            fi
        else
            ## our server reload failed. throw an error, we can't recover from this here.
            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server reload on ${NAMED_MASTER} has failed. Unable to proceed.";
            RETURN_CODE=52;
        fi
    else
        ## failover process has failed. inform.
        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover processing on ${NAMED_MASTER} has failed. Unable to proceed.";

        RETURN_CODE=${FAILOVER_CODE};
    fi

    unset UNIT;
    unset TARGET;
    unset CHG_CTRL;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. Return code->${RETURN_CODE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  failover_datacenter
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function failover_datacenter
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET -> ${TARGET}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL -> ${CHG_CTRL}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";

    ## spawn an ssh connection to the DNS master
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing datacenter failover on ${NAMED_MASTER}..";

    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
    then
        unset METHOD_NAME;
        unset CNAME;

        ## capture the current optind
        RFR_OPTIND=${OPTIND};

        $(${APP_ROOT}/lib/executors/execute_dc_failover.sh -t ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e);

        CNAME="$(basename "${0}")";
        [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

        ## and put it back
        OPTIND=${RFR_OPTIND};

        unset RFR_OPTIND;
    else
        ${APP_ROOT}/lib/tcl/runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/lib/executors/execute_dc_failover.sh -t ${TARGET} -c ${CHG_CTRL} -i ${IUSER_AUDIT} -e";
    fi

    ## capture the return code
    FAILOVER_CODE=${?};

    if [ ${FAILOVER_CODE} -eq 0 ]
    then
        ## failover was successful. lets do some more work...
        unset RETURN_CODE;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover completed. Reloading server configuration..";

        unset METHOD_NAME;
        unset CNAME;

        ## capture the current optind
        RFR_OPTIND=${OPTIND};

        ## send an rndc reload to the server to make it active
        . ${APP_ROOT}/lib/run_rndc_request.sh -s ${NAMED_MASTER} -c reload -e;
        RETURN_CODE=${?};

        CNAME="$(basename "${0}")";
        [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

        ## and put it back
        OPTIND=${RFR_OPTIND};

        unset RFR_OPTIND;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";

        if [ ${RETURN_CODE} -eq 0 ]
        then
            ## we've successfully reloaded our configuration. move forward
            ## with a reload against our configured slaves.
            ## NOTE: we could run verification here. but we're failing over
            ## an entire datacenter, and that may take a LONG time depending
            ## on the number of zones.
            unset RETURN_CODE;

            ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "$(${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: ${UNIT}/${PRJCODE}/${FILENAME} - Change Request: ${CHG_CTRL} - Switched To: ${TARGET}");";

            ## sleep for the configured thread delay to allow processing changes in the master
            sleep "${MESSAGE_DELAY}";

            ## make sure that we have slaves to operate against
            if [ ${#DNS_SLAVES[@]} -ne 0 ]
            then
                ## we have slaves to process against. do so.
                ## make sure D is 0, ERROR_COUNT is 0
                D=0;
                ERROR_COUNT=0;

                while [ ${D} -ne ${#DNS_SLAVES[@]} ]
                do
                    ## temp unset
                    unset METHOD_NAME;
                    unset CNAME;

                    ## capture the current optind
                    RFR_OPTIND=${OPTIND};

                    ## send an rndc reload to the server to make it active
                    . ${APP_ROOT}/lib/run_rndc_request.sh -s ${DNS_SLAVE[${D}]} -c reload -e;
                    RETURN_CODE=${?};

                    CNAME="$(basename "${0}")";
                    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

                    ## and put it back
                    OPTIND=${RFR_OPTIND};

                    unset RFR_OPTIND;

                    if [ ${RETURN_CODE} -eq 0 ]
                    then
                        ## reloaded config on slave.
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully reloaded configuration on ${DNS_SLAVE[${D}]}";
                    else
                        ## rndc request failed.
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reload request on ${DNS_SLAVE[${D}]} has failed.";
                        set -A FAILED_SERVER ${FAILED_SERVER[@]} ${DNS_SLAVE[${D}]};

                        (( ERROR_COUNT += 1 ));
                    fi

                    ## increment d and unset the return code
                    (( D += 1 ));
                    unset RETURN_CODE;
                done

                if [ ${ERROR_COUNT} -eq 0 ]
                then
                    ## all servers were successfully reloaded. return 0
                    unset RETURN_CODE;
                    ERROR_COUNT=0;
                    D=0;

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Successfully reloaded configuration on ${DNS_SLAVE[${D}]}";

                    RETURN_CODE=0;
                else
                    ## one or more of our slave servers failed to reload. this is fatal to that server, but not the entire process.
                    ## notify as such
                    FAILED_SERVERS=${FAILED_SERVER[@]};
                    RETURN_CODE=62;
                fi
            else
                ## we have no slaves to operate against. return success since the servers we have
                ## are done
                RETURN_CODE=0;
            fi
        else
            ## our server reload failed. throw an error, we can't recover from this here.
            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server reload on ${NAMED_MASTER} has failed. Unable to proceed.";
            RETURN_CODE=52;
        fi
    else
        ## failover process has failed. inform.
        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover processing on ${NAMED_MASTER} has failed. Unable to proceed.";

        RETURN_CODE=${FAILOVER_CODE};
    fi

    unset TARGET;
    unset CHG_CTRL;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. Return code->${RETURN_CODE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Execute a DNS failover request on the master nameserver based on the provided information.";
    print "Usage: ${CNAME} [-s <request data>] [-b <request data>] [-p <request data>] [-d <request data>] [-e execute] [-?|-h show this help]";
    print "  -s      Process a site failover based on a comma-delimited information set";
    print "  -b      Process a business unit failover based on a comma-delimited information set";
    print "  -p      Process a project code failover based on a comma-delimited information set";
    print "  -d      Process a datacenter failover based on a comma-delimited information set";
    print "  -e      Execute processing";
    print "  -h|-?   Show this help";

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

unset METHOD_NAME;
unset CNAME;

## check security
. ${PLUGIN_ROOT_DIR}/lib/security/check_main.sh;
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Security configuration does not allow the requested action." && exit ${RET_CODE};

## unset the return code
unset RET_CODE;

CNAME="$(basename "${0}")";
METHOD_NAME="${CNAME}#startup";

while getopts ":r:s:b:p:d:eh:" OPTIONS
do
    case "${OPTIONS}" in
        r)
            ## set up the service partition. this determines where we go
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            typeset -l PARTITION="${OPTARG}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PARTITION->${PARTITION}";
            ;;
        s)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            FAILOVER_TYPE="site";

            case ${PARTITION} in
                ${INTRANET_TYPE_IDENTIFIER})
                    SITE_HOSTNAME=$(echo "${OPTARG}" | cut -d "," -f 1);
                    ENABLE_POP=$(echo "${OPTARG}" | cut -d "," -f 2);
                    DISABLE_POP=$(echo "${OPTARG}" | cut -d "," -f 3);
                    CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 6);
                    ;;
                ${INTERNET_TYPE_IDENTIFIER})
                    ## comma-delimited information set, lets strip the info
                    SITE_HOSTNAME=$(echo "${OPTARG}" | cut -d "," -f 1);
                    UNIT=$(echo "${OPTARG}" | cut -d "," -f 2);
                    FILENAME=$(echo "${OPTARG}" | cut -d "," -f 3);
                    TARGET=$(echo "${OPTARG}" | cut -d "," -f 4);
                    PRJCODE=$(echo "${OPTARG}" | cut -d "," -f 5);
                    CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 6);
                    IUSER_AUDIT=$(echo "${OPTARG}" | cut -d "," -f 7);
                    ;;
            esac

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FAILOVER_TYPE->${FAILOVER_TYPE}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILENAME->${FILENAME}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET->${TARGET}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRJCODE->${PRJCODE}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT->${IUSER_AUDIT}";
            ;;
        b)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            FAILOVER_TYPE="unit";

            ## comma-delimited information set, lets strip the info
            UNIT=$(echo "${OPTARG}" | cut -d "," -f 2);
            TARGET=$(echo "${OPTARG}" | cut -d "," -f 3);
            CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 4);
            IUSER_AUDIT=$(echo "${OPTARG}" | cut -d "," -f 5);

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FAILOVER_TYPE->${FAILOVER_TYPE}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET->${TARGET}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT->${IUSER_AUDIT}";
            ;;
        p)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            FAILOVER_TYPE="project";

            ## comma-delimited information set, lets strip the info
            PRJCODE=$(echo "${OPTARG}" | cut -d "," -f 2);
            UNIT=$(echo "${OPTARG}" | cut -d "," -f 3);
            TARGET=$(echo "${OPTARG}" | cut -d "," -f 4);
            CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 5);
            IUSER_AUDIT=$(echo "${OPTARG}" | cut -d "," -f 6);

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FAILOVER_TYPE->${FAILOVER_TYPE}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRJCODE->${PRJCODE}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UNIT->${UNIT}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET->${TARGET}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT->${IUSER_AUDIT}";
            ;;
        d)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting dataset..";

            FAILOVER_TYPE="datacenter";

            ## comma-delimited information set, lets strip the info
            TARGET=$(echo "${OPTARG}" | cut -d "," -f 1);
            CHG_CTRL=$(echo "${OPTARG}" | cut -d "," -f 2);
            IUSER_AUDIT=$(echo "${OPTARG}" | cut -d "," -f 3);

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FAILOVER_TYPE->${FAILOVER_TYPE}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET->${TARGET}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_CTRL->${CHG_CTRL}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT->${IUSER_AUDIT}";
            ;;
        e)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            case ${FAILOVER_TYPE} in
                site)
                    ## Make sure we have enough information to process
                    ## and execute
                    if [ -z "${UNIT}" ]
                    then
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No business unit was provided. Unable to continue processing.";

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=15;
                    elif [ -z "${FILENAME}" ]
                    then
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone filename was provided. Unable to continue processing.";

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=16;
                    elif [ -z "${TARGET}" ]
                    then
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target datacenter was provided. Unable to continue processing.";

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=17;
                    elif [ -z "${PRJCODE}" ]
                    then
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No project code was    provided. Unable to continue processing.";

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=18;
                    elif [ -z "${CHG_CTRL}" ]
                    then
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=19;
                    else
                        ## We have enough information to process the request, continue
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;

                        case ${PARTITION} in
                            ${INTRANET_TYPE_IDENTIFIER})
                                runIntranetSiteFailover;
                                ;;
                            ${INTERNET_TYPE_IDENTIFIER})
                                runInternetSiteFailover;
                                ;;
                        esac
                    fi
                    ;;
                unit)
                    ## Make sure we have enough information to process
                    ## and execute
                    if [ -z "${UNIT}" ]
                    then
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No business unit was provided. Unable to continue processing.";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=15;
                    elif [ -z "${TARGET}" ]
                    then
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target datacenter was provided. Unable to continue processing.";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=17;
                    elif [ -z "${CHG_CTRL}" ]
                    then
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset UNIT;
                        unset FILENAME;
                        unset TARGET;
                        unset PRJCODE;
                        unset CHG_CTRL;

                        RETURN_CODE=19;
                    else
                        ## We have enough information to process the request, continue
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing.";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        failover_bu;
                    fi
                    ;;
                datacenter)
                    ## Make sure we have enough information to process
                    ## and execute
                    if [ -z "${TARGET}" ]
                    then
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target datacenter was provided. Unable to continue processing.";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset TARGET;
                        unset CHG_CTRL;

                        RETURN_CODE=17;
                    elif [ -z "${CHG_CTRL}" ]
                    then
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        unset TARGET;
                        unset CHG_CTRL;

                        RETURN_CODE=19;
                    else
                        ## We have enough information to process the request, continue
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing.";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        unset FAILOVER_TYPE;
                        failover_datacenter;
                    fi
                    ;;
                *)
                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No information was provided. Unable to continue processing.";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    unset FAILOVER_TYPE;
                    RETURN_CODE=999;
                    ;;
            esac
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

return ${RETURN_CODE};
