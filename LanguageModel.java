import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
    In input = new In(fileName);

    // Build the initial window (first windowLength characters)
    String currentWindow = "";
    for (int i = 0; i < windowLength; i++) {
        currentWindow += input.readChar();
    }

    // Read the rest of the file one char at a time and update the model
    while (!input.isEmpty()) {
        char nextChar = input.readChar();

        if (CharDataMap.containsKey(currentWindow)) {
            CharDataMap.get(currentWindow).update(nextChar);
        } else {
            List charList = new List();
            charList.update(nextChar);
            CharDataMap.put(currentWindow, charList);
        }

        // slide the window by 1 char
        currentWindow = currentWindow.substring(1) + nextChar;
    }

    // After counts are collected, compute probabilities for each list
    for (List listValue : CharDataMap.values()) {
        calculateProbabilities(listValue);
    }
  }
	

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	void calculateProbabilities(List probs) {
    // First pass: sum counts
    ListIterator iter = probs.listIterator(0);
    int sumCounts = 0;

    while (iter.hasNext()) {
        sumCounts += iter.next().count;
    }

    // Second pass: compute p and cumulative p
    iter = probs.listIterator(0);
    double runningCP = 0.0;

    while (iter.hasNext()) {
    CharData cd = iter.next();
     cd.p = (double) cd.count / sumCounts;
    runningCP += cd.p;
    cd.cp = runningCP;
    }
  }

    // Returns a random character from the given probabilities list.
	char getRandomChar(List probs) {
    double roll = randomGenerator.nextDouble();
    ListIterator iter = probs.listIterator(0);
    while (iter.hasNext()) {
    CharData cd = iter.next();
        if (cd.cp > roll) {
            return cd.chr;
        }
    }
 // Fallback (should rarely happen due to floating point issues)
    return probs.get(probs.getSize() - 1).chr;
  }

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
    if (initialText.length() < windowLength) {
        return initialText;
    }

    String result = initialText;
    String currentWindow = initialText.substring(initialText.length() - windowLength);

    int generatedCount = 0;
    while (generatedCount < textLength) {
        List probList = CharDataMap.get(currentWindow);
        if (probList == null) {
            // Window not in model => stop early
            return result;
        }

        char chosenChar = getRandomChar(probList);
        result += chosenChar;

        // slide the window
        currentWindow = currentWindow.substring(1) + chosenChar;
        generatedCount++;
    }

    return result;
  }

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
    int windowLength = Integer.parseInt(args[0]);
    String initialText = args[1];
    int lettersToGenerate = Integer.parseInt(args[2]);
    boolean useRandomSeed = args[3].equals("random");
    String corpusFile = args[4];
    LanguageModel model;
    if (useRandomSeed) {
        model = new LanguageModel(windowLength);
    } else {
        model = new LanguageModel(windowLength, 20);
    }

    model.train(corpusFile);

    System.out.println(model.generate(initialText, lettersToGenerate));
  }
}
