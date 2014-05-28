#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  add_txt_ui_helper.sh
#         USAGE:  ./add_txt_ui_helper.sh
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
PLUGIN_NAME="dnsadmin";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#          NAME:  add_ip_info
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function add_zone_ui_helper
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting subdomain NS record information..";

    trap "print '$(grep system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

    while true
    do
        if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

            break;
        fi

        reset; clear;

        print "\t\t\t$(grep plugin.application.name ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2)\n";
        print "\t$(grep add.record.alias ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2 | sed "s/%RECORD_TYPE%/$(echo ${RECORD_TYPE} | tr "[a-z]" "[A-Z]")/")";
        print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        read TXT_ALIAS;

        reset; clear;
        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TXT_ALIAS->${TXT_ALIAS}";

        if [[ ${TXT_ALIAS} == [Xx] || ${TXT_ALIAS} == [Qq] || ${TXT_ALIAS} == [Cc] ]]
        then
            ## user chose to cancel this request
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Subdomain record addition for ${RECORD_TYPE} canceled.";
            unset TXT_ALIAS;
            unset RECORD_TYPE;
            unset ADD_SUBDOMAINS;
            CANCEL_REQ=${_TRUE};

            print "$(grep system.request.canceled ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2)";

            sleep "${MESSAGE_DELAY}"; reset; clear; break;
        elif [ -z "${TXT_ALIAS}" ]
        then
            ## alias provided was blank
            unset TXT_ALIAS;

            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A target requested was blank. Cannot continue.";
            print "$(grep selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
        else
            while true
            do
                if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                then
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

                    break;
                fi

                reset; clear;

                print "\t\t\t$(grep plugin.application.name ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2)\n";
                print "\t$(grep add.record.target ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2 | sed "s/%RECORD_TYPE%/$(echo ${RECORD_TYPE}| tr "[a-z]" "[A-Z]")/")";
                print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                read TXT_TARGET;

                reset; clear;
                print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TXT_TARGET->${TXT_TARGET}";

                if [ -z "${TXT_TARGET}" ]
                then
                    ## ip addr provided is blank
                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Address ${TXT_TARGET} was blank. Cannot continue.";
                    print "$(grep selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    unset TXT_TARGET;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                elif [[ ${TXT_TARGET} == [Xx] || ${TXT_TARGET} == [Qq] || ${TXT_TARGET} == [Cc] ]]
                then
                    ## user has chosen to cancel
                    unset TXT_TARGET;
                    unset TXT_ALIAS;
                    unset RECORD_TYPE;
                    unset ADD_SUBDOMAINS;

                    CANCEL_REQ=${_TRUE};

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                    reset; clear;
                    print "$(grep system.request.canceled ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2)\n";
                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                else
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Address record provided is ${TXT_TARGET}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${TXT_ALIAS},${TXT_TARGET} -s";

                    ## our provided address is valid.
                    ## make a call out to add_records
                    ## to process the request

                    ## temp unset
                    unset METHOD_NAME;
                    unset CNAME;

                    . ${APP_ROOT}/lib/helpers/data/add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${TXT_ALIAS},${TXT_TARGET} -r;
                    RET_CODE=${?};

                    ## reset vars
                    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";
                    CNAME="$(basename "${0}")";

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing complete. Return code -> ${RET_CODE}";

                    if [ ${RET_CODE} -eq 0 ]
                    then
                        unset RET_CODE;
                        unset RETURN_CODE;
                        unset TXT_ALIAS;
                        unset TXT_TARGET;
                        unset RECORD_TYPE;

                        ## record was successfully added. ask if we should add more
                        ADD_COMPLETE=${_TRUE};
                        reset; clear; break;
                    else
                        ## an error occurred
                        ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while adding ${RECORD_TYPE} to ${SITE_HOSTNAME}. Return code from add_${RECORD_TYPE}_record.sh -> ${RET_CODE}";
                        print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                        unset RET_CODE;
                        unset RETURN_CODE;
                        unset TXT_TARGET;
                        unset TXT_ALIAS;

                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                    fi
                fi
            done
        fi
    done
}

