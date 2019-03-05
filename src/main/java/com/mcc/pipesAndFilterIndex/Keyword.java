package com.mcc.pipesAndFilterIndex;

import java.util.HashSet;
import java.util.Set;

public class Keyword {
  private String word;
  private Set<Integer> page;

  Keyword() {
    page = new HashSet<>();
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public void addPage(int pageNumber) {
    this.page.add(pageNumber);
  }

  public void addPages(Set<Integer> pageNumber) {
    for (Integer item : pageNumber) {
      this.addPage(item);
    }
  }


  public Set<Integer> getPages() {
    return page;
  }

  public void setPage(Set<Integer> page) {
    this.page = page;
  }
}
