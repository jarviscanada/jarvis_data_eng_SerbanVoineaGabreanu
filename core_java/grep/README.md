# Introduction
The aim of this project is to implement a grep app using Java, which allows for searching files for lines matching a regex pattern.  
If matches are found, then they are written to an output file. This app is designed to be used in a Terminal.  
It uses interfaces and implemented classes for a clean structure. The classes handle file traversal, regex matching, and output writing.  
The app uses Core Java, Regular Expressions (`Pattern`), Java NIO (`Files.walk`, `Files.readAllLines`), and SLF4J for logging.  
It has been built and packaged with Maven, and includes a shaded (uber) JAR for easier deployment.  
Finally, it has been deployed with Docker for easy distribution and consistent behaviour across machines.

# Quick Start
To run it locally: #Navigate to the directory of the project. 
cd core_java/grep 
#Then use maven to clean and deploy the package.
mvn clean package 

java -jar target/grep-1.0-SNAPSHOT.jar ".*Romeo.*Juliet.*" ./data ./out/grep.out

To run it with Docker: #Builds the image. docker build -t serbanvg/grep . 

#Run (mount input + output directories)
docker run --rm \
  -v "$(pwd)"/data:/data \
  -v "$(pwd)"/log:/log \
  serbanvg/grep ".*Romeo.*Juliet.*" /data /log/grep.out

# Implementation
## Pseudocode
process():
  validate rootPath, regex, outFile are set
  compile regex Pattern once

  matchedLines = empty list

  for each file in listFiles(rootPath) (recursive):
    for each line in readLines(file) (Files.readAllLines):
      if line matches regex:
        matchedLines.add(line)

  writeToFile(matchedLines)
  log number of matches and output path



## Performance Issue
The implementation currently uses a lot of memory because it reads entire files using `Files.readAllLines()`, and it also stores the matched lines in a `List<String>` before writing them.
For large datasets, this can exceed the heap limits. 
One possible fix is to read the input line by line using a `BufferedReader` and write matches directly to the output file using a `BufferedWriter`, so it never needs to hold the whole file (or all matches) in memory at once.

# Test
For the Stream/Lambda exercise, JUnit tests were done in `LambdaStreamImpTest`. The file was run directly in the IDE, and Maven was reloaded (and the `pom.xml` was updated) so the JUnit dependency and test runner were used properly.

For the grep app, it was tested manually by running the jar and Docker container on sample files under `./data`, then checking the output file to confirm the matched lines were correct.

# Deployment
This app was dockerized by packaging it into one single shaded uber JAR via Maven. Then a Docker image was created that uses a lightweight Java runtime base image. The Dockerfile copies the JAR into the image and sets an `ENTRYPOINT` to run `java -jar`. At runtime, the input and output folders are mounted into the container using `-v` so the app can read `/data` and write the result to `/log/grep.out`. The image is then pushable to Docker Hub (e.g., `docker push serbanvg/grep`).

# Improvement
- Update the grep app implementation to allow it to handle huge datasets by reading files line-by-line with `BufferedReader` and writing matches incrementally instead of storing all matched lines in a list first.
- Add proper JUnit tests for the grep app by testing `containsPattern`, `listFiles`, and `readLines` using temporary files/directories.
- Improve the usability of the app in the terminal by supporting optional flags (e.g., case-insensitive mode, file extension filters) and better error messages for problems such as invalid paths or missing permissions.
