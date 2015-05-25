#!/bin/bash
echo config.sh : Copy Android Device Driver and Authorization Setting
sudo chmod 755 ./bin/*
sudo cp ./bin/51-android.rules /etc/udev/rules.d/51-android.rules
sudo cp ./bin/adb /usr/bin/adb


