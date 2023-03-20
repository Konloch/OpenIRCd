package com.konloch.irc.server.data.serializer;

import com.konloch.util.FastStringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Konloch
 * @since 3/20/2023
 */
class CollectionDeserializer
{
	private final String[] lines;
	private String keyClass;
	private String keyValue = null;
	
	private String valueVariableName;
	private String valueClass;
	private String valueValue;
	
	private Object dynamicBuild = null;
	
	private boolean readingKey = true;
	private boolean keyIsObject = false;
	private boolean valueIsObject = false;
	
	private final ArrayList<Object> parents = new ArrayList<>();
	private final LinkedList<Object> builtInstances = new LinkedList<>();
	private final ArrayList<String> childValueVariableNames = new ArrayList<>();
	private final ArrayList<StringBuilder> childDynamicBuilds = new ArrayList<>();
	private int childDynamicBuildIndex = -1;
	
	CollectionDeserializer(String lines)
	{
		this.lines = FastStringUtils.split(lines, "\n");
	}
	
	public void deserializeHashMap(HashMap map)
	{
		for(String line : lines)
		{
			if(childDynamicBuildIndex >= 0)
			{
				if(line.endsWith("{"))
				{
					StringBuilder sb = new StringBuilder();
					String[] split = FastStringUtils.split(line, "=", 2);
					String valueVariableName = split[0];
					String parsed = split[1];
					
					sb.append(parsed).append("\n");
					childValueVariableNames.add(valueVariableName);
					childDynamicBuilds.add(sb);
					
					parents.add(dynamicBuild);
					childDynamicBuildIndex++;
				}
				else if(line.equals("}"))
				{
					//TODO finalize and build here
					StringBuilder sb = childDynamicBuilds.get(childDynamicBuildIndex);
					String childValueVariableName = childValueVariableNames.get(childDynamicBuildIndex);
					Object parent = parents.get(childDynamicBuildIndex);
					
					boolean depth = childDynamicBuildIndex >= 1;
					
					//decouple
					childValueVariableNames.remove(childDynamicBuildIndex);
					childDynamicBuilds.remove(childDynamicBuildIndex);
					parents.remove(childDynamicBuildIndex);
					childDynamicBuildIndex--;
					
					//append end
					sb.append(line).append("\n");
					
					//create new deserializer instance
					CollectionDeserializer deserializer = new CollectionDeserializer(sb.toString());
					deserializer.readingKey = false;
					deserializer.deserializeHashMap(null);
					
					//pop the previous injection and insert now
					if(!builtInstances.isEmpty())
					{
						Object builtInstance = builtInstances.pop();
						injectValue(deserializer.dynamicBuild, childValueVariableName, builtInstance);
					}
					
					if(depth) //delay the injection for later
						builtInstances.push(deserializer.dynamicBuild);
					else //inject now
						injectValue(parent, childValueVariableName, deserializer.dynamicBuild);
				}
				else
					childDynamicBuilds.get(childDynamicBuildIndex).append(line).append("\n");
				
				continue;
			}
			
			if(line == null || line.isEmpty())
				continue;
			
			if(readingKey)
			{
				if(keyIsObject)
				{
					//EOL
					if(line.equals("}"))
					{
						keyIsObject = false;
						readingKey = false;
					}
					else
					{
						//TODO processing goes here for object based keys
					}
				}
				else if(line.endsWith("{")) //object
				{
					keyIsObject = true;
				}
				else //primitive(ish)
				{
					String[] classValue = FastStringUtils.split(line, "=", 2);
					keyClass = classValue[0];
					keyValue = classValue[1];
					readingKey = false;
				}
			}
			else
			{
				if(valueIsObject)
				{
					if(line.endsWith("{"))
					{
						StringBuilder sb = new StringBuilder();
						String[] split = FastStringUtils.split(line, "=", 2);
						String valueVariableName = split[0];
						String parsed = split[1];
						
						sb.append(parsed).append("\n");
						childValueVariableNames.add(valueVariableName);
						childDynamicBuilds.add(sb);
						parents.add(dynamicBuild);
						childDynamicBuildIndex++;
						
						//TODO it should turn on "recursive X, and each time it goes deeper into recursive, it should
						//  track the depth, then manage the deserializer, once the lines are created, run the deserializer & return the object value (dynamicBuild)
						// needs to be a linkedlist
						continue;
					}
					
					//EOL
					if(line.equals("}"))
					{
						//TODO re-casting is needed for non-string values
						if(map != null)
							map.put(keyValue, dynamicBuild);
						
						valueIsObject = false;
						readingKey = true;
					}
					else
					{
						String[] classValue = FastStringUtils.split(line, "=", 4);
						valueVariableName = classValue[0];
						valueClass = classValue[1];
						
						//enum
						if(classValue.length == 4)
						{
							valueValue = classValue[2];
							
							//inject enum values
							injectEnumValue(dynamicBuild, valueVariableName, valueValue);
						}
						//object
						else if(classValue.length == 3)
						{
							valueValue = classValue[2];
							
							//inject object
							injectValue(dynamicBuild, valueVariableName, valueValue);
						}
						//primitive(ish)
						else
						{
							//inject null
							injectValue(dynamicBuild, valueVariableName, null);
						}
					}
				}
				else if(line.endsWith("{")) //object
				{
					String[] classValue = FastStringUtils.split(line, "=", 2);
					
					try
					{
						dynamicBuild = CollectionSerializer.class.getClassLoader().loadClass(classValue[0]).newInstance();
						
						valueIsObject = true;
					}
					catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
					{
						e.printStackTrace();
					}
				}
				else //primitive(ish)
				{
					String[] classValue = FastStringUtils.split(line, "=", 2);
					valueClass = classValue[0];
					valueValue = classValue[1];
				}
			}
		}
	}
	
