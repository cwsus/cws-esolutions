#!/usr/bin/ksh -x
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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

## Application constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";
typeset -i OPTIND=0;
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

#===  FUNCTION  ===============================================================
#          NAME:  validateIPAddress
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#==============================================================================
function validateIPAddress
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    if [ -z "${2}" ] || [ $(echo ${2} | tr -dc "." | wc -c) -ne 3 ]
    then
        unset FIRST_OCTET;
        unset SECOND_OCTET;
        unset THIRD_OCTET;
        unset FOURTH_OCTET;

        RETURN_CODE=45;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    FIRST_OCTET=$(echo ${2} | cut -d "." -f 1);
    SECOND_OCTET=$(echo ${2} | cut -d "." -f 2);
    THIRD_OCTET=$(echo ${2} | cut -d "." -f 3);
    FOURTH_OCTET=$(echo ${2} | cut -d "." -f 4);

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FIRST_OCTET -> ${FIRST_OCTET}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SECOND_OCTET -> ${SECOND_OCTET}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "THIRD_OCTET -> ${THIRD_OCTET}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FOURTH_OCTET -> ${FOURTH_OCTET}";

    for OCTET in ${FIRST_OCTET} ${SECOND_OCTET} ${THIRD_OCTET} ${FOURTH_OCTET}
    do
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OCTET -> ${OCTET}";

        if [ ! ${OCTET} -le 255 ]
        then
            RETURN_CODE=45;

            break;
        fi
    done

    [ -z "${RETURN_CODE}" ] && RETURN_CODE=0;

    unset FIRST_OCTET;
    unset SECOND_OCTET;
    unset THIRD_OCTET;
    unset FOURTH_OCTET;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  validateRecordType
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#==============================================================================
function validateRecordType
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";
    local RECORD_TYPE=$(echo ${2} | tr "[a-z]" "[A-Z]");

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    if [ -z "${ALLOWED_RECORD_LIST}" ] || [ ! -f ${ALLOWED_RECORD_LIST} ]
    then
        RETURN_CODE=0;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    while read -r ALLOWED_RECORD
    do
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ALLOWED_RECORD -> ${ALLOWED_RECORD}";

        [ -z "$(echo ${ALLOWED_RECORD} | sed -e '/^ *#/d;s/#.*//;s/^ *//g;s/ *$//g')" ] && continue;

        if [ "${RECORD_TYPE}" = "${ALLOWED_RECORD}" ]
        then
            RETURN_CODE=0;

            break;
        fi

        continue;
    done < ${ALLOWED_RECORD_LIST}

    [ -z "${RETURN_CODE}" ] && RETURN_CODE=1;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset RECORD_TYPE;
    unset ALLOWED_RECORD;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
