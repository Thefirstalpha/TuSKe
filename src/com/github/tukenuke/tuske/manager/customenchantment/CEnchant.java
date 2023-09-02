package com.github.tukenuke.tuske.manager.customenchantment;

public class CEnchant {

	private final int clevel;
	private final CustomEnchantment cenchant;
	public CEnchant(CustomEnchantment ce, int level){
		cenchant = ce;
		clevel = level;
	}
	public int getLevel(){
		return clevel;
	}
	public CustomEnchantment getEnchant(){
		return cenchant;
	}
}
