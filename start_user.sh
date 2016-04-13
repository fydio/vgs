#!/bin/bash

killall java
#Param1: jobDuration
#Param2: user ID
#Param3: flag indicating type of simulation (regular, overload or offload).
#Param4 (optional): number of jobs per RM.
java -Djava.rmi.server.hostname=172.31.28.4 -Djava.net.preferIPv4Stack=true -Djava.security.manager -Djava.security.policy=./my.policy -jar User.jar 2000 0 regular 500 &
java -Djava.rmi.server.hostname=172.31.28.4 -Djava.net.preferIPv4Stack=true -Djava.security.manager -Djava.security.policy=./my.policy -jar User.jar 2000 1 regular 500 &
#java -Djava.rmi.server.hostname=172.31.28.4 -Djava.net.preferIPv4Stack=true -Djava.security.manager -Djava.security.policy=./my.policy -jar User.jar 2000 2 250 &
#java -Djava.rmi.server.hostname=172.31.28.4 -Djava.net.preferIPv4Stack=true -Djava.security.manager -Djava.security.policy=./my.policy -jar User.jar 2000 3 250 &
