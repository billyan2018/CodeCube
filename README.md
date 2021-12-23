# CodeCube
One-Stop PR Code Review Tool

# Quick start

-  build the app with gradle
```
gradlew jar
```
- Scan a PR:
```
 java -cp build\libs\CodeCube-1.0.jar codecube.PrRunner {YOUR GITHUB TOKEN} {YOUR PR URL}
```

- Scan source files:

```
 java -cp build\libs\CodeCube-1.0.jar codecube.FilesRunner file1 file2 file3 ...
```