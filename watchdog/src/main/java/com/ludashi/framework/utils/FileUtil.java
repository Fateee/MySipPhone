package com.ludashi.framework.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.ludashi.framework.utils.log.LogUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * file util
 *
 * @author liuyaqi
 */
@SuppressWarnings("ALL")
public class FileUtil {

    private static final String TAG = "FileUtil";

    public static final int IO_BUFFER_SIZE = 8 * 1024;

    /**
     * 检查文件目录是否存在，如果不存在，则创建
     *
     * @param path
     */
    public static File makeDirIfNeed(String path) {
        File dir = null;
        if (!TextUtils.isEmpty(path)) {
            dir = makeDirIfNeed(new File(path));
        }
        return dir;
    }

    /**
     * 检查文件目录是否存在，如果不存在，则创建
     *
     * @param dir
     */
    public static File makeDirIfNeed(File dir) {
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 拷贝文件
     *
     * @param pIn
     * @param pOut
     * @throws IOException
     */
    public static void copyFile(final File pIn, final File pOut) throws IOException {
        final FileInputStream fis = new FileInputStream(pIn);
        final FileOutputStream fos = new FileOutputStream(pOut);
        try {
            FileUtil.copy(fis, fos, -1);
        } finally {
            FileUtil.close(fis);
            FileUtil.close(fos);
        }
    }

    /**
     * 拷贝流
     *
     * @param pInputStream
     * @param pOutputStream
     * @param pByteLimit
     * @throws IOException
     */
    public static void copy(final InputStream pInputStream, final OutputStream pOutputStream,
                            final long pByteLimit) throws IOException {
        if (pByteLimit < 0) {
            final byte[] buf = new byte[FileUtil.IO_BUFFER_SIZE];
            int read;
            while ((read = pInputStream.read(buf)) != -1) {
                pOutputStream.write(buf, 0, read);
            }
        } else {
            final byte[] buf = new byte[FileUtil.IO_BUFFER_SIZE];
            final int bufferReadLimit = Math.min((int) pByteLimit, FileUtil.IO_BUFFER_SIZE);
            long pBytesLeftToRead = pByteLimit;
            int read;

            while ((read = pInputStream.read(buf, 0, bufferReadLimit)) != -1) {
                if (pBytesLeftToRead > read) {
                    pOutputStream.write(buf, 0, read);
                    pBytesLeftToRead -= read;
                } else {
                    pOutputStream.write(buf, 0, (int) pBytesLeftToRead);
                    break;
                }
            }
        }
        pOutputStream.flush();
    }

    /**
     * 读取内容
     *
     * @param is
     * @return
     */
    public static String readContentFromStream(InputStream is) {
        if (is != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            try {
                String lineTxt;
                while ((lineTxt = br.readLine()) != null) {
                    sb.append(lineTxt).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close(br);
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * 读取第n行的内容
     *
     * @param is
     * @param lineNum 行号计数由1开始
     * @return
     */
    public static String readLine(InputStream is, int lineNum) {
        if (is == null || lineNum < 1) {
            return "";
        }
        BufferedReader br = null;
        try {
            int count = lineNum;
            br = new BufferedReader(new InputStreamReader(is));
            String lineTxt;
            while ((lineTxt = br.readLine()) != null) {
                if (--count == 0) {
                    return lineTxt;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(br);
        }
        return "";
    }

    /**
     * 读取第一行内容
     *
     * @param is
     * @return
     */
    public static String readFirstLine(InputStream is) {
        return readLine(is, 1);
    }

    /**
     * 读取文件中所有内容
     *
     * @param file
     * @return
     */
    public static String readFileContent(File file) {
        if (file != null && file.exists()) {
            try {
                return readContentFromStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 读取文件中所有内容
     *
     * @param filePath
     * @return
     */
    public static String readFileContent(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            return readFileContent(new File(filePath));
        }
        return "";
    }

    /**
     * 从文件中读取特殊字符串开头的所有行
     *
     * @param filePath
     * @param specialStr
     * @return
     */
    public static List<String> readAllLineStartWithSpecialString(String filePath, String specialStr) {
        List<String> result = new ArrayList<>();
        if (!TextUtils.isEmpty(filePath) && !TextUtils.isEmpty(specialStr)) {
            File file = new File(filePath);
            if (file.exists()) {
                BufferedReader bf = null;
                try {
                    bf = new BufferedReader(new FileReader(file));
                    String lineTxt;
                    while ((lineTxt = bf.readLine()) != null) {
                        if (lineTxt.startsWith(specialStr)) {
                            result.add(lineTxt);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    close(bf);
                }
            }
        }
        return result;
    }

    /**
     * 从文件中读取特殊字符串开头的一行
     *
     * @param filePath
     * @param specialStr
     * @return
     */
    public static String readLineStartWithSpecialString(String filePath, String specialStr) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists()) {
                try {
                    return readLineStartWithSpecialString(new FileReader(file), specialStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 从文件中读取特殊字符串开头的一行
     *
     * @param file
     * @param specialStr
     * @return
     */
    public static String readLineStartWithSpecialString(File file, String specialStr) {
        if (file != null && file.exists()) {
            try {
                return readLineStartWithSpecialString(new FileReader(file), specialStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 读取特殊字符串开头的一行
     *
     * @param reader
     * @param specialStr
     * @return
     */
    public static String readLineStartWithSpecialString(Reader reader, String specialStr) {
        if (reader != null && specialStr != null) {
            BufferedReader bf = null;
            try {
                bf = new BufferedReader(reader);
                String lineTxt;
                while ((lineTxt = bf.readLine()) != null) {
                    if (lineTxt.startsWith(specialStr)) {
                        return lineTxt;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close(bf);
            }
        }
        return "";
    }

    /**
     * 从文件中读取第n行的内容
     *
     * @param file
     * @param lineNum
     * @return
     */
    public static String readLine(File file, int lineNum) {
        if (file != null && file.exists()) {
            try {
                return readLine(new FileInputStream(file), lineNum);
            } catch (Throwable ignored) {
            }
        }
        return "";
    }

    /**
     * 读取文件中第一行的内容
     *
     * @param file
     * @return
     */
    public static String readFirstLine(File file) {
        return readLine(file, 1);
    }

    /**
     * 读取文件中第一行的内容
     *
     * @param filePath
     * @return
     */
    public static String readFirstLine(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            return readLine(new File(filePath), 1);
        } else {
            return "";
        }
    }

    /**
     * 将内容写入文件
     *
     * @param content
     * @param file
     * @return
     */
    public static boolean saveContentToFile(String content, File file) {
        return content == null || saveContentToFile(content.getBytes(), file);
    }

    /**
     * 将内容写入文件
     *
     * @param content
     * @param file
     * @return
     */
    public static boolean saveContentToFile(byte[] content, File file) {
        if (content == null) {
            return true;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content);
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 关闭流
     *
     * @param pCloseable
     */
    public static void close(Closeable pCloseable) {
        if (pCloseable != null) {
            try {
                pCloseable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean deleteFileIfExist(String filePath) {
        return !TextUtils.isEmpty(filePath) && deleteFileIfExist(new File(filePath));
    }

    /**
     * 如果文件存在则删除文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFileIfExist(File file) {
        return file == null || !file.exists() || file.delete();
    }

    /**
     * 删除目录里面所有文件
     *
     * @param dir
     */
    public static void deleteAllFiles(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteAllFiles(f);
                }
                deleteFileIfExist(f);
            }
        }
    }

    /**
     * create filePath if not exists,
     * append content to filePath
     *
     * @param filePath
     * @param content
     */
    public static void append(String filePath, String content) {
        try {
            FileWriter fw = new FileWriter(filePath, true);
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            LogUtil.e(TAG, e);
        }
    }

    public static String readFromInternal(Context context, String virtual_apps) {
        try {
            FileInputStream fileInputStream = context.openFileInput(virtual_apps);
            return readContentFromStream(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFromAssets(Context context, String fileName) {
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            return readContentFromStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean write2Internal(Context context, String fileName, String content) {
        OutputStream outStream = null;
        try {
            outStream = new BufferedOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outStream.write(content.getBytes());
            outStream.flush();
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean decompressZipFile(File zip, File destDir) {
        if (zip == null || !zip.exists() || destDir == null || (destDir.exists() && destDir.isFile())) {
            return false;
        }
        makeDirIfNeed(destDir);
        try {
            ZipFile zf = new ZipFile(zip);
            Enumeration entries = zf.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                Log.d("decompressZipFile", entry.getName());
                if (entry.isDirectory()) {
                    makeDirIfNeed(new File(destDir, entry.getName()));
                } else {
                    // 表示文件
                    File f = new File(destDir, entry.getName());
                    makeDirIfNeed(f.getParent());
                    InputStream is = zf.getInputStream(entry);
                    FileOutputStream os = new FileOutputStream(f);
                    copy(is, os, -1);
                    close(is);
                    close(os);
                }
            }
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 文件是否存在
     *
     * @param filePathName
     * @return
     */
    public static boolean isFileExist(String filePathName) {
        if (TextUtils.isEmpty(filePathName)) {
            return false;
        }
        File file = new File(filePathName);
        return (!file.isDirectory() && file.exists());
    }

    /**
     * 根据文件路径取文件名
     *
     * @param filePathName
     * @return
     */
    public static String getFileName(String filePathName) {
        if (!TextUtils.isEmpty(filePathName)) {
            File file = new File(filePathName);
            if (!file.isDirectory() && file.exists()) {
                return file.getName();
            }
        }
        return "";
    }

    public static File moveFileCheckFileNameLength(String filePath, String newDirPath, String fileName,
                                                   String suffix) {
        if (filePath == null || filePath.length() == 0 || newDirPath == null
                || newDirPath.length() == 0 || suffix == null) {
            return null;
        }
        try {
            //拷贝文件
            //File file = copyFile(filePath, newDirPath, fileName, suffix);
            //检测文件长度,不能总文件长度不能超过255
            if (fileName.length() > 255 - suffix.length()) {
                fileName = fileName.substring(0, 255 - suffix.length());
            }
            File file = new File(filePath);
            File targetFile = new File(newDirPath, fileName + suffix);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            if (targetFile.exists()) {
                removeFile(targetFile.getAbsolutePath());
            }

            boolean renameTo = file.renameTo(targetFile);
            LogUtil.d(TAG, "----" + fileName + "suffix" + suffix + "---" + file.exists() + "---"
                    + file.getAbsolutePath() + " ----" + targetFile.getAbsolutePath() + "----"
                    + renameTo + " ----" + targetFile.exists() + "---" + targetFile.getName().length());
            //删除原文件
            //removeFile(filePath);
            return targetFile;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, "----" + e.getMessage() + "---" + e);
            //删除原文件
        }

        return null;

    }

    /**
     * 删除文件
     */
    public static boolean removeFile(String filePath) {
        if (filePath == null || filePath.length() == 0) {
            return false;
        }
        try {
            File file = new File(filePath);
            if (file.exists()) {
                removeFile(file);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * 循环删除
     */
    private static void removeFile(File file) {
        //如果是文件直接删除
        if (file.isFile()) {
            file.delete();
            return;
        }
        //如果是目录，递归判断，如果是空目录，直接删除，如果是文件，遍历删除
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                removeFile(f);
            }
            file.delete();
        }
    }

    /**
     * 移动文件到指定目录下
     * <p>
     * reName 效率更高
     */
    public static File moveFile(String filePath, String newDirPath, String fileName,
                                String suffix) {
        if (filePath == null || filePath.length() == 0 || newDirPath == null
                || newDirPath.length() == 0) {
            return null;
        }
        try {
            //拷贝文件
            //File file = copyFile(filePath, newDirPath, fileName, suffix);
            File file = new File(filePath);
            File targetFile = new File(newDirPath, fileName + suffix);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            if (targetFile.exists()) {
                removeFile(targetFile.getAbsolutePath());
            }

            boolean renameTo = file.renameTo(targetFile);
            LogUtil.d(TAG, "----" + fileName + "suffix" + suffix + "---" + file.exists() + "---"
                    + file.getAbsolutePath() + " ----" + targetFile.getAbsolutePath() + "----"
                    + renameTo + " ----" + targetFile.exists() + "---" + targetFile.getName().length());
            //删除原文件
            //removeFile(filePath);
            return targetFile;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, "----" + e.getMessage() + "---" + e);
            //删除原文件
        }

        return null;
    }

    /**
     * 移动文件到指定目录下
     * <p>
     * copy delete
     */
    public static File moveFileSave(String filePath, String newDirPath, String fileName,
                                    String suffix) {
        if (filePath == null || filePath.length() == 0 || newDirPath == null
                || newDirPath.length() == 0) {
            return null;
        }
        try {

            //File file = new File(filePath);
            File targetFile = new File(newDirPath, fileName + suffix);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            if (targetFile.exists()) {
                removeFile(targetFile.getAbsolutePath());
            }

            //拷贝文件
            File file = copyFile(filePath, newDirPath, fileName, suffix);

            LogUtil.d(TAG, "---" + file == null);
            LogUtil.d(TAG, "---" + file.exists());
            LogUtil.d(TAG,
                    "----" + file.getAbsolutePath() + " ----" + targetFile.getAbsolutePath() + "----"
                            + file.exists() + " ----" + targetFile.exists() + "---" + targetFile.exists());
            //删除原文件
            //removeFile(filePath);
            return targetFile;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 拷贝文件到指定目录下
     */
    public static File copyFile(String filePath, String newDirPath, String fileName,
                                String suffix) {
        if (filePath == null || filePath.length() == 0) {
            return null;
        }
        File newFile;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            //判断目录是否存在，如果不存在，则创建
            File newDir = new File(newDirPath);
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            //创建目标文件
            newFile = new File(newDirPath, fileName + suffix);
            InputStream is = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[4096];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
            return newFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件后缀名
     * <p>
     * 获取 .jpg
     */
    public static String getFormatSuffix(String fileName) {
        fileName = fileName.trim();
        int indexOf = fileName.lastIndexOf(".");
        if (indexOf != -1) {
            return fileName.substring(indexOf);
        }

        return "";
    }

    /**
     * 获取文件名
     * <p>
     * 获取 jpg
     */
    public static String getFileNameSuffix(String fileName) {
        fileName = fileName.trim();
        int indexOf = fileName.lastIndexOf(".");
        if (indexOf != -1) {
            return fileName.substring(indexOf + 1);
        }

        return "";
    }

    /**
     * 获取w文件名
     */
    public static String getFileNameNoSuffix(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return "";
        }

        String name = file.getName();
        int indexOf = name.lastIndexOf(".");
        if (indexOf >= 0) {
            return name.substring(0, indexOf);
        }
        return "";
    }

    /**
     * 获取文件名称
     */
    public static String getFormatName(String fileName) {
        fileName = fileName.trim();

        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            return file.getName();
        }

        return "";
    }

    public static boolean copyFilesStream(Context context, InputStream is, OutputStream fos) {
        boolean bRet = false;
        try {
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
            }
            fos.flush();//刷新缓冲区
            is.close();
            fos.close();
            bRet = true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("DocumentsUtils----" + e.getMessage());
        }

        return bRet;
    }

    /**
     * 检查文件目录是否存在，如果不存在，则创建
     */
    public static boolean makeDir(String path) {
        boolean success = false;
        if (!TextUtils.isEmpty(path)) {
            success = makeDir(new File(path));
        }
        return success;
    }

    /**
     * 检查文件目录是否存在，如果不存在，则创建
     */
    public static boolean makeDir(File dir) {
        if (!checkHandleFilePermission(dir.getAbsolutePath())) {
            return false;
        }
        boolean success = false;
        if (dir != null && !dir.exists()) {
            success = dir.mkdirs();
        }
        return success;
    }

    private static boolean checkHandleFilePermission(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("File Path is Empty");
        }
        String sdCardRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (filePath.startsWith(sdCardRootPath)) {
            boolean sdCardAvailable =
                    Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
            if (!sdCardAvailable) {
                return false;
            }
        }
        return true;
    }

}
