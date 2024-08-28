#!/bin/bash

function add_changed_dsl_jobs_to_jobs_to_build() {
    echo "INFO: Adding changed seed jobs and pipelines to DSL jobs to build file"
    changed_files=$(cat < diff.txt | tr "\r\n" " ")
    for changed_file in ${changed_files[@]}; do
        echo "single change file"
        echo "${changed_file}"
        if [[ "${changed_file}" != *"commonclasses"* ]] && [[ "${changed_file}" == *"DSL/"* ]]; then
            dsl_files_to_run="${dsl_files_to_run} ${changed_file}"
            echo "dsl files to run"
            echo "${dsl_files_to_run}"
        fi
    done

    if [[ -n "${dsl_files_to_run}" ]]; then
        echo "${dsl_files_to_run}" >> "${WORKSPACE}/dsl_seed_jobs_to_build.txt"
    fi
}

########################
#     SCRIPT START     #
########################

add_changed_dsl_jobs_to_jobs_to_build
