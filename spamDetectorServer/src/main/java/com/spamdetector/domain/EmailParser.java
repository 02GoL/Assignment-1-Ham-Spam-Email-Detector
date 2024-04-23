package com.spamdetector.domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class EmailParser {
    // A function to return the word frequency all the files in a given directory
    // by getting the set of words from each file and combining them
    public Map<String,Integer> getWordFrequency(File[] fileDir){
        // Call a function with trainFileDir[x].listFile();
        Map<String,Integer> wordFrequency = new TreeMap<>();
        for(File file: fileDir){
            Map<String,Integer> fileWordFrequency = calculateFrequency(file);
            Set<String> words = fileWordFrequency.keySet();
            for(String word: words){
                int wordCount = fileWordFrequency.get(word);
                if(!wordFrequency.containsKey(word)){
                    wordFrequency.put(word,wordCount);
                }else{
                    int oldWordCount = wordFrequency.get(word);
                    wordFrequency.put(word,oldWordCount + wordCount);
                }
            }
        }
        return wordFrequency;
    }
    // Goes through a given file line by line, adding words not a part of the set
    public Map<String,Integer> calculateFrequency(File emailFile){
        Map<String,Integer> fileWordFrequency = new TreeMap<>();
        String line = null;
        boolean header = true;
        try{
            Scanner fileScanner = new Scanner(emailFile);
            while(fileScanner.hasNextLine()){
                line = fileScanner.nextLine();
                if (line == "") {
                    header = false;
                }
                if(!header) {
                    String[] wordList = line.toLowerCase().split(" ");
                    for (String word : wordList) {
                        word = filter(word);
                        if (!Objects.equals(word, "") && !fileWordFrequency.containsKey(word)) {
                            fileWordFrequency.put(word, 1);
                        }
                    }
                }
            }
            fileScanner.close();
        }catch(FileNotFoundException e){
            throw new RuntimeException(e);
        }
        return fileWordFrequency;
    }
    public String filter(String word){
        // This filter can be optimized further

        word = word.replaceAll("[^a-z']"," ")
                .replaceAll(".*\\s.*","")
                .replaceAll("^'+|'$+","")
                .replaceAll(".*(\\b\\w*?)(\\w)\\2{2,}(\\w*).*","")
                .replaceAll(".{20,}","")
                .replaceAll(".*(bq|bx|bz|cf|cg|cj|cp|cv|cw|cx|dx|fq|fv|fx|" +
                        "fz|gv|gx|hx|hz|jb|jc|jd|jf|jg|jh|jj|jl|jm|jn|jp|jq|jt|jv|jw|" +
                        "kx|kz|lx|mg|mj|mx|mz|pq|pv|px|qc|qd|qe|qf|qg|qh|qj|ql|qm|qn|" +
                        "qo|qp|qq|qr|qs|qt|qv|qw|qx|qy|qz|sx|tq|tx|vb|vc|vf|vj|vk|vm|" +
                        "vp|vq|vw|vx|vz|wj|wq|wv|wx|xd|xj|xk|xn|xr|yq|zf|zj|zx).*", "");
        return word;
    }
    // https://www.jojhelfer.com/lettercombos
    // https://stackoverflow.com/questions/37089227/how-to-remove-3-or-more-consecutive-letters-in-java-into-2-consecutive-letters
}
// bq,bx,bz,cf,cg,cj,cp,cv,cw,cx,dx,fq,fv,fx,fz,gv,gx,hx,hz,jb,jc,jd,jf,jg,jh,jj,jl,jm,jn,jp,jq,jt,jv,jw,
// jx,hy,hz,jw,jv,kx,kz,lx,mg,mj,mx,mz,pq,pv,px,qc,qd,qe,qf,qg,qh,qj,ql,qm,qn,qo,qp,qq,qr,qs,qt,qv,qw,qx,
// qy,qz,sx,tq,tx,vb,vc,vf,vj,vk,vm,vp,vq,vw,vx,vz,wj,wq,wv,wx,xd,xj,xk,xn,xr,yq,zf,zj,zx