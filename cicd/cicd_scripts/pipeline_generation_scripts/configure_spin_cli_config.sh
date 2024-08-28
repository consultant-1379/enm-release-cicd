#!/bin/bash

function configure_spin_cli_config() {
    sed -i -e "s/BB_USERNAME/${bb_func_user_username}/g" "${WORKSPACE}"/spinnaker_files/config/spin_cli.config
    check_for_exit_code "${?}"
    sed -i -e "s/BB_PASSWORD/${bb_func_user_password}/g" "${WORKSPACE}"/spinnaker_files/config/spin_cli.config
    check_for_exit_code "${?}"
}

function check_for_exit_code() {
    exit_code="${1}"

    if [[ ${exit_code} -ne 0 ]]; then
        echo -e "\n"
        echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
        echo "ERROR: Unable to configure the spin_cli.config in configure_spin_cli_config.sh"
        echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
        echo -e "\n"
        exit 1
    else
        echo "INFO: configure_spin_cli_config.sh has passed"
        echo -e "\n"
    fi
}

########################
#     SCRIPT START     #
########################
bb_func_user_username="${1}"
bb_func_user_password="${2}"

configure_spin_cli_config