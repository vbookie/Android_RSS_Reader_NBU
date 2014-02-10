package com.rssreader.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

/**
 * A class used to get extract meaningful parts of a word.
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 */
class Stemmer {
	private Dictionary dictionary;
	
	/**
	 * Creates a new instance of Stemmer class.
	 * 
	 * @param dictionary the dictionary to use.
	 */
	public Stemmer(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	/**
	 * Extracts the meaningful information from a given 'raw' word.
	 * 
	 * @param word the 'raw' word.
	 * @return an instance of Word class.
	 */
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

	/**
	 * Extracts the value part from a given 'raw' word.
	 * 
	 * @param word the 'raw' word.
	 * @return the value.
	 */
	private String extractValue(String word)
    {
        word = stripPrefix(word, dictionary.step1PrefixRules);
        word = stripSuffix(word, dictionary.step1SuffixRules);
        return word;
    }
	
    /**
     * Extracts the stem from a given word value.
     * 
     * @param wordValue the word value.
     * @return the stem.
     */
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

    /**
     * Removes a given set of prefixes from a word.
     * 
     * @param word the word.
     * @param prefixRules the set of prefixes that should be removed.
     * @return a word without the prefixes.
     */
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

    /**
     * Removes a given set of suffixes from a word.
     * 
     * @param word the word.
     * @param suffixRules the set of suffixes that should be removed.
     * @return a word without the suffixes.
     */
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

    /**
     * Replaces a word with a word from a given set of words.
     * 
     * @param word the word to try to replace.
     * @param replacementRules the set of words that can replace the given word.
     * @return either the original word, or a word from the given set.
     */
    private String replaceWord(String word, HashMap<String, String> replacementRules)
    {
    	if (replacementRules.containsKey(word)) {
    		return replacementRules.get(word);
    	}
        return word;
    }
}
