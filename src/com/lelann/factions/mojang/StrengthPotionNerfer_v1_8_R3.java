package com.lelann.factions.mojang;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.bukkit.potion.PotionEffectType;

import net.minecraft.server.v1_8_R3.AttributeModifier;
import net.minecraft.server.v1_8_R3.MinecraftKey;
import net.minecraft.server.v1_8_R3.MobEffectAttackDamage;
import net.minecraft.server.v1_8_R3.MobEffectList;

public class StrengthPotionNerfer_v1_8_R3 extends MobEffectAttackDamage {
	@SuppressWarnings({ "unchecked" })
	public static void nerf(){
		MobEffectList baseEffect = MobEffectList.INCREASE_DAMAGE;
//		StrengthPotionNerfer_v1_8_R3 effect = null;

		try {
			int newModifier = Modifier.STATIC | Modifier.PUBLIC;
			Field modifierField = Field.class.getDeclaredField("modifiers");
			modifierField.setAccessible(true);

			/** PotionEffectType : byId **/
			Field byId = PotionEffectType.class.getDeclaredField("byId");
			modifierField.set(byId, newModifier);

			PotionEffectType[] byIdValuePotion = (PotionEffectType[]) byId.get(null);
			byIdValuePotion[baseEffect.getId()] = null;

			byId.set(null, byIdValuePotion);

			/** PotionEffectType : byName **/
			Field byName = PotionEffectType.class.getDeclaredField("byName");
			modifierField.set(byName, newModifier);

			Map<String, PotionEffectType> byNameValuePotion = (Map<String, PotionEffectType>) byName.get(null);
			byNameValuePotion.remove("INCREASE_DAMAGE".toLowerCase());

			byName.set(null, byNameValuePotion);
			
			/** PotionEffectType : acceptingNew **/
			Field acceptingNew = PotionEffectType.class.getDeclaredField("acceptingNew");
			modifierField.set(acceptingNew, newModifier);
			
			acceptingNew.set(null, true);
			
			/** MobEffectList : public final field **/
			StrengthPotionNerfer_v1_8_R3 effect = new StrengthPotionNerfer_v1_8_R3(baseEffect.getId(), new MinecraftKey("strength"), false, baseEffect.k());
			
			Field increaseDamage = MobEffectList.class.getField("INCREASE_DAMAGE");

			modifierField.set(increaseDamage, newModifier);
			increaseDamage.set(null, effect);
			
			acceptingNew.set(null, false);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	protected StrengthPotionNerfer_v1_8_R3(int id, MinecraftKey key, boolean badEffect, int unknow) {
		super(id, key, badEffect, unknow);
	}

	public void setDurationModifier(double duration){
		a(duration);
	}

	@Override
	public double a(int level, AttributeModifier modifier) {
		return 1.0D * (level + 1);
	}
}
