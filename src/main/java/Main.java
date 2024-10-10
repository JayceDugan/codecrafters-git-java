import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.InflaterInputStream;

public class Main {
  public static void main(String[] inputArgs) {
    final String command = inputArgs[0];

    switch (command) {
      case "init" -> {
        // Why does a directory have the type File??
        final File rootDirectory = new File(".git");
        final File headDirectory = new File(rootDirectory, "HEAD");

        new File(rootDirectory, "objects").mkdirs();
        new File(rootDirectory, "refs").mkdirs();

        try {
          headDirectory.createNewFile();

          Files.write(headDirectory.toPath(), "ref: refs/heads/main\n".getBytes());
          System.out.println("Initialized git directory");
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
      case "cat-file" -> {
        final File rootDirectory = new File(".git");
        final File objectsDirectory = new File(rootDirectory, "objects");

        final String blobSha = inputArgs[2];
        final String blobDirectoryName = blobSha.substring(0, 2);
        final String blobFilename = blobSha.substring(2);

        final File blobFile = new File(objectsDirectory + "/" + blobDirectoryName + "/" + blobFilename);

        try {
          String blob = new BufferedReader(
              new InputStreamReader(
                  new InflaterInputStream(
                      new FileInputStream(blobFile)
                  )
              )
          ).readLine();
          String blobContent = blob.substring(blob.indexOf("\0") + 1);

          System.out.print(blobContent);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
      default -> System.out.println("Unknown command: " + command);
    }
  }
}
