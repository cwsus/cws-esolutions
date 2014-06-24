#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  addInstanceUI.sh
#         USAGE:  ./backout_change_ui.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
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

## Application constants
CNAME="${THIS_CNAME}";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; printf "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

trap "print '$(grep -w system.trap.signals "${SYSTEM_MESSAGES}"| grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SIGNAL%/Ctrl-C/")'; sleep "${MESSAGE_DELAY}"; reset; clear; continue " 1 2 3

#===  FUNCTION  ===============================================================
#          NAME:  main
#   DESCRIPTION:  Main entry point for application. Currently, it is configured
#                 to run both interactively and non-interactively, however, the
#                 non-interactive functionality has not yet been implemented.
#    PARAMETERS:  none
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function main
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";
    local RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    if [[ ! -z "${IS_WEB_BUILD_ENABLED}" && "${IS_WEB_BUILD_ENABLED}" != "${_TRUE}" ]]
    then
        $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Web builds has not been enabled. Cannot continue.");

        print "$(grep -w request.not.authorized "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        exec ${MAIN_CLASS};

        exit 0;
    fi

    while true
    do
        reset; clear;

        print "\n";
        print "\t\t+-------------------------------------------------------------------+";
        print "\t\t               WELCOME TO \E[0;31m $(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<plugin.application.title\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g') \033[0m";
        print "\t\t+-------------------------------------------------------------------+";
        print "\t\tSystem Type         : \E[0;36m ${SYSTEM_HOSTNAME} \033[0m";
        print "\t\tSystem Uptime       : \E[0;36m ${SYSTEM_UPTIME} \033[0m";
        print "\t\tUser                : \E[0;36m ${IUSER_AUDIT} \033[0m";
        print "\t\t+-------------------------------------------------------------------+";
        print "";
        print "\t\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.available.options\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<createsite.application.title\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<createsite.provide.hostname\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read SITE_HOSTNAME;

        typeset -l SITE_HOSTNAME;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME -> ${SITE_HOSTNAME}";

        reset; clear;

        print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        case ${SITE_HOSTNAME} in
            [Xx]|[Qq]|[Cc])
                unset SITE_HOSTNAME;

                ## user opted to cancel, remove the lockfile
                if [ -f ${APP_ROOT}/${APP_FLAG} ]
                then
                    rm -rf ${APP_ROOT}/${APP_FLAG};
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

                print "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

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
            *.*)
                if [ ! -z "${SITE_HOSTNAME}" ]
                then
                    ## check to see if this is an in-process build
                    SERVER_FOUND=0;

                    for SERVER_BUILD in $(find ${APP_ROOT}/${BUILD_TMP_DIR} -type f -name ${IPLANET_SERVER_CONFIG})
                    do
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Searching for ${SITE_HOSTNAME} in ${SERVER_BUILD} ..";

                        if [ $(grep -c ${SITE_HOSTNAME} ${SERVER_BUILD}) != 0 ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_HOSTNAME} found in ${SERVER_BUILD}";

                            SERVER_PATH=${SERVER_BUILD};

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_PATH -> ${SERVER_PATH}";

                            (( SERVER_FOUND += 1 ));
                        fi
                    done

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_PATH -> ${SERVER_PATH}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_FOUND -> ${SERVER_FOUND}";

                    if [ ${SERVER_FOUND} -ne 0 ]
                    then
                        if [ ${SERVER_FOUND} -gt 1 ]
                        then
                            ## multiple servers were found with this hostname
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Multiple builds were found with urlhost ${SITE_HOSTNAME}. Faulting build - cleaning up.";

                            BUILD_FAULTED=${_TRUE};
                        else
                            ## get the information required to install
                            PROJECT_CODE=$(grep ${PROJECT_CODE_IDENTIFIER} ${SERVER_PATH} | awk '{print $3}' | cut -d "\"" -f 2);
                            SITE_HOSTNAME=$(grep ${SITE_HOSTNAME_IDENTIFIER} ${SERVER_PATH} | awk '{print $3}' | cut -d "\"" -f 2);
                            PLATFORM_CODE=$(grep ${PLATFORM_CODE_IDENTIFIER} ${SERVER_PATH} | awk '{print $3}' | cut -d "\"" -f 2);
                            SERVER_ID=$(grep ${SERVERID_IDENTIFIER} ${SERVER_PATH} | awk '{print $3}' | cut -d "\"" -f 2);

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROJECT_CODE -> ${PROJECT_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME -> ${SITE_HOSTNAME}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLATFORM_CODE -> ${PLATFORM_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_ID -> ${SERVER_ID}";

                            if [ ! z ${PROJECT_CODE} ] && [ ! -z "${SITE_HOSTNAME}" ] && [ ! -z "${PLATFORM_CODE}" ] && [ ! -z "${SERVER_ID}" ]
                            then
                                while true
                                do
                                    ## find out if we want to install
                                    print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    print "\t\t\t$(grep -w createsite.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    print "\t$(grep -w createsite.install.server "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                    read INSTALL_SERVER;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "INSTALL_SERVER -> ${INSTALL_SERVER}";

                                    print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                    case ${INSTALL_SERVER} in
                                        [Yy][Ee][Ss]|[Yy])
                                            reset; clear;

                                            while true
                                            do
                                                print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                print "\t\t\t$(grep -w createsite.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                print "\t$(grep -w system.provide.changenum "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                                read CHANGE_NUM;

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";

                                                reset; clear;

                                                print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                if [[ ${CHANGE_NUM} == [Ee] ]] || [ $(${APP_ROOT}/${LIB_DIRECTORY}/validators/validate_change_ticket.sh ${CHANGE_NUM}) -ne 0 ]
                                                then
                                                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A change was attempted with an invalid change order by ${IUSER_AUDIT}. Change request was ${CHANGE_NUM}.";
                                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid change control was provided. A valid change control number is required to process the request.";

                                                    unset CHANGE_NUM;

                                                    print "$(grep -w change.control.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                else
                                                    reset; clear;

                                                    print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Beginning installation of ${SERVER_ID} ..";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/${LIB_DIRECTORY}/runAddInstance.sh -s ${SERVER_ID} -p ${PLATFORM_CODE} -P ${PROJECT_CODE} -w ${WS_PLATFORM} -c ${CHANGE_NUM} -e ..";

                                                    ## call out to the installer
                                                    unset METHOD_NAME;
                                                    unset CNAME;
                                                    unset RET_CODE;
                                                    unset RETURN_CODE;

                                                    . ${APP_ROOT}/${LIB_DIRECTORY}/runAddInstance.sh -s ${SERVER_ID} -p ${PLATFORM_CODE} -P ${PROJECT_CODE} -w ${WS_PLATFORM} -c ${CHANGE_NUM} -e;
                                                    typeset -i RET_CODE=${?};

                                                    CNAME=$(basename ${0});
                                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
local METHOD_NAME="${CNAME}#${0}";

                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                                    if [ ${RET_CODE} -eq 0 ]
                                                    then
                                                        ## installation complete.
                                                        unset RET_CODE;
                                                        unset VHOST_NAME;
                                                        unset ENABLE_WEBSPHERE;
                                                        unset IS_ACTIVE;
                                                        unset SSL_PORTNUM;
                                                        unset NOSSL_PORTNUM;
                                                        unset SITE_BUILD_TYPE;
                                                        unset SERVER_TYPE;
                                                        unset WEBSERVER_NAMES;
                                                        unset PROJECT_CODE;
                                                        unset PLATFORM_CODE;
                                                        unset SITE_HOSTNAME;
                                                        unset PORT_CONFIRMATION;
                                                        unset CHANGE_NUM;
                                                        unset INSTALL_SERVER;
                                                        unset CHANGE_NUM;

                                                        while true
                                                        do
                                                            reset; clear;

                                                            print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                            print "\t\t\t$(grep -w createsite.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                            print "\t$(grep -w createsite.installation.complete "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                            print "\t$(grep -w createsite.perform.more.tasks "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                                            read RESPONSE;

                                                            print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                                            case ${RESPONSE} in
                                                                [Yy][Ee][Ss]|[Yy])
                                                                    ## request to continue forward with new stuff
                                                                    unset RESPONSE;
                                                                    unset BUILD_COMPLETE;

                                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further processing requested.";

                                                                    sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                                                    ;;
                                                                [Nn][Oo]|[Nn])
                                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                                    ## request if we want to install here
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

                                                                    print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                                    ;;
                                                            esac
                                                        done
                                                    else
                                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred processing installation for ${SERVER_ID}. RET_CODE -> ${RET_CODE}.";

                                                        unset VHOST_NAME;
                                                        unset ENABLE_WEBSPHERE;
                                                        unset IS_ACTIVE;
                                                        unset SSL_PORTNUM;
                                                        unset NOSSL_PORTNUM;
                                                        unset SITE_BUILD_TYPE;
                                                        unset SERVER_TYPE;
                                                        unset WEBSERVER_NAMES;
                                                        unset PROJECT_CODE;
                                                        unset PLATFORM_CODE;
                                                        unset SITE_HOSTNAME;
                                                        unset PORT_CONFIRMATION;
                                                        unset CHANGE_NUM;
                                                        unset INSTALL_SERVER;

                                                        while true
                                                        do
                                                            print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                            print "\t\t\t$(grep -w createsite.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                            print "\t$(grep -w ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                            print "\t$(grep -w createsite.installation.failed "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                            print "\t$(grep -w createsite.perform.more.tasks "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                                            read RESPONSE;

                                                            unset RET_CODE;

                                                            print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                                            case ${RESPONSE} in
                                                                [Yy][Ee][Ss]|[Yy])
                                                                    ## request to continue forward with new stuff
                                                                    unset RESPONSE;
                                                                    unset BUILD_COMPLETE;

                                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further processing requested.";

                                                                    sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                                                    ;;
                                                                [Nn][Oo]|[Nn])
                                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                                    ## request if we want to install here
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

                                                                    print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                                    ;;
                                                            esac
                                                        done
                                                    fi
                                                fi
                                            done
                                            ;;
                                        [Nn][Oo]|[Nn])
                                            unset RET_CODE;
                                            unset VHOST_NAME;
                                            unset ENABLE_WEBSPHERE;
                                            unset IS_ACTIVE;
                                            unset SSL_PORTNUM;
                                            unset NOSSL_PORTNUM;
                                            unset SITE_BUILD_TYPE;
                                            unset SERVER_TYPE;
                                            unset WEBSERVER_NAMES;
                                            unset PROJECT_CODE;
                                            unset PLATFORM_CODE;
                                            unset SITE_HOSTNAME;
                                            unset PORT_CONFIRMATION;

                                            while true
                                            do
                                                print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                print "\t\t\t$(grep -w createsite.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                print "\t$(grep -w createsite.perform.more.tasks "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                                read RESPONSE;

                                                print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                                case ${RESPONSE} in
                                                    [Yy][Ee][Ss]|[Yy])
                                                        ## request to continue forward with new stuff
                                                        unset RESPONSE;
                                                        unset BUILD_COMPLETE;

                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further processing requested.";

                                                        sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                                        ;;
                                                    [Nn][Oo]|[Nn])
                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                        ## request if we want to install here
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

                                                        print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                        ;;
                                                esac
                                            done
                                            ;;
                                        *)
                                            unset INSTALL_SERVER;

                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                                            print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            ;;
                                    esac
                                done
                            else
                                ## one or more identifiers were blank
                                ## consider the build incomplete and remove it
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more identifiers is incomplete. Faulting build - cleaning up.";

                                BUILD_FAULTED=${_TRUE};
                            fi
                        fi
                    fi

                    if [ -z "${BUILD_FAULTED}" ] || [ "${BUILD_FAULTED}" = "${_TRUE}" ]
                    then
                        unset BUILD_FAULTED;
                        unset PROJECT_CODE;
                        unset PLATFORM_CODE;
                        unset SERVER_ID;
                        unset SERVER_FOUND;
                        unset SERVER_BUILD;
                        unset SERVER_PATH;

                        ## validate that the hostname provided exists in website_defs. if it
                        ## doesnt, its not supported with this utility
                        if [ $(getWebInfo | grep -w ${SITE_HOSTNAME} | wc -l) != 0 ]
                        then
                            ## site already exists, "ERROR" out
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A site with hostname ${SITE_HOSTNAME} already exists. Cannot continue.";

                            unset SITE_HOSTNAME;

                            print "$(grep -w site.already.exists "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        else
                            ## get the platform code
                            while true
                            do
                                reset; clear;

                                print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                print "\t\t\t$(grep -w createsite.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                print "\t$(grep -w createsite.provide.project.code "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                print "\t$(grep -w system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                read PROJECT_CODE;

                                typeset -u PROJECT_CODE;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROJECT_CODE -> ${PROJECT_CODE}";

                                reset; clear;

                                print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                case ${PROJECT_CODE} in
                                    [Xx]|[Qq]|[Cc])
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process aborted";

                                        print "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                        ## unset SVC_LIST, we dont need it now
                                        unset SITE_HOSTNAME;
                                        unset PROJECT_CODE;

                                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                        ;;
                                    *)
                                        ## accept the project code as-is. it is possible that it exists multiple times, and thats ok.
                                        if [ ! -z "${PROJECT_CODE}" ]
                                        then
                                            while true
                                            do
                                                reset; clear;

                                                print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                print "\t\t\t$(grep -w createsite.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                print "\t$(grep -w createsite.provide.platform.code "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                print "\t$(grep -w system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                read PLATFORM_CODE;

                                                typeset -u PLATFORM_CODE;

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLATFORM_CODE -> ${PLATFORM_CODE}";

                                                reset; clear;

                                                print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                case ${PLATFORM_CODE} in
                                                    [Xx]|[Qq]|[Cc])
                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process aborted";

                                                        print "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                        ## unset SVC_LIST, we dont need it now
                                                        unset SITE_HOSTNAME;
                                                        unset PLATFORM_CODE;
                                                        unset PROJECT_CODE;

                                                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                        ;;
                                                    *)
                                                        if [ ! -z "${PLATFORM_CODE}" ]
                                                        then
                                                            ## make sure it exists..
                                                            if [ $(getPlatformInfo | grep -c ${PLATFORM_CODE}) != 0 ]
                                                            then
                                                                ## ok good, get webservers
                                                                WEBSERVER_NAMES=$(getPlatformInfo | grep -w ${PLATFORM_CODE} | grep -v "#" | grep -v "none" | cut -d "|" -f 5 | sort | uniq | sed -e "s/,/ /g");

                                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEBSERVER_NAMES -> ${WEBSERVER_NAMES}";

                                                                if [ ! -z "${WEBSERVER_NAMES}" ]
                                                                then
                                                                    ## xlnt, we have web names. lets keep going
                                                                    ## find out what kind of server we're building
                                                                    ## non-ssl, ssl, or both
                                                                    while true
                                                                    do
                                                                        reset; clear;

                                                                        print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                                        print "\t\t\t$(grep -w createsite.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                                        print "\t$(grep -w createsite.provide.server.type "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                                        print "$(grep -w createsite.server.type.ssl "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                                        print "$(grep -w createsite.server.type.nossl "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                                        print "$(grep -w createsite.server.type.both "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                                        print "$(grep -w system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                                        read SERVER_TYPE;

                                                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_TYPE -> ${SERVER_TYPE}";

                                                                        reset; clear;

                                                                        print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                                        case ${SERVER_TYPE} in
                                                                            [Xx]|[Qq]|[Cc])
                                                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process aborted";

                                                                                print "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                                                ## unset SVC_LIST, we dont need it now
                                                                                unset SITE_HOSTNAME;
                                                                                unset PLATFORM_CODE;
                                                                                unset PROJECT_CODE;
                                                                                unset SERVER_TYPE;
                                                                                unset WEBSERVER_NAMES;

                                                                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                                                ;;
                                                                            1)
                                                                                ## build up a new ssl-only website
                                                                                SITE_BUILD_TYPE=${BUILD_TYPE_SSL};

                                                                                buildServerInstance;
                                                                                ;;
                                                                            2)
                                                                                SITE_BUILD_TYPE=${BUILD_TYPE_NOSSL};

                                                                                buildServerInstance;
                                                                                ;;
                                                                            3)
                                                                                SITE_BUILD_TYPE=${BUILD_TYPE_BOTH};

                                                                                buildServerInstance;
                                                                                ;;
                                                                            *)
                                                                                unset SERVER_TYPE;

                                                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                                                                print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                                                ;;
                                                                        esac
                                                                    done
                                                                else
                                                                    ## no webservers
                                                                    unset WEBSERVER_NAMES;

                                                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No webservers were found with requested platform code ${PLATFORM_CODE}. continue.";

                                                                    unset PLATFORM_CODE;

                                                                    print "$(grep -w configuration.not.found.for.host "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                                fi
                                                            else
                                                                ## no platform information was found
                                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No information was found with requested platform code ${PLATFORM_CODE}. continue.";

                                                                unset PLATFORM_CODE;

                                                                print "$(grep -w configuration.not.found.for.host "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                            fi
                                                        else
                                                            ## platform code blank
                                                            unset PLATFORM_CODE;

                                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                                            print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                        fi
                                                        ;;
                                                esac
                                            done
                                        else
                                            ## project code blank
                                            unset PROJECT_CODE;

                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                                            print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        fi
                                        ;;
                                esac
                            done
                        fi
                    else
                        ## provided site hostname was blank
                        unset SITE_HOSTNAME;

                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No answer was provided. Cannot continue.";

                        print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi
                fi
                ;;
            *)
                unset SITE_HOSTNAME;

                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Site hostname not properly formatted. Cannot continue.";

                print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  buildServerInstance
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function buildServerInstance
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";
    local RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing via csrGenerationUI helper..";

    while true
    do
        if [ ! -z "${CANCEL_REQ}" ] && [ "${CANCEL_REQ}" = "${_TRUE}" ]
        then
            CNAME=$(basename ${0});
        local METHOD_NAME="${CNAME}#${0}";
