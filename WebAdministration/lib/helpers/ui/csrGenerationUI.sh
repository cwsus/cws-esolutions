#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  csrGenerationUI.sh.sh
#         USAGE:  ./csrGenerationUI.sh.sh
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

## Application constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#          NAME:  generateCSR
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function generateCSR
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting subdomain CNAME record information..";

    trap "print '$(grep system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

    while true
    do
        if [ ! -z "${CSR_COMPLETE}" ] && [ "${CSR_COMPLETE}" = "${_TRUE}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

            break;
        fi

        unset MGMT_OP;
        unset RET_CODE;
        unset RETURN_CODE;

        ## generate a csr
        ## request the user's contact phone number
        while true
        do
            if [ ! -z "${CSR_COMPLETE}" ] && [ "${CSR_COMPLETE}" = "${_TRUE}" ]
            then
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

                break;
            fi

            reset; clear;

            print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            print "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
            print "\t$(grep -w cert.mgmt.provide.contact.number "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

            read CONTACT_NUMBER;
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CONTACT_NUMBER -> ${CONTACT_NUMBER}";

            reset; clear;

            print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

            ## validate it
            case ${CONTACT_NUMBER} in
                [Xx]|[Qq]|[Cc])
                    reset; clear;
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request to cancel.";

                    unset CONTACT_NUMBER;

                    print "$(grep -w system.request.canceled "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                    CANCEL_REQ=${_TRUE};

                    sleep "${MESSAGE_DELAY}"; reset; clear; break;
                    ;;
                *)
                    if [ $(${APP_ROOT}/${LIB_DIRECTORY}/validators/validate_tel_num.sh ${CONTACT_NUMBER}) -ne 0 ]
                    then
                        reset; clear;
                        ## number provided was invalid
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An invalid telephone number was provided. A valid telephone number is required to process the request.";

                        unset CONTACT_NUMBER;
                        unset RET_CODE;
                        unset RETURN_CODE;

                        print "$(grep -w contact.number.invalid "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";
                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    else
                        reset; clear;

                        print "$(grep -w system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                        ## call out to run_key_generation
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing information confirmed. Continuing..";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command runKeyGeneration.sh -s ${SITE_HOSTNAME} -v ${MASTER_WEBSERVER} -w ${WEBSERVER_PLATFORM} -p ${SERVER_ROOT} -d ${CERTDB} -c $(echo ${PLATFORM_CODE} | awk '{print $1}') -t ${CONTACT_NUMBER} -e..";

                        ## tmp unset
                        unset CNAME;
                        unset METHOD_NAME;

                        . ${APP_ROOT}/${LIB_DIRECTORY}/runKeyGeneration.sh -s ${SITE_HOSTNAME} -v ${MASTER_WEBSERVER} \
                            -w ${WEBSERVER_PLATFORM} -p ${SERVER_ROOT} -d ${CERTDB} -c $(echo ${PLATFORM_CODE} | awk '{print $1}') \
                            -t ${CONTACT_NUMBER} -e;
                        RET_CODE=${?}

                        CNAME=$(basename ${0});
                        local METHOD_NAME="${CNAME}#${0}";
                        CSR_COMPLETE=${_TRUE};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CSR_COMPLETE -> ${CSR_COMPLETE}";

                        reset; clear;

                        if [ ! -z "${RET_CODE}" ]
                        then
                            if [ ${RET_CODE} -eq 0 ]
                            then
                                print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                print "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                print "\t$(grep -w cert.mgmt.csr.generated "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")";
                                print "\n\n\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                read INPUT;

                                reset; clear; break;
                            elif [ ${RET_CODE} -eq 95 ]
                            then
                                ## ask if we want to do another, if yes, clear
                                ## and send back to the beginning, otherwise send
                                ## back to main
                                print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                print "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                print "\t$(grep -w csr.mail.generation.failed "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                sleep "${MESSAGE_DELAY}"; reset; clear;

                                cat ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr;

                                print "\n\n\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                                read INPUT;
                                reset; clear; break;
                            fi
                        else
                            print "\t\t\t$(grep -w system.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            print "\t\t\t$(grep -w certmgmt.application.title "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            print "\t$(grep -w ${RET_CODE} "${PLUGIN_ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            print "\t$(grep -w cert.mgmt.csr.generation.failed "${PLUGIN_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SITE_HOSTNAME%/${SITE_HOSTNAME}/")\n";
                            print "\n\n\t$(grep -w system.continue.enter "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                            read INPUT;

                            reset; clear; break;
                        fi
                    fi
                    ;;
            esac
        done
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";


    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

    return 0;
}

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin.sh ]] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

generateCSR;


[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return 0;