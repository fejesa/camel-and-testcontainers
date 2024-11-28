package io.crunch.template;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Expression;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class Templates {

    private final String templateFolder;

    public Templates(@ConfigProperty(name = "app.archive.template.folder") String templateFolder) {
        this.templateFolder = templateFolder;
    }

    private static final Pattern pattern = Pattern.compile(":\\?\\w+");

    public String getTemplate(int year, String templateFile) throws IOException, URISyntaxException {
        var path = getTemplatePath(templateFile);
        return Files.readAllLines(path, StandardCharsets.UTF_8).stream().reduce((a, b) -> a + " " + b.trim()).orElse("");
    }

    public Map<String, Expression> getJdbcParameterExpressions(int year, String templateFile, RouteBuilder routeBuilder) throws IOException, URISyntaxException {
        return getParameters(year, templateFile)
                .collect(Collectors.toMap(
                    name -> name,
                    name -> routeBuilder.simple("${body[" + name + "]}")));
    }

    private Stream<String> getParameters(int year, String templateFile) throws IOException, URISyntaxException {
        var matcher = pattern.matcher(getTemplate(year, templateFile));
        return matcher.results()
                .map(MatchResult::group)
                .map(s -> s.substring(2));
    }

    private Path getTemplatePath(String templateFile) throws URISyntaxException, IOException {
        var resourcePath = '/' + templateFolder + '/' + templateFile;
        var url = getClass().getResource(resourcePath);
        if (url != null) {
            var uri = url.toURI();
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem;
                try {
                    fileSystem = FileSystems.getFileSystem(uri);
                } catch (FileSystemNotFoundException e) {
                    fileSystem = FileSystems.newFileSystem(uri, Map.of());
                }
                return fileSystem.getPath(resourcePath);
            }
        }
        return Paths.get(templateFolder, templateFile).toAbsolutePath();
    }
}
