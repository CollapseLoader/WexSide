package ru.wexside.misc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public final class ShaderIncludeProcessor {
   private static final String INCLUDE_DIRECTIVE = "#include";

   private static String resolveIncludePath(String includePath, String currentDirectory) {
      if (!includePath.startsWith("/") && !includePath.startsWith("assets/")) {
         if (currentDirectory == null || currentDirectory.isEmpty()) {
            return includePath;
         } else {
            return currentDirectory.endsWith("/") ? currentDirectory + includePath : currentDirectory + "/" + includePath;
         }
      } else {
         return includePath.startsWith("/") ? includePath : "/" + includePath;
      }
   }

   private static String expandIncludes(String source, String currentDirectory, ClassLoader classLoader, Set<String> includeStack, int depth) {
      if (depth > 64) {
         throw new IllegalStateException("Include depth too large (possible cycle)");
      } else {
         StringBuilder result = new StringBuilder(source.length() + 1024);

         String line;
         try (BufferedReader reader = new BufferedReader(
               new InputStreamReader(new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)
            )) {
            while((line = reader.readLine()) != null) {
               String trimmedLine = line.trim();
               if (trimmedLine.startsWith("#include")) {
                  int openingQuote = trimmedLine.indexOf(34);
                  int closingQuote = trimmedLine.lastIndexOf(34);
                  if (openingQuote < 0 || closingQuote <= openingQuote) {
                     throw new IllegalStateException("Malformed include: " + line);
                  }

                  String requestedPath = trimmedLine.substring(openingQuote + 1, closingQuote).trim();
                  String resolvedPath = resolveIncludePath(requestedPath, currentDirectory);
                  String absolutePath = resolvedPath.startsWith("/") ? resolvedPath : "/" + resolvedPath;
                  if (!includeStack.add(absolutePath)) {
                     throw new IllegalStateException("Include cycle detected: " + absolutePath);
                  }

                  String includedSource = readResource(classLoader, absolutePath);
                  String expandedSource = expandIncludes(includedSource, parentDirectory(absolutePath), classLoader, includeStack, depth + 1);
                  includeStack.remove(absolutePath);
                  result.append(expandedSource).append('\n');
               } else {
                  result.append(line).append('\n');
               }
            }
         } catch (Exception var18) {
            throw new RuntimeException("Preprocess failed (in " + currentDirectory + "): " + var18.getMessage(), var18);
         }

         return result.toString();
      }
   }

   private static String readResource(ClassLoader classLoader, String path) {
      String resource = path.startsWith("/") ? path.substring(1) : path;

      try {
         String var4;
         try (InputStream inputStream = classLoader.getResourceAsStream(resource)) {
            if (inputStream == null) {
               throw new IllegalStateException("Include not found on classpath: " + path);
            }

            var4 = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
         }

         return var4;
      } catch (Exception var8) {
         throw new RuntimeException("Read failed: " + path, var8);
      }
   }

   private static String parentDirectory(String path) {
      int separator = path.lastIndexOf(47);
      return separator <= 0 ? "/" : path.substring(0, separator);
   }

   public static String preprocess(String source, String sourceDirectory, ClassLoader classLoader) {
      return expandIncludes(source, normalizeDirectory(sourceDirectory), classLoader, new HashSet<>(), 0);
   }

   private static String normalizeDirectory(String directory) {
      if (directory != null && !directory.isEmpty()) {
         return directory.endsWith("/") ? directory : directory + "/";
      } else {
         return "/";
      }
   }
}
