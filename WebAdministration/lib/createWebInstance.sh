#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  create_zone.sh
#         USAGE:  ./create_zone.sh
#   DESCRIPTION:  Creates a skeleton zone file and directory structure
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

## Application constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#          NAME:  create_skeleton_zone
#   DESCRIPTION:  Creates the necessary group folder, domain folders and creates
#                 skeleton zone files. Skeletons are then updated with the
#                 provided zone name.
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function createSecuredInstance
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    [ -z "${1}" ] && HYBRID="${_TRUE}";

    SERVER_ID=${IPLANET_CERT_STORE_PREFIX}$(echo ${SITE_HOSTNAME} | cut -d "." -f -2)_${PROJECT_CODE};
    SERVER_ROOT=$(
        PLATFORM_TYPE_IDENTIFIER=$(echo ${PLATFORM_CODE} | cut -d "_" -f 3 | tr '[A-Z]' '[a-z]');

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLATFORM_TYPE_IDENTIFIER -> ${PLATFORM_TYPE_IDENTIFIER}";

        case ${PLATFORM_TYPE_IDENTIFIER} in
            ${ENV_TYPE_IST})
                echo "${IPLANET_IST_ROOT}";
                ;;
            ${ENV_TYPE_QA})
                echo "${IPLANET_QA_ROOT}";
                ;;
            ${ENV_TYPE_STG}|${ENV_TYPE_TRN}|${ENV_TYPE_PRD})
                echo "${IPLANET_SERVER_ROOT}";
                ;;
            *)
                echo "${_FALSE}";
                ;;
        esac
    );
    WEB_LOG_ROOT=${IPLANET_BASE_LOG_ROOT}/${SERVER_ID};
    WEB_DOC_ROOT=$(echo ${IPLANET_BASE_DOC_ROOT} | sed -e "s/%PROJECT_CODE%/${PROJECT_CODE}/");
    CERT_NICKNAME=$(echo ${SERVER_ID} | cut -d "-" -f 2);
    WEB_TMP_DIR=${IPLANET_WEB_TMPDIR}/${SERVER_ID}-$(returnRandomCharacters 6);

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_ROOT -> ${SERVER_ROOT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_ID -> ${SERVER_ID}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_LOG_ROOT -> ${WEB_LOG_ROOT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_DOC_ROOT -> ${WEB_DOC_ROOT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_NICKNAME -> ${CERT_NICKNAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_TMP_DIR -> ${WEB_TMP_DIR}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating working copy of template..";

    if [ -z "${SERVER_ROOT}" ] || [ "${SERVER_ROOT}" = "${_FALSE}" ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The iPlanet installation root could not be determined. Unable to continue.";

        RETURN_CODE=90;
    else
        ## make sure our build directory exists
        [ ! -d ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT} ] && mkdir -p ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT};

        ## ok, first things first. copy the template
        ## make some directories
        [ ! -d ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_ACL_DIR} ] && mkdir ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_ACL_DIR} > /dev/null 2>&1;
        [ ! -d ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR} ] && mkdir ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR} > /dev/null 2>&1;

        ## copy files
        if [ ! -z "${HYBRID}" ] && [ "${HYBRID}" = "${_TRUE}" ]
        then
            cp -R -p ${APP_ROOT}/${IPLANET_BOTH_TEMPLATE} ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID} > /dev/null 2>&1;
        else
            cp -R -p ${APP_ROOT}/${IPLANET_SSL_TEMPLATE} ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID} > /dev/null 2>&1;
        fi

        for SUFFIX in ${IPLANET_CERT_STORE_KEY_SUFFIX} ${IPLANET_CERT_STORE_CERT_SUFFIX}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying certificate databases..";

            cp -p ${APP_ROOT}/${IPLANET_CERTDB_TEMPLATE}${SUFFIX} \
                ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR}/${SERVER_ID}-${IUSER_AUDIT}-${SUFFIX} > /dev/null 2>&1;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copy complete. Validating..";

            if [ ! -s ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR}/${SERVER_ID}-${IUSER_AUDIT}-${SUFFIX} ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred copying ${APP_ROOT}/${IPLANET_CERTDB_TEMPLATE} to ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR}/${SERVER_ID}-${IUSER_AUDIT}-${SUFFIX}";

                (( ERROR_COUNT += 1 ));
            fi
        done

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ERROR_COUNT -> ${ERROR_COUNT}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate databases copied. Copying ACL files..";

        CURR_IFS=${IFS};
        IFS=${MODIFIED_IFS};

        for ACL in ${IPLANET_ACL_NAMES}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying ${ACL} ..";

            sed -e "s^%SERVER_ROOT%^${SERVER_ROOT}^g" ${APP_ROOT}/${IPLANET_ACL_TEMPLATE} \
                > ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_ACL_DIR}/$(echo ${ACL} | sed -e "s^%SERVER_ID%^${SERVER_ID}^");

            if [ ! -s ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_ACL_DIR}/$(echo ${ACL} | sed -e "s^%SERVER_ID%^${SERVER_ID}^") ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred copying ${APP_ROOT}/${IPLANET_ACL_TEMPLATE} to ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_ACL_DIR}/$(echo ${ACL} | sed -e "s^%SERVER_ID%^${SERVER_ID}^")";

                (( ERROR_COUNT += 1 ));
            fi
        done

        IFS=${CURR_IFS};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ACL files copied.";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copy created. Validating..";

        if [ ${ERROR_COUNT} -eq 0 ]
        then
            if [ -d ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID} ]
            then
                ## ok, we've created our directory - lets rock out
                ## we need to replace some things..
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validation complete. Operating..";

                for REPLACEMENT_ITEM in $(grep "&" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG} | cut -d "&" -f 2)
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REPLACEMENT_ITEM - ${REPLACEMENT_ITEM} -> $(eval echo \${${REPLACEMENT_ITEM}})";

                    sed -e "s^&${REPLACEMENT_ITEM}&^$(eval echo \${${REPLACEMENT_ITEM}})^g" \
                        ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG} \
                        > ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG}.tmp;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating...";

                    if [ ! -z "$(grep "$(eval echo \${${REPLACEMENT_ITEM}})" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG}.tmp)" !]
                    then
                        ## ok, move it over now..
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                        mv ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG}.tmp \
                            ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG} > /dev/null 2>&1;

                        ## and ensure..
                        if [ ! -z "$(grep "$(eval echo \${${REPLACEMENT_ITEM}})" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG})" !]
                        then
                            ## good, keep going
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                            continue;
                        else
                            ## ok, its not there. break out - doesnt make sense to continue
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                            (( ERROR_COUNT += 1 ));

                            break;
                        fi
                    else
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                        (( ERROR_COUNT += 1 ));

                        break;
                    fi
                done

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ERROR_COUNT -> ${ERROR_COUNT}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${IPLANET_CORE_CONFIG} built. Validating..";

                if [ ${ERROR_COUNT} -eq 0 ]
                then
                    ## magnus complete. see if this is websphere-enabled..
                    if [ ! -z "${ENABLE_WEBSPHERE}" ] && [ "${ENABLE_WEBSPHERE}" = "${_TRUE}" ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding WebSphere configuration to ${IPLANET_CORE_CONFIG} ..";

                        print "${IPLANET_WAS_FUNCTION}" >> ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG};
                        print "$(echo ${IPLANET_WAS_BOOTSTRAP} | sed -e "s^%SERVER_ROOT%^${SERVER_ROOT}^" \
                            -e "s^%SERVER_ID%^${SERVER_ID}^g" -e "s/%PROJECT_CODE%/${PROJECT_CODE}/")" >> ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding WebSphere configuration to ${IPLANET_WEB_CONFIG} ..";

                        ## and update the obj files..
                        for WEB_CONFIG in $(ls ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/ | grep ${IPLANET_WEB_CONFIG})
                        do
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding WebSphere configuration to ${WEB_CONFIG} ..";

                            sed -e "14a ${IPLANET_WAS_HANDLER}" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${WEB_CONFIG} \
                                > ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${WEB_CONFIG}.tmp;

                            if [ ! -z "$(grep "${IPLANET_WAS_HANDLER}" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${WEB_CONFIG}.tmp)" !]
                            then
                                ## ok, move it over now..
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                                mv ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${WEB_CONFIG}.tmp \
                                    ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${WEB_CONFIG} > /dev/null 2>&1;

                                ## and ensure..
                                if [ ! -z "$(grep "${IPLANET_WAS_HANDLER}" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${WEB_CONFIG})" !]
                                then
                                    ## good, keep going
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                                    continue;
                                else
                                    ## ok, its not there. break out - doesnt make sense to continue
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                                    (( ERROR_COUNT += 1 ));

                                    break;
                                fi
                            else
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                                (( ERROR_COUNT += 1 ));

                                break;
                            fi
                        done

                        unset WEB_CONFIG;
                    fi

                    ## magnus complete, continue with server.xml
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${IPLANET_CORE_CONFIG} validated. Continuing with ${IPLANET_SERVER_CONFIG}...";

                    unset REPLACEMENT_ITEM;

                    for REPLACEMENT_ITEM in $(grep "&" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG} | cut -d "&" -f 2)
                    do
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REPLACEMENT_ITEM - ${REPLACEMENT_ITEM} -> $(eval echo \${${REPLACEMENT_ITEM}})";

                        sed -e "s^&${REPLACEMENT_ITEM}&^$(eval echo \${${REPLACEMENT_ITEM}})^g" \
                            ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG} \
                            > ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG}.tmp;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating...";

                        if [ ! -z "$(grep "$(eval echo \${${REPLACEMENT_ITEM}})" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG}.tmp)" !]
                        then
                            ## ok, move it over now..
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                            mv ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG}.tmp \
                                ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG} > /dev/null 2>&1;

                            ## and ensure..
                            if [ ! -z "$(grep "$(eval echo \${${REPLACEMENT_ITEM}})" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG})" !]
                            then
                                ## good, keep going
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                                continue;
                            else
                                ## ok, its not there. break out - doesnt make sense to continue
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                                (( ERROR_COUNT += 1 ));

                                break;
                            fi
                        else
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                            (( ERROR_COUNT += 1 ));

                            break;
                        fi
                    done

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ERROR_COUNT -> ${ERROR_COUNT}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${IPLANET_SERVER_CONFIG} built. Validating..";

                    if [ ${ERROR_COUNT} -eq 0 ]
                    then
                        unset REPLACEMENT_ITEM;

                        ## and finish off with the scripts
                        for SCRIPT in ${IPLANET_START_SCRIPT} ${IPLANET_STOP_SCRIPT} ${IPLANET_RESTART_SCRIPT} ${IPLANET_ROTATE_SCRIPT} ${IPLANET_RECONFIG_SCRIPT}
                        do
                            for REPLACEMENT_ITEM in $(grep "&" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${SCRIPT} | cut -d "&" -f 2)
                            do
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REPLACEMENT_ITEM - ${REPLACEMENT_ITEM} -> $(eval echo \${${REPLACEMENT_ITEM}})";

                                sed -e "s^&${REPLACEMENT_ITEM}&^$(eval echo \${${REPLACEMENT_ITEM}})^g" \
                                    ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${SCRIPT} \
                                    > ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${SCRIPT}.tmp;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating...";

                                if [ ! -z "$(grep "$(eval echo \${${REPLACEMENT_ITEM}})" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${SCRIPT}.tmp)" !]
                                then
                                    ## ok, move it over now..
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                                    mv ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${SCRIPT}.tmp \
                                        ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${SCRIPT} > /dev/null 2>&1;

                                    ## and ensure..
                                    if [ ! -z "$(grep "$(eval echo \${${REPLACEMENT_ITEM}})" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${SCRIPT})" !]
                                    then
                                        ## good, keep going
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                                        continue;
                                    else
                                        ## ok, its not there. break out - doesnt make sense to continue
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                                        (( ERROR_COUNT += 1 ));

                                        break;
                                    fi
                                else
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                                    (( ERROR_COUNT += 1 ));

                                    break;
                                fi
                            done
                        done

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ERROR_COUNT -> ${ERROR_COUNT}";

                        if [ ${ERROR_COUNT} -eq 0 ]
                        then
                            ## this is an ssl-enabled site. generate a temporary, self-signed certificate, and generate a CSR to send off to security
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generating CSR..";

                            unset METHOD_NAME;
                            unset CNAME;

                            . ${APP_ROOT}/${LIB_DIRECTORY}/runKeyGeneration.sh -s ${SITE_HOSTNAME} -w ${IPLANET_TYPE_IDENTIFIER} \
                                -d ${SERVER_ID}-${IUSER_AUDIT}- -c ${PLATFORM_CODE} -t ${CONTACT_NUMBER} -n -e;
                            typeset -i RET_CODE=${?};

                            CNAME=$(basename ${0});
                            local METHOD_NAME="${CNAME}#${0}";

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                            if [ ! -z "${RET_CODE}" ]
                            then
                                if [ ${RET_CODE} -eq 0 ]
                                then
                                    ## databases created. build the password file
                                    ## create the file if it isnt already there
                                    [ ! -f ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_PASSWORD_FILE} ] && touch \
                                        ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_PASSWORD_FILE};

                                    CURR_IFS=${IFS};
                                    IFS=${REPLACEMENT_IFS};

                                    for TOKEN in ${SECURITY_TOKENS}
                                    do
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding security token $(echo ${TOKEN} | cut -d ":" -f 1) ..";

                                        print "${TOKEN}" >> ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_PASSWORD_FILE};

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Entry added. Validating ..";

                                        if [ $(grep -c "${TOKEN}" \
                                            ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_PASSWORD_FILE}) -eq 0 ]
                                        then
                                            ## "ERROR" occurred adding
                                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred adding $(echo ${TOKEN} | cut -d ":" -f 1) to ${IPLANET_PASSWORD_FILE}";

                                            (( ERROR_COUNT += 1 ));
                                        fi
                                    done

                                    IFS=${CURR_IFS};

                                    if [ ${ERROR_COUNT} -eq 0 ]
                                    then
                                        ## build complete
                                        ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Instance build for ${SITE_HOSTNAME} completed by ${IUSER_AUDIT}.";

                                        RETURN_CODE=0;
                                    else
                                        ## one or more errors occurred
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Password configuration file could not be generated. Cannot continue.";

                                        RETURN_CODE=48;
                                    fi
                                else
                                    ## failed to create the certificate databases
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to generate certificate database information. Cannot continue.";

                                    RETURN_CODE=48;
                                fi
                            else
                                ## return code was blank
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No return code was received from runKeyGeneration. Cannot continue.";

                                RETURN_CODE=48;
                            fi
                        else
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more build operations has failed. ERROR_COUNT -> ${ERROR_COUNT}. Cannot continue.";

                            RETURN_CODE=48;
                        fi
                    else
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more build operations has failed. ERROR_COUNT -> ${ERROR_COUNT}. Cannot continue.";

                        RETURN_CODE=48;
                    fi
                else
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more build operations has failed. ERROR_COUNT -> ${ERROR_COUNT}. Cannot continue.";

                    RETURN_CODE=48;
                fi
            else
                ## no working directory
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to create working copy of template installation. Please try again.";

                RETURN_CODE=47;
            fi
        else
            ## one or more errors were encountered while copying the templates
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to create working copy of template installation. Please try again.";

            RETURN_CODE=48;
        fi
    fi

    ERROR_COUNT=0;
    unset REPLACEMENT_ITEM;
    unset PLATFORM_TYPE_IDENTIFIER;
    unset SERVER_ROOT;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

