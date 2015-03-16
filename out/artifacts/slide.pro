-injars      slide-raw.jar
-injars      "C:\Program Files (x86)\scala\lib\scala-library.jar"
-outjars     slide.jar
-libraryjars <java.home>/lib/rt.jar

-dontwarn scala.**
-dontobfuscate

-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

-keep class * implements org.xml.sax.EntityResolver

-keepclassmembers class * {
    ** MODULE$;
}

-keepclassmembernames class scala.concurrent.forkjoin.ForkJoinPool {
    long eventCount;
    int  workerCounts;
    int  runControl;
    scala.concurrent.forkjoin.ForkJoinPool$WaitQueueNode syncStack;
    scala.concurrent.forkjoin.ForkJoinPool$WaitQueueNode spareStack;
}

-keepclassmembernames class scala.concurrent.forkjoin.ForkJoinWorkerThread {
    int base;
    int sp;
    int runState;
}

-keepclassmembernames class scala.concurrent.forkjoin.ForkJoinTask {
    int status;
}

-keepclassmembernames class scala.concurrent.forkjoin.LinkedTransferQueue {
    scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference head;
    scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference tail;
    scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference cleanMe;
}