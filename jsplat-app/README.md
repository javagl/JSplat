# jsplat-app

A splat application.

This is not part of a release and not fully integrated into the build process
yet. Parts of the `jsplat-viewer` rely on a local Maven repository,
and may be replaced with proper Maven releases. Parts of the `jsplat-app`
are using classes that may eventually become part of Maven packages.

The process for building the application, starting in _this_ directory
(the `jsplat-app` directory) is
```
cd..
call mvn clean
call mvn package
call mvn install

cd jsplat-viewer
call mvn clean
call mvn package
call mvn install
cd..

cd jsplat-viewer-lwjgl
call mvn clean
call mvn package
call mvn install
cd..

cd jsplat-app
call mvn clean compile assembly:single -U
```
