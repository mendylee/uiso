#!/bin/bash

usage() {
 echo "Usage: build.sh [pkg] [uiac]"
 exit 1
}

PROJDIR=`pwd`
INSTALL_DIR="install"
VERSION="V1.0"
BUILD_SUCC=0

function build_uiac(){
 echo "===============================================================================";
 cd $PROJDIR 
 
 echo "> Get last version"
 git pull origin develop
 echo "> Get last version finished."
  
 echo "> Build UIAC service ...";
 third-party/apache-maven-3.3.3/bin/mvn clean install package -DskipTests | tee build.out
 echo "> Build uiac package finished."
 echo "===============================================================================";

 BUILD_SUCC=`grep "BUILD SUCCESS" build.out | wc -l`
}

function buildpkg(){
  if [ ${BUILD_SUCC} -ne 1 ]; then
     echo "> compiler wrong, please check error and rebuild!"
     return
  fi

  echo "> Build install package ...";
  cd $PROJDIR
  
  if [ -d ${INSTALL_DIR} ]; then
    rm -rf ${INSTALL_DIR}
  fi 
  #create install folder
  mkdir -p ${INSTALL_DIR}
  mkdir -p ${INSTALL_DIR}/bin
  mkdir -p ${INSTALL_DIR}/jre
  mkdir -p ${INSTALL_DIR}/conf
  mkdir -p ${INSTALL_DIR}/lib
  mkdir -p ${INSTALL_DIR}/pg
  
  #generate version file
  generateVersionFile  
  
  #uiac op bin scripts  
  if [ -e build_tool ]; then
    cp -rf build_tool/* ${INSTALL_DIR}/bin
  fi
  
  #copy jre runtime
  cp -rf third-party/jre1.8.0_45/* ${INSTALL_DIR}/jre
  #copy postgresql tools
  cp -rf third-party/pgsql9.3.5/* ${INSTALL_DIR}/pg
  
  #copy uiac build file
  cp -rf uiac.service/target/conf/* ${INSTALL_DIR}/conf
  cp -rf uiac.service/target/lib/* ${INSTALL_DIR}/lib
  cp -rf uiac.service/target/conf/* ${INSTALL_DIR}/conf
  cp -rf uiac.service/target/*.jar ${INSTALL_DIR}
  cp -rf uiac.service/target/*.sh ${INSTALL_DIR}  
  cp documentation/db/*.sql ${INSTALL_DIR}
 
  #remove .git directory
  cd $PROJDIR
  find ${INSTALL_DIR}/ |grep .git | xargs rm -rf

  chmod +x ${INSTALL_DIR}/start_uiac.sh
  chmod +x ${INSTALL_DIR}/stop_uiac.sh
  chmod +x ${INSTALL_DIR}/uiacMonitor.sh
  
  echo "> Build install package completed.";
}

function generateVersionFile(){

  #gitUiacPath=http://192.168.9.18:4000/xbase/uiac.git

  cd ${INSTALL_DIR}

  if [ -f UIACVersion.txt ]; then
     rm -f UIACVersion.txt
  fi

  echo 'UIAC service version info:' >> UIACVersion.txt
  echo -n ' branches= ' >> UIACVersion.txt
  #svn info $gitUiacPath | grep Path |cut -d : -f2 >> UIACVersion.txt
  git branch | grep \* | cut -b 3- >> UIACVersion.txt
  echo -n ' revision =' >> UIACVersion.txt
  #svn info $gitUiacPath | grep Revision |cut -d : -f2 >> UIACVersion.txt
  git show-ref | grep heads/develop | cut -d ' ' -f1 >> UIACVersion.txt
  echo -n ' date = ' >> UIACVersion.txt
  date +"%Y%m%d" >> UIACVersion.txt
  
  cd ..
}

function mkInstallPkg(){
  if [ ${BUILD_SUCC} -ne 1 ]; then
     echo "> compiler wrong, please check error and rebuild!"
     return
  fi

  cd $PROJDIR
  packageName=$1
  selfPackageName=$2 
  
  cat build_tool/inst_script.sh > ${selfPackageName}
  echo "__VER_INFO_START__" >> ${selfPackageName}
  cat ${INSTALL_DIR}/UIACVersion.txt >> ${selfPackageName}
  echo "__ARCHIVE_BELOW__" >> ${selfPackageName}
  cat  $packageName  >> ${selfPackageName}
  
  chmod +x ${selfPackageName}
  if [ ! -d packages ]; then
	  mkdir packages
  fi
  
  rm -rf ${INSTALL_DIR}/UIACVersion.txt
  mv ${selfPackageName} packages/${selfPackageName}
}

function buildinstall(){

  if [ ${BUILD_SUCC} -ne 1 ]; then
     echo "> compiler wrong, please check error and rebuild!"
     return
  fi
  
  cd $PROJDIR
 
  if [ -e ${INSTALL_DIR}/jre ]; then
  
  echo "> Make the self-extract install package for UIAC Server ...";
  dataTime=`date +"%Y%m%d"`
  packageName="uiacInstall_${VERSION}_${dataTime}.tar.bz"
  selfextpkg="uiacInstall_${VERSION}_${dataTime}.sh"
  
  if [ -e ${packageName} ]; then
    rm -f ${packageName}
  fi
  
  if [ -e ${selfextpkg} ]; then
    rm -f ${selfextpkg}
  fi
  
  tar -jcvf ${packageName} ${INSTALL_DIR}
  	
  if [ -e ${packageName} ]; then
  
    echo "> The self-extract install package made completed !";
    mkInstallPkg ${packageName} $selfextpkg
    rm -f ${packageName}
  
  fi  
 else
  echo "> Error: The package number is not enough, please check."
 fi
 
 #rm -rf ${INSTALL_DIR}
}

if [ "${1}" = "pkg" ]; then 
  build_uiac   
  buildpkg
elif [ "${1}" = "uiac" ]; then
  build_uiac
else
  build_uiac
  buildpkg
  buildinstall
fi

if [ -e build.out ]; then
  rm -f build.out
fi
