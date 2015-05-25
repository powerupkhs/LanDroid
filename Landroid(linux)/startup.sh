#!/bin/bash
echo startup.sh : Initialize IP Address and Launching Landroid
sudo ifconfig eth0 211.189.127.37 
sudo route add default gw 211.189.127.1 dev eth0 
cd ~/landroid && ./landroid


