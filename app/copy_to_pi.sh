#! /bin/bash
# update the name
mv app-release.apk syllabus.apk
# copy to the remote host
scp syllabus.apk xiaofud@$XIAOFUD:/home/xiaofud/share
scp syllabus.apk syllabus@121.42.175.83:/home/syllabus/syllabus/apk/
