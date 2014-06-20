#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  certRenewalUI.sh.sh
#         USAGE:  ./certRenewalUI.sh.sh
#   DESCRIPTION:  Helper interface for add_record_ui. Pluggable, can be modified
#                 or copied for all allowed record types.
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
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; printf "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#          NAME:  implementCertificate
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function implementCertificate
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting subdomain CNAME record information..";

    trap "print '$(grep system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

    while true
    do
        if [ -z "${IMPL_COMPLETE}" ] || [ "${IMPL_COMPLETE}" = "${_FALSE}" ]
        then
            ## implement update to target webnodes
            ## call out to run_renewal
            ## we require a change order to operate
            while true
            do
                print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                print "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                print "\t$(grep -w system.provide.changenum "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                print "\t$(grep -w system.emergency.changenum "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                read CHANGE_NUM;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";

                reset; clear;

                print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                if [[ ${CHANGE_NUM} == [Ee] ]]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change specified as emergency..";
                    CHANGE_NUM="E-$(date +"%m-%d-%Y_%H:%M:%S")";
                fi

                ## validate the CR number
                if [ $(${APP_ROOT}/${LIB_DIRECTORY}/validators/validate_change_ticket.sh ${CHANGE_NUM}) -ne 0 ]
                then
                    ## change control provided was invalid
                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A change was attempted with an invalid change order by ${IUSER_AUDIT}. Change request was ${CHANGE_NUM}.";
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid change control was provided. A valid change control number is required to process the request.";

                    unset CHANGE_NUM;

                    print "$(grep -w change.control.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                else
                    ## we have a valid change ticket. we can continue
                    ## call out to run_renewal
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing information confirmed. Continuing..";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runCertRenewal.sh -d ${CERTDB} -n ${CERT_NICKNAME} -s ${SITE_HOSTNAME} -c ${CHANGE_NUM} -a -e..";

                    ## tmp unset
                    unset CNAME;
                    unset METHOD_NAME;

                    if [ "${ENVIRONMENT_TYPE}" = "${ENV_TYPE_PRD}" ]
                    then
                        unset METHOD_NAME;
                        unset CNAME;

                        . ${APP_ROOT}/${LIB_DIRECTORY}/runQuery.sh -u ${SITE_HOSTNAME} -e;
                        RET_CODE=${?}

                        CNAME=$(basename ${0});
                        local METHOD_NAME="${CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ACTIVE_DATACENTER -> ${ACTIVE_DATACENTER}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        unset RET_CODE;
                        unset RETURN_CODE;

                        if [ ! -z "${ACTIVE_DATACENTER}" ] && [ ${RET_CODE} -eq 0 ]
                        then
                            ## we have an active datacenter, re-order the server list
                            PRI_PLATFORM_CODE=$(getWebInfo | grep "${SITE_HOSTNAME}" | grep -v "#" | grep -v ${ACTIVE_DATACENTER} | \
                                cut -d "|" -f 2 | sort | uniq); ## get the platform code, if multiples spit with space
                            SEC_PLATFORM_CODE=$(getWebInfo | grep "${SITE_HOSTNAME}" | grep -v "#" | grep ${ACTIVE_DATACENTER} | \
                                cut -d "|" -f 2 | sort | uniq); ## get the platform code, if multiples spit with space

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRI_PLATFORM_CODE -> ${PRI_PLATFORM_CODE}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SEC_PLATFORM_CODE -> ${SEC_PLATFORM_CODE}";
                        else
                            while true
                            do
                                if [ -z "${IMPL_COMPLETE}" ] || [ "${IMPL_COMPLETE}" = "${_FALSE}" ]
                                then
                                    reset; clear;

                                    print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    print "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    print "\t$(grep -w cert.mgmt.provide.datacenter "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                    print "\t$(grep -w system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                    read REQ_DATACENTER;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQ_DATACENTER -> ${REQ_DATACENTER}";

                                    case ${REQ_DATACENTER} in
                                        [Xx]|[Qq]|[Cc])
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate renewal process canceled..";

                                            print "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

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
                                            if [ -z "$(printf ${AVAILABLE_DATACENTERS} | grep -i ${REQ_DATACENTER})" ]
                                            then
                                                ## selected datacenter is NOT valid
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Selected datacenter is not valid. Please utilize a different datacenter.";

                                                print "$(grep -w datacenter.not.configured "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%DATACENTER%/${REQ_DATACENTER}/")\n";

                                                ## unset SVC_LIST, we dont need it now
                                                unset REQ_DATACENTER;

                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            else
                                                ## get the platform code, if multiples split with space
                                                typeset -u PRI_PLATFORM_CODE=$(getWebInfo | grep "${SITE_HOSTNAME}" | grep -v "#" | grep -v $(printf ${REQ_DATACENTER} | cut -d "|" -f 2 | sort | uniq));
                                                ## get the platform code, if multiples split with space
                                                typeset -u SEC_PLATFORM_CODE=$(getWebInfo | grep "${SITE_HOSTNAME}" | grep -v "#" | grep $(printf ${REQ_DATACENTER} | cut -d "|" -f 2 | sort | uniq));

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRI_PLATFORM_CODE -> ${PRI_PLATFORM_CODE}";
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SEC_PLATFORM_CODE -> ${SEC_PLATFORM_CODE}";

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
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Now operating on platform ${PLATFORM} ..";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runCertRenewal.sh -d ${CERTDB} -s ${SITE_HOSTNAME} -w ${WEBSERVER_PLATFORM} -p ${PLATFORM} -c ${CHANGE_NUM} -a -e";

                            unset METHOD_NAME;
                            unset CNAME;

                            . ${APP_ROOT}/${LIB_DIRECTORY}/runCertRenewal.sh -d ${CERTDB} -s ${SITE_HOSTNAME} \
                                -w ${WEBSERVER_PLATFORM} -p ${PLATFORM} -c ${CHANGE_NUM} -a -e;
                            typeset -i RET_CODE=${?};

                            CNAME=$(basename ${0});
                            local METHOD_NAME="${CNAME}#${0}";

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                            if [ ! -z "${RET_CODE}" ] && [ ${RET_CODE} -eq 0 ]
                            then
                                ## backup datacenter was successfully updated.
                                ## request verification, and if good, continue forward.
                                while true
                                do
                                    if [ -z "${IMPL_COMPLETE}" ] || [ "${IMPL_COMPLETE}" = "${_FALSE}" ]
                                    then
                                        print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        print "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        print "\t$(grep -w cert.mgmt.cert.implemented "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/" -e "s/%REQ_DATACENTER%/$(printf ${PLATFORM} | cut -d "_" -f 1)/")";
                                        print "\t$(grep -w cert.mgmt.cert.verify "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        read RESPONSE;

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE} ..";

                                        case ${RESPONSE} in
                                            [Yy][Ee][Ss]|[Yy])
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received positive resonse. Continuing ..";
                                                ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate Renewal (implementation) by ${IUSER_AUDIT}: Site: ${SITE_HOSTNAME}; Certificate Database: ${CERTIFICATE_DATABASE_STORE}; Successfully implemented in $(printf ${PLATFORM} | cut -d "_" -f 1)";

                                                continue;
                                                ;;
                                            [Nn][Oo]|[Nn])
                                                ## either it didnt work right or we just dont want to do it right now
                                                ## TODO: if it didnt work right, lets start the backout process
                                                printf "failed";
                                                exit 1;
                                                ;;
                                            *)
                                                unset RESPONSE;
                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                                                print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                ;;
                                        esac
                                    else
                                        reset; clear; break;
                                    fi
                                done
                            else
                                ## backup datacenter failed. "ERROR" out
                                printf "failed";
                                exit 1;
                            fi
                        done
                    else
                        ## dev renewal
                        unset METHOD_NAME;
                        unset CNAME;

                        . ${APP_ROOT}/${LIB_DIRECTORY}/runCertRenewal.sh -d ${CERTDB} -s ${SITE_HOSTNAME} -w ${WEBSERVER_PLATFORM} -p ${PLATFORM_CODE} -c ${CHANGE_NUM} -a -e;
                        typeset -i RET_CODE=${?};

                        CNAME=$(basename ${0});
                        local METHOD_NAME="${CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

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

                                        print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        print "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        print "\t$(grep -w cert.mgmt.cert.implemented "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";
                                        print "\t$(grep -w cert.mgmt.perform.more.tasks "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        read RESPONSE;

                                        unset SITE_HOSTNAME;

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                        case ${RESPONSE} in
                                            [Yy][Ee][Ss]|[Yy])
                                                ## request to continue forward with new stuff
                                                unset RESPONSE;
                                                IMPL_COMPLETE=${_TRUE};

                                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                ;;
                                            [Nn][Oo]|[Nn])
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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
                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                                                print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
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

                                        print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        print "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        print "\t$(grep -w ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                        print "\t$(grep -w cert.mgmt.cert.implemented "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";
                                        print "\t$(grep -w cert.mgmt.perform.more.tasks "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        read RESPONSE;

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                        case ${RESPONSE} in
                                            [Yy][Ee][Ss]|[Yy])
                                                ## request to continue forward with new stuff
                                                unset RESPONSE;
                                                IMPL_COMPLETE=${_TRUE};

                                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                ;;
                                            [Nn][Oo]|[Nn])
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

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
                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                                                print "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                ;;
                                        esac
                                    else
                                        reset; clear; break;
                                    fi
                                done
                            else
                                ## an "ERROR" occurred, we can start over
                                while true
                                do
                                    reset; clear;

                                    print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    print "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    print "\t$(grep ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    print "\t$(grep -w cert.mgmt.cert.application.failed "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";

                                    read RESPONSE;

                                    unset SITE_HOSTNAME;
                                    unset RET_CODE;

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                    case ${RESPONSE} in
                                        [Yy][Ee][Ss]|[Yy])
                                            ## request to continue forward with new stuff
                                            unset RESPONSE;

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further renewal requests are required.";

                                            sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                            ;;
                                        [Nn][Oo]|[Nn])
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                            ## temporarily unset stuff
                                            unset METHOD_NAME;
                                            unset CNAME;

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
                        else
                            ## ret_code was blank ? weird
                            while true
                            do
                                reset; clear;

                                print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                print "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                print "\t$(grep ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                print "\t$(grep -w cert.mgmt.cert.application.failed "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";

                                read RESPONSE;

                                unset SITE_HOSTNAME;
                                unset RET_CODE;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RESPONSE -> ${RESPONSE}";

                                case ${RESPONSE} in
                                    [Yy][Ee][Ss]|[Yy])
                                        ## request to continue forward with new stuff
                                        unset RESPONSE;

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. Further renewal requests are required.";

                                        sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                        ;;
                                    [Nn][Oo]|[Nn])
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Renewal process completed. No further renewal requests performed.";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                        ## temporarily unset stuff
                                        unset METHOD_NAME;
                                        unset CNAME;

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

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin.sh ]] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

implementCertificate;

