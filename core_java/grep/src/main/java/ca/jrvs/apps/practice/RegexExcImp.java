package ca.jrvs.apps.practice;

import java.util.regex.Pattern;

/**
 *Implements {@link RegexExc} using regular expressions.
 *This class checks if  a filename ends with .jpg or .jpeg (case-insensitive),
 *If a string matches a IPv4 format (0-999.0-999.0-999.0-999)
 *It also checks Whether a line is empty or contains only whitespace
 *
 */
public class RegexExcImp implements RegexExc {

    /**
     *Matches the filenames that end with .jpg or .jpeg (case-insensitive).
     */
    private static final Pattern JPEG = Pattern.compile("(?i)^.+\\.jpe?g$");

    /**
     *Matches a simplified IPv4 address in the form xxx.xxx.xxx.xxx where each segment is 1-3 numbers long.
     */
    private static final Pattern IP = Pattern.compile("^(?:\\d{1,3}\\.){3}\\d{1,3}$");

    /**
     *Matches an empty line or a line with only whitespace.
     */
    private static final Pattern EMPTY_LINE = Pattern.compile("^\\s*$");

    /**
     *Checks to see if the given filename ends with .jpg or .jpeg (case insensitive).
     *
     * @param filename the filename to check
     * @return true if filename is a jpg/jpeg name; otherwise false
     */
    @Override
    public boolean matchJpeg(String filename) {
        return filename != null && JPEG.matcher(filename).matches();
    }

    /**
     *Checks for the given string to see if it matches a IPv4 format (0-999.0-999.0-999.0-999).
     *
     * @param ip the string to check
     * @return true if ip matches the IPv4 format; otherwise false
     */
    @Override
    public boolean matchIp(String ip) {
        return ip != null && IP.matcher(ip).matches();
    }

    /**
     *Checks if the  line is empty or contains only whitespace.
     *
     * @param line the line to check
     * @return true if line is empty or whitespace-only; otherwise false
     */
    @Override
    public boolean isEmptyLine(String line) {
        return line != null && EMPTY_LINE.matcher(line).matches();
    }
}
