package com.lelann.factions.utils;

import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Représente une créature (mob), comme org.bukkit.entity.CreatureType (déprécié) et rajoute un nombre conséquant d'informations non disponnible via l'API Bukkit.
 * Pour les autres entités, utiliser EntityType (Bukkit).
 * 
 * @author LeLanN
 */
public enum CreatureType {
	BLAZE("blaze", "blaze", Reaction.HOSTILE, NaturallySpawnReason.SPAWNER),
	CAVE_SPIDER("cave_spider", "arraîgnée venimeuse", Reaction.HOSTILE, NaturallySpawnReason.SPAWNER),
	CHICKEN("chicken", "poulet", Reaction.FRIENDLY, NaturallySpawnReason.NATURAL),
	COW("cow", "vache", Reaction.FRIENDLY, NaturallySpawnReason.NATURAL),
	CREEPER("creeper", "creeper", Reaction.HOSTILE, NaturallySpawnReason.NATURAL),
	ENDER_DRAGON("ender_dragon", "ender dragon", Reaction.HOSTILE, NaturallySpawnReason.GENERATION),
	ENDERMAN("enderman", "enderman", Reaction.ANGRY, NaturallySpawnReason.NATURAL),
	ENDERMITE("endermite", "endermite", Reaction.HOSTILE, NaturallySpawnReason.ENDERPEARL),
	GHAST("ghast", "ghast", Reaction.HOSTILE, NaturallySpawnReason.NATURAL),
	GIANT("giant", "zombie géant", Reaction.HOSTILE, NaturallySpawnReason.ONLY_PLUGIN),
	GUARDIAN("guardian", "guardian", Reaction.HOSTILE, NaturallySpawnReason.NATURAL),
	MAGMA_CUBE("magma_cube", "magma cube", Reaction.HOSTILE, NaturallySpawnReason.NATURAL),
	MUSHROOM_COW("mushroom_cow", "champimeuh", Reaction.FRIENDLY, NaturallySpawnReason.NATURAL),
	PIG("pig", "cochon", Reaction.FRIENDLY, NaturallySpawnReason.NATURAL),
	PIG_ZOMBIE("pig_zombie", "pigman", Reaction.ANGRY, NaturallySpawnReason.NATURAL),
	RABBIT("rabbit", "lapin", Reaction.FRIENDLY, NaturallySpawnReason.NATURAL),
	SHEEP("sheep", "mouton", Reaction.FRIENDLY, NaturallySpawnReason.NATURAL),
	SILVERFISH("silverfish", "silverfish", Reaction.HOSTILE, NaturallySpawnReason.BLOCK),
	SKELETON("skeleton", "squelette", Reaction.HOSTILE, NaturallySpawnReason.NATURAL),
	SLIME("slime", "slime", Reaction.HOSTILE, NaturallySpawnReason.NATURAL),
	SNOWMAN("snowman", "bonhomme de neige", Reaction.FRIENDLY, NaturallySpawnReason.NATURAL),
	SPIDER("spider", "arraîgnée", Reaction.HOSTILE, NaturallySpawnReason.NATURAL),
	SQUID("squid", "pieuvre", Reaction.FRIENDLY, NaturallySpawnReason.NATURAL),
	VILLAGER("villager", "villageois", Reaction.FRIENDLY, NaturallySpawnReason.NATURAL),
	WITCH("witch", "sorcière", Reaction.HOSTILE, NaturallySpawnReason.NATURAL),
	WITHER("wither", "wither", Reaction.HOSTILE, NaturallySpawnReason.BLOCK),
	WOLF("wolf", "loup", Reaction.ANGRY, NaturallySpawnReason.NATURAL),
	ZOMBIE("zombie", "zombie", Reaction.HOSTILE, NaturallySpawnReason.NATURAL);
	
	@Getter private String name, frenchName;
	@Getter private Reaction reaction;
	@Getter private NaturallySpawnReason spawnReason;
	
	/**
	 * Vérifie si la créature est amicale
	 * @return Un boolean
	 */
	public boolean isFriendly(){
		return reaction == Reaction.FRIENDLY;
	}
	
	/**
	 * Vérifie si la créature est hostile
	 * @return Un boolean
	 */
	public boolean isHostile(){
		return !isFriendly();
	}
	
	/**
	 * Récupère l'EntityType équivalent
	 * @return L'EntityType
	 */
	public EntityType bukkit(){
		return EntityType.valueOf(name());
	}
	
	/**
	 * Fait spawn une nouvelle entité du type du CreatureType à une certaine position
	 * @param l La location
	 * @return La nouvelle entité
	 */
	public Entity spawn(Location l){
		return l.getWorld().spawnEntity(l, bukkit());
	}
	
	private CreatureType(String name, String frenchName, Reaction reaction, NaturallySpawnReason spawnReason){
		this.name = name;
		this.frenchName = frenchName;
		this.reaction = reaction;
		this.spawnReason = spawnReason;
	}
	
	/**
	 * Récupère une CreatureType via une EntityType Bukkit
	 * @param bukkit L'EntityType Bukkit
	 * @return La CreatureType (si non trouvé, null)
	 */
	public static CreatureType getByBukkit(EntityType bukkit){
		for(CreatureType ct : values())
			if(bukkit == ct.bukkit())
				return ct;
		return null;
	}
	
	/**
	 * Récupère une CreatureType via une entité Bukkit
	 * @param bukkit L'entité Bukkit
	 * @return La CreatureType (si non trouvé, null)
	 */
	public static CreatureType getByBukkitEntity(Entity bukkit){
		return getByBukkit(bukkit.getType());
	}

	/**
	 * Récupère un CreatureType à partir du nom de l'entité
	 * @param name Le nom
	 * @return Le CreatureType (si non trouvé, null)
	 */
	public static CreatureType matchCreature(String name){
		for(CreatureType ct : values())
			if(ct.getName().equalsIgnoreCase(name) || ct.getFrenchName().equalsIgnoreCase(name))
				return ct;
		return null;
	}
	
	/**
	 * Représente une réaction de créature
	 * @author LeLanN
	 */
	public static enum Reaction {
		FRIENDLY,
		HOSTILE,
		ANGRY;
	}
	
	/**
	 * Représente les raisons de spawn d'une créature
	 * @author LeLanN
	 */
	public static enum NaturallySpawnReason {
		BLOCK,
		ENDERPEARL,
		GENERATION,
		NATURAL,
		ONLY_PLUGIN,
		SPAWNER;
	}
}

