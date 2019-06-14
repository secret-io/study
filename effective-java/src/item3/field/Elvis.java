package item3.field;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

// 코드 3-1 public static final 필드 방식의 싱글턴 (23쪽)
public class Elvis {

    public static final Elvis INSTANCE = new Elvis();

    private Elvis() {
        if(INSTANCE != null) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void leaveTheBuilding() {
        System.out.println("Whoa baby, I'm outta here!");
    }

}

class Main {

    public static void main(String[] args) {
        Elvis elvis = Elvis.INSTANCE;
        elvis.leaveTheBuilding();
        System.out.println("public static 필드 : " + elvis);

        Class<Elvis> reflectedElvis = Elvis.class;
        Constructor[] declaredConstructors = reflectedElvis.getDeclaredConstructors();

        for (Constructor declaredConstructor : declaredConstructors) {
            declaredConstructor.setAccessible(true);
            try {
                System.out.println("리플렉션을 통한 private 생성자 접근 후 만든 새로운 인스턴스 : " + declaredConstructor.newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }

}
