package com.beintoo.wrappers;

import java.util.List;

public class VgoodChooseOne {
	public VgoodChooseOne() {
	}

	List<Vgood> vgoods;

	public VgoodChooseOne(List<Vgood> vgoods) {
		this.vgoods = vgoods;
	}

	public List<Vgood> getVgoods() {
		return vgoods;
	}

	public void setVgoods(List<Vgood> vgoods) {
		this.vgoods = vgoods;
	}

}
