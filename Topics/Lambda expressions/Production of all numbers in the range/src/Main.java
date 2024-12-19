import java.util.function.*;


class Operator {

    public static LongBinaryOperator binaryOperator = (a, b) ->{
        if(a == b){
            return a;
        }
        long product = 1;
        for (long i = a; i <= b; i++) {
            product*=i;
        }
        return product;
    }; // Write your code here
}