package org.javabee.service.impl;

import java.util.Comparator;

import org.javabee.entities.JarTO;

public class JarUtilComparator {
	
	public static Comparator<JarTO> getIdComparator() {
		return new Comparator<JarTO>() {
			@Override
			public int compare(JarTO current, JarTO another) {
				if (another == null || another.getId() == null) {
					return -1;
				}
				return current.getId().compareTo(another.getId());
			}
		};
	}

	public static Comparator<JarTO> getNameComparator() {
		return new Comparator<JarTO>() {
			@Override
			public int compare(JarTO current, JarTO another) {
				if (another == null || another.getName() == null) {
					return -1;
				}
				return current.getName().compareTo(another.getName());
			}
		};
	}
	
	public static Comparator<JarTO> getVersionComparator() {
		return new Comparator<JarTO>() {
			@Override
			public int compare(JarTO current, JarTO another) {
				if (another == null || another.getVersion() == null) {
					return -1;
				}
				return current.getVersion().compareTo(another.getVersion());
			}
		};
	}
	
	public static Comparator<JarTO> getFileNameComparator() {
		return new Comparator<JarTO>() {
			@Override
			public int compare(JarTO current, JarTO another) {
				if (another == null || another.getFilename() == null) {
					return -1;
				}
				return current.getFilename().compareTo(another.getFilename());
			}
		};
	}

}
