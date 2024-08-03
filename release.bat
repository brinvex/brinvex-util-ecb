set JAVA_HOME="C:\tools\java\jdk-21.0.1"
set MVN_HOME="C:\tools\mvn\mvn-3.9.7"

REM Dont forget to update version in README
set new_version=1.0.0

call %MVN_HOME%\bin\mvn clean package

call %MVN_HOME%\bin\mvn versions:set -DnewVersion=%new_version%
call %MVN_HOME%\bin\mvn versions:commit
call %MVN_HOME%\bin\mvn clean deploy -DskipTests

REM Commit and push Release info