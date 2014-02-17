#!/bin/bash
#determiner hostname
host=`hostname`

# Detection des serveurs
#Detection Apache
apache="faux"
ps -fe | grep -v grep | grep httpd >/dev/null 2>&1
if [ $? = 0 ]; then
	echo Apache detecte
	apache="vrai"
fi

jonas="faux"
ps -fe | grep -v grep | grep org.objectweb.jonas.server.Server >/dev/null 2>&1
if [ $? = 0 ]; then
	echo Jonas detecte
	jonas="vrai"
fi

jonas5="faux"
ps -fe | grep -v grep | grep org.ow2.jonas.commands.admin.ClientAdmin >/dev/null 2>&1
if [ $? = 0 ]; then
	echo Jonas5 detecte
	jonas5="vrai"
fi

weblogic="faux"
ps -fe | grep -v grep | grep weblogic.Server >/dev/null 2>&1
if [ $? = 0 ]; then
	echo Weblogic detecte
	weblogic="vrai"
fi

##************************************
## Analyse Weblogic
##************************************



cd /tmp
function determinerDomain
{
## tentative detection par ps et /proc
pids=` ps -fe | grep java | grep -v grep | grep weblogic.Server | awk '{print $2}' | tr '\n' ' '`
if [ "x$pids" != "x" ]; then
#	## On a trouve des pids
	echo recherche par pid
#	## On prend le premier pid
	pid=`echo $pids | awk '{print $1}'`
	domain=`ls -l /proc/$pid | grep cwd | grep -oEi '/.*'`
	domainDir=`expr "$domain" : '\([/a-zA-Z0-9_]*/\)'`
	domainName=`expr "$domain" : '.*/\([/a-zA-Z0-9_]*$\)'`
	 user=`ps -fe | grep java | grep -v grep | grep weblogic.Server |   grep $pid |   awk '{print $1}'` 
else
##	## on essaie de trouver domain.properties
	echo recherche fichier domain.properties
	propsDomain=`find / -name domain.properties| grep admin`
	domainDir=`cat $propsDomain | grep domaindir=| cut -d "=" -f2`
	domainName=`cat $propsDomain | grep domain=| cut -d "=" -f2`
	echo propsDomain=$propsDomain
fi
echo domainDir=$domainDir
echo domainName=$domainName
}
if [ $weblogic = "vrai" ]; then
	if [ -d /tmp/confWeblo ]; then
		rm -rf /tmp/confWeblo
	fi
	mkdir -p  /tmp/confWeblo/ 2>/dev/null
	user="unknown"
	determinerDomain
