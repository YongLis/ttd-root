package com.ly.ttd.utils;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 *
 */
public abstract class ManifestUtils {

    private ManifestUtils() {
    }

    public static Optional<Manifest> getManifest(Class<?> klass) {
        try {
            CodeSource codeSource = klass.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                URL location = codeSource.getLocation();
                if (location != null) {
                    JarFile jarFile = new JarFile(new File(location.toURI()));
                    Optional<Manifest> manifest;
                    try {
                        manifest = Optional.of(jarFile.getManifest());
                    } catch (Throwable e) {
                        try {
                            jarFile.close();
                        } catch (Throwable ex) {
                            e.addSuppressed(ex);
                        }
                        throw e;
                    }
                    jarFile.close();
                    return manifest;
                }
            }
            return Optional.empty();
        } catch (Throwable e) {
            return Optional.empty();
        }
    }

    public static Optional<String> getCIBuildNumberVersion(Class<?> klass) {
        return getManifest(klass).map(Manifest::getMainAttributes).map((attributes) -> attributes.getValue("CI-Build-Number"));
    }
}
