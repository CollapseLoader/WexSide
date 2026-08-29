package ru.wexside.misc;

import java.util.function.Function;

public class ResourceResolver {
   private final Function<String, ResourceData> resourceLoader;
   private final String basePath;

   public ResourceResolver(String basePath, Function<String, ResourceData> resourceLoader) {
      this.basePath = basePath;
      this.resourceLoader = resourceLoader;
   }

   private static String normalizePath(String path) {
      if (path != null && !path.isEmpty()) {
         String normalizedPath = path.replace('\\', '/').trim();

         while(normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
         }

         while(normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
         }

         return normalizedPath;
      } else {
         return "";
      }
   }

   public ResourceData resolve(String relativePath) {
      String resolvedPath = (normalizePath(this.basePath) + "/" + normalizePath(relativePath)).replaceAll("/{2,}", "/");
      if (!resolvedPath.startsWith("/")) {
         resolvedPath = "/" + resolvedPath;
      }

      return this.resourceLoader.apply(resolvedPath);
   }
}
