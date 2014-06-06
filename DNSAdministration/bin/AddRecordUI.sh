#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  addRecordUI.sh
#         USAGE:  ./addRecordUI.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Processes a DNS failover by using information previously
#             obtained by retrieve_site_info.sh
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

## Application constants
[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ -z "${PLUGIN_ROOT_DIR}" && -f ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && echo "Failed to locate configuration data. Cannot continue." && exit 1;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

unset METHOD_NAME;
unset CNAME;

## check security
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
RET_CODE=${?};

[ ${RET_CODE} -ne 0 ] && echo "Security configuration does not allow the requested action." && exit ${RET_CODE} || unset RET_CODE;

trap "print '$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.trap.signals\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%SIGNAL%/Ctrl-C/")'; sleep "${MESSAGE_DELAY}"; reset; clear; continue " 1 2 3

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
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    if [ -z "${IS_DNS_RECORD_ADD_ENABLED}" ] || [ "${IS_DNS_RECORD_ADD_ENABLED}" != "${_TRUE}" ]
    then
        $(${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS zone additions has not been enabled. Cannot continue.");

        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<request.not.authorized\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

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
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.enter.business.unit\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read BIZ_UNIT;

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${BIZ_UNIT} in
            [Xx]|[Qq]|[Cc])
                ## user chose to cancel
                unset BIZ_UNIT;

                reset; clear;

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled \>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## close out this app and reload the main
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                exec ${MAIN_CLASS};

                exit 0;
                ;;
            *)
                if [ -z "${BIZ_UNIT}" ]
                then
                    ## business unit provided was blank
                    unset BIZ_UNIT;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                else
                    ## capitalize it
                    typeset -u BIZ_UNIT;

                    reset; clear;

                    ## go to another method here
                    provideProjectCode;
                fi
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

#===  FUNCTION  ===============================================================
#
#         NAME:  provideProjectCode
#  DESCRIPTION:  Main entry point for application.
#   PARAMETERS:  None
#      RETURNS:  0
#
#==============================================================================
function provideProjectCode
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        reset; clear;

        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.enter.prjcode\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
        
        read SITE_PRJCODE;

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${SITE_PRJCODE} in
            [Xx]|[Qq]|[Cc])
                reset; clear;
                unset SITE_PRJCODE;
                unset BIZ_UNIT;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS record add canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## terminate this thread and return control to main
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            *)
                ## we cant really validate a project code. as long as it isnt blank
                ## we'll use it.
                if [ -z "${SITE_PRJCODE}" ]
                then
                    reset; clear;
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No site project code was provided. Cannot continue.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                else
                    ## capitalize it
                    typeset -u SITE_PRJCODE;

                    reset; clear;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_PRJCODE -> ${SITE_PRJCODE}";

                    ## keep going
                    provideSiteHostname;
                fi
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

