# panda-db
A gradle plugin built on top of dbdeploy.  

# How to Configure
Add the database driver that you want to use to your buildscript dependencies.  This is needed 
so the plugin can connect to your database. Below is an example of how you can configure the plugin

    buildscript {
        dependencies {
    ...
            // classpath dependency for the database is needed by the dbdeploy plugin
            // this example shows the plugin being configured for use with postgres
            classpath group: 'org.postgresql', name: 'postgresql', version: '42.1.4'
        }
    ...
    }

Apply the PandaDbPlugin plugin.  Below is an example of how to do this in your gradle.build script:
    * apply plugin: org.panda.PandaDbPlugin

# How to Use
Below is a list of the tasks that you can use.  These tasks are intended to help in supporting a database 
change management philosophy where all db changes are incremental and appended.

## Create a 'CreatePatch' Task
the 'CreatePatch' task does not connect to your database.  It is a convenience task for creating properly 
constructed patch file names.  Below is the format of the patch file:
    * [epoch in ms]-[two letter random string]-[desc].sql
    
The intention of the task to create chronologically ordered sql files.  This ordering is leveraged by the 
'GenerateSql' task.

You can configure two properties when using the create-patch task:
   * desc
   * dir (default: './patch')

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
    
<b>Note</b>: You can add the dir property to the task shown above if you want to change the patch directory from it's 
default value of './patch'

You can run the below task, as follows: 
    ./gradlew createPatch -Ddesc="create user table"

## Create a 'GenerateSql' task
The 'GenerateSql' task connects to the database so that it can figure out what changes have already been applied.  
It then creates a sql file that has all of the sql patches not already applied.

The sql file created can be run into the database to bring it up to date.

Below is a list of the properties that can be used to configure the GenerateSql task:
  * driver (default: "org.postgresql.Driver")
  * url (default: "jdbc:postgresql://127.0.0.1/test")
  * user (default: "test")
  * password (default: "test")
  * patchDir (default: "patch")
  * outputFile (default: "output.sql")
  * undoOutputFile (default: "undo.sql") 
  * dbms (default: "pgsql")
  * createChangelog (default: false)  

Most of the above mentioned properties are self explanatory.  The 'createChangelog' property is one that if set
to true will create a changelog table, if one does not exist, as the first patch applied to the database.
       
