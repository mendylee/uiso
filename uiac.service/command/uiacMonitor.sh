#!/bin/bash
source ~/.bash_profile

if [ -f ${UIAC_CONFIG_FILE} ]; then
  source ${UIAC_CONFIG_FILE}
fi

PROCNAME_LIST="xrk.uiac.service"
for PROCNAME in $PROCNAME_LIST;
do
let PROCNUM=$(ps -eo uid,pid,cmd|grep -w ${PROCNAME}|grep -vw grep |wc -l)
if [ "$PROCNUM" -le "0" ]; then
  case  ${PROCNAME} in
    "xrk.uiac.service") start_uiac.sh;;
  esac
else
  echo "> $PROCNAME already start, do nothing ... "
fi
done



