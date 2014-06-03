#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  serviceAdministrationUI.sh
#         USAGE:  ./serviceAdministrationUI.sh
#   DESCRIPTION:  
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
#
#==============================================================================

[[ ! -z "${TRACE}" && "${TRACE}" = "TRUE" ]] && set -x;

## Application constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[[ -z "${APP_ROOT}" && ! -s ${SCRIPT_ROOT}/../lib/constants.sh ]] && echo "Failed to locate configuration data. Cannot continue." && exit 1;

[ -z "${APP_ROOT}" ] && . ${SCRIPT_ROOT}/../lib/constants.sh;

[[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;

METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

trap "print '$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F  "=" '/system.trap.signals/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

#===  FUNCTION  ===============================================================
#
#         NAME:  main
#  DESCRIPTION:  Main entry point for application.
#   PARAMETERS:  None
#      RETURNS:  0
#
#==============================================================================
function main
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    while true
    do
        reset; clear;

        print "\n";
        print "\t\t+-------------------------------------------------------------------+";
        print "\t\t               WELCOME TO \E[0;31m $(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F  "=" '/system.application.title/{print $2}' | sed -e 's/^ *//g;s/ *$//g') \033[0m";
        print "\t\t+-------------------------------------------------------------------+";
        print "";
        print "\t\tSystem Type         : \E[0;36m ${SYSTEM_HOSTNAME} \033[0m";
        print "\t\tSystem Uptime       : \E[0;36m ${SYSTEM_UPTIME} \033[0m";
        print "\t\tUser                : \E[0;36m ${IUSER_AUDIT} \033[0m";
        print "";
        print "\t\t+-------------------------------------------------------------------+";
        print "";
        print "\t\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F  "=" '/system.available.options/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        set -A PLUGIN_LIST $(ls -ltr ${APP_ROOT}/${PLUGIN_DIR} | egrep "^l|^d" | awk '{print $9}' | cut -d "/" -f 1);

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLUGIN_LIST -> ${PLUGIN_LIST[@]}";

        for LOADABLE_PLUGIN in ${PLUGIN_LIST[@]}
        do
            A=0; ## make sure A is zero
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LOADABLE_PLUGIN -> ${LOADABLE_PLUGIN}";

            [ ! -s ${APP_ROOT}/${PLUGIN_DIR}/${LOADABLE_PLUGIN}/${ETC_DIRECTORY}/ui.properties ] && continue;

            egrep -v "^$|^#" ${APP_ROOT}/${PLUGIN_DIR}/${LOADABLE_PLUGIN}/${ETC_DIRECTORY}/ui.properties | while read -r ENTRY
            do
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTRY -> ${ENTRY}";

                print "\t${LOADABLE_PLUGIN}/${A} - $(echo ${ENTRY} | cut -d "=" -f 1 | sed -e '/^ *#/d;s/#.*//' | cut -d "|" -f 2)";

                (( A += 1 ));
            done
        done

        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F  "=" '/system.option.cancel/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read ANSWER;

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        PLUGIN_RESPONSE=${ANSWER%/*};
        ENTRY_RESPONSE=${ANSWER##*/};

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLUGIN_RESPONSE -> ${PLUGIN_RESPONSE}";
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTRY_RESPONSE -> ${ENTRY_RESPONSE}";

        case ${PLUGIN_RESPONSE} in
            [Xx]|[Qq]|[Cc])
                ## user chose to quit, close us out
                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F  "=" '/system.terminate.message/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                TERMINATE_APPLICATION=${_TRUE};

                reset; clear; break;
                ;;
            *)
                if [ $(echo ${PLUGIN_LIST[@]} | grep -c ${PLUGIN_RESPONSE}) -eq 0 ]
                then
                    unset PLUGIN_RESPONSE;
                    unset ENTRY_RESPONSE;
                    unset CMD_ENTRY;
                    unset RET_CODE;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F  "=" '/selection.invalid/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                CMD_ENTRY=$(sed -e '/^ *#/d;s/#.*//' ${APP_ROOT}/${PLUGIN_DIR}/${PLUGIN_RESPONSE}/${ETC_DIRECTORY}/ui.properties | awk -F  "=" "/${ENTRY_RESPONSE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g');

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CMD_ENTRY -> ${CMD_ENTRY}";

                if [[ -z "${CMD_ENTRY}" || "${CMD_ENTRY}" = "" ]]
                then
                    unset PLUGIN_RESPONSE;
                    unset ENTRY_RESPONSE;
                    unset CMD_ENTRY;
                    unset RET_CODE;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F  "=" '/selection.invalid/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Starting ${CMD_ENTRY} ..";
                [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                exec ${APP_ROOT}/${PLUGIN_DIR}/${PLUGIN_RESPONSE}/${BIN_DIRECTORY}/${CMD_ENTRY};

                exit 0;
                ;;
        esac

        [[ ! -z "${TERMINATE_APPLICATION}" && "${TERMINATE_APPLICATION}" = "${_TRUE}" ]] && break;
    done

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    ## clear variables
    env -i;

    return 0;
}

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

main;

return 0;
