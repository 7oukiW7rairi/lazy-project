package org.lazy.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

public class PackageAssemblerTest {

    @BeforeMethod
    public void setUp() {
    }

    @Test
    public void testAssembleWar() {
    }

    @Test
    public void testAssembleJar() throws MojoExecutionException {
        File file = new File(getClass().getResource("/").getPath());
        PackageAssembler.getInstance().assembleJar(file, "output-test-name");
    }
}