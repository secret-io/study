# equals는 일반 규약을 지켜 재정의 하라

**다음에서 열거한 것 중 하나에 해당한다면 재정의 하지 않아도 된다.**  

* 각 인스턴스가 본질적으로 고유하다.  

* 인스턴스의 논리적 동치성(equality)을 검사할 일이 없다.  

* 상위 클래스에서 재정의한 equals가 하위 클래스에도 딱 들어 맞는다.
    * ex ) Set 구현체는 AbstractSet이 구현한 equals를 상속받아 쓰고,  List 구현체들은 AbstractList로부터,  Map 구현체들은 AbstractMap으로 부터 상속받아 그대로 쓴다.  

    ![컬렉션 관계도](http://how2examples.com/java/images/Java-Collection-Hierarchy.png)  
      
* 클래스가 private이거나 package-private이고 equals 메서드를 호출할 일이 없다.
    * 혹시 실수로라도 equals가 호출되는 걸 막고싶다면 다음처럼 구현해보자
    ```java
    @Overide public boolean equals(Object o) {
        throw new AssertionError();     //호출 금지!
    }
    ```
#

> 그렇다면 언제 equasl를 재정의 해야할까?  

**논리적 동치성을 확인해야 하는데, 상위 클래스의 equals가 논리적 동치성을 비교하도록 재정의 되지 않았을 때다.**

##

equals를 재정의하지 않아도 될 때
1. 인스턴스 통제 클래스
    * 싱글턴 클래스
    * 인스턴스화 불가(private 생성자)
2. Enum

1,2는 어차피 논리적으로 같은 인스턴스가 2개이상 만들어지지 않으니 논리적 동치성과 객체 식별성이 사실상 같다고 볼 수 있다.

---

> equals 메서드를 재정의할 때는 반드시 일반 규약을 따라야 한다.

1. 반사성 
    * null이 아닌 참조 값 x에 대해, x.equals(x)는 true다.
2. 대칭성 : 
    * null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)가 true면 y.equals(x)도 true다.
3. 일관성 
    * null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환한다.
4. null-아님
    * null이 아닌 모든 참조 값 x에 대해, x.quals(null)은 false다.

이 규약을 어기면 프로그램이 이상하게 동작하거나 종료될 것이고, 원인이 되는 코드를 찾기도 어려울 것이다.
##

컬렉션 클래스들을 포함해 많은 클래스는 전달받은 객체가 equals 규약을 지킨다고 가정하고 동작한다.  

Object 명세에서 말하는 동치관계란 쉽게말해, 집합을 서로 같은 원소들로 이뤄진 부분집합으로 나누는 연산이다.   
이 부분집합을 동치류(equivalence class; 동치 클래스)라 한다.  
equals 메서드가 쓸모 있으려면 모든 원소가 같은 동치류에 속한 어떤 원소와도 서로 교환할 수 있어야 한다.

---

동치관계를 만족시키기 위해 다섯 요건을 살펴보자.  
* 반사성 : 객체는 자기 자신과 같아야 한다는 뜻이다.
    * 이 요건을 어긴 클래스의 인스턴스를 컬렉션에 넣은 다음 contains 메서드를 호출 하면 방금 넣은 인스턴스가 없다고 답할 것이다.  
##
* 대칭성 : 두 객체는 서로에 대한 동치 여부에 똑같이 답해야 한다.  
```java
public class Equals {
    public static void main(String[] args) {
        CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
        String s = "Polish";

        System.out.println(cis.equals(s));      // true
        System.out.println(s.equals(cis));      // false
    }

    public static class CaseInsensitiveString {
        private final String s;

        public CaseInsensitiveString(String s) {
            this.s = s;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof CaseInsensitiveString)
                return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
            if (o instanceof String)
                return s.equalsIgnoreCase((String) o);
            return false;
        }

    }
}
```
cis.equals(s)는 true를 반환한다. CaseInsensitiveString의 equals는 일반 String을 알고 있지만
String의 equals는 CaseInsensitiveString의 존재를 모르다는 것이다.
따라서 s.equals(cis)는 false를 반환하므로 대칭성을 위반한다.

*참고*
```java
// String 의 equals 메서드를 살펴 보자

public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof String) {
            ...
                return true;
            }
        }
        return false;
    }
```
이 문제를 해결하려면 CaseInsensitiveString 의 equals를 String과도 연동 하겠다는 허황한 꿈을 버려야한다.  
그 결과 간결한 equals메서드를 만들 수 있다.

