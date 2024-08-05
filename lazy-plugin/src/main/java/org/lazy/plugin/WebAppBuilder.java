package org.lazy.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Execute(lifecycle = "download-dependencies", phase = LifecyclePhase.PROCESS_RESOURCES)
@Mojo(name = "build-app", defaultPhase = LifecyclePhase.PACKAGE)
public class WebAppBuilder extends AbstractMojo {


    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private File target;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if("war".equals(project.getPackaging())) {
            PackageAssembler.getInstance().assembleWar(target, project.getName() + "-" + project.getVersion());
        } else if ("jar".equals(project.getPackaging())) {
            PackageAssembler.getInstance().assembleJar(target, project.getName() + "-" + project.getVersion());
        }
        try {
            Files.walk(Paths.get(target.getAbsolutePath(), "libs"))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile).forEach(File::delete);
        } catch (IOException e) {
            getLog().warn(e.getMessage());
        }
    }

}