#===  FUNCTION  ===============================================================
#
#         NAME:  provideSiteHostname
#  DESCRIPTION:  Main entry point for application.
#   PARAMETERS:  None
#      RETURNS:  0
#
#==============================================================================
function provideSiteHostname
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.enter.hostname\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.enter.format.hostname\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.enter.format.allowed.tlds\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read SITE_HOSTNAME;

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        case ${SITE_HOSTNAME} in
            [Xx]|[Qq]|[Cc])
                reset; clear;

                ## unset variables
                unset SITE_PRJCODE;
                unset BIZ_UNIT;
                unset SITE_HOSTNAME;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            [Hh])
                ## we want to print out the available record type list
                print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<allowed.gtld.list\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                awk 'NR>17' ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_GTLD_LIST};

                print "\nsed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES}  | awk -F "=" '/\<allowed.cctld.list\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                awk 'NR>16' ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_GTLD_LIST};

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.continue.enter\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                read COMPLETE;

                case ${COMPLETE} in
                    *)
                        unset COMPLETE;
                        unset SITE_HOSTNAME;

                        reset; clear; continue;
                        ;;
                esac
                ;;
            *)
                ## we cant validate the hostname other than to say it has
                ## only two parts, the name and the tld. other than that,
                ## not much we can do here
                if [ -z "${SITE_HOSTNAME}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No site hostname was provided. Cannot continue.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                if [ $(echo ${SITE_HOSTNAME} | tr -dc "." | wc -c) -ne 1 ]
                then
                    $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "${SITE_HOSTNAME} is not properly formatted.");

                    unset SITE_HOSTNAME;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep ${MESSAGE_DELAY}; reset; clear; continue;
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_HOSTNAME -> ${SITE_HOSTNAME}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating TLD..";

                ## make sure we got a valid tld. we're only checking the gTLD's,
                ## for a list, see http://en.wikipedia.org/wiki/List_of_Internet_top-level_domains
                if [ $(grep -c $(echo ${SITE_HOSTNAME} | cut -d "." -f 2) ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_GTLD_LIST}) -ne 1 ] || \
                    [ $(grep -c $(echo ${SITE_HOSTNAME} | cut -d "." -f 2) ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_CCTLD_LIST}) -ne 1 ]
                then
                    $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "${SITE_HOSTNAME} is not properly formatted.");

                    unset SITE_HOSTNAME;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep ${MESSAGE_DELAY}; reset; clear; continue;
                fi

                ## make sure there isnt already a zone with this hostname
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling retrieve_service to ensure that no records exist with ${SITE_HOSTNAME}..";

                unset METHOD_NAME;
                unset CNAME;

                . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/retrieveServiceInfo.sh ${INTERNET_TYPE_IDENTIFIER} u,${SITE_HOSTNAME} chk-info;
                RET_CODE=${?};

                ## re-set our info
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                local METHOD_NAME="${CNAME}#${0}";
                CNAME="$(basename "${0}")";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution of retrieve_service complete. RET_CODE -> ${RET_CODE}";

                if [ ${RET_CODE} -eq 12 ] || [ ${RET_CODE} -eq 13 ]
                then
                    ## this zone doesnt yet exist, so we're safe to create.
                    ## now we need to get the associated change control. we
                    ## dont need it to create the zone other than for audit
                    ## purposes
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${SITE_HOSTNAME} does NOT already exist in the DNS infrastructure";

                    unset RET_CODE;
                    unset RETURN_CODE;

                    reset; clear;
                    ## continue
                    provideChangeControl;
                else
                    ## we already have a zone file with this hostname in it. we can't create a duplicate zone
                    ## it wont load, but we can add additional records to an existing zone
                    unset RET_CODE;
                    unset RETURN_CODE;
                    reset; clear;

                    while true
                    do
                        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_MESSAGES} | awk -F "=" '/\<add.zone.already.exists\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%HOSTNAME%/${SITE_HOSTNAME}/")";
                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                        read ADD_EXISTING;

                        reset; clear;

                        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} DEBUG ${METHOD_NAME} ${CNAME} ${LINENO} "ADD_EXISTING -> ${ADD_EXISTING}";

                        case ${ADD_EXISTING} in
                            [Yy][Ee][Ss]|[Yy])
                                ## yes, we're adding a new entry to an existing zone. take user to the
                                ## zone update ui to request info
                                unset RESPONSE;
                                unset RET_CODE;
                                unset RETURN_CODE;

                                ADD_EXISTING_RECORD=${_TRUE};

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add additional records to existing zone confirmed.";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_EXISTING_RECORD->${ADD_EXISTING_RECORD}";

                                ## need to capture change order number here
                                reset; clear;

                                ## continue
                                provideChangeControl;
                                ;;
                            [Nn][Oo]|[Nn])
                                $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "${SITE_HOSTNAME} already exists in the DNS infrastructure.");

                                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<add.zone.already.exists.no.add\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%HOSTNAME%/${SITE_HOSTNAME}/")";

                                unset RET_CODE;
                                unset RETURN_CODE;
                                unset SITE_HOSTNAME;

                                sleep ${MESSAGE_DELAY}; reset; clear; break;
                                ;;
                            [Xx]|[Qq]|[Cc])
                                ## cancel request
                                reset; clear;

                                ## unset variables
                                unset ADD_EXISTING;
                                unset SITE_HOSTNAME;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS add request canceled.";

                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                sleep ${MESSAGE_DELAY}; reset; clear; break;
                                ;;
                            *)
                                $(${LOGGER} ERROR ${METHOD_NAME} ${CNAME} ${LINENO} "${RESPONSE} is not valid.");

                                unset RESPONSE;

                                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                sleep ${MESSAGE_DELAY}; reset; clear; continue;
                                ;;
                        esac
                    done
                fi
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