```java
@Override
        public boolean equals(Object o) {
            return o instanceof CaseInsensitiveString 
            && ((CaseInsensitiveString)o).s.equalsIgnoreCase(s);
        }
```

~~CaseInsensitiveString객체는 동치성을 CaseInsensitiveString객체와 비교해야지 String과 비교하는 꿈을 버리라는 것으로 이해했다.~~


* 추이성 : 첫 번째 객체와 두번째 객체가 같고, 두 번째 객체와 세 번째 객체가 같다면, 첫 번째 객체와 세 번째 객체도 같아야 한다.   ~~3단 논법같은 느낌이다.~~

```java
class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

    }
    
class ColorPoint extends Point {
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }
    
    // ... 코드 생략
}
```

ColorPoint의 equals메서드는 어떻게 고쳐야 할까?  
그대로 둔다면 Point의 구현이 상속되어 색상 정보는 무시한채 비교를 수행할 것이다.

```java
//ColorPoint의 equals 메서드
   @Override
   public boolean equals(Object o) {
      if (!(o instanceof ColorPoint)) //비교대상이 Point이면 항상 false
          return false;
      return super.equals(o) && ((ColorPoint) o).color == color;
   }
```
위 코드는 비교 대상이 ColorPoint이고 위치와 색상이 같을때만 true를 반환하는 메서드이다.  
그런데 이 메서드는 일반 Point를 ColorPoint에 비교한 결과가 다를 수 있다.  
ColorPoint의 equals는 입력 매개변수의 클래스 종류가 다르다며 매번 false만 반환할 것이다.  
##
```java
@Override
        public boolean equals(Object o) {
            if (!(o instanceof Point))
                return false;
            
            // o가 일반 Point면 색상을 무시하고 비교한다.
            if (!(o instanceof ColorPoint))
                return o.equals(this);
            
            // o가 ColorPoint면 색상까지 비교한다.
            return super.equals(o) && ((ColorPoint) o).color == color;
        }
```

이 방식은 대칭성은 지켜주지만, 추이성을 깨버린다.
```java
ColorPooint p1 = new ColorPoint(1,2,Color.RED);
Point p2 = new Point(1,2);
ColorPoint p3 = new ColorPoint(1,2,Color.BLUE);
```

p1.equals(p2) 와 p2.equals(p3)는 true를 반환하는데, p1.equals(p3)가 false를 반환 한다.

또한 이방식은 무한 재귀에 빠질 위험도 있다. Point의 또 다른하위 클래스 SmellPoint를 만들고 equals는 같은 방식으로 구현했다고 해보자.  
myColorPoint.equals(mySmellPoint)를 호출하면 StackOverflowError를 일으킨다.  
~~잘 이해 안가지만 느낌은 온다~~  

**구체 클래스를 확장해 새로운 값을 추가하면서 equals 규약을 만족시킬 방법은 존재하지 않는다.**  
이 말은 얼핏, equals안의 instanceof 검사를 getClass 검사로 바꾸면 규약도 지키고 값도 추가하면서 구체 클래스를 상속 할 수 있다는 뜻으로 들린다.
```java
// 잘못된 코드 - 리스코프 치환원칙 위배!
    @Override
    public boolean equals(Object o) {
        if(o == null || o.getClass() != getClass())
            return false;
        Point p = (Point) o;
        return p.x == x && p.y == y;
    }
```
리스코프 치환 원칙에 따르면, 어떤 타입에 있어 중요한 속성이라면 그 하위 타입에서도 마찬가지로 중요하다.  

*참고*   
*리스코프 치환의 원칙*  
*다형성과 확장성을 극대화 하려면 하위 클래스를 사용하는 것보다는 상위의 클래스(인터페이스)를 사용하는 것이 더 좋습니다. 일반적으로 선언은 기반 클래스로 생성은 구체 클래스로 대입하는 방법을 사용합니다.Ex) List<String> list = new ArraysList<String>();*  

