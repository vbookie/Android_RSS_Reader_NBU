package com.rssreader.utils;

import java.util.Collections;
import java.util.LinkedList;

class Article {
	LinkedList<Sentence> sentences;
    LinkedList<Word> importantWords;
    int wordCount;
    
    Article() { 
        this.sentences = new LinkedList<Sentence>();
        this.importantWords = new LinkedList<Word>();
        this.wordCount =  0;
    }
    
    void addSentence(Sentence newSentence) {
    	this.sentences.add(newSentence);
    }
    
    void addImportantWord(Word word) {
    	this.importantWords.add(word);
    }

	void sortImportantWords() {
		Collections.sort(this.importantWords);
	}
}
