#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  executeDataRetrieval.sh
#         USAGE:  ./executeDataRetrieval.sh [-v] [-u] [-d] [-h] [-?]
#   DESCRIPTION:  Obtains service information for a provided value, which
#                 can be one of Business Unit, Project Code or URL
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
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

unset METHOD_NAME;
unset CNAME;

## lock it
${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/lock.sh lock ${$};
RET_CODE=${?};

[ ${RET_CODE} -ne 0 ] && echo "Application currently in use." && echo ${RET_CODE} && exit ${RET_CODE};

unset RET_CODE;

CNAME="$(basename "${0}")";
METHOD_NAME="${CNAME}#startup";

trap "${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/lock.sh unlock ${$}; exit" INT TERM EXIT;

#===  FUNCTION  ===============================================================
#      NAME:  get_site_by_url
#   DESCRIPTION:  Queries zone files for the provided URL,
#         if results are found, the results are obtained
#         and placed into a site file list for further
#         processing
#    PARAMETERS:  SITE_ID -> the URL to search for
#   RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function get_site_by_url
{
    METHOD_NAME="${CNAME}#${0}"

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Retrieving list of URLs for ${SITE_ID}..";

    if [ ! -d ${NAMED_ROOT} ] || [ ! -d ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configured named root directory ${NAMED_ROOT} does not exist. Cannot continue.";
        RETURN_CODE=23;
    fi

    ## Set the array with the results of a find
    ## for the URL provided. This should return
    ## all the files with the URL in it - and then
    ## filter the ones in the DC directories
    if [ ! -z "${SEARCH_DECOM}" ] && [ "${SEARCH_DECOM}" = "${_TRUE}" ]
    then
        set -A RETRIEVAL_LIST $(find ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR} -type f -name "${NAMED_ZONE_PREFIX}.*" -exec grep -i "${SITE_ID}" {} /dev/null \; -print | awk '{print $1}' | cut -d ":" -f 1-1 | grep -v -w "[PV]H" | uniq);
    else
        set -A RETRIEVAL_LIST $(find ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} -type f -name "${NAMED_ZONE_PREFIX}.*" -exec grep -i "${SITE_ID}" {} /dev/null \; -print  | grep -v ${GROUP_ID}${NAMED_DECOM_DIR} | awk '{print $1}' | cut -d ":" -f 1-1 | grep -v -w "[PV]H" | uniq);
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Completed command execution.. processing..";

    ## check to make sure we got a resultset
    if [ ${#RETRIEVAL_LIST[@]} -eq 0 ]
    then
        ## no resultset, log an error and return the code
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No sites were found for ${SITE_ID}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        ## clear the array
        set -A RETRIEVAL_LIST;

        RETURN_CODE=12;
    else
        ## get the site information
        ## we need the path and filename,
        ## and we're displaying the currently
        ## active datacenter and the site url
        ## make sure A is zero
        A=0;

        while [ ${A} -ne ${#RETRIEVAL_LIST[@]} ]
        do
            if [ $(echo ${IGNORE_LIST} | grep -c $(echo ${RETRIEVAL_LIST[${A}]} | cut -d "/" -f 6)) -eq 1 ] && [ "${SEARCH_DECOM}" != "${_TRUE}" ]
            then
                ## drop it, its in the ignore list
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Ignoring ${RETRIEVAL_LIST[${A}]} due to exclusion rule.";

                TMP_LIST=$(for ITEM in ${RETRIEVAL_LIST[@]}; do echo ${ITEM} | grep -v ${RETRIEVAL_LIST[${A}]}; done);

                if [ ! -z "${TMP_LIST}" ]
                then
                    set -A RETRIEVAL_LIST $(echo ${TMP_LIST});
                else
                    ## we didnt get a resultset, log the error
                    ## and return the code
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No retrievable DNS records were found for ${SITE_ID}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                    ## clear variables
                    set -A RETRIEVAL_LIST;

                    RETURN_CODE=13;

                    break;
                fi
            else
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "URLs obtained: ${RETRIEVAL_LIST[${A}]}";

                SITE_FILE=$(echo ${RETRIEVAL_LIST[${A}]})"|";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_FILE -> ${SITE_FILE[${A}]}";

                ## get the current datacenter
                ## and the site url
                ## temporarily set IFS (input field separator
                CURR_IFS=${IFS};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Replacing IFS...";

                IFS="^";

                for INFO in ${ZONE_DATA_RETRIEVALS}
                do
                    ## because of differences in the way
                    ## the text is placed, we need to know
                    ## where to look based on the info requested
                    if [ "${INFO}" = "$(echo ${ZONE_DATA_RETRIEVALS} | awk '{print $1}')" ]
                    then
                        INFO_VAR=${INFO_VAR}""$(grep "${INFO}" ${RETRIEVAL_LIST[${A}]} | awk '{print $5}')"|";
                    else
                        INFO_VAR=${INFO_VAR}""$(grep "${INFO}" ${RETRIEVAL_LIST[${A}]} | awk '{print $1}');
                    fi
                done

                ## set IFS back to what it was
                IFS=${CURR_IFS};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "INFO_VAR -> ${INFO_VAR}";

                ## write the data out to a file
                echo "$(echo ${SITE_FILE} | sed -e 's/^[ \t]*//')$(echo ${INFO_VAR} | sed -e 's/^[ \t]*//')";

                ## unset the variables so we get fresh
                ## results
                unset SITE_FILE;
                unset INFO_VAR;
                unset INFO;
            fi

            ## increment the counter
            (( A += 1 ));
        done

        ## reset the counter
        A=0

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        ## clear our array
        set -A RETRIEVAL_LIST;

        if [ -z "${RETURN_CODE}" ]
        then
            RETURN_CODE=0;
        fi
    fi
}

#===  FUNCTION  ===============================================================
#      NAME:  get_site_by_bu
#   DESCRIPTION:  Queries zone files for the provided business unit,
#         if results are found, the results are obtained
#         and placed into a site file list for further
#         processing
#    PARAMETERS:  SITE_ID -> the business unit to search for
#   RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function get_site_by_bu
{
    METHOD_NAME="${CNAME}#${0}"

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Retrieving list of Business Units for ${SITE_ID}..";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_ROOT..";

    ## Find the directory for the provided business
    ## unit and set the site root variable to the
    ## returned value
    if [ ! -z "${SEARCH_DECOM}" ] && [ "${SEARCH_DECOM}" = "${_TRUE}" ]
    then
        SITE_ROOT=$(find ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR} -type d -name "*${SITE_ID}*");
    else
        SITE_ROOT=$(find ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} -type d -name "*${SITE_ID}*" | grep -v ${GROUP_ID}${NAMED_DECOM_DIR});
    fi

    ## check to see if we have a resultset..
    if [ -z "${SITE_ROOT}" ]
    then
        ## We don't have a resultset, log the error
        ## and return the code
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No business units were found for ${SITE_ID}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset SITE_ROOT;

        RETURN_CODE=12;
    else
        ## got a working resultset, continue processing
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_ROOT -> ${SITE_ROOT}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting RETRIEVAL_LIST..";

        ## set the RETRIEVAL array to the zone files
        ## contained within ${SITE_ROOT} for processing
        set -A RETRIEVAL_LIST $(ls -ltr ${SITE_ROOT} | awk '{print $9}' | cut -d ":" -f 1-1 | grep -v "[PV]H" | uniq | sed -e '/ *#/d; /^ *$/d');

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETRIEVAL_LIST set - continue processing..";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Completed command execution.. processing..";

        ## check to ensure we got a resultset
        if [ ${#RETRIEVAL_LIST[@]} -eq 0 ]
        then
            ## we didnt get a resultset, log the error
            ## and return the code
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No retrievable DNS records were found for ${SITE_ID}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            ## clear variables
            set -A RETRIEVAL_LIST;
            unset SITE_ROOT;

            RETURN_CODE=13;
        else
            ## get the site information
            ## we need the path and filename,
            ## and we're displaying the currently
            ## active datacenter and the site url
            ## make sure A is zero
            A=0;

            while [ ${A} -ne ${#RETRIEVAL_LIST[@]} ]
            do
                if [ $(echo ${IGNORE_LIST} | grep -c $(echo ${RETRIEVAL_LIST[${A}]} | cut -d "/" -f 6)) -eq 1 ] && [ "${SEARCH_DECOM}" != "${_TRUE}" ]
                then
                    ## drop it, its in the ignore list
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Ignoring ${RETRIEVAL_LIST[${A}]} due to exclusion rule.";

                    TMP_LIST=$(for ITEM in ${RETRIEVAL_LIST[@]}; do echo ${ITEM} | grep -v ${RETRIEVAL_LIST[${A}]}; done);

                    if [ ! -z "${TMP_LIST}" ]
                    then
                        set -A RETRIEVAL_LIST $(echo ${TMP_LIST});
                    else
                        ## we didnt get a resultset, log the error
                        ## and return the code
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No retrievable DNS records were found for ${SITE_ID}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                        ## clear variables
                        set -A RETRIEVAL_LIST;
                        unset SITE_ROOT;

                        RETURN_CODE=13;
                        break;
                    fi
                else
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "URLs obtained: ${RETRIEVAL_LIST[${A}]}";

                    SITE_FILE=$(echo ${RETRIEVAL_LIST[${A}]})"|";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_FILE -> ${SITE_FILE[${A}]}";

                    ## get the current datacenter
                    ## and the site url
                    ## temporarily set IFS (input field separator
                    CURR_IFS=${IFS};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Replacing IFS...";

                    IFS="^";

                    for INFO in ${ZONE_DATA_RETRIEVALS}
                    do
                        ## because of differences in the way
                        ## the text is placed, we need to know
                        ## where to look based on the info requested
                        if [ "${INFO}" = "$(echo ${ZONE_DATA_RETRIEVALS} | awk '{print $1}')" ]
                        then
                            INFO_VAR=${INFO_VAR}""$(grep "${INFO}" ${SITE_ROOT}/${RETRIEVAL_LIST[${A}]} | awk '{print $5}')"|";
                        else
                            INFO_VAR=${INFO_VAR}""$(grep "${INFO}" ${SITE_ROOT}/${RETRIEVAL_LIST[${A}]} | awk '{print $1}');
                        fi
                    done

                    ## set IFS back to what it was
                    IFS=${CURR_IFS};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "INFO_VAR -> ${INFO_VAR}";

                    ## write the data out to a file
                    echo "$(echo ${SITE_ROOT}/${SITE_FILE} | sed -e 's/^[ \t]*//')$(echo ${INFO_VAR} | sed -e 's/^[ \t]*//')";

                    ## unset the variables so we get fresh
                    ## results
                    unset SITE_FILE;
                    unset INFO_VAR;
                    unset INFO;
                fi

                ## increment the counter
                (( A += 1 ));
            done

            ## reset the counter
            A=0

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            ## clear our variables
            set -A RETRIEVAL_LIST;
            unset SITE_ROOT;

            if [ -z "${RETURN_CODE}" ]
            then
                RETURN_CODE=0;
            fi
        fi
    fi
}

#===  FUNCTION  ===============================================================
#      NAME:  get_site_by_prj_code
#   DESCRIPTION:  Queries zone files for the provided project code,
#         if results are found, the results are obtained
#         and placed into a site file list for further
#         processing
#    PARAMETERS:  SITE_ID -> the project code to search for
#   RETURNS:  0 for positive result, >1 for nn-positive
#==============================================================================
function get_site_by_prj_code
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Retrieving list of Business Units for ${SITE_ID}..";

    ## set the RETRIEVAL array to the zone files
    ## contained within ${SITE_ROOT} for processing
    if [ ! -z "${SEARCH_DECOM}" ] && [ "${SEARCH_DECOM}" = "${_TRUE}" ]
    then
        set -A RETRIEVAL_LIST $(find ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT}/${GROUP_ID}${NAMED_DECOM_DIR} -type f -name "*${SITE_ID}*");
    else
        set -A RETRIEVAL_LIST $(find ${NAMED_ROOT}/${NAMED_ZONE_DIR}/${NAMED_MASTER_ROOT} -type f -name "*${SITE_ID}*" | grep -v ${GROUP_ID}${NAMED_DECOM_DIR});
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Completed command execution.. processing..";

    ## check to make sure we have a valid
    ## resultset
    if [ ${#RETRIEVAL_LIST[@]} -eq 0 ]
    then
        ## no resultset, log an error and return the code
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No sites were found for ${SITE_ID}";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        ## clear our variables
        set -A RETRIEVAL_LIST;

        ## return status code
        RETURN_CODE=12;
    else
        ## get the site information
        ## we need the path and filename,
        ## and we're displaying the currently
        ## active datacenter and the site url
        ## make sure A is zero
        A=0;

        while [ ${A} -ne ${#RETRIEVAL_LIST[@]} ]
        do
            if [ $(echo ${IGNORE_LIST} | grep -c $(echo ${RETRIEVAL_LIST[${A}]} | cut -d "/" -f 6)) -eq 1 ] && [ "${SEARCH_DECOM}" != "${_TRUE}" ]
            then
                ## drop it, its in the ignore list
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Ignoring ${RETRIEVAL_LIST[${A}]} due to exclusion rule.";

                TMP_LIST=$(for ITEM in ${RETRIEVAL_LIST[@]}; do echo ${ITEM} | grep -v ${RETRIEVAL_LIST[${A}]}; done);

                if [ ! -z "${TMP_LIST}" ]
                then
                    set -A RETRIEVAL_LIST $(echo ${TMP_LIST});
                else
                    ## we didnt get a resultset, log the error
                    ## and return the code
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No retrievable DNS records were found for ${SITE_ID}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                    ## clear variables
                    set -A RETRIEVAL_LIST;
                    unset SITE_ROOT;

                    RETURN_CODE=13;
                    break;
                fi
            else
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "URLs obtained: ${RETRIEVAL_LIST[${A}]}";

                SITE_FILE=$(echo ${RETRIEVAL_LIST[${A}]})"|";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_FILE -> ${SITE_FILE[${A}]}";

                ## get the current datacenter
                ## and the site url
                ## temporarily set IFS (input field separator
                CURR_IFS=${IFS};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Replacing IFS...";

                IFS="^";

                for INFO in ${ZONE_DATA_RETRIEVALS}
                do
                    ## because of differences in the way
                    ## the text is placed, we need to know
                    ## where to look based on the info requested
                    if [ "${INFO}" = "$(echo ${ZONE_DATA_RETRIEVALS} | awk '{print $1}')" ]
                    then
                        INFO_VAR=${INFO_VAR}""$(grep "${INFO}" ${SITE_ROOT}/${RETRIEVAL_LIST[${A}]} | awk '{print $5}')"|";
                    else
                        INFO_VAR=${INFO_VAR}""$(grep "${INFO}" ${SITE_ROOT}/${RETRIEVAL_LIST[${A}]} | awk '{print $1}');
                    fi
                done

                ## set IFS back to what it was
                IFS=${CURR_IFS};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "INFO_VAR -> ${INFO_VAR}";

                ## write the data out to a file
                echo "$(echo ${SITE_FILE} | sed -e 's/^[ \t]*//')$(echo ${INFO_VAR} | sed -e 's/^[ \t]*//')";

                ## unset the variables so we get fresh
                ## results
                unset SITE_FILE;
                unset INFO_VAR;
                unset INFO;
            fi

            ## increment the counter
            (( A += 1 ));
        done

        ## reset the counter
        A=0

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        ## clear our variables
        set -A RETRIEVAL_LIST;

        if [ -z "${RETURN_CODE}" ]
        then
            RETURN_CODE=0;
        fi
    fi
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#   RETURNS:  0
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Obtain information regarding DNS entries";
    print "Usage: ${CNAME} [-u url] [-b business unit)] [-p project code] [ -d ] [ -e ] [-h] [-?]";
    print "  -u      Obtains information from the DNS master regarding the supplied URL";
    print "  -b      Obtains information from the DNS master regarding the supplied Business Unit";
    print "  -p      Obtains information from the DNS master regarding the supplied project code";
    print "  -d      Search only for decommissioned records";
    print "  -e      Execute processing";
    print "  -h|-?   Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[ ${#} -eq 0 ] && usage;

while getopts ":u:b:p:deh:" OPTIONS
do
    case "${OPTIONS}" in
        u)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_OPTION and SITE_ID..";

            SITE_OPTION=u;
            typeset -u SITE_ID=$(echo "${OPTARG}" | sed -e 's| ||g');

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_OPTION set to ${SITE_OPTION}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_ID set to ${SITE_ID}";
            ;;
        b)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_OPTION and SITE_ID..";

            SITE_OPTION=b;
            typeset -u SITE_ID=$(echo "${OPTARG}" | sed -e 's| ||g');

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_OPTION set to ${SITE_OPTION}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_ID set to ${SITE_ID}";
            ;;
        p)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_OPTION and SITE_ID..";

            SITE_OPTION=p;
            typeset -u SITE_ID=$(echo "${OPTARG}" | sed -e 's| ||g');

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_OPTION set to ${SITE_OPTION}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_ID set to ${SITE_ID}";
            ;;
        d)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SEARCH_DECOM to true..";

            SEARCH_DECOM=${_TRUE};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SEARCH_DECOM -> ${SEARCH_DECOM}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            if [ -z "${SITE_ID}" ]
            then
                ## site information wasnt provided. return an error
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No site information was provided. Unable to continue processing.";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                RETURN_CODE=6;
            else
                case ${SITE_OPTION} in
                    u)
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                        get_site_by_url;
                        ;;
                    b)
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                        get_site_by_bu;
                        ;;
                    p)
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                        get_site_by_prj_code;
                        ;;
                    *)
                        ## no valid option was found. return an error
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No valid SITE_OPTION was found. Please try again.";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
                        RETURN_CODE=99;
                        ;;
                esac
            fi
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done

shift ${OPTIND}-1;

echo ${RETURN_CODE};
exit ${RETURN_CODE};

