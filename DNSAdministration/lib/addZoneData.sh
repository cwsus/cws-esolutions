#!/usr/bin/ksh -x
#==============================================================================
#
#          FILE:  addRecordEntries.sh
#         USAGE:  ./addRecordEntries.sh
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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

## Application constants
CNAME="$(basename ${0})";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname ${SCRIPT_ABSOLUTE_PATH})";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[[ -z "${PLUGIN_ROOT_DIR}" && -f ${SCRIPT_ROOT}/../lib/plugin.sh ]] && . ${SCRIPT_ROOT}/../lib/plugin.sh;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${PLUGIN_ROOT_DIR}" ] && print "Failed to locate configuration data. Cannot continue." && exit 1;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

THIS_CNAME="${CNAME}";
unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

## validate the input
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

CNAME="${THIS_CNAME}";
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
then
    print "Security configuration does not allow the requested action.";

    return ${RET_CODE};
fi

#===  FUNCTION  ===============================================================
#          NAME:  addApexRecordEntry
#   DESCRIPTION:  Adds the initial A record to a provided zone file.
#    PARAMETERS:  None
#==============================================================================
function addApexRecordEntry
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    THIS_CNAME="${CNAME}";
    unset METHOD_NAME;
    unset CNAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    ## validate the input
    ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validateRecordData.sh type ${RECORD_TYPE};
    typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

    CNAME="${THIS_CNAME}";
    METHOD_NAME="${CNAME}#startup";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z ${RET_CODE} ] || [ ${RET_CODE} -ne 0 ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The provided record target is not currently allowed.";

        RETURN_CODE=45;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        unset RECORD_TARGET;
        unset RECORD_WEIGHT;
        unset RECORD_TYPE;
        unset DATACENTER;
        unset WRITE_FILES;
        unset SECONDARY_ZONEFILE;
        unset PRIMARY_ZONEFILE;
        unset DC_ZONEFILE_NAME;
        unset ZONEFILE_NAME;
        unset METHOD_NAME;

        return ${RETURN_CODE};
    fi

    ## set up our zonefile names so we can operate on them
    ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1).${PROJECT_CODE};
    DC_ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1);
    PRIMARY_ZONEFILE=${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DATACENTER}/${DC_ZONEFILE_NAME};
    SECONDARY_ZONEFILE=${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DATACENTER}/${DC_ZONEFILE_NAME};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE_NAME -> ${ZONEFILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DC_ZONEFILE_NAME -> ${DC_ZONEFILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRIMARY_ZONEFILE -> ${PRIMARY_ZONEFILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SECONDARY_ZONEFILE -> ${SECONDARY_ZONEFILE}";

    if [ ! -s ${PRIMARY_ZONEFILE} ] || [ ! -s ${SECONDARY_ZONEFILE} ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more of the requested zone files do not yet exist. Cannot continue.";

        RETURN_CODE=54;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        unset RECORD_TARGET;
        unset RECORD_WEIGHT;
        unset RECORD_TYPE;
        unset DATACENTER;
        unset WRITE_FILES;
        unset SECONDARY_ZONEFILE;
        unset PRIMARY_ZONEFILE;
        unset DC_ZONEFILE_NAME;
        unset ZONEFILE_NAME;
        unset METHOD_NAME;

        return ${RETURN_CODE};
    fi

    [ "${DATACENTER}" = "BOTH" ] && set -A WRITE_FILES ${PRIMARY_ZONEFILE} ${SECONDARY_ZONEFILE};
    [ "${DATACENTER}" = "${PRIMARY_DATACENTER}" ] && set -A WRITE_FILES ${PRIMARY_ZONEFILE};
    [ "${DATACENTER}" = "${SECONDARY_DATACENTER}" ] && set -A WRITE_FILES ${SECONDARY_ZONEFILE};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WRITE_FILES -> ${WRITE_FILES[@]}";

    case ${RECORD_TYPE} in
        [Aa]|[Nn][Ss])
            if [ "${RECORD_TYPE}" = "A" ]
            then
                THIS_CNAME="${CNAME}";
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validateRecordData.sh address ${RECORD_DATA};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                METHOD_NAME="${CNAME}#startup";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z ${RET_CODE} ] || [ ${RET_CODE} -ne 0 ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The provided record data is invalid. Cannot continue.";

                    RETURN_CODE=45;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                    unset RECORD_TARGET;
                    unset RECORD_WEIGHT;
                    unset RECORD_TYPE;
                    unset DATACENTER;
                    unset WRITE_FILES;
                    unset SECONDARY_ZONEFILE;
                    unset PRIMARY_ZONEFILE;
                    unset DC_ZONEFILE_NAME;
                    unset ZONEFILE_NAME;
                    unset METHOD_NAME;
                    unset RET_CODE;

                    return ${RETURN_CODE};
                fi
            fi

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

            ## validate the input
            ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validateRecordData.sh target ${RECORD_TYPE} ${RECORD_DATA};
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

            CNAME="${THIS_CNAME}";
            METHOD_NAME="${CNAME}#startup";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            [ -z ${RET_CODE} ] || [ ${RET_CODE} -ne 0 ] && ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: The provided record data could not be located.";

            if [ $(grep -c "${RECORD_DATA}" ${PRIMARY_ZONEFILE}) -ne 0 ] || [ $(grep -c "${RECORD_DATA}" ${SECONDARY_ZONEFILE}) -ne 0 ]
            then
                ## record already exists, return
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Record for ${RECORD_TYPE}, ${RECORD_DATA} already exists. Cannot add duplicate.";

                RETURN_CODE=43;
            else
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Information valid. Continuing..";

                for ZONEFILE in ${WRITE_FILES[@]}
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE -> ${ZONEFILE}";

                    printf "            IN      ${RECORD_TYPE}                ${RECORD_DATA}\n" >> ${ZONEFILE};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printed ${RECORD_TYPE} record to ${ZONEFILE}";

                    if [ $(grep "${RECORD_DATA}" ${ZONEFILE} | grep -c "${RECORD_TYPE}") -eq 0 ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to write record data to file ${ZONEFILE}.";

                        (( ERROR_COUNT += 1 ));

                        continue;
                    fi

                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONEFILE_NAME} updated on $(date +"%m-%d-%Y") by ${IUSER_AUDIT} per change ${CHANGE_NUM}";
                done

                RETURN_CODE=${ERROR_COUNT};
            fi
            ;;
        [Mm][Xx])
            RECORD_TARGET=$(echo ${RECORD_DATA} | cut -d "," -f 1);
            RECORD_WEIGHT=$(echo ${RECORD_DATA} | cut -d "," -f 2);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_WEIGHT -> ${RECORD_WEIGHT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TARGET -> ${RECORD_TARGET}";

            if [ -z "${RECORD_WEIGHT}" ] || [ -z "${RECORD_TARGET}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more of the requested data entries were not provided. Cannot continue.";

                RETURN_CODE=30;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                unset RECORD_TARGET;
                unset RECORD_WEIGHT;
                unset RECORD_TYPE;
                unset DATACENTER;
                unset WRITE_FILES;
                unset SECONDARY_ZONEFILE;
                unset PRIMARY_ZONEFILE;
                unset DC_ZONEFILE_NAME;
                unset ZONEFILE_NAME;
                unset METHOD_NAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                return ${RETURN_CODE};
            fi

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

            ## validate the input
            ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validateRecordData.sh address ${RECORD_TARGET};
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

            CNAME="${THIS_CNAME}";
            METHOD_NAME="${CNAME}#startup";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ -z ${RET_CODE} ] || [ ${RET_CODE} -ne 0 ]
            then
                ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARN: The provided address is not an IP. If this is intentional, this warning can be ignored.";
            fi

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

            ## validate the input
            ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validateRecordData.sh target ${RECORD_TYPE} ${RECORD_TARGET};
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

            CNAME="${THIS_CNAME}";
            METHOD_NAME="${CNAME}#startup";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            [ -z ${RET_CODE} ] || [ ${RET_CODE} -ne 0 ] && ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: The provided record data could not be located.";

            if [ $(grep "${RECORD_TARGET}" ${PRIMARY_ZONEFILE} | grep "${RECORD_TYPE}") -ne 0 ] || [ $(grep "${RECORD_TARGET}" ${SECONDARY_ZONEFILE} | grep "${RECORD_TYPE}") -ne 0 ]
            then
                ## record already exists, return
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Record for ${RECORD_TYPE}, ${RECORD_DATA} already exists. Cannot add duplicate.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=43;
            else
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Information valid. Continuing..";

                for ZONEFILE in ${WRITE_FILES[@]}
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE -> ${ZONEFILE}";

                    printf "            IN      ${RECORD_TYPE}      ${RECORD_WEIGHT}      ${RECORD_TARGET}\n" >> ${ZONEFILE};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printed ${RECORD_TYPE} record to ${ZONEFILE}";

                    if [ $(grep "${RECORD_TARGET}" ${ZONEFILE} | grep -c "${RECORD_TYPE}") -eq 0 ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to write record data to file ${ZONEFILE}.";

                        (( ERROR_COUNT += 1 ));

                        continue;
                    fi

                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONEFILE_NAME} updated on $(date +"%m-%d-%Y") by ${IUSER_AUDIT} per change ${CHANGE_NUM}";
                done

                RETURN_CODE=${ERROR_COUNT};
            fi
            ;;
        *)
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The selected record type, ${RECORD_TYPE}, cannot exist in the apex of the zone.";

            RETURN_CODE=51;
            ;;
    esac

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    unset RECORD_TARGET;
    unset RECORD_WEIGHT;
    unset RECORD_TYPE;
    unset DATACENTER;
    unset WRITE_FILES;
    unset SECONDARY_ZONEFILE;
    unset PRIMARY_ZONEFILE;
    unset DC_ZONEFILE_NAME;
    unset ZONEFILE_NAME;
    unset METHOD_NAME;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  addSubRecordEntry
#   DESCRIPTION:  Adds the initial A record to a provided zone file.
#    PARAMETERS:  None
#==============================================================================
function addSubRecordEntry
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    THIS_CNAME="${CNAME}";
    unset METHOD_NAME;
    unset CNAME;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    ## validate the input
    ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validateRecordData.sh type ${RECORD_TYPE};
    typeset -i RET_CODE=${?};

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

    CNAME="${THIS_CNAME}";
    METHOD_NAME="${CNAME}#startup";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

    if [ -z ${RET_CODE} ] || [ ${RET_CODE} -ne 0 ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The provided record target is not currently allowed.";

        RETURN_CODE=45;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        unset RECORD_TARGET;
        unset RECORD_WEIGHT;
        unset RECORD_TYPE;
        unset DATACENTER;
        unset WRITE_FILES;
        unset SECONDARY_ZONEFILE;
        unset PRIMARY_ZONEFILE;
        unset DC_ZONEFILE_NAME;
        unset ZONEFILE_NAME;
        unset METHOD_NAME;

        return ${RETURN_CODE};
    fi

    ## set up our zonefile names so we can operate on them
    ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1).${PROJECT_CODE};
    DC_ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1);
    PRIMARY_ZONEFILE=${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DATACENTER}/${DC_ZONEFILE_NAME};
    SECONDARY_ZONEFILE=${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DATACENTER}/${DC_ZONEFILE_NAME};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE_NAME -> ${ZONEFILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DC_ZONEFILE_NAME -> ${DC_ZONEFILE_NAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PRIMARY_ZONEFILE -> ${PRIMARY_ZONEFILE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SECONDARY_ZONEFILE -> ${SECONDARY_ZONEFILE}";

    if [ ! -s ${PRIMARY_ZONEFILE} ] || [ ! -s ${SECONDARY_ZONEFILE} ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more of the requested zone files do not yet exist. Cannot continue.";

        RETURN_CODE=54;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        ERROR_COUNT=0;

        unset METHOD_NAME;
        unset ZONEFILE_NAME;
        unset DC_ZONEFILE_NAME;
        unset PRIMARY_ZONEFILE;
        unset SECONDARY_ZONEFILE;
        unset WRITE_FILES;
        unset RECORD_TYPE;
        unset RECORD_WEIGHT;
        unset RECORD_TARGET;
        unset ZONEFILE;
        unset SRV_TYPE;
        unset SRV_PROTOCOL;
        unset SRV_NAME;
        unset SRV_TTL;
        unset SRV_PRIORITY;
        unset SRV_WEIGHT;
        unset SRV_PORT;
        unset SRV_TARGET;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    [ "${DATACENTER}" = "BOTH" ] && set -A WRITE_FILES ${PRIMARY_ZONEFILE} ${SECONDARY_ZONEFILE};
    [ "${DATACENTER}" = "${PRIMARY_DATACENTER}" ] && set -A WRITE_FILES ${PRIMARY_ZONEFILE};
    [ "${DATACENTER}" = "${SECONDARY_DATACENTER}" ] && set -A WRITE_FILES ${SECONDARY_ZONEFILE};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WRITE_FILES -> ${WRITE_FILES[@]}";

    [ $(grep -c "\$ORIGIN ${ZONE_NAME}." ${PRIMARY_ZONEFILE}) -eq 0 ] && printf "\n\$ORIGIN ${ZONE_NAME}.\n" >> ${PRIMARY_ZONEFILE};
    [ $(grep -c "\$ORIGIN ${ZONE_NAME}." ${SECONDARY_ZONEFILE}) -eq 0 ] && printf "\n\$ORIGIN ${ZONE_NAME}.\n" >> ${SECONDARY_ZONEFILE};

    case ${RECORD_TYPE} in
        [Aa]|[Cc][Nn][Aa][Mm][Ee]|[Tt][Xx][Tt])
            RECORD_ALIAS=$(echo ${RECORD_DATA} | cut -d "," -f 1);
            RECORD_TARGET=$(echo ${RECORD_DATA} | cut -d "," -f 2);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_ALIAS -> ${RECORD_ALIAS}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TARGET -> ${RECORD_TARGET}";

            if [ "${RECORD_TYPE}" = "A" ]
            then
                THIS_CNAME="${CNAME}";
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validateRecordData.sh address ${RECORD_TARGET};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                METHOD_NAME="${CNAME}#startup";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ -z ${RET_CODE} ] || [ ${RET_CODE} -ne 0 ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The provided record data is invalid. Cannot continue.";

                    RETURN_CODE=45;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                    ERROR_COUNT=0;

                    unset METHOD_NAME;
                    unset ZONEFILE_NAME;
                    unset DC_ZONEFILE_NAME;
                    unset PRIMARY_ZONEFILE;
                    unset SECONDARY_ZONEFILE;
                    unset WRITE_FILES;
                    unset RECORD_TYPE;
                    unset RECORD_WEIGHT;
                    unset RECORD_TARGET;
                    unset ZONEFILE;
                    unset SRV_TYPE;
                    unset SRV_PROTOCOL;
                    unset SRV_NAME;
                    unset SRV_TTL;
                    unset SRV_PRIORITY;
                    unset SRV_WEIGHT;
                    unset SRV_PORT;
                    unset SRV_TARGET;

                    return ${RETURN_CODE};
                fi
            fi

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

            ## validate the input
            [ "${RECORD_TYPE}" != "TXT" ] && ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validateRecordData.sh target ${RECORD_TYPE} ${RECORD_TARGET};
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

            CNAME="${THIS_CNAME}";
            METHOD_NAME="${CNAME}#startup";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            [ -z ${RET_CODE} ] || [ ${RET_CODE} -ne 0 ] && ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: The provided record data could not be located.";

            if [ $(grep -c "${RECORD_DATA}" ${PRIMARY_ZONEFILE}) -ne 0 ] || [ $(grep -c "${RECORD_DATA}" ${SECONDARY_ZONEFILE}) -ne 0 ]
            then
                ## record already exists, return
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Record for ${RECORD_TYPE}, ${RECORD_DATA} already exists. Cannot add duplicate.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=43;
            else
                for ZONEFILE in ${WRITE_FILES[@]}
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE -> ${ZONEFILE}";

                    [ "${RECORD_TYPE}" = "TXT" ] && printf "${RECORD_ALIAS}      IN      ${RECORD_TYPE}      \"${RECORD_TARGET}\"\n" >> ${ZONEFILE};
                    [ "${RECORD_TYPE}" != "TXT" ] && printf "${RECORD_ALIAS}      IN      ${RECORD_TYPE}      ${RECORD_TARGET}\n" >> ${ZONEFILE};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printed ${RECORD_TYPE} record to ${ZONEFILE}";

                    if [ $(grep "${RECORD_TARGET}" ${ZONEFILE} | grep -c "${RECORD_TYPE}") -eq 0 ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to write record data to file ${ZONEFILE}.";

                        (( ERROR_COUNT += 1 ));

                        continue;
                    fi

                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONEFILE_NAME} updated on $(date +"%m-%d-%Y") by ${IUSER_AUDIT} per change ${CHANGE_NUM}";
                done

                RETURN_CODE=${ERROR_COUNT};
            fi
            ;;
        [Mm][Xx])
            RECORD_ALIAS=$(echo ${RECORD_DATA} | cut -d "," -f 1);
            RECORD_WEIGHT=$(echo ${RECORD_DATA} | cut -d "," -f 2);
            RECORD_TARGET=$(echo ${RECORD_DATA} | cut -d "," -f 3);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_ALIAS -> ${RECORD_ALIAS}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_WEIGHT -> ${RECORD_WEIGHT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TARGET -> ${RECORD_TARGET}";

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

            ## validate the input
            ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validateRecordData.sh address ${RECORD_TARGET};
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

            CNAME="${THIS_CNAME}";
            METHOD_NAME="${CNAME}#startup";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            if [ -z ${RET_CODE} ] || [ ${RET_CODE} -ne 0 ]
            then
                ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: The provided target is not an IP address. If this is intentional, this warning can be ignored.";
            fi

            THIS_CNAME="${CNAME}";
            unset METHOD_NAME;
            unset CNAME;

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

            ## validate the input
            [ "${RECORD_TYPE}" != "TXT" ] && ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validateRecordData.sh target ${RECORD_TYPE} ${RECORD_TARGET};
            typeset -i RET_CODE=${?};

            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
            [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

            CNAME="${THIS_CNAME}";
            METHOD_NAME="${CNAME}#startup";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

            [ -z ${RET_CODE} ] || [ ${RET_CODE} -ne 0 ] && ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: The provided record data could not be located.";

            if [ $(grep -c "${RECORD_DATA}" ${PRIMARY_ZONEFILE}) -ne 0 ] || [ $(grep -c "${RECORD_DATA}" ${SECONDARY_ZONEFILE}) -ne 0 ]
            then
                ## record already exists, return
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Record for ${RECORD_TYPE}, ${RECORD_DATA} already exists. Cannot add duplicate.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=43;
            else
                for ZONEFILE in ${WRITE_FILES[@]}
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE -> ${ZONEFILE}";

                    printf "${ALIAS}      IN      ${RECORD_TYPE}      ${RECORD_WEIGHT}      ${RECORD_TARGET}.\n" >> ${ZONEFILE};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printed ${RECORD_TYPE} record to ${ZONEFILE}";

                    if [ $(grep "${RECORD_TARGET}" ${ZONEFILE} | grep -c "${RECORD_TYPE}") -eq 0 ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to write record data to file ${ZONEFILE}.";

                        (( ERROR_COUNT += 1 ));

                        continue;
                    fi

                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONEFILE_NAME} updated on $(date +"%m-%d-%Y") by ${IUSER_AUDIT} per change ${CHANGE_NUM}";
                done

                RETURN_CODE=${ERROR_COUNT};
            fi
            ;;
        [Ss][Rr][Vv])
            ## service records are special because theres ALOT of info
            ## in them
            ## service records are constructed as follows:
            ##_service._protocol.name TTL Class SRV Priority Weight Port Target
            ## sample (email record for smtp):
            ## _submission._tcp.email.caspersbox.com 86400 IN SRV 10 10 25 caspersb-r1b13.caspersbox.com
            ## see http://en.wikipedia.org/wiki/SRV_record for more "INFO"
            ## set up our record information
            SRV_TYPE=$(echo ${IP_ADDR} | cut -d "," -f 1);
            SRV_PROTOCOL=$(echo ${IP_ADDR} | cut -d "," -f 2);
            SRV_NAME=$(echo ${IP_ADDR} | cut -d "," -f 3);
            SRV_TTL=$(echo ${IP_ADDR} | cut -d "," -f 4);
            SRV_PRIORITY=$(echo ${IP_ADDR} | cut -d "," -f 5);
            SRV_WEIGHT=$(echo ${IP_ADDR} | cut -d "," -f 6);
            SRV_PORT=$(echo ${IP_ADDR} | cut -d "," -f 7);
            SRV_TARGET=$(echo ${IP_ADDR} | cut -d "," -f 8);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_TYPE->${SRV_TYPE}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_PROTOCOL->${SRV_PROTOCOL}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_NAME->${SRV_NAME}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_TTL->${SRV_TTL}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_PRIORITY->${SRV_PRIORITY}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_WEIGHT->${SRV_WEIGHT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_PORT->${SRV_PORT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_TARGET->${SRV_TARGET}";

            ## check to make sure we have the information, if not, throw an "ERROR"
            if [ -z "${SRV_TYPE}" ] || [ -z "${SRV_PROTOCOL}" ] || [ -z "${SRV_NAME}" ] || [ -z "${SRV_TTL}" ] ||
                [ -z "${SRV_PRIORITY}" ] || [ -z "${SRV_WEIGHT}" ] || [ -z "${SRV_PORT}" ] || [ -z "${SRV_TARGET}" ]
            then
                ## something was blank. return an "ERROR"
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more of the requested data entries were not provided. Cannot continue.";

                RETURN_CODE=30;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                ERROR_COUNT=0;

                unset METHOD_NAME;
                unset ZONEFILE_NAME;
                unset DC_ZONEFILE_NAME;
                unset PRIMARY_ZONEFILE;
                unset SECONDARY_ZONEFILE;
                unset WRITE_FILES;
                unset RECORD_TYPE;
                unset RECORD_WEIGHT;
                unset RECORD_TARGET;
                unset ZONEFILE;
                unset SRV_TYPE;
                unset SRV_PROTOCOL;
                unset SRV_NAME;
                unset SRV_TTL;
                unset SRV_PRIORITY;
                unset SRV_WEIGHT;
                unset SRV_PORT;
                unset SRV_TARGET;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                return ${RETURN_CODE};
            else
                THIS_CNAME="${CNAME}";
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validateRecordData.sh srvtype ${SRV_TYPE};
                typeset -i SRV_TYPE_CODE=${?};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_TYPE_CODE -> ${SRV_TYPE_CODE}";

                ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validateRecordData.sh srvproto ${SRV_PROTOCOL};
                typeset -i SRV_PROTO_CODE=${?};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_PROTO_CODE -> ${SRV_PROTO_CODE}";

                ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validateRecordData.sh target SRV ${SRV_TARGET};
                typeset -i SRV_TARGET_CODE=${?};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SRV_TARGET_CODE -> ${SRV_TARGET_CODE}";

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                METHOD_NAME="${CNAME}#startup";

                [ -z "${SRV_TYPE_CODE}" ] || [ ${SRV_TYPE_CODE} -ne 0 ] && RET_CODE=1;
                [ -z "${SRV_PROTO_CODE}" ] || [ ${SRV_PROTO_CODE} -ne 0 ] && RET_CODE=1;
                [ -z "${SRV_TARGET_CODE}" ] || [ ${SRV_TARGET_CODE} -ne 0 ] && RET_CODE=1;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                if [ ! -z "${RET_CODE}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The provided record data is invalid. Cannot continue.";

                    RETURN_CODE=45;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                    ERROR_COUNT=0;

                    unset METHOD_NAME;
                    unset ZONEFILE_NAME;
                    unset DC_ZONEFILE_NAME;
                    unset PRIMARY_ZONEFILE;
                    unset SECONDARY_ZONEFILE;
                    unset WRITE_FILES;
                    unset RECORD_TYPE;
                    unset RECORD_WEIGHT;
                    unset RECORD_TARGET;
                    unset ZONEFILE;
                    unset SRV_TYPE;
                    unset SRV_PROTOCOL;
                    unset SRV_NAME;
                    unset SRV_TTL;
                    unset SRV_PRIORITY;
                    unset SRV_WEIGHT;
                    unset SRV_PORT;
                    unset SRV_TARGET;
                    unset SRV_TYPE_CODE;
                    unset SRV_PROTO_CODE;
                    unset SRV_TARGET_CODE;
                    unset RET_CODE;

                    return ${RETURN_CODE};
                fi

                THIS_CNAME="${CNAME}";
                unset METHOD_NAME;
                unset CNAME;

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                ## validate the input
                [ "${RECORD_TYPE}" != "TXT" ] && ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/validators/validateRecordData target ${RECORD_TYPE} ${RECORD_TARGET};
                typeset -i RET_CODE=${?};

                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                CNAME="${THIS_CNAME}";
                METHOD_NAME="${CNAME}#startup";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                [ -z ${RET_CODE} ] || [ ${RET_CODE} -ne 0 ] && ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WARNING: The provided record data could not be located.";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Information valid. Continuing..";

                for ZONEFILE in ${WRITE_FILES[@]}
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE -> ${ZONEFILE}";

                    printf "_${SRV_TYPE}._${SRV_PROTOCOL}.${SRV_NAME}      ${SRV_TTL}      IN      SRV      ${SRV_PRIORITY}      ${SRV_WEIGHT}      ${SRV_PORT}      ${SRV_TARGET}.\n" >> ${ZONEFILE}

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Printed ${RECORD_TYPE} record to ${ZONEFILE}";

                    if [ $(grep "${SRV_TARGET}" ${ZONEFILE} | grep -c "${RECORD_TYPE}") -eq 0 ]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to write record data to file ${ZONEFILE}.";

                        (( ERROR_COUNT += 1 ));

                        continue;
                    fi

                    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONEFILE_NAME} updated on $(date +"%m-%d-%Y") by ${IUSER_AUDIT} per change ${CHANGE_NUM}";
                done

                RETURN_CODE=${ERROR_COUNT};
            fi
            ;;
        *)
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The selected record type, ${RECORD_TYPE}, cannot exist in the apex of the zone.";

            RETURN_CODE=51;
            ;;
    esac

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    ERROR_COUNT=0;

    unset METHOD_NAME;
    unset ZONEFILE_NAME;
    unset DC_ZONEFILE_NAME;
    unset PRIMARY_ZONEFILE;
    unset SECONDARY_ZONEFILE;
    unset WRITE_FILES;
    unset RECORD_TYPE;
    unset RECORD_WEIGHT;
    unset RECORD_TARGET;
    unset ZONEFILE;
    unset SRV_TYPE;
    unset SRV_PROTOCOL;
    unset SRV_NAME;
    unset SRV_TTL;
    unset SRV_PRIORITY;
    unset SRV_WEIGHT;
    unset SRV_PORT;
    unset SRV_TARGET;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";
    typeset -i RETURN_CODE=3;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    print "${CNAME} - Add zone entries to a given zonefile";
    print "Usage: ${CNAME} [ -b <business unit> ] [ -p <project code> ] [ -z <zone name> ] [ -c <change request> ] [ -t <address type> ] [ -a <record information> ] [ -d <datacenter> ] [ -r ] [ -s ] [ -e ] [-?|-h show this help]";
    print "  -b      The associated business unit.";
    print "  -p      The associated project code";
    print "  -z      The zone name, eg example.com";
    print "  -c      The change order associated with this request";
    print "  -t      Address type to add, eg A, MX, CNAME";
    print "  -a      Comma-delimited record information to add";
    print "  -d      The datacenter to add the initial A record to.";
    print "  -r      Add record to the apex of the provided zone";
    print "  -s      Add record as a subdomain of the provided zone";
    print "  -s      Execute processing";
    print "  -h|-?   Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

[ ${#} -eq 0 ] && usage;

while getopts "b:p:z:c:t:a:d:rseh" OPTIONS 2>/dev/null
do
    case ${OPTIONS} in
        b)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting BUSINESS_UNIT..";

            typeset -u BUSINESS_UNIT="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
            ;;
        p)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting PROJECT_CODE..";

            typeset -u PROJECT_CODE="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROJECT_CODE -> ${PROJECT_CODE}";
            ;;
        z)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ZONE_NAME..";

            ZONE_NAME="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONE_NAME -> ${ZONE_NAME}";
            ;;
        c)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            typeset -u CHANGE_NUM="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        t)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting RECORD_TYPE..";

            typeset -u RECORD_TYPE="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_TYPE -> ${RECORD_TYPE}";
            ;;
        a)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting RECORD_DATA..";

            RECORD_DATA="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RECORD_DATA -> ${RECORD_DATA}";
            ;;
        d)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting DATACENTER..";

            typeset -u DATACENTER="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DATACENTER -> ${DATACENTER}";
            ;;
        r)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ADD_APEX..";

            ADD_APEX="${_TRUE}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_APEX -> ${ADD_APEX}";
            ;;
        s)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting ADD_SUB..";

            ADD_SUB="${_TRUE}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADD_SUB -> ${ADD_SUB}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating data..";

            if [ -z "${BUSINESS_UNIT}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";

                RETURN_CODE=15;
            elif [ -z "${PROJECT_CODE}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";

                RETURN_CODE=24;
            elif [ -z "${ZONE_NAME}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";

                RETURN_CODE=24;
            elif [ -z "${CHANGE_NUM}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change order was provided. Unable to continue processing.";

                RETURN_CODE=17;
            else
                if [ -z "${RECORD_TYPE}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The record type was not provided. Unable to continue processing.";

                    RETURN_CODE=20;
                elif [ -z "${RECORD_DATA}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The record data was not provided. Unable to continue processing.";

                    RETURN_CODE=20;
                else
                    [ -z "${DATACENTER}" ] && DATACENTER="BOTH";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    [ ! -z "${ADD_APEX}" ] && [ "${ADD_APEX}" = "${_TRUE}" ] && addApexRecordEntry;
                    [ ! -z "${ADD_SUB}" ] && [ "${ADD_SUB}" = "${_TRUE}" ] && addSubRecordEntry;
                fi
            fi
            ;;
        *)
            usage;
            ;;
    esac
done

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset BUSINESS_UNIT;
unset PROJECT_CODE;
unset ZONE_NAME;
unset CHANGE_NUM;
unset RECORD_TYPE;
unset RECORD_DATA;
unset DATACENTER;
unset ADD_APEX;
unset ADD_SUB;
unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset THIS_CNAME;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return ${RETURN_CODE};
