#!/bin/bash
printf "%x\n" $(($(echo $(unzip -v raw/zip$1 | gawk '$7 ~ /^[a-f0-9]+$/ { printf "0x%s ", $7 }') | tr ' ' +) )) > raw/crc$1
