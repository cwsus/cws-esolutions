#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  add_srv_record.sh
#         USAGE:  ./add_srv_record.sh
#   DESCRIPTION:  Utility to add various record types to a new or existing zone
#                 file.
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

#===  FUNCTION  ===============================================================
#          NAME:  add_zone_addr
#   DESCRIPTION:  Adds the initial A record to a provided zone file.
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function add_zone_addr
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding IP addresses..";

    ## set up our zonefile names so we can operate on them
    ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1).${PROJECT_CODE};
    DC_ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1);

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE_NAME -> ${ZONEFILE_NAME}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DC_ZONEFILE_NAME -> ${DC_ZONEFILE_NAME}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "checking for ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DATACENTER}/${DC_ZONEFILE_NAME}..";

    if [ "${DATACENTER}" = "BOTH" ]
    then
        ## user has requested to operate against both datacenters
        if [ -s ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME} ] &&
            [ -s ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${DC_ZONEFILE_NAME} ]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zonefiles exists. Checking for record type..";

            if [ ! -z "${RECORD_TYPE}" ]
            then
                ## set up our record information
                ## service records are special because theres ALOT of info
                ## in them
                ## service records are constructed as follows:
                ##_service._protocol.name TTL Class SRV Priority Weight Port Target
                ## sample (email record for smtp):
                ## _submission._tcp.email.caspersbox.com 86400 IN SRV 10 10 25 caspersb-r1b13.caspersbox.com
                ## see http://en.wikipedia.org/wiki/SRV_record for more info
                ## set up our record information
                SRV_TYPE=$(echo ${IP_ADDR} | cut -d "," -f 1);
                SRV_PROTOCOL=$(echo ${IP_ADDR} | cut -d "," -f 2);
                SRV_NAME=$(echo ${IP_ADDR} | cut -d "," -f 3);
                SRV_TTL=$(echo ${IP_ADDR} | cut -d "," -f 4);
                SRV_PRIORITY=$(echo ${IP_ADDR} | cut -d "," -f 5);
                SRV_WEIGHT=$(echo ${IP_ADDR} | cut -d "," -f 6);
                SRV_PORT=$(echo ${IP_ADDR} | cut -d "," -f 7);
                SRV_TARGET=$(echo ${IP_ADDR} | cut -d "," -f 8);

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_TYPE->${SRV_TYPE}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_PROTOCOL->${SRV_PROTOCOL}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_NAME->${SRV_NAME}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_TTL->${SRV_TTL}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_PRIORITY->${SRV_PRIORITY}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_WEIGHT->${SRV_WEIGHT}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_PORT->${SRV_PORT}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_TARGET->${SRV_TARGET}";

                ## check to make sure we have the information, if not, throw an error
                if [ -z "${SRV_TYPE}" ] || [ -z "${SRV_PROTOCOL}" ] || [ -z "${SRV_NAME}" ] || [ -z "${SRV_TTL}" ] ||
                    [ -z "${SRV_PRIORITY}" ] || [ -z "${SRV_WEIGHT}" ] || [ -z "${SRV_PORT}" ] || [ -z "${SRV_TARGET}" ]
                then
                    ## something was blank. return an error
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A required entry was blank. Cannot continue.";
                    RETURN_CODE=30;
                else
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Information valid. Continuing..";

                    ## srv records for the zone go into both datacenters equally
                    printf "_${SRV_TYPE}._${SRV_PROTOCOL}.${SRV_NAME}      ${SRV_TTL}      IN      SRV      ${SRV_PRIORITY}      ${SRV_WEIGHT}      ${SRV_PORT}      ${SRV_TARGET}.\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME}
                    printf "_${SRV_TYPE}._${SRV_PROTOCOL}.${SRV_NAME}      ${SRV_TTL}      IN      SRV      ${SRV_PRIORITY}      ${SRV_WEIGHT}      ${SRV_PORT}      ${SRV_TARGET}.\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${DC_ZONEFILE_NAME}

                    ## validate the record was written
                    if [ $(grep -c "_${SRV_TYPE}._${SRV_PROTOCOL}.${SRV_NAME}      ${SRV_TTL}      IN      SRV      ${SRV_PRIORITY}      ${SRV_WEIGHT}      ${SRV_PORT}      ${SRV_TARGET}." ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME}) -eq 1 ] &&
                        [ $(grep -c "_${SRV_TYPE}._${SRV_PROTOCOL}.${SRV_NAME}      ${SRV_TTL}      IN      SRV      ${SRV_PRIORITY}      ${SRV_WEIGHT}      ${SRV_PORT}      ${SRV_TARGET}." ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${DC_ZONEFILE_NAME}) -eq 1 ]
                    then
                        ## record was successfully written
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printed ${RECORD_TYPE} record to ${DC_ZONEFILE_NAME}";

                        ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONEFILE_NAME} updated on $(date +"%m-%d-%Y") by ${IUSER_AUDIT} per change ${CHANGE_NUM}";
                        RETURN_CODE=0;
                    else
                        ## record wasnt written properly to one or more zone files
                        ## cant continue.
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to validate information printed into zone file. Unable to continue.";

                        RETURN_CODE=42;
                    fi
                fi
            else
                ## record type is blank, send an error
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No RECORD_TYPE was provided.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                RETURN_CODE=30;
            fi
        else
            ## zone file doesnt exist or is blank, send an error
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The zone provided either does not exist or is empty.";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
            RETURN_CODE=30;
        fi
    else
        ## set up the datacenter-specific copies
        if [ -s ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DATACENTER}/${DC_ZONEFILE_NAME} ]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zonefile exists. Checking for record type..";

            if [ ! -z "${RECORD_TYPE}" ]
            then
                ## set up our record information
                ## service records are special because theres ALOT of info
                ## in them
                ## service records are constructed as follows:
                ##_service._protocol.name TTL Class SRV Priority Weight Port Target
                ## sample (email record for smtp):
                ## _submission._tcp.email.caspersbox.com 86400 IN SRV 10 10 25 mail.caspersbox.com
                ## see http://en.wikipedia.org/wiki/SRV_record for more info
                ## set up our record information
                SRV_TYPE=$(echo ${IP_ADDR} | cut -d "," -f 1);
                SRV_PROTOCOL=$(echo ${IP_ADDR} | cut -d "," -f 2);
                SRV_NAME=$(echo ${IP_ADDR} | cut -d "," -f 3);
                SRV_TTL=$(echo ${IP_ADDR} | cut -d "," -f 4);
                SRV_PRIORITY=$(echo ${IP_ADDR} | cut -d "," -f 5);
                SRV_WEIGHT=$(echo ${IP_ADDR} | cut -d "," -f 6);
                SRV_PORT=$(echo ${IP_ADDR} | cut -d "," -f 7);
                SRV_TARGET=$(echo ${IP_ADDR} | cut -d "," -f 8);

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_TYPE->${SRV_TYPE}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_PROTOCOL->${SRV_PROTOCOL}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_NAME->${SRV_NAME}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_TTL->${SRV_TTL}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_PRIORITY->${SRV_PRIORITY}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_WEIGHT->${SRV_WEIGHT}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_PORT->${SRV_PORT}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_TARGET->${SRV_TARGET}";

                ## check to make sure we have the information, if not, throw an error
                if [ -z "${SRV_TYPE}" || -z "${SRV_PROTOCOL}" || -z "${SRV_NAME}" || -z "${SRV_TTL}" ] ||
                    [ -z "${SRV_PRIORITY}" || -z "${SRV_WEIGHT}" || -z "${SRV_PORT}" || -z "${SRV_TARGET}" ]
                then
                    ## something was blank. return an error
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A required entry was blank. Cannot continue.";
                    RETURN_CODE=30;
                else
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Information valid. Continuing..";

                    ## srv records for the zone go into both datacenters equally
                    printf "_${SRV_TYPE}._${SRV_PROTOCOL}.${SRV_NAME}      ${SRV_TTL}      IN      SRV      ${SRV_PRIORITY}      ${SRV_WEIGHT}      ${SRV_PORT}      ${SRV_TARGET}.\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DATACENTER}/${DC_ZONEFILE_NAME}

                    ## validate the record was written
                    if [ $(grep -c "_${SRV_TYPE}._${SRV_PROTOCOL}.${SRV_NAME}      ${SRV_TTL}      IN      SRV      ${SRV_PRIORITY}      ${SRV_WEIGHT}      ${SRV_PORT}      ${SRV_TARGET}." ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DATACENTER}/${DC_ZONEFILE_NAME}) -eq 1 ]
                    then
                        ## record was successfully written
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printed ${RECORD_TYPE} record to ${DC_ZONEFILE_NAME}";

                        ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONEFILE_NAME} updated on $(date +"%m-%d-%Y") by ${IUSER_AUDIT} per change ${CHANGE_NUM}";
                        RETURN_CODE=0;
                    else
                        ## record wasnt written properly to one or more zone files
                        ## cant continue.
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to validate information printed into zone file. Unable to continue.";

                        RETURN_CODE=42;
                    fi
                fi
            else
                ## record type is blank, send an error
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No RECORD_TYPE was provided.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                RETURN_CODE=30;
            fi
        else
            ## zone file doesnt exist or is blank, send an error
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The zone provided either does not exist or is empty.";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
            RETURN_CODE=30;
        fi
    fi
}

