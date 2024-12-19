import java.util.*;

public class Main {
    static class Box<T> {
        // your code here
        T value;

        Box(T value){
            this.value = value;
        }

        T getValue() {
            return value;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        if(sc.hasNextInt()) {
            int num = sc.nextInt();
            Box<Integer> box = new Box<>(num);
            System.out.println(box.getValue());
        } else if (sc.hasNextFloat()) {
            float num = sc.nextFloat();
            Box<Float> box = new Box<>(num);
            System.out.println(box.getValue());
        } else {
            String str = sc.next();
            Box<String> box = new Box<>(str);
            System.out.println(box.getValue());
        }
    }
}