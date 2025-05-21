import javax.swing.*;

public class Ball {
    int x, y;
    int spdY = 0;
    int spdX = 0;
    double money = 1000000;
    boolean canMove = false;
    boolean canTheft = true;
    int target = 0;
    JLabel label;
    Timer cooldown;

    Ball(int x, int y, int spdX, int spdY, JLabel label) {
        this.x = x;
        this.y = y;
        this.spdX = spdX;
        this.spdY = spdY;
        this.label = label;
        cooldown = new Timer(3000, e ->
                thiefTimer()
        );
    }

    public void startTimer(){
        if (!cooldown.isRunning()) {
            canTheft = false;
            cooldown.setRepeats(false);
            cooldown.start();
        }
    }

    public void thiefTimer(){
        canTheft = true;
        if(cooldown.isRunning()){
            cooldown.stop();
        }
    }
}

