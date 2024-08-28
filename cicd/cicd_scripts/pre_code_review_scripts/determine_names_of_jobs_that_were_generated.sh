#!/bin/bash

function determine_changed_dsl_files() {
    echo "INFO: Adding names of generated jobs to file on workspace"
    generated_dsl_file_paths=$(cat < "${WORKSPACE}/dsl_seed_jobs_to_build.txt")

    for generated_dsl_file_path in ${generated_dsl_file_paths[@]}; do

        if [[ "${generated_dsl_file_path}" == *"View"* ]]; then
            echo "INFO: ${generated_dsl_file_path} is a view, skipping file..."
        else
            name_generated=$(cat "${generated_dsl_file_path}" | grep "jobBeingGeneratedName =")

            if [[ -z "${name_generated}" ]]; then
                name_generated=$(cat "${generated_dsl_file_path}" | grep "pipelineBeingGeneratedName =")
            fi

            if [[ -z "${name_generated}" ]]; then
                echo "ERROR: Unable to determine job or pipeline name generated"
                exit 1
            fi

            names_of_dsl_jobs_built="${names_of_dsl_jobs_built} $(echo ${name_generated} | cut -d \" -f 2)"
        fi
    done

    echo "${names_of_dsl_jobs_built}" >> "${WORKSPACE}/names_of_dsl_seed_jobs_that_were_built.txt"
    echo "INFO: Changed DSL files:"
    cat "${WORKSPACE}/names_of_dsl_seed_jobs_that_were_built.txt"
}

########################
#     SCRIPT START     #
########################

determine_changed_dsl_files