#===  FUNCTION  ===============================================================
#          NAME:  create_skeleton_zone
#   DESCRIPTION:  Creates the necessary group folder, domain folders and creates
#                 skeleton zone files. Skeletons are then updated with the
#                 provided zone name.
#    PARAMETERS:  Parameters obtained via command-line flags
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function createUnsecuredInstance
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    SERVER_ID=${IPLANET_CERT_STORE_PREFIX}$(echo ${SITE_HOSTNAME} | cut -d "." -f -2)_${PROJECT_CODE};
    SERVER_ROOT=$(
        PLATFORM_TYPE_IDENTIFIER=$(echo ${PLATFORM_CODE} | cut -d "_" -f 3 | tr '[A-Z]' '[a-z]');

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PLATFORM_TYPE_IDENTIFIER -> ${PLATFORM_TYPE_IDENTIFIER}";

        case ${PLATFORM_TYPE_IDENTIFIER} in
            ${ENV_TYPE_IST})
                echo "${IPLANET_IST_ROOT}";
                ;;
            ${ENV_TYPE_QA})
                echo "${IPLANET_QA_ROOT}";
                ;;
            ${ENV_TYPE_STG}|${ENV_TYPE_TRN}|${ENV_TYPE_PRD})
                echo "${IPLANET_SERVER_ROOT}";
                ;;
            *)
                echo "${_FALSE}";
                ;;
        esac
    );
    WEB_LOG_ROOT=${IPLANET_BASE_LOG_ROOT}/${SERVER_ID};
    WEB_DOC_ROOT=$(echo ${IPLANET_BASE_DOC_ROOT} | sed -e "s^%SERVER_ID%^${SERVER_ID}^");
    CERT_NICKNAME=$(echo ${SERVER_ID} | cut -d "-" -f 2);
    WEB_TMP_DIR=${IPLANET_WEB_TMPDIR}/${SERVER_ID}-$(returnRandomCharacters 6);

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_ROOT -> ${SERVER_ROOT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SERVER_ID -> ${SERVER_ID}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_LOG_ROOT -> ${WEB_LOG_ROOT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_DOC_ROOT -> ${WEB_DOC_ROOT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_NICKNAME -> ${CERT_NICKNAME}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WEB_TMP_DIR -> ${WEB_TMP_DIR}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Creating working copy of template..";

    if [ -z "${SERVER_ROOT}" ] || [ "${SERVER_ROOT}" = "${_FALSE}" ]
    then
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "The iPlanet installation root could not be determined. Unable to continue.";

        RETURN_CODE=90;
    else
        ## ok, first things first. copy the template
        ## make some directories
        mkdir ${APP_ROOT}/${BUILD_TMP_DIR}/${IPLANET_ACL_DIR};

        ## copy files
        cp -R -p ${APP_ROOT}/${IPLANET_NOSSL_TEMPLATE} ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID} > /dev/null 2>&1;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying ACL files..";

        CURR_IFS=${IFS};
        IFS=${MODIFIED_IFS};

        for ACL in ${IPLANET_ACL_NAMES}
        do
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copying ${ACL} ..";

            sed -e "s^%SERVER_ROOT%^${SERVER_ROOT}^g" ${APP_ROOT}/${IPLANET_ACL_TEMPLATE} \
                > ${APP_ROOT}/${BUILD_TMP_DIR}/${IPLANET_ACL_DIR}$(echo ${ACL} | sed -e "s^%SERVER_ID%^${SERVER_ID}^");

            if [ ! -s ${APP_ROOT}/${BUILD_TMP_DIR}/${IPLANET_ACL_DIR}$(echo ${ACL} | sed -e "s^%SERVER_ID%^${SERVER_ID}^") ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred copying ${APP_ROOT}/${IPLANET_ACL_TEMPLATE} to ${APP_ROOT}/${BUILD_TMP_DIR}/${IPLANET_ACL_DIR}$(echo ${ACL} | sed -e "s^%SERVER_ID%^${SERVER_ID}^")";

                (( ERROR_COUNT += 1 ));
            fi
        done

        IFS=${CURR_IFS};

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ACL files copied.";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Copy created. Validating..";

        if [ ${ERROR_COUNT} -eq 0 ]
        then
            if [ -d ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID} ]
            then
                ## ok, we've created our directory - lets rock out
                ## we need to replace some things..
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validation complete. Operating..";

                for REPLACEMENT_ITEM in $(grep "&" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG} | cut -d "&" -f 2)
                do
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REPLACEMENT_ITEM - ${REPLACEMENT_ITEM} -> $(eval echo \${${REPLACEMENT_ITEM}})";

                    sed -e "s^&${REPLACEMENT_ITEM}&^$(eval echo \${${REPLACEMENT_ITEM}})^g" \
                        ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG} \
                        > ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG}.tmp;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating...";

                    if [ ! -z "$(grep "$(eval echo \${${REPLACEMENT_ITEM}})" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG}.tmp)" !]
                    then
                        ## ok, move it over now..
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                        mv ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG}.tmp \
                            ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG} > /dev/null 2>&1;

                        ## and ensure..
                        if [ ! -z "$(grep "$(eval echo \${${REPLACEMENT_ITEM}})" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG})" !]
                        then
                            ## good, keep going
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                            continue;
                        else
                            ## ok, its not there. break out - doesnt make sense to continue
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                            (( ERROR_COUNT += 1 ));

                            break;
                        fi
                    else
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                        (( ERROR_COUNT += 1 ));

                        break;
                    fi
                done

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ERROR_COUNT -> ${ERROR_COUNT}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${IPLANET_CORE_CONFIG} built. Validating..";

                if [ ${ERROR_COUNT} -eq 0 ]
                then
                    ## magnus complete. see if this is websphere-enabled..
                    if [ ! -z "${ENABLE_WEBSPHERE}" ] && [ "${ENABLE_WEBSPHERE}" = "${_TRUE}" ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding WebSphere configuration to ${IPLANET_CORE_CONFIG} ..";

                        print "${IPLANET_WAS_FUNCTION}" >> ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG};
                        print "$(echo ${IPLANET_WAS_BOOTSTRAP} | sed -e "s^%SERVER_ROOT%^${SERVER_ROOT}^" \
                            -e "s^%SERVER_ID%^${SERVER_ID}^g" -e "s/%PROJECT_CODE%/${PROJECT_CODE}/")" >> ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_CORE_CONFIG};

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding WebSphere configuration to ${IPLANET_WEB_CONFIG} ..";

                        ## and update the obj files..
                        for WEB_CONFIG in $(ls ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/ | grep ${IPLANET_WEB_CONFIG})
                        do
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding WebSphere configuration to ${WEB_CONFIG} ..";

                            sed -e "14a ${IPLANET_WAS_HANDLER}" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${WEB_CONFIG} \
                                > ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${WEB_CONFIG}.tmp;

                            if [ ! -z "$(grep "${IPLANET_WAS_HANDLER}" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${WEB_CONFIG}.tmp)" !]
                            then
                                ## ok, move it over now..
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                                mv ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${WEB_CONFIG}.tmp \
                                    ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${WEB_CONFIG} > /dev/null 2>&1;

                                ## and ensure..
                                if [ ! -z "$(grep "${IPLANET_WAS_HANDLER}" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${WEB_CONFIG})" !]
                                then
                                    ## good, keep going
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                                    continue;
                                else
                                    ## ok, its not there. break out - doesnt make sense to continue
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                                    (( ERROR_COUNT += 1 ));

                                    break;
                                fi
                            else
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                                (( ERROR_COUNT += 1 ));

                                break;
                            fi
                        done

                        unset WEB_CONFIG;
                    fi

                    ## magnus complete, continue with server.xml
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${IPLANET_CORE_CONFIG} validated. Continuing with ${IPLANET_SERVER_CONFIG}...";

                    unset REPLACEMENT_ITEM;

                    for REPLACEMENT_ITEM in $(grep "&" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG} | cut -d "&" -f 2)
                    do
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REPLACEMENT_ITEM - ${REPLACEMENT_ITEM} -> $(eval echo \${${REPLACEMENT_ITEM}})";

                        sed -e "s^&${REPLACEMENT_ITEM}&^$(eval echo \${${REPLACEMENT_ITEM}})^g" \
                            ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG} \
                            > ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG}.tmp;

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating...";

                        if [ ! -z "$(grep "$(eval echo \${${REPLACEMENT_ITEM}})" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG}.tmp)" !]
                        then
                            ## ok, move it over now..
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                            mv ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG}.tmp \
                                ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG} > /dev/null 2>&1;

                            ## and ensure..
                            if [ ! -z "$(grep "$(eval echo \${${REPLACEMENT_ITEM}})" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${IPLANET_CONFIG_PATH}/${IPLANET_SERVER_CONFIG})" !]
                            then
                                ## good, keep going
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                                continue;
                            else
                                ## ok, its not there. break out - doesnt make sense to continue
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                                (( ERROR_COUNT += 1 ));

                                break;
                            fi
                        else
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                            (( ERROR_COUNT += 1 ));

                            break;
                        fi
                    done

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ERROR_COUNT -> ${ERROR_COUNT}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${IPLANET_SERVER_CONFIG} built. Validating..";

                    if [ ${ERROR_COUNT} -eq 0 ]
                    then
                        unset REPLACEMENT_ITEM;

                        ## and finish off with the scripts
                        for SCRIPT in ${IPLANET_START_SCRIPT} ${IPLANET_STOP_SCRIPT} ${IPLANET_RESTART_SCRIPT} ${IPLANET_ROTATE_SCRIPT} ${IPLANET_RECONFIG_SCRIPT}
                        do
                            for REPLACEMENT_ITEM in $(grep "&" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${SCRIPT} | cut -d "&" -f 2)
                            do
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REPLACEMENT_ITEM - ${REPLACEMENT_ITEM} -> $(eval echo \${${REPLACEMENT_ITEM}})";

                                sed -e "s^&${REPLACEMENT_ITEM}&^$(eval echo \${${REPLACEMENT_ITEM}})^g" \
                                    ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${SCRIPT} \
                                    > ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${SCRIPT}.tmp;

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating...";

                                if [ ! -z "$(grep "$(eval echo \${${REPLACEMENT_ITEM}})" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${SCRIPT}.tmp)" !]
                                then
                                    ## ok, move it over now..
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                                    mv ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${SCRIPT}.tmp \
                                        ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${SCRIPT} > /dev/null 2>&1;

                                    ## and ensure..
                                    if [ ! -z "$(grep "$(eval echo \${${REPLACEMENT_ITEM}})" ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${SCRIPT})" !]
                                    then
                                        ## good, keep going
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Change validated. Continuing..";

                                        ## update permissions.. for some reason they arent carrying over
                                        chmod 754 ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${SERVER_ID}/${SCRIPT} > /dev/null 2>&1;

                                        continue;
                                    else
                                        ## ok, its not there. break out - doesnt make sense to continue
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                                        (( ERROR_COUNT += 1 ));

                                        break;
                                    fi
                                else
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating the server configuration. Please try again.";

                                    (( ERROR_COUNT += 1 ));

                                    break;
                                fi
                            done
                        done

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "ERROR_COUNT -> ${ERROR_COUNT}";

                        if [ ${ERROR_COUNT} -eq 0 ]
                        then
                            ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Instance build for ${SITE_HOSTNAME} completed by ${IUSER_AUDIT}.";

                            RETURN_CODE=0;
                        else
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more build operations has failed. ERROR_COUNT -> ${ERROR_COUNT}. Cannot continue.";

                            RETURN_CODE=48;
                        fi
                    else
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more build operations has failed. ERROR_COUNT -> ${ERROR_COUNT}. Cannot continue.";

                        RETURN_CODE=48;
                    fi
                else
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "One or more build operations has failed. ERROR_COUNT -> ${ERROR_COUNT}. Cannot continue.";

                    RETURN_CODE=48;
                fi
            else
                ## no working directory
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to create working copy of template installation. Please try again.";

                RETURN_CODE=47;
            fi
        else
            ## one or more errors were encountered while copying the templates
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Unable to create working copy of template installation. Please try again.";

            RETURN_CODE=48;
        fi
    fi

    ERROR_COUNT=0;
    unset REPLACEMENT_ITEM;
    unset PLATFORM_TYPE_IDENTIFIER;
    unset SERVER_ROOT;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the usage of this application
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Create a skeleton zone file with the necessary components.";
    print "Usage: ${CNAME} <build type>";
    print "\t\tBuild type can be one of the following:";
    print "\t\t\tssl";
    print "\t\t\tnonssl";
    print "\t\t\tboth";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[[ -z "${PLUGIN_ROOT_DIR}" && -s ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin.sh ]] && . ${SCRIPT_ROOT}/../${LIB_DIRECTORY}/plugin.sh;
[ -z "${PLUGIN_ROOT_DIR}" ] && exit 1

[ ${#} -eq 0 ] && usage;

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

[ "$1" = "${BUILD_TYPE_SSL}" ] && createSecuredInstance;
[ "$1" = "${BUILD_TYPE_BOTH}" ] && createSecuredInstance ${_TRUE};
[ "$1" = "${BUILD_TYPE_NOSSL}" ] && createUnsecuredInstance;

return ${RETURN_CODE};
