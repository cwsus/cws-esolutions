#!/usr/bin/env ksh
#==============================================================================
#          FILE:  zoneUpdateUI.sh
#         USAGE:  ./zoneUpdateUI.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Allows processing of zone/business unit updates, such as
#                 add/remove or change records within a zone file or
#                 decommission/removal of a zone/business unit
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

unset RET_CODE;

trap '$(awk -F "=" "/\<system.trap.signals\>/{print $2}" ${SYSTEM_MESSAGES} | sed -e "s/^ *//g;s/ *$//g;/^ *#/d;s/#.*//" -e "s/%SIGNAL%/Ctrl-C/"); sleep ${MESSAGE_DELAY}; reset; clear; continue' 1 2 3
trap 'set +v; set +x' INT TERM EXIT;

#===  FUNCTION  ===============================================================
#
#         NAME:  main
#  DESCRIPTION:  Main entry point for application.
#   PARAMETERS:  None
#      RETURNS:  0
#
#==============================================================================
function main
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    if [ ! -z "${IS_DNS_RECORD_MOD_ENABLED}" ] && [ "${IS_DNS_RECORD_MOD_ENABLED}" = "${_FALSE}" ]
    then
        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS zone modification has not been enabled. Cannot continue.";

        awk -F "=" '/\<request.not.authorized\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

        sleep ${MESSAGE_DELAY}; reset; clear; /usr/bin/env -i ksh ${MAIN_CLASS};

        return 0;
    fi

    while true
    do
        reset; clear;

        printf "\n
            \t\t+-------------------------------------------------------------------+
            \t\t               $(awk -F "=" '/\<system.application.title\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//')
            \t\t+-------------------------------------------------------------------+
            \t\t$(awk -F "=" '/\<system.application.hostname\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//') ${SYSTEM_HOSTNAME}
            \t\t$(awk -F "=" '/\<system.application.uptime\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//') ${SYSTEM_UPTIME}
            \t\t$(awk -F "=" '/\<system.application.user\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//') ${IUSER_AUDIT}
            \t\t+-------------------------------------------------------------------+\n\n";

        awk -F "=" '/\<system.available.options\>/{print "\t" $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<update.zone.remove.record\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<update.zone.decom.zone\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<update.zone.decom.bu\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<update.zone.remove.zone\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<update.zone.remove.bu\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.select\>/{print $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        ## get the requested project code/url or business unit
        read MAINTENANCE_TYPE;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MAINTENANCE_TYPE -> ${MAINTENANCE_TYPE}";

        case ${MAINTENANCE_TYPE} in
            [Xx]|[Qq]|[Cc])
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                ## unset SVC_LIST, we dont need it now
                unset SVC_LIST;

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                sleep ${MESSAGE_DELAY}; reset; clear; /usr/bin/env -i ksh ${MAIN_CLASS};

                return 0;
                ;;
            1)
                ## remove a record from an existing zone
                ## not yet functional
                ## NOTE: this probably wont work well on dnssec-signed zones.. havent tried
                ## so not really sure. zone will at a minimum probably need to be re-signed.
                writeLogEntry writeLogEntry "AUDIT" "${METHOD_NAME}" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Function not currently available.";

                awk -F "=" '/\<system.function.not.available\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                ## unset SVC_LIST, we dont need it now
                unset MAINTENANCE_TYPE;

                ## exit this method and send to main
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
            2)
                ## decommission a zone
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone decommission requested. Processing..";

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                ## unset SVC_LIST, we dont need it now
                MAINTENANCE_TYPE="site_decom";

                ## exit this method and send to main
                retrieveSiteInfo;
                ;;
            3)
                ## decomssion a business unit
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Business Unit decommission requested. Processing..";

                awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                ## unset SVC_LIST, we dont need it now
                MAINTENANCE_TYPE="bu_decom";

                ## exit this method and send to main
                retrieveSiteInfo;
                ;;
            4)
                ## remove a zone
                ## must have already been decommissioned
                writeLogEntry writeLogEntry "AUDIT" "${METHOD_NAME}" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Function not currently available.";

                awk -F "=" '/\<system.function.not.available\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                ## unset SVC_LIST, we dont need it now
                unset MAINTENANCE_TYPE;

                ## exit this method and send to main
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
            5)
                ## remove a business unit
                ## must have already been decommissioned
                writeLogEntry writeLogEntry "AUDIT" "${METHOD_NAME}" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Function not currently available.";

                awk -F "=" '/\<system.function.not.available\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                ## unset SVC_LIST, we dont need it now
                unset MAINTENANCE_TYPE;

                ## exit this method and send to main
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  retrieveSiteInfo
#   DESCRIPTION:  Requests for information regarding the zone or business unit
#                 desired, and then performs a search against the configured
#                 master nameserver based on the information provided. Reacts
#                 according to response from search
#    PARAMETERS:  None
#       RETURNS:  None
#==============================================================================
function retrieveSiteInfo
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    reset; clear;

    ## get the request information
    while true
    do
        awk -F "=" '/\<failover.request.info\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<failover.pcode.format\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<failover.bu.format\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<failover.url.format\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        ## get the requested project code/url or business unit
        read SVC_LIST;

        case ${SVC_LIST} in
            [Xx]|[Qq]|[Cc])
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                ## unset SVC_LIST, we dont need it now
                unset SVC_LIST;
                unset MAINTENANCE_TYPE;

                ## exit this method and send to main
                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                ;;
            *)
                if [ -z "${SVC_LIST}" ]
                then
                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                    awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                else
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Read in SVC_LIST -> ${SVC_LIST}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

                    reset; clear;

                    awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    typeset THIS_CNAME=${CNAME};
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/validators/validate_service_request.sh -s ${SVC_LIST} -e;
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from call: ${RET_CODE}";

                    if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
                    then
                        [ ! -z "${RET_CODE}" ] && awk -F "=" "/${RET_CODE}/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                        [ -z "${RET_CODE}" ] && awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        ## unset variables
                        unset RET_CODE;
                        unset SVC_LIST;

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    else
                        SVC_REQUEST_TYPE=$(cut -d "," -f 1 <<< ${SVC_LIST});
                        SVC_REQUEST_OPTION=$(cut -d "," -f 2 <<< ${SVC_LIST});

                        ## unset this RET_CODE
                        unset RET_CODE;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${PLUGIN_LIB_DIRECTORY}/retrieveServiceInfo.sh ${INTERNET_TYPE_IDENTIFIER} ${SVC_LIST}";

                        typeset THIS_CNAME=${CNAME};
                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                        ## validate the input
                        ${PLUGIN_LIB_DIRECTORY}/retrieveServiceInfo.sh ${INTERNET_TYPE_IDENTIFIER} ${SVC_LIST};
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                        CNAME="${THIS_CNAME}";
                        typeset METHOD_NAME="${THIS_CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "retrieve_service executed..";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from call: ${RET_CODE}";

                        if [ ${RET_CODE} -eq 0 ]
                        then
                            ## we have all the data we need, do the failover
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Information for ${SVC_LIST} obtained..";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_DETAIL -> ${SERVICE_DETAIL[*]}";

                            ## unset this RET_CODE
                            unset RET_CODE;
                            unset SVC_LIST;

                            reset; clear;

                            ## write out the data retrieved so the user
                            ## can make an informed decision
                            if [ ${#SERVICE_DETAIL[*]} -gt ${LIST_DISPLAY_MAX} ]
                            then
                                while true
                                do
                                    awk -F "=" '/\<system.list.available\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                    while [ ${A} -ne ${LIST_DISPLAY_MAX} ]
                                    do
                                        if [ ! ${B} -eq ${#SERVICE_DETAIL[*]} ]
                                        then
                                            ## prints the following:
                                            ## 0 - db.example.XMPL - Live in: PH - Site URL: example.com
                                            ## 7-8 for prd
                                            printf "${B} - $(cut -d "|" -f 1 <<< "${SERVICE_DETAIL[${B}]}" | cut -d "/" -f 7-8) - Live in: $(cut -d "|" -f 2 <<< "${SERVICE_DETAIL[${B}]}") - Site URL: $(cut -d "|" -f 3 <<< "${SERVICE_DETAIL[${B}]}")";

                                            (( A += 1 ));
                                            (( B += 1 ));
                                        else
                                            B=${#SERVICE_DETAIL[*]};
                                            A=${LIST_DISPLAY_MAX};
                                        fi
                                    done

                                    if [ $(( ${B} - ${LIST_DISPLAY_MAX} )) -eq 0 ]
                                    then
                                        awk -F "=" '/\<system.display.next\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                                    else
                                        awk -F "=" '/\<system.display.prev\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                                        awk -F "=" '/\<system.display.next\>/{print $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                                    fi

                                    ## add the option to run against all sites listed
                                    awk -F "=" '/\<system.display.next\>/{print "A - " $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%SITE%/all sites for ${SVC_REQUEST_OPTION}/";
                                    awk -F "=" '/\<system.option.cancel\>/{print "C - " $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                                    awk -F "=" '/\<system.option.select\>/{print $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                    read SELECTION;

                                    case ${SELECTION} in
                                        [Nn])
                                            clear;
                                            unset SELECTION;

                                            if [ ${B} -ge ${#SERVICE_DETAIL[*]} ]
                                            then
                                                awk -F "=" '/\<forward.shift.failed\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                                A=0;
                                                B=0;

                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            else
                                                A=0;
                                                continue;
                                            fi
                                            ;;
                                        [Pp])
                                            clear;
                                            unset SELECTION;

                                            if [ $(( ${B} - ${LIST_DISPLAY_MAX} )) -eq 0 ]
                                            then
                                                awk -F "=" '/\<previous.shift.failed\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                                A=0; B=0;

                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            else
                                                A=0;

                                                (( B -= (( ${LIST_DISPLAY_MAX} * 2 )) ));

                                                continue;
                                            fi
                                            ;;
                                        [0-${B}]*)
                                            ## make b 0 again
                                            A=0;
                                            B=0;
                                            reset; clear;

                                            case ${MAINTENANCE_TYPE} in
                                                bu_decom|site_decom)
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    processDecomRequest;
                                                    ;;
                                                bu_removal|site_removal)
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    process_removal_request;
                                                    ;;
                                                remove_entry)
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    remove_zone_entry
                                                    ;;
                                            esac
                                            ;;
                                        [Xx]|[Qq]|[Cc])
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";
                                            ## make b 0 again
                                            A=0; B=0;

                                            awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                            ;;
                                        *)
                                            clear;
                                            A=0; B=0;

                                            unset SELECTION;

                                            writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                            awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            ;;
                                    esac
                                done
                            else
                                while true
                                do
                                    awk -F "=" '/\<system.list.available\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                    while [ ${A} -ne ${#SERVICE_DETAIL[*]} ]
                                    do
                                        ## prints the following:
                                        ## 0 - db.example.XMPL - Live in: PH - Site URL: example.com
                                        ## 7-8 for prd
                                        printf "${A} - $(printf "${SERVICE_DETAIL[${A}]}" | cut -d "|" -f 1 | cut -d "/" -f 7-8) - Live in: $(printf "${SERVICE_DETAIL[${A}]}" | cut -d "|" -f 2) - Site URL: $(printf "${SERVICE_DETAIL[${A}]}" | cut -d "|" -f 3)";

                                        (( A += 1 ));
                                    done

                                    ## add the option to run against all sites listed
                                    [ ${#SERVICE_DETAIL[*]} -ge 1 ] && awk -F "=" '/\<system.display.next\>/{print "\tA - " $2}' | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//' -e "s/%SITE%/all sites for ${SVC_REQUEST_OPTION}/";
                                    awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                                    awk -F "=" '/\<system.option.select\>/{print $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                    read SELECTION;

                                    case ${SELECTION} in
                                        [0-${A}]|[Aa]*)
                                            reset; clear;

                                            A=0; B=0;

                                            ## TODO: make this call the appropriate method
                                            case ${MAINTENANCE_TYPE} in
                                                bu_decom|site_decom)
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    processDecomRequest;
                                                    ;;
                                                bu_removal|site_removal)
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                    process_removal_request;
                                                    ;;
                                                remove_entry)
                                                    ## remove is not a valid option when selecting ALL sites
                                                    ## check to be sure
                                                    if [[ ${SELECTION} == [Aa] ]]
                                                    then
                                                        ## invalid selection
                                                        unset SELECTION;

                                                        clear;

                                                        A=0; B=0;

                                                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                                        awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                    else
                                                        ## ok keep going
                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection process complete. Sending to process_removal_request..";
                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                        remove_zone_entry;
                                                    fi
                                                    ;;
                                            esac
                                            ;;
                                        [Xx]|[Qq]|[Cc])
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";

                                            awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                            A=0; B=0;

                                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                            ;;
                                        *)
                                            unset SELECTION;

                                            clear;

                                            A=0; B=0;

                                            writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                            awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            ;;
                                    esac
                                done
                            fi
                        else
                            ## result code was non-zero from retrieve_service_info
                            ## return message to user
                            writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from retrieve_service->${RET_CODE}";

                            [ ! -z "${RET_CODE}" ] && awk -F "=" "/${RET_CODE}/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                            [ ! -z "${RET_CODE}" ] && awk -F "=" "/\<99\>/{print $2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            ## unset our variables
                            unset SVC_LIST;
                            unset RET_CODE;
                            unset BU;
                            unset FNAME;
                            unset PCODE;
                            unset TDC;
                            unset CHANGE_CONTROL;
                            unset SVC_REQUEST_TYPE;
                            unset SVC_REQUEST_OPTION;

                            ## reload
                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        fi
                    fi
                fi
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  processDecomRequest
#   DESCRIPTION:  Processes a decommission request using the information
#                 previously provided. Determines, based on flags, if the decom
#                 should be performed for a single zone or an entire business
#                 unit. Executes first on the configured master, if successful,
#                 executes on configured slaves (if any)
#    PARAMETERS:  None
#       RETURNS:  None
#==============================================================================
function processDecomRequest
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    reset; clear;

    while true
    do
        awk -F "=" '/\<confirm.request\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        read CONFIRM;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONFIRM -> ${CONFIRM}";

        reset; clear;

        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        case ${CONFIRM} in
            [Yy][Ee][Ss]|[Yy])
                ## unset confirmation
                unset CONFIRM;

                ## we need to process the actual failover here for the requested
                ## site. this is going to require the following information:
                ##
                ## 1. the site
                ## 2. the current datacenter (so we know where to go)
                ## 3. the change/ticket number
                ## 4. the logged in user-name
                ##
                ## all of these will be used to make the actual change,
                ## while 3/4 are also used for auditing
                while true
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting change information..";

                    typeset THIS_CNAME=${CNAME};
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                    "${APP_ROOT}/${BIN_DIRECTORY}"/obtainChangeControl.sh;

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";

                    if [[ ! -z "${CANCEL_REQ}" && "${CANCEL_REQ}" = "${_TRUE}" ]]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                        awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        ## unset SVC_LIST, we dont need it now
                        unset SVC_LIST;

                        ## terminate this thread and return control to main
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;

                        sleep ${MESSAGE_DELAY}; reset; clear; main;
                    fi

                    break;
                done

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting up required flags..";

                if [[ "${SELECTION}" = "A" || "${SELECTION}" = "a" ]]
                then
                    ## user has selected all zones that were returned. this should all be a single business unit, so we process a BU decom
                    ## TODO: call the right runner
                    unset DC_MISMATCH;
                    unset SVC_REQUEST_OPTION;
                    unset SVC_REQUEST_TYPE;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BU -> ${BU}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TDC -> ${TDC}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_CONTROL -> ${CHANGE_CONTROL}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command run_decom.sh -b $(printf "${SERVICE_DETAIL[${B}]}" | cut -d "|" -f 3),${BU},${CHANGE_CONTROL},${IUSER_AUDIT} -e";

                    typeset THIS_CNAME=${CNAME};
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/run_decom.sh -b ${BU},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";
                else
                    ## user has selected a single zone. call out to the right runner to perform the work
                    BU=$(cut -d "/" -f 6 <<< ${SERVICE_DETAIL[${SELECTION}]} | cut -d "_" -f 3);
                    PCODE=$(cut -d "/" -f 7 <<< ${SERVICE_DETAIL[${SELECTION}]} | cut -d "|" -f 1 | cut -d "." -f 3);
                    ZNAME=$(cut -d "|" -f 3 <<< ${SERVICE_DETAIL[${SELECTION}]})

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BU -> ${BU}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FNAME -> ${FNAME}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PCODE -> ${PCODE}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TDC -> ${TDC}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_CONTROL -> ${CHANGE_CONTROL}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command run_decom.sh -s ${BU},${PCODE},${ZNAME},${CHANGE_CONTROL},${IUSER_AUDIT} -e";

                    typeset THIS_CNAME=${CNAME};
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

                    ## validate the input
                    ${PLUGIN_LIB_DIRECTORY}/run_decom.sh -s ${BU},${PCODE},${ZNAME},${CHANGE_CONTROL},${IUSER_AUDIT} -e;
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
                    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;

                    CNAME="${THIS_CNAME}";
                    typeset METHOD_NAME="${THIS_CNAME}#${0}";
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "run_decom executed..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Result code from call: ${RET_CODE}";

                if [ ${RET_CODE} -eq 0 ]
                then
                    ## ok, our master went through just fine, loop through and process slaves (if any)
                    if [[ ! -z "${ERROR_COUNT}" && ${ERROR_COUNT} -ne 0 ]]
                    then
                        ## one or more slave services failed processing
                        if [ ! -z "${FAILED_SERVERS}" ]
                        then
                            awk -F "=" '/\<failover.request.servers.success.failure\>/{print $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            ## make sure d is zero
                            D=0;

                            while [ ${D} -ne ${#FAILED_SERVERS[*]} ]
                            do
                                printf "${FAILED_SERVERS[${D}]}\n";

                                (( D += 1 ));
                            done

                            ## make d zero again
                            D=0;
                        fi

                        sleep "${MESSAGE_DELAY}";
                    fi

                    ## we're all set here. audit log it
                    if [[ "${SELECTION}" = "A" || "${SELECTION}" = "a" ]]
                    then
                        writeLogEntry "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: All sites in ${SVC_REQUEST_OPTION} - Change Request: ${CHANGE_CONTROL} - Switched To: ${TDC}";
                    else
                        writeLogEntry "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: $(cut -d "/" -f 9 <<< ${SERVICE_DETAIL[${SELECTION}]}) - Change Request: ${CHANGE_CONTROL} - Switched To: ${TDC}";
                    fi

                    ## unset variables
                    unset SVC_REQUEST_OPTION;
                    unset SVC_REQUEST_TYPE;
                    unset SVC_LIST;
                    unset SELECTION;
                    unset CONFIRM;
                    unset DC_MISMATCH;
                    unset BU;
                    unset FNAME;
                    unset PCODE;
                    unset RET_CODE;
                    unset TDC;
                    unset CHANGE_CONTROL;
                    unset RETURN_CODE;
                    unset ZNAME;

                    ## TODO: put in slave processing here
                    ## we've finished our processing. site(s) requested have been failed over,
                    ## reloaded and verified. what should we do now ?
                    while true
                    do
                        reset; clear;

                        awk -F "=" '/\<system.process.successful\>/{print "\t" $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                        awk -F "=" '/\<system.process.perform.another\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        read RESPONSE;
                        awk -F "=" '/\<system.pending.message\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                        case ${RESPONSE} in
                            [Yy][Ee][Ss]|[Yy])
                                ## user has elected to perform further failovers. restart the process
                                unset RESPONSE;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Transferring control back to main..";

                                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                ;;
                            *)
                                ## user does not wish to process further failovers. let's exit out and open up the main class.
                                unset RESPONSE;

                                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                ;;
                        esac
                    done
                else
                    ## caught an error, log it out and
                    ## show it
                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Caught an error performing the failover. RET_CODE -> ${RET_CODE}";

                    [ -z "${RET_CODE}" ] && awk -F "=" '/\<99\>/{print $2}' ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
                    [ ! -z "${RET_CODE}" ] && awk -F "=" "/\<\${RET_CODE}\>/{print \$2}" ${PLUGIN_ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                    unset CHANGE_CONTROL;
                    unset RET_CODE;
                    unset SELECTION;
                    unset CONFIRM;
                    unset DC_MISMATCH;
                    unset BU;
                    unset FNAME;
                    unset PCODE;
                    unset SVC_LIST;
                    unset RETURN_CODE;
                    unset ZNAME;

                    ## break out
                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                fi
                ;;
            [Nn][Oo]|[Nn])
                unset CONFIRM;
                unset SELECTION;
                unset SVC_REQUEST_OPTION;
                unset SVC_REQUEST_TYPE;

                ## user opted to cancel
                ## we leave the in-use flag in place
                ## because we aren't starting over
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover request has been cancelled.";

                awk -F "=" '/\<system.request.canceled\>/{print $2}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
            *)
                ## user did not provide a yes/no answer
                unset CONFIRM;
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selection provided is invalid";

                awk -F "=" '/\<selection.invalid\>/{print $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  remove_zone_entry
#   DESCRIPTION:  Removes an entry from an existing zone
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function removeZoneEntry
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

    return 0;
}

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

main;

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

return 0;
