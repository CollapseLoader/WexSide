package ru.wexside.misc;

import java.io.InputStream;

public class ClasspathResource implements ResourceData {
   private final String path;

   public ClasspathResource(String path) {
      this.path = path;
   }

   @Override
   public String getPath() {
      return this.path;
   }

   @Override
   public InputStream openStream() {
      String normalizedPath = normalizePath(this.path);
      InputStream stream = ClasspathResource.class.getResourceAsStream(normalizedPath);
      if (stream != null) {
         return stream;
      } else {
         String classLoaderPath = normalizedPath.startsWith("/") ? normalizedPath.substring(1) : normalizedPath;
         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
         return classLoader.getResourceAsStream(classLoaderPath);
      }
   }

   private static String normalizePath(String path) {
      String normalizedPath = path == null ? "" : path.trim().replace('\\', '/');
      normalizedPath = normalizedPath.replaceAll("/{2,}", "/");
      if (!normalizedPath.startsWith("/")) {
         normalizedPath = "/" + normalizedPath;
      }

      return normalizedPath;
   }
}