```java
private static final Set<Point> unitCircle = Set.of(new Point(1,0), new Point(0,1),new Point(-1,0),new Point(0,-1));

public static boolean onUnitCircle(Point p) {
    return unitCircle.contains(p);
}
```
```java
public class counterPoint extends Point {
    private satic final AtomicInteger counter = new AtomicInteger();

    public counterPoint(int x, int y) {
        super(x,y);
        counter.incrementAndGet();
    }
    ...
}
```
point 클래스의 equals메서드를 getClass를 사용해 작성했다면 onUnitCircle은 false를 반환 할 것 이다.  

**원인은 컬렉션 구현체에서 주어진 원소를 담고 있는지를 확인하는 방법에 있다. Set을 포함하여 대부분의 컬렉션은 이 작업에 equals를 메서드를 이용한다.**  
CounterPoint의 인스턴스는 어떤 Point와 같을 수 없기 때문이다. 반면 Point의 equals를 instanceof 기반으로 올바로 구현했다면 CounterPoint 인스턴스를 건네줘도 onUnitCircle 메서드가 제대로 동작 할 것이다.

**구체 클래스의 하위 클래스에서 값을 추가할 방법은 없지만 괜찮은 우회 방법이 하나 있다.**
> 상속 대신 컴포지션을 사용하라(아이템18)을 사용하면 된다.


```java
class ColorPoint {
        private final Point point;
        private final Color color;

        public ColorPoint(int x, int y, Color color) {
            point = new Point(x,y);
            this.color = Objects.requireNonNull(color);
        }

        // 이 ColorPoint의 Point 뷰를 반환한다.
        public Point asPoint() {
            return point;
        }


        @Override
        public boolean equals(Object o) {
            if(!(o instanceof ColorPoint))
                return false;
            ColorPoint cp = (ColorPoint) o;
            return cp.point.equals(point) && cp.color.equals(color);
        }

        // ... 코드 생략
    }
```
Point를 상속하는 대신 Point를 ColorPoint의 private필드로 두고, ColorPoint와 같은 위치의 일반 Point를 반환하는 뷰 메서드를 public으로 추가하는 식이다.

* 일관성 : 두 객체가 같다면 앞으로도 영원히 같아야 한다는 뜻이다.
    * 가변 객체 : 비교 시점에 따라 서로 다를 수도 혹은 같을 수도 있다.
    * 불변 객체 : 한번 다르면 끝까지 달라야 한다. (클래스를 작성할때 불변 클래스로 만드는것이 나을지 심사숙고하자(아이템 17))  

클래스가 불변이든 가변이든 equals의 판단에 신뢰할 수 없는 자원이 끼어들게 해서는 안된다.  
equals는 항시 메모리에 존재하는 객체만을 사용한 결정적 계산만 수행해야 한다.
##
* null-아님 : 모든 객체가 null과 같지 않아야 한다는 뜻이다.  

o.equals(null)이 true를 반환하는 상황은 상상하기 어렵겠지만, 실수로 NullPointException을 던지는 코드는 흔할 것이다.    
이 일반 규약은 이런 경우도 허용하지 않는다.
```java
// 명시적 null 검사 - 필요없다!
@Override
public boolean equals(Object o) {
    if(o == null)
        return false;
    ...
}
```
보단
```java
// 묵시적 null 검사 - 이쪽이 낫다.
@Override
public boolean equals(Object o) {
    if(!(o instanceof MyType))
        return false;
    MyType mt = (MyType) o;
    ...
} 
```
동치성을 검사하려면 equals는 건네받은 객체를 적절히 형변환후 필수 필드들의 값을 알아 내야 한다. 그러려면 형변환에 앞서 instanceof 연산자로 입력 매개변수가 올바르 타입인지 검사해야 한다.  
instanceof는 입력이 null이면 타입 확인 단계에서 false를 반환하기 때문에 null검사를 명시적으로 하지 않아도 된다.
##
**equals 메서드 구현 방법을 단계별로 정리해보면 다음과 같다.**
1. == 연산자를 사용해 입력이 자기 자신의 참조인지 확인한다.
    * 이는 단순한 성능 최적화용으로, 비교 작업이 복잡한 상황일 때 값어치를 할 것이다.
2. instanceof 연산자로 입력이 올바른 타입인지 확인한다.
    * 어떤 인터페이스는 자신을 구현한 (서로 다르) 클래스 끼리 비교할 수 있도록 equals 규약을 수정하기도 한다.이런 인터페이스를 구현한 클래스라면 equals에서 (클래스가 아닌) 해당 인터페이스를 사용해야 한다. 예를 들면 Set,List, Map,Map.Entry등의 컬렉션 인터페이스들이 여기 해당한다.
