#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  create_zone.sh
#         USAGE:  ./create_zone.sh
#   DESCRIPTION:  Creates a skeleton zone file and directory structure
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

[[ ! -z "${TRACE}" && "${TRACE}" = "TRUE" ]] && set -x;

## Application constants
[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ -z "${PLUGIN_ROOT_DIR}" && -f ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && echo "Failed to locate configuration data. Cannot continue." && exit 1;

[[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[ ${#} -eq 0 ] && usage;

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

unset METHOD_NAME;
unset CNAME;

## check security
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Security configuration does not allow the requested action." && exit ${RET_CODE};

## unset the return code
unset RET_CODE;

CNAME="$(basename "${0}")";
METHOD_NAME="${CNAME}#startup";

#===  FUNCTION  ===============================================================
#          NAME:  create_skeleton_zone
#   DESCRIPTION:  Creates the necessary group folder, domain folders and creates
#                 skeleton zone files. Skeletons are then updated with the
#                 provided zone name.
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function create_skeleton_zone
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing service indicators..";

    ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1).${PROJECT_CODE};
    DC_ZONEFILE_NAME=${NAMED_ZONE_PREFIX}.$(echo ${ZONE_NAME} | cut -d "." -f 1);
    ADMIN_CONTACT=$(echo ${DNS_SERVER_ADMIN_EMAIL} | sed -e "s/@/./");

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ZONEFILE_NAME -> ${ZONEFILE_NAME}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DC_ZONEFILE_NAME -> ${DC_ZONEFILE_NAME}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ADMIN_CONTACT -> ${ADMIN_CONTACT}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating directories..";

    ## create our directory structure
    mkdir -p ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT};
    mkdir -p ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC};
    mkdir -p ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC};

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating skeleton zonefiles..";

    ## write out the file
    for DC in ${PRIMARY_DC} ${SECONDARY_DC}
    do
        printf "; zone '%%ZONE_NAME%%'   last serial %%LAST_SERIAL%%\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC}/${DC_ZONEFILE_NAME};
        printf "; Currently live in: %%DATACENTER%%\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC}/${DC_ZONEFILE_NAME};
        printf "; updated on %%DATE%% by %%USER_NAME%% per change order %%REQUEST_NUMBER%%\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC}/${DC_ZONEFILE_NAME};
        printf "\$ORIGIN .\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC}/${DC_ZONEFILE_NAME};
        printf "\$TTL ${NAMED_TTL_TIME}\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC}/${DC_ZONEFILE_NAME};
        printf "%%ZONE_NAME%% IN SOA ${NAMED_PRIMARY_SOA}.${NAMED_INTERNET_SUFFIX}. ${ADMIN_CONTACT}. (\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC}/${DC_ZONEFILE_NAME};
        printf "            %%SERIAL_NUM%%      ; serial number of this zone file\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC}/${DC_ZONEFILE_NAME};
        printf "            ${NAMED_REFRESH_INTERVAL}              ; slave refresh\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC}/${DC_ZONEFILE_NAME};
        printf "            ${NAMED_RETRY_INTERVAL}             ; slave retry time in case of a problem\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC}/${DC_ZONEFILE_NAME};
        printf "            ${NAMED_EXPIRATION_INTERVAL}           ; slave expiration time\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC}/${DC_ZONEFILE_NAME};
        printf "            ${NAMED_CACHE_INTERVAL}             ; minimum caching time in case of failed lookups\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC}/${DC_ZONEFILE_NAME};
        printf "            )\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC}/${DC_ZONEFILE_NAME};
        printf "            IN    RP          ${ADMIN_CONTACT}\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC}/${DC_ZONEFILE_NAME};

        if [ ! -z "${ENABLE_LOC_RECORD}" ] && [ "${ENABLE_LOC_RECORD}" = "${_TRUE}" ]
        then
            SITE_COORDINATES=$(grep -w $(echo ${DC} | tr "[A-Z]" "[a-z]")_site_coords ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e 's/^ *//');

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_COORDINATES -> ${SITE_COORDINATES}";

            printf "            IN    LOC         ${SITE_COORDINATES}\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC}/${DC_ZONEFILE_NAME};
        fi

        while [ ${D} -ne ${#NAMED_INTERNET_ADDR[@]} ]
        do
            printf "            IN    NS          ${NAMED_INTERNET_ADDR[${D}]}.${NAMED_INTERNET_SUFFIX}.\n" >> ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${DC}/${DC_ZONEFILE_NAME};

            (( D += 1 ));
        done

        D=0;
    done

    unset DC;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Directory creation and zonefile creation complete. Adding data..";

    ## make sure the directories got created
    if [ -d ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT} ] &&
        [ -d ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC} ] &&
        [ -d ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC} ]
    then
        ## make sure the files got created
        if [ -s ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME} ] &&
            [ -s ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${DC_ZONEFILE_NAME} ]
        then
            ## set up the datacenter-specific copies
            sed -e "s/%ZONE_NAME%/${ZONE_NAME}/" ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME} \
                > ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME}.tmp;
            sed -e "s/%ZONE_NAME%/${ZONE_NAME}/" ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${DC_ZONEFILE_NAME} \
                > ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${DC_ZONEFILE_NAME}.tmp;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moving ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME}.tmp ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Moving ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${DC_ZONEFILE_NAME}.tmp ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${DC_ZONEFILE_NAME}";

            ## move the temp files to overwrite the template
            mv ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME}.tmp ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${PRIMARY_DC}/${DC_ZONEFILE_NAME};
            mv ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${DC_ZONEFILE_NAME}.tmp ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BUSINESS_UNIT}/${SECONDARY_DC}/${DC_ZONEFILE_NAME};

            ## audit log it
            ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Zone ${ZONEFILE_NAME} created on $(date +"%m-%d-%Y") by ${IUSER_AUDIT} per change ${CHANGE_NUM}";

            ## reset
            A=0;
            unset ZONE_NAME;
            unset ZONEFILE_NAME;
            unset DC_ZONEFILE_NAME;
            unset ZONE_NAME;
            unset CHANGE_NUM;
            unset RECORD_TYPE;
            unset IP_ADDR;
            unset DATACENTER;
            unset BUSINESS_UNIT;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
        else
            ## directories were created, but the zone file was not
            ## send back an error
            unset ZONE_NAME;
            unset ZONEFILE_NAME;
            unset DC_ZONEFILE_NAME;
            unset ZONE_NAME;
            unset CHANGE_NUM;
            unset RECORD_TYPE;
            unset IP_ADDR;
            unset DATACENTER;
            unset BUSINESS_UNIT;

            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create datacenter zone files. Please try again.";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            RETURN_CODE=41;
        fi
    else
        ## directories were not created, send back an error
        unset ZONE_NAME;
        unset ZONEFILE_NAME;
        unset DC_ZONEFILE_NAME;
        unset ZONE_NAME;
        unset CHANGE_NUM;
        unset RECORD_TYPE;
        unset IP_ADDR;
        unset DATACENTER;
        unset BUSINESS_UNIT;

        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to create zone directory structure. Please try again.";

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=40;
    fi

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
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Create a skeleton zone file with the necessary components.";
    print "Usage: ${CNAME} [-b business unit] [-p project code] [-z zone name] [-i requestor] [-c change request] [-e] [-?|-h]";
    print "  -b      The associated business unit";
    print "  -p      The associated project code";
    print "  -z      The zone name, eg example.com";
    print "  -i      The user performing the request";
    print "  -c      The change order associated with this request";
    print "  -e      Execute processing";
    print "  -?|-h   Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

while getopts ":b:p:z:i:c:eh:" OPTIONS
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
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting BUSINESS_UNIT..";

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
        e)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${BUSINESS_UNIT}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No business unit was provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=15;
            elif [ -z "${PROJECT_CODE}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No project code was provided. Unable to continue processing.";

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=18;
            elif [ -z "${ZONE_NAME}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No zone name was provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=37;
            elif [ -z "${CHANGE_NUM}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change order was provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=19;
            elif [ -z "${IUSER_AUDIT}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requestors username was not provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=20;
            else
                ## We have enough information to process the request, continue
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                create_skeleton_zone;
            fi
            ;;
        h|[\?])
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
        *)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done

shift ${OPTIND}-1;
return ${RETURN_CODE};
