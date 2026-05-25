
public class Function implements Expression {
    private Variable v;
    private Expression e;

    public Function(Variable v, Expression e){
        this.v = v;
        this.e = e;
    }
    public Variable getParam() {
        return v;
    }

    public Expression getBody() {
        return e;
    }

    public void setBody(Expression e) {
        this.e = e;
    }

    public String toString() {
        return "(λ" + v + "." + e + ")";
    }
}
