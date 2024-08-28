Using Repo for MTV cENM Deployment Setup and Network & workload Flow Job Generation for use in the end to end Initial Install flow:
-In order to generate all Bumblebee jobs in this repo associated with the MTV cENM Install pipeline, create a Jenkins job which mirrors the following job
  -https://fem4s11-eiffel004.eiffel.gic.ericsson.se:8443/jenkins/job/MTV_cENM_Initial_Builder/
-As a note, ensure in the "Process Job DSLs" section, under advanced settings, "DSL" is set as the "Additional classpath"
-This will create the MTV_cENM_Initial_Install_Pipeline_Generator job under a "cENM Jobs" tab
-The MTV_cENM_Initial_Install_Pipeline_Generator job must be built by the user, passing in the necessary parameters as described in the job itself

What running this job will do:
-Running this job will generate the necessary MTV Deployment Setup and NW & WL Jenkins pipelines and jobs for use in the install pipeline
-It will generate a cENM Install jenkins pipeline that handles Bumblebee logic for the II but will not generate the cENM install job itself as this will be handled by team Misty and will be generated separately  
-It will also generate the job MTV_cENM_Initial_Install_Pipeline_Updater, which looks at gerrit for DSL changes. Any jobs associated with these flows which have been modified from a DSL perspective, will have their jobs automatically updated

Required Plugins on Jenkins instance to run our DSL scripts:
-Some of our DSL calls specific Jenkins plugins. These are likely on most Jenkins instances but here is a list of plugins that are required
-Note, where versions are specified, the versions detailed are the minimum supported versions
  -taf-trigger (1.0.47): Is required in order to execute a TAF TE build and generate jobs which execute a TAF TE build
  -build-name-setter: Is required to set build names which is a convention of Bumblebee jobs
  -credentials: Is necessary for restricting jobs to the appropriate parties
  -description setter plugin: Allows us to set the description for each build which is a convention of Bumblebee jobs
  -Environment Injector: Makes it possible to set environment variables in our generated jobs
  -Export Parameters: Allows us to export parameters provided by Jenkins to a file, which is necessary for our generated jobs
  -Git: Various git plugins are needed for pulling repo's from Jenkinsfiles etc
  -Job DSL (1.68): Needed to actually generate Bumblebee jobs
  -Mailer: Allows us to email necessary parties on job failures etc.
  -Pipeline (2.5): Suite of plugins which allow us to run Jenkins pipelines
  -Pipeline Basic Steps (2.8.1): Used for Jenkins pipelines
  -Pipeline Build Step (2.7): Used for Jenkins pipelines
  -Pipeline Declarative (1.2.7): Used for Jenkins pipelines

Running full MTV cENM Install Flow:
-Once the Bumblebee sections of the MTV cENM Install are generated, you should also generate the Misty sections of the cENM Install by following Mistys release process
--For support in generating Mistys section of the e2e flow, contact Bumblebee and we will point you in the right direction
-The sections of flow should then be connected/orchestrated via spinnaker using the following Bumblebee documentation: https://confluence-oss.seli.wh.rnd.internal.ericsson.com/x/QBUHFg