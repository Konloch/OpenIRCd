package com.konloch.irc.server.translation;

import java.util.*;

/**
 * All of the supported languages
 *
 * TODO: Hindi, Bengali, Korean, Thai & Javanese need fonts to be supplied for them to show.
 *  The default font should be saved so it can be restored for latin-character based languages
 *
 * @author Konloch
 * @since 6/28/2021
 */
public enum Language
{
	ENGLISH("/translations/english.ini", "English", "en"),
	/*ARABIC("/translations/arabic.ini", "عربى", "ar"),
	CROATIAN("/translations/croatian.ini", "hrvatski", "hr"),
	CZECH("/translations/czech.ini", "čeština", "cs"),
	BULGARIAN("/translations/bulgarian.ini", "български", "bg"),
	DANISH("/translations/danish.ini", "dansk", "da"),
	ESTONIAN("/translations/estonian.ini", "Eesti", "et"),
	FARSI("/translations/farsi.ini", "فارسی ", "fa"),
	FINNISH("/translations/finnish.ini", "Suomen Kieli", "fi"),
	FRENCH("/translations/french.ini", "Français", "fr"),
	GERMAN("/translations/german.ini", "Deutsch", "de"),
	GEORGIAN("/translations/georgian.ini", "ქართული ენა", "ka"),
	GREEK("/translations/greek.ini", "ελληνικά", "el"),
	HAUSA("/translations/hausa.ini", "Hausa", "ha"),
	HEBREW("/translations/hebrew.ini", "עִבְרִית\u200E", "iw", "he"),
	HUNGARIAN("/translations/hungarian.ini", "Magyar Nyelv", "hu"),
	INDONESIAN("/translations/indonesian.ini", "bahasa Indonesia", "id"),
	ITALIAN("/translations/italian.ini", "Italiano", "it"),
	JAPANESE("/translations/japanese.ini", "日本語", "ja"),
	LATIVAN("/translations/lativan.ini", "Lativan", "lv"),
	LITHUANIAN("/translations/lithuanian.ini", "Lietuvių", "lt"),
	MALAY("/translations/malay.ini", "Bahasa Melayu", "ms"),
	MANDARIN("/translations/mandarin.ini", "普通话", "zh-CN", "zh_cn", "zh"),
	NEDERLANDS("/translations/nederlands.ini", "Nederlands", "nl"), //dutch
	NORWEGIAN("/translations/norwegian.ini", "Norsk", "no"),
	POLISH("/translations/polish.ini", "Polski", "pl"),
	PORTUGUESE("/translations/portuguese.ini", "Português", "pt"),
	ROMANIAN("/translations/romanian.ini", "Română", "ro"),
	RUSSIAN("/translations/russian.ini", "русский", "ru"),
	SLOVAK("/translations/slovak.ini", "Slovensky", "sk"),
	SLOVENIAN("/translations/slovenian.ini", "Slovenščina", "sl"),
	SPANISH("/translations/spanish.ini", "Español", "es"),
	SERBIAN("/translations/serbian.ini", "српски језик", "sr"),
	SWAHILI("/translations/swahili.ini", "Kiswahili", "sw"),
	SWEDISH("/translations/swedish.ini", "svenska", "sv"),
	TURKISH("/translations/turkish.ini", "Türkçe", "tr"),
	UKRAINIAN("/translations/ukrainian.ini", "украї́нська мо́ва", "uk"),
	VIETNAMESE("/translations/vietnamese.ini", "Tiếng Việt", "vi"),*/
	;
	
	private static final Map<String, Language> languageCodeLookup;
	
	static
	{
		languageCodeLookup = new LinkedHashMap<>();
		for(Language l : values())
			l.languageCode.forEach((langCode)->
					languageCodeLookup.put(langCode, l));
	}
	
	private final String resourcePath;
	private final String readableName;
	private final Set<String> languageCode;
	
	Language(String resourcePath, String readableName, String htmlIdentifier, String... languageCodes)
	{
		this.resourcePath = resourcePath;
		this.readableName = readableName;
		this.languageCode = new LinkedHashSet<>(Arrays.asList(languageCodes));
	}
	
	public String getResourcePath()
	{
		return resourcePath;
	}
	
	public String getReadableName()
	{
		return readableName;
	}
	
	public static Map<String, Language> getLanguageCodeLookup()
	{
		return languageCodeLookup;
	}
}