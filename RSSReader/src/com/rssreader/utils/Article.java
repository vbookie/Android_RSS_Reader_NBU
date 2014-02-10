package com.rssreader.utils;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Describes an Article.
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 */
class Article {
	/**
	 * A list of all sentences in the article.
	 */
	LinkedList<Sentence> sentences;
    /**
     * A list of distinct important words.
     */
    LinkedList<Word> importantWords;
    /**
     * The number of words in the article.
     */
    int wordCount;
    
    /**
     * Creates an instance of Article class.
     */
    Article() { 
        this.sentences = new LinkedList<Sentence>();
        this.importantWords = new LinkedList<Word>();
        this.wordCount =  0;
    }
    
    /**
     * Adds a new sentence to the article.
     * 
     * @param newSentence the sentence to add.
     */
    void addSentence(Sentence newSentence) {
    	this.sentences.add(newSentence);
    }
    
    /**
     * Adds an important word to the list of important words in the article.
     * 
     * @param word an important word to add.
     */
    void addImportantWord(Word word) {
    	this.importantWords.add(word);
    }

	/**
	 * Sorts the important words by their term frequency.
	 */
	void sortImportantWords() {
		Collections.sort(this.importantWords);
	}
}
