/* File: ReadabilityIndices.java
 * Name: Ben Kremer ben@kremerdesign.com
 * 
 * Enter the filename or http address to get
 * the Flesch Kincaid Grade Level score
 * and the Dale Chall Readability Score
 */

import java.util.ArrayList;
import java.io.*;
import acm.program.*;

public class ReadabilityIndices extends ConsoleProgram {
	
	private static final char[] vowels = {'a','e','i','o','u','y'};
	
	
	public void run() {
		
		while (true) {
			String filename = readLine("Enter filename ");
			if (filename.isEmpty()) break;
			if (filename.substring(0, 4).equalsIgnoreCase("http")){
				tokens = Scraper.pageContents(filename);
			} else {
				tokens = fileContents(filename);
			}
//			println("Num of Tokens: " + tokens.size());
//			for(int i = 0; i < tokens.size();i++) {
//			println(" Token #" + (i+1) + ": ["+ tokens.get(i) +"]");
//			}
			
//			println("Num of lines: " + tokens.size());
//			for(int i = 0; i < tokens.size();i++) {
//			println(" Line #" + (i+1) + ": ["+ tokens.get(i) +"]");
//			}
			
			println("Flesch: "+ fleschKincaidGradeLevelOf(tokens));
			println("Dale: "+ daleChallReadabilityScoreOf(tokens));
			
//			println(" Syllables: " + syllablesInLine(tokens));
//			println(" Words:     " + wordsInLine(tokens));
//			println(" Sentences: " + sentencesInLine(tokens));
		}
	}
	
	private double daleChallReadabilityScoreOf(ArrayList<String> lines) {
		double totWords = 0.0;
		double totSent = 0.0;
		double totDiff = 0.0;
		for (int i = 0; i < lines.size();i++) {
			totWords += wordsInLine(tokenize(lines.get(i)));
			totSent += sentencesInLine(tokenize(lines.get(i)));
			totDiff += difficultSyllablesInLine(tokenize(lines.get(i)));
		}
		if (totWords == 0){
			totWords = 1.0;
		}
		if (totSent == 0){
			totSent = 1.0;
		}
//		println(totWords);
//		println(totSent);
//		println(totDiff);
		
		double bonus = 0;
		if (totDiff/totWords >= 0.05 ){
			bonus = 1;
		}
		return 15.79*(totDiff/totWords) + 0.0496*(totWords/totSent) + 3.6365*bonus;
	}
	
	private double fleschKincaidGradeLevelOf(ArrayList<String> lines) {
		double totWords = 0.0;
		double totSent = 0.0;
		double totSylls = 0.0;
		for (int i = 0; i < lines.size();i++) {
			totWords += wordsInLine(tokenize(lines.get(i)));
			totSent += sentencesInLine(tokenize(lines.get(i)));
 			totSylls += syllablesInLine(tokenize(lines.get(i)));
		}
		if (totWords == 0){
			totWords = 1.0;
		}
		if (totSent == 0){
			totSent = 1.0;
		}
//		println(totWords);
//		println(totSent);
//		println(totSylls);
		return 0.39*(totWords/totSent) + 11.8*(totSylls/totWords) - 15.59;
	}
	
	private ArrayList<String> fileContents(String filename) {
		BufferedReader rd = openFileReader(filename);
		ArrayList<String> tokens = new ArrayList<String>();
		try {
			while (true) {
				String line = rd.readLine();
				if (line == null) break;
//				println("addinglines");
//				println(line);
				tokens.add(line);
			}
			rd.close();
		} catch (IOException ex) {
			println("oops");
		}
		return tokens;
	}
	
	private BufferedReader openFileReader(String name){
		BufferedReader rd = null;
		while (rd == null) {
			try {
				rd = new BufferedReader(new FileReader(name));
			} catch (IOException ex) {
				//throw new IOException(ex);
				name = readLine("try again?");
			}
		}
		//println("opened");
		return rd;
	}
	
	private int syllablesInLine(ArrayList<String> tokens) {
		int syllNum = 0;
		for (int i = 0; i < tokens.size();i++){
			String iToken = tokens.get(i);
			if (Character.isLetter(iToken.charAt(0))) {
				syllNum += syllablesInWord(iToken);
			}
		}
		return syllNum;
	}
	
	private int difficultSyllablesInLine(ArrayList<String> tokens) {
		int diffNum = 0;
		for (int i = 0; i < tokens.size();i++){
			String iToken = tokens.get(i);
			if (Character.isLetter(iToken.charAt(0))) {
				if (iToken.length() > 6 && (iToken.endsWith("ed") || iToken.endsWith("es"))) {
					iToken = iToken.substring(iToken.length()-1);
				}
				if (syllablesInWord(iToken) >= 3){
					diffNum++;
				}
			}
		}
		return diffNum;
	}

	private int wordsInLine(ArrayList<String> tokens) {
		int wordNum = 0;
		for (int i = 0; i < tokens.size();i++){
			String iToken = tokens.get(i);
			if (Character.isLetter(iToken.charAt(0))) {
				wordNum++;
			}
		}		
		return wordNum;
	}

	private int sentencesInLine(ArrayList<String> tokens) {
		int sentNum = 0;
		for (int i = 0; i < tokens.size();i++){
			String iToken = tokens.get(i);
			if (iToken.equals(".") || iToken.equals("?") || iToken.equals("!")){
				sentNum++;
			}
		}
		return sentNum;
	}

	private ArrayList<String> tokenize(String input) {
		ArrayList<String> strs = new ArrayList<String>();
		boolean prevChar = false; 
		int strsNum = 0;
		for(int i = 0; i < input.length();i++){
			char letterAt = input.charAt(i);
			if (!Character.isLetter(letterAt) && letterAt != '\'') {
				strs.add("" + letterAt);
				strsNum++;
				prevChar = false;
			} else {
				if (!(prevChar)) {
					strsNum++;
					strs.add("");
				}
				if (strsNum != 0) {
				strsNum--;
				}
				String setter = strs.get(strsNum) + letterAt;
				strs.set(strsNum, setter);
				strsNum++;
				prevChar = true;
			}
		}
		return strs;
	}

	
	/**
	 * Given a word, returns an estimate of the number of syllables in that word.
	 * 
	 * @param word The word in question.
	 * @return An estimate of the number of syllables in the word.
	 */
	private int syllablesInWord(String word) {
		int syls = 0;
		for (int i = 0; i < word.length(); i++) {
			char ch = word.charAt(i);
			for (int j = 0; j < vowels.length; j++) {
				if (ch == vowels[j]) {
					syls++;
					for (int k = 0; k < vowels.length; k++) {
						if (i != word.length()-1 && word.charAt(i+1) == vowels[k]) {
							syls--;
						}
					}
				}
			}
			if (ch == 'e' && i == word.length()-1) {
				syls--;
			}		
		}
		if (syls == 0) {
			return 1;
		}
		return syls;
	}
	
	
	private ArrayList<String> tokens = new ArrayList<String>();

}