#!/usr/bin/env ksh
#==============================================================================
#          FILE:  certManagementUI.sh
#         USAGE:  ./certManagementUI.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Processes a DNS failover by using information previously
#                 obtained by retrieve_site_info.sh
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
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
typeset SCRIPT_ROOT="$(/usr/bin/env dirname "${SCRIPT_ABSOLUTE_PATH}")";
typeset typeset METHOD_NAME="${CNAME}#startup";

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
"${APP_ROOT}"/"${LIB_DIRECTORY}"/validateSecurityAccess.sh -a;
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

function main
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";

    if [ ! -z "${IS_WEB_BUILD_ENABLED}" ] && [ "${IS_WEB_BUILD_ENABLED}" != "${_TRUE}" ]
    then
        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate management has not been enabled. Cannot continue.";

        awk -F "=" '/\<request.not.authorized\>/{print "\t" $2}' ${ERROR_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
        [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

        sleep ${MESSAGE_DELAY}; reset; clear; /usr/bin/env -i ksh ${MAIN_CLASS};

        return 0;
    fi

    ## get the request information
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

        awk -F "=" '/\<cert.mgmt.create.new.cert\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<cert.mgmt.renew.cert\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<cert.mgmt.exclude.cert\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<cert.mgmt.run.adhoc.cert\>/{print "\t" $2}' ${PLUGIN_SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.cancel\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';
        awk -F "=" '/\<system.option.select\>/{print $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

        ## get the requested project code/url or business unit
        read REQUEST_OPTION;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_OPTION -> ${REQUEST_OPTION}";

        case ${REQUEST_OPTION} in
            [Xx]|[Qq]|[Cc])
                unset REQUEST_OPTION;
                unset SITE_HOSTNAME;
                unset WEB_PROJECT_CODE;
                unset PLATFORM_CODE;
                unset MASTER_WEBSERVER;
                unset ENVIRONMENT_TYPE;
                unset SERVER_ROOT;
                unset CONTACT_CODE;
                unset OWNER_DIST;
                unset INSTANCE_NAME;
                unset CERTDB;
                unset RET_CODE;
                unset ACTIVE_DATACENTER;
                unset PRI_PLATFORM_CODE;
                unset SEC_PLATFORM_CODE;
                unset RETURN_CODE;
                unset REQ_DATACENTER;
                unset PLATFORM;
                unset MGMT_OP;
                unset CONTACT_NUMBER;
                unset RESPONSE;
                unset INPUT;
                unset CHANGE_NUM;
                unset PROCESS_DATE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                awk -F "=" '/\<system.request.canceled\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                ## unset SVC_LIST, we dont need it now
                unset SVC_LIST;

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                sleep "${MESSAGE_DELAY}"; reset; clear;

                sleep ${MESSAGE_DELAY}; reset; clear; /usr/bin/env -i ksh ${MAIN_CLASS};

                return 0;
                ;;
            1)
                ## request to create a new certificate for an existing site
                ## this would be for a new cert, most commonly a new site too
                printf "not yet implemented";
                reset; clear; continue;
                ;;
            2)
                ## cert renewal. we need to know if we're just getting started or if we already have data.
                reset; clear;

                while true
                do
                    printf "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    printf "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    printf "\t$(grep -w cert.mgmt.renew.provide.site "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                    read SITE_HOSTNAME;

                    typeset -l SITE_HOSTNAME;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME -> ${SITE_HOSTNAME}";

                    reset; clear;

                    printf "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                    case ${SITE_HOSTNAME} in
                        [Xx]|[Qq]|[Cc])
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process aborted";

                            awk -F "=" '/\<system.request.canceled\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            ## unset SVC_LIST, we dont need it now
                            unset SITE_HOSTNAME;

                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                            ;;
                        *)
                            if [ ! -z "${SITE_HOSTNAME}" ]
                            then
                                ## validate that the hostname provided exists in website_defs. if it
                                ## doesnt, its not supported with this utility
                                if [ $(getWebInfo | grep -w ${SITE_HOSTNAME} | wc -l) != 0 ]
                                then
                                    ## we've been provided a hostname, lets get the data from esupport
                                    ## this sets up the project code
                                    ## first, the stuff out of URL_Defs
                                    WEB_PROJECT_CODE=$(getWebInfo | grep -w ${SITE_HOSTNAME} | grep -v "#" | \
                                        cut -d "|" -f 1 | cut -d ":" -f 2 | sort | uniq); ## get the webcode
                                    PLATFORM_CODE=$(getWebInfo | grep -w ${SITE_HOSTNAME} | grep -v "#" | \
                                        cut -d "|" -f 2 | sort | uniq | tr "[\n]" "[ ]"); ## get the platform code, if multiples spit with space
                                    MASTER_WEBSERVER=$(getPlatformInfo | grep -w $(printf ${PLATFORM_CODE} | awk '{print $1}') | \
                                        grep -v "#" | cut -d "|" -f 5 | sort | uniq | sed -e "s/,/ /g" | awk '{print $1}');
                                    [ -z "$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep -w ${SITE_HOSTNAME} | \
                                        cut -d "|" -f 10 | sort | uniq | grep enterprise)" ] \
                                            && WEBSERVER_PLATFORM=${IHS_TYPE_IDENTIFIER} \
                                            || WEBSERVER_PLATFORM=${IPLANET_TYPE_IDENTIFIER};
                                    ENVIRONMENT_TYPE=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep "${SITE_HOSTNAME}" | \
                                        cut -d "|" -f 3 | sort | uniq); ## the environment type (dev, ist etc) TODO: fix this cut, it isnt right
                                    SERVER_ROOT=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep -w ${SITE_HOSTNAME} | \
                                        cut -d "|" -f 10 | cut -d "/" -f 1-3 | sort | uniq); ## web instance name
                                    CONTACT_CODE=$(getWebInfo | grep -w ${SITE_HOSTNAME} | grep -v "#" | \
                                        cut -d "|" -f 14 | cut -d ":" -f 2 | sort | uniq); ## get the contact code
                                    OWNER_DIST=$(getContactInfo | grep -w ${CONTACT_CODE} | grep -v "#" | \
                                        cut -d "|" -f 7 | sort | uniq); ## get the contact dist list

                                    ## make sure we have a valid and supported platform
                                    if [ "${WEBSERVER_PLATFORM}" != "${IPLANET_TYPE_IDENTIFIER}" ] \
                                        && [ "${WEBSERVER_PLATFORM}" != "${IHS_TYPE_IDENTIFIER}" ]
                                    then
                                        ## unsupported platform
                                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unsupported platform detected - Renewal process aborted";

                                        printf "$(grep -w unsupported.platform.detected "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                        ## unset SVC_LIST, we dont need it now
                                        unset REQUEST_OPTION;
                                        unset SITE_HOSTNAME;
                                        unset WEB_PROJECT_CODE;
                                        unset PLATFORM_CODE;
                                        unset MASTER_WEBSERVER;
                                        unset ENVIRONMENT_TYPE;
                                        unset SERVER_ROOT;
                                        unset CONTACT_CODE;
                                        unset OWNER_DIST;

                                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                    else
                                        if [ "${WEBSERVER_PLATFORM}" = "${IPLANET_TYPE_IDENTIFIER}" ]
                                        then
                                            INSTANCE_NAME=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep -w ${SITE_HOSTNAME} | \
                                                cut -d "|" -f 10 | cut -d "/" -f 4 | sort | uniq); ## web instance name
                                        elif [ "${WEBSERVER_PLATFORM}" = "${IHS_TYPE_IDENTIFIER}" ]
                                        then
                                            INSTANCE_NAME=$(getWebInfo | grep -w ${WEB_PROJECT_CODE} | grep -v "#" | grep -w ${SITE_HOSTNAME} | \
                                                cut -d "|" -f 10 | cut -d "/" -f 5 | sort | uniq); ## web instance name
                                        fi

                                        CERTDB=${INSTANCE_NAME}-${IUSER_AUDIT}-;

                                        reset; clear;
                                        printf "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_PROJECT_CODE -> ${WEB_PROJECT_CODE}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEBSERVER_PLATFORM -> ${WEBSERVER_PLATFORM}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLATFORM_CODE -> ${PLATFORM_CODE}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENVIRONMENT_TYPE -> ${ENVIRONMENT_TYPE}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "INSTANCE_NAME -> ${INSTANCE_NAME}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MASTER_WEBSERVER -> ${MASTER_WEBSERVER}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERTDB -> ${CERTDB}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_ROOT -> ${SERVER_ROOT}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONTACT_CODE -> ${CONTACT_CODE}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OWNER_DIST -> ${OWNER_DIST}";

                                        if [ ! -z "${WEB_PROJECT_CODE}" ] && [ ! -z "${PLATFORM_CODE}" ] && [ ! -z "${CERTDB}" ] \
                                            && [ ! -z "${MASTER_WEBSERVER}" ] && [ ! -z "${MASTER_WEBSERVER}" ] && [ ! -z "${SERVER_ROOT}" ]
                                        then
                                            ## ok we have enough info to process the request
                                            ## determine what type of request to run -
                                            ## we could be:
                                            ## generating a csr
                                            ## applying a cert (pre-implementation)
                                            ## applying a cert (implementation)
                                            ## find out
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Determining operations..";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command "${APP_ROOT}"/"${LIB_DIRECTORY}"/validators/validate_change_request.sh ${CERTDB}";

                                            unset METHOD_NAME;
                                            unset CNAME;

                                            MGMT_OP=$("${APP_ROOT}"/"${LIB_DIRECTORY}"/validators/validate_change_request.sh ${WEBSERVER_PLATFORM} ${CERTDB});

                                            CNAME=$(/usr/bin/env basename "${0}");
                                            typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
                                            typeset RETURN_CODE=0;

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MGMT_OP -> ${MGMT_OP}";

                                            case ${MGMT_OP} in
                                                csrgen)
                                                    unset MGMT_OP;
                                                    unset RET_CODE;
                                                    unset RETURN_CODE;

                                                    createCSR;
                                                    ;;
                                                preimp)
                                                    ## pre-implementation. we have a csr, we have a certificate, we want to apply it to our local
                                                    ## copy of the keystores. do it.
                                                    unset MGMT_OP;
                                                    unset RET_CODE;
                                                    unset RETURN_CODE;

                                                    applyLocalCertificate;
                                                    ;;
                                                impl)
                                                    unset MGMT_OP;
                                                    unset RET_CODE;
                                                    unset RETURN_CODE;

                                                    implementCertificateChange;
                                                    ;;
                                            esac
                                        else
                                            ## couldnt get enough info to operate properly
                                            unset REQUEST_OPTION;
                                            unset SITE_HOSTNAME;
                                            unset WEB_PROJECT_CODE;
                                            unset PLATFORM_CODE;
                                            unset MASTER_WEBSERVER;
                                            unset ENVIRONMENT_TYPE;
                                            unset SERVER_ROOT;
                                            unset CONTACT_CODE;
                                            unset OWNER_DIST;
                                            unset INSTANCE_NAME;
                                            unset CERTDB;
                                            unset RET_CODE;
                                            unset ACTIVE_DATACENTER;
                                            unset PRI_PLATFORM_CODE;
                                            unset SEC_PLATFORM_CODE;
                                            unset RETURN_CODE;
                                            unset REQ_DATACENTER;
                                            unset PLATFORM;
                                            unset MGMT_OP;
                                            unset CONTACT_NUMBER;
                                            unset RESPONSE;
                                            unset INPUT;
                                            unset CHANGE_NUM;
                                            unset PROCESS_DATE;

                                            writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No information was found for the provided hostname. Cannot continue.";

                                            printf "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        fi
                                    fi
                                else
                                    unset SITE_HOSTNAME;

                                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to locate configuration data for provided site hostname. Cannot continue.";

                                    printf "$(grep -w configuration.not.found.for.host "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                fi
                            else
                                ## no hostname was provided
                                unset SITE_HOSTNAME;

                                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                printf "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                            fi
                            ;;
                    esac
                done
                ;;
            3)
                ## update ssl exclusion list
                reset; clear;

                while true
                do
                    printf "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    printf "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    printf "\t$(grep -w cert.mgmt.exception.provide.site "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                    read SITE_HOSTNAME;

                    typeset -l SITE_HOSTNAME;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME -> ${SITE_HOSTNAME}";

                    reset; clear;

                    printf "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                    case ${SITE_HOSTNAME} in
                        [Xx]|[Qq]|[Cc])
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process aborted";

                            awk -F "=" '/\<system.request.canceled\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            ## unset SVC_LIST, we dont need it now
                            unset SITE_HOSTNAME;

                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                            ;;
                        *)
                            if [ ! -z "${SITE_HOSTNAME}" ]
                            then
                                ## validate that the hostname provided exists in website_defs. if it
                                ## doesnt, its not supported with this utility
                                if [ $(getWebInfo | grep -w ${SITE_HOSTNAME} | wc -l) != 0 ]
                                then
                                    ## we have a site hostname, lets get to work
                                    ## send to updateExceptions
                                    . "${APP_ROOT}"/"${LIB_DIRECTORY}"/updateExceptions.sh ${SSL_EXCEPTION_LIST};
                                    typeset -i RET_CODE=${?};

                                    unset SITE_HOSTNAME;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                    if [ ${RET_CODE} -eq 0 ]
                                    then
                                        ## entry added, yay
                                        reset; clear;

                                        printf "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        printf "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        printf "\t$(grep -w exception.list.updated "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s^&LIST^${SSL_EXCEPTION_LIST}^")";

                                        read RESPONSE;

                                        reset; clear; main;
                                    else
                                        ## some error occurred adding the entry
                                        reset; clear;

                                        printf "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        printf "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        printf "\t$(grep -w ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        read INPUT;

                                        unset INPUT;
                                        unset RET_CODE;
                                        unset SITE_HOSTNAME;

                                        reset; clear; main;
                                    fi
                                else
                                    unset SITE_HOSTNAME;

                                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to locate configuration data for provided site hostname. Cannot continue.";

                                    printf "$(grep -w configuration.not.found.for.host "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                fi
                            else
                                ## no hostname was provided
                                unset SITE_HOSTNAME;

                                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                printf "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                            fi
                            ;;
                    esac
                done
                ;;
            4)
                ## execute an ad-hoc report
                reset; clear;

                while true
                do
                    printf "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    printf "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    printf "\t$(grep -w cert.mgmt.adhoc.provide.platform "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                    read PLATFORM_CODE;

                    typeset -u PLATFORM_CODE;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLATFORM_CODE -> ${PLATFORM_CODE}";

                    reset; clear;

                    printf "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                    case ${PLATFORM_CODE} in
                        [Xx]|[Qq]|[Cc])
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process aborted";

                            awk -F "=" '/\<system.request.canceled\>/{print "\t" $2 "\n"}' ${SYSTEM_MESSAGES} | sed -e 's/^ *//g;s/ *$//g;/^ *#/d;s/#.*//';

                            ## unset SVC_LIST, we dont need it now
                            unset PLATFORM_CODE;

                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                            ;;
                        *)
                            if [ ! -z "${PLATFORM_CODE}" ]
                            then
                                ## make sure it exists..
                                for PLATFORM in ${PLATFORM_CODE}
                                do
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLATFORM -> ${PLATFORM}";

                                    if [ $(getPlatformInfo | grep -c ${PLATFORM}) != 0 ]
                                    then
                                        set -A OPERABLE_PLATFORMS ${OPERABLE_PLATFORMS[*]} ${PLATFORM};
                                    fi
                                done

                                unset PLATFORM;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPERABLE_PLATFORMS -> ${OPERABLE_PLATFORMS[*]}";

                                if [ ! -z "${OPERABLE_PLATFORMS}" ]
                                then
                                    ## ok good, get webservers
                                    for PLATFORM in ${OPERABLE_PLATFORMS[*]}
                                    do
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLATFORM -> ${PLATFORM}";

                                        set -A WEBSERVER_NAMES ${WEBSERVER_NAMES[*]} $(getPlatformInfo | grep -w ${PLATFORM} | grep -v "#" | grep -v "none" | cut -d "|" -f 5 | sort | uniq | sed -e "s/,/ /g");
                                    done

                                    unset PLATFORM;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEBSERVER_NAMES -> ${WEBSERVER_NAMES[*]}";

                                    if [ ! -z "${WEBSERVER_NAMES}" ]
                                    then
                                        while true
                                        do
                                            reset; clear;

                                            ## ask if we want a specific date
                                            printf "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                            printf "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                            printf "\t$(grep -w cert.mgmt.adhoc.provide.days "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                            read REPORT_DAYS;

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REPORT_DAYS -> ${REPORT_DAYS}";

                                            reset; clear;

                                            printf "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                            if [ ! -z "${REPORT_DAYS}" ]
                                            then
                                                if [ "$(isNaN ${REPORT_DAYS})" = "${_TRUE}" ]
                                                then
                                                    reset; clear; break;
                                                else
                                                    ## not a number
                                                    unset REPORT_DAYS;

                                                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided response is not numeric. Cannot continue.";

                                                    printf "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                fi
                                            else
                                                ## input was blank, use default
                                                REPORT_DAYS=${VALIDATION_PERIOD};

                                                reset; clear; break;
                                            fi
                                        done

                                        ## ask if we want to send a targetted email
                                        printf "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        printf "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        printf "\t$(grep -w cert.mgmt.adhoc.provide.email "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        read TARGET_EMAIL;

                                        if [ -z "${TARGET_EMAIL}" ]
                                        then
                                            TARGET_EMAIL=${ALERT_CERTIFICATE_ADDRESS};
                                        fi

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_EMAIL -> ${TARGET_EMAIL}";

                                        reset; clear;

                                        printf "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command nohup "${APP_ROOT}"/"${LIB_DIRECTORY}"/runMonitor.sh -m monitorCertificateDatabases -s \"$(printf ${WEBSERVER_NAMES[*]})\" -d $(returnEpochTime \"$(date +"%Y %m %d")\" ${REPORT_DAYS}) -a \"${TARGET_EMAIL}\" -e > /dev/null 2>&1 &";

                                        nohup "${APP_ROOT}"/"${LIB_DIRECTORY}"/runMonitor.sh -m monitorCertificateDatabases -s "$(printf ${WEBSERVER_NAMES[*]})" -d $(returnEpochTime "$(date +"%Y %m %d")" ${REPORT_DAYS}) -a "${TARGET_EMAIL}" -e > /dev/null 2>&1 &

                                        reset; clear;

                                        unset WEBSERVER_NAMES;
                                        unset PLATFORM_CODE;
                                        unset REQUEST_OPTION;
                                        unset REPORT_DAYS;
                                        unset PLATFORM;
                                        unset OPERABLE_PLATFORMS;
                                        unset TARGET_EMAIL;

                                        printf "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        printf "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        printf "\t$(grep -w cert.mgmt.adhoc.report.running "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                        printf "\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        read INPUT;

                                        reset; clear; break;
                                    else
                                        ## no webservers
                                        unset WEBSERVER_NAMES;

                                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No webservers were found with requested platform code ${PLATFORM_CODE}. continue.";

                                        unset PLATFORM_CODE;

                                        printf "$(grep -w configuration.not.found.for.host "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    fi
                                else
                                    ## no platform information was found
                                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No information was found with requested platform code ${PLATFORM_CODE}. continue.";

                                    unset PLATFORM_CODE;

                                    printf "$(grep -w configuration.not.found.for.host "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                fi
                            else
                                ## platform code blank
                                unset PLATFORM_CODE;

                                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                printf "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                            fi
                            ;;
                    esac
                done
                ;;
            *)
                unset SITE_HOSTNAME;

                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                printf "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done
}

