#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  add_mx_ui_helper.sh
#         USAGE:  ./add_mx_ui_helper.sh
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

[ ${#} -eq 0 ] && usage;

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

trap "print '$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/system.trap.signals/{print $2}' | sed -e 's/^ *//g' -e 's/ *$//g' -e "s/%SIGNAL%/Ctrl-C/")'; sleep "${MESSAGE_DELAY}"; reset; clear; continue " 1 2 3

#===  FUNCTION  ===============================================================
#          NAME:  add_ip_info
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#==============================================================================
function add_zone_ui_helper
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting zone-level MX record information..";

    trap "print '$(grep system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

    while true
    do
        if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

            break;
        fi

        reset; clear;

        print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
        print "\t$(grep add.record.target ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/$(echo ${RECORD_TYPE}| tr "[a-z]" "[A-Z]")/")";
        print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
        read MX_TARGET;

        reset; clear;
        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        if [ -z "${MX_TARGET}" ]
        then
            ## no valid response found
            unset MX_TARGET;

            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MX target provided was blank. Cannot continue.";
            print "$(grep selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
        elif [[ ${MX_TARGET} == [Xx] || ${MX_TARGET} == [Qq] || ${MX_TARGET} == [Cc] ]]
        then
            ## user chose to cancel this request
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Subdomain record addition for ${RECORD_TYPE} canceled.";
            unset MX_TARGET;
            CANCEL_REQ=${_TRUE};

            print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";

            sleep "${MESSAGE_DELAY}"; reset; clear; break;
        else
            ## validate our mx target
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating target information..";

            unset METHOD_NAME;
            unset CNAME;

            . ${PLUGIN_ROOT_DIR}/lib/validators/validate_record_target.sh mx ${MX_TARGET};
            RET_CODE=${?};

            ## reset methodname/cname
            CNAME="$(basename "${0}")";
            local METHOD_NAME="${CNAME}#${0}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validation complete. RET_CODE -> ${RET_CODE}";

            if [ ${RET_CODE} -eq 0 ] || [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
            then
                if [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                then
                    ## we got a warning on validation - we arent failing, but we do want to inform
                    ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while updating the requested zone. Please try again.";
                    print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    sleep "${MESSAGE_DELAY}";
                fi

                unset RET_CODE;
                unset RETURN_CODE;

                ## request this mx record's priority
                while true
                do
                    if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                    then
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                        break;
                    fi

                    reset; clear;

                    print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                    print "\t$(grep add.record.priority ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/${RECORD_TYPE}/")";
                    print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    read MX_PRIORITY;

                    reset; clear;
                    print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                    if [ -z "${MZ_PRIORITY}" ]
                    then
                        ## no valid response found
                        unset MX_PRIORITY;

                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MX priority provided was blank. Cannot continue.";
                        print "$(grep selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    elif [[ ${MX_PRIORITY} == [Xx] || ${MX_PRIORITY} == [Qq] || ${MX_PRIORITY} == [Cc] ]]
                    then
                        ## user chose to cancel this request
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Subdomain record addition for ${RECORD_TYPE} canceled.";
                        unset MX_TARGET;
                        unset MX_PRIORITY;
                        CANCEL_REQ=${_TRUE};

                        print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";

                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                    else
                        ## got a response for priority
                        ## need to make sure its numeric
                        if [ "$(isNaN ${MX_PRIORITY})" = "${_FALSE}" ]
                        then
                            ## priority didnt pass validation
                            ## show an error and return
                            reset; clear;
                            print "$(grep number.provided.not.valid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%REQUEST_TYPE%/${RECORD_TYPE}/" -e "s/%REQUEST_NUMBER%/${MX_PRIORITY}/")\n";

                            unset MX_PRIORITY;

                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        else
                            ## request passed validation, move forward
                            while true
                            do
                                if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                                then
                                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                                    break;
                                fi

                                reset; clear;

                                print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                print "\t$(grep add.record.provide.datacenter ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";
                                print "\t$(grep add.record.available.datacenters ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%DATACENTER%/${PRIMARY_DC}, ${SECONDARY_DC}/")";
                                print "\t$(grep add.record.add.to.both.datacenters ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";
                                print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                read DATACENTER;

                                reset; clear;

                                print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                typeset -u DATACENTER;

                                case ${DATACENTER} in
                                    ${PRIMARY_DC}|${SECONDARY_DC}|[Bb][Oo][Tt][Hh]|[PVpv][Hh])
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing addition of ${RECORD_TYPE}, address ${RECORD_DETAIL}, to ${SITE_HOSTNAME}..";
                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${PRIORITY},${RECORD_DETAIL} -d ${DATACENTER} -r";

                                        ## our provided address is valid.
                                        ## make a call out to add_records
                                        ## to process the request
                                        ## we know what to add and where to add it.
                                        ## so lets do it
                                        ## temp unset
                                        unset METHOD_NAME;
                                        unset CNAME;

                                        . ${PLUGIN_ROOT_DIR}/lib/helpers/data/add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${MX_PRIORITY},${MX_TARGET} -d ${DATACENTER} -r;
                                        RET_CODE=${?};

                                        ## re-set
                                        local METHOD_NAME="${CNAME}#${0}";
                                        CNAME="$(basename "${0}")";

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing complete. Return code -> ${RET_CODE}";

                                        if [ ${RET_CODE} -eq 0 ]
                                        then
                                            ## record was successfully added. ask if we should add more
                                            unset RET_CODE;
                                            unset RETURN_CODE;
                                            unset DATACENTER;
                                            unset MX_PRIORITY;
                                            unset MX_TARGET;

                                            ADD_COMPLETE=${_TRUE};
                                            reset; clear; break;
                                        else
                                            ## an error occurred
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while adding ${RECORD_TYPE} to ${SITE_HOSTNAME}. Return code from add_${RECORD_TYPE}_record.sh -> ${RET_CODE}";
                                            print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                            unset RET_CODE;
                                            unset RETURN_CODE;
                                            unset DATACENTER;
                                            unset MX_PRIORITY;
                                            unset MX_TARGET;

                                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                        fi
                                        ;;
                                    [Xx]|[Qq]|[Cc])
                                        ## user chose to cancel
                                        ## clear our variables
                                        unset DATACENTER;
                                        unset MX_TARGET;
                                        unset MX_PRIORITY;
                                        unset RECORD_TYPE;

                                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone configuration canceled.";

                                        CANCEL_REQ=${_TRUE};
                                        print "$(grep system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                        ;;
                                    *)
                                        ## datacenter isnt valid as its not configured
                                        ## inform and re-execute
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Datacenter ${DATACENTER} is not currently configured.";
                                        print "$(grep datacenter.not.configured "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%DATACENTER%/${DATACENTER}/")\n";
                                        unset DATACENTER;
                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        ;;
                                esac
                            done
                        fi
                    fi
                done
            else
                ## mx target didnt pass validation
                ## show an error
                reset; clear;
                print "$(grep target.provided.not.valid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/${RECORD_TYPE}/" -e "s/%TARGET%/${MX_TARGET}/")\n";

                unset MX_TARGET;
                unset RET_CODE;
                unset RETURN_CODE;

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
            fi
        fi
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

