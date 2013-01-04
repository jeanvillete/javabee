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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.javabee.client.common.JavaBeeConstants;

/**
 * @author Jean Villete
 *
 */
public class JarClassLoader extends ClassLoader {
	
	private File							folder = null;
	private Map<Integer, String>			knownLibraries = new HashMap<Integer, String>();
    private Hashtable<String, Class<?>> 	classes = new Hashtable<String, Class<?>>(); //used to cache already defined classes
    
    private class JarFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(JavaBeeConstants.JAR_EXTENSION);
		}
    }
    
    public JarClassLoader(ClassLoader classLoader) {
    	super(classLoader);
    	System.out.println("Executing constructor with classLoader as parameter");
    	this.folder = new File("C:\\Documents and Settings\\villjea\\Local Settings\\Temp\\JavaBeeTmpDir1824923277810117");
   		this.initialize();
    }
    
    public JarClassLoader(File folder) throws IOException, ClassNotFoundException {
        super(JarClassLoader.class.getClassLoader()); //calls the parent class loader's constructor
        
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
        	throw new IllegalArgumentException("The argument folder is null, doesn't exist or is not a directory");
        }
        this.folder = folder;
        this.initialize();
    }
    
    private void initialize() {
    	try {
    		System.out.println("starting initialize");
	    	for (File jarFile : this.folder.listFiles(new JarFilter())) {
	    		JarFile jar = new JarFile(jarFile);
	    		Enumeration<JarEntry> jarEntries = jar.entries();
	    		while (jarEntries.hasMoreElements()) {
	    			JarEntry entry = jarEntries.nextElement();
	    			if (entry.isDirectory() || !entry.getName().toLowerCase().endsWith(JavaBeeConstants.CLASS_EXTENSION)) {
	    				continue;
	    			}
	    			System.out.println("adding " + this.formatClassName(entry.getName()) + " to knownLibraries, from jar: " + jarFile.getName());
	    			this.knownLibraries.put(this.formatClassName(entry.getName()).hashCode(), jarFile.getName());
	    		}
	    	}
	    	System.out.println("initialize has been end, the knownLibraries's size: " + this.knownLibraries.size());
    	} catch (IOException e) {
    		throw new RuntimeException(e);
    	}
    }
    
    private String formatClassName(String jarEntryName) {
    	return jarEntryName.trim().replace("/", ".").replace(".class", "");
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
    	System.out.println("Executing loadClass, searching for: " + className);
    	try {
    		System.out.println("trying get the class from super.loadClass");
    		return super.loadClass(className);
		} catch (ClassNotFoundException classNotFoundException) {
			System.out.println("a ClassNotFoundException has happend from super.loadClass");
			if (this.knownLibraries.containsKey(className.hashCode())) {
	    		System.out.println("We know this library, let's try get it from knownLibraries, invoking this.findClass");
	    		return this.findClass(className);
	    	} else throw classNotFoundException;
		}
//    	
//    	System.out.println("Executing loadClass, searching for: " + className);
//    	int hashClassName = className.hashCode();
//    	 else {
//    		System.out.println("We don't know this library, invoking super.loadClass");
//    		return ;
//    	}
//    	Class<?> result = findClass(className);
//    	if (result == null) {
//    		System.out.println("Executing loadClass, No class were found, let's call super.loadClass");
//    		result = super.loadClass(className);
//    	}
//        return result;
    }

    @Override
    public Class<?> findClass(String className) {
    	System.out.println("Executing findClass, searching for: " + className);
    	
        byte classByte[];
        Class<?> result = null;  
        if (this.classes.containsKey(className)) {
        	return this.classes.get(className);
        }
        try {
        	System.out.println("trying get class from super");
            return super.findSystemClass(className);  
        } catch (ClassNotFoundException classNotFoundException) {
        	System.out.println("ClassNotFoundException from super, let's search in the ");
        	
        	try {
        		int hashClassName = className.hashCode();
        		if (this.knownLibraries.containsKey(hashClassName)) {
        			String jarFile = this.folder.getCanonicalPath() + 
        					JavaBeeConstants.FILE_SEPARATOR + 
        					this.knownLibraries.get(hashClassName);
        			JarFile jar = new JarFile(jarFile);  
        			JarEntry entry = jar.getJarEntry(className.replace(".", "/") + ".class");  
        			InputStream is = jar.getInputStream(entry);  
        			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();  
        			int nextValue = is.read();  
        			while (-1 != nextValue) {  
        				byteStream.write(nextValue);  
        				nextValue = is.read();  
        			}  
        			classByte = byteStream.toByteArray();  
        			result = super.defineClass(className, classByte, 0, classByte.length, null);  
        			this.classes.put(className, result);  
        			return result;  
        		} else throw new ClassNotFoundException("ClassNotFound by JarClassLoader");
			} catch (Exception exception) {
				exception.printStackTrace();
				throw new RuntimeException(exception);
			}
        } catch (Exception exception) {
        	exception.printStackTrace();
			throw new RuntimeException(exception);
		}
    }
    
}
