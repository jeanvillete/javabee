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
				return current.getId().toLowerCase().compareTo(another.getId().toLowerCase());
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
				return current.getName().toLowerCase().compareTo(another.getName().toLowerCase());
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
				return current.getVersion().toLowerCase().compareTo(another.getVersion().toLowerCase());
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
				return current.getFilename().toLowerCase().compareTo(another.getFilename().toLowerCase());
			}
		};
	}

}
