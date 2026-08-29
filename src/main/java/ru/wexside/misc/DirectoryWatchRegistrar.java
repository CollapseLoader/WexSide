package ru.wexside.misc;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import ru.wexside.util.CustomSoundLibrary;

public class DirectoryWatchRegistrar extends SimpleFileVisitor<Path> {
   final CustomSoundLibrary this$0;

   public DirectoryWatchRegistrar(CustomSoundLibrary customSoundLibrary2) {
      this.this$0 = customSoundLibrary2;
   }

   public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
      this.this$0.registerDirectory(path);
      return FileVisitResult.CONTINUE;
   }
}
