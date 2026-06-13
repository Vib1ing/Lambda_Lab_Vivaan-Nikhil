// Nikhil Maalige & Vivaan Joshi
import java.util.HashSet;
import java.util.Set;

public class Runner {

    public static Expression run(Expression exp) {
        while (hasRedex(exp)) {
            exp = reduceOnce(exp);
        }

        return exp;
    }


    private static boolean hasRedex(Expression exp) {
        if (exp instanceof Variable) {
            return false;
        }

        if (exp instanceof Function) {
            return hasRedex(((Function) exp).getBody());
        }

        Application app = (Application) exp;

        if (app.getLeft() instanceof Function) {
            return true;
        }

        return hasRedex(app.getLeft()) || hasRedex(app.getRight());
    }


    private static Expression reduceOnce(Expression exp) {
        if (exp instanceof Variable) {
            return exp;
        }

        if (exp instanceof Function) {
            Function f = (Function) exp;
            return new Function(f.getParam(), reduceOnce(f.getBody()));
        }

        Application app = (Application) exp;

        if (app.getLeft() instanceof Function) {
            Function f = (Function) app.getLeft();
            Expression copy = deepCopy(app.getRight());
            return substitute(f.getBody(), f.getParam().getName(), copy);
        }

        if (hasRedex(app.getLeft())) {
            return new Application(reduceOnce(app.getLeft()), app.getRight());
        }

        return new Application(app.getLeft(), reduceOnce(app.getRight()));
    }


    private static Expression substitute(Expression exp, String paramName, Expression replacement) {
        if (exp instanceof Variable) {
            Variable v = (Variable) exp;

            if (v.getName().equals(paramName)) {
                return deepCopy(replacement);
            }

            return exp;
        }

        if (exp instanceof Function) {
            Function f = (Function) exp;

            if (f.getParam().getName().equals(paramName)) {
                return exp;
            }

            Set<String> freeVars = getFreeVariables(replacement);

            if (freeVars.contains(f.getParam().getName())) {
                String newParam = getNewName(f.getParam().getName(), freeVars);
                Expression newBody = rename(f.getBody(), f.getParam().getName(), newParam);
                return new Function(new Variable(newParam), substitute(newBody, paramName, replacement));
            }

            return new Function(f.getParam(), substitute(f.getBody(), paramName, replacement));
        }

        Application app = (Application) exp;
        return new Application(substitute(app.getLeft(), paramName, replacement), substitute(app.getRight(), paramName, replacement));
    }


    public static Expression deepCopy(Expression exp) {
        if (exp instanceof Variable) {
            return new Variable(((Variable) exp).getName());
        }

        if (exp instanceof Function) {
            Function f = (Function) exp;
            return new Function(new Variable(f.getParam().getName()), deepCopy(f.getBody()));
        }

        Application app = (Application) exp;
        return new Application(deepCopy(app.getLeft()), deepCopy(app.getRight()));
    }


    private static Set<String> getFreeVariables(Expression exp) {
        Set<String> free = new HashSet<>();

        if (exp instanceof Variable) {
            free.add(((Variable) exp).getName());
        }
        else if (exp instanceof Function) {
            Function f = (Function) exp;
            free.addAll(getFreeVariables(f.getBody()));
            free.remove(f.getParam().getName());
        }
        else {
            Application app = (Application) exp;
            free.addAll(getFreeVariables(app.getLeft()));
            free.addAll(getFreeVariables(app.getRight()));
        }
        
        return free;
    }


    private static String getNewName(String base, Set<String> takenNames) {
        int counter = 1;
        String newName = base + String.valueOf(counter);

        while (takenNames.contains(newName)) {
            counter++;
            newName = base + String.valueOf(counter);
        }

        return newName;
    }

    
    private static Expression rename(Expression exp, String oldName, String newName) {
        if (exp instanceof Variable) {
            Variable v = (Variable) exp;

            if (v.getName().equals(oldName)) {
                return new Variable(newName);
            }

            return exp;
        }

        if (exp instanceof Function) {
            Function f = (Function) exp;

            if (f.getParam().getName().equals(oldName)) {
                return exp;
            }

            return new Function(f.getParam(), rename(f.getBody(), oldName, newName));
        }
        
        Application app = (Application) exp;
        return new Application(rename(app.getLeft(), oldName, newName), rename(app.getRight(), oldName, newName));
    }
}