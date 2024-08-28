#!/bin/bash

function write_changed_dsl_files_to_workspace() {
    if [[ ${changed_files} == *"commonclasses"* ]]; then
        add_all_dsl_seed_child_jobs_and_seed_pipelines_to_jobs_to_build
    else
        add_changed_dsl_jobs_to_jobs_to_build
    fi
}

function add_all_dsl_seed_child_jobs_and_seed_pipelines_to_jobs_to_build() {
    echo "INFO: Adding all pipeline associated seed jobs to build file as a common script was changed"
    echo "${array_of_files_associated_with_pipeline}" >> "${WORKSPACE}/dsl_seed_jobs_to_build.txt"
}

function add_changed_dsl_jobs_to_jobs_to_build() {
    echo "INFO: Adding changed seed jobs and pipelines to DSL jobs to build file"
    for changed_file in ${changed_files[@]}; do
        if [[ "${array_of_files_associated_with_pipeline}" ==  *"${changed_file}"* ]]; then
            dsl_files_to_run="${dsl_files_to_run} ${changed_file}"
        fi
    done

    if [[ -n "${dsl_files_to_run}" ]]; then
        echo "${dsl_files_to_run}" >> "${WORKSPACE}/dsl_seed_jobs_to_build.txt"
    fi
}

########################
#     SCRIPT START     #
########################

array_of_files_associated_with_pipeline="${1}"
changed_files="${2}"

write_changed_dsl_files_to_workspace
