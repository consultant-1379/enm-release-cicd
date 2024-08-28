#!/bin/bash

function start_jenkinsfile_check() {
    echo -e "\n"
    echo "************************************************"
    echo "*  CHECKING FOR JENKINSFILE SYNTAX VIOLATIONS  *"
    echo "************************************************"

    if [[ -z "${changed_files}" ]]; then
        echo "INFO: No Jenkinsfile files changed"
        exit 0
    fi
}

function run_jenkinsfile_syntax_violations_check() {
    for script_to_check in ${changed_files}; do
        if [[ "${script_to_check}" == *"Jenkinsfile"* ]]; then
            check_jenkinsfile_script "${script_to_check}"
        fi
    done
}

function check_jenkinsfile_script() {
    script_to_check="${1}"

    echo "Checking Jenkinsfile file: ${script_to_check}"
    curl --user BBFUNCUSR:119761587666ed3ce259707b3850d9efd4 -X POST -F "jenkinsfile=<${script_to_check}" https://fem12s11-eiffel004.eiffel.gic.ericsson.se:8443/jenkins/pipeline-model-converter/validate  > jenkins_validator.txt
    errors=$(cat jenkins_validator.txt | grep "Errors encountered")
    cat jenkins_validator.txt

    if [[ -n "${errors}" ]]; then
        is_check_successful=false
        let "number_of_failures++"
        jenkinsfile_scripts_with_errors+="${script_to_check}"
        echo -e "\n"
        echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
        echo "ERROR: Please review the above Jenkinsfile syntax errors in : ${script_to_check}"
        echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
        echo -e "\n"
    else
        echo "INFO: ${script_to_check} has passed"
        echo -e "\n"
    fi
}

function check_exit_criteria() {
    if [[ ${is_check_successful} = true ]]; then
        echo "==============================================="
        echo "PASS: All Jenkinsfile syntax checks have passed"
        echo "==============================================="
    else
        echo "============================================================================"
        echo "FAILURE: ${number_of_failures} files have Jenkinsfile syntax violations"
        echo "Files with Jenkinsfile violations:"
        for file_with_error in ${jenkinsfile_scripts_with_errors}; do
            echo -e "\t ${file_with_error}"
        done
        echo "============================================================================"
        exit 1
    fi
}

########################
#     SCRIPT START     #
########################

changed_files=$(cat < diff.txt)
is_check_successful=true

start_jenkinsfile_check
run_jenkinsfile_syntax_violations_check
check_exit_criteria
