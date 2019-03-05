package com.mcc.pipesAndFilterIndex;

import com.mcc.Filter;
import com.mcc.Pipe;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KeywordsGenerator extends Filter {

  public KeywordsGenerator(Pipe in, Pipe out) {
    super(in, out);
  }

  private List<Keyword> generateKeywordsIndex(String path) {
    List<Keyword> keywords = new ArrayList<>();
    try {
      PDDocument document = PDDocument.load(new File(path));
      PDFTextStripper reader = new PDFTextStripper();
      String pageText;
      KeywordsUtils KeywordsUtils = new KeywordsUtils();

      for (int i = 1; i <= document.getNumberOfPages(); i++) {
        reader.setStartPage(i);
        reader.setEndPage(i);
        pageText = reader.getText(document);
        if (pageText != null && !pageText.isEmpty()) {
          KeywordsUtils.savePage(pageText, i);
        }
      }

      keywords.addAll(KeywordsUtils.getKeywords());

    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("KWIC Error: No se encontro el archivo a leer.");
    }

    return keywords;
  }

  @Override
  protected void transform() {
    List<String> lines = new ArrayList<>();
    CharArrayWriter writer = new CharArrayWriter();
    try {
      int c = input.read();
      while (c != -1) {
        writer.write(c);
        if (((char) c) == '\n') {
          String line = writer.toString();
          lines.add(line);
          writer.reset();
        }
        c = input.read();
      }

      if (!lines.isEmpty()) {
        String path = lines.get(0).replaceAll("\n", "");

        List<Keyword> keywords = generateKeywordsIndex(path);

        if (!keywords.isEmpty()) {
          for (Keyword word : keywords) {
            String shift = word.getWord() + ", Pages:" + word.getPages().toString() + '\n';

            char[] chars = shift.toCharArray();
            for (char aChar : chars) {
              output.write(aChar);
            }
          }
          output.closeWriter();
        }
      }
    } catch (IOException e) {
      System.err.println("KWIC Error: Ha ocurrido un error al leer los datos.");
    }
  }
}
