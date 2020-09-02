package com.linkingluck.midware.resource.reader.impl;

import com.linkingluck.midware.resource.reader.ResourceReader;

import java.io.InputStream;
import java.util.List;

public class PropertiesReader implements ResourceReader {
	@Override
	public String getSuffix() {
		return "properties";
	}

	@Override
	public boolean isBatch() {
		return false;
	}

	@Override
	public <E> List<E> read(Class<E> clz, InputStream inputStream) {
		return null;
	}
}