set -x
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    ## first things first. lets find out what type of record we're
    ## validating. we can perform validation for A, MX, CNAME, and
    ## NS records.
    if [ ${#} -ne 3 ]
    then
        usage;

        RETURN_CODE=3;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        unset PING_RCODE;
        unset RR_TYPE;
        unset SOURCE_FILE;
        unset RESOLVED_NAME;
        unset PING_RESPONSE;

        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

        return ${RETURN_CODE};
    fi

    typeset -u RR_TYPE=${2};

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RR_TYPE -> ${RR_TYPE}";

    case ${RR_TYPE} in
        [Aa]|[Nn][Ss])
            ## A records will ALWAYS be IP addresses
            ## validate to be sure, but we shouldnt have
            ## been executed if this wasnt true.
            case ${3} in
                ?([+-])+([0-9]|['.']))
                    ## yup its numbers. lets make sure we can ping it - if its alive then we can use it.
                    ## if not, we throw a warning - it IS possible that the record is being added prior
                    ## to the IP being put in place (although this is highly unlikely)
                    ## we cant ping from the bastions. we have to run against
                    ## one of the proxies
                    ping ${3} > /dev/null 2>&1;
                    PING_RCODE=${?}

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                    if [ ${PING_RCODE} -eq 0 ]
                    then
                        ## IP is up and responsive. all set.
                        RETURN_CODE=0;
                    else
                        ## we didn't get a response back. try externally
                        for EXTERNAL_SERVER in ${EXT_SLAVES[@]}
                        do
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EXTERNAL_SERVER -> ${EXTERNAL_SERVER}";

                            PING_RCODE=$(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${EXTERNAL_SERVER} "ping ${3} > /dev/null 2>&1 && echo ${?}" ${SSH_USER_NAME} ${SSH_USER_AUTH});
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                            if [ -z "${PING_RCODE}" ] || [ ${PING_RCODE} -ne 0 ]
                    		then
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${EXTERNAL_SERVER} not responding to ping. Continuing..";

                                (( ERROR_COUNT += 1 ));

                                continue;
                            fi
                        done

                        [ ${ERROR_COUNT} -ne 0 ] && RETURN_CODE=63 || RETURN_CODE=0;
                    fi
                    ;;
                +([a-z]|[A-Z]|[0-9]|['.']))
                    ## we're doing both a and ns here.. make sure this is an ns
                    if [ "${RR_TYPE}" = "NS" ]
                    then
                        ## okay, ns record. ping validate
                        ping ${3} > /dev/null 2>&1;
                        PING_RCODE=${?}

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                        if [ ${PING_RCODE} -eq 0 ]
                        then
                            ## IP is up and responsive. all set.
                            RETURN_CODE=0;
                        else
                            ## we didn't get a response back. try externally
                            for EXTERNAL_SERVER in ${EXT_SLAVES[@]}
                            do
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "EXTERNAL_SERVER -> ${EXTERNAL_SERVER}";

                                PING_RCODE=$(${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSSHConnection.exp ${EXTERNAL_SERVER} "ping ${3} > /dev/null 2>&1 && echo ${?}" ${SSH_USER_NAME} ${SSH_USER_AUTH});
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                                if [ -z "${PING_RCODE}" ] || [ ${PING_RCODE} -ne 0 ]
                        		then
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${EXTERNAL_SERVER} not responding to ping. Continuing..";

                                    (( ERROR_COUNT += 1 ));

                                    continue;
                                fi
                            done

                            [ ${ERROR_COUNT} -ne 0 ] && RETURN_CODE=63 || RETURN_CODE=0;
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
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                    if [[ -z "${RET_CODE}" || ${RET_CODE} -ne 0 ]]
                    then
                        RETURN_CODE=1;
                    else
                        ## we have a name associated with our IP. we can therefore
                        ## skip the other validation check (reverse lookup) and move into
                        ## ping validation
                        ping ${RESOLVED_NAME} > /dev/null 2>&1;
                        PING_RCODE=${?}

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                        if [ ${PING_RCODE} -eq 0 ]
                        then
                            ## all set.
                            RETURN_CODE=0;
                        else
                            ## ping validation failed. "WARN", but dont fail.
                            RETURN_CODE=63;
                        fi
                    fi
                    ;;
                +([a-z]|[A-Z]|[0-9]|['.']))
                    ## ahh, we were given a name. all we need to do now is make sure its alive, has a valid
                    ## a record and if these two checks pass, then let it on through
                    ## make sure this record has an A record .. and maybe further to that make sure it has a reverse entry
                    for EXTERNAL_SERVER in ${EXT_SLAVES[@]}
                    do
                        ping ${EXTERNAL_SERVER} > /dev/null 2>&1;
                        PING_RCODE=${?}

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                        if [ -z "${PING_RCODE}" ] || [ ${PING_RCODE} -ne 0 ]
                		then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${EXTERNAL_SERVER} not responding to ping. Continuing..";

                            (( ERROR_COUNT += 1 ));

                            continue;
                        fi

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command on ${EXTERNAL_SERVER}..";

                        ## unset methodname and cname
                        local THIS_CNAME="${CNAME}";
                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                        ## validate the input
                        ${PLUGIN_ROOT_DIR}/lib/runQuery.sh -s ${EXTERNAL_SERVER} -t a -u ${2} -o -e;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        local METHOD_NAME="${THIS_CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        [ ! -z "${RET_CODE}" ] && [ ${RET_CODE} -eq 0 ] && RESOLVED_NAME=$(cat ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE});
                    done

                    if [ ! -z "${RESOLVED_NAME}" ]
                    then
                        ## we have a name associated with our IP. we can therefore
                        ## skip the other validation check (reverse lookup) and move into
                        ## ping validation
                        ping ${RESOLVED_NAME} > /dev/null 2>&1;
                        PING_RCODE=${?}

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                        if [ ${PING_RCODE} -eq 0 ]
                        then
                            ## all set.
                            RETURN_CODE=0;
                        else
                            ## ping validation failed. "WARN", but dont fail.
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
            SOURCE_FILE=${PLUGIN_ROOT_DIR}/${TMP_DIRECTORY}/${GROUP_ID}${BIZ_UNIT}/${PRIMARY_DC}/${NAMED_ZONE_PREFIX}.$(echo ${SITE_HOSTNAME} | cut -d "." -f 1);

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SOURCE_FILE -> ${SOURCE_FILE}";

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
                    for EXTERNAL_SERVER in ${EXT_SLAVES[@]}
                    do
                        ping ${EXTERNAL_SERVER} > /dev/null 2>&1;
                        PING_RCODE=${?}

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

                        if [ -z "${PING_RCODE}" ] || [ ${PING_RCODE} -ne 0 ]
                		then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${EXTERNAL_SERVER} not responding to ping. Continuing..";

                            (( ERROR_COUNT += 1 ));

                            continue;
                        fi

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command on ${EXTERNAL_SERVER}..";

                        ## unset methodname and cname
                        local THIS_CNAME="${CNAME}";
                        unset METHOD_NAME;
                        unset CNAME;

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                        ## validate the input
                        ${PLUGIN_ROOT_DIR}/lib/runQuery.sh -s ${EXTERNAL_SERVER} -t a -u ${2} -o -e;
                        typeset -i RET_CODE=${?};

                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                        [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                        CNAME="${THIS_CNAME}";
                        local METHOD_NAME="${THIS_CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        [ ! -z "${RET_CODE}" ] && [ ${RET_CODE} -eq 0 ] && RESOLVED_NAME=$(cat ${PLUGIN_ROOT_DIR}/${DIG_DATA_FILE});
                    done

                    if [ ! -z "${RESOLVED_NAME}" ]
                    then
                        ## we found a record, so we'll allow it
                        RETURN_CODE=0;
                    else
                        ## nothing. "WARN", but not fail
                        RETURN_CODE=63;
                    fi
                fi
            else
                ## we don't have a source file to review. can't really continue here.
                RETURN_CODE=1;
            fi
            ;;
    esac

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset PING_RCODE;
    unset RR_TYPE;
    unset SOURCE_FILE;
    unset RESOLVED_NAME;
    unset PING_RESPONSE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  validateProtocol
#   DESCRIPTION:  Validate service record protocol data
#    PARAMETERS:  None
#==============================================================================
function validateServiceProtocol
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    [[ -z "${ALLOWED_PROTOCOLS}" || ! -f ${ALLOWED_PROTOCOLS} ]] && RETURN_CODE=0;

    egrep -v "^$|^#" ${ALLOWED_PROTOCOLS} | while read -r ALLOWED_PROTOCOL
    do
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ALLOWED_PROTOCOL -> ${ALLOWED_PROTOCOL}";

        if [ "${2}" = "${ALLOWED_PROTOCOL}" ]
        then
            RETURN_CODE=0;

            break;
        fi

        RETURN_CODE=1;
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset ALLOWED_PROTOCOL;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  validateType
#   DESCRIPTION:  Validate service record protocol type
#    PARAMETERS:  None
#==============================================================================
function validateServiceType
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    [[ -z "${ALLOWED_SERVICE_TYPES}" || ! -f ${ALLOWED_SERVICE_TYPES} ]] && RETURN_CODE=0;

    egrep -v "^$|^#" ${ALLOWED_SERVICE_TYPES} | while read -r ALLOWED_TYPE
    do
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ALLOWED_TYPE -> ${ALLOWED_TYPE}";

        if [ "${2}" = "${ALLOWED_TYPE}" ]
        then
            RETURN_CODE=0;

            break;
        fi

        RETURN_CODE=1;
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset ALLOWED_TYPE;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

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
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    print "${CNAME} - Validate data provided for a SRV record.";
    print "Usage:  ${CNAME} validate-type validate-data";
    print "         validate-type can be one of: protocol or type";
    print "         validate-data must be the data to perform validation against";

    return 3;
}

[ ${#} -eq 0 ] && usage;
METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

## make sure we have args
[ "${1}" = "address" ] && validateIPAddress ${@};
[ "${1}" = "type" ] && validateRecordType ${@};
[ "${1}" = "target" ] && validateRecordTarget ${@};
[ "${1}" = "srvtype" ] && validateServiceType ${@};
[ "${1}" = "srvproto" ] && validateServiceProtocol ${@};

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset OPTIND;
unset THIS_CNAME;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return ${RETURN_CODE};
