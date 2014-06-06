#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  add_srv_ui_helper.sh
#         USAGE:  ./add_srv_ui_helper.sh
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

## Application constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[ -z "${PLUGIN_ROOT_DIR}" ] && exit 0;

[[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

trap "print '$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.trap.signals\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%SIGNAL%/Ctrl-C/")'; sleep "${MESSAGE_DELAY}"; reset; clear; continue " 1 2 3

#===  FUNCTION  ===============================================================
#          NAME:  add_ip_info
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#==============================================================================
function add_zone_helper
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting zone-level SRV record information..";

    ## need a bunch of information for SRV record types
    while true
    do
        if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

            break;
        fi

        reset; clear;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service type..";

        ## ask for the service type
        print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
        print "\t$(grep add.record.srv.type ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";
        print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        read SERVICE_TYPE;

        reset; clear;
        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service type received -> ${SERVICE_TYPE}";

        ## validate
        if [[ ${SERVICE_TYPE} == [Xx] || ${SERVICE_TYPE} == [Qq] || ${SERVICE_TYPE} == [Cc] ]]
        then
            ## user chose to cancel
            unset SERVICE_TYPE;
            unset RECORD_TYPE;

            CANCEL_REQ=${_TRUE};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
            reset; clear;
            print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
            sleep "${MESSAGE_DELAY}"; reset; clear; break;
        else
            if [ $(${PLUGIN_ROOT_DIR}/lib/validators/validateServiceRecordData.sh type ${SERVICE_TYPE}) -eq 0 ]
            then
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_TYPE->${SERVICE_TYPE}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service protocol..";

                ## service type is valid,
                ## continue with request
                while true
                do
                    if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                    then
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                        break;
                    fi

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service protocol..";

                    reset; clear;

                    ## ask for the service protocol
                    ## this can be tcp or udp
                    print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                    print "\t$(grep add.record.srv.protocol ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";
                    print "\t$(grep add.record.srv.valid.protocols ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";
                    print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                    read SERVICE_PROTO;

                    reset; clear;
                    print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service protocol received -> ${SERVICE_PROTO}";

                    ## validate the provided protocol
                    if [ ${SERVICE_PROTO} == [Xx] ] || [ ${SERVICE_PROTO} == [Qq] ] || [ ${SERVICE_PROTO} = [Cc] ]
                    then
                        ## user chose to cancel
                        unset SERVICE_TYPE;
                        unset SERVICE_PROTO;
                        unset RECORD_TYPE;

                        CANCEL_REQ=${_TRUE};

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                        reset; clear;
                        print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                    else
                        if [ $(${PLUGIN_ROOT_DIR}/lib/validators/validateServiceRecordData.sh protocol ${SERVICE_PROTO}) -eq 0 ]
                        then
                            ## protocol is valid, move forward
                            ## now we need to request the service name
                            while true
                            do
                                if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                                then
                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                                    break;
                                fi

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service name..";

                                reset; clear;

                                print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                print "\t$(grep add.record.alias ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/${RECORD_TYPE}/")";
                                print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                read SERVICE_NAME;

                                reset; clear;
                                print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service named received -> ${SERVICE_NAME}";

                                if [[ ${SERVICE_NAME} == [Xx] || ${SERVICE_NAME} == [Qq] || ${SERVICE_NAME} == [Cc] ]]
                                then
                                    ## user chose to cancel
                                    unset SERVICE_TYPE;
                                    unset SERVICE_PROTO;
                                    unset SERVICE_NAME;
                                    unset RECORD_TYPE;

                                    CANCEL_REQ=${_TRUE};

                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                                    reset; clear;
                                    print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                elif [ -z "${SERVICE_NAME}" ]
                                then
                                    ## no service name was provided, this is technically allowed,
                                    ## but we're going to dis-allow it because we want to know for
                                    ## sure what to add.
                                    unset SERVICE_NAME;
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No SERVICE_NAME was provided. Cannot continue.";

                                    reset; clear; continue;
                                else
                                    ## thats all the validation we do here. the name
                                    ## is entirely of the user's choosing and can be
                                    ## whatever they want
                                    ## ask for the TTL - default to 86400
                                    while true
                                    do
                                        if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                                        then
                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                                            break;
                                        fi

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service TTL..";

                                        reset; clear;

                                        print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                        print "\t$(grep add.record.srv.ttl ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";
                                        print "\t$(grep add.record.srv.default.ttl ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";

                                        read SERVICE_TTL;

                                        reset; clear;
                                        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service TTL received -> ${SERVICE_TTL}";

                                        if [[ ${SERVICE_NAME} == [Xx] || ${SERVICE_NAME} == [Qq] || ${SERVICE_NAME} == [Cc] ]]
                                        then
                                            ## user chose to cancel
                                            unset SERVICE_TYPE;
                                            unset SERVICE_PROTO;
                                            unset SERVICE_NAME;
                                            unset SERVICE_TTL;
                                            unset RECORD_TYPE;

                                            CANCEL_REQ=${_TRUE};

                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                                            reset; clear;
                                            print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                        else
                                            [ -z "${SERVICE_TTL}" ] && SERVICE_TTL=86400;

                                            if [ "$(isNaN ${SERVICE_TTL})" = "${_TRUE}" ]
                                            then
                                                ## service ttl passed validation or we defaulted
                                                ## to 86400
                                                ## now we need to ask for the priority.
                                                while true
                                                do
                                                    if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                                                    then
                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                                                        break;
                                                    fi

                                                    reset; clear;

                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service priority..";

                                                    print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                    print "\t$(grep add.record.priority ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/${RECORD_TYPE}/")";

                                                    read SERVICE_PRIORITY;

                                                    reset; clear;
                                                    print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service priority received -> ${SERVICE_PRIORITY}";

                                                    if [[ ${SERVICE_PRIORITY} == [Xx] || ${SERVICE_PRIORITY} == [Qq] || ${SERVICE_PRIORITY} == [Cc] ]]
                                                    then
                                                        ## user chose to cancel
                                                        unset SERVICE_TYPE;
                                                        unset SERVICE_PROTO;
                                                        unset SERVICE_NAME;
                                                        unset SERVICE_TTL;
                                                        unset SERVICE_PRIORITY;
                                                        unset RECORD_TYPE;

                                                        CANCEL_REQ=${_TRUE};

                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                                                        reset; clear;
                                                        print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                    else
                                                        ## validate the priority
                                                        if [ "$(isNaN ${SERVICE_PRIORITY})" = "${_TRUE}" ]
                                                        then
                                                            ## service priority is good
                                                            ## as for service weight
                                                            while true
                                                            do
                                                                if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                                                                then
                                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                                                                    break;
                                                                fi

                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service weight..";

                                                                reset; clear;

                                                                print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                                print "\t$(grep add.record.srv.weight ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";

                                                                read SERVICE_WEIGHT;

                                                                reset; clear;
                                                                print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service weight received -> ${SERVICE_WEIGHT}";

                                                                if [[ ${SERVICE_WEIGHT} == [Xx] || ${SERVICE_WEIGHT} == [Qq] || ${SERVICE_WEIGHT} == [Cc] ]]
                                                                then
                                                                    ## user chose to cancel
                                                                    unset SERVICE_TYPE;
                                                                    unset SERVICE_PROTO;
                                                                    unset SERVICE_NAME;
                                                                    unset SERVICE_TTL;
                                                                    unset SERVICE_PRIORITY;
                                                                    unset SERVICE_WEIGHT;
                                                                    unset RECORD_TYPE;

                                                                    CANCEL_REQ=${_TRUE};

                                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                                                                    reset; clear;
                                                                    print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                                else
                                                                    ## validate the priority
                                                                    if [ "$(isNaN ${SERVICE_WEIGHT})" = "${_TRUE}" ]
                                                                    then
                                                                        ## service weight validated. continue..
                                                                        ## request the service port
                                                                        ## this is the port that the target is listening on
                                                                        while true
                                                                        do
                                                                            if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                                                                            then
                                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                                                                                break;
                                                                            fi

                                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service port..";

                                                                            reset; clear;

                                                                            print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                                            print "\t$(grep add.record.srv.port ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";

                                                                            read SERVICE_PORT;

                                                                            reset; clear;
                                                                            print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service port received -> ${SERVICE_PORT}";

                                                                            if [[ ${SERVICE_PORT} == [Xx] || ${SERVICE_PORT} == [Qq] || ${SERVICE_PORT} == [Cc] ]]
                                                                            then
                                                                                ## user chose to cancel
                                                                                unset SERVICE_TYPE;
                                                                                unset SERVICE_PROTO;
                                                                                unset SERVICE_NAME;
                                                                                unset SERVICE_TTL;
                                                                                unset SERVICE_PRIORITY;
                                                                                unset SERVICE_WEIGHT;
                                                                                unset SERVICE_PORT;
                                                                                unset RECORD_TYPE;

                                                                                CANCEL_REQ=${_TRUE};

                                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                                                                                reset; clear;
                                                                                print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                                            else
                                                                                ## validate the port
                                                                                if [ "$(isNaN ${SERVICE_PORT})" = "${_TRUE}" ]
                                                                                then
                                                                                    ## service port provided was valid, continue
                                                                                    ## now we need to ask for the target
                                                                                    ## this is the last peice of info we need
                                                                                    while true
                                                                                    do
                                                                                        if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                                                                                        then
                                                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                                                                                            break;
                                                                                        fi

                                                                                        reset; clear;

                                                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service target..";

                                                                                        print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                                                        print "\t$(grep add.record.target ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/${RECORD_TYPE}/")\n";

                                                                                        read SERVICE_TARGET;

                                                                                        reset; clear;
                                                                                        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service target received -> ${SERVICE_TARGET}";

                                                                                        if [[ ${SERVICE_TARGET} == [Xx] || ${SERVICE_TARGET} == [Qq] || ${SERVICE_TARGET} == [Cc] ]]
                                                                                        then
                                                                                            ## user chose to cancel
                                                                                            unset SERVICE_TYPE;
                                                                                            unset SERVICE_PROTO;
                                                                                            unset SERVICE_NAME;
                                                                                            unset SERVICE_TTL;
                                                                                            unset SERVICE_PRIORITY;
                                                                                            unset SERVICE_WEIGHT;
                                                                                            unset SERVICE_PORT;
                                                                                            unset SERVICE_TARGET;
                                                                                            unset RECORD_TYPE;

                                                                                            CANCEL_REQ=${_TRUE};

                                                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                                                                                            reset; clear;
                                                                                            print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                                                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                                                        else
                                                                                            ## validate the target
                                                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating target information..";

                                                                                            unset METHOD_NAME;
                                                                                            unset CNAME;

                                                                                            . ${PLUGIN_ROOT_DIR}/lib/validators/validate_record_target.sh srv ${SERVICE_TARGET};
                                                                                            RET_CODE=${?};

                                                                                            ## reset methodname/cname
                                                                                            CNAME="$(basename "${0}")";
                                                                                            local METHOD_NAME="${CNAME}#${0}";

                                                                                            if [ ${RET_CODE} -eq 0 ] || [ ${RET_CODE} -eq 63 ]
                                                                                            then
                                                                                                if [ ${RET_CODE} -eq 63 ]
                                                                                                then
                                                                                                    ## we got a warning on validation - we arent failing, but we do want to inform
                                                                                                    ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while updating the requested zone. Please try again.";
                                                                                                    print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                                                                    sleep "${MESSAGE_DELAY}";
                                                                                                fi

                                                                                                unset RET_CODE;
                                                                                                unset RETURN_CODE;

                                                                                                ## our service target is valid. we can write out the record.
                                                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing add_${RECORD_TYPE}_record.sh -p kvn -z kevin.com -i h33355 -c C991882 -t srv -a ${SERVICE_TYPE},${SERVICE_PROTO},${SERVICE_NAME},${SERVICE_TTL},${SERVICE_PRIORITY},${SERVICE_WEIGHT},${SERVICE_PORT},${SERVICE_TARGET} -r";

                                                                                                . ${PLUGIN_ROOT_DIR}/lib/helpers/data/add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${SERVICE_TYPE},${SERVICE_PROTO},${SERVICE_NAME},${SERVICE_TTL},${SERVICE_PRIORITY},${SERVICE_WEIGHT},${SERVICE_PORT},${SERVICE_TARGET} -r;
                                                                                                RET_CODE=${?};

                                                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. Return code -> ${RET_CODE}";

                                                                                                if [ ${RET_CODE} -eq 0 ]
                                                                                                then
                                                                                                    ## processing successfully completed.
                                                                                                    ## unset our variables, we dont need
                                                                                                    ## them anymore
                                                                                                    unset SERVICE_TYPE;
                                                                                                    unset SERVICE_PROTO;
                                                                                                    unset SERVICE_NAME;
                                                                                                    unset SERVICE_TTL;
                                                                                                    unset SERVICE_PRIORITY;
                                                                                                    unset SERVICE_WEIGHT;
                                                                                                    unset SERVICE_PORT;
                                                                                                    unset SERVICE_TARGET;
                                                                                                    unset RET_CODE;
                                                                                                    unset RETURN_CODE;

                                                                                                    ## record was successfully added. ask if we should add more
                                                                                                    ADD_COMPLETE=${_TRUE};
                                                                                                    reset; clear; break;
                                                                                                else
                                                                                                    ## an error occurred during processing.
                                                                                                    ## unset our variables, we dont need
                                                                                                    ## them anymore
                                                                                                    unset SERVICE_TYPE;
                                                                                                    unset SERVICE_PROTO;
                                                                                                    unset SERVICE_NAME;
                                                                                                    unset SERVICE_TTL;
                                                                                                    unset SERVICE_PRIORITY;
                                                                                                    unset SERVICE_WEIGHT;
                                                                                                    unset SERVICE_PORT;
                                                                                                    unset SERVICE_TARGET;

                                                                                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while updating the requested zone. Please try again.";
                                                                                                    print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                                                                    unset RET_CODE;
                                                                                                    unset RETURN_CODE;
                                                                                                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                                                                fi
                                                                                            else
                                                                                                ## service target not valid, re-try
                                                                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service target provided is invalid.";
                                                                                                print "$(grep target.provided.not.valid ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/${RECORD_TYPE}/" -e "s/%TARGET%/${SERVICE_TARGET}/")\n";

                                                                                                unset SERVICE_TARGET;
                                                                                                unset RET_CODE;
                                                                                                unset RETURN_CODE;

                                                                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                                                            fi
                                                                                        fi
                                                                                    done
                                                                                else
                                                                                    ## service port failed validation, cannot continue
                                                                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service port provided is invalid.";
                                                                                    print "$(grep number.provided.not.valid ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%REQUEST_TYPE%/service port/" -e "s/%REQUEST_NUMBER%/${SERVICE_PORT}/")\n";

                                                                                    unset SERVICE_PORT;
                                                                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                                                fi
                                                                            fi
                                                                        done
                                                                    else
                                                                        ## service weight didnt validate, return
                                                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service weight provided is invalid.";
                                                                        print "$(grep number.provided.not.valid ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%REQUEST_TYPE%/service weight/" -e "s/%REQUEST_NUMBER%/${SERVICE_WEIGHT}/")\n";

                                                                        unset SERVICE_WEIGHT;
                                                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                                    fi
                                                                fi
                                                            done
                                                        else
                                                            ## service priority didnt validate, return
                                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service priority provided is invalid.";
                                                            print "$(grep number.provided.not.valid ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%REQUEST_TYPE%/service priority/" -e "s/%REQUEST_NUMBER%/${SERVICE_PRIORITY}/")\n";

                                                            unset SERVICE_PRIORITY;
                                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                        fi
                                                    fi
                                                done
                                            else
                                                ## ttl didnt pass validation,
                                                ## show the error and re-try
                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service TTL provided is invalid.";
                                                print "$(grep number.provided.not.valid ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%REQUEST_TYPE%/service TTL/" -e "s/%REQUEST_NUMBER%/${SERVICE_TTL}/")\n";

                                                unset SERVICE_TTL;
                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            fi
                                        fi
                                    done
                                fi
                            done
                        else
                            ## protocol didnt pass validation
                            ## show the error and re-try
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service protocol provided is invalid.";
                            print "$(grep protocol.not.valid ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%PROTOCOL%/${SERVICE_PROTO}/")\n";

                            unset SERVICE_PROTO;
                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        fi
                    fi
                done
            else
                ## data didnt pass validation
                ## show the error code and re-try
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service type provided is invalid.";
                print "$(grep record.type.disallowed ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/${SERVICE_TYPE}/")\n";

                unset SERVICE_TYPE;
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
            fi
        fi
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

