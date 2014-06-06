#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  validateServiceRecordData.sh
#         USAGE:  ./validateServiceRecordData.sh
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

[[ ! -z "${TRACE}" && "${TRACE}" = "TRUE" ]] && set -x;

## Application constants
[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";
METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

#===  FUNCTION  ===============================================================
#          NAME:  validateIPAddress
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#==============================================================================
function validateIPAddress
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    if [ ! -z "${1}" ] && [ $(echo ${1} | tr -dc "." | wc -c) -eq 3 ]
    then
        FIRST_OCTET=$(echo ${1} | cut -d "." -f 1);
        SECOND_OCTET=$(echo ${1} | cut -d "." -f 2);
        THIRD_OCTET=$(echo ${1} | cut -d "." -f 3);
        FOURTH_OCTET=$(echo ${1} | cut -d "." -f 4);

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FIRST_OCTET -> ${FIRST_OCTET}";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SECOND_OCTET -> ${SECOND_OCTET}";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "THIRD_OCTET -> ${THIRD_OCTET}";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FOURTH_OCTET -> ${FOURTH_OCTET}";

        if [ ! ${FIRST_OCTET} -le 255 ] && [ ! ${SECOND_OCTET} -le 255 ] &&
            [ ! ${THIRD_OCTET} -le 255 ] && [ ! ${FOURTH_OCTET} -le 255 ]
        then
            ## provided IP address is invalid
            ## print an error
            unset RECORD_DETAIL;
            unset FIRST_OCTET;
            unset SECOND_OCTET;
            unset THIRD_OCTET;
            unset FOURTH_OCTET;

            RETURN_CODE=45;
        else
            unset RECORD_DETAIL;
            unset FIRST_OCTET;
            unset SECOND_OCTET;
            unset THIRD_OCTET;
            unset FOURTH_OCTET;

            RETURN_CODE=0;
        fi
    else
        ## the ip address is not a 4 octet string or is blank
        ## throw an error
        RETURN_CODE=45;
    fi
}

