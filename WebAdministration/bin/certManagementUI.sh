#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  certManagementUI.sh
#         USAGE:  ./certManagementUI.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Processes a DNS failover by using information previously
#                 obtained by retrieve_site_info.sh
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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

## Application constants
CNAME="${THIS_CNAME}";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

function main
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    if [ ! -z "${IS_WEB_BUILD_ENABLED}" ] && [ "${IS_WEB_BUILD_ENABLED}" = "${_TRUE}" ]
    then
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

        ## don't allow ctrl-c to be sent
        trap "echo '$(grep -w system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SIGNAL%/Ctrl-C/")'; sleep "${MESSAGE_DELAY}"; reset; clear; continue " 1 2 3

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checking if application is already in operation...";

        reset; clear;

        ## get the request information
        while true
        do
            echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            echo "\t$(grep -w cert.mgmt.create.new.cert "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
            echo "\t$(grep -w cert.mgmt.renew.cert "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
            echo "\t$(grep -w cert.mgmt.exclude.cert "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
            echo "\t$(grep -w cert.mgmt.run.adhoc.report "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
            echo "\t$(grep -w system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

            ## get the requested project code/url or business unit
            read REQUEST_OPTION;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_OPTION -> ${REQUEST_OPTION}";

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

                    ## user opted to cancel, remove the lockfile
                    if [ -f ${APP_ROOT}/${APP_FLAG} ]
                    then
                        rm -rf ${APP_ROOT}/${APP_FLAG};
                    fi

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                    echo "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                    ## unset SVC_LIST, we dont need it now
                    unset SVC_LIST;

                    ## terminate this thread and return control to main
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    ## temporarily unset stuff
                    unset METHOD_NAME;
                    unset CNAME;

                    sleep "${MESSAGE_DELAY}"; reset; clear;

                    exec ${MAIN_CLASS};

                    exit 0;
                    ;;
                1)
                    ## request to create a new certificate for an existing site
                    ## this would be for a new cert, most commonly a new site too
                    echo "not yet implemented";
                    reset; clear; continue;
                    ;;
                2)
                    ## cert renewal. we need to know if we're just getting started or if we already have data.
                    reset; clear;

                    while true
                    do
                        echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                        echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                        echo "\t$(grep -w cert.mgmt.renew.provide.site "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                        read SITE_HOSTNAME;

                        typeset -l SITE_HOSTNAME;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME -> ${SITE_HOSTNAME}";

                        reset; clear;

                        echo "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                        case ${SITE_HOSTNAME} in
                            [Xx]|[Qq]|[Cc])
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process aborted";

                                echo "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

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
                                        MASTER_WEBSERVER=$(getPlatformInfo | grep -w $(echo ${PLATFORM_CODE} | awk '{print $1}') | \
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
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unsupported platform detected - Renewal process aborted";

                                            echo "$(grep -w unsupported.platform.detected "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

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
                                            echo "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_PROJECT_CODE -> ${WEB_PROJECT_CODE}";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEBSERVER_PLATFORM -> ${WEBSERVER_PLATFORM}";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLATFORM_CODE -> ${PLATFORM_CODE}";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENVIRONMENT_TYPE -> ${ENVIRONMENT_TYPE}";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "INSTANCE_NAME -> ${INSTANCE_NAME}";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MASTER_WEBSERVER -> ${MASTER_WEBSERVER}";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERTDB -> ${CERTDB}";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_ROOT -> ${SERVER_ROOT}";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONTACT_CODE -> ${CONTACT_CODE}";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OWNER_DIST -> ${OWNER_DIST}";

                                            if [ ! -z "${WEB_PROJECT_CODE}" ] && [ ! -z "${PLATFORM_CODE}" ] && [ ! -z "${CERTDB}" ] \
                                                && [ ! -z "${MASTER_WEBSERVER}" ] && [ ! -z "${MASTER_WEBSERVER}" ] && [ ! -z "${SERVER_ROOT}" ]
                                            then
                                                ## ok we have enough "INFO" to process the request
                                                ## determine what type of request to run -
                                                ## we could be:
                                                ## generating a csr
                                                ## applying a cert (pre-implementation)
                                                ## applying a cert (implementation)
                                                ## find out
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Determining operations..";
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/${LIB_DIRECTORY}/validators/validate_change_request.sh ${CERTDB}";

                                                unset METHOD_NAME;
                                                unset CNAME;

                                                MGMT_OP=$(${APP_ROOT}/${LIB_DIRECTORY}/validators/validate_change_request.sh ${WEBSERVER_PLATFORM} ${CERTDB});

                                                CNAME=$(basename ${0});
                                            typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MGMT_OP -> ${MGMT_OP}";

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
                                                ## couldnt get enough "INFO" to operate properly
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

                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No information was found for the provided hostname. Cannot continue.";

                                                echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            fi
                                        fi
                                    else
                                        unset SITE_HOSTNAME;

                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to locate configuration data for provided site hostname. Cannot continue.";

                                        echo "$(grep -w configuration.not.found.for.host "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    fi
                                else
                                    ## no hostname was provided
                                    unset SITE_HOSTNAME;

                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                    echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
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
                        echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                        echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                        echo "\t$(grep -w cert.mgmt.exception.provide.site "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                        read SITE_HOSTNAME;

                        typeset -l SITE_HOSTNAME;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME -> ${SITE_HOSTNAME}";

                        reset; clear;

                        echo "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                        case ${SITE_HOSTNAME} in
                            [Xx]|[Qq]|[Cc])
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process aborted";

                                echo "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

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
                                        . ${APP_ROOT}/${LIB_DIRECTORY}/updateExceptions.sh ${SSL_EXCEPTION_LIST};
                                        typeset -i RET_CODE=${?};

                                        unset SITE_HOSTNAME;

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                        if [ ${RET_CODE} -eq 0 ]
                                        then
                                            ## entry added, yay
                                            reset; clear;

                                            echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                            echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                            echo "\t$(grep -w exception.list.updated "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s^&LIST^${SSL_EXCEPTION_LIST}^")";

                                            read RESPONSE;

                                            reset; clear; main;
                                        else
                                            ## some "ERROR" occurred adding the entry
                                            reset; clear;

                                            echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                            echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                            echo "\t$(grep -w ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                            read INPUT;

                                            unset INPUT;
                                            unset RET_CODE;
                                            unset SITE_HOSTNAME;

                                            reset; clear; main;
                                        fi
                                    else
                                        unset SITE_HOSTNAME;

                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to locate configuration data for provided site hostname. Cannot continue.";

                                        echo "$(grep -w configuration.not.found.for.host "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    fi
                                else
                                    ## no hostname was provided
                                    unset SITE_HOSTNAME;

                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                    echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
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
                        echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                        echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                        echo "\t$(grep -w cert.mgmt.adhoc.provide.platform "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                        read PLATFORM_CODE;

                        typeset -u PLATFORM_CODE;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLATFORM_CODE -> ${PLATFORM_CODE}";

                        reset; clear;

                        echo "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                        case ${PLATFORM_CODE} in
                            [Xx]|[Qq]|[Cc])
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process aborted";

                                echo "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

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
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLATFORM -> ${PLATFORM}";

                                        if [ $(getPlatformInfo | grep -c ${PLATFORM}) != 0 ]
                                        then
                                            set -A OPERABLE_PLATFORMS ${OPERABLE_PLATFORMS[@]} ${PLATFORM};
                                        fi
                                    done

                                    unset PLATFORM;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPERABLE_PLATFORMS -> ${OPERABLE_PLATFORMS[@]}";

                                    if [ ! -z "${OPERABLE_PLATFORMS}" ]
                                    then
                                        ## ok good, get webservers
                                        for PLATFORM in ${OPERABLE_PLATFORMS[@]}
                                        do
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLATFORM -> ${PLATFORM}";

                                            set -A WEBSERVER_NAMES ${WEBSERVER_NAMES[@]} $(getPlatformInfo | grep -w ${PLATFORM} | grep -v "#" | grep -v "none" | cut -d "|" -f 5 | sort | uniq | sed -e "s/,/ /g");
                                        done

                                        unset PLATFORM;

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEBSERVER_NAMES -> ${WEBSERVER_NAMES[@]}";

                                        if [ ! -z "${WEBSERVER_NAMES}" ]
                                        then
                                            while true
                                            do
                                                reset; clear;

                                                ## ask if we want a specific date
                                                echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                echo "\t$(grep -w cert.mgmt.adhoc.provide.days "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                                read REPORT_DAYS;

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REPORT_DAYS -> ${REPORT_DAYS}";

                                                reset; clear;

                                                echo "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                if [ ! -z "${REPORT_DAYS}" ]
                                                then
                                                    if [ "$(isNaN ${REPORT_DAYS})" = "${_TRUE}" ]
                                                    then
                                                        reset; clear; break;
                                                    else
                                                        ## not a number
                                                        unset REPORT_DAYS;

                                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided response is not numeric. Cannot continue.";

                                                        echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                    fi
                                                else
                                                    ## input was blank, use default
                                                    REPORT_DAYS=${VALIDATION_PERIOD};

                                                    reset; clear; break;
                                                fi
                                            done

                                            ## ask if we want to send a targetted email
                                            echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                            echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                            echo "\t$(grep -w cert.mgmt.adhoc.provide.email "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                            read TARGET_EMAIL;

                                            if [ -z "${TARGET_EMAIL}" ]
                                            then
                                                TARGET_EMAIL=${ALERT_CERTIFICATE_ADDRESS};
                                            fi

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_EMAIL -> ${TARGET_EMAIL}";

                                            reset; clear;

                                            echo "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command nohup ${APP_ROOT}/${LIB_DIRECTORY}/runMonitor.sh -m monitorCertificateDatabases -s \"$(echo ${WEBSERVER_NAMES[@]})\" -d $(returnEpochTime \"$(date +"%Y %m %d")\" ${REPORT_DAYS}) -a \"${TARGET_EMAIL}\" -e > /dev/null 2>&1 &";

                                            nohup ${APP_ROOT}/${LIB_DIRECTORY}/runMonitor.sh -m monitorCertificateDatabases -s "$(echo ${WEBSERVER_NAMES[@]})" -d $(returnEpochTime "$(date +"%Y %m %d")" ${REPORT_DAYS}) -a "${TARGET_EMAIL}" -e > /dev/null 2>&1 &

                                            reset; clear;

                                            unset WEBSERVER_NAMES;
                                            unset PLATFORM_CODE;
                                            unset REQUEST_OPTION;
                                            unset REPORT_DAYS;
                                            unset PLATFORM;
                                            unset OPERABLE_PLATFORMS;
                                            unset TARGET_EMAIL;

                                            echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                            echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                            echo "\t$(grep -w cert.mgmt.adhoc.report.running "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                            echo "\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                            read INPUT;

                                            reset; clear; break;
                                        else
                                            ## no webservers
                                            unset WEBSERVER_NAMES;

                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No webservers were found with requested platform code ${PLATFORM_CODE}. continue.";

                                            unset PLATFORM_CODE;

                                            echo "$(grep -w configuration.not.found.for.host "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        fi
                                    else
                                        ## no platform information was found
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No information was found with requested platform code ${PLATFORM_CODE}. continue.";

                                        unset PLATFORM_CODE;

                                        echo "$(grep -w configuration.not.found.for.host "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    fi
                                else
                                    ## platform code blank
                                    unset PLATFORM_CODE;

                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                    echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                fi
                                ;;
                        esac
                    done
                    ;;
                *)
                    unset SITE_HOSTNAME;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                    echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    ;;
            esac
        done
    else
        $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate management has not been enabled. Cannot continue.");

        echo "$(grep -w request.not.authorized "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        exec ${MAIN_CLASS};

        exit 0;
    fi
}

