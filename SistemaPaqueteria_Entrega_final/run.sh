#!/usr/bin/env bash
set -e
mkdir -p out
find src/main/java -name '*.java' > sources.txt
javac -encoding UTF-8 -d out @sources.txt
rm sources.txt
java -cp out co.edu.unilibre.paqueteria.app.Main
