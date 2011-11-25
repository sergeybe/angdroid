#!/bin/bash
. ~/code/secret-keys.sh
grep -rl '%SL_SECRET%' src/org/angdroid | grep -v '~$' | xargs perl -pi~ -e "s/%SL_SECRET%/$SL_SECRET_ANGBAND/"
