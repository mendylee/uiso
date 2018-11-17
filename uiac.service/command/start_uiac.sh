#!/bin/bash
source ~/.bash_profile
if [ -f ${UIAC_CONFIG_FILE} ]; then
  source ${UIAC_CONFIG_FILE}
fi

CURR_PATH=`pwd`

function uiacUpdateCrontab()
{
  #delete old config
  username=`whoami`
  crontab -l > /tmp/uiaccronfile.${username}.1
  sed /uiacMonitor/d /tmp/uiaccronfile.${username}.1 > /tmp/uiaccronfile.${username}
  crontab -r
  
  echo "* * * * * bash ${UIAC_INSTALL_PATH}/uiacMonitor.sh >/dev/null 2>&1" >> /tmp/uiaccronfile.${username}
  
  crontab /tmp/uiaccronfile.${username}
  rm -rf /tmp/uiaccronfile.${username}*
}

cd ${UIAC_INSTALL_PATH}
PARAMER=-Dfile.encoding=utf-8
JAVA_MEM='-Xmx2048m -Xms1024m -Xmn712m'
JAVA_HOME=jre
JAVA_FILE=xrk.uiac.service-1.0-SNAPSHOT.jar

if [ ! -x ${JAVA_HOME}/bin/java ]; then
    chmod +x ${JAVA_HOME}/bin/*
fi

let uiac_start_num=0
let MAX_TRY_COUNT=3
uiac_pid=`ps -eo pid,cmd | grep xrk.uiac.service | grep java | grep -v grep | sed -n '1p' | awk '{print $1}'`

if [ ! -z ${uiac_pid} ]; then
  echo "> UIAC server process already started, please stop it first!"
  exit 0
fi

while [ -z ${uiac_pid} ]; do
  ${JAVA_HOME}/bin/java ${PARAMER} ${JAVA_MEM} -jar ${JAVA_FILE} >/dev/null 2>&1 &
  sleep 1
  
  uiac_pid=`ps -eo pid,cmd | grep xrk.uiac.service | grep java | grep -v grep | sed -n '1p' | awk '{print $1}'`
  let uiac_start_num=$((${uiac_start_num} + 1))
  
  if [ ! -z ${uiac_pid} ]; then
    echo "> Start uiac server process successfully!"
  else
    echo "> Start uiac server process failure, try ${uiac_start_num} already !"
    if [ ${uiac_start_num} -gt ${MAX_TRY_COUNT} ]; then
      echo "> Exceed the ${MAX_TRY_COUNT} times, start  failure!"
      break;
    fi
  fi

done

uiacUpdateCrontab

cd ${CURR_PATH}
