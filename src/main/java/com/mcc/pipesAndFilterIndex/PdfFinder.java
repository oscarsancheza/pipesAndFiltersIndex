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

public class PdfFinder extends Filter {

  private String path;

  public PdfFinder(Pipe in, Pipe out, String path) {
    super(in, out);
    this.path = path;
  }

  private List<String> find(List<String> words) {
    List<String> wordsPages = new ArrayList<>();

    try {
      PDDocument document = PDDocument.load(new File(path));
      PDFTextStripper reader = new PDFTextStripper();
      String pageText;
      KeywordsUtils KeywordsUtils = new KeywordsUtils();

      StringBuilder wordsFormat;

      for (String item : words) {
        wordsFormat = new StringBuilder(item);
        wordsFormat.append(" ,pages:");
        for (int i = 1; i <= document.getNumberOfPages(); i++) {
          reader.setStartPage(i);
          reader.setEndPage(i);
          pageText = reader.getText(document);
          if (pageText != null && !pageText.isEmpty()) {

            pageText = pageText.replaceAll("\n", " ");

            if (pageText.contains(item)
                || pageText.contains(item.toLowerCase())
                || pageText.contains(item.toUpperCase())) {
              wordsFormat.append(i);
              wordsFormat.append(" ");
            }

            KeywordsUtils.savePage(pageText, i);
          }
        }
        wordsPages.add(wordsFormat.toString());
      }

    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("KWIC Error: No se encontro el archivo a leer.");
    }

    return wordsPages;
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
          lines.add(line.replaceAll("\n", ""));
          writer.reset();
        }
        c = input.read();
      }

      if (!lines.isEmpty()) {

        List<String> keywords = find(lines);

        if (!keywords.isEmpty()) {
          for (String word : keywords) {
            String shift = word + '\n';

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