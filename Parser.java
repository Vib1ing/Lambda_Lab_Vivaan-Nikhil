
import java.text.ParseException;
import java.util.ArrayList;

public class Parser {
	
	/*
	 * Turns a set of tokens into an expression.  Comment this back in when you're ready.
	 */
	public Expression parse(ArrayList<String> tokens) throws ParseException {
		ArrayList<ArrayList<String>> top_level_tokens= getTopLevelTokens(tokens);
		if(top_level_tokens.size()==1){
			ArrayList<String> top_level_token = top_level_tokens.get(0);
			if(top_level_token.size()==1){//means ts is a variable
				return new Variable(top_level_token.get(0));
			}
			else if (isFunc(top_level_tokens.get(0))) {
				Variable var = new Variable(top_level_tokens.get(0).get(2));
				ArrayList<String> expression_body = new ArrayList<>(top_level_tokens.get(0).subList(4,top_level_tokens.get(0).size()-1));
				System.out.println(expression_body);
                return new Function(var, parse(expression_body));
			}
			else{
				return parse(new ArrayList<>(top_level_token.subList(1,top_level_token.size()-1))); //this is if there's any unnecessary parenthesis in this stuff so that you only have the variable
			}
		}
		else{//means ts is an application
			Expression left = parse(top_level_tokens.get(0));
			for(int i=1; i<top_level_tokens.size();i++){
				left = new Application(left, parse(top_level_tokens.get(i)));
			}
			return left;
		}
	}

	private ArrayList<ArrayList<String>> getTopLevelTokens (ArrayList<String> tokens) {
		ArrayList<ArrayList<String>> top_level_tokens = new ArrayList<>();//double array list cuz its more convenient cuz you'll have to recurse when you find a top-level item and having a double array list makes it easier
		int start = 0;
		int stop = 0;
		int depth = 0;
		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).equals("(")) {
				start = i;
				depth++;
				while (depth != 0) {
					i++;
					if (tokens.get(i).equals("(")) {
						depth++;
					} else if (tokens.get(i).equals(")")) {
						depth--;
					}
				}
				stop = i;
				top_level_tokens.add(new ArrayList<>(tokens.subList(start, stop + 1)));
			} else {
				ArrayList<String> single_token = new ArrayList<>();
				single_token.add(tokens.get(i));
				top_level_tokens.add(single_token);
			}
		}
		return top_level_tokens;
	}

	private boolean isFunc(ArrayList<String> tokens) {
		if(tokens.size() < 4) {
			return false;
		}
		else {
            return (tokens.get(1).equals("\\") || tokens.get(1).equals("λ")) && tokens.get(3).equals(".");
		}

	}
}
