package io.crunch.template;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Expression;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A service for managing and processing templates stored in a configurable folder.
 * This class provides methods to read SQL template files, extract placeholders, and generate
 * expressions for use in Apache Camel routes.
 */
@ApplicationScoped
public class SqlTemplates {

    /** The name of the folder containing the templates. */
    private final String templateFolder;

    /**
     * A regular expression pattern for matching placeholders in templates.
     * Placeholders are expected to follow the format {@code :?placeholder}.
     */
    private static final Pattern pattern = Pattern.compile(":\\?\\w+");

    /**
     * Constructs the {@link SqlTemplates} service and initializes the template folder path.
     *
     * @param templateFolder the folder where templates are stored, injected from the application configuration.
     */
    public SqlTemplates(@ConfigProperty(name = "app.archive.template.folder") String templateFolder) {
        this.templateFolder = templateFolder;
    }

    /**
     * Reads the specified template file and returns its content as a single concatenated string.
     *
     * @param year          the year to contextualize the template (currently unused in this method).
     * @param templateFile  the name of the template file to read.
     * @return the concatenated content of the template file.
     * @throws IOException          if an I/O error occurs while reading the file.
     * @throws URISyntaxException   if the template file's URI is malformed.
     */
    public String getSqlTemplate(int year, String templateFile) throws IOException, URISyntaxException {
        var path = getTemplatePath(templateFile);
        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            return lines.collect(Collectors.joining(" "));
        }
    }

    /**
     * Generates a map of JDBC parameter expressions for placeholders found in the specified template file.
     * The expressions are compatible with Apache Camel's simple language.
     *
     * @param year          the year to contextualize the template (currently unused in this method).
     * @param templateFile  the name of the template file to process.
     * @param routeBuilder  the {@link RouteBuilder} used to generate the expressions.
     * @return a map where the keys are placeholder names (without {@code :?}) and the values are expressions.
     * @throws IOException          if an I/O error occurs while reading the file.
     * @throws URISyntaxException   if the template file's URI is malformed.
     */
    public Map<String, Expression> getJdbcParameterExpressions(int year, String templateFile, RouteBuilder routeBuilder)
            throws IOException, URISyntaxException {
        return getParameters(year, templateFile)
                .collect(Collectors.toMap(
                    name -> name,
                    name -> routeBuilder.simple("${body[" + name + "]}")));
    }

    /**
     * Extracts placeholder names from the specified template file.
     *
     * @param year          the year to contextualize the template (currently unused in this method).
     * @param templateFile  the name of the template file to process.
     * @return a stream of placeholder names (without {@code :?}).
     * @throws IOException          if an I/O error occurs while reading the file.
     * @throws URISyntaxException   if the template file's URI is malformed.
     */
    private Stream<String> getParameters(int year, String templateFile) throws IOException, URISyntaxException {
        var matcher = pattern.matcher(getSqlTemplate(year, templateFile));
        return matcher.results()
                .map(MatchResult::group)
                .map(s -> s.substring(2));// trims the first two characters ":?"
    }

    /**
     * Resolves the file system path to the specified template file.
     * Supports both local file systems and files inside JAR archives.
     *
     * @param templateFile  the name of the template file.
     * @return the {@link Path} to the template file.
     * @throws IOException          if an I/O error occurs while resolving the path.
     * @throws URISyntaxException   if the resource's URI is malformed.
     */
    private Path getTemplatePath(String templateFile) throws URISyntaxException, IOException {
        var resourcePath = '/' + templateFolder + '/' + templateFile;
        var url = getClass().getResource(resourcePath);
        if (url != null) {
            var uri = url.toURI();
            if ("jar".equals(uri.getScheme())) {
                FileSystem fileSystem;
                try {
                    fileSystem = FileSystems.getFileSystem(uri);
                } catch (FileSystemNotFoundException e) {
                    fileSystem = FileSystems.newFileSystem(uri, Map.of());
                }
                return fileSystem.getPath(resourcePath);
            }
        }
        return Path.of(templateFolder, templateFile).toAbsolutePath();
    }
}
