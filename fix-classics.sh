#!/bin/bash
grep -rl org\.angdroid\.angband . | xargs perl -pi~ -e "s/org\.angdroid\.angband/org\.angdroid\.classics/"
rm -rf src/org/angdroid/classics
mv src/org/angdroid/angband src/org/angdroid/classics