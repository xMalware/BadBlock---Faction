package com.lelann.factions.scoreboard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

public class ReflectionUtils {
	/**
	 * Récupère la version Bukkit
	 * @return La version
	 */
	public static String getVersion() {
		String name = Bukkit.getServer().getClass().getPackage().getName();
		return name.substring(name.lastIndexOf('.') + 1) + ".";
	}

	/**
	 * Récupère une classe NMS
	 * @param className Le nom de la classe recherchée
	 * @return La classe trouvée
	 */
	public static Class<?> getNMSClass(String className) {
		try {
			return Class.forName("net.minecraft.server." + getVersion() + className);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Récupère une classe OBC (org.bukkit.craftbukkit)
	 * @param className Le nom de la classe à trouver
	 * @return La classe trouvée
	 */
	public static Class<?> getOBCClass(String className) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + getVersion() + className);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Récupère une classe appartenant à une autre
	 * @param clazz La classe "mère"
	 * @param name Le nom de la sous classe
	 * @return La classe trouvée
	 */
	public static Class<?> getSubClass(Class<?> clazz, String name){
		for(Class<?> c : clazz.getDeclaredClasses()) {
			if(c.getSimpleName().equals("EnumTitleAction")) {
				return c;
			}
		}	
		
		return null;
	}
	
	/**
	 * Récupère la méthode getHandle présente sur beaucoup d'objets OBC
	 * @param obj L'object en question
	 * @return Le résultat du getHandle()s
	 */
	public static Object getHandle(Object obj) {
		try {
			return getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Récupère et rend accessible un Field
	 * @param clazz Classe contenant le field
	 * @param name Nom du field
	 * @return Le Field
	 */
	public static Field getField(Class<?> clazz, String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Récupère la valeur d'un field
	 * @param object Instance de la classe contenant le field
	 * @param name Nom du field
	 * @return La valeur
	 */
	public static Object getFieldValue(Object object, String name) {
		try {
			Field field = object.getClass().getDeclaredField(name);
			field.setAccessible(true);

			return field.get(object);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Redéfinit la valeur d'un field
	 * @param instance L'objet à changer
	 * @param name Le nom du field
	 * @param value La nouvelle valeur
	 */
	public static void setFieldValue(Object instance, String name, Object value){
		try {
			Field field = instance.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(instance, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Récupère une méthode
	 * @param clazz La classe contenenant la méthode
	 * @param name Le nom de la méthode
	 * @param args Les arguments de la méthode
	 * @return La méthode
	 */
	public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
		try {
			return clazz.getMethod(name, args);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Récupère un constructeur
	 * @param clazz La classe contenenant le constructeur
	 * @param args Les arguments du constructeur
	 * @return Le constructeur
	 */
	public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... args) {
		try {
			return clazz.getConstructor(args);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
}
