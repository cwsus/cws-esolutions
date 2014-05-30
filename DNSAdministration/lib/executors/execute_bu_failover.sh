#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  execute_bu_failover.sh
#         USAGE:  ./execute_bu_failover.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Processes a DNS failover for a specified business unit,
#                 failing over all sites within that unit.
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

trap "${APP_ROOT}/${LIB_DIRECTORY}/lock.sh unlock ${$}; exit" INT TERM EXIT;

## Application contants
[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#          NAME:  failover_bu
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function failover_bu
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_DC -> ${TARGET_DC}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting change array..";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_ARRAY configured..";

    if [ "${VERBOSE}" = "${_TRUE}" ]
    then
        while [ ${A} -ne ${#CHG_ARRAY[@]} ]
        do
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHG_ARRAY->${CHG_ARRAY[${A}]}";
            (( A += 1 ));
        done
    fi

    ## reset counters
    A=0;

    ## First, lets make sure the directory for the provided
    ## BU actually exists
    if [ -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT} ]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_ROOT..";

        SITE_ROOT=${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${BUSINESS_UNIT};
        TARFILE_DATE=$(date +"%m-%d-%Y");
        BACKUP_FILE=${GROUP_ID}${BUSINESS_UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT};

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_ROOT set -> ${SITE_ROOT}";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backing up files..";

        ## backup the existing zone files. if we need to back out a change,
        ## we can do it with these. why tar+gzip ? to carry the process over. we
        ## want consistency, even when it doesnt make a difference
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "BACKUP_FILE->${BACKUP_FILE}");

        ## tar+gzip
        tar cf ${APP_ROOT}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar -C ${SITE_ROOT} ${NAMED_ZONE_PREFIX}.* > /dev/null 2>&1;
        gzip ${APP_ROOT}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar > /dev/null 2>&1;

        ## make sure backup file got created
        if [ -s ${APP_ROOT}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar.gz ]
        then
            unset BACKUP_FILE;

            ## NOTE: this looks good, leave it alone
            for FILENAME in $(ls -ltr ${SITE_ROOT} | awk '{print $9}' | cut -d ":" -f 1 | grep -v "[PV]H" | uniq | sed -e '/ *#/d' -e '/^ *$/d')
            do
                SPLIT=$(echo ${FILENAME} | tr -dc '.' | wc -c);
                DC_FILE=$(echo ${FILENAME} | cut -d "." -f 0-$(echo ${SPLIT}));

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SPLIT->${SPLIT}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DC_FILE->${DC_FILE}";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating operational copy in ${TMP_DIRECTORY}..";

                ## take operational copy
                cp ${SITE_ROOT}/${TARGET_DC}/${DC_FILE} ${APP_ROOT}/${TMP_DIRECTORY}/${FILENAME};

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Calling addServiceIndicators.sh..";

                ## temporarily unset stuff
                unset METHOD_NAME;
                unset CNAME;

                ## add our informational and audit indicators
                . ${APP_ROOT}/lib/addServiceIndicators.sh -r ${GROUP_ID}${BUSINESS_UNIT} -f ${FILENAME} -t ${TARGET_DC} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e;
                RET_CODE=${?};

                ## set method_name and cname back to this
                CNAME="$(basename "${0}")";
                [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "addServiceIndicators execution complete. Return code->${RET_CODE}";

                ## make sure we got zero back
                if [ ${RET_CODE} -eq 0 ]
                then
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Indicators set - checksumming..";

                    ## set the checksum
                    set -A TMP_MD5SUM $(echo "${TMP_MD5SUM[@]}" "$(cksum ${APP_ROOT}/${TMP_DIRECTORY}/${FILENAME} | awk '{print $1}')");

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum complete -> ${TMP_MD5SUM[${A}]}";

                    (( A += 1 ));
                else
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An error occurred during indicator processing. Return code->${RET_CODE}";

                    A=0;
                    B=0;
                    ## a failure occurred during audit processing. abort
                    RETURN_CODE=999;
                fi
            done

            ## unset variables
            unset FILENAME;
            unset SPLIT;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup complete and indicators set. Proceeding..";

            ## reset counters
            A=0;
            B=0;

            ## All our preliminary processing has been completed,
            ## so lets move the updated zone file into place
            for FILENAME in $(ls -ltr ${APP_ROOT}/${TMP_DIRECTORY} | awk '{print $9}' | cut -d ":" -f 1-1 | grep -v "[PV]H" | uniq | sed -e '/ *#/d; /^ *$/d')
            do
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying ${FILENAME} to ${SITE_ROOT}..";

                cp ${APP_ROOT}/${TMP_DIRECTORY}/${FILENAME} ${SITE_ROOT}/${FILENAME};

                ## configure the checksum array
                set -A NEW_MD5SUM $(echo "${NEW_MD5SUM[@]}" "$(cksum ${SITE_ROOT}/${FILENAME} | awk '{print $1}')");

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checksum complete -> ${TMP_MD5SUM[${A}]}";

                (( A += 1 ));
            done

            ## reset counters
            A=0;

            ## validate checksums - they need to match, if not,
            ## something isn't right
            while [ ${A} -ne ${C} ]
            do
                if [ "${NEW_MD5SUM[${A}]}" = "${TMP_MD5SUM[${A}]}" ]
                then
                    ## Log an audit record
                    ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: $(date +"%d-%m-%Y") - Site: ${UNIT}/${DC_FILE} - Change Request: ${CHANGE_NUM} - Switched To: ${TARGET_DC}";
                else
                    ## some kind of error occurred
                    ## log it out and return the code
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHECKSUM FAILURE: ${NEW_MD5SUM[${A}]} !=  ${TMP_MD5SUM[${A}]}";
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    ## unset variables
                    unset SITE_ROOT;
                    unset FILENAME;
                    unset SPLIT;
                    set -A TMP_MD5SUM;
                    set -A NEW_MD5SUM;

                    RETURN_CODE=8;
                fi

                (( A += 1 ));
            done

            ## reset the counter
            A=0;
            C=0;

            ## if we got this far, it means the checksums
            ## match, and the failover should have succeeded.
            ## we can clean up our temp files now
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Deleting temp files..";

            rm ${APP_ROOT}/${TMP_DIRECTORY}/*;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            ## unset variables
            unset SITE_ROOT;
            unset FILENAME;
            unset SPLIT;
            unset DC_FILE;
            set -A TMP_MD5SUM;
            set -A NEW_MD5SUM;

            RETURN_CODE=0;
        else
            ## failed to create backup file
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested business unit does not have a defined group. Cannot continue.";

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            ## unset variables
            unset SITE_ROOT;
            unset FILENAME;
            unset SPLIT;
            set -A TMP_MD5SUM;
            set -A NEW_MD5SUM;

            RETURN_CODE=57;
        fi
    else
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The requested business unit does not have a defined group. Cannot continue.";

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        ## unset variables
        unset SITE_ROOT;
        unset FILENAME;
        unset SPLIT;
        set -A TMP_MD5SUM;
        set -A NEW_MD5SUM;

        RETURN_CODE=10;
    fi
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function usage
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Execute a site failover";
    print "Usage: ${CNAME} [-b business unit] [-f named zone file] [-t target datacenter] [-p project code] [-h] [-?]";
    print " -b     The business unit that will be failed over";
    print " -t     The new target datacenter";
    print " -c     The change control associated with this change";
    print " -i     The user executing the request";
    print " -e     Execute the request";
    print " -h|-?  Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../lib/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

[ ${#} -eq 0 ] && usage;

OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

unset METHOD_NAME;
unset CNAME;

## check security
. ${PLUGIN_ROOT_DIR}/lib/security/check_main.sh > /dev/null 2>&1;
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Security configuration does not allow the requested action." && echo ${RET_CODE} && exit ${RET_CODE};

## unset the return code
unset RET_CODE;

## lock it
${APP_ROOT}/${LIB_DIRECTORY}/lock.sh lock ${$};
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Application currently in use." && echo ${RET_CODE} && exit ${RET_CODE};

unset RET_CODE;

CNAME="$(basename "${0}")";
METHOD_NAME="${CNAME}#startup";

while getopts ":b:t:c:i:eh:" OPTIONS
do
    case "${OPTIONS}" in
        b)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting BUSINESS_UNIT..";

            ## Capture the business unit
            typeset -u BUSINESS_UNIT="${OPTARG}"; # This will be the BU to move

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "BUSINESS_UNIT -> ${BUSINESS_UNIT}";
            ;;
        t)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting TARGET_DC..";

            ## Capture the target datacenter
            typeset -u TARGET_DC="${OPTARG}"; # This will be the target datacenter to move to

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_DC -> ${TARGET_DC}";
            ;;
        c)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            ## Capture the change control
            typeset -u CHANGE_NUM="${OPTARG}"; # This will be the project code to move

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        i)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting IUSER_AUDIT..";

            ## Capture the audit userid
            IUSER_AUDIT="${OPTARG}"; # This will be the target datacenter to move to

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
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
            elif [ -z "${TARGET_DC}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target datacenter was provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=17;
            elif [ -z "${CHANGE_NUM}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No change number was provided. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=19;
            elif [ -z "${IUSER_AUDIT}" ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to audit user account. Unable to continue processing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=20;
            else
                ## We have enough information to process the request, continue
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing.";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                failover_bu;
            fi
            ;;
        h)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
        [\?])
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

echo ${RETURN_CODE};
exit ${RETURN_CODE};

