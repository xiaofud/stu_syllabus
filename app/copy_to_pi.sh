#! /bin/bash
# update the name
mv app-release.apk syllabus.apk
# copy to the remote host
scp syllabus.apk pi@10.22.27.65:/home/pi/Desktop/share
