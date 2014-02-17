<html>
swingScaViewer<br/>
==============<br/>
This tool transforms dated logs ( as http acces logs, Was logs, Log4J logs, JVM GC logs ...) into csv files.<br/>
It permits to visualize the corresponding Chart with the framework JFreeChart<br/>
<br/>
The whole binaries are in the archive swingScaViewer.zip<br/>
<br/>
Important :<br/>
To use this tool, you must know basics on Pattern Matching with regular expression (Perl regex for example).<br/>
At the end of the document, I give some examples of regex that are currently used in swingScaViewer.<br/><br/> 
The general mechanism used in this tool is to parse in 2 phases :<br/>
First phase : first regex extracts from a source that contains the interesting information in a result<br/>
Second  phase, if necessary : second regex extracts, from precedent result, the final information<br/>
This mechanism can handle almost all cases <br/>
<br/>
The product SwingScaViewer is a kind of workbench that groups several tools :<br/>
parsing dated  logs ( system logs, Web servers,WAS, application ...) and converting them into csv files<br/>
visualisation of csv files, or direct visualisation with certain types of logs ( JVM GC logs for example).
Integration of others tools like   AspectPerf( packaging AspectJ LTW Weaving for profiling Java application),<br/>
 JDBC Requests, statFilesAdvanced …<br/>
upload and download of files<br/>
Utilities tools => date <=> dateInMillis, aggregation of files, regex expression tester ...<br/>
</html>