#===  FUNCTION  ===============================================================
#          NAME:  applyLocalCertificate
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function createCSR
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing via csrGenerationUI helper..";

    while true
    do
        if [ ! -z "${CANCEL_REQ}" ] && [ "${CANCEL_REQ}" = "${_TRUE}" ]
        then
            unset REQUEST_OPTION;
            unset WEB_PROJECT_CODE;
            unset PLATFORM_CODE;
            unset MASTER_WEBSERVER;
            unset ENVIRONMENT_TYPE;
            unset SERVER_ROOT;
            unset CONTACT_CODE;
            unset OWNER_DIST;
            unset INSTANCE_NAME;
            unset CERTDB;
            unset RET_CODE;
            unset ACTIVE_DATACENTER;
            unset PRI_PLATFORM_CODE;
            unset SEC_PLATFORM_CODE;
            unset RETURN_CODE;
            unset REQ_DATACENTER;
            unset PLATFORM;
            unset MGMT_OP;
            unset CONTACT_NUMBER;
            unset RESPONSE;
            unset INPUT;
            unset CHANGE_NUM;
            unset PROCESS_DATE;

            ## user chose to cancel out of the subshell
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break. CANCEL_REQ->${CANCEL_REQ}, ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}. Breaking..";

            ## put methodname and cname back
            typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
            typeset RETURN_CODE=0;
            CNAME=$(/usr/bin/env basename "${0}");

            reset; clear; main;
        elif [ ! -z "${CSR_COMPLETE}" ] && [ "${CSR_COMPLETE}" = "${_TRUE}" ]
        then
            ## record has been added successfully through the helper
            ## ask if we want to add additional records to the zone
            ## put methodname and cname back
            typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
            typeset RETURN_CODE=0;
            CNAME=$(/usr/bin/env basename "${0}");

            unset REQUEST_OPTION;
            unset WEB_PROJECT_CODE;
            unset PLATFORM_CODE;
            unset MASTER_WEBSERVER;
            unset ENVIRONMENT_TYPE;
            unset SERVER_ROOT;
            unset CONTACT_CODE;
            unset OWNER_DIST;
            unset INSTANCE_NAME;
            unset CERTDB;
            unset RET_CODE;
            unset ACTIVE_DATACENTER;
            unset PRI_PLATFORM_CODE;
            unset SEC_PLATFORM_CODE;
            unset RETURN_CODE;
            unset REQ_DATACENTER;
            unset PLATFORM;
            unset MGMT_OP;
            unset CONTACT_NUMBER;
            unset RESPONSE;
            unset INPUT;
            unset CHANGE_NUM;
            unset PROCESS_DATE;

            while true
            do
                printf "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                printf "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                printf "\t$(grep -w cert.mgmt.perform.more.tasks "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                read RESPONSE;

                unset SITE_HOSTNAME;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                case ${RESPONSE} in
                    [Yy][Ee][Ss]|[Yy])
                        ## request to continue forward with new stuff
                        unset RESPONSE;
                        unset CSR_COMPLETE;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further processing requested.";

                        sleep "${MESSAGE_DELAY}"; reset; clear; main;
                        ;;
                    [Nn][Oo]|[Nn])
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;
                        unset RESPONSE;

                        sleep "${MESSAGE_DELAY}"; reset; clear; exec ${MAIN_CLASS};

                        exit 0;
                        ;;
                    *)
                        unset RESPONSE;
                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                        printf "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        ;;
                esac
            done
        else
            ## unset methodname and cname
            unset METHOD_NAME;
            unset CNAME;

            ## we hardcode to go to add_a_record, although this probably isnt right.
            ## we could also go to ns. we go to A because if we use NS, then the
            ## nameserver this zone gets applied to really doesnt need it there,
            ## because another nameserver already has it and can do it on its own.
            ## for this reason, we dont ask.
            . "${APP_ROOT}"/"${LIB_DIRECTORY}"/helpers/ui/csrGenerationUI.sh;
        fi
    done
}

