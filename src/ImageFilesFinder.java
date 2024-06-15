package src;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

class ImageFilesFinder {
    File[] resultFiles;
    public static int NUM_THREADS = 4;

    public ImageFilesFinder(File[] files) {

    };

    static boolean isImage(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            if (image != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    static File[] findIndividualImages(File[] files) {
        return Arrays.stream(files)
                .parallel()
                .flatMap(ImageFilesFinder::FileCollector)
                .filter(ImageFilesFinder::isImage)
                .toArray(File[]::new);
    }

    private static Stream<File> FileCollector(File file) {

        if (file.isDirectory() && file.listFiles() != null) {
            try {
                return Files.walk(file.toPath()).map(Path::toFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Stream.of(file);
    }

}
