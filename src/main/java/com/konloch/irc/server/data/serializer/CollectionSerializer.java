package com.konloch.irc.server.data.serializer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This is used to serialize maps, lists or any other kind of collection.
 *
 * Disclaimer:
 * + Map keys can only be Strings
 * + Arrays, lists and hashmaps are not supported as serialized data values, only as the initial input.
 *
 * @author Konloch
 * @since 3/20/2023
 */
public class CollectionSerializer
{
	public String serialize(Map<?, ?> map)
	{
		StringBuilder sb = new StringBuilder();
		
		//iterate through each key object
		for(Object o : map.keySet())
		{
			//write key data
			serializeObject(sb, o);
			
			//grab value
			Object value = map.get(o);
			
			//write value data
			serializeObject(sb, value);
		}
		
		return sb.toString();
	}
	
	public String serialize(Collection<?> collection)
	{
		StringBuilder sb = new StringBuilder();
		
		//iterate through each value
		for(Object o : collection)
		{
			//write collection value
			serializeObject(sb, o);
		}
		
		return sb.toString();
	}
	
	public void serializeObject(StringBuilder sb, Object value)
	{
		if(value == null)
			sb.append("null").append("\n");
		else
		{
			sb.append(value.getClass().getName()).append("=");
			
			if (value instanceof String)
				sb.append(unicodeEscapeNewLineOnly((String) value)).append("\n");
			else if (value instanceof Boolean
					|| value instanceof Byte
					|| value instanceof Short
					|| value instanceof Integer
					|| value instanceof Long
					|| value instanceof Double
					|| value instanceof Float
			)
				sb.append(value).append("\n");
			else if (value instanceof Enum
			)
				sb.append(value).append("=").append("ENUM\n");
			else //write as non-primitive objects
			{
				boolean containsAnyValidFields = false;
				for(Field f : value.getClass().getDeclaredFields())
				{
					//skip all static fields
					if(Modifier.isStatic(f.getModifiers()))
						continue;
					
					//skip all transient fields
					if(Modifier.isTransient(f.getModifiers()))
						continue;
					
					containsAnyValidFields = true;
					break;
				}
				
				//skip serializing empty values
				if(!containsAnyValidFields)
					return;
				
				sb.append("{\n");
				for(Field f : value.getClass().getDeclaredFields())
				{
					//skip all static fields
					if(Modifier.isStatic(f.getModifiers()))
						continue;
					
					//skip all transient fields
					if(Modifier.isTransient(f.getModifiers()))
						continue;
					
					sb.append(f.getName()).append("=");
					
					try
					{
						//access
						f.setAccessible(true);
						
						Object obj =  f.get(value);
						
						serializeObject(sb, obj);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				sb.append("}").append("\n");
			}
		}
	}
	
	public void deserializeHashMap(String s, HashMap map)
	{
		CollectionDeserializer deserializer = new CollectionDeserializer(s);
		deserializer.deserializeHashMap(map);
	}
	
	/**
	 * Unicode escape only new line breaks for any string
	 *
	 * @param input any String to escape
	 * @return String value of the escaped input
	 */
	public static String unicodeEscapeNewLineOnly(String input)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < input.length(); i++)
		{
			char c = input.charAt(i);
			
			switch(c)
			{
				case '\n':
				case '\r':
					sb.append(unicodeEscape(c));
					break;
					
				default:
					sb.append(c);
					break;
			}
		}
		return sb.toString();
	}
	
	/**
	 * Unicode escape any string
	 *
	 * @param input any String to escape
	 * @return String value of the escaped input
	 */
	public static String unicodeEscape(String input)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < input.length(); i++)
		{
			char c = input.charAt(i);
			sb.append(unicodeEscape(c));
		}
		return sb.toString();
	}
	
	/**
	 * Unicode escape any character
	 *
	 * @param c any char to escape
	 * @return String value of the escaped character
	 */
	public static String unicodeEscape(char c)
	{
		if (c < 0x10)
			return "\\u000" + Integer.toHexString(c);
		else if (c < 0x100)
			return "\\u00" + Integer.toHexString(c);
		else if (c < 0x1000)
			return "\\u0" + Integer.toHexString(c);
		
		return "\\u" + Integer.toHexString(c);
	}
}
