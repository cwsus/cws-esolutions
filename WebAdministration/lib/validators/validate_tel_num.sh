#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  validate_tel_numess.sh
#         USAGE:  ./add_mx_ui_helper.sh
#   DESCRIPTION:  Helper interface for add_record_ui. Pluggable, can be modified
#     or copied for all allowed record types.
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
PLUGIN_NAME="webadmin";
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

function validate_tel_num
{
    [[ ! -z "${TRACE}" && "${TRACE}" == "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    if [ ! -z "${1}" ] && [ $(echo ${1} | tr -dc "." | wc -c) -eq 2 ]
    then
        if [ $(echo ${1} | tr -dc "." | wc -c) -eq 2 ]
        then
            TEL_AREA_CODE=$(echo ${1} | cut -d "." -f 1);
            TEL_PREFIX=$(echo ${1} | cut -d "." -f 2);
            TEL_TARGET=$(echo ${1} | cut -d "." -f 3);
        elif [ $(echo ${1} | tr -dc "-" | wc -c) -eq 2 ]
        then
            TEL_AREA_CODE=$(echo ${1} | cut -d "-" -f 1);
            TEL_PREFIX=$(echo ${1} | cut -d "-" -f 2);
            TEL_TARGET=$(echo ${1} | cut -d "-" -f 3);
        else
            RET_CODE=22;
        fi

        if [ -z "${RET_CODE}" ]
        then
            case ${TEL_AREA_CODE} in
                [0-9][0-9][0-9])
                    TEL_AREA_CODE_VALID=true;
                    ;;
                *)
                    TEL_AREA_CODE_VALID=false;
                    ;;
            esac

            case ${TEL_PREFIX} in
                [0-9][0-9][0-9])
                    TEL_PREFIX_VALID=true;
                    ;;
                *)
                    TEL_PREFIX_VALID=false;
                    ;;
            esac

            case ${TEL_TARGET} in
                [0-9][0-9][0-9][0-9])
                    TEL_TARGET_VALID=true;
                    ;;
                *)
                    TEL_TARGET_VALID=false;
                    ;;
            esac

            if [ ! -z "${TEL_AREA_CODE_VALID}" ] && [ ! -z "${TEL_PREFIX_VALID}" ] && [ ! -z "${TEL_TARGET_VALID}" ]
            then
                if [ ${TEL_AREA_CODE_VALID} ] && [ ${TEL_PREFIX_VALID} ] && [ ${TEL_TARGET_VALID} ]
                then
                    RET_CODE=0;
                else
                    RET_CODE=22;
                fi
            else
                RET_CODE=22;
            fi
        fi
    else
        RET_CODE=22;
    fi
}

METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} DEBUG "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

validate_tel_num ${1};

echo ${RET_CODE};
return ${RET_CODE};

