package com.rssreader.utils;

import java.util.LinkedList;

/**
 * Represents data for a single sentence.
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 */
class Sentence implements Comparable<Sentence>{
	/**
	 * A list of all words in the sentence.
	 */
	LinkedList<Word> words;	
	/**
	 * The whole sentence.
	 */
	String originalSentence;
	/**
	 * The importance of the sentence.
	 */
	double score;
	/**
	 * Whether the sentence to be in the summary.
	 */
	boolean selected;
	
	/**
	 * Creates a new instance of Sentence class.
	 */
	Sentence() {
		this.words = new LinkedList<Word>();
		this.originalSentence = "";
		this.score = 0;
		this.selected = false;
	}
	
	/**
	 * @return the number of words in the sentence.
	 */
	int wordCount() {
		return this.words.size();
	}
	
	/**
	 * Adds a new word to the sentence.
	 * 
	 * @param word the word to add.
	 */
	void addWord(Word word) {
		this.words.add(word);
	}

	@Override
	public int compareTo(Sentence sentence) {
		return (int) (this.score - sentence.score);
	}
}
