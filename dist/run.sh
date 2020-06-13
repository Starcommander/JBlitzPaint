#!/bin/bash

cd ..
java --add-modules=java.base,javafx.base,javafx.fxml,javafx.graphics,javafx.swing,javafx.controls --module-path target/lib/ -cp "target/lib/*.jar" -jar target/jblitzpaint-1.0-SNAPSHOT.jar