#	#recuperation du config.xml du domaine
	sep=/
	dir1=$domainDir$domainName
	dir=`echo ${dir1//$sep/+}`
	echo "dir=$dir"
	mkdir -p  /tmp/confWeblo/$dir/ 2>/dev/null
	configFile=$domainDir$domainName/config/config.xml
	listFiles=$domainDir$domainName/config/config.xml;
	cp -p $domainDir$domainName/config/config.xml /tmp/confWeblo/$dir/ 2>/dev/null
	if [ ! -f $domainDir$domainName/config/config.xml.BEFORETUNING ]; then
		cp -p   $domainDir$domainName/config/config.xml    $domainDir$domainName/config/config.xml.BEFORETUNING
		listFiles=$listFile$domainDir/$domainName/config/config.xml.BEFORETUNING";"
	fi
	cp -p  $domainDir$domainName/config/*.BEFORETUNING /tmp/confWeblo/$dir/  2>/dev/null
	cp -p $domainDir$domainName/bin/setDomainEnv.sh /tmp/confWeblo/$dir/  2>/dev/null
	listFiles=$listFiles$domainDir$domainName/bin/setDomainEnv.sh";"
	if [ ! -f $domainDir$domainName/bin/setDomainEnv.sh.BEFORETUNING ]; then
		cp -p     $domainDir$domainName/bin/setDomainEnv.sh   $domainDir$domainName/bin/setDomainEnv.sh.BEFORETUNING
	fi
    listFiles=$listFiles$domainDir$domainName/bin/setDomainEnv.sh.BEFORETUNING";"
	cp -p $domainDir$domainName/bin/*.BEFORETUNING /tmp/confWeblo/$dir/  2>/dev/null
	cp -p  $domainDir$domainName/config/jdbc/*.xml /tmp/confWeblo/$dir/  2>/dev/null
	listFiles=$listFiles`ls   $domainDir$domainName/config/jdbc/*.xml 2>/dev/null | tr '\n' ';'`
	listJDBCFiles=`ls   $domainDir$domainName/config/jdbc/*.xml 2>/dev/null | tr '\n' ' '`
## Recopie de tous les fichiers de conf en .BeforeTuning s'ils n'existent pas
	for file in $listJDBCFiles; do
	echo "file=$file"
		if [ ! -f ${file}.BEFORETUNING ]; then
		echo "copy de ${file}.BEFORETUNING"
			cp -p $file ${file}.BEFORETUNING 2>/dev/null
		fi
	
	done
	cp -p $domainDir$domainName/config/jdbc/*.BEFORETUNING /tmp/confWeblo/$dir/  2>/dev/null
	listFiles=$listFiles`ls   $domainDir$domainName/config/jdbc/*.BEFORETUNING 2>/dev/null | tr '\n' ';'`
	
  fileWebloXml=${host}_weblogicConfig.xml
  echo "<?xml version=\"1.0\" encoding=\"iso-8859-15\"?>" > ./${fileWebloXml}
	echo "<xml>" >>   ./${fileWebloXml}
	echo "<domainWeblogic " >>    ./${fileWebloXml}
	echo "user=\"$user\" "        >> ./${fileWebloXml}
	echo "domainConfig=\"$configFile\" " >> ./${fileWebloXml}
	echo "filesConfig=\"$listFiles\" >" >>  ./${fileWebloXml}
	echo " </domainWeblogic>" >>    ./${fileWebloXml}
	echo "</xml>" >>    ./${fileWebloXml}
	rm -rf ${host}_weblogicConfigs.zip 2>/dev/null
	zip -r  ${host}_weblogicConfigs ./confWeblo
	chmod 777 *weblogicConfigs.zip
	rm -rf  ./confWeblo 
fi


##************************************
## Analyse Apache
##************************************


creerBeforeTunningAndCopy()
{
# liste des fichiers a prefixe conf et vhost dans conf
	listeFichiersConf=`ls $1*.conf  $1*vhost 2>/dev/null`
   listeFichiersConfPointD=`ls  $1../conf.d/*.properties $1../conf.d/*.conf $1../conf.d/*.vhost 2>/dev/null`
    
    lstConf=`echo $listeFichiersConf | tr '\n' ';' |tr ' ' ';'`
    lstConfD=`echo $listeFichiersConfPointD | tr '\n' ';' |tr ' ' ';'`
    listeFichiers="$lstConf$lstConfD"
  for file in $listeFichiersConf; do
                
      if [ ! -f ${file}.BEFORETUNING ]; then
       
              cp -p   ${file}   ${file}.BEFORETUNING
      fi
      cp -p    ${file} /tmp/apacheConfigs/$2/conf/
      cp -p    ${file}.BEFORETUNING /tmp/apacheConfigs/$2/conf/
  done
        
    for file in $listeFichiersConfPointD; do
              echo file=$file
      if [ ! -f ${file}.BEFORETUNING ]; then
              cp -p   ${file}   ${file}.BEFORETUNING
      fi
      cp -p    ${file} /tmp/apacheConfigs/$2/conf.d/
      cp -p    ${file}.BEFORETUNING /tmp/apacheConfigs/$2/conf.d/
  done
    
    
}

detectermode()
{
modules=
version=
chaineALancer=`ps -fe | grep httpd | grep -v grep | grep -oEi '^[^ ]+[ ]+[0-9]+[ ]+1[ ].*$' | grep " "$1 | awk '{print $8}'`

version=`$chaineALancer -v |  tr '\n' ' '`
modules=`$chaineALancer -l | tr '\n' ' '`

}

if [ $apache = "vrai" ]; then
	cd /tmp
	
  fileApacheXml=${host}_apacheConfig.xml
	rm -rf  *apacheConfigs.zip *apacheConfig.xml apacheConfigs 2>/dev/null
#	## Trouver tous les Apaches rattachés au process PPID 1
	psAll=`ps -fewww | grep httpd | grep -v grep | grep  -oEi '^[a-z0-9]+[ ]+[0-9]+[ ]+1[ ]+.*$'`
#	## Chercher si on a un fichier de conf definit dans laligne de lancement de Apache
#	##ficConf=`ps -fewww | grep httpd | grep -v grep | grep  -oEi '^[a-z0-9]+[ ]+[0-9]+1[ ]+.*$' | grep -oEi '\-f[ ]+[/a-zA-Z0-9]+' | grep -oEi '[/a-zA-Z0-9]+$'` 
	echo psAll=$psAll
	pids=`ps -fewww | grep httpd | grep -v grep | grep  -oEi '^[a-z0-9]+[ ]+[0-9]+[ ]+1[ ]+.*$' | awk '{print $2}'`
	echo pids=$pids
	user=`whoami`
	echo user=$user
	sep=/
	echo "<?xml version=\"1.0\" encoding=\"iso-8859-15\"?>" > ./${fileApacheXml}
	echo "<xml>"  >> ./${fileApacheXml}
	for pid in $pids; do
		echo pid=$pid
		echo "<ApacheConfig " >>    ./${fileApacheXml}
#		## Chercher si on a un fichier de conf definit dans laligne de lancement de Apache
		listeFichiers=
		ficConf=` ps -fewww | grep httpd | grep -v grep| grep  $pid  | grep -E '^[^ ]+[ ]+[0-9]+[ ]+1[ ].+' |  grep -oEi '\-f[ ]+[^ ]+'| awk '{print $2}'` 
		echo ficConf=$ficConf
		dirConf=/etc/httpd/conf/
		if [ ! x$ficConf = x ]; then
#			## on  n est pas en conf par defaut 
#			## On extrait le repertoire contenant le fichier de conf httpd.conf ou équivalent
			dirConf=`expr "$ficConf" : '\(.*/\)'`
			mainFic=`expr "$ficConf" : '.*/\([a-zA-Z0-9\._]*$\)'`
		else
		
			mainFic=httpd.conf
		
		fi
	
		echo " mainConf=\"$dirConf$mainFic\" " >>   ./${fileApacheXml}
		dir=`echo ${dirConf//$sep/+}`