	private void injectValue(Object parent, String variableName, Object variableValue)
	{
		try
		{
			Field field = parent.getClass().getDeclaredField(variableName);
			Class<?> fieldType = field.getType();
			field.setAccessible(true);
			
			boolean isPrimitive = fieldType.isPrimitive();
			
			if(variableValue == null)
				field.set(parent, null);
			else if(fieldType.isAssignableFrom(String.class))
				field.set(parent, variableValue);
			else if(isPrimitive && fieldType.getName().equals("boolean") || fieldType.isAssignableFrom(Boolean.class))
				field.set(parent, Boolean.parseBoolean(String.valueOf(variableValue)));
			else if(isPrimitive && fieldType.getName().equals("byte") || fieldType.isAssignableFrom(Byte.class))
				field.set(parent, Byte.parseByte(String.valueOf(variableValue)));
			else if(isPrimitive && fieldType.getName().equals("short") || fieldType.isAssignableFrom(Short.class))
				field.set(parent, Short.parseShort(String.valueOf(variableValue)));
			else if(isPrimitive && fieldType.getName().equals("int") || fieldType.isAssignableFrom(Integer.class))
				field.set(parent, Integer.parseInt(String.valueOf(variableValue)));
			else if(isPrimitive && fieldType.getName().equals("long") || fieldType.isAssignableFrom(Long.class))
				field.set(parent, Long.parseLong(String.valueOf(variableValue)));
			else if(isPrimitive && fieldType.getName().equals("double") || fieldType.isAssignableFrom(Double.class))
				field.set(parent, Double.parseDouble(String.valueOf(variableValue)));
			else if(isPrimitive && fieldType.getName().equals("float") || fieldType.isAssignableFrom(Float.class))
				field.set(parent, Float.parseFloat(String.valueOf(variableValue)));
			else
				field.set(parent, variableValue);
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
	
	private void injectEnumValue(Object parent, String variableName, String variableValue)
	{
		try
		{
			Field field = parent.getClass().getDeclaredField(variableName);
			field.setAccessible(true);
			field.set(parent, Enum.valueOf((Class<Enum>) field.getType(), variableValue));
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
}
