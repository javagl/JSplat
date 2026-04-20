# jsplat-app

A splat application.

This is not part of a release and not fully integrated into the build process
yet. 

The process for building the application, starting in _this_ directory
(the `jsplat-app` directory) is
```
cd..
call mvn clean
call mvn package
call mvn install

cd jsplat-app
call mvn clean compile assembly:single -U
```