#===  FUNCTION  ===============================================================
#          NAME:  applyLocalCertificate
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function applyLocalCertificate
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing via certRenewalUI helper..";

    while true
    do
        if [ ! -z "${CANCEL_REQ}" ] && [ "${CANCEL_REQ}" = "${_TRUE}" ]
        then
            unset REQUEST_OPTION;
            unset WEB_PROJECT_CODE;
            unset PLATFORM_CODE;
            unset MASTER_WEBSERVER;
            unset ENVIRONMENT_TYPE;
            unset SERVER_ROOT;
            unset CONTACT_CODE;
            unset OWNER_DIST;
            unset INSTANCE_NAME;
            unset CERTDB;
            unset RET_CODE;
            unset ACTIVE_DATACENTER;
            unset PRI_PLATFORM_CODE;
            unset SEC_PLATFORM_CODE;
            unset RETURN_CODE;
            unset REQ_DATACENTER;
            unset PLATFORM;
            unset MGMT_OP;
            unset CONTACT_NUMBER;
            unset RESPONSE;
            unset INPUT;
            unset CHANGE_NUM;
            unset PROCESS_DATE;

            ## user chose to cancel out of the subshell
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break. CANCEL_REQ->${CANCEL_REQ}, ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}. Breaking..";

            ## put methodname and cname back
            typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
            typeset RETURN_CODE=0;
            CNAME=$(/usr/bin/env basename "${0}");

            reset; clear; main;
        elif [ ! -z "${PREIMP_COMPLETE}" ] && [ "${PREIMP_COMPLETE}" = "${_TRUE}" ]
        then
            ## record has been added successfully through the helper
            ## ask if we want to add additional records to the zone
            ## put methodname and cname back
            typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
            typeset RETURN_CODE=0;
            CNAME=$(/usr/bin/env basename "${0}");

            unset REQUEST_OPTION;
            unset WEB_PROJECT_CODE;
            unset PLATFORM_CODE;
            unset MASTER_WEBSERVER;
            unset ENVIRONMENT_TYPE;
            unset SERVER_ROOT;
            unset CONTACT_CODE;
            unset OWNER_DIST;
            unset INSTANCE_NAME;
            unset CERTDB;
            unset RET_CODE;
            unset ACTIVE_DATACENTER;
            unset PRI_PLATFORM_CODE;
            unset SEC_PLATFORM_CODE;
            unset RETURN_CODE;
            unset REQ_DATACENTER;
            unset PLATFORM;
            unset MGMT_OP;
            unset CONTACT_NUMBER;
            unset RESPONSE;
            unset INPUT;
            unset CHANGE_NUM;
            unset PROCESS_DATE;

            while true
            do
                printf "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                printf "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                printf "\t$(grep -w cert.mgmt.perform.more.tasks "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                read RESPONSE;

                unset SITE_HOSTNAME;
                unset PREIMP_COMPLETE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                case ${RESPONSE} in
                    [Yy][Ee][Ss]|[Yy])
                        ## request to continue forward with new stuff
                        unset RESPONSE;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further processing requested.";

                        sleep "${MESSAGE_DELAY}"; reset; clear; main;
                        ;;
                    [Nn][Oo]|[Nn])
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;
                        unset RESPONSE;

                        sleep "${MESSAGE_DELAY}"; reset; clear; exec ${MAIN_CLASS};

                        exit 0;
                        ;;
                    *)
                        unset RESPONSE;
                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                        printf "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        ;;
                esac
            done
        else
            ## unset methodname and cname
            unset METHOD_NAME;
            unset CNAME;

            ## we hardcode to go to add_a_record, although this probably isnt right.
            ## we could also go to ns. we go to A because if we use NS, then the
            ## nameserver this zone gets applied to really doesnt need it there,
            ## because another nameserver already has it and can do it on its own.
            ## for this reason, we dont ask.
            . "${APP_ROOT}"/"${LIB_DIRECTORY}"/helpers/ui/certRenewalUI.sh;
        fi
    done
}

