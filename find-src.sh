#!/bin/bash
dir=plugin/$1
echo 'LOCAL_SRC_FILES += \' > $dir/jni/Makefile.src
for f in $(perl -pe 's/\\\n//g' $dir/extsrc/src/Makefile.src | perl -ne 'if(/^ANGFILES =|^ZFILES =/) { s/^.*=//; s/\.o/.c/g; print }'); do echo "../extsrc/src/$f \\"; done >> $dir/jni/Makefile.src
echo '' >> $dir/jni/Makefile.src
