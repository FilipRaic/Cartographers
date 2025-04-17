package hr.tvz.cartographers.shared.thread;

import hr.tvz.cartographers.models.GameMove;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SaveLastGameMoveThread extends AbstractGameMoveThread implements Runnable {

    private GameMove theLastGameMove;

    @Override
    public void run() {
        super.saveGameMove(theLastGameMove);
    }
}