#===  FUNCTION  ===============================================================
#          NAME:  implementCertificateChange
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function implementCertificateChange
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing via implementCertUI helper..";

    while true
    do
        if [[ ! -z "${CANCEL_REQ}" && "${CANCEL_REQ}" = "${_TRUE}" ]]
        then
            unset REQUEST_OPTION;
            unset WEB_PROJECT_CODE;
            unset PLATFORM_CODE;
            unset MASTER_WEBSERVER;
            unset ENVIRONMENT_TYPE;
            unset SERVER_ROOT;
            unset CONTACT_CODE;
            unset OWNER_DIST;
            unset INSTANCE_NAME;
            unset CERTDB;
            unset RET_CODE;
            unset ACTIVE_DATACENTER;
            unset PRI_PLATFORM_CODE;
            unset SEC_PLATFORM_CODE;
            unset RETURN_CODE;
            unset REQ_DATACENTER;
            unset PLATFORM;
            unset MGMT_OP;
            unset CONTACT_NUMBER;
            unset RESPONSE;
            unset INPUT;
            unset CHANGE_NUM;
            unset PROCESS_DATE;

            ## user chose to cancel out of the subshell
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break. CANCEL_REQ->${CANCEL_REQ}, ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}. Breaking..";

            ## put methodname and cname back
            typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
            typeset RETURN_CODE=0;
            CNAME=$(/usr/bin/env basename "${0}");

            reset; clear; main;
        elif [ ! -z "${PREIMP_COMPLETE}" ] && [ "${PREIMP_COMPLETE}" = "${_TRUE}" ]]
        then
            ## record has been added successfully through the helper
            ## ask if we want to add additional records to the zone
            ## put methodname and cname back
            typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
            typeset RETURN_CODE=0;
            CNAME=$(/usr/bin/env basename "${0}");

            unset REQUEST_OPTION;
            unset WEB_PROJECT_CODE;
            unset PLATFORM_CODE;
            unset MASTER_WEBSERVER;
            unset ENVIRONMENT_TYPE;
            unset SERVER_ROOT;
            unset CONTACT_CODE;
            unset OWNER_DIST;
            unset INSTANCE_NAME;
            unset CERTDB;
            unset RET_CODE;
            unset ACTIVE_DATACENTER;
            unset PRI_PLATFORM_CODE;
            unset SEC_PLATFORM_CODE;
            unset RETURN_CODE;
            unset REQ_DATACENTER;
            unset PLATFORM;
            unset MGMT_OP;
            unset CONTACT_NUMBER;
            unset RESPONSE;
            unset INPUT;
            unset CHANGE_NUM;
            unset PROCESS_DATE;

            while true
            do
                printf "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                printf "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                printf "\t$(grep -w cert.mgmt.cert.applied "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";
                printf "\t$(grep -w cert.mgmt.perform.more.tasks "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                read RESPONSE;

                unset SITE_HOSTNAME;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                case ${RESPONSE} in
                    [Yy][Ee][Ss]|[Yy])
                        ## request to continue forward with new stuff
                        unset RESPONSE;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further processing requested.";

                        IMPL_COMPLETE=${_TRUE};

                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                        ;;
                    [Nn][Oo]|[Nn])
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;
                        unset RESPONSE;

                        sleep "${MESSAGE_DELAY}"; reset; clear; exec ${MAIN_CLASS};

                        exit 0;
                        ;;
                    *)
                        unset RESPONSE;
                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                        printf "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        ;;
                esac
            done
        else
            ## unset methodname and cname
            unset METHOD_NAME;
            unset CNAME;
            unset IMPL_COMPLETE;

            ## we hardcode to go to add_a_record, although this probably isnt right.
            ## we could also go to ns. we go to A because if we use NS, then the
            ## nameserver this zone gets applied to really doesnt need it there,
            ## because another nameserver already has it and can do it on its own.
            ## for this reason, we dont ask.
            . "${APP_ROOT}"/"${LIB_DIRECTORY}"/helpers/ui/implementCertUI.sh;
        fi
    done
}

