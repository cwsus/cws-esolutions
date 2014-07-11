#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  certRenewalUI.sh.sh
#         USAGE:  ./certRenewalUI.sh.sh
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
CNAME="${THIS_CNAME}";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#          NAME:  updateCertificate
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function updateCertificate
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    typeset METHOD_NAME="${CNAME}#${0}";
    typeset RETURN_CODE=0;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting subdomain CNAME record information..";

    trap "echo '$(grep system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

    while true
    do
        if [ ! -z "${PREIMP_COMPLETE}" ] && [ "${PREIMP_COMPLETE}" = "${_TRUE}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

            break;
        fi

        reset; clear;

        unset MGMT_OP;
        unset RET_CODE;
        unset RETURN_CODE;

        ## certificate received, apply to typeset keystores
        ## call out to run_renewal
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing information confirmed. Continuing..";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting certificate..";

        while true
        do
            if [ ! -z "${PREIMP_COMPLETE}" ] && [ "${PREIMP_COMPLETE}" = "${_TRUE}" ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

                break;
            fi

            echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            echo "\t$(grep -w cert.mgmt.provide.certificate "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";

            sleep "${MESSAGE_DELAY}"; reset; clear;

            ## all we do here is execute vi to get the certificate. thats it.
            ## name it after the certdb var, we'll handle the rename in run_renewal
            vi ${APP_ROOT}/${CERTSTORE}/${CERTDB}.cer;

            ## make sure file got created..
            if [ -s ${APP_ROOT}/${CERTSTORE}/${CERTDB}.cer ]
            then
                ## make sure its actually a certificate
                if [ ! -z "$(grep "BEGIN CERTIFICATE" ${APP_ROOT}/${CERTSTORE}/${CERTDB}.cer)" ] \
                    && [ ! -z "$(grep "END CERTIFICATE" ${APP_ROOT}/${CERTSTORE}/${CERTDB}.cer)" ]
                then
                    ## looks good, pop it off
                    reset; clear; break;
                else
                    ## no cert here.....
                    rm ${APP_ROOT}/${CERTSTORE}/${CERTDB}.cer > /dev/null 2>&1;

                    echo "\t$(grep -w no.cert.data.found "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                fi
            else
                ## no cert file generated
                rm ${APP_ROOT}/${CERTSTORE}/${CERTDB}.cer > /dev/null 2>&1;

                echo "\t$(grep -w no.cert.data.found "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
            fi
        done

        echo "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing runCertRenewal.sh -d ${CERTDB} -s ${SITE_HOSTNAME} -w ${WEBSERVER_PLATFORM} -e..";

        ## tmp unset
        unset CNAME;
        unset METHOD_NAME;
        CURR_OPTIND=${OPTIND};

        . ${APP_ROOT}/${LIB_DIRECTORY}/runCertRenewal.sh -d ${CERTDB} -s ${SITE_HOSTNAME} -w ${WEBSERVER_PLATFORM} -e;
        RET_CODE=${?}

        OPTIND=${CURR_OPTIND};
        CNAME=$(basename ${0});
    typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

        if [ ${RET_CODE} -eq 0 ] || [ ${RET_CODE} -eq 97 ]
        then
            ## get the change number and send the owner notify
            while true
            do
                if [ ! -z "${PREIMP_COMPLETE}" ] && [ "${PREIMP_COMPLETE}" = "${_TRUE}" ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

                    break;
                fi

                reset; clear;

                if [ ${RET_CODE} -eq 97 ]
                then
                    unset RET_CODE;

                    echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    echo "\t$(grep -w pem.mail.generation.failed "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                    sleep "${MESSAGE_DELAY}"; reset; clear;

                    cat ${APP_ROOT}/${MAILSTORE}/PEM-${SITE_HOSTNAME}.message;

                    echo "\n\n\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                    read INPUT;

                    reset; clear;
                fi

                unset INPUT;

                ## if this is a production renewal, send the owner notify
                if [ "${ENVIRONMENT_TYPE}" = "${ENV_TYPE_PRD}" ]
                then
                    echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    echo "\t$(grep -w system.provide.changenum "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                    read CHANGE_NUM;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CHANGE_NUM -> ${CHANGE_NUM}";

                    reset; clear;

                    echo "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                    if [[ ${CHANGE_NUM} == [Ee] ]]
                    then
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change specified as emergency, but this is not an emergency change.";

                        unset CHANGE_NUM;

                        echo "$(grep -w change.control.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi

                    ## validate the CR number
                    if [ $(${APP_ROOT}/${LIB_DIRECTORY}/validators/validate_change_ticket.sh ${CHANGE_NUM}) -ne 0 ]
                    then
                        ## change control provided was invalid
                        ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A change was attempted with an invalid change order by ${IUSER_AUDIT}. Change request was ${CHANGE_NUM}.";
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid change control was provided. A valid change control number is required to process the request.";

                        unset CHANGE_NUM;

                        echo "$(grep -w change.control.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    else
                        while true
                        do
                            if [ ! -z "${PREIMP_COMPLETE}" ] && [ "${PREIMP_COMPLETE}" = "${_TRUE}" ]
                            then
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

                                break;
                            fi

                            ## valid cr number. get the expected process date
                            reset; clear;

                            echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            echo "\t$(grep -w cert.mgmt.provide.process.date "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                            read PROCESS_DATE;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_DATE -> ${PROCESS_DATE}";

                            reset; clear;

                            if [ ! -z "${PROCESS_DATE}" ]
                            then
                                ## validate it
                                returnEpochTime ${PROCESS_DATE} > /dev/null 2>&1;
                                typeset -i RET_CODE=${?};

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                if [ ${RET_CODE} -eq 0 ]
                                then
                                    ## all good
                                    unset RET_CODE;

                                    ## re-define process date
                                    PROCESS_DATE=$(echo ${PROCESS_DATE} | awk '{print $2, $3, $1}');

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PROCESS_DATE -> ${PROCESS_DATE}";

                                    ## send the owner notify
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Sending owner notification..";

                                    unset METHOD_NAME;
                                    unset CNAME;

                                    . ${MAILER_CLASS} -m ${NOTIFY_OWNER_EMAIL} -p ${WEB_PROJECT_CODE} -a "${OWNER_DIST}" -t ${NOTIFY_TYPE_NOTIFY} -e;
                                    typeset -i RET_CODE=${?};

                                    CNAME=$(basename ${0});
                                typeset METHOD_NAME="${CNAME}#${0}";
typeset RETURN_CODE=0;

                                    PREIMP_COMPLETE=${_TRUE};

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PREIMP_COMPLETE -> ${PREIMP_COMPLETE}";

                                    if [ ${RET_CODE} -ne 0 ]
                                    then
                                        ## owner notify failed
                                        reset; clear;

                                        echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        echo "\t$(grep -w owner.mail.generation.failed "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        sleep "${MESSAGE_DELAY}"; reset; clear;

                                        cat ${APP_ROOT}/${MAILSTORE}/OWNER-${SITE_HOSTNAME}.message;

                                        echo "\n\n\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        read INPUT;

                                        reset; clear; break;
                                    else
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Owner notification sent. Continuing..";

                                        echo "\t$(grep -w cert.mgmt.owner.notification.sent "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                        sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                    fi
                                else
                                    unset PROCESS_DATE;

                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                                    echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                fi
                            else
                                unset PROCESS_DATE;

                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A valid response was not provided. Cannot continue.";

                                echo "$(grep -w selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                                sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                            fi
                        done
                    fi
                else
                    PREIMP_COMPLETE=${_TRUE};

                    echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    echo "\t$(grep -w cert.mgmt.cert.implemented "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";
                    echo "\n\n\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                    read INPUT;

                    reset; clear; break;
                fi
            done
        else
            ## an "ERROR" occurred, we can start over
            PREIMP_COMPLETE=${_TRUE};

            echo "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            echo "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            echo "\t$(grep ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            echo "\t$(grep -w cert.mgmt.cert.application.failed "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";
            echo "\n\n\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

            read INPUT;

            reset; clear; break;
        fi
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

[ -z "${PLUGIN_ROOT_DIR}" ] && [ -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin ] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

updateCertificate;

