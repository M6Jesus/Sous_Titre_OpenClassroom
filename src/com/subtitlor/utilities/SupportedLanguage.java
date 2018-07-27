package com.subtitlor.utilities;

public enum SupportedLanguage {
	Original{
        public String getID() {
            return "ORG";
        }
    }, 
	Translation{
        public String getID() {
            return "TRA";
        }
    };
	
	public abstract String getID();
	
	public static SupportedLanguage getLanguage(String filename) {
		if (filename.endsWith(Key.TRANSLATION_SUFFIX)) {
			return Translation;
		}
		return Original;
	}
}