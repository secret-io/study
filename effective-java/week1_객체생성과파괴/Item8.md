# 아이템8 : finalizer와 cleaner 사용을 피하라


Java는 finailzer/cleaner 라는 2가지 객체 소멸자를 제공한다. 객체 소멸자는 해당 객체가 GC에 의해 해제되기 직전에 실행되는 메소드다. 예를 들어 해당 객체에서 파일 리소스를 사용했을 때 반납하는 과정을 객체 소멸전에 강제하는 목적으로 사용할 수 있다. 그러나 실제 애플리케이션상에서는 이렇게 반납 처리해서는 안된다. 그 이유는 페이지 전체에 걸쳐 설명한다.

그리고 Java 9에서 finalizer를 deprecated로 지정하면서 개선안으로 나온 것이 cleaner다.

**하지만 결론적으로 둘 다 예측할 수 없고 위험하며, 느리고 일반적으로 불필요하다.**


## 1. finailzer와 cleaner는 일반적으로 최악

아래는 finailzer를 활용한 자원 회수 코드이다.

```java
public class Example {
    public static void main(String[] args) throws Exception {
        Example example = new Example();
        example.run();
        Thread.sleep(1000);
        System.gc();
    }
    
    private void run() {
        FinalizerEx finalizerEx = new FinalizerEx();
        finalizerEx.hello();
    }
}
```

```java
public class FinalizerEx {
    @Override // Object의 finalize()를 Override
    protected void finalize() throws Throwable {
        System.out.println("finailzer로 자원 회수!");
    }

    public void hello() {
        System.out.println("hello~");
    }
}
```

