#!/bin/bash

if [ $# -ne 4 ]; then
echo "Incorrect usage"
exit 0
fi

LocalProperties=$1
DatabaseName=$2
OutputFile=$3
Modules=$4

cat ${LocalProperties} | sed "s/{DATABASE}/${DatabaseName}/" | sed "s/{MODULES}/${Modules}/"> ${OutputFile}