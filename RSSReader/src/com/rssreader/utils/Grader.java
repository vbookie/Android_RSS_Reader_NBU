package com.rssreader.utils;

class Grader {
	public static void gradeArticle(Article article) {              
        gradeSentences(article);
        applySentenceFactors(article);
	}
	
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
	
    private static void applySentenceFactors(Article article)
    {
        //grade the first sentence of the article higher.
        article.sentences.get(0).score *= 2;

        //grade first sentence of new paragraphs (denoted by two \n in a row) higher
        for (Sentence sentence : article.sentences)
        {
            if (sentence.words.size() < 2)
            	continue;
            if (sentence.words.getFirst().value.contains("\n") && sentence.words.get(1).value.contains("\n"))
            {
                sentence.score *= 1.6;
            }
        }
    }
}
