# panda-db
A gradle plugin built on top of dbdeploy.  

# How to Configure
Add the database driver that you want to use to your buildscript dependencies.  This is needed 
so the plugin can connect to your database. Below is an example of how you can configure the plugin

    buildscript {
        dependencies {
    ...
            // classpath dependency for the database is needed by the dbdeploy plugin
            classpath group: 'org.postgresql', name: 'postgresql', version: '42.1.4'
        }
    ...
    }

Apply the PandaDbPlugin plugin.  Below is an example of how to do this in your build script:
    * apply plugin: org.panda.PandaDbPlugin

# How to Use
Below is a list of the tasks that you can use.  These tasks are intended to help in supporting a database 
change management philosophy where all db changes are incremental and appended.

## Create a 'create-patch' Task
the 'create-patch' task does not connect to your database.  It is only a convenience task for creating properly 
constructed patch file names.  Below is the format of the patch file:
    * [epoch]-[uuid]-[desc].sql
    
The intention of the task to create a chronologically applicable sql file.  This ordering is leveraged by the 
'generate-sql' task.

You can configure two properties when using the create-patch task:
   * desc
   * dir

The 'desc' property is required and it is a small description that will be appended to your patch file.  
This is helpful for supplying context to your sql script file.

The 'dir' property identifies the directory that all of your patch files should live.  This property is defaulted to 
the './patch' directory.     
 
Below is an example of how you can configure this task in your build.gradle file:

    task createPatch(type: org.panda.CreatePatch) {
        if (project.hasProperty("desc")) {
            desc = project.getProperty("desc")
        }
    }

You can run this task using gradle as follows: 
    ./gradlew createPatch -Ddesc="create user table"



  
       
