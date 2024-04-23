package com.spamdetector.service;

import com.spamdetector.domain.EmailParser;
import com.spamdetector.domain.TestFile;
import com.spamdetector.util.SpamDetector;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import jakarta.ws.rs.core.Response;

@Path("/spam")
public class SpamResource {

//    your SpamDetector Class responsible for all the SpamDetecting logic
    SpamDetector detector = new SpamDetector();
    List<TestFile> list;
    double accuracy;
    double precision;
    SpamResource(){
//      TODO: load resources, train and test to improve performance on the endpoint calls
        System.out.print("Training and testing the model, please wait");
//      TODO: call  this.trainAndTest();
        list = this.trainAndTest();
        accuracy = this.detector.getAccuracy(list);
        precision = this.detector.getPrecision(list);
    }
    @GET
    @Produces("application/json")
    public Response getSpamResults() {
//       TODO: return the test results list of TestFile, return in a Response object
        //basically do what you did in lab 5 after splitting and calculating everything
        return Response.status(200)
                .header("Access-Control-Allow-Origin", "http://localhost:63342")
                .header("Content-Type", "application/json")
                .entity(list).build();
    }
    // A test path that allows you to see the unique words and count of each over all files in ham
    @Path("/filename")
    @GET
    public String test(){
        String str = "";
        EmailParser e = new EmailParser();
        URL url = this.getClass().getClassLoader().getResource("/data/train/ham");
        File f = null;
        try {
            f = new File(url.toURI());
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
        Map<String,Integer> map = e.getWordFrequency(f.listFiles());
        Set<String> set = map.keySet();
        for(String word: set){
            str += "{" + word + ", " + map.get(word) + "} ";
        }
        return str;
    }
    @GET
    @Path("/accuracy")
    @Produces("application/json")
    public Response getAccuracy() {
//      TODO: return the accuracy of the detector, return in a Response object
        return Response.status(200)
                .header("Access-Control-Allow-Origin", "http://localhost:63342")
                .header("Content-Type", "application/json")
                .entity(accuracy).build();
    }

    @GET
    @Path("/precision")
    @Produces("application/json")
    public Response getPrecision() {
       //      TODO: return the precision of the detector, return in a Response object
        return Response.status(200)
                .header("Access-Control-Allow-Origin", "http://localhost:63342")
                .header("Content-Type", "application/json")
                .entity(precision).build();
    }

    private List<TestFile> trainAndTest()  {
        if (this.detector==null){
            this.detector = new SpamDetector();
        }
//        TODO: load the main directory "data" here from the Resources folder
        File mainDirectory = null;
        URL url = this.getClass().getClassLoader().getResource("/data");
        try{
            mainDirectory = new File(url.toURI());
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
        return this.detector.trainAndTest(mainDirectory);
    }
}
