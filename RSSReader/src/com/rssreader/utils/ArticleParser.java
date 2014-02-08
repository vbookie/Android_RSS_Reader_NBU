package com.rssreader.utils;

class ArticleParser {
	Dictionary dictionary;
	Stemmer stemmer;
	
	ArticleParser(Dictionary dictionary) {
		this.dictionary = dictionary;
		this.stemmer = new Stemmer(dictionary);
	}
	
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
        
        article.sortImportantWords();
        return article;
    }

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
    
    private boolean isImportantWord(Word word) {
    	if (this.dictionary.redundantWords.contains(word))
    		return false;
    	else
    		return true;
    }

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
