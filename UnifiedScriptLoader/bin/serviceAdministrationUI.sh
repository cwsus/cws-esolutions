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

## clear any existing variables
env -i;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

## Application constants
CNAME="${THIS_CNAME}";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; printf "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;

[[ -z "${APP_ROOT}" && -f ${SCRIPT_ROOT}/../lib/constants.sh ]] && . ${SCRIPT_ROOT}/../lib/constants.sh;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

[ -z "${APP_ROOT}" ] && print "Failed to locate configuration data. Cannot continue." && exit 1;

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

trap "print '$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F  "=" '/\<system.trap.signals\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

#===  FUNCTION  ===============================================================
#         NAME:  main
#  DESCRIPTION:  Main entry point for application.
#   PARAMETERS:  None
#      RETURNS:  0
#==============================================================================
function main
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";
    local RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    while true
    do
        reset; clear;

        print "\n";
        print "\t\t+-------------------------------------------------------------------+";
        print "\t\t               WELCOME TO \E[0;31m $(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F  "=" '/\<system.application.title\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g') \033[0m";
        print "\t\t+-------------------------------------------------------------------+";
        print "\t\tSystem Type         : \E[0;36m ${SYSTEM_HOSTNAME} \033[0m";
        print "\t\tSystem Uptime       : \E[0;36m ${SYSTEM_UPTIME} \033[0m";
        print "\t\tUser                : \E[0;36m ${IUSER_AUDIT} \033[0m";
        print "\t\t+-------------------------------------------------------------------+";
        print "";
        print "\t\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F  "=" '/\<system.available.options\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        set -A PLUGIN_LIST $(ls -ltr ${PLUGIN_DIR} | egrep "^l|^d" | awk '{print $9}' | cut -d "/" -f 1);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLUGIN_LIST -> ${PLUGIN_LIST[@]}";

        for LOADABLE_PLUGIN in ${PLUGIN_LIST[@]}
        do
            A=0; ## make sure A is zero
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "LOADABLE_PLUGIN -> ${LOADABLE_PLUGIN}";

            [ ! -s ${PLUGIN_DIR}/${LOADABLE_PLUGIN}/${ETC_DIRECTORY}/ui.properties ] && continue;

            egrep -v "^$|^#" ${PLUGIN_DIR}/${LOADABLE_PLUGIN}/${ETC_DIRECTORY}/ui.properties | while read -r ENTRY
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTRY -> ${ENTRY}";

                print "\t${LOADABLE_PLUGIN}/${A} - $(printf ${ENTRY} | cut -d "=" -f 1 | sed -e '/^ *#/d;s/#.*//' | cut -d "|" -f 2)";

                (( A += 1 ));
            done
        done

        print "\t$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F  "=" '/\<system.option.cancel\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')\n";

        read ANSWER;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ANSWER -> ${ANSWER}";

        local PLUGIN_RESPONSE=${ANSWER%/*};
        local ENTRY_RESPONSE=${ANSWER##*/};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLUGIN_RESPONSE -> ${PLUGIN_RESPONSE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ENTRY_RESPONSE -> ${ENTRY_RESPONSE}";

        case ${PLUGIN_RESPONSE} in
            [Xx]|[Qq]|[Cc])
                ## user chose to quit, close us out
                print "$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F  "=" '/\<system.terminate.message\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                local TERMINATE_APPLICATION=${_TRUE};

                reset; clear; break;
                ;;
            *)
                if [ $(printf ${PLUGIN_LIST[@]} | grep -c ${PLUGIN_RESPONSE}) -eq 0 ]
                then
                    unset PLUGIN_RESPONSE;
                    unset ENTRY_RESPONSE;
                    unset CMD_ENTRY;
                    unset RET_CODE;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F  "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                local CMD_ENTRY=$(sed -e '/^ *#/d;s/#.*//' ${PLUGIN_DIR}/${PLUGIN_RESPONSE}/${ETC_DIRECTORY}/ui.properties | awk -F  "=" "/${ENTRY_RESPONSE}/{print \$2}" | sed -e 's/^ *//g;s/ *$//g');

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CMD_ENTRY -> ${CMD_ENTRY}";

                if [[ -z "${CMD_ENTRY}" || "${CMD_ENTRY}" = "" ]]
                then
                    unset PLUGIN_RESPONSE;
                    unset ENTRY_RESPONSE;
                    unset CMD_ENTRY;
                    unset RET_CODE;

                    print "$(sed -e '/^ *#/d;s/#.*//' ${ERROR_MESSAGES} | awk -F  "=" '/\<selection.invalid\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g')";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Starting ${CMD_ENTRY} ..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                exec ${PLUGIN_DIR}/${PLUGIN_RESPONSE}/${BIN_DIRECTORY}/${CMD_ENTRY};

                exit 0;
                ;;
        esac

        [[ ! -z "${TERMINATE_APPLICATION}" && "${TERMINATE_APPLICATION}" = "${_TRUE}" ]] && break;
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    unset PLUGIN_LIST;
    unset LOADABLE_PLUGIN;
    unset ANSWER;
    unset PLUGIN_RESPONSE;
    unset ENTRY_RESPONSE;
    unset TERMINATE_APPLICATION;
    unset CMD_ENTRY;

    ## clear variables
    env -i;

    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 0;
}

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

main;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset THIS_CNAME;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return ${RETURN_CODE};
