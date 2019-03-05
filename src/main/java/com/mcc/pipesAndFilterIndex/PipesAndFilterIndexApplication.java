package com.mcc.pipesAndFilterIndex;

import com.mcc.*;

import java.io.FileInputStream;
import java.io.IOException;

public class PipesAndFilterIndexApplication {

  private static final String pathPDF = "./src/main/java/com/mcc/pipesAndFilterIndex/ejemplo.pdf";
  private static final String path = "./src/main/java/com/mcc/pipesAndFilterIndex/input.txt";
  private static final String pathWords = "./src/main/java/com/mcc/pipesAndFilterIndex/input_finder.txt";

  public void executeKeyWordsGenerator(Filter.onFinishListener finishListener) {

    try {
      Pipe inToKw = new Pipe();
      Pipe kwToPdfS = new Pipe();
      Pipe alphabetizerToOutput = new Pipe();

      FileInputStream in = new FileInputStream(path);

      Input input = new Input(in, inToKw);
      KeywordsGenerator keywordsGenerator = new KeywordsGenerator(inToKw, kwToPdfS);
      Alphabetizer alpha = new Alphabetizer(kwToPdfS, alphabetizerToOutput);
      Output output = new Output(alphabetizerToOutput, finishListener);

      input.start();
      keywordsGenerator.start();
      alpha.start();
      output.start();
    } catch (IOException exc) {
      exc.printStackTrace();
    }
  }

  public void executeFinder(Filter.onFinishListener finishListener) {

    try {
      Pipe inToKw = new Pipe();
      Pipe kwToPdfS = new Pipe();
      Pipe alphabetizerToOutput = new Pipe();

      FileInputStream in = new FileInputStream(pathWords);

      Input input = new Input(in, inToKw);
      PdfFinder pdfFinder = new PdfFinder(inToKw, kwToPdfS, pathPDF);
      Alphabetizer alpha = new Alphabetizer(kwToPdfS, alphabetizerToOutput);
      Output output = new Output(alphabetizerToOutput, finishListener);

      input.start();
      pdfFinder.start();
      alpha.start();
      output.start();
    } catch (IOException exc) {
      exc.printStackTrace();
    }
  }

  public static void main(String[] args) {
    PipesAndFilterIndexApplication app = new PipesAndFilterIndexApplication();
    app.executeFinder(System.out::println);
  }
}
