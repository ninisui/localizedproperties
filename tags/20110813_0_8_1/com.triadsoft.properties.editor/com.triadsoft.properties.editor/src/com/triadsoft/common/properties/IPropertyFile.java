package com.triadsoft.common.properties;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;

public interface IPropertyFile {
	public void save() throws IOException, CoreException;

	public Object setProperty(String key, String value);

	public String[] getKeys();
}
