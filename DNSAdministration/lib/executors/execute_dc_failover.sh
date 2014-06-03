#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  execute_dc_failover.sh
#         USAGE:  ./execute_dc_failover.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#   DESCRIPTION:  Processes failover requests for an entire datacenter.
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
[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/${PLUGIN_NAME}.sh ]] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/${PLUGIN_NAME}.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

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
. ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/security/check_main.sh > /dev/null 2>&1;
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Security configuration does not allow the requested action." && echo ${RET_CODE} && exit ${RET_CODE};

## unset the return code
unset RET_CODE;

## lock it
${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/lock.sh lock ${$};
RET_CODE=${?};

[ ${RET_CODE} != 0 ] && echo "Application currently in use." && echo ${RET_CODE} && exit ${RET_CODE};

unset RET_CODE;

CNAME="$(basename "${0}")";
METHOD_NAME="${CNAME}#startup";

trap "${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/lock.sh unlock ${$}; exit" INT TERM EXIT;

#===  FUNCTION  ===============================================================
#          NAME:  failover_bu
#   DESCRIPTION:  Processes and implements a DNS site failover
#    PARAMETERS:  Parameters obtained via command-line flags
#          NAME:  usage for positive result, >1 for non-positive
#==============================================================================
function failover_dc
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_DC -> ${TARGET_DC}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Starting up..";

    ## we want to take pretty much everything
    ## in the db directory.
    for UNIT in $(ls -ltr ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} | awk '{print $9}' | grep -v '^$')
    do
        if [ $(echo ${IGNORE_LIST} | grep -c ${UNIT}) -eq 1 ]
        then
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Dropping ${UNIT} per configured ignore list");
        else
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Now operating on ${UNIT}..");
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Backing up files..");

            ## create the filename
            TARFILE_DATE=$(date +"%m-%d-%Y");
            BACKUP_FILE=${UNIT}.${CHANGE_NUM}.${TARFILE_DATE}.${IUSER_AUDIT};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "BACKUP_FILE->${BACKUP_FILE}");

            ## tar+gzip
            tar cf ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar \
                -C ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} ${UNIT}/${NAMED_ZONE_PREFIX}.* > /dev/null 2>&1;
            gzip ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar > /dev/null 2>&1;

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Backup for ${UNIT} complete - continuing..");

            ## make sure we got a good backup
            if [ -s ${PLUGIN_ROOT_DIR}/${BACKUP_DIRECTORY}/${BACKUP_FILE}.tar.gz ]
            then
                ## we did. keep going.
                ## create the business unit directories
                mkdir ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT};

                ## loop through the zone files
                for FILENAME in $(ls -ltr ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${UNIT} | awk '{print $9}' | cut -d ":" -f 1-1 | grep -v "[PV]H" | uniq | sed -e '/ *#/d; /^ *$/d');
                do
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Now operating on ${UNIT}/${FILENAME}..");

                    ## copy the target datacenter file
                    cp ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${UNIT}/${TARGET_DC}/$(echo ${FILENAME} | cut -d "." -f 1-2) ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME};

                    ## setup serial numbers
                    LAST_SERIAL=$(grep "; serial" ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME} | awk '{print $1}' | sed -e '/^$/d');
                    [ $(echo ${LAST_SERIAL} | cut -c 1-8) -eq $(date +"%Y%m%d") ] && SERIAL_NUM=$(( ${LAST_SERIAL} + 1 )) || SERIAL_NUM=$(date +"%Y%m%d")00

                    ## fill it
                    set -A CHG_ARRAY ${LAST_SERIAL} ${TARGET_DC} $(date +"%m-%d-%Y") ${IUSER_AUDIT} ${CHANGE_NUM} ${SERIAL_NUM};

                    for INDICATOR in LAST_SERIAL DATACENTER DATE USER_NAME REQUEST_NUMBER SERIAL_NUM
                    do
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${INDICATOR} -> ${CHG_ARRAY[${A}]}";

                        ## we can't use addServiceIndicators here because the right variables
                        ## aren't set AND the sheer volume. so we're just going to do it
                        ## here. well, probably can, but its easier here. its not good
                        ## code re-use, but whatever, it should work
                        ## TODO: try and make this work with addServiceIndicators
                        . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/addServiceIndicators.sh -r ${GROUP_ID}${UNIT} -f ${FILENAME} -t ${TARGET_DC} -i ${IUSER_AUDIT} -c ${CHANGE_NUM} -e;
                        RET_CODE=${?};

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "RET_CODE_>${RET_CODE}");

                        if [ ${RET_CODE} != 0 ]
                        then
                            $(${LOGGER} "ERROR" ${METHOD_NAME} ${CNAME} ${LINENO} "Failed to update ${GROUP_ID}${UNIT}/${FILENAME}.");

                            (( ERROR_COUNT += 1 ));
                        fi

                        (( A += 1 ));
                    done

                    ## unset serials and array
                    set -A CHG_ARRAY;
                    unset LAST_SERIAL;
                    unset SERIAL_NUM;
                    unset INDICATOR;
                    A=0;
                done

                unset FILENAME;
                unset BACKUP_FILE;
            else
                ## failed to create backup file. this is not good.
                ## fail here.
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup processing FAILED. Cannot continue.";

                RETURN_CODE=57;
                break;
            fi
        fi
    done

    unset UNIT;
    unset UNIT;
    unset LAST_SERIAL;
    unset SERIAL_NUM;
    set -A CHG_ARRAY;
    A=0;

    if [ -z "${RETURN_CODE}" ]
    then
        if [ ! ${ERROR_COUNT} -ge ${FAILURE_THRESHOLD} ]
        then
            ## some errors were encountered during temp file processing,
            ## and the number meets or exceeds our failure threshold.
            ## throw back an error
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Backup processing FAILED. Cannot continue.";

            RETURN_CODE=57;
        else
            ## backup processing didnt fail, so keep going
            ## the above should complete processing against the zones
            ## we should be able to copy them in place at this point
            ## we'll just copy the zone files into the right places,
            ## shouldn't need to do more than that
            ERROR_COUNT=0;

            for UNIT in $(ls -ltr ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY} | awk '{print $9}' | grep -v '^$')
            do
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Now operating on ${UNIT}..");

                ## loop through the zone files
                for FILENAME in $(ls -ltr ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT} | awk '{print $9}' | cut -d ":" -f 1-1 | uniq | sed -e '/ *#/d; /^ *$/d');
                do
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Copying ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME} to ${NAMED_ROOT}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME}");

                    ## copy the target datacenter file
                    cp ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME} ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME};

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Copied ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME} to ${NAMED_ROOT}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME}");
                done
            done

            unset FILENAME;
            unset UNIT;

            ## files should be in place, change should be complete
            ## perform a checksum to make sure
            for UNIT in $(ls -ltr ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY} | awk '{print $9}' | grep -v '^$')
            do
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Now operating on ${UNIT}..");

                ## loop through the zone files
                for FILENAME in $(ls -ltr ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT} | awk '{print $9}' | cut -d ":" -f 1-1 | uniq | sed -e '/ *#/d; /^ *$/d');
                do
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Checksum ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME} <-> ${NAMED_ROOT}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME}");
                    TMP_CHECKSUM=$(cksum ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME} | awk '{print $1}');
                    OP_CHECKSUM=$(cksum ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME} | awk '{print $1}');

                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "${FILENAME} -> TMP_CHECKSUM -> ${TMP_CHECKSUM}");
                    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "${FILENAME} -> OP_CHECKSUM -> ${OP_CHECKSUM}");

                    ## copy the target datacenter file
                    if [ ${TMP_CHECKSUM} -eq ${OP_CHECKSUM} ]
                    then
                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && $(${LOGGER} "DEBUG" ${METHOD_NAME} ${CNAME} ${LINENO} "Checksum ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME} <-> ${NAMED_ROOT}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME} match - continuing..");
                        ${LOGGER} AUDIT "${METHOD_NAME}" "${CNAME}" "${LINENO}" "DNS Failover: Requestor: ${IUSER_AUDIT} - Date: \`date +"%d-%m-%Y"\` - Site: ${UNIT}/${FILENAME} - Change Request: ${CHANGE_NUM} - Switched To: ${TARGET_DC}";

                        unset TMP_CHECKSUM;
                        unset OP_CHECKSUM;
                    else
                        unset TMP_CHECKSUM;
                        unset OP_CHECKSUM;

                        $(${LOGGER} "ERROR" ${METHOD_NAME} ${CNAME} ${LINENO} "Checksum ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${UNIT}/${FILENAME} <-> ${NAMED_ROOT}/${NAMED_MASTER_ROOT}/${UNIT}/${FILENAME} mismatch - continuing..");

                        (( ERROR_COUNT += 1 ));

                        if [ ${ERROR_COUNT} -gt ${FAILURE_THRESHOLD} ]
                        then
                            $(${LOGGER} "ERROR" ${METHOD_NAME} ${CNAME} ${LINENO} "Checksum failure count exceeds threshold. Aborting.");

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            unset FILENAME;
                            unset CHANGE_NUM;
                            unset TARGET_DC;
                            unset UNIT;
                            B=0;

                            RETURN_CODE=8;
                        fi
                    fi
                done

                ERROR_COUNT=0;
                unset FILENAME;
            done

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing temporary files..";
            rm -rf ${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/*;
        fi
    fi

    unset CHANGE_NUM;
    unset TARGET_DC;
    unset UNIT;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
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
    print "Usage: ${CNAME} [-t target datacenter] [-c change order] [-i username] [-e execute] [-h] [-?]";
    print " -t     The datacenter to failover to";
    print " -c     The change control for the request";
    print " -i     The user performing the request";
    print " -e     Execute the request";
    print " -h     Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

while getopts ":t:c:i:eh:" OPTIONS
do
    case "${OPTIONS}" in
        t)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> $OPTARG";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting TARGET_DC..";

            ## Capture the target datacenter
            typeset -u TARGET_DC=${OPTARG}; # This will be the target datacenter to move to

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_DC -> ${TARGET_DC}";
            ;;
        c)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> $OPTARG";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CHANGE_NUM..";

            ## Capture the change control
            typeset -u CHANGE_NUM=${OPTARG};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";
            ;;
        i)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> $OPTARG";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting IUSER_AUDIT..";

            ## Capture the audit userid
            typeset -u IUSER_AUDIT=${OPTARG};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "IUSER_AUDIT -> ${IUSER_AUDIT}";
            ;;
        e)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${TARGET_DC}" ]
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
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                failover_dc;
            fi
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