#===  FUNCTION  ===============================================================
#          NAME:  add_subdomains
#   DESCRIPTION:  Adds subdomains to a provided zone file.
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function add_subdomains
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding IP addresses..";

    ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1).${PROJECT_CODE};
    DC_ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1);

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE_NAME -> ${ZONEFILE_NAME}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DC_ZONEFILE_NAME -> ${DC_ZONEFILE_NAME}";

    if [ -s ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME} ] ||
        [ -s ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${DC_ZONEFILE_NAME} ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checking if an \$ORIGIN ${ZONE_NAME}. line exists..";

        ## check if theres an existing $ORIGIN ${ZONE_NAME}. line
        ## if there is, we dont want to add another, its just tacky
        ## we only check the primary datacenter because if it isnt
        ## there, it isnt in the secondary either
        if [ $(grep -c "\$ORIGIN ${ZONE_NAME}." ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME}) -eq 0 ]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "\$ORIGIN ${ZONE_NAME}. does NOT exist. Writing..";

            printf "\n\$ORIGIN ${ZONE_NAME}.\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME};
            printf "\n\$ORIGIN ${ZONE_NAME}.\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${DC_ZONEFILE_NAME};
        fi

        ## service records are special because theres ALOT of info
        ## in them
        ## service records are constructed as follows:
        ##_service._protocol.name TTL Class SRV Priority Weight Port Target
        ## sample (email record for smtp):
        ## _submission._tcp.email.caspersbox.com 86400 IN SRV 10 10 25 mail.caspersbox.com
        ## see http://en.wikipedia.org/wiki/SRV_record for more info
        ## set up our record information
        SRV_TYPE=$(echo ${IP_ADDR} | cut -d "," -f 1);
        SRV_PROTOCOL=$(echo ${IP_ADDR} | cut -d "," -f 2);
        SRV_NAME=$(echo ${IP_ADDR} | cut -d "," -f 3);
        SRV_TTL=$(echo ${IP_ADDR} | cut -d "," -f 4);
        SRV_PRIORITY=$(echo ${IP_ADDR} | cut -d "," -f 5);
        SRV_WEIGHT=$(echo ${IP_ADDR} | cut -d "," -f 6);
        SRV_PORT=$(echo ${IP_ADDR} | cut -d "," -f 7);
        SRV_TARGET=$(echo ${IP_ADDR} | cut -d "," -f 8);

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_TYPE->${SRV_TYPE}";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_PROTOCOL->${SRV_PROTOCOL}";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_NAME->${SRV_NAME}";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_TTL->${SRV_TTL}";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_PRIORITY->${SRV_PRIORITY}";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_WEIGHT->${SRV_WEIGHT}";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_PORT->${SRV_PORT}";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_TARGET->${SRV_TARGET}";

        ## check to make sure we have the information, if not, throw an error
        if [ -z "${SRV_TYPE}" || -z "${SRV_PROTOCOL}" || -z "${SRV_NAME}" || -z "${SRV_TTL}" ] ||
            [ -z "${SRV_PRIORITY}" || -z "${SRV_WEIGHT}" || -z "${SRV_PORT}" || -z "${SRV_TARGET}" ]
        then
            ## something was blank. return an error
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A required entry was blank. Cannot continue.";
            RETURN_CODE=30;
        else
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Information valid. Continuing..";

            ## srv records for the zone go into both datacenters equally
            printf "_${SRV_TYPE}._${SRV_PROTOCOL}.${SRV_NAME}      ${SRV_TTL}      IN      SRV      ${SRV_PRIORITY}      ${SRV_WEIGHT}      ${SRV_PORT}      ${SRV_TARGET}.\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME}
            printf "_${SRV_TYPE}._${SRV_PROTOCOL}.${SRV_NAME}      ${SRV_TTL}      IN      SRV      ${SRV_PRIORITY}      ${SRV_WEIGHT}      ${SRV_PORT}      ${SRV_TARGET}.\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${DC_ZONEFILE_NAME}

            ## validate the record was written
            if [ $(grep -c "_${SRV_TYPE}._${SRV_PROTOCOL}.${SRV_NAME}      ${SRV_TTL}      IN      SRV      ${SRV_PRIORITY}      ${SRV_WEIGHT}      ${SRV_PORT}      ${SRV_TARGET}." ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME}) -eq 1 ] &&
                 [ $(grep -c "_${SRV_TYPE}._${SRV_PROTOCOL}.${SRV_NAME}      ${SRV_TTL}      IN      SRV      ${SRV_PRIORITY}      ${SRV_WEIGHT}      ${SRV_PORT}      ${SRV_TARGET}." ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${DC_ZONEFILE_NAME}) -eq 1 ]
            then
                ## record was successfully written
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printed ${RECORD_TYPE} record to ${DC_ZONEFILE_NAME}";

                ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONEFILE_NAME} updated on $(date +"%m-%d-%Y") by ${IUSER_AUDIT} per change ${CHANGE_NUM}";
                RETURN_CODE=0;
            else
                ## record wasnt written properly to one or more zone files
                ## cant continue.
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to validate information printed into zone file. Unable to continue.";

                RETURN_CODE=42;
            fi
        fi

        ## unset variables
        unset PROJECT_CODE;
        unset ZONE_NAME;
        unset ZONEFILE_NAME;
        unset DC_ZONEFILE_NAME;
        unset ZONE_NAME;
        unset CHANGE_NUM;
        unset RECORD_TYPE;
        unset IP_ADDR;
        unset IPADDR;
        unset DATACENTER;
        unset SRV_TYPE;
        unset SRV_PROTOCOL;
        unset SRV_NAME;
        unset SRV_TTL;
        unset SRV_PRIORITY;
        unset SRV_WEIGHT;
        unset SRV_PORT;
        unset SRV_TARGET;
        unset BUSINESS_UNIT;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
    else
        ## zone files dont exist, return an error back
        unset PROJECT_CODE;
        unset ZONE_NAME;
        unset ZONEFILE_NAME;
        unset DC_ZONEFILE_NAME;
        unset ZONE_NAME;
        unset CHANGE_NUM;
        unset RECORD_TYPE;
        unset IP_ADDR;
        unset IPADDR;
        unset DATACENTER;
        unset SRV_TYPE;
        unset SRV_PROTOCOL;
        unset SRV_NAME;
        unset SRV_TTL;
        unset SRV_PRIORITY;
        unset SRV_WEIGHT;
        unset SRV_PORT;
        unset SRV_TARGET;
        unset BUSINESS_UNIT;

        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested zone file does not yet exist. Please try again.";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
        RETURN_CODE=39;
    fi
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
    print "Usage: ${CNAME} [-b business unit] [-p project code] [-z zone name] [-i requestor] [-c change request] [-t address type] [-a record information] [-d datacenter] [-r|s] [-?|-h show this help]";
    print "  -b      The associated business unit.";
    print "  -p      The associated project code";
    print "  -z      The zone name, eg example.com";
    print "  -i      The user performing the request";
    print "  -c      The change order associated with this request";
    print "  -t      Address type to add, eg A, MX, CNAME";
    print "  -a      Address matching to A/CNAME or MX record type. Comma-delimited";
    print "  -d      The datacenter to add the initial A record to.";
    print "  -r      Execute processing to add an A record for the zone";
    print "  -s      Execute processing to add a subdomain record for the zone";
    print "  -h|-?   Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[ ${#} -eq 0 ] && usage;

