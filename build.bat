@echo off
rd /S /Q output
dir /s /B *.java > sources.txt
javac @sources.txt -d output
del sources.txt
@echo on