#===  FUNCTION  ===============================================================
#
#         NAME:  provideChangeControl
#  DESCRIPTION:  Main entry point for application.
#   PARAMETERS:  None
#      RETURNS:  0
#
#==============================================================================
function provideChangeControl
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    while true
    do
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting change information..";

        ${PLUGIN_ROOT_DIR}/${BIN_DIRECTORY}/obtainChangeControl.sh;

        if [[ ! -z "${CANCEL_REQ}" && "${CANCEL_REQ}" = "${_TRUE}" ]]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failover process aborted";

            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

            ## unset SVC_LIST, we dont need it now
            unset SVC_LIST;

            ## terminate this thread and return control to main
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            ## temporarily unset stuff
            unset METHOD_NAME;
            unset CNAME;

            sleep ${MESSAGE_DELAY}; reset; clear; main;
        fi

        break;
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  addDomainAddress
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function addDomainAddress
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing via add_a_ui_helper to add root ip addresses..";

    while true
    do
        unset ADD_RECORDS;
        unset ADD_SUBDOMAINS;
        unset ADD_COMPLETE;
        unset CANCEL_REQ;

        while true
        do
            if [[ ! -z "${CANCEL_REQ}" && "${CANCEL_REQ}" = "${_TRUE}" ]]
            then
                unset BIZ_UNIT;
                unset SITE_HOSTNAME;
                unset SITE_PRJCODE;
                unset CHG_CTRL;

                ## user chose to cancel out of the subshell
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break. CANCEL_REQ->${CANCEL_REQ}, ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}. Breaking..";

                ## put methodname and cname back
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                local METHOD_NAME="${CNAME}#${0}";
                CNAME="$(basename "${0}")";

                reset; clear; main;
            elif [[ ! -z "${ADD_COMPLETE}" && "${ADD_COMPLETE}" = "${_TRUE}" ]]
            then
                ## record has been added successfully through the helper
                ## ask if we want to add additional records to the zone
                ## put methodname and cname back
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                local METHOD_NAME="${CNAME}#${0}";
                CNAME="$(basename "${0}")";

                while true
                do
                    unset ADD_COMPLETE;

                    reset; clear;

                    print "\t\t\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.zone.build.complet\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%ZONE_NAME%/${SITE_HOSTNAME}/")\n";

                    read ANSWER;

                    reset; clear;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                    case ${ANSWER} in
                        [Yy][Ee][Ss]|[Yy])
                            ## user wishes to add additional records to root of zone
                            ## make sure our variables are empty and break to restart
                            unset ANSWER;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add additional records confirmed. ADD_RECORDS has been set to true. Breaking..";

                            reset; clear; addZoneData;
                            ;;
                        [Nn][Oo]|[Nn])
                            ## user does not wish to add additional records to root zone
                            ## ask if user wishes to add subdomains to zone
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add additional records declined. Request for subdomains..";
                            unset ANSWER;

                            while true
                            do
                                reset; clear;

                                print "\t\t\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.subdomains\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%ZONE_NAME%/${SITE_HOSTNAME}/")\n";

                                read ANSWER;

                                reset; clear;

                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                case ${ANSWER} in
                                    [Yy][Ee][Ss]|[Yy])
                                        ## user wishes to now add subdomain records.
                                        ## process via add_records
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add subdomain records confirmed. ADD_SUBDOMAINS has been set to true. Breaking..";
                                        unset ANSWER;

                                        reset; clear; addSubdomainAddresses;
                                        ;;
                                    [Nn][Oo]|[Nn])
                                        ## user does not wish to add subdomain records
                                        ## this completes processing, send to reviewZone
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add subdomain records declined. ADD_SUBDOMAINS has been set to false. Breaking..";
                                        unset ANSWER;

                                        reset; clear; reviewZone;
                                        ;;
                                    *)
                                        ## no valid selection provided
                                        unset ANSWER;
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not received. Please try again";

                                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        ;;
                                esac
                            done
                            ;;
                        *)
                            ## no valid response provided
                            unset ANSWER;

                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not received. Please try again";

                            print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

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
                . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/helpers/ui/add_a_ui_helper.sh root;
            fi
        done
    done

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  addZoneData
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function addZoneData
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting zone-level record types..";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing zone-level record requests..";

    while true
    do
        unset ADD_RECORDS;
        unset ADD_SUBDOMAINS;
        unset ADD_COMPLETE;
        unset CANCEL_REQ;

        reset; clear;

        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.enter.record.type\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read RECORD_TYPE;

        reset; clear;
        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TYPE->${RECORD_TYPE}";

        case ${RECORD_TYPE} in
            [Xx]|[Qq]|[Cc])
                reset; clear;
                unset RECORD_TYPE;

                ## remove the files we just created
                rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS record add canceled..";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## terminate this thread and return control to main
                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                ;;
            [Hh])
                ## we want to print out the available record type list
                awk 'NR>16' ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_RECORD_LIST};

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.continue.enter\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
                read COMPLETE;

                case ${COMPLETE} in
                    *)
                        unset COMPLETE;
                        unset RECORD_TYPE;

                        reset; clear; continue;
                    ;;
                esac
                ;;
            *)
                ## validate the request
                if [ $(${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validate_record_type.sh ${RECORD_TYPE} ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_RECORD_LIST}) -eq 0 ]
                then
                    ## record type successfully validated. continue with request
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Obtained request for ${RECORD_TYPE}. Validating..";

                    while true
                    do
                        if [ ! -z "${CANCEL_REQ}" ] || [ ! -z "${ADD_RECORDS}" ] || [ ! -z "${ADD_SUBDOMAINS}" ]
                        then
                            ## user chose to cancel out of the subshell
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break. CANCEL_REQ->${CANCEL_REQ}, ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}. Breaking..";

                            ## put methodname and cname back
                            local METHOD_NAME="${CNAME}#${0}";
                            CNAME="$(basename "${0}")";

                            unset CANCEL_REQ;

                            break;
                        elif [[ ! -z "${ADD_COMPLETE}" && "${ADD_COMPLETE}" = "${_TRUE}" ]]
                        then
                            ## record has been added successfully through the helper
                            ## ask if we want to add additional records to the zone
                            ## put methodname and cname back
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                            local METHOD_NAME="${CNAME}#${0}";
                            CNAME="$(basename "${0}")";

                            while true
                            do
                                if [ ! -z "${ADD_RECORDS}" ] || [ ! -z "${ADD_SUBDOMAINS}" ]
                                then
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add additional records received. ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}. Breaking..";

                                    break;
                                fi

                                reset; clear;
                                print "\t\t\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.type.added\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%ZONE_NAME%/${SITE_HOSTNAME}/")\n";

                                read ANSWER;
                                reset; clear;
                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                case ${ANSWER} in
                                    [Yy][Ee][Ss]|[Yy])
                                        ## user wishes to add additional records to root of zone
                                        ## make sure our variables are empty and break to restart
                                        unset ANSWER;
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add additional records confirmed. ADD_RECORDS has been set to true. Breaking..";

                                        ADD_RECORDS=${_TRUE};
                                        reset; clear; break;
                                        ;;
                                    [Nn][Oo]|[Nn])
                                        ## user does not wish to add additional records to root zone
                                        ## ask if user wishes to add subdomains to zone
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add additional records declined. Request for subdomains..";
                                        unset ANSWER;

                                        while true
                                        do
                                            reset; clear;

                                            print "\t\t\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.subdomains\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%ZONE_NAME%/${SITE_HOSTNAME}/")\n";

                                            read ANSWER;

                                            reset; clear;

                                            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                            case ${ANSWER} in
                                                [Yy][Ee][Ss]|[Yy])
                                                    ## user wishes to now add subdomain records.
                                                    ## process via add_records
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add subdomain records confirmed. ADD_SUBDOMAINS has been set to true. Breaking..";
                                                    unset ANSWER;

                                                    reset; clear; addSubdomainAddresses;
                                                    ;;
                                                [Nn][Oo]|[Nn])
                                                    ## user does not wish to add subdomain records
                                                    ## this completes processing, send to reviewZone
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add subdomain records declined. ADD_SUBDOMAINS has been set to false. Breaking..";
                                                    unset ANSWER;

                                                    reset; clear; reviewZone;
                                                    ;;
                                                *)
                                                    ## no valid selection provided
                                                    unset ANSWER;

                                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not received. Please try again";

                                                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                    ;;
                                            esac
                                        done
                                        ;;
                                    *)
                                        ## no valid response provided
                                        unset ANSWER;
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not received. Please try again";

                                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        ;;
                                esac
                            done
                        else
                            ## unset methodname and cname
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing subshell lib/helpers/ui/add_${RECORD_TYPE}_ui_helper";

                            unset METHOD_NAME;
                            unset CNAME;

                            . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/helpers/ui/add_${RECORD_TYPE}_ui_helper.sh zone;
                        fi
                    done
                else
                    ## provided response was blank
                    reset; clear;
                    unset RECORD_TYPE;
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requested record type failed validation. Cannot continue.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                    ## terminate this thread and return control to main
                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi
                ;;
        esac
    done

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  addSubdomainAddresses
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function addSubdomainAddresses
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting subdomain record type..";

    while true
    do
        unset ADD_RECORDS;
        unset ADD_COMPLETE;
        unset ADD_SUBDOMAINS;

        reset; clear;

        print "\t\t\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.enter.record.type\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read RECORD_TYPE;

        reset; clear;
        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TYPE->${RECORD_TYPE}";

        case ${RECORD_TYPE} in
            [Xx]|[Qq]|[Cc])
                ## user chose to cancel
                reset; clear;
                unset RECORD_TYPE;

                ## remove the files we just created
                rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS record add canceled..";

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.request.canceled\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                ## terminate this thread and return control to main
                sleep "${MESSAGE_DELAY}"; reset; clear; main;
                ;;
            [Hh])
                ## we want to print out the available record type list
                awk 'NR>16' ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_RECORD_LIST};

                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.continue.enter\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";
                read COMPLETE;

                case ${COMPLETE} in
                    *)
                        unset COMPLETE;
                        unset RECORD_TYPE;

                        reset; clear; continue;
                    ;;
                esac
                ;;
            *)
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling validate_record_type.sh ${RECORD_TYPE} ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_RECORD_LIST}..";

                if [ $(${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validate_record_type.sh ${RECORD_TYPE} ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_RECORD_LIST}) -eq 0 ]
                then
                    ## unset return code
                    unset RETURN_CODE;

                    ## record type passed validation, continue
                    ## call pluggable helper interfaces
                    while true
                    do
                        if [[ ! -z "${CANCEL_REQ}" && "${CANCEL_REQ}" = "${_TRUE}" || ! -z "${ADD_RECORDS}" && "${ADD_RECORDS}" = "${_TRUE}" ]]
                        then
                            ## user chose to cancel out of the subshell
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to cancel. CANCEL_REQ->${CANCEL_REQ}";

                            unset CANCEL_REQ;

                            ## put methodname and cname back
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                            local METHOD_NAME="${CNAME}#${0}";

                            [ ${#0} -ne 8 ] && addSubdomainAddresses=$(echo ${0} | tr -dc '/' | wc -c); SPLIT=$((${SPLIT}+1)); CNAME=$(echo ${0} | cut -d "/" -f ${SPLIT}-) || CNAME=$(echo ${0} | sed -e 's|\.\/||g');

                            break;
                        elif [[ ! -z "${ADD_COMPLETE}" && "${ADD_COMPLETE}" = "${_TRUE}" ]]
                        then
                            ## record was successfully added. ask if we should add more
                            ## put methodname and cname back
                            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                            local METHOD_NAME="${CNAME}#${0}";
                            CNAME="$(basename "${0}")";

                            while true
                            do
                                reset; clear;

                                print "\t\t\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.record.type.added\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%ZONE_NAME%/${SITE_HOSTNAME}/")\n";

                                read ANSWER;

                                reset; clear;

                                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                case ${ANSWER} in
                                    [Yy][Ee][Ss]|[Yy])
                                        ## user wishes to add additional records to root of zone
                                        ## make sure our variables are empty and break to restart
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add additional records confirmed. ADD_RECORDS has been set to true. Breaking..";

                                        unset ANSWER;

                                        ADD_RECORDS=${_TRUE};
                                        reset; clear; break;
                                        ;;
                                    [Nn][Oo]|[Nn])
                                        ## user does not wish to add additional records to root zone
                                        ## ask if user wishes to add subdomains to zone
                                        unset ANSWER;

                                        if [ ! -z "${ADD_EXISTING_RECORD}" ] && [ "${ADD_EXISTING_RECORD}" = "${_TRUE}" ]
                                        then
                                            ## we were adding a new record to an existing zone
                                            ## theres nothing to review so we arent taking the
                                            ## user there. we're going to break out
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add additional records to existing zone canceled. Breaking..";

                                            unset ANSWER;
                                            unset SITE_HOSTNAME;
                                            unset CHANGE_NUM;
                                            unset RECORD_TYPE;

                                            reset; clear; main;
                                        else
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to add additional records declined. ADD_RECORDS has been set to false. Breaking..";

                                            reset; clear; reviewZone;
                                        fi
                                        ;;
                                    *)
                                        ## no valid response provided
                                        unset ANSWER;

                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not received. Please try again";

                                        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        ;;
                                esac
                            done
                        else
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing subshell lib/helpers/ui/add_${RECORD_TYPE}_ui_helper";

                            ## unset methodname and cname
                            unset METHOD_NAME;
                            unset CNAME;

                            . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/helpers/ui/add_${RECORD_TYPE}_ui_helper.sh subdomain;
                        fi
                    done
                else
                    reset; clear;

                    unset RECORD_TYPE;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No response was provided for record type.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                    ## terminate this thread and return control to main
                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi
                ;;
        esac
    done

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  reviewZone
#   DESCRIPTION:  Allows review and processing of the newly created zone file
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function reviewZone
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating operational zone..";

    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%ZONE%/${SITE_HOSTNAME}/")";

    ## temp unset
    unset CNAME;
    unset METHOD_NAME;

    . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/run_addition.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -e;
    RET_CODE=${?};

    ## re-set vars
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";
    CNAME="$(basename "${0}")";

    if [ ${RET_CODE} -eq 0 ]
    then
        unset RET_CODE;
        unset RETURN_CODE;

        ## operational zone file got created. continue..
        while true
        do
            reset; clear;

            print "\t\t\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.review.zone\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%ZONE%/${SITE_HOSTNAME}/")\n";

            read ANSWER;
            reset; clear;
            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

            case ${ANSWER} in
                [Yy][Ee][Ss]|[Yy])
                    reset; clear;

                    unset ANSWER;
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printing zonefile content..";

                    cat ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT}/${NAMED_ZONE_PREFIX}.$(echo ${SITE_HOSTNAME} | cut -d "." -f 1).${SITE_PRJCODE};

                    print "\n$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.review.accurate.zone.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%ZONE%/${SITE_HOSTNAME}/")";

                    read ANSWER;

                    reset; clear;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    while true
                    do
                        case ${ANSWER} in
                            [Yy][Ee][Ss]|[Yy])
                                ## zone was created and is accurate. send to master
                                unset ANSWER;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling send_zone to stage the files";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                                send_zone;
                                ;;
                            [Nn][Oo]|[Nn])
                                unset ANSWER;
                                ## we know something isnt right because
                                ## we were told so. ask if we should start
                                ## over or provide the user the option to
                                ## manually edit the file
                                while true
                                do
                                    reset; clear;

                                    print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.zone.inaccurate\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                                    print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.zone.inaccurate.restart\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                                    print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.zone.inaccurate.manual\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                    read ANSWER;
                                    reset; clear;
                                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                    case ${ANSWER} in
                                        1)
                                            ## user has chosen to restart the process
                                            ## clear all variables and send control to main
                                            unset ANSWER;

                                            sleep "${MESSAGE_DELAY}"; reset; clear; main;
                                            ;;
                                        2)
                                            ## user has chosen to manually update the file
                                            ## and correct any errors
                                            ## create a copy of the existing
                                            unset ANSWER;
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Manual update requested. Copying zone directory..";

                                            cp -R ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT} ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT}.mod;

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copy complete. Validating..";

                                            if [ -d ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT}.mod ]
                                            then
                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validation complete. Launching vi..";

                                                ## modify the primary datacenter
                                                vi ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT}.mod/${PRIMARY_DC}/${NAMED_ZONE_PREFIX}.$(echo ${SITE_HOSTNAME} | cut -d "." -f 1);
                                                RET_CODE=${?};

                                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Manual edits complete. Return code -> ${RET_CODE}";

                                                if [ ${RET_CODE} -eq 0 ]
                                                then
                                                    unset RET_CODE;
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksumming..";

                                                    MOD_FILE_CKSUM=$(cksum ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT}.mod/${PRIMARY_DC}/${NAMED_ZONE_PREFIX}.$(echo ${SITE_HOSTNAME} | cut -d "." -f 1) | awk '{print $1}');
                                                    OP_FILE_CKSUM=$(cksum ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT}/${PRIMARY_DC}/${NAMED_ZONE_PREFIX}.$(echo ${SITE_HOSTNAME} | cut -d "." -f 1) | awk '{print $1}');

                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MOD_FILE_CKSUM->${MOD_FILE_CKSUM}";
                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OP_FILE_CKSUM->${OP_FILE_CKSUM}";

                                                    if [ ${MOD_FILE_CKSUM} -eq ${OP_FILE_CKSUM} ]
                                                    then
                                                        ## no changes were detected, advise and ask if appropriate
                                                        while true;
                                                        do
                                                            reset; clear;

                                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksums match - no changes detected. Validating..";

                                                            print "\t\t\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.zone.no.changes.made\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";
                                                    
                                                            read ANSWER;

                                                            reset; clear;

                                                            print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                                            case ${ANSWER} in
                                                                [Yy][Ee][Ss]|[Yy])
                                                                    ## user has confirmed no changes were made and this is correct
                                                                    ## move forward with processing

                                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling send_zone to stage the files";
                                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                                                                    send_zone;
                                                                    ;;
                                                                [Nn][Oo]|[Nn])
                                                                    ## user has confirmed no changes were made and this is NOT correct
                                                                    ## reload into this method and restart the process
                                                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No file changes were detected. Confirmed this is incorrect.";

                                                                    print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.zone.changes.declined\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

                                                                    unset ANSWER;
                                                                    unset MOD_FILE_CKSUM;
                                                                    unset OP_FILE_CKSUM;
                                                                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                                    ;;
                                                                *)
                                                                    ## we need a yes or no response
                                                                    ## no valid selection found.
                                                                    ## advise and re-try
                                                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reponse ${ANSWER} invalid";

                                                                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                                                    unset ANSWER;
                                                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                                    ;;
                                                            esac
                                                        done
                                                    else
                                                        ## ok, so we know something was changed. we dont know what yet.
                                                        ## because we only modified the primary datacenter files, we
                                                        ## can run a diff and patch.
                                                        unset MOD_FILE_CKSUM;
                                                        unset OP_FILE_CKSUM;

                                                        diff ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT}/${PRIMARY_DC}/${NAMED_ZONE_PREFIX}.$(echo ${SITE_HOSTNAME} | cut -d "." -f 1) ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT}.mod/${PRIMARY_DC}/${NAMED_ZONE_PREFIX}.$(echo ${SITE_HOSTNAME} | cut -d "." -f 1) > ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/output;

                                                        if [ -s ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/output ]
                                                        then
                                                            patch ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT}/${PRIMARY_DC}/${NAMED_ZONE_PREFIX}.$(echo ${SITE_HOSTNAME} | cut -d "." -f 1) < ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/output;

                                                            ## file has been patched, verify
                                                            MOD_FILE_CKSUM=$(cksum ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT}.mod/${PRIMARY_DC}/${NAMED_ZONE_PREFIX}.$(echo ${SITE_HOSTNAME} | cut -d "." -f 1) | awk '{print $1}');
                                                            OP_FILE_CKSUM=$(cksum ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT}/${PRIMARY_DC}/${NAMED_ZONE_PREFIX}.$(echo ${SITE_HOSTNAME} | cut -d "." -f 1) | awk '{print $1}');

                                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Post-patch: MOD_FILE_CKSUM->${MOD_FILE_CKSUM}";
                                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Post-patch: OP_FILE_CKSUM->${OP_FILE_CKSUM}";

                                                            if [ ${MOD_FILE_CKSUM} -eq ${OP_FILE_CKSUM} ]
                                                            then
                                                                ## patches successfully applied and verified.
                                                                ## we can move forward.
                                                                ## TODO: this should then move into the secondary dc to
                                                                ## apply necessary changes
                                                                exit 0;
                                                            else
                                                                ## an error occurred while applying the patch.
                                                                ## TODO: error handle
                                                                exit 0;
                                                            fi
                                                        else
                                                            ## diff didnt produce an output file
                                                            ## TODO: error handle
                                                            exit 0;
                                                        fi
                                                    fi
                                                else
                                                    ## an error occurred in vi. show the error
                                                    ## and break, we'll start over.
                                                    ## TODO: error handle
                                                    exit 0;
                                                fi
                                            else
                                                ## an error occurred creating the mod directory.
                                                ## show an error and break, we'll start over.
                                                ## TODO: error handle
                                                exit 0;
                                            fi
                                            ;;
                                        *)
                                            ## no valid selection found.
                                            ## advise and re-try
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reponse ${ANSWER} invalid";

                                            print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                            unset ANSWER;

                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            ;;
                                    esac
                                done
                                ;;
                            *)
                                ## we need a yes or no here
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Reponse ${ANSWER} invalid";

                                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                                unset RET_CODE;
                                unset RETURN_CODE;
                                unset ANSWER;

                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                ;;
                        esac
                    done
                    ;;
                [Nn][Oo]|[Nn])
                    ## user chose not to review the zone - send it up
                    unset ANSWER;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling send_zone to stage the files";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                    send_zone;
                    ;;
                *)
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Invalid response received for request.";

                    print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}system.selection.invalid "${ERROR_MESSAGES}" | awk -F "=" '/\<remote_app_root\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    unset ANSWER;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    ;;
            esac
        done
    else
        ## return code from run_addition to create the operational zone was non-zero
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Return code from run_addition nonzero -> ${RET_CODE}";
        print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";

        unset RET_CODE;
        unset RETURN_CODE;

        exit 0;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  sendZone
