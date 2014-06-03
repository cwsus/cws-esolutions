#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  add_a_ui_helper.sh
#         USAGE:  ./add_a_ui_helper.sh
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
function add_root_ui_helper
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    while true
    do
        if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

            break;
        fi

        reset; clear;

        print "\t\t\t$(grep system.application.title ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
        print "\t$(grep add.enter.ipaddr.primary ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%PRIMARY_DATACENTER%/${PRIMARY_DC}/")";
        print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        read PRIMARY_INFO;

        reset; clear;
        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

        if [[ ${PRIMARY_INFO} == [Xx] || ${PRIMARY_INFO} == [Qq] || ${PRIMARY_INFO} == [Cc] ]]
        then
            reset; clear;
            unset PRIMARY_INFO;
            unset SECONDARY_INFO;

            ## clean up our tmp directories
            rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

            print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";

            ## terminate this thread and return control to main
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            CANCEL_REQ=${_TRUE};
            sleep "${MESSAGE_DELAY}"; reset; clear; break;
        else
            if [ $(${PLUGIN_ROOT_DIR}/lib/validators/validate_ip_address.sh ${PRIMARY_INFO}) -ne 0 ]
            then
                unset PRIMARY_INFO;
                unset RET_CODE;

                reset; clear;
                print "$(grep ip.address.improperly.formatted "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
            else
                ## run the ip addr through the validator
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating record target..";

                unset METHOD_NAME;
                unset CNAME;

                . ${PLUGIN_ROOT_DIR}/lib/validators/validate_record_target.sh a ${PRIMARY_INFO};
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
                        ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A warning occurred during record validation - failed to validate that record is active.";
                        print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                        sleep "${MESSAGE_DELAY}";
                    fi

                    ## IP address is formatted correctly, so we'll update our zone with it
                    ## temporarily unset methodname and cname
                    unset METHOD_NAME;
                    unset CNAME;
                    unset RET_CODE;
                    unset RETURN_CODE;

                    . ${PLUGIN_ROOT_DIR}/lib/helpers/data/add_a_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t A -a ${PRIMARY_INFO} -d ${PRIMARY_DC} -r;
                    RET_CODE=${?};

                    ## re-set methodname and cname
                    local METHOD_NAME="${CNAME}#${0}";
                    CNAME="$(basename "${0}")";

                    if [ ${RET_CODE} -eq 0 ]
                    then
                        unset RET_CODE;
                        unset RETURN_CODE;
                        unset PRIMARY_INFO;

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone successfully updated";

                        while true
                        do
                            if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                            then
                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

                                break;
                            fi

                            reset; clear;

                            ## added our primary ip, now lets get our secondary
                            print "\t\t\t$(grep system.application.title ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                            print "\t$(grep add.enter.ipaddr.secondary ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SECONDARY_DATACENTER%/${SECONDARY_DC}/")";
                            print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                            read SECONDARY_INFO;

                            reset; clear;
                            print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                            if [[ ${SECONDARY_INFO} == [Xx] || ${SECONDARY_INFO} == [Qq] || ${SECONDARY_INFO} == [Cc] ]]
                            then
                                ## user chose to cancel
                                reset; clear;
                                unset SECONDARY_INFO;
                                unset PRIMARY_INFO;

                                ## clean up our tmp directories
                                rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT};

                                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS query canceled.";

                                print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";

                                ## terminate this thread and return control to main
                                CANCEL_REQ=${_TRUE};
                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                            else
                                if [ $(${PLUGIN_ROOT_DIR}/lib/validators/validate_ip_address.sh ${SECONDARY_INFO}) -ne 0 ]
                                then
                                    unset SECONDARY_INFO;
                                    unset RET_CODE;

                                    reset; clear;
                                    print "$(grep ip.address.improperly.formatted "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                else
                                    ## run the ip addr through the validator
                                    unset METHOD_NAME;
                                    unset CNAME;

                                    . ${PLUGIN_ROOT_DIR}/lib/validators/validate_record_target.sh a ${SECONDARY_INFO};
                                    RET_CODE=${?};

                                    ## reset methodname/cname
                                    CNAME="$(basename "${0}")";
                                    local METHOD_NAME="${CNAME}#${0}";

                                    if [ ${RET_CODE} -eq 0 ] || [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                                    then
                                        if [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                                        then
                                            ## we got a warning on validation - we arent failing, but we do want to inform
                                            ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A warning occurred during record validation - failed to validate that record is active.";
                                            print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                            sleep "${MESSAGE_DELAY}";
                                        fi

                                        ## ip addr is properly formatted
                                        ## continue with request
                                        ## temporarily unset things
                                        unset CNAME;
                                        unset METHOD_NAME;
                                        unset RET_CODE;
                                        unset RETURN_CODE;

                                        . ${PLUGIN_ROOT_DIR}/lib/helpers/data/add_a_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t A -a ${SECONDARY_INFO} -d ${SECONDARY_DC} -r;
                                        RET_CODE=${?};

                                        ## re-set methodname and cname
                                        local METHOD_NAME="${CNAME}#${0}";
                                        CNAME="$(basename "${0}")";

                                        if [ ${RET_CODE} -eq 0 ]
                                        then
                                            unset RET_CODE;
                                            unset RETURN_CODE;
                                            unset SECONDARY_INFO;

                                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone successfully updated";

                                            ## we should at this point have a functioning zone that would
                                            ## work in a dns installation.
                                            ## ask the user if any additional information should be added,
                                            ## if not, then we're ready to send
                                            ADD_COMPLETE=${_TRUE};
                                            reset; clear; break;
                                        else
                                            ## zone failed to update with secondary ip addr
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone update to add secondary IP FAILED. Return code -> ${RET_CODE}";
                                            print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                            unset RET_CODE;
                                            unset RETURN_CODE;
                                            unset SECONDARY_INFO;
                                            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                        fi
                                    else
                                        ## failed to validate record.
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Secondary IP address provided failed validation. Cannot continue.";
                                        print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                                        unset RET_CODE;
                                        unset RETURN_CODE;
                                        unset PRIMARY_INFO;
                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    fi
                                fi
                            fi
                        done
                    else
                        ## zone failed to update with primary ip addr
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone update to add primary IP FAILED. Return code -> ${RET_CODE}";
                        print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                        unset RET_CODE;
                        unset RETURN_CODE;
                        unset PRIMARY_INFO;
                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi
                else
                    ## failed to validate record.
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Primary IP address provided failed validation. Cannot continue.";
                    print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                    unset RET_CODE;
                    unset RETURN_CODE;
                    unset PRIMARY_INFO;
                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi
            fi
        fi
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  add_ip_info
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function add_zone_ui_helper
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting zone-level A record information..";

    trap "print '$(grep system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

    while true
    do
        if [ ! -z "${ADD_RECORDS}" ] || [ ! -z "${ADD_SUBDOMAINS}" ] || [ ! -z "${CANCEL_REQ}" ] || [ ! -z "${ADD_COMPLETE}" ]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";

            break;
        fi

        reset; clear;

        print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
        print "\t$(grep add.record.target ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/$(echo ${RECORD_TYPE}| tr "[a-z]" "[A-Z]")/")";
        print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        read RECORD_DETAIL;
        reset; clear;

        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_DETAIL->${RECORD_DETAIL}";

        if [ ${RECORD_DETAIL} == [Xx] || ${RECORD_DETAIL} == [Qq] || ${RECORD_DETAIL} == [Cc] ]]
        then
            ## user has chosen to cancel
            unset RECORD_DETAIL;
            unset RECORD_TYPE;

            CANCEL_REQ=${_TRUE};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
            reset; clear;
            print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
            sleep "${MESSAGE_DELAY}"; reset; clear; break;
        elif [ $(${PLUGIN_ROOT_DIR}/lib/validators/validate_ip_address.sh ${RECORD_DETAIL}) -ne 0 ]
        then
            ## an error occurred in the validator
            ## advise and retry
            unset RECORD_DETAIL;

            reset; clear;
            print "$(grep ip.address.improperly.formatted "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
        else
            ## run the ip addr through the validator
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating target information..";

            unset METHOD_NAME;
            unset CNAME;

            . ${PLUGIN_ROOT_DIR}/lib/validators/validate_record_target.sh a ${RECORD_DETAIL};
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
                    ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A warning occurred during record validation - failed to validate that record is active.";
                    print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    sleep "${MESSAGE_DELAY}";
                fi

                ## ip addr is properly formatted
                ## continue with request
                unset RET_CODE;
                unset RETURN_CODE;
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Address record provided is ${RECORD_DETAIL}";

                ## now we need to know where to apply the change
                while true
                do
                    if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                    then
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_RECORDS->${ADD_RECORDS}, ADD_SUBDOMAINS->${ADD_SUBDOMAINS}, breaking..";
                        unset RECORD_DETAIL;
                        unset RECORD_TYPE;

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

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DATACENTER->${DATACENTER}";

                    ## request datacenter
                    ## valid options are the configured primary, configured secondary, or both
                    case ${DATACENTER} in
                        ${PRIMARY_DC}|${SECONDARY_DC}|[Bb][Oo][Tt][Hh]|[PVpv][Hh])
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing addition of ${RECORD_TYPE}, address ${RECORD_DETAIL}, to ${SITE_HOSTNAME}..";
                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${RECORD_DETAIL} -d ${DATACENTER} -r";

                            ## our provided address is valid.
                            ## make a call out to add_records
                            ## to process the request

                            ## temp unset
                            unset METHOD_NAME;
                            unset CNAME;

                            ## we know what to add and where to add it.
                            ## so lets do it
                            . ${PLUGIN_ROOT_DIR}/lib/helpers/data/add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${RECORD_DETAIL} -d ${DATACENTER} -r;
                            RET_CODE=${?};

                            ## reset vars
                            local METHOD_NAME="${CNAME}#${0}";
                            CNAME="$(basename "${0}")";

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing complete. Return code -> ${RET_CODE}";

                            if [ ${RET_CODE} -eq 0 ]
                            then
                                unset RET_CODE;
                                unset RETURN_CODE;
                                unset DATACENTER;
                                unset RECORD_DETAIL;
                                unset RECORD_TYPE;

                                ## record was successfully added, from here we can
                                ## break into the main interface
                                ADD_COMPLETE=${_TRUE};
                                reset; clear; break;
                            else
                                ## an error occurred
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while adding ${RECORD_TYPE} to ${SITE_HOSTNAME}. Return code from add_${RECORD_TYPE}_record.sh -> ${RET_CODE}";
                                print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                unset RET_CODE;
                                unset RETURN_CODE;
                                unset DATACENTER;
                                unset RECORD_DETAIL;

                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                            fi
                            ;;
                        [Xx]|[Qq]|[Cc])
                            ## user chose to cancel
                            ## clear our variables
                            unset DATACENTER;
                            unset RECORD_DETAIL;
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
            else
                ## failed to validate record.
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IP address provided failed validation. Cannot continue.";
                print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                unset RET_CODE;
                unset RETURN_CODE;
                unset PRIMARY_INFO;
                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
            fi
        fi
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  add_subdomain_ui_helper
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function add_subdomain_ui_helper
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting subdomain A record information..";

    trap "print '$(grep system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

    while true
    do
        if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

            break;
        fi

        reset; clear;

        print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
        print "\t$(grep add.record.alias ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/$(echo ${RECORD_TYPE} | tr "[a-z]" "[A-Z]")/")";
        print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        read A_ALIAS;

        reset; clear;
        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A_ALIAS->${A_ALIAS}";

        if [[ ${A_ALIAS} == [Xx] || ${A_ALIAS} == [Qq] || ${A_ALIAS} == [Cc] ]]
        then
            ## user chose to cancel this request
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Subdomain record addition for ${RECORD_TYPE} canceled.";
            unset A_ALIAS;
            unset RECORD_TYPE;
            unset ADD_SUBDOMAINS;
            CANCEL_REQ=${_TRUE};

            print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";

            sleep "${MESSAGE_DELAY}"; reset; clear; break;
        elif [ -z "${A_ALIAS}" ]
        then
            ## alias provided was blank
            unset A_ALIAS;

            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A target requested was blank. Cannot continue.";
            print "$(grep selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
        else
            while true
            do
                if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                then
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

                    break;
                fi

                reset; clear;

                print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                print "\t$(grep add.record.target ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/$(echo ${RECORD_TYPE}| tr "[a-z]" "[A-Z]")/")";
                print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                read A_TARGET;

                reset; clear;
                print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A_TARGET->${A_TARGET}";

                if [ -z "${A_TARGET}" ]
                then
                    ## ip addr provided is blank
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Address ${A_TARGET} was blank. Cannot continue.";
                    print "$(grep selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    unset A_TARGET;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                elif [[ ${A_TARGET} == [Xx] || ${A_TARGET} == [Qq] || ${A_TARGET} == [Cc] ]]
                then
                    ## user has chosen to cancel
                    unset A_TARGET;
                    unset A_ALIAS;
                    unset RECORD_TYPE;
                    unset ADD_SUBDOMAINS;

                    CANCEL_REQ=${_TRUE};

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                    reset; clear;
                    print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                elif [ $(${PLUGIN_ROOT_DIR}/lib/validators/validate_ip_address.sh ${A_TARGET}) -ne 0 ]
                then
                    ## an error occurred in the validator
                    ## advise and retry
                    unset RECORD_DETAIL;

                    reset; clear;
                    print "$(grep ip.address.improperly.formatted "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                else
                    ## run the ip addr through the validator
                    . ${PLUGIN_ROOT_DIR}/lib/validators/validate_record_target.sh a ${RECORD_DETAIL};
                    RET_CODE=${?};

                    if [ ${RET_CODE} -eq 0 ] || [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                    then
                        if [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                        then
                            ## we got a warning on validation - we arent failing, but we do want to inform
                            ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A warning occurred during record validation - failed to validate that record is active.";
                            print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            sleep "${MESSAGE_DELAY}";
                        fi

                        unset RET_CODE;

                        ## ip addr is properly formatted
                        ## continue with request

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Address record provided is ${A_TARGET}";
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${A_ALIAS},${A_TARGET} -s";

                        ## our provided address is valid.
                        ## make a call out to add_records
                        ## to process the request
                        ## couple things we could do here. we're being used to both
                        ## add records to brand new zones as well as add new entries
                        ## to existing zones. find out which we need to do
                        ## this is a new entry to a new zone
                        unset METHOD_NAME;
                        unset CNAME;

                        . ${PLUGIN_ROOT_DIR}/lib/helpers/data/add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${A_ALIAS},${A_TARGET} -s;
                        RET_CODE=${?};

                        ## reset vars
                        local METHOD_NAME="${CNAME}#${0}";
                        CNAME="$(basename "${0}")";

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing complete. Return code -> ${RET_CODE}";

                        if [ ${RET_CODE} -eq 0 ]
                        then
                            unset RET_CODE;
                            unset RETURN_CODE;
                            unset A_ALIAS;
                            unset A_TARGET;
                            unset RECORD_TYPE;

                            ## record added successfully
                            ## from here, we can break out back to main
                            ADD_COMPLETE=${_TRUE};
                            reset; clear; break;
                        else
                            ## an error occurred
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while adding ${RECORD_TYPE} to ${SITE_HOSTNAME}. Return code from add_${RECORD_TYPE}_record.sh -> ${RET_CODE}";
                            print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            unset RET_CODE;
                            unset RETURN_CODE;
                            unset A_TARGET;
                            unset A_ALIAS;

                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                        fi
                    else
                        ## failed to validate record.
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IP address provided failed validation. Cannot continue.";
                        print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                        unset RET_CODE;
                        unset RETURN_CODE;
                        unset PRIMARY_INFO;
                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi
                fi
            done
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
    print "Usage: ${CNAME} [ root | zone | subdomain ]";
    print "  -h|-?   Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

[ "${1}" = "root" ] && add_root_ui_helper;
[ "${1}" = "zone" ] && add_zone_ui_helper;
[ "${1}" = "subdomain" ] && add_subdomain_ui_helper;