function add_subdomain_helper
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting subdomain SRV record information..";

    ## need a bunch of information for SRV record types
    while true
    do
        if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

            break;
        fi

        reset; clear;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service type..";

        ## ask for the service type
        print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
        print "\t$(grep add.record.srv.type ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";
        print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        read SERVICE_TYPE;

        reset; clear;
        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service type received -> ${SERVICE_TYPE}";

        ## validate
        if [[ ${SERVICE_TYPE} == [Xx] || ${SERVICE_TYPE} == [Qq] || ${SERVICE_TYPE} == [Cc] ]]
        then
            ## user chose to cancel
            unset SERVICE_TYPE;
            unset RECORD_TYPE;

            CANCEL_REQ=${_TRUE};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
            reset; clear;
            print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
            sleep "${MESSAGE_DELAY}"; reset; clear; break;
        else
            if [ $(${PLUGIN_ROOT_DIR}/lib/validators/validateServiceRecordData.sh type ${SERVICE_TYPE}) -eq 0 ]
            then
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVICE_TYPE->${SERVICE_TYPE}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service protocol..";

                ## service type is valid,
                ## continue with request
                while true
                do
                    if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                    then
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                        break;
                    fi

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service protocol..";

                    reset; clear;

                    ## ask for the service protocol
                    ## this can be tcp or udp
                    print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                    print "\t$(grep add.record.srv.protocol ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";
                    print "\t$(grep add.record.srv.valid.protocols ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";
                    print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                    read SERVICE_PROTO;

                    reset; clear;
                    print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service protocol received -> ${SERVICE_PROTO}";

                    ## validate the provided protocol
                    if [ ${SERVICE_PROTO} == [Xx] ] || [ ${SERVICE_PROTO} == [Qq] ] || [ ${SERVICE_PROTO} = [Cc] ]
                    then
                        ## user chose to cancel
                        unset SERVICE_TYPE;
                        unset SERVICE_PROTO;
                        unset RECORD_TYPE;

                        CANCEL_REQ=${_TRUE};

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                        reset; clear;
                        print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                    else
                        if [ $(${PLUGIN_ROOT_DIR}/lib/validators/validateServiceRecordData.sh protocol ${SERVICE_PROTO}) -eq 0 ]
                        then
                            ## protocol is valid, move forward
                            ## now we need to request the service name
                            while true
                            do
                                if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                                then
                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                                    break;
                                fi

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service name..";

                                reset; clear;

                                print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                print "\t$(grep add.record.alias ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/${RECORD_TYPE}/")";
                                print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                read SERVICE_NAME;

                                reset; clear;
                                print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service named received -> ${SERVICE_NAME}";

                                if [[ ${SERVICE_NAME} == [Xx] || ${SERVICE_NAME} == [Qq] || ${SERVICE_NAME} == [Cc] ]]
                                then
                                    ## user chose to cancel
                                    unset SERVICE_TYPE;
                                    unset SERVICE_PROTO;
                                    unset SERVICE_NAME;
                                    unset RECORD_TYPE;

                                    CANCEL_REQ=${_TRUE};

                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                                    reset; clear;
                                    print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                elif [ -z "${SERVICE_NAME}" ]
                                then
                                    ## no service name was provided, this is technically allowed,
                                    ## but we're going to dis-allow it because we want to know for
                                    ## sure what to add.
                                    unset SERVICE_NAME;
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No SERVICE_NAME was provided. Cannot continue.";

                                    reset; clear; continue;
                                else
                                    ## thats all the validation we do here. the name
                                    ## is entirely of the user's choosing and can be
                                    ## whatever they want
                                    ## ask for the TTL - default to 86400
                                    while true
                                    do
                                        if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                                        then
                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                                            break;
                                        fi

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service TTL..";

                                        reset; clear;

                                        print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                        print "\t$(grep add.record.srv.ttl ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";
                                        print "\t$(grep add.record.srv.default.ttl ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";

                                        read SERVICE_TTL;

                                        reset; clear;
                                        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service TTL received -> ${SERVICE_TTL}";

                                        if [[ ${SERVICE_NAME} == [Xx] || ${SERVICE_NAME} == [Qq] || ${SERVICE_NAME} == [Cc] ]]
                                        then
                                            ## user chose to cancel
                                            unset SERVICE_TYPE;
                                            unset SERVICE_PROTO;
                                            unset SERVICE_NAME;
                                            unset SERVICE_TTL;
                                            unset RECORD_TYPE;

                                            CANCEL_REQ=${_TRUE};

                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                                            reset; clear;
                                            print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                        else
                                            [ -z "${SERVICE_TTL}" ] && SERVICE_TTL=86400;

                                            if [ "$(isNaN ${SERVICE_TTL})" = "${_TRUE}" ]
                                            then
                                                ## service ttl passed validation or we defaulted
                                                ## to 86400
                                                ## now we need to ask for the priority.
                                                while true
                                                do
                                                    if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                                                    then
                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                                                        break;
                                                    fi

                                                    reset; clear;

                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service priority..";

                                                    print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                    print "\t$(grep add.record.priority ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/${RECORD_TYPE}/")";

                                                    read SERVICE_PRIORITY;

                                                    reset; clear;
                                                    print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service priority received -> ${SERVICE_PRIORITY}";

                                                    if [[ ${SERVICE_PRIORITY} == [Xx] || ${SERVICE_PRIORITY} == [Qq] || ${SERVICE_PRIORITY} == [Cc] ]]
                                                    then
                                                        ## user chose to cancel
                                                        unset SERVICE_TYPE;
                                                        unset SERVICE_PROTO;
                                                        unset SERVICE_NAME;
                                                        unset SERVICE_TTL;
                                                        unset SERVICE_PRIORITY;
                                                        unset RECORD_TYPE;

                                                        CANCEL_REQ=${_TRUE};

                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                                                        reset; clear;
                                                        print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                    else
                                                        ## validate the priority
                                                        if [ "$(isNaN ${SERVICE_PRIORITY})" = "${_TRUE}" ]
                                                        then
                                                            ## service priority is good
                                                            ## as for service weight
                                                            while true
                                                            do
                                                                if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                                                                then
                                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                                                                    break;
                                                                fi

                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service weight..";

                                                                reset; clear;

                                                                print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                                print "\t$(grep add.record.srv.weight ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";

                                                                read SERVICE_WEIGHT;

                                                                reset; clear;
                                                                print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service weight received -> ${SERVICE_WEIGHT}";

                                                                if [[ ${SERVICE_WEIGHT} == [Xx] || ${SERVICE_WEIGHT} == [Qq] || ${SERVICE_WEIGHT} == [Cc] ]]
                                                                then
                                                                    ## user chose to cancel
                                                                    unset SERVICE_TYPE;
                                                                    unset SERVICE_PROTO;
                                                                    unset SERVICE_NAME;
                                                                    unset SERVICE_TTL;
                                                                    unset SERVICE_PRIORITY;
                                                                    unset SERVICE_WEIGHT;
                                                                    unset RECORD_TYPE;

                                                                    CANCEL_REQ=${_TRUE};

                                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                                                                    reset; clear;
                                                                    print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                                else
                                                                    ## validate the priority
                                                                    if [ "$(isNaN ${SERVICE_WEIGHT})" = "${_TRUE}" ]
                                                                    then
                                                                        ## service weight validated. continue..
                                                                        ## request the service port
                                                                        ## this is the port that the target is listening on
                                                                        while true
                                                                        do
                                                                            if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                                                                            then
                                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                                                                                break;
                                                                            fi

                                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service port..";

                                                                            reset; clear;

                                                                            print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                                            print "\t$(grep add.record.srv.port ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";

                                                                            read SERVICE_PORT;

                                                                            reset; clear;
                                                                            print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service port received -> ${SERVICE_PORT}";

                                                                            if [[ ${SERVICE_PORT} == [Xx] || ${SERVICE_PORT} == [Qq] || ${SERVICE_PORT} == [Cc] ]]
                                                                            then
                                                                                ## user chose to cancel
                                                                                unset SERVICE_TYPE;
                                                                                unset SERVICE_PROTO;
                                                                                unset SERVICE_NAME;
                                                                                unset SERVICE_TTL;
                                                                                unset SERVICE_PRIORITY;
                                                                                unset SERVICE_WEIGHT;
                                                                                unset SERVICE_PORT;
                                                                                unset RECORD_TYPE;

                                                                                CANCEL_REQ=${_TRUE};

                                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                                                                                reset; clear;
                                                                                print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                                            else
                                                                                ## validate the port
                                                                                if [ "$(isNaN ${SERVICE_PORT})" = "${_TRUE}" ]
                                                                                then
                                                                                    ## service port provided was valid, continue
                                                                                    ## now we need to ask for the target
                                                                                    ## this is the last peice of info we need
                                                                                    while true
                                                                                    do
                                                                                        if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                                                                                        then
                                                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                                                                                            break;
                                                                                        fi

                                                                                        reset; clear;

                                                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting service target..";

                                                                                        print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                                                        print "\t$(grep add.record.target ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/${RECORD_TYPE}/")\n";

                                                                                        read SERVICE_TARGET;

                                                                                        reset; clear;
                                                                                        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service target received -> ${SERVICE_TARGET}";

                                                                                        if [[ ${SERVICE_TARGET} == [Xx] || ${SERVICE_TARGET} == [Qq] || ${SERVICE_TARGET} == [Cc] ]]
                                                                                        then
                                                                                            ## user chose to cancel
                                                                                            unset SERVICE_TYPE;
                                                                                            unset SERVICE_PROTO;
                                                                                            unset SERVICE_NAME;
                                                                                            unset SERVICE_TTL;
                                                                                            unset SERVICE_PRIORITY;
                                                                                            unset SERVICE_WEIGHT;
                                                                                            unset SERVICE_PORT;
                                                                                            unset SERVICE_TARGET;
                                                                                            unset RECORD_TYPE;

                                                                                            CANCEL_REQ=${_TRUE};

                                                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                                                                                            reset; clear;
                                                                                            print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                                                                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                                                        else
                                                                                            ## validate the target
                                                                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating target information..";

                                                                                            unset METHOD_NAME;
                                                                                            unset CNAME;

                                                                                            . ${PLUGIN_ROOT_DIR}/lib/validators/validate_record_target.sh srv ${SERVICE_TARGET};
                                                                                            RET_CODE=${?};

                                                                                            ## reset methodname/cname
                                                                                            CNAME="$(basename "${0}")";
                                                                                            local METHOD_NAME="${CNAME}#${0}";

                                                                                            if [ ${RET_CODE} -eq 0 ] || [ ${RET_CODE} -eq 63 ]
                                                                                            then
                                                                                                if [ ${RET_CODE} -eq 63 ]
                                                                                                then
                                                                                                    ## we got a warning on validation - we arent failing, but we do want to inform
                                                                                                    ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while updating the requested zone. Please try again.";
                                                                                                    print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                                                                    sleep "${MESSAGE_DELAY}";
                                                                                                fi

                                                                                                ## our service target is valid. we can write out the record.
                                                                                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing add_${RECORD_TYPE}_record.sh -p kvn -z kevin.com -i h33355 -c C991882 -t srv -a ${SERVICE_TYPE},${SERVICE_PROTO},${SERVICE_NAME},${SERVICE_TTL},${SERVICE_PRIORITY},${SERVICE_WEIGHT},${SERVICE_PORT},${SERVICE_TARGET} -s";

                                                                                                if [ "${ADD_EXISTING_RECORD}" = "${_TRUE}" ]
                                                                                                then
                                                                                                    ## we're adding a new record to an existing zone.
                                                                                                    ## call out the appropriate runner
                                                                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding new record type to zone..";

                                                                                                    ## temp unset
                                                                                                    unset METHOD_NAME;
                                                                                                    unset CNAME;
                                                                                                    execute runner here
                                                                                                    
                                                                                                    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";
                                                                                                    CNAME="$(basename "${0}")";                            

                                                                                                    check retcode here
                                                                                                else
                                                                                                    . ${PLUGIN_ROOT_DIR}/lib/helpers/data/add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${SERVICE_TYPE},${SERVICE_PROTO},${SERVICE_NAME},${SERVICE_TTL},${SERVICE_PRIORITY},${SERVICE_WEIGHT},${SERVICE_PORT},${SERVICE_TARGET} -s;
                                                                                                    RET_CODE=${?};

                                                                                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Execution complete. Return code -> ${RET_CODE}";

                                                                                                    if [ ${RET_CODE} -eq 0 ]
                                                                                                    then
                                                                                                        ## processing successfully completed.
                                                                                                        ## unset our variables, we dont need
                                                                                                        ## them anymore
                                                                                                        unset SERVICE_TYPE;
                                                                                                        unset SERVICE_PROTO;
                                                                                                        unset SERVICE_NAME;
                                                                                                        unset SERVICE_TTL;
                                                                                                        unset SERVICE_PRIORITY;
                                                                                                        unset SERVICE_WEIGHT;
                                                                                                        unset SERVICE_PORT;
                                                                                                        unset SERVICE_TARGET;
                                                                                                        unset RET_CODE;
                                                                                                        unset RETURN_CODE;

                                                                                                        ## ask if the user wants to add additional records
                                                                                                        ADD_COMPLETE=${_TRUE};
                                                                                                        reset; clear; break;
                                                                                                    else
                                                                                                        ## an error occurred during processing.
                                                                                                        ## unset our variables, we dont need
                                                                                                        ## them anymore
                                                                                                        unset SERVICE_TYPE;
                                                                                                        unset SERVICE_PROTO;
                                                                                                        unset SERVICE_NAME;
                                                                                                        unset SERVICE_TTL;
                                                                                                        unset SERVICE_PRIORITY;
                                                                                                        unset SERVICE_WEIGHT;
                                                                                                        unset SERVICE_PORT;
                                                                                                        unset SERVICE_TARGET;

                                                                                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while updating the requested zone. Please try again.";
                                                                                                        print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                                                                                        unset RET_CODE;
                                                                                                        unset RETURN_CODE;
                                                                                                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                                                                                    fi
                                                                                                fi
                                                                                            else
                                                                                                ## service target not valid, re-try
                                                                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service target provided is invalid.";
                                                                                                print "$(grep target.provided.not.valid ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/${RECORD_TYPE}/" -e "s/%TARGET%/${SERVICE_TARGET}/")\n";

                                                                                                unset SERVICE_TARGET;
                                                                                                unset RET_CODE;
                                                                                                unset RETURN_CODE;

                                                                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                                                            fi
                                                                                        fi
                                                                                    done
                                                                                else
                                                                                    ## service port failed validation, cannot continue
                                                                                    ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service port provided is invalid.";
                                                                                    print "$(grep number.provided.not.valid ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%REQUEST_TYPE%/service port/" -e "s/%REQUEST_NUMBER%/${SERVICE_PORT}/")\n";

                                                                                    unset SERVICE_PORT;
                                                                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                                                fi
                                                                            fi
                                                                        done
                                                                    else
                                                                        ## service weight didnt validate, return
                                                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service weight provided is invalid.";
                                                                        print "$(grep number.provided.not.valid ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%REQUEST_TYPE%/service weight/" -e "s/%REQUEST_NUMBER%/${SERVICE_WEIGHT}/")\n";

                                                                        unset SERVICE_WEIGHT;
                                                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                                    fi
                                                                fi
                                                            done
                                                        else
                                                            ## service priority didnt validate, return
                                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service priority provided is invalid.";
                                                            print "$(grep number.provided.not.valid ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%REQUEST_TYPE%/service priority/" -e "s/%REQUEST_NUMBER%/${SERVICE_PRIORITY}/")\n";

                                                            unset SERVICE_PRIORITY;
                                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                                        fi
                                                    fi
                                                done
                                            else
                                                ## ttl didnt pass validation,
                                                ## show the error and re-try
                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service TTL provided is invalid.";
                                                print "$(grep number.provided.not.valid ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%REQUEST_TYPE%/service TTL/" -e "s/%REQUEST_NUMBER%/${SERVICE_TTL}/")\n";

                                                unset SERVICE_TTL;
                                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                            fi
                                        fi
                                    done
                                fi
                            done
                        else
                            ## protocol didnt pass validation
                            ## show the error and re-try
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service protocol provided is invalid.";
                            print "$(grep protocol.not.valid ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%PROTOCOL%/${SERVICE_PROTO}/")\n";

                            unset SERVICE_PROTO;
                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        fi
                    fi
                done
            else
                ## data didnt pass validation
                ## show the error code and re-try
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Service type provided is invalid.";
                print "$(grep record.type.disallowed ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/${SERVICE_TYPE}/")\n";

                unset SERVICE_TYPE;
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
            fi
        fi
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function usage
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Add audit indicators and other flags to the failover zone file";
    print "Usage: ${CNAME} [ zone | subdomain ]";
    print "  -h|-?   Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[ ${#} -eq 0 ] && usage;

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

[ "${1}" = "zone" ] && add_zone_ui_helper || add_subdomain_ui_helper;
