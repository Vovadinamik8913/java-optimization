package nsu.abramenko;

import java.util.ArrayList;
import java.util.Random;

public class DiagnosticDemo {

    public static final class Singleton {
        private static final Singleton INSTANCE = new Singleton();
        private final String name = "TheOneAndOnlySingleton";

        private Singleton() {}

        public static Singleton getInstance() {
            return INSTANCE;
        }

        public String getName() {
            return name;
        }
    }

    public static class Bean {
        private final String name;
        private byte[] data;
        private ArrayList<String> list;
        private int primitiveInt;
        private boolean primitiveBool;
        private Bean selfReference;
        private Bean ringNext;

        public Bean(String name, int dataSize) {
            this.name = name;
            this.data = new byte[dataSize];
            this.list = new ArrayList<>();
            this.primitiveInt = new Random().nextInt(1000);
            this.primitiveBool = true;
            this.selfReference = this;

            for (int i = 0; i < 5; i++) {
                list.add("Item-" + i + "-for-" + name);
            }
        }

        public void setRingNext(Bean next) {
            this.ringNext = next;
        }

        public String getName() {
            return name;
        }

        public Bean getRingNext() {
            return ringNext;
        }
    }

    static class SleepingThread extends Thread {
        private final Singleton singleton;
        private Bean myBean;

        public SleepingThread(String name) {
            super(name);
            this.singleton = Singleton.getInstance();
            this.myBean = new Bean(name + "'sBean", 1024 * 10);
        }

        @Override
        public void run() {
            System.out.println(getName() + " started. Bean: " + myBean.getName() +
                    ", Singleton: " + singleton.getName());
            while (true) {
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                    System.out.println(getName() + " was interrupted");
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    static class HarmfulThread extends Thread {

        public HarmfulThread() {
            super("HarmfulThread-Allocator");
        }

        @Override
        public void run() {
            System.out.println("!!! HARMFUL THREAD STARTED !!!");
            System.out.println("!!! WILL CONSUME MEMORY UNTIL OOM !!!");

            ArrayList<byte[]> memoryEater = new ArrayList<>();
            int counter = 0;

            while (true) {
                try {
                    // Аллоцируем большие массивы (10 МБ за раз)
                    byte[] chunk = new byte[10 * 1024 * 1024]; // 10 MB
                    memoryEater.add(chunk);
                    counter++;

                    System.out.println("HarmfulThread: Allocated chunk #" + counter +
                            " (10 MB). Total: " + (counter * 10) + " MB");
                    Thread.sleep(100);

                } catch (OutOfMemoryError e) {
                    System.out.println("!!! OUT OF MEMORY ERROR REACHED !!!");
                    System.out.println("!!! Total allocated: " + (counter * 10) + " MB !!!");

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                    }

                    throw e;

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==============================================");
        System.out.println("Diagnostic Demo Application Starting...");
        System.out.println("==============================================");

        System.out.println("\n[1] Creating ring of Beans in main thread...");

        Bean beanA = new Bean("BeanA_Root", 1024 * 100);  // ~100kb
        Bean beanB = new Bean("BeanB_Root", 1024 * 100);  // ~100kb
        Bean beanC = new Bean("BeanC_Root", 1024 * 100);  // ~100kb

        beanA.setRingNext(beanB);
        beanB.setRingNext(beanC);
        beanC.setRingNext(beanA);

        System.out.println("Ring created: A -> B -> C -> A");
        System.out.println("Bean A next: " + beanA.getRingNext().getName());
        System.out.println("Bean B next: " + beanB.getRingNext().getName());
        System.out.println("Bean C next: " + beanC.getRingNext().getName());

        System.out.println("\n[2] Starting sleeping threads...");

        SleepingThread thread1 = new SleepingThread("SleepingThread-1");
        SleepingThread thread2 = new SleepingThread("SleepingThread-2");
        SleepingThread thread3 = new SleepingThread("SleepingThread-3");

        thread1.start();
        thread2.start();
        thread3.start();

        System.out.println("Started 3 sleeping threads");

        System.out.println("\n[3] Checking for harmful thread argument...");

        if (args.length > 0 && "--harmful".equals(args[0])) {
            System.out.println(">>> Starting harmful thread due to command line argument...");
            HarmfulThread harmfulThread = new HarmfulThread();
            harmfulThread.start();
        } else {
            System.out.println(">>> Harmful thread NOT started.");
            System.out.println(">>> To start harmful thread, pass '--harmful' argument:");
            System.out.println(">>> java DiagnosticDemo --harmful");
        }

        System.out.println("\n==============================================");
        System.out.println("Application is running. Main thread will now sleep.");
        System.out.println("Use jps to find PID: jps -l | grep DiagnosticDemo");
        System.out.println("Use jmap to dump heap: jmap -dump:live,format=b,file=heap.hprof <PID>");
        System.out.println("Use jstack to dump threads: jstack <PID> > threads.txt");
        System.out.println("Use jcmd for diagnostics: jcmd <PID> help");
        System.out.println("Use jconsole to monitor: jconsole");
        System.out.println("==============================================");

        Thread.sleep(Long.MAX_VALUE);
    }
}