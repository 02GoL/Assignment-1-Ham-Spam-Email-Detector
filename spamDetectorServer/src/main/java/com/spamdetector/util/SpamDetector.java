package com.spamdetector.util;

import com.spamdetector.domain.EmailParser;
import com.spamdetector.domain.TestFile;
import jakarta.enterprise.event.ObserverException;

import java.io.*;
import java.util.*;


/**
 * TODO: This class will be implemented by you
 * You may create more methods to help you organize you strategy and make you code more readable
 */
public class SpamDetector {
    EmailParser emailParser = new EmailParser();
    public List<TestFile> trainAndTest(File mainDirectory) {
        if (this.emailParser==null){
            this.emailParser = new EmailParser();
        }
//        TODO: main method of loading the directories and files, training and testing the model
        ArrayList<TestFile> fileList = new ArrayList<TestFile>();
        File[] mainFileDir = mainDirectory.listFiles(); // List of directories in /data

        File[] testFileDir = null;
        File[] trainFileDir = null;
        if(mainFileDir != null) {
            // testFileDir[0] = ham, testFileDir[1] = spam
            testFileDir = mainFileDir[0].listFiles(); // /data/test/ham & spam
            // trainFileDir[0] = ham, trainFileDir[1] ham2, trainFileDir[2] = spam
            trainFileDir = mainFileDir[1].listFiles(); // /data/train/ham & ham2 & spam
        }

        // wordFrequencyHam is total amount of words in the ham file
        Map<String,Integer> wordFrequencyHam = emailParser.getWordFrequency(trainFileDir[0].listFiles());
        Map<String,Integer> wordFrequencyHam2  = emailParser.getWordFrequency(trainFileDir[1].listFiles());
        // wordFrequencySpam is total amount of words in the ham file
        Map<String,Integer> wordFrequencySpam = emailParser.getWordFrequency(trainFileDir[2].listFiles());

        // Combines the frequency of words and adds new words if not found into the main map wordFrequencyHam
        Set<String> words = wordFrequencyHam2.keySet();
        for(String word: words){
            int wordCount = wordFrequencyHam2.get(word);
            if(!wordFrequencyHam.containsKey(word)){
                wordFrequencyHam.put(word,wordCount);
            }else{
                int oldWordCount = wordFrequencyHam.get(word);
                wordFrequencyHam.put(word,oldWordCount + wordCount);
            }
        }

        // calculates the spam probability of a file
        Map<String,Double>probabilty = naiveBayes(wordFrequencyHam, wordFrequencySpam, trainFileDir);

        // Need to add function that calcuates and sets the values for accuracy and precision


        // To be changed so that the spam probability is a function output
        for(File file: testFileDir[0].listFiles()){
            double spamProb = getSpamProbability(probabilty,file);
            String guess = "";
            if(spamProb >= 0.21){
                guess = "Spam";
            }else{
                guess = "Ham";
            }
            fileList.add(new TestFile(file.getName(),spamProb, guess,"Ham"));
        }
        for(File file: testFileDir[1].listFiles()){
            double spamProb = getSpamProbability(probabilty,file);
            String guess = "";
            if(spamProb >= 0.21){
                guess = "Spam";
            }else{
                guess = "Ham";
            }
            fileList.add(new TestFile(file.getName(), spamProb, guess,"Spam"));
        }

        return fileList;
    }
    public double getAccuracy(List<TestFile> myList){
        int totalGuesses = 0;
        int totalCorrectGuesses = 0;
        for(TestFile file:myList){
            if(Objects.equals(file.getGuessClass(), file.getActualClass())){
                totalCorrectGuesses += 1;
            }
            totalGuesses += 1;
        }
        return (double) totalCorrectGuesses/totalGuesses;
    }
    public double getPrecision(List<TestFile> myList){
        int totalCorrectGuesses = 0;
        double totalFalsePositive = 0;

        for(TestFile file:myList){
            if(Objects.equals(file.getActualClass(),"Ham")){
                if(Objects.equals(file.getGuessClass(),"Ham")) {
                    totalCorrectGuesses += 1;
                }else{
                    totalFalsePositive += 1;
                }
            }
        }
        return (double) totalCorrectGuesses/(totalFalsePositive+totalCorrectGuesses);
    }

    public Map<String,Double> naiveBayes(Map<String,Integer> wordFrequencyHam, Map<String,Integer> wordFrequencySpam, File[] trainFileDir){
        // takes the number of spam and ham files
        int hamFileSize = trainFileDir[0].listFiles().length + trainFileDir[1].listFiles().length;
        int spamFileSize = trainFileDir[2].listFiles().length;

        // set of keys for the Spam map
        Set<String>keys1 = wordFrequencySpam.keySet();
        // maps of probablities for all of hte words from spam and ham folders
        Map<String, Double> probabilities = new TreeMap<>();

        // going through the spam map to calculate the probabablity of every word

        for(String word:keys1){
            // checks if word is in both spam and ham folders
            if(wordFrequencyHam.containsKey(word) && wordFrequencySpam.containsKey(word)){
                // number of times "word" shows up in a ham file
                int wordCount1 = wordFrequencyHam.get(word);
                // number of times "word" shows up in a spam file
                int wordCount2 = wordFrequencySpam.get(word);
                double probOfWordInHam = (double)wordCount1/hamFileSize;
                double probOfWordInSpam = (double)wordCount2/spamFileSize;

                // calculate the probability that the file is spam based on word
                double probOfSpamFromWord = probOfWordInSpam/(probOfWordInSpam+probOfWordInHam);
                probabilities.put(word, probOfSpamFromWord);

            }else if(!wordFrequencyHam.containsKey(word)){ // or if its contained in only spam it is 100 spam
                probabilities.put(word, 1.0);
            }else{
                probabilities.put(word, 0.0);
            }
        }

        return probabilities;
    }

    // calculating whether the file is spam
    public double getSpamProbability(Map<String,Double> probability, File file){
        // creates the probability that the file is a spam file
        double fileProbabilty;
        // goes through the file, creating a map of key words, which is every word in the file
        Map<String,Integer> tempMap = emailParser.calculateFrequency(file);
        // creates a set of words from the map above
        Set<String> words = tempMap.keySet();
        // creates a set of words from the map given from the parameter
        //Set<String>wordsProb = probability.keySet();
        // initiates double variable to hold 'eta' (Greek letter)
        double n = 0;
        // iterates through the set of words from the file
        for(String word: words){
            // gets the probability that the file is spam based on the word from the map
            if(probability.containsKey(word)) {
                double tempProbability = probability.get(word);
                // uses the formula from naives bayes to get the probability on whether the file is spam or not
                double tempNumber1 = 1 - tempProbability;
                double tempNumber2 = Math.log(tempNumber1) - Math.log(tempProbability);
                n += tempNumber2;
            }
        }
        double tempNumber3 = 1 + Math.pow(Math.E,n);
        fileProbabilty = 1/tempNumber3;

        return fileProbabilty;
    }
}

