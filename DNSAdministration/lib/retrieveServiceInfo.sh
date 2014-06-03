#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  retrieveServiceInfo.sh
#         USAGE:  ./retrieveServiceInfo.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Obtains service information for the provided criteria
#                 from the zone file.
#
#       OPTIONS:  ---
#  REQUIREMENTS:  ---
#          BUGS:  ---
#         NOTES:  ---
#        AUTHOR:  Kevin Huntly <kmhuntly@gmail.com
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

[ ${#} -eq 0 ] && usage;

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
#          NAME:  obtainInternetService
#   DESCRIPTION:  Executes the necessary request against the primary DNS server
#                 to further the process of a failover request.
#    PARAMETERS:  None
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function obtainInternetService
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting request detail..";

    ## configure the request indicators
    REQUEST_TYPE=$(echo ${1} | cut -d "," -f 1);
    REQUEST_OPTION=$(echo ${1} | cut -d "," -f 2);

    if [ ! -z "${SERVICE_DETAIL}" ]
    then
        set -A SERVICE_DETAIL;
        unset SERVICE_DETAIL;
    fi

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_TYPE set to ${REQUEST_TYPE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_OPTION set to ${REQUEST_OPTION}";

    ## If we were invoked with verbosity turned on, carry it through
    if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${PLUGIN_ROOT_DIR}lib/executors/executeDataRetrieval.sh -${REQUEST_TYPE} ${REQUEST_OPTION} -e";

        set -A SERVICE_DETAIL $(${PLUGIN_ROOT_DIR}lib/executors/executeDataRetrieval.sh -${REQUEST_TYPE} ${REQUEST_OPTION} -e);
        RET_CODE=${?};
    else
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMED_MASTER} \"executeDataRetrieval.sh -${REQUEST_TYPE} ${REQUEST_OPTION} -e\"";

        set -A SERVICE_DETAIL $(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${NAMED_MASTER} "${REMOTE_APP_ROOT}/${LIB_DIRECTORY}/executors/executeDataRetrieval.sh -${REQUEST_TYPE} ${REQUEST_OPTION} -e");
    fi

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_DETAIL->${SERVICE_DETAIL[@]}";

    if [ ! -z ${SERVICE_DETAIL[@]} ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Command executed successfully on ${NAMED_MASTER}.";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing data..";

        ## temporarily unset stuff
        unset METHOD_NAME;
        unset CNAME;

        if [ ! -z "${2}" ] && [ "${2}" = "chk-info" ]
        then
            ## no further processing is required. stop and return
            RETURN_CODE=0;
        else
            ## we have the data we require, so we're going to exit out here
            ## and it'll be returned to the f/e
            if [ ${JAVA_RUNNABLE} ]
            then
                echo ${SERVICE_DETAIL[@]};
            fi

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=0;
        fi
    else
        unset SERVICE_DETAIL;
        unset REQUEST_TYPE;
        unset REQUEST_OPTION;

        ## an error occurred, so lets re-throw the
        ## error code
        RETURN_CODE=12;
    fi

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  obtainIntranetService
#   DESCRIPTION:  Executes the necessary request against the primary DNS server
#                 to further the process of a failover request.
#    PARAMETERS:  None
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function obtainIntranetService
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    SITE_VHOST_NAME=${1};

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_VHOST_NAME -> ${SITE_VHOST_NAME}";
    
    if [ ! -z "${SITE_VHOST_NAME}" ]
    then
        ## we have a domain name, lets go fetch its info
        if [[ ! -z "${LOCAL_EXECUTION}" && "${LOCAL_EXECUTION}" = "${_TRUE}" ]]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command gdctl -k .."; 

            $(gdctl -k | sed -e "s/^M//g" >${PLUGIN_ROOT_DIR}/${GD_CONFIG_FILE});
        else
            for GD_SERVER in ${GD_SERVERS}
            do
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now validating proxy ${GD_SERVER}..";

                $(ping ${GD_SERVER} > /dev/null 2>&1);

                PING_RCODE=${?}

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                if [ ${PING_RCODE} == 0 ]
                then
                    ## stop if its available and run the command
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server availability confirmed. GD_SERVER -> ${GD_SERVER}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runSSHConnection.exp ${GD_SERVER} \"gdctl -k\" > ${PLUGIN_ROOT_DIR}/${GD_CONFIG_FILE}";

                    $(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${GD_SERVER} "gdctl -k" | sed -e "s/^M//g" > ${PLUGIN_ROOT_DIR}/${GD_CONFIG_FILE});

                    break;
                else
                    ## first one wasnt available, check the remaining
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Server appears unavailable -> GD_SERVER -> ${GD_SERVER}";

                    unset PING_RCODE;
                    continue;
                fi
            done
        fi

        ## command executed, verify file exists
        if [ -s ${PLUGIN_ROOT_DIR}/${GD_CONFIG_FILE} ]
        then
            ## xlnt, we have the data file. get the info we need
            VHOST_LINE_NUMBER=$(sed -n "/${SITE_VHOST_NAME}/=" ${PLUGIN_ROOT_DIR}/${GD_CONFIG_FILE} | head -1);

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VHOST_LINE_NUMBER -> ${VHOST_LINE_NUMBER}";

            if [ ! -z "${VHOST_LINE_NUMBER}" ]
            then
                START_LINE_NUMBER=$((${VHOST_LINE_NUMBER}-2));
                END_LINE_NUMBER=$((${VHOST_LINE_NUMBER}+19));
                POP_NAMES=$(sed -n -e "${START_LINE_NUMBER},${END_LINE_NUMBER}p" ${PLUGIN_ROOT_DIR}/${GD_CONFIG_FILE} \ |
                    grep "${GD_POP_IDENTIFIER}" | cut -d "=" -f 2 | sed -e "s/^ *//g" -e "s/;//g");

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "VHOST_LINE_NUMBER -> ${VHOST_LINE_NUMBER}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "START_LINE_NUMBER -> ${START_LINE_NUMBER}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "END_LINE_NUMBER -> ${END_LINE_NUMBER}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POP_NAMES -> ${POP_NAMES}";

                if [ ! -z "${POP_NAMES}" ]
                then
                    ## nifty, we have a pop list. we can continue forward
                    for POP_NAME in ${POP_NAMES}
                    do
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POP_NAME -> ${POP_NAME}";

                        POPS_LINE_NUMBER=$(grep -n "label = ${POP_NAME}" ${PLUGIN_ROOT_DIR}/${GD_CONFIG_FILE} | cut -d ":" -f 1);
                        POPE_LINE_NUMBER=$((${POPS_LINE_NUMBER}+1))
                        set -A POP_STATUS ${POP_STATUS[@]} $(echo "${POP_NAME}|$(sed -n -e "${POPE_LINE_NUMBER}p" \
                            ${PLUGIN_ROOT_DIR}/${GD_CONFIG_FILE} | cut -d "=" -f 2 | sed -e "s/^ *//g" -e "s/;//g")")

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POPS_LINE_NUMBER -> ${POPS_LINE_NUMBER}";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POPE_LINE_NUMBER -> ${POPE_LINE_NUMBER}";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "POP_STATUS -> ${POP_STATUS[@]}";
                    done

                    if [ ! -z ${POP_STATUS[@]} ]
                    then
                        for STATUS in ${POP_STATUS[@]}
                        do
                            if [ "$(echo ${STATUS} | cut -d "|" -f 2)" = "no" ]
                            then
                                ENABLE_POP=$(echo ${STATUS} | cut -d "|" -f 1);
                            fi

                            if [ "$(echo ${STATUS} | cut -d "|" -f 2)" = "yes" ]
                            then
                                DISABLE_POP=$(echo ${STATUS} | cut -d "|" -f 1);
                            fi
                        done

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENABLE_POP -> ${ENABLE_POP}";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DISABLE_POP -> ${DISABLE_POP}";

                        RETURN_CODE=0;
                    else
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to determine current POP status. Cannot continue.";

                        RETURN_CODE=28;
                    fi
                else
                    ## no pop names were returned
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No POP names were returned for the provided name. Cannot continue.";

                    RETURN_CODE=28;
                fi
            else
                ## no information was found in the current config with the name provided
                ## no pop names were returned, we cant failover the site
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to determine current POP status. Cannot continue.";

                RETURN_CODE=12;
            fi
        else
            ## failed to get the config file, cant continue
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to generate configuration file. Cannot continue.";

            RETURN_CODE=14;
        fi
    else
        ## no site name to do anything with
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No site hostname was provided. Cannot continue.";

        RETURN_CODE=29;
    fi

    unset POP_NAMES;
    unset END_LINE_NUMBER;
    unset START_LINE_NUMBER;
    unset VHOST_LINE_NUMBER;
    unset PING_RCODE;
    unset GD_SERVER;
    unset SITE_VHOST_NAME;
    unset POPS_LINE_NUMBER;
    unset POPE_LINE_NUMBER;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#   RETURNS:  0
#==============================================================================
function usage
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Retrieve service information for provided values";
    print "Usage: ${CNAME} [ service type ] [ service values ]";
    print " Service type should be one of X or I - X for internet, I for intranet";
    print " Service values must be a comma-delimited set of information to return data against.";
    print "  -h|-?   Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

case ${1} in
    ${INTERNET_TYPE_IDENTIFIER})
        obtainInternetService ${2};
        ;;
    ${INTRANET_TYPE_IDENTIFIER})
        obtainIntranetService ${2};
        ;;
    *)
        RETURN_CODE=7;
        ;;
esac

return ${RETURN_CODE};
