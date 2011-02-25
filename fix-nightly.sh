#!/bin/bash
grep -rl org\.angdroid\.angband . | xargs perl -pi~ -e "s/org\.angdroid\.angband/org\.angdroid\.nightly/"
rm -rf src/org/angdroid/nightly
mv src/org/angdroid/angband src/org/angdroid/nightly