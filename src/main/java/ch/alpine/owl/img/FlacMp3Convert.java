package ch.alpine.owl.img;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import ch.alpine.tensor.ext.FileExtension;
import ch.alpine.tensor.ext.HomeDirectory;

enum FlacMp3Convert {
  ;
  static void main() throws IOException, InterruptedException {
    Path src = HomeDirectory.Music.resolve("Venom The Last Dance (2024)");
    Path dst = HomeDirectory.Music.resolve("mp3", src.getFileName().toString());
    Files.createDirectories(dst);
    for (Path file : Files.list(src).toList())
      if (FileExtension.of(file).equalsIgnoreCase("FLAC")) {
        // String fi = file.getAbsolutePath();
        Path fo = dst.resolve(file.getFileName() + ".mp3");
        System.out.println(fo);
        List<String> list = List.of("ffmpeg", "-i", file.toString(), "-q:a", "0", "-map", "a", fo.toString());
        ProcessBuilder processBuilder = new ProcessBuilder(list);
        Process process = processBuilder.start();
        process.waitFor();
      }
  }
}