local RETURN_CODE=0;

            unset RET_CODE;
            unset VHOST_NAME;
            unset ENABLE_WEBSPHERE;
            unset IS_ACTIVE;
            unset SSL_PORTNUM;
            unset NOSSL_PORTNUM;
            unset SITE_BUILD_TYPE;
            unset SERVER_TYPE;
            unset WEBSERVER_NAMES;
            unset PROJECT_CODE;
            unset PLATFORM_CODE;
            unset SITE_HOSTNAME;
            unset PORT_CONFIRMATION;

            ## user chose to cancel out of the subshell
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break. CANCEL_REQ->${CANCEL_REQ}, ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}. Breaking..";

            reset; clear; main;
        elif [ ! -z "${BUILD_COMPLETE}" ] && [ "${BUILD_COMPLETE}" = "${_TRUE}" ]
        then
            ## record has been added successfully through the helper
            ## ask if we want to add additional records to the zone
            ## put methodname and cname back
        local METHOD_NAME="${CNAME}#${0}";
local RETURN_CODE=0;
            CNAME=$(basename ${0});

            while true
            do
                ## find out if we want to install
                print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                print "\t\t\t$(grep -w createsite.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                print "\t$(grep -w createsite.install.server "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                read INSTALL_SERVER;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "INSTALL_SERVER -> ${INSTALL_SERVER}";

                print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                case ${INSTALL_SERVER} in
                    [Yy][Ee][Ss]|[Yy])
                        reset; clear;

                        while true
                        do
                            print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            print "\t\t\t$(grep -w createsite.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            print "\t$(grep -w system.provide.changenum "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                            read CHANGE_NUM;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";

                            reset; clear;

                            print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                            if [[ ${CHANGE_NUM} == [Ee] ]] || [ $(${APP_ROOT}/${LIB_DIRECTORY}/validators/validate_change_ticket.sh ${CHANGE_NUM}) -ne 0 ]
                            then
                                ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A change was attempted with an invalid change order by ${IUSER_AUDIT}. Change request was ${CHANGE_NUM}.";
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid change control was provided. A valid change control number is required to process the request.";

                                unset CHANGE_NUM;

                                print "$(grep -w change.control.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                            else
                                reset; clear;

                                print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Beginning installation of ${SERVER_ID} ..";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/${LIB_DIRECTORY}/runAddInstance.sh -s ${SERVER_ID} -p ${PLATFORM_CODE} -P ${PROJECT_CODE} -w ${WS_PLATFORM} -c ${CHANGE_NUM} -e ..";

                                ## call out to the installer
                                unset METHOD_NAME;
                                unset CNAME;
                                unset RET_CODE;
                                unset RETURN_CODE;

                                . ${APP_ROOT}/${LIB_DIRECTORY}/runAddInstance.sh -s ${SERVER_ID} -p ${PLATFORM_CODE} -P ${PROJECT_CODE} -w ${WS_PLATFORM} -c ${CHANGE_NUM} -e;
                                typeset -i RET_CODE=${?};

                                CNAME=$(basename ${0});
                            local METHOD_NAME="${CNAME}#${0}";
local RETURN_CODE=0;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                if [ ${RET_CODE} -eq 0 ]
                                then
                                    ## installation complete.
                                    unset RET_CODE;
                                    unset VHOST_NAME;
                                    unset ENABLE_WEBSPHERE;
                                    unset IS_ACTIVE;
                                    unset SSL_PORTNUM;
                                    unset NOSSL_PORTNUM;
                                    unset SITE_BUILD_TYPE;
                                    unset SERVER_TYPE;
                                    unset WEBSERVER_NAMES;
                                    unset PROJECT_CODE;
                                    unset PLATFORM_CODE;
                                    unset SITE_HOSTNAME;
                                    unset PORT_CONFIRMATION;
                                    unset CHANGE_NUM;
                                    unset INSTALL_SERVER;
                                    unset CHANGE_NUM;

                                    while true
                                    do
                                        reset; clear;

                                        print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        print "\t\t\t$(grep -w createsite.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        print "\t$(grep -w createsite.installation.complete "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                        print "\t$(grep -w createsite.perform.more.tasks "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        read RESPONSE;

                                        print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                        case ${RESPONSE} in
                                            [Yy][Ee][Ss]|[Yy])
                                                ## request to continue forward with new stuff
                                                unset RESPONSE;
                                                unset BUILD_COMPLETE;

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further processing requested.";

                                                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                                ;;
                                            [Nn][Oo]|[Nn])
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                ## request if we want to install here
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

                                                print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                ;;
                                        esac
                                    done
                                else
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred processing installation for ${SERVER_ID}. RET_CODE -> ${RET_CODE}.";

                                    unset VHOST_NAME;
                                    unset ENABLE_WEBSPHERE;
                                    unset IS_ACTIVE;
                                    unset SSL_PORTNUM;
                                    unset NOSSL_PORTNUM;
                                    unset SITE_BUILD_TYPE;
                                    unset SERVER_TYPE;
                                    unset WEBSERVER_NAMES;
                                    unset PROJECT_CODE;
                                    unset PLATFORM_CODE;
                                    unset SITE_HOSTNAME;
                                    unset PORT_CONFIRMATION;
                                    unset CHANGE_NUM;
                                    unset INSTALL_SERVER;

                                    while true
                                    do
                                        print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        print "\t\t\t$(grep -w createsite.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        print "\t$(grep -w ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                        print "\t$(grep -w createsite.installation.failed "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                        print "\t$(grep -w createsite.perform.more.tasks "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        read RESPONSE;

                                        unset RET_CODE;

                                        print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                        case ${RESPONSE} in
                                            [Yy][Ee][Ss]|[Yy])
                                                ## request to continue forward with new stuff
                                                unset RESPONSE;
                                                unset BUILD_COMPLETE;

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further processing requested.";

                                                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                                ;;
                                            [Nn][Oo]|[Nn])
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                                ## request if we want to install here
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

                                                print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                ;;
                                        esac
                                    done
                                fi
                            fi
                        done
                        ;;
                    [Nn][Oo]|[Nn])
                        reset; clear;

                        unset RET_CODE;
                        unset VHOST_NAME;
                        unset ENABLE_WEBSPHERE;
                        unset IS_ACTIVE;
                        unset SSL_PORTNUM;
                        unset NOSSL_PORTNUM;
                        unset SITE_BUILD_TYPE;
                        unset SERVER_TYPE;
                        unset WEBSERVER_NAMES;
                        unset PROJECT_CODE;
                        unset PLATFORM_CODE;
                        unset SITE_HOSTNAME;
                        unset PORT_CONFIRMATION;

                        while true
                        do
                            print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            print "\t\t\t$(grep -w createsite.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            print "\t$(grep -w createsite.perform.more.tasks "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                            read RESPONSE;

                            print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                            case ${RESPONSE} in
                                [Yy][Ee][Ss]|[Yy])
                                    ## request to continue forward with new stuff
                                    unset RESPONSE;
                                    unset BUILD_COMPLETE;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further processing requested.";

                                    sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                    ;;
                                [Nn][Oo]|[Nn])
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                    ## request if we want to install here
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

                                    print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    ;;
                            esac
                        done
                        ;;
                    *)
                        unset INSTALL_SERVER;

                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                        print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        ;;
                esac
            done
        else
            ## we hardcode to go to add_a_record, although this probably isnt right.
            ## we could also go to ns. we go to A because if we use NS, then the
            ## nameserver this zone gets applied to really doesnt need it there,
            ## because another nameserver already has it and can do it on its own.
            ## for this reason, we dont ask.
            case ${SITE_BUILD_TYPE} in
                ${BUILD_TYPE_SSL}|${BUILD_TYPE_BOTH})
                    while true
                    do
                        if [ ! -z "${BUILD_COMPLETE}" ] && [ "${BUILD_COMPLETE}" = "${_TRUE}" ]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

                            break;
                        fi
                        reset; clear;

                        print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                        print "\t\t\t$(grep -w createsite.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                        print "\t$(grep -w cert.mgmt.provide.contact.number "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                        read CONTACT_NUMBER;
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONTACT_NUMBER -> ${CONTACT_NUMBER}";

                        reset; clear;

                        print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                        ## validate it
                        case ${CONTACT_NUMBER} in
                            [Xx]|[Qq]|[Cc])
                                reset; clear;
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to cancel.";

                                unset CONTACT_NUMBER;

                                print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                print "\t\t\t$(grep -w createsite.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                print "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                CANCEL_REQ=${_TRUE};

                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                ;;
                            *)
                                if [ $(${APP_ROOT}/${LIB_DIRECTORY}/validators/validate_tel_num.sh ${CONTACT_NUMBER}) -ne 0 ]
                                then
                                    reset; clear;
                                    ## number provided was invalid
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid telephone number was provided. A valid telephone number is required to process the request.";

                                    unset CONTACT_NUMBER;
                                    unset RET_CODE;
                                    unset RETURN_CODE;

                                    print "$(grep -w contact.number.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                else
                                    unset CNAME;
                                    unset METHOD_NAME;

                                    if [ "${SITE_BUILD_TYPE}" = "${BUILD_TYPE_SSL}" ]
                                    then
                                        . ${APP_ROOT}/${LIB_DIRECTORY}/helpers/ui/buildSSLSiteUI.sh;
                                    elif [ "${SITE_BUILD_TYPE}" = "${BUILD_TYPE_BOTH}" ]
                                    then
                                        . ${APP_ROOT}/${LIB_DIRECTORY}/helpers/ui/buildHybridSiteUI.sh;
                                    fi
                                fi
                                ;;
                        esac
                    done
                    ;;
                ${BUILD_TYPE_NOSSL})
                    unset METHOD_NAME;
                    unset CNAME;

                    . ${APP_ROOT}/${LIB_DIRECTORY}/helpers/ui/buildNoSSLSiteUI.sh;
                    ;;
            esac
        fi
    done
}

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin.sh ]] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

main;
