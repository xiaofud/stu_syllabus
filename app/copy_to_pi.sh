#! /bin/bash
# update the name
mv app-release.apk syllabus.apk
# copy to the remote host
scp syllabus.apk xiaofud@10.28.31.32:/home/xiaofud/share
