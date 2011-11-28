#!/bin/bash
. ~/code/secret-keys.sh
egrep -rl 'org\.angdroid\.angband|%SL_SECRET%' . | grep -v '~$' | xargs perl -pi~ -e "s/org\.angdroid\.angband/org\.angdroid\.nightly/; s/%SL_SECRET%/$SL_SECRET_ANGBAND/"
rm -rf src/org/angdroid/nightly
mv src/org/angdroid/angband src/org/angdroid/nightly