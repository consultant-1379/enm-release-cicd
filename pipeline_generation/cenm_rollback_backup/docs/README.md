How to generate the cENM Rollback Backup pipeline:
-The first step is to create a Jenkins Job manually. The following page outlines how to do this:
  -https://confluence-oss.seli.wh.rnd.internal.ericsson.com/x/75izG
-This will Guide the cENM CI pipeline Backup/Restore investigation a "cENM Jobs" view
-The cENM_Backup job must be built by the user, passing in the necessary parameters as described in the job itself

What running this job will do:
-Running this job will generate the cENM Rollback Backup Pipeline and all the necessary jobs for use in the cENM  Backup pipeline
When ever this backup job triggers it will get Full back or Partial Backup
DEFAULT gives full backup
ROLLBACK gives partial backup

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

Running full cENM Rollback Backup Pipeline:
-Once the Bumblebee sections of the cENM Rollback Backup are generated, you should also generate the Misty sections of the cENM Backup by following Mistys release process
--For support in generating Mistys section of the e2e flow, contact Bumblebee and we will point you in the right direction