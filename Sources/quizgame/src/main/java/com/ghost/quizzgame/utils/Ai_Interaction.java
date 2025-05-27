package com.ghost.quizzgame.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Ai_Interaction {
    private static final String API_KEY = "AIzaSyBcaj5jfdgyLvJkLNTPQIuRocj63asdPV8";  
    private static final String ENDPOINT =  "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY;

    /*
     * Envoie un prompt à Gemini et retourne la reponse brute en json
     * 
     * @param prompt Le texte d'instruction à envoyer a L'IA.
     * @return La reponse brute JSON de gemini sous forme de String
     */

}
