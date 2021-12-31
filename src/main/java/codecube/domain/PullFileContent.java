package codecube.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor
public class PullFileContent {

    private final String filename;
    private final String content;
}

