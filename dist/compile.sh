#!/bin/bash

# This script compiles jblitzpaint, and creates the jar.

SCR_DIR=$(dirname "$0")
cd "$SCR_DIR/.."
mvn compile
mvn package
ls target/*.jar