#===  FUNCTION  ===============================================================
#          NAME:  applyLocalCertificate
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function createCSR
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing via csrGenerationUI helper..";

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
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break. CANCEL_REQ->${CANCEL_REQ}, ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}. Breaking..";

            ## put methodname and cname back
        typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;
            CNAME=$(basename ${0});

            reset; clear; main;
        elif [ ! -z "${CSR_COMPLETE}" ] && [ "${CSR_COMPLETE}" = "${_TRUE}" ]
        then
            ## record has been added successfully through the helper
            ## ask if we want to add additional records to the zone
            ## put methodname and cname back
        typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;
            CNAME=$(basename ${0});

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
                echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                echo "\t$(grep -w cert.mgmt.perform.more.tasks "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                read RESPONSE;

                unset SITE_HOSTNAME;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                case ${RESPONSE} in
                    [Yy][Ee][Ss]|[Yy])
                        ## request to continue forward with new stuff
                        unset RESPONSE;
                        unset CSR_COMPLETE;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further processing requested.";

                        sleep "${MESSAGE_DELAY}"; reset; clear; main;
                        ;;
                    [Nn][Oo]|[Nn])
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;
                        unset RESPONSE;

                        sleep "${MESSAGE_DELAY}"; reset; clear; exec ${MAIN_CLASS};

                        exit 0;
                        ;;
                    *)
                        unset RESPONSE;
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                        echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
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
            . ${APP_ROOT}/${LIB_DIRECTORY}/helpers/ui/csrGenerationUI.sh;
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
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing via certRenewalUI helper..";

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
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break. CANCEL_REQ->${CANCEL_REQ}, ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}. Breaking..";

            ## put methodname and cname back
        typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;
            CNAME=$(basename ${0});

            reset; clear; main;
        elif [ ! -z "${PREIMP_COMPLETE}" ] && [ "${PREIMP_COMPLETE}" = "${_TRUE}" ]
        then
            ## record has been added successfully through the helper
            ## ask if we want to add additional records to the zone
            ## put methodname and cname back
        typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;
            CNAME=$(basename ${0});

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
                echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                echo "\t$(grep -w cert.mgmt.perform.more.tasks "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                read RESPONSE;

                unset SITE_HOSTNAME;
                unset PREIMP_COMPLETE;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                case ${RESPONSE} in
                    [Yy][Ee][Ss]|[Yy])
                        ## request to continue forward with new stuff
                        unset RESPONSE;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further processing requested.";

                        sleep "${MESSAGE_DELAY}"; reset; clear; main;
                        ;;
                    [Nn][Oo]|[Nn])
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;
                        unset RESPONSE;

                        sleep "${MESSAGE_DELAY}"; reset; clear; exec ${MAIN_CLASS};

                        exit 0;
                        ;;
                    *)
                        unset RESPONSE;
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                        echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
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
            . ${APP_ROOT}/${LIB_DIRECTORY}/helpers/ui/certRenewalUI.sh;
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
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing via implementCertUI helper..";

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
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break. CANCEL_REQ->${CANCEL_REQ}, ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}. Breaking..";

            ## put methodname and cname back
        typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;
            CNAME=$(basename ${0});

            reset; clear; main;
        elif [ ! -z "${PREIMP_COMPLETE}" ] && [ "${PREIMP_COMPLETE}" = "${_TRUE}" ]]
        then
            ## record has been added successfully through the helper
            ## ask if we want to add additional records to the zone
            ## put methodname and cname back
        typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;
            CNAME=$(basename ${0});

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
                echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                echo "\t$(grep -w cert.mgmt.cert.applied "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";
                echo "\t$(grep -w cert.mgmt.perform.more.tasks "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                read RESPONSE;

                unset SITE_HOSTNAME;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                case ${RESPONSE} in
                    [Yy][Ee][Ss]|[Yy])
                        ## request to continue forward with new stuff
                        unset RESPONSE;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further processing requested.";

                        IMPL_COMPLETE=${_TRUE};

                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                        ;;
                    [Nn][Oo]|[Nn])
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                        ## temporarily unset stuff
                        unset METHOD_NAME;
                        unset CNAME;
                        unset RESPONSE;

                        sleep "${MESSAGE_DELAY}"; reset; clear; exec ${MAIN_CLASS};

                        exit 0;
                        ;;
                    *)
                        unset RESPONSE;
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                        echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
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
            . ${APP_ROOT}/${LIB_DIRECTORY}/helpers/ui/implementCertUI.sh;
        fi
    done
}

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin ] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

main;
