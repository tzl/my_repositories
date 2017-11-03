/**
 *@file EclipseDocDetector.java 
 */
package main;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import misc.EclipseDocHanlder;
import util.zip.Zip;

public class EclipseDocDetector
{
	private static EclipseDocHanlder handler = null;
    private static final Pattern pat = Pattern.compile("(org.eclipse.*)\\.jar$", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args)
    {
    	String pluginDir, jarFile, toDir;
    	File files[], file;
    	Matcher matcher;
    	int i;

    	if (2 > args.length) {
    		System.out.println("usage:\n\t java EclipseDocDetector EclipsePluginDir toDir");
    		return;
    	}

    	pluginDir = args[0];
    	if (!pluginDir.endsWith(File.separator)) {
    		pluginDir = pluginDir.concat(File.separator);
    	}

    	toDir = args[1];
    	if (!toDir.endsWith(File.separator)) {
    		toDir = toDir.concat(File.separator);
    	}

    	File dir = new File(pluginDir);
    	if (!dir.isDirectory()) {
    		System.out.println(pluginDir.concat(" is not a directory."));
    		return;
    	}

    	dir = new File(toDir);
    	if (!dir.isDirectory() && !dir.mkdirs()) {
    		System.out.println("failed to create destination directory.");
    		return;
    	}

    	try {
			handler = new EclipseDocHanlder(toDir);
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}

		dir = new File(pluginDir);
    	files = dir.listFiles();
    	for (i = files.length - 1; 0 <= i; --i) {
    		file = files[i];

    		if (file.isFile()) {
    			jarFile = files[i].getName();
    			matcher = pat.matcher(jarFile);
    			if (matcher.matches()) {
    				handler.setExtractFolder(toDir.concat(matcher.group(1)));
    				Zip.processEntries(pluginDir + jarFile, handler);
    			}
    		}
    	}
    }
}
