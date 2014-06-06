#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  validateSecurityAccess.sh
#         USAGE:  ./validateSecurityAccess.sh (create|renew)
#   DESCRIPTION:  Generates a random character file for use with certutil and
#                 other utilities requiring pseudo-entropy. This is meant to run
#                 as a cron job, rotating out the existing entropy file every so
#                 often.
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
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;

METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

[[ -z "${ENFORCE_SECURITY}" || "${ENFORCE_SECURITY}" = "${_FALSE}" ]] && return 0;

#===  FUNCTION  ===============================================================
#          NAME:  validateAccountAccess
#   DESCRIPTION:  Provide information on the usage of this application
#    PARAMETERS:  None
#       RETURNS:  0
#===========================================================================
function validateAccountAccess
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    if [[ ! -z "${#AUTHORIZED_USERS[@]}" && ${#AUTHORIZED_USERS[@]} -ne 0 ]]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "We have a list of authorized users. Validate..";

        for USER in ${AUTHORIZED_USERS[@]}
        do
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "USER -> ${USER}";

            if [ $(echo ${AUTHORIZED_USERS[@]} | grep -c ${USER}) -eq 1 ]
            then
                ## we only need to match one, its unlikely that all users will be in all groups

                USER_RETURN=0;

                break;
            fi

            USER_RETURN=1;
        done

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "USER_RETURN -> ${USER_RETURN}";
    fi

    if [[ ! -z "${#AUTHORIZED_GROUPS[@]}" && ${#AUTHORIZED_GROUPS[@]} -ne 0 ]]
    then
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "We have a list of authorized groups. Validate..";

        for GROUP in ${AUTHORIZED_GROUPS[@]}
        do
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "GROUP -> ${GROUP}";

            if [ $(echo ${AUTHORIZED_GROUPS[@]} | grep -c ${GROUP}) -eq 1 ]
            then
                ## we only need to match one, its unlikely that all users will be in all groups

                GROUP_RETURN=0;

                break;
            fi

            GROUP_RETURN=1;
        done

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "GROUP_RETURN -> ${GROUP_RETURN}";
    fi

    [[ -z "${GROUP_RETURN}" || ${GROUP_RETURN} -ne 0 ]] && RETURN_CODE=1 || RETURN_CODE=0;
    [[ -z "${USER_RETURN}" || ${USER_RETURN} -ne 0 ]] && RETURN_CODE=1 || RETURN_CODE=0;

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  validateSystemAccess
#   DESCRIPTION:  Provide information on the usage of this application
#    PARAMETERS:  None
#       RETURNS:  0
#===========================================================================
function validateSystemAccess
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";

    set -A AUTHORIZED_HOSTS ${1};

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "AUTHORIZED_HOSTS -> ${AUTHORIZED_HOSTS}";

    for HOST in ${AUTHORIZED_HOSTS[@]}
    do
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HOST -> ${HOST}";

        if [ "${HOST}" = "${SYSTEM_HOSTNAME}" ]
        then
            RETURN_CODE=0;

            break;
        elif [ $(echo ${REAL_ALIAS_NAMES} | grep -c ${SYSTEM_HOSTNAME}) -eq 1 ]
        then
            RETURN_CODE=0;

            break;
        fi

        RETURN_CODE=1;
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return ${RETURN_CODE};
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#   RETURNS:  0
#==============================================================================
function usage
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Security request validation";
    print "Usage: ${CNAME} [ -a ] [ -s ] [ -?|-h show this help ]";
    print "  -a      Perform authorized account security validation";
    print "  -s      Perform authorized system validation";
    print "  -h|-?   Show this help";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

while getopts ":as:" OPTIONS
do
    case "${OPTIONS}" in
        a)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            ## Capture the site root
            validateAccountAccess;
            ;;
        s)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting HOST_LIST..";

            ## Capture the filename to work on
            set -A HOST_LIST ${OPTARG};

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "HOST_LIST -> ${HOST_LIST}";

            if [ -z "${HOST_LIST[@]}" ]
            then
                usage;
            fi

            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            validateSystemAccess;
            ;;
        *)
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done

shift ${OPTIND}-1;

return ${RETURN_CODE};

