package com.cis084javaprogramming;

import static javax.sound.sampled.AudioSystem.*;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import org.json.simple.*;
import org.json.simple.parser.*;

/**
 * Name: Brendan Bobryk
 * Student ID #: 1036738
 * Class: CIS 084 Java Programming
 * Date: 10/30/23
 * Program: Spotify-Like-App
 */

// declares a class for the app
public class SpotifyLikeApp {

  // global variables for the app
  String status;
  Long position;
  static Clip audioClip;

  private static String basePath = "C:\\Users\\Brendan\\Documents\\GitHub\\Spotify-Like-App\\spotify_like_app\\src\\main\\java\\com\\cis084javaprogramming";

  // "main" makes this class a java app that can be executed
  public static void main(final String[] args) {
    // reading audio library from json file
    JSONArray library = readAudioLibrary();

    // create a scanner for user input
    Scanner input = new Scanner(System.in);

    String userInput = "";
    while (!userInput.equals("q")) {
      menu();

      // get input
      userInput = input.next();

      // accept upper or lower case commands
      userInput = userInput.toLowerCase();

      // do something
      handleMenu(userInput, library, input);
    }

    // close the scanner
    input.close();
  }

  /*
   * displays the menu for the app
   */
  public static void menu() {
    System.out.println("---- SpotifyLikeApp ----");
    System.out.println("[H]ome");
    System.out.println("[S]earch by title");
    System.out.println("[L]ibrary");
    System.out.println("[Q]uit");

    System.out.println("");
    System.out.print("Enter q to Quit:");
  }

  /*
   * handles the user input for the app
   */
  public static void handleMenu(String userInput, JSONArray library, Scanner input) {
    switch (userInput) {
      case "h":
        System.out.println("-->Home<--");
        break;
      case "s":
        System.out.println("-->Search by title<--");
        System.out.println("Enter the title of the song you would like to play:");
        break;
      case "l":
        System.out.println("-->Library<--");
        libraryDisplay(library);
        System.out.println("Which song would you like to play? (1-" + library.size() + ")?");
        int songIndex = (input.nextInt() - 1);
        play(library, songIndex);
        break;
      case "q":
        System.out.println("-->Quit<--");
        break;
      default:
        break;
    }
  }

  // prints the music library
  public static void libraryDisplay(JSONArray library) {
    // loops through JSONArray and print song names of each file
    for (int i = 0; i < library.size(); i++) {
      JSONObject obj = (JSONObject) library.get(i);
      String songName = (String) obj.get("name");
      System.out.println((i + 1) + ". " + songName);
    }
  }

  // plays an audio file
  public static void play(JSONArray library, Integer songIndex) {
    // open the audio file

    // get the filePath and open a audio file
    JSONObject obj = (JSONObject) library.get(songIndex);
    final String filename = (String) obj.get("filename");
    final String filePath = basePath + "/wav/" + filename;
    final File file = new File(filePath);

    // stop the current song from playing, before playing the next one
    if (audioClip != null) {
      audioClip.close();
    }

    try {
      // create clip
      audioClip = AudioSystem.getClip();

      // get input stream
      final AudioInputStream in = AudioSystem.getAudioInputStream(file);

      audioClip.open(in);
      audioClip.setMicrosecondPosition(0);
      audioClip.loop(Clip.LOOP_CONTINUOUSLY);
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Now playing: " + obj.get("name") + " by " + obj.get("artist") + ", " + obj.get("year") + ", "
        + obj.get("genre"));
    System.out.println("Found in: " + filePath);
  }

  //
  // Func: readJSONFile
  // Desc: Reads a json file storing an array and returns an object
  // that can be iterated over
  //
  public static JSONArray readJSONArrayFile(String fileName) {
    // JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();

    JSONArray dataArray = null;

    try (FileReader reader = new FileReader(fileName)) {
      // Read JSON file
      Object obj = jsonParser.parse(reader);

      dataArray = (JSONArray) obj;
      // System.out.println(dataArray);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return dataArray;
  }

  // read the audio library of music
  public static JSONArray readAudioLibrary() {
    final String jsonFileName = "audio-library.json";
    final String filePath = basePath + "/" + jsonFileName;

    JSONArray jsonData = readJSONArrayFile(filePath);

    System.out.println("Reading the file " + filePath);

    return jsonData;
  }
}