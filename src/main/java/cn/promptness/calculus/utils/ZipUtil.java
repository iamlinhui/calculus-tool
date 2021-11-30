package cn.promptness.calculus.utils;


import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * zip 压缩 解压缩
 */
@Slf4j
public class ZipUtil {

    /**
     * 文件读取缓冲区大小
     */
    private static final int CACHE_SIZE = 1024;

    /**
     * 压缩文件
     *
     * @param sourceFolder 压缩文件夹
     * @param zipFilePath  压缩文件输出路径
     */
    public static boolean zip(String sourceFolder, String zipFilePath) {
        try (OutputStream os = new FileOutputStream(zipFilePath)) {
            try (BufferedOutputStream bos = new BufferedOutputStream(os)) {
                try (ZipOutputStream zos = new ZipOutputStream(bos)) {
                    File file = new File(sourceFolder);
                    String basePath;
                    //压缩文件夹
                    if (file.isDirectory()) {
                        basePath = file.getPath();
                    } else {
                        basePath = file.getParent();
                    }
                    zipFile(file, basePath, zos);
                    return true;
                }
            }

        } catch (Exception e) {
            log.error("压缩文件异常", e);
        }
        return false;
    }

    /**
     * 递归压缩文件
     */
    private static void zipFile(File parentFile, String basePath, ZipOutputStream zos)
            throws Exception {
        File[] files = parentFile.isDirectory() ? parentFile.listFiles() : new File[]{parentFile};
        String pathName;
        InputStream is;
        BufferedInputStream bis;
        byte[] cache = new byte[CACHE_SIZE];
        for (File file : files) {
            if (file.isDirectory()) {
                pathName = file.getPath().substring(basePath.length() + 1) + File.separator;
                zos.putNextEntry(new ZipEntry(pathName));
                zipFile(file, basePath, zos);
            } else {
                pathName = file.getPath().substring(basePath.length() + 1);
                is = new FileInputStream(file);
                bis = new BufferedInputStream(is);
                zos.putNextEntry(new ZipEntry(pathName));
                int nRead;
                while ((nRead = bis.read(cache, 0, CACHE_SIZE)) != -1) {
                    zos.write(cache, 0, nRead);
                }
                bis.close();
                is.close();
            }
        }
    }

    /**
     * 解压压缩包
     *
     * @param zipFilePath 压缩文件路径
     * @param destDir     解压目录
     */
    public static void unZip(String zipFilePath, String destDir) {
        ZipFile zipFile = null;
        try {
            BufferedInputStream bis;
            FileOutputStream fos;
            BufferedOutputStream bos;
            zipFile = new ZipFile(zipFilePath);
            Enumeration zipEntries = zipFile.entries();
            File file, parentFile;
            ZipEntry entry;
            byte[] cache = new byte[CACHE_SIZE];
            while (zipEntries.hasMoreElements()) {
                entry = (ZipEntry) zipEntries.nextElement();
                if (entry.getName().endsWith(File.separator)) {
                    new File(destDir + File.separator + entry.getName()).mkdirs();
                    continue;
                }
                bis = new BufferedInputStream(zipFile.getInputStream(entry));
                file = new File(destDir + File.separator + entry.getName());
                parentFile = file.getParentFile();
                if (parentFile != null && (!parentFile.exists())) {
                    parentFile.mkdirs();
                }
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos, CACHE_SIZE);
                int readIndex;
                while ((readIndex = bis.read(cache, 0, CACHE_SIZE)) != -1) {
                    fos.write(cache, 0, readIndex);
                }
                bos.flush();
                bos.close();
                fos.close();
                bis.close();
            }
        } catch (IOException e) {
            log.error("解压压缩包异常", e);
        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (IOException e) {
                log.error("关闭zip文件异常", e);
            }
        }
    }


}
