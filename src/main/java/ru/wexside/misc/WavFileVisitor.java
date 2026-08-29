package ru.wexside.misc;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import ru.wexside.util.CustomSoundLibrary;

public class WavFileVisitor extends SimpleFileVisitor<Path> {
   final CustomSoundLibrary this$0;

   public WavFileVisitor(CustomSoundLibrary customSoundLibrary2) {
      this.this$0 = customSoundLibrary2;
   }

   public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) {
      if (CustomSoundLibrary.process9(path)) {
         this.this$0.setPath2(path);
      }

      return FileVisitResult.CONTINUE;
   }
}