#		## JLP a terminer

		mkdir -p /tmp/apacheConfigs/$dir/conf
		mkdir -p /tmp/apacheConfigs/$dir/conf.d

#		## Copier les fichiers interessants et BEFORE TUNNING dans le repertoire cree
		echo dirConf=$dirConf
		echo dir=$dir
		creerBeforeTunningAndCopy $dirConf  $dir
		if [ x$ficConf = x ]; then
			detectermode " "
		else
			detectermode $ficConf
		fi
		echo " version=\"$version\" " >>    ./${fileApacheXml}	
		echo " modules=\"$modules\" " >>   ./${fileApacheXml}
#		## Detecter php.conf
		php=`echo $listeFichiers | grep "/php.conf;"`
		if [ $? = 0 ]; then
			phpIni=`find / -name php.ini 2>/dev/null `
			mkdir -p /tmp/apacheConfigs/$dir/php
			for ini in $phpIni;do
				dirIni=`expr "$ini" : '\([/a-zA-Z0-9_]*/\)'`
				ficIni=`expr "$ini" : '.*/\([a-zA-Z0-9\._]*$\)'`
				if [ ! -f $ini.BEFORETUNING ]; then
					cp -p $ini $ini.BEFORETUNING	
				fi
				sep=/
				dirIniPlus=`echo ${dirIni//$sep/+}`
#			echo dirIni=$dirIni
#			echo ficIni=$ficIni
#			echo dirIniPlus=$dirIniPlus
				cp -p $ini /tmp/apacheConfigs/$dir/php/${dirIniPlus}$ficIni
				cp -p $ini.BEFORETUNING  /tmp/apacheConfigs/$dir/php/${dirIniPlus}$ficIni.BEFORETUNING

				listeFichiers=${listeFichiers}$ini
			done
			echo "phpIni=\"$ini\" " >> ./${fileApacheXml}
	
		fi
		echo "fichiersConfig=\"$listeFichiers\" ">>    ./${fileApacheXml}
		echo " >" >>    ./${fileApacheXml}
		echo "</ApacheConfig>" >>    ./${fileApacheXml}
	done
	echo "</xml>"  >> ./${fileApacheXml}

