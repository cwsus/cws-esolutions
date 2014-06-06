#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  constants.sh
#         USAGE:  ./constants.sh
#   DESCRIPTION:  Sets and unsets system variables
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

###############################################################################
#       check if is running on a eSupport DR Node, exit if it is not.
###############################################################################
typeset -r ES_LIB="/opt/esupport/lib";
typeset -r ECOMSERVER_MODULE="${ES_LIB}/runsOnEcomServer.mod";

if [ -s ${ECOMSERVER_MODULE} ]
then
    . ${ECOMSERVER_MODULE};

    runsOnEcomServer "ED";
fi

[ -z "${PLUGIN_NAME}" ] && PLUGIN_NAME="DNSAdministration";

## load application-wide constants if not already done
if [ -z "${APP_ROOT}" ]
then
    case $(pwd) in
        *monitors*|*executors*|*sys*|*bin*) . ${SCRIPT_ROOT}/../../../constants.sh ;;
        *home*) . ${SCRIPT_ROOT}/../../../constants.sh ;;
        *) . ${SCRIPT_ROOT}/../../constants.sh ;;
    esac
fi

typeset -rx PLUGIN_ROOT_DIR=${PLUGIN_DIR}/${PLUGIN_NAME};
typeset -rx PLUGIN_CONF_BASE=${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY};

case ${EXPORT_ENVIRONMENT} in
    [Ss][Tt][Gg]|[Ss][Tt][Aa][Gg][Ee]) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/stg/ ;;
    [Uu][Aa][Tt]|[Qq][Aa]) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/qa/ ;;
    [Ii][Ss][Tt]|[Dd][Ee][Vv]|[Dd][Ee][Vv][Ee][Ll][Oo][Pp][Mm][Ee][Nn][Tt]) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/dev/ ;;
    *) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/ ;; ## default to production
esac

[[ -z "${PLUGIN_CONF_ROOT}" || ! -s ${PLUGIN_CONF_ROOT}/plugin.properties ]] && return 1;

typeset -rx PLUGIN_LOADED=true;
typeset -rx PLUGIN_CONFIG=${PLUGIN_CONF_ROOT}/plugin.properties;

[ -f ${PLUGIN_CONFIG} ] && . ${PLUGIN_CONFIG};

typeset -i AUTHORIZATION_COUNT; AUTHORIZATION_COUNT=0;

[[ ! -z "${TMP_DIRECTORY}" && ! -d ${TMP_DIRECTORY} ]] && mkdir ${TMP_DIRECTORY};
[[ ! -z "${DATA_DIRECTORY}" && ! -d ${DATA_DIRECTORY} ]] && mkdir ${DATA_DIRECTORY};
[[ ! -z "${WORK_DIRECTORY}" && ! -d ${WORK_DIRECTORY} ]] && mkdir ${WORK_DIRECTORY};
[[ ! -z "${MAILSTORE}" && ! -d ${MAILSTORE} ]] && mkdir ${MAILSTORE};
[[ ! -z "${BACKUP_DIRECTORY}" && ! -d ${BACKUP_DIRECTORY} ]] && mkdir ${BACKUP_DIRECTORY};
[[ ! -z "${BACKUP_DIRECTORY}" && ! -d ${BACKUP_DIRECTORY} ]] && mkdir ${BACKUP_DIRECTORY};

## common aliases
[ -s ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/aliases ] && . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/aliases;
[ -s ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/functions ] && . ${PLUGIN_ROOT_DIR}/${LIB_DIRECTORY}/functions;