3. 입력을 올바르 타입으로 형변환 한다.
4. 입력 객체와 자기 자신의 대응되는 '핵심' 필드들이 모두 일치 하는지 하나 씩 검사한다.

## 
  
![기본타입 참조타입](https://t1.daumcdn.net/cfile/tistory/99E8E24B5B613AB212)

##

float와 double을 제외한 기본 타입 필드는 == 연산자로 비교하고, 참조 타입 필드는 각각의 equals 메서드로 비교한다.  
float와 double 필드는 각각 정적 메서드인 Float.compare(float,float)와Double.compare(double,double)로 비교한다.  
*float와 double을 특별히 취급하는 이유는 Float.NaN, -0.0f, 특수한 부동소수 값등을 다뤄야 하기 때문이다.*  
이 메서드들은 오토박싱을 수반할 수 있으니 성능상 좋지 않다. 배열 필드는 원소 각각을 앞서의 지침대로 비교한다.  
배열의 모든 원소가 핵심 필드라면 Arrays.equals메서드들 중 하나를 사용하자.

```java
     String[] arr = {"a","b","c","d","e","f"};
     String[] arr2 = {"a","b","c","d","e","h"};
     System.out.println(Arrays.equals(arr,arr2)); // true

    List<String> list = new ArrayList<>(Arrays.asList("a","b","c","d","e"));
    List<String> list2 = new ArrayList<>(Arrays.asList("a","b","c","d","h"));
    System.out.println(list.equals(list2)); // true
```

##

어떤 필드를 먼저 비교하느냐가 equals의 성능을 좌우하기도 한다.  
 * 다를 가능성이 더 큰것  
 * 비교하는 비용이 싼것  

이 두 필드를 먼저 비교하자.
##

비교할 필요가 없는 것
* 동기화용 락(Lock) 필드 같이 객체의 논리적 상태와 관련 없는 필드
* 핵심 필드로부터 계산해낼 수 있는 파생 필드

> equals 를 다 구현했다면 세 가지를 자문해보자. 대칭적인가? 추이성이 있는가? 일관 적인가?

자문에서 끝내지 말고 단위 테스트를 돌려보자 단, equals 메서드를 AutoValue를 이용해 작성했다면 테스트를 생략해도 안심할 수 있다. 나머지 `반사성`과 `null-아님`도 만족해야 하지만 이 둘이 문제되는 경우는 별로 없다.

##

* 마지막 주의사항  
1.  equals를 재정의 할때 hashCode도 반드시 재정의하자(아이템 11)
2. 너무 복잡하게 해결하려 들지 말자.
    * 필드들의 동치성만 검사해도 equals 규약을 어렵지 않게 지킬 수 있다.
3. Object 외의 타입을 매개변수로 받는 equals 메서드는 선언하지 말자.
```java
// 잘못된 예 - 입력 타입은 반드시 Object여야 한다!
public boolean equals(MyClass o) {
    ...
}
```
이 메서드는 Object.equals를 재정의 한게 아니다.  
입력 타입이 Object가 아니므로 재정의가 아니라 다중정의(아이템 52)한 것이다.  
기본 equals를 그대로 둔 채로 추가한 것일지라도, 이처럼 타입을 '구체적으로 명시한' equals는 오히려 해가 된다.  
이 메서드는 하위 클래스에서의 @Override 애너테이션이 긍정 오류(false positive; 거짓 양성)를 내게 하고 보안 측면에서도 잘 못된 정보를 준다.

```java
// 여전히 잘못된 예 - 컴파일되지 않음
@Override
public boolean equals(MyClass o) {
    ...
}
```
equals(hashCode도 마찬가지)를 작성하고 테스트하는 일은 지루하고 테스트 코드도 뻔하다.  
이 작업을 대신해줄 오픈소스가 있으니 바로 **구글이 만든 AutoValue 프레임 워크다.**

---

>핵심정리  
```
1. 꼭 필요한 경우가 아니라면 equals를 재정의 하지 말자.(많은 경우 Object의 equals가 원하는 비교를 정확히 수행해준다.)
2. 재정의해야 할때는 그 클래스의 핵심 필드를 빠짐없이, 다섯가지 규약을 확실히 지킨다.
3. AutoValue 프레임워크를 적극 활용하자.
```


