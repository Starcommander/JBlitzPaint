#!/bin/bash

# This script starts jblitzpaint.
# Hint: Run compile.sh first.

SCR_DIR=$(dirname "$0")
cd "$SCR_DIR/.."
java --add-modules=java.base,javafx.base,javafx.fxml,javafx.graphics,javafx.swing,javafx.controls --module-path target/lib/ -cp "target/lib/*.jar" -jar target/jblitzpaint-1.0-SNAPSHOT.jar
