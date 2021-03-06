package com.rssreader.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

class Dictionary {
	private static Dictionary backupDictionary = null;
	
	LinkedList<Word> redundantWords;
    LinkedList<String> sentanceBreakers;
    LinkedList<String> nonSentanceBreakers;
    
    LinkedList<String> depreciateValueRule;
    LinkedList<String> termFreqMultiplierRule;

    // The replacement rules are stored as KeyValuePair<string,string>
    // The Key is the search term. The Value is the replacement term.
    HashMap<String, String> step1PrefixRules;
    HashMap<String, String> step1SuffixRules;
    HashMap<String, String> manualRules;
    HashMap<String, String> prefixRules;
    HashMap<String, String> suffixRules;
    HashMap<String, String> synonymRules;

    private Dictionary(){}

    static Dictionary LoadFromFile() throws XmlPullParserException, IOException
    {
    	if (backupDictionary != null)
    		return backupDictionary;
    	
        String dictionaryFile = "/com/rssreader/utils/dics/en.xml";
        URL dicFileURL = Dictionary.class.getResource(dictionaryFile);
        if (dicFileURL == null) {
        	throw new FileNotFoundException(dictionaryFile + " couldn't be found.");
        }
        Dictionary dictionary = new Dictionary();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser parser = factory.newPullParser();
        InputStream in = Dictionary.class.getResourceAsStream(dictionaryFile);
        parser.setInput(in, null);
        
        String tagName;
        int eventType = parser.getEventType(); 
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				tagName = parser.getName();
				DictionaryTag dictionaryTag = DictionaryTag.getValueOf(tagName);
				switch (dictionaryTag) {
					case DEPRECIATE:
						dictionary.depreciateValueRule =
							loadSingleRules(parser, dictionaryTag, DictionaryTag.RULE);
						break;
					case GRADER_TC:
						LinkedList<String> words =
							loadSingleRules(parser, dictionaryTag, DictionaryTag.WORD);
						dictionary.redundantWords = new LinkedList<Word>();
						for (String word : words) {
							Word newWord = new Word(word);
							newWord.stem = word;
							dictionary.redundantWords.add(newWord);
						}
						break;
					case GRADER_TF:
						dictionary.termFreqMultiplierRule =
							loadSingleRules(parser, dictionaryTag, DictionaryTag.WORD);
						break;
					case LINEBREAK:
						dictionary.sentanceBreakers =
							loadSingleRules(parser, dictionaryTag, DictionaryTag.RULE);
						break;
					case LINEDONTBREAK:
						dictionary.nonSentanceBreakers =
							loadSingleRules(parser, dictionaryTag, DictionaryTag.RULE);
						break;
					case MANUAL:
						dictionary.manualRules = loadKeyValueRules(parser, dictionaryTag);
						break;
					case POST:
						dictionary.suffixRules = loadKeyValueRules(parser, dictionaryTag);
						break;
					case PRE:
						dictionary.prefixRules = loadKeyValueRules(parser, dictionaryTag);
						break;
					case STEP1_POST:
						dictionary.step1SuffixRules = loadKeyValueRules(parser, dictionaryTag);
						break;
					case STEP1_PRE:
						dictionary.step1PrefixRules = loadKeyValueRules(parser, dictionaryTag);					
						break;
					case SYNONYMS:
						dictionary.synonymRules = loadKeyValueRules(parser, dictionaryTag);
						break;
					default:
						break;	
				}	
			}
			eventType = parser.next();
		}
        
		backupDictionary = dictionary;
        return dictionary;
    }
    
    // the parent tag is the tag under which there are rule tags
    private static HashMap<String, String> loadKeyValueRules(XmlPullParser parser, DictionaryTag parentTag) throws XmlPullParserException, IOException
    {
    	HashMap<String, String> rules = new HashMap<String, String>();
    	int eventType = parser.next();
    	DictionaryTag tag = DictionaryTag.getValueOf(parser.getName());
    	while(tag != parentTag) {
    		if (eventType == XmlPullParser.START_TAG && tag == DictionaryTag.RULE) {
    			if (parser.next() == XmlPullParser.TEXT) {
	    			String rule = parser.getText();
	    			int separatorIndex = rule.indexOf('|');
	    			String[] pair = new String[2];
	    			pair[0] = rule.substring(0, separatorIndex);
	    			pair[1] = "";
	    			if (++separatorIndex < rule.length())
	    				rule.substring(separatorIndex);
	    			if (!rules.containsKey(pair[0]))
	    				rules.put(pair[0], pair[1]);
    			}
    		}
    		eventType = parser.next();
    		tag = DictionaryTag.getValueOf(parser.getName());
    	}
        return rules;
    }
    
    private static LinkedList<String> loadSingleRules(XmlPullParser parser, DictionaryTag parentTag, DictionaryTag childTag) throws XmlPullParserException, IOException
    {
    	LinkedList<String> rules = new LinkedList<String>();
    	int eventType = parser.next();
    	DictionaryTag tag = DictionaryTag.getValueOf(parser.getName());
    	while(tag != parentTag) {
    		if (eventType == XmlPullParser.START_TAG && tag == childTag) {
    			if (parser.next() == XmlPullParser.TEXT) {
	    			String rule = parser.getText();
	    			if (!rules.contains(rule))
	    				rules.add(rule);
    			}
    		}
    		eventType = parser.next();
    		tag = DictionaryTag.getValueOf(parser.getName());
    	}
        return rules;
    }
    
    private enum DictionaryTag {
    	RULE,
    	WORD,
    	STEP1_PRE,
    	STEP1_POST,
    	PRE,
    	POST,
    	MANUAL,
    	SYNONYMS,
    	DICTIONARY,
    	STEMMER,
    	PARSER,
    	LINEBREAK,
    	LINEDONTBREAK,
    	GRADER_SYN,
    	GRADER_TF,
    	GRADER_TC,
    	DEPRECIATE;

    	public static DictionaryTag getValueOf(String text) {
    		if (text != null) {
    			for(DictionaryTag tag : DictionaryTag.values()) {
    				if (text.equalsIgnoreCase(tag.name()))
    					return tag;
    			}
    		}
    		return null;
    	}
    }
}
