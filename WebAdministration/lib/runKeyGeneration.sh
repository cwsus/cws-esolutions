#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  run_key_generation.sh
#         USAGE:  ./run_key_generation.sh
#   DESCRIPTION:  Processes backout requests for previously executed change
#                 requests.
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

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

## Application constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#          NAME:  generate_iplanet_csr
#   DESCRIPTION:  Generates a certificate signing request (CSR) for an iPlanet
#                 webserver
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function createNewCertificate
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERTDB_STORE -> ${CERTDB_STORE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CSRSTORE -> ${CSRSTORE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MAILSTORE -> ${MAILSTORE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NA_CSR_SUBJECT -> ${NA_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CA_CSR_SUBJECT -> ${CA_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "AU_CSR_SUBJECT -> ${AU_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UK_CSR_SUBJECT -> ${UK_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERTIFICATE_DATABASE -> ${CERTIFICATE_DATABASE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_DOMAIN_NAME -> ${SITE_DOMAIN_NAME}";

    SITE_IDENTIFIER=$(echo ${PLATFORM_CODE} | cut -d "_" -f 1);
    REGION_IDENTIFIER=$(echo ${PLATFORM_CODE} | cut -d "_" -f 2);

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_IDENTIFIER -> ${SITE_IDENTIFIER}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REGION_IDENTIFIER -> ${REGION_IDENTIFIER}";

    if [ -s ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR}/${CERTIFICATE_DATABASE}${IPLANET_CERT_STORE_KEY_SUFFIX} ] \
        && [ -s ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR}/${CERTIFICATE_DATABASE}${IPLANET_CERT_STORE_CERT_SUFFIX} ]
    then
        ## ok. we have a cert db and we've been asked to generate a csr. do it.
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generating CSR for ${SITE_DOMAIN_NAME}..";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Obtaining certificate information..";

        ## determine the subject to utilize
        if [ ! -z "$(grep -w ${SITE_DOMAIN_NAME} ${APP_ROOT}/${SITE_OVERRIDES})" ]
        then
            ## site exists in the site overrides file
            CERT_SIGNER=$(grep -w ${SITE_DOMAIN_NAME} ${APP_ROOT}/${SITE_OVERRIDES} | cut -d ":" -f 2);
            CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
        else
            if [ "$(echo ${PLATFORM_CODE} | cut -d "_" -f 2)" = "I" ]
            then
                CERT_SIGNER=${INTRANET_CERT_SIGNATORY};
                CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
            elif [ "$(echo ${PLATFORM_CODE} | cut -d "_" -f 2)" = "X" ]
            then
                CERT_SIGNER=${INTERNET_CERT_SIGNATORY};

                case ${SITE_IDENTIFIER} in
                    [Pp][Hh]|[Vv][Hh]|[Bb][Rr]|[Aa][Hh]|[Bb][Uu]|[Vv][Oo]|[Vv][Oo][Vv][Hh])
                        CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                        ;;
                    [Nn][Ww]|[Ss][Yy]|[Nn][Ww][Nn]|[Ss][Yy][Gg]|[Ss][Hh]|[Ll][Pp])
                        if [ ! -z "$(echo ${SITE_DOMAIN_NAME} | grep .ca)" ]
                        then
                            CERT_SUBJECT=$(echo ${CA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                        elif [ ! -z "$(echo ${SITE_DOMAIN_NAME} | grep .au)" ]
                        then
                            CERT_SUBJECT=$(echo ${AU_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                        else
                            ## default to north america
                            CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                        fi
                        ;;
                    *)
                        ## unknown site identifier, default to north america for now
                        CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                        ;;
                esac
            else
                ## platform code doesn't specify an I or an X in the second field
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An unknown platform type was encountered. Cannot continue.";

                RETURN_CODE=4;
            fi
        fi

        if [ -z "${RETURN_CODE}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_SIGNER -> ${CERT_SIGNER}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_SUBJECT -> ${CERT_SUBJECT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing certutil -R -d ${APP_ROOT}/${CERTDB_STORE} -P ${CERTIFICATE_DATABASE} -s ${CERT_SUBJECT} -o ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -p ${REQUEST_CONTACT_NUM} -a -f ${APP_ROOT}/${IPLANET_CERT_DB_PASSFILE} -z "${APP_ROOT}"/${ENTROPY_FILE} -g ${CERT_BIT_LENGTH} > ${APP_ROOT}/${LOG_ROOT}/certutil.csr-gen.${SITE_DOMAIN_NAME}.${IUSER_AUDIT} 2>&1";

            ## add all available root and intermediate certificates to the database
            for SIGNATORY in $(find ${APP_ROOT}/${ROOT_CERT_STORE} -type f -name \*.cer)
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SIGNATORY -> ${SIGNATORY}";

                SIGNER_NICKNAME=$(openssl x509 -in ${SIGNATORY} -noout -subject | grep CN | sed -e "s/CN=/@/" | cut -d "@" -f 2 | cut -d "/" -f 1);

                if [ -z "${SIGNER_NICKNAME}" ]
                then
                    ## signer nickname couldnt be identified. use the filename instead
                    SIGNER_NICKNAME=$(basename "${SIGNATORY}" .cer);
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SIGNER_NICKNAME -> ${SIGNER_NICKNAME}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command certutil -d ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR} -A -i \"${SIGNATORY}\" -n \"${SIGNER_NICKNAME}\" -t \"T,C,c\" -P ${CERTIFICATE_DATABASE}";

                if [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ]
                then
                    certutil -d ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR} -A -i "${SIGNATORY}" \
                        -n "${SIGNER_NICKNAME}" -t "T,C,c" -P ${CERTIFICATE_DATABASE};
                else
                    certutil -d ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR} -A -i "${SIGNATORY}" \
                        -n "${SIGNER_NICKNAME}" -t "T,C,c" -P ${CERTIFICATE_DATABASE} > ${APP_ROOT}/${LOG_ROOT}/certutil.add-roots.${SITE_HOSTNAME}.${IUSER_AUDIT} 2>&1;
                fi
            done

            if [ ! -z "${REGION_IDENTIFIER}" ] && [ "${REGION_IDENTIFIER}" != "${ENV_TYPE_PRD}" ]
            then
                ## if we're configured to self-sign, do so here
                if [ ! -z "${GENERATE_SELF_SIGNED}" ] && [ "${GENERATE_SELF_SIGNED}" = "${_TRUE}" ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configured for self-signed certs. Continuing..";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command certutil -R -d ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR} -P ${CERTIFICATE_DATABASE} -s \"$(echo ${SELF_SIGN_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_HOSTNAME}/")\" -o ${APP_ROOT}/${CSRSTORE}/SS-${CERT_NICKNAME}.csr -p ${REQUEST_CONTACT_NUM} -a -f ${APP_ROOT}/${IPLANET_CERT_DB_PASSFILE} -z "${APP_ROOT}"/${ENTROPY_FILE} -g ${CERT_BIT_LENGTH}";

                    ## this is basically the same as generating a normal csr, except we're going to
                    ## generate a cert off it too.
                    if [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ]
                    then
                        certutil -R -d ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR} -P ${CERTIFICATE_DATABASE} \
                            -s "$(echo ${SELF_SIGN_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_HOSTNAME}/")" \
                            -o ${APP_ROOT}/${CSRSTORE}/SS-${CERT_NICKNAME}.csr -p ${REQUEST_CONTACT_NUM} -a \
                            -f ${APP_ROOT}/${IPLANET_CERT_DB_PASSFILE} -z "${APP_ROOT}"/${ENTROPY_FILE} -g ${CERT_BIT_LENGTH};
                    else
                        certutil -R -d ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR} -P ${CERTIFICATE_DATABASE} \
                            -s "$(echo ${SELF_SIGN_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_HOSTNAME}/")" \
                            -o ${APP_ROOT}/${CSRSTORE}/SS-${CERT_NICKNAME}.csr -p ${REQUEST_CONTACT_NUM} -a \
                            -f ${APP_ROOT}/${IPLANET_CERT_DB_PASSFILE} -z "${APP_ROOT}"/${ENTROPY_FILE} -g ${CERT_BIT_LENGTH} > ${APP_ROOT}/${LOG_ROOT}/certutil.csr-gen.${SITE_DOMAIN_NAME}.${IUSER_AUDIT} 2>&1;
                    fi

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Self-signed CSR generated. Continuing..";

                    if [ -s ${APP_ROOT}/${CSRSTORE}/SS-${CERT_NICKNAME}.csr ]
                    then
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CSR confirmed. Generating certificate..";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/${LIB_DIRECTORY}/runCertRenewal.sh -d ${CERTIFICATE_DATABASE} -s ${SITE_HOSTNAME} -w ${IPLANET_TYPE_IDENTIFIER} -p ${PLATFORM_CODE} -S -e..";

                        ## move forward
                        unset METHOD_NAME;
                        unset CNAME;

                        . ${APP_ROOT}/${LIB_DIRECTORY}/runCertRenewal.sh -d ${CERTIFICATE_DATABASE} -s ${SITE_HOSTNAME} \
                            -w ${IPLANET_TYPE_IDENTIFIER} -p ${PLATFORM_CODE} -S -e;
                        typeset -i RET_CODE=${?};

                        CNAME=$(basename ${0});
                        local METHOD_NAME="${CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                        if [ ! -z "${RET_CODE}" ]
                        then
                            if [ ${RET_CODE} -ne 0 ]
                            then
                                ## failed to make and apply self-signed cert. this is not a failure.
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate generation FAILED. Continuing.";

                                (( ERROR_COUNT += 1 ));
                            fi
                        else
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate generation FAILED. Continuing.";

                            (( ERROR_COUNT += 1 ));
                        fi
                    else
                        ## no csr, no self-sign
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CSR generation FAILED. Continuing.";

                        (( ERROR_COUNT += 1 ));
                    fi
                fi
            fi

            if [ ${ERROR_COUNT} -ne 0 ]
            then
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred generating a self-signed certificate for ${SITE_HOSTNAME}.";
            fi

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generating CSR for ${SITE_HOSTNAME} ..";
			[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command certutil -R -d ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR} -P ${CERTIFICATE_DATABASE} -s \"${CERT_SUBJECT}\" -o ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -p ${REQUEST_CONTACT_NUM} -a -f ${APP_ROOT}/${IPLANET_CERT_DB_PASSFILE} -z "${APP_ROOT}"/${ENTROPY_FILE} -g ${CERT_BIT_LENGTH}";

            if [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ]
            then
                certutil -R -d ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR} -P ${CERTIFICATE_DATABASE} -s "${CERT_SUBJECT}" \
                    -o ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -p ${REQUEST_CONTACT_NUM} -a \
                    -f ${APP_ROOT}/${IPLANET_CERT_DB_PASSFILE} -z "${APP_ROOT}"/${ENTROPY_FILE} -g ${CERT_BIT_LENGTH};
            else
                certutil -R -d ${APP_ROOT}/${BUILD_TMP_DIR}/${IUSER_AUDIT}/${IPLANET_CERT_DIR} -P ${CERTIFICATE_DATABASE} -s "${CERT_SUBJECT}" \
                    -o ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -p ${REQUEST_CONTACT_NUM} -a \
                    -f ${APP_ROOT}/${IPLANET_CERT_DB_PASSFILE} -z "${APP_ROOT}"/${ENTROPY_FILE} -g ${CERT_BIT_LENGTH} > ${APP_ROOT}/${LOG_ROOT}/certutil.csr-gen.${SITE_DOMAIN_NAME}.${IUSER_AUDIT} 2>&1;
            fi

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "certutil executed..";

            if [ -s ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr ]
            then
                ## cool, we have a csr. mail it out.
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generation complete. Mailing CSR..";

                unset CNAME;
                unset METHOD_NAME;

                . ${MAILER_CLASS} -m ${NOTIFY_CSR_EMAIL} -p ${PROJECT_CODE} -a "${NOTIFY_CSR_ADDRESS}" -t ${NOTIFY_TYPE_NOTIFY} -e;
                MAILER_CODE=${?};

                CNAME=$(basename ${0});
                local METHOD_NAME="${CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MAILER_CODE -> ${MAILER_CODE}";

                if [ ${MAILER_CODE} -ne 0 ]
                then
                    ## notification failed to send. "WARN" but dont "ERROR"
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to send notification.";

                    RETURN_CODE=95;
                else
                    ## done
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Email sent. Continuing..";

                    RETURN_CODE=0;
                fi
            else
                ## no csr was generated. "ERROR" out
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No CSR was generated. Cannot continue.";

                RETURN_CODE=5;
            fi
        fi
    else
        ## certificate databases dont exist
        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to confirm that the necessary certificate databases exist. Cannot continue.";

        RETURN_CODE=24;
    fi

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

