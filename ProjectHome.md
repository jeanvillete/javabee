# JavaBee Org - Library Manager - Engine #
<br><br>
Have you ever thought why you should carry on all <b>dependencies</b> of your project when it is packaged?<br>
<br><br>
Have you ever asked yourself why your project is so <b>lightweight</b> but because of a lot of dependencies when it is packaged it is so <b>heavyweight</b>?<br>
<br><br>
<b>My bad experience: </b> I little time ago a did an application hosted as a Web Service and its source was just <b>115Kb</b>, but when packaged with all libraries, it jumps up to <b>30Mb</b>.<br>
<br>
If I need do a little bug fix or something like that, I would need package the app and upload the package to my web server, and it means that I would need upload <b>30Mb</b>, but I did a little change and my app source is so little.<br>
<br>
Tell me, does it make sense?<br>
<br><br>
If you have faced in any time the same situation, I would like to show you the <b>JavaBee Engine</b>.<br>
<br><br>
This is a little app to help you manage the libraries that usually are used by you.<br>
The app will keep safe all libraries, and you'll be able to move your app from anywhere to anywhere <b>in a lightweight way</b>, and pay attention, I said just your app.<br>
<br><br>
First of all don't confuse, it's not Apache Maven and doesn't the same job.<br>
<br><br>
Take a look, I think it can help you.<br>
<br><br>
All good and not so good suggestions are appreciated.<br>
<br><br>
Thanks and enjoy it!<br>
<br><br>
<pre><code>
me@mydomain:`/`$ javabee
JavaBee Org - Library Manager - Engine
command javabee -help
 -help[-h]                      show possible actions with its needed parameters
 -version[-v]                   show version of the current JavaBee
 -add                           add a new library to JavaBee manage
   -file                        the full file (library) address
   -name                        the name of the library in javabee`'`s context
   -version                     the version of the library
   [-dependencies]              the list id dependencies splitted by comma(,)
 -delete[-d]                    delete a library from the current JavaBee
   -id                          the desired id library to be deleted
 -update[-u]                    update info about some library
   -id                          the desired id library to be updated
   [-name]                      the new name of the library
   [-version]                   the new version of the library
   [-dependencies]              the new list id dependencies splitted by comma(,)
 -export                        export the current JavaBee`'`s state to jbs file
   [-ids]                       export just a desired set ids
   [-target_directory]          target directory to save the .jbs file
 -import                        import a JavaBee`'`s state from a .jbs file
   -file                        the target .jbs file
   [-override]                  override some library if it exists(true`/`1`/`yes`/`y or false`/`0`/`no`/`n)
 -list[-l]                      show the current stored libraries
   [-columns][-c]               choice select columns(id,name,version,filename)
   [-show_header][-sh]          list header(true`/`1`/`yes`/`y or false`/`0`/`no`/`n)
   [-show_dependencies][-sd]    list dependencies(true`/`1`/`yes`/`y or false`/`0`/`no`/`n)
   [-sort_column][-sc]          order by column ASC(id,name,version,filename)
   [-sort_size][-sz]            show size at the end(true`/`1`/`yes`/`y or false`/`0`/`no`/`n)
 -libraries                     mount a directory with all desired libraries
   -ids                         a set with all desired id libraries
   -target_directory            the target diretory to copy the desired libraries
   [-manage_dependencies][-md]  inject or not dependencies(true`/`1`/`yes`/`y or false`/`0`/`no`/`n)
 -mount                         mount a file .jbf (JavaBee File) from a completed application file
   -file                        the current completed and compressed file address
 -unmount                       return the .jbf (JavaBee File) to application`'`s properly state
   -file                        a .jbf valid file address
   [-to]                        the target directory to the application compressed file
 -app_descriptor                command to generate or update a javabee.desc.ssd file
   [-show]                      just show the state of the javabee.desc.ssd file
   [-app_name]                  the application name to javabee.desc.ssd file
   [-extract_name]              the desired file name when extracted from .jbf (JavaBee File)
   [-target_directory]          a managed target directory that holds libraries
   [-inject_dependency]         inject or don`'`t dependencies to this target_directory
   [-set_id]                    the set id dependencies that will be place in this target_directoy
   [-selective_removing]        the name of the files that will be removed from this target_directory
   [-remove_target]             parameter to remove the current target_directory from the javabee.desc.ssd file

</code></pre>