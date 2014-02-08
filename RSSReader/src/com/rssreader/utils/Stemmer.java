package com.rssreader.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

class Stemmer {
	Dictionary dictionary;
	
	public Stemmer(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	public Word stemWord(String word)
    {
        word = word.toLowerCase(Locale.ENGLISH);
        Word stemmedWord = new Word();
        String wordValue = extractValue(word);;
        stemmedWord.value = wordValue;
        stemmedWord.stem = extractStem(wordValue);
        stemmedWord.termFrequency = 1;
        return stemmedWord;
    }

	private String extractValue(String word)
    {
        word = stripPrefix(word, dictionary.step1PrefixRules);
        word = stripSuffix(word, dictionary.step1SuffixRules);
        return word;
    }
	
    private String extractStem(String wordValue)
    {
        String stem = wordValue;
        stem = replaceWord(stem, dictionary.manualRules);
        stem = stripPrefix(stem, dictionary.prefixRules);
        stem = stripSuffix(stem, dictionary.suffixRules);
        stem = replaceWord(stem, dictionary.synonymRules);
        if (stem.length() <= 2)
        	stem = wordValue;
        return stem;
    }

    private String stripPrefix(String word, HashMap<String, String> prefixRules)
    {
        for (Entry<String, String> rule : prefixRules.entrySet())
        {
            if (word.startsWith(rule.getKey()))
            {
                word = rule.getValue() + word.substring(rule.getKey().length());
            }
        }
        return word;
    }

    private String stripSuffix(String word, HashMap<String, String> suffixRules)
    {
        for (Entry<String, String> rule : suffixRules.entrySet())
        {
            if (word.endsWith(rule.getKey()))
            {
            	int wordLength = word.length();
            	int ruleLength = rule.getKey().length();
                word = word.substring(0, wordLength - ruleLength) + rule.getValue();
            }
        }

        return word;
    }

    private String replaceWord(String word, HashMap<String, String> replacementRules)
    {
    	if (replacementRules.containsKey(word)) {
    		return replacementRules.get(word);
    	}
        return word;
    }
}
