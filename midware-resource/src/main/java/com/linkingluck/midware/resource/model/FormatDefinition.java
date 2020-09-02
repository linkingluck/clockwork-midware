package com.linkingluck.midware.resource.model;

public class FormatDefinition {

	private String defaultFormat;

	private String defaultLocation;

	private String defaultSuffix;

	public static FormatDefinition valueOf(String defaultFormat, String defaultLocation, String defaultSuffix) {
		FormatDefinition vo = new FormatDefinition();
		vo.defaultFormat = defaultFormat;
		vo.defaultLocation = defaultLocation;
		vo.defaultSuffix = defaultSuffix;
		return vo;
	}

	public String getDefaultFormat() {
		return defaultFormat;
	}

	public String getDefaultLocation() {
		return defaultLocation;
	}

	public String getDefaultSuffix() {
		return defaultSuffix;
	}
}
