#!/bin/bash 
project=$1
env=$2
version=$3

PROJECT_HOME=/opt/deploy/$project

mkdir -p $PROJECT_HOME/release
mkdir -p $PROJECT_HOME/shared

if [ -f "$project-$env-$version.tar.gz" ]; then

  # 假如已有版本在运行，先停止
  if [ -L "$PROJECT_HOME/current" ]; then
    $PROJECT_HOME/current/bootstrap.sh stop
  fi

  # rm -f $PROJECT_HOME/current/log
  rm -f $PROJECT_HOME/current

  if [ -d "$PROJECT_HOME/release/$project-$env-$version"]; then
    rm -rf $PROJECT_HOME/release/$project-$env-$version
    echo "这个版本的程序已存在，强制删除！"
  fi

  if [ -d "$PROJECT_HOME/shared/pids" ]; then
    rm -rf $PROJECT_HOME/shared/pids
  fi
  if [ -d "$PROJECT_HOME/shared/log" ]; then
    rm -rf $PROJECT_HOME/shared/log
  fi

  mkdir -p $PROJECT_HOME/shared/pids
  # mkdir -p $PROJECT_HOME/shared/log

  tar -xzvf $project-$env-$version.tar.gz -C $PROJECT_HOME/release/
  cp $PROJECT_HOME/release/$project-$env-$version/bin/bootstrap.sh $PROJECT_HOME/release/$project-$env-$version/bootstrap.sh
  chmod 777 $PROJECT_HOME/release/$project-$env-$version/bootstrap.sh
  chmod 777 $PROJECT_HOME/release/$project-$env-$version/jre/bin/java
  mkdir -p $PROJECT_HOME/release/$project-$env-$version/shared/log

  ln -s $PROJECT_HOME/release/$project-$env-$version/ $PROJECT_HOME/current
  ln -s $PROJECT_HOME/release/$project-$env-$version/shared/log/ $PROJECT_HOME/shared/log

  # 暂时注释启动命令
  # $PROJECT_HOME/current/bin/bootstrap.sh restart
else
  echo "不存在$project-$env-$version.tar.gz"
fi

