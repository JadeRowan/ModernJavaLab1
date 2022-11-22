package modern.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class Main {
    private static final String INPUT_FOLDER = "data/input";
    private static final String OUTPUT_FOLDER = "data/output";
    private static final String SLASH = "/";

    public static void main(String[] args) throws Exception {
        generateSourceFiles();
        List<File> sourceFiles = getSourceFiles(INPUT_FOLDER);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        sourceFiles.forEach(file -> executor.execute(
                () -> writeAllLines(Path.of(OUTPUT_FOLDER + SLASH + file.getName()),
                        readAllLines(file).stream()
                                .map(line -> new StringBuilder(line).reverse().toString())
                                .collect(Collectors.toList()))));
        executor.shutdown();
    }

    private static List<String> readAllLines(File file) {
        try {

            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read all lines from " + file, e);
        }
    }

    private static void writeAllLines(Path path, List<String> lines) {
        try {
            Files.write(path, lines);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write all lines to " + path, e);
        }
    }

    private static List<File> getSourceFiles(String directoryPath) {
        return Arrays.stream(new File(directoryPath).listFiles())
                .filter(f -> f.getAbsolutePath().matches(".+\\.java"))
                .collect(Collectors.toList());
    }

    private static void generateSourceFiles() throws IOException {
        String fileStart = "public class Class%d {\n"
                + "    public Class%d() {\n";
        String fileEnd = "    }\n"
                + "}";
        for (int i = 1; i <= 100; i++) {
            Path iterPath = Path.of(String.format("%s/Class%d.java", INPUT_FOLDER, i));
            Files.writeString(iterPath, String.format(fileStart, i, i));
            String sout = String.format("        System.out.println(\"This is constructor of Class%d\");\n", i);
            Files.writeString(iterPath, sout.repeat(1_000_000), StandardOpenOption.APPEND);
            Files.writeString(iterPath, fileEnd, StandardOpenOption.APPEND);
        }
    }
}
