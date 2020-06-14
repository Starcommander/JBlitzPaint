#!/bin/bash

# This script creates a debian-package of jblitzpaint.
# Hint: Run compile.sh first.

SCR_DIR=$(dirname "$0")
cd "$SCR_DIR/.."
TMP_DIR=$(mktemp -d)

check_cmd()
{
  local cmd=$(which $1)
  if [ -z "$cmd" ]; then
    echo "Error, command not found: $1" 1>&2
    exit 3
  fi
}

check_exist()
{
  if [ ! -e "$1" ]; then
    echo "Error, missing file or directory: $1" 1>&2
    echo "Current path is: $(pwd)" 1>&2
    exit 2
  fi
}

write_start_sh() # Args: targetFile
{
  cat dist/run.sh | sed -e 's#target/#/usr/lib/jblitzpaint/#g' > "$1"
  chmod 755 "$1"
}

write_desktop_to # Args: targetDir
{
  cp dist/flatpak/data/com.starcom.jblitzpaint.desktop "$1"
}

check_cmd "fakeroot"
check_cmd "dpkg"
check_cmd "dpkg-deb"
check_exist "target"
check_exist "dist/run.sh"
check_exist "dist/flatpak/data/com.starcom.jblitzpaint.desktop"

mkdir -p "$TMP_DIR/DEBIAN"
mkdir -p "$TMP_DIR/usr/bin"
mkdir -p "$TMP_DIR/usr/lib/jblitzpaint/lib"
write_start_sh "$TMP_DIR/usr/bin/jblitzpaint"
write_desktop_to "$TMP_DIR/usr/share/applications/"

cp target/jblitzpaint-1.0-SNAPSHOT.jar "$TMP_DIR/usr/lib/jblitzpaint/"
cp -r target/lib "$TMP_DIR/usr/lib/jblitzpaint/lib"
fakeroot dpkg-deb -b "$TMP_DIR/" target/jblitzpaint.deb
RET_VAL=$?
if [ "$RET_VAL" = 0 ]; then
  echo "Successfully created: target/jblitzpaint.deb"
else
  echo "Build error, exit-value not null!"
fi
