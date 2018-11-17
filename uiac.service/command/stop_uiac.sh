#!/bin/bash
source ~/.bash_profile
if [ -f ${UIAC_CONFIG_FILE} ]; then
  source ${UIAC_CONFIG_FILE}
fi

CURR_PATH=`pwd`
uiacRemoveCrontab()
{
  username=`whoami`
  crontab -l > /tmp/cronfile.${username}.1
  sed /uiacMonitor/d /tmp/cronfile.${username}.1 > /tmp/cronfile.${username}
  
  crontab -r
  
  crontab /tmp/cronfile.${username}
  rm -rf /tmp/cronfile.${username}*
}

uiacRemoveCrontab

stop_num=0
uiac_pid=$(ps -eo pid,cmd|grep -w xrk.uiac.service|grep -vw grep | sed -n '1p' | awk '{print $1;}')
if [ -z ${uiac_pid} ]; then
  uiac_pid=0
fi

while [ ${uiac_pid} -gt 0 ]; do
  echo "> Find uiac server thread id:${uiac_pid}"
  kill -9 ${uiac_pid}
  sleep 1

  uiac_pid=$(ps -eo pid,cmd|grep -w xrk.uiac.service|grep -vw grep | sed -n '1p' | awk '{print $1;}')
  if [ -z ${uiac_pid} ]; then
    uiac_pid=0
  fi
done

echo "> Stop uiac server successfully! "
