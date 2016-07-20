#!/bin/sh

#
# Environment config
#
CF_ANDROID_SDK_ROOT="${HOME}/local/opt/android-sdk"
CF_ANDROID_NDK_ROOT="${HOME}/local/opt/android-ndk"


#
# Load project config
#
if [ ! -f "./project.conf" ]; then
  echo "Failed to load project.conf" 1>&2
  exit 1
fi
. ./project.conf


#
# Generate local project files
#
build_local ()
{
    ${CF_ANDROID_SDK_ROOT}/tools/android update project --name ${PROJ_APPNAME} --target ${PROJ_TARGET} --path .
}
clean_local ()
{
    rm -f build.xml local.properties project.properties proguard-project.txt
}


#
# Generate bindings for compiled code
#
build_swig ()
{
    mkdir -p "./jni"
    mkdir -p "./src/${PROJ_APPNAME}"

    swig -java -c++ -o "jni/${PROJ_APPNAME}_wrap.cpp" -outdir "./src/${PROJ_APPNAME}" -package "${PROJ_PACKAGE}" ./jni/swig.i
}
clean_swig ()
{
    rm -f ./src/${PROJ_APPNAME}/*.java
    rm -f ./jni/myproject_wrap.cpp
}


#
# Compile native binary code
#
build_ndk ()
{
    ${CF_ANDROID_NDK_ROOT}/ndk-build
}
clean_ndk ()
{
    rm -rf ./libs
    rm -rf ./obj
}


#
# Build APK file for device
#
build_ant ()
{
    ant debug
}
clean_ant ()
{
    ant clean
}



command="${1}"; shift
case ${command} in
    local)
	build_local
	;;
    swig)
	build_swig
        ;;
    ndk)
	build_ndk
        ;;
    build)
	build_ant
	;;
    clean)
	clean_ant
	;;
    full-build)
	build_local
	build_swig
	build_ndk
	build_ant
	;;
    full-clean)
	clean_ant
	clean_ndk
	clean_swig
	clean_local
	;;
    install)
	if [ -n "${1}" ]; then
	    ${CF_ANDROID_SDK_ROOT}/platform-tools/adb -s "emulator-${1}" install -r ./bin/${PROJ_APPNAME}-debug.apk
	else
	    ${CF_ANDROID_SDK_ROOT}/platform-tools/adb install -r ./bin/${PROJ_APPNAME}-debug.apk
	fi
	;;
    create)
	${CF_ANDROID_SDK_ROOT}/tools/android create project --name ${PROJ_APPNAME} --path . --target ${PROJ_TARGET} --package ${PROJ_PACKAGE} --activity MainActivity
	;;
    update)
	${CF_ANDROID_SDK_ROOT}/tools/android update project --name ${PROJ_APPNAME} --target ${PROJ_TARGET} --path .
	;;
    debug)
	${CF_ANDROID_SDK_ROOT}/platform-tools/adb logcat
	;;
esac

