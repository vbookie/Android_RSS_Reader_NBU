package com.rssreader.utils;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import java.util.TreeSet;
import android.util.Log;

public class Summarizer {
	private static final int MIN_PERCENT = 20;
	private static final int MAX_PERCENT = 100;

	private Dictionary dictionary;
	private ArticleParser parser;
	
	public Summarizer() {
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
	
	public String summarize(String text, int summarizationPercent) {
		if (text == null || text.trim().isEmpty())
			return null;
		
		if (this.dictionary == null)
			return null;
		
		// Remove HTML elements from text.
		text = text.replaceAll("<[^>]+>", "");
		
		Article article = this.parser.parseText(text);
		Grader.gradeArticle(article);
		String summary = Summarizer.getSummaryByPercent(article, summarizationPercent);
		return summary;
	}
	
	private static String getSummaryByPercent(Article article, int percent)
    {
		if(percent > MAX_PERCENT) percent = MAX_PERCENT;
        if(percent < MIN_PERCENT) percent = MIN_PERCENT;
		
		TreeSet<Sentence> sentencesByScore = new TreeSet<Sentence>(article.sentences);

        int requiredWordCount = (int) (article.wordCount * (percent/100f));
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