`example.run()`를 실행하고나면 `finalizerEx`는 GC의 대상인 Unreachable한 Object가 되었다. 그리고 `System.gc()`를 통해 GC를 요청한다.<sup id="a1">[1](#f1)</sup> 그렇다면 GC가 해당 객체를 회수하기 직전 `finalize()`가 실행되어야 한다.

**하지만 실제 결과는 그럴 수도 있고 아닐 수도 있다.** 왜냐하면 finalizer와 cleaner는...

**1. 즉시 수행된다는 보장이 없다. 즉 언제 실행될 지 알 수 없다.**
* 그래서 타이밍이 중요한 작업을 절대로 finalizer/cleaner 내부에서 수행해선 안된다.
    
**2. 오히려 인스턴스 자원 회수를 지연시킬 수 있다.**
* finalizer를 수행하는 쓰레드의 우선 순위가 낮기 때문에, 최악의 경우 OOME이 발생할 수도 있다.
* 별도의 쓰레드로 운용하는 cleaner도 크게 다르지 않다. 백그라운드에서 쓰레드가 언제 실행될지 알 수 없기에.

**3. 아예 수행조차 되지 않을 수 있다. 즉 수행 여부가 보장되지 않는다.**
* 언제 수행될지 정해지지 않았기 때문에(이는 GC마다 천차만별), 애플리케이션이 종료될 때 까지 수행되지 않을 수도 있다.
* 수행 가능성을 높여줄 수 있는 방안은 존재하지만, 쓰레드 중단이라는 심각한 결함을 야기해서 사실상 쓸 수 없다.

**4. 심각한 성능 문제를 동반한다.(매우 느림)**
* 현재 보편적인 자원 회수 방식인 `AutoCloseable`를 구현한 방식보다 약 50배 이상 느리다.

**5. finalizer 공격에 노출되어 심각한 보안 문제를 야기할 수 있다.**
* 어떤 이유로 인해 객체 생성 시 예외가 발생하여 객체 생성에 실패했을 때, 해당 객체의 하위 클래스에서 finalizer가 수행될 수 있다. 객체 생성에 실패해서 실제로는 사라져야할 객체지만 finalizer를 통해 내부를 건드릴 수 있는 보안 이슈가 발생한다.
* 이를 해결하기 위해서는 부모 클래스의 `finalize()`를 `final`로 선언하면 된다.  


## 2. 대신 AutoCloseable을 구현하자

그렇다면 finalizer 대신 어떤 방법으로 자원을 회수해야 할까?

아래의 코드를 살펴보자.

```java
public class Example {
    public static void main(String[] args) throws RuntimeException {
        AutoCloseEx autoCloseEx = null;
        
        // Java 7 이전
        try {
            autoCloseEx = new AutoCloseEx();
            autoCloseEx.hello();
        } finally {
            if(autoCloseEx != null) {
                autoCloseEx.close();
            }
        }
        
        // Java 7 이후
        try (AutoCloseEx autoCloseEx = new AutoCloseEx()) {
            autoCloseEx.hello();
        }
    }
}
```

```java
public class AutoCloseEx implements AutoCloseable {
    @Override
    public void close() throws RuntimeException {
        System.out.println("AutoCloseable의 close()로 자원 회수!");
    }

    public void hello() {
        System.out.println("hello");
    }
}
```
**그저 자원회수가 필요한 객체는 `AutoCloseable`을 구현한 뒤, `close()` 메소드를 호출하면 된다.**

추가적으로 해당 코드에서는 `try-finally` 혹은 `try-with-resources` 구문을 이용하여 예외발생 시 자원회수를 보장하도록 했다.<sup id="a2">[2](#f2)</sup>


## 3. Finalizer와 Cleaner를 쓰는 경우도 있다
 
 하지만 finalizer와 cleaner가 아무런 의미가 없는 것은 아니다. 적절한 쓰임새 2가지가 존재한다.
 
**1. 자원 반납의 안전망 역할**
* `close()` 메소드를 호출하지 않았을 경우를 대비한 안전망 역할이다. (늦게라도 하는게 없는 것보단 나으니) 
* 실제로 자바 라이브러리 중 `FileInputStream`, `FileOutputStream`, `ThreadPoolExecutor` 등에 안전망 역할을 하는 finalizer가 존재한다.
    
**2. 네이티브 피어의 자원 반납**
* 네이티브 객체는 자바 객체가 아니므로 GC가 그 존재를 알 수 없다. 그렇기 때문에 프로그래머가 해당 자원을 직접 반납해줘야 한다. 이 때, 사용할 수도 있다.
* 단, 네이티브 피어 리소스의 중요도가 낮거나 성능에 미치는 영향이 작은 경우에만 사용하며, 중요하거나 성능이 중요하다면 역시 `close()`를 사용해야 한다.
 
 아래 finalizer를 이용해 안전망을 구축한 코드다.
  
```java
 public class Example {
     public static void main(String[] args) throws RuntimeException {
         AutoCloseWithFinalizerEx autoCloseWithFinalizerEx = null;
         
        try {
            autoCloseWithFinalizerEx = new autoCloseWithFinalizerEx();
            autoCloseWithFinalizerEx.hello();
        } finally {
            System.out.println("close()가 없다");
        }
     }
 }
 ```
 
 ```java
 public class AutoCloseWithFinalizerEx implements AutoCloseable {
     private boolean closed;
 
     @Override
     public void close() throws RuntimeException {
         if (this.closed) {
             throw new IllegalStateException();
         }
         closed = true;
     }
 
     public void hello() {
         System.out.println("hello");
     }
 
     @Override
     protected void finalize() throws Throwable {
         if(!this.closed) close();
     }
 }
 ```
 
 앞서 살펴봤던 `AutoCloseable`의 예제코드가 거의 동일하다. 다만 `Example`에서 `close()`를 실행하지 않은 경우로 상정했다. 이 때 `autoCloseEx`가 언젠가라도 회수하기 위해서 안전망으로 `AutoCloseEx`클래스 내부에 `finalize()`를 `Override` 해준 것이다.
 
 지금까지 우리는 계속해서 finalizer에 대해서만 알아봤는데, cleaner의 코드는 어떤지 살펴보자. finalizer보다는 약간 까다롭지만 크게 다른건 없다.
  
```java
public class Example {
   public static void main(String[] args) throws RuntimeException {
       AutoCloseWithCleanerEx autoCloseWithCleanerEx = null;
       
      try {
          autoCloseWithCleanerEx = new AutoCloseWithCleanerEx();
          autoCloseWithCleanerEx.hello();
      } finally {
          System.out.println("close()가 없다");
      }
   }
}
```
   
 ```java
public class AutoCloseWithCleanerEx implements AutoCloseable {
    private final ResourceCleaner resourceCleaner;
    private static final Cleaner CLEANER = Cleaner.create();
    private final Cleaner.Cleanable cleanable;
    private boolean closed;
    
    public AutoCloseWithCleanerEx() {
        this.resourceCleaner = new ResourceCleaner();
        this.cleanable = CLEANER.register(this, resourceCleaner);
    }
    
    private static class ResourceCleaner implements Runnable {
        @Override
        public void run() {
            System.out.println("cleaner로 자원 회수!");
        } 
    }
    
    @Override
    public void close() throws RuntimeException {
        if (this.closed) {
            throw new IllegalStateException();
        }
        closed = true;
        cleanable.clean();
    }

    public void hello() {
        System.out.println("hello");
    }
}
 ```
 cleaner를 사용하는 방법 이해가 가지 않으면 넘어가도 괜찮다.
1. `Cleaner` 클래스가 존재한다. `Cleaner.create()`를 소환하여 인스턴스를 생성한다.
2. cleaner를 사용할 별도의 쓰레드가 필요하다. 그래서 `AutoCloseWithCleanerEx` 내부에 `Runnable`를 구현한 `ResourceCleaner` 클래스를 만들었다. 해당 클래스 내부에 자원을 회수하는 과정을 작성하면 된다. (단, 순환참조의 위험 때문에 절대 AutoCloseWithCleanerEx를 참조해서는 안된다.)
3. 실제 사용은 `cleanable`을 통해서 해야한다. 미리 생성했던 `Cleaner`의 인스턴스에 실제 수행할 쓰레드인 `resourceCleaner`를 `register()`하고, `cleanable`에 넣어준다.
4. `close()` 메소드내에서 `cleanable.clean()`를 통해 안전망을 구축할 수 있다. (역시 100% 실행된다고 보장할 순 없다)


## 정리


> finailzer와 cleaner는 안전망 역할이나 네이티브 자원 회수용으로만 사용하자. 이 경우에도 성능 저하에 주의 할 필요가 있다.  
 
 
---


*<i id="f1">[1.](#a1)</b> 왜냐하면 finalizerEx는 GC의 대상이 되었을 뿐, 실제 GC 알고리즘 상 해당 코드에서 GC가 실행될 확률은 매우 낮기 때문이다. 물론 `System.gc()`도 해당 애플리케이션 종료시까지 GC 수행 여부는 불확실하다.*  
*<i id="f2">[2.](#a2)</b> 해당 내용은 아이템9를 참고.*