#   DESCRIPTION:  Executes commands to send the created zone information
#             to the configured master nameserver
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function sendZone
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command run_addition.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -x -e..";

    ## temp unset
    unset METHOD_NAME;
    unset CNAME;

    . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/run_addition.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -x -e;
    RET_CODE=${?};

    ## reset vars
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";
    CNAME="$(basename "${0}")";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 || ${RET_CODE} -ne 66 || ${RET_CODE} -ne 52 ]]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone installation FAILED on node ${NAMED_MASTER}.";

        [ ! -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" "/${RET_CODE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g')";
        [ -z "${RET_CODE}" ] && print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<99\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        unset RET_CODE;
        unset RETURN_CODE;
        unset ANSWER;

        sleep "${MESSAGE_DELAY}"; reset; clear; return 1;
    fi

    ## 0 - all good
    ## 66 - validation failed
    ## 52 - reconfiguration failed
    if [ ${RET_CODE} -eq 52 ]
    then
        ## our zone installed just fine. server failed to reconfig with the new
        ## data, probably because of invalid syntax in a file.
        print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_ERROR_MESSAGES} | awk -F "=" '/\<possible.zone.syntax.error\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        sleep "${MESSAGE_DELAY}"; reset; clear;
    fi

    unset RET_CODE;
    unset RETURN_CODE;

    ## files were copied and decompressed successfully.
    ## we've also performed the necessary reloads and
    ## validated that the service exists on the master.
    ## at this point we should perform execution against
    ## our configured slaves (if any) and call it a day.
    if [ ${#DNS_SLAVES[@]} -ne 0 ]
    then
        ## make sure A is 0
        C=0;

        while [ ${C} -ne ${#DNS_SLAVES[@]} ]
        do
            reset; clear;

            print "$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.zone.send.slave\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

            ## send out to slave servers
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Sending zone to slave server ${DNS_SLAVES[${C}]}";

            ## temp unset
            unset METHOD_NAME;
            unset CNAME;

            . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/run_addition.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -s ${DNS_SLAVES[${C}]} -e;
            RET_CODE=${?};

            ## reset vars
            local METHOD_NAME="${CNAME}#${0}";
            CNAME="$(basename "${0}")";

            if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]]
            then
                ## something failed on the request. show the error code and continue.
                ## increment our error counter
                (( ERROR_COUNT += 1 ));
                
                unset RET_CODE;

                print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%ZONE%/${SITE_HOSTNAME}/" -e "s/%SERVER%/${DNS_SLAVES[${C}]}/")";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
            fi

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Changes successfully applied to ${DNS_SLAVES[${C}]}";

            (( C += 1 ));
        done

        ## make a zero again
        C=0;
    fi

    ## check the error count. if its not zero, something broke on something,
    ## so we leave our temp files in place for any manual operations that may
    ## be necessary.
    if [[ ${ERROR_COUNT} -ne 0 ]]
    then
        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES}slave.installation.possible.failure "${ERROR_MESSAGES}" | awk -F "=" '/\<remote_app_root\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        sleep "${MESSAGE_DELAY}";
    fi

    ## all processing successfully completed. we can remove our temp files
    [ -f ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz ] && \
        rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz;
    [ -f ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar.gz ] && \
        rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT}.tar;
    [ -d ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT} ] && rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT};

    while true
    do
        reset; clear;

        print "\t$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_SYSTEM_MESSAGES} | awk -F "=" '/\<add.zone.add.another\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        reset; clear;

        print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.pending.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        case ${ANSWER} in
            [Yy][Ee][Ss]|[Yy])
                ## user has selected to add more stuff
                ## set it up.
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "User has elected to add further data. Reloading..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset ANSWER;

                reset; clear; main;
                ;;
            [Nn][Oo]|[Nn])
                ## user does not wish to add more stuff
                ## redirect user back to main class
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "User has elected to add further data. Reloading..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset ANSWER;

                reset; clear;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

                exec ${MAIN_CLASS};
                exit 0;
                ;;
            *)
                ## we need a yes/no answer here
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Response provided was blank. Cannot continue.";

                ## unset variables
                unset ANSWER;

                print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

main;


return 0;
