package net.sourceforge.jfox.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */

public class FileUtils {
    /**
     * The extension separator character.
     */
    private static final char EXTENSION_SEPARATOR = '.';

    /**
     * The Unix separator character.
     */
    private static final char UNIX_SEPARATOR = '/';

    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_SEPARATOR = '\\';

    /**
     * The system separator character.
     */
    private static final char SYSTEM_SEPARATOR = File.separatorChar;

    /**
     * The separator character that is the opposite of the system separator.
     */
    private static final char OTHER_SEPARATOR;

    static {
        if (SYSTEM_SEPARATOR == WINDOWS_SEPARATOR) {
            OTHER_SEPARATOR = UNIX_SEPARATOR;
        }
        else {
            OTHER_SEPARATOR = WINDOWS_SEPARATOR;
        }
    }

    /**
     * <p/> Delete a file. If file is a directory, delete it and all
     * sub-directories.
     * </p>
     * <p/> The difference between File.delete() and this method are:
     * </p>
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>You get exceptions when a file or directory cannot be deleted.
     * (java.io.File methods returns a boolean)</li>
     * </ul>
     *
     * @param file file or directory to delete.
     */
    public static boolean delete(File file) {
        if (!file.exists()) {
            return true;
        }
        if (file.isDirectory()) {
            return deleteDirectory(file);
        }
        else {
            return file.delete();
        }
    }

    /**
     * Delete a file, or a directory and all of its contents.
     *
     * @param dir The directory or file to delete.
     * @return True if all delete operations were successfull.
     */
    public static boolean deleteDirectory(File dir) {

        if (!dir.exists())
            return true;
        boolean result = cleanDirectory(dir);
        return result & dir.delete();
    }

