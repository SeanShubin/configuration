#!/usr/bin/env bash

java -jar app/target/configuration-app.jar app/target/sample.json set string  hello sampleSet string
java -jar app/target/configuration-app.jar app/target/sample.json set byte    123   sampleSet byte
java -jar app/target/configuration-app.jar app/target/sample.json set short   234   sampleSet short
java -jar app/target/configuration-app.jar app/target/sample.json set int     345   sampleSet int
java -jar app/target/configuration-app.jar app/target/sample.json set long    567   sampleSet long
java -jar app/target/configuration-app.jar app/target/sample.json set float   1.23  sampleSet float
java -jar app/target/configuration-app.jar app/target/sample.json set double  2.34  sampleSet double
java -jar app/target/configuration-app.jar app/target/sample.json set char    a     sampleSet char
java -jar app/target/configuration-app.jar app/target/sample.json set boolean true  sampleSet boolean

java -jar app/target/configuration-app.jar app/target/sample.json get string  hello sampleGet string
java -jar app/target/configuration-app.jar app/target/sample.json get byte    123   sampleGet byte
java -jar app/target/configuration-app.jar app/target/sample.json get short   234   sampleGet short
java -jar app/target/configuration-app.jar app/target/sample.json get int     345   sampleGet int
java -jar app/target/configuration-app.jar app/target/sample.json get long    567   sampleGet long
java -jar app/target/configuration-app.jar app/target/sample.json get float   1.23  sampleGet float
java -jar app/target/configuration-app.jar app/target/sample.json get double  2.34  sampleGet double
java -jar app/target/configuration-app.jar app/target/sample.json get char    a     sampleGet char
java -jar app/target/configuration-app.jar app/target/sample.json get boolean true  sampleGet boolean

java -jar app/target/configuration-app.jar app/target/sample.json set string  hello sampleDefault string
java -jar app/target/configuration-app.jar app/target/sample.json set byte    123   sampleDefault byte
java -jar app/target/configuration-app.jar app/target/sample.json set short   234   sampleDefault short
java -jar app/target/configuration-app.jar app/target/sample.json set int     345   sampleDefault int
java -jar app/target/configuration-app.jar app/target/sample.json set long    567   sampleDefault long
java -jar app/target/configuration-app.jar app/target/sample.json set float   1.23  sampleDefault float
java -jar app/target/configuration-app.jar app/target/sample.json set double  2.34  sampleDefault double
java -jar app/target/configuration-app.jar app/target/sample.json set char    a     sampleDefault char
java -jar app/target/configuration-app.jar app/target/sample.json set boolean true  sampleDefault boolean

java -jar app/target/configuration-app.jar app/target/sample.json get string  default sampleDefault string
java -jar app/target/configuration-app.jar app/target/sample.json get byte    0       sampleDefault byte
java -jar app/target/configuration-app.jar app/target/sample.json get short   0       sampleDefault short
java -jar app/target/configuration-app.jar app/target/sample.json get int     0       sampleDefault int
java -jar app/target/configuration-app.jar app/target/sample.json get long    0       sampleDefault long
java -jar app/target/configuration-app.jar app/target/sample.json get float   0.0     sampleDefault float
java -jar app/target/configuration-app.jar app/target/sample.json get double  0.0     sampleDefault double
java -jar app/target/configuration-app.jar app/target/sample.json get char    d       sampleDefault char
java -jar app/target/configuration-app.jar app/target/sample.json get boolean false   sampleDefault boolean