#	## zipper le fichier
	cd /tmp
	rm -f *apacheConfigs.zip 2>/dev/null
	zip -r ${host}_apacheConfigs ./apacheConfigs
	rm -rf  apacheConfigs
fi


##************************************
## Analyse Jonas
##************************************

creerBeforeTunning()
{
	if [ ! -f $1/conf/server.xml.BEFORETUNING ]; then 
		#echo $1/conf/server.xml.BEFORETUNING n existe pas
		cp -p  $1/conf/server.xml  $1/conf/server.xml.BEFORETUNING
	fi
	if [ ! -f $1/conf/web.xml.BEFORETUNING ]; then 
#	#	echo $1/conf/web.xml.BEFORETUNING n existe pas
		cp -p  $1/conf/web.xml  $1/conf/web.xml.BEFORETUNING
	fi

	if [ ! -f $1/conf/jonas.properties.BEFORETUNING ]; then 
	#	echo $1/conf/jonas.properties.xml.BEFORETUNING n existe pas
		cp -p  $1/conf/jonas.properties  $1/conf/jonas.properties.BEFORETUNING
	fi
	if [ ! -f $1/conf/carol.properties.BEFORETUNING ]; then 
#		#echo $1/conf/carol.properties.xml.BEFORETUNING n existe pas
		cp -p  $1/conf/carol.properties  $1/conf/carol.properties.BEFORETUNING
	fi

	if [  -f $1/env.start.sh ]; then 
		if [ ! -f $1/env.start.sh.BEFORETUNING ]; then
			cp -p  $1/env.start.sh $1/env.start.sh.BEFORETUNING
		fi
	fi


}

