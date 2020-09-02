package com.linkingluck.midware.resource.excel;

import com.linkingluck.midware.resource.anno.Resource;
import com.linkingluck.midware.resource.anno.ResourceId;

@Resource(suffix = "xlsx")
public class MapResource {

	@ResourceId
	private int mapId;

	private String mapName;

	private int limitState;

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public int getLimitState() {
		return limitState;
	}

	public void setLimitState(int limitState) {
		this.limitState = limitState;
	}
}
