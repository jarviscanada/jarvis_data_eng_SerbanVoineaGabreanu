package ca.jrvs.apps.grep;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *This class is responsible for searching files in a root directory and
 *it then writes all the lines that match a regex to an output file.
 */
public class JavaGrepImp implements JavaGrep {

    private static final Logger logger = LoggerFactory.getLogger(JavaGrepImp.class);

    private String rootPath;
    private String regex;
    private String outFile;

    private Pattern pattern;

    /**
     *Reads the CLI arguments, saves them using setters, and then runs the grep process.
     *
     * @param args args[0]=regex, args[1]=rootPath, args[2]=outFile
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        if (args == null || args.length != 3) {
            logger.error("Usage: JavaGrepImp <regex> <rootPath> <outFile>");
            return;
        }

        JavaGrepImp app = new JavaGrepImp();
        try {
            app.setRegex(args[0]);
        } catch (PatternSyntaxException e) {
            logger.error("Invalid regex: {}", args[0], e);
            return;
        }
        app.setRootPath(args[1]);
        app.setOutFile(args[2]);

        try {
            app.process();
        } catch (IOException e) {
            logger.error("Grep process failed", e);
        }
    }

    /**
     *The process method lists files, reads lines, collects matches, and then writes the output to a .txt file.
     * @throws IOException if writing the output file fails
     */
    @Override
    public void process() throws IOException {
        if (rootPath == null || regex == null || outFile == null) {
            throw new IllegalStateException("rootPath, regex, and outFile must be set before process()");
        }
        if (pattern == null) {
            pattern = Pattern.compile(regex);
        }

        List<String> matchedLines = new ArrayList<>();

        for (File file : listFiles(rootPath)) {
            for (String line : readLines(file)) {
                if (containsPattern(line)) {
                    matchedLines.add(line);
                }
            }
        }

        writeToFile(matchedLines);
        logger.info("Done. Matched {} lines. Output: {}", matchedLines.size(), outFile);
    }

    /**
     *Recursively looks through the root directory (looks at everything in the root directory,
     *then finds subfolders and looks in those if present) and returns all regular files
     * @param rootDir the directory to search
     * @return a list of files under rootDir (recursive); empty list if rootDir is null/invalid
     */
    @Override
    public List<File> listFiles(String rootDir) {
        if (rootDir == null) {
            return Collections.emptyList();
        }

        Path root = Paths.get(rootDir);
        if (!Files.exists(root)) {
            logger.warn("Root path does not exist: {}", rootDir);
            return Collections.emptyList();
        }

        try (Stream<Path> paths = Files.walk(root)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Failed to list files under: {}", rootDir, e);
            return Collections.emptyList();
        }
    }

    /**
     *Reads all of the lines from a file and returns them as a list.
     *
     * @param inputFile the file to read
     * @return all lines in the file; empty list if inputFile is null or cannot be read
     */
    @Override
    public List<String> readLines(File inputFile) {
        if (inputFile == null) {
            return Collections.emptyList();
        }

        try {
            return Files.readAllLines(inputFile.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Failed to read file: {}", inputFile.getAbsolutePath(), e);
            return Collections.emptyList();
        }
    }

    /**
     *Checks to see if the regex appears anywhere in the file.
     *
     * @param line the input line
     * @return true if the regex matches anywhere in the line; otherwise false
     */
    @Override
    public boolean containsPattern(String line) {
        return line != null && pattern != null && pattern.matcher(line).find();
    }

    /**
     *Writes the matched lines to the output file (also creates parent folders if needed).
     *
     * @param lines the matched lines to write
     * @throws IOException if writing fails
     */
    @Override
    public void writeToFile(List<String> lines) throws IOException {
        if (outFile == null) {
            throw new IllegalStateException("outFile must be set before writeToFile()");
        }

        Path out = Paths.get(outFile);
        Path parent = out.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.write(out, lines, StandardCharsets.UTF_8);
    }

    /**
     *Returns the root directory path to search in.
     *
     * @return the rootPath
     */
    @Override
    public String getRootPath() {
        return rootPath;
    }

    /**
     *Sets the root directory path to search in.
     *
     * @param rootPath the root directory path
     */
    @Override
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     *Returns the regex pattern string used for matching.
     *
     * @return the regex string
     */
    @Override
    public String getRegex() {
        return regex;
    }

    /**
     *Sets the regex pattern string and compiles it once for efficient reuse.
     *
     * @param regex the regex string
     */
    @Override
    public void setRegex(String regex) {
        this.regex = regex;
        this.pattern = (regex == null) ? null : Pattern.compile(regex);
    }

    /**
     *Returns the output file path where matched lines will be written.
     *
     * @return the outFile path
     */
    @Override
    public String getOutFile() {
        return outFile;
    }

    /**
     *Sets the output file path where matched lines will be written.
     *
     * @param outFile the output file path
     */
    @Override
    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }
}
