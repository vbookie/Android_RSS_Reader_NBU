package com.rssreader.utils;

/**
 * Represents data for a single word.
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 */
class Word implements Comparable<Word> {
	/**
	 * The whole word.
	 */
	public String value;
	/**
	 * The stem of the word.
	 */
	public String stem;
	/**
	 * The number of times the word is contained in an Article.
	 */
	public double termFrequency;
	
    /**
     * Creates a new instance of Word class.
     */
    Word() {
    	this(null);
    }
    
    /**
     * Creates a new instance of Word class with the specified value.
     * 
     * @param word the whole word.
     */
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
