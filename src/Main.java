import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

public class Main {
	
	public static final int wordUsedLimitToShow = 5;
	FileHandler fileHandler;
	ScrapeWebsites scrapeWebsites;
	WordsSorter wordsSorter;
	ImageCreator imageCreator;
	public static interface User32 extends Library {
	     User32 INSTANCE = (User32) Native.loadLibrary("user32",User32.class,W32APIOptions.DEFAULT_OPTIONS);        
	     boolean SystemParametersInfo (int one, int two, String s ,int three);         
	}
	public static void main(String[] args) throws IOException {
		Main main = new Main();
		main.options(args);
	}
	private void options(String[] args){
		fileHandler = new FileHandler();
		scrapeWebsites = new ScrapeWebsites();
		wordsSorter = new WordsSorter();
		imageCreator = new ImageCreator();
		if(args.length == 0){
			doProgram();
		}
		if(args.length >= 2){
			if(args[0].equals("-refresh")){
				if(args[1].equals("soft")){
					softRefresh();
				}else if(args[1].equals("hard")){
					hardRefresh();
				}
				changeBackgroundImage();
			}
			if(args[0].equals("-add")){
				fileHandler.addWordToBlackList(args[1]);
				System.out.println("Word added!");
			}
		}
		if(args.length == 1){
			if(args[0].equals("-timestamp")){
				showTimeSinceLastWebScrape();
			}
			if(args[0].equals("-add")){
				System.out.println("type -add followed by the word you want to add");
			}
			if(args[0].equals("-help") || args[0].equals("help") || args[0].equals("halp")){
				System.out.println("-refresh hard");
				System.out.println("-refresh soft");
				System.out.println("-timestamp");
				System.out.println("-add \"word\"");
			}
		}
		
	}
	private void changeBackgroundImage(){
		File imageFile = new File(System.getProperty("user.dir")+"/img.png");
        try {
			User32.INSTANCE.SystemParametersInfo(0x0014, 0, imageFile.getCanonicalPath() , 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void showTimeSinceLastWebScrape(){
		long millis = new Date().getTime() - fileHandler.getTimeSinceLastWebScrape();
		long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

		System.out.println("Time since last scrape: ");
		if(hours > 0){
			System.out.print(days+"days ");
		}
		if(hours > 0){
			System.out.print(hours+"h ");
		}
		if(minutes > 0){
			System.out.print(minutes+"m ");
		}
		if(seconds > 0){
			System.out.print(seconds+"s ");
		}
	}
	private void doProgram(){
		if(isInternetReachable()){
			if(fileHandler.textIsOld()){
				String text = getText();
				HashMap<String, Word> wordMap = wordsSorter.countAllWords(text);
				ArrayList<Word> words = wordsSorter.hashMapToSortedArrayList(wordMap);
				try {
					fileHandler.printWordsToFile(words);
					System.out.println("do program");
					imageCreator.createImage();
					changeBackgroundImage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				printWords(words);
			}else{
				System.out.print("text is up to date, no update was needed. Type -refresh hard/soft for update");
			}
		}else{
			setTimer((1000*60*60)/4); //15min if no internet
		}
	}
	private void setTimer(int time){
		Timer timer = new Timer ();
		TimerTask hourlyTask = new TimerTask () {
		    @Override
		    public void run () {
		        Main main = new Main();
		        main.doProgram();
		    }
		};
		timer.schedule (hourlyTask, 0l, time);
	}
	private String getText(){
		String text = "";
		if(fileHandler.textIsOld()){
			text = scrapeWebsites.scrapeWebsites();
			fileHandler.writeTextToFile(text);
		}else{
			text = fileHandler.getDataFromTextFile();
		}
		return text;
	}
	private void hardRefresh(){
		String text = scrapeWebsites.scrapeWebsites();
		fileHandler.writeTextToFile(text);
		HashMap<String, Word> wordMap = wordsSorter.countAllWords(text);
		ArrayList<Word> words = wordsSorter.hashMapToSortedArrayList(wordMap);
		try {
			fileHandler.printWordsToFile(words);
			System.out.println("creating hard refresh image");
			imageCreator.createImage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		printWords(words);
	}
	private void softRefresh(){
		String text = fileHandler.getDataFromTextFile();
		HashMap<String, Word> wordMap = wordsSorter.countAllWords(text);
		ArrayList<Word> words = wordsSorter.hashMapToSortedArrayList(wordMap);
		try {
			fileHandler.printWordsToFile(words);
			System.out.println("creating soft refresh image");
			imageCreator.createImage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		printWords(words);
	}
	private void printWords(ArrayList<Word> words){
		for(Word word : words){
			if(word.getCount() > this.wordUsedLimitToShow){
				System.out.println(word.getCount()+":"+word.getWord());
			}
		}
	}
	
	//checks for connection to the internet through dummy request
    public static boolean isInternetReachable()
    {
        try {
            //make a URL to a known source
            URL url = new URL("http://www.google.com");

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            Object objData = urlConnect.getContent();

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }
}