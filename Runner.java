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
            Expression arg_copy = deepCopy(app.getRight());
            return substitute(f.getBody(), f.getParam().getName(), arg_copy);
        }
        if (hasRedex(app.getLeft())) {
            return new Application(reduceOnce(app.getLeft()), app.getRight());
        }
        return new Application(app.getLeft(), reduceOnce(app.getRight()));
    }

    private static Expression substitute(Expression exp, String param_name, Expression replacement) {
        if (exp instanceof Variable) {
            Variable v = (Variable) exp;
            if (v.getName().equals(param_name)) {
                return deepCopy(replacement);
            }
            return exp;
        }
        if (exp instanceof Function) {
            Function f = (Function) exp;
            if (f.getParam().getName().equals(param_name)) {
                return exp;
            }
            return new Function(f.getParam(), substitute(f.getBody(), param_name, replacement));
        }
        Application app = (Application) exp;
        return new Application(
                substitute(app.getLeft(),  param_name, replacement),
                substitute(app.getRight(), param_name, replacement)
        );
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
}