#===  FUNCTION  ===============================================================
#          NAME:  updateCertificate
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function updateCertificate
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting subdomain CNAME record information..";

    trap "echo '$(grep system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

    while true
    do
        if [ ! -z "${PREIMP_COMPLETE}" ] && [ "${PREIMP_COMPLETE}" = "${_TRUE}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

            break;
        fi

        reset; clear;

        unset MGMT_OP;
        unset RET_CODE;
        unset RETURN_CODE;

        ## certificate received, apply to typeset keystores
        ## call out to run_renewal
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing information confirmed. Continuing..";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting certificate..";

        while true
        do
            if [ ! -z "${PREIMP_COMPLETE}" ] && [ "${PREIMP_COMPLETE}" = "${_TRUE}" ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

                break;
            fi

            echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            echo "\t$(grep -w cert.mgmt.provide.certificate "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";

            sleep "${MESSAGE_DELAY}"; reset; clear;

            ## all we do here is execute vi to get the certificate. thats it.
            ## name it after the certdb var, we'll handle the rename in run_renewal
            vi "${APP_ROOT}"/${CERTSTORE}/${CERTDB}.cer;

            ## make sure file got created..
            if [ -s "${APP_ROOT}"/${CERTSTORE}/${CERTDB}.cer ]
            then
                ## make sure its actually a certificate
                if [ ! -z "$(grep "BEGIN CERTIFICATE" "${APP_ROOT}"/${CERTSTORE}/${CERTDB}.cer)" ] \
                    && [ ! -z "$(grep "END CERTIFICATE" "${APP_ROOT}"/${CERTSTORE}/${CERTDB}.cer)" ]
                then
                    ## looks good, pop it off
                    reset; clear; break;
                else
                    ## no cert here.....
                    rm "${APP_ROOT}"/${CERTSTORE}/${CERTDB}.cer > /dev/null 2>&1;

                    echo "\t$(grep -w no.cert.data.found "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi
            else
                ## no cert file generated
                rm "${APP_ROOT}"/${CERTSTORE}/${CERTDB}.cer > /dev/null 2>&1;

                echo "\t$(grep -w no.cert.data.found "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
            fi
        done

        echo "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing runCertRenewal.sh -d ${CERTDB} -s ${SITE_HOSTNAME} -w ${WEBSERVER_PLATFORM} -e..";

        ## tmp unset
        unset CNAME;
        unset METHOD_NAME;
        CURR_OPTIND=${OPTIND};

        . "${APP_ROOT}"/"${LIB_DIRECTORY}"/runCertRenewal.sh -d ${CERTDB} -s ${SITE_HOSTNAME} -w ${WEBSERVER_PLATFORM} -e;
        RET_CODE=${?}

        OPTIND=${CURR_OPTIND};
        CNAME=$(/usr/bin/env basename "${0}");
        typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
        typeset RETURN_CODE=0;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

        if [ ${RET_CODE} -eq 0 ] || [ ${RET_CODE} -eq 97 ]
        then
            ## get the change number and send the owner notify
            while true
            do
                if [ ! -z "${PREIMP_COMPLETE}" ] && [ "${PREIMP_COMPLETE}" = "${_TRUE}" ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

                    break;
                fi

                reset; clear;

                if [ ${RET_CODE} -eq 97 ]
                then
                    unset RET_CODE;

                    echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    echo "\t$(grep -w pem.mail.generation.failed "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                    sleep "${MESSAGE_DELAY}"; reset; clear;

                    cat "${APP_ROOT}"/${MAILSTORE}/PEM-${SITE_HOSTNAME}.message;

                    echo "\n\n\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                    read INPUT;

                    reset; clear;
                fi

                unset INPUT;

                ## if this is a production renewal, send the owner notify
                if [ "${ENVIRONMENT_TYPE}" = "${ENV_TYPE_PRD}" ]
                then
                    echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    echo "\t$(grep -w system.provide.changenum "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                    read CHANGE_NUM;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";

                    reset; clear;

                    echo "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                    if [[ ${CHANGE_NUM} == [Ee] ]]
                    then
                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change specified as emergency, but this is not an emergency change.";

                        unset CHANGE_NUM;

                        echo "$(grep -w change.control.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi

                    ## validate the CR number
                    if [ $("${APP_ROOT}"/"${LIB_DIRECTORY}"/validators/validate_change_ticket.sh ${CHANGE_NUM}) -ne 0 ]
                    then
                        ## change control provided was invalid
                        writeLogEntry "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A change was attempted with an invalid change order by ${IUSER_AUDIT}. Change request was ${CHANGE_NUM}.";
                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid change control was provided. A valid change control number is required to process the request.";

                        unset CHANGE_NUM;

                        echo "$(grep -w change.control.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    else
                        while true
                        do
                            if [ ! -z "${PREIMP_COMPLETE}" ] && [ "${PREIMP_COMPLETE}" = "${_TRUE}" ]
                            then
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

                                break;
                            fi

                            ## valid cr number. get the expected process date
                            reset; clear;

                            echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            echo "\t$(grep -w cert.mgmt.provide.process.date "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                            read PROCESS_DATE;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_DATE -> ${PROCESS_DATE}";

                            reset; clear;

                            if [ ! -z "${PROCESS_DATE}" ]
                            then
                                ## validate it
                                returnEpochTime ${PROCESS_DATE} > /dev/null 2>&1;
                                typeset -i RET_CODE=${?};

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                if [ ${RET_CODE} -eq 0 ]
                                then
                                    ## all good
                                    unset RET_CODE;

                                    ## re-define process date
                                    PROCESS_DATE=$(echo ${PROCESS_DATE} | awk '{print $2, $3, $1}');

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_DATE -> ${PROCESS_DATE}";

                                    ## send the owner notify
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Sending owner notification..";

                                    unset METHOD_NAME;
                                    unset CNAME;

                                    . ${MAILER_CLASS} -m ${NOTIFY_OWNER_EMAIL} -p ${WEB_PROJECT_CODE} -a "${OWNER_DIST}" -t ${NOTIFY_TYPE_NOTIFY} -e;
                                    typeset -i RET_CODE=${?};

                                    CNAME=$(/usr/bin/env basename "${0}");
                                    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
                                    typeset RETURN_CODE=0;

                                    PREIMP_COMPLETE=${_TRUE};

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PREIMP_COMPLETE -> ${PREIMP_COMPLETE}";

                                    if [ ${RET_CODE} -ne 0 ]
                                    then
                                        ## owner notify failed
                                        reset; clear;

                                        echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        echo "\t$(grep -w owner.mail.generation.failed "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        sleep "${MESSAGE_DELAY}"; reset; clear;

                                        cat "${APP_ROOT}"/${MAILSTORE}/OWNER-${SITE_HOSTNAME}.message;

                                        echo "\n\n\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        read INPUT;

                                        reset; clear; break;
                                    else
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Owner notification sent. Continuing..";

                                        echo "\t$(grep -w cert.mgmt.owner.notification.sent "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                    fi
                                else
                                    unset PROCESS_DATE;

                                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                                    echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                fi
                            else
                                unset PROCESS_DATE;

                                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                                echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                            fi
                        done
                    fi
                else
                    PREIMP_COMPLETE=${_TRUE};

                    echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    echo "\t$(grep -w cert.mgmt.cert.implemented "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";
                    echo "\n\n\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                    read INPUT;

                    reset; clear; break;
                fi
            done
        else
            ## an error occurred, we can start over
            PREIMP_COMPLETE=${_TRUE};

            echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            echo "\t$(grep ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            echo "\t$(grep -w cert.mgmt.cert.application.failed "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";
            echo "\n\n\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

            read INPUT;

            reset; clear; break;
        fi
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

#===  FUNCTION  ===============================================================
#          NAME:  generateCSR
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function generateCSR
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting subdomain CNAME record information..";

    trap "echo '$(grep system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

    while true
    do
        if [ ! -z "${CSR_COMPLETE}" ] && [ "${CSR_COMPLETE}" = "${_TRUE}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

            break;
        fi

        unset MGMT_OP;
        unset RET_CODE;
        unset RETURN_CODE;

        ## generate a csr
        ## request the user's contact phone number
        while true
        do
            if [ ! -z "${CSR_COMPLETE}" ] && [ "${CSR_COMPLETE}" = "${_TRUE}" ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

                break;
            fi

            reset; clear;

            echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            echo "\t$(grep -w cert.mgmt.provide.contact.number "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

            read CONTACT_NUMBER;
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONTACT_NUMBER -> ${CONTACT_NUMBER}";

            reset; clear;

            echo "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

            ## validate it
            case ${CONTACT_NUMBER} in
                [Xx]|[Qq]|[Cc])
                    reset; clear;
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to cancel.";

                    unset CONTACT_NUMBER;

                    echo "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                    CANCEL_REQ=${_TRUE};

                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                    ;;
                *)
                    if [ $("${APP_ROOT}"/"${LIB_DIRECTORY}"/validators/validate_tel_num.sh ${CONTACT_NUMBER}) -ne 0 ]
                    then
                        reset; clear;
                        ## number provided was invalid
                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid telephone number was provided. A valid telephone number is required to process the request.";

                        unset CONTACT_NUMBER;
                        unset RET_CODE;
                        unset RETURN_CODE;

                        echo "$(grep -w contact.number.invalid "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    else
                        reset; clear;

                        echo "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                        ## call out to run_key_generation
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing information confirmed. Continuing..";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runKeyGeneration.sh -s ${SITE_HOSTNAME} -v ${MASTER_WEBSERVER} -w ${WEBSERVER_PLATFORM} -p ${SERVER_ROOT} -d ${CERTDB} -c $(echo ${PLATFORM_CODE} | awk '{print $1}') -t ${CONTACT_NUMBER} -e..";

                        ## tmp unset
                        unset CNAME;
                        unset METHOD_NAME;

                        . "${APP_ROOT}"/"${LIB_DIRECTORY}"/runKeyGeneration.sh -s ${SITE_HOSTNAME} -v ${MASTER_WEBSERVER} \
                            -w ${WEBSERVER_PLATFORM} -p ${SERVER_ROOT} -d ${CERTDB} -c $(echo ${PLATFORM_CODE} | awk '{print $1}') \
                            -t ${CONTACT_NUMBER} -e;
                        RET_CODE=${?}

                        CNAME=$(/usr/bin/env basename "${0}");
                        typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
                        typeset RETURN_CODE=0;
                        CSR_COMPLETE=${_TRUE};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CSR_COMPLETE -> ${CSR_COMPLETE}";

                        reset; clear;

                        if [ ! -z "${RET_CODE}" ]
                        then
                            if [ ${RET_CODE} -eq 0 ]
                            then
                                echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                echo "\t$(grep -w cert.mgmt.csr.generated "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";
                                echo "\n\n\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                read INPUT;

                                reset; clear; break;
                            elif [ ${RET_CODE} -eq 95 ]
                            then
                                ## ask if we want to do another, if yes, clear
                                ## and send back to the beginning, otherwise send
                                ## back to main
                                echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                echo "\t$(grep -w csr.mail.generation.failed "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                sleep "${MESSAGE_DELAY}"; reset; clear;

                                cat "${APP_ROOT}"/${CSRSTORE}/${CERT_NICKNAME}.csr;

                                echo "\n\n\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                read INPUT;
                                reset; clear; break;
                            fi
                        else
                            echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            echo "\t$(grep -w ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            echo "\t$(grep -w cert.mgmt.csr.generation.failed "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")\n";
                            echo "\n\n\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                            read INPUT;

                            reset; clear; break;
                        fi
                    fi
                    ;;
            esac
        done
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";


    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set +x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +v;

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  implementCertificate
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function implementCertificate
{
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "true" ] && set -x;
    [ ! -z "${ENABLE_VERBOSE}" ] && [ "${ENABLE_VERBOSE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -v;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -v;
    typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting subdomain CNAME record information..";

    trap "echo '$(grep system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

    while true
    do
        if [ -z "${IMPL_COMPLETE}" ] || [ "${IMPL_COMPLETE}" = "${_FALSE}" ]
        then
            ## implement update to target webnodes
            ## call out to run_renewal
            ## we require a change order to operate
            while true
            do
                echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                echo "\t$(grep -w system.provide.changenum "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                echo "\t$(grep -w system.emergency.changenum "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                read CHANGE_NUM;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";

                reset; clear;

                echo "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                if [[ ${CHANGE_NUM} == [Ee] ]]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change specified as emergency..";
                    CHANGE_NUM="E-$(date +"%m-%d-%Y_%H:%M:%S")";
                fi

                ## validate the CR number
                if [ $("${APP_ROOT}"/"${LIB_DIRECTORY}"/validators/validate_change_ticket.sh ${CHANGE_NUM}) -ne 0 ]
                then
                    ## change control provided was invalid
                    writeLogEntry "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A change was attempted with an invalid change order by ${IUSER_AUDIT}. Change request was ${CHANGE_NUM}.";
                    writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid change control was provided. A valid change control number is required to process the request.";

                    unset CHANGE_NUM;

                    echo "$(grep -w change.control.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                else
                    ## we have a valid change ticket. we can continue
                    ## call out to run_renewal
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing information confirmed. Continuing..";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runCertRenewal.sh -d ${CERTDB} -n ${CERT_NICKNAME} -s ${SITE_HOSTNAME} -c ${CHANGE_NUM} -a -e..";

                    ## tmp unset
                    unset CNAME;
                    unset METHOD_NAME;

                    if [ "${ENVIRONMENT_TYPE}" = "${ENV_TYPE_PRD}" ]
                    then
                        unset METHOD_NAME;
                        unset CNAME;

                        . "${APP_ROOT}"/"${LIB_DIRECTORY}"/runQuery.sh -u ${SITE_HOSTNAME} -e;
                        RET_CODE=${?}

                        CNAME=$(/usr/bin/env basename "${0}");
                        typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
                        typeset RETURN_CODE=0;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ACTIVE_DATACENTER -> ${ACTIVE_DATACENTER}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        unset RET_CODE;
                        unset RETURN_CODE;

                        if [ ! -z "${ACTIVE_DATACENTER}" ] && [ ${RET_CODE} -eq 0 ]
                        then
                            ## we have an active datacenter, re-order the server list
                            PRI_PLATFORM_CODE=$(getWebInfo | grep "${SITE_HOSTNAME}" | grep -v "#" | grep -v ${ACTIVE_DATACENTER} | \
                                cut -d "|" -f 2 | sort | uniq); ## get the platform code, if multiples spit with space
                            SEC_PLATFORM_CODE=$(getWebInfo | grep "${SITE_HOSTNAME}" | grep -v "#" | grep ${ACTIVE_DATACENTER} | \
                                cut -d "|" -f 2 | sort | uniq); ## get the platform code, if multiples spit with space

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRI_PLATFORM_CODE -> ${PRI_PLATFORM_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SEC_PLATFORM_CODE -> ${SEC_PLATFORM_CODE}";
                        else
                            while true
                            do
                                if [ -z "${IMPL_COMPLETE}" ] || [ "${IMPL_COMPLETE}" = "${_FALSE}" ]
                                then
                                    reset; clear;

                                    echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    echo "\t$(grep -w cert.mgmt.provide.datacenter "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                    echo "\t$(grep -w system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                    read REQ_DATACENTER;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQ_DATACENTER -> ${REQ_DATACENTER}";

                                    case ${REQ_DATACENTER} in
                                        [Xx]|[Qq]|[Cc])
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate renewal process canceled..";

                                            echo "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                            ## unset SVC_LIST, we dont need it now
                                            unset REQ_DATACENTER;
                                            unset RET_CODE;
                                            unset CONTACT_NUMBER;
                                            unset MGMT_OP;
                                            unset SERVER_ROOT;
                                            unset PLATFORM;
                                            unset SEC_PLATFORM_CODE;
                                            unset PRI_PLATFORM_CODE;
                                            unset REQ_DATACENTER;
                                            unset SEC_PLATFORM_CODE;
                                            unset PRI_PLATFORM_CODE;
                                            unset RETURN_CODE;
                                            unset ACTIVE_DATACENTER;
                                            unset CHANGE_NUM;
                                            unset MASTER_WEBSERVER;
                                            unset CERTDB;
                                            unset INSTANCE_NAME;
                                            unset ENVIRONMENT_TYPE;
                                            unset WEBSERVER_PLATFORM;
                                            unset PLATFORM_CODE;
                                            unset WEB_PROJECT_CODE;
                                            unset REQUEST_OPTION;

                                            IMPL_COMPLETE=${_TRUE};
                                            CANCEL_REQ=${_TRUE};

                                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                            ;;
                                        *)
                                            if [ -z "$(echo ${AVAILABLE_DATACENTERS} | grep -i ${REQ_DATACENTER})" ]
                                            then
                                                ## selected datacenter is NOT valid
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selected datacenter is not valid. Please utilize a different datacenter.";

                                                echo "$(grep -w datacenter.not.configured "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%DATACENTER%/${REQ_DATACENTER}/")\n";

                                                ## unset SVC_LIST, we dont need it now
                                                unset REQ_DATACENTER;

                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            else
                                                ## get the platform code, if multiples split with space
                                                typeset -u PRI_PLATFORM_CODE=$(getWebInfo | grep "${SITE_HOSTNAME}" | grep -v "#" | grep -v $(echo ${REQ_DATACENTER} | cut -d "|" -f 2 | sort | uniq));
                                                ## get the platform code, if multiples split with space
                                                typeset -u SEC_PLATFORM_CODE=$(getWebInfo | grep "${SITE_HOSTNAME}" | grep -v "#" | grep $(echo ${REQ_DATACENTER} | cut -d "|" -f 2 | sort | uniq));

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRI_PLATFORM_CODE -> ${PRI_PLATFORM_CODE}";
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SEC_PLATFORM_CODE -> ${SEC_PLATFORM_CODE}";

                                                unset PLATFORM;
                                                reset; clear; break;
                                            fi
                                            ;;
                                    esac
                                else
                                    reset; clear; break;
                                fi
                            done
                        fi

                        ## production environment. we start in the secondary and move to the primary
                        for PLATFORM in ${PRI_PLATFORM_CODE} ${SEC_PLATFORM_CODE}
                        do
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on platform ${PLATFORM} ..";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runCertRenewal.sh -d ${CERTDB} -s ${SITE_HOSTNAME} -w ${WEBSERVER_PLATFORM} -p ${PLATFORM} -c ${CHANGE_NUM} -a -e";

                            unset METHOD_NAME;
                            unset CNAME;

                            . "${APP_ROOT}"/"${LIB_DIRECTORY}"/runCertRenewal.sh -d ${CERTDB} -s ${SITE_HOSTNAME} \
                                -w ${WEBSERVER_PLATFORM} -p ${PLATFORM} -c ${CHANGE_NUM} -a -e;
                            typeset -i RET_CODE=${?};

                            CNAME=$(/usr/bin/env basename "${0}");
                            typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
                            typeset RETURN_CODE=0;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                            if [ ! -z "${RET_CODE}" ] && [ ${RET_CODE} -eq 0 ]
                            then
                                ## backup datacenter was successfully updated.
                                ## request verification, and if good, continue forward.
                                while true
                                do
                                    if [ -z "${IMPL_COMPLETE}" ] || [ "${IMPL_COMPLETE}" = "${_FALSE}" ]
                                    then
                                        echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        echo "\t$(grep -w cert.mgmt.cert.implemented "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/" -e "s/%REQ_DATACENTER%/$(echo ${PLATFORM} | cut -d "_" -f 1)/")";
                                        echo "\t$(grep -w cert.mgmt.cert.verify "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        read RESPONSE;

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE} ..";

                                        case ${RESPONSE} in
                                            [Yy][Ee][Ss]|[Yy])
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received positive resonse. Continuing ..";
                                                writeLogEntry "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate Renewal (implementation) by ${IUSER_AUDIT}: Site: ${SITE_HOSTNAME}; Certificate Database: ${CERTIFICATE_DATABASE_STORE}; Successfully implemented in $(echo ${PLATFORM} | cut -d "_" -f 1)";

                                                continue;
                                                ;;
                                            [Nn][Oo]|[Nn])
                                                ## either it didnt work right or we just dont want to do it right now
                                                ## TODO: if it didnt work right, lets start the backout process
                                                echo "failed";
                                                exit 1;
                                                ;;
                                            *)
                                                unset RESPONSE;
                                                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                                                echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                ;;
                                        esac
                                    else
                                        reset; clear; break;
                                    fi
                                done
                            else
                                ## backup datacenter failed. error out
                                echo "failed";
                                exit 1;
                            fi
                        done
                    else
                        ## dev renewal
                        unset METHOD_NAME;
                        unset CNAME;

                        . "${APP_ROOT}"/"${LIB_DIRECTORY}"/runCertRenewal.sh -d ${CERTDB} -s ${SITE_HOSTNAME} -w ${WEBSERVER_PLATFORM} -p ${PLATFORM_CODE} -c ${CHANGE_NUM} -a -e;
                        typeset -i RET_CODE=${?};

                        CNAME=$(/usr/bin/env basename "${0}");
                        typeset METHOD_NAME="${THIS_CNAME}#${FUNCNAME[0]}";
                        typeset RETURN_CODE=0;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        if [ ! -z "${RET_CODE}" ]
                        then
                            if [ ${RET_CODE} -eq 0 ]
                            then
                                ## ask if we want to do another, if yes, clear
                                ## and send back to the beginning, otherwise send
                                ## back to main
                                ## unset the old
                                while true
                                do
                                    if [ -z "${IMPL_COMPLETE}" ] || [ "${IMPL_COMPLETE}" = "${_FALSE}" ]
                                    then
                                        reset; clear;

                                        echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        echo "\t$(grep -w cert.mgmt.cert.implemented "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";
                                        echo "\t$(grep -w cert.mgmt.perform.more.tasks "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        read RESPONSE;

                                        unset SITE_HOSTNAME;

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                        case ${RESPONSE} in
                                            [Yy][Ee][Ss]|[Yy])
                                                ## request to continue forward with new stuff
                                                unset RESPONSE;
                                                IMPL_COMPLETE=${_TRUE};

                                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                ;;
                                            [Nn][Oo]|[Nn])
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                ## temporarily unset stuff
                                                unset METHOD_NAME;
                                                unset CNAME;
                                                unset SITE_HOSTNAME;
                                                unset RESPONSE;

                                                sleep "${MESSAGE_DELAY}"; reset; clear; exec ${MAIN_CLASS};

                                                exit 0;
                                                ;;
                                            *)
                                                unset SITE_HOSTNAME;
                                                unset RESPONSE;
                                                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                                                echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                ;;
                                        esac
                                    else
                                        reset; clear; break;
                                    fi
                                done
                            elif [ ${RET_CODE} -eq 94 ]
                            then
                                while true
                                do
                                    if [ -z "${IMPL_COMPLETE}" ] || [ "${IMPL_COMPLETE}" = "${_FALSE}" ]
                                    then
                                        reset; clear;

                                        echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        echo "\t$(grep -w ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                        echo "\t$(grep -w cert.mgmt.cert.implemented "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";
                                        echo "\t$(grep -w cert.mgmt.perform.more.tasks "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        read RESPONSE;

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                        case ${RESPONSE} in
                                            [Yy][Ee][Ss]|[Yy])
                                                ## request to continue forward with new stuff
                                                unset RESPONSE;
                                                IMPL_COMPLETE=${_TRUE};

                                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                ;;
                                            [Nn][Oo]|[Nn])
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                ## temporarily unset stuff
                                                unset METHOD_NAME;
                                                unset CNAME;
                                                unset SITE_HOSTNAME;
                                                unset RESPONSE;

                                                sleep "${MESSAGE_DELAY}"; reset; clear; exec ${MAIN_CLASS};

                                                exit 0;
                                                ;;
                                            *)
                                                unset SITE_HOSTNAME;
                                                unset RESPONSE;
                                                writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                                                echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                ;;
                                        esac
                                    else
                                        reset; clear; break;
                                    fi
                                done
                            else
                                ## an error occurred, we can start over
                                while true
                                do
                                    reset; clear;

                                    echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    echo "\t$(grep ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    echo "\t$(grep -w cert.mgmt.cert.application.failed "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";

                                    read RESPONSE;

                                    unset SITE_HOSTNAME;
                                    unset RET_CODE;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                    case ${RESPONSE} in
                                        [Yy][Ee][Ss]|[Yy])
                                            ## request to continue forward with new stuff
                                            unset RESPONSE;

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further renewal requests are required.";

                                            sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                            ;;
                                        [Nn][Oo]|[Nn])
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                            ## temporarily unset stuff
                                            unset METHOD_NAME;
                                            unset CNAME;

                                            sleep "${MESSAGE_DELAY}"; reset; clear; exec ${MAIN_CLASS};

                                            exit 0;
                                            ;;
                                        *)
                                            unset RESPONSE;
                                            writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                                            echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            ;;
                                    esac
                                done
                            fi
                        else
                            ## ret_code was blank ? weird
                            while true
                            do
                                reset; clear;

                                echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                echo "\t$(grep ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                echo "\t$(grep -w cert.mgmt.cert.application.failed "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";

                                read RESPONSE;

                                unset SITE_HOSTNAME;
                                unset RET_CODE;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                case ${RESPONSE} in
                                    [Yy][Ee][Ss]|[Yy])
                                        ## request to continue forward with new stuff
                                        unset RESPONSE;

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further renewal requests are required.";

                                        sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                        ;;
                                    [Nn][Oo]|[Nn])
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                        ## temporarily unset stuff
                                        unset METHOD_NAME;
                                        unset CNAME;

                                        sleep "${MESSAGE_DELAY}"; reset; clear; exec ${MAIN_CLASS};

                                        exit 0;
                                        ;;
                                    *)
                                        unset RESPONSE;
                                        writeLogEntry "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                                        echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        ;;
                                esac
                            done
                        fi
                    fi
                fi
            done
        else
            reset; clear; break;
        fi
    done

    unset REQUEST_OPTION;
    unset WEB_PROJECT_CODE;
    unset PLATFORM_CODE;
    unset MASTER_WEBSERVER;
    unset ENVIRONMENT_TYPE;
    unset SERVER_ROOT;
    unset CONTACT_CODE;
    unset OWNER_DIST;
    unset INSTANCE_NAME;
    unset CERTDB;
    unset RET_CODE;
    unset ACTIVE_DATACENTER;
    unset PRI_PLATFORM_CODE;
    unset SEC_PLATFORM_CODE;
    unset RETURN_CODE;
    unset REQ_DATACENTER;
    unset PLATFORM;
    unset MGMT_OP;
    unset CONTACT_NUMBER;
    unset RESPONSE;
    unset INPUT;
    unset CHANGE_NUM;
    unset PROCESS_DATE;
    unset SITE_HOSTNAME;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -s "${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin" ] && . "${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin";
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

typeset typeset METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${*}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && writeLogEntry "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

main;
