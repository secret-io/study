## private 생성자나 열거 타입으로 싱글턴임을 보증하라

싱글턴을 만드는 세 가지 방식
1. public static 필드
2. public static 메서드
    * 위의 두 방식 모두 생성자는 private으로 감춰두고, 유일한 인스턴스에 접근할 수 있는 수단으로 public static 멤버를 하나 마련해둔다
3. enum 타입

### public static 필드
```java
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
```

* private 생성자는 필드를 초기화할 때 한 번만 호출된다
* 예외  
권한이 있는 클라이언트는 리플렉션 API인 AccessibleObject.setAccessible을 사용해 private 생성자를 호출할 수 있다  
이를 피하기 위해선 생성자를 수정하여 두 번째 객체가 생성되려 할 때 예외를 던지게 하면 된다

* 필드 방식의 큰 장점은 해당 클래스가 싱글턴임이 API에 명백히 드러난다는 점
* 간결함

### public static 메서드
```java
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
```

* getInstance는 항상 같은 객체의 참조를 반환한다(하지만 리플렉션을 통한 예외는 똑같이 적용된다)

* 필요에 따라 API를 바꾸지 않고도, 싱글턴이 아니게 변경이 가능하다  
(유일한 인스턴스를 반환하던 팩터리 메서드가 호출하는 스레드별로 다른 인스턴스를 넘겨주게 할 수 있다)
* 원한다면 정적 팩터리를 제네릭 싱글턴 팩터리로 만들 수 있다는 점이다(Item30)
* 정적 팩터리의 메서드 참조를 공급자(supplier)로 사용할 수 있다는 점  
가령 Elvis::getInstance를 Supplier<Elvis>로 사용하는 식(Item 43, 44)
* 위와 같은 장점들이 굳이 필요하지 않다면 필드 방식이 좋다


위의 두 방식으로 만든 싱글턴 클래스는 직렬화할 때 단순히 Serializable을 구현한다고 선언하는 것만으로는 부족하다

모든 인스턴스 필드를 일시적(transient) 선언하고, readResolve 메서드를 제공해야한다(Item 89)

이렇게 하지 않으면 직렬화된 인스턴스를 역직렬화할 때 새로운 인스턴스가 만들어진다

---

```java
//싱글턴임을 보장해주는 메서드
private Object readResolve() {
    //진짜 Elvis를 반환하고, 가짜 Elvis는 GC에 맡긴다
    return INSTANCE;
}

``` 


### enum type
```java
public enum Elvis {
    
    INSTANCE;

    public void leaveTheBuilding() {
        System.out.println("기다려 자기야, 지금 나갈께!");
    }

    // 이 메서드는 보통 클래스 바깥(다른 클래스)에 작성해야 한다!
    public static void main(String[] args) {
        Elvis elvis = Elvis.INSTANCE;
        elvis.leaveTheBuilding();
    }

}
```

* public static 필드 방식과 비슷하지만 더 간결하고, 직렬화 가능
* 아주 복잡한 직렬화 상황이나 리플렉션 공격에도 제2의 인스턴스가 생기는 일을 완벽히 막아준다
* 대부분 상황에서 원소가 하나뿐인 열거 타입이 싱글턴을 만드는 가장 좋은 방법이다 <- 정말?
* 단, 만들려는 싱글턴이 Enum 외의 클래스를 상속해야 한다면 이 방법은 사용할 수 없다
