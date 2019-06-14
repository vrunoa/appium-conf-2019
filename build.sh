#!/bin/bash

adb_repl() {
    pushd appium-adb
    npm link
    popd
    pushd appium-adb-repl
    npm link appium-adb
    popd
}

appium() {
    pushd appium-adb
    npm link
    popd
    pushd appium-android-driver
    npm link appium-adb
    npm link
    popd
    pushd appium-base-driver
    npm link
    popd
    pushd appium
    npm link appium-adb
    npm link appium-android-driver
    npm link appium-base-driver
    popd
    pushd wd
    npm link
    popd
    npm link wd
}

while :
do
    case "$1" in
        adb_repl)
            adb_repl;
            exit 0
            ;;
        appium)
            appium;
            exit 0
            ;;
        *)
            help
            break
            ;;
    esac
done