function add_subdomain_ui_helper
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting subdomain MX record information..";

    trap "print '$(grep system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

    while true
    do
        if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

            break;
        fi

        reset; clear;

        print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
        print "\t$(grep add.record.target ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/$(echo ${RECORD_TYPE}| tr "[a-z]" "[A-Z]")/")";
        print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
        read MX_TARGET;

        reset; clear;
        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        if [[ ${MX_TARGET} == [Xx] || ${MX_TARGET} == [Qq] || ${MX_TARGET} == [Cc] ]]
        then
            ## user chose to cancel this request
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Subdomain record addition for ${RECORD_TYPE} canceled.";
            unset MX_TARGET;
            CANCEL_REQ=${_TRUE};

            print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";

            sleep "${MESSAGE_DELAY}"; reset; clear; break;
        else
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating target information..";

            unset METHOD_NAME;
            unset CNAME;

            . ${PLUGIN_ROOT_DIR}/lib/validators/validate_record_target.sh mx ${MX_TARGET};
            RET_CODE=${?};

            ## reset methodname/cname
            CNAME="$(basename "${0}")";
            local METHOD_NAME="${CNAME}#${0}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validation complete. RET_CODE -> ${RET_CODE}";

            if [ ${RET_CODE} -eq 0 ] || [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
            then
                if [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                then
                    ## we got a warning on validation - we arent failing, but we do want to inform
                    ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while updating the requested zone. Please try again.";
                    print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    sleep "${MESSAGE_DELAY}";
                fi

                unset RET_CODE;
                unset RETURN_CODE;

                ## successfully validated our target. lets keep moving.
                while true
                do
                    if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                    then
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                        break;
                    fi

                    reset; clear;

                    print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                    print "\t$(grep add.record.priority ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/${RECORD_TYPE}/")";
                    print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    read MX_PRIORITY;

                    reset; clear;
                    print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                    if [[ ${MX_PRIORITY} == [Xx] || ${MX_PRIORITY} == [Qq] || ${MX_PRIORITY} == [Cc] ]]
                    then
                        ## user chose to cancel this request
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Subdomain record addition for ${RECORD_TYPE} canceled.";
                        unset MX_TARGET;
                        unset MX_PRIORITY;
                        CANCEL_REQ=${_TRUE};

                        print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";

                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                    else
                        if [ "$(isNaN ${MX_PRIORITY})" = "${_FALSE}" ]
                        then
                            ## priority didnt pass validation
                            ## show an error and return
                            reset; clear;
                            print "$(grep number.provided.not.valid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%REQUEST_TYPE%/${RECORD_TYPE}/" -e "s/%REQUEST_NUMBER%/${MX_PRIORITY}/")\n";

                            unset MX_PRIORITY;

                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                        else
                            ## mx priority provided was valid
                            ## continue with request
                            ## we have enough information to write out our mx record to the subdomain section
                            ## of the zone file. rock it out
                            ## we know what to add and where to add it.
                            ## so lets do it

                            if [ "${ADD_EXISTING_RECORD}" = "${_TRUE}" ]
                            then
                                ## we're adding a new record to an existing zone.
                                ## call out the appropriate runner
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding new record type to zone..";

                                ## temp unset
                                unset METHOD_NAME;
                                unset CNAME;
                                execute runner here
                                
                                local METHOD_NAME="${CNAME}#${0}";
                                CNAME="$(basename "${0}")";                            

                                check retcode here
                            else
                                . ${PLUGIN_ROOT_DIR}/lib/helpers/data/add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${MX_PRIORITY},${MX_TARGET} -s;
                                RET_CODE=${?};

                                ## re-set
                                local METHOD_NAME="${CNAME}#${0}";
                                CNAME="$(basename "${0}")";

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing complete. Return code -> ${RET_CODE}";

                                if [ ${RET_CODE} -eq 0 ]
                                then
                                    ## record was successfully added. ask if we should add more
                                    unset RET_CODE;
                                    unset RETURN_CODE;
                                    unset MX_PRIORITY;
                                    unset MX_TARGET;

                                    ADD_COMPLETE=${_TRUE};
                                    reset; clear; break;
                                else
                                    ## record was not successfully added
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while adding ${RECORD_TYPE} to ${SITE_HOSTNAME}. Return code from add_${RECORD_TYPE}_record.sh -> ${RET_CODE}";
                                    print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    unset RET_CODE;
                                    unset RETURN_CODE;
                                    unset DATACENTER;
                                    unset MX_PRIORITY;
                                    unset MX_TARGET;

                                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                fi
                            fi
                        fi
                    fi
                done
            else
                ## mx target didnt pass validation
                ## show an error
                reset; clear;
                print "$(grep target.provided.not.valid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/${RECORD_TYPE}/" -e "s/%TARGET%/${MX_TARGET}/")\n";

                unset MX_TARGET;
                unset RET_CODE;
                unset RETURN_CODE;

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

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

[ "${1}" = "zone" ] && add_zone_ui_helper || add_subdomain_ui_helper;
