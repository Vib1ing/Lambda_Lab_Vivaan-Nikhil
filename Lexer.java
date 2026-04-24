
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

	/*
	 * A lexer (or "tokenizer") converts an input into tokens that
	 * eventually need to be interpreted.
	 * 
	 * Given the input
	 * (\bat .bat flies)cat λg.joy! )
	 * you should output the ArrayList of strings
	 * [(, \, bat, ., bat, flies, ), cat, \, g, ., joy!, )]
	 *
	 */
	public ArrayList<String> tokenize(String input) {
		ArrayList<String> tokens = new ArrayList<String>();

		int counter = 0;
		String temp = "";
		while (counter < input.length() && input.charAt(counter) != ';') {
			char current = input.charAt(counter);

			 if (current == '\\' || current == 'λ' || current == '(' || current == ')' || current == '.' || current == '=' || current == ' ') {
				if (!temp.isEmpty()) {
					tokens.add(temp);
					temp = "";
				}
				if(current != ' '){
					if(current == 'λ') {
						tokens.add("\\");
					}
					else {
						tokens.add(Character.toString(current));
					}
				}
				counter++;
			} else {
				temp += Character.toString(current);
				counter++;
			}
		}
		if (!temp.isEmpty()) {
			tokens.add(temp);
		}
		//System.out.println(tokens);
		return tokens;
	}

}
