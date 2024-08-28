#!/bin/bash

function start_json_files_check() {
    echo -e "\n"
    echo "************************************************"
    echo "*  CHECKING FOR JSON FILE SYNTAX VIOLATIONS  *"
    echo "************************************************"

    if [[ -z "${changed_files}" ]]; then
        echo "INFO: No json files changed"
        exit 0
    fi
}

function run_json_syntax_violations_check() {
    for script_to_check in ${changed_files}; do
        if [[ "${script_to_check}" == *".json"* ]]; then
            check_json_file_script "${script_to_check}"
            check_spinnaker_file_script "${script_to_check}"
            if [[ "${script_to_check}" == *"spinnaker_files"* ]]; then
                check_spinnaker_file_script "${script_to_check}"
            fi
        fi
        
    done
}

function check_json_file_script() {
    script_to_check="${1}"

    echo "Checking Json file: ${script_to_check}"
    cat "${script_to_check}" | python -m json.tool
    exit_code=${?}
    if [[ ${exit_code} -ne 0 ]]; then
        is_json_check_successful=false
        let "number_of_failures++"
        json_scripts_with_errors+="${script_to_check}"$'\n'
        echo -e "\n"
        echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
        echo "ERROR: Please review the above Jsonfile syntax errors in : ${script_to_check}"
        echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
        echo -e "\n"
    else
        echo "INFO: ${script_to_check} has passed"
        echo -e "\n"
    fi
}

function check_spinnaker_file_script() {
    script_to_check="${1}"

    check_attribute_values "${script_to_check}" "application" "APPLICATION_NAME"
    check_attribute_values "${script_to_check}" "master" "_CONTROLLER"
    check_id_value "${script_to_check}"
}

function check_attribute_values() {
    script_to_check="${1}"
    attribute_name="${2}"
    attribute_value_format="${3}"
    attribute_values=$(cat "${script_to_check}" | grep "${attribute_name}" | tr -d ' ')
    for attribute_value in $(echo "${attribute_values}" | tr '\n' ' '); do
        if [[ "${attribute_value}" != *"${attribute_value_format}"* ]]; then
            if [[ "${attribute_value}" == *"default"* ]] || [[ "${attribute_value}" == *"value"* ]]; then
                INFO "Exception allowed for Master as it is define as default value"
            else
                is_spinnaker_check_successful=false
                spinnaker_json_files_with_errors+="${script_to_check}"$'\n'
                echo -e "\n"
                echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
                echo "ERROR: ${attribute_name} wrongly updated in : ${script_to_check}. Please Investigate"
                echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
                echo -e "\n"
            fi
        fi
    done
}

function check_id_value() {
    script_to_check="${1}"
    id=$(cat "${script_to_check}" | grep "\"id\":")
    if [[ -n "${id}" ]]; then
        is_spinnaker_check_successful=false
        spinnaker_json_files_with_errors+="${script_to_check}"$'\n'
        echo -e "\n"
        echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
        echo "ERROR: Found value for id in : ${script_to_check}. Please remove id from the spinnaker json config"
        echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
        echo -e "\n"
    fi
}

function check_json_exit_criteria() {
    if [[ ${is_json_check_successful} = false ]]; then
        echo "============================================================================"
        echo "FAILURE: ${number_of_failures} file(s) have json syntax violations"
        echo "Files with Json violations:"
        echo "${json_scripts_with_errors}"
        echo "============================================================================"
    else
        echo "==============================================="
        echo "PASS: All Json file syntax checks have passed"
        echo "==============================================="
    fi
}

function check_spinnaker_exit_criteria() {
    if [[ ${is_spinnaker_check_successful} = false ]]; then
        echo "============================================================================"
        files_with_error=$(echo "${spinnaker_json_files_with_errors}" | uniq)
        echo "FAILURE: $(echo "${files_with_error}" | wc -l) file(s) have Spinnaker Json violations:"
        echo "${files_with_error}"
        echo "============================================================================"
    else
        echo "==============================================="
        echo "PASS: All Spinnaker Json syntax checks have passed"
        echo "==============================================="
    fi
}

function check_exit_criteria() {
    if [[ ${is_json_check_successful} = false ]] || [[ ${is_spinnaker_check_successful} = false ]]; then
        exit 1
    fi
}

########################
#     SCRIPT START     #
########################

changed_files=$(cat < diff.txt)

start_json_files_check
run_json_syntax_violations_check
check_json_exit_criteria
check_spinnaker_exit_criteria
check_exit_criteria
