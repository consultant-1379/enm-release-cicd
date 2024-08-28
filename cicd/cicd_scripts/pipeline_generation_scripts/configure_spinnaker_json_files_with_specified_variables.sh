#!/bin/bash

function configure_spinnaker_json_files_with_specified_variables() {
    sed -i -e "s/APPLICATION_NAME/${spinnaker_application_name}/g" "${WORKSPACE}"/spinnaker_files/pipelines/cenm/${test_phase}/${test_phase}_cenm_${deployment_stage}.json
    check_for_exit_code "${?}"
    sed -i -e "s/PIPELINE_NAME/${spinnaker_pipeline_name}/g" "${WORKSPACE}"/spinnaker_files/pipelines/cenm/${test_phase}/${test_phase}_cenm_${deployment_stage}.json
    check_for_exit_code "${?}"
    sed -i -e "s/FEM_CONTROLLER/${spinnaker_fem_controller}/g" "${WORKSPACE}"/spinnaker_files/pipelines/cenm/${test_phase}/${test_phase}_cenm_${deployment_stage}.json
    check_for_exit_code "${?}"
    sed -i -e "s/SYNC_JOB_CONTROLLER/${spinnaker_sync_job_fem_controller}/g" "${WORKSPACE}"/spinnaker_files/pipelines/cenm/${test_phase}/${test_phase}_cenm_${deployment_stage}.json
    check_for_exit_code "${?}"
    sed -i -e "s/CBRS_JOB_CONTROLLER/${cbrs_fem_controller_name}/g" "${WORKSPACE}"/spinnaker_files/pipelines/cenm/${test_phase}/${test_phase}_cenm_${deployment_stage}.json
    check_for_exit_code "${?}"
}

function check_for_exit_code() {
    exit_code="${1}"

    if [[ ${exit_code} -ne 0 ]]; then
        echo -e "\n"
        echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
        echo "ERROR: Unable to configure the ${test_phase}/${test_phase}_cenm_${deployment_stage}.json file in configure_spinnaker_json_files_with_specified_variables.sh"
        echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
        echo -e "\n"
        exit 1
    else
        echo "INFO: configure_spinnaker_json_files_with_specified_variables.sh has passed"
        echo -e "\n"
    fi
}

########################
#     SCRIPT START     #
########################

spinnaker_application_name="${1}"
spinnaker_pipeline_name="${2}"
spinnaker_fem_controller="${3}"
spinnaker_sync_job_fem_controller="${4}"
test_phase="${5}"
deployment_stage="${6}"
cbrs_fem_controller_name="${7}"

configure_spinnaker_json_files_with_specified_variables
