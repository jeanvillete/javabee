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
<pre><code><br>
me@mydomain:`/`$ javabee<br>
JavaBee Org - Library Manager - Engine<br>
command javabee -help<br>
 -help[-h]                      show possible actions with its needed parameters<br>
 -version[-v]                   show version of the current JavaBee<br>
 -add                           add a new library to JavaBee manage<br>
   -file                        the full file (library) address<br>
   -name                        the name of the library in javabee`'`s context<br>
   -version                     the version of the library<br>
   [-dependencies]              the list id dependencies splitted by comma(,)<br>
 -delete[-d]                    delete a library from the current JavaBee<br>
   -id                          the desired id library to be deleted<br>
 -update[-u]                    update info about some library<br>
   -id                          the desired id library to be updated<br>
   [-name]                      the new name of the library<br>
   [-version]                   the new version of the library<br>
   [-dependencies]              the new list id dependencies splitted by comma(,)<br>
 -export                        export the current JavaBee`'`s state to jbs file<br>
   [-ids]                       export just a desired set ids<br>
   [-target_directory]          target directory to save the .jbs file<br>
 -import                        import a JavaBee`'`s state from a .jbs file<br>
   -file                        the target .jbs file<br>
   [-override]                  override some library if it exists(true`/`1`/`yes`/`y or false`/`0`/`no`/`n)<br>
 -list[-l]                      show the current stored libraries<br>
   [-columns][-c]               choice select columns(id,name,version,filename)<br>
   [-show_header][-sh]          list header(true`/`1`/`yes`/`y or false`/`0`/`no`/`n)<br>
   [-show_dependencies][-sd]    list dependencies(true`/`1`/`yes`/`y or false`/`0`/`no`/`n)<br>
   [-sort_column][-sc]          order by column ASC(id,name,version,filename)<br>
   [-sort_size][-sz]            show size at the end(true`/`1`/`yes`/`y or false`/`0`/`no`/`n)<br>
 -libraries                     mount a directory with all desired libraries<br>
   -ids                         a set with all desired id libraries<br>
   -target_directory            the target diretory to copy the desired libraries<br>
   [-manage_dependencies][-md]  inject or not dependencies(true`/`1`/`yes`/`y or false`/`0`/`no`/`n)<br>
 -mount                         mount a file .jbf (JavaBee File) from a completed application file<br>
   -file                        the current completed and compressed file address<br>
 -unmount                       return the .jbf (JavaBee File) to application`'`s properly state<br>
   -file                        a .jbf valid file address<br>
   [-to]                        the target directory to the application compressed file<br>
 -app_descriptor                command to generate or update a javabee.desc.ssd file<br>
   [-show]                      just show the state of the javabee.desc.ssd file<br>
   [-app_name]                  the application name to javabee.desc.ssd file<br>
   [-extract_name]              the desired file name when extracted from .jbf (JavaBee File)<br>
   [-target_directory]          a managed target directory that holds libraries<br>
   [-inject_dependency]         inject or don`'`t dependencies to this target_directory<br>
   [-set_id]                    the set id dependencies that will be place in this target_directoy<br>
   [-selective_removing]        the name of the files that will be removed from this target_directory<br>
   [-remove_target]             parameter to remove the current target_directory from the javabee.desc.ssd file<br>
<br>
</code></pre>