    /**
     * Clean a directory without deleting it.
     *
     * @param directory directory to clean
     */
    public static boolean cleanDirectory(File directory) {
        if (!directory.exists()) {
            return true;
        }
        if (!directory.isDirectory()) {
            return false;
        }
        boolean result = true;

        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            result = result && delete(file);
        }
        return result;
    }

    /**
     * Schedule a file to be deleted when JVM exits. If file is directory delete
     * it and all sub-directories.
     *
     * @param file file or directory to delete.
     * @throws IOException in case deletion is unsuccessful
     */
    public static void deleteOnExit(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectoryOnExit(file);
        }
        else {
            file.deleteOnExit();
        }
    }

    /**
     * Recursively schedule directory for deletion on JVM exit.
     *
     * @param directory directory to delete.
     * @throws IOException in case deletion is unsuccessful
     */
    private static void deleteDirectoryOnExit(File directory)
            throws IOException {
        if (!directory.exists()) {
            return;
        }

        cleanDirectoryOnExit(directory);
        directory.deleteOnExit();
    }

    /**
     * Clean a directory without deleting it.
     *
     * @param directory directory to clean.
     * @throws IOException in case cleaning is unsuccessful
     */
    private static void cleanDirectoryOnExit(File directory) throws IOException {
        if (!directory.exists()) {
            // do nothing
            return;
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        IOException exception = null;

        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            try {
                deleteOnExit(file);
            }
            catch (IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    /**
     * Copy a file.
     *
     * @param source Source file to copy.
     * @param dir    Destination target dir.
     * @throws java.io.IOException Failed to copy file.
     */
    public static File copy(final File source, final File dir)
            throws IOException {
        if (source.getParentFile().equals(dir)) {
            return source;
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.isDirectory()) {
            throw new IOException("Destination must be a directory.");
        }

        File target = new File(dir, source.getName());
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                source));
        BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(target));
        try {
            IOUtils.copy(in, out);
        }
        finally {
            out.flush();
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
        return target;
    }

    /**
     * move the source file to the targe directory
     *
     * @param source
     * @param dir
     */
    public static File move(final File source, final File dir)
            throws IOException {
        File target = copy(source, dir);
        source.delete();
        return target;
    }

    /**
     * Implements the same behaviour as the "touch" utility on Unix. It creates
     * a new file with size 0 or, if the file exists already, it is opened and
     * closed without modifying it, but updating the file date and time.
     *
     * @param file the File to touch
     * @throws IOException If an I/O problem occurs
     */
    public static void touch(File file) throws IOException {
        OutputStream out = new java.io.FileOutputStream(file);
        IOUtils.closeQuietly(out);
    }

    /**
     * Convert the array of Files into a list of URLs.
     *
     * @param files the array of files
     * @return the array of URLs
     * @throws IOException if an error occurs
     */
    public static URL[] toURLs(File[] files) throws IOException {
        URL[] urls = new URL[files.length];

        for (int i = 0; i < urls.length; i++) {
            urls[i] = files[i].toURI().toURL();
        }

        return urls;
    }

    /**
     * Convert from a <code>URL</code> to a <code>File</code>.
     *
     * @param url File URL.
     * @return The equivalent <code>File</code> object, or <code>null</code>
     *         if the URL's protocol is not <code>file</code>
     */
    public static File toFile(URL url) {
        if (!url.getProtocol().equals("file")) {
            return null;
        }
        else {
            String filename = url.getFile().replace('/', File.separatorChar);
            return new File(filename);
        }
    }

    /**
     * get file content
     */
    public static String content(File file) throws IOException {
        InputStream in = new java.io.FileInputStream(file);
        try {
            return IOUtils.toString(in);
        }
        finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * list Files recursively
     *
     * @param dir    directory
     * @param filter file filter
     * @return File list
     */
    public static List<File> listFiles(File dir, FileFilter filter) {
        List<File> files = new ArrayList<File>();
        if (!dir.exists() || dir.isFile())
            return files;
        listFiles(files, dir, filter);
        return files;
    }

    private static void listFiles(List filesList, File dir, FileFilter filter) {
        File[] files = dir.listFiles(filter);
        List temp = Arrays.asList(files);
        Collections.sort(temp);
        filesList.addAll(temp);

        File[] subDirs = dir.listFiles(FileFilterUtils.directoryFileFilter());
        for (int i = 0; i < subDirs.length; i++) {
            listFiles(filesList, subDirs[i], filter);
        }
    }


    /**
     * Dump the contents of a JarArchive to the dpecified destination.
     */
    public static void extractJar(File jarFile, File dest) throws IOException {
        if (!dest.exists()) {
            dest.mkdirs();
        }
        if (!dest.isDirectory()) {
            throw new IOException("Destination must be a directory.");
        }
        JarInputStream jin = new JarInputStream(new FileInputStream(jarFile));
        byte[] buffer = new byte[1024];

        ZipEntry entry = jin.getNextEntry();
        while (entry != null) {
            String fileName = entry.getName();
            if (fileName.charAt(fileName.length() - 1) == '/') {
                fileName = fileName.substring(0, fileName.length() - 1);
            }
            if (fileName.charAt(0) == '/') {
                fileName = fileName.substring(1);
            }
            if (File.separatorChar != '/') {
                fileName = fileName.replace('/', File.separatorChar);
            }
            File file = new File(dest, fileName);
            if (entry.isDirectory()) {
                // make sure the directory exists
                file.mkdirs();
                jin.closeEntry();
            }
            else {
                // make sure the directory exists
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }

                // dump the file
                OutputStream out = new FileOutputStream(file);
                int len = 0;
                while ((len = jin.read(buffer, 0, buffer.length)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
                out.close();
                jin.closeEntry();
                file.setLastModified(entry.getTime());
            }
            entry = jin.getNextEntry();
        }
        /*
         * Explicity write out the META-INF/MANIFEST.MF so that any headers such
         * as the Class-Path are see for the unpackaged jar
         */
        Manifest mf = jin.getManifest();
        if (mf != null) {
            File file = new File(dest, "META-INF/MANIFEST.MF");
            File parent = file.getParentFile();
            if (parent.exists() == false) {
                parent.mkdirs();
            }
            OutputStream out = new FileOutputStream(file);
            mf.write(out);
            out.flush();
            out.close();
        }
        jin.close();
    }

    /**
     * use URLClassLoader.findResource, this method will not find resource use
     * parent
     *
     * @param loader
     * @param descriptorName
     * @return null if no such descriptor
     */
    public static URL getDescriptor(URLClassLoader loader, String descriptorName) {
        return loader.findResource(descriptorName);
    }

    public static Manifest getManifest(File jarFile) throws IOException {
        return new JarFile(jarFile).getManifest();
    }

    /**
     * 获得一个 URL 中所有的Class Name
     *
     * @param location url location
     * @throws IOException ioexception
     */
    public static Map<String, byte[]> getClassBytesMap(URL location) throws IOException {
        File file = toFile(location);
        return getClassBytesMap(file);
    }

    /**
     * 从文件或目录中搜索所有的 class 文件
     *
     * @param file file
     * @return classname=>class bytes
     * @throws IOException ioexception
     */
    public static Map<String, byte[]> getClassBytesMap(File file) throws IOException {
        Map<String, byte[]> contents = new HashMap<String, byte[]>();
        if (file.isDirectory()) {
            List<File> files = listFiles(file, FileFilterUtils.suffixFileFilter(".class"));
            for (File _file : files) {
                String className = _file.getPath().substring(file.getPath().length() + 1);
                className = className.replaceFirst(".class$", "");
                className = className.replace(File.separatorChar, '.');
                byte[] content = IOUtils.toByteArray(new FileInputStream(_file));
                contents.put(className,content);
            }
        }
        else {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> enu = jarFile.entries();
            try {
                while (enu.hasMoreElements()) {
                    JarEntry entry = enu.nextElement();
                    if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                        continue;
                    }
                    String className = entry.getName();
                    className = className.replaceFirst(".class$", "");
                    className = className.replace('/', '.');
                    InputStream in = null;
                    try {
                        in = new BufferedInputStream(jarFile.getInputStream(entry));
                        byte[] content = IOUtils.toByteArray(in);
                        contents.put(className, content);
                    }
                    finally {
                        if(in != null) {
                            in.close();
                        }
                    }
                }
            }
            finally {
                if(jarFile != null) {
                    jarFile.close();
                }
            }
        }
        return Collections.unmodifiableMap(contents);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getClassBytesMap(new File("framework/lib/log4j-1.2.14.jar").toURI().toURL()));
        System.out.println(getClassBytesMap(new File("framework/classes").toURI().toURL()));
    }
}
