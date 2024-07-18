package com.client.staff.libs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    public static String zipFolder(String sourceFolderPath) throws IOException {

        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedTimestamp = timestamp.format(formatter);

        Path tempDir = Files.createTempDirectory("zip_temp");
        String zipFilePath = tempDir.resolve("compressed_"
                        + formattedTimestamp + ".zip").toString();


        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            File sourceFolder = new File(sourceFolderPath);
            compressFolder(sourceFolder, sourceFolder.getName(), zos);
        }
        return zipFilePath;
    }

    private static void compressFolder(File sourceFolder, String parentName, ZipOutputStream zos) throws IOException {
        for (File file : sourceFolder.listFiles()) {
            if (file.isDirectory()) {
                compressFolder(file, parentName + "/" + file.getName(), zos);
                continue;
            }

            ZipEntry zipEntry = new ZipEntry(parentName + "/" + file.getName());
            zos.putNextEntry(zipEntry);

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
            }

            zos.closeEntry();
        }
    }
}

