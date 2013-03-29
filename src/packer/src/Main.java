package packer.src;
public class Main {

    private final static int SLEEP_TIME = 10*60*1000;

    public static void main(String[] args) {

        Packer packer = new Packer();

        try {

            while (true) {

                packer.performRoutineTasks();
                Thread.sleep(SLEEP_TIME);
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