copyfiles()
{
	
	cp -p $1/conf/web.xml  /tmp/jonasConfigs/$2/web.xml	
	cp -p $1/conf/server.xml  /tmp/jonasConfigs/$2/server.xml
	cp -p  $1/conf/*.properties  /tmp/jonasConfigs/$2/
	cp -p  $3/versions.properties  /tmp/jonasConfigs/$2/  2>/dev/null
	cp -p  $3/VERSIONS  /tmp/jonasConfigs/$2/versions.properties  2>/dev/null

##	copy des rars
 
	cp -p $1/rars/autoload/*.rar /tmp/jonasConfigs/$2/rars/autoload
	cp -p  $1/rars/*.rar /tmp/jonasConfigs/$2/rars
	rm -f /tmp/jonasConfigs/$2/rars/autoload/joram*.rar 2>/dev/null
	rm -f  /tmp/jonasConfigs/$2/rars/autoload/scout*.rar 2>/dev/null
	rm -f  /tmp/jonasConfigs/$2/rars/autoload/speedo*.rar 2>/dev/null
	rm -f /tmp/jonasConfigs/$2/rars/joram* 2>/dev/null
	rm -f  /tmp/jonasConfigs/$2/rars/scout*.rar 2>/dev/null
	rm -f  /tmp/jonasConfigs/$2/rars/speedo*.rar 2>/dev/null
## Copy des fichiers originaux s'ils existent
	
	beforetuning=
	if [  -f $1/env.start.sh ]; then 
		cp -p  $1/env.start.sh /tmp/jonasConfigs/$2/env.start.sh
		listeProperties="$1/env.start.sh;"
		if [ -f $1/env.start.sh.BEFORETUNING ]; then
			beforetuning=${beforetuning}${1}/env.start.sh.BEFORETUNING";"
			cp -p $1/env.start.sh.BEFORETUNING /tmp/jonasConfigs/$2/env.start.sh.BEFORETUNING
		fi
	fi
       cp  -p $1/conf/*.BEFORETUNING      /tmp/jonasConfigs/$2/
 
	   beforetuning=${beforetuning}`ls $1/conf/*.BEFORETUNING 2>/dev/null | tr '\n' ';'`
	 echo -n "listFiles=\"$1/conf/web.xml;$1/conf/server.xml;" >>    ./${fileJonasXml} 
	listeProperties=`ls $1/conf/*.properties 2>/dev/null | tr '\n' ';'`
	echo "$listeProperties\" " >>  ./${fileJonasXml} 

	listeRars=`ls  $1/rars/autoload/*.rar $1/rars/*.rar 2>/dev/null | tr '\n' ';' `
	echo  "listeRars=\"$listeRars\" ">> ./${fileJonasXml} 
 
	echo  "listFilesBEFORETUNING=\"$beforetuning\" ">>./${fileJonasXml} 
}

if [ $jonas = "vrai" ]; then
	rm -f *jonasConfig.xml 2>/dev/null
	rm -f *jonasConfigs.zip 2>/dev/null
  fileJonasXml=${host}_jonasConfig.xml

	echo "<?xml version=\"1.0\" encoding=\"iso-8859-15\"?>" > ./${fileJonasXml} 
	echo "<xml>" >> ./${fileJonasXml}  
##	## Trouver tous les Jonas_base
	psAll=`ps -fewww | grep java | grep -v grep | grep  -oEi '\-Djonas.base=[^ ]+'`
	echo psAll=$psAll
	user=`whoami`
	echo user=$user
	sep=/
	if [ -d /tmp/jonasConfigs ]; then
		rm -rf /tmp/jonasConfigs
	fi
	for jb in $psAll; do
		echo jb=$jb
		pathjb=`echo $jb | cut -d "=" -f2`
		echo pathjb=$pathjb
		jr=`ps -fewww | grep java | grep -v grep | grep $pathjb | grep  -oEi '\-Dinstall.root=[^ ]+'`
		echo jr=$jr
		pathjr=`echo $jr | cut -d "=" -f2`
		echo "<jonasBase" >>     ./${fileJonasXml}
		echo "jonasBase=\"$pathjb\" " >>   ./${fileJonasXml} 
		echo "jonasRoot=\"$pathjr\" "	 >>  ./${fileJonasXml} 
  
# Recherche des jars sous jonasbases
    listeJars=`find $pathjb -name *.jar | tr '\n' ';'`
	echo listeJars=$listeJars
     echo "listeJars=\"$listeJars\" " >>   ./${fileJonasXml} 
	
#r Recherche des war  sous jonasbases
    listeWars=`find $pathjb -name *.war  -type f| tr '\n' ';'`
	echo listeWars=$listeWars
     echo "listeWars=\"$listeWars\" " >>   ./${fileJonasXml} 
  
#r Recherche des ear  sous jonasbases
    listeEars=`find $pathjb -name *.ear  -type f| tr '\n' ';'`
	echo listeEars=$listeEars
     echo "listeEars=\"$listeEars\" " >>   ./${fileJonasXml} 


  	dir=`echo ${pathjb//$sep/+}`


		mkdir -p /tmp/jonasConfigs/$dir
		mkdir -p /tmp/jonasConfigs/$dir/rars
		mkdir -p /tmp/jonasConfigs/$dir/rars/autoload

##		##	 Trouver le user jonas
		userjonas=`ps -fewww | grep java | grep Djonas.base=${pathjb} | grep -oEi '^[^ ]+' `
		echo "userjonas=\"$userjonas\" ">>  ./${fileJonasXml} 

		echo pathjb=$pathjb
		echo dir=$dir

		if [ $user = root ] || [ $user = $userjonas ]; then
			creerBeforeTunning $pathjb

		fi

## 		Copier les fichiers interessants et BEFORE TUNNING dans le repertoire cree
		copyfiles $pathjb  $dir $pathjr
		echo " > " >>./${fileJonasXml} 
		echo "</jonasBase>" >>    ./${fileJonasXml} 
	done
	echo "</xml>" >> ./${fileJonasXml} 
## 	zipper le fichier
	cd /tmp  
	rm -f  *jonasConfigs.zip 2>/dev/null                           
	zip -r ${host}_jonasConfigs ./jonasConfigs
	rm -rf  jonasConfigs
	rm -rf configJonas.sh
fi


#########################
# Analyse Jonas 5
###########

creerBeforeTunningJonas5 ()
{
	if [ ! -f $1/conf/tomcat6-server.xml.BEFORETUNING ]; then 
		#echo $1/conf/server.xml.BEFORETUNING n existe pas
		cp -p  $1/conf/tomcat6-server.xml  $1/conf/tomcat6-server.xml.BEFORETUNING
	fi
	if [ ! -f $1/conf/tomcat6-web.xml.BEFORETUNING ]; then 
#	#	echo $1/conf/web.xml.BEFORETUNING n existe pas
		cp -p  $1/conf/tomcat6-web.xml  $1/conf/tomcat6-web.xml.BEFORETUNING
	fi
  if [ ! -f $1/conf/tomcat7-server.xml.BEFORETUNING ]; then 
		#echo $1/conf/server.xml.BEFORETUNING n existe pas
		cp -p  $1/conf/tomcat7-server.xml  $1/conf/tomcat7-server.xml.BEFORETUNING
	fi
	if [ ! -f $1/conf/tomcat7-web.xml.BEFORETUNING ]; then 
#	#	echo $1/conf/web.xml.BEFORETUNING n existe pas
		cp -p  $1/conf/tomcat7-web.xml  $1/conf/tomcat7-web.xml.BEFORETUNING
	fi
	if [ ! -f $1/conf/jonas.properties.BEFORETUNING ]; then 
	#	echo $1/conf/jonas.properties.xml.BEFORETUNING n existe pas
		cp -p  $1/conf/jonas.properties  $1/conf/jonas.properties.BEFORETUNING
	fi
	if [ ! -f $1/conf/carol.properties.BEFORETUNING ]; then 
#		#echo $1/conf/carol.properties.xml.BEFORETUNING n existe pas
		cp -p  $1/conf/carol.properties  $1/conf/carol.properties.BEFORETUNING
	fi
	if [ -f $1/conf/felix-config.properties ];then
			if [ ! -f $1/conf/felix-config.properties.BEFORETUNING ]; then 
#			#echo $1/conf/felix-config.properties.BEFORETUNING n existe pas
			cp -p  $1/conf/felix-config.properties  $1/conf/felix-config.properties.BEFORETUNING
		fi
	fi
## Fichier de configuration

	if [  -d $2/env-ft ]; then
## on cherche le fichier de conf avec le nom jonas.base	
		
		cd $2/env-ft
		
		fileConf=`find . -type f | grep $3 | grep -v BEFORETUNING | cut -c3-`
		
		if [ ! -f ${fileConf}.BEFORETUNING ]; then
			cp -p ${fileConf} ${fileConf}.BEFORETUNING
			
		fi
		
	fi


}

copyfilesJOnas5 ()
{

	
	cp -p $1/conf/tomcat6-web.xml  /tmp/jonas5Configs/$2/tomcat6-web.xml	
	cp -p $1/conf/tomcat6-server.xml  /tmp/jonas5Configs/$2/tomcat6-server.xml
	 cp -p $1/conf/tomcat7-web.xml  /tmp/jonas5Configs/$2/tomcat7-web.xml	
	cp -p $1/conf/tomcat7-server.xml  /tmp/jonas5Configs/$2/tomcat7-server.xml
  cp -p  $1/conf/*.properties  /tmp/jonas5Configs/$2/
	cp -p  $3/versions.properties  /tmp/jonas5Configs/$2/  2>/dev/null
	cp -p  $3/VERSIONS  /tmp/jonasConfigs/$2/versions.properties  2>/dev/null

##	copy des rars
 
	cp -p $1/deploy/*.rar /tmp/jonas5Configs/$2/deploy 2>/dev/null
	
## Copy des fichiers originaux s'ils existent
	
	beforetuning=
	listeProperties=
	if [ -d $3/env-ft ]; then 
		cd $3/env-ft
		
		fileConf2=`find . -type f | grep $4 | grep -v BEFORETUNING | cut -c3-`
		
		cp -p  $fileConf2 /tmp/jonas5Configs/$2/${fileConf2} 2>/dev/null
		cp -p  ${fileConf2}.BEFORETUNING /tmp/jonas5Configs/$2/${fileConf2}.BEFORETUNING 2>/dev/null
		listeProperties="$3/env-ft/${fileConf2};"
		beforetuning="$3/env-ft/${fileConf2}.BEFORETUNING;"
		
	fi
	
       cp  -p $1/conf/*.BEFORETUNING      /tmp/jonas5Configs/$2/
	
	   beforetuning=${beforetuning}`ls $1/conf/*.BEFORETUNING 2>/dev/null | tr '\n' ';'`
	
	echo -n "listFiles=\"$1/conf/tomcat6-web.xml;$1/conf/tomcat6-server.xml;$1/conf/tomcat7-web.xml;$1/conf/tomcat7-server.xml;" >>    /tmp/${fileJonas5Xml} 
	
	listeProperties=$listeProperties`ls $1/conf/*.properties 2>/dev/null | tr '\n' ';'`
	echo "$listeProperties\" " >>  /tmp/${fileJonas5Xml} 

	listeRars=`ls  $1/deploy/*.rar  2>/dev/null | tr '\n' ';' `
	echo  "listeRars=\"$listeRars\" ">> /tmp/${fileJonas5Xml} 
 
	echo  "listFilesBEFORETUNING=\"$beforetuning\" ">>/tmp/${fileJonas5Xml} 

}

if [ $jonas5 = "vrai" ]; then
	cd /tmp
	rm -f *jonas5Config.xml 2>/dev/null
	rm -f *jonas5Configs.zip 2>/dev/null
  fileJonas5Xml=${host}_jonas5Config.xml

	echo "<?xml version=\"1.0\" encoding=\"iso-8859-15\"?>" > /tmp/${fileJonas5Xml} 
	echo "<xml>" >> /tmp/${fileJonas5Xml}  
##	## Trouver tous les Jonas_base
	psAll=`ps -fewww | grep java | grep -v grep | grep  -oEi '\-Djonas.base=[^ ]+'`
	echo psAll=$psAll
	user=`whoami`
	echo user=$user
	sep=/
	if [ -d /tmp/jonas5Configs ]; then
		rm -rf /tmp/jonas5Configs
	fi
	for jb in $psAll; do
		echo jb=$jb
		pathjb=`echo $jb  | cut -d "=" -f2`
		echo pathjb=$pathjb
		jr=`ps -fewww | grep java | grep -v grep | grep $pathjb | grep  -oEi '\-Djonas.root=[^ ]+' |tr '\n' " "| cut -d " " -f1`
		## Jonas name
		jn=`ps -fewww | grep java | grep -v grep | grep $pathjb | grep  -oEi '\-Djonas.name=[^ ]+' |tr '\n' " " | cut -d " " -f1`
		
		echo jn=$jn
		if [ "$jn" == "" ]; then
			jn=jonas
		else
			jn=`echo $jn | cut -d "=" -f2`
		fi
		echo jr=$jr
		
		pathjr=`echo $jr | cut -d "=" -f2`
		echo "<jonasBase" >>     /tmp/${fileJonas5Xml}
		echo "jonasBase=\"$pathjb\" " >>   /tmp/${fileJonas5Xml} 
		echo "jonasRoot=\"$pathjr\" "	 >>  /tmp/${fileJonas5Xml} 
    # Recherche des jars sous jonasbases
    listeJars=`find $pathjb -name *.jar | tr '\n' ';'`
	echo listeJars=$listeJars
     echo "listeJars=\"$listeJars\" " >>   ./${fileJonas5Xml} 
	
#r Recherche des war  sous jonasbases
    listeWars=`find $pathjb -name *.war  -type f| tr '\n' ';'`
	echo listeWars=$listeWars
     echo "listeWars=\"$listeWars\" " >>   ./${fileJonas5Xml} 
  
#r Recherche des ear  sous jonasbases
    listeEars=`find $pathjb -name *.ear  -type f| tr '\n' ';'`
	echo listeEars=$listeEars
     echo "listeEars=\"$listeEars\" " >>   ./${fileJonas5Xml} 
	
		dir=`echo ${pathjb//$sep/+}`
		echo jnAfter=$jn
	
		mkdir -p /tmp/jonas5Configs/$dir
		mkdir -p /tmp/jonas5Configs/$dir/deploy
		

##		##	 Trouver le user jonas
		userjonas=`ps -fewww | grep java | grep Djonas.base=${pathjb} | grep -oEi '^[^ ]+' `
		echo "userjonas=\"$userjonas\" ">>  /tmp/${fileJonas5Xml} 

		echo pathjb=$pathjb
		echo dir=$dir

		if [ $user = root ] || [ $user = $userjonas ]; then
			creerBeforeTunningJonas5 $pathjb $pathjr $jn

		fi

## 		Copier les fichiers interessants et BEFORE TUNNING dans le repertoire cree
		copyfilesJOnas5  $pathjb  $dir $pathjr $jn
		echo " > " >>/tmp/${fileJonas5Xml} 
		echo "</jonasBase>" >>    /tmp/${fileJonas5Xml} 
	done
	echo "</xml>" >> /tmp/${fileJonas5Xml} 
## 	zipper le fichier
	cd /tmp  
	rm -f  *jonas5Configs.zip 2>/dev/null                           
	zip -r ${host}_jonas5Configs ./jonas5Configs
	rm -rf  jonas5Configs
	rm -rf config5Jonas.sh
fi


##************************************
## Analyse Systeme
##************************************

function ulimitUser
{
qui=`whoami`
echo "user=$qui "
ulimit -aS
}

function ulimitJonas
{
	# Declaration des variables
	utilisateurJonas=""
	listeFichiers1=""
	
	if [ "${qui}" = "root" ]
	then
		# Donc je suis root et je fais ce que je veux...
		# Trouve tous les utilsateurs et fait un tri unique
		utilisateurJonas=`ps -ef | grep java | grep -v grep | awk -F " " '{print $1}' | sort -u`
		if [ -n "${utilisateurJonas}" ]
		then
			# Faire le ulimit pour chaque VU
			for vu in ${utilisateurJonas}
			do
				echo "user=${vu}"
				su - ${vu} -c 'ulimit -aS'
				echo ""
			done
		fi
		# Trouve le type d'OS
		vOS=`uname -a | awk -F " " '{print $1}'`
		
		# Trouve le PID du processus JONAS si OS = Linux
		
		
		if [ "${vOS}" = "AIX" ]
		then
			echo "Ce script s'utilise seulement sur Linux"
		fi
		

		
	else
		echo "je suis pas root"
	fi
}
#tester si VMware
vmware="faux"
ps -fe | grep -v grep | grep -i vmware
if [ $? = 0 ]; then
	echo Vmware  detecte
	vmware="vrai"
fi

fileLinuxXml=${host}_linuxConfig.xml
echo "<?xml version=\"1.0\" encoding=\"iso-8859-15\"?>" > ./${fileLinuxXml}
echo "<xml>" >>./${fileLinuxXml}
echo "<LinuxConfig>" >> ./${fileLinuxXml}
echo "<Uname parser=\"uname\"><![CDATA[" >> ./${fileLinuxXml}
uname -a >> ./${fileLinuxXml}
echo "]]></Uname>" >> ./${fileLinuxXml}
echo " <Ulimit parser=\"ulimit\"><![CDATA[" >> ./${fileLinuxXml}
ulimitUser >>./${fileLinuxXml}
echo  "]]></Ulimit>" >> ./${fileLinuxXml}
echo " <Mpstat parser=\"mpstat\" vmware=\"$vmware\" ><![CDATA[" >> ./${fileLinuxXml}
mpstat -P ALL >> ./${fileLinuxXml}
echo  "]]></Mpstat>" >> ./${fileLinuxXml}
echo " <CpuInfo parser=\"cpuinfo\"><![CDATA[" >> ./${fileLinuxXml}
cat /proc/cpuinfo  >> ./${fileLinuxXml}
echo "]]></CpuInfo>" >>./${fileLinuxXml}
echo " <Memory parser=\"memory\"><![CDATA[" >> ./${fileLinuxXml}
free -mt >> ./${fileLinuxXml}
echo "]]></Memory>">> ./${fileLinuxXml}
echo " <Running parser=\"ps\"><![CDATA[" >> ./${fileLinuxXml}
ps -fewww >>./${fileLinuxXml}
echo "]]></Running>" >> ./${fileLinuxXml}

echo " <UlimitJonas parser=\"ulimitjonas\"><![CDATA[" >> ./${fileLinuxXml}
ulimitJonas >>./${fileLinuxXml}
echo  "]]></UlimitJonas>" >> ./${fileLinuxXml}

echo "</LinuxConfig>" >> ./${fileLinuxXml}
echo "</xml>" >>./${fileLinuxXml}
cd /tmp
chmod 777 *.zip *.xml
