package net.sourceforge.jfox.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */

public class FileFilterUtils {

    /**
     * FileFilterUtils is not normally instantiated.
     */
    private FileFilterUtils() {

    }

    public static FileFilter prefixFileFilter(final String... prefixes) {
        return new FileFilter() {
            public boolean accept(File pathname) {
                for(String prefix : prefixes){
                    if(pathname.getName().startsWith(prefix)) {
                        return true;
                    }
                }
                return false;
            }
        };

    }

    public static FileFilter suffixFileFilter(final String... suffixes) {
        return new FileFilter() {
            public boolean accept(File pathname) {
                for(String suffix : suffixes){
                    if(pathname.getName().endsWith(suffix)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Returns a filter that returns true if the filename matches the specified
     * text.
     *
     * @param name the filename
     * @return a name checking filter
     */
    public static FileFilter nameFileFilter(final String name) {
        return new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().equals(name);
            }
        };
    }

    public static FileFilter regexFileFile(final String regex) {
        return new FileFilter() {
            public boolean accept(File pathname) {
                return Pattern.matches(regex,pathname.getName());
            }
        };
    }

    /**
     * Returns a filter that checks if the file is a directory.
     *
     * @return directory file filter
     */
    public static FileFilter directoryFileFilter() {
        return new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        };
    }

    public static FileFilter sizeLtFileFilter(final long size) {
        return new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.length() < size;
            }
        };
    }

    public static FileFilter sizeGtFileFilter(final long size) {
        return new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.length() > size;
            }
        };
    }

    public static FileFilter sizeEqualFileFilter(final long size) {
        return new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.length() == size;
            }
        };
    }

    // -----------------------------------------------------------------------

    /**
     * Returns a filter that ANDs the two specified filters.
     *
     * @param filters and filters
     * @return a filter that ANDs the two specified filters
     */
    public static FileFilter and(final FileFilter... filters) {
        return new FileFilter() {
            public boolean accept(File pathname) {
                boolean result = true;
                for (FileFilter filter : filters) {
                    result = result && filter.accept(pathname);
                }
                return result;
            }
        };
    }

    /**
     * Returns a filter that ORs the two specified filters.
     *
     * @param filters or filters
     * @return a filter that ORs the two specified filters
     */
    public static FileFilter or(final FileFilter... filters) {
        return new FileFilter() {
            public boolean accept(File pathname) {
                boolean result = false;
                for (FileFilter filter : filters) {
                    result = (result || filter.accept(pathname));
                }
                return result;
            }
        };
    }

    /**
     * Returns a filter that NOTs the specified filter.
     *
     * @param filter the filter to invert
     * @return a filter that NOTs the specified filter
     */
    public static FileFilter not(final FileFilter filter) {
        return new FileFilter() {
            public boolean accept(File pathname) {
                return !filter.accept(pathname);
            }
        };
    }

    public static void main(String[] args) {

    }
}
