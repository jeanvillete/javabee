/**
 * 
 */
package org.javabee.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.javabee.client.common.JavaBeeConstants;

/**
 * @author Jean Villete
 *
 */
public class JarClassLoader extends ClassLoader {
	
    private Hashtable<String, Class<?>> classes = null; //used to cache already defined classes

    public JarClassLoader(File folder) throws IOException, ClassNotFoundException {
        super(JarClassLoader.class.getClassLoader()); //calls the parent class loader's constructor
        
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
        	throw new IllegalArgumentException("The argument folder is null, doesn't exist or is not a directory");
        }
        this.classes = new Hashtable<String, Class<?>>();
        for (File jarFile : folder.listFiles(new JarFilter())) {
        	JarFile jar = new JarFile(jarFile);
        	JarEntry entry = null;
        	Enumeration<JarEntry> jarEntries = jar.entries();
            while ((entry = jarEntries.nextElement()) != null) {
            	if (entry.isDirectory() || !entry.getName().toLowerCase().endsWith(JavaBeeConstants.CLASS_EXTENSION))  continue;
            	InputStream is = jar.getInputStream(entry);
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                int nextValue = is.read();
                while (-1 != nextValue) {
                    byteStream.write(nextValue);
                    nextValue = is.read();
                }
                byte classByte[] = byteStream.toByteArray();
                Class<?> result = defineClass(entry.getName(), classByte, 0, classByte.length, null);
                this.classes.put(entry.getName(), result);
            }
        }
    }
    
    private class JarFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(JavaBeeConstants.JAR_EXTENSION);
		}
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return findClass(className);
    }

    @Override
    public Class<?> findClass(String className) {
        if (this.classes.containsKey(className)) {
        	return this.classes.get(className);
        }
        try {
            return super.findSystemClass(className);
        } catch (Exception e) {
        	return null;
        }
    }
}
