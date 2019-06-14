package item3.staticfactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

// 코드 3-2 정적 팩터리 방식의 싱글턴 (24쪽)
public class Elvis {

    private static final Elvis INSTANCE = new Elvis();

    private Elvis() { }

    public static Elvis getInstance() {
        return INSTANCE;
    }

    public void leaveTheBuilding() {
        System.out.println("Whoa baby, I'm outta here!");
    }

}

class Main {

    public static void main(String[] args) {
        Elvis elvis = Elvis.getInstance();
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
