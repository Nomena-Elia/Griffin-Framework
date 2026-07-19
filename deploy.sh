#!/bin/bash

APP_NAME="griffin"
SRC_DIR="./"
BUILD_DIR="bin"
LIB_DIR="lib"
LIBS="$LIB_DIR/*"
TEST_LIB="/home/nomena/Documents/S4/web dyn/griffin/Test/lib"

find $SRC_DIR -name "*.java" > sources.txt
javac -cp "$LIBS" -d $BUILD_DIR/ @sources.txt
# rm sources.txt

# Générer le fichier .jar dans le dossier bin
cd $BUILD_DIR || exit
jar -cvf $APP_NAME.jar *
cd ..
# 
cp -f $BUILD_DIR/$APP_NAME.jar "$TEST_LIB/"

cp -f $LIBS "$TEST_LIB"
