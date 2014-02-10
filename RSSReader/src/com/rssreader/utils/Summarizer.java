package com.rssreader.utils;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import java.util.TreeSet;
import android.util.Log;

/**
 * A class used to generate summaries for articles.
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 */
public class Summarizer {
	private static final int MIN_PERCENT = 20;
	private static final int MAX_PERCENT = 100;

	private int summarizationPercent;
	private Dictionary dictionary;
	private ArticleParser parser;
	
	/**
	 * Checks whether the dictionary is actually loaded.
	 * 
	 * @return true if loaded, false otherwise.
	 */
	boolean isDictionaryAvailable() {
		return this.dictionary != null;
	}
	
	/**
	 * Creates a new instance of Summarizer class.
	 * 
	 * @param summarizationPercent what percent of the whole text the summary should be.
	 */
	public Summarizer(int summarizationPercent) {
		if(summarizationPercent > MAX_PERCENT)
			summarizationPercent = MAX_PERCENT;
        if(summarizationPercent < MIN_PERCENT)
        	summarizationPercent = MIN_PERCENT;
		
		this.summarizationPercent = summarizationPercent;
		
		this.dictionary = null;		
		try {
			this.dictionary = Dictionary.LoadFromFile();
			this.parser = new ArticleParser(dictionary);
		} catch (XmlPullParserException e) {
			Log.e("Summarizer", "Failed to parse the dictionary. Error: " + e.getMessage());
		} catch (IOException e) {
			Log.e("Summarizer", "Failed to load dictionary. Error: " + e.getMessage());
		}
	}
	
	/**
	 * Generate summary from a given article text.
	 * 
	 * @param text the article text.
	 * @return the summary.
	 */
	public String summarize(String text) {
		if (text == null)
			return null;
		
		// Remove HTML elements from text.
		text = text.replaceAll("<[^>]+>", "");
		if (text.trim().isEmpty())
			return null;
		
		Article article = this.parser.parseText(text);
		Grader.gradeArticle(article);
		String summary = getSummaryByPercent(article);
		return summary;
	}
	
	/**
	 * Generates summary for an article by percent.
	 * 
	 * @param article the article.
	 * @return the summary.
	 */
	private String getSummaryByPercent(Article article)
    {
		TreeSet<Sentence> sentencesByScore = new TreeSet<Sentence>(article.sentences);

        int requiredWordCount = (int) (article.wordCount * (summarizationPercent/100f));
        int wordsCount = 0;
        for (Sentence sentence : sentencesByScore)
        {
            if (sentence.originalSentence == null)
            	continue;
            sentence.selected = true;
            wordsCount += sentence.wordCount();
            if (wordsCount >= requiredWordCount)
            	break;
        }

        String summary = extractSummary(article);
        return summary;
    }

    @SuppressWarnings("unused")
	private static String getSummaryBySentenceCount(Article article, int requiredSentecesCount)
    {
    	TreeSet<Sentence> sentencesByScore = new TreeSet<Sentence>(article.sentences);

        int sentencesCounter = 1;
        for (Sentence sentence : sentencesByScore)
        {
            if (sentence.originalSentence == null)
            	continue;
            sentence.selected = true;
            
            if (sentencesCounter >= requiredSentecesCount)
            	break;
            sentencesCounter++;
        }
        
        String summary = extractSummary(article);
        return summary;
    }
    
    /**
     * Extracts the sentences marked as selected from the article 
     * and puts them together for the summary.
     * @param article the article the sentences belong to.
     * @return the summary.
     */
    private static String extractSummary(Article article) {
    	StringBuilder sb = new StringBuilder();
    	for (Sentence sentence : article.sentences) {
    		if (sentence.selected) {
    			sb.append(sentence.originalSentence);
    		}
    	}
    	return sb.toString();
    }
}