#===  FUNCTION  ===============================================================
#          NAME:  generate_iplanet_csr
#   DESCRIPTION:  Generates a certificate signing request (CSR) for an iPlanet
#                 webserver
#    PARAMETERS:  None
#       RETURNS:  0
#==============================================================================
function createiPlanetCSR
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERTDB_STORE -> ${CERTDB_STORE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CSRSTORE -> ${CSRSTORE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MAILSTORE -> ${MAILSTORE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NA_CSR_SUBJECT -> ${NA_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CA_CSR_SUBJECT -> ${CA_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "AU_CSR_SUBJECT -> ${AU_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UK_CSR_SUBJECT -> ${UK_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERTIFICATE_DATABASE -> ${CERTIFICATE_DATABASE}";

    SITE_IDENTIFIER=$(echo ${TARGET_PLATFORM_CODE} | cut -d "_" -f 1);

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_IDENTIFIER -> ${SITE_IDENTIFIER}";

    if [ -s ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IPLANET_CERT_STORE_KEY_SUFFIX} ] \
        && [ -s ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IPLANET_CERT_STORE_CERT_SUFFIX} ]
    then
        ## ok. we have a cert db and we've been asked to generate a csr. do it.
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generating CSR for ${SITE_DOMAIN_NAME}..";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Obtaining certificate information..";

        CERT_NICKNAME=$(certutil -L -d ${APP_ROOT}/${CERTDB_STORE} -P ${CERTIFICATE_DATABASE} | grep "u,u,u" | awk '{print $1}');
        CERT_HOSTNAME=$(certutil -L -d ${APP_ROOT}/${CERTDB_STORE} -P ${CERTIFICATE_DATABASE} -n ${CERT_NICKNAME} \
            | grep Subject | grep "CN" | sed -e "s/CN=/^/" | cut -d "^" -f 2 | cut -d "," -f 1);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_NICKNAME -> ${CERT_NICKNAME}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_HOSTNAME -> ${CERT_HOSTNAME}";

        if [ "${CERT_HOSTNAME}" != "${SITE_DOMAIN_NAME}" ]
        then
            ## hostname mismatch. use the one in the cert db, but "WARN" of it
            ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_DOMAIN_NAME does not match CERT_HOSTNAME. SITE_DOMAIN_NAME -> ${SITE_DOMAIN_NAME}, CERT_HOSTNAME -> ${CERT_HOSTNAME}. Using CERT_HOSTNAME.";

            SITE_DOMAIN_NAME=${CERT_HOSTNAME};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_DOMAIN_NAME -> ${SITE_DOMAIN_NAME}";
        fi

        ## determine the subject to utilize
        if [ ! -z "$(grep -w ${SITE_DOMAIN_NAME} ${APP_ROOT}/${SITE_OVERRIDES})" ]
        then
            ## site exists in the site overrides file
            CERT_SIGNER=$(grep -w ${SITE_DOMAIN_NAME} ${APP_ROOT}/${SITE_OVERRIDES} | cut -d ":" -f 2);
            CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
        else
            if [ "$(echo ${TARGET_PLATFORM_CODE} | cut -d "_" -f 2)" = "I" ]
            then
                CERT_SIGNER=${INTRANET_CERT_SIGNATORY};
                CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
            elif [ "$(echo ${TARGET_PLATFORM_CODE} | cut -d "_" -f 2)" = "X" ]
            then
                CERT_SIGNER=${INTERNET_CERT_SIGNATORY};

                case ${SITE_IDENTIFIER} in
                    [Pp][Hh]|[Vv][Hh]|[Bb][Rr]|[Aa][Hh]|[Bb][Uu]|[Vv][Oo]|[Vv][Oo][Vv][Hh])
                        CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                        ;;
                    [Nn][Ww]|[Ss][Yy]|[Nn][Ww][Nn]|[Ss][Yy][Gg]|[Ss][Hh]|[Ll][Pp])
                        if [ ! -z "$(echo ${SITE_DOMAIN_NAME} | grep .ca)" ]
                        then
                            CERT_SUBJECT=$(echo ${CA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                        elif [ ! -z "$(echo ${SITE_DOMAIN_NAME} | grep .au)" ]
                        then
                            CERT_SUBJECT=$(echo ${AU_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                        else
                            ## default to north america
                            CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                        fi
                        ;;
                    *)
                        ## unknown site identifier, default to north america for now
                        CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                        ;;
                esac
            else
                ## platform code doesn't specify an I or an X in the second field
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An unknown platform type was encountered. Cannot continue.";

                RETURN_CODE=4;
            fi
        fi

        if [ -z "${RETURN_CODE}" ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_SIGNER -> ${CERT_SIGNER}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_SUBJECT -> ${CERT_SUBJECT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing certutil -R -d ${APP_ROOT}/${CERTDB_STORE} -P ${CERTIFICATE_DATABASE} -s ${CERT_SUBJECT} -o ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -p ${REQUEST_CONTACT_NUM} -a -f ${APP_ROOT}/${IPLANET_CERT_DB_PASSFILE} -z "${APP_ROOT}"/${ENTROPY_FILE} -g ${CERT_BIT_LENGTH} > ${APP_ROOT}/${LOG_ROOT}/certutil.csr-gen.${SITE_DOMAIN_NAME}.${IUSER_AUDIT} 2>&1";

            if [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ]
            then
                certutil -R -d ${APP_ROOT}/${CERTDB_STORE} -P ${CERTIFICATE_DATABASE} -s "${CERT_SUBJECT}" \
                    -o ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -p ${REQUEST_CONTACT_NUM} -a \
                    -f ${APP_ROOT}/${IPLANET_CERT_DB_PASSFILE} -z "${APP_ROOT}"/${ENTROPY_FILE} -g ${CERT_BIT_LENGTH};
            else
                certutil -R -d ${APP_ROOT}/${CERTDB_STORE} -P ${CERTIFICATE_DATABASE} -s "${CERT_SUBJECT}" \
                    -o ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -p ${REQUEST_CONTACT_NUM} -a \
                    -f ${APP_ROOT}/${IPLANET_CERT_DB_PASSFILE} -z "${APP_ROOT}"/${ENTROPY_FILE} -g ${CERT_BIT_LENGTH} > ${APP_ROOT}/${LOG_ROOT}/certutil.csr-gen.${SITE_DOMAIN_NAME}.${IUSER_AUDIT} 2>&1;
            fi

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "certutil executed..";

            if [ -s ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr ]
            then
                ## cool, we have a csr. mail it out.
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generation complete. Mailing CSR..";

                unset CNAME;
                unset METHOD_NAME;

                . ${MAILER_CLASS} -m ${NOTIFY_CSR_EMAIL} -p ${WEB_PROJECT_CODE} -a "${NOTIFY_CSR_ADDRESS}" -t ${NOTIFY_TYPE_NOTIFY} -e;
                MAILER_CODE=${?};

                CNAME=$(basename ${0});
                local METHOD_NAME="${CNAME}#${0}";

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MAILER_CODE -> ${MAILER_CODE}";

                if [ ${MAILER_CODE} -ne 0 ]
                then
                    ## notification failed to send. "WARN" but dont "ERROR"

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to send notification.";

                    RETURN_CODE=95;
                fi

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Email sent. Continuing..";

                RETURN_CODE=0;
            else
                ## no csr was generated. "ERROR" out
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No CSR was generated. Cannot continue.";

                RETURN_CODE=5;
            fi
        fi
    else
        ## we dont have a cert database, so lets go out and get it
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate database not found for ${SITE_DOMAIN_NAME}. Obtaining..";

        SOURCE_CERT_DATABASE=$(echo ${CERTIFICATE_DATABASE} | sed -e "s/${IUSER_AUDIT}/${SOURCE_WEB_SERVER}/");

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SOURCE_CERT_DATABASE -> ${SOURCE_CERT_DATABASE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating access to ${SOURCE_WEB_SERVER}..";

        $(ping ${SOURCE_WEB_SERVER} > /dev/null 2>&1);

        PING_RCODE=${?}

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

        if [ ${PING_RCODE} -eq 0 ]
        then
            ## run_scp_connection...
            for SUFFIX in ${IPLANET_CERT_STORE_KEY_SUFFIX} ${IPLANET_CERT_STORE_CERT_SUFFIX}
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSCPConnection.exp remote-copy ${SOURCE_WEB_SERVER} ${SOURCE_PATH}/${IPLANET_CERT_DIR}/${SOURCE_CERT_DATABASE}${SUFFIX} ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${SUFFIX} ${IPLANET_OWNING_USER}";

                ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSCPConnection.exp remote-copy ${SOURCE_WEB_SERVER} \
                    ${SOURCE_PATH}/${IPLANET_CERT_DIR}/${SOURCE_CERT_DATABASE}${SUFFIX} \
                    ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${SUFFIX} ${IPLANET_OWNING_USER};

                if [ -s ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${SUFFIX} ]
                then
                    (( FILE_COUNT += 1 ));
                fi
            done

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILE_COUNT -> ${FILE_COUNT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Databases copied. Validating..";

            ## make sure we got the files..
            if [ ${FILE_COUNT} -eq 2 ]
            then
                ## ok. we have a cert db and we've been asked to generate a csr. do it.
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generating CSR for ${SITE_DOMAIN_NAME}..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Obtaining certificate information..";

                CERT_NICKNAME=$(certutil -L -d ${APP_ROOT}/${CERTDB_STORE} -P ${CERTIFICATE_DATABASE} | grep "u,u,u" | awk '{print $1}');
                CERT_HOSTNAME=$(certutil -L -d ${APP_ROOT}/${CERTDB_STORE} -P ${CERTIFICATE_DATABASE} -n ${CERT_NICKNAME} \
                    | grep Subject | grep "CN" | sed -e "s/CN=/^/" | cut -d "^" -f 2 | cut -d "," -f 1);

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_NICKNAME -> ${CERT_NICKNAME}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_HOSTNAME -> ${CERT_HOSTNAME}";

                if [ "${CERT_HOSTNAME}" != "${SITE_DOMAIN_NAME}" ]
                then
                    ## hostname mismatch. use the one in the cert db, but "WARN" of it
                    ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_DOMAIN_NAME does not match CERT_HOSTNAME. SITE_DOMAIN_NAME -> ${SITE_DOMAIN_NAME}, CERT_HOSTNAME -> ${CERT_HOSTNAME}. Using CERT_HOSTNAME.";

                    RETURN_CODE=99;

                    SITE_DOMAIN_NAME=${CERT_HOSTNAME};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_DOMAIN_NAME -> ${SITE_DOMAIN_NAME}";
                fi

		        ## determine the subject to utilize
		        if [ ! -z "$(grep -w ${SITE_DOMAIN_NAME} ${APP_ROOT}/${SITE_OVERRIDES})" ]
		        then
		            ## site exists in the site overrides file
		            CERT_SIGNER=$(grep -w ${SITE_DOMAIN_NAME} ${APP_ROOT}/${SITE_OVERRIDES} | cut -d ":" -f 2);
		            CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
		        else
		            if [ "$(echo ${TARGET_PLATFORM_CODE} | cut -d "_" -f 2)" = "I" ]
		            then
		                CERT_SIGNER=${INTRANET_CERT_SIGNATORY};
		                CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
		            elif [ "$(echo ${TARGET_PLATFORM_CODE} | cut -d "_" -f 2)" = "X" ]
		            then
		                CERT_SIGNER=${INTERNET_CERT_SIGNATORY};

		                case ${SITE_IDENTIFIER} in
		                    [Pp][Hh]|[Vv][Hh]|[Bb][Rr]|[Aa][Hh]|[Bb][Uu]|[Vv][Oo]|[Vv][Oo][Vv][Hh])
		                        CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
		                        ;;
		                    [Nn][Ww]|[Ss][Yy]|[Nn][Ww][Nn]|[Ss][Yy][Gg]|[Ss][Hh]|[Ll][Pp])
		                        if [ ! -z "$(echo ${SITE_DOMAIN_NAME} | grep .ca)" ]
		                        then
		                            CERT_SUBJECT=$(echo ${CA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
		                        elif [ ! -z "$(echo ${SITE_DOMAIN_NAME} | grep .au)" ]
		                        then
		                            CERT_SUBJECT=$(echo ${AU_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
		                        else
		                            ## default to north america
		                            CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
		                        fi
		                        ;;
		                    *)
		                        ## unknown site identifier, default to north america for now
		                        CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
		                        ;;
		                esac
		            else
		                ## platform code doesn't specify an I or an X in the second field
		                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An unknown platform type was encountered. Cannot continue.";

		                RETURN_CODE=4;
		            fi
		        fi

                if [ -z "${RETURN_CODE}" ] || [ ${RETURN_CODE} -eq 99 ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_SIGNER -> ${CERT_SIGNER}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_SUBJECT -> ${CERT_SUBJECT}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing certutil -R -d ${APP_ROOT}/${CERTDB_STORE} -P ${CERTIFICATE_DATABASE} -s ${CERT_SUBJECT} -o ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -p ${REQUEST_CONTACT_NUM} -a -f ${APP_ROOT}/${IPLANET_CERT_DB_PASSFILE} -z "${APP_ROOT}"/${ENTROPY_FILE} -g ${CERT_BIT_LENGTH} > ${APP_ROOT}/${LOG_ROOT}/certutil.csr-gen.${SITE_DOMAIN_NAME}.${IUSER_AUDIT} 2>&1";

                    if [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ]
                    then
                        certutil -R -d ${APP_ROOT}/${CERTDB_STORE} -P ${CERTIFICATE_DATABASE} -s "${CERT_SUBJECT}" \
                            -o ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -p ${REQUEST_CONTACT_NUM} -a \
                            -f ${APP_ROOT}/${IPLANET_CERT_DB_PASSFILE} -z "${APP_ROOT}"/${ENTROPY_FILE} -g ${CERT_BIT_LENGTH};
                    else
                        certutil -R -d ${APP_ROOT}/${CERTDB_STORE} -P ${CERTIFICATE_DATABASE} -s "${CERT_SUBJECT}" \
                            -o ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -p ${REQUEST_CONTACT_NUM} -a \
                            -f ${APP_ROOT}/${IPLANET_CERT_DB_PASSFILE} -z "${APP_ROOT}"/${ENTROPY_FILE} \
                            -g ${CERT_BIT_LENGTH} > ${APP_ROOT}/${LOG_ROOT}/certutil.csr-gen.${SITE_DOMAIN_NAME}.${IUSER_AUDIT} 2>&1;
                    fi

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "certutil executed..";

                    if [ -s ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr ]
                    then
                        ## cool, we have a csr. mail it out.
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generation complete. Mailing CSR..";

                        unset CNAME;
                        unset METHOD_NAME;

                        . ${MAILER_CLASS} -m ${NOTIFY_CSR_EMAIL} -p ${WEB_PROJECT_CODE} -a "${NOTIFY_CSR_ADDRESS}" -t ${NOTIFY_TYPE_NOTIFY} -e;
                        MAILER_CODE=${?};

                        CNAME=$(basename ${0});
                        local METHOD_NAME="${CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MAILER_CODE -> ${MAILER_CODE}";

                        if [ ${MAILER_CODE} -ne 0 ]
                        then
                            ## notification failed to send. "WARN" but dont "ERROR"
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to send notification.";

                            RETURN_CODE=95;
                        else
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Email sent. Continuing..";

                            RETURN_CODE=0;
                        fi
                    else
                        ## no csr was generated. "ERROR" out
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No CSR was generated. Cannot continue.";

                        RETURN_CODE=5;
                    fi
                fi
            else
                ## failed to obtain the cert db, cant generate a csr
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to obtain the necessary certificate databases. Cannot continue.";

                RETURN_CODE=6;
            fi
        else
            ## source web server appears unavailable, so we cant go get our files
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to obtain the necessary certificate databases. Cannot continue.";

            RETURN_CODE=24;
        fi
    fi

    FILE_COUNT=0;
    unset CERT_HOSTNAME;
    unset CERT_SIGNER;
    unset CERT_SUBJECT;
    unset SUFFIX;
    unset RET_CODE;
    unset MAILER_CODE;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

function createIHSCSR
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERTDB_STORE -> ${CERTDB_STORE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CSRSTORE -> ${CSRSTORE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MAILSTORE -> ${MAILSTORE}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NA_CSR_SUBJECT -> ${NA_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CA_CSR_SUBJECT -> ${CA_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "AU_CSR_SUBJECT -> ${AU_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "UK_CSR_SUBJECT -> ${UK_CSR_SUBJECT}";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERTIFICATE_DATABASE -> ${CERTIFICATE_DATABASE}";

    SITE_IDENTIFIER=$(echo ${TARGET_PLATFORM_CODE} | cut -d "_" -f 1);

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_IDENTIFIER -> ${SITE_IDENTIFIER}";

    if [ -s ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_STASH_SUFFIX} ] \
        && [ -s ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_REQ_SUFFIX} ] \
        && [ -s ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} ]
    then
        ## ok. we have a cert db and we've been asked to generate a csr. do it.
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generating CSR for ${SITE_DOMAIN_NAME}..";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Obtaining certificate information..";

        CERT_NICKNAME=$(keyman -cert -list personal -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
            -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} | grep -v ${CERTIFICATE_DATABASE} | sed -e "s/^ *//g");
        CERT_HOSTNAME=$(keyman -cert -details -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
            -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -label "${CERT_NICKNAME}" -type ${IHS_KEY_DB_TYPE} | \
                grep Subject | cut -d "=" -f 2 | cut -d "," -f 1);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_NICKNAME -> ${CERT_NICKNAME}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_HOSTNAME -> ${CERT_HOSTNAME}";

        ## gsk7 kindof annoys me in that it isnt allowing me to create a
        ## csr of the same name as the certificate in the db. i do not know
        ## why. export the cert to p12, then convert it to a pem so we can
        ## use it later in the owner notify
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Exporting certificate..";

        keyman -cert -export -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
            -label ${CERT_NICKNAME} -target ${APP_ROOT}/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs \
            -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -target_pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) \
            -target_type pkcs12 -type ${IHS_KEY_DB_TYPE} -encryption strong;

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate exported. Validating..";

        if [ -s ${APP_ROOT}/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs ]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Export validated. Generating PEM..";

            openssl pkcs12 -nodes -nocerts -in ${APP_ROOT}/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs \
                -out ${APP_ROOT}/${PEMSTORE}/${CERTIFICATE_DATABASE}.pem \
                -password file:${APP_ROOT}/${IHS_CERT_DB_PASSFILE} \
                -passout pass:$(${APP_ROOT}/${IHS_CERT_DB_PASSFILE});

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Pem generated. Validating..";

            if [ -s ${APP_ROOT}/${PEMSTORE}/${CERTIFICATE_DATABASE}.pem ]
            then
                ## ok, pem was built
                rm -rf ${APP_ROOT}/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs > /dev/null 2>&1;

                if [ "${CERT_HOSTNAME}" != "${SITE_DOMAIN_NAME}" ]
                then
                    ## hostname mismatch. use the one in the cert db, but "WARN" of it
                    ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_DOMAIN_NAME does not match CERT_HOSTNAME. SITE_DOMAIN_NAME -> ${SITE_DOMAIN_NAME}, CERT_HOSTNAME -> ${CERT_HOSTNAME}. Using CERT_HOSTNAME.";

                    SITE_DOMAIN_NAME=${CERT_HOSTNAME};

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_DOMAIN_NAME -> ${SITE_DOMAIN_NAME}";
                fi

                ## determine the subject to utilize
                if [ ! -z "$(grep -w ${SITE_DOMAIN_NAME} ${APP_ROOT}/${SITE_OVERRIDES})" ]
                then
                    ## site exists in the site overrides file
                    CERT_SIGNER=$(grep -w ${SITE_DOMAIN_NAME} ${APP_ROOT}/${SITE_OVERRIDES} | cut -d ":" -f 2);
                    CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                else
                    if [ "$(echo ${TARGET_PLATFORM_CODE} | cut -d "_" -f 2)" = "I" ]
                    then
                        CERT_SIGNER=${INTRANET_CERT_SIGNATORY};
                        CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                    elif [ "$(echo ${TARGET_PLATFORM_CODE} | cut -d "_" -f 2)" = "X" ]
                    then
                        CERT_SIGNER=${INTERNET_CERT_SIGNATORY};

                        case ${SITE_IDENTIFIER} in
                            [Pp][Hh]|[Vv][Hh]|[Bb][Rr]|[Aa][Hh]|[Bb][Uu]|[Vv][Oo]|[Vv][Oo][Vv][Hh])
                                CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                ;;
                            [Nn][Ww]|[Ss][Yy]|[Nn][Ww][Nn]|[Ss][Yy][Gg]|[Ss][Hh]|[Ll][Pp])
                                if [ ! -z "$(echo ${SITE_DOMAIN_NAME} | grep .ca)" ]
                                then
                                    CERT_SUBJECT=$(echo ${CA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                elif [ ! -z "$(echo ${SITE_DOMAIN_NAME} | grep .au)" ]
                                then
                                    CERT_SUBJECT=$(echo ${AU_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                else
                                    ## default to north america
                                    CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                fi
                                ;;
                            *)
                                ## unknown site identifier, default to north america for now
                                CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                ;;
                        esac
                    else
                        ## platform code doesn't specify an I or an X in the second field
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An unknown platform type was encountered. Cannot continue.";

                        RETURN_CODE=4;
                    fi
                fi

                if [ -z "${RETURN_CODE}" ]
                then
                    ## ihs doesnt like ; or E=, so remove them
                    CERT_SUBJECT=$(echo ${CERT_SUBJECT} | cut -d ";" -f 1-6 | sed -e "s/;/,/g");

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_SIGNER -> ${CERT_SIGNER}";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_SUBJECT -> ${CERT_SUBJECT}";

                    ## clean up the certificate database
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing certificate from database..";

                    keyman -cert -delete -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                        -label ${CERT_NICKNAME} -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} > /dev/null 2>&1;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing certificate request from database..";

                    keyman -certreq -delete -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                        -label ${CERT_NICKNAME} -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} > /dev/null 2>&1;

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing keyman -certreq -create -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} -label ${CERT_NICKNAME} -file ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} -dn ${CERT_SUBJECT} -size ${CERT_BIT_LENGTH}";

                    if [ "${VERBOSE}" = "${_TRUE}" ]
                    then
                        keyman -certreq -create -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                            -label ${CERT_NICKNAME} -file ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) \
                            -type ${IHS_KEY_DB_TYPE} -dn "${CERT_SUBJECT}" -size ${CERT_BIT_LENGTH};
                    else
                        keyman -certreq -create -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                            -label ${CERT_NICKNAME} -file ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) \
                            -type ${IHS_KEY_DB_TYPE} -dn "${CERT_SUBJECT}" -size ${CERT_BIT_LENGTH} > ${APP_ROOT}/${LOG_ROOT}/keyman.csr-gen.${SITE_DOMAIN_NAME}.${IUSER_AUDIT} 2>&1;
                    fi

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "keyman executed..";

                    if [ -s ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr ]
                    then
                        ## cool, we have a csr. mail it out.
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generation complete. Mailing CSR..";

                        unset CNAME;
                        unset METHOD_NAME;

                        . ${MAILER_CLASS} -m ${NOTIFY_CSR_EMAIL} -p ${WEB_PROJECT_CODE} -a "${NOTIFY_CSR_ADDRESS}" -t ${NOTIFY_TYPE_NOTIFY} -e;
                        MAILER_CODE=${?};

                        CNAME=$(basename ${0});
                        local METHOD_NAME="${CNAME}#${0}";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MAILER_CODE -> ${MAILER_CODE}";

                        if [ ${MAILER_CODE} -ne 0 ]
                        then
                            ## notification failed to send. "WARN" but dont "ERROR"
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to send notification.";

                            RETURN_CODE=95;
                        fi

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Email sent. Continuing..";

                        RETURN_CODE=0;
                    else
                        ## no csr was generated. "ERROR" out
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No CSR was generated. Cannot continue.";

                        RETURN_CODE=5;
                    fi
                fi
            else
                ## no pem file, the owner notify wont generate properly
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No PEM was generated. Cannot continue.";

                RETURN_CODE=5;
            fi
        else
            ## no pkcs file, cant generate pem, the owner notify wont generate
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No PKCS#12 was generated. Cannot continue.";

            RETURN_CODE=5;
        fi
    else
        ## we dont have a cert database, so lets go out and get it
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate database not found for ${SITE_DOMAIN_NAME}. Obtaining..";

        SOURCE_CERT_DATABASE=$(echo ${CERTIFICATE_DATABASE} | cut -d "-" -f 1);

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SOURCE_CERT_DATABASE -> ${SOURCE_CERT_DATABASE}";
        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating access to ${SOURCE_WEB_SERVER}..";

        $(ping ${SOURCE_WEB_SERVER} > /dev/null 2>&1);

        PING_RCODE=${?}

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "PING_RCODE -> ${PING_RCODE}";

        if [ ${PING_RCODE} -eq 0 ]
        then
            ## run_scp_connection...
            for SUFFIX in ${IHS_DB_STASH_SUFFIX} ${IHS_DB_REQ_SUFFIX} ${IHS_DB_CRT_SUFFIX}
            do
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing command ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSCPConnection.exp remote-copy ${SOURCE_WEB_SERVER} ${IHS_CERT_DIR}/${SOURCE_CERT_DATABASE}${SUFFIX} ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${SUFFIX} ${IHS_OWNING_USER}";

                ${APP_ROOT}/${LIB_DIRECTORY}/tcl/runSCPConnection.exp remote-copy ${SOURCE_WEB_SERVER} \
                    ${IHS_CERT_DIR}/${SOURCE_CERT_DATABASE}${SUFFIX} \
                    ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${SUFFIX} ${IHS_OWNING_USER};

                if [ -s ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${SUFFIX} ]
                then
                    (( FILE_COUNT += 1 ));
                fi
            done

            unset SUFFIX;

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "FILE_COUNT -> ${FILE_COUNT}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Databases copied. Validating..";

            ## make sure we got the files..
            if [ ${FILE_COUNT} -eq 3 ]
            then
                ## ok. we have a cert db and we've been asked to generate a csr. do it.
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generating CSR for ${SITE_DOMAIN_NAME}..";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Obtaining certificate information..";

                CERT_NICKNAME=$(keyman -cert -list personal -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                    -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} | grep -v ${CERTIFICATE_DATABASE} | sed -e "s/^ *//g");
                CERT_HOSTNAME=$(keyman -cert -details -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                    -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -label ${CERT_NICKNAME} -type ${IHS_KEY_DB_TYPE} | \
                        grep Subject | cut -d "=" -f 2 | cut -d "," -f 1);

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_NICKNAME -> ${CERT_NICKNAME}";
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_HOSTNAME -> ${CERT_HOSTNAME}";

                ## gsk7 kindof annoys me in that it isnt allowing me to create a
                ## csr of the same name as the certificate in the db. i do not know
                ## why. export the cert to p12, then convert it to a pem so we can
                ## use it later in the owner notify
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Exporting certificate..";

                keyman -cert -export -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                    -label ${CERT_NICKNAME} -target ${APP_ROOT}/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs \
                    -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -target_pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) \
                    -target_type pkcs12 -type ${IHS_KEY_DB_TYPE} -encryption strong;

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Certificate exported. Validating..";

                if [ -s ${APP_ROOT}/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs ]
                then
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Export validated. Generating PEM..";

                    openssl pkcs12 -in ${APP_ROOT}/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs \
                        -out ${APP_ROOT}/${PEMSTORE}/${CERTIFICATE_DATABASE}.pem \
                        -password file:${APP_ROOT}/${IHS_CERT_DB_PASSFILE} \
                        -passout pass:$(${APP_ROOT}/${IHS_CERT_DB_PASSFILE});

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Pem generated. Validating..";

                    if [ -s ${APP_ROOT}/${PEMSTORE}/${CERTIFICATE_DATABASE}.pem ]
                    then
                        ## ok, pem was built
                        rm -rf ${APP_ROOT}/${PKCS12STORE}/${CERTIFICATE_DATABASE}.pkcs > /dev/null 2>&1;

                        if [ "${CERT_HOSTNAME}" != "${SITE_DOMAIN_NAME}" ]
                        then
                            ## hostname mismatch. use the one in the cert db, but "WARN" of it
                            ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_DOMAIN_NAME does not match CERT_HOSTNAME. SITE_DOMAIN_NAME -> ${SITE_DOMAIN_NAME}, CERT_HOSTNAME -> ${CERT_HOSTNAME}. Using CERT_HOSTNAME.";

                            RETURN_CODE=99;

                            SITE_DOMAIN_NAME=${CERT_HOSTNAME};

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_DOMAIN_NAME -> ${SITE_DOMAIN_NAME}";
                        fi

                        ## determine the subject to utilize
                        if [ "$(echo ${TARGET_PLATFORM_CODE} | cut -d "_" -f 2)" = "I" ]
                        then
                            if [[ "${SITE_IDENTIFIER}" = "[Cc][Ll]" ]]
                            then
                                ## clarity cert. required for verisign.
                                CERT_SIGNER=${INTERNET_CERT_SIGNATORY};
                            else
                                CERT_SIGNER=${INTRANET_CERT_SIGNATORY};
                            fi

                            CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                        elif [ "$(echo ${TARGET_PLATFORM_CODE} | cut -d "_" -f 2)" = "X" ]
                        then
                            CERT_SIGNER=${INTERNET_CERT_SIGNATORY};

                            case ${SITE_IDENTIFIER} in
                                [Pp][Hh]|[Vv][Hh]|[Bb][Rr]|[Aa][Hh]|[Bb][Uu]|[Vv][Oo]|[Vv][Oo][Vv][Hh])
                                    CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                    ;;
                                [Nn][Ww]|[Ss][Yy]|[Nn][Ww][Nn]|[Ss][Yy][Gg]|[Ss][Hh]|[Ll][Pp])
                                    if [ ! -z "$(echo ${SITE_DOMAIN_NAME} | grep .ca)" ]
                                    then
                                        CERT_SUBJECT=$(echo ${CA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                    elif [ ! -z "$(echo ${SITE_DOMAIN_NAME} | grep .au)" ]
                                    then
                                        CERT_SUBJECT=$(echo ${AU_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                    else
                                        ## default to north america
                                        CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                    fi
                                    ;;
                                *)
                                    ## unknown site identifier, default to north america for now
                                    CERT_SUBJECT=$(echo ${NA_CSR_SUBJECT} | sed -e "s/{SITE_HOSTNAME}/${SITE_DOMAIN_NAME}/");
                                    ;;
                            esac
                        else
                            ## platform code doesn't specify an I or an X in the second field
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An unknown platform type was encountered. Cannot continue.";

                            RETURN_CODE=4;
                        fi

                        if [ -z "${RETURN_CODE}" ] || [ ${RETURN_CODE} -eq 99 ]
                        then
                            ## ihs doesnt like ; or E=, so remove them
                            CERT_SUBJECT=$(echo ${CERT_SUBJECT} | cut -d ";" -f 1-6 | sed -e "s/;/,/g");

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_SIGNER -> ${CERT_SIGNER}";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERT_SUBJECT -> ${CERT_SUBJECT}";

                            ## clean up the certificate database
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing certificate from database..";

                            keyman -cert -delete -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                                -label ${CERT_NICKNAME} -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} > /dev/null 2>&1;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Removing certificate request from database..";

                            keyman -certreq -delete -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                                -label ${CERT_NICKNAME} -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} > /dev/null 2>&1;

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing keyman -certreq -create -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} -label ${CERT_NICKNAME} -file ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) -type ${IHS_KEY_DB_TYPE} -dn ${CERT_SUBJECT} -size ${CERT_BIT_LENGTH}";

                            if [ "${VERBOSE}" = "${_TRUE}" ]
                            then
                                keyman -certreq -create -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                                    -label ${CERT_NICKNAME} -file ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) \
                                    -type $(echo ${IHS_DB_CRT_SUFFIX} | sed -e "s/.//") -dn "${CERT_SUBJECT}" -size ${CERT_BIT_LENGTH};
                            else
                                keyman -certreq -create -db ${APP_ROOT}/${CERTDB_STORE}/${CERTIFICATE_DATABASE}${IHS_DB_CRT_SUFFIX} \
                                    -label ${CERT_NICKNAME} -file ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr -pw $(cat ${APP_ROOT}/${IHS_CERT_DB_PASSFILE}) \
                                    -type $(echo ${IHS_DB_CRT_SUFFIX} | sed -e "s/.//") -dn "${CERT_SUBJECT}" -size ${CERT_BIT_LENGTH} > ${APP_ROOT}/${LOG_ROOT}/keyman.csr-gen.${SITE_DOMAIN_NAME}.${IUSER_AUDIT} 2>&1;
                            fi

                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "keyman executed..";

                            if [ -s ${APP_ROOT}/${CSRSTORE}/${CERT_NICKNAME}.csr ]
                            then
                                ## cool, we have a csr. mail it out.
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Generation complete. Mailing CSR..";

                                unset CNAME;
                                unset METHOD_NAME;

                                . ${MAILER_CLASS} -m ${NOTIFY_CSR_EMAIL} -p ${WEB_PROJECT_CODE} -a "${NOTIFY_CSR_ADDRESS}" -t ${NOTIFY_TYPE_NOTIFY} -e;
                                MAILER_CODE=${?};

                                CNAME=$(basename ${0});
                                local METHOD_NAME="${CNAME}#${0}";

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "MAILER_CODE -> ${MAILER_CODE}";

                                if [ ${MAILER_CODE} -ne 0 ]
                                then
                                    ## notification failed to send. "WARN" but dont "ERROR"
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to send notification.";

                                    RETURN_CODE=95;
                                else
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Email sent. Continuing..";

                                    RETURN_CODE=0;
                                fi
                            else
                                ## no csr was generated. "ERROR" out
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No CSR was generated. Cannot continue.";

                                RETURN_CODE=5;
                            fi
                        fi
                    else
                        ## no pem file, the owner notify wont generate properly
                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No PEM was generated. Cannot continue.";

                        RETURN_CODE=5;
                    fi
                else
                    ## no pkcs file, cant generate pem, the owner notify wont generate
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No PKCS#12 was generated. Cannot continue.";

                    RETURN_CODE=5;
                fi
            else
                ## failed to obtain the cert db, cant generate a csr
                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to obtain the necessary certificate databases. Cannot continue.";

                RETURN_CODE=6;
            fi
        else
            ## source web server appears unavailable, so we cant go get our files
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Failed to obtain the necessary certificate databases. Cannot continue.";

            RETURN_CODE=24;
        fi
    fi

    FILE_COUNT=0;
    unset FILE_COUNT;
    unset CERT_HOSTNAME;
    unset CERT_SIGNER;
    unset CERT_SUBJECT;
    unset SUFFIX;
    unset RET_CODE;
    unset MAILER_CODE;

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
}

