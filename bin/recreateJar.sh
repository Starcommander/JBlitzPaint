#!/bin/bash

# This script is used, to fast copy bin into the jar.

cd dirname "$0"
TMP_DIR=$(mktemp -d)
SCR_DIR=$(pwd)
BIN_DIR="/home/ppp/Workspace_Eclipse/JBlitzPaint/bin/starcom/"

cleanup()
{
  rm -r "$TMP_DIR"
}

test_dir() # Args: dir
{
  if [ ! -d "$1" ]; then
    echo "Error, missing dir: $1"
    cleanup
    exit 1
  fi
}

test_dir "$BIN_DIR"

cd "$SCR_DIR"
mkdir "$TMP_DIR/src"
mkdir "$TMP_DIR/tar"
unzip JBlitzPaint.jar -d "$TMP_DIR/tar"
cp -r "$BIN_DIR" "$TMP_DIR/src"
meld "$TMP_DIR/src/starcom/" "$TMP_DIR/tar/starcom/"
rm JBlitzPaint.jar
cd "$TMP_DIR/tar"
zip -r "$SCR_DIR"/JBlitzPaint.jar .
cd "$SCR_DIR"
cleanup
