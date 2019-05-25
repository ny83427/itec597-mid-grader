@echo off
rem only works for me dude
call jdk8.bat
call mvn.bat test-compile
if not exist target\libs\github-api-1.95.jar (
    call mvn.bat dependency:copy-dependencies -DoutputDirectory=target/libs
)
java -cp target\libs\*;target\test-classes\ Grader G