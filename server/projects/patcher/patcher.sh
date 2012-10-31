#!/bin/bash
err=0

# Determine how this script was called
SCRIPT_DIR=`dirname $0`
if [ ${SCRIPT_DIR:0:1} == '/' ]; then
# Called with an absolute path
ABS_SCRIPT_DIR=${SCRIPT_DIR}
else
# Called with a relative path, compute the absolute path.
CWD=`pwd`
ABS_SCRIPT_DIR="${CWD}/${SCRIPT_DIR}"
fi

# Workaround the hard-coded path to META-INF & friends:
cd ${ABS_SCRIPT_DIR}


ROOT_DIR=`pwd`
CONFIG_FILE="META-INF/local.properties"

function patch {
echo "Running database patcher..."
pushd ${ROOT_DIR}

THIRDPARTY=${ABS_SCRIPT_DIR}/lib/*
PATCHER_JAR=${ABS_SCRIPT_DIR}/patcher.jar
CLASSPATH="${PATCHER_JAR}:${THIRDPARTY}"

java -server -ea -Xmx2000M -cp ${CLASSPATH} com.kikbak.patcher.Patcher ${CONFIG_FILE}
err=$?

if [ $err -ne 0 ]; then
echo "ERROR - Database patch failed in ${ROOT_DIR} with status $err"
popd
exit 1
fi

popd
}


case $1 in

patch)
patch
;;

*)
echo "Usage: $0 (patch)"
esac
