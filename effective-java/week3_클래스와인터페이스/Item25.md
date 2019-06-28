# 아이템 25 : 톱레벨 클래스는 한 파일에 하나만 담아라  

소스 파일 하나에 톱레벨 클래스를 여러개 선언하더라도 자바 컴파일러는 불평하지 않는다.  
하지만 득이 없을 뿐더러 오류가 발생할 위험을 낳는다.  
다음의 예를 확인해 보자.

```java
// Main클래스의 Main메서드는 
// 다른 톱레벨 클래스(Utensil, Dessert) 를 참조 하고있다.
public class Main {
    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }
}
```

```java
// Utensil.java 파일
class Utensil {
    static final String NAME = "pan";
}

class Dessert {
    static final String NAME = "cake";
}
```  
```java
// Dessert.java 파일
class Utensil {
    static final String NAME = "pot";
}

class Dessert {
    static final String NAME = "pie";
}
```  
컴파일러에 어느 소스 파일을 먼저 건네느냐에 따라 동작이 달라지므로(pancake, potpie)
반드시 바로 잡아야 한다.  
> 다행히 해결책은 아주 간단하다. 톱레벨 클래스들(Utensil, Dessert)을 서로 다르 클래스로 분리하면 그만이다.  

다르 클래스에 딸린 부차적인 클래스라면 정적 멤버 클래스로 만드는 쪽이 일반적으로 더 나을 것이다.
```java
// 톱레벨 클래스들을 정적 멤버 클래스로 바꿔본 모습
public class Test {
    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }

    private static class Utensil {
        static final String NAME = "pan";
    }

    private static class Dessert {
        static final String NAME = "cake";
    }
}
```

---

>결론 : 소스 파일 하나에는 반드시 톱레벨 클래스(혹은 톱레벨 인터페이스) 를 하나만 담자.

