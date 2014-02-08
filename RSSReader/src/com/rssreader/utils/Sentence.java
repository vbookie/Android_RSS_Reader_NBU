package com.rssreader.utils;

import java.util.LinkedList;

class Sentence implements Comparable<Sentence>{
	LinkedList<Word> words;
	String originalSentence;
	double score;
	boolean selected;
	
	Sentence() {
		this.words = new LinkedList<Word>();
		this.originalSentence = "";
		this.score = 0;
		this.selected = false;
	}
	
	int wordCount() {
		return this.words.size();
	}
	
	void addWord(Word word) {
		this.words.add(word);
	}

	@Override
	public int compareTo(Sentence sentence) {
		return (int) (this.score - sentence.score);
	}
}
