## OIT ITEC597 Mid Term Grader

A simple grade tool for [OIT ITEC597 Mid Term](https://github.com/ny83427/itec597-mid-term).

It's built based on:
+ JUnit5: `junit-platform-launcher` used to execute unit tests programmatically
+ `maven-invoker` used to build Maven projects programmatically
+ JGit: used to clone/pull students' repositories
+ GitHub API Java Wrapper: More lightweight choice for basic stuff, like retrieving forked repositories

## TODO
* [ ] Automate Maven re-import and hot-load, even though we can use a workaround via generating random file name for jar files
* [ ] Try to build an Online-Judge system for Olivet Institute of Technology supporting Java, Python, Ruby and JavaScript