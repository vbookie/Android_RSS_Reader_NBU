package com.rssreader.utils;

/**
 * A class used to grade sentences in an article.
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 */
class Grader {
	/**
	 * Grades all sentences in a given article.
	 * 
	 * @param article the article to grade.
	 */
	public static void gradeArticle(Article article) {              
        gradeSentences(article);
        applySentenceFactors(article);
	}
	
	/**
	 * Grades all sentences in a given article by the number of important words.
	 * 
	 * @param article the article to grade.
	 */
	private static void gradeSentences(Article article)
    {
        for (Sentence sentence : article.sentences)
        {
            for (Word word : sentence.words)
            {
                if (article.importantWords.contains(word))
                	sentence.score++;
            }
        }
    }
	
    /**
     * Adds some additional points to specific sentences.
     * 
     * @param article
     */
    private static void applySentenceFactors(Article article)
    {
        //grade the first sentence of the article higher.
        article.sentences.get(0).score *= 2;
    }
}
