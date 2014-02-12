package com.rssreader.utils;

/**
 * A class used to parse text to an Article.
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 */
class ArticleParser {
	private Dictionary dictionary;
	private Stemmer stemmer;
	
	/**
	 * Creates a new instance of ArticleParser class with the specified Dictionary.
	 * 
	 * @param dictionary the dictionary to use while parsing.
	 */
	ArticleParser(Dictionary dictionary) {
		this.dictionary = dictionary;
		this.stemmer = new Stemmer(dictionary);
	}
	
    /**
     * Parses a text to an Article.
     * 
     * @param text the text of the article to parse.
     * @return An instance of an Article class.
     */
    Article parseText(String text)
    {
    	Article article = new Article();
        
        Sentence currentSentence = new Sentence();
        article.sentences.add(currentSentence);
        StringBuilder originalSentence = new StringBuilder();
        
        // Space and line feed characters mark the end of words.
        String[] allWords = text.split("( |\r)");
        for (String word : allWords)
        {
        	if (word.isEmpty())
        		continue;
        	article.wordCount++;
            if (word.startsWith("\n") && word.length() > 2)
            	word = word.replace("\n", "");
            originalSentence.append(word + " ");
            Word newWord = new Word(word);  
            currentSentence.addWord(newWord);
            this.countWordByStem(article, newWord);
            if (isLastWordOfSentence(word))
            {
                currentSentence.originalSentence = originalSentence.toString();
                currentSentence = new Sentence();
                originalSentence = new StringBuilder();
                article.addSentence(currentSentence);
            }
        }
        if (currentSentence.originalSentence == null) {
        	currentSentence.originalSentence = originalSentence.toString();
        }
        
        article.sortImportantWords();
        return article;
    }

    /**
     * Finds the stem of the given word and checks if it is an important one.
     * 
     * @param article the article being parsed.
     * @param word the word to check.
     */
    private void countWordByStem(Article article, Word word)
    {
        Word stemmedWord = this.stemmer.stemWord(word.value);
        word.stem = stemmedWord.stem;
        
        String wordValue = word.value;
        if (wordValue == null 
    		|| wordValue.isEmpty()
    		|| wordValue == " "
    		|| wordValue == "\n"
    		|| wordValue == "\t")
        	return;
        
        for (Word importantWord : article.importantWords) {
        	if (importantWord.equals(stemmedWord)) {
        		importantWord.termFrequency++;
        		return;
        	}
        }
        
        if (isImportantWord(stemmedWord))
        	article.addImportantWord(stemmedWord);
    }
    
    /**
     * Checks if a given word is important.
     * 
     * @param word the word to check.
     * @return true if important, false otherwise.
     */
    private boolean isImportantWord(Word word) {
    	if (this.dictionary.redundantWords.contains(word))
    		return false;
    	else
    		return true;
    }

    /**
     * Checks if a given word is the last in a sentence.
     * 
     * @param word the word to check for.
     * @return true if is the end of a sentence, false otherwise.
     */
    private boolean isLastWordOfSentence(String word)
    {
        if (word.contains("\r") || word.contains("\n"))
        	return true;
        
        boolean shouldBreak = endsWithLinebreaker(word);

        if (!shouldBreak)
        	return false;

        shouldBreak = !startsWithNonLinebreaker(word);
        return shouldBreak;
    }

	/**
	 * Checks if a given word end with a linebreaker.
	 * 
	 * @param word the word to check for.
	 * @return true if the word ends with a linebreaker, false otherwise.
	 */
	private boolean endsWithLinebreaker(String word) {
		int lastCharIndex = word.length() - 1;
    	String lastChar = "" +  word.charAt(lastCharIndex);
        for (String endChar : this.dictionary.sentanceBreakers) {
        	if (lastChar.equalsIgnoreCase(endChar)) {
        		return true;
        	}
        }
		return false;
	}

	/**
	 * Checks of a given word starts with non-linebreaker.
	 * 
	 * @param word the word to check for.
	 * @return true if the word starts with a non-linebreaker, false otherwise.
	 */
	private boolean startsWithNonLinebreaker(String word) {
		String firstChar = "" +  word.charAt(0);
        for (String startChar : this.dictionary.nonSentanceBreakers) {
        	if (firstChar.equalsIgnoreCase(startChar)) {
        		return true;
        	}
        }
		return false;
	}
}
