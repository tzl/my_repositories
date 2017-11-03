package util;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ------------------ usage -----------------
 *
 * String dir, file;
 * FindJavaFile findProcedure = null;
 *
 * dir = "c:/myworkspace/project1/src";
 *
 * try {
 *     findProcedure = new FindJavaFile(dir, "svn.FindFile");
 * }
 * catch (ClassNotFoundException e) {
 *     e.printStackTrace();
 * }
 *
 * file = findProcedure.search(FindJavaFile.T_Extens);
 * System.out.println("java file:" + file);
 *
 * ------------------------------------------
 */
public class FindJavaFile
{
    private String _path;
    private Class<?> _clz;
    private Vector<String> _javaFiles = new Vector<String>();
    private static final FileFilter _fileFilter = JavaFileFilter.getInstance();
    /*determined by the class name itself*/
    public static final int T_Is = 0;
    /*determined by its direct supper class*/
    public static final int T_Extends = 1;
    /*determine whether the class is a instance of T.
     * (including T_Extends|T_Is)*/
    public static final int T_InstanceOf = 2;

    protected FindJavaFile() {}

    public FindJavaFile(String path, String clzName) throws ClassNotFoundException
    {
        _path = path;
        _clz = Class.forName(clzName);
    }

    public void setSearchPath(String path)
    {
        _path = path;
    }

    public void setSearchClz(String clzName) throws ClassNotFoundException
    {
        _clz = Class.forName(clzName);
    }

    public String search(int type)
    {
        String file = null;
        Class<?> tempClz;
        Iterator<?> itr;

        if (null == _path || null == _clz) {
            return null;
        }

        listClasses(_path, 0);

        try {
            itr = _javaFiles.iterator();
            while (itr.hasNext()) {
                file = (String) itr.next();
                tempClz = Class.forName(file);

                if (T_Is == type) {
                    if (tempClz == _clz) {
                        break;
                    }
                    else {
                        file = null;
                    }
                }
                else if (T_Extends == type) {
                    Class<?> cls = tempClz.getSuperclass();
                    if (cls == _clz) {
                        break;
                    }
                    else {
                        file = null;
                    }
                }
                else {
                    if (tempClz == _clz) {
                        break;
                    }

                    Class<?> cls = tempClz.getSuperclass();
                    while (cls.getName() != "java.lang.Object") {
                        if (cls == _clz) {
                            break;
                        }
                        else {
                            file = null;
                        }

                        cls = cls.getSuperclass();
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    private void listClasses(String path, int level)
    {
        File dir;
        File[] files = null;
        String name;
        int i;

        if (null != path) {
            dir = new File(path);

            files = dir.listFiles(_fileFilter);
            if (null == files || 0 == files.length) {
                return;
            }
        }

        for (i = 0; i < files.length; ++i) {
            if (files[i].isFile()) {
                name = constructClassName(files[i].getPath(), level);
                _javaFiles.add(name);
            }
            else {
                listClasses(files[i].getPath(), level + 1);
            }
        }
    }

    /**
     * constructClassName(dir/src/class1.java, 0)--> class1
     *
     * constructClassName(dir/src/com/class2.java, 1)--> com.class2
     *
     * constructClassName(dir/src/com/package1/class3.java, 1)-->
     * com.package1.class3
     *
     */
    private String constructClassName(String fileName, int level)
    {
        int index;
        String str;

        index = fileName.lastIndexOf(File.separatorChar);
        for (int i = 0; i < level; ++i) {
            index = fileName.lastIndexOf(File.separatorChar, index - 1);
        }
        str = fileName.substring(index + 1, fileName.length() - 5);

        return str.replace(File.separatorChar, ".".charAt(0));
    }
}

class JavaFileFilter implements FileFilter
{
    private static JavaFileFilter _instance = null;
    private static Pattern norm_pat = Pattern.compile(".*\\.java$",
            Pattern.CASE_INSENSITIVE);
    private static Pattern spec_pat = Pattern.compile("^(.|_)(svn|cvs)$",
            Pattern.CASE_INSENSITIVE);
    Matcher matcher = null;

    public boolean accept(File file)
    {
        Matcher matcher;
        boolean ret;

        if (file.isFile()) {
            matcher = norm_pat.matcher(file.getName());
            ret = matcher.matches();
        }
        else {
            matcher = spec_pat.matcher(file.getName());
            ;
            ret = !matcher.matches();
        }

        return ret;
    }

    public static JavaFileFilter getInstance()
    {
        if (null == _instance) {
            _instance = new JavaFileFilter();
        }

        return _instance;
    }
}
