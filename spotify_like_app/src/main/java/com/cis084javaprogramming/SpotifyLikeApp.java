package com.cis084javaprogramming;

// import static javax.sound.sampled.AudioSystem.*;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import org.json.simple.*;
import org.json.simple.parser.*;

/**
 * Name: Brendan Bobryk
 * Student ID #: 1036738
 * Class: CIS 084 Java Programming
 * Date: 11/24/23
 * Program: Spotify-Like-App
 */

public class SpotifyLikeApp {

  // global variables for the app
  static private Integer currentSongIndex = -1;
  static private ArrayList<Integer> recentlyPlayedSongIndexes = new ArrayList<Integer>();
  static Clip audioClip;
  static Integer favoritesCount;
  static ArrayList<Integer> favouriteArrayList = new ArrayList<Integer>();;

  private static String basePath = "C:\\Users\\Brendan\\Documents\\GitHub\\Spotify-Like-App\\spotify_like_app\\src\\main\\java\\com\\cis084javaprogramming";

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

  // displays the menu for the app
  public static void menu() {
    System.out.println("---- SpotifyLikeApp ----");
    System.out.println("[H]ome");
    System.out.println("[S]earch by title");
    System.out.println("[L]ibrary");
    System.out.println("[F]avorites");
    System.out.println("[Q]uit");

    System.out.print("\nEnter q to Quit: ");
  }

  // handles the user input for the app
  public static void handleMenu(String userInput, JSONArray library, Scanner input) {
    System.out.println("");
    switch (userInput) {
      // [H]ome
      case "h":
        System.out.println("-->Home<--");
        homeDisplay(library);
        break;
      // [S]earch by title
      case "s":
        System.out.println("-->Search by title<--");
        // prompt and retrieval of song title
        System.out.println("Enter the title of the song you would like to play:");
        String inputtedSongName = input.next();
        Integer songSearchIndex = -1;
        songSearchIndex = searchByTitle(library, inputtedSongName, songSearchIndex);
        // checks if value was reassigned
        if (songSearchIndex != (-1)) {
          play(library, songSearchIndex, input);
        } else {
          System.out.println("\n Could not find a song with the given search criteria. \n");
        }
        break;
      // [L]ibrary
      case "l":
        System.out.println("-->Library<--");
        // displays current library
        libraryDisplay(library);
        // prompts user to select a song to play
        System.out.println("\nWhich song would you like to play? (1-" + library.size() + ")?");
        int songIndex = (input.nextInt() - 1);
        // input validation to ensure the value of the selection is an option listed
        while (songIndex < 0 || songIndex > (library.size() - 1)) {
          System.out.println("\nThat is not a valid entry, please enter a number between 1-" + library.size() + ".");
          songIndex = (input.nextInt() - 1);
        }
        // plays selected song
        play(library, songIndex, input);
        break;
      // [F]avorites
      case "f":
        System.out.println("-->Favorites<--");
        // displays favorites library
        favoritesDisplay(library);
        if (favoritesCount > 0) {
          // prompts user to select a song to play if they have favorited songs
          System.out.println("\nWhich song would you like to play? (1-" + favoritesCount + ")");
          int favoriteSongIndex = (input.nextInt() - 1);
          // input validation to ensure the value of the selection is an option listed
          while (favoriteSongIndex < 0 || favoriteSongIndex > (favoritesCount - 1)) {
            System.out.println("\nThat is not a valid entry, please enter a number between 1-" + favoritesCount + ".");
            favoriteSongIndex = (input.nextInt() - 1);
          }
          favoriteSongIndex = favouriteArrayList.get(favoriteSongIndex);
          // plays selected song
          play(library, favoriteSongIndex, input);
        }
        break;
      // [Q]uit
      case "q":
        System.out.println("-->Quit<--");
        break;
      default:
        break;
    }
  }

  // searches for the users input within the song library
  public static int searchByTitle(JSONArray library, String inputtedSongName, Integer songSearchIndex) {
    for (int i = 0; i < library.size(); i++) {
      JSONObject obj = (JSONObject) library.get(i);
      String songName = (String) obj.get("name");
      // checks name to see if it matches input
      if (songName.toLowerCase().contains(inputtedSongName.toLowerCase())) {
        songSearchIndex = i;
        break;
      }
    }
    // returns song index
    return songSearchIndex;
  }

  // prints the currently playing song and previously played songs
  public static void homeDisplay(JSONArray library) {
    // currently playing display
    System.out.println("Currently playing:");
    // if no song is playing
    if (currentSongIndex == -1) {
      System.out.println("There is currently no song playing.\n");
      // if a song is playing
    } else {
      JSONObject obj = (JSONObject) library.get(currentSongIndex);
      System.out.println(obj.get("name") + " by " + obj.get("artist") + ", " + obj.get("year") + ", "
          + obj.get("genre") + "\n");
    }
    // previously played display
    System.out.println("Previously played songs:");
    // if no songs have played or only one which is currently playing
    if (recentlyPlayedSongIndexes.size() == 0 || recentlyPlayedSongIndexes.size() == 1) {
      System.out.println("No songs have been played previously.\n");
      // if the user has changed songs after the first song played
      // lists the three, or less, most recent songs played
    } else {
      int countUp = 0;
      for (int i = (recentlyPlayedSongIndexes.size() - 1); i > (recentlyPlayedSongIndexes.size() - 4) && i > 0; i--) {
        countUp++;
        JSONObject obj = (JSONObject) library.get(recentlyPlayedSongIndexes.get(i - 1));
        System.out.println(countUp + ". " + obj.get("name") + " by " + obj.get("artist") + ", " + obj.get("year") + ", "
            + obj.get("genre"));
      }
    }
  }