while getopts ":b:p:z:i:c:t:a:d:rsh:" OPTIONS
do
    case "${OPTIONS}" in
        b)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting BUSINESS_UNIT..";

            ## Capture the site root
            typeset -u BUSINESS_UNIT="${OPTARG}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
            ;;
        p)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting PROJECT_CODE..";

            ## Capture the site root
            typeset -u PROJECT_CODE="${OPTARG}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROJECT_CODE -> ${PROJECT_CODE}";
            ;;
        z)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ZONE_NAME..";

            ## Capture the site root
            ZONE_NAME=${OPTARG};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_NAME -> ${ZONE_NAME}";
            ;;
        i)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting IUSER_AUDIT..";

            ## Capture the change control
            IUSER_AUDIT="${OPTARG}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
            ;;
        c)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            ## Capture the change control
            typeset -u CHANGE_NUM="${OPTARG}";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        t)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting RECORD_TYPE..";

            ## Capture the change control
            typeset -u RECORD_TYPE;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TYPE -> ${RECORD_TYPE}";
            ;;
        a)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting IP_ADDR..";

            IP_ADDR=${OPTARG};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IP_ADDR -> ${IP_ADDR}";
            ;;
        d)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting DATACENTER..";

            typeset -u DATACENTER;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DATACENTER -> ${DATACENTER}";
            ;;
        r)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${BUSINESS_UNIT}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=15;
            elif [ -z "${PROJECT_CODE}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=24;
            elif [ -z "${ZONE_NAME}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=24;
            elif [ -z "${CHANGE_NUM}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change order was provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=17;
            elif [ -z "${IUSER_AUDIT}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requestors username was not provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=20;
            elif [ -z "${IP_ADDR}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The IP address was not provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=20;
            elif [ -z "${DATACENTER}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The datacenter to apply the request to was not provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=20;
            else
                ## We have enough information to process the request, continue
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                add_zone_addr;
            fi
            ;;
        s)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${PROJECT_CODE}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=24;
            elif [ -z "${ZONE_NAME}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=24;
            elif [ -z "${CHANGE_NUM}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change order was provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=17;
            elif [ -z "${IUSER_AUDIT}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requestors username was not provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=20;
            elif [ -z "${RECORD_TYPE}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The record type was not provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=20;
            elif [ -z "${IP_ADDR}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The record type was not provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=20;
            else
                ## We have enough information to process the request, continue
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                add_subdomains;
            fi
            ;;
        *)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done

shift ${OPTIND}-1;

return ${RETURN_CODE};
