package com.mcc.pipesAndFilterIndex;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class KeywordsUtils {
  private String stopWordsPattern;
  private List<Keyword> keywords = new ArrayList<>();
  private int pageNumber = 0;
  private final int SCORE = 1;

  KeywordsUtils() {
    try {

      InputStream stream = new FileInputStream("./src/main/java/com/mcc/pipesAndFilterIndex/stopwords.txt");
      String line;

      List<String> stopWords = new ArrayList<>();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

      while ((line = bufferedReader.readLine()) != null) {
        stopWords.add(line.trim());
      }

      List<String> regexList = new ArrayList<>();

      for (String word : stopWords) {
        String regex = "\\b" + word + "(?![\\w-])";
        regexList.add(regex);
      }

      this.stopWordsPattern = String.join("|", regexList);
    } catch (Exception e) {
      throw new Error(e.toString());
    }
  }


  private List<String> getSentences(String text) {
    return Arrays.asList(text.split("[.!?,;:\\t\"()'-]|\\s-\\s"));
  }

  private String[] separateWords(String text, int size) {
    String[] split = text.split("[^a-zA-Z]");
    List<String> words = new ArrayList<>();

    for (String word : split) {
      String current = word.trim().toLowerCase();
      int len = current.length();

      if (len > size && !StringUtils.isNumeric(current)) {
        words.add(current);
      }
    }

    return words.toArray(new String[words.size()]);
  }


  private List<Keyword> getKeywords(List<String> sentences) {
    List<Keyword> phraseList = new ArrayList<>();
    Keyword keyword;
    for (String sentence : sentences) {
      String temp = sentence.trim().replaceAll(this.stopWordsPattern, "|");
      String[] phrases = temp.split("\\|");

      for (String phrase : phrases) {
        phrase = phrase.trim().toLowerCase();

        if (phrase.length() > 3) {
          keyword = new Keyword();
          keyword.addPage(this.pageNumber);
          keyword.setWord(phrase);
          phraseList.add(keyword);
        }
      }
    }

    return phraseList;
  }

  private HashMap<String, Double> calculateWordScores(List<Keyword> phrases) {
    HashMap<String, Integer> wordFrequencies = new HashMap<>();
    HashMap<String, Integer> wordDegrees = new HashMap<>();
    HashMap<String, Double> wordScores = new HashMap<>();

    for (Keyword phrase : phrases) {
      String[] words = this.separateWords(phrase.getWord(), 3);
      int length = words.length;
      int degree = length - 1;

      for (String word : words) {
        wordFrequencies.put(word, wordDegrees.getOrDefault(word, 0) + 1);
        wordDegrees.put(word, wordFrequencies.getOrDefault(word, 0) + degree);
      }
    }

    for (String item : wordFrequencies.keySet()) {
      wordDegrees.put(item, wordDegrees.get(item) + wordFrequencies.get(item));
      wordScores.put(item, wordDegrees.get(item) / (wordFrequencies.get(item) * 1.0));
    }

    return wordScores;
  }


  private List<Keyword> getCandidateKeywordScores(
      List<Keyword> phrases, HashMap<String, Double> wordScores) {
    List<Keyword> keywordCandidates = new ArrayList<>();

    for (Keyword phrase : phrases) {
      double score = 0.0;

      String[] words = this.separateWords(phrase.getWord(), 3);

      for (String word : words) {
        score += wordScores.get(word);
      }

      if (score > this.SCORE) {
        boolean find = false;
        for (Keyword item : keywordCandidates) {
          if (item.getWord().equals(phrase.getWord())) {
            item.addPages(phrase.getPages());
            find = true;
            break;
          }
        }

        if (!find) {
          keywordCandidates.add(phrase);
        }
      }
    }

    return keywordCandidates;
  }

  public void savePage(String text, int pageNumber) {
    this.pageNumber = pageNumber;
    List<String> sentences = getSentences(text);
    List<Keyword> keywords = this.getKeywords(sentences);

    for (Keyword item : keywords) {
      this.keywords.add(item);
    }

  }

  public List<Keyword> getKeywords() {
    HashMap<String, Double> wordScores = this.calculateWordScores(this.keywords);
    return this.getCandidateKeywordScores(this.keywords, wordScores);
  }
}
