package codecube.core;

import org.apache.commons.io.FileUtils;
import org.sonarsource.sonarlint.core.client.api.common.analysis.ClientInputFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class BufferedInputFile implements ClientInputFile {
    private final String path;
    private final String code;

    public BufferedInputFile(String path) {
        this.path = path;
        this.code = loadFile(path);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean isTest() {
        return path.contains("/test/");
    }

    @Override
    public Charset getCharset() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public InputStream inputStream() {
        return new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String contents() {
        return code;
    }


    public List<String> extractBlock(int begin, int end) {
        List<String> lines = new ArrayList<>();
        try (InputStream stream = this.inputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String strLine;
            int number = 0;
            while ((strLine = br.readLine()) != null && number <= end) {
                if (number >= begin) {
                    lines.add(strLine);
                }
                number++;
            }
        } catch (IOException e) {
        }
        return lines;
    }

    private static String loadFile(String path) {
        try {
            return FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return "";
        }
    }

}
