# Wallpaper program
### This java program is a hobby project created by me 2016.
### This program scrapes swedish news sites such as aftonbladet, expressen, nyheter 24 etc. using jsoup. After sorting the words after most freaquently appeared it creates a png word cloud image using kennycason/kumo library and then sets as the wallpaper.
Example:
![alt text](https://github.com/bc222az/WallpaperProgram/blob/master/example.jpg)

Files:
  - img.png the word cloud image will be replaced with the new one after you run the program.
  - blacklistedwords.txt a list with unwanted words that usaually appears such as och,han,hon (and,he,she).
  - run.bat windows task scheduler had a problem running the run.jar file so run this file instead that runs the run.jar file hihi ;P
  - run.jar the file to run.
  - text.txt saves the text from the websites with a timestamp
  - textWords.txt save the words in most frequent order.
  
Commands/arguments:
- To run program <run.jar location>java -jar run.jar -optinalargument
- -help (shows arguments)
- -timestamp (shows time since last website scrape)
- -refresh soft (creates new word cloud png from exisitn words and sets it to your wallpaper)
- -refresh hard (scrapes the websites again and creates a new word cloud png and sets it to your wallpaper)
- -add "word" adds the word to blacklist file.
- (no arguments does nothing unless it was more then 5 hours since you scraped the websites)

Example:
![alt text](https://github.com/bc222az/WallpaperProgram/blob/master/cmd.jpg)
    
To make it run automatically use windows task scheduler (Create basic task). Make it run the run.bat file and choose startup in the same map.