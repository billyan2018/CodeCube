# CodeCube
One Stop PR Review Tool

## How to make it work
This tool is based on the cocept of ** Write once, modify everytime**, so it will take a few steps to make it work for you in your secure environment.

- Create a java class `/src/main/java/codecube/utils/GitHubToken.java`
Put your github token into it. The content of the file will be look like:
```java
package codecube.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
class GitHubToken {
    static final String TOKEN = "YOUR TOKEN HERE";
}
```
- If you got compile error with Gradle, please enable "Annotation Processing"

