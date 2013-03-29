package sender.src;
public class Main {

    private final static int SLEEP_TIME = 1*60*1000;

    public static void main(String[] args) {
        NovaSender nv = new NovaSender();

        try {

            while (true) {

                nv.performRoutineTasks();
                Thread.sleep(SLEEP_TIME);
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