#===  FUNCTION  ===============================================================
#          NAME:  validateRecordType
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#==============================================================================
function validateRecordType
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    [[ -z "${ALLOWED_RECORD_LIST}" || ! -f ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_RECORD_LIST} ]] && RETURN_CODE=0;

    egrep -v "^$|^#" ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_RECORD_LIST} | while read -r ALLOWED_RECORD
    do
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ALLOWED_RECORD -> ${ALLOWED_RECORD}";

        if [ "${1}" = "${ALLOWED_RECORD}" ]
        then
            RETURN_CODE=0;

            break;
        fi

        RETURN_CODE=1;
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset ALLOWED_RECORD;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  validateType
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#       RETURNS:  1
#==============================================================================
function validateType
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    [[ -z "${ALLOWED_RECORD_LIST}" || ! -f ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_RECORD_LIST} ]] && RETURN_CODE=0;

    egrep -v "^$|^#" ${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY}/${ALLOWED_RECORD_LIST} | while read -r ALLOWED_RECORD
    do
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ALLOWED_RECORD -> ${ALLOWED_RECORD}";

        if [ "${1}" = "${ALLOWED_RECORD}" ]
        then
            RETURN_CODE=0;

            break;
        fi

        RETURN_CODE=1;
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset VALIDATE_TYPE;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  validateRecordTarget
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#       RETURNS:  1
#==============================================================================
function validateRecordTarget
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    ## first things first. lets find out what type of record we're
    ## validating. we can perform validation for A, MX, CNAME, and
    ## NS records.
    if [ ! -z "${1}" ]
    then
        typeset -u RR_TYPE=${1};

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RR_TYPE -> ${RR_TYPE}";

        case ${RR_TYPE} in
            [Aa]|[Nn][Ss])
                ## A records will ALWAYS be IP addresses
                ## validate to be sure, but we shouldnt have
                ## been executed if this wasnt true.
                case ${2} in
                    ?([+-])+([0-9]|['.']))
                        ## yup its numbers. lets make sure we can ping it - if its alive then we can use it.
                        ## if not, we throw a warning - it IS possible that the record is being added prior
                        ## to the IP being put in place (although this is highly unlikely)
                        ## we cant ping from the bastions. we have to run against
                        ## one of the proxies
                        ping ${2} > /dev/null 2>&1;
                        PING_RCODE=${?}

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                        if [ ${PING_RCODE} == 0 ]
                        then
                            ## IP is up and responsive. all set.
                            RETURN_CODE=0;
                        else
                            ## we didnt get a good response from ping. warn, but not fail
                            RETURN_CODE=63;
                        fi
                        ;;
                    +([a-z]|[A-Z]|[0-9]|['.']))
                        ## we're doing both a and ns here.. make sure this is an ns
                        if [ "${RR_TYPE}" = "NS" ]
                        then
                            ## okay, ns record. ping validate
                            ping ${2} > /dev/null 2>&1;
                            PING_RCODE=${?}

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                            if [ ${PING_RCODE} == 0 ]
                            then
                                ## all set
                                RETURN_CODE=0;
                            else
                                ## warn, but not fail
                                RETURN_CODE=63;
                            fi
                        else
                            ## ahh. we got a name for an a record. can't have this.
                            RETURN_CODE=1;
                        fi
                        ;;
                esac
                ;;
            [Mm][Xx]|[Ss][Rr][Vv])
                ## mx/srv records get their own special verification process.
                ## we need to validate that the target both has an available A
                ## record AND that the A record is responsive
                case ${2} in
                    ?([+-])+([0-9]|['.']))
                        ## got an IP. for these types of records we should really use a name if available.
                        ## do a reverse lookup to get the name
                        ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/runQuery.sh -t ${RR_TYPE} -u ${2} -o -e;
                        RET_CODE=${?};

                        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]]
                        then
                            RETURN_CODE=1;
                        else
                            ## we have a name associated with our IP. we can therefore
                            ## skip the other validation check (reverse lookup) and move into
                            ## ping validation
                            ping ${RESOLVED_NAME} > /dev/null 2>&1;
                            PING_RCODE=${?}

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                            if [ ${PING_RCODE} == 0 ]
                            then
                                ## all set.
                                RETURN_CODE=0;
                            else
                                ## ping validation failed. warn, but dont fail.
                                RETURN_CODE=63;
                            fi
                        fi
                        ;;
                    +([a-z]|[A-Z]|[0-9]|['.']))
                        ## ahh, we were given a name. all we need to do now is make sure its alive, has a valid
                        ## a record and if these two checks pass, then let it on through
                        ## make sure this record has an A record .. and maybe further to that make sure it has a reverse entry
                        perform_lookup rev ${2};

                        if [ ! -z "${RESOLVED_NAME}" ]
                        then
                            ## we have a name associated with our IP. we can therefore
                            ## skip the other validation check (reverse lookup) and move into
                            ## ping validation
                            ping ${RESOLVED_NAME} > /dev/null 2>&1;
                            PING_RCODE=${?}

                            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                            if [ ${PING_RCODE} == 0 ]
                            then
                                ## all set.
                                RETURN_CODE=0;
                            else
                                ## ping validation failed. warn, but dont fail.
                                RETURN_CODE=63;
                            fi
                        else
                            ## outright fail. for most of these record types, reverse lookups will be
                            ## essential. not sure this is enforced everywhere, but it really should be.
                            RETURN_CODE=1;
                        fi
                        ;;
                esac
                ;;
            [Cc][Nn][Aa][Mm][Ee])
                ## cname records are a bit tricky, but not too awful.
                ## the target can reside either in the zone itself (eg
                ## www points to example.com in example.com's zonefile
                ## or it might point to a wholly different resources
                ## (eg search.example.com points to www.google.com)
                ## need to know what file to look at
                SOURCE_FILE=${3};

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SOURCE_FILE -> ${SOURCE_FILE}";

                if [ -s ${SOURCE_FILE} ]
                then
                    ## we have our source file. check it.
                    if [ $(grep -c ${2} ${SOURCE_FILE}) -ne 0 ]
                    then
                        ## ok, so the target goes back to this domain.
                        ## pass it
                        RETURN_CODE=0;
                    else
                        ## NOT a part of this domain. lets see if it exists anywhere -
                        perform_lookup fwd ${2};

                        if [ ! -z "${RESOLVED_NAME}" ]
                        then
                            ## we found a record, so we'll allow it
                            RETURN_CODE=0;
                        else
                            ## nothing. warn, but not fail
                            RETURN_CODE=63;
                        fi
                    fi
                else
                    ## we don't have a source file to review. can't really continue here.
                    RETURN_CODE=1;
                fi
                ;;
        esac
    else
        ## we dont know what type of record to perform validation for, so we cant really keep going.
        RETURN_CODE=1;
    fi

    unset PING_RCODE;
    unset RR_TYPE;
    unset SOURCE_FILE;
    unset RESOLVED_NAME;
    unset PING_RESPONSE;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  perform_lookup
#   DESCRIPTION:  Provide a re-usable interface for this class to perform
#                 dns lookups
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function perform_lookup
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    for EXTERNAL_SERVER in ${EXT_SLAVES[@]}
    do
        $(ping ${EXTERNAL_SERVER} > /dev/null 2>&1);

        PING_RCODE=${?}

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

        if [ ${PING_RCODE} == 0 ]
        then
            ## stop if its available and run the command
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command on ${EXTERNAL_SERVER}..";

            ## find out if we should do a forward or reverse lookup
            if [ "${1}" -eq "fwd" ]
            then
                ## forward lookup -
                . ${PLUGIN_ROOT_DIR}/lib/runQuery.sh -s ${EXTERNAL_SERVER} -t a -u ${2} -o -e;
            else
                ## reverse lookup -
                . ${PLUGIN_ROOT_DIR}/lib/runQuery.sh -s ${EXTERNAL_SERVER} -r -u ${2} -o -e;
            fi

            ## capture the return code
            RETURN_CODE=${?};

            if [ ${RETURN_CODE} -eq 0 ]
            then
                ## the provided IP does have a name associated. use it.
                RESOLVED_NAME=$(cat ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE});
            fi
        else
            ## first one wasnt available, check the remaining
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${EXTERNAL_SERVER} not responding to ping. Continuing..";
        fi
    done

    unset PING_RCODE;
    unset EXTERNAL_SERVER;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#       RETURNS:  1
#==============================================================================
function usage
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    print "${CNAME} - Validate data provided for a SRV record.";
    print "Usage:  ${CNAME} validate-type validate-data";
    print "         validate-type can be one of: protocol or type";
    print "         validate-data must be the data to perform validation against";

    return 3;
}

[ ${#} -eq 0 ] && usage;
METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

## make sure we have args
[ ${#} -eq 0 ] && usage || validate_protocol ${@};

echo ${RETURN_CODE};
return ${RETURN_CODE};
