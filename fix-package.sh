#!/bin/bash
grep -rl org\.angdroid\.angband . | xargs perl -pi~ -e "s/org\.angdroid\.angband/org\.angdroid\.variants/"
rm -rf src/org/angdroid/variants
mv src/org/angdroid/angband src/org/angdroid/variants