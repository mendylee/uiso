#!/bin/bash

echo ""
echo "Self Extracting Installer"
echo ""
 
export TMPDIR=`mktemp -d /tmp/selfextract.XXXXXX`

ARCHIVE=`awk '/^__ARCHIVE_BELOW__/ {print NR + 1; exit 0; }' $0`

tail -n+$ARCHIVE $0 | tar xj -C $TMPDIR
 
CDIR=`pwd`
cd $TMPDIR
chmod +x ./install/bin/INSTALL

./install/bin/INSTALL
 
cd $CDIR
rm -rf $TMPDIR
 
# never remove the line below
exit 0
