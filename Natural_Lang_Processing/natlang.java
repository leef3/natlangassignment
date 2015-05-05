import java.util.*;
import java.io.*;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

//Natural Language Processing using Tale of Two Cities Text

//Program will record words following words in a map and print out probabiltiies of
//Certain words following other words

public class natlang
{
	private static String previousWord, currentWord;
	private static HashMap<String, ArrayList<String>> wordMap;

	public static void main(String args[]) throws Exception
	{
		FileReader file = new FileReader("twocities.txt");
		BufferedReader buffer = new BufferedReader(file);

		//Initialize the wordMap
		wordMap = new HashMap<String, ArrayList<String>>();

		//int wordCounter = 0;
		List<String> singleLineWordList = new ArrayList<String>();
		//Only read next line and store data no need to store the whole doc
		String readInLine;

		System.out.println("Learning Tale of Two Cities... Please Wait...");
		//Read the next line of the text file
		while((readInLine = buffer.readLine()) != null)
		{
			//Remove commas, quotations, periods, stars, etc
			readInLine = readInLine.replace(",", "");
			readInLine = readInLine.replace(".", "");
			readInLine = readInLine.replace(":", "");
			readInLine = readInLine.replace(";", "");
			readInLine = readInLine.replace("!", "");
			readInLine = readInLine.replace("\\?", "");
			//Don't remove dashes they should count as a single word
			readInLine = readInLine.replace("\"", "");
			readInLine = readInLine.replace("--", "");

			/*We dont really care about two words that follow with a period even
			though they are two sentences because naturally their probability in the
			long run will be low in our array, thus never actually show up to the user
			*/

			//Split by white spaces for an array of words for that line
			singleLineWordList = Arrays.asList(readInLine.split(" "));

			//Loop through all words
			if(singleLineWordList.size() > 1)
			{
				for(int y = 1; y < singleLineWordList.size(); y++)
				{
					currentWord = singleLineWordList.get(y);
					previousWord = singleLineWordList.get(y-1);
					ArrayList<String> record = wordMap.get(previousWord);
					//Its a new word, create a list for it
					if(record == null)
					{
						ArrayList<String> newAdd = new ArrayList<String>();
						newAdd.add(currentWord);
						wordMap.put(previousWord, newAdd);
						//System.out.println("NEW WORD");
					}
					//ArrayList of words following already exists, add to it
					else
					{
						record.add(currentWord);
						wordMap.put(previousWord, record);
						//System.out.println("ADD EXIST");
					}
				}
			}

			//wordCounter = wordCounter + singleLineWordList.size();
			//Debugging
			//System.out.println(readInLine);
		}
		System.out.println("Success: Learning Complete... \r\n");
		System.out.println("Type any word followed by ENTER to request suggestions");

		//==========================BEGIN USER INPUT===================================
		//Read in the user input word
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

		while(true)
		{
			String stringEntry = console.readLine();
			//If it is a phrase break it up
			String[] inputArray = stringEntry.split(" ");
			//Last word lookup
			String toSearch = inputArray[inputArray.length - 1];

			printSuggestions(toSearch);

			System.out.println("\r\n Type any word followed by ENTER to request suggestions...");
			System.out.println("\r\n");
		}
		
	}
	//Implementation of tree map to use special comparator
	public static TreeMap<String, Integer> SortByValue(HashMap<String, Integer> map)
	{
		ValueComparator vc = new ValueComparator(map);
		TreeMap<String, Integer> sortMap = new TreeMap<String, Integer>(vc);
		sortMap.putAll(map);
		return sortMap;
	}

	private static void printSuggestions(String wordKey)
	{
		//Find in map
		ArrayList<String> resultArray = wordMap.get(wordKey);
		if(resultArray == null)
		{
			System.out.println("No Such Word Found!");
			return;
		}
		//Word Exists
		else
		{

			//Counting word follow frequency using a map
			HashMap<String, Integer> countMap = new HashMap<String, Integer>();
			for(String temp : resultArray)
			{
				Integer count = countMap.get(temp);
				countMap.put(temp, (count == null) ? 1 : count + 1);
			}

			TreeMap<String, Integer> sortedMap = SortByValue(countMap);
			//System.out.println(sortedMap);

			//Only print out top 4 frequency words or all if under 4
			if(sortedMap.size() > 4)
			{
				int onlyFour = 0;
				for(Map.Entry<String, Integer> entry : sortedMap.entrySet())
				{
					//Cutoff at top 4
					if(onlyFour == 4) { return; }

					//Get Values and calculate probability
					Integer value = entry.getValue();
					float frequency = ((value*100)/resultArray.size());
					String key = entry.getKey();
					System.out.println("Word: " + key + "   Probability: " + frequency + "%" + "   Raw Number: " + value);
					onlyFour++;
				}
			}
			else
			{
				System.out.println(sortedMap);
			}
		}
	}
}

//Special comparator to sort HashMap by Values rather than keys
class ValueComparator implements Comparator<String>
{
	Map<String, Integer> base;
	public ValueComparator(Map<String, Integer> base)
	{
		this.base = base;
	}
	public int compare(String a, String b)
	{
		if(base.get(a) >= base.get(b))
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}
}