#===  FUNCTION  ===============================================================
#          NAME:  add_subdomain_ui_helper
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function add_subdomain_ui_helper
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting subdomain NS record information..";

    trap "print '$(grep system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

    while true
    do
        if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

            break;
        fi

        reset; clear;

        print "\t\t\t$(grep plugin.application.name ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2)\n";
        print "\t$(grep add.record.alias ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2 | sed "s/%RECORD_TYPE%/$(echo ${RECORD_TYPE} | tr "[a-z]" "[A-Z]")/")";
        print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        read TXT_ALIAS;

        reset; clear;
        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TXT_ALIAS->${TXT_ALIAS}";

        if [[ ${TXT_ALIAS} == [Xx] || ${TXT_ALIAS} == [Qq] || ${TXT_ALIAS} == [Cc] ]]
        then
            ## user chose to cancel this request
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Subdomain record addition for ${RECORD_TYPE} canceled.";
            unset TXT_ALIAS;
            unset RECORD_TYPE;
            unset ADD_SUBDOMAINS;
            CANCEL_REQ=${_TRUE};

            print "$(grep system.request.canceled ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2)";

            sleep "${MESSAGE_DELAY}"; reset; clear; break;
        elif [ -z "${TXT_ALIAS}" ]
        then
            ## alias provided was blank
            unset TXT_ALIAS;

            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A target requested was blank. Cannot continue.";
            print "$(grep selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

            sleep "${MESSAGE_DELAY}"; reset; clear; continue;
        else
            while true
            do
                if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                then
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

                    break;
                fi

                reset; clear;

                print "\t\t\t$(grep plugin.application.name ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2)\n";
                print "\t$(grep add.record.target ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2 | sed "s/%RECORD_TYPE%/$(echo ${RECORD_TYPE}| tr "[a-z]" "[A-Z]")/")";
                print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                read TXT_TARGET;

                reset; clear;
                print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TXT_TARGET->${TXT_TARGET}";

                if [ -z "${TXT_TARGET}" ]
                then
                    ## ip addr provided is blank
                    ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Address ${TXT_TARGET} was blank. Cannot continue.";
                    print "$(grep selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    unset TXT_TARGET;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                elif [[ ${TXT_TARGET} == [Xx] || ${TXT_TARGET} == [Qq] || ${TXT_TARGET} == [Cc] ]]
                then
                    ## user has chosen to cancel
                    unset TXT_TARGET;
                    unset TXT_ALIAS;
                    unset RECORD_TYPE;
                    unset ADD_SUBDOMAINS;

                    CANCEL_REQ=${_TRUE};

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                    reset; clear;
                    print "$(grep system.request.canceled ${PLUGIN_CONFIG} | grep -v "#" | cut -d "=" -f 2)\n";
                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                else
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Address record provided is ${TXT_TARGET}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${TXT_ALIAS},${TXT_TARGET} -s";

                    ## our provided address is valid.
                    ## make a call out to add_records
                    ## to process the request
                    if [ "${ADD_EXISTING_RECORD}" = "${_TRUE}" ]
                    then
                        ## we're adding a new record to an existing zone.
                        ## call out the appropriate runner
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding new record type to zone..";

                        ## temp unset
                        unset METHOD_NAME;
                        unset CNAME;
                        execute runner here
                            
                        [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";
                        CNAME="$(basename "${0}")";                            

                        check retcode here
                    else
                        . ${APP_ROOT}/lib/helpers/data/add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${TXT_ALIAS},${TXT_TARGET} -s;
                        RET_CODE=${?};

                        ## reset vars
                        [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";
                        CNAME="$(basename "${0}")";

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing complete. Return code -> ${RET_CODE}";

                        if [ ${RET_CODE} -eq 0 ]
                        then
                            unset RET_CODE;
                            unset RETURN_CODE;
                            unset TXT_ALIAS;
                            unset TXT_TARGET;
                            unset RECORD_TYPE;

                            ## record was successfully added. ask if we should add more
                            ADD_COMPLETE=${_TRUE};
                            reset; clear; break;
                        else
                            ## an error occurred
                            ${LOGGER} ERROR "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred while adding ${RECORD_TYPE} to ${SITE_HOSTNAME}. Return code from add_${RECORD_TYPE}_record.sh -> ${RET_CODE}";
                            print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            unset RET_CODE;
                            unset RETURN_CODE;
                            unset TXT_TARGET;
                            unset TXT_ALIAS;

                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                        fi
                    fi
                fi
            done
        fi
    done
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function usage
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Add audit indicators and other flags to the failover zone file";
    print "Usage: ${CNAME} [ zone | subdomain ]";
    print "  -h|-?   Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

## make sure we have arguments
[ ${#} -eq 0 ] && usage;

METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID $$";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments -> ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

[ "${1}" = "zone" ] && add_zone_ui_helper || add_subdomain_ui_helper;