#===  FUNCTION  ===============================================================
#      NAME:  usage
#   DESCRIPTION:  Provide information on the usage of this application
#    PARAMETERS:  None
#   RETURNS:  0
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Generates a certificate signing request for a provided host.";
    print " -s    -> The site domain name to operate against";
    print " -v    -> The source server to obtain the necessary key databases from";
    print " -w    -> Platform type to execute against - iplanet or ihs";
    print " -p    -> The webserver base path (e.g. /opt/IBMIHS70)";
    print " -d    -> The certificate database to work against";
    print " -c    -> The target platform code.";
    print " -t    -> The requestor telephone number";
    print " -n    -> Indicates that this is a new SSL request";
    print " -e    -> Execute the request";
    print " -h|-? -> Show this help";

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

while getopts ":s:v:w:p:d:c:t:neh:" OPTIONS 2>/dev/null
do
    case "${OPTIONS}" in
        s)
            ## set the platform
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SITE_DOMAIN_NAME..";

            ## Capture the site root
            typeset -l SITE_DOMAIN_NAME="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SITE_DOMAIN_NAME -> ${SITE_DOMAIN_NAME}";
            ;;
        v)
            ## set the platform
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SOURCE_WEB_SERVER..";

            ## Capture the site root
            typeset -l SOURCE_WEB_SERVER="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SOURCE_WEB_SERVER -> ${SOURCE_WEB_SERVER}";
            ;;
        w)
            ## set the platform
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting WS_PLATFORM..";

            ## Capture the site root
            WS_PLATFORM=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "WS_PLATFORM -> ${WS_PLATFORM}";
            ;;
        p)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting SOURCE_PATH..";

            ## Capture the site root
            SOURCE_PATH=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "SOURCE_PATH -> ${SOURCE_PATH}";
            ;;
        d)
            ## set the certificate database name
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting CERTIFICATE_DATABASE..";

            ## Capture the site root
            CERTIFICATE_DATABASE=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "CERTIFICATE_DATABASE -> ${CERTIFICATE_DATABASE}";
            ;;
        c)
            ## set the certificate database name
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting TARGET_PLATFORM_CODE..";

            ## Capture the site root
            typeset -u TARGET_PLATFORM_CODE="${OPTARG}";

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "TARGET_PLATFORM_CODE -> ${TARGET_PLATFORM_CODE}";
            ;;
        t)
            ## set the certificate database name
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "OPTARG -> ${OPTARG}";
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting REQUEST_CONTACT_NUM..";

            ## Capture the site root
            REQUEST_CONTACT_NUM=${OPTARG};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "REQUEST_CONTACT_NUM -> ${REQUEST_CONTACT_NUM}";
            ;;
        n)
            ## this is a new certificate database
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Setting NEW_CERTIFICATE..";

            ## Capture the site root
            NEW_CERTIFICATE=${_TRUE};

            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NEW_CERTIFICATE -> ${NEW_CERTIFICATE}";
            ;;
        e)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating request..";

            ## Make sure we have enough information to process
            ## and execute
            if [ -z "${RETURN_CODE}" ]
            then
                if [ -z "${WS_PLATFORM}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No webserver platform was provided. Cannot continue.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=21;
                elif [ -z "${CERTIFICATE_DATABASE}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No certificate database was provided. Cannot continue.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=9;
                elif [ -z "${TARGET_PLATFORM_CODE}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No target platform code was provided. Cannot continue.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=9;
                elif [ -z "${REQUEST_CONTACT_NUM}" ]
                then
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No contact telephone number was provided. Cannot continue.";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    RETURN_CODE=9;
                else
                    ## We have enough information to process the request, continue
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Request validated - executing";
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                    if [ ! -z "${NEW_CERTIFICATE}" ] && [ "${NEW_CERTIFICATE}" = "${_TRUE}" ]
                    then
                        createNewCertificate;
                    else
                        if [ -z "${SOURCE_WEB_SERVER}" ]
                        then
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No source webserver was provided. Cannot continue.";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            RETURN_CODE=21;
                        elif [ -z "${SOURCE_PATH}" ]
                        then
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No source path was provided. Cannot continue.";
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                            RETURN_CODE=9;
                        else
                            if [ "${WS_PLATFORM}" = "${IPLANET_TYPE_IDENTIFIER}" ]
                            then
                                createiPlanetCSR;
                            elif [ "${WS_PLATFORM}" = "${IHS_TYPE_IDENTIFIER}" ]
                            then
                                createIHSCSR;
                            else
                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "No valid webserver platform was provided. Cannot continue.";
                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

                                RETURN_CODE=999;
                            fi
                        fi
                    fi
                fi
            fi
            ;;
        h)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
        [\?])
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
        *)
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

            usage;
            ;;
    esac
done

return ${RETURN_CODE};

