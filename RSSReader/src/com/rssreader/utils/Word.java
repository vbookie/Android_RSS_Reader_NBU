package com.rssreader.utils;

class Word implements Comparable<Word> {
	public String value;
	public String stem;
	public double termFrequency;
	
    Word() {
    	this(null);
    }
    Word(String word) {
    	this.value = word;
    	this.stem = null;
    	this.termFrequency = 0;
    }

    @Override
    public boolean equals(Object o) {
    	if (o == null || o.getClass() != this.getClass()) {
    		return false;
    	}
    	Word word = (Word)o;
    	return this.stem.equalsIgnoreCase(word.stem);
    }
    
    @Override
    public int hashCode() {
    	return this.value.hashCode();
    }
    
    @Override
    public String toString() {
    	return this.value;
    }
    
	@Override
	public int compareTo(Word another) {
		Word word = (Word)another;
		return Double.compare(this.termFrequency, word.termFrequency);
	}
}