  // prints the music library
  public static void libraryDisplay(JSONArray library) {
    // loops through JSONArray and prints the details of each file
    for (int i = 0; i < library.size(); i++) {
      JSONObject obj = (JSONObject) library.get(i);
      System.out.println((i + 1) + ". " + obj.get("name") + " by " + obj.get("artist") + ", " + obj.get("year") + ", "
          + obj.get("genre"));
    }
  }

  // prints the favorites library
  public static void favoritesDisplay(JSONArray library) {
    favouriteArrayList.clear();
    favoritesCount = 0;
    // loops through JSONArray and prints the favorited songs
    for (int i = 0; i < library.size(); i++) {
      JSONObject obj = (JSONObject) library.get(i);
      if (obj.get("favorite").equals("true")) {
        favouriteArrayList.add(i);
        favoritesCount++;
        System.out.println(
            (favoritesCount) + ". " + obj.get("name") + " by " + obj.get("artist") + ", " + obj.get("year") + ", "
                + obj.get("genre"));
      }
    }
    // provides an output to the user if there are no favorited songs
    if (favoritesCount == 0) {
      System.out.println("You have no favorited songs. \n");
    }
  }

  // plays an audio file
  public static void play(JSONArray library, Integer songIndex, Scanner input) {
    // get the filePath and open the selected audio file
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

    // updates the currentSongIndex
    currentSongIndex = songIndex;

    // updates the recentlyPlayedSongIndexes
    recentlyPlayedSongIndexes.add(songIndex);

    // prints the details of the song along with the location in the file system
    System.out.println("\nNow playing: " + obj.get("name") + " by " + obj.get("artist") + ", " + obj.get("year") + ", "
        + obj.get("genre"));
    if (obj.get("favorite").equals("true")) {
      System.out.println("Favorite: Yes");
    } else {
      System.out.println("Favorite: No");
    }
    System.out.println("Found in: " + filePath + "\n");

    // displays and loops through song options until user chooses to go back to the
    // main menu
    String songOption = "";
    long time = 0;
    while (!songOption.equals("m")) {
      System.out.println("---- Song Options ----");
      System.out.println("[P]ause / Resume");
      System.out.println("[S]top");
      System.out.println("[R]ewind 5 seconds");
      System.out.println("[A]dvance 5 seconds");
      System.out.println("[F]avorite");
      System.out.println("[M]ain Menu");
      System.out.print("\nEnter m to go back to the Main Menu: ");
      songOption = input.next().toLowerCase();
      System.out.println("");
      switch (songOption) {
        // pauses or resumes the audio file
        case "p":
          if (audioClip.isRunning()) {
            System.out.println("The song is now paused.");
            // pauses the audio file
            audioClip.stop();
          } else {
            System.out.println("The song is now resuming.");
            // resumes audio file from the stopped position
            audioClip.start();
          }
          break;
        // stops the audio file
        case "s":
          System.out.println("The song is now stopped.");
          // stops the audio file
          audioClip.stop();
          break;
        // rewinds an audio file 5 seconds
        case "r":
          // stops audio file and retrieves the microsecond position
          audioClip.stop();
          // calculates the rewinded position in the audio file
          time = audioClip.getMicrosecondPosition() - (5 * (long) Math.pow(10, 6));
          // sets the position of the adjusted microsecond value and resumes playing from
          // adjusted position
          audioClip.setMicrosecondPosition(time);
          audioClip.start();
          break;
        // advances an audio file 5 seconds
        case "a":
          // stops audio file and retrieves the microsecond position
          audioClip.stop();
          // calculates the advanced position in the audio file
          time = audioClip.getMicrosecondPosition() + (5 * (long) Math.pow(10, 6));
          // sets the position of the adjusted microsecond value and resumes playing from
          // adjusted position
          audioClip.setMicrosecondPosition(time);
          audioClip.start();
          break;
        case "f":
          if (obj.get("favorite").equals("true")) {
            System.out.println("Would you like to remove this song to your favourites? (Y/N)");
            String favoriteAnswer = input.next().toLowerCase();
            // if the song is in the favorites list, program asks user if they would like it
            // removed
            if (favoriteAnswer.equals("y")) {
              obj.put((String) "favorite", (String) "false");
              favouriteArrayList.remove(currentSongIndex);
              System.out.println("\nFavorites updated.\n");
            } else {
              // if not removed from favorites, confirms the result to the user
              System.out.println("\nNo changes made.\n");
            }
          } else {
            // if the song is not in the favorites list, program asks user if they would
            // like it added
            System.out.println("Would you like to add this song to your favourites? (Y/N)");
            String favoriteAnswer = input.next().toLowerCase();
            if (favoriteAnswer.equals("y")) {
              obj.put((String) "favorite", (String) "true");
              favouriteArrayList.add(currentSongIndex);
              System.out.println("\nFavorites updated.\n");
            } else {
              // if not added to favorites, confirms the result to the user
              System.out.println("\nNo changes made.\n");
            }
          }
          break;
        case "m":
          break;
        default:
          break;
      }

    }
  }

  // Func: readJSONFile
  // Desc: Reads a json file storing an array and returns an object
  // that can be